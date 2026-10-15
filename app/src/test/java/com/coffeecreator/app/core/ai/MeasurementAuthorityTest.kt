package com.coffeecreator.app.core.ai

import com.coffeecreator.app.core.engine.CoffeeScienceEngine
import org.junit.Assert.assertFalse
import org.junit.Test

class MeasurementAuthorityTest {
    @Test fun givenMissingMeasuredValues_whenModelInventsToolParameters_thenAuthorityRejectsThem() {
        val result = CoffeeScienceEngine.calculateExtractionYield(18.0, 270.0, 1.3).getOrThrow()
        assertFalse(LocalLlmCoordinator.hasExplicitInputs(result, "Help me calculate extraction yield."))
        assertFalse(LocalLlmCoordinator.hasExplicitInputs(result, "Dose 18 g, beverage 270 g; TDS not measured."))
        assertFalse(LocalLlmCoordinator.hasExplicitInputs(result, "Dose 18 g, beverage 270 g, measured TDS 1.30%."))
        assertFalse(LocalLlmCoordinator.hasExplicitInputs(result, "Dose 18 g, brew WATER 270 g, TDS1.3%; beverage not measured."))
        assertFalse(LocalLlmCoordinator.hasExplicitInputs(result, "Dose -18 g, beverage 270 mL, TDS1.3%."))
    }
}
