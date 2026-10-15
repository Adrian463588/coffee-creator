package com.coffeecreator.app.core.catalog

import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate

// ============================================================================
// S2-001 & S2-002: Bean & Bag Inventory Domain Models
// ============================================================================

enum class RoastLevel {
    CINNAMON, LIGHT, MEDIUM_LIGHT, MEDIUM, MEDIUM_DARK, DARK, VERY_DARK, UNKNOWN
}

enum class ProcessMethod {
    WASHED, NATURAL, HONEY, ANAEROBIC_NATURAL, ANAEROBIC_WASHED,
    CARBONIC_MACERATION, WET_HULLED, EXPERIMENTAL, UNKNOWN
}

enum class BagStatus {
    PURCHASED, ACTIVE, FINISHED, FROZEN
}

data class FreezeEvent(
    val frozenAtEpochMs: Long,
    val thawedAtEpochMs: Long? = null
) {
    val isOngoing: Boolean get() = thawedAtEpochMs == null

    fun durationDays(currentEpochMs: Long = System.currentTimeMillis()): Long {
        val end = thawedAtEpochMs ?: currentEpochMs
        val elapsedMs = maxOf(0L, end - frozenAtEpochMs)
        return elapsedMs / (1000L * 60L * 60L * 24L)
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
    val roastDate: LocalDate,
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

        val (peakStart, peakEnd, staleCutoff) = when (roastLevel) {
            RoastLevel.LIGHT, RoastLevel.CINNAMON -> {
                if (process == ProcessMethod.NATURAL || process == ProcessMethod.ANAEROBIC_NATURAL) {
                    Triple(11, 35, 75)
                } else {
                    Triple(8, 28, 60)
                }
            }
            RoastLevel.MEDIUM_LIGHT -> Triple(7, 24, 50)
            RoastLevel.MEDIUM -> Triple(6, 21, 45)
            RoastLevel.MEDIUM_DARK -> Triple(5, 16, 35)
            RoastLevel.DARK, RoastLevel.VERY_DARK -> Triple(4, 12, 28)
            RoastLevel.UNKNOWN -> Triple(7, 21, 45)
        }

        val state = when {
            effectiveRestDays < peakStart -> FreshnessState.TOO_FRESH_DEGASSING
            effectiveRestDays <= peakEnd -> FreshnessState.PEAK_FLAVOR
            effectiveRestDays <= staleCutoff -> FreshnessState.STABLE_MATURE
            else -> FreshnessState.STALE_OXIDIZED
        }

        val rationale = when (state) {
            FreshnessState.TOO_FRESH_DEGASSING ->
                "Day $effectiveRestDays: Rapid CO2 desorption underway. High risk of extraction channeling and sour, unstable crema."
            FreshnessState.PEAK_FLAVOR ->
                "Day $effectiveRestDays: Optimal aromatic expression (peak window: days $peakStart–$peakEnd). Balanced solubility and sweetness."
            FreshnessState.STABLE_MATURE ->
                "Day $effectiveRestDays: Degassing complete; gradual volatile loss. Consistent extraction, slightly muted florals."
            FreshnessState.STALE_OXIDIZED ->
                "Day $effectiveRestDays: Exceeded day $staleCutoff threshold. Oxidation of delicate lipids produces papery, woody, flat flavor."
        }

        return FreshnessAnalysis(
            effectiveRestDays = effectiveRestDays,
            totalFrozenDays = totalFrozenDays,
            state = state,
            peakStartDay = peakStart,
            peakEndDay = peakEnd,
            staleCutoffDay = staleCutoff,
            heuristicRationale = rationale
        )
    }
}

// ============================================================================
// S2-004 & S2-005: Grinder Records & Equipment Catalog
// ============================================================================

enum class CalibrationUnit {
    CLICKS, MICRONS, STEPLESS_DEGREES, NUMERICAL_MARKS
}

enum class BurrType {
    CONICAL, FLAT, GHOST_TOOTH, HYBRID
}

enum class BurrMaterial {
    STAINLESS_STEEL, TITANIUM_COATED, DLC_COATED, CERAMIC, CARBON_STEEL
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
    DRIPPER, FILTER_PAPER, KETTLE, SCALE, ESPRESSO_MACHINE,
    PORTAFILTER_BASKET, PUCK_PREP_TOOL, OTHER
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
    val calciumPpm: BigDecimal,       // mg/L Ca2+
    val magnesiumPpm: BigDecimal,     // mg/L Mg2+
    val sodiumPpm: BigDecimal,        // mg/L Na+
    val potassiumPpm: BigDecimal = BigDecimal.ZERO,
    val bicarbonatePpm: BigDecimal,   // mg/L HCO3-
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
    val confidence: Double,
    val rawSnippet: String = ""
)

