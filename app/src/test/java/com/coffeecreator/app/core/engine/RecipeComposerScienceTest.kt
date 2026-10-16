package com.coffeecreator.app.core.engine

import com.coffeecreator.app.core.data.BrewScenarioFixtures
import com.coffeecreator.app.core.data.RecipeExportFormatter
import com.coffeecreator.app.core.model.BrewStep
import com.coffeecreator.app.core.model.CoffeeBean
import com.coffeecreator.app.core.model.Recipe
import com.coffeecreator.app.core.model.RefractometerProfile
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * BDD-style unit tests verifying Epic S2-E02 (Recipe composer and deterministic brewing science).
 * Covers S2-013 through S2-026.
 */
class RecipeComposerScienceTest {

    // --- S2-017: Exact Pour Scaling Engine (Hamilton-Hare largest-remainder apportionment) ---

    @Test
    fun `S2-017 Scale four-pour V60 recipe from 300g to 450g water recomputes exact steps and monotonic cumulative mass`() {
        val originalSteps = listOf(
            BrewStep(stepNumber = 1, action = "Bloom", targetWaterGrams = 50.0, cumulativeMassGrams = 50.0, durationSeconds = 45),
            BrewStep(stepNumber = 2, action = "Pour 1", targetWaterGrams = 50.0, cumulativeMassGrams = 100.0, durationSeconds = 30),
            BrewStep(stepNumber = 3, action = "Pour 2", targetWaterGrams = 100.0, cumulativeMassGrams = 200.0, durationSeconds = 35),
            BrewStep(stepNumber = 4, action = "Pour 3", targetWaterGrams = 100.0, cumulativeMassGrams = 300.0, durationSeconds = 40)
        )

        val scaledResult = CoffeeScienceEngine.scaleRecipeSteps(
            steps = originalSteps,
            initialWaterGrams = 300.0,
            targetWaterGrams = 450.0
        )

        assertTrue(scaledResult.isSuccess)
        val scaled = scaledResult.getOrThrow()
        assertEquals(4, scaled.size)

        // 50 * 1.5 = 75, 100 * 1.5 = 150
        assertEquals(75.0, scaled[0].targetWaterGrams, 0.001)
        assertEquals(75.0, scaled[0].cumulativeMassGrams, 0.001)

        assertEquals(75.0, scaled[1].targetWaterGrams, 0.001)
        assertEquals(150.0, scaled[1].cumulativeMassGrams, 0.001)

        assertEquals(150.0, scaled[2].targetWaterGrams, 0.001)
        assertEquals(300.0, scaled[2].cumulativeMassGrams, 0.001)

        assertEquals(150.0, scaled[3].targetWaterGrams, 0.001)
        assertEquals(450.0, scaled[3].cumulativeMassGrams, 0.001)

        // Exact sum equality
        val totalWater = scaled.sumOf { it.targetWaterGrams }
        assertEquals(450.0, totalWater, 0.001)
    }

    @Test
    fun `S2-017 Scale non-divisible water mass using Hamilton-Hare apportionment prevents rounding drift`() {
        val originalSteps = listOf(
            BrewStep(stepNumber = 1, action = "Bloom", targetWaterGrams = 50.0, cumulativeMassGrams = 50.0, durationSeconds = 45),
            BrewStep(stepNumber = 2, action = "Pour 1", targetWaterGrams = 100.0, cumulativeMassGrams = 150.0, durationSeconds = 30),
            BrewStep(stepNumber = 3, action = "Pour 2", targetWaterGrams = 100.0, cumulativeMassGrams = 250.0, durationSeconds = 35)
        )

        val scaledResult = CoffeeScienceEngine.scaleRecipeSteps(
            steps = originalSteps,
            initialWaterGrams = 250.0,
            targetWaterGrams = 333.3,
            precisionQuantum = 0.1
        )

        assertTrue(scaledResult.isSuccess)
        val scaled = scaledResult.getOrThrow()

        // Total sum must match 333.3g exactly with zero drift
        val totalWater = scaled.sumOf { it.targetWaterGrams }
        assertEquals(333.3, totalWater, 0.001)
        assertEquals(333.3, scaled.last().cumulativeMassGrams, 0.001)

        // Monotonic check
        for (i in 1 until scaled.size) {
            assertTrue(scaled[i].cumulativeMassGrams >= scaled[i - 1].cumulativeMassGrams)
        }
    }

