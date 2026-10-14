package com.coffeecreator.app.core.engine

import com.coffeecreator.app.core.model.CoffeeBean
import com.coffeecreator.app.core.model.Recipe
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SensoryRulesEngineTest {
    @Test
    fun `given uncalibrated heuristic when estimating then numerical confidence stays unknown`() {
        val estimate = SensoryRulesEngine.estimateProfile(CoffeeBean("bean", "Lot", roastLevel = "Light"), Recipe("recipe", "Manual"), null)
        assertNull(estimate.confidencePercent)
        assertTrue(estimate.basis.contains("Light"))
        assertTrue(estimate.uncertaintyDisclaimer.contains("uncalibrated", ignoreCase = true))
    }

    @Test
    fun `given country alone when estimating then origin cannot promise an exact flavor`() {
        val recipe = Recipe("recipe", "Manual")
        val unknown = CoffeeBean("bean", "Lot")
        val ethiopian = SensoryRulesEngine.estimateProfile(unknown.copy(country = "Ethiopia"), recipe, null)
        val colombian = SensoryRulesEngine.estimateProfile(unknown.copy(country = "Colombia"), recipe, null)
        assertEquals(ethiopian, colombian)
        assertTrue(ethiopian.suggestedDescriptors.none { it.contains("Bergamot") || it.contains("Jasmine") })
    }

    @Test
    fun `given missing measurements when estimating then extraction is not fabricated`() {
        val estimate = SensoryRulesEngine.estimateProfile(null, Recipe("recipe", "Manual"), null)
        assertTrue(estimate.basis.contains("not provided"))
        assertTrue(estimate.uncertaintyDisclaimer.contains("observation", ignoreCase = true))
    }
}
