package com.coffeecreator.app.core.ai.personalization

import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate

// ============================================================================
// 1. Palate Quiz Mapper (S2-063)
// ============================================================================

object PalateQuizMapper {
    private const val INITIAL_VARIANCE = 0.04

    fun createFromQuiz(answers: PalateQuizAnswers, id: String = "personal_palate"): PalateProfile {
        val vector = PalateVector(
            roast = answers.roast.vectorValue,
            acidity = answers.acidity.vectorValue,
            sweetness = answers.sweetness.vectorValue,
            bitterness = answers.bitterness.vectorValue,
            body = answers.body.vectorValue
        )
        return PalateProfile(
            id = id,
            vector = vector,
            cumulativeEvidenceWeight = 5.0,
            qualifiedSessionCount = 0,
            varianceVector = PalateVector(
                INITIAL_VARIANCE,
                INITIAL_VARIANCE,
                INITIAL_VARIANCE,
                INITIAL_VARIANCE,
                INITIAL_VARIANCE
            ),
            isQuizCompleted = true,
            lastUpdatedEpochMs = System.currentTimeMillis()
        )
    }

    fun updateManualSliders(
        current: PalateProfile,
        roast: Double? = null,
        acidity: Double? = null,
        sweetness: Double? = null,
        bitterness: Double? = null,
        body: Double? = null
    ): PalateProfile {
        val cur = current.vector
        val updatedVector = PalateVector(
            roast = (roast ?: cur.roast).coerceIn(0.0, 1.0),
            acidity = (acidity ?: cur.acidity).coerceIn(0.0, 1.0),
            sweetness = (sweetness ?: cur.sweetness).coerceIn(0.0, 1.0),
            bitterness = (bitterness ?: cur.bitterness).coerceIn(0.0, 1.0),
            body = (body ?: cur.body).coerceIn(0.0, 1.0)
        )
        return current.copy(
            vector = updatedVector,
            hasManualPreference = current.hasManualPreference ||
                listOf(roast, acidity, sweetness, bitterness, body).any { it != null },
            lastUpdatedEpochMs = System.currentTimeMillis()
        )
    }
}

// ============================================================================
// 2. Bayesian Sensory Evidence Update Engine (S2-064)
// ============================================================================

object PalateBayesianEngine {
    private const val DECAY_TAU_DAYS = 90.0

    fun updateProfile(current: PalateProfile, obs: PersonalSensoryEvidence): PalateProfile {
        val recencyFactor = Math.exp(-obs.daysAgo / DECAY_TAU_DAYS)
        val observationWeight = ((obs.rating - 7.0) / 3.0) * recencyFactor
        val newCumulativeWeight = current.cumulativeEvidenceWeight + observationWeight
        val alpha = observationWeight / newCumulativeWeight

        val curArr = current.vector.toDoubleArray()
        val obsArr = doubleArrayOf(obs.roast, obs.acidity, obs.sweetness, obs.bitterness, obs.body)
        val varArr = current.varianceVector.toDoubleArray()

        val updatedArr = DoubleArray(5) { i ->
            ((1.0 - alpha) * curArr[i] + alpha * obsArr[i]).coerceIn(0.0, 1.0)
        }
        val updatedVarArr = DoubleArray(5) { i ->
            val diff = obsArr[i] - updatedArr[i]
            ((1.0 - alpha) * varArr[i] + alpha * (diff * diff)).coerceIn(0.001, 1.0)
        }

        return current.copy(
            vector = PalateVector(
                BigDecimal.valueOf(updatedArr[0]).setScale(4, RoundingMode.HALF_UP).toDouble(),
                BigDecimal.valueOf(updatedArr[1]).setScale(4, RoundingMode.HALF_UP).toDouble(),
                BigDecimal.valueOf(updatedArr[2]).setScale(4, RoundingMode.HALF_UP).toDouble(),
                BigDecimal.valueOf(updatedArr[3]).setScale(4, RoundingMode.HALF_UP).toDouble(),
                BigDecimal.valueOf(updatedArr[4]).setScale(4, RoundingMode.HALF_UP).toDouble()
            ),
            cumulativeEvidenceWeight = BigDecimal.valueOf(newCumulativeWeight).setScale(3, RoundingMode.HALF_UP).toDouble(),
            qualifiedSessionCount = current.qualifiedSessionCount + 1,
            varianceVector = PalateVector(
                BigDecimal.valueOf(updatedVarArr[0]).setScale(4, RoundingMode.HALF_UP).toDouble(),
                BigDecimal.valueOf(updatedVarArr[1]).setScale(4, RoundingMode.HALF_UP).toDouble(),
                BigDecimal.valueOf(updatedVarArr[2]).setScale(4, RoundingMode.HALF_UP).toDouble(),
                BigDecimal.valueOf(updatedVarArr[3]).setScale(4, RoundingMode.HALF_UP).toDouble(),
                BigDecimal.valueOf(updatedVarArr[4]).setScale(4, RoundingMode.HALF_UP).toDouble()
            ),
            lastUpdatedEpochMs = System.currentTimeMillis()
        )
    }
}

