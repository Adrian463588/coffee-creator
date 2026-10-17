package com.coffeecreator.app.core.sensory.greengrading

import java.security.MessageDigest
import kotlin.math.abs

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
    val standardEquivalentRatio: Int, // e.g. 5 beans = 1 full defect
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
)

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
    val isMassBalanceValid: Boolean, // Mismatch <= 1.5g per ISO 4150
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
    val uncertaintyPercent: Double? = 0.2, // e.g. +/- 0.2%
    val meterDeviceModel: String? = null,
    val meterSerialNumber: String? = null,
    val calibrationDateEpochMs: Long? = null,
    val sampleTemperatureCelsius: Double? = null,
    val measurementMethod: MoistureMeasurementMethod = MoistureMeasurementMethod.DIELECTRIC_CAPACITANCE,
    val isMeasured: Boolean = moisturePercent != null
)

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
    val measurementTemperatureCelsius: Double? = 25.0,
    val meterModel: String? = null,
    val calibrationDateEpochMs: Long? = null,
    val isMeasured: Boolean = waterActivityAw != null,
    val isSafeStorage: Boolean = (waterActivityAw ?: 1.0) <= 0.70
)

data class BulkDensityAssessment(
    val bulkDensityGramsPerLiter: Double?,
    val containerVolumeLiters: Double = 1.0,
    val testSampleMassGrams: Double? = null,
    val isMeasured: Boolean = bulkDensityGramsPerLiter != null,
    val isStrictlyHardBean: Boolean = (bulkDensityGramsPerLiter ?: 0.0) >= 700.0
)

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
            name = "SCA Washed Arabica 2004 Reference",
            version = "2004.1",
            sourceCitation = "SCAA Green Coffee Defect Handbook (2004)",
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
    val species: GreenCoffeeSpecies,
    val processMethod: GreenProcessingMethod,
    val originCountry: String = "Ethiopia",
    val originRegion: String = "Yirgacheffe",
    val producer: String = "Local Smallholders",
    val harvestCropYear: String = "2025/2026",
    val sampleMassGrams: Double = 350.0, // Reference standard mass
    val chainOfCustody: ChainOfCustody? = null,
    val defects: List<DefectTallyRecord> = emptyList(),
    val sieveDistribution: List<SieveFraction> = emptyList(),
    val moisture: MoistureAssessment = MoistureAssessment(null),
    val waterActivity: WaterActivityAssessment = WaterActivityAssessment(null),
    val bulkDensity: BulkDensityAssessment = BulkDensityAssessment(null),
    val color: GreenBeanColor = GreenBeanColor.BLUISH_GREEN,
    val odor: GreenBeanOdor = GreenBeanOdor.CLEAN_FRESH_VEGETAL,
    val foreignMatterObservation: String? = null,
    val standardRef: GradingStandardRef = GradingStandardRef.SCA_WASHED_ARABICA_2004
)

enum class PhysicalGradeClassification(val label: String) {
    SPECIALTY_GRADE_1("Specialty Grade 1"),
    PREMIUM_GRADE_2("Premium Grade 2"),
    EXCHANGE_GRADE_3("Exchange Grade 3"),
    BELOW_STANDARD_GRADE_4("Below Standard Grade 4"),
    OFF_GRADE_5("Off Grade 5"),
    UNDETERMINED_OUT_OF_SCOPE("Undetermined / Out of Scope")
}

data class PhysicalGradingEvaluation(
    val standardApplied: String, // e.g. "SCA Washed Arabica 2004 Reference"
    val sampleMassGrams: Double,
    val totalCategory1Equivalents: Double,
    val totalCategory2Equivalents: Double,
    val totalFullDefectsEquivalents: Double,
    val grade: PhysicalGradeClassification,
    val isSpecialtyPhysicalEligible: Boolean,
    val moistureWithinStandardRange: Boolean,
    val odorClean: Boolean,
    val evaluationNotes: List<String>,
    val disclaimer: String = "Physical grading evaluation based on historical 2004 washed Arabica standards. Evaluates raw seed integrity only. Does not guarantee or compute sensory CVA cupping scores."
)

data class DualSpecialtyEvaluation(
    val physicalGrade: PhysicalGradeClassification,
    val isPhysicalSpecialtyEligible: Boolean,
    val sensoryCvaAffectiveScore: Double?,
    val isSensorySpecialtyEligible: Boolean = (sensoryCvaAffectiveScore ?: 0.0) >= 80.0,
    val isFullSpecialtyCertified: Boolean = isPhysicalSpecialtyEligible && isSensorySpecialtyEligible,
    val guidanceMessage: String
)

