package com.coffeecreator.app.core.journal

import com.coffeecreator.app.core.data.RecordJson
import com.coffeecreator.app.core.model.CalculationMetadata
import com.coffeecreator.app.core.model.CoffeeBean
import com.google.gson.JsonObject
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.UUID

/**
 * Pure Kotlin/JVM deterministic engine for brew analytics, espresso kinematics,
 * single-variable recommendations (OVAT), caffeine estimations, and Beanconqueror migrations.
 */
object BrewAnalyticsEngine {

    /**
     * Compute deterministic numeric deltas between two brew sessions (B minus A).
     */
    fun compareSessions(baseline: BrewJournalEntry, comparison: BrewJournalEntry): BrewSessionComparison {
        val doseDelta = BigDecimal.valueOf(comparison.doseGrams)
            .subtract(BigDecimal.valueOf(baseline.doseGrams))
            .setScale(2, RoundingMode.HALF_UP).toDouble()

        val waterDelta = BigDecimal.valueOf(comparison.waterGrams)
            .subtract(BigDecimal.valueOf(baseline.waterGrams))
            .setScale(2, RoundingMode.HALF_UP).toDouble()

        val ratioDelta = BigDecimal.valueOf(comparison.ratioDenominator)
            .subtract(BigDecimal.valueOf(baseline.ratioDenominator))
            .setScale(2, RoundingMode.HALF_UP).toDouble()

        val durationDelta = comparison.brewDurationSeconds - baseline.brewDurationSeconds

        val tdsDelta = if (comparison.measuredTdsPercent != null && baseline.measuredTdsPercent != null) {
            BigDecimal.valueOf(comparison.measuredTdsPercent)
                .subtract(BigDecimal.valueOf(baseline.measuredTdsPercent))
                .setScale(2, RoundingMode.HALF_UP).toDouble()
        } else {
            null
        }

        val eyDelta = if (comparison.extractionYieldPercent != null && baseline.extractionYieldPercent != null) {
            BigDecimal.valueOf(comparison.extractionYieldPercent)
                .subtract(BigDecimal.valueOf(baseline.extractionYieldPercent))
                .setScale(2, RoundingMode.HALF_UP).toDouble()
        } else {
            null
        }

        val ratingDelta = if (comparison.rating != null && baseline.rating != null) {
            BigDecimal.valueOf(comparison.rating)
                .subtract(BigDecimal.valueOf(baseline.rating))
                .setScale(2, RoundingMode.HALF_UP).toDouble()
        } else {
            null
        }

        val tempDelta = BigDecimal.valueOf(comparison.waterTemperatureCelsius)
            .subtract(BigDecimal.valueOf(baseline.waterTemperatureCelsius))
            .setScale(1, RoundingMode.HALF_UP).toDouble()

        return BrewSessionComparison(
            baselineId = baseline.id,
            comparisonId = comparison.id,
            doseDeltaGrams = doseDelta,
            waterDeltaGrams = waterDelta,
            ratioDelta = ratioDelta,
            durationDeltaSeconds = durationDelta,
            tdsDeltaPercent = tdsDelta,
            eyDeltaPercent = eyDelta,
            ratingDelta = ratingDelta,
            temperatureDeltaCelsius = tempDelta,
            calculationMetadata = CalculationMetadata(
                exactInputs = mapOf(
                    "baselineDose" to "${baseline.doseGrams}g",
                    "comparisonDose" to "${comparison.doseGrams}g",
                    "baselineWater" to "${baseline.waterGrams}g",
                    "comparisonWater" to "${comparison.waterGrams}g"
                ),
                uncertainty = "Numeric difference is exact based on recorded log inputs."
            )
        )
    }

