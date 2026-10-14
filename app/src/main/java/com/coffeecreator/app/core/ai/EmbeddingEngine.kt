package com.coffeecreator.app.core.ai

import ai.onnxruntime.OnnxJavaType
import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import ai.onnxruntime.TensorInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import java.io.Closeable
import java.io.File
import java.nio.LongBuffer
import java.text.Normalizer
import java.util.Locale
import kotlin.math.sqrt

/** BERT-uncased WordPiece adapter for the verified local all-MiniLM-L6-v2 ONNX export. */
class OnnxEmbeddingEngine(modelFile: File, vocabFile: File) : Closeable {
    private val environment = OrtEnvironment.getEnvironment()
    private val lock = Any()
    private val tokenizer: BertWordPieceTokenizer
    private val session: OrtSession
    private var closed = false

    init {
        require(modelFile.isFile && modelFile.canRead() && modelFile.length() in 1..128L * 1024 * 1024) {
            "A verified local MiniLM ONNX model is required."
        }
        require(vocabFile.isFile && vocabFile.canRead() && vocabFile.length() in 1..2L * 1024 * 1024) {
            "A verified local BERT vocab.txt is required."
        }
        tokenizer = BertWordPieceTokenizer(vocabFile.readLines(Charsets.UTF_8))
        session = OrtSession.SessionOptions().use { options ->
            options.setIntraOpNumThreads(2)
            options.setInterOpNumThreads(1)
            options.setOptimizationLevel(OrtSession.SessionOptions.OptLevel.ALL_OPT)
            environment.createSession(modelFile.canonicalPath, options)
        }
        try {
            require(session.inputNames.containsAll(listOf("input_ids", "attention_mask")) &&
                session.inputNames.all { it in setOf("input_ids", "attention_mask", "token_type_ids") }) {
                "ONNX model inputs do not match BERT WordPiece."
            }
            session.inputInfo.values.forEach { node ->
                val info = node.info as? TensorInfo ?: error("Embedding input is not a tensor.")
                require(info.type == OnnxJavaType.INT64 && info.shape.size == 2 &&
                    (info.shape[0] == -1L || info.shape[0] == 1L) &&
                    (info.shape[1] == -1L || info.shape[1] in 2..256)) {
                    "Embedding model input shape/type is unsupported."
                }
            }
            require("last_hidden_state" in session.outputNames || "sentence_embedding" in session.outputNames) {
                "ONNX model has no supported sentence/token embedding output."
            }
        } catch (failure: Exception) {
            session.close()
            throw failure
        }
    }

    suspend fun embed(text: String): FloatArray = withContext(Dispatchers.IO) {
        require(text.isNotBlank() && text.length <= 32768) { "Embedding text is empty or exceeds 32768 characters." }
        currentCoroutineContext().ensureActive()
        val result = synchronized(lock) {
            check(!closed) { "Embedding engine has been closed." }
            val info = session.inputInfo.getValue("input_ids").info as TensorInfo
            val fixedLength = info.shape[1].takeIf { it > 0 }?.toInt()
            val input = tokenizer.encode(text, fixedLength ?: 256, padToMax = fixedLength != null)
            val shape = longArrayOf(1, input.ids.size.toLong())
            val tensors = linkedMapOf<String, OnnxTensor>()
            try {
                tensors["input_ids"] = OnnxTensor.createTensor(environment, LongBuffer.wrap(input.ids), shape)
                tensors["attention_mask"] = OnnxTensor.createTensor(environment, LongBuffer.wrap(input.attentionMask), shape)
                if ("token_type_ids" in session.inputNames) {
                    tensors["token_type_ids"] = OnnxTensor.createTensor(environment, LongBuffer.wrap(LongArray(input.ids.size)), shape)
                }
                session.run(tensors).use { outputs ->
                    val tokenOutput = outputs.get("last_hidden_state").orElse(null)
                    val sentenceOutput = outputs.get("sentence_embedding").orElse(null)
                    val tensor = (tokenOutput ?: sentenceOutput) as? OnnxTensor
                        ?: error("ONNX embedding output is not a tensor.")
                    val outputShape = tensor.info.shape
                    require(tensor.info.type == OnnxJavaType.FLOAT) { "Embedding output must be float32." }
                    if (tokenOutput != null) {
                        require(outputShape.size == 3 && outputShape[0] == 1L && outputShape[1] == input.ids.size.toLong() &&
                            outputShape[2] in 1..4096) { "Token embedding output shape is invalid." }
                        val buffer = tensor.floatBuffer ?: error("Embedding output is not floating point.")
                        require(buffer.remaining() == input.ids.size * outputShape[2].toInt()) { "Embedding buffer size is invalid." }
                        val values = FloatArray(buffer.remaining())
                        buffer.get(values)
                        EmbeddingPooling.meanNormalize(values, input.ids.size, outputShape[2].toInt(), input.attentionMask)
                    } else {
                        require(outputShape.size == 2 && outputShape[0] == 1L && outputShape[1] in 1..4096 &&
                            outputShape[1] <= 4096) { "Sentence embedding output shape is invalid." }
                        val buffer = tensor.floatBuffer ?: error("Embedding output is not floating point.")
                        require(buffer.remaining() == outputShape[1].toInt()) { "Embedding buffer size is invalid." }
                        val values = FloatArray(buffer.remaining())
                        buffer.get(values)
                        EmbeddingPooling.normalize(values)
                    }
                }
            } finally {
                tensors.values.forEach { it.close() }
            }
        }
        currentCoroutineContext().ensureActive()
        result
    }

