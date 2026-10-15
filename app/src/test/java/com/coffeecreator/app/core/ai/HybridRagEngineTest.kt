package com.coffeecreator.app.core.ai

import com.coffeecreator.app.core.model.DocumentChunk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Test

class HybridRagEngineTest {
    @Test fun givenLexicalAndSemanticRankings_whenFused_thenSharedSourceRanksFirst() {
        val a = chunk("a")
        val b = chunk("b")
        val c = chunk("c")
        assertEquals(listOf(b, a, c), RagRanking.fuse(listOf(a, b), listOf(b, c)))
    }

    @Test fun givenNormalizedEmbeddings_whenCompared_thenCosineMatchesReference() {
        assertEquals(1.0, RagRanking.cosine(floatArrayOf(1f, 0f), floatArrayOf(1f, 0f)), 1e-9)
        assertEquals(0.0, RagRanking.cosine(floatArrayOf(1f, 0f), floatArrayOf(0f, 1f)), 1e-9)
    }

    @Test fun givenInvalidVector_whenCompared_thenRejectWithoutSilentScore() {
        assertThrows(IllegalArgumentException::class.java) { RagRanking.cosine(floatArrayOf(Float.NaN), floatArrayOf(1f)) }
        assertThrows(IllegalArgumentException::class.java) { RagRanking.cosine(floatArrayOf(1f), floatArrayOf(1f, 2f)) }
    }

    @Test fun givenCommonWords_whenQueryTokenized_thenDoNotCreateFalseEvidence() {
        assertEquals(emptySet<String>(), RagRanking.tokens("What are the and for?"))
        assertFalse("temp" in RagRanking.tokens("temperature"))
    }
    private fun chunk(id: String) = DocumentChunk(id, "source", "User document", 0, 1, "Coffee extraction")
}