    /**
     * One-Variable-at-a-Time (OVAT) diagnostic recommendation engine.
     * Evaluates extraction yield, flow rate, and sensory taste flags to recommend
     * strictly ONE modification at a time.
     */
    fun evaluateOvat(entry: BrewJournalEntry, tasteFlags: List<String> = emptyList()): OvatRecommendation? {
        val combinedTaste = (tasteFlags + entry.tasteNotes).map { it.uppercase().trim() }
        val isEspresso = entry.isEspresso || entry.brewMethod.contains("Espresso", ignoreCase = true)
        val meanFlow = entry.espressoDetails?.meanFlowRateGps ?: 0.0
        val ey = entry.extractionYieldPercent
        val tds = entry.measuredTdsPercent
        if (entry.timerOnly || (ey == null && tds == null && combinedTaste.isEmpty())) return null

        // 1. Channeling / Fracture Detection
        val hasChannelingFlags = ("SOUR" in combinedTaste && "ASTRINGENT" in combinedTaste) ||
            ("SOUR" in combinedTaste && "BITTER" in combinedTaste) ||
            (isEspresso && entry.brewDurationSeconds < 20 && meanFlow > 3.0)

        if (hasChannelingFlags) {
            return OvatRecommendation(
                targetVariable = OvatTargetVariable.PUCK_PREPARATION,
                direction = AdjustmentDirection.MODIFY_TECHNIQUE,
                suggestedMagnitude = "WDT + level tamp",
                physicalReasoning = "Erratic fast flow and concurrent sour/astringent notes indicate micro-channeling where water bypasses bed zones.",
                expectedImpact = "Eliminates localized under- and over-extraction channels before adjusting grinder setting.",
                diagnosticBasis = "Channeling indicators detected: rapid duration (<20s) or dual sour/bitter sensory dissonance.",
                confidenceScore = 0.95
            )
        }

        // 2. Under-Extracted / Sour / Acidic
        val isUnderExtracted = (ey != null && ey < 18.0) ||
            combinedTaste.any { it in setOf("SOUR", "ACIDIC", "UNDER_EXTRACTED", "SALTY", "HOLLOW") }

        if (isUnderExtracted) {
            return OvatRecommendation(
                targetVariable = OvatTargetVariable.GRIND_SIZE,
                direction = AdjustmentDirection.FINER,
                suggestedMagnitude = "1 step / notch finer",
                physicalReasoning = "Decreasing particle radius increases contact surface area and tortuosity, increasing extraction yield.",
                expectedImpact = "Increases Extraction Yield by ~0.5% - 1.2%, softening sour acids into sweet caramelized sugars.",
                diagnosticBasis = "Extraction yield < 18.0% or dominant sour/under-extracted sensory descriptors.",
                confidenceScore = 0.92
            )
        }

        // 3. Over-Extracted / Bitter / Dry / Astringent
        val isOverExtracted = (ey != null && ey > 22.0) ||
            combinedTaste.any { it in setOf("BITTER", "ASTRINGENT", "DRY", "HARSH", "OVER_EXTRACTED", "ASHY") }

        if (isOverExtracted) {
            return OvatRecommendation(
                targetVariable = OvatTargetVariable.GRIND_SIZE,
                direction = AdjustmentDirection.COARSER,
                suggestedMagnitude = "1 step / notch coarser",
                physicalReasoning = "Increasing particle radius reduces contact time and curbs dissolution of high molecular-weight chlorogenic acid lactones and phenylindanes.",
                expectedImpact = "Decreases Extraction Yield by ~0.6% - 1.5%, removing dry astringency and harsh finish.",
                diagnosticBasis = "Extraction yield > 22.0% or prominent bitterness/dry astringency.",
                confidenceScore = 0.90
            )
        }

        // 4. Weak / Low TDS
        val isWeak = if (isEspresso) (tds != null && tds < 7.5) else (tds != null && tds < 1.15)
        if (isWeak) {
            return OvatRecommendation(
                targetVariable = OvatTargetVariable.BREW_RATIO_YIELD,
                direction = AdjustmentDirection.DECREASE,
                suggestedMagnitude = if (isEspresso) "-2.0g liquid yield" else "-1.0 ratio denominator (e.g. 1:16 -> 1:15)",
                physicalReasoning = "Decreasing solvent volume relative to solute raises total dissolved solids concentration.",
                expectedImpact = "Increases beverage strength and tactile mouthfeel body without over-extracting solids.",
                diagnosticBasis = "TDS below optimal beverage strength window.",
                confidenceScore = 0.88
            )
        }

        // 5. Strong / High TDS
        val isStrong = if (isEspresso) (tds != null && tds > 12.0) else (tds != null && tds > 1.55)
        if (isStrong) {
            return OvatRecommendation(
                targetVariable = OvatTargetVariable.BREW_RATIO_YIELD,
                direction = AdjustmentDirection.INCREASE,
                suggestedMagnitude = if (isEspresso) "+3.0g liquid yield" else "+15g bypass water",
                physicalReasoning = "Adding solvent dilutes intense dissolved solids concentration without altering extracted flavour profile.",
                expectedImpact = "Increases clarity and separation of delicate floral/fruit aromatic compounds.",
                diagnosticBasis = "TDS exceeds optimal drinkability window.",
                confidenceScore = 0.88
            )
        }

        // 6. Optimal & Dialed-in
        return OvatRecommendation(
            targetVariable = OvatTargetVariable.GRIND_SIZE,
            direction = AdjustmentDirection.FINER,
            suggestedMagnitude = "Hold steady (0 adjustment)",
            physicalReasoning = "Extraction parameters reside within the ideal SCA Gold Cup window.",
            expectedImpact = "Maintain consistent repeatable preparation.",
            diagnosticBasis = "Extraction yield and strength are harmoniously balanced.",
            confidenceScore = 0.98
        )
    }