    override fun close() = synchronized(lock) {
        if (!closed) {
            closed = true
            session.close()
        }
    }
}

internal data class BertTokens(val ids: LongArray, val attentionMask: LongArray)

internal class BertWordPieceTokenizer(vocabulary: List<String>) {
    private val vocabulary = vocabulary.withIndex().associate { it.value to it.index.toLong() }
    private val unknown: Long
    private val start: Long
    private val separator: Long
    private val pad: Long
    private val special = setOf("[UNK]", "[CLS]", "[SEP]", "[PAD]", "[MASK]")
    private val specialPattern = Regex("\\[(?:UNK|CLS|SEP|PAD|MASK)]")

    init {
        require(vocabulary.size in 5..65536 && this.vocabulary.size == vocabulary.size && vocabulary.none { it.isEmpty() }) {
            "BERT vocabulary is malformed, duplicated or oversized."
        }
        require(special.all { it in this.vocabulary }) { "BERT vocabulary lacks required special tokens." }
        unknown = this.vocabulary.getValue("[UNK]")
        start = this.vocabulary.getValue("[CLS]")
        separator = this.vocabulary.getValue("[SEP]")
        pad = this.vocabulary.getValue("[PAD]")
    }

    fun encode(text: String, maxTokens: Int = 256, padToMax: Boolean = false): BertTokens {
        require(maxTokens in 2..256 && text.length <= 32768) { "Invalid embedding token/text budget." }
        val ids = mutableListOf(start)
        for (word in basicTokens(text)) {
            val pieces = if (word in special) listOf(vocabulary.getValue(word)) else wordPieces(word)
            for (piece in pieces) {
                if (ids.size >= maxTokens - 1) break
                ids += piece
            }
            if (ids.size >= maxTokens - 1) break
        }
        ids += separator
        val used = ids.size
        if (padToMax) while (ids.size < maxTokens) ids += pad
        return BertTokens(ids.toLongArray(), LongArray(ids.size) { if (it < used) 1L else 0L })
    }

    private fun basicTokens(text: String): List<String> {
        val result = mutableListOf<String>()
        var offset = 0
        for (match in specialPattern.findAll(text)) {
            result += normalizeWords(text.substring(offset, match.range.first))
            result += match.value
            offset = match.range.last + 1
        }
        result += normalizeWords(text.substring(offset))
        return result
    }

