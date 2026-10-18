package com.coffeecreator.app.core.sensory.greengrading

import java.security.MessageDigest
import kotlin.math.abs

private const val DEFECT_EQUIVALENCE_FLOAT_TOLERANCE = 1e-9
private const val HISTORICAL_GRADE_3_MAX_DEFECT_EQUIVALENTS = 23.0
private const val HISTORICAL_GRADE_4_MAX_DEFECT_EQUIVALENTS = 86.0

enum class GreenCoffeeSpecies(val label: String) {
    COFFEA_ARABICA("Coffea Arabica"),
    COFFEA_CANEPHORA("Coffea Canephora (Robusta)"),
    COFFEA_LIBERICA("Coffea Liberica"),
    COFFEA_EUGENIOIDES("Coffea Eugenioides")
}

enum class GreenProcessingMethod(val label: String) {
    WASHED("Fully Washed"),
    NATURAL("Natural / Dry Processed"),
    HONEY("Honey / Pulped Natural"),
    WET_HULLED("Wet Hulled (Giling Basah)"),
    EXPERIMENTAL_FERMENTATION("Experimental / Anaerobic")
}

enum class DefectTier(val labelEn: String, val labelId: String) {
    CATEGORY_1_PRIMARY("Category 1: Primary Full Defects", "Kategori 1: Cacat Utama Penuh"),
    CATEGORY_2_SECONDARY("Category 2: Secondary Defects", "Kategori 2: Cacat Sekunder")
}

enum class GreenDefectType(
    val tier: DefectTier,
    // e.g. 5 beans = 1 full defect
    val standardEquivalentRatio: Int,
    val nameEn: String,
    val nameId: String,
    val isPhysicalHazard: Boolean = false
) {
    // Category 1: Full Defects (1 bean = 1 full defect, except Severe Insect = 5:1)
    FULL_BLACK(DefectTier.CATEGORY_1_PRIMARY, 1, "Full Black", "Biji Hitam Penuh"),
    FULL_SOUR(DefectTier.CATEGORY_1_PRIMARY, 1, "Full Sour", "Biji Asam Penuh"),
    DRIED_CHERRY_POD(DefectTier.CATEGORY_1_PRIMARY, 1, "Dried Cherry / Pod", "Gelondong Kering"),
    FUNGUS_DAMAGED(DefectTier.CATEGORY_1_PRIMARY, 1, "Fungus Damaged", "Terserang Jamur"),
    FOREIGN_MATTER(DefectTier.CATEGORY_1_PRIMARY, 1, "Foreign Matter (Stone/Stick)", "Benda Asing", isPhysicalHazard = true),
    SEVERE_INSECT_DAMAGE(DefectTier.CATEGORY_1_PRIMARY, 5, "Severe Insect Damage (>=3 holes)", "Serangga Parah (>=3 Lubang)"),

    // Category 2: Secondary Defects
    PARTIAL_BLACK(DefectTier.CATEGORY_2_SECONDARY, 3, "Partial Black", "Hitam Sebagian"),
    PARTIAL_SOUR(DefectTier.CATEGORY_2_SECONDARY, 3, "Partial Sour", "Asam Sebagian"),
    PARCHMENT(DefectTier.CATEGORY_2_SECONDARY, 5, "Parchment Bean", "Biji Bertanduk"),
    FLOATER(DefectTier.CATEGORY_2_SECONDARY, 5, "Floater Bean", "Biji Melayang"),
    IMMATURE_UNRIPE(DefectTier.CATEGORY_2_SECONDARY, 5, "Immature / Unripe", "Biji Muda / Kerut"),
    WITHERED(DefectTier.CATEGORY_2_SECONDARY, 5, "Withered Bean", "Biji Keriput"),
    SHELL(DefectTier.CATEGORY_2_SECONDARY, 5, "Shell / Elephant Ear", "Biji Kerang / Telinga"),
    BROKEN_CHIPPED_CUT(DefectTier.CATEGORY_2_SECONDARY, 5, "Broken / Chipped / Cut", "Biji Pecah / Terpotong"),
    HULL_HUSK(DefectTier.CATEGORY_2_SECONDARY, 5, "Hull / Husk Piece", "Kulit Kering"),
    SLIGHT_INSECT_DAMAGE(DefectTier.CATEGORY_2_SECONDARY, 10, "Slight Insect Damage (<3 holes)", "Serangga Ringan (<3 Lubang)")
}

data class DefectTallyRecord(
    val defectType: GreenDefectType,
    val rawCount: Int,
    val standardSupported: Boolean = true,
    val equivalentFullDefects: Double = if (standardSupported) rawCount.toDouble() / defectType.standardEquivalentRatio else 0.0,
    val evidencePhotoUris: List<String> = emptyList()
) {
    init {
        require(rawCount >= 0) { "Defect count cannot be negative" }
        require(equivalentFullDefects.isFinite() && equivalentFullDefects >= 0.0) {
            "Defect equivalence must be a finite non-negative value"
        }
        val expectedEquivalence = rawCount.toDouble() / defectType.standardEquivalentRatio
        require(
            if (standardSupported) {
                abs(equivalentFullDefects - expectedEquivalence) <= DEFECT_EQUIVALENCE_FLOAT_TOLERANCE
            } else {
                equivalentFullDefects == 0.0
            }
        ) {
            "Defect equivalence must match the raw count and selected standard support"
        }
    }
}

enum class ScreenSieveNumber(val holeSize64thInch: Int, val holeSizeMm: Double) {
    SCREEN_20(20, 7.94),
    SCREEN_19(19, 7.54),
    SCREEN_18(18, 7.14),
    SCREEN_17(17, 6.75),
    SCREEN_16(16, 6.35),
    SCREEN_15(15, 5.95),
    SCREEN_14(14, 5.56),
    SCREEN_13(13, 5.16),
    SCREEN_12(12, 4.76),
    BELOW_12(0, 0.0)
}

data class SieveFraction(
    val sieve: ScreenSieveNumber,
    val massGrams: Double,
    val percentageOfTotal: Double
)