    /**
     * Compute current and longest active brewing streak from session dates.
     */
    fun calculateStreak(sessionDates: List<LocalDate>, asOfDate: LocalDate = LocalDate.now()): BrewStreakResult {
        if (sessionDates.isEmpty()) {
            return BrewStreakResult(0, 0, 0, asOfDate)
        }

        val uniqueDates = sessionDates.toSortedSet()
        val activeCount = uniqueDates.size

        // Current Streak
        var currentStreak = 0
        var checkDate = if (uniqueDates.contains(asOfDate)) {
            asOfDate
        } else if (uniqueDates.contains(asOfDate.minusDays(1))) {
            asOfDate.minusDays(1)
        } else {
            null
        }

        while (checkDate != null && uniqueDates.contains(checkDate)) {
            currentStreak++
            checkDate = checkDate.minusDays(1)
        }

        // Longest Streak
        var longestStreak = 0
        var currentRun = 0
        var prevDate: LocalDate? = null

        for (date in uniqueDates) {
            if (prevDate == null || date.isEqual(prevDate.plusDays(1))) {
                currentRun++
            } else {
                currentRun = 1
            }
            if (currentRun > longestStreak) {
                longestStreak = currentRun
            }
            prevDate = date
        }

        return BrewStreakResult(
            currentStreakDays = currentStreak,
            longestStreakDays = longestStreak,
            activeDaysCount = activeCount,
            streakAsOfDate = asOfDate
        )
    }

    /**
     * Aggregate monthly statistics for a given year and month.
     */
    fun calculateMonthlyStats(entries: List<BrewJournalEntry>, year: Int, month: Int): MonthlyBrewStatistics {
        val zone = ZoneId.systemDefault()
        val monthlyEntries = entries.filter { entry ->
            val localDate = Instant.ofEpochMilli(entry.createdAt).atZone(zone).toLocalDate()
            localDate.year == year && localDate.monthValue == month
        }

        if (monthlyEntries.isEmpty()) {
            return MonthlyBrewStatistics(
                year = year,
                month = month,
                totalBrewsCount = 0,
                totalDoseGrams = 0.0,
                topBrewMethod = "None",
                averageRating = null,
                averageExtractionYield = null,
                brewCountByMethod = emptyMap()
            )
        }

        val totalBrews = monthlyEntries.size
        val totalDose = BigDecimal.valueOf(monthlyEntries.sumOf { it.doseGrams })
            .setScale(1, RoundingMode.HALF_UP).toDouble()

        val methodCounts = monthlyEntries.groupingBy { it.brewMethod }.eachCount()
        val topMethod = methodCounts.maxByOrNull { it.value }?.key ?: "Pour Over"

        val ratedEntries = monthlyEntries.mapNotNull { it.rating }
        val avgRating = if (ratedEntries.isNotEmpty()) {
            BigDecimal.valueOf(ratedEntries.average()).setScale(1, RoundingMode.HALF_UP).toDouble()
        } else {
            null
        }

        val eyEntries = monthlyEntries.mapNotNull { it.extractionYieldPercent }
        val avgEy = if (eyEntries.isNotEmpty()) {
            BigDecimal.valueOf(eyEntries.average()).setScale(2, RoundingMode.HALF_UP).toDouble()
        } else {
            null
        }

        return MonthlyBrewStatistics(
            year = year,
            month = month,
            totalBrewsCount = totalBrews,
            totalDoseGrams = totalDose,
            topBrewMethod = topMethod,
            averageRating = avgRating,
            averageExtractionYield = avgEy,
            brewCountByMethod = methodCounts
        )
    }

