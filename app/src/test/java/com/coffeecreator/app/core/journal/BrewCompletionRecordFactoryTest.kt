package com.coffeecreator.app.core.journal

import com.coffeecreator.app.core.model.Recipe
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class BrewCompletionRecordFactoryTest {
    @Test
    fun givenTimerCompletionWithoutMeasurements_whenMapped_thenOnlyRecipeAndTimerFactsAreRecorded() {
        val recipe = Recipe(
            id = "recipe-1",
            name = "Morning pour over",
            brewMethod = "Pour Over (V60)",
            doseGrams = 18.0,
            waterGrams = 300.0,
            beverageMassGrams = null,
            measuredTdsPercent = null
        )

        val entry = BrewCompletionRecordFactory.fromTimerCompletion(
            recipe = recipe,
            elapsedSeconds = 184,
            beanName = null
        )

        assertTrue(entry.timerOnly)
        assertEquals("Unknown Bean", entry.beanName)
        assertEquals(recipe.doseGrams, entry.doseGrams, 0.0)
        assertEquals(recipe.waterGrams, entry.waterGrams, 0.0)
        assertEquals(184, entry.brewDurationSeconds)
        assertNull(entry.beverageMassGrams)
        assertNull(entry.measuredTdsPercent)
        assertNull(entry.extractionYieldPercent)
        assertNull(entry.rating)
        assertTrue(entry.tasteNotes.isEmpty())
        assertNull(entry.espressoDetails)
    }
}
