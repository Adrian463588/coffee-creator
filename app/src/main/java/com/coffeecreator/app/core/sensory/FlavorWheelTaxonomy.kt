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
    val accessibilityDescription: String
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
// 3. Taxonomy & Calibration Registry
// ============================================================================

object SensoryTaxonomyRegistry {

    val DEFAULT_CATEGORIES: List<FlavorCategory> = listOf(
        FlavorCategory(
            id = "cat_fruity",
            name = "Fruity",
            colorHex = "#E53935",
            subcategories = listOf(
                FlavorSubcategory(
                    id = "sub_berry",
                    categoryId = "cat_fruity",
                    name = "Berry",
                    descriptors = listOf(
                        FlavorDescriptor("desc_blackberry", "Blackberry", "cat_fruity", "sub_berry", "#4A148C", "Rich dark berry sweetness with mild tartness", listOf("bramble"), "Blackberry: deep dark berry sweetness"),
                        FlavorDescriptor("desc_raspberry", "Raspberry", "cat_fruity", "sub_berry", "#C2185B", "Bright tart berry with floral lift", listOf("framboise"), "Raspberry: bright tangy red berry note"),
                        FlavorDescriptor("desc_blueberry", "Blueberry", "cat_fruity", "sub_berry", "#1A237E", "Sweet ripe blueberry classic natural Ethiopia hallmark", emptyList(), "Blueberry: sweet jammy berry aroma"),
                        FlavorDescriptor("desc_strawberry", "Strawberry", "cat_fruity", "sub_berry", "#E91E63", "Juicy ripe red strawberry sweetness", emptyList(), "Strawberry: fresh juicy sweet fruit")
                    )
                ),
                FlavorSubcategory(
                    id = "sub_citrus",
                    categoryId = "cat_fruity",
                    name = "Citrus Fruit",
                    descriptors = listOf(
                        FlavorDescriptor("desc_lemon", "Lemon", "cat_fruity", "sub_citrus", "#FDD835", "Crisp sharp citric acid sensation", listOf("citrus"), "Lemon: sharp bright citric acid"),
                        FlavorDescriptor("desc_lime", "Lime", "cat_fruity", "sub_citrus", "#7CB342", "Zesty high-tone green citrus", emptyList(), "Lime: sharp zesty green citrus note"),
                        FlavorDescriptor("desc_grapefruit", "Grapefruit", "cat_fruity", "sub_citrus", "#FB8C00", "Tart citrus with pleasant bitter quinine-like tonic bite", listOf("pamplemousse"), "Grapefruit: vibrant citrus note with gentle pleasant bitterness"),
                        FlavorDescriptor("desc_orange", "Orange", "cat_fruity", "sub_citrus", "#FF9800", "Round sweet citric sweetness, mandarin juice", listOf("mandarin", "tangerine"), "Orange: sweet balanced citrus fruit")
                    )
                ),
                FlavorSubcategory(
                    id = "sub_stone_fruit",
                    categoryId = "cat_fruity",
                    name = "Stone Fruit",
                    descriptors = listOf(
                        FlavorDescriptor("desc_peach", "Peach", "cat_fruity", "sub_stone_fruit", "#FFAB91", "Velvety sweet delicate stone fruit", listOf("white peach"), "Peach: gentle sweet stone fruit sweetness"),
                        FlavorDescriptor("desc_apricot", "Apricot", "cat_fruity", "sub_stone_fruit", "#FFB74D", "Tangy stone fruit with malic backbone", emptyList(), "Apricot: tangy and sweet yellow fruit"),
                        FlavorDescriptor("desc_cherry", "Cherry", "cat_fruity", "sub_stone_fruit", "#B71C1C", "Red cherry fruitiness with sweet-tart finish", listOf("black cherry"), "Cherry: sweet-tart red stone fruit note")
                    )
                ),
                FlavorSubcategory(
                    id = "sub_tropical",
                    categoryId = "cat_fruity",
                    name = "Tropical Fruit",
                    descriptors = listOf(
                        FlavorDescriptor("desc_mango", "Mango", "cat_fruity", "sub_tropical", "#FFB300", "Rich resinous tropical fruit sweetness", emptyList(), "Mango: lush tropical sweetness"),
                        FlavorDescriptor("desc_passion_fruit", "Passion Fruit", "cat_fruity", "sub_tropical", "#8E24AA", "Intense tart tropical aroma, high malic/citric", emptyList(), "Passion Fruit: punchy tart tropical punch"),
                        FlavorDescriptor("desc_pineapple", "Pineapple", "cat_fruity", "sub_tropical", "#FBC02D", "Bright acidic tropical sweetness", emptyList(), "Pineapple: sharp sweet tropical fruit")
                    )
                )
            )
        ),
        FlavorCategory(
            id = "cat_floral",
            name = "Floral",
            colorHex = "#BA68C8",
            subcategories = listOf(
                FlavorSubcategory(
                    id = "sub_floral_notes",
                    categoryId = "cat_floral",
                    name = "Floral Notes",
                    descriptors = listOf(
                        FlavorDescriptor("desc_jasmine", "Jasmine", "cat_floral", "sub_floral_notes", "#E1BEE7", "Delicate perfume, white flower, Geisha signature", emptyList(), "Jasmine: ethereal white blossom aroma"),
                        FlavorDescriptor("desc_bergamot", "Bergamot", "cat_floral", "sub_floral_notes", "#9CCC65", "Citrus-floral Earl Grey tea hallmark", emptyList(), "Bergamot: perfumed citrus-tea floral note"),
                        FlavorDescriptor("desc_rose", "Rose", "cat_floral", "sub_floral_notes", "#F48FB1", "Sweet elegant floral aromatics", emptyList(), "Rose: sweet floral petal aroma"),
                        FlavorDescriptor("desc_chamomile", "Chamomile", "cat_floral", "sub_floral_notes", "#FFF59D", "Soothing herbal sweet flower tea", emptyList(), "Chamomile: soft herbal floral sweetness")
                    )
                )
            )
        ),
        FlavorCategory(
            id = "cat_sweet",
            name = "Sweet",
            colorHex = "#FFA726",
            subcategories = listOf(
                FlavorSubcategory(
                    id = "sub_sugars",
                    categoryId = "cat_sweet",
                    name = "Sugars & Syrups",
                    descriptors = listOf(
                        FlavorDescriptor("desc_caramel", "Caramel", "cat_sweet", "sub_sugars", "#D84315", "Cooked sucrose sweetness with buttery undertones", emptyList(), "Caramel: rich cooked sugar sweetness"),
                        FlavorDescriptor("desc_honey", "Honey", "cat_sweet", "sub_sugars", "#FFD54F", "Floral nectar sweetness with heavy viscosity", emptyList(), "Honey: viscous floral sweetness"),
                        FlavorDescriptor("desc_maple", "Maple Syrup", "cat_sweet", "sub_sugars", "#8D6E63", "Woody sweet maple finish", emptyList(), "Maple Syrup: woody concentrated syrup"),
                        FlavorDescriptor("desc_brown_sugar", "Brown Sugar", "cat_sweet", "sub_sugars", "#A1887F", "Warm molasses-tinted cane sugar sweetness", emptyList(), "Brown Sugar: warm cane sugar with mild molasses")
                    )
                )
            )
        ),
        FlavorCategory(
            id = "cat_nutty_cocoa",
            name = "Nutty / Cocoa",
            colorHex = "#795548",
            subcategories = listOf(
                FlavorSubcategory(
                    id = "sub_cocoa",
                    categoryId = "cat_nutty_cocoa",
                    name = "Cocoa & Chocolate",
                    descriptors = listOf(
                        FlavorDescriptor("desc_dark_chocolate", "Dark Chocolate", "cat_nutty_cocoa", "sub_cocoa", "#3E2723", "Deep cocoa bitterness with sweet finish", listOf("cacao"), "Dark Chocolate: rich bittersweet cacao"),
                        FlavorDescriptor("desc_milk_chocolate", "Milk Chocolate", "cat_nutty_cocoa", "sub_cocoa", "#5D4037", "Creamy sweet chocolate with dairy sensation", emptyList(), "Milk Chocolate: creamy smooth chocolate note")
                    )
                ),
                FlavorSubcategory(
                    id = "sub_nuts",
                    categoryId = "cat_nutty_cocoa",
                    name = "Nuts",
                    descriptors = listOf(
                        FlavorDescriptor("desc_almond", "Almond", "cat_nutty_cocoa", "sub_nuts", "#D7CCC8", "Sweet nutty benzaldehyde character", emptyList(), "Almond: sweet nutty marzipan tone"),
                        FlavorDescriptor("desc_hazelnut", "Hazelnut", "cat_nutty_cocoa", "sub_nuts", "#BCAAA4", "Toasty rich nut sweetness", emptyList(), "Hazelnut: roasted round nut aroma"),
                        FlavorDescriptor("desc_walnut", "Walnut", "cat_nutty_cocoa", "sub_nuts", "#8D6E63", "Nutty with mild pleasant astringent skin note", emptyList(), "Walnut: earthy nutty note with dry finish")
                    )
                )
            )
        ),
        FlavorCategory(
            id = "cat_spices",
            name = "Spices",
            colorHex = "#D32F2F",
            subcategories = listOf(
                FlavorSubcategory(
                    id = "sub_sweet_spice",
                    categoryId = "cat_spices",
                    name = "Sweet & Pungent Spices",
                    descriptors = listOf(
                        FlavorDescriptor("desc_cinnamon", "Cinnamon", "cat_spices", "sub_sweet_spice", "#A52A2A", "Warm sweet woody cinnamaldehyde", emptyList(), "Cinnamon: warm baking spice sweetness"),
                        FlavorDescriptor("desc_cardamom", "Cardamom", "cat_spices", "sub_sweet_spice", "#4E342E", "Herbaceous aromatic pine-camphor spice", emptyList(), "Cardamom: cool pungent herbal spice"),
                        FlavorDescriptor("desc_clove", "Clove", "cat_spices", "sub_sweet_spice", "#3E2723", "Deep eugenol medicinal warmth", emptyList(), "Clove: pungent warm eugenol spice"),
                        FlavorDescriptor("desc_black_pepper", "Black Pepper", "cat_spices", "sub_sweet_spice", "#212121", "Sharp piperine tingling pungency", emptyList(), "Black Pepper: sharp peppery piquancy")
                    )
                )
            )
        ),
        FlavorCategory(
            id = "cat_roasted",
            name = "Roasted",
            colorHex = "#424242",
            subcategories = listOf(
                FlavorSubcategory(
                    id = "sub_roast_notes",
                    categoryId = "cat_roasted",
                    name = "Roast & Char",
                    descriptors = listOf(
                        FlavorDescriptor("desc_toast", "Toast", "cat_roasted", "sub_roast_notes", "#8D6E63", "Maillard toasted bread crust", emptyList(), "Toast: golden toasted grain note"),
                        FlavorDescriptor("desc_tobacco", "Tobacco", "cat_roasted", "sub_roast_notes", "#4E342E", "Cured dry pipe tobacco leaf", emptyList(), "Tobacco: aromatic cured dried leaf"),
                        FlavorDescriptor("desc_smoky", "Smoky", "cat_roasted", "sub_roast_notes", "#263238", "Wood smoke or charcoal roast aroma", emptyList(), "Smoky: pungent wood smoke aroma")
                    )
                )
            )
        ),
        FlavorCategory(
            id = "cat_cereal",
            name = "Cereal / Grain",
            colorHex = "#FDD835",
            subcategories = listOf(
                FlavorSubcategory(
                    id = "sub_grains",
                    categoryId = "cat_cereal",
                    name = "Grains",
                    descriptors = listOf(
                        FlavorDescriptor("desc_malt", "Malt", "cat_cereal", "sub_grains", "#D7CCC8", "Sweet germinated barley sugar", emptyList(), "Malt: sweet rich barley cereal note"),
                        FlavorDescriptor("desc_oat", "Oat", "cat_cereal", "sub_grains", "#FFF9C4", "Creamy cooked rolled oat texture and taste", emptyList(), "Oat: gentle sweet grain flavor")
                    )
                )
            )
        ),
        FlavorCategory(
            id = "cat_fermented",
            name = "Fermented",
            colorHex = "#6A1B9A",
            subcategories = listOf(
                FlavorSubcategory(
                    id = "sub_ferment",
                    categoryId = "cat_fermented",
                    name = "Wine & Ferment",
                    descriptors = listOf(
                        FlavorDescriptor("desc_red_wine", "Red Wine", "cat_fermented", "sub_ferment", "#880E4F", "Tannic vinous red grape fermented acidity", listOf("vinous"), "Red Wine: fermented grape and vinous depth"),
                        FlavorDescriptor("desc_whiskey", "Whiskey / Bourbon", "cat_fermented", "sub_ferment", "#BF360C", "Oak aged spirit warmth with vanilla esters", emptyList(), "Whiskey: barrel oak and spirit warmth"),
                        FlavorDescriptor("desc_kombucha", "Kombucha / Cider", "cat_fermented", "sub_ferment", "#4A148C", "Funky probiotic acetic/lactic effervescent tang", emptyList(), "Kombucha: tangy fermented fruit effervescence")
                    )
                )
            )
        ),
        FlavorCategory(
            id = "cat_green",
            name = "Green / Vegetative",
            colorHex = "#2E7D32",
            subcategories = listOf(
                FlavorSubcategory(
                    id = "sub_vegetative",
                    categoryId = "cat_green",
                    name = "Herbs & Fresh Green",
                    descriptors = listOf(
                        FlavorDescriptor("desc_green_tea", "Green Tea", "cat_green", "sub_vegetative", "#81C784", "Fresh vegetal sencha with gentle astringency", emptyList(), "Green Tea: steamed green leaf and clean clarity"),
                        FlavorDescriptor("desc_lemongrass", "Lemongrass", "cat_green", "sub_vegetative", "#AED581", "Crisp herbal citrus stalk", emptyList(), "Lemongrass: fresh herbal citrus aroma"),
                        FlavorDescriptor("desc_peas", "Green Pea / Vegetal", "cat_green", "sub_vegetative", "#66BB6A", "Under-developed green grassy or snap pea note", emptyList(), "Green Pea: sharp vegetal green note")
                    )
                )
            )
        )
    )

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
            preparationInstructions = "Dissolve exactly 0.50 g of food-grade citric acid into 1000 mL of clean, room-temperature distilled water (TDS < 10 ppm). Stir thoroughly until clear.",
            sensoryCueDescription = "Bright, crisp, salivary-stimulating lemon/lime sharpness focused on the sides of the tongue with a fast clean finish.",
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
            preparationInstructions = "Dissolve exactly 0.40 g of food-grade malic acid into 1000 mL of distilled water. Mix until fully transparent.",
            sensoryCueDescription = "Crisp green apple or stone fruit tartness that builds gradually on the mid-palate and lingers longer than citric acid.",
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
            preparationInstructions = "Weigh 10.00 g of pure refined white cane sugar and dissolve in 1000 mL of clean odorless water at 25°C.",
            sensoryCueDescription = "Clean, rounded, pure sweet sensation focused on the tip and front of the tongue with a coating tactile body.",
            safetyWarning = "Store in a sanitized food container. Use within 12 hours to prevent microbial fermentation.",
            maxDailyConsumptionLimitMl = 1000
        ),
        CalibrationReferenceExercise(
            id = "cal_quinine_bitter",
            compoundName = "Quinine Hydrochloride Dihydrate (USP/FCC)",
            chemicalFormula = "C20H24N2O2·HCl·2H2O",
            tasteModality = TasteModality.BITTERNESS,
            requiredGrade = ChemicalGrade.PHARMACEUTICAL_USP,
            targetConcentrationGPerL = BigDecimal("0.005"), // 5 mg/L
            thresholdConcentrationGPerL = BigDecimal("0.001"),
            preparationInstructions = "Prepare 5 mg/L solution: prepare a 100x stock (0.50 g in 1000 mL), then dilute 10 mL of stock into 990 mL distilled water to yield 5 mg/L.",
            sensoryCueDescription = "Sharp tonic-water bitterness that manifests at the back of the tongue and throat with a persistent drying aftertaste.",
            safetyWarning = "Strict limit: target is 5 mg/L. Maximum tasting volume is 50 mL. Contraindicated during pregnancy or cinchona alkaloid allergy.",
            maxDailyConsumptionLimitMl = 50,
            allergenNotice = "Contraindicated in pregnancy or hypersensitivity to quinine"
        ),
        CalibrationReferenceExercise(
            id = "cal_caffeine_bitter",
            compoundName = "Anhydrous Caffeine (USP)",
            chemicalFormula = "C8H10N4O2",
            tasteModality = TasteModality.BITTERNESS,
            requiredGrade = ChemicalGrade.PHARMACEUTICAL_USP,
            targetConcentrationGPerL = BigDecimal("0.20"), // 200 mg/L
            thresholdConcentrationGPerL = BigDecimal("0.05"),
            preparationInstructions = "Dissolve 0.20 g anhydrous caffeine USP in 1000 mL warm distilled water (40°C), cool to room temperature before tasting.",
            sensoryCueDescription = "Classic coffee bitterness with a mild astringent tactile sensation on the palate.",
            safetyWarning = "Keep out of reach of children. Total caffeine per 100 mL sample is 20 mg. Do not consume if caffeine sensitive.",
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

    fun calculateCalibrationBatch(exercise: CalibrationReferenceExercise, volumeMl: BigDecimal): BigDecimal {
        // mass (g) = concentration (g/L) * volume (L)
        val volumeLiters = volumeMl.divide(BigDecimal("1000"), 6, RoundingMode.HALF_UP)
        return exercise.targetConcentrationGPerL.multiply(volumeLiters).setScale(4, RoundingMode.HALF_UP)
    }
}