data class SieveAnalysisResult(
    val sampleMassGrams: Double,
    val fractions: List<SieveFraction>,
    val totalRecoveredMassGrams: Double,
    val massBalanceMismatchGrams: Double,
    // Mismatch <= 1.5g per ISO 4150
    val isMassBalanceValid: Boolean,
    val cumulativeRetentionScreen16Plus: Double,
    val shakerDurationSeconds: Int = 180,
    val methodologyNote: String = "ISO 4150:2010 Green coffee — Size analysis — Manual and mechanical sieving"
)

enum class MoistureMeasurementMethod(val label: String) {
    DIELECTRIC_CAPACITANCE("Dielectric Capacitance"),
    LOSS_ON_DRYING("Loss on Drying (ISO 6673 Ref Oven)"),
    NEAR_INFRARED("Near-Infrared (NIR)")
}

data class MoistureAssessment(
    val moisturePercent: Double?,
    val uncertaintyPercent: Double? = null,
    val meterDeviceModel: String? = null,
    val meterSerialNumber: String? = null,
    val calibrationDateEpochMs: Long? = null,
    val sampleTemperatureCelsius: Double? = null,
    val measurementMethod: MoistureMeasurementMethod? = null,
    val isMeasured: Boolean = moisturePercent != null
) {
    init {
        require(moisturePercent == null || (moisturePercent.isFinite() && moisturePercent in 0.0..100.0)) {
            "Moisture must be a finite percentage from 0 to 100"
        }
        require(uncertaintyPercent == null || (uncertaintyPercent.isFinite() && uncertaintyPercent >= 0.0)) {
            "Measurement uncertainty must be finite and non-negative"
        }
        require(sampleTemperatureCelsius == null || sampleTemperatureCelsius.isFinite()) { "Sample temperature must be finite" }
        require(calibrationDateEpochMs == null || calibrationDateEpochMs >= 0L) { "Calibration date must be a valid epoch timestamp" }
        require(isMeasured == (moisturePercent != null)) { "Moisture value and measurement status must agree" }
    }
}

enum class GreenBeanColor(val label: String) {
    BLUISH_GREEN("Bluish-green (Fresh high-grown)"),
    GREEN("Green (Standard washed)"),
    GREENISH("Greenish"),
    PALE_YELLOW("Pale yellow (Fading / Aging)"),
    YELLOWISH("Yellowish"),
    BROWNISH("Brownish (Past crop / Damaged)")
}

enum class GreenBeanOdor(val label: String, val isOrganolepticallyClean: Boolean) {
    CLEAN_FRESH_VEGETAL("Clean, fresh, vegetal", true),
    EARTHY("Earthy", true),
    MUSTY("Musty / Moldy (Contaminated)", false),
    FERMENTED_SOUR("Fermented / Sour (Over-fermented)", false),
    CHEMICAL_FOREIGN("Chemical / Foreign taints", false)
}

data class WaterActivityAssessment(
    val waterActivityAw: Double?,
    val measurementTemperatureCelsius: Double? = null,
    val meterModel: String? = null,
    val calibrationDateEpochMs: Long? = null,
    val isMeasured: Boolean = waterActivityAw != null
) {
    init {
        require(waterActivityAw == null || (waterActivityAw.isFinite() && waterActivityAw in 0.0..1.0)) {
            "Water activity must be a finite value from 0 to 1"
        }
        require(measurementTemperatureCelsius == null || measurementTemperatureCelsius.isFinite()) {
            "Measurement temperature must be finite"
        }
        require(isMeasured == (waterActivityAw != null)) { "Water activity value and measurement status must agree" }
    }
}

data class BulkDensityAssessment(
    val bulkDensityGramsPerLiter: Double?,
    val containerVolumeLiters: Double = 1.0,
    val testSampleMassGrams: Double? = null,
    val isMeasured: Boolean = bulkDensityGramsPerLiter != null
) {
    init {
        require(bulkDensityGramsPerLiter == null || (bulkDensityGramsPerLiter.isFinite() && bulkDensityGramsPerLiter > 0.0)) {
            "Bulk density must be finite and greater than zero"
        }
        require(containerVolumeLiters.isFinite() && containerVolumeLiters > 0.0) { "Container volume must be finite and greater than zero" }
        require(testSampleMassGrams == null || (testSampleMassGrams.isFinite() && testSampleMassGrams > 0.0)) {
            "Test sample mass must be finite and greater than zero"
        }
        require(isMeasured == (bulkDensityGramsPerLiter != null)) { "Bulk density value and measurement status must agree" }
    }
}

data class ChainOfCustody(
    val exporter: String? = null,
    val importer: String? = null,
    val warehouseLocation: String? = null,
    val transportContainerId: String? = null,
    val harvestCropYear: String? = null,
    val ambientStorageTempCelsius: Double? = null,
    val ambientStorageRelativeHumidityPercent: Double? = null
)

data class GradingStandardRef(
    val id: String,
    val name: String,
    val version: String,
    val sourceCitation: String,
    val supportedSpecies: List<GreenCoffeeSpecies>,
    val supportedProcesses: List<GreenProcessingMethod>
) {
    companion object {
        val SCA_WASHED_ARABICA_2004 = GradingStandardRef(
            id = "SCA_WASHED_ARABICA_2004",
            name = "Historical 2004 Washed Arabica Reference (unverified)",
            version = "2004.1",
            sourceCitation = "Historical reference; source rights and current applicability not verified",
            supportedSpecies = listOf(GreenCoffeeSpecies.COFFEA_ARABICA),
            supportedProcesses = listOf(GreenProcessingMethod.WASHED)
        )
    }
}

enum class MeasurementStatus {
    MEASURED,
    NOT_MEASURED,
    UNKNOWN,
    OUT_OF_SCOPE,
    PENDING_REVIEW
}

data class GreenLotSample(
    val lotId: String,
    val lotName: String,
    val species: GreenCoffeeSpecies?,
    val processMethod: GreenProcessingMethod?,
    val originCountry: String = "",
    val originRegion: String = "",
    val producer: String = "",
    val harvestCropYear: String = "",
    val sampleMassGrams: Double = 0.0,
    val defectInspectionComplete: Boolean = false,
    val chainOfCustody: ChainOfCustody? = null,
    val defects: List<DefectTallyRecord> = emptyList(),
    val sieveDistribution: List<SieveFraction> = emptyList(),
    val moisture: MoistureAssessment = MoistureAssessment(null),
    val waterActivity: WaterActivityAssessment = WaterActivityAssessment(null),
    val bulkDensity: BulkDensityAssessment = BulkDensityAssessment(null),
    val color: GreenBeanColor? = null,
    val odor: GreenBeanOdor? = null,
    val foreignMatterObservation: String? = null,
    val standardRef: GradingStandardRef = GradingStandardRef.SCA_WASHED_ARABICA_2004
)

