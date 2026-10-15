package com.coffeecreator.app.core.ai

import android.app.ActivityManager
import android.content.Context
import com.coffeecreator.app.core.model.InputLimits
import java.io.File
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.CharBuffer
import java.nio.charset.CodingErrorAction
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.max
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

sealed interface NativeLlmStatus {
    data object Unloaded : NativeLlmStatus
    data object Loading : NativeLlmStatus
    data class Ready(val modelName: String) : NativeLlmStatus
    data object Generating : NativeLlmStatus
    data class Failed(val reason: String) : NativeLlmStatus
}

data class NativeChatTurn(val role: String, val content: String) {
    init {
        require(role == "user" || role == "assistant") { "History roles must be user or assistant." }
        require(content.isNotBlank() && content.length <= InputLimits.NATIVE_TEXT_CHARS && '\u0000' !in content) {
            "History message is empty, too long or contains a null character."
        }
    }
}

data class NativeGenerationOptions(
    val maxTokens: Int = 128,
    val temperature: Float = 0.7f,
    val topP: Float = 0.9f,
    val topK: Int = 40,
    val seed: Int = 0
) {
    init {
        require(maxTokens in 1..256) { "Maximum output must be 1–256 tokens." }
        require(temperature.isFinite() && temperature in 0f..1.5f) { "Temperature must be 0–1.5." }
        require(topP.isFinite() && topP > 0f && topP <= 1f) { "Top-p must be greater than 0 and at most 1." }
        require(topK in 1..100) { "Top-k must be 1–100." }
    }
}

/** Application-owned CPU runtime. SAF assets must be verified and privately staged by the caller. */
class NativeLlmEngine(context: Context) {
    private val applicationContext = context.applicationContext
    private val session = Mutex()
    private val cancellationRequested = AtomicBoolean(false)
    private val mutableStatus = MutableStateFlow<NativeLlmStatus>(NativeLlmStatus.Unloaded)
    private var libraryLoaded = false
    private var modelName = ""
    var contextLimit: Int = 1024
        private set

    @Volatile private var handle = 0L

    @Volatile private var modelLoaded = false

    val status: StateFlow<NativeLlmStatus> = mutableStatus.asStateFlow()
    val initialized: Boolean get() = modelLoaded

    suspend fun load(file: File, contextSize: Int = 1024) = withContext(Dispatchers.IO) {
        session.withLock {
            require(contextSize in 256..1024) { "Context must be 256–1024 tokens." }
            try {
                val model = file.canonicalFile
                val privateRoot = applicationContext.dataDir.canonicalFile.path + File.separator
                require(model.path.startsWith(privateRoot) && model.isFile && model.canRead()) {
                    "Select a verified model staged in app-private storage."
                }
                RandomAccessFile(model, "r").use { input ->
                    val header = ByteArray(24)
                    require(input.length() >= header.size) { "Model is truncated." }
                    input.readFully(header)
                    NativeModelPolicy.requireHeader(header, input.length())
                }
                releaseLocked()
                val activityManager = applicationContext.getSystemService(ActivityManager::class.java)
                    ?: error("Device memory information is unavailable.")
                val memory = ActivityManager.MemoryInfo()
                activityManager.getMemoryInfo(memory)
                NativeModelPolicy.requireMemory(
                    model.length(),
                    memory.availMem,
                    memory.totalMem,
                    memory.threshold,
                    memory.lowMemory
                )
                if (!libraryLoaded) {
                    System.loadLibrary("coffee_llama")
                    libraryLoaded = true
                }
                cancellationRequested.set(false)
                handle = nativeCreate()
                check(handle != 0L) { "Native runtime allocation failed." }
                mutableStatus.value = NativeLlmStatus.Loading
                val localHandle = handle
                withNativeCancellation(localHandle) {
                    val threads = (Runtime.getRuntime().availableProcessors() / 2).coerceIn(1, 4)
                    val nativeBudget = memory.availMem - max(512L * 1024 * 1024, memory.threshold)
                    nativeLoad(localHandle, model.path.toByteArray(Charsets.UTF_8), contextSize, threads, nativeBudget)
                    currentCoroutineContext().ensureActive()
                    if (cancellationRequested.get()) throw CancellationException("Model load cancelled.")
                }
                modelName = model.name
                contextLimit = contextSize
                modelLoaded = true
                mutableStatus.value = NativeLlmStatus.Ready(modelName)
            } catch (cancelled: CancellationException) {
                releaseLocked()
                mutableStatus.value = NativeLlmStatus.Unloaded
                throw cancelled
            } catch (boundaryFailure: Exception) {
                releaseLocked()
                mutableStatus.value = NativeLlmStatus.Failed(boundaryFailure.message ?: "Model load failed.")
                throw boundaryFailure
            } catch (failure: LinkageError) {
                releaseLocked()
                mutableStatus.value = NativeLlmStatus.Failed("Native runtime is unavailable for this device ABI.")
                throw IllegalStateException("Native runtime is unavailable for this device ABI.", failure)
            }
        }
    }

