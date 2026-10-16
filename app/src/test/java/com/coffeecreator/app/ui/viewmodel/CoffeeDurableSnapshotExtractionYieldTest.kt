package com.coffeecreator.app.ui.viewmodel

import com.coffeecreator.app.core.journal.BrewJournalEntry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class CoffeeDurableSnapshotExtractionYieldTest {
    @Test
    fun givenStandaloneExtractionYield_whenDecodingBackup_thenRejectWithoutMeasurementEvidence() {
        val unsupported = journal(extractionYieldPercent = 19.5)

        assertThrows(IllegalArgumentException::class.java) { decode(unsupported) }
    }

    @Test
    fun givenYieldThatDisagreesWithMeasuredInputs_whenDecodingBackup_thenReject() {
        val unsupported = journal(
            beverageMassGrams = 270.0,
            measuredTdsPercent = 1.30,
            extractionYieldPercent = 18.0
        )

        assertThrows(IllegalArgumentException::class.java) { decode(unsupported) }
    }

    @Test
    fun givenYieldDerivedFromMeasuredInputs_whenDecodingBackup_thenPreserveEvidence() {
        val supported = journal(
            beverageMassGrams = 270.0,
            measuredTdsPercent = 1.30,
            extractionYieldPercent = 19.5
        )

        val decoded = decode(supported)

        assertEquals(19.5, decoded.journalEntries.single().extractionYieldPercent!!, 0.0)
    }

    private fun decode(entry: BrewJournalEntry): CoffeeDurableSnapshot = CoffeeDurableSnapshotCodec.decode(
        mapOf("journal_entries" to CoffeeDurableSnapshotCodec.serializer.toJson(listOf(entry)))
    )

    private fun journal(beverageMassGrams: Double? = null, measuredTdsPercent: Double? = null, extractionYieldPercent: Double? = null) =
        BrewJournalEntry(
            id = "journal-1",
            brewMethod = "Pour Over",
            doseGrams = 18.0,
            waterGrams = 300.0,
            beverageMassGrams = beverageMassGrams,
            measuredTdsPercent = measuredTdsPercent,
            extractionYieldPercent = extractionYieldPercent,
            brewDurationSeconds = 180
        )
}