    // --- S2-018: Typed Physical Quantities & Temperature Conversion ---

    @Test
    fun `S2-018 Temperature conversions between Celsius and Fahrenheit are exact`() {
        assertEquals(199.4, CoffeeScienceEngine.celsiusToFahrenheit(93.0), 0.01)
        assertEquals(212.0, CoffeeScienceEngine.celsiusToFahrenheit(100.0), 0.01)
        assertEquals(32.0, CoffeeScienceEngine.celsiusToFahrenheit(0.0), 0.01)

        assertEquals(93.0, CoffeeScienceEngine.fahrenheitToCelsius(199.4), 0.01)
        assertEquals(100.0, CoffeeScienceEngine.fahrenheitToCelsius(212.0), 0.01)
        assertEquals(0.0, CoffeeScienceEngine.fahrenheitToCelsius(32.0), 0.01)
    }

    @Test
    fun `S2-018 Volume to mass requires density and validates accurately`() {
        val converted = CoffeeScienceEngine.convertVolumeToMass(250.0, 1.05)
        assertTrue(converted.isSuccess)
        assertEquals(262.50, converted.getOrThrow(), 0.001)

        val rejected = CoffeeScienceEngine.convertVolumeToMass(250.0, null)
        assertTrue(rejected.isFailure)
    }

    // --- S2-020: Optical Brix to TDS Conversion with Calibration Profiles ---

    @Test
    fun `S2-020 Standard profile converts Brix to TDS accurately`() {
        val result = CoffeeScienceEngine.calculateBrixToTds(
            brixPercent = 1.53,
            profile = RefractometerProfile.STANDARD
        )
        assertTrue(result.isSuccess)
        val conv = result.getOrThrow()
        assertEquals(1.3005, conv.tdsPercent, 0.0001)
        assertEquals(RefractometerProfile.STANDARD.name, conv.instrumentProfile)
        assertEquals(0.85, conv.calibrationSlope, 0.001)
        assertEquals(0.0, conv.calibrationIntercept, 0.001)
    }

    @Test
    fun `S2-020 Atago PAL-COFFEE profile converts Brix to TDS accurately`() {
        val result = CoffeeScienceEngine.calculateBrixToTds(
            brixPercent = 1.55,
            profile = RefractometerProfile.ATAGO
        )
        assertTrue(result.isSuccess)
        val conv = result.getOrThrow()
        assertEquals(1.3020, conv.tdsPercent, 0.0001)
        assertEquals(RefractometerProfile.ATAGO.name, conv.instrumentProfile)
    }

    @Test
    fun `S2-020 VST LAB Coffee III profile converts Brix to TDS accurately`() {
        val result = CoffeeScienceEngine.calculateBrixToTds(
            brixPercent = 1.52,
            profile = RefractometerProfile.VST
        )
        assertTrue(result.isSuccess)
        val conv = result.getOrThrow()
        assertEquals(1.3026, conv.tdsPercent, 0.0001)
        assertEquals(RefractometerProfile.VST.name, conv.instrumentProfile)
    }

    @Test
    fun `S2-020 Rejects out of range Brix above 35 percent`() {
        val result = CoffeeScienceEngine.calculateBrixToTds(
            brixPercent = 36.5,
            profile = RefractometerProfile.STANDARD
        )
        assertTrue(result.isFailure)
    }

    // --- S2-021: Concentrate Mass-Balance Dilution & Dosing ---

    @Test
    fun `S2-021 Dilute cold brew concentrate maintains mass balance`() {
        val result = CoffeeScienceEngine.calculateDilution(
            initialMassGrams = 500.0,
            initialTdsPercent = 4.50,
            addedWaterGrams = 1000.0
        )
        assertTrue(result.isSuccess)
        val dilution = result.getOrThrow()
        assertEquals(1500.0, dilution.finalMassGrams, 0.001)
        assertEquals(1.50, dilution.finalTdsPercent, 0.001)
    }