// ============================================================================
// 3. Explainable Inventory Recommendation Engine (S2-065)
// ============================================================================

object RecommendationEngine {
    fun rankInventory(
        palate: PalateProfile,
        candidates: List<BeanInventoryCandidate>,
        availableGearMethods: Set<String>,
        brewMethod: String,
        today: LocalDate = LocalDate.now()
    ): List<RecommendationItem> {
        val gearFactor = if (availableGearMethods.contains(brewMethod) || availableGearMethods.isEmpty()) 1.0 else 0.0
        if (gearFactor == 0.0 || !palate.hasRecordedPreference) return emptyList()

        val p = palate.vector.toDoubleArray()
        val pNorm = Math.sqrt(p.sumOf { it * it })

        return candidates
            .asSequence()
            .filter { it.availableMassGrams.isFinite() && it.availableMassGrams > 0.0 }
            .map { bean ->
                val b = bean.sensoryProfile.toDoubleArray()
                val bNorm = Math.sqrt(b.sumOf { it * it })
                val dot = p.indices.sumOf { p[it] * b[it] }
                val cosine = if (pNorm > 0 && bNorm > 0) (dot / (pNorm * bNorm)).coerceIn(0.0, 1.0) else 0.0

                val days = java.time.temporal.ChronoUnit.DAYS.between(bean.roastDate, today).coerceAtLeast(0)
                val freshness = when {
                    days < 5 -> 0.75 + 0.25 * (days.toDouble() / 5.0)
                    days in 5..28 -> 1.00
                    else -> Math.max(0.40, Math.exp(-(days - 28).toDouble() / 30.0))
                }

                val score = cosine * freshness * gearFactor
                val degasStatus = if (days in 5..28) "Peak flavor window ($days days post-roast)" else "$days days post-roast"
                val explanation =
                    "Vector similarity: ${(cosine * 100).toInt()}% from supplied profile vectors. " +
                        "Roast age: $degasStatus. Available stock: ${bean.availableMassGrams}g. Gear: $brewMethod available."

                RecommendationItem(
                    beanId = bean.beanId,
                    beanName = bean.beanName,
                    compositeScore = BigDecimal.valueOf(score).setScale(4, RoundingMode.HALF_UP).toDouble(),
                    cosineSimilarity = BigDecimal.valueOf(cosine).setScale(4, RoundingMode.HALF_UP).toDouble(),
                    freshnessFactor = BigDecimal.valueOf(freshness).setScale(4, RoundingMode.HALF_UP).toDouble(),
                    roastAgeDays = days,
                    explanationText = explanation
                )
            }
            .sortedByDescending { it.compositeScore }
            .toList()
    }

    private val PalateProfile.hasRecordedPreference: Boolean
        get() = isQuizCompleted || hasManualPreference || qualifiedSessionCount > 0
}

// ============================================================================
// 4. OVAT Brewing Feedback Loop Engine (S2-066)
// ============================================================================

object FeedbackLoopEngine {
    private const val FAST_DRAW_DOWN_THRESHOLD_SECONDS = 15
    private const val GRIND_ADJUSTMENT_CLICKS = 1
    private const val MINIMUM_GRINDER_SETTING = 1
    private const val MAXIMUM_GRINDER_SETTING = Int.MAX_VALUE
    private const val TEMPERATURE_ADJUSTMENT_CELSIUS = 2.0
    private const val BOILING_POINT_MARGIN_CELSIUS = 0.5
    private const val MINIMUM_BREW_TEMPERATURE_CELSIUS = 85.0
    private const val DOSE_ADJUSTMENT_GRAMS = 1.0
    private const val WATER_ADJUSTMENT_GRAMS = 20.0
    private const val FAST_DRAW_DOWN_EXPECTED_DELTA_SECONDS = 20
    private const val SLOW_DRAW_DOWN_EXPECTED_DELTA_SECONDS = -20
    private const val DOSE_EXPECTED_DELTA_SECONDS = 5
    private const val WATER_EXPECTED_DELTA_SECONDS = 8

