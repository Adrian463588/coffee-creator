package com.coffeecreator.app.core.catalog

import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate

private const val MILLIS_PER_DAY = 86_400_000L

// ============================================================================
// S2-001 & S2-002: Bean & Bag Inventory Domain Models
// ============================================================================

enum class RoastLevel {
    CINNAMON,
    LIGHT,
    MEDIUM_LIGHT,
    MEDIUM,
    MEDIUM_DARK,
    DARK,
    VERY_DARK,
    UNKNOWN
}

enum class ProcessMethod {
    WASHED,
    NATURAL,
    HONEY,
    ANAEROBIC_NATURAL,
    ANAEROBIC_WASHED,
    CARBONIC_MACERATION,
    WET_HULLED,
    EXPERIMENTAL,
    UNKNOWN
}

enum class BagStatus {
    PURCHASED,
    ACTIVE,
    FINISHED,
    FROZEN
}

data class FreezeEvent(
    val frozenAtEpochMs: Long,
    val thawedAtEpochMs: Long? = null
) {
    val isOngoing: Boolean get() = thawedAtEpochMs == null

    fun durationDays(currentEpochMs: Long = System.currentTimeMillis()): Long {
        val end = thawedAtEpochMs ?: currentEpochMs
        val elapsedMs = maxOf(0L, end - frozenAtEpochMs)
        return elapsedMs / MILLIS_PER_DAY
    }
}

data class BagInventoryItem(
    val id: String,
    val beanId: String,
    val packageMassGrams: BigDecimal,
    val remainingMassGrams: BigDecimal,
    val status: BagStatus = BagStatus.PURCHASED,
    val freezeHistory: List<FreezeEvent> = emptyList(),
    val openedAtEpochMs: Long? = null,
    val finishedAtEpochMs: Long? = null,
    val pricePaid: BigDecimal? = null,
    val currency: String = "USD"
) {
    init {
        require(remainingMassGrams >= BigDecimal.ZERO) { "Remaining mass cannot be negative." }
        require(remainingMassGrams <= packageMassGrams) { "Remaining mass cannot exceed package size." }
    }
}

data class DetailedBeanRecord(
    val id: String,
    val roaster: String,
    val name: String,
    val originCountry: String,
    val region: String = "Unknown",
    val farm: String = "Unknown",
    val producer: String = "Unknown",
    val varietal: String = "Unknown",
    val process: ProcessMethod = ProcessMethod.UNKNOWN,
    val altitudeMeters: Int? = null,
    val harvestDate: String? = null,
    val roastDate: LocalDate? = null,
    val roastLevel: RoastLevel = RoastLevel.UNKNOWN,
    val tastingNotes: List<String> = emptyList(),
    val tags: List<String> = emptyList(),
    val isFavorite: Boolean = false,
    val bags: List<BagInventoryItem> = emptyList(),
    val photoUri: String? = null,
    val createdAtEpochMs: Long = System.currentTimeMillis()
)

// ============================================================================
// S2-003: Roast-Rest Calculator Models & Contracts
// ============================================================================

enum class FreshnessState {
    TOO_FRESH_DEGASSING,
    PEAK_FLAVOR,
    STABLE_MATURE,
    STALE_OXIDIZED
}

data class FreshnessAnalysis(
    val effectiveRestDays: Long,
    val totalFrozenDays: Long,
    val state: FreshnessState,
    val peakStartDay: Int,
    val peakEndDay: Int,
    val staleCutoffDay: Int,
    val heuristicRationale: String
)

