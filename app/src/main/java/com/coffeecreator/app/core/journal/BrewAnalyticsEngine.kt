package com.coffeecreator.app.core.journal

import com.coffeecreator.app.core.data.RecordJson
import com.coffeecreator.app.core.model.CalculationMetadata
import com.coffeecreator.app.core.model.CoffeeBean
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParseException
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
        val context = OvatContext.from(entry, tasteFlags)
        if (!context.hasRecommendationEvidence) return null
        return channelingRecommendation(context)
            ?: underExtractedRecommendation(context)
            ?: overExtractedRecommendation(context)
            ?: weakBrewRecommendation(context)
            ?: strongBrewRecommendation(context)
            ?: balancedRecommendation().takeIf { context.hasMeasuredBalancedCup }
    }

    private fun channelingRecommendation(context: OvatContext): OvatRecommendation? {
        val hasSensoryDissonance = "SOUR" in context.taste &&
            ("ASTRINGENT" in context.taste || "BITTER" in context.taste)
        val hasRapidEspressoFlow = context.isEspresso &&
            context.entry.brewDurationSeconds < 20 && context.meanFlow > 3.0
        if (!hasSensoryDissonance && !hasRapidEspressoFlow) return null
        return OvatRecommendation(
            targetVariable = OvatTargetVariable.PUCK_PREPARATION,
            direction = AdjustmentDirection.MODIFY_TECHNIQUE,
            suggestedMagnitude = "WDT + level tamp",
            physicalReasoning = "Erratic fast flow and concurrent sour/astringent notes indicate micro-channeling " +
                "where water bypasses bed zones.",
            expectedImpact = "Eliminates localized under- and over-extraction channels before adjusting grinder setting.",
            diagnosticBasis = "Channeling indicators detected: rapid duration (<20s) or dual sour/bitter sensory dissonance.",
            confidenceScore = 0.95
        )
    }

    private fun underExtractedRecommendation(context: OvatContext): OvatRecommendation? {
        val isUnderExtracted = (context.ey != null && context.ey < 18.0) ||
            context.taste.any { it in UNDER_EXTRACTED_TASTES }
        if (!isUnderExtracted) return null
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

    private fun overExtractedRecommendation(context: OvatContext): OvatRecommendation? {
        val isOverExtracted = (context.ey != null && context.ey > 22.0) ||
            context.taste.any { it in OVER_EXTRACTED_TASTES }
        if (!isOverExtracted) return null
        return OvatRecommendation(
            targetVariable = OvatTargetVariable.GRIND_SIZE,
            direction = AdjustmentDirection.COARSER,
            suggestedMagnitude = "1 step / notch coarser",
            physicalReasoning = "Increasing particle radius reduces contact time and curbs dissolution of high " +
                "molecular-weight chlorogenic acid lactones and phenylindanes.",
            expectedImpact = "Decreases Extraction Yield by ~0.6% - 1.5%, removing dry astringency and harsh finish.",
            diagnosticBasis = "Extraction yield > 22.0% or prominent bitterness/dry astringency.",
            confidenceScore = 0.90
        )
    }

    private fun weakBrewRecommendation(context: OvatContext): OvatRecommendation? {
        val threshold = if (context.isEspresso) 7.5 else 1.15
        if (context.tds == null || context.tds >= threshold) return null
        return OvatRecommendation(
            targetVariable = OvatTargetVariable.BREW_RATIO_YIELD,
            direction = AdjustmentDirection.DECREASE,
            suggestedMagnitude = if (context.isEspresso) "-2.0g liquid yield" else "-1.0 ratio denominator (e.g. 1:16 -> 1:15)",
            physicalReasoning = "Decreasing solvent volume relative to solute raises total dissolved solids concentration.",
            expectedImpact = "Increases beverage strength and tactile mouthfeel body without over-extracting solids.",
            diagnosticBasis = "TDS below optimal beverage strength window.",
            confidenceScore = 0.88
        )
    }

    private fun strongBrewRecommendation(context: OvatContext): OvatRecommendation? {
        val threshold = if (context.isEspresso) 12.0 else 1.55
        if (context.tds == null || context.tds <= threshold) return null
        return OvatRecommendation(
            targetVariable = OvatTargetVariable.BREW_RATIO_YIELD,
            direction = AdjustmentDirection.INCREASE,
            suggestedMagnitude = if (context.isEspresso) "+3.0g liquid yield" else "+15g bypass water",
            physicalReasoning = "Adding solvent dilutes intense dissolved solids concentration without altering extracted flavour profile.",
            expectedImpact = "Increases clarity and separation of delicate floral/fruit aromatic compounds.",
            diagnosticBasis = "TDS exceeds optimal drinkability window.",
            confidenceScore = 0.88
        )
    }

    private fun balancedRecommendation() = OvatRecommendation(
        targetVariable = OvatTargetVariable.GRIND_SIZE,
        direction = AdjustmentDirection.FINER,
        suggestedMagnitude = "Hold steady (0 adjustment)",
        physicalReasoning = "Recorded extraction yield and beverage strength fall within method-specific reference ranges.",
        expectedImpact = "Maintain consistent repeatable preparation.",
        diagnosticBasis = "Measured extraction yield and strength fall within method-specific reference ranges.",
        confidenceScore = 0.80
    )

    private data class OvatContext(
        val entry: BrewJournalEntry,
        val taste: List<String>,
        val isEspresso: Boolean,
        val meanFlow: Double,
        val ey: Double?,
        val tds: Double?
    ) {
        val hasMeasuredBalancedCup: Boolean
            get() {
                val expectedTds = if (isEspresso) 7.5..12.0 else 1.15..1.45
                return ey?.let { it in 18.0..22.0 } == true && tds?.let { it in expectedTds } == true
            }

        val hasRecommendationEvidence: Boolean
            get() = !entry.timerOnly && (ey != null || tds != null || taste.isNotEmpty())

        companion object {
            fun from(entry: BrewJournalEntry, tasteFlags: List<String>): OvatContext {
                val isEspresso = entry.isEspresso || entry.brewMethod.contains("Espresso", ignoreCase = true)
                return OvatContext(
                    entry = entry,
                    taste = (tasteFlags + entry.tasteNotes)
                        .map { it.uppercase().trim() }
                        .filter { it in RECOGNIZED_TASTES },
                    isEspresso = isEspresso,
                    meanFlow = entry.espressoDetails?.meanFlowRateGps ?: 0.0,
                    ey = entry.extractionYieldPercent,
                    tds = entry.measuredTdsPercent
                )
            }
        }
    }

    private val UNDER_EXTRACTED_TASTES = setOf("SOUR", "ACIDIC", "UNDER_EXTRACTED", "SALTY", "HOLLOW")
    private val OVER_EXTRACTED_TASTES = setOf("BITTER", "ASTRINGENT", "DRY", "HARSH", "OVER_EXTRACTED", "ASHY")
    private val RECOGNIZED_TASTES = UNDER_EXTRACTED_TASTES + OVER_EXTRACTED_TASTES

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
            "id,createdAt,beanName,brewMethod,equipment,doseGrams,waterGrams,ratio,temperatureC," +
                "brewDurationSeconds,drawdownSeconds,tdsPercent,eyPercent,rating,grinderModel,grindSetting,tags,generalNotes\n"
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
        val root = parseJsonObject(json) ?: return emptyList<CoffeeBean>() to emptyList()
        val beans = root.objects("BEANS").map(::parseImportedBean)
        val brews = root.objects("BREWS").mapNotNull { parseImportedBrew(it, beans) }
        return beans to brews
    }

    private fun parseJsonObject(json: String): JsonObject? = try {
        RecordJson.gson.fromJson(json, JsonObject::class.java)
    } catch (_: JsonParseException) {
        null
    } catch (_: IllegalStateException) {
        null
    }

    private fun JsonObject.objects(field: String): List<JsonObject> =
        get(field)?.takeIf(JsonElement::isJsonArray)?.asJsonArray?.mapNotNull { element ->
            element.takeIf(JsonElement::isJsonObject)?.asJsonObject
        }.orEmpty()

    private fun parseImportedBean(obj: JsonObject): CoffeeBean {
        val elevation = obj.stringValue("elevation").orEmpty()
        return CoffeeBean(
            id = obj.stringValue("id") ?: UUID.randomUUID().toString(),
            name = obj.stringValue("name") ?: "Imported Bean",
            producer = obj.stringValue("roaster") ?: "Unknown",
            country = obj.stringValue("origin") ?: "Unknown",
            process = obj.stringValue("processing") ?: "Unknown",
            roastDate = obj.stringValue("roastingDate"),
            altitudeMeters = Regex("\\d+").find(elevation)?.value?.toIntOrNull(),
            provenance = "Imported from Beanconqueror export"
        )
    }

    private fun parseImportedBrew(obj: JsonObject, beans: List<CoffeeBean>): BrewJournalEntry? {
        val doseGrams = obj.doubleValue("coffee_amount")?.takeIf { it > 0.0 } ?: return null
        val waterGrams = obj.doubleValue("water_amount")?.takeIf { it > 0.0 } ?: return null
        val brewDurationSeconds = obj.intValue("total_brew_time")?.takeIf { it > 0 } ?: return null
        val beanId = obj.stringValue("bean")
        val rating = obj.doubleValue("rating")?.let { value -> if (value <= 5.0) value * 2.0 else value }
        return BrewJournalEntry(
            id = obj.stringValue("id") ?: UUID.randomUUID().toString(),
            beanId = beanId,
            beanName = beans.find { it.id == beanId }?.name ?: "Beanconqueror Bean",
            brewMethod = obj.stringValue("method_of_preparation") ?: "Pour Over",
            doseGrams = doseGrams,
            waterGrams = waterGrams,
            beverageMassGrams = obj.doubleValue("brew_beverage_quantity"),
            brewDurationSeconds = brewDurationSeconds,
            grinderModel = obj.stringValue("grinder") ?: "",
            grindSetting = obj.stringValue("grind_size") ?: "",
            measuredTdsPercent = obj.doubleValue("tds"),
            rating = rating,
            generalNotes = obj.stringValue("note") ?: ""
        )
    }

    private fun JsonObject.stringValue(field: String): String? = get(field)?.takeIf(JsonElement::isJsonPrimitive)?.asJsonPrimitive?.asString

    private fun JsonObject.doubleValue(field: String): Double? = stringValue(field)?.toDoubleOrNull()?.takeIf(Double::isFinite)

    private fun JsonObject.intValue(field: String): Int? = stringValue(field)
        ?.toDoubleOrNull()
        ?.takeIf { it.isFinite() && it % 1.0 == 0.0 }
        ?.toInt()
}
