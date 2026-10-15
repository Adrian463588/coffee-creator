package com.coffeecreator.app.core.engine

import com.coffeecreator.app.core.model.CoffeeBean
import com.coffeecreator.app.core.model.Ingredient
import com.coffeecreator.app.core.model.MassFractionPercent
import com.coffeecreator.app.core.model.MassGrams
import com.coffeecreator.app.core.model.Recipe
import com.coffeecreator.app.core.model.RecipeLine
import com.coffeecreator.app.core.model.SensoryObservation
import java.math.BigDecimal
import java.util.Locale
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class DomainValidationTest {
    @Test
    fun `given missing bean facts when creating then unknown metadata remains unknown`() {
        val bean = CoffeeBean(id = "bean", name = "User lot")
        DomainValidation.validateBean(bean)
        assertEquals("Unknown", bean.country)
        assertEquals("Unknown", bean.variety)
        assertEquals("Unknown", bean.process)
        assertEquals("Unknown", bean.provenance)
        assertNull(bean.foodGrade)
        assertNull(bean.densityGramsPerMl)
    }

    @Test
    fun `given new recipe when creating then no measurement or equipment is invented`() {
        val recipe = Recipe(id = "recipe", name = "Manual recipe")
        DomainValidation.validateRecipe(recipe)
        assertNull(recipe.beverageMassGrams)
        assertNull(recipe.measuredTdsPercent)
        assertEquals("", recipe.equipment)
        assertEquals("", recipe.grindSize)
        assertEquals("recipe", recipe.revisionGroupId)
    }

    @Test
    fun `given Indonesian decimal when parsing then decimal comma is exact`() {
        assertEquals(BigDecimal("18.50"), DomainValidation.parseDecimal("18,50", Locale.forLanguageTag("id-ID")))
        assertEquals(BigDecimal("18.50"), DomainValidation.parseDecimal("18.50", Locale.forLanguageTag("id-ID")))
        assertEquals(BigDecimal("0.001"), DomainValidation.parseDecimal("0.001", Locale.US))
    }

    @Test
    fun `given ambiguous or nonnumeric input when parsing then no silent coercion occurs`() {
        listOf("", "NaN", "Infinity", "1,000.50", "1.000,50", "18g", "1e3", "1 000").forEach { input ->
            assertThrows(IllegalArgumentException::class.java) { DomainValidation.parseDecimal(input, Locale.US) }
        }
    }

    @Test
    fun `given invalid quantities when validating recipe then invalid records cannot be saved`() {
        val recipe = Recipe(id = "recipe", name = "Manual")
        listOf(0.0, -1.0, Double.NaN, Double.POSITIVE_INFINITY).forEach { invalid ->
            assertThrows(IllegalArgumentException::class.java) { DomainValidation.validateRecipe(recipe.copy(doseGrams = invalid)) }
            assertThrows(IllegalArgumentException::class.java) { DomainValidation.validateRecipe(recipe.copy(waterGrams = invalid)) }
        }
        assertThrows(IllegalArgumentException::class.java) { DomainValidation.validateRecipe(recipe.copy(measuredTdsPercent = 101.0)) }
        assertThrows(IllegalArgumentException::class.java) { DomainValidation.validateRecipe(recipe.copy(bypassGrams = -1.0)) }
    }

    @Test
    fun `given unknown or nonfood ingredient when validating recipe then use is rejected`() {
        val line = RecipeLine(ingredientId = "aroma", quantity = 1.0)
        val recipe = Recipe(id = "recipe", name = "Manual", lines = listOf(line))
        assertThrows(IllegalArgumentException::class.java) { DomainValidation.validateRecipe(recipe) }
        listOf<Boolean?>(null, false).forEach { grade ->
            val ingredient = Ingredient(id = "aroma", name = "Aroma", foodGrade = grade)
            assertThrows(IllegalArgumentException::class.java) { DomainValidation.validateRecipe(recipe, listOf(ingredient)) }
        }
    }

    @Test
    fun `given volume ingredient without density when validating then conversion is refused`() {
        val ingredient = Ingredient(id = "ingredient", name = "Solution", foodGrade = true)
        val recipe = Recipe(id = "recipe", name = "Manual", lines = listOf(RecipeLine("ingredient", 1.0, "mL")))
        assertThrows(IllegalArgumentException::class.java) { DomainValidation.validateRecipe(recipe, listOf(ingredient)) }
        DomainValidation.validateRecipe(recipe.copy(lines = listOf(RecipeLine("ingredient", 1.0, "mL", 1.0))), listOf(ingredient))
    }

    @Test
    fun `given supplier dosage limit when validating then total solution dosage is bounded`() {
        val ingredient = Ingredient("aroma", "Food-grade aroma solution", foodGrade = true, supplierMaxPercent = 1.0)
        val recipe = Recipe(
            id = "recipe",
            name = "Manual",
            doseGrams = 18.0,
            waterGrams = 300.0,
            lines = listOf(RecipeLine("aroma", 5.0))
        )
        assertThrows(IllegalArgumentException::class.java) { DomainValidation.validateRecipe(recipe, listOf(ingredient)) }
        DomainValidation.validateRecipe(recipe.copy(lines = listOf(RecipeLine("aroma", 2.0))), listOf(ingredient))
        assertThrows(IllegalArgumentException::class.java) {
            DomainValidation.validateRecipe(
                recipe.copy(lines = listOf(RecipeLine("aroma", 2.0), RecipeLine("aroma", 3.0))),
                listOf(ingredient)
            )
        }
    }

    @Test
    fun `given ingredient allergen when saving then explicit acknowledgement is required`() {
        val ingredient = Ingredient("milk", "Milk", allergens = listOf("Milk"), foodGrade = true)
        val recipe = Recipe(id = "recipe", name = "Latte", lines = listOf(RecipeLine("milk", 100.0)))
        assertThrows(IllegalArgumentException::class.java) { DomainValidation.validateRecipe(recipe, listOf(ingredient)) }
        DomainValidation.validateRecipe(recipe.copy(acknowledgedAllergens = listOf("milk")), listOf(ingredient))
    }

    @Test
    fun `given invalid metadata when importing then malformed catalog data is rejected`() {
        assertThrows(IllegalArgumentException::class.java) { DomainValidation.validateBean(CoffeeBean("bean", "Lot", altitudeMeters = -1)) }
        assertThrows(IllegalArgumentException::class.java) {
            DomainValidation.validateIngredient(Ingredient("id", "Name", densityGramsPerMl = Double.NaN))
        }
        assertThrows(IllegalArgumentException::class.java) {
            DomainValidation.validateIngredient(Ingredient("id", "Name", supplierMaxPercent = 101.0))
        }
        assertThrows(IllegalArgumentException::class.java) { DomainValidation.validateBean(CoffeeBean("", "Lot")) }
    }

    @Test
    fun `given observed descriptor intensity when recording then descriptor and assessor metadata persist`() {
        val observation = SensoryObservation(
            "observation", "recipe", 8, 7, 8, body = 6, finish = 7, balance = 8,
            tastingDescriptors = listOf("Citrus"), assessor = "Assessor A", panel = "Panel 1", descriptorIntensities = mapOf("Citrus" to 4)
        )
        DomainValidation.validateObservation(observation)
        assertEquals("Panel 1", observation.panel)
        assertEquals(4, observation.descriptorIntensities["Citrus"])
        assertThrows(IllegalArgumentException::class.java) { DomainValidation.validateObservation(observation.copy(aroma = 11)) }
        assertThrows(IllegalArgumentException::class.java) {
            DomainValidation.validateObservation(
                observation.copy(
                    descriptorIntensities = mapOf(
                        "Citrus" to 0
                    )
                )
            )
        }
    }

    @Test
    fun `given typed exact quantities when calculating then scale and method provenance are retained`() {
        val result = CoffeeScienceEngine.calculateExtractionYield(
            MassGrams(BigDecimal("18.00")),
            MassGrams(BigDecimal("270.00")),
            MassFractionPercent(BigDecimal("1.30")),
            "French Press"
        ).getOrThrow()
        assertEquals(19.50, result.extractionYieldPercent, 0.0001)
        assertEquals("1.30", result.exactInputs["measuredTdsPercent"])
        assertTrue(result.assumptions.contains("immersion", ignoreCase = true))
        assertTrue(result.methodVersion.isNotBlank())
        assertTrue(result.inputProvenance["measuredTdsPercent"]!!.contains("caller"))
    }
}