object RoastRestCalculator {
    private const val LIGHT_NATURAL_PEAK_START_DAY = 11
    private const val LIGHT_NATURAL_PEAK_END_DAY = 35
    private const val LIGHT_NATURAL_STALE_CUTOFF_DAY = 75
    private const val LIGHT_WASHED_PEAK_START_DAY = 8
    private const val LIGHT_WASHED_PEAK_END_DAY = 28
    private const val LIGHT_WASHED_STALE_CUTOFF_DAY = 60
    private const val MEDIUM_LIGHT_PEAK_START_DAY = 7
    private const val MEDIUM_LIGHT_PEAK_END_DAY = 24
    private const val MEDIUM_LIGHT_STALE_CUTOFF_DAY = 50
    private const val MEDIUM_PEAK_START_DAY = 6
    private const val MEDIUM_PEAK_END_DAY = 21
    private const val MEDIUM_STALE_CUTOFF_DAY = 45
    private const val MEDIUM_DARK_PEAK_START_DAY = 5
    private const val MEDIUM_DARK_PEAK_END_DAY = 16
    private const val MEDIUM_DARK_STALE_CUTOFF_DAY = 35
    private const val DARK_PEAK_START_DAY = 4
    private const val DARK_PEAK_END_DAY = 12
    private const val DARK_STALE_CUTOFF_DAY = 28
    private const val UNKNOWN_PEAK_START_DAY = 7
    private const val UNKNOWN_PEAK_END_DAY = 21
    private const val UNKNOWN_STALE_CUTOFF_DAY = 45

    private data class RestWindow(val peakStart: Int, val peakEnd: Int, val staleCutoff: Int)

    fun calculateFreshness(
        roastDate: LocalDate,
        roastLevel: RoastLevel,
        process: ProcessMethod,
        freezeHistory: List<FreezeEvent> = emptyList(),
        evaluationDate: LocalDate = LocalDate.now(),
        isCurrentlyFrozen: Boolean = false
    ): FreshnessAnalysis {
        val totalCalendarDays = maxOf(0L, java.time.temporal.ChronoUnit.DAYS.between(roastDate, evaluationDate))

        // Sum total frozen days
        val totalFrozenDays = freezeHistory.sumOf { it.durationDays() }
        val effectiveRestDays = if (isCurrentlyFrozen) {
            // When currently frozen, effective aging is paused at the frozen moment
            maxOf(0L, totalCalendarDays - totalFrozenDays)
        } else {
            maxOf(0L, totalCalendarDays - totalFrozenDays)
        }

        val window = when (roastLevel) {
            RoastLevel.LIGHT, RoastLevel.CINNAMON -> {
                if (process == ProcessMethod.NATURAL || process == ProcessMethod.ANAEROBIC_NATURAL) {
                    RestWindow(
                        LIGHT_NATURAL_PEAK_START_DAY,
                        LIGHT_NATURAL_PEAK_END_DAY,
                        LIGHT_NATURAL_STALE_CUTOFF_DAY
                    )
                } else {
                    RestWindow(
                        LIGHT_WASHED_PEAK_START_DAY,
                        LIGHT_WASHED_PEAK_END_DAY,
                        LIGHT_WASHED_STALE_CUTOFF_DAY
                    )
                }
            }
            RoastLevel.MEDIUM_LIGHT -> RestWindow(
                MEDIUM_LIGHT_PEAK_START_DAY,
                MEDIUM_LIGHT_PEAK_END_DAY,
                MEDIUM_LIGHT_STALE_CUTOFF_DAY
            )
            RoastLevel.MEDIUM -> RestWindow(MEDIUM_PEAK_START_DAY, MEDIUM_PEAK_END_DAY, MEDIUM_STALE_CUTOFF_DAY)
            RoastLevel.MEDIUM_DARK -> RestWindow(
                MEDIUM_DARK_PEAK_START_DAY,
                MEDIUM_DARK_PEAK_END_DAY,
                MEDIUM_DARK_STALE_CUTOFF_DAY
            )
            RoastLevel.DARK, RoastLevel.VERY_DARK -> RestWindow(DARK_PEAK_START_DAY, DARK_PEAK_END_DAY, DARK_STALE_CUTOFF_DAY)
            RoastLevel.UNKNOWN -> RestWindow(UNKNOWN_PEAK_START_DAY, UNKNOWN_PEAK_END_DAY, UNKNOWN_STALE_CUTOFF_DAY)
        }

        val state = when {
            effectiveRestDays < window.peakStart -> FreshnessState.TOO_FRESH_DEGASSING
            effectiveRestDays <= window.peakEnd -> FreshnessState.PEAK_FLAVOR
            effectiveRestDays <= window.staleCutoff -> FreshnessState.STABLE_MATURE
            else -> FreshnessState.STALE_OXIDIZED
        }

        val rationale = when (state) {
            FreshnessState.TOO_FRESH_DEGASSING ->
                "Day $effectiveRestDays: Rapid CO2 desorption underway. High risk of extraction channeling and sour, unstable crema."
            FreshnessState.PEAK_FLAVOR ->
                "Day $effectiveRestDays: Optimal aromatic expression (peak window: days ${window.peakStart}–${window.peakEnd}). " +
                    "Balanced solubility and sweetness."
            FreshnessState.STABLE_MATURE ->
                "Day $effectiveRestDays: Degassing complete; gradual volatile loss. Consistent extraction, slightly muted florals."
            FreshnessState.STALE_OXIDIZED ->
                "Day $effectiveRestDays: Exceeded day ${window.staleCutoff} threshold. " +
                    "Oxidation of delicate lipids produces papery, woody, flat flavor."
        }

        return FreshnessAnalysis(
            effectiveRestDays = effectiveRestDays,
            totalFrozenDays = totalFrozenDays,
            state = state,
            peakStartDay = window.peakStart,
            peakEndDay = window.peakEnd,
            staleCutoffDay = window.staleCutoff,
            heuristicRationale = rationale
        )
    }
}