data class BuyerAcceptanceLimits(
    val contractName: String = "Specialty Washed Arabica Contract Spec",
    val maxCategory1FullDefects: Int = 0,
    val maxTotalEquivalentDefects: Double = 5.0,
    val minMoisturePercent: Double = 10.0,
    val maxMoisturePercent: Double = 12.0,
    val maxWaterActivityAw: Double = 0.620,
    val minScreen16PlusPercent: Double = 85.0,
    val minBulkDensityGramsPerLiter: Double = 680.0
)

data class BuyerContractEvaluationResult(
    val isAccepted: Boolean,
    val parameterEvaluations: Map<String, Boolean>,
    val failureReasons: List<String>,
    val notes: List<String>
)

data class GreenQCAuditRecord(
    val assessorName: String,
    val assessorId: String,
    val assessorCertification: String? = "Q-Grader Practice Assessor",
    val evaluationTimestampEpochMs: Long,
    val evidencePhotoUris: List<String> = emptyList(),
    val standardVersion: String = "SCA 2004 Washed Arabica Reference v1.0",
    val equipmentCalibrationSummary: String = "Sinar AP 6060 (Calibrated 2026-08-15), A&D Precision Balance 0.01g",
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
                odorClean = sample.odor.isOrganolepticallyClean,
                evaluationNotes = listOf("Standard only valid for Coffea Arabica washed process. Current sample is ${sample.species.label} / ${sample.processMethod.label}. Robusta or Natural requires separate specialized grading matrices.")
            )
        }

        var cat1Sum = 0.0
        var cat2Sum = 0.0

        sample.defects.forEach { record ->
            if (record.defectType.tier == DefectTier.CATEGORY_1_PRIMARY) {
                cat1Sum += record.equivalentFullDefects
            } else {
                cat2Sum += record.equivalentFullDefects
            }
        }
        val totalDefects = cat1Sum + cat2Sum

        // Odor check: organoleptically clean is mandatory for specialty
        val odorClean = sample.odor.isOrganolepticallyClean
        if (!odorClean) {
            notes.add("ODOR_CONTAMINATION_DISQUALIFIED: Sample has unacceptable odor '${sample.odor.label}'. Immediately disqualified from Specialty Grade.")
        }

        // Moisture check: Standard Arabica Washed is 9.0% to 13.0% (ideally 10-12%)
        val moisture = sample.moisture.moisturePercent
        val moistureValid = moisture != null && moisture in 9.0..13.0
        if (moisture == null) {
            notes.add("Moisture was NOT_MEASURED. Moisture verification is required for definitive grade classification.")
        } else if (!moistureValid) {
            notes.add("Moisture level ($moisture%) out of standard range (9.0% - 13.0%).")
        }

        // Classification per 350g washed Arabica:
        // Grade 1 Specialty: 0 Cat 1 defects, <= 5 Cat 2 full defects, moisture 9-13%, odor clean
        // Grade 2 Premium: <= 8 full defects
        // Grade 3 Exchange: 9 to 23 full defects
        // Grade 4 Below Standard: 24 to 86 full defects
        // Grade 5 Off Grade: > 86 full defects
        val grade: PhysicalGradeClassification
        val isSpecialtyEligible: Boolean

        if (cat1Sum == 0.0 && cat2Sum <= 5.0 && moistureValid && odorClean) {
            grade = PhysicalGradeClassification.SPECIALTY_GRADE_1
            isSpecialtyEligible = true
            notes.add("Meets Specialty Grade 1 physical criteria (0 Category 1 defects, <= 5 Category 2 equivalents, valid moisture, clean odor).")
        } else if (totalDefects <= 8.0 && odorClean) {
            grade = PhysicalGradeClassification.PREMIUM_GRADE_2
            isSpecialtyEligible = false
            if (cat1Sum > 0.0) {
                notes.add("Category 1 defects present ($cat1Sum). Disqualified from Specialty Grade 1.")
            } else {
                notes.add("Classified as Premium Grade 2 (<= 8 full defect equivalents).")
            }
        } else if (totalDefects <= 23.0 && odorClean) {
            grade = PhysicalGradeClassification.EXCHANGE_GRADE_3
            isSpecialtyEligible = false
            notes.add("Classified as Exchange Grade 3 (9-23 full defect equivalents).")
        } else if (totalDefects <= 86.0 && odorClean) {
            grade = PhysicalGradeClassification.BELOW_STANDARD_GRADE_4
            isSpecialtyEligible = false
            notes.add("Classified as Below Standard Grade 4 (24-86 full defect equivalents).")
        } else {
            grade = PhysicalGradeClassification.OFF_GRADE_5
            isSpecialtyEligible = false
            notes.add("Classified as Off Grade 5 (> 86 full defect equivalents or severe contamination).")
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
        require(sampleMassGrams > 0.0) { "Sample mass must be greater than zero" }
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

    fun calculateSieveFractions(
        sampleMassGrams: Double,
        sieveMasses: Map<ScreenSieveNumber, Double>
    ): List<SieveFraction> {
        return calculateSieveAnalysis(sampleMassGrams, sieveMasses).fractions
    }

    fun calculateBulkDensity(
        massGrams: Double,
        volumeLiters: Double = 1.0
    ): BulkDensityAssessment {
        require(volumeLiters > 0.0) { "Volume must be greater than zero" }
        val densityGramsPerLiter = massGrams / volumeLiters
        return BulkDensityAssessment(
            bulkDensityGramsPerLiter = densityGramsPerLiter,
            containerVolumeLiters = volumeLiters,
            testSampleMassGrams = massGrams,
            isMeasured = true,
            isStrictlyHardBean = densityGramsPerLiter >= 700.0
        )
    }

    fun evaluateDualSpecialty(
        physicalEvaluation: PhysicalGradingEvaluation,
        sensoryCvaScore: Double?
    ): DualSpecialtyEvaluation {
        val isSensoryEligible = sensoryCvaScore != null && sensoryCvaScore >= 80.0
        val isFull = physicalEvaluation.isSpecialtyPhysicalEligible && isSensoryEligible

        val guidance = when {
            isFull -> "Certified Specialty: Passed both physical defect thresholds (Grade 1) and sensory cupping standard (>= 80.00)."
            physicalEvaluation.isSpecialtyPhysicalEligible && !isSensoryEligible ->
                "Physical Grade 1 achieved, but sensory score (${sensoryCvaScore ?: "NOT_CUIPPED"}) is below 80.00. Lot cannot be sold as full specialty coffee."
            !physicalEvaluation.isSpecialtyPhysicalEligible && isSensoryEligible ->
                "High sensory cup score (${sensoryCvaScore}), but physical defects disqualify lot from Grade 1 (${physicalEvaluation.grade.label}). Physical grade cannot be overridden by cup score."
            else -> "Neither physical grade nor sensory score meets specialty criteria."
        }

        return DualSpecialtyEvaluation(
            physicalGrade = physicalEvaluation.grade,
            isPhysicalSpecialtyEligible = physicalEvaluation.isSpecialtyPhysicalEligible,
            sensoryCvaAffectiveScore = sensoryCvaScore,
            isSensorySpecialtyEligible = isSensoryEligible,
            isFullSpecialtyCertified = isFull,
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
        sample.defects.forEach { record ->
            if (record.defectType.tier == DefectTier.CATEGORY_1_PRIMARY) {
                cat1Count += record.rawCount
            } else {
                cat2Equiv += record.equivalentFullDefects
            }
        }
        val totalDefects = cat1Count + cat2Equiv

        // Cat 1 Defect check
        val cat1Pass = cat1Count <= limits.maxCategory1FullDefects
        evaluations["Category 1 Defects"] = cat1Pass
        if (!cat1Pass) failures.add("Category 1 defects ($cat1Count) exceed contract limit (${limits.maxCategory1FullDefects})")

        // Total Defect check
        val totalPass = totalDefects <= limits.maxTotalEquivalentDefects
        evaluations["Total Full Defect Equivalents"] = totalPass
        if (!totalPass) failures.add("Total defects ($totalDefects) exceed contract limit (${limits.maxTotalEquivalentDefects})")

        // Moisture check
        val moisture = sample.moisture.moisturePercent
        val moisturePass = moisture != null && moisture >= limits.minMoisturePercent && moisture <= limits.maxMoisturePercent
        evaluations["Moisture Content"] = moisturePass
        if (moisture == null) {
            failures.add("Moisture is NOT_MEASURED; required by contract")
        } else if (!moisturePass) {
            failures.add("Moisture ($moisture%) outside contract tolerance [${limits.minMoisturePercent}% - ${limits.maxMoisturePercent}%]")
        }

        // Water activity check
        val aw = sample.waterActivity.waterActivityAw
        if (aw != null) {
            val awPass = aw <= limits.maxWaterActivityAw
            evaluations["Water Activity (aw)"] = awPass
            if (!awPass) failures.add("Water activity ($aw) exceeds max contract limit (${limits.maxWaterActivityAw})")
        }

        // Sieve check if available
        if (sieveAnalysis != null) {
            val screenPass = sieveAnalysis.cumulativeRetentionScreen16Plus >= limits.minScreen16PlusPercent
            evaluations["Screen 16+ Retention"] = screenPass
            if (!screenPass) failures.add("Screen 16+ retention (${sieveAnalysis.cumulativeRetentionScreen16Plus}%) below contract minimum (${limits.minScreen16PlusPercent}%)")
        }

        // Density check if available
        val density = sample.bulkDensity.bulkDensityGramsPerLiter
        if (density != null) {
            val densityPass = density >= limits.minBulkDensityGramsPerLiter
            evaluations["Bulk Density"] = densityPass
            if (!densityPass) failures.add("Bulk density (${density} g/L) below contract minimum (${limits.minBulkDensityGramsPerLiter} g/L)")
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
        audit: GreenQCAuditRecord
    ): String {
        val rawJson = buildString {
            append("{\n")
            append("  \"schema_version\": \"1.0.0\",\n")
            append("  \"lot_id\": \"${sample.lotId}\",\n")
            append("  \"lot_name\": \"${sample.lotName}\",\n")
            append("  \"species\": \"${sample.species.name}\",\n")
            append("  \"process_method\": \"${sample.processMethod.name}\",\n")
            append("  \"origin\": \"${sample.originRegion}, ${sample.originCountry}\",\n")
            append("  \"producer\": \"${sample.producer}\",\n")
            append("  \"sample_mass_grams\": ${sample.sampleMassGrams},\n")
            append("  \"standard_applied\": \"${evaluation.standardApplied}\",\n")
            append("  \"grade\": \"${evaluation.grade.name}\",\n")
            append("  \"is_specialty_physical_eligible\": ${evaluation.isSpecialtyPhysicalEligible},\n")
            append("  \"cat1_full_defects\": ${evaluation.totalCategory1Equivalents},\n")
            append("  \"cat2_full_defect_equivalents\": ${evaluation.totalCategory2Equivalents},\n")
            append("  \"total_defect_equivalents\": ${evaluation.totalFullDefectsEquivalents},\n")
            append("  \"moisture_percent\": ${sample.moisture.moisturePercent ?: "null"},\n")
            append("  \"water_activity_aw\": ${sample.waterActivity.waterActivityAw ?: "null"},\n")
            append("  \"bulk_density_g_l\": ${sample.bulkDensity.bulkDensityGramsPerLiter ?: "null"},\n")
            append("  \"odor\": \"${sample.odor.name}\",\n")
            append("  \"assessor_name\": \"${audit.assessorName}\",\n")
            append("  \"assessor_id\": \"${audit.assessorId}\",\n")
            append("  \"evaluation_timestamp\": ${audit.evaluationTimestampEpochMs},\n")
            append("  \"equipment_calibration\": \"${audit.equipmentCalibrationSummary}\"\n")
            append("}")
        }
        val hash = computeSha256(rawJson)
        return rawJson.dropLast(2) + ",\n  \"sha256_audit_seal\": \"$hash\"\n}"
    }

    fun generateGreenQcCsv(
        sample: GreenLotSample,
        evaluation: PhysicalGradingEvaluation,
        audit: GreenQCAuditRecord
    ): String {
        return buildString {
            append("Parameter,Value,Unit,Status\n")
            append("Lot Identifier,${sample.lotId},N/A,MEASURED\n")
            append("Species,${sample.species.label},N/A,MEASURED\n")
            append("Process Method,${sample.processMethod.label},N/A,MEASURED\n")
            append("Sample Mass,${sample.sampleMassGrams},g,MEASURED\n")
            append("Standard Applied,${evaluation.standardApplied},N/A,VERIFIED\n")
            append("Physical Grade,${evaluation.grade.label},N/A,EVALUATED\n")
            append("Category 1 Defects,${evaluation.totalCategory1Equivalents},Full Defect,EVALUATED\n")
            append("Category 2 Equivalents,${evaluation.totalCategory2Equivalents},Full Defect Equiv,EVALUATED\n")
            append("Total Defect Equivalents,${evaluation.totalFullDefectsEquivalents},Full Defect Equiv,EVALUATED\n")
            append("Moisture,${sample.moisture.moisturePercent ?: "NOT_MEASURED"},%,${if (sample.moisture.isMeasured) "MEASURED" else "NOT_MEASURED"}\n")
            append("Water Activity,${sample.waterActivity.waterActivityAw ?: "NOT_MEASURED"},aw,${if (sample.waterActivity.isMeasured) "MEASURED" else "NOT_MEASURED"}\n")
            append("Bulk Density,${sample.bulkDensity.bulkDensityGramsPerLiter ?: "NOT_MEASURED"},g/L,${if (sample.bulkDensity.isMeasured) "MEASURED" else "NOT_MEASURED"}\n")
            append("Odor,${sample.odor.label},N/A,OBSERVED\n")
            append("Assessor,${audit.assessorName},N/A,AUDITED\n")
            append("Evaluation Timestamp,${audit.evaluationTimestampEpochMs},epoch_ms,AUDITED\n")
        }
    }
}
