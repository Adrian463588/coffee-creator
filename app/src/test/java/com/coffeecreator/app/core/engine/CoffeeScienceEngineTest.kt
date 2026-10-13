package com.coffeecreator.app.core.engine

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CoffeeScienceEngineTest {

    @Test
    fun `FR-03 Golden Extraction Yield Calculation - 18g dose, 270g beverage, 1_30 percent TDS yields 19_50 percent`() {
        val result = CoffeeScienceEngine.calculateExtractionYield(
            doseGrams = 18.0,
            beverageMassGrams = 270.0,
            measuredTdsPercent = 1.30
        )

        assertTrue(result.isSuccess)
        val extraction = result.getOrThrow()
        assertEquals(19.50, extraction.extractionYieldPercent, 0.001)
        assertEquals(18.0, extraction.doseGrams, 0.001)
        assertEquals(270.0, extraction.beverageMassGrams, 0.001)
        assertEquals(1.30, extraction.measuredTdsPercent, 0.001)
        assertTrue(extraction.methodLabel.isNotEmpty())
        assertTrue(extraction.assumptions.contains("TDS%"))
    }

    @Test
    fun `FR-03 Validation rejects zero and negative dose`() {
        val zeroDose = CoffeeScienceEngine.calculateExtractionYield(
            doseGrams = 0.0,
            beverageMassGrams = 270.0,
            measuredTdsPercent = 1.30
        )
        assertTrue(zeroDose.isFailure)

        val negativeDose = CoffeeScienceEngine.calculateExtractionYield(
            doseGrams = -15.0,
            beverageMassGrams = 270.0,
            measuredTdsPercent = 1.30
        )
        assertTrue(negativeDose.isFailure)
    }

    @Test
    fun `FR-03 Validation rejects negative beverage mass or negative TDS`() {
        val negativeBeverage = CoffeeScienceEngine.calculateExtractionYield(
            doseGrams = 18.0,
            beverageMassGrams = -200.0,
            measuredTdsPercent = 1.30
        )
        assertTrue(negativeBeverage.isFailure)

        val negativeTds = CoffeeScienceEngine.calculateExtractionYield(
            doseGrams = 18.0,
            beverageMassGrams = 270.0,
            measuredTdsPercent = -0.5
        )
        assertTrue(negativeTds.isFailure)
    }

    @Test
    fun `FR-03 Brew ratio calculation matches water over dose`() {
        val result = CoffeeScienceEngine.calculateBrewRatio(
            doseGrams = 15.0,
            waterGrams = 250.0
        )

        assertTrue(result.isSuccess)
        val ratio = result.getOrThrow()
        assertEquals(16.67, ratio.ratioDenominator, 0.01)
        assertEquals("1:16.67", ratio.ratioString)
    }

    @Test
    fun `FR-03 Dilution calculation maintains mass conservation`() {
        val result = CoffeeScienceEngine.calculateDilution(
            initialMassGrams = 100.0,
            initialTdsPercent = 2.00,
            addedWaterGrams = 100.0
        )

        assertTrue(result.isSuccess)
        val dilution = result.getOrThrow()
        assertEquals(200.0, dilution.finalMassGrams, 0.001)
        assertEquals(1.00, dilution.finalTdsPercent, 0.001)
    }

    @Test
    fun `FR-03 Concentration ppm is mass fraction times 10,000`() {
        val result = CoffeeScienceEngine.calculateConcentration(
            soluteMassGrams = 1.5,
            solutionMassGrams = 1000.0
        )

        assertTrue(result.isSuccess)
        val concentration = result.getOrThrow()
        assertEquals(0.15, concentration.massFractionPercent, 0.001)
        assertEquals(1500.0, concentration.ppm, 0.01)
    }

    @Test
    fun `FR-03 Volume to mass conversion strictly requires density`() {
        val failWithoutDensity = CoffeeScienceEngine.convertVolumeToMass(
            volumeMl = 250.0,
            densityGramsPerMl = null
        )
        assertTrue(failWithoutDensity.isFailure)

        val successWithDensity = CoffeeScienceEngine.convertVolumeToMass(
            volumeMl = 250.0,
            densityGramsPerMl = 1.05
        )
        assertTrue(successWithDensity.isSuccess)
        assertEquals(262.50, successWithDensity.getOrThrow(), 0.01)
    }
}
