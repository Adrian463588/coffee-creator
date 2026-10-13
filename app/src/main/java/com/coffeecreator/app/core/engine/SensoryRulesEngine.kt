package com.coffeecreator.app.core.engine

import com.coffeecreator.app.core.model.CoffeeBean
import com.coffeecreator.app.core.model.Recipe
import com.coffeecreator.app.core.model.SensoryEstimate

object SensoryRulesEngine {

    /**
     * FR-04 Heuristic Sensory Profile Estimation.
     * Generates a directional estimate based on roast level, origin, process, and extraction yield.
     * Carries explicit uncertainty and disclaimer (no deterministic flavor claims).
     */
    fun estimateProfile(
        bean: CoffeeBean?,
        recipe: Recipe,
        extractionYieldPercent: Double?
    ): SensoryEstimate {
        var aroma = 6
        var acidity = 6
        var sweetness = 6
        var bitterness = 3
        var body = 6
        var clarity = 7
        var finish = 6
        var balance = 7
        val descriptors = mutableListOf<String>()

        val roast = bean?.roastLevel?.lowercase() ?: "medium"
        val process = bean?.process?.lowercase() ?: "washed"
        val origin = bean?.country?.lowercase() ?: ""

        when {
            roast.contains("light") -> {
                acidity += 2
                sweetness += 1
                body -= 1
                bitterness -= 1
                clarity += 2
                descriptors.addAll(listOf("Bright Citrus", "Floral", "Tea-like"))
            }
            roast.contains("dark") -> {
                acidity -= 2
                body += 2
                bitterness += 3
                clarity -= 2
                finish += 1
                descriptors.addAll(listOf("Dark Chocolate", "Roasted Walnut", "Molasses"))
            }
            else -> { // Medium
                sweetness += 2
                balance += 1
                clarity += 1
                descriptors.addAll(listOf("Milk Chocolate", "Caramel", "Red Apple"))
            }
        }

        when {
            process.contains("natural") -> {
                sweetness += 1
                body += 1
                aroma += 1
                clarity -= 1
                descriptors.addAll(listOf("Berry Jam", "Dried Fruit", "Winey"))
            }
            process.contains("honey") -> {
                sweetness += 2
                descriptors.addAll(listOf("Honey", "Peach", "Cane Sugar"))
            }
            process.contains("washed") -> {
                acidity += 1
                balance += 1
                clarity += 1
                descriptors.addAll(listOf("Clean Citrus", "Crisp Apple", "Floral Jasmine"))
            }
            process.contains("wet hulled") -> {
                body += 2
                acidity -= 1
                bitterness += 1
                clarity -= 1
                descriptors.addAll(listOf("Earthy", "Herbal", "Cedar"))
            }
        }

        if (origin.contains("ethiopia") || origin.contains("kenya")) {
            aroma += 1
            acidity += 1
            clarity += 1
            descriptors.add("Bergamot / Blackcurrant")
        }

        if (extractionYieldPercent != null) {
            when {
                extractionYieldPercent < 18.0 -> {
                    // Under-extracted
                    acidity += 1
                    sweetness -= 1
                    finish -= 1
                    balance -= 2
                    descriptors.add("(Under-extracted tendency: Sour / Hollow)")
                }
                extractionYieldPercent > 22.0 -> {
                    // Over-extracted
                    sweetness -= 1
                    finish -= 1
                    balance -= 2
                    bitterness += 3
                    clarity -= 1
                    body += 1
                    descriptors.add("(Over-extracted tendency: Bitter / Astringent)")
                }
                else -> {
                    // Ideal Specialty range (18-22%)
                    sweetness += 1
                    balance += 1
                }
            }
        }

        // Clamp to 1-10 range
        fun clamp(v: Int) = v.coerceIn(1, 10)

        return SensoryEstimate(
            aroma = clamp(aroma),
            acidity = clamp(acidity),
            sweetness = clamp(sweetness),
            bitterness = clamp(bitterness),
            body = clamp(body),
            clarity = clamp(clarity),
            finish = clamp(finish),
            balance = clamp(balance),
            suggestedDescriptors = descriptors.distinct().take(6),
            method = "Heuristic SCA Flavor Vocabulary Rule-Engine v1.0",
            confidencePercent = 70,
            uncertaintyDisclaimer = "Warning: This is a directional heuristic estimate based on recipe variables and roast profile. It is NOT a laboratory physical measurement or calibrated instrument reading."
        )
    }
}
