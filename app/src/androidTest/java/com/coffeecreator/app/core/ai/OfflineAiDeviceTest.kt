package com.coffeecreator.app.core.ai

import android.content.Context
import android.os.SystemClock
import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.security.MessageDigest
import java.util.Locale
import java.util.UUID
import kotlin.math.abs

/** Uses real verified assets and private engine instances; never touches the application graph/DB. */
@RunWith(AndroidJUnit4::class)
class OfflineAiDeviceTest {
    private val instrumentation get() = InstrumentationRegistry.getInstrumentation()
    private val targetContext get() = instrumentation.targetContext

    @Test
    fun givenVerifiedMiniLm_whenEmbeddingOnDevice_thenMatchCpuReferenceAndTokenIds() = runBlocking<Unit> {
        val directory = ownedDirectory(targetContext)
        val model = File(directory, "model_quantized.onnx")
        val vocabulary = File(directory, "vocab.txt")
        var engine: OnnxEmbeddingEngine? = null
        try {
            val reference = instrumentation.context.assets.open("embedding-parity.json").bufferedReader().use {
                JSONObject(it.readText())
            }
            withContext(Dispatchers.IO) {
                copyAsset(targetContext, "embedding/model_quantized.onnx", model)
                copyAsset(targetContext, "embedding/vocab.txt", vocabulary)
                assertEquals(reference.getString("model_sha256"), sha256(model))
                assertEquals(reference.getString("vocab_sha256"), sha256(vocabulary))
            }
            val tokenizer = withContext(Dispatchers.IO) { BertWordPieceTokenizer(vocabulary.readLines(Charsets.UTF_8)) }
            val localEngine = withContext(Dispatchers.IO) { OnnxEmbeddingEngine(model, vocabulary) }
            engine = localEngine
            val cases = reference.getJSONArray("cases")
            assertEquals("Reference must contain the three requested texts.", 3, cases.length())
            val tolerance = reference.getDouble("absolute_tolerance")
            assertTrue("Reference tolerance must be at most 1e-4.", tolerance > 0 && tolerance <= 0.0001)
            for (index in 0 until cases.length()) {
                val item = cases.getJSONObject(index)
                val text = item.getString("text")
                val tokens = tokenizer.encode(text, reference.getInt("max_tokens"))
                assertArrayEquals("WordPiece IDs case $index", item.getJSONArray("input_ids").longs(), tokens.ids)
                assertArrayEquals("Attention mask case $index", item.getJSONArray("attention_mask").longs(), tokens.attentionMask)
                val expected = item.getJSONArray("embedding").floats()
                val started = SystemClock.elapsedRealtimeNanos()
                val actual = withTimeout(30_000) { localEngine.embed(text) }
                val elapsedMs = (SystemClock.elapsedRealtimeNanos() - started) / 1_000_000.0
                assertEquals("Embedding dimensions case $index", 384, actual.size)
                assertEquals("Reference dimensions case $index", expected.size, actual.size)
                assertTrue("Embedding must be finite.", actual.all { it.isFinite() })
                val maxError = actual.indices.maxOf { abs(actual[it].toDouble() - expected[it].toDouble()) }
                Log.i(TAG, JSONObject().put("check", "embedding_parity").put("case", index)
                    .put("dimensions", actual.size).put("max_absolute_error", maxError)
                    .put("elapsed_ms", elapsedMs).put("model_sha256", reference.getString("model_sha256")).toString())
                assertEquals("ONNX CPU reference parity case $index", 0.0, maxError, tolerance)
            }
        } finally {
            withContext(NonCancellable + Dispatchers.IO) {
                engine?.close()
                deleteOwnedFiles(directory, listOf(model, vocabulary))
            }
        }
    }