// ============================================================================
// S2-004 & S2-005: Grinder Records & Equipment Catalog
// ============================================================================

enum class CalibrationUnit {
    CLICKS,
    MICRONS,
    STEPLESS_DEGREES,
    NUMERICAL_MARKS
}

enum class BurrType {
    CONICAL,
    FLAT,
    GHOST_TOOTH,
    HYBRID
}

enum class BurrMaterial {
    STAINLESS_STEEL,
    TITANIUM_COATED,
    DLC_COATED,
    CERAMIC,
    CARBON_STEEL
}

data class BurrProfile(
    val burrType: BurrType,
    val diameterMm: BigDecimal,
    val material: BurrMaterial,
    val burrModelName: String = "Stock"
)

data class GrinderRecord(
    val id: String,
    val brand: String,
    val model: String,
    val calibrationUnit: CalibrationUnit,
    val burrProfile: BurrProfile,
    val zeroPointOffset: BigDecimal = BigDecimal.ZERO,
    val clicksPerRotation: Int? = null,
    val typicalRpm: Int? = null,
    val notes: String = "",
    val isFavorite: Boolean = false
)

enum class EquipmentCategory {
    DRIPPER,
    FILTER_PAPER,
    KETTLE,
    SCALE,
    ESPRESSO_MACHINE,
    PORTAFILTER_BASKET,
    PUCK_PREP_TOOL,
    OTHER
}

data class EquipmentItem(
    val id: String,
    val category: EquipmentCategory,
    val brand: String,
    val model: String,
    val parameters: Map<String, String> = emptyMap(),
    val isFavorite: Boolean = false,
    val tags: List<String> = emptyList()
)

// ============================================================================
// S2-006: Water Mineral Profile & Chemistry Engine
// ============================================================================

enum class WaterRecipePreset {
    SCA_STANDARD,
    RAO_PERGER,
    HENDON_WATER_FOR_COFFEE,
    CUSTOM
}

data class WaterMineralProfile(
    val id: String,
    val name: String,
    /** Dissolved calcium concentration in mg/L. */
    val calciumPpm: BigDecimal,
    /** Dissolved magnesium concentration in mg/L. */
    val magnesiumPpm: BigDecimal,
    /** Dissolved sodium concentration in mg/L. */
    val sodiumPpm: BigDecimal,
    val potassiumPpm: BigDecimal = BigDecimal.ZERO,
    /** Bicarbonate concentration in mg/L. */
    val bicarbonatePpm: BigDecimal,
    val totalHardnessPpmCaCO3: BigDecimal,
    val totalAlkalinityPpmCaCO3: BigDecimal,
    val calculatedTdsPpm: BigDecimal,
    val ph: BigDecimal? = null,
    val preset: WaterRecipePreset = WaterRecipePreset.CUSTOM
)