enum class PhysicalGradeClassification(val label: String) {
    SPECIALTY_GRADE_1("Historical reference: Grade 1"),
    PREMIUM_GRADE_2("Historical reference: Grade 2"),
    EXCHANGE_GRADE_3("Historical reference: Grade 3"),
    BELOW_STANDARD_GRADE_4("Historical reference: Grade 4"),
    OFF_GRADE_5("Historical reference: Grade 5"),
    UNDETERMINED_OUT_OF_SCOPE("Undetermined / Out of Scope")
}

data class PhysicalGradingEvaluation(
    // e.g. "SCA Washed Arabica 2004 Reference"
    val standardApplied: String,
    val sampleMassGrams: Double,
    val totalCategory1Equivalents: Double,
    val totalCategory2Equivalents: Double,
    val totalFullDefectsEquivalents: Double,
    val grade: PhysicalGradeClassification,
    val isSpecialtyPhysicalEligible: Boolean,
    val moistureWithinStandardRange: Boolean,
    val odorClean: Boolean,
    val evaluationNotes: List<String>,
    val disclaimer: String = "Educational comparison to a historical reference only. " +
        "Source rights and current applicability are unverified; this is not an official grade or certification."
)

data class DualSpecialtyEvaluation(
    val physicalGrade: PhysicalGradeClassification,
    val isPhysicalSpecialtyEligible: Boolean,
    val sensoryCvaAffectiveScore: Double?,
    val isSensorySpecialtyEligible: Boolean = false,
    val meetsCombinedConfiguredThresholds: Boolean = false,
    val guidanceMessage: String
)

data class BuyerAcceptanceLimits(
    val contractName: String,
    val sourceReference: String,
    val maxCategory1FullDefects: Int,
    val maxTotalEquivalentDefects: Double,
    val minMoisturePercent: Double,
    val maxMoisturePercent: Double,
    val maxWaterActivityAw: Double,
    val minScreen16PlusPercent: Double,
    val minBulkDensityGramsPerLiter: Double
) {
    init {
        require(contractName.isNotBlank() && sourceReference.isNotBlank()) { "Contract name and source are required" }
        require(maxCategory1FullDefects >= 0) { "Category 1 limit cannot be negative" }
        require(maxTotalEquivalentDefects.isFinite() && maxTotalEquivalentDefects >= 0.0) { "Defect limit must be finite and non-negative" }
        require(
            minMoisturePercent.isFinite() &&
                maxMoisturePercent.isFinite() &&
                minMoisturePercent in 0.0..maxMoisturePercent &&
                maxMoisturePercent <= 100.0
        ) {
            "Moisture limits must be finite and within 0-100%"
        }
        require(maxWaterActivityAw.isFinite() && maxWaterActivityAw in 0.0..1.0) { "Water activity limit must be within 0-1" }
        require(minScreen16PlusPercent.isFinite() && minScreen16PlusPercent in 0.0..100.0) { "Screen retention must be within 0-100%" }
        require(minBulkDensityGramsPerLiter.isFinite() && minBulkDensityGramsPerLiter > 0.0) { "Density limit must be positive" }
    }
}

data class BuyerContractEvaluationResult(
    val isAccepted: Boolean,
    val parameterEvaluations: Map<String, Boolean>,
    val failureReasons: List<String>,
    val notes: List<String>
)

data class GreenQCAuditRecord(
    val assessorName: String? = null,
    val assessorId: String? = null,
    val assessorCertification: String? = null,
    val evaluationTimestampEpochMs: Long,
    val evidencePhotoUris: List<String> = emptyList(),
    val standardVersion: String = "Historical 2004 reference (unverified)",
    val equipmentCalibrationSummary: String? = null,
    val sha256AuditSeal: String? = null
)

object StandardGreenGradingEngine {

