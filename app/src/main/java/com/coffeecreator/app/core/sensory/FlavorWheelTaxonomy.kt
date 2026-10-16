package com.coffeecreator.app.core.sensory

import java.math.BigDecimal
import java.math.RoundingMode

// ============================================================================
// 1. Hierarchical Flavor Wheel & Taxonomy (S2-051)
// ============================================================================

data class FlavorDescriptor(
    val id: String,
    val name: String,
    val categoryId: String,
    val subcategoryId: String,
    val colorHex: String,
    val sensoryNotes: String,
    val synonyms: List<String> = emptyList(),
    val accessibilityDescription: String,
    val isDefect: Boolean = false
)

data class FlavorSubcategory(
    val id: String,
    val categoryId: String,
    val name: String,
    val descriptors: List<FlavorDescriptor>
)

data class FlavorCategory(
    val id: String,
    val name: String,
    val colorHex: String,
    val subcategories: List<FlavorSubcategory>
)

data class FlavorWheelTaxonomy(
    val version: String,
    val categories: List<FlavorCategory>
)

// ============================================================================
// 2. Safe Food-Grade Calibration Exercises (S2-052)
// ============================================================================

enum class TasteModality {
    SWEETNESS,
    ACIDITY_CITRIC,
    ACIDITY_MALIC,
    ACIDITY_PHOSPHORIC,
    ACIDITY_ACETIC,
    BITTERNESS,
    SALTINESS,
    BODY_TEXTURE
}

enum class ChemicalGrade {
    FOOD_GRADE_FCC,
    PHARMACEUTICAL_USP,
    REAGENT_ACS
}

data class CalibrationReferenceExercise(
    val id: String,
    val compoundName: String,
    val chemicalFormula: String,
    val tasteModality: TasteModality,
    val requiredGrade: ChemicalGrade,
    val targetConcentrationGPerL: BigDecimal,
    val thresholdConcentrationGPerL: BigDecimal,
    val preparationInstructions: String,
    val sensoryCueDescription: String,
    val safetyWarning: String,
    val maxDailyConsumptionLimitMl: Int,
    val allergenNotice: String = "None"
)

// ============================================================================
// 3. Taxonomy & Calibration Registry (SCA/WCR + Indonesian Roda Rasa Kopi)
// ============================================================================

object SensoryTaxonomyRegistry {

    val DEFAULT_CATEGORIES: List<FlavorCategory> = DEFAULT_FLAVOR_CATEGORIES

    val DEFAULT_TAXONOMY: FlavorWheelTaxonomy = FlavorWheelTaxonomy(
        version = "1.0-ORIGINAL-COFFEE-CREATOR",
        categories = DEFAULT_CATEGORIES
    )

    val STANDARD_CALIBRATION_EXERCISES: List<CalibrationReferenceExercise> = listOf(
        CalibrationReferenceExercise(
            id = "cal_citric_lemon",
            compoundName = "Citric Acid Monohydrate (Food Grade)",
            chemicalFormula = "C6H8O7·H2O",
            tasteModality = TasteModality.ACIDITY_CITRIC,
            requiredGrade = ChemicalGrade.FOOD_GRADE_FCC,
            targetConcentrationGPerL = BigDecimal("0.50"),
            thresholdConcentrationGPerL = BigDecimal("0.15"),
            preparationInstructions =
            "Dissolve exactly 0.50 g of food-grade citric acid into 1000 mL of clean, room-temperature " +
                "distilled water (TDS < 10 ppm). Stir thoroughly until clear.",
            sensoryCueDescription = "Bright, crisp, salivary-stimulating lemon/lime sharpness focused on the " +
                "sides of the tongue with a fast clean finish.",
            safetyWarning = "Use only food-grade FCC certified material. Discard solution after 24 hours. Keep away from eyes.",
            maxDailyConsumptionLimitMl = 500
        ),
        CalibrationReferenceExercise(
            id = "cal_malic_apple",
            compoundName = "DL-Malic Acid (Food Grade)",
            chemicalFormula = "C4H6O5",
            tasteModality = TasteModality.ACIDITY_MALIC,
            requiredGrade = ChemicalGrade.FOOD_GRADE_FCC,
            targetConcentrationGPerL = BigDecimal("0.40"),
            thresholdConcentrationGPerL = BigDecimal("0.12"),
            preparationInstructions = "Dissolve exactly 0.40 g of food-grade malic acid into 1000 mL of " +
                "distilled water. Mix until fully transparent.",
            sensoryCueDescription = "Crisp green apple or stone fruit tartness that builds gradually on the " +
                "mid-palate and lingers longer than citric acid.",
            safetyWarning = "Food grade FCC only. Ensure precise milligram weighing. Do not ingest concentrated dry powder.",
            maxDailyConsumptionLimitMl = 500
        ),
        CalibrationReferenceExercise(
            id = "cal_sucrose_sweetness",
            compoundName = "Pure Sucrose (Refined Cane Sugar)",
            chemicalFormula = "C12H22O11",
            tasteModality = TasteModality.SWEETNESS,
            requiredGrade = ChemicalGrade.FOOD_GRADE_FCC,
            targetConcentrationGPerL = BigDecimal("10.00"),
            thresholdConcentrationGPerL = BigDecimal("2.50"),
            preparationInstructions = "Weigh 10.00 g of pure refined white cane sugar and dissolve in 1000 mL " +
                "of clean odorless water at 25°C.",
            sensoryCueDescription = "Clean, rounded, pure sweet sensation focused on the tip and front of the " +
                "tongue with a coating tactile body.",
            safetyWarning = "Store in a sanitized food container. Use within 12 hours to prevent microbial fermentation.",
            maxDailyConsumptionLimitMl = 1000
        ),
        CalibrationReferenceExercise(
            id = "cal_quinine_bitter",
            compoundName = "Quinine Hydrochloride Dihydrate (USP/FCC)",
            chemicalFormula = "C20H24N2O2·HCl·2H2O",
            tasteModality = TasteModality.BITTERNESS,
            requiredGrade = ChemicalGrade.PHARMACEUTICAL_USP,
            // 5 mg/L
            targetConcentrationGPerL = BigDecimal("0.005"),
            thresholdConcentrationGPerL = BigDecimal("0.001"),
            preparationInstructions = "Prepare 5 mg/L solution: prepare a 100x stock (0.50 g in 1000 mL), " +
                "then dilute 10 mL of stock into 990 mL distilled water to yield 5 mg/L.",
            sensoryCueDescription = "Sharp tonic-water bitterness that manifests at the back of the tongue " +
                "and throat with a persistent drying aftertaste.",
            safetyWarning = "Strict limit: target is 5 mg/L. Maximum tasting volume is 50 mL. " +
                "Contraindicated during pregnancy or cinchona alkaloid allergy.",
            maxDailyConsumptionLimitMl = 50,
            allergenNotice = "Contraindicated in pregnancy or hypersensitivity to quinine"
        ),
        CalibrationReferenceExercise(
            id = "cal_caffeine_bitter",
            compoundName = "Anhydrous Caffeine (USP)",
            chemicalFormula = "C8H10N4O2",
            tasteModality = TasteModality.BITTERNESS,
            requiredGrade = ChemicalGrade.PHARMACEUTICAL_USP,
            // 200 mg/L
            targetConcentrationGPerL = BigDecimal("0.20"),
            thresholdConcentrationGPerL = BigDecimal("0.05"),
            preparationInstructions = "Dissolve 0.20 g anhydrous caffeine USP in 1000 mL warm distilled " +
                "water (40°C), cool to room temperature before tasting.",
            sensoryCueDescription = "Classic coffee bitterness with a mild astringent tactile sensation on " +
                "the palate.",
            safetyWarning = "Keep out of reach of children. Total caffeine per 100 mL sample is 20 mg. " +
                "Do not consume if caffeine sensitive.",
            maxDailyConsumptionLimitMl = 250
        )
    )