object WaterChemistryCalculator {
    private val FACTOR_CA = BigDecimal("2.4973")
    private val FACTOR_MG = BigDecimal("4.1179")
    private val FACTOR_ALK = BigDecimal("0.82016")

    fun computeHardness(calciumPpm: BigDecimal, magnesiumPpm: BigDecimal): BigDecimal {
        return calciumPpm.multiply(FACTOR_CA)
            .add(magnesiumPpm.multiply(FACTOR_MG))
            .setScale(2, RoundingMode.HALF_UP)
    }

    fun computeAlkalinity(bicarbonatePpm: BigDecimal): BigDecimal {
        return bicarbonatePpm.multiply(FACTOR_ALK)
            .setScale(2, RoundingMode.HALF_UP)
    }

    fun formulateWater(
        name: String,
        calciumPpm: BigDecimal,
        magnesiumPpm: BigDecimal,
        sodiumPpm: BigDecimal,
        potassiumPpm: BigDecimal = BigDecimal.ZERO,
        bicarbonatePpm: BigDecimal,
        ph: BigDecimal? = null,
        preset: WaterRecipePreset = WaterRecipePreset.CUSTOM,
        id: String = java.util.UUID.randomUUID().toString()
    ): WaterMineralProfile {
        val gh = computeHardness(calciumPpm, magnesiumPpm)
        val kh = computeAlkalinity(bicarbonatePpm)
        val tds = calciumPpm.add(magnesiumPpm).add(sodiumPpm).add(potassiumPpm).add(bicarbonatePpm)
            .setScale(2, RoundingMode.HALF_UP)

        return WaterMineralProfile(
            id = id,
            name = name,
            calciumPpm = calciumPpm,
            magnesiumPpm = magnesiumPpm,
            sodiumPpm = sodiumPpm,
            potassiumPpm = potassiumPpm,
            bicarbonatePpm = bicarbonatePpm,
            totalHardnessPpmCaCO3 = gh,
            totalAlkalinityPpmCaCO3 = kh,
            calculatedTdsPpm = tds,
            ph = ph,
            preset = preset
        )
    }

    val PRESET_SCA = formulateWater(
        name = "SCA Standard Water",
        calciumPpm = BigDecimal("17.00"),
        magnesiumPpm = BigDecimal("6.20"),
        sodiumPpm = BigDecimal("10.00"),
        potassiumPpm = BigDecimal.ZERO,
        bicarbonatePpm = BigDecimal("48.77"),
        ph = BigDecimal("7.0"),
        preset = WaterRecipePreset.SCA_STANDARD,
        id = "preset-sca-standard"
    )

    val PRESET_RAO_PERGER = formulateWater(
        name = "Rao / Perger Water",
        calciumPpm = BigDecimal("10.00"),
        magnesiumPpm = BigDecimal("20.65"),
        sodiumPpm = BigDecimal("5.00"),
        potassiumPpm = BigDecimal.ZERO,
        bicarbonatePpm = BigDecimal("54.87"),
        ph = BigDecimal("7.2"),
        preset = WaterRecipePreset.RAO_PERGER,
        id = "preset-rao-perger"
    )

    val PRESET_HENDON = formulateWater(
        name = "Hendon 'Water for Coffee'",
        calciumPpm = BigDecimal("5.00"),
        magnesiumPpm = BigDecimal("28.53"),
        sodiumPpm = BigDecimal("5.00"),
        potassiumPpm = BigDecimal.ZERO,
        bicarbonatePpm = BigDecimal("36.58"),
        ph = BigDecimal("6.8"),
        preset = WaterRecipePreset.HENDON_WATER_FOR_COFFEE,
        id = "preset-hendon-coffee"
    )

