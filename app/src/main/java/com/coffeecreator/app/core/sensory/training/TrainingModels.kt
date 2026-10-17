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

enum class TrainingSessionState { SETUP, IN_PROGRESS, SUBMITTED, EVALUATED, ARCHIVED }

data class TriangleSession(
    val sessionId: String,
    val sessionSeed: Long,
    val operatorPseudonym: String,
    val assessorPseudonym: String,
    val referenceCoffee: String,
    val contrastCoffee: String,
    val ambientTempCelsius: Double?,
    val waterTdsPpm: Double?,
    val washoutIntervalSeconds: Int,
    val trials: List<TriangleTrial>,
    val state: TrainingSessionState = TrainingSessionState.IN_PROGRESS,
    val createdAtEpochMs: Long,
    val completedAtEpochMs: Long? = null
)

data class AscendingConcentrationStep(
    val stepIndex: Int,
    val concentrationGramsPerLiter: Double,
    val blindCode: String,
    val isDistinguishableFromWater: Boolean? = null,
    val identifiedTaste: BasicTaste? = null,
    val isCorrectIdentification: Boolean? = null
)

data class TasteThresholdResult(
    val taste: BasicTaste,
    val detectionThresholdGramsPerLiter: Double?,
    val recognitionThresholdGramsPerLiter: Double?,
    val nonClinicalDisclaimer: String = "Educational threshold test only. Does not infer medical or clinical gustatory impairment."
)

enum class AromaFamily(val labelEn: String, val labelId: String) {
    ENZYMATIC("Enzymatic (Floral/Fruity/Herbal)", "Enzimatik (Bunga/Buah/Herbal)"),
    SUGAR_BROWNING("Sugar Browning (Nutty/Caramelly/Chocolaty)", "Sugar Browning (Kacang/Karamel/Cokelat)"),
    DRY_DISTILLATION("Dry Distillation (Spicy/Resinous/Pyrolytic)", "Distilasi Kering (Rempah/Resin/Sangrai Gelap)"),
    AROMATIC_TAINT("Aromatic Taints / Defects", "Cacat Aroma / Kontaminasi")
}

data class OlfactoryVialReference(
    val vialId: String,
    val family: AromaFamily,
    val descriptorNameEn: String,
    val descriptorNameId: String,
    val naturalFoodReferencesEn: List<String>,
    val naturalFoodReferencesId: List<String>,
    val userCustomVialLabel: String? = null
)