    fun generate(
        systemPrompt: String,
        userPrompt: String,
        history: List<NativeChatTurn> = emptyList(),
        options: NativeGenerationOptions = NativeGenerationOptions()
    ): Flow<String> = flow {
        require(systemPrompt.isNotBlank() && userPrompt.isNotBlank()) { "System and user prompts are required." }
        require(history.size <= 12) { "Use at most 12 recent history messages." }
        val turns = listOf("system" to systemPrompt) + history.map { it.role to it.content } +
            listOf("user" to userPrompt)
        require(turns.sumOf { it.second.length.toLong() } <= InputLimits.NATIVE_TEXT_CHARS && turns.none { '\u0000' in it.second }) {
            "Prompt is too long or contains a null character."
        }
        session.withLock {
            check(modelLoaded && handle != 0L) { "Install and load a compatible GGUF model before chatting." }
            val localHandle = handle
            cancellationRequested.set(false)
            nativePrepare(localHandle)
            mutableStatus.value = NativeLlmStatus.Generating
            try {
                withNativeCancellation(localHandle) {
                    nativeStart(
                        localHandle,
                        turns.map { it.first }.toTypedArray(),
                        turns.map { it.second.toByteArray(Charsets.UTF_8) }.toTypedArray(),
                        options.maxTokens,
                        options.temperature,
                        options.topP,
                        options.topK,
                        options.seed
                    )
                    val decoder = Utf8TokenDecoder()
                    while (true) {
                        currentCoroutineContext().ensureActive()
                        if (cancellationRequested.get()) throw CancellationException("Inference cancelled.")
                        val bytes = nativeNext(localHandle) ?: break
                        val delta = decoder.decode(bytes)
                        if (delta.isNotEmpty()) emit(delta)
                    }
                    currentCoroutineContext().ensureActive()
                    if (cancellationRequested.get()) throw CancellationException("Inference cancelled.")
                    val tail = decoder.finish()
                    if (tail.isNotEmpty()) emit(tail)
                }
            } finally {
                nativeEnd(localHandle)
                mutableStatus.value = NativeLlmStatus.Ready(modelName)
            }
        }
    }.flowOn(Dispatchers.IO)

    /** Safe from UI thread: only sets an atomic flag; no inference lock is acquired. */
    fun cancel() {
        cancellationRequested.set(true)
        val localHandle = handle
        if (libraryLoaded && localHandle != 0L) nativeCancel(localHandle)
    }

    suspend fun unload() {
        cancel()
        withContext(Dispatchers.IO) {
            session.withLock {
                releaseLocked()
                mutableStatus.value = NativeLlmStatus.Unloaded
            }
        }
    }

    suspend fun close() = unload()

    private fun releaseLocked() {
        val oldHandle = handle
        handle = 0L
        modelLoaded = false
        modelName = ""
        if (libraryLoaded && oldHandle != 0L) nativeRelease(oldHandle)
    }

    private suspend fun <T> withNativeCancellation(localHandle: Long, block: suspend () -> T): T = coroutineScope {
        val finished = AtomicBoolean(false)
        val watcher = launch(Dispatchers.IO, start = CoroutineStart.UNDISPATCHED) {
            try {
                awaitCancellation()
            } finally {
                if (!finished.get()) nativeCancel(localHandle)
            }
        }
        try {
            block()
        } finally {
            finished.set(true)
            withContext(NonCancellable) { watcher.cancelAndJoin() }
        }
    }