    fun searchDescriptors(query: String, taxonomy: FlavorWheelTaxonomy = DEFAULT_TAXONOMY): List<FlavorDescriptor> {
        val clean = query.trim().lowercase()
        if (clean.isEmpty()) return getAllDescriptors(taxonomy)
        return getAllDescriptors(taxonomy).filter { desc ->
            desc.name.lowercase().contains(clean) ||
                desc.sensoryNotes.lowercase().contains(clean) ||
                desc.synonyms.any { it.lowercase().contains(clean) } ||
                desc.categoryId.lowercase().contains(clean) ||
                desc.subcategoryId.lowercase().contains(clean)
        }
    }

    fun getAllDescriptors(taxonomy: FlavorWheelTaxonomy = DEFAULT_TAXONOMY): List<FlavorDescriptor> {
        return taxonomy.categories.flatMap { cat ->
            cat.subcategories.flatMap { sub -> sub.descriptors }
        }
    }

    fun findDescriptorByName(name: String, taxonomy: FlavorWheelTaxonomy = DEFAULT_TAXONOMY): FlavorDescriptor? {
        val clean = name.trim().lowercase()
        return getAllDescriptors(taxonomy).firstOrNull {
            it.name.lowercase() == clean ||
                it.name.lowercase().startsWith(clean) ||
                it.synonyms.any { syn -> syn.lowercase() == clean }
        }
    }

    fun findCategoryById(categoryId: String, taxonomy: FlavorWheelTaxonomy = DEFAULT_TAXONOMY): FlavorCategory? {
        return taxonomy.categories.firstOrNull { it.id == categoryId }
    }

    fun findSubcategoryById(subcategoryId: String, taxonomy: FlavorWheelTaxonomy = DEFAULT_TAXONOMY): FlavorSubcategory? {
        return taxonomy.categories.flatMap { it.subcategories }.firstOrNull { it.id == subcategoryId }
    }

    fun getDescriptorsForCategory(categoryId: String, taxonomy: FlavorWheelTaxonomy = DEFAULT_TAXONOMY): List<FlavorDescriptor> {
        return taxonomy.categories.firstOrNull { it.id == categoryId }
            ?.subcategories?.flatMap { it.descriptors } ?: emptyList()
    }

    fun getDescriptorsForSubcategory(subcategoryId: String, taxonomy: FlavorWheelTaxonomy = DEFAULT_TAXONOMY): List<FlavorDescriptor> {
        return taxonomy.categories.flatMap { it.subcategories }
            .firstOrNull { it.id == subcategoryId }?.descriptors ?: emptyList()
    }

    fun calculateCalibrationBatch(exercise: CalibrationReferenceExercise, volumeMl: BigDecimal): BigDecimal {
        // mass (g) = concentration (g/L) * volume (L)
        val volumeLiters = volumeMl.divide(BigDecimal("1000"), 6, RoundingMode.HALF_UP)
        return exercise.targetConcentrationGPerL.multiply(volumeLiters).setScale(4, RoundingMode.HALF_UP)
    }
}
