package com.coffeecreator.app.core.sensory.academy

enum class AcademyCurriculumCategory(val labelEn: String, val labelId: String) {
    BOTANY_AND_AGRONOMY("Coffee Botany & Agronomy", "Botani & Agronomi Kopi"),
    PROCESSING_AND_FERMENTATION("Processing & Fermentation", "Pengolahan & Fermentasi"),
    SENSORY_PSYCHOPHYSICS("Sensory Science & Psychophysics", "Sains Sensori & Psikofisika"),
    PHYSIOLOGY_AND_ANATOMY("Sensory Anatomy & Physiology", "Anatomi & Fisiologi Sensori"),
    ORGANIC_ACIDS_CHEMISTRY("Organic Acid Chemistry", "Kimia Asam Organik"),
    AROMA_AND_VOLATILES("Aroma & Volatile Chemistry", "Aroma & Kimia Volatil"),
    STANDARDS_AND_PROTOCOLS("Cupping Protocols & Standards", "Protokol & Standar Cupping"),
    PHYSICAL_GREEN_GRADING("Green Coffee Physical Grading", "Grading Fisik Kopi Beras"),
    CALIBRATION_AND_PANEL_OPS("Calibration & Panel Management", "Kalibrasi & Manajemen Panel"),
    ETHICS_GOVERNANCE_ACCESSIBILITY("Ethics, Governance & Reporting", "Etika, Tata Kelola & Pelaporan")
}

data class GlossaryEntry(
    val termEn: String,
    val termId: String,
    val definitionEn: String,
    val definitionId: String
)

data class QuizOption(
    val id: String,
    val textEn: String,
    val textId: String,
    val isCorrect: Boolean
)

data class AcademyQuizQuestion(
    val id: String,
    val promptEn: String,
    val promptId: String,
    val options: List<QuizOption>,
    val explanationEn: String,
    val explanationId: String
)

data class AcademyCitation(
    val title: String,
    val authorOrOrganization: String,
    val year: Int,
    val sourceUrl: String
)

data class AcademyLesson(
    val id: String, // e.g. "EDU-01"
    val moduleNumber: Int, // 1 to 22
    val category: AcademyCurriculumCategory,
    val titleEn: String,
    val titleId: String,
    val estimatedDurationMinutes: Int,
    val objectivesEn: List<String>,
    val objectivesId: List<String>,
    val coreConceptsEn: List<String>,
    val coreConceptsId: List<String>,
    val contentMarkdownEn: String,
    val contentMarkdownId: String,
    val asciiDiagram: String? = null,
    val glossary: List<GlossaryEntry> = emptyList(),
    val practiceQuestions: List<AcademyQuizQuestion> = emptyList(),
    val accessibilityNotesEn: String,
    val accessibilityNotesId: String,
    val citations: List<AcademyCitation> = emptyList(),
    val version: String = "1.0.0"
)

data class OrganicAcidDefinition(
    val id: String,
    val nameEn: String,
    val nameId: String,
    val chemicalFormula: String,
    val molarMassGPerMol: Double,
    val pKa1: Double,
    val typicalSensoryCuesEn: String,
    val typicalSensoryCuesId: String,
    val occurrenceInCoffeeEn: String,
    val occurrenceInCoffeeId: String,
    val safeReferencePreparationNotesEn: String,
    val safeReferencePreparationNotesId: String,
    val standardTestingConcentrationGramsPerLiter: Double,
    val maxSafeDailyDosageThresholdGramsPerLiter: Double
)

data class AromaTaxonomyNode(
    val id: String,
    val nameEn: String,
    val nameId: String,
    val familyGroup: String, // Enzymatic, Sugar Browning, Dry Distillation, Taints
    val chemicalCompoundHint: String,
    val householdFoodReferenceEn: String,
    val householdFoodReferenceId: String,
    val userKitVialMappingId: String? = null // User's custom physical vial tag
)

