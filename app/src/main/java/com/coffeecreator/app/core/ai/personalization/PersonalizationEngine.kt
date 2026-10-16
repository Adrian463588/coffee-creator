package com.coffeecreator.app.core.ai.personalization

import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate

// ============================================================================
// 1. Palate Quiz Mapper (S2-063)
// ============================================================================

object PalateQuizMapper {
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
            varianceVector = PalateVector(0.04, 0.04, 0.04, 0.04, 0.04),
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
        if (gearFactor == 0.0) return emptyList()

        val p = palate.vector.toDoubleArray()
        val pNorm = Math.sqrt(p.sumOf { it * it })

        return candidates.map { bean ->
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
            val dominantTrait = when {
                bean.sensoryProfile.acidity > 0.7 -> "Vibrant Acidity"
                bean.sensoryProfile.sweetness > 0.7 -> "Rich Sweetness"
                bean.sensoryProfile.body > 0.7 -> "Silky Body"
                else -> "Balanced Roast"
            }
            val degasStatus = if (days in 5..28) "Peak flavor window ($days days post-roast)" else "$days days post-roast"
            val explanation = "Matched ${(cosine * 100).toInt()}% with palate ($dominantTrait). Degas: $degasStatus. Gear: $brewMethod available."

            RecommendationItem(
                beanId = bean.beanId,
                beanName = bean.beanName,
                compositeScore = BigDecimal.valueOf(score).setScale(4, RoundingMode.HALF_UP).toDouble(),
                cosineSimilarity = BigDecimal.valueOf(cosine).setScale(4, RoundingMode.HALF_UP).toDouble(),
                freshnessFactor = BigDecimal.valueOf(freshness).setScale(4, RoundingMode.HALF_UP).toDouble(),
                roastAgeDays = days,
                explanationText = explanation
            )
        }.sortedByDescending { it.compositeScore }
    }
}

// ============================================================================
// 4. OVAT Brewing Feedback Loop Engine (S2-066)
// ============================================================================