    fun proposeAdjustment(
        previousRecipe: Map<String, Any>,
        actualDurationSeconds: Int,
        targetDurationSeconds: Int,
        diagnosis: SensoryFeedbackDiagnosis,
        boilingPointCelsius: Double? = null
    ): OvatAdjustmentProposal {
        require(actualDurationSeconds > 0 && targetDurationSeconds > 0) { "Brew durations must be positive." }
        val durationDeltaSeconds = actualDurationSeconds - targetDurationSeconds
        return when (diagnosis) {
            SensoryFeedbackDiagnosis.SOUR_UNDER_EXTRACTED ->
                if (durationDeltaSeconds < -FAST_DRAW_DOWN_THRESHOLD_SECONDS) {
                    adjustGrind(
                        previousRecipe,
                        finer = true,
                        actualDurationSeconds = actualDurationSeconds,
                        targetDurationSeconds = targetDurationSeconds
                    )
                } else {
                    adjustTemperature(
                        previousRecipe,
                        increase = true,
                        boilingPointCelsius = boilingPointCelsius
                    )
                }
            SensoryFeedbackDiagnosis.BITTER_OVER_EXTRACTED ->
                if (durationDeltaSeconds > FAST_DRAW_DOWN_THRESHOLD_SECONDS) {
                    adjustGrind(
                        previousRecipe,
                        finer = false,
                        actualDurationSeconds = actualDurationSeconds,
                        targetDurationSeconds = targetDurationSeconds
                    )
                } else {
                    adjustTemperature(
                        previousRecipe,
                        increase = false,
                        boilingPointCelsius = boilingPointCelsius
                    )
                }
            SensoryFeedbackDiagnosis.WEAK_WATERY -> adjustDose(previousRecipe)
            SensoryFeedbackDiagnosis.HARSH_CONCENTRATED -> adjustWater(previousRecipe)
            SensoryFeedbackDiagnosis.BALANCED -> OvatAdjustmentProposal(
                variableAdjusted = "None",
                previousValue = "Optimal",
                proposedValue = "Lock Recipe",
                diagnosticReason = "Recipe is dialed in. No variable changes required.",
                expectedDrawdownDeltaSeconds = 0,
                modifiedRecipePayload = previousRecipe
            )
        }
    }

    private fun adjustGrind(
        previousRecipe: Map<String, Any>,
        finer: Boolean,
        actualDurationSeconds: Int,
        targetDurationSeconds: Int
    ): OvatAdjustmentProposal {
        val current = numericSetting(previousRecipe, "grindClicks")
        require(current % 1.0 == 0.0) { "Grinder clicks must be a whole number." }
        require(current in MINIMUM_GRINDER_SETTING.toDouble()..MAXIMUM_GRINDER_SETTING.toDouble()) {
            "Grinder clicks must fit the supported positive integer range."
        }
        val currentClicks = current.toInt()
        require(if (finer) currentClicks > MINIMUM_GRINDER_SETTING else currentClicks < MAXIMUM_GRINDER_SETTING) {
            "The requested grind adjustment is outside the supported click range."
        }
        val adjustedClicks = if (finer) {
            currentClicks - GRIND_ADJUSTMENT_CLICKS
        } else {
            currentClicks + GRIND_ADJUSTMENT_CLICKS
        }
        val reason = if (finer) {
            "Fast drawdown (${actualDurationSeconds}s vs target ${targetDurationSeconds}s) " +
                "causing under-extraction. Grind 1 click finer."
        } else {
            "Slow drawdown (${actualDurationSeconds}s vs target ${targetDurationSeconds}s) " +
                "indicates bed clogging/over-extraction. Grind 1 click coarser."
        }
        return OvatAdjustmentProposal(
            variableAdjusted = "Grind Size",
            previousValue = "$currentClicks clicks",
            proposedValue = "$adjustedClicks clicks",
            diagnosticReason = reason,
            expectedDrawdownDeltaSeconds = if (finer) {
                FAST_DRAW_DOWN_EXPECTED_DELTA_SECONDS
            } else {
                SLOW_DRAW_DOWN_EXPECTED_DELTA_SECONDS
            },
            modifiedRecipePayload = previousRecipe + ("grindClicks" to adjustedClicks)
        )
    }