    fun evaluateWashedArabicaGrade(sample: GreenLotSample): PhysicalGradingEvaluation {
        val notes = mutableListOf<String>()

        // Invariant guard: species and method validation
        if (sample.species != GreenCoffeeSpecies.COFFEA_ARABICA || sample.processMethod != GreenProcessingMethod.WASHED) {
            return PhysicalGradingEvaluation(
                standardApplied = sample.standardRef.name,
                sampleMassGrams = sample.sampleMassGrams,
                totalCategory1Equivalents = 0.0,
                totalCategory2Equivalents = 0.0,
                totalFullDefectsEquivalents = 0.0,
                grade = PhysicalGradeClassification.UNDETERMINED_OUT_OF_SCOPE,
                isSpecialtyPhysicalEligible = false,
                moistureWithinStandardRange = false,
                odorClean = sample.odor?.isOrganolepticallyClean == true,
                evaluationNotes = listOf(
                    "This reference applies only to explicitly selected Coffea Arabica and " +
                        "washed process. Current selection is ${sample.species?.label ?: "UNKNOWN"} / " +
                        "${sample.processMethod?.label ?: "UNKNOWN"}."
                )
            )
        }

        var cat1Sum = 0.0
        var cat2Sum = 0.0

        sample.defects.filter(DefectTallyRecord::standardSupported).forEach { record ->
            if (record.defectType.tier == DefectTier.CATEGORY_1_PRIMARY) {
                cat1Sum += record.equivalentFullDefects
            } else {
                cat2Sum += record.equivalentFullDefects
            }
        }
        val totalDefects = cat1Sum + cat2Sum
        val unsupportedDefectTally = sample.defects.any { !it.standardSupported && it.rawCount > 0 }
        if (unsupportedDefectTally) {
            notes.add("OUT_OF_SCOPE: unsupported defect evidence cannot be converted to this historical reference's equivalence rules.")
            return PhysicalGradingEvaluation(
                standardApplied = sample.standardRef.name,
                sampleMassGrams = sample.sampleMassGrams,
                totalCategory1Equivalents = cat1Sum,
                totalCategory2Equivalents = cat2Sum,
                totalFullDefectsEquivalents = totalDefects,
                grade = PhysicalGradeClassification.UNDETERMINED_OUT_OF_SCOPE,
                isSpecialtyPhysicalEligible = false,
                moistureWithinStandardRange = sample.moisture.isMeasured &&
                    sample.moisture.moisturePercent?.let { it in 9.0..13.0 } == true,
                odorClean = sample.odor?.isOrganolepticallyClean == true,
                evaluationNotes = notes
            )
        }

        // Odor check: organoleptically clean is mandatory for specialty
        val odor = sample.odor
        val odorClean = odor?.isOrganolepticallyClean == true
        if (odor == null) {
            notes.add("Odor inspection is PENDING_REVIEW.")
        } else if (!odorClean) {
            notes.add(
                "ODOR_CONTAMINATION_DISQUALIFIED: Sample has unacceptable odor '${odor.label}'. " +
                    "Immediately disqualified from Specialty Grade."
            )
        }

        // Moisture check: Standard Arabica Washed is 9.0% to 13.0% (ideally 10-12%)
        val moisture = sample.moisture.moisturePercent
        val moistureValid = sample.moisture.isMeasured && moisture != null && moisture in 9.0..13.0
        if (moisture == null) {
            notes.add("Moisture was NOT_MEASURED. Moisture verification is required for definitive grade classification.")
        } else if (!moistureValid) {
            notes.add("Moisture level ($moisture%) out of standard range (9.0% - 13.0%).")
        }

        val missingRequirements = buildList {
            if (!sample.sampleMassGrams.isFinite() || sample.sampleMassGrams != 350.0) add("350 g reference sample mass")
            if (!sample.defectInspectionComplete) add("defect inspection")
            if (!sample.moisture.isMeasured || moisture == null) add("moisture measurement")
            if (odor == null) add("odor inspection")
        }
        if (missingRequirements.isNotEmpty()) {
            notes.add("Definitive classification withheld; pending: ${missingRequirements.joinToString()}.")
            return PhysicalGradingEvaluation(
                standardApplied = sample.standardRef.name,
                sampleMassGrams = sample.sampleMassGrams,
                totalCategory1Equivalents = cat1Sum,
                totalCategory2Equivalents = cat2Sum,
                totalFullDefectsEquivalents = totalDefects,
                grade = PhysicalGradeClassification.UNDETERMINED_OUT_OF_SCOPE,
                isSpecialtyPhysicalEligible = false,
                moistureWithinStandardRange = moisture != null && moisture in 9.0..13.0,
                odorClean = odorClean,
                evaluationNotes = notes
            )
        }

        // Classification per 350g washed Arabica:
        // Historical reference bands only; rights and current applicability are not verified.
        val grade: PhysicalGradeClassification
        val isSpecialtyEligible: Boolean

        if (cat1Sum == 0.0 && cat2Sum <= 5.0 && moistureValid && odorClean) {
            grade = PhysicalGradeClassification.SPECIALTY_GRADE_1
            isSpecialtyEligible = true
            notes.add("Meets the historical reference's Grade 1 thresholds; this is not an official grade or certification.")
        } else if (totalDefects <= 8.0 && odorClean) {
            grade = PhysicalGradeClassification.PREMIUM_GRADE_2
            isSpecialtyEligible = false
            if (cat1Sum > 0.0) {
                notes.add("Category 1 defects present ($cat1Sum); the historical Grade 1 threshold is not met.")
            } else {
                notes.add("Historical reference band: Grade 2 (<= 8 full defect equivalents); not an official grade.")
            }
        } else if (totalDefects <= HISTORICAL_GRADE_3_MAX_DEFECT_EQUIVALENTS && odorClean) {
            grade = PhysicalGradeClassification.EXCHANGE_GRADE_3
            isSpecialtyEligible = false
            notes.add("Historical reference band: Grade 3 (9-23 full defect equivalents); not an official grade.")
        } else if (totalDefects <= HISTORICAL_GRADE_4_MAX_DEFECT_EQUIVALENTS && odorClean) {
            grade = PhysicalGradeClassification.BELOW_STANDARD_GRADE_4
            isSpecialtyEligible = false
            notes.add("Historical reference band: Grade 4 (24-86 full defect equivalents); not an official grade.")
        } else {
            grade = PhysicalGradeClassification.OFF_GRADE_5
            isSpecialtyEligible = false
            notes.add("Historical reference band: Grade 5 (> 86 full defect equivalents or severe contamination); not an official grade.")
        }

        return PhysicalGradingEvaluation(
            standardApplied = sample.standardRef.name,
            sampleMassGrams = sample.sampleMassGrams,
            totalCategory1Equivalents = cat1Sum,
            totalCategory2Equivalents = cat2Sum,
            totalFullDefectsEquivalents = totalDefects,
            grade = grade,
            isSpecialtyPhysicalEligible = isSpecialtyEligible,
            moistureWithinStandardRange = moistureValid,
            odorClean = odorClean,
            evaluationNotes = notes
        )
    }

    fun calculateSieveAnalysis(
        sampleMassGrams: Double,
        sieveMasses: Map<ScreenSieveNumber, Double>,
        shakerDurationSeconds: Int = 180
    ): SieveAnalysisResult {
        require(sampleMassGrams.isFinite() && sampleMassGrams > 0.0) { "Sample mass must be finite and greater than zero" }
        require(sieveMasses.values.all { it.isFinite() && it >= 0.0 }) { "Sieve masses must be finite and non-negative" }
        require(shakerDurationSeconds > 0) { "Shaker duration must be positive" }
        val recoveredMass = sieveMasses.values.sum()
        val mismatch = abs(recoveredMass - sampleMassGrams)
        val fractions = sieveMasses.map { (sieve, mass) ->
            SieveFraction(
                sieve = sieve,
                massGrams = mass,
                percentageOfTotal = if (recoveredMass > 0.0) (mass / recoveredMass) * 100.0 else 0.0
            )
        }.sortedByDescending { it.sieve.holeSize64thInch }

        val screen16PlusRetention = fractions
            .filter { it.sieve.holeSize64thInch >= 16 }
            .sumOf { it.percentageOfTotal }

        return SieveAnalysisResult(
            sampleMassGrams = sampleMassGrams,
            fractions = fractions,
            totalRecoveredMassGrams = recoveredMass,
            massBalanceMismatchGrams = mismatch,
            isMassBalanceValid = mismatch <= 1.5,
            cumulativeRetentionScreen16Plus = screen16PlusRetention,
            shakerDurationSeconds = shakerDurationSeconds
        )
    }