    /**
     * Estimate caffeine mass extracted in cup with explicit scientific uncertainty range.
     * Arabica ~1.20% w/w, Robusta ~2.20% w/w.
     * Extraction efficiency ~85% (range 80% to 90%).
     */
    fun estimateCaffeine(doseGrams: Double, arabicaFraction: Double = 1.0): CaffeineEstimate {
        val clampedArabica = arabicaFraction.coerceIn(0.0, 1.0)
        val robustaFraction = 1.0 - clampedArabica

        // Weighted caffeine content fraction
        val meanSpeciesFraction = (clampedArabica * 0.0120) + (robustaFraction * 0.0220)

        // Milligrams of caffeine in dry dose
        val totalAvailableMg = doseGrams * meanSpeciesFraction * 1000.0

        // Extracted nominal (85% efficiency)
        val nominalMg = BigDecimal.valueOf(totalAvailableMg * 0.85)
            .setScale(1, RoundingMode.HALF_UP).toDouble()

        // Uncertainty bounds (80% to 90%)
        val minMg = BigDecimal.valueOf(totalAvailableMg * 0.80)
            .setScale(1, RoundingMode.HALF_UP).toDouble()
        val maxMg = BigDecimal.valueOf(totalAvailableMg * 0.90)
            .setScale(1, RoundingMode.HALF_UP).toDouble()

        val speciesDescription = when {
            clampedArabica >= 0.99 -> "100% Arabica (~1.20% dry caffeine)"
            clampedArabica <= 0.01 -> "100% Robusta (~2.20% dry caffeine)"
            else -> "${(clampedArabica * 100).toInt()}% Arabica / ${(robustaFraction * 100).toInt()}% Robusta"
        }

        return CaffeineEstimate(
            doseGrams = doseGrams,
            speciesRatioDescription = speciesDescription,
            estimatedCaffeineMg = nominalMg,
            uncertaintyRangeMg = Pair(minMg, maxMg),
            assumedExtractionEfficiencyPercent = 85.0
        )
    }

    /**
     * Export a list of journal entries to standardized CSV string.
     */
    fun exportJournalToCsv(entries: List<BrewJournalEntry>): String {
        val sb = StringBuilder()
        sb.append(
            "id,createdAt,beanName,brewMethod,equipment,doseGrams,waterGrams,ratio,temperatureC,brewDurationSeconds,drawdownSeconds,tdsPercent,eyPercent,rating,grinderModel,grindSetting,tags,generalNotes\n"
        )

        for (e in entries) {
            val safeBean = e.beanName.replace("\"", "\"\"")
            val safeMethod = e.brewMethod.replace("\"", "\"\"")
            val safeGear = e.equipment.replace("\"", "\"\"")
            val safeGrinder = e.grinderModel.replace("\"", "\"\"")
            val safeSetting = e.grindSetting.replace("\"", "\"\"")
            val safeTags = e.tags.joinToString(";").replace("\"", "\"\"")
            val safeNotes = e.generalNotes.replace("\"", "\"\"").replace("\n", " ")

            sb.append("\"${e.id}\",")
            sb.append("${e.createdAt},")
            sb.append("\"$safeBean\",")
            sb.append("\"$safeMethod\",")
            sb.append("\"$safeGear\",")
            sb.append("${e.doseGrams},")
            sb.append("${e.waterGrams},")
            sb.append("1:${e.ratioDenominator},")
            sb.append("${e.waterTemperatureCelsius},")
            sb.append("${e.brewDurationSeconds},")
            sb.append("${e.drawdownSeconds ?: ""},")
            sb.append("${e.measuredTdsPercent ?: ""},")
            sb.append("${e.extractionYieldPercent ?: ""},")
            sb.append("${e.rating ?: ""},")
            sb.append("\"$safeGrinder\",")
            sb.append("\"$safeSetting\",")
            sb.append("\"$safeTags\",")
            sb.append("\"$safeNotes\"\n")
        }
        return sb.toString()
    }