    private fun adjustTemperature(
        previousRecipe: Map<String, Any>,
        increase: Boolean,
        boilingPointCelsius: Double?
    ): OvatAdjustmentProposal {
        val current = numericSetting(previousRecipe, "waterTempCelsius")
        val boilingPoint = boilingPointCelsius
            ?.takeIf { it.isFinite() && it > 0.0 }
            ?: throw IllegalArgumentException("A confirmed local boiling point is required for a temperature adjustment.")
        require(current <= boilingPoint) {
            "Recorded water temperature cannot exceed the measured boiling point."
        }
        require(current >= MINIMUM_BREW_TEMPERATURE_CELSIUS) {
            "Recorded water temperature is below the supported brew adjustment range."
        }
        val adjusted = if (increase) {
            val proposed = current + TEMPERATURE_ADJUSTMENT_CELSIUS
            require(proposed <= boilingPoint - BOILING_POINT_MARGIN_CELSIUS) {
                "A 2.0°C increase would exceed the safe limit below the measured boiling point."
            }
            proposed
        } else {
            val proposed = current - TEMPERATURE_ADJUSTMENT_CELSIUS
            require(proposed >= MINIMUM_BREW_TEMPERATURE_CELSIUS) {
                "A 2.0°C decrease would fall below the supported brew adjustment range."
            }
            proposed
        }
        return OvatAdjustmentProposal(
            variableAdjusted = "Water Temperature",
            previousValue = "$current°C",
            proposedValue = "$adjusted°C",
            diagnosticReason = if (increase) {
                "Normal drawdown timing but sour cup indicates thermodynamic under-extraction. " +
                    "Increase temperature by 2.0°C."
            } else {
                "Normal drawdown but astringent/bitter finish indicates excessive thermal extraction. " +
                    "Decrease temperature by 2.0°C."
            },
            expectedDrawdownDeltaSeconds = 0,
            modifiedRecipePayload = previousRecipe + ("waterTempCelsius" to adjusted)
        )
    }

    private fun adjustDose(previousRecipe: Map<String, Any>): OvatAdjustmentProposal {
        val currentDose = numericSetting(previousRecipe, "doseGrams")
        val newDose = BigDecimal.valueOf(currentDose + DOSE_ADJUSTMENT_GRAMS)
            .setScale(1, RoundingMode.HALF_UP)
            .toDouble()
        return OvatAdjustmentProposal(
            variableAdjusted = "Coffee Dose",
            previousValue = "${currentDose}g",
            proposedValue = "${newDose}g",
            diagnosticReason =
            "Weak concentration/low body with balanced flavor profile. " +
                "Tighten brew ratio by increasing coffee dose.",
            expectedDrawdownDeltaSeconds = DOSE_EXPECTED_DELTA_SECONDS,
            modifiedRecipePayload = previousRecipe + ("doseGrams" to newDose)
        )
    }

    private fun adjustWater(previousRecipe: Map<String, Any>): OvatAdjustmentProposal {
        val currentWater = numericSetting(previousRecipe, "waterGrams")
        val newWater = currentWater + WATER_ADJUSTMENT_GRAMS
        return OvatAdjustmentProposal(
            variableAdjusted = "Water Volume",
            previousValue = "${currentWater}g",
            proposedValue = "${newWater}g",
            diagnosticReason = "Harsh high concentration. Loosen brew ratio by adding 20g water to soften TDS.",
            expectedDrawdownDeltaSeconds = WATER_EXPECTED_DELTA_SECONDS,
            modifiedRecipePayload = previousRecipe + ("waterGrams" to newWater)
        )
    }

    private fun numericSetting(recipe: Map<String, Any>, name: String): Double {
        val value = (recipe[name] as? Number)?.toDouble()
            ?: throw IllegalArgumentException("A recorded $name value is required for this adjustment.")
        require(value.isFinite() && value > 0.0) { "Recorded $name must be positive." }
        return value
    }
}

// ============================================================================
// 6. Offline Weather & Altitude Boiling Point Calculator (S2-073)
// ============================================================================

object WeatherAltitudeCalculator {
    private const val MINIMUM_SUPPORTED_ELEVATION_METERS = -500.0
    private const val MAXIMUM_SUPPORTED_ELEVATION_METERS = 9000.0