    private external fun nativeCreate(): Long
    private external fun nativeLoad(handle: Long, path: ByteArray, contextSize: Int, threads: Int, memoryBudget: Long)
    private external fun nativePrepare(handle: Long)
    private external fun nativeStart(
        handle: Long,
        roles: Array<String>,
        content: Array<ByteArray>,
        maxTokens: Int,
        temperature: Float,
        topP: Float,
        topK: Int,
        seed: Int
    )
    suspend fun promptTokenCount(systemPrompt: String, prompt: String, history: List<NativeChatTurn>): Int = withContext(Dispatchers.IO) {
        session.withLock {
            check(initialized) { "Load a verified local model first." }
            require(history.size <= 12 && systemPrompt.length + prompt.length <= InputLimits.NATIVE_TEXT_CHARS)
            val turns = listOf("system" to systemPrompt) + history.map { it.role to it.content } + listOf("user" to prompt)
            nativeTokenCount(
                handle,
                turns.map { it.first }.toTypedArray(),
                turns.map { it.second.toByteArray(Charsets.UTF_8) }.toTypedArray()
            )
        }
    }
    private external fun nativeTokenCount(handle: Long, roles: Array<String>, content: Array<ByteArray>): Int
    private external fun nativeNext(handle: Long): ByteArray?
    private external fun nativeEnd(handle: Long)
    private external fun nativeCancel(handle: Long)
    private external fun nativeRelease(handle: Long)
}

internal object NativeModelPolicy {
    private val GGUF_MAGIC = "GGUF".toByteArray(Charsets.US_ASCII)
    private const val MIB = 1024L * 1024
    private const val MAX_MODEL_BYTES = 2L * 1024 * MIB

    fun requireHeader(header: ByteArray, fileLength: Long) {
        require(header.size == 24 && fileLength in 24..MAX_MODEL_BYTES) { "Model is truncated or exceeds 2 GiB." }
        require(header.copyOfRange(0, 4).contentEquals(GGUF_MAGIC)) { "File is not GGUF." }
        val fields = ByteBuffer.wrap(header).order(ByteOrder.LITTLE_ENDIAN)
        fields.position(4)
        require(fields.int in 2..3) { "Only GGUF versions 2 and 3 are supported." }
        require(fields.long in 1..4096 && fields.long in 1..4096) { "Model header counts exceed safe limits." }
    }

    fun requireMemory(modelBytes: Long, availableBytes: Long, totalBytes: Long, thresholdBytes: Long, lowMemory: Boolean): Long {
        require(modelBytes in 24..MAX_MODEL_BYTES) { "Choose a compatible model smaller than 2 GiB." }
        require(!lowMemory) { "Device memory is low. Close other apps or choose a smaller model." }
        require(totalBytes > 0 && availableBytes in 1..totalBytes && thresholdBytes in 0..totalBytes) {
            "Device memory information is invalid."
        }
        // Includes weights, tokenizer, bounded KV/compute scratch and a reserve for Android/manual UI.
        val workingBytes = modelBytes + modelBytes / 4 + 384 * MIB
        val reserve = max(512 * MIB, thresholdBytes)
        require(workingBytes <= totalBytes - totalBytes / 3 && availableBytes - reserve >= workingBytes) {
            "Insufficient free RAM for this model. Close other apps or choose a smaller quantized model."
        }
        return workingBytes + reserve
    }
}

internal class Utf8TokenDecoder {
    private val decoder = Charsets.UTF_8.newDecoder()
        .onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE)
    private var pending = ByteArray(0)

    fun decode(bytes: ByteArray): String = decodeBytes(bytes, false)

    fun finish(): String = decodeBytes(ByteArray(0), true)

    private fun decodeBytes(bytes: ByteArray, end: Boolean): String {
        val input = ByteBuffer.wrap(pending + bytes)
        val output = CharBuffer.allocate(input.remaining() + 4)
        decoder.decode(input, output, end).throwExceptionIfError()
        pending = ByteArray(input.remaining()).also { input.get(it) }
        if (end) decoder.flush(output).throwExceptionIfError()
        output.flip()
        return output.toString()
    }

    private fun java.nio.charset.CoderResult.throwExceptionIfError() {
        if (isError) throwException()
    }
}
