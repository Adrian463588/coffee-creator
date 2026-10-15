package com.coffeecreator.app.core.ai

import com.coffeecreator.app.core.model.DocumentChunk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RagEvidencePolicyTest {
    @Test fun givenMethodGuidance_whenAskedForUnrecordedPersonalTds_thenAbstain() {
        assertFalse(
            RagEvidencePolicy.supportsPersonalFact(
                "What measured TDS did today's brew produce?",
                listOf("Measured TDS means dissolved solids. Example beverage TDS1.30% is used in the extraction formula.")
            )
        )
        assertFalse(
            RagEvidencePolicy.supportsPersonalFact(
                "What allergens are certified absent from my supplier's actual aroma solution?",
                listOf("Allergens must be documented by suppliers; food grade status is required.")
            )
        )
    }

    @Test fun givenActualIdentifiedObservation_whenAskedForMeasuredData_thenRecordCanSupportRetrieval() {
        assertTrue(
            RagEvidencePolicy.supportsPersonalFact(
                "What measured TDS did my brew produce?",
                listOf("Brew ID: Q17. On2026-10-15 the measured beverage TDS was1.30%, measured beverage mass270g.")
            )
        )
        assertTrue(RagEvidencePolicy.supportsPersonalFact("What is water coffee ratio?", listOf("Ratio is mass-based.")))
    }

    @Test fun givenObservations_whenAskedForGuaranteedFutureScore_thenNoDeterministicPrediction() {
        assertFalse(RagEvidencePolicy.supportsPersonalFact("What score will my panel assign?", listOf("Observed sweetness7 in2026-10-15.")))
    }

    @Test fun givenCommonEarlyRowsAndRareLateRecords_whenRanked_thenRelevantRecordWins() {
        val common = DocumentChunk("a", "a", "Ratio guidance", 0, 0, "Coffee brew dose and water ratio")
        val specific = DocumentChunk("z", "z", "Experiment record", 0, 0, "The two recorded temperature plans disagree:93 and96C")
        val query = RagRanking.tokens("What temperatures disagree between two plans?")
        assertEquals(specific, RagRanking.lexical(query, listOf(common, specific), listOf(common, specific)).first())
        assertEquals(RagRanking.tokens("variety temperature mass"), RagRanking.tokens("varieties temperatures masses"))
    }
}