    val ALL_PRESETS = listOf(PRESET_SCA, PRESET_RAO_PERGER, PRESET_HENDON)
}

// ============================================================================
// S2-007: Food-Grade Ingredient Catalog Models
// ============================================================================

data class FoodGradeIngredientItem(
    val id: String,
    val name: String,
    val densityGramsPerMl: BigDecimal,
    val allergens: List<String> = emptyList(),
    val supplierMaxPercent: BigDecimal? = null,
    val isFoodGradeVerified: Boolean = false,
    val casNumber: String? = null,
    val provenance: String = "Verified food additive datasheet"
) {
    init {
        require(densityGramsPerMl > BigDecimal.ZERO) { "Density must be positive (g/mL)." }
        supplierMaxPercent?.let {
            require(it >= BigDecimal.ZERO && it <= BigDecimal("100.00")) {
                "Supplier limit must be between 0 and 100%."
            }
        }
    }
}

// ============================================================================
// S2-008: Bag Offline OCR Models
// ============================================================================

data class ExtractedOcrField<T>(
    val value: T?,
    /** Null until a calibrated OCR model can provide a validated probability. */
    val confidence: Double? = null,
    val rawSnippet: String = ""
)

data class BagOcrParsedDraft(
    val photoUri: String?,
    val rawExtractedText: String,
    val roaster: ExtractedOcrField<String>,
    val beanName: ExtractedOcrField<String>,
    val origin: ExtractedOcrField<String>,
    val process: ExtractedOcrField<ProcessMethod>,
    val varietal: ExtractedOcrField<String>,
    val altitudeMeters: ExtractedOcrField<Int>,
    val roastDate: ExtractedOcrField<LocalDate>,
    val packageMassGrams: ExtractedOcrField<BigDecimal>
)

object OfflineBagOcrParser {
    private val countries = listOf(
        "ETHIOPIA", "COLOMBIA", "KENYA", "GUATEMALA", "PANAMA", "INDONESIA",
        "COSTA RICA", "BRAZIL", "RWANDA", "BURUNDI", "YEMEN", "HONDURAS"
    )
    private val altitudeRegex = Regex("(\\d{3,4})\\s*(?:-|–)?\\s*(\\d{3,4})?\\s*(?:M|MASL|MTR)", RegexOption.IGNORE_CASE)
    private val roastDateRegex = Regex("(\\d{4})[-/](\\d{2})[-/](\\d{2})")
    private val packageMassRegex = Regex("(\\d+(?:\\.\\d+)?)\\s*(G|KG)", RegexOption.IGNORE_CASE)

    fun parseBagText(photoUri: String?, text: String): BagOcrParsedDraft {
        val lines = text.lines().map(String::trim).filter(String::isNotBlank)
        val fields = BagFields()
        lines.forEach { fields.read(it) }
        val roaster = lines.firstOrNull { !it.contains("ROAST", ignoreCase = true) && !it.contains("WEIGHT", ignoreCase = true) }
        val beanName = lines.drop(1).firstOrNull { it != fields.origin && !it.contains("PROCESS", ignoreCase = true) }
        return BagOcrParsedDraft(
            photoUri = photoUri,
            rawExtractedText = text,
            roaster = ExtractedOcrField(roaster, rawSnippet = roaster ?: ""),
            beanName = ExtractedOcrField(beanName, rawSnippet = beanName ?: ""),
            origin = ExtractedOcrField(fields.origin, rawSnippet = fields.origin ?: ""),
            process = ExtractedOcrField(
                fields.process.takeUnless { it == ProcessMethod.UNKNOWN },
                rawSnippet = processSnippet(lines)
            ),
            varietal = ExtractedOcrField(null, rawSnippet = ""),
            altitudeMeters = ExtractedOcrField(fields.altitude, rawSnippet = fields.altitude?.toString() ?: ""),
            roastDate = ExtractedOcrField(fields.roastDate, rawSnippet = fields.roastDate?.toString() ?: ""),
            packageMassGrams = ExtractedOcrField(fields.packageMass, rawSnippet = fields.packageMass?.toPlainString() ?: "")
        )
    }