object FeedbackLoopEngine {
    fun proposeAdjustment(
        previousRecipe: Map<String, Any>,
        actualDurationSeconds: Int,
        targetDurationSeconds: Int,
        diagnosis: SensoryFeedbackDiagnosis,
        boilingPointCelsius: Double? = null
    ): OvatAdjustmentProposal {
        val deltaSec = actualDurationSeconds - targetDurationSeconds
        require(actualDurationSeconds > 0 && targetDurationSeconds > 0) { "Brew durations must be positive." }
        fun numericSetting(name: String): Double {
            val value = (previousRecipe[name] as? Number)?.toDouble()
                ?: throw IllegalArgumentException("A recorded $name value is required for this adjustment.")
            require(value.isFinite() && value > 0.0) { "Recorded $name must be positive." }
            return value
        }
        fun grindClicks(): Int {
            val value = numericSetting("grindClicks")
            require(value % 1.0 == 0.0) { "Grinder clicks must be a whole number." }
            return value.toInt()
        }
        fun temperature(): Double {
            val value = numericSetting("waterTempCelsius")
            val boilingPoint = boilingPointCelsius
                ?.takeIf { it.isFinite() && it > 0.0 }
                ?: throw IllegalArgumentException("A confirmed local boiling point is required for a temperature adjustment.")
            require(value <= boilingPoint) {
                "Recorded water temperature cannot exceed the measured boiling point."
            }
            return value
        }

        return when (diagnosis) {
            SensoryFeedbackDiagnosis.SOUR_UNDER_EXTRACTED -> {
                if (deltaSec < -15) {
                    val currentGrind = grindClicks()
                    val newGrind = Math.max(1, currentGrind - 1)
                    OvatAdjustmentProposal(
                        variableAdjusted = "Grind Size",
                        previousValue = "$currentGrind clicks",
                        proposedValue = "$newGrind clicks",
                        diagnosticReason = "Fast drawdown (${actualDurationSeconds}s vs target ${targetDurationSeconds}s) causing under-extraction. Grind 1 click finer.",
                        expectedDrawdownDeltaSeconds = 20,
                        modifiedRecipePayload = previousRecipe + ("grindClicks" to newGrind)
                    )
                } else {
                    val currentTemp = temperature()
                    val maxSafeTemp = requireNotNull(boilingPointCelsius) - 0.5
                    val newTemp = Math.min(maxSafeTemp, currentTemp + 2.0)
                    OvatAdjustmentProposal(
                        variableAdjusted = "Water Temperature",
                        previousValue = "$currentTemp°C",
                        proposedValue = "$newTemp°C",
                        diagnosticReason = "Normal drawdown timing but sour cup indicates thermodynamic under-extraction. Increase temperature by 2.0°C.",
                        expectedDrawdownDeltaSeconds = 0,
                        modifiedRecipePayload = previousRecipe + ("waterTempCelsius" to newTemp)
                    )
                }
            }
            SensoryFeedbackDiagnosis.BITTER_OVER_EXTRACTED -> {
                if (deltaSec > 15) {
                    val currentGrind = grindClicks()
                    val newGrind = currentGrind + 1
                    OvatAdjustmentProposal(
                        variableAdjusted = "Grind Size",
                        previousValue = "$currentGrind clicks",
                        proposedValue = "$newGrind clicks",
                        diagnosticReason = "Slow drawdown (${actualDurationSeconds}s vs target ${targetDurationSeconds}s) indicates bed clogging/over-extraction. Grind 1 click coarser.",
                        expectedDrawdownDeltaSeconds = -20,
                        modifiedRecipePayload = previousRecipe + ("grindClicks" to newGrind)
                    )
                } else {
                    val currentTemp = temperature()
                    val newTemp = Math.max(85.0, currentTemp - 2.0)
                    OvatAdjustmentProposal(
                        variableAdjusted = "Water Temperature",
                        previousValue = "$currentTemp°C",
                        proposedValue = "$newTemp°C",
                        diagnosticReason = "Normal drawdown but astringent/bitter finish indicates excessive thermal extraction. Decrease temperature by 2.0°C.",
                        expectedDrawdownDeltaSeconds = 0,
                        modifiedRecipePayload = previousRecipe + ("waterTempCelsius" to newTemp)
                    )
                }
            }
            SensoryFeedbackDiagnosis.WEAK_WATERY -> {
                val currentDose = numericSetting("doseGrams")
                val newDose = BigDecimal.valueOf(currentDose + 1.0).setScale(1, RoundingMode.HALF_UP).toDouble()
                OvatAdjustmentProposal(
                    variableAdjusted = "Coffee Dose",
                    previousValue = "${currentDose}g",
                    proposedValue = "${newDose}g",
                    diagnosticReason = "Weak concentration/low body with balanced flavor profile. Tighten brew ratio by increasing coffee dose.",
                    expectedDrawdownDeltaSeconds = 5,
                    modifiedRecipePayload = previousRecipe + ("doseGrams" to newDose)
                )
            }
            SensoryFeedbackDiagnosis.HARSH_CONCENTRATED -> {
                val currentWater = numericSetting("waterGrams")
                val newWater = currentWater + 20.0
                OvatAdjustmentProposal(
                    variableAdjusted = "Water Volume",
                    previousValue = "${currentWater}g",
                    proposedValue = "${newWater}g",
                    diagnosticReason = "Harsh high concentration. Loosen brew ratio by adding 20g water to soften TDS.",
                    expectedDrawdownDeltaSeconds = 8,
                    modifiedRecipePayload = previousRecipe + ("waterGrams" to newWater)
                )
            }
            SensoryFeedbackDiagnosis.BALANCED -> {
                OvatAdjustmentProposal(
                    variableAdjusted = "None",
                    previousValue = "Optimal",
                    proposedValue = "Lock Recipe",
                    diagnosticReason = "Recipe is dialed in. No variable changes required.",
                    expectedDrawdownDeltaSeconds = 0,
                    modifiedRecipePayload = previousRecipe
                )
            }
        }
    }
}

// ============================================================================
// 6. Offline Weather & Altitude Boiling Point Calculator (S2-073)
// ============================================================================

object WeatherAltitudeCalculator {
    fun calculateBoilingPointCelsius(elevationValue: Double, unit: ElevationUnit = ElevationUnit.METERS): Double {
        require(elevationValue.isFinite()) { "Elevation must be finite." }
        val elevationMeters = when (unit) {
            ElevationUnit.METERS -> elevationValue
            ElevationUnit.FEET -> elevationValue * 0.3048
        }
        require(elevationMeters in -500.0..9000.0) { "Elevation must be between -500m and 9000m." }
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
                "CRITICAL: Substance '$name' lacks verified Food Chemicals Codex (FCC) food-grade certification. Perfumery and cosmetic aroma compounds are prohibited from coffee beverages."
            )
            return SafetyAuditResult(IngredientSafetyLevel.CRITICAL_NON_FOOD_PROHIBITED, flags, false)
        }

        if (BANNED_COSMETIC_COMPOUNDS.any { name.contains(it, ignoreCase = true) }) {
            flags.add("CRITICAL: Industrial cosmetic solvent detected in '$name'. Immediate ingestion hazard.")
            return SafetyAuditResult(IngredientSafetyLevel.CRITICAL_NON_FOOD_PROHIBITED, flags, false)
        }

        if (supplierMaxPercent != null && massFractionPercent > supplierMaxPercent) {
            flags.add("WARNING: Dosage $massFractionPercent% exceeds supplier safe food dosage limit ($supplierMaxPercent%).")
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