    private fun normalizeWords(text: String): List<String> {
        val clean = buildString {
            text.codePoints().forEach { code ->
                val type = Character.getType(code)
                when {
                    code == 0 || code == 0xfffd -> Unit
                    isWhitespace(code) -> append(' ')
                    type == Character.CONTROL.toInt() || type == Character.FORMAT.toInt() -> Unit
                    isCjk(code) -> { append(' '); appendCodePoint(code); append(' ') }
                    else -> appendCodePoint(code)
                }
            }
        }.lowercase(Locale.ROOT)
        val normalized = Normalizer.normalize(clean, Normalizer.Form.NFD)
        val result = mutableListOf<String>()
        val word = StringBuilder()
        fun flush() {
            if (word.isNotEmpty()) { result += word.toString(); word.setLength(0) }
        }
        normalized.codePoints().forEach { code ->
            when {
                Character.getType(code) == Character.NON_SPACING_MARK.toInt() -> Unit
                isWhitespace(code) -> flush()
                isPunctuation(code) -> { flush(); result += String(Character.toChars(code)) }
                else -> word.appendCodePoint(code)
            }
        }
        flush()
        return result
    }

    private fun wordPieces(word: String): List<Long> {
        val codePoints = word.codePoints().toArray()
        if (codePoints.size > 100) return listOf(unknown)
        val pieces = mutableListOf<Long>()
        var startIndex = 0
        while (startIndex < codePoints.size) {
            var end = codePoints.size
            var found: Long? = null
            while (end > startIndex) {
                val piece = (if (startIndex > 0) "##" else "") + String(codePoints, startIndex, end - startIndex)
                found = vocabulary[piece]
                if (found != null) break
                end--
            }
            if (found == null) return listOf(unknown)
            pieces += found
            startIndex = end
        }
        return pieces
    }

    private fun isWhitespace(code: Int): Boolean = code == 32 || code == 9 || code == 10 || code == 13 ||
        Character.getType(code) == Character.SPACE_SEPARATOR.toInt()

    private fun isPunctuation(code: Int): Boolean = code in 33..47 || code in 58..64 || code in 91..96 || code in 123..126 ||
        Character.getType(code) in setOf(
            Character.CONNECTOR_PUNCTUATION.toInt(), Character.DASH_PUNCTUATION.toInt(),
            Character.START_PUNCTUATION.toInt(), Character.END_PUNCTUATION.toInt(),
            Character.INITIAL_QUOTE_PUNCTUATION.toInt(), Character.FINAL_QUOTE_PUNCTUATION.toInt(),
            Character.OTHER_PUNCTUATION.toInt()
        )

    private fun isCjk(code: Int): Boolean = code in 0x4e00..0x9fff || code in 0x3400..0x4dbf ||
        code in 0x20000..0x2a6df || code in 0x2a700..0x2b73f || code in 0x2b740..0x2b81f ||
        code in 0x2b820..0x2ceaf || code in 0xf900..0xfaff || code in 0x2f800..0x2fa1f
}

internal object EmbeddingPooling {
    fun meanNormalize(values: FloatArray, tokens: Int, dimensions: Int, mask: LongArray): FloatArray {
        require(tokens in 1..256 && dimensions in 1..4096 && values.size == tokens * dimensions && mask.size == tokens) {
            "Embedding shape/mask mismatch."
        }
        require(values.all { it.isFinite() } && mask.all { it == 0L || it == 1L }) { "Embedding values or mask are invalid." }
        val count = mask.sum()
        require(count > 0) { "Embedding attention mask is empty." }
        val sums = DoubleArray(dimensions)
        for (token in 0 until tokens) {
            if (mask[token] == 1L) for (dimension in 0 until dimensions) {
                sums[dimension] += values[token * dimensions + dimension]
            }
        }
        return normalize(FloatArray(dimensions) { (sums[it] / count).toFloat() })
    }

    fun normalize(values: FloatArray): FloatArray {
        require(values.isNotEmpty() && values.size <= 4096 && values.all { it.isFinite() }) { "Embedding is empty/nonfinite." }
        val norm = sqrt(values.sumOf { it.toDouble() * it.toDouble() })
        require(norm.isFinite() && norm > 1e-12) { "Embedding has zero or invalid norm." }
        return FloatArray(values.size) { (values[it] / norm).toFloat() }
    }
}