    private fun processSnippet(lines: List<String>): String = lines.firstOrNull { line ->
        PROCESS_LABELS.any { label -> line.contains(label, ignoreCase = true) }
    }.orEmpty()

    private data class BagFields(
        var origin: String? = null,
        var process: ProcessMethod = ProcessMethod.UNKNOWN,
        var altitude: Int? = null,
        var roastDate: LocalDate? = null,
        var packageMass: BigDecimal? = null
    ) {
        fun read(line: String) {
            val upper = line.uppercase()
            detectOrigin(upper)
            detectProcess(upper)
            detectAltitude(line)
            detectRoastDate(line)
            detectPackageMass(line)
        }

        private fun detectOrigin(upper: String) {
            if (origin == null) origin = countries.firstOrNull(upper::contains)
        }

        private fun detectProcess(upper: String) {
            process = detectedProcess(upper) ?: process
        }

        private fun detectAltitude(line: String) {
            altitudeRegex.find(line)?.let { altitude = it.groupValues[1].toIntOrNull() }
        }

        private fun detectRoastDate(line: String) {
            roastDateRegex.find(line)?.let { match ->
                runCatching { roastDate = LocalDate.parse(match.value.replace('/', '-')) }
            }
        }

        private fun detectPackageMass(line: String) {
            packageMassRegex.find(line)?.let { match ->
                val amount = match.groupValues[1].toBigDecimalOrNull() ?: return
                if (amount > BigDecimal.ZERO) {
                    val unit = match.groupValues[2].uppercase()
                    val grams = if (unit == "KG") amount.multiply(BigDecimal("1000")) else amount
                    packageMass = grams.setScale(2, RoundingMode.HALF_UP)
                }
            }
        }

        private fun detectedProcess(upper: String): ProcessMethod? = when {
            upper.contains("WASHED") -> ProcessMethod.WASHED
            upper.contains("NATURAL") -> ProcessMethod.NATURAL
            upper.contains("HONEY") -> ProcessMethod.HONEY
            upper.contains("ANAEROBIC") -> ProcessMethod.ANAEROBIC_NATURAL
            upper.contains("WET HULLED") || upper.contains("GILING BASAH") -> ProcessMethod.WET_HULLED
            else -> null
        }
    }

    private val PROCESS_LABELS = listOf("WASHED", "NATURAL", "HONEY", "ANAEROBIC", "WET HULLED", "GILING BASAH")
}

// ============================================================================
// S2-009: Bulk Import/Export & Conflict Resolution Contracts
// ============================================================================

enum class ConflictResolutionStrategy {
    OVERWRITE,
    KEEP_BOTH_RENAME,
    SKIP_DUPLICATE
}

data class CatalogExportPayload(
    val version: Int = 1,
    val exportedAtEpochMs: Long = System.currentTimeMillis(),
    val beans: List<DetailedBeanRecord> = emptyList(),
    val grinders: List<GrinderRecord> = emptyList(),
    val equipment: List<EquipmentItem> = emptyList(),
    val waters: List<WaterMineralProfile> = emptyList(),
    val ingredients: List<FoodGradeIngredientItem> = emptyList()
)

data class ImportDryRunSummary(
    val totalRecords: Int,
    val newRecordsCount: Int,
    val conflictingRecordsCount: Int,
    val conflicts: List<String>
)

data class ImportExecutionReceipt(
    val createdCount: Int,
    val updatedCount: Int,
    val renamedCount: Int,
    val skippedCount: Int,
    val errors: List<String> = emptyList()
)

// ============================================================================
// S2-011: Green Coffee Physical QC Models
// ============================================================================

enum class SampleLotStatus {
    OFFER_SAMPLE,
    PRE_SHIPMENT,
    LANDED_SAMPLE,
    PRODUCTION_LOT,
    ARCHIVED
}

