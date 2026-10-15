package com.coffeecreator.app.core.engine

import com.coffeecreator.app.core.model.CoffeeBean
import com.coffeecreator.app.core.model.Ingredient
import com.coffeecreator.app.core.model.InputLimits
import com.coffeecreator.app.core.model.Recipe
import com.coffeecreator.app.core.model.RecipeLine
import com.coffeecreator.app.core.model.SensoryObservation
import java.math.BigDecimal
import java.text.DecimalFormatSymbols
import java.time.LocalDate
import java.util.Locale

/** Shared validation boundary for editor, tools, persistence and backup import. */
object DomainValidation {
    /** Decimal separators are accepted explicitly; grouping and exponent notation are not. */
    fun parseDecimal(input: String, locale: Locale = Locale.getDefault()): BigDecimal {
        val value = input.trim()
        require(value.length in 1..InputLimits.DECIMAL_CHARS) { "Enter a decimal number without grouping separators." }
        val commaAllowed = DecimalFormatSymbols.getInstance(locale).decimalSeparator == ','
        val pattern = if (commaAllowed) "[+-]?(?:[0-9]+(?:[.,][0-9]+)?|[.,][0-9]+)" else "[+-]?(?:[0-9]+(?:[.][0-9]+)?|[.][0-9]+)"
        require(Regex(pattern).matches(value)) { "Enter a decimal number without grouping separators." }
        return value.replace(',', '.').toBigDecimal()
    }

    fun validateBean(bean: CoffeeBean) {
        requireText(bean.id, "Bean identifier", 128)
        requireText(bean.name, "Bean name", 256)
        listOf(bean.country, bean.region, bean.producer, bean.variety, bean.process, bean.roastLevel, bean.allergens)
            .forEach { requireText(it, "Bean metadata (use Unknown for missing facts)", InputLimits.METADATA_CHARS) }
        requireText(bean.provenance, "Bean provenance (use Unknown if unavailable)", InputLimits.METADATA_CHARS)
        bean.altitudeMeters?.let { require(it >= 0) { "Altitude cannot be negative." } }
        bean.degasDays?.let { require(it >= 0) { "Degas days cannot be negative." } }
        bean.densityGramsPerMl?.let { positive(it, "Density (g/mL)") }
        bean.roastDate?.let {
            require(runCatching { LocalDate.parse(it) }.isSuccess) { "Roast date must be a valid ISO date (YYYY-MM-DD)." }
        }
        if (bean.blendComponents.isNotEmpty()) CoffeeScienceEngine.validateBlendRatios(bean.blendComponents).getOrThrow()
    }

    fun validateIngredient(ingredient: Ingredient) {
        requireText(ingredient.id, "Ingredient identifier", 128)
        requireText(ingredient.name, "Ingredient name", 256)
        requireText(ingredient.provenance, "Ingredient provenance (use Unknown if unavailable)", InputLimits.METADATA_CHARS)
        ingredient.densityGramsPerMl?.let { positive(it, "Density (g/mL)") }
        ingredient.supplierMaxPercent?.let { percent(it, "Supplier maximum (% w/w)") }
        require(ingredient.allergens.size <= InputLimits.ALLERGEN_ITEMS) { "Too many allergen entries." }
        ingredient.allergens.forEach { requireText(it, "Allergen", 128) }
    }

