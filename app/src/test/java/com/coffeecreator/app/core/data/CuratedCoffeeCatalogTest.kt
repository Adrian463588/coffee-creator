package com.coffeecreator.app.core.data

import com.coffeecreator.app.core.engine.CoffeeScienceEngine
import com.coffeecreator.app.core.engine.DomainValidation
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CuratedCoffeeCatalogTest {

    @Test
    fun givenCuratedBeans_whenValidated_thenAllPassDomainRules() {
        assertEquals(8, CuratedCoffeeCatalog.beans.size)
        CuratedCoffeeCatalog.beans.forEach { bean ->
            DomainValidation.validateBean(bean)
            assertTrue("Bean must have valid name", bean.name.isNotBlank())
            assertTrue("Bean must have country", bean.country != "Unknown")
            assertTrue("Bean altitude must be positive", (bean.altitudeMeters ?: 0) > 0)
        }
    }

    @Test
    fun givenCuratedRecipes_whenValidated_thenAllPassDomainRulesAndRatios() {
        assertEquals(10, CuratedCoffeeCatalog.recipes.size)
        CuratedCoffeeCatalog.recipes.forEach { recipe ->
            DomainValidation.validateRecipe(recipe)
            assertTrue("Recipe must have positive dose", recipe.doseGrams > 0)
            assertTrue("Recipe must have positive water", recipe.waterGrams > 0)
            assertTrue("Recipe must have steps", recipe.brewSteps.isNotEmpty())
            assertTrue("Recipe temperature must be 0-100", recipe.temperatureCelsius in 0.0..100.0)
        }
    }

    @Test
    fun givenCuratedObservations_whenValidated_thenAllPassSensoryRulesAndExactMassBalance() {
        assertEquals(5, CuratedCoffeeCatalog.observations.size)
        CuratedCoffeeCatalog.observations.forEach { obs ->
            DomainValidation.validateObservation(obs)
            val recipe = CuratedCoffeeCatalog.recipes.first { it.id == obs.recipeId }
            val beverage = recipe.beverageMassGrams ?: error("Curated observation recipe must have beverage mass")
            val tds = recipe.measuredTdsPercent ?: error("Curated observation recipe must have measured TDS")

            val eyResult = CoffeeScienceEngine.calculateExtractionYield(
                recipe.doseGrams,
                beverage,
                tds,
                recipe.brewMethod
            ).getOrThrow()

            // Verify extraction yield is between 18% and 23% for specialty calibration
            assertTrue("Extraction yield must be within standard specialty range", eyResult.extractionYieldPercent in 18.0..23.0)
        }
    }
}