data class GreenCoffeeLot(
    val id: String,
    val lotNumber: String,
    val originCountry: String,
    val producer: String,
    val harvestYear: String,
    val moisturePercent: BigDecimal,
    val bulkDensityGramsPerLiter: BigDecimal,
    val waterActivityAw: BigDecimal,
    val category1DefectCount: Int,
    val category2DefectCount: Int,
    // Screen sizes e.g. 15..19
    val screenDistributionPercent: Map<Int, BigDecimal> = emptyMap(),
    val sampleStatus: SampleLotStatus = SampleLotStatus.OFFER_SAMPLE,
    val notes: String = ""
) {
    val totalDefectPoints: BigDecimal get() =
        BigDecimal(category1DefectCount).multiply(BigDecimal.ONE)
            .add(BigDecimal(category2DefectCount).multiply(BigDecimal("0.20")))
            .setScale(2, RoundingMode.HALF_UP)

    val isSpecialtyGradeEligible: Boolean get() =
        category1DefectCount == 0 && totalDefectPoints <= BigDecimal("5.00") &&
            moisturePercent in BigDecimal("10.00")..BigDecimal("12.00") &&
            waterActivityAw < BigDecimal("0.65")
}

// ============================================================================
// S2-012: Roast Batch, RoR Curves & Blend Formulation Models
// ============================================================================

data class RoastDataPoint(
    val timeSeconds: Int,
    val beanTemperatureCelsius: BigDecimal,
    val rateOfRiseCelsiusPerMinute: BigDecimal? = null
)

data class BlendComponentSpec(
    val beanId: String,
    val componentPercentage: BigDecimal
)

data class RoastBatchRecord(
    val id: String,
    val batchNumber: String,
    val greenLotId: String? = null,
    val chargeTemperatureCelsius: BigDecimal,
    val dropTemperatureCelsius: BigDecimal,
    val totalRoastDurationSeconds: Int,
    val firstCrackTimeSeconds: Int,
    val developmentTimeRatioPercent: BigDecimal,
    val curvePoints: List<RoastDataPoint> = emptyList(),
    val blendComponents: List<BlendComponentSpec> = emptyList(),
    val roastedAtEpochMs: Long = System.currentTimeMillis()
) {
    init {
        require(firstCrackTimeSeconds in 1..<totalRoastDurationSeconds) {
            "First crack ($firstCrackTimeSeconds s) must precede drop time ($totalRoastDurationSeconds s)."
        }
        if (blendComponents.isNotEmpty()) {
            val total = blendComponents.fold(BigDecimal.ZERO) { acc, c -> acc.add(c.componentPercentage) }
            require(total.compareTo(BigDecimal("100.00")) == 0) {
                "Blend components must sum to exactly 100.00% (was $total%)."
            }
        }
    }

    companion object {
        fun calculateDtr(totalDurationSeconds: Int, firstCrackSeconds: Int): BigDecimal {
            require(firstCrackSeconds in 1..<totalDurationSeconds) { "Invalid roast timestamps." }
            val devTime = totalDurationSeconds - firstCrackSeconds
            return BigDecimal(devTime)
                .divide(BigDecimal(totalDurationSeconds), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal("100"))
                .setScale(2, RoundingMode.HALF_UP)
        }

        fun calculateRorSeries(rawPoints: List<Pair<Int, Double>>): List<RoastDataPoint> {
            val result = mutableListOf<RoastDataPoint>()
            for (i in rawPoints.indices) {
                val current = rawPoints[i]
                val tempBd = BigDecimal.valueOf(current.second).setScale(2, RoundingMode.HALF_UP)
                val ror = if (i == 0) {
                    null
                } else {
                    val prev = rawPoints[i - 1]
                    val dt = current.first - prev.first
                    if (dt <= 0) {
                        null
                    } else {
                        val dTemp = current.second - prev.second
                        val rorVal = (dTemp / dt.toDouble()) * 60.0
                        BigDecimal.valueOf(rorVal).setScale(2, RoundingMode.HALF_UP)
                    }
                }
                result.add(RoastDataPoint(current.first, tempBd, ror))
            }
            return result
        }
    }
}
