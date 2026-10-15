package com.coffeecreator.app.ui.viewmodel

import com.coffeecreator.app.core.engine.DomainValidation
import com.coffeecreator.app.core.model.Ingredient
import com.coffeecreator.app.core.model.InputLimits
import com.coffeecreator.app.core.model.Recipe
import com.coffeecreator.app.core.model.RecipeLine
import java.util.UUID

data class RecipeDraft(
    val id: String? = null,
    val group: String? = null,
    val fields: Map<String, String> = defaultFields(),
    val lines: List<RecipeLine> = emptyList()
) {
    fun build(ingredients: List<Ingredient>): Recipe {
        val recipeId = id ?: UUID.randomUUID().toString()
        val acknowledged = if (fields["allergens"] == "yes") {
            lines.flatMap { line ->
                ingredients.find { it.id == line.ingredientId }?.allergens.orEmpty()
            }.distinct()
        } else {
            emptyList()
        }
        return Recipe(
            id = recipeId, revisionGroupId = group ?: recipeId, name = fields.getValue("name").trim(),
            beanId = fields.getValue("bean").ifBlank { null }, brewMethod = fields.getValue("method"),
            doseGrams = decimal(fields.getValue("dose")), waterGrams = decimal(fields.getValue("water")),
            beverageMassGrams = optionalDecimal(fields.getValue("beverage")), measuredTdsPercent = optionalDecimal(fields.getValue("tds")),
            temperatureCelsius = decimal(fields.getValue("temperature")), pressureBars = optionalDecimal(fields.getValue("pressure")),
            brewTimeSeconds = fields.getValue("seconds").toIntOrNull() ?: error("Brew time must be whole seconds."),
            equipment = fields.getValue("equipment"), grindSize = fields.getValue("grind"), notes = fields.getValue("notes"),
            agitation = fields.getValue("agitation"), bypassGrams = decimal(fields.getValue("bypass")),
            lines = lines, acknowledgedAllergens = acknowledged
        ).also { DomainValidation.validateRecipe(it, ingredients) }
    }

    fun validate() {
        require(
            fields.keys == RecipeDraft.defaultFields().keys &&
                fields.values.all {
                    it.length <= InputLimits.METADATA_CHARS
                }
        ) {
            "Stored draft is incomplete; begin a new recipe. Saved revisions are retained."
        }
        require(
            lines.size <= InputLimits.COLLECTION_ITEMS &&
                lines.all {
                    it.quantity.isFinite() && it.quantity > 0 && it.unit in setOf("g", "mg", "mL")
                }
        ) {
            "Stored ingredient draft is invalid."
        }
    }

    private fun decimal(input: String) = DomainValidation.parseDecimal(input).toDouble()
    private fun optionalDecimal(input: String) = input.takeIf(String::isNotBlank)?.let(::decimal)

    companion object {
        fun fromRecipe(recipe: Recipe): RecipeDraft = RecipeDraft(
            recipe.id,
            recipe.revisionGroupId,
            mapOf(
                "name" to recipe.name, "bean" to recipe.beanId.orEmpty(), "method" to recipe.brewMethod,
                "dose" to recipe.doseGrams.toString(), "water" to recipe.waterGrams.toString(),
                "beverage" to recipe.beverageMassGrams?.toString().orEmpty(),
                "tds" to recipe.measuredTdsPercent?.toString().orEmpty(), "temperature" to recipe.temperatureCelsius.toString(),
                "pressure" to recipe.pressureBars?.toString().orEmpty(), "seconds" to recipe.brewTimeSeconds.toString(),
                "equipment" to recipe.equipment,
                "grind" to recipe.grindSize, "bypass" to recipe.bypassGrams.toString(), "agitation" to recipe.agitation,
                "notes" to recipe.notes,
                "allergens" to if (recipe.acknowledgedAllergens.isNotEmpty()) "yes" else ""
            ),
            recipe.lines
        )

        fun defaultFields() = mapOf(
            "name" to "", "bean" to "", "method" to "V60", "dose" to "18", "water" to "300", "beverage" to "",
            "tds" to "", "temperature" to "93", "pressure" to "", "seconds" to "180", "equipment" to "", "grind" to "",
            "bypass" to "0", "agitation" to "", "notes" to "", "allergens" to ""
        )
    }
}
