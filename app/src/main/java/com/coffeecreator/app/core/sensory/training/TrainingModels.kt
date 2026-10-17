package com.coffeecreator.app.core.sensory.training

enum class TriangleSampleOrder(val presentation: String, val oddCupIndex: Int) {
    AAB("A - A - B", 2),
    ABA("A - B - A", 1),
    BAA("B - A - A", 0),
    BBA("B - B - A", 2),
    BAB("B - A - B", 1),
    ABB("A - B - B", 0)
}

data class TriangleTrial(
    val trialId: String,
    val trialIndex: Int,
    val referenceSampleName: String,
    val contrastSampleName: String,
    val assignedOrder: TriangleSampleOrder,
    val blindSampleCodes: List<String>, // Exactly 3 opaque codes e.g. ["219", "843", "501"]
    val submittedChoiceIndex: Int? = null, // 0, 1, or 2
    val confidenceRating: Int? = null, // 1 to 5
    val timestampEpochMs: Long? = null,
    val isEvaluated: Boolean = false,
    val isCorrect: Boolean = false
)

enum class BasicTaste(val labelEn: String, val labelId: String) {
    SWEET("Sweet (Sucrose)", "Manis (Sukrosa)"),
    SOUR("Sour (Citric/Malic)", "Asam (Sitrat/Malat)"),
    SALTY("Salty (Sodium Chloride)", "Asin (Natrium Klorida)"),
    BITTER("Bitter (Caffeine/Quinine)", "Pahit (Kafein/Kina)"),
    UMAMI("Umami (Monosodium Glutamate)", "Umami (Monosodium Glutamat)")
}

data class TasteReferenceConcentration(
    val taste: BasicTaste,
    val chemicalName: String,
    val chemicalFormula: String,
    val detectionThresholdGramsPerLiter: Double,
    val lowIntensityGramsPerLiter: Double,
    val mediumIntensityGramsPerLiter: Double,
    val highIntensityGramsPerLiter: Double,
    val solventWaterSpec: String = "Clean, demineralized or odorless filtered water (<30 ppm TDS)",
    val safeHandlingWarning: String = "Use food-grade FCC/USP ingredients only. Do not ingest concentrated dry reagents."
)

object StandardTasteReferences {
    val ALL_REFERENCES: List<TasteReferenceConcentration> = listOf(
        TasteReferenceConcentration(
            taste = BasicTaste.SWEET,
            chemicalName = "Pure Sucrose (Refined Cane Sugar)",
            chemicalFormula = "C12H22O11",
            detectionThresholdGramsPerLiter = 2.0,
            lowIntensityGramsPerLiter = 6.0,
            mediumIntensityGramsPerLiter = 12.0,
            highIntensityGramsPerLiter = 24.0,
            safeHandlingWarning = "Clean pure cane sugar. Discard prepared solution after 24 hours to prevent mold."
        ),
        TasteReferenceConcentration(
            taste = BasicTaste.SOUR,
            chemicalName = "Citric Acid Monohydrate (Food Grade FCC)",
            chemicalFormula = "C6H8O7·H2O",
            detectionThresholdGramsPerLiter = 0.15,
            lowIntensityGramsPerLiter = 0.35,
            mediumIntensityGramsPerLiter = 0.70,
            highIntensityGramsPerLiter = 1.40,
            safeHandlingWarning = "Food grade FCC only. Keep away from eyes. Do not swallow dry powder."
        ),
        TasteReferenceConcentration(
            taste = BasicTaste.SALTY,
            chemicalName = "Sodium Chloride (Non-Iodized Pure Salt)",
            chemicalFormula = "NaCl",
            detectionThresholdGramsPerLiter = 0.20,
            lowIntensityGramsPerLiter = 0.70,
            mediumIntensityGramsPerLiter = 1.50,
            highIntensityGramsPerLiter = 3.00,
            safeHandlingWarning = "Use non-iodized pure salt to avoid iodine odor interference."
        ),
        TasteReferenceConcentration(
            taste = BasicTaste.BITTER,
            chemicalName = "Anhydrous Caffeine (USP)",
            chemicalFormula = "C8H10N4O2",
            detectionThresholdGramsPerLiter = 0.05,
            lowIntensityGramsPerLiter = 0.15,
            mediumIntensityGramsPerLiter = 0.30,
            highIntensityGramsPerLiter = 0.60,
            safeHandlingWarning = "Weigh on an analytical milligram balance. Maximum daily tasting volume: 100 mL."
        ),
        TasteReferenceConcentration(
            taste = BasicTaste.UMAMI,
            chemicalName = "Monosodium L-Glutamate (Food Grade MSG)",
            chemicalFormula = "C5H8NO4Na·H2O",
            detectionThresholdGramsPerLiter = 0.10,
            lowIntensityGramsPerLiter = 0.30,
            mediumIntensityGramsPerLiter = 0.80,
            highIntensityGramsPerLiter = 1.80,
            safeHandlingWarning = "Food grade MSG. Pure umami savory sensation."
        )
    )
}