    @Test
    fun `S2-021 Concentrate target dosing solves required concentrate and dilution water`() {
        val result = CoffeeScienceEngine.calculateConcentrateDosing(
            concentrateTdsPercent = 4.50,
            targetTdsPercent = 1.50,
            targetBeverageMassGrams = 300.0
        )
        assertTrue(result.isSuccess)
        val dosing = result.getOrThrow()
        assertEquals(100.0, dosing.concentrateMassGrams, 0.001)
        assertEquals(200.0, dosing.dilutionWaterMassGrams, 0.001)
    }

    @Test
    fun `S2-021 Concentrate dosing rejects target TDS exceeding concentrate TDS`() {
        val result = CoffeeScienceEngine.calculateConcentrateDosing(
            concentrateTdsPercent = 2.00,
            targetTdsPercent = 3.00,
            targetBeverageMassGrams = 300.0
        )
        assertTrue(result.isFailure)
    }

    // --- S2-022: Food-Grade Aroma Solution Dosage with Active vs Stock ---

    @Test
    fun `S2-022 Food-grade aroma dosage separates stock percentage from active compound ppm`() {
        val result = CoffeeScienceEngine.calculateAromaDosage(
            beverageMassGrams = 270.0,
            stockAdditionGrams = 0.30,
            activeStockPercent = 2.50,
            supplierMaxPercent = 0.50
        )
        assertTrue(result.isSuccess)
        val dosage = result.getOrThrow()

        // Total mass = 270.30g
        // Stock % = 0.30 / 270.30 * 100 = 0.11%
        assertEquals(0.11, dosage.stockDosagePercent, 0.01)

        // Active % = 0.30 * 0.025 / 270.30 = 0.00277469...
        // Active ppm = active % * 10,000 = 27.75 ppm
        assertEquals(27.75, dosage.activeCompoundPpm, 0.01)
        assertTrue(dosage.isWithinSupplierLimit)
    }

    @Test
    fun `S2-022 Food-grade aroma dosage flags violation when exceeding supplier maximum`() {
        val result = CoffeeScienceEngine.calculateAromaDosage(
            beverageMassGrams = 100.0,
            stockAdditionGrams = 2.0,
            activeStockPercent = 1.0,
            // 2.0 / 102.0 = ~1.96% > 1.0%.
            supplierMaxPercent = 1.0
        )
        assertTrue(result.isSuccess)
        val dosage = result.getOrThrow()
        assertFalse(dosage.isWithinSupplierLimit)
    }

    // --- S2-016 & S2-023: Safe Bounds Validation & Monotonic Steps ---

    @Test
    fun `S2-023 Pressure exceeding 15 bar is rejected`() {
        val invalidRecipe = Recipe(
            id = "rec-test-press",
            name = "Overpressurized Espresso",
            revisionGroupId = "grp-01",
            brewMethod = "Espresso",
            doseGrams = 18.0,
            waterGrams = 36.0,
            temperatureCelsius = 93.0,
            pressureBars = 16.0,
            brewTimeSeconds = 30
        )

        val error = runCatching { DomainValidation.validateRecipe(invalidRecipe) }.exceptionOrNull()
        assertNotNull(error)
        assertTrue(error?.message?.contains("15.0 bar") == true)
    }

    @Test
    fun `S2-016 Non-monotonic cumulative mass in brew steps is rejected`() {
        val nonMonotonicRecipe = Recipe(
            id = "rec-test-monotonic",
            name = "Non-monotonic V60",
            revisionGroupId = "grp-01",
            brewMethod = "V60",
            doseGrams = 15.0,
            waterGrams = 250.0,
            temperatureCelsius = 93.0,
            brewTimeSeconds = 180,
            brewSteps = listOf(
                // Error: cumulative water drops to 40g.
                BrewStep(
                    stepNumber = 1,
                    description = "Bloom pour",
                    action = "Bloom",
                    targetWaterGrams = 50.0,
                    cumulativeMassGrams = 50.0,
                    durationSeconds = 45
                ),
                BrewStep(
                    stepNumber = 2,
                    description = "First pour",
                    action = "Pour 1",
                    targetWaterGrams = 100.0,
                    cumulativeMassGrams = 40.0,
                    durationSeconds = 30
                )
            )
        )

        val error = runCatching { DomainValidation.validateRecipe(nonMonotonicRecipe) }.exceptionOrNull()
        assertNotNull(error)
        assertTrue(error?.message?.contains("monotonically non-decreasing") == true)
    }

