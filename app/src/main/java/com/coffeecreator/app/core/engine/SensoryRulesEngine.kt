package com.coffeecreator.app.core.engine

import com.coffeecreator.app.core.model.CoffeeBean
import com.coffeecreator.app.core.model.Recipe
import com.coffeecreator.app.core.model.SensoryEstimate

/** Qualitative roast directions, never an origin-to-flavor lookup or calibrated sensory model. */
object SensoryRulesEngine {
    fun estimateProfile(bean: CoffeeBean?, recipe: Recipe, extractionYieldPercent: Double?): SensoryEstimate {
        extractionYieldPercent?.let {
            require(it.isFinite() && it in 0.0..100.0) { "Extraction yield must be finite and between 0 and 100%." }
        }
        val roast = bean?.roastLevel?.takeUnless { it.equals("Unknown", ignoreCase = true) }.orEmpty()
        var acidity = 5
        var bitterness = 5
        var body = 5
        val directions = mutableListOf<String>()
        when {
            roast.contains("dark", ignoreCase = true) -> {
                acidity = 4
                bitterness = 6
                body = 6
                directions.add("Darker roasting may increase perceived roast bitterness; verify by tasting.")
            }
            roast.contains("light", ignoreCase = true) -> {
                acidity = 6
                bitterness = 4
                body = 4
                directions.add("Lighter roasting may retain more perceived acidity; verify by tasting.")
            }
            roast.isNotBlank() -> directions.add("Roast metadata alone does not establish exact sensory descriptors.")
            else -> directions.add("No directional roast basis is available; record an observation first.")
        }
        val extractionBasis = extractionYieldPercent?.let { "Caller-provided approximate cup extraction: $it%; no flavor is inferred from this value." }
            ?: "Measured extraction inputs not provided; no extraction value is inferred."
        return SensoryEstimate(
            aroma = 5,
            acidity = acidity,
            sweetness = 5,
            bitterness = bitterness,
            body = body,
            clarity = 5,
            finish = 5,
            balance = 5,
            suggestedDescriptors = directions,
            method = "roast-direction/1.0.0 (uncalibrated)",
            confidencePercent = null,
            basis = "Roast: ${roast.ifBlank { "not provided" }}. Brew method: ${recipe.brewMethod}. $extractionBasis Origin is not a flavor predictor.",
            uncertaintyDisclaimer = "Uncalibrated directional heuristic, not measured sensory intensity. The ordinal diagram positions and midpoints are qualitative suggestions, not laboratory predictions. Confidence is unknown; actual sensory observation is required."
        )
    }
}
