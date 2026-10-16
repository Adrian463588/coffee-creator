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

    val DEFAULT_CATEGORIES: List<FlavorCategory> = listOf(
        // 1. FLORAL
        FlavorCategory(
            id = "cat_floral",
            name = "Floral",
            colorHex = "#DB4C6C",
            subcategories = listOf(
                FlavorSubcategory(
                    id = "sub_black_tea",
                    categoryId = "cat_floral",
                    name = "Black Tea",
                    descriptors = listOf(
                        FlavorDescriptor(
                            "desc_black_tea",
                            "Black Tea / Darjeeling",
                            "cat_floral",
                            "sub_black_tea",
                            "#A04858",
                            "Brisk tannic dried oxidized tea leaf with gentle astringency",
                            listOf("darjeeling", "ceylon"),
                            "Black Tea: brisk tannic dried black tea note, dark rose red, hex A04858"
                        ),
                        FlavorDescriptor(
                            "desc_bergamot",
                            "Bergamot",
                            "cat_floral",
                            "sub_black_tea",
                            "#B36274",
                            "Zesty aromatic citrus-floral Earl Grey hallmark",
                            listOf("earl grey"),
                            "Bergamot: perfumed citrus-tea floral note, mauve pink, hex B36274"
                        ),
                        FlavorDescriptor(
                            "desc_orange_pekoe",
                            "Orange Pekoe",
                            "cat_floral",
                            "sub_black_tea",
                            "#8C3848",
                            "Malty, sweet dried tea leaves with mild floral resinous finish",
                            emptyList(),
                            "Orange Pekoe: sweet malty dried tea leaf aroma, deep wine rose, hex 8C3848"
                        )
                    )
                ),
                FlavorSubcategory(
                    id = "sub_floral_notes",
                    categoryId = "cat_floral",
                    name = "Floral Notes",
                    descriptors = listOf(
                        FlavorDescriptor(
                            "desc_jasmine",
                            "Jasmine / Melati",
                            "cat_floral",
                            "sub_floral_notes",
                            "#F4A3BE",
                            "Delicate perfume, ethereal white blossom, Geisha signature",
                            listOf("melati"),
                            "Jasmine: sweet delicate white flower blossom aroma, soft blossom pink, hex F4A3BE"
                        ),
                        FlavorDescriptor(
                            "desc_chamomile",
                            "Chamomile",
                            "cat_floral",
                            "sub_floral_notes",
                            "#FCD1DC",
                            "Soft, sweet, soothing herbal flower with mild dried apple tone",
                            emptyList(),
                            "Chamomile: soft herbal floral sweetness, pale petal pink, hex FCD1DC"
                        ),
                        FlavorDescriptor(
                            "desc_rose",
                            "Rose / Mawar",
                            "cat_floral",
                            "sub_floral_notes",
                            "#E85A84",
                            "Sweet elegant perfumed red petal aromatics",
                            listOf("mawar"),
                            "Rose: sweet floral petal aroma, vibrant rose pink, hex E85A84"
                        ),
                        FlavorDescriptor(
                            "desc_coffee_blossom",
                            "Coffee Blossom / Bunga Kopi",
                            "cat_floral",
                            "sub_floral_notes",
                            "#F8C8D8",
                            "Fragrant sweet honeyed white flower, blend of jasmine and orange flower",
                            listOf("bunga kopi"),
                            "Coffee Blossom: sweet fragrant coffee tree flower aroma, pale pastel pink, hex F8C8D8"
                        ),
                        FlavorDescriptor(
                            "desc_orange_blossom",
                            "Orange Blossom / Neroli",
                            "cat_floral",
                            "sub_floral_notes",
                            "#F7B2BD",
                            "Citrusy floral, fresh white blossom with subtle green background",
                            listOf("neroli"),
                            "Orange Blossom: zesty sweet white orange flower aroma, blush pink, hex F7B2BD"
                        )
                    )
                )
            )
        ),

        // 2. FRUITY
        FlavorCategory(
            id = "cat_fruity",
            name = "Fruity",
            colorHex = "#E03E3E",
            subcategories = listOf(
                FlavorSubcategory(
                    id = "sub_berry",
                    categoryId = "cat_fruity",
                    name = "Berry",
                    descriptors = listOf(
                        FlavorDescriptor(
                            "desc_blackberry",
                            "Blackberry",
                            "cat_fruity",
                            "sub_berry",
                            "#5C1D48",
                            "Rich dark berry sweetness with mild tartness",
                            listOf("bramble"),
                            "Blackberry: deep jammy dark berry flavor, deep plum purple, hex 5C1D48"
                        ),
                        FlavorDescriptor(
                            "desc_raspberry",
                            "Raspberry",
                            "cat_fruity",
                            "sub_berry",
                            "#C2185B",
                            "Bright tart berry with floral lift and crisp ester pop",
                            listOf("framboise"),
                            "Raspberry: bright tangy red berry note, vivid berry crimson, hex C2185B"
                        ),
                        FlavorDescriptor(
                            "desc_blueberry",
                            "Blueberry",
                            "cat_fruity",
                            "sub_berry",
                            "#2C3E80",
                            "Sweet ripe blueberry aroma with purple grape skin note, natural Ethiopia hallmark",
                            emptyList(),
                            "Blueberry: sweet ripe wild blueberry aroma, dark indigo blue, hex 2C3E80"
                        ),
                        FlavorDescriptor(
                            "desc_strawberry",
                            "Strawberry",
                            "cat_fruity",
                            "sub_berry",
                            "#E91E63",
                            "Juicy ripe red strawberry sweetness with fresh acidity",
                            emptyList(),
                            "Strawberry: juicy fresh red strawberry sweetness, bright magenta red, hex E91E63"
                        )
                    )
                ),
                FlavorSubcategory(
                    id = "sub_dried_fruit",
                    categoryId = "cat_fruity",
                    name = "Dried Fruit",
                    descriptors = listOf(
                        FlavorDescriptor(
                            "desc_raisin",
                            "Raisin / Kismis",
                            "cat_fruity",
                            "sub_dried_fruit",
                            "#7B2D26",
                            "Concentrated sweet dark dried grape with molasses finish",
                            listOf("kismis"),
                            "Raisin: concentrated sweet dark dried fruit flavor, rich brown maroon, hex 7B2D26"
                        ),
                        FlavorDescriptor(
                            "desc_prune",
                            "Prune / Dried Plum",
                            "cat_fruity",
                            "sub_dried_fruit",
                            "#5A2A38",
                            "Heavy, sweet, dark stewed plum with fruit-skin tannin",
                            emptyList(),
                            "Prune: rich stewed dark plum note, dark prune purple, hex 5A2A38"
                        ),
                        FlavorDescriptor(
                            "desc_fig",
                            "Fig / Buah Tin",
                            "cat_fruity",
                            "sub_dried_fruit",
                            "#873D48",
                            "Earthy-sweet syrupy dried fig with mild grainy texture",
                            listOf("buah tin"),
                            "Fig: earthy sweet dried fig note, muted wine brown, hex 873D48"
                        ),
                        FlavorDescriptor(
                            "desc_date",
                            "Date / Kurma",
                            "cat_fruity",
                            "sub_dried_fruit",
                            "#6E2C1E",
                            "Dense caramelized honeyed fruit sweetness, sticky sucrose body",
                            listOf("kurma"),
                            "Date: dense caramelized honeyed fruit sweetness, deep date brown, hex 6E2C1E"
                        )
                    )
                ),
                FlavorSubcategory(
                    id = "sub_citrus",
                    categoryId = "cat_fruity",
                    name = "Citrus Fruit",
                    descriptors = listOf(
                        FlavorDescriptor(
                            "desc_lemon",
                            "Lemon",
                            "cat_fruity",
                            "sub_citrus",
                            "#F6D155",
                            "Crisp sharp citric acid sensation with yellow peel oils",
                            listOf("citrus"),
                            "Lemon: sharp tangy yellow citrus acidity, golden lemon yellow, hex F6D155"
                        ),
                        FlavorDescriptor(
                            "desc_lime",
                            "Lime / Jeruk Nipis",
                            "cat_fruity",
                            "sub_citrus",
                            "#8CBF3F",
                            "Zesty high-tone green citrus, brisk and mouth-cleansing",
                            listOf("jeruk nipis"),
                            "Lime: sharp green citrus punch, vibrant lime green, hex 8CBF3F"
                        ),
                        FlavorDescriptor(
                            "desc_grapefruit",
                            "Grapefruit",
                            "cat_fruity",
                            "sub_citrus",
                            "#FB8C00",
                            "Tart citrus with pleasant bitter quinine-like tonic bite",
                            listOf("pamplemousse"),
                            "Grapefruit: vibrant citrus note with gentle pleasant bitterness, coral orange, hex FB8C00"
                        ),
                        FlavorDescriptor(
                            "desc_orange",
                            "Orange / Jeruk Keprok",
                            "cat_fruity",
                            "sub_citrus",
                            "#FF9F1C",
                            "Round sweet citric sweetness, juicy mandarin and tangerine juice",
                            listOf("mandarin", "tangerine"),
                            "Orange: sweet balanced round orange juice note, bright citrus orange, hex FF9F1C"
                        )
                    )
                ),
                FlavorSubcategory(
                    id = "sub_stone_fruit",
                    categoryId = "cat_fruity",
                    name = "Stone Fruit",
                    descriptors = listOf(
                        FlavorDescriptor(
                            "desc_peach",
                            "Peach",
                            "cat_fruity",
                            "sub_stone_fruit",
                            "#FFAAA6",
                            "Velvety sweet delicate stone fruit with floral aroma",
                            listOf("white peach"),
                            "Peach: delicate sweet velvety peach stone fruit note, soft peach pink, hex FFAAA6"
                        ),
                        FlavorDescriptor(
                            "desc_apricot",
                            "Apricot",
                            "cat_fruity",
                            "sub_stone_fruit",
                            "#FFB366",
                            "Tangy stone fruit with malic backbone and amber sweetness",
                            emptyList(),
                            "Apricot: bright tangy yellow stone fruit flavor, warm apricot amber, hex FFB366"
                        ),
                        FlavorDescriptor(
                            "desc_cherry",
                            "Cherry",
                            "cat_fruity",
                            "sub_stone_fruit",
                            "#B22222",
                            "Red cherry fruitiness with sweet-tart finish and mild benzaldehyde",
                            listOf("black cherry"),
                            "Cherry: sweet tart red cherry fruit note, deep crimson cherry red, hex B22222"
                        ),
                        FlavorDescriptor(
                            "desc_plum",
                            "Plum",
                            "cat_fruity",
                            "sub_stone_fruit",
                            "#701C38",
                            "Juicy sweet and tart dark purple stone fruit flesh",
                            emptyList(),
                            "Plum: juicy sweet and tart dark plum flavor, deep plum maroon, hex 701C38"
                        )
                    )
                ),
                FlavorSubcategory(
                    id = "sub_tropical",
                    categoryId = "cat_fruity",
                    name = "Tropical Fruit",
                    descriptors = listOf(
                        FlavorDescriptor(
                            "desc_mango",
                            "Mango / Mangga Gedong",
                            "cat_fruity",
                            "sub_tropical",
                            "#FFB703",
                            "Rich resinous tropical fruit sweetness, ripe juicy mango",
                            listOf("mangga"),
                            "Mango: lush tropical yellow mango sweetness, vibrant mango gold, hex FFB703"
                        ),
                        FlavorDescriptor(
                            "desc_passion_fruit",
                            "Passion Fruit / Markisa",
                            "cat_fruity",
                            "sub_tropical",
                            "#A84268",
                            "Intense tart tropical punch, high malic and citric aroma",
                            listOf("markisa"),
                            "Passion Fruit: intense tart tropical aroma, vivid passion fruit magenta, hex A84268"
                        ),
                        FlavorDescriptor(
                            "desc_pineapple",
                            "Pineapple / Nanas",
                            "cat_fruity",
                            "sub_tropical",
                            "#F9C74F",
                            "Sharp acidic tropical sweetness with vibrant bromelain pop",
                            listOf("nanas"),
                            "Pineapple: sharp sweet tropical pineapple tang, bright pineapple yellow, hex F9C74F"
                        ),
                        FlavorDescriptor(
                            "desc_banana",
                            "Banana / Pisang Raja",
                            "cat_fruity",
                            "sub_tropical",
                            "#FFE066",
                            "Creamy sweet ripe banana flesh with isoamyl acetate note",
                            listOf("pisang"),
                            "Banana: creamy sweet ripe banana note, soft banana yellow, hex FFE066"
                        ),
                        FlavorDescriptor(
                            "desc_jackfruit",
                            "Jackfruit / Nangka",
                            "cat_fruity",
                            "sub_tropical",
                            "#E79C24",
                            "Pungent sweet honeyed tropical jackfruit note, Indonesian specialty hallmark",
                            listOf("nangka"),
                            "Jackfruit: pungent sweet honeyed tropical jackfruit note, warm amber gold, hex E79C24"
                        )
                    )
                )
            )
        ),

        // 3. SOUR / FERMENTED
        FlavorCategory(
            id = "cat_fermented",
            name = "Sour / Fermented",
            colorHex = "#C8B936",
            subcategories = listOf(
                FlavorSubcategory(
                    id = "sub_sour_acidity",
                    categoryId = "cat_fermented",
                    name = "Sour & Acidity",
                    descriptors = listOf(
                        FlavorDescriptor(
                            "desc_citric_acid",
                            "Citric Acid",
                            "cat_fermented",
                            "sub_sour_acidity",
                            "#D4CE46",
                            "Crisp sharp lateral tongue stimulation, refreshing lemon bite",
                            listOf("citric"),
                            "Citric Acid: crisp sharp refreshing citrus-like acidity, bright chartreuse yellow, hex D4CE46"
                        ),
                        FlavorDescriptor(
                            "desc_malic_acid",
                            "Malic Acid",
                            "cat_fermented",
                            "sub_sour_acidity",
                            "#A4C639",
                            "Crisp green apple style lingering tartness centered on mid-palate",
                            listOf("malic"),
                            "Malic Acid: crisp green apple style lingering acidity, apple green, hex A4C639"
                        ),
                        FlavorDescriptor(
                            "desc_acetic_acid",
                            "Acetic Acid / Cuka",
                            "cat_fermented",
                            "sub_sour_acidity",
                            "#8B9B28",
                            "Sharp pungent vinegar volatile acidity with drying throat sensation",
                            listOf("vinegar", "cuka"),
                            "Acetic Acid: sharp pungent vinegar volatile acidity, olive yellow green, hex 8B9B28"
                        ),
                        FlavorDescriptor(
                            "desc_phosphoric_acid",
                            "Phosphoric Acid",
                            "cat_fermented",
                            "sub_sour_acidity",
                            "#C2C84A",
                            "Sparkling mineral cola-like tactile bite, Kenya coffee hallmark",
                            emptyList(),
                            "Phosphoric Acid: sparkling mineral cola-like tactile acidity, crisp lime olive, hex C2C84A"
                        ),
                        FlavorDescriptor(
                            "desc_tartaric_acid",
                            "Tartaric Acid",
                            "cat_fermented",
                            "sub_sour_acidity",
                            "#96A136",
                            "Hard dry astringent green grape tartness with dry chalky finish",
                            emptyList(),
                            "Tartaric Acid: dry hard green grape acidity with chalky finish, sage olive, hex 96A136"
                        )
                    )
                ),
                FlavorSubcategory(
                    id = "sub_alcohol_ferment",
                    categoryId = "cat_fermented",
                    name = "Alcohol & Ferment",
                    descriptors = listOf(
                        FlavorDescriptor(
                            "desc_red_wine",
                            "Red Wine",
                            "cat_fermented",
                            "sub_alcohol_ferment",
                            "#722F37",
                            "Tannic vinous red grape fermented acidity with deep body",
                            listOf("vinous"),
                            "Red Wine: rich fermented grape and wine tannin depth, deep wine red, hex 722F37"
                        ),
                        FlavorDescriptor(
                            "desc_whiskey",
                            "Whiskey / Bourbon",
                            "cat_fermented",
                            "sub_alcohol_ferment",
                            "#A65B2A",
                            "Oak aged spirit warmth with vanilla esters and malt alcohol complexity",
                            emptyList(),
                            "Whiskey: oak barrel vanilla and spirit warmth, warm whiskey amber, hex A65B2A"
                        ),
                        FlavorDescriptor(
                            "desc_kombucha",
                            "Kombucha / Cider",
                            "cat_fermented",
                            "sub_alcohol_ferment",
                            "#94486E",
                            "Funky probiotic acetic/lactic effervescent cider tang",
                            emptyList(),
                            "Kombucha: tart probiotic fermented tea effervescence, muted berry purple, hex 94486E"
                        ),
                        FlavorDescriptor(
                            "desc_tape_singkong",
                            "Tape Singkong / Fermented Cassava",
                            "cat_fermented",
                            "sub_alcohol_ferment",
                            "#8C3B56",
                            "Sweet boozy yeast-fermented delicacy note with lactic sweetness",
                            listOf("tape"),
                            "Tape Singkong: sweet boozy yeast-fermented delicacy note, wine mauve, hex 8C3B56"
                        ),
                        FlavorDescriptor(
                            "desc_rum",
                            "Rum / Molasses Ferment",
                            "cat_fermented",
                            "sub_alcohol_ferment",
                            "#682845",
                            "Heavy dark sugarcane spirit and molasses ester warmth",
                            emptyList(),
                            "Rum: heavy dark sugarcane spirit and molasses ester note, deep garnet plum, hex 682845"
                        )
                    )
                )
            )
        ),

        // 4. GREEN / VEGETATIVE
        FlavorCategory(
            id = "cat_green",
            name = "Green / Vegetative",
            colorHex = "#2F8745",
            subcategories = listOf(
                FlavorSubcategory(
                    id = "sub_fresh_green",
                    categoryId = "cat_green",
                    name = "Fresh Green",
                    descriptors = listOf(
                        FlavorDescriptor(
                            "desc_grassy",
                            "Grassy / Cut Grass",
                            "cat_green",
                            "sub_fresh_green",
                            "#66BB6A",
                            "Fresh crushed green grass lawn aroma, cis-3-hexenol note",
                            emptyList(),
                            "Grassy: fresh crushed green grass lawn aroma, bright meadow green, hex 66BB6A"
                        ),
                        FlavorDescriptor(
                            "desc_green_tea",
                            "Green Tea / Sencha",
                            "cat_green",
                            "sub_fresh_green",
                            "#81C784",
                            "Fresh vegetal steamed sencha leaf with clean clarity",
                            emptyList(),
                            "Green Tea: steamed green tea leaf and clean vegetal clarity, soft leaf green, hex 81C784"
                        ),
                        FlavorDescriptor(
                            "desc_pandan",
                            "Pandan Leaf / Daun Pandan",
                            "cat_green",
                            "sub_fresh_green",
                            "#388E3C",
                            "Sweet fragrant aromatic green pandan leaf scent, SE Asian specialty note",
                            listOf("daun pandan"),
                            "Pandan: sweet fragrant aromatic green pandan leaf scent, forest green, hex 388E3C"
                        ),
                        FlavorDescriptor(
                            "desc_lemongrass",
                            "Lemongrass / Serai",
                            "cat_green",
                            "sub_fresh_green",
                            "#A5D6A7",
                            "Crisp herbal citrus stalk aroma with citral lift",
                            listOf("serai"),
                            "Lemongrass: crisp herbal citrus stalk aroma, pale herb green, hex A5D6A7"
                        )
                    )
                ),
                FlavorSubcategory(
                    id = "sub_vegetative",
                    categoryId = "cat_green",
                    name = "Vegetative & Beany",
                    descriptors = listOf(
                        FlavorDescriptor(
                            "desc_peas",
                            "Green Pea / Snap Pea",
                            "cat_green",
                            "sub_vegetative",
                            "#689F38",
                            "Fresh green pea pod vegetal taste, mild under-developed roast indicator",
                            emptyList(),
                            "Green Pea: fresh green pea pod vegetal taste, light olive green, hex 689F38"
                        ),
                        FlavorDescriptor(
                            "desc_olive_oil",
                            "Olive Oil",
                            "cat_green",
                            "sub_vegetative",
                            "#827717",
                            "Rich green olive oil vegetal smoothness with subtle peppery finish",
                            emptyList(),
                            "Olive Oil: rich green olive oil vegetal smoothness, olive gold green, hex 827717"
                        ),
                        FlavorDescriptor(
                            "desc_raw_legume",
                            "Raw Legume / Raw Bean",
                            "cat_green",
                            "sub_vegetative",
                            "#558B2F",
                            "Starchy raw green bean and legume aroma",
                            emptyList(),
                            "Raw Legume: starchy raw green bean and legume aroma, earthy olive green, hex 558B2F"
                        ),
                        FlavorDescriptor(
                            "desc_hay",
                            "Hay / Jerami",
                            "cat_green",
                            "sub_vegetative",
                            "#9E9D24",
                            "Dry sun-cured straw and dried agricultural grass note",
                            listOf("jerami"),
                            "Hay: dry sun-cured straw and dried grass note, dry khaki yellow green, hex 9E9D24"
                        )
                    )
                ),
                FlavorSubcategory(
                    id = "sub_herbaceous",
                    categoryId = "cat_green",
                    name = "Herbaceous",
                    descriptors = listOf(
                        FlavorDescriptor(
                            "desc_mint",
                            "Mint / Peppermint",
                            "cat_green",
                            "sub_herbaceous",
                            "#26A69A",
                            "Cool refreshing herbal menthol sensation on palate",
                            emptyList(),
                            "Mint: cool refreshing herbal menthol sensation, seafoam mint green, hex 26A69A"
                        ),
                        FlavorDescriptor(
                            "desc_thyme",
                            "Thyme / Rosemary",
                            "cat_green",
                            "sub_herbaceous",
                            "#2E7D32",
                            "Resinous culinary herb with savory herbal camphor undertones",
                            emptyList(),
                            "Thyme: resinous savory culinary herb scent, deep herbal green, hex 2E7D32"
                        ),
                        FlavorDescriptor(
                            "desc_daun_salam",
                            "Daun Salam / Indonesian Bay",
                            "cat_green",
                            "sub_herbaceous",
                            "#1E4D2B",
                            "Woodsy warm herbal leaf fragrance used in Indonesian bases",
                            listOf("salam"),
                            "Daun Salam: warm woody aromatic bay leaf note, dark forest green, hex 1E4D2B"
                        ),
                        FlavorDescriptor(
                            "desc_eucalyptus",
                            "Eucalyptus",
                            "cat_green",
                            "sub_herbaceous",
                            "#00695C",
                            "Pungent cooling camphoric herbal note, clean medicinal breeze",
                            emptyList(),
                            "Eucalyptus: pungent cooling camphoric herbal note, deep teal green, hex 00695C"
                        )
                    )
                )
            )
        ),

        // 5. ROASTED
        FlavorCategory(
            id = "cat_roasted",
            name = "Roasted",
            colorHex = "#C7703A",
            subcategories = listOf(
                FlavorSubcategory(
                    id = "sub_cereal_grain",
                    categoryId = "cat_roasted",
                    name = "Cereal & Grain",
                    descriptors = listOf(
                        FlavorDescriptor(
                            "desc_grain",
                            "Grain / Raw Cereal",
                            "cat_roasted",
                            "sub_cereal_grain",
                            "#E2BA8A",
                            "Warm toasted wheat and cereal grain kernel aroma",
                            emptyList(),
                            "Grain: warm toasted wheat and cereal grain aroma, pale wheat tan, hex E2BA8A"
                        ),
                        FlavorDescriptor(
                            "desc_malt",
                            "Malt / Malted Barley",
                            "cat_roasted",
                            "sub_cereal_grain",
                            "#C68B59",
                            "Sweet germinated barley sugar, rich warm wort and malt",
                            emptyList(),
                            "Malt: rich sweet toasted malt cereal flavor, warm caramel malt tan, hex C68B59"
                        ),
                        FlavorDescriptor(
                            "desc_roasted_barley",
                            "Roasted Barley / Gandum Panggang",
                            "cat_roasted",
                            "sub_cereal_grain",
                            "#B0723E",
                            "Dark toasted barley with nutty roasted grain tea finish",
                            listOf("mugicha"),
                            "Roasted Barley: toasted grain and dark barley flavor, medium roast brown, hex B0723E"
                        ),
                        FlavorDescriptor(
                            "desc_toast",
                            "Toast / Roti Panggang",
                            "cat_roasted",
                            "sub_cereal_grain",
                            "#A06332",
                            "Golden toasted bread crust, Maillard pyrazine reaction hallmark",
                            emptyList(),
                            "Toast: golden brown toasted bread crust note, golden toast brown, hex A06332"
                        )
                    )
                ),
                FlavorSubcategory(
                    id = "sub_roast_notes",
                    categoryId = "cat_roasted",
                    name = "Roast & Burnt",
                    descriptors = listOf(
                        FlavorDescriptor(
                            "desc_brown_roast",
                            "Brown Roast / Crust",
                            "cat_roasted",
                            "sub_roast_notes",
                            "#7A3E1D",
                            "Deeply browned caramelization crust, dark roast coffee aroma",
                            emptyList(),
                            "Brown Roast: deep caramelized roasted bean crust flavor, dark roast brown, hex 7A3E1D"
                        ),
                        FlavorDescriptor(
                            "desc_smoky",
                            "Smoky / Asap Kayu",
                            "cat_roasted",
                            "sub_roast_notes",
                            "#4A4A4A",
                            "Wood smoke or charcoal roast aroma, hardwood campfire smoke",
                            emptyList(),
                            "Smoky: aromatic wood campfire smoke aroma, charcoal gray, hex 4A4A4A"
                        ),
                        FlavorDescriptor(
                            "desc_ashy",
                            "Ashy / Abu",
                            "cat_roasted",
                            "sub_roast_notes",
                            "#5A5A5A",
                            "Cold fireplace ash and dry mineral burnt residue",
                            emptyList(),
                            "Ashy: dry cold ash and burnt charcoal flavor, medium slate gray, hex 5A5A5A"
                        ),
                        FlavorDescriptor(
                            "desc_acrid",
                            "Acrid / Hangus",
                            "cat_roasted",
                            "sub_roast_notes",
                            "#382E2B",
                            "Sharp stinging carbonized edge, burnt organic matter",
                            emptyList(),
                            "Acrid: sharp bitter burnt carbon sensation, dark soot brown, hex 382E2B"
                        )
                    )
                ),
                FlavorSubcategory(
                    id = "sub_tobacco",
                    categoryId = "cat_roasted",
                    name = "Tobacco",
                    descriptors = listOf(
                        FlavorDescriptor(
                            "desc_pipe_tobacco",
                            "Pipe Tobacco",
                            "cat_roasted",
                            "sub_tobacco",
                            "#543825",
                            "Sweet aromatic cured pipe tobacco leaf with vanilla undertone",
                            emptyList(),
                            "Pipe Tobacco: sweet aromatic cured pipe tobacco aroma, deep tobacco brown, hex 543825"
                        ),
                        FlavorDescriptor(
                            "desc_tobacco",
                            "Cured Tobacco Leaf",
                            "cat_roasted",
                            "sub_tobacco",
                            "#7D5836",
                            "Cured dry tobacco leaf with earthy leathery undertones",
                            emptyList(),
                            "Cured Tobacco: dry earthy cured tobacco leaf scent, warm leather brown, hex 7D5836"
                        ),
                        FlavorDescriptor(
                            "desc_cigar_leaf",
                            "Cigar Leaf / Cerutu",
                            "cat_roasted",
                            "sub_tobacco",
                            "#482D1B",
                            "Pungent aged fermented cigar wrapper leaf with spicy wood",
                            listOf("cerutu"),
                            "Cigar Leaf: pungent aged cigar leaf with woody finish, dark espresso brown, hex 482D1B"
                        )
                    )
                )
            )
        ),

        // 6. SPICES
        FlavorCategory(
            id = "cat_spices",
            name = "Spices",
            colorHex = "#C6342E",
            subcategories = listOf(
                FlavorSubcategory(
                    id = "sub_brown_spice",
                    categoryId = "cat_spices",
                    name = "Brown Spice / Rempah Manis",
                    descriptors = listOf(
                        FlavorDescriptor(
                            "desc_cinnamon",
                            "Cinnamon / Kayu Manis",
                            "cat_spices",
                            "sub_brown_spice",
                            "#B24A32",
                            "Warm sweet woody cinnamaldehyde baking spice sweetness",
                            listOf("kayu manis"),
                            "Cinnamon: warm sweet baking spice aroma, warm terracotta cinnamon red, hex B24A32"
                        ),
                        FlavorDescriptor(
                            "desc_clove",
                            "Clove / Cengkeh",
                            "cat_spices",
                            "sub_brown_spice",
                            "#7B241C",
                            "Pungent sweet eugenol medicinal warmth, Indonesian signature",
                            listOf("cengkeh"),
                            "Clove: pungent sweet medicinal clove spice note, deep maroon red, hex 7B241C"
                        ),
                        FlavorDescriptor(
                            "desc_nutmeg",
                            "Nutmeg / Pala",
                            "cat_spices",
                            "sub_brown_spice",
                            "#8E3B27",
                            "Warm fragrant nutty seed spice with peppery edge",
                            listOf("pala"),
                            "Nutmeg: warm fragrant nutty seed spice flavor, rustic brick brown red, hex 8E3B27"
                        ),
                        FlavorDescriptor(
                            "desc_cardamom",
                            "Cardamom / Kapulaga",
                            "cat_spices",
                            "sub_brown_spice",
                            "#6E2F20",
                            "Pungent cooling herbal pine-camphor spice, cineole rich",
                            listOf("kapulaga"),
                            "Cardamom: cooling herbal citrus-pine spice aroma, deep earth crimson, hex 6E2F20"
                        ),
                        FlavorDescriptor(
                            "desc_star_anise",
                            "Star Anise / Bunga Lawang",
                            "cat_spices",
                            "sub_brown_spice",
                            "#581845",
                            "Intense sweet licorice, fennel-like anethole warm spice",
                            listOf("bunga lawang"),
                            "Star Anise: sweet pungent licorice fennel spice aroma, dark aubergine wine, hex 581845"
                        )
                    )
                ),
                FlavorSubcategory(
                    id = "sub_pungent_pepper",
                    categoryId = "cat_spices",
                    name = "Pungent & Pepper",
                    descriptors = listOf(
                        FlavorDescriptor(
                            "desc_black_pepper",
                            "Black Pepper / Lada Hitam",
                            "cat_spices",
                            "sub_pungent_pepper",
                            "#2B2B2B",
                            "Sharp pungent piperine bite, prickling back-palate sensation",
                            listOf("lada hitam"),
                            "Black Pepper: sharp pungent peppery piquancy, dark charcoal black, hex 2B2B2B"
                        ),
                        FlavorDescriptor(
                            "desc_white_pepper",
                            "White Pepper / Lada Putih",
                            "cat_spices",
                            "sub_pungent_pepper",
                            "#C0C0C0",
                            "Earthy sharp fermented pepper heat, Indonesian Muntok hallmark",
                            listOf("lada putih"),
                            "White Pepper: earthy sharp pungent white pepper heat, light silver gray, hex C0C0C0"
                        ),
                        FlavorDescriptor(
                            "desc_ginger",
                            "Ginger / Jahe",
                            "cat_spices",
                            "sub_pungent_pepper",
                            "#D4AC0D",
                            "Fiery warming zesty gingerol warmth and mouthfeel",
                            listOf("jahe"),
                            "Ginger: fiery warming zesty ginger root aroma, mustard ginger gold, hex D4AC0D"
                        ),
                        FlavorDescriptor(
                            "desc_chili",
                            "Chili / Capsicum",
                            "cat_spices",
                            "sub_pungent_pepper",
                            "#C0392B",
                            "Sharp pungent capsaicin heat sensation, tingling palate trigger",
                            listOf("cabai"),
                            "Chili: sharp pungent capsaicin pepper burn, vibrant pepper red, hex C0392B"
                        )
                    )
                )
            )
        ),

        // 7. NUTTY / COCOA
        FlavorCategory(
            id = "cat_nutty_cocoa",
            name = "Nutty / Cocoa",
            colorHex = "#8D5B3A",
            subcategories = listOf(
                FlavorSubcategory(
                    id = "sub_nuts",
                    categoryId = "cat_nutty_cocoa",
                    name = "Nuts",
                    descriptors = listOf(
                        FlavorDescriptor(
                            "desc_almond",
                            "Almond / Amandel",
                            "cat_nutty_cocoa",
                            "sub_nuts",
                            "#D4A373",
                            "Sweet nutty benzaldehyde character, delicate marzipan tone",
                            listOf("amandel"),
                            "Almond: sweet nutty marzipan tone, pale almond tan, hex D4A373"
                        ),
                        FlavorDescriptor(
                            "desc_hazelnut",
                            "Hazelnut",
                            "cat_nutty_cocoa",
                            "sub_nuts",
                            "#9E6938",
                            "Toasty rich nut sweetness, buttery filbertone aroma",
                            emptyList(),
                            "Hazelnut: toasty buttery rich roasted nut note, warm hazel brown, hex 9E6938"
                        ),
                        FlavorDescriptor(
                            "desc_peanut",
                            "Peanut / Kacang Tanah",
                            "cat_nutty_cocoa",
                            "sub_nuts",
                            "#C28D58",
                            "Roasted peanut skin and savory nut butter warmth",
                            listOf("kacang"),
                            "Peanut: roasted peanut skin and savory nut sweetness, warm peanut gold, hex C28D58"
                        ),
                        FlavorDescriptor(
                            "desc_walnut",
                            "Walnut / Kenari",
                            "cat_nutty_cocoa",
                            "sub_nuts",
                            "#7D4E27",
                            "Nutty with mild pleasant astringent skin note, rich woody oil",
                            listOf("kenari"),
                            "Walnut: rich woody nut flavor with dry clean finish, deep walnut brown, hex 7D4E27"
                        )
                    )
                ),
                FlavorSubcategory(
                    id = "sub_cocoa",
                    categoryId = "cat_nutty_cocoa",
                    name = "Cocoa & Chocolate",
                    descriptors = listOf(
                        FlavorDescriptor(
                            "desc_dark_chocolate",
                            "Dark Chocolate",
                            "cat_nutty_cocoa",
                            "sub_cocoa",
                            "#382212",
                            "Deep bittersweet roasted cacao nibs with cocoa tannins",
                            listOf("cacao"),
                            "Dark Chocolate: rich bittersweet dark cacao nib flavor, very dark chocolate brown, hex 382212"
                        ),
                        FlavorDescriptor(
                            "desc_milk_chocolate",
                            "Milk Chocolate",
                            "cat_nutty_cocoa",
                            "sub_cocoa",
                            "#70482B",
                            "Creamy sweet smooth chocolate with dairy sensation",
                            emptyList(),
                            "Milk Chocolate: creamy sweet smooth milk chocolate note, medium chocolate brown, hex 70482B"
                        ),
                        FlavorDescriptor(
                            "desc_cocoa_powder",
                            "Cocoa Powder / Cacao Nibs",
                            "cat_nutty_cocoa",
                            "sub_cocoa",
                            "#4A2B15",
                            "Dry powdery unsweetened pure Dutch cocoa aromatics",
                            emptyList(),
                            "Cocoa Powder: dry bittersweet unsweetened cocoa powder scent, rich dark cocoa brown, hex 4A2B15"
                        ),
                        FlavorDescriptor(
                            "desc_bakers_chocolate",
                            "Baker's Chocolate",
                            "cat_nutty_cocoa",
                            "sub_cocoa",
                            "#2C1A0E",
                            "Dense 100% unsweetened chocolate liquor roasted bitterness",
                            emptyList(),
                            "Baker's Chocolate: dense unsweetened pure chocolate liquor bitterness, deep espresso black brown, hex 2C1A0E"
                        )
                    )
                )
            )
        ),

        // 8. SWEET
        FlavorCategory(
            id = "cat_sweet",
            name = "Sweet",
            colorHex = "#E59837",
            subcategories = listOf(
                FlavorSubcategory(
                    id = "sub_sugars",
                    categoryId = "cat_sweet",
                    name = "Sugar & Syrup",
                    descriptors = listOf(
                        FlavorDescriptor(
                            "desc_cane_sugar",
                            "Cane Sugar / Sucrose",
                            "cat_sweet",
                            "sub_sugars",
                            "#F5C26B",
                            "Pure clean white cane sugar sweetness, front-tongue sensation",
                            emptyList(),
                            "Cane Sugar: pure clean white sugar sweetness, soft golden straw, hex F5C26B"
                        ),
                        FlavorDescriptor(
                            "desc_caramel",
                            "Caramel",
                            "cat_sweet",
                            "sub_sugars",
                            "#C86A1D",
                            "Rich cooked sugar and butter caramel sweetness with gentle toast",
                            emptyList(),
                            "Caramel: rich cooked sugar and butter caramel sweetness, warm caramel amber, hex C86A1D"
                        ),
                        FlavorDescriptor(
                            "desc_honey",
                            "Honey / Madu Hutan",
                            "cat_sweet",
                            "sub_sugars",
                            "#EAA228",
                            "Floral nectar sweetness with heavy viscosity, multi-dimensional bee honey",
                            listOf("madu"),
                            "Honey: viscous floral nectar sweetness, golden honey yellow, hex EAA228"
                        ),
                        FlavorDescriptor(
                            "desc_maple",
                            "Maple Syrup",
                            "cat_sweet",
                            "sub_sugars",
                            "#A6521A",
                            "Rich woody concentrated tree sap sweetness, sotolon aroma",
                            emptyList(),
                            "Maple Syrup: rich woody concentrated maple sap sweetness, deep maple amber, hex A6521A"
                        ),
                        FlavorDescriptor(
                            "desc_palm_sugar",
                            "Palm Sugar / Gula Aren",
                            "cat_sweet",
                            "sub_sugars",
                            "#7B3F00",
                            "Smoky, deep, molasses-tinted Indonesian palm nectar, rich coconut-sugar body",
                            listOf("gula aren", "gula jawa"),
                            "Gula Aren: deep smoky caramelized palm sugar sweetness, rich palm brown, hex 7B3F00"
                        )
                    )
                ),
                FlavorSubcategory(
                    id = "sub_vanilla_confectionery",
                    categoryId = "cat_sweet",
                    name = "Vanilla & Confectionery",
                    descriptors = listOf(
                        FlavorDescriptor(
                            "desc_vanilla",
                            "Vanilla Bean",
                            "cat_sweet",
                            "sub_vanilla_confectionery",
                            "#F0E2B6",
                            "Sweet creamy natural vanilla bean fragrance with woody balsamic background",
                            emptyList(),
                            "Vanilla: sweet creamy natural vanilla bean fragrance, cream custard ivory, hex F0E2B6"
                        ),
                        FlavorDescriptor(
                            "desc_brown_sugar",
                            "Brown Sugar",
                            "cat_sweet",
                            "sub_vanilla_confectionery",
                            "#B36B2B",
                            "Warm moist sugar crystals coated with gentle cane molasses",
                            emptyList(),
                            "Brown Sugar: warm moist sugar with gentle molasses flavor, light brown sugar amber, hex B36B2B"
                        ),
                        FlavorDescriptor(
                            "desc_molasses",
                            "Molasses / Tetes Tebu",
                            "cat_sweet",
                            "sub_vanilla_confectionery",
                            "#4A2311",
                            "Heavy bittersweet dense boiled cane molasses syrup with mineral depth",
                            emptyList(),
                            "Molasses: heavy bittersweet dense cane molasses flavor, dark molasses brown, hex 4A2311"
                        ),
                        FlavorDescriptor(
                            "desc_marshmallow",
                            "Marshmallow / Nougat",
                            "cat_sweet",
                            "sub_vanilla_confectionery",
                            "#F7E7CE",
                            "Airy sweet confectionery nougat sweetness and marshmallow fluff",
                            emptyList(),
                            "Marshmallow: airy sweet confectionery nougat sweetness, soft cream ecru, hex F7E7CE"
                        )
                    )
                )
            )
        ),

        // 9. DEFECTS & TAINTS (Critical for Professional Cupping QC)
        FlavorCategory(
            id = "cat_defects_taints",
            name = "Defects & Taints",
            colorHex = "#546E7A",
            subcategories = listOf(
                FlavorSubcategory(
                    id = "sub_chemical_phenolic",
                    categoryId = "cat_defects_taints",
                    name = "Chemical & Phenolic",
                    descriptors = listOf(
                        FlavorDescriptor(
                            "desc_defect_phenolic", "Phenolic / Carbolic", "cat_defects_taints", "sub_chemical_phenolic", "#263238", "Antiseptic hospital disinfectant TCP carbolic taste, guaiacol taint",
                            listOf(
                                "carbolic"
                            ),
                            "Phenolic defect: harsh antiseptic carbolic taste, dark blue slate, hex 263238", isDefect = true
                        ),
                        FlavorDescriptor(
                            "desc_defect_rubber", "Rubber / Karet Bakar", "cat_defects_taints", "sub_chemical_phenolic", "#212121", "Acrid burnt vulcanized rubber odor and sulfurous tire taste",
                            listOf(
                                "karet"
                            ),
                            "Rubber taint: acrid burnt vulcanized rubber odor, deep asphalt black, hex 212121", isDefect = true
                        ),
                        FlavorDescriptor(
                            "desc_defect_petroleum", "Petroleum / Minyak Tanah", "cat_defects_taints", "sub_chemical_phenolic", "#3E4A52", "Diesel exhaust, kerosene and machine lubricant oil contamination",
                            listOf(
                                "solar"
                            ),
                            "Petroleum taint: industrial fuel and kerosene solvent note, dark steel gray, hex 3E4A52", isDefect = true
                        ),
                        FlavorDescriptor("desc_defect_medicinal", "Medicinal / Bandaid", "cat_defects_taints", "sub_chemical_phenolic", "#455A64", "Sterile adhesive bandage and iodine disinfectant chlorophenol odor", emptyList(), "Medicinal defect: sterile bandage and iodine disinfectant odor, muted slate blue, hex 455A64", isDefect = true)
                    )
                ),
                FlavorSubcategory(
                    id = "sub_papery_stale",
                    categoryId = "cat_defects_taints",
                    name = "Papery, Stale & Baggy",
                    descriptors = listOf(
                        FlavorDescriptor(
                            "desc_defect_baggy", "Baggy / Karung Goni", "cat_defects_taints", "sub_papery_stale", "#8D7B68", "Dry aged jute burlap sacking and damp sisal fiber storage taint",
                            listOf(
                                "goni"
                            ),
                            "Baggy defect: dry aged jute burlap sack taste, muted burlap tan, hex 8D7B68", isDefect = true
                        ),
                        FlavorDescriptor("desc_defect_cardboard", "Cardboard / Papery", "cat_defects_taints", "sub_papery_stale", "#B0BEC5", "Wet corrugated cardboard, paper filter and stale oxidized lipid note", emptyList(), "Cardboard defect: stale oxidized wet paper note, light steel gray, hex B0BEC5", isDefect = true),
                        FlavorDescriptor("desc_defect_past_crop", "Past Crop / Stale", "cat_defects_taints", "sub_papery_stale", "#90A4AE", "Lifeless faded oxidized cellulose lacking sweetness, over-aged bean", emptyList(), "Past Crop defect: lifeless faded oxidized green coffee character, cool slate gray, hex 90A4AE", isDefect = true)
                    )
                ),
                FlavorSubcategory(
                    id = "sub_earthy_musty",
                    categoryId = "cat_defects_taints",
                    name = "Earthy & Musty",
                    descriptors = listOf(
                        FlavorDescriptor(
                            "desc_defect_earthy", "Earthy / Tanah Basah", "cat_defects_taints", "sub_earthy_musty", "#5D4037", "Heavy damp potting soil geosmin aroma, damp cellar mud",
                            listOf(
                                "tanah"
                            ),
                            "Earthy defect: heavy damp potting soil aroma, rich earth brown, hex 5D4037", isDefect = true
                        ),
                        FlavorDescriptor(
                            "desc_defect_musty", "Musty / Moldy / Apek", "cat_defects_taints", "sub_earthy_musty", "#4A5B4F", "Damp mildewed basement, fungal mold spores, 2-MIB taint",
                            listOf(
                                "apek"
                            ),
                            "Musty defect: damp moldy cellar fungal odor, dark moss gray, hex 4A5B4F", isDefect = true
                        ),
                        FlavorDescriptor("desc_defect_humus", "Humus / Forest Floor", "cat_defects_taints", "sub_earthy_musty", "#3E3D32", "Decaying damp forest mulch odor, rotting leaf swamp soil", emptyList(), "Humus taint: decaying damp forest mulch odor, deep swamp olive, hex 3E3D32", isDefect = true)
                    )
                ),
                FlavorSubcategory(
                    id = "sub_microbial_taints",
                    categoryId = "cat_defects_taints",
                    name = "Microbial & Physiological Taints",
                    descriptors = listOf(
                        FlavorDescriptor(
                            "desc_defect_potato", "Potato Defect (PTD)", "cat_defects_taints", "sub_microbial_taints", "#796B58", "Intense raw unpeeled potato skin odor from 2-IPMP Antestia bug damage",
                            listOf(
                                "ptd"
                            ),
                            "Potato defect: intense raw unpeeled potato skin odor, dusty potato tan, hex 796B58", isDefect = true
                        ),
                        FlavorDescriptor(
                            "desc_defect_stinker", "Ferment / Stinker Bean", "cat_defects_taints", "sub_microbial_taints", "#664638", "Foul rotting fruit pulp, butyric acid vomit, overfermentation rot",
                            listOf(
                                "stinker"
                            ),
                            "Stinker defect: foul overfermented rotten fruit pulp stench, rotten pulp brown, hex 664638", isDefect = true
                        ),
                        FlavorDescriptor("desc_defect_onion", "Onion / Alliaceous", "cat_defects_taints", "sub_microbial_taints", "#827055", "Rotting sulfurous onion, dimethyl disulfide processing taint", emptyList(), "Onion defect: rotten sulfurous onion taint, muted olive khaki, hex 827055", isDefect = true),
                        FlavorDescriptor("desc_defect_rioy", "Rioy / Harsh Iodine", "cat_defects_taints", "sub_microbial_taints", "#4A3B32", "Harsh biting medicinal iodine saltiness, Rio Minas defect", emptyList(), "Rioy defect: harsh biting medicinal iodine note, dark charred umber, hex 4A3B32", isDefect = true)
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