    fun calculateSieveFractions(sampleMassGrams: Double, sieveMasses: Map<ScreenSieveNumber, Double>): List<SieveFraction> {
        return calculateSieveAnalysis(sampleMassGrams, sieveMasses).fractions
    }

    fun calculateBulkDensity(massGrams: Double, volumeLiters: Double = 1.0): BulkDensityAssessment {
        require(massGrams.isFinite() && massGrams > 0.0) { "Measured mass must be finite and greater than zero" }
        require(volumeLiters.isFinite() && volumeLiters > 0.0) { "Measured volume must be finite and greater than zero" }
        val densityGramsPerLiter = massGrams / volumeLiters
        return BulkDensityAssessment(
            bulkDensityGramsPerLiter = densityGramsPerLiter,
            containerVolumeLiters = volumeLiters,
            testSampleMassGrams = massGrams,
            isMeasured = true
        )
    }

    fun evaluateDualSpecialty(physicalEvaluation: PhysicalGradingEvaluation, sensoryCvaScore: Double?): DualSpecialtyEvaluation {
        val validSensoryScore = sensoryCvaScore?.takeIf { it.isFinite() && it in 0.0..100.0 }
        val guidance = "Physical reference grading and sensory observations remain separate. " +
            "No approved protocol mapping is configured for a combined status."

        return DualSpecialtyEvaluation(
            physicalGrade = physicalEvaluation.grade,
            isPhysicalSpecialtyEligible = physicalEvaluation.isSpecialtyPhysicalEligible,
            sensoryCvaAffectiveScore = validSensoryScore,
            isSensorySpecialtyEligible = false,
            meetsCombinedConfiguredThresholds = false,
            guidanceMessage = guidance
        )
    }

    fun evaluateBuyerContract(
        sample: GreenLotSample,
        limits: BuyerAcceptanceLimits,
        sieveAnalysis: SieveAnalysisResult? = null
    ): BuyerContractEvaluationResult {
        val evaluations = mutableMapOf<String, Boolean>()
        val failures = mutableListOf<String>()
        val notes = mutableListOf<String>()

        var cat1Count = 0.0
        var cat2Equiv = 0.0
        val unsupportedDefectTally = sample.defects.any { !it.standardSupported && it.rawCount > 0 }
        sample.defects.filter(DefectTallyRecord::standardSupported).forEach { record ->
            if (record.defectType.tier == DefectTier.CATEGORY_1_PRIMARY) {
                cat1Count += record.equivalentFullDefects
            } else {
                cat2Equiv += record.equivalentFullDefects
            }
        }
        val totalDefects = cat1Count + cat2Equiv
        if (unsupportedDefectTally) {
            failures.add("Unsupported defect evidence is OUT_OF_SCOPE for this contract's equivalence rules")
        }
        if (!sample.defectInspectionComplete) failures.add("Defect inspection is NOT_MEASURED")
        if (!sample.sampleMassGrams.isFinite() || sample.sampleMassGrams <= 0.0) failures.add("Sample mass is NOT_MEASURED")

        // Cat 1 Defect check
        val cat1Pass = !unsupportedDefectTally && cat1Count <= limits.maxCategory1FullDefects
        evaluations["Category 1 Defects"] = cat1Pass
        if (!cat1Pass) failures.add("Category 1 defects ($cat1Count) exceed contract limit (${limits.maxCategory1FullDefects})")

        // Total Defect check
        val totalPass = !unsupportedDefectTally && totalDefects <= limits.maxTotalEquivalentDefects
        evaluations["Total Full Defect Equivalents"] = totalPass
        if (!totalPass) failures.add("Total defects ($totalDefects) exceed contract limit (${limits.maxTotalEquivalentDefects})")

        // Moisture check
        val moisture = sample.moisture.moisturePercent
        val moisturePass = sample.moisture.isMeasured &&
            moisture != null &&
            moisture >= limits.minMoisturePercent &&
            moisture <= limits.maxMoisturePercent
        evaluations["Moisture Content"] = moisturePass
        if (!sample.moisture.isMeasured || moisture == null) {
            failures.add("Moisture is NOT_MEASURED; required by contract")
        } else if (!moisturePass) {
            failures.add("Moisture ($moisture%) outside contract tolerance [${limits.minMoisturePercent}% - ${limits.maxMoisturePercent}%]")
        }

        // Water activity check
        val aw = sample.waterActivity.waterActivityAw
        if (sample.waterActivity.isMeasured && aw != null) {
            val awPass = aw <= limits.maxWaterActivityAw
            evaluations["Water Activity (aw)"] = awPass
            if (!awPass) failures.add("Water activity ($aw) exceeds max contract limit (${limits.maxWaterActivityAw})")
        } else {
            failures.add("Water activity is NOT_MEASURED; required by contract")
        }

        // Sieve check if available
        if (sieveAnalysis != null) {
            if (!sieveAnalysis.isMassBalanceValid) failures.add("Sieve mass balance is invalid")
            val screenPass = sieveAnalysis.cumulativeRetentionScreen16Plus >= limits.minScreen16PlusPercent
            evaluations["Screen 16+ Retention"] = screenPass
            if (!screenPass) {
                failures.add(
                    "Screen 16+ retention (${sieveAnalysis.cumulativeRetentionScreen16Plus}%) below " +
                        "contract minimum (${limits.minScreen16PlusPercent}%)"
                )
            }
        } else {
            failures.add("Sieve analysis is NOT_MEASURED; required by contract")
        }

        // Density check if available
        val density = sample.bulkDensity.bulkDensityGramsPerLiter
        if (sample.bulkDensity.isMeasured && density != null) {
            val densityPass = density >= limits.minBulkDensityGramsPerLiter
            evaluations["Bulk Density"] = densityPass
            if (!densityPass) failures.add("Bulk density ($density g/L) below contract minimum (${limits.minBulkDensityGramsPerLiter} g/L)")
        } else {
            failures.add("Bulk density is NOT_MEASURED; required by contract")
        }

        val isAccepted = failures.isEmpty()
        if (isAccepted) {
            notes.add("All evaluated parameters conform to contract '${limits.contractName}'.")
        } else {
            notes.add("Contract rejected: ${failures.size} parameters out of specification.")
        }

        return BuyerContractEvaluationResult(
            isAccepted = isAccepted,
            parameterEvaluations = evaluations,
            failureReasons = failures,
            notes = notes
        )
    }