object StandardOlfactoryVials {
    val ALL_VIALS: List<OlfactoryVialReference> = listOf(
        OlfactoryVialReference(
            vialId = "enz_01",
            family = AromaFamily.ENZYMATIC,
            descriptorNameEn = "Coffee Blossom / Jasmine",
            descriptorNameId = "Bunga Kopi / Melati",
            naturalFoodReferencesEn = listOf("Fresh jasmine petals", "Orange blossom water", "Chamomile"),
            naturalFoodReferencesId = listOf("Kelopak melati segar", "Air bunga jeruk", "Kamomil")
        ),
        OlfactoryVialReference(
            vialId = "enz_02",
            family = AromaFamily.ENZYMATIC,
            descriptorNameEn = "Lemon / Citrus",
            descriptorNameId = "Lemon / Jeruk Sitrus",
            naturalFoodReferencesEn = listOf("Fresh lemon zest", "Lime leaf", "Citric peel"),
            naturalFoodReferencesId = listOf("Parutan kulit lemon", "Daun jeruk purut", "Kulit sitrus")
        ),
        OlfactoryVialReference(
            vialId = "enz_03",
            family = AromaFamily.ENZYMATIC,
            descriptorNameEn = "Stone Fruit / Peach",
            descriptorNameId = "Buah Persik / Aprikot",
            naturalFoodReferencesEn = listOf("Ripe yellow peach", "Apricot nectar", "Nectarine"),
            naturalFoodReferencesId = listOf("Persik kuning matang", "Nektar aprikot", "Nektarin")
        ),
        OlfactoryVialReference(
            vialId = "sb_01",
            family = AromaFamily.SUGAR_BROWNING,
            descriptorNameEn = "Roasted Hazelnut",
            descriptorNameId = "Kacang Hazelnut Panggang",
            naturalFoodReferencesEn = listOf("Roasted hazelnuts", "Praline paste", "Nut butter"),
            naturalFoodReferencesId = listOf("Kacang hazelnut sangrai", "Pasta praline", "Selai kacang")
        ),
        OlfactoryVialReference(
            vialId = "sb_02",
            family = AromaFamily.SUGAR_BROWNING,
            descriptorNameEn = "Dark Caramel / Toffee",
            descriptorNameId = "Karamel Gelap / Gula Bakar",
            naturalFoodReferencesEn = listOf("Browned butter caramel", "Toffee syrup", "Panela"),
            naturalFoodReferencesId = listOf("Karamel mentega kecokelatan", "Sirup toffee", "Gula aren / Panela")
        ),
        OlfactoryVialReference(
            vialId = "sb_03",
            family = AromaFamily.SUGAR_BROWNING,
            descriptorNameEn = "Dark Cocoa / Chocolate",
            descriptorNameId = "Cokelat Hitam / Kakao",
            naturalFoodReferencesEn = listOf("85% dark chocolate", "Roasted cacao nibs", "Dutch process cocoa"),
            naturalFoodReferencesId = listOf("Cokelat hitam 85%", "Biji kakao sangrai", "Bubuk kakao pekat")
        ),
        OlfactoryVialReference(
            vialId = "dd_01",
            family = AromaFamily.DRY_DISTILLATION,
            descriptorNameEn = "Clove / Warm Spice",
            descriptorNameId = "Cengkeh / Rempah Hangat",
            naturalFoodReferencesEn = listOf("Whole dried cloves", "Eugenol reference", "Allspice"),
            naturalFoodReferencesId = listOf("Cengkeh kering utuh", "Larutan eugenol", "Bunga pala")
        ),
        OlfactoryVialReference(
            vialId = "dd_02",
            family = AromaFamily.DRY_DISTILLATION,
            descriptorNameEn = "Cedar / Pipe Tobacco",
            descriptorNameId = "Kayu Cedar / Tembakau Pipa",
            naturalFoodReferencesEn = listOf("Fresh cedar wood shavings", "Aromatic cured tobacco leaf"),
            naturalFoodReferencesId = listOf("Serutan kayu cedar segar", "Daun tembakau kering beraroma")
        ),
        OlfactoryVialReference(
            vialId = "taint_01",
            family = AromaFamily.AROMATIC_TAINT,
            descriptorNameEn = "Earthy / Moldy / TCA",
            descriptorNameId = "Bumi Basah / Jamur / TCA",
            naturalFoodReferencesEn = listOf("Damp soil", "2,4,6-trichloroanisole reference", "Cellar must"),
            naturalFoodReferencesId = listOf("Tanah basah berjamur", "Bau apek gudang lembap")
        )
    )
}

data class AromaConfusionEntry(
    val targetVialId: String,
    val selectedVialId: String,
    val frequency: Int
)

data class AromaSpacedRepetitionCard(
    val vialId: String,
    val repetitionCount: Int = 0,
    val intervalDays: Double = 1.0,
    val easeFactor: Double = 2.5,
    val nextReviewEpochMs: Long,
    val totalAttempts: Int = 0,
    val correctAttempts: Int = 0
)

enum class OrganicAcidType(
    val chemicalFormula: String,
    val pKa1: Double,
    val sensoryProfileEn: String,
    val typicalOccurrenceEn: String,
    val referencePreparationGramsPerLiter: Double
) {
    CITRIC("C6H8O7", 3.13, "Bright, sharp, refreshing lemon-lime citrus", "Universal in fresh arabica; wet-processed lots", 0.50),
    MALIC("C4H6O5", 3.40, "Crisp, tart, lingering green apple stone fruit", "High-altitude microclimates and cold fermentation", 0.40),
    TARTARIC("C4H6O6", 2.98, "Hard, astringent, dry grape/winey sensation", "Natural process anaerobics, extended yeast ferment", 0.35),
    ACETIC("C2H4O2", 4.76, "Pungent, vinegar, sharp lifting sourness", "Over-fermentation, delayed drying, fruit vinegar taint", 0.25),
    PHOSPHORIC("H3PO4", 2.15, "Inorganic sparkling, tingling cola-like sensation", "East African volcanic soils (Kenya, Rwanda)", 0.20),
    QUINIC("C7H12O6", 3.46, "Dark, bitter-astringent, tonic sensation", "Roast degradation of chlorogenic acids", 0.30),
    LACTIC("C3H6O3", 3.86, "Milky, creamy, round velvety acidity", "Lactic anaerobic and yeast-inoculated fermentations", 0.45)
}