    // --- S2-024: Curated Starter Recipes Catalog ---

    @Test
    fun `S2-024 Curated starter recipes catalog has source-attributed recipes with steps`() {
        val recipes = BrewScenarioFixtures.recipes
        assertTrue(recipes.size >= 10)

        recipes.forEach { recipe ->
            assertTrue(recipe.name.isNotBlank())
            assertTrue(recipe.brewMethod.isNotBlank())
            assertTrue(recipe.doseGrams in 10.0..100.0)
            assertTrue(recipe.waterGrams in 20.0..1000.0)
            assertTrue(recipe.brewSteps.isNotEmpty())
            assertTrue(recipe.notes.isNotBlank())
            DomainValidation.validateRecipe(recipe)
        }
    }

    // --- S2-025: Expert Mode Dial-in Metadata ---

    @Test
    fun `S2-025 Expert mode dial-in metadata persists on recipe`() {
        val expertRecipe = Recipe(
            id = "rec-expert-01",
            name = "EK43 Dial-In Light Roast",
            revisionGroupId = "grp-ek43",
            brewMethod = "V60",
            doseGrams = 15.0,
            waterGrams = 250.0,
            temperatureCelsius = 94.0,
            brewTimeSeconds = 175,
            firstDropSeconds = 7,
            grinderModel = "Mahlkönig EK43S",
            grinderSetting = "1.8 burr touch + 2",
            waterHardnessPpm = 70.0,
            waterAlkalinityPpm = 35.0,
            basketPuckPrep = "WDT needle 0.3mm + paper filter top"
        )

        DomainValidation.validateRecipe(expertRecipe)
        assertEquals(7, expertRecipe.firstDropSeconds)
        assertEquals("Mahlkönig EK43S", expertRecipe.grinderModel)
        assertEquals("1.8 burr touch + 2", expertRecipe.grinderSetting)
        assertEquals(70.0, expertRecipe.waterHardnessPpm!!, 0.001)
        assertEquals(35.0, expertRecipe.waterAlkalinityPpm!!, 0.001)
        assertEquals("WDT needle 0.3mm + paper filter top", expertRecipe.basketPuckPrep)
    }

    // --- S2-026: Offline Recipe Sharing (JSON and Barista Card) ---

    @Test
    fun `S2-026 Recipe exports to structured offline JSON`() {
        val recipe = BrewScenarioFixtures.recipes.first()
        val json = RecipeExportFormatter.exportToJson(recipe)

        assertTrue(json.contains("\"name\": \"${recipe.name}\""))
        assertTrue(json.contains("\"brewMethod\": \"${recipe.brewMethod}\""))
        assertTrue(json.contains("\"doseGrams\": ${recipe.doseGrams}"))
        assertTrue(json.contains("\"waterGrams\": ${recipe.waterGrams}"))
        assertTrue(json.contains("\"brewSteps\":"))
    }

    @Test
    fun `S2-026 Recipe exports to formatted Barista Card for Android Sharesheet`() {
        val bean = CoffeeBean(
            id = "bean-eth-01",
            name = "Ethiopia Yirgacheffe Washed",
            country = "Ethiopia",
            region = "Yirgacheffe",
            producer = "Gedeb Cooperative",
            variety = "Heirloom",
            process = "Washed",
            roastLevel = "Light"
        )
        val recipe = BrewScenarioFixtures.recipes.first()
        val card = RecipeExportFormatter.exportToBaristaCard(recipe, bean)

        assertTrue(card.contains("COFFEE CREATOR — RECIPE SPECIFICATION"))
        assertTrue(card.contains("Recipe: ${recipe.name}"))
        assertTrue(card.contains("Bean: ${bean.name}"))
        assertTrue(card.contains("Dose:        ${recipe.doseGrams} g"))
        assertTrue(card.contains("Water:       ${recipe.waterGrams} g"))
        assertTrue(card.contains("ORDERED BREW STEPS:"))
        assertTrue(card.contains("Offline-generated by Coffee Creator"))
    }
}