    fun computeSha256(input: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(input.toByteArray(Charsets.UTF_8))
        return hashBytes.joinToString("") { "%02x".format(it) }
    }

    fun generateGreenQcJson(
        sample: GreenLotSample,
        evaluation: PhysicalGradingEvaluation,
        audit: GreenQCAuditRecord,
        sieveAnalysis: SieveAnalysisResult? = null
    ): String {
        val rawJson = buildString {
            append("{\n")
            appendJsonField("schema_version", jsonValue("1.0.0"))
            appendJsonField("lot_id", jsonValue(sample.lotId))
            appendJsonField("lot_name", jsonValue(sample.lotName))
            appendJsonField("species", jsonValue(sample.species?.name))
            appendJsonField("process_method", jsonValue(sample.processMethod?.name))
            appendJsonField("origin", jsonValue("${sample.originRegion}, ${sample.originCountry}"))
            appendJsonField("producer", jsonValue(sample.producer))
            appendJsonField("sample_mass_grams", numberValue(sample.sampleMassGrams))
            appendJsonField("standard_applied", jsonValue(evaluation.standardApplied))
            appendJsonField("grade", jsonValue(evaluation.grade.name))
            appendJsonField("classification_basis", jsonValue("HISTORICAL_REFERENCE_UNVERIFIED"))
            appendJsonField("standard_version", jsonValue(audit.standardVersion))
            appendJsonField("official_certification", "false")
            appendJsonField("is_specialty_physical_eligible", evaluation.isSpecialtyPhysicalEligible.toString())
            appendJsonField("cat1_full_defects", numberValue(evaluation.totalCategory1Equivalents))
            appendJsonField("cat2_full_defect_equivalents", numberValue(evaluation.totalCategory2Equivalents))
            appendJsonField("total_defect_equivalents", numberValue(evaluation.totalFullDefectsEquivalents))
            appendJsonField("defect_inspection_status", jsonValue(defectInspectionStatus(sample)))
            appendJsonField("defect_records", defectRecordsJson(sample.defects))
            appendJsonField("chain_of_custody", chainOfCustodyJson(sample.chainOfCustody))
            appendJsonField("sieve_analysis", sieveAnalysis?.let(::sieveAnalysisJson) ?: "null")
            appendJsonField("moisture_percent", numberValue(sample.moisture.moisturePercent))
            appendJsonField("moisture_status", jsonValue(measurementStatus(sample.moisture.isMeasured)))
            appendJsonField("moisture_uncertainty_percent", numberValue(sample.moisture.uncertaintyPercent))
            appendJsonField("moisture_measurement_method", jsonValue(sample.moisture.measurementMethod?.name))
            appendJsonField("moisture_meter_model", jsonValue(sample.moisture.meterDeviceModel))
            appendJsonField("moisture_meter_serial", jsonValue(sample.moisture.meterSerialNumber))
            appendJsonField("moisture_calibration_epoch_ms", sample.moisture.calibrationDateEpochMs?.toString() ?: "null")
            appendJsonField("moisture_sample_temperature_celsius", numberValue(sample.moisture.sampleTemperatureCelsius))
            appendJsonField("water_activity_aw", numberValue(sample.waterActivity.waterActivityAw))
            appendJsonField("water_activity_status", jsonValue(measurementStatus(sample.waterActivity.isMeasured)))
            appendJsonField("water_activity_meter_model", jsonValue(sample.waterActivity.meterModel))
            appendJsonField("water_activity_calibration_epoch_ms", sample.waterActivity.calibrationDateEpochMs?.toString() ?: "null")
            appendJsonField("bulk_density_g_l", numberValue(sample.bulkDensity.bulkDensityGramsPerLiter))
            appendJsonField("bulk_density_status", jsonValue(measurementStatus(sample.bulkDensity.isMeasured)))
            appendJsonField("bulk_density_container_volume_liters", numberValue(sample.bulkDensity.containerVolumeLiters))
            appendJsonField("bulk_density_test_sample_mass_grams", numberValue(sample.bulkDensity.testSampleMassGrams))
            appendJsonField("bean_color", jsonValue(sample.color?.name))
            appendJsonField("odor", jsonValue(sample.odor?.name))
            appendJsonField("assessor_name", jsonValue(audit.assessorName))
            appendJsonField("assessor_id", jsonValue(audit.assessorId))
            appendJsonField("assessor_certification", jsonValue(audit.assessorCertification))
            appendJsonField("evaluation_timestamp", audit.evaluationTimestampEpochMs.toString())
            appendJsonField("equipment_calibration", jsonValue(audit.equipmentCalibrationSummary))
            appendJsonField("evidence_photo_uri_references", stringArrayJson(audit.evidencePhotoUris))
            appendJsonField(
                "evidence_photo_note",
                jsonValue("URI references only; images are not embedded and provider permission may need renewal after reinstall."),
                last = true
            )
            append("}")
        }
        val hash = computeSha256(rawJson)
        return rawJson.dropLast(2) + ",\n  \"sha256_audit_seal\": \"$hash\"\n}"
    }