    fun validateRecipe(recipe: Recipe, ingredients: List<Ingredient> = emptyList()) {
        requireText(recipe.id, "Recipe identifier", 128)
        requireText(recipe.name, "Recipe name", 256)
        requireText(recipe.revisionGroupId, "Revision group identifier", 128)
        requireText(recipe.brewMethod, "Brew method", 256)
        val dose = positive(recipe.doseGrams, "Dose (g)")
        require(recipe.doseGrams in 0.5..1000.0) { "Dose must be between 0.5 and 1000.0 g." }
        val water = positive(recipe.waterGrams, "Water (g)")
        require(recipe.waterGrams in 5.0..20000.0) { "Water must be between 5.0 and 20000.0 g." }
        val bypass = nonNegative(recipe.bypassGrams, "Bypass water (g)")
        val beverage = recipe.beverageMassGrams?.let { positive(it, "Measured beverage mass (g)") }
        val tds = recipe.measuredTdsPercent?.let { percent(it, "Measured TDS (% w/w)") }
        val temperature = decimal(recipe.temperatureCelsius, "Temperature (°C)")
        require(temperature >= BigDecimal.ZERO && temperature <= BigDecimal("100")) { "Brew temperature must be between 0 and 100 °C." }
        recipe.pressureBars?.let {
            val p = nonNegative(it, "Pressure (bar)")
            require(p <= BigDecimal("15.0")) { "Pressure cannot exceed 15.0 bar." }
        }
        require(recipe.brewTimeSeconds > 0) { "Brew time must be positive (seconds)." }
        require(recipe.version > 0) { "Recipe version must be positive." }
        require(recipe.createdAt >= 0L && recipe.updatedAt >= 0L) { "Recipe timestamps cannot be negative." }
        require(recipe.lines.size <= InputLimits.COLLECTION_ITEMS) { "Recipe has too many ingredient lines." }
        require(recipe.acknowledgedAllergens.size <= InputLimits.ALLERGEN_ITEMS) { "Too many allergen acknowledgements." }
        recipe.acknowledgedAllergens.forEach { requireText(it, "Acknowledged allergen", 128) }

        val ingredientById = ingredients.associateBy { it.id }
        require(ingredientById.size == ingredients.size) { "Ingredient identifiers must be unique." }
        val masses = linkedMapOf<String, BigDecimal>()
        recipe.lines.forEach { line ->
            requireText(line.ingredientId, "Ingredient identifier", 128)
            val ingredient = requireNotNull(ingredientById[line.ingredientId]) { "Unknown ingredient '${line.ingredientId}'." }
            validateIngredient(ingredient)
            require(ingredient.foodGrade == true) {
                "Ingredient '${ingredient.name}' must have verified food-grade status before beverage use."
            }
            val mass = lineMassGrams(line, ingredient)
            masses[line.ingredientId] = (masses[line.ingredientId] ?: BigDecimal.ZERO).add(mass)
            ingredient.allergens.forEach { allergen ->
                require(recipe.acknowledgedAllergens.any { it.trim().equals(allergen.trim(), ignoreCase = true) }) {
                    "Acknowledge allergen '$allergen' before using '${ingredient.name}'."
                }
            }
        }
        val additions = masses.values.fold(BigDecimal.ZERO, BigDecimal::add)
        // A measured beverage is the final solution. Otherwise use planned liquid + ingredient masses,
        // excluding dry grounds. Supplier limits apply to solution dosage, not active aromatic compound.
        val solutionMass = beverage ?: water.add(bypass).add(additions)
        require(additions <= solutionMass) { "Ingredient mass exceeds final solution mass." }
        masses.forEach { (ingredientId, mass) ->
            ingredientById.getValue(ingredientId).supplierMaxPercent?.let { maximum ->
                require(mass.multiply(BigDecimal("100")) <= solutionMass.multiply(BigDecimal.valueOf(maximum))) {
                    "Supplier maximum dosage exceeded for '${ingredientById.getValue(ingredientId).name}'."
                }
            }
        }
        beverage?.let {
            require(it <= water.add(bypass).add(dose).add(additions)) {
                "Measured beverage mass exceeds the formulation's total input mass."
            }
            if (tds != null && recipe.lines.isEmpty()) {
                CoffeeScienceEngine.calculateExtractionYield(
                    recipe.doseGrams,
                    it.toDouble(),
                    tds.toDouble(),
                    recipe.brewMethod
                ).getOrThrow()
            }
        }
        require(recipe.brewSteps.size <= InputLimits.COLLECTION_ITEMS) { "Recipe has too many brew steps." }
        recipe.brewSteps.forEach { step ->
            require(step.stepNumber > 0 && step.durationSeconds > 0) { "Brew steps require positive step numbers and durations." }
            requireText(step.description, "Brew-step description", InputLimits.METADATA_CHARS)
            nonNegative(step.targetWaterGrams, "Brew-step water (g)")
        }
        if (recipe.brewSteps.size > 1) {
            for (i in 1 until recipe.brewSteps.size) {
                require(recipe.brewSteps[i].cumulativeMassGrams >= recipe.brewSteps[i - 1].cumulativeMassGrams) {
                    "Cumulative pour targets must be monotonically non-decreasing."
                }
            }
        }
    }

    fun validateObservation(observation: SensoryObservation) {
        requireText(observation.id, "Observation identifier", 128)
        requireText(observation.recipeId, "Observation recipe identifier", 128)
        listOf(
            observation.aroma,
            observation.acidity,
            observation.sweetness,
            observation.bitterness,
            observation.body,
            observation.clarity,
            observation.finish,
            observation.balance
        ).forEach {
            require(it in 1..10) { "Observed sensory intensities must be between 1 and 10." }
        }
        require(observation.timestamp >= 0L) { "Observation timestamp cannot be negative." }
        require(observation.assessor.length <= 256 && observation.panel.length <= 256) { "Assessor or panel metadata is too long." }
        require(observation.tastingDescriptors.size <= 256 && observation.descriptorIntensities.size <= 256) {
            "Too many sensory descriptors."
        }
        observation.tastingDescriptors.forEach { requireText(it, "Sensory descriptor", 256) }
        observation.descriptorIntensities.forEach { (descriptor, intensity) ->
            require(descriptor in observation.tastingDescriptors) { "Descriptor intensity must refer to a recorded descriptor." }
            require(intensity in 1..10) { "Descriptor intensity must be between 1 and 10." }
        }
    }

    internal fun lineMassGrams(line: RecipeLine, ingredient: Ingredient): BigDecimal {
        val quantity = positive(line.quantity, "Ingredient quantity")
        line.densityGramsPerMl?.let { positive(it, "Line density (g/mL)") }
        return when (line.unit) {
            "g" -> quantity
            "mg" -> quantity.movePointLeft(3)
            "mL" -> {
                val density =
                    requireNotNull(line.densityGramsPerMl ?: ingredient.densityGramsPerMl) {
                        "Volume cannot be converted without a density (g/mL)."
                    }
                quantity.multiply(positive(density, "Density (g/mL)"))
            }
            else -> throw IllegalArgumentException("Ingredient unit must be g, mg or mL.")
        }
    }

    internal fun decimal(value: Double, label: String): BigDecimal {
        require(value.isFinite()) { "$label must be finite." }
        return BigDecimal.valueOf(value)
    }

    internal fun positive(value: Double, label: String): BigDecimal = decimal(value, label).also {
        require(it.signum() > 0) { "$label must be greater than zero." }
    }

    internal fun nonNegative(value: Double, label: String): BigDecimal = decimal(value, label).also {
        require(it.signum() >= 0) { "$label cannot be negative." }
    }

    internal fun percent(value: Double, label: String): BigDecimal = nonNegative(value, label).also {
        require(it <= BigDecimal("100")) { "$label cannot exceed 100%." }
    }

    private fun requireText(value: String, label: String, maximumLength: Int) {
        require(value.isNotBlank() && value.length <= maximumLength && value.none { it.isISOControl() }) { "$label is missing or invalid." }
    }
}