    @Test
    fun givenVerifiedQwen_whenStreamingCancellingAndReloading_thenProduceRealTextAndReleaseSession() = runBlocking<Unit> {
        val model = File(targetContext.filesDir, "native-test/qwen2.5-0.5b-instruct-q4_k_m.gguf")
        assertTrue("Integrator must stage the actual Qwen GGUF in native-test before this check.", model.isFile)
        assertEquals("Actual GGUF byte length", 491400032L, model.length())
        withContext(Dispatchers.IO) { assertEquals(QWEN_SHA256, sha256(model)) }
        val engine = NativeLlmEngine(targetContext)
        try {
            val loadStarted = SystemClock.elapsedRealtimeNanos()
            withTimeout(90_000) { engine.load(model) }
            val loadMs = (SystemClock.elapsedRealtimeNanos() - loadStarted) / 1_000_000.0
            assertTrue(engine.initialized)
            assertTrue(engine.status.value is NativeLlmStatus.Ready)

            val output = StringBuilder()
            var firstTokenMs: Double? = null
            val generationStarted = SystemClock.elapsedRealtimeNanos()
            withTimeout(90_000) {
                engine.generate(SYSTEM_PROMPT, "Reply with one short coffee greeting.",
                    options = NativeGenerationOptions(maxTokens = 32, temperature = 0f)).collect { delta ->
                    if (firstTokenMs == null) firstTokenMs = (SystemClock.elapsedRealtimeNanos() - generationStarted) / 1_000_000.0
                    output.append(delta)
                }
            }
            assertTrue("Model must emit actual nonblank text.", output.isNotBlank())
            assertTrue("Streaming must produce a first-token timing receipt.", firstTokenMs != null)

            val firstDelta = CompletableDeferred<Unit>()
            val generation = async(Dispatchers.Default) {
                try {
                    engine.generate(SYSTEM_PROMPT,
                        "Write a long numbered list of 100 different coffee brewing tips. Continue the list without a summary.",
                        options = NativeGenerationOptions(maxTokens = 256, temperature = 0f)).collect { delta ->
                        if (delta.isNotEmpty()) firstDelta.complete(Unit)
                    }
                    "completed"
                } catch (_: CancellationException) {
                    "cancelled"
                }
            }
            withTimeout(90_000) { firstDelta.await() }
            assertTrue("Cancellation must occur during an active generation.", generation.isActive)
            val cancelStarted = SystemClock.elapsedRealtimeNanos()
            engine.cancel()
            val result = withTimeout(5_000) { generation.await() }
            val cancelMs = (SystemClock.elapsedRealtimeNanos() - cancelStarted) / 1_000_000.0
            assertEquals("Native cancellation must reach the generation collector.", "cancelled", result)
            assertTrue("Native cancellation must finish within 5 seconds.", cancelMs <= 5000)
            assertTrue("Cancelled session retains loaded weights.", engine.initialized)
            assertTrue("Cancelled session must release its context/sampler.", engine.status.value is NativeLlmStatus.Ready)

            engine.unload()
            assertFalse(engine.initialized)
            assertEquals(NativeLlmStatus.Unloaded, engine.status.value)
            withTimeout(90_000) { engine.load(model) }
            val afterReload = StringBuilder()
            withTimeout(90_000) {
                engine.generate(SYSTEM_PROMPT, "Reply with one short coffee greeting.",
                    options = NativeGenerationOptions(maxTokens = 24, temperature = 0f)).collect { afterReload.append(it) }
            }
            assertTrue("Reloaded model must still generate actual text.", afterReload.isNotBlank())
            engine.unload()
            assertFalse(engine.initialized)
            assertEquals(NativeLlmStatus.Unloaded, engine.status.value)
            Log.i(TAG, JSONObject().put("check", "gguf_stream_cancel_reload").put("model_sha256", QWEN_SHA256)
                .put("load_ms", loadMs).put("first_token_ms", firstTokenMs)
                .put("cancel_ms", cancelMs).put("output_characters", output.length)
                .put("reload_output_characters", afterReload.length).toString())
        } finally {
            withContext(NonCancellable) { engine.close() }
            // The integrator-owned staged GGUF is preserved for subsequent tests and app QA.
        }
    }

    @Test
    fun givenMalformedGguf_whenLoading_thenReportRecoverableFailureWithoutInitializingNativeModel() = runBlocking<Unit> {
        val directory = ownedDirectory(targetContext)
        val malformed = File(directory, "malformed.gguf")
        val engine = NativeLlmEngine(targetContext)
        try {
            withContext(Dispatchers.IO) { malformed.writeBytes(ByteArray(24)) }
            var rejected = false
            try {
                withTimeout(5_000) { engine.load(malformed) }
            } catch (_: IllegalArgumentException) {
                rejected = true
            }
            assertTrue("Malformed model must be rejected.", rejected)
            assertFalse(engine.initialized)
            assertTrue("Manual callers need an actionable failure state.", engine.status.value is NativeLlmStatus.Failed)
            assertTrue((engine.status.value as NativeLlmStatus.Failed).reason.isNotBlank())
            engine.unload()
            assertEquals(NativeLlmStatus.Unloaded, engine.status.value)
        } finally {
            withContext(NonCancellable + Dispatchers.IO) {
                engine.close()
                deleteOwnedFiles(directory, listOf(malformed))
            }
        }
    }

    private fun ownedDirectory(context: Context): File = File(context.filesDir, "offline-ai-${UUID.randomUUID()}").also {
        check(it.mkdir()) { "Could not create isolated test staging directory." }
    }

    private fun copyAsset(context: Context, name: String, target: File) {
        context.assets.open(name).use { input -> target.outputStream().use { input.copyTo(it) } }
    }

    private fun deleteOwnedFiles(directory: File, files: List<File>) {
        check(directory.parentFile?.canonicalFile == targetContext.filesDir.canonicalFile && directory.name.startsWith("offline-ai-"))
        files.forEach { file ->
            check(file.parentFile?.canonicalFile == directory.canonicalFile)
            if (file.exists() && !file.delete()) Log.w(TAG, "Owned test staging file could not be removed.")
        }
        if (directory.exists() && !directory.delete()) Log.w(TAG, "Owned test staging directory is not empty or could not be removed.")
    }

    private fun sha256(file: File): String {
        val digest = MessageDigest.getInstance("SHA-256")
        file.inputStream().use { input ->
            val buffer = ByteArray(65536)
            while (true) {
                val count = input.read(buffer)
                if (count < 0) break
                digest.update(buffer, 0, count)
            }
        }
        return digest.digest().joinToString("") { String.format(Locale.ROOT, "%02x", it.toInt() and 255) }
    }

    private fun JSONArray.longs(): LongArray = LongArray(length()) { getLong(it) }
    private fun JSONArray.floats(): FloatArray = FloatArray(length()) { getDouble(it).toFloat() }

    private companion object {
        const val TAG = "CoffeeCreatorDeviceTest"
        const val QWEN_SHA256 = "74a4da8c9fdbcd15bd1f6d01d621410d31c6fc00986f5eb687824e7b93d7a9db"
        const val SYSTEM_PROMPT = "You are a concise coffee assistant. Answer in plain text."
    }
}