    fun generateGreenQcCsv(
        sample: GreenLotSample,
        evaluation: PhysicalGradingEvaluation,
        audit: GreenQCAuditRecord,
        sieveAnalysis: SieveAnalysisResult? = null
    ): String = buildString {
        append("Parameter,Value,Unit,Status\n")
        appendCsvRow("Lot Identifier", sample.lotId, "N/A", providedStatus(sample.lotId))
        appendCsvRow("Species", sample.species?.label ?: "NOT_SELECTED", "N/A", selectionStatus(sample.species))
        appendCsvRow("Process Method", sample.processMethod?.label ?: "NOT_SELECTED", "N/A", selectionStatus(sample.processMethod))
        appendCsvRow("Sample Mass", numberValue(sample.sampleMassGrams), "g", measurementStatus(sample.sampleMassGrams > 0.0))
        appendCsvRow("Standard Applied", evaluation.standardApplied, "N/A", "REFERENCE_APPLIED")
        appendCsvRow("Standard Version", audit.standardVersion, "N/A", "RECORDED")
        appendCsvRow("Physical Grade", evaluation.grade.label, "N/A", "HISTORICAL_REFERENCE_ONLY")
        appendCsvRow("Category 1 Defects", numberValue(evaluation.totalCategory1Equivalents), "Full Defect", "EVALUATED")
        appendCsvRow("Category 2 Equivalents", numberValue(evaluation.totalCategory2Equivalents), "Full Defect Equiv", "EVALUATED")
        appendCsvRow("Total Defect Equivalents", numberValue(evaluation.totalFullDefectsEquivalents), "Full Defect Equiv", "EVALUATED")
        appendCsvRow("Defect Inspection", defectInspectionStatus(sample), "N/A", defectInspectionStatus(sample))
        append(defectRecordsCsv(sample.defects))
        append(chainOfCustodyCsv(sample.chainOfCustody))
        append(sieveAnalysisCsv(sieveAnalysis))
        append(measurementsCsv(sample))
        append(auditCsv(audit))
        appendCsvRow("Evaluation Timestamp", audit.evaluationTimestampEpochMs.toString(), "epoch_ms", "RECORDED")
    }

    private fun defectRecordsJson(defects: List<DefectTallyRecord>): String =
        defects.joinToString(",", prefix = "[", postfix = "]") { record ->
            jsonObject(
                listOf(
                    "type" to jsonValue(record.defectType.name),
                    "name_en" to jsonValue(record.defectType.nameEn),
                    "raw_count" to record.rawCount.toString(),
                    "standard_supported" to record.standardSupported.toString(),
                    "equivalent_full_defects" to numberValue(record.equivalentFullDefects),
                    "evidence_photo_uri_references" to stringArrayJson(record.evidencePhotoUris)
                )
            )
        }

    private fun chainOfCustodyJson(custody: ChainOfCustody?): String = custody?.let {
        jsonObject(
            listOf(
                "exporter" to jsonValue(it.exporter),
                "importer" to jsonValue(it.importer),
                "warehouse_location" to jsonValue(it.warehouseLocation),
                "transport_container_id" to jsonValue(it.transportContainerId),
                "harvest_crop_year" to jsonValue(it.harvestCropYear),
                "ambient_storage_temperature_celsius" to numberValue(it.ambientStorageTempCelsius),
                "ambient_storage_relative_humidity_percent" to numberValue(it.ambientStorageRelativeHumidityPercent)
            )
        )
    } ?: "null"

    private fun sieveAnalysisJson(sieve: SieveAnalysisResult): String = jsonObject(
        listOf(
            "sample_mass_grams" to numberValue(sieve.sampleMassGrams),
            "total_recovered_mass_grams" to numberValue(sieve.totalRecoveredMassGrams),
            "mass_balance_mismatch_grams" to numberValue(sieve.massBalanceMismatchGrams),
            "mass_balance_valid" to sieve.isMassBalanceValid.toString(),
            "screen_16_plus_retention_percent" to numberValue(sieve.cumulativeRetentionScreen16Plus),
            "shaker_duration_seconds" to sieve.shakerDurationSeconds.toString(),
            "methodology" to jsonValue(sieve.methodologyNote),
            "fractions" to sieve.fractions.joinToString(",", prefix = "[", postfix = "]") { fraction ->
                jsonObject(
                    listOf(
                        "sieve" to jsonValue(fraction.sieve.name),
                        "hole_size_mm" to numberValue(fraction.sieve.holeSizeMm),
                        "mass_grams" to numberValue(fraction.massGrams),
                        "percentage_of_total" to numberValue(fraction.percentageOfTotal)
                    )
                )
            }
        )
    )

    private fun defectRecordsCsv(defects: List<DefectTallyRecord>): String = buildString {
        defects.forEach { record ->
            val supported = if (record.standardSupported) "SUPPORTED" else "OUT_OF_SCOPE"
            appendCsvRow("Defect ${record.defectType.name}", record.rawCount.toString(), "beans", supported)
            appendCsvRow(
                "Defect ${record.defectType.name} Equivalence",
                numberValue(record.equivalentFullDefects),
                "full_defects",
                if (record.standardSupported) "DETERMINISTIC" else "OUT_OF_SCOPE"
            )
            record.evidencePhotoUris.forEach { uri ->
                appendCsvRow("Defect Evidence URI", uri, "content_uri", "REFERENCE_ONLY")
            }
        }
    }

    private fun chainOfCustodyCsv(custody: ChainOfCustody?): String = buildString {
        appendOptionalCsvRow("Chain of Custody Exporter", custody?.exporter)
        appendOptionalCsvRow("Chain of Custody Importer", custody?.importer)
        appendOptionalCsvRow("Warehouse Location", custody?.warehouseLocation)
        appendOptionalCsvRow("Transport Container ID", custody?.transportContainerId)
        appendOptionalCsvRow("Harvest Crop Year", custody?.harvestCropYear)
        appendOptionalCsvRow("Ambient Storage Temperature", optionalNumberValue(custody?.ambientStorageTempCelsius), "°C")
        appendOptionalCsvRow(
            "Ambient Storage Relative Humidity",
            optionalNumberValue(custody?.ambientStorageRelativeHumidityPercent),
            "%"
        )
    }

    private fun sieveAnalysisCsv(sieve: SieveAnalysisResult?): String {
        if (sieve == null) return csvRow("Sieve Analysis", "NOT_MEASURED", "N/A", "NOT_MEASURED")
        return buildString {
            appendCsvRow(
                "Sieve Mass Balance",
                numberValue(sieve.massBalanceMismatchGrams),
                "g",
                if (sieve.isMassBalanceValid) "VALID" else "INVALID"
            )
            appendCsvRow("Screen 16+ Retention", numberValue(sieve.cumulativeRetentionScreen16Plus), "%", "MEASURED")
            appendCsvRow("Sieve Shaker Duration", sieve.shakerDurationSeconds.toString(), "seconds", "RECORDED")
            appendCsvRow("Sieve Methodology", sieve.methodologyNote, "N/A", "RECORDED")
            sieve.fractions.forEach { fraction ->
                appendCsvRow("Sieve ${fraction.sieve.name}", numberValue(fraction.massGrams), "g", "MEASURED")
                appendCsvRow(
                    "Sieve ${fraction.sieve.name} Share",
                    numberValue(fraction.percentageOfTotal),
                    "%",
                    "CALCULATED"
                )
            }
        }
    }

