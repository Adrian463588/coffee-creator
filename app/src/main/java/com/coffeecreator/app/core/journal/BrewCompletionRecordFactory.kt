package com.coffeecreator.app.core.journal

import com.coffeecreator.app.core.model.Recipe
import java.util.UUID

/** Builds a journal entry from a completed timer without promoting recipe targets to measurements. */
object BrewCompletionRecordFactory {
    fun fromTimerCompletion(recipe: Recipe, elapsedSeconds: Int, beanName: String?): BrewJournalEntry {
        require(elapsedSeconds >= 0) { "Elapsed brew time cannot be negative." }

        return BrewJournalEntry(
            id = UUID.randomUUID().toString(),
            recipeId = recipe.id,
            beanId = recipe.beanId,
            beanName = beanName?.takeIf(String::isNotBlank) ?: "Unknown Bean",
            brewMethod = recipe.brewMethod,
            equipment = recipe.equipment,
            doseGrams = recipe.doseGrams,
            waterGrams = recipe.waterGrams,
            grinderModel = recipe.grinderModel.orEmpty(),
            grindSetting = recipe.grinderSetting ?: recipe.grindSize,
            waterTemperatureCelsius = recipe.temperatureCelsius,
            brewDurationSeconds = elapsedSeconds,
            measuredTdsPercent = null,
            rating = null,
            tasteNotes = emptyList(),
            generalNotes = "Timer completed. Water is the recipe target; measurements and sensory notes were not recorded.",
            tags = listOf("Timer only"),
            isEspresso = recipe.brewMethod.contains("Espresso", ignoreCase = true),
            espressoDetails = null,
            dialInChainId = null,
            timerOnly = true
        )
    }
}