    /**
     * Parse Beanconqueror JSON export into CoffeeBean and BrewJournalEntry lists.
     */
    fun parseBeanconquerorJson(json: String): Pair<List<CoffeeBean>, List<BrewJournalEntry>> {
        val beans = mutableListOf<CoffeeBean>()
        val brews = mutableListOf<BrewJournalEntry>()

        try {
            val root = RecordJson.gson.fromJson(json, JsonObject::class.java)

            // Parse BEANS
            if (root.has("BEANS") && root["BEANS"].isJsonArray) {
                val array = root.getAsJsonArray("BEANS")
                for (elem in array) {
                    if (elem.isJsonObject) {
                        val obj = elem.asJsonObject
                        val beanId = obj.get("id")?.asString ?: UUID.randomUUID().toString()
                        val name = obj.get("name")?.asString ?: "Imported Bean"
                        val roaster = obj.get("roaster")?.asString ?: "Unknown"
                        val origin = obj.get("origin")?.asString ?: "Unknown"
                        val roastDate = obj.get("roastingDate")?.asString
                        val process = obj.get("processing")?.asString ?: "Unknown"
                        val elevationStr = obj.get("elevation")?.asString ?: ""
                        val altitude = Regex("\\d+").find(elevationStr)?.value?.toIntOrNull()

                        beans.add(
                            CoffeeBean(
                                id = beanId,
                                name = name,
                                producer = roaster,
                                country = origin,
                                process = process,
                                roastDate = roastDate,
                                altitudeMeters = altitude,
                                provenance = "Imported from Beanconqueror export"
                            )
                        )
                    }
                }
            }

            // Parse BREWS
            if (root.has("BREWS") && root["BREWS"].isJsonArray) {
                val array = root.getAsJsonArray("BREWS")
                for (elem in array) {
                    if (elem.isJsonObject) {
                        val obj = elem.asJsonObject
                        val brewId = obj.get("id")?.asString ?: UUID.randomUUID().toString()
                        val beanId = obj.get("bean")?.asString
                        val grindSize = obj.get("grind_size")?.asString ?: ""
                        val grinder = obj.get("grinder")?.asString ?: ""
                        val method = obj.get("method_of_preparation")?.asString ?: "Pour Over"
                        val coffeeAmount = obj.get("coffee_amount")?.asDouble ?: 15.0
                        val waterAmount = obj.get("water_amount")?.asDouble ?: 250.0
                        val brewTime = obj.get("total_brew_time")?.asInt ?: 180
                        val beverageMass = obj.get("brew_beverage_quantity")?.asDouble
                        val tds = obj.get("tds")?.asDouble
                        val ratingRaw = obj.get("rating")?.asDouble
                        // Beanconqueror rating is often 1-5, map to 1-10
                        val mappedRating = if (ratingRaw != null && ratingRaw <= 5.0) {
                            ratingRaw * 2.0
                        } else {
                            ratingRaw
                        }
                        val note = obj.get("note")?.asString ?: ""

                        brews.add(
                            BrewJournalEntry(
                                id = brewId,
                                beanId = beanId,
                                beanName = beans.find { it.id == beanId }?.name ?: "Beanconqueror Bean",
                                brewMethod = method,
                                doseGrams = coffeeAmount,
                                waterGrams = waterAmount,
                                beverageMassGrams = beverageMass,
                                brewDurationSeconds = brewTime,
                                grinderModel = grinder,
                                grindSetting = grindSize,
                                measuredTdsPercent = tds,
                                rating = mappedRating,
                                generalNotes = note
                            )
                        )
                    }
                }
            }
        } catch (_: Exception) {
            // Graceful fallback on non-Beanconqueror schemas
        }

        return Pair(beans, brews)
    }
}
