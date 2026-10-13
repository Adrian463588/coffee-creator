package com.coffeecreator.app.core.ai

import com.coffeecreator.app.core.data.DataRepository
import com.coffeecreator.app.core.model.DocumentChunk
import com.coffeecreator.app.core.model.KnowledgeDocument
import java.security.MessageDigest
import java.util.UUID

data class RagSearchResult(
    val text: String,
    val citations: List<String>,
    val didAbstain: Boolean = false,
    val matchedChunks: List<DocumentChunk> = emptyList()
)

object HybridRagEngine {

    /**
     * FR-06 Local Hybrid RAG search.
     * Computes lexical + keyword token overlap against local document chunks.
     * Returns exact source citations or explicitly abstains if evidence is insufficient.
     */
    fun search(query: String, maxResults: Int = 3): RagSearchResult {
        val queryTokens = query.lowercase()
            .split(Regex("[^a-zA-Z0-9]+"))
            .filter { it.length > 2 }
            .toSet()

        if (queryTokens.isEmpty()) {
            return RagSearchResult(
                text = "No query keywords detected.",
                citations = emptyList(),
                didAbstain = true
            )
        }

        val allChunks = DataRepository.chunks.value

        // Score chunks based on token frequency and coverage
        val scoredChunks = allChunks.map { chunk ->
            val chunkText = chunk.content.lowercase()
            var score = 0
            for (token in queryTokens) {
                if (chunkText.contains(token)) {
                    score += 1
                }
            }
            Pair(chunk, score)
        }
            .filter { it.second > 0 }
            .sortedByDescending { it.second }
            .take(maxResults)

        if (scoredChunks.isEmpty() || scoredChunks.first().second < 1) {
            return RagSearchResult(
                text = "No verified evidence found in local coffee reference documents for this question. Abstaining to avoid ungrounded statements.",
                citations = emptyList(),
                didAbstain = true
            )
        }

        val citations = scoredChunks.map { it.first.citationText }
        val responseBuilder = StringBuilder()
        responseBuilder.append("Evidence from local coffee documents:\n\n")

        scoredChunks.forEachIndexed { index, pair ->
            val chunk = pair.first
            responseBuilder.append("${index + 1}. \"${chunk.content}\"\n")
            responseBuilder.append("   Source: ${chunk.citationText}\n\n")
        }

        return RagSearchResult(
            text = responseBuilder.toString().trim(),
            citations = citations,
            didAbstain = false,
            matchedChunks = scoredChunks.map { it.first }
        )
    }

    /**
     * Ingests a new document, chunks it by paragraph / length, and adds to repository.
     */
    fun ingestDocument(title: String, content: String, sourceUri: String = "content://saf/imported_manual.txt"): KnowledgeDocument {
        val hash = MessageDigest.getInstance("SHA-256")
            .digest(content.toByteArray())
            .joinToString("") { "%02x".format(it) }

        val docId = UUID.randomUUID().toString()
        val paragraphs = content.split(Regex("\n\n+")).filter { it.isNotBlank() }

        val chunks = paragraphs.mapIndexed { index, p ->
            val pageNumber = (index / 3) + 1
            DocumentChunk(
                id = UUID.randomUUID().toString(),
                documentId = docId,
                documentTitle = title,
                pageNumber = pageNumber,
                chunkIndex = index + 1,
                content = p.trim()
            )
        }

        val doc = KnowledgeDocument(
            id = docId,
            title = title,
            sourceUri = sourceUri,
            fileType = "TXT",
            pageCount = (chunks.size / 3) + 1,
            chunkCount = chunks.size,
            contentHash = hash,
            importedAt = System.currentTimeMillis()
        )

        DataRepository.addDocumentWithChunks(doc, chunks)
        return doc
    }
}