data class OrganicAcidTrial(
    val trialId: String,
    val acidTarget: OrganicAcidType,
    val baseBrewDescription: String,
    val doseGramsPerLiter: Double,
    val blindCode: String,
    val submittedPerceptionIndex: OrganicAcidType? = null,
    val supervisorPseudonym: String,
    val foodGradeVerified: Boolean
)

data class RoastDefectTrial(
    val trialId: String,
    val targetDefect: RoastDefectCategory,
    val controlCoffeeName: String,
    val defectiveCoffeeName: String,
    val blindCupCodes: List<String>,
    val submittedDefect: RoastDefectCategory? = null,
    val causalInferenceCaveat: String = "Sensory profile reflects multi-variable roast dynamics; does not prove single roast flaw without roaster curves."
)

enum class MouthfeelAttribute(val labelEn: String, val labelId: String) {
    BODY_WEIGHT("Body / Weight", "Bobot / Densitas"),
    TEXTURE_TYPE("Texture Smoothness", "Kehalusan Tekstur"),
    ASTRINGENCY("Astringency / Dryness", "Kelat / Kesat Kering")
}

data class TactileTrial(
    val trialId: String,
    val sampleABlindCode: String,
    val sampleBBlindCode: String,
    val targetAttribute: MouthfeelAttribute,
    val servingTempCelsius: Double,
    val waterSpecTdsPpm: Double,
    val submittedChoice: String? = null
)

enum class ComparativeDrillKind { PAIRED_COMPARISON_2AFC, RANKING_SERIES }

data class ComparativeTrial(
    val trialId: String,
    val drillKind: ComparativeDrillKind,
    val attributeUnderTest: String, // e.g. "Acidity Intensity" or "Sweetness"
    val blindSampleCodes: List<String>,
    val submittedOrder: List<String>? = null,
    val correctOrder: List<String>,
    val isEvaluated: Boolean = false,
    val isCorrect: Boolean = false
)

enum class SensorySkillPillar(val labelEn: String, val labelId: String) {
    TRIANGLE_DISCRIMINATION("Triangle Discrimination", "Diskriminasi Segitiga"),
    BASIC_TASTE_THRESHOLDS("Basic Taste Thresholds", "Ambang Rasa Dasar"),
    OLFACTORY_IDENTIFICATION("Olfactory Recognition", "Pengenalan Olfaktori"),
    ORGANIC_ACID_RECOGNITION("Organic Acid Analysis", "Analisis Asam Organik"),
    ROAST_DEFECT_DETECTION("Roast Defect Detection", "Deteksi Cacat Sangrai"),
    TACTILE_MOUTHFEEL("Tactile & Mouthfeel", "Taktil & Tekstur Mulut")
}

data class SkillReadinessMetric(
    val pillar: SensorySkillPillar,
    val completedPracticalTrials: Int,
    val accuracyPercentage: Double,
    val calibrationScore: Double, // 0.0 to 100.0
    val lastPracticedEpochMs: Long?
)

data class SensoryReadinessDashboard(
    val assessorPseudonym: String,
    val metrics: Map<SensorySkillPillar, SkillReadinessMetric>,
    val overallReadinessScore: Double,
    val legalDisclaimer: String = "Coffee Creator Sensory Lab provides self-directed practice only. Does NOT confer official SCA or CQI Q-Grader certification."
)

enum class PracticalVerificationType {
    PHYSICAL_TASTING_SELF_LOGGED,
    TRAINER_SUPERVISED_CO_SIGNED,
    PANEL_CALIBRATION_CROSS_CHECKED
}

data class PracticalExerciseVerification(
    val exerciseId: String,
    val verificationType: PracticalVerificationType,
    val physicalSamplePrepared: Boolean,
    val verifierPseudonym: String,
    val timestampEpochMs: Long,
    val isAutoPassed: Boolean = false // Invariant: Must ALWAYS be false
)