    fun calculateBoilingPointCelsius(elevationValue: Double, unit: ElevationUnit = ElevationUnit.METERS): Double {
        require(elevationValue.isFinite()) { "Elevation must be finite." }
        val elevationMeters = when (unit) {
            ElevationUnit.METERS -> elevationValue
            ElevationUnit.FEET -> elevationValue * 0.3048
        }
        require(elevationMeters in MINIMUM_SUPPORTED_ELEVATION_METERS..MAXIMUM_SUPPORTED_ELEVATION_METERS) {
            "Elevation must be between -500m and 9000m."
        }
        val tBoil = 100.0 - (elevationMeters / 300.0)
        return BigDecimal.valueOf(tBoil).setScale(2, RoundingMode.HALF_UP).toDouble()
    }

    fun clampWaterTemperature(targetTempCelsius: Double, elevationMeters: Double): Double {
        val tBoil = calculateBoilingPointCelsius(elevationMeters, ElevationUnit.METERS)
        val maxSafeBrewTemp = tBoil - 0.50
        return if (targetTempCelsius > maxSafeBrewTemp) maxSafeBrewTemp else targetTempCelsius
    }
}

// ============================================================================
// 7. Food-Grade Safety & Allergen Validator (S2-074)
// ============================================================================

object IngredientSafetyValidator {
    private val BANNED_COSMETIC_COMPOUNDS = setOf(
        "dipropylene glycol",
        "dpg",
        "diethyl phthalate",
        "dep",
        "synthetic musk",
        "denatured alcohol"
    )

    fun auditIngredient(
        name: String,
        foodGrade: Boolean?,
        allergens: List<String>,
        massFractionPercent: Double,
        supplierMaxPercent: Double?
    ): SafetyAuditResult {
        require(name.isNotBlank()) { "Ingredient name is required." }
        require(massFractionPercent.isFinite() && massFractionPercent in 0.0..100.0) {
            "Ingredient mass fraction must be within 0% and 100%."
        }
        val flags = mutableListOf<String>()

        if (foodGrade != true) {
            flags.add(
                "CRITICAL: Substance '$name' lacks verified Food Chemicals Codex (FCC) food-grade certification. " +
                    "Perfumery and cosmetic aroma compounds are prohibited from coffee beverages."
            )
            return SafetyAuditResult(IngredientSafetyLevel.CRITICAL_NON_FOOD_PROHIBITED, flags, false)
        }

        if (BANNED_COSMETIC_COMPOUNDS.any { name.contains(it, ignoreCase = true) }) {
            flags.add("CRITICAL: Industrial cosmetic solvent detected in '$name'. Immediate ingestion hazard.")
            return SafetyAuditResult(IngredientSafetyLevel.CRITICAL_NON_FOOD_PROHIBITED, flags, false)
        }

        if (supplierMaxPercent != null) {
            require(supplierMaxPercent.isFinite() && supplierMaxPercent in 0.0..100.0) {
                "Supplier dosage limit must be finite and within 0% and 100%."
            }
            if (massFractionPercent > supplierMaxPercent) {
                flags.add("CRITICAL: Dosage $massFractionPercent% exceeds supplier safe food dosage limit ($supplierMaxPercent%).")
                return SafetyAuditResult(IngredientSafetyLevel.CRITICAL_DOSAGE_LIMIT_EXCEEDED, flags, false)
            }
        }

        if (allergens.isNotEmpty()) {
            flags.add("ALLERGEN: Contains declared allergens: ${allergens.joinToString(", ")}. Explicit user acknowledgment mandatory.")
            return SafetyAuditResult(IngredientSafetyLevel.WARNING_ALLERGEN_PRESENT, flags, true)
        }

        return SafetyAuditResult(IngredientSafetyLevel.SAFE_FOOD_GRADE, flags, true)
    }
}

// ============================================================================
// 8. Barista Persona Sanitizer & Length Guard (S2-072)
// ============================================================================

object PersonaSanitizer {
    private const val MAX_CHARS = 300
    private val INJECTION_REGEX = Regex("(?i)(\\b(system|assistant|user)\\b|\\[/?inst\\]|<\\|im_start\\|>|<\\|im_end\\|>)")

    fun sanitizeCustomPrompt(raw: String): String {
        val cleaned = raw.replace(INJECTION_REGEX, "").replace(Regex("[\\x00-\\x1F\\x7F]"), "").trim()
        return cleaned.take(MAX_CHARS)
    }
}
