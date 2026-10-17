package com.coffeecreator.app.core.sensory.greengrading

enum class GreenCoffeeSpecies(val label: String) {
    COFFEA_ARABICA("Coffea Arabica"),
    COFFEA_CANEPHORA("Coffea Canephora (Robusta)")
}

enum class GreenProcessingMethod(val label: String) {
    WASHED("Fully Washed"),
    NATURAL("Natural / Dry Processed"),
    HONEY("Honey / Pulped Natural"),
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
    val nameId: String
) {
    // Category 1: Full Defects (1 bean = 1 full defect, except Severe Insect = 5:1)
    FULL_BLACK(DefectTier.CATEGORY_1_PRIMARY, 1, "Full Black", "Biji Hitam Penuh"),
    FULL_SOUR(DefectTier.CATEGORY_1_PRIMARY, 1, "Full Sour", "Biji Asam Penuh"),
    DRIED_CHERRY_POD(DefectTier.CATEGORY_1_PRIMARY, 1, "Dried Cherry / Pod", "Gelondong Kering"),
    FUNGUS_DAMAGED(DefectTier.CATEGORY_1_PRIMARY, 1, "Fungus Damaged", "Terserang Jamur"),
    FOREIGN_MATTER(DefectTier.CATEGORY_1_PRIMARY, 1, "Foreign Matter", "Benda Asing"),
    SEVERE_INSECT_DAMAGE(DefectTier.CATEGORY_1_PRIMARY, 5, "Severe Insect Damage", "Serangga Parah (>=3 Lubang)"),

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
    SLIGHT_INSECT_DAMAGE(DefectTier.CATEGORY_2_SECONDARY, 10, "Slight Insect Damage", "Serangga Ringan (<3 Lubang)")
}

data class DefectTallyRecord(
    val defectType: GreenDefectType,
    val rawCount: Int,
    val equivalentFullDefects: Double = rawCount.toDouble() / defectType.standardEquivalentRatio
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

data class MoistureAssessment(
    val moisturePercent: Double?,
    val uncertaintyPercent: Double? = 0.2, // e.g. +/- 0.2%
    val meterDeviceModel: String? = null,
    val calibrationDateEpochMs: Long? = null,
    val sampleTemperatureCelsius: Double? = null,
    val isMeasured: Boolean = moisturePercent != null
)

data class GreenLotSample(
    val lotId: String,
    val lotName: String,
    val species: GreenCoffeeSpecies,
    val processMethod: GreenProcessingMethod,
    val sampleMassGrams: Double = 350.0, // Reference standard mass
    val defects: List<DefectTallyRecord> = emptyList(),
    val sieveDistribution: List<SieveFraction> = emptyList(),
    val moisture: MoistureAssessment = MoistureAssessment(null),
    val waterActivityAw: Double? = null,
    val bulkDensityGramsPerLiter: Double? = null,
    val odorObservation: String = "Clean, fresh, vegetal",
    val colorObservation: String = "Bluish-green"
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
    val evaluationNotes: List<String>,
    val disclaimer: String = "Physical grading evaluation based on historical 2004 washed Arabica standards. Does not certify commercial grade or legal contract specifications."
)

object StandardGreenGradingEngine {

    fun evaluateWashedArabicaGrade(sample: GreenLotSample): PhysicalGradingEvaluation {
        val notes = mutableListOf<String>()

        // Invariant guard: species and method validation
        if (sample.species != GreenCoffeeSpecies.COFFEA_ARABICA || sample.processMethod != GreenProcessingMethod.WASHED) {
            return PhysicalGradingEvaluation(
                standardApplied = "SCA Washed Arabica 2004 Reference",
                sampleMassGrams = sample.sampleMassGrams,
                totalCategory1Equivalents = 0.0,
                totalCategory2Equivalents = 0.0,
                totalFullDefectsEquivalents = 0.0,
                grade = PhysicalGradeClassification.UNDETERMINED_OUT_OF_SCOPE,
                isSpecialtyPhysicalEligible = false,
                moistureWithinStandardRange = false,
                evaluationNotes = listOf("Standard only valid for Coffea Arabica washed process. Current sample is ${sample.species.label} / ${sample.processMethod.label}.")
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

        // Moisture check: Standard Arabica Washed is typically 9.0% to 13.0% (ideally 10-12%)
        val moisture = sample.moisture.moisturePercent
        val moistureValid = moisture != null && moisture in 9.0..13.0
        if (moisture == null) {
            notes.add("Moisture was NOT_MEASURED. Moisture verification is required for definitive grade classification.")
        } else if (!moistureValid) {
            notes.add("Moisture level ($moisture%) out of standard range (9.0% - 13.0%).")
        }

        // Classification per 350g washed Arabica:
        // Grade 1 Specialty: 0 Cat 1 defects, <= 5 Cat 2 full defects, moisture 9-13%
        // Grade 2 Premium: <= 8 full defects
        // Grade 3 Exchange: 9 to 23 full defects
        // Grade 4 Below Standard: 24 to 86 full defects
        // Grade 5 Off Grade: > 86 full defects
        val grade: PhysicalGradeClassification
        val isSpecialtyEligible: Boolean

        if (cat1Sum == 0.0 && cat2Sum <= 5.0 && moistureValid) {
            grade = PhysicalGradeClassification.SPECIALTY_GRADE_1
            isSpecialtyEligible = true
            notes.add("Meets Specialty Grade 1 physical criteria (0 Category 1 defects, <= 5 Category 2 equivalents).")
        } else if (totalDefects <= 8.0) {
            grade = PhysicalGradeClassification.PREMIUM_GRADE_2
            isSpecialtyEligible = false
            notes.add("Classified as Premium Grade 2 (<= 8 full defect equivalents).")
        } else if (totalDefects <= 23.0) {
            grade = PhysicalGradeClassification.EXCHANGE_GRADE_3
            isSpecialtyEligible = false
            notes.add("Classified as Exchange Grade 3 (9-23 full defect equivalents).")
        } else if (totalDefects <= 86.0) {
            grade = PhysicalGradeClassification.BELOW_STANDARD_GRADE_4
            isSpecialtyEligible = false
            notes.add("Classified as Below Standard Grade 4 (24-86 full defect equivalents).")
        } else {
            grade = PhysicalGradeClassification.OFF_GRADE_5
            isSpecialtyEligible = false
            notes.add("Classified as Off Grade 5 (> 86 full defect equivalents).")
        }

        return PhysicalGradingEvaluation(
            standardApplied = "SCA Washed Arabica 2004 Reference",
            sampleMassGrams = sample.sampleMassGrams,
            totalCategory1Equivalents = cat1Sum,
            totalCategory2Equivalents = cat2Sum,
            totalFullDefectsEquivalents = totalDefects,
            grade = grade,
            isSpecialtyPhysicalEligible = isSpecialtyEligible,
            moistureWithinStandardRange = moistureValid,
            evaluationNotes = notes
        )
    }

    fun calculateSieveFractions(
        sampleMassGrams: Double,
        sieveMasses: Map<ScreenSieveNumber, Double>
    ): List<SieveFraction> {
        require(sampleMassGrams > 0.0) { "Sample mass must be greater than zero" }
        return sieveMasses.map { (sieve, mass) ->
            SieveFraction(
                sieve = sieve,
                massGrams = mass,
                percentageOfTotal = (mass / sampleMassGrams) * 100.0
            )
        }.sortedByDescending { it.sieve.holeSize64thInch }
    }
}
