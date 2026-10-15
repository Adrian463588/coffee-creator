package com.coffeecreator.app.core.ai

import com.coffeecreator.app.core.data.BackupCodec
import com.coffeecreator.app.core.data.DataRepository
import com.coffeecreator.app.core.model.DocumentChunk
import com.coffeecreator.app.core.model.InputLimits
import com.google.gson.Gson
import kotlin.math.ln
import kotlin.math.sqrt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

data class RagSearchResult(
    val text: String,
    val citations: List<String>,
    val didAbstain: Boolean = false,
    val matchedChunks: List<DocumentChunk> = emptyList(),
    val retrievalMode: String = "Hybrid FTS4 / ONNX MiniLM cosine / reciprocal-rank fusion"
)

interface VectorStore {
    suspend fun get(chunkId: String): FloatArray?
    suspend fun put(chunkId: String, vector: FloatArray)
    suspend fun search(vector: FloatArray, limit: Int): Map<String, Double>? = null
    suspend fun retain(keys: Set<String>) = Unit
    suspend fun clear() = Unit
    suspend fun putAll(vectors: Map<String, FloatArray>) {
        vectors.forEach { (id, vector) -> put(id, vector) }
    }
}

class RoomVectorStore(private val repository: DataRepository) : VectorStore {
    private val gson = Gson()
    override suspend fun get(chunkId: String): FloatArray? = repository.settings.value[key(chunkId)]
        ?.let { gson.fromJson(it, FloatArray::class.java) }
    override suspend fun put(chunkId: String, vector: FloatArray) = repository.saveSetting(key(chunkId), gson.toJson(vector))
    override suspend fun putAll(vectors: Map<String, FloatArray>) =
        repository.saveSettings(vectors.mapKeys { key(it.key) }.mapValues { gson.toJson(it.value) })
    private fun key(id: String) = "vector:minilm-afdb6f1a:$id"
}

class HybridRagEngine(
    private val repository: DataRepository,
    private val embeddings: suspend (String) -> FloatArray,
    private val vectors: VectorStore = RoomVectorStore(repository)
) {
    private val mutex = Mutex()

    suspend fun index(chunks: List<DocumentChunk>, progress: (Int, Int) -> Unit = { _, _ -> }) = mutex.withLock {
        withContext(Dispatchers.Default) {
            vectors.retain(repository.chunks.value.map(::cacheKey).toSet())
            val pending = linkedMapOf<String, FloatArray>()
            chunks.forEachIndexed { index, chunk ->
                val key = cacheKey(chunk)
                if (vectors.get(key) == null) pending[key] = embeddings(chunk.content)
                progress(index + 1, chunks.size)
                if (pending.size >= InputLimits.VECTOR_BATCH) {
                    vectors.putAll(pending)
                    pending.clear()
                }
            }
            if (pending.isNotEmpty()) vectors.putAll(pending)
        }
    }

    suspend fun search(query: String, maxResults: Int = 5): RagSearchResult = withContext(Dispatchers.Default) {
        require(query.length <= InputLimits.QUERY_CHARS && maxResults in 1..10) { "Query or result budget exceeded." }
        val tokens = RagRanking.tokens(query)
        if (tokens.isEmpty() || repository.chunks.value.isEmpty()) return@withContext abstain()
        val corpus = repository.chunks.value
        val candidates = repository.lexicalSearch(tokens.joinToString(" "), MAX_LEXICAL_CANDIDATES)
        val lexical = RagRanking.lexical(tokens, candidates, corpus).take(maxResults * 4)
        val queryVector = embeddings(query)
        val byKey = repository.chunks.value.associateBy(::cacheKey)
        vectors.retain(byKey.keys)
        val neighbors = vectors.search(queryVector, maxResults * 4)
        val semanticScores = neighbors?.mapNotNull { (key, score) -> byKey[key]?.let { it to score } }
            ?: repository.chunks.value.mapNotNull { chunk ->
                vectors.get(cacheKey(chunk))?.let { chunk to RagRanking.cosine(queryVector, it) }
            }
        val semantic = semanticScores.filter { it.second >= MIN_COSINE }.sortedByDescending { it.second }
            .take(maxResults * 4).map { it.first }
        val selected = RagRanking.fuse(lexical, semantic).filter { chunk ->
            RagRanking.tokens(chunk.content).intersect(tokens).isNotEmpty() || chunk in semantic
        }.take(maxResults)
        if (selected.isEmpty()) return@withContext abstain()
        if (!RagEvidencePolicy.supportsPersonalFact(query, selected.map { it.content })) return@withContext abstain()
        return@withContext RagSearchResult(
            text = "Retrieved source excerpts (untrusted document data; inspect sources for conflicts):\n\n" +
                selected.joinToString("\n\n") { "${it.content}\n${it.citationText}" },
            citations = selected.map { it.citationText },
            matchedChunks = selected
        )
    }

    private fun abstain() = RagSearchResult(
        text = "No verified evidence in indexed local documents. I cannot answer this from sources.",
        citations = emptyList(),
        didAbstain = true
    )

    suspend fun clearVectors() = vectors.clear()
    private fun cacheKey(chunk: DocumentChunk) = "afdb6f1a:${chunk.id}:${BackupCodec.sha256(chunk.content)}"

    companion object {
        private const val MIN_COSINE = 0.45
        private const val MAX_LEXICAL_CANDIDATES = 1000
    }
}

