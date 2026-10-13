package com.coffeecreator.app.core.ai

import com.coffeecreator.app.core.model.CalculationResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ToolExecutorTest {

    @Test
    fun `FR-07 execute calculate_extraction_yield returns exact math and metadata`() {
        val result = ToolExecutor.execute(
            "calculate_extraction_yield",
            mapOf("doseGrams" to 18.0, "beverageMassGrams" to 270.0, "measuredTdsPercent" to 1.30)
        )

        assertTrue(result.isSuccess)
        assertFalse(result.requiresUserConfirmation)
        val data = result.structuredData as? CalculationResult.ExtractionYield
        assertNotNull(data)
        assertEquals(19.50, data!!.extractionYieldPercent, 0.001)
        assertTrue(result.output.contains("19.5%"))
    }

    @Test
    fun `FR-07 execute calculate_brew_ratio returns correct ratio`() {
        val result = ToolExecutor.execute(
            "calculate_brew_ratio",
            mapOf("doseGrams" to 15.0, "waterGrams" to 250.0)
        )

        assertTrue(result.isSuccess)
        val ratio = result.structuredData as? CalculationResult.BrewRatio
        assertNotNull(ratio)
        assertEquals(16.67, ratio!!.ratioDenominator, 0.01)
    }

    @Test
    fun `FR-07 execute calculate_dilution returns mass-fraction weighted TDS`() {
        val result = ToolExecutor.execute(
            "calculate_dilution",
            mapOf("initialMassGrams" to 100.0, "initialTdsPercent" to 2.0, "addedWaterGrams" to 100.0)
        )

        assertTrue(result.isSuccess)
        val dilution = result.structuredData as? CalculationResult.Dilution
        assertNotNull(dilution)
        assertEquals(200.0, dilution!!.finalMassGrams, 0.001)
        assertEquals(1.00, dilution.finalTdsPercent, 0.001)
    }

    @Test
    fun `FR-07 execute convert_units without density is rejected`() {
        val result = ToolExecutor.execute(
            "convert_units",
            mapOf("volumeMl" to 250.0, "densityGramsPerMl" to null)
        )

        assertFalse(result.isSuccess)
        assertTrue(result.output.contains("Conversion failed"))
    }

    @Test
    fun `FR-07 execute save_recipe triggers write confirmation gate`() {
        val result = ToolExecutor.execute(
            "save_recipe",
            mapOf("name" to "Test Recipe", "doseGrams" to 18.0, "waterGrams" to 300.0, "brewMethod" to "V60")
        )

        assertTrue(result.isSuccess)
        assertTrue(result.requiresUserConfirmation)
        assertTrue(result.output.contains("Confirm write operation"))
    }

    @Test
    fun `FR-07 unallowlisted tool invocation is safely rejected`() {
        val result = ToolExecutor.execute(
            "execute_arbitrary_shell_command",
            emptyMap()
        )

        assertFalse(result.isSuccess)
        assertTrue(result.output.contains("not in the allowlist registry"))
    }
}
