package com.coffeecreator.app.core.engine

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CoffeeScienceRegressionTest {
    @Test
    fun `given nonfinite measurements when calculating then failure replaces a crash`() {
        listOf(Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY).forEach { invalid ->
            assertTrue(CoffeeScienceEngine.calculateExtractionYield(invalid, 270.0, 1.3).isFailure)
            assertTrue(CoffeeScienceEngine.calculateExtractionYield(18.0, invalid, 1.3).isFailure)
            assertTrue(CoffeeScienceEngine.calculateExtractionYield(18.0, 270.0, invalid).isFailure)
            assertTrue(CoffeeScienceEngine.calculateBrewRatio(18.0, invalid).isFailure)
            assertTrue(CoffeeScienceEngine.calculateDilution(100.0, 2.0, invalid).isFailure)
            assertTrue(CoffeeScienceEngine.calculateConcentration(invalid, 1000.0).isFailure)
            assertTrue(CoffeeScienceEngine.convertVolumeToMass(10.0, invalid).isFailure)
        }
    }

    @Test
    fun `given impossible dissolved mass when calculating then physical bounds reject it`() {
        assertTrue(CoffeeScienceEngine.calculateExtractionYield(18.0, 270.0, 101.0).isFailure)
        assertTrue(CoffeeScienceEngine.calculateExtractionYield(18.0, 270.0, 10.0).isFailure)
        assertTrue(CoffeeScienceEngine.calculateDilution(100.0, 101.0, 100.0).isFailure)
        assertTrue(CoffeeScienceEngine.calculateConcentration(1001.0, 1000.0).isFailure)
    }

    @Test
    fun `given microgram solute when calculating ppm then tiny concentration remains visible`() {
        val result = CoffeeScienceEngine.calculateConcentration(0.000001, 1000.0).getOrThrow()
        assertEquals(0.001, result.ppm, 0.000000001)
    }

    @Test
    fun `given halfway-adjacent yield when rounding then only final rounding occurs`() {
        val result = CoffeeScienceEngine.calculateExtractionYield(100.0, 1949.495, 1.0).getOrThrow()
        assertEquals(19.49, result.extractionYieldPercent, 0.0000001)
    }

    @Test
    fun `given halfway-adjacent dilution when rounding then only final rounding occurs`() {
        val result = CoffeeScienceEngine.calculateDilution(100.0, 2.4699, 100.0).getOrThrow()
        assertEquals(1.23, result.finalTdsPercent, 0.0000001)
    }

    @Test
    fun `given milligram conversion when density is known then mass is not rounded to zero`() {
        assertEquals(0.001, CoffeeScienceEngine.convertVolumeToMass(0.001, 1.0).getOrThrow(), 0.000000001)
    }

    @Test
    fun `given nonfinite blend share when validating then failure replaces a crash`() {
        val blend = listOf(com.coffeecreator.app.core.model.BlendComponent("bean", "Bean", Double.NaN))
        assertTrue(CoffeeScienceEngine.validateBlendRatios(blend).isFailure)
    }
}
