package com.coffeecreator.app.core.ai

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class HybridRagEngineTest {

    @Test
    fun `FR-06 Hybrid RAG search retrieves matching chunks and provides citations`() {
        val result = HybridRagEngine.search("brewing water temperature 93")

        assertFalse(result.didAbstain)
        assertTrue(result.citations.isNotEmpty())
        assertTrue(result.text.contains("SCA"))
        assertTrue(result.citations.any { it.contains("SCA Coffee Brewing Handbook") })
    }

    @Test
    fun `FR-06 Hybrid RAG search abstains when no evidence exists in local corpus`() {
        val result = HybridRagEngine.search("quantum teleportation interstellar spaceflight")

        assertTrue(result.didAbstain)
        assertTrue(result.citations.isEmpty())
        assertTrue(result.text.contains("Abstaining to avoid ungrounded statements"))
    }

    @Test
    fun `FR-06 Document ingestion creates hash and searchable chunks`() {
        val title = "Espresso Pressure Profiling"
        val content = "Modern espresso machines allow declining pressure profiles from 9 bar to 6 bar to prevent channeling in late extraction.\n\nPre-infusion at 3 bar allows full puck saturation before ramp-up."

        val doc = HybridRagEngine.ingestDocument(title, content)

        assertTrue(doc.chunkCount >= 2)
        assertTrue(doc.contentHash.isNotEmpty())

        val search = HybridRagEngine.search("declining pressure profiling")
        assertFalse(search.didAbstain)
        assertTrue(search.citations.any { it.contains(title) })
    }
}