    private fun measurementsCsv(sample: GreenLotSample): String = buildString {
        val moisture = sample.moisture
        appendCsvRow("Moisture", numberValue(moisture.moisturePercent), "%", measurementStatus(moisture.isMeasured))
        appendOptionalCsvRow("Moisture Uncertainty", optionalNumberValue(moisture.uncertaintyPercent), "%")
        appendOptionalCsvRow("Moisture Method", moisture.measurementMethod?.name)
        appendOptionalCsvRow("Moisture Meter Model", moisture.meterDeviceModel)
        appendOptionalCsvRow("Moisture Meter Serial", moisture.meterSerialNumber)
        appendOptionalCsvRow("Moisture Calibration Date", moisture.calibrationDateEpochMs?.toString(), "epoch_ms")
        appendOptionalCsvRow("Moisture Sample Temperature", optionalNumberValue(moisture.sampleTemperatureCelsius), "°C")
        appendCsvRow(
            "Water Activity",
            numberValue(sample.waterActivity.waterActivityAw),
            "aw",
            measurementStatus(sample.waterActivity.isMeasured)
        )
        appendOptionalCsvRow("Water Activity Meter Model", sample.waterActivity.meterModel)
        appendOptionalCsvRow(
            "Water Activity Calibration Date",
            sample.waterActivity.calibrationDateEpochMs?.toString(),
            "epoch_ms"
        )
        appendCsvRow(
            "Bulk Density",
            numberValue(sample.bulkDensity.bulkDensityGramsPerLiter),
            "g/L",
            measurementStatus(sample.bulkDensity.isMeasured)
        )
        appendCsvRow(
            "Bulk Density Container Volume",
            numberValue(sample.bulkDensity.containerVolumeLiters),
            "L",
            "RECORDED"
        )
        appendOptionalCsvRow(
            "Bulk Density Test Sample Mass",
            optionalNumberValue(sample.bulkDensity.testSampleMassGrams),
            "g"
        )
        appendCsvRow(
            "Odor",
            sample.odor?.label ?: "NOT_ASSESSED",
            "N/A",
            if (sample.odor == null) "NOT_ASSESSED" else "OBSERVED"
        )
        appendCsvRow(
            "Bean Color",
            sample.color?.label ?: "NOT_ASSESSED",
            "N/A",
            if (sample.color == null) "NOT_ASSESSED" else "OBSERVED"
        )
    }

    private fun auditCsv(audit: GreenQCAuditRecord): String = buildString {
        appendOptionalCsvRow("Assessor", audit.assessorName)
        appendOptionalCsvRow("Assessor ID", audit.assessorId)
        appendOptionalCsvRow("Assessor Certification", audit.assessorCertification)
        appendOptionalCsvRow("Equipment Calibration", audit.equipmentCalibrationSummary)
        audit.evidencePhotoUris.forEach { uri ->
            appendCsvRow("Evidence Photo URI", uri, "content_uri", "REFERENCE_ONLY")
        }
    }

    private fun StringBuilder.appendJsonField(key: String, value: String, last: Boolean = false) {
        append("  \"").append(escapeJson(key)).append("\": ").append(value)
        if (!last) append(",")
        append("\n")
    }

    private fun jsonObject(fields: List<Pair<String, String>>): String =
        fields.joinToString(",", prefix = "{", postfix = "}") { (key, value) ->
            "${jsonValue(key)}: $value"
        }

    private fun csvRow(parameter: String, value: String, unit: String, status: String): String =
        "$parameter,${csvCell(value)},$unit,$status\n"

    private fun StringBuilder.appendCsvRow(parameter: String, value: String, unit: String, status: String) {
        append(csvRow(parameter, value, unit, status))
    }

    private fun StringBuilder.appendOptionalCsvRow(parameter: String, value: String?, unit: String = "N/A") {
        val providedValue = value?.takeIf(String::isNotBlank)
        appendCsvRow(parameter, providedValue ?: "NOT_PROVIDED", unit, providedStatus(providedValue))
    }

    private fun defectInspectionStatus(sample: GreenLotSample): String = if (sample.defectInspectionComplete) "COMPLETE" else "NOT_MEASURED"

    private fun measurementStatus(isMeasured: Boolean): String = if (isMeasured) "MEASURED" else "NOT_MEASURED"

    private fun providedStatus(value: String?): String = if (value.isNullOrBlank()) "NOT_PROVIDED" else "RECORDED"

    private fun selectionStatus(value: Any?): String = if (value == null) "NOT_SELECTED" else "SELECTED"

    private fun stringArrayJson(values: List<String>): String = values.joinToString(",", prefix = "[", postfix = "]") { jsonValue(it) }

    private fun jsonValue(value: String?): String = value?.let { "\"${escapeJson(it)}\"" } ?: "null"

    private fun numberValue(value: Double?): String = value?.takeIf(Double::isFinite)?.toString() ?: "null"

    private fun optionalNumberValue(value: Double?): String? = value?.takeIf(Double::isFinite)?.toString()

    private fun escapeJson(value: String): String = buildString(value.length) {
        value.forEach { character ->
            when (character) {
                '\\' -> append("\\\\")
                '"' -> append("\\\"")
                '\n' -> append("\\n")
                '\r' -> append("\\r")
                '\t' -> append("\\t")
                else -> if (character.code < 0x20) append("\\u%04x".format(character.code)) else append(character)
            }
        }
    }

    private fun csvCell(value: String): String {
        val startsWithFormula = value.trimStart().firstOrNull() in setOf('=', '+', '-', '@')
        val safeValue = if (startsWithFormula) "'$value" else value
        return "\"${safeValue.replace("\"", "\"\"")}\""
    }
}
