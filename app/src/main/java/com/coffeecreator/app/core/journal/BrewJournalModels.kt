package com.coffeecreator.app.core.journal

import com.coffeecreator.app.core.model.CalculationMetadata
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate

/**
 * Detailed brew journal record representing a completed brewing event.
 * Pure Kotlin/JVM, zero Android/Room dependencies.
 */
data class BrewJournalEntry(
    val id: String,
    val recipeId: String? = null,
    val beanId: String? = null,
    val beanName: String = "Unknown Bean",
    val brewMethod: String,
    val equipment: String = "",
    val doseGrams: Double,
    val waterGrams: Double,
    val beverageMassGrams: Double? = null,
    val ratioDenominator: Double = if (doseGrams > 0) {
        BigDecimal.valueOf(waterGrams)
            .divide(BigDecimal.valueOf(doseGrams), 2, RoundingMode.HALF_UP)
            .toDouble()
    } else {
        0.0
    },
    val grinderModel: String = "",
    val grindSetting: String = "",
    val waterTemperatureCelsius: Double = 93.0,
    val brewDurationSeconds: Int,
    val drawdownSeconds: Int? = null,
    val measuredTdsPercent: Double? = null,
    val extractionYieldPercent: Double? = if (beverageMassGrams != null && measuredTdsPercent != null && doseGrams > 0) {
        BigDecimal.valueOf(beverageMassGrams)
            .multiply(BigDecimal.valueOf(measuredTdsPercent))
            .divide(BigDecimal.valueOf(doseGrams), 2, RoundingMode.HALF_UP)
            .toDouble()
    } else {
        null
    },
    val rating: Double? = null, // Scale 1.0 to 10.0
    val tasteNotes: List<String> = emptyList(),
    val sensoryScores: Map<String, Int> = emptyMap(), // Aroma, Acidity, Sweetness, Body, Bitterness (1-10)
    val generalNotes: String = "",
    /** The water value is a recipe target and the timer duration is the only observed event fact. */
    val timerOnly: Boolean = false,
    val photoUris: List<String> = emptyList(), // Preserved as standard URI strings
    val tags: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val dialInChainId: String? = null,
    val isEspresso: Boolean = brewMethod.contains("Espresso", ignoreCase = true),
    val espressoDetails: EspressoJournalDetails? = null
)

data class EspressoJournalDetails(
    val targetDoseGrams: Double,
    val actualDoseGrams: Double,
    val targetYieldGrams: Double,
    val actualYieldGrams: Double,
    val targetDurationSeconds: Int,
    val actualDurationSeconds: Int,
    val meanFlowRateGps: Double = if (actualDurationSeconds > 0) {
        BigDecimal.valueOf(actualYieldGrams)
            .divide(BigDecimal.valueOf(actualDurationSeconds.toLong()), 2, RoundingMode.HALF_UP)
            .toDouble()
    } else {
        0.0
    },
    val targetFlowRateGps: Double = if (targetDurationSeconds > 0) {
        BigDecimal.valueOf(targetYieldGrams)
            .divide(BigDecimal.valueOf(targetDurationSeconds.toLong()), 2, RoundingMode.HALF_UP)
            .toDouble()
    } else {
        0.0
    },
    val basketSizeGrams: Double? = null,
    val puckPrepMethods: List<String> = emptyList(), // e.g. "WDT", "RDT", "Screen", "Puck Paper"
    val firstDropSeconds: Int? = null,
    val peakPressureBars: Double? = null
)

/**
 * Filter specification for querying brew history.
 */
data class BrewFilterCriteria(
    val beanId: String? = null,
    val brewMethod: String? = null,
    val equipment: String? = null,
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
    val minRating: Double? = null,
    val maxRating: Double? = null,
    val tags: Set<String> = emptySet(),
    val searchKeyword: String? = null
)

/**
 * Deterministic numeric deltas between two brew sessions.
 */
data class BrewSessionComparison(
    val baselineId: String,
    val comparisonId: String,
    val doseDeltaGrams: Double,
    val waterDeltaGrams: Double,
    val ratioDelta: Double,
    val durationDeltaSeconds: Int,
    val tdsDeltaPercent: Double?,
    val eyDeltaPercent: Double?,
    val ratingDelta: Double?,
    val temperatureDeltaCelsius: Double,
    val calculationMetadata: CalculationMetadata = CalculationMetadata()
)

/**
 * One-Variable-at-a-Time (OVAT) diagnostic suggestion.
 */
enum class OvatTargetVariable {
    GRIND_SIZE,
    BREW_RATIO_YIELD,
    WATER_TEMPERATURE,
    PUCK_PREPARATION
}

enum class AdjustmentDirection {
    INCREASE,
    DECREASE,
    FINER,
    COARSER,
    MODIFY_TECHNIQUE
}

data class OvatRecommendation(
    val targetVariable: OvatTargetVariable,
    val direction: AdjustmentDirection,
    val suggestedMagnitude: String,
    val physicalReasoning: String,
    val expectedImpact: String,
    val diagnosticBasis: String,
    val confidenceScore: Double = 0.95
)

/**
 * Caffeine estimation result model with explicit uncertainty metadata.
 */
data class CaffeineEstimate(
    val doseGrams: Double,
    val speciesRatioDescription: String,
    val estimatedCaffeineMg: Double,
    val uncertaintyRangeMg: Pair<Double, Double>,
    val assumedExtractionEfficiencyPercent: Double,
    val disclaimer: String = "Heuristic estimation; actual caffeine varies by bean variety, " +
        "agricultural terroir, roast degree, and extraction dynamics. No clinical or laboratory diagnostic accuracy is claimed."
)

/**
 * Local calendar analytics & streak models.
 */
data class BrewStreakResult(
    val currentStreakDays: Int,
    val longestStreakDays: Int,
    val activeDaysCount: Int,
    val streakAsOfDate: LocalDate
)

data class MonthlyBrewStatistics(
    val year: Int,
    val month: Int,
    val totalBrewsCount: Int,
    val totalDoseGrams: Double,
    val topBrewMethod: String,
    val averageRating: Double?,
    val averageExtractionYield: Double?,
    val brewCountByMethod: Map<String, Int>
)

/**
 * Cafe Cup Journal record (manual entry, zero remote maps).
 */
data class CafeCupEntry(
    val id: String,
    val cafeName: String,
    val roaster: String = "",
    val drinkName: String,
    val originOrBean: String = "",
    val pricePaid: String = "",
    val rating: Double = 8.0, // 1.0 to 10.0
    val notes: String = "",
    val photoUris: List<String> = emptyList(),
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Backup / Import conflict status.
 */
enum class ImportRecordStatus {
    NEW,
    IDENTICAL,
    MODIFIED_REMOTE_NEWER,
    CONFLICT_LOCAL_NEWER,
    CONFLICT_DIVERGENT
}

data class ImportConflictItem<T>(
    val recordId: String,
    val localRecord: T?,
    val incomingRecord: T,
    val status: ImportRecordStatus,
    val localHash: String?,
    val incomingHash: String
)

data class ImportConflictPreview(
    val newRecordsCount: Int,
    val identicalCount: Int,
    val conflictCount: Int,
    val items: List<ImportConflictItem<*>>
)