data class BagOcrParsedDraft(
    val photoUri: String,
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
    fun parseBagText(photoUri: String, text: String): BagOcrParsedDraft {
        val lines = text.lines().map { it.trim() }.filter { it.isNotBlank() }
        
        var roaster: String? = null
        var beanName: String? = null
        var origin: String? = null
        var process: ProcessMethod = ProcessMethod.UNKNOWN
        var varietal: String? = null
        var altitude: Int? = null
        var roastDate: LocalDate? = null
        var packageMass: BigDecimal? = null

        val countries = listOf(
            "ETHIOPIA", "COLOMBIA", "KENYA", "GUATEMALA", "PANAMA", "INDONESIA",
            "COSTA RICA", "BRAZIL", "RWANDA", "BURUNDI", "YEMEN", "HONDURAS"
        )

        for (line in lines) {
            val upper = line.uppercase()
            // Origin detection
            countries.find { upper.contains(it) }?.let { matched ->
                if (origin == null) origin = matched
            }

            // Process detection
            when {
                upper.contains("WASHED") -> process = ProcessMethod.WASHED
                upper.contains("NATURAL") -> process = ProcessMethod.NATURAL
                upper.contains("HONEY") -> process = ProcessMethod.HONEY
                upper.contains("ANAEROBIC") -> process = ProcessMethod.ANAEROBIC_NATURAL
                upper.contains("WET HULLED") || upper.contains("GILING BASAH") -> process = ProcessMethod.WET_HULLED
            }

            // Altitude detection (e.g. 1800m, 1800 masl, 1800-2000m)
            val altRegex = Regex("(\\d{3,4})\\s*(?:-|–)?\\s*(\\d{3,4})?\\s*(?:M|MASL|MTR)", RegexOption.IGNORE_CASE)
            altRegex.find(line)?.let { match ->
                altitude = match.groupValues[1].toIntOrNull()
            }

            // Roast date detection (YYYY-MM-DD or DD/MM/YYYY)
            val isoDateRegex = Regex("(\\d{4})[-/](\\d{2})[-/](\\d{2})")
            isoDateRegex.find(line)?.let { match ->
                runCatching {
                    roastDate = LocalDate.parse(match.value.replace('/', '-'))
                }
            }

            // Package mass (e.g., 250g, 250 g, 500g, 1kg)
            val weightRegex = Regex("(\\d+(?:\\.\\d+)?)\\s*(G|KG)", RegexOption.IGNORE_CASE)
            weightRegex.find(line)?.let { match ->
                val num = match.groupValues[1].toDoubleOrNull() ?: 250.0
                val unit = match.groupValues[2].uppercase()
                val massG = if (unit == "KG") num * 1000.0 else num
                packageMass = BigDecimal.valueOf(massG).setScale(2, RoundingMode.HALF_UP)
            }
        }

        // Heuristic fallback for roaster and name
        if (lines.isNotEmpty()) {
            roaster = lines.firstOrNull { !it.contains("ROAST", ignoreCase = true) && !it.contains("WEIGHT", ignoreCase = true) }
            beanName = lines.drop(1).firstOrNull { it != origin && !it.contains("PROCESS", ignoreCase = true) }
        }

        return BagOcrParsedDraft(
            photoUri = photoUri,
            rawExtractedText = text,
            roaster = ExtractedOcrField(roaster, if (roaster != null) 0.90 else 0.0, roaster ?: ""),
            beanName = ExtractedOcrField(beanName, if (beanName != null) 0.85 else 0.0, beanName ?: ""),
            origin = ExtractedOcrField(origin, if (origin != null) 0.95 else 0.0, origin ?: ""),
            process = ExtractedOcrField(process, if (process != ProcessMethod.UNKNOWN) 0.95 else 0.0, process.name),
            varietal = ExtractedOcrField(varietal, if (varietal != null) 0.80 else 0.0, varietal ?: ""),
            altitudeMeters = ExtractedOcrField(altitude, if (altitude != null) 0.90 else 0.0, altitude?.toString() ?: ""),
            roastDate = ExtractedOcrField(roastDate, if (roastDate != null) 0.95 else 0.0, roastDate?.toString() ?: ""),
            packageMassGrams = ExtractedOcrField(packageMass ?: BigDecimal("250.00"), 0.90, packageMass?.toString() ?: "250")
        )
    }
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
    OFFER_SAMPLE, PRE_SHIPMENT, LANDED_SAMPLE, PRODUCTION_LOT, ARCHIVED
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
    val screenDistributionPercent: Map<Int, BigDecimal> = emptyMap(), // Screen sizes e.g. 15..19
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
                val ror = if (i == 0) null else {
                    val prev = rawPoints[i - 1]
                    val dt = current.first - prev.first
                    if (dt <= 0) null else {
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