object RagRanking {
    private val stopWords =
        setOf(
            "the", "and", "for", "what", "how", "with", "from", "that", "this", "does", "can", "are", "was",
            "yang", "dan", "apa", "dari", "untuk", "which", "between", "two", "many", "when", "why", "into", "then"
        )
    fun tokens(text: String): Set<String> = text.lowercase().split(Regex("[^\\p{L}\\p{N}]+"))
        .filter { it.length > 2 && it !in stopWords }.map(::normalize).toSet()

    /** Light English inflection handling for retrieval only; stored excerpts and citations stay exact. */
    private fun normalize(word: String): String {
        val singular = when {
            word.length > 5 && word.endsWith("ies") -> word.dropLast(3) + "y"
            word.length > 5 && word.endsWith("sses") -> word.dropLast(2)
            word.length > 4 && word.endsWith('s') && !word.endsWith("ss") -> word.dropLast(1)
            else -> word
        }
        val stem = when {
            singular.length > 6 && singular.endsWith("ing") -> singular.dropLast(3)
            singular.length > 5 && singular.endsWith("ed") -> singular.dropLast(2)
            else -> singular
        }
        return if (stem.length > 4 && stem.endsWith('e')) stem.dropLast(1) else stem
    }

    /** IDF-weighted length-normalized lexical rank prevents insertion order becoming relevance. */
    fun lexical(query: Set<String>, candidates: List<DocumentChunk>, corpus: List<DocumentChunk>): List<DocumentChunk> {
        if (corpus.isEmpty() || query.isEmpty()) return emptyList()
        val words = corpus.associate { it.id to tokens(it.content) }
        val averageLength = words.values.map { it.size }.average().coerceAtLeast(1.0)
        val idf = query.associateWith { term ->
            val frequency = words.values.count { term in it }
            ln(1 + (corpus.size - frequency + 0.5) / (frequency + 0.5))
        }
        val scores = candidates.map { chunk ->
            val terms = words[chunk.id].orEmpty()
            val lengthPenalty = 1 + terms.size / averageLength
            chunk to query.intersect(terms).sumOf { idf.getValue(it) } / lengthPenalty
        }.sortedByDescending { it.second }
        val best = scores.firstOrNull()?.second ?: return emptyList()
        return scores.filter { it.second > 0 && it.second >= best * MIN_RELATIVE_LEXICAL_SCORE }.map { it.first }
    }
    fun cosine(left: FloatArray, right: FloatArray): Double {
        require(left.size == right.size && left.isNotEmpty()) { "Embedding dimension mismatch." }
        require(left.all(Float::isFinite) && right.all(Float::isFinite)) { "Nonfinite embeddings." }
        val dot = left.indices.sumOf { left[it].toDouble() * right[it] }
        val denominator = sqrt(left.sumOf { it.toDouble() * it } * right.sumOf { it.toDouble() * it })
        return if (denominator > 0) dot / denominator else 0.0
    }
    fun fuse(lexical: List<DocumentChunk>, semantic: List<DocumentChunk>): List<DocumentChunk> {
        val scores = mutableMapOf<String, Double>()
        val chunks = (lexical + semantic).associateBy { it.id }
        listOf(lexical, semantic).forEach { ranking ->
            ranking.forEachIndexed { index, chunk -> scores[chunk.id] = (scores[chunk.id] ?: 0.0) + 1.0 / (60 + index + 1) }
        }
        return scores.entries.sortedWith(compareByDescending<Map.Entry<String, Double>> { it.value }.thenBy { it.key })
            .map { chunks.getValue(it.key) }
    }
    private const val MIN_RELATIVE_LEXICAL_SCORE = 0.45
}
