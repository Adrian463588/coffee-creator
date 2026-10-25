package com.coffeecreator.app.core.journal

import com.coffeecreator.app.core.model.BrewStep
import com.coffeecreator.app.core.model.Recipe
import com.coffeecreator.app.ui.viewmodel.CoffeeDurableSnapshotCodec
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class BrewReplayFactoryTest {
    private val original = Recipe(
        id = "original-v60",
        name = "V60 two pours",
        doseGrams = 20.0,
        waterGrams = 300.0,
        beverageMassGrams = 270.0,
        measuredTdsPercent = 1.3,
        firstDropSeconds = 8,
        brewSteps = listOf(
            BrewStep(1, "Bloom", 60.0, 45, cumulativeMassGrams = 60.0),
            BrewStep(2, "Main pour", 240.0, 135, startOffsetSeconds = 45, cumulativeMassGrams = 300.0)
        )
    )

    @Test
    fun givenHistoricalSnapshotAndEditedSource_whenReplayed_thenOriginalPlanWinsWithoutOldMeasurements() {
        val entry = BrewCompletionRecordFactory.fromTimerCompletion(original, 185, "Actual bean")
        val changed = original.copy(doseGrams = 25.0, waterGrams = 400.0, brewSteps = emptyList())

        val replay = BrewReplayFactory.fromJournal(entry, changed)

        assertEquals(original.brewSteps, replay.brewSteps)
        assertEquals(original.doseGrams, replay.doseGrams, 0.0)
        assertNull(replay.beverageMassGrams)
        assertNull(replay.measuredTdsPercent)
        assertNull(replay.firstDropSeconds)
        assertEquals(original, entry.recipeSnapshot)
        assertEquals(185, entry.brewDurationSeconds)
        assertNull(entry.rating)
    }

    @Test
    fun givenLegacyEntryWithoutSource_whenReplayed_thenOnlyRecordedPlanValuesAreReconstructed() {
        val entry = BrewJournalEntry(
            id = "legacy-v60",
            brewMethod = "Pour Over (V60)",
            equipment = "Hario V60",
            doseGrams = 18.0,
            waterGrams = 270.0,
            brewDurationSeconds = 185,
            measuredTdsPercent = 1.3,
            beverageMassGrams = 240.0,
            grinderModel = "User grinder",
            grindSetting = "20 clicks"
        )

        val replay = BrewReplayFactory.fromJournal(entry)

        assertEquals(270.0, replay.waterGrams, 0.0)
        assertEquals(185, replay.brewTimeSeconds)
        assertEquals(entry.equipment, replay.equipment)
        assertEquals(entry.grinderModel, replay.grinderModel)
        assertTrue(replay.brewSteps.isEmpty())
        assertTrue(replay.notes.contains("Original pour schedule is unavailable"))
        assertNull(replay.measuredTdsPercent)
        assertNull(replay.beverageMassGrams)
    }

    @Test
    fun givenInvalidReplayOrMismatchedSnapshot_whenResolved_thenRejectsInsteadOfSubstitutingAnotherRecipe() {
        val entry = BrewCompletionRecordFactory.fromTimerCompletion(original, 180, null)
        assertThrows(IllegalArgumentException::class.java) {
            BrewReplayFactory.fromJournal(entry.copy(recipeId = "unrelated"))
        }
        assertThrows(IllegalArgumentException::class.java) {
            BrewReplayFactory.fromJournal(entry.copy(recipeSnapshot = original.copy(doseGrams = Double.NaN)))
        }
        assertThrows(IllegalArgumentException::class.java) {
            BrewReplayFactory.fromJournal(entry.copy(recipeSnapshot = null, recipeId = null, doseGrams = 0.0))
        }
    }

    @Test
    fun givenLegacyAndNewJournalJson_whenStrictlyRestored_thenSnapshotIsAdditiveAndOriginalStepsSurvive() {
        val serializer = CoffeeDurableSnapshotCodec.serializer
        val entry = BrewCompletionRecordFactory.fromTimerCompletion(original, 185, "Actual bean")
        val legacy = serializer.toJsonTree(entry).asJsonObject.apply { remove("recipeSnapshot") }
        val legacyDecoded = CoffeeDurableSnapshotCodec.decode(mapOf("journal_entries" to "[$legacy]"))
        assertNull(legacyDecoded.journalEntries.single().recipeSnapshot)

        val restored = CoffeeDurableSnapshotCodec.decode(mapOf("journal_entries" to serializer.toJson(listOf(entry))))
        assertEquals(entry, restored.journalEntries.single())
        assertEquals(original.brewSteps, restored.journalEntries.single().recipeSnapshot?.brewSteps)
    }
}
