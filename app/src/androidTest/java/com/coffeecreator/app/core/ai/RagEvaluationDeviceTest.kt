package com.coffeecreator.app.core.ai

import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.provider.Settings
import android.util.Log
import androidx.core.content.FileProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.coffeecreator.app.core.data.CoffeeDatabase
import com.coffeecreator.app.core.data.DataRepository
import com.coffeecreator.app.core.model.CalculationResult
import com.coffeecreator.app.core.model.DocumentChunk
import com.coffeecreator.app.core.model.KnowledgeDocument
import java.io.File
import java.security.MessageDigest
import java.util.Locale
import java.util.UUID
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.abs
import kotlin.math.ceil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/** Authored QA benchmark, not a domain-adjudicated corpus or generated-answer evaluation. */
@RunWith(AndroidJUnit4::class)
class RagEvaluationDeviceTest {
    private val instrumentation get() = InstrumentationRegistry.getInstrumentation()
    private val context get() = instrumentation.targetContext

    @Test
    fun givenOfflineAuthoredCorpus_whenUsingRealOnnxRoomAndObjectBox_thenReportRetrievalAndTools() = runBlocking<Unit> {
        val runId = UUID.randomUUID().toString()
        val databaseName = "rag-evaluation-$runId.db"
        val vectorName = "rag-evaluation-$runId"
        val sourceParent = File(context.filesDir, "qa-sources")
        val sourceDirectory = File(sourceParent, "rag-$runId")
        val vectorParent = File(context.filesDir, "objectbox")
        val vectorDirectory = File(vectorParent, vectorName)
        var database: CoffeeDatabase? = null
        var vectors: ObjectBoxVectorStore? = null
        var embeddings: OnnxEmbeddingEngine? = null
        var sourceDirectoryOwned = false
        var databaseOwned = false
        var vectorDirectoryOwned = false
        try {
            withTimeout(600_000) {
                assertOffline()
                val fixtureBytes = withContext(Dispatchers.IO) {
                    instrumentation.context.assets.open("rag-evaluation.json").use { it.readBytes() }
                }
                require(fixtureBytes.size in 1..1_000_000) { "RAG fixture exceeds its byte budget." }
                val fixture = JSONObject(fixtureBytes.toString(Charsets.UTF_8))
                validateFixture(fixture)
                withContext(Dispatchers.IO) {
                    check(sourceParent.canonicalFile.parentFile == context.filesDir.canonicalFile)
                    check(sourceParent.isDirectory || sourceParent.mkdir())
                    check(!sourceDirectory.exists() && sourceDirectory.mkdir())
                    sourceDirectoryOwned = true
                    check(!context.getDatabasePath(databaseName).exists())
                    databaseOwned = true
                    check(vectorParent.canonicalFile.parentFile == context.filesDir.canonicalFile)
                    check(!vectorDirectory.exists())
                    vectorDirectoryOwned = true
                }

                val model = fixture.getJSONObject("model")
                val modelFile = File(sourceDirectory, "model_quantized.onnx")
                val vocabularyFile = File(sourceDirectory, "vocab.txt")
                withContext(Dispatchers.IO) {
                    copyAsset(model.getString("model_asset"), modelFile)
                    copyAsset(model.getString("vocab_asset"), vocabularyFile)
                    assertEquals("Actual ONNX bytes", model.getString("model_sha256"), sha256(modelFile))
                    assertEquals("Actual vocabulary bytes", model.getString("vocab_sha256"), sha256(vocabularyFile))
                }
                val localEmbeddings = withContext(Dispatchers.IO) { OnnxEmbeddingEngine(modelFile, vocabularyFile) }
                embeddings = localEmbeddings
                val calls = AtomicInteger()
                val embed: suspend (String) -> FloatArray = { text ->
                    calls.incrementAndGet()
                    localEmbeddings.embed(text)
                }
                val initialDatabase = CoffeeDatabase.open(context, databaseName)
                database = initialDatabase
                val initialRepository = DataRepository(context, initialDatabase)
                initialRepository.initialize()
                val sourceChunks = linkedMapOf<String, DocumentChunk>()
                val documents = fixture.getJSONArray("documents")
                for (index in 0 until documents.length()) {
                    val source = documents.getJSONObject(index)
                    val (document, chunks) = withContext(Dispatchers.IO) { stageSource(sourceDirectory, source) }
                    initialRepository.addDocumentWithChunks(document, chunks)
                    chunks.forEach { check(sourceChunks.put(it.id, it) == null) }
                }
                validateGold(fixture.getJSONArray("questions"), sourceChunks)
                assertEquals("Isolated Room chunk count", sourceChunks.size, initialRepository.chunks.value.size)
                val initialVectors = ObjectBoxVectorStore(context, vectorName)
                vectors = initialVectors
                val initialRag = HybridRagEngine(initialRepository, embed, initialVectors)
                val indexStarted = SystemClock.elapsedRealtimeNanos()
                withTimeout(180_000) { initialRag.index(initialRepository.chunks.value) }
                val indexMs = elapsedMs(indexStarted)
                assertEquals("Every fresh chunk uses actual ONNX inference", sourceChunks.size, calls.get())

                val reopenStarted = SystemClock.elapsedRealtimeNanos()
                withContext(Dispatchers.IO) {
                    initialVectors.close()
                    vectors = null
                    initialDatabase.close()
                    database = null
                }
                val reopenedDatabase = CoffeeDatabase.open(context, databaseName)
                database = reopenedDatabase
                val repository = DataRepository(context, reopenedDatabase)
                repository.initialize()
                assertEquals("Room reopen preserves exact sources", sourceChunks, repository.chunks.value.associateBy { it.id })
                assertTrue(
                    "Real Room FTS4 must retrieve the source containing refractometer",
                    repository.lexicalSearch("refractometer", 20).any { it.id == "tds:chunk:0" }
                )
                val reopenedVectors = ObjectBoxVectorStore(context, vectorName)
                vectors = reopenedVectors
                val rag = HybridRagEngine(repository, embed, reopenedVectors)
                val beforeReindex = calls.get()
                withTimeout(180_000) { rag.index(repository.chunks.value) }
                assertEquals("ObjectBox reopen preserves every actual embedding", beforeReindex, calls.get())
                val reopenMs = elapsedMs(reopenStarted)
                val tools = ToolExecutor(
                    { repository.beans.value },
                    { repository.ingredients.value },
                    { repository.recipes.value },
                    { query -> rag.search(query, maxResults = 5) }
                )
                Log.i(
                    TAG,
                    JSONObject().put("check", "rag_setup").put("run_id", runId)
                        .put("fixture_sha256", sha256(fixtureBytes)).put("documents", documents.length())
                        .put("chunks", sourceChunks.size).put("source_kind", "actual_txt_via_debug_fileprovider")
                        .put("txt_page_number", 0).put("room_fts4_verified", true)
                        .put("objectbox_reopen_embedding_cache_verified", true)
                        .put("index_embedding_calls", beforeReindex).put("index_ms", indexMs)
                        .put("storage_reopen_ms", reopenMs).toString()
                )

                val scores = CATEGORY_COUNTS.mapValues { CategoryScore() }
                val latencies = mutableListOf<Double>()
                val numericScores = mutableListOf<NumericScore>()
                val questions = fixture.getJSONArray("questions")
                for (index in 0 until questions.length()) {
                    assertOffline()
                    val item = questions.getJSONObject(index)
                    val required = item.getJSONArray("required_chunk_ids").strings().toSet()
                    val started = SystemClock.elapsedRealtimeNanos()
                    val result = withTimeout(30_000) { rag.search(item.getString("question"), maxResults = 5) }
                    val latency = elapsedMs(started)
                    latencies += latency
                    assertTrue("Retrieval must respect the five-chunk budget", result.matchedChunks.size <= 5)
                    assertEquals("Returned chunk IDs must be unique", result.matchedChunks.size, result.matchedChunks.map { it.id }.toSet().size)
                    result.matchedChunks.forEach { actual ->
                        assertEquals("${item.getString("id")}: source integrity", sourceChunks[actual.id], actual)
                        assertEquals("TXT source has no page", 0, actual.pageNumber)
                        assertFalse("TXT citation must not invent a page", actual.citationText.contains(", page "))
                    }
                    assertEquals("Citations must exactly match returned sources", result.matchedChunks.map { it.citationText }, result.citations)
                    assertEquals("Abstention and empty evidence must agree", result.matchedChunks.isEmpty(), result.didAbstain)
                    val retrieved = result.matchedChunks.map { it.id }.toSet()
                    val relevant = retrieved.intersect(required).size
                    val category = item.getString("category")
                    scores.getValue(category).record(required, retrieved, result.didAbstain)
                    val receipt = JSONObject().put("check", "rag_question").put("id", item.getString("id"))
                        .put("category", category).put("required_chunk_ids", JSONArray(required.toList()))
                        .put("retrieved_chunk_ids", JSONArray(retrieved.toList())).put("citations", JSONArray(result.citations))
                        .put("recall_at_5", if (required.isEmpty()) JSONObject.NULL else relevant.toDouble() / required.size)
                        .put("retrieved_source_precision", fraction(relevant, retrieved.size))
                        .put("did_abstain", result.didAbstain).put("expected_abstention", item.getBoolean("expected_abstention"))
                        .put("retrieval_ms", latency)
                    if (category == "numerical_tool") {
                        val numeric = withTimeout(5_000) { evaluateTool(item.getJSONObject("tool"), tools) }
                        numericScores += numeric
                        receipt.put("tool_name", item.getJSONObject("tool").getString("name"))
                            .put("tool_values", numeric.actual).put("arithmetic_correct", numeric.arithmeticCorrect ?: JSONObject.NULL)
                            .put("tool_output_valid", numeric.outputValid).put("tool_metadata_valid", numeric.metadataValid)
                            .put("tool_request_origin", "authored_fixture_not_llm")
                    }
                    Log.i(TAG, receipt.toString())
                }
                assertOffline()
                val answerable = scores.filterKeys { it != "unanswerable" }.values
                val unanswerable = scores.getValue("unanswerable")
                val correctAbstentions = unanswerable.abstentions
                val falseAbstentions = answerable.sumOf { it.abstentions }
                val answerableRetrieved = answerable.sumOf { it.retrieved }
                val relevant = answerable.sumOf { it.relevant }
                val categorySummary = JSONObject()
                scores.forEach { (category, score) -> categorySummary.put(category, score.toJson(category != "unanswerable")) }
                val summary = JSONObject().put("check", "rag_summary").put("run_id", runId)
                    .put("fixture_id", fixture.getString("fixture_id")).put("fixture_sha256", sha256(fixtureBytes))
                    .put("adjudication_status", fixture.getString("adjudication_status"))
                    .put("model_sha256", model.getString("model_sha256")).put("vocab_sha256", model.getString("vocab_sha256"))
                    .put("device", "${Build.MANUFACTURER} ${Build.MODEL}").put("api", Build.VERSION.SDK_INT)
                    .put("abis", JSONArray(Build.SUPPORTED_ABIS.toList())).put("questions", questions.length())
                    .put("answerable_questions", answerable.sumOf { it.questions }).put("categories", categorySummary)
                    .put("recall_at_5", answerable.sumOf { it.recall } / answerable.sumOf { it.questions })
                    .put("retrieved_source_precision_answerable", fraction(relevant, answerableRetrieved))
                    .put("retrieved_source_precision_all", fraction(relevant, answerableRetrieved + unanswerable.retrieved))
                    .put("true_abstentions", correctAbstentions).put("unanswerable_questions", unanswerable.questions)
                    .put("false_abstentions", falseAbstentions)
                    .put("abstention_precision", fraction(correctAbstentions, correctAbstentions + falseAbstentions))
                    .put("abstention_recall", fraction(correctAbstentions, unanswerable.questions))
                    .put("numeric_cases", numericScores.size).put("correct_numeric_cases", numericScores.count { it.correct })
                    .put("incorrect_arithmetic_cases", numericScores.count { it.arithmeticCorrect == false })
                    .put("tool_output_failures", numericScores.count { !it.outputValid })
                    .put("tool_metadata_failures", numericScores.count { it.outputValid && !it.metadataValid })
                    .put("generated_answer_faithfulness", "not_measured").put("llm_tool_selection", "not_measured")
                    .put("query_median_ms", median(latencies)).put("query_p95_ms", percentile95(latencies))
                    .put("index_ms", indexMs).put("storage_reopen_ms", reopenMs)
                    .put("airplane_mode_and_no_active_network_checked", true)
                Log.i(TAG, summary.toString())
                instrumentation.sendStatus(0, Bundle().apply { putString("rag_evaluation_summary", summary.toString()) })
                assertEquals("Direct-fact golden source hits; inspect measured receipts for misses", 40, scores.getValue("direct_fact").complete)
                assertEquals("Every authored numeric case executes exact typed Kotlin math with metadata", 20, numericScores.count { it.correct })
            }
        } finally {
            withContext(NonCancellable + Dispatchers.IO) {
                try {
                    vectors?.close()
                } finally {
                    try {
                        database?.close()
                    } finally {
                        try {
                            embeddings?.close()
                        } finally {
                            try {
                                if (databaseOwned) {
                                    check(databaseName == "rag-evaluation-$runId.db")
                                    check(context.deleteDatabase(databaseName) || !context.getDatabasePath(databaseName).exists())
                                }
                            } finally {
                                try {
                                    if (vectorDirectoryOwned) deleteOwnedTree(vectorDirectory, vectorParent, vectorName)
                                } finally {
                                    if (sourceDirectoryOwned) deleteOwnedTree(sourceDirectory, sourceParent, "rag-$runId")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun assertOffline() {
        assertEquals("Enable airplane mode before running RAG QA", 1, Settings.Global.getInt(context.contentResolver, Settings.Global.AIRPLANE_MODE_ON, 0))
        val connectivity = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        assertTrue("Disable Wi-Fi/Ethernet and all active networks for RAG QA", connectivity.activeNetwork == null)
    }

    private fun validateFixture(fixture: JSONObject) {
        assertEquals(1, fixture.getInt("schema_version"))
        assertEquals("authored_qa_not_independently_adjudicated", fixture.getString("adjudication_status"))
        assertEquals("not_measured", fixture.getString("generated_answer_faithfulness"))
        assertEquals(5, fixture.getInt("max_results"))
        val questions = fixture.getJSONArray("questions")
        assertEquals(100, questions.length())
        val ids = mutableSetOf<String>()
        val counts = mutableMapOf<String, Int>()
        for (index in 0 until questions.length()) {
            val question = questions.getJSONObject(index)
            val category = question.getString("category")
            require(category in CATEGORY_COUNTS && ids.add(question.getString("id")))
            require(question.getString("question").length in 10..4096 && question.getString("expected_answer").isNotBlank())
            assertEquals(category == "unanswerable", question.getBoolean("expected_abstention"))
            assertEquals(category == "numerical_tool", question.has("tool"))
            counts[category] = (counts[category] ?: 0) + 1
        }
        assertEquals(CATEGORY_COUNTS, counts)
        val declaredCounts = fixture.getJSONObject("category_counts")
        assertEquals(CATEGORY_COUNTS.keys, declaredCounts.keys().asSequence().toSet())
        CATEGORY_COUNTS.forEach { (category, count) -> assertEquals(count, declaredCounts.getInt(category)) }
        val model = fixture.getJSONObject("model")
        assertEquals("embedding/model_quantized.onnx", model.getString("model_asset"))
        assertEquals("embedding/vocab.txt", model.getString("vocab_asset"))
        require(fixture.getJSONArray("documents").length() in 1..100)
    }

    private fun stageSource(directory: File, source: JSONObject): Pair<KnowledgeDocument, List<DocumentChunk>> {
        val id = source.getString("id")
        require(id.matches(Regex("[a-z][a-z0-9-]{0,80}")))
        val file = File(directory, "$id.txt")
        check(!file.exists()) { "Duplicate fixture document ID." }
        val sourceText = source.getString("text")
        require(sourceText.length in 1..32_000 && !sourceText.contains('\r'))
        file.writeText(sourceText, Charsets.UTF_8)
        val uri = FileProvider.getUriForFile(context, "com.coffeecreator.app.qa.files", file)
        val bytes = checkNotNull(context.contentResolver.openInputStream(uri)).use { it.readBytes() }
        val actualText = bytes.toString(Charsets.UTF_8)
        assertEquals("Fixture source URI must return actual TXT bytes", sourceText, actualText)
        val title = source.getString("title")
        require(title.startsWith("QA "))
        val chunks = actualText.split("\n\n").mapIndexed { index, text ->
            DocumentChunk("$id:chunk:$index", id, title, pageNumber = 0, chunkIndex = index, content = text)
        }
        val document = KnowledgeDocument(
            id = id,
            title = title,
            sourceUri = uri.toString(),
            fileType = "TXT",
            pageCount = 0,
            chunkCount = chunks.size,
            contentHash = sha256(bytes),
            importedAt = 0
        )
        return document to chunks
    }

    private fun validateGold(questions: JSONArray, chunks: Map<String, DocumentChunk>) {
        for (index in 0 until questions.length()) {
            val question = questions.getJSONObject(index)
            val required = question.getJSONArray("required_chunk_ids").strings()
            assertEquals("Gold chunk IDs must be unique", required.size, required.toSet().size)
            require(required.all { it in chunks }) { "Gold source is not present in actual TXT chunks." }
            if (question.getString("category") in setOf("cross_document", "conflicting_source")) {
                assertEquals("Cross-document/conflict evidence uses two actual documents", 2, required.map { chunks.getValue(it).documentId }.toSet().size)
            }
            assertEquals("Only unanswerable questions have empty gold sets", question.getBoolean("expected_abstention"), required.isEmpty())
        }
    }

    private suspend fun evaluateTool(spec: JSONObject, executor: ToolExecutor): NumericScore {
        val input = spec.getJSONObject("arguments")
        val arguments = input.keys().asSequence().associateWith { input.getDouble(it) }
        val result = executor.execute(spec.getString("name"), arguments)
        val value = result.structuredData as? CalculationResult
        if (!result.isSuccess || result.requiresUserConfirmation || value == null) {
            return NumericScore(null, false, false, JSONObject().put("error", result.output))
        }
        val actual = when (value) {
            is CalculationResult.BrewRatio -> JSONObject().put("ratioDenominator", value.ratioDenominator).put("ratioString", value.ratioString)
            is CalculationResult.ExtractionYield -> JSONObject().put("extractionYieldPercent", value.extractionYieldPercent)
            is CalculationResult.Dilution -> JSONObject().put("finalMassGrams", value.finalMassGrams).put("finalTdsPercent", value.finalTdsPercent)
            is CalculationResult.Concentration -> JSONObject().put("massFractionPercent", value.massFractionPercent).put("ppm", value.ppm)
            is CalculationResult.UnitConversion -> JSONObject().put("massGrams", value.massGrams)
        }
        val expected = spec.getJSONObject("expected")
        val tolerance = spec.getDouble("absolute_tolerance")
        require(tolerance > 0 && tolerance <= 1e-9)
        val arithmeticCorrect = expected.keys().asSequence().all { key ->
            val gold = expected.get(key)
            val output = actual.opt(key)
            if (gold is Number && output is Number) {
                output.toDouble().isFinite() && abs(gold.toDouble() - output.toDouble()) <= tolerance
            } else {
                gold is String && gold == output
            }
        }
        val metadata = value.metadata
        val metadataValid = metadata.methodVersion.isNotBlank() && metadata.uncertainty.isNotBlank() &&
            metadata.exactInputs.keys == arguments.keys && metadata.inputUnits.keys == arguments.keys &&
            metadata.inputProvenance.keys == arguments.keys && metadata.inputUnits.values.all { it.isNotBlank() } &&
            metadata.inputProvenance.values.all { it.isNotBlank() } && metadata.exactInputs.all { (key, exact) ->
                exact.toBigDecimalOrNull()?.compareTo(arguments.getValue(key).toString().toBigDecimal()) == 0
            }
        return NumericScore(arithmeticCorrect, true, metadataValid, actual)
    }

    private fun copyAsset(asset: String, destination: File) {
        context.assets.open(asset).use { input -> destination.outputStream().use { input.copyTo(it) } }
    }

    private fun deleteOwnedTree(directory: File, parent: File, name: String) {
        check(directory.name == name && directory.canonicalFile.parentFile == parent.canonicalFile)
        check(parent.canonicalFile.parentFile == context.filesDir.canonicalFile)
        if (!directory.exists()) return
        val ownedPath = directory.canonicalPath
        directory.walkBottomUp().forEach { file ->
            val canonical = file.canonicalPath
            check(canonical == ownedPath || canonical.startsWith(ownedPath + File.separator))
            check(file.delete()) { "Could not delete owned RAG QA file: ${file.name}" }
        }
    }

    private fun sha256(file: File): String {
        val digest = MessageDigest.getInstance("SHA-256")
        file.inputStream().use { input ->
            val buffer = ByteArray(65_536)
            while (true) {
                val count = input.read(buffer)
                if (count < 0) break
                digest.update(buffer, 0, count)
            }
        }
        return digest.digest().hex()
    }

    private fun sha256(bytes: ByteArray): String = MessageDigest.getInstance("SHA-256").digest(bytes).hex()
    private fun ByteArray.hex(): String = joinToString("") { String.format(Locale.ROOT, "%02x", it.toInt() and 255) }
    private fun JSONArray.strings(): List<String> = List(length()) { getString(it) }
    private fun elapsedMs(started: Long): Double = (SystemClock.elapsedRealtimeNanos() - started) / 1_000_000.0
    private fun fraction(numerator: Int, denominator: Int): Any = if (denominator == 0) JSONObject.NULL else numerator.toDouble() / denominator
    private fun median(values: List<Double>): Double = values.sorted().let { (it[(it.size - 1) / 2] + it[it.size / 2]) / 2 }
    private fun percentile95(values: List<Double>): Double = values.sorted()[(ceil(values.size * 0.95).toInt() - 1).coerceAtLeast(0)]

    private data class NumericScore(val arithmeticCorrect: Boolean?, val outputValid: Boolean, val metadataValid: Boolean, val actual: JSONObject) {
        val correct: Boolean get() = arithmeticCorrect == true && outputValid && metadataValid
    }

    private class CategoryScore {
        var questions = 0
        var recall = 0.0
        var retrieved = 0
        var relevant = 0
        var complete = 0
        var abstentions = 0

        fun record(required: Set<String>, found: Set<String>, didAbstain: Boolean) {
            questions++
            retrieved += found.size
            val matches = found.intersect(required).size
            relevant += matches
            if (required.isNotEmpty()) {
                recall += matches.toDouble() / required.size
                if (matches == required.size) complete++
            }
            if (didAbstain) abstentions++
        }

        fun toJson(answerable: Boolean): JSONObject = JSONObject().put("questions", questions)
            .put("recall_at_5", if (answerable) recall / questions else JSONObject.NULL)
            .put("complete_required_set_cases", if (answerable) complete else JSONObject.NULL)
            .put("relevant_retrieved_chunks", relevant).put("retrieved_chunks", retrieved)
            .put("retrieved_source_precision", if (retrieved == 0) JSONObject.NULL else relevant.toDouble() / retrieved)
            .put("abstentions", abstentions)
    }

    private companion object {
        const val TAG = "CoffeeCreatorRagEval"
        val CATEGORY_COUNTS = linkedMapOf(
            "direct_fact" to 40,
            "cross_document" to 20,
            "numerical_tool" to 20,
            "conflicting_source" to 10,
            "unanswerable" to 10
        )
    }
}