enum class RoastDefectCategory(val labelEn: String, val labelId: String) {
    UNDERDEVELOPED("Underdeveloped", "Kurang Matang / Mentah"),
    OVERDEVELOPED("Overdeveloped (Dark / Burnt)", "Terlalu Matang / Gosong"),
    BAKED("Baked Roast (Stalled RoR)", "Baked / Terpanggang Hambar"),
    SCORCHED("Scorched / Faced", "Scorched / Gosong Permukaan"),
    QUAKER("Quaker (Immature Bean)", "Quaker / Biji Mentah")
}

data class RoastDefectReference(
    val category: RoastDefectCategory,
    val visualAttributesEn: String,
    val visualAttributesId: String,
    val sensoryAttributesEn: String,
    val sensoryAttributesId: String,
    val roastingRootCauseEn: String,
    val roastingRootCauseId: String
)

object StandardRoastDefects {
    val ALL_DEFECTS: List<RoastDefectReference> = listOf(
        RoastDefectReference(
            category = RoastDefectCategory.UNDERDEVELOPED,
            visualAttributesEn = "Light tan center cut, tight wrinkled bean crease, dense hard fracture.",
            visualAttributesId = "Inti tengah berwarna cokelat muda pucat, lipatan biji berkerut, patahan keras.",
            sensoryAttributesEn = "Raw cereal, green hay, astringent peanut skin, sharp grassy sourness.",
            sensoryAttributesId = "Sereal mentah, jerami hijau, sepat kulit kacang tanah, asam rumput menusuk.",
            roastingRootCauseEn = "Insufficient heat penetration into the bean core before drop; development time ratio (DTR) too low (<12%).",
            roastingRootCauseId = "Penetrasi panas ke inti biji tidak cukup sebelum diturunkan; rasio waktu pengembangan (DTR) terlalu singkat (<12%)."
        ),
        RoastDefectReference(
            category = RoastDefectCategory.BAKED,
            visualAttributesEn = "Dull matte brown, loss of surface sheen, prolonged roast color without expansion.",
            visualAttributesId = "Warna cokelat kusam tanpa kilau, waktu sangrai terlalu lama tanpa pemuaian optimal.",
            sensoryAttributesEn = "Flat, cardboard, dry toast, total loss of sweet aromatics and lively acidity.",
            sensoryAttributesId = "Hambar datar, kardus, roti tawar kering, hilangnya keasaman buah dan aroma manis.",
            roastingRootCauseEn = "Stalled rate of rise (RoR flat or declining to 0) during Maillard or post-first crack phase.",
            roastingRootCauseId = "Laju kenaikan suhu (RoR) mendatar atau jatuh ke 0 selama fase Maillard atau setelah first crack."
        ),
        RoastDefectReference(
            category = RoastDefectCategory.SCORCHED,
            visualAttributesEn = "Dark blackened burn patches on the flat bean face or outer edges.",
            visualAttributesId = "Bercak hitam gosong pada permukaan pipih atau tepi luar biji.",
            sensoryAttributesEn = "Acrid wood smoke, ash, harsh charcoal bitterness with a drying harsh finish.",
            sensoryAttributesId = "Asap kayu menusuk, abu, pahit arang tajam dengan rasa kesat mengeringkan.",
            roastingRootCauseEn = "Charge temperature too high or drum rotation speed too slow, allowing beans to contact overheated metal.",
            roastingRootCauseId = "Suhu awal (charge temp) terlalu tinggi atau drum berputar terlalu lambat sehingga biji menempel pada logam panas."
        ),
        RoastDefectReference(
            category = RoastDefectCategory.QUAKER,
            visualAttributesEn = "Light pale yellow or straw-colored bean post-roast; fails to brown.",
            visualAttributesId = "Biji berwarna kuning pucat atau jerami setelah disangrai; gagal menjadi cokelat.",
            sensoryAttributesEn = "Peanut shells, dry straw, popcorn husk, oily cereal paper.",
            sensoryAttributesId = "Kulit kacang tanah, jerami kering, kulit popcorn, kertas minyak.",
            roastingRootCauseEn = "Agronomic fault: immature fruit harvested with deficient reducing sugars, preventing the Maillard reaction.",
            roastingRootCauseId = "Cacat agronomi: buah mentah dipetik saat kadar gula pereduksi belum cukup, menggagalkan reaksi Maillard."
        )
    )
}