object GenericAromaRegistry {
    val ALL_GENERIC_AROMAS: List<AromaTaxonomyNode> = listOf(
        AromaTaxonomyNode("aroma_01", "Coffee Blossom / Floral", "Bunga Kopi / Bunga Putih", "Enzymatic", "Linalool", "Jasmine flower, citrus blossom", "Bunga melati, bunga jeruk", "Vial #01"),
        AromaTaxonomyNode("aroma_02", "Lemon / Citrus", "Lemon / Jeruk Asam", "Enzymatic", "Limonene", "Fresh lemon peel, citric zest", "Kulit jeruk lemon segar", "Vial #02"),
        AromaTaxonomyNode("aroma_03", "Apple / Stone Fruit", "Apel / Buah Persik", "Enzymatic", "Hexyl acetate", "Fresh green apple, pear", "Apel hijau renyah, pir", "Vial #03"),
        AromaTaxonomyNode("aroma_04", "Blackcurrant / Berry", "Kismis Hitam / Buah Beri", "Enzymatic", "beta-Damascenone", "Blackcurrant, blackberry jam", "Buah kismis hitam, selai beri", "Vial #04"),
        AromaTaxonomyNode("aroma_05", "Honey / Sweet Nectar", "Madu / Nektar Bunga", "Sugar Browning", "Phenylacetic acid", "Raw honey, clover blossom", "Madu mentah alami", "Vial #05"),
        AromaTaxonomyNode("aroma_06", "Caramel / Brown Sugar", "Karamel / Gula Merah", "Sugar Browning", "Furaneol / Maltol", "Cooked brown sugar, toffee", "Gula karamel, gula aren", "Vial #06"),
        AromaTaxonomyNode("aroma_07", "Roasted Hazelnut", "Kacang Hazelnut Panggang", "Sugar Browning", "2-Methylbutanal", "Toasted hazelnuts, praline", "Kacang hazel panggang", "Vial #07"),
        AromaTaxonomyNode("aroma_08", "Dark Chocolate / Cacao", "Cokelat Hitam / Kakao", "Sugar Browning", "Pyrazines", "Pure unsweetened cocoa powder", "Bubuk kakao murni tanpa pemanis", "Vial #08"),
        AromaTaxonomyNode("aroma_09", "Cinnamon / Brown Spice", "Kayu Manis / Rempah", "Dry Distillation", "Cinnamaldehyde", "Ceylon cinnamon stick", "Batang kayu manis", "Vial #09"),
        AromaTaxonomyNode("aroma_10", "Clove / Warm Pungent", "Cengkeh / Rempah Hangat", "Dry Distillation", "Eugenol", "Dried clove buds", "Kuntum cengkeh kering", "Vial #10"),
        AromaTaxonomyNode("aroma_11", "Cedar / Resinous Wood", "Kayu Cedar / Damar", "Dry Distillation", "alpha-Pinene", "Aromatic cedar chest, pine resin", "Kayu cedar kering, getah pinus", "Vial #11"),
        AromaTaxonomyNode("aroma_12", "Pipe Tobacco / Smoky", "Tembakau Cerutu / Asap", "Dry Distillation", "Guaiacol / Pyridines", "Unlit pipe tobacco leaf", "Daun tembakau kering", "Vial #12"),
        AromaTaxonomyNode("aroma_13", "Earthy / Geosmin", "Tanah Lembap / Kompos", "Taints", "Geosmin", "Damp garden soil, wet clay", "Tanah basah sehabis hujan", "Vial #13"),
        AromaTaxonomyNode("aroma_14", "Potato Defect / Pyrazine", "Cacat Kentang (PTD)", "Taints", "2-Isopropyl-3-methoxypyrazine", "Raw unpeeled potato skin", "Kulit kentang mentah basah", "Vial #14"),
        AromaTaxonomyNode("aroma_15", "Phenolic / Medicinal", "Fenolik / Antiseptik", "Taints", "2-Ethylguaiacol", "Hospital bandage, iodized iodine", "Plester perban obat, iodin", "Vial #15"),
        AromaTaxonomyNode("aroma_16", "Baggy / Stale Jute", "Goni Usang / Kardus Apek", "Taints", "Hexanal oxidation", "Damp unwashed burlap sacks", "Karung goni lembap tua", "Vial #16")
    )
}

data class UserAcademyProgress(
    val lessonId: String,
    val isCompleted: Boolean = false,
    val quizScorePercent: Int = 0,
    val lastAccessedEpochMs: Long = System.currentTimeMillis()
)
