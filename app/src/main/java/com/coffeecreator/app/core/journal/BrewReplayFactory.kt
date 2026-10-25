package com.coffeecreator.app.core.journal

import com.coffeecreator.app.core.engine.DomainValidation
import com.coffeecreator.app.core.model.Ingredient
import com.coffeecreator.app.core.model.Recipe

/** Reuses a brewing plan without promoting previous measurements to new results. */
object BrewReplayFactory {
    fun freshPlan(recipe: Recipe, ingredients: List<Ingredient> = emptyList()): Recipe = recipe.copy(
        beverageMassGrams = null,
        measuredTdsPercent = null,
        firstDropSeconds = null,
        brewSteps = recipe.brewSteps.toList(),
        lines = recipe.lines.toList(),
        stages = recipe.stages.toList(),
        acknowledgedAllergens = recipe.acknowledgedAllergens.toList()
    ).also { DomainValidation.validateRecipe(it, ingredients) }

    fun fromJournal(
        entry: BrewJournalEntry,
        linkedRecipe: Recipe? = null,
        ingredients: List<Ingredient> = emptyList()
    ): Recipe {
        val snapshot = entry.recipeSnapshot
        require(snapshot == null || entry.recipeId == null || snapshot.id == entry.recipeId) {
            "Journal recipe snapshot does not match its source identifier."
        }
        val source = snapshot ?: linkedRecipe?.takeIf { it.id == entry.recipeId }
        if (source != null) return freshPlan(source, ingredients)

        // Old records did not store pour schedules. Do not invent bloom or staged-pour targets.
        return freshPlan(
            Recipe(
                id = entry.recipeId ?: "journal:${entry.id}",
                name = entry.brewMethod,
                beanId = entry.beanId,
                brewMethod = entry.brewMethod,
                equipment = entry.equipment,
                doseGrams = entry.doseGrams,
                waterGrams = entry.waterGrams,
                temperatureCelsius = entry.waterTemperatureCelsius,
                brewTimeSeconds = entry.brewDurationSeconds,
                grindSize = entry.grindSetting,
                grinderModel = entry.grinderModel.takeIf(String::isNotBlank),
                grinderSetting = entry.grindSetting.takeIf(String::isNotBlank),
                notes = "Reconstructed from journal ${entry.id}. Original pour schedule is unavailable; " +
                    "the previous duration is reused as a timer target.\nPrevious notes: ${entry.generalNotes}",
                createdAt = entry.createdAt,
                updatedAt = entry.updatedAt
            ),
            ingredients
        )
    }
}
