package com.coffeecreator.app.core.sensory

import java.math.BigDecimal

// ============================================================================
// 1. Epistemological Origin & Provenance Badges (S2-054)
// ============================================================================

enum class EpistemologicalOrigin {
    HUMAN_OBSERVATION, // Empirical score directly logged by human evaluator
    AI_HEURISTIC_HYPOTHESIS, // Rule-based inference from origins/roast parameters
    CALIBRATED_PREDICTION // Statistical/ML prediction with confidence intervals
}

data class ProvenanceBadge(
    val origin: EpistemologicalOrigin,
    val authorOrModelId: String,
    val confidenceLower: BigDecimal? = null,
    val confidenceUpper: BigDecimal? = null,
    // e.g. 95%
    val confidenceLevelPercent: Int? = null,
    val methodDisclaimer: String,
    val timestamp: Long
)

// ============================================================================
// 2. Sensory Sliders, Attributes & Templates (S2-053, S2-055)
// ============================================================================

enum class SensoryScaleType {
    Q_SCALE_6_TO_10, // 6.00 to 10.00 in 0.25 increments
    ORDINAL_1_TO_10, // 1.0 to 10.0 continuous
    INTENSITY_0_TO_5, // 0 to 5 perceived intensity
    CHECKLIST_5_CUPS // 5 bowls (0 to 5 checked)
}

data class AttributeDefinition(
    val key: String,
    val label: String,
    val scaleType: SensoryScaleType,
    val minValue: BigDecimal,
    val maxValue: BigDecimal,
    val stepSize: BigDecimal,
    val weight: BigDecimal = BigDecimal.ONE,
    val isDefectEligible: Boolean = false
)

enum class CuppingTemplateType {
    SPECIALTY_SCA_EQUIVALENT,
    QUICK_BARISTA_DIAL_IN,
    PRODUCTION_QC,
    ESPRESSO_SENSORY
}

data class CuppingTemplate(
    val type: CuppingTemplateType,
    val title: String,
    val description: String,
    val attributes: List<AttributeDefinition>
)

object StandardCuppingTemplates {

    val SPECIALTY_STANDARD = CuppingTemplate(
        type = CuppingTemplateType.SPECIALTY_SCA_EQUIVALENT,
        title = "Specialty 100-Point Protocol",
        description = "Standard 10-attribute specialty evaluation protocol with 6.00-10.00 scale and defect deductions.",
        attributes = listOf(
            AttributeDefinition(
                "fragrance_aroma",
                "Fragrance / Aroma",
                SensoryScaleType.Q_SCALE_6_TO_10,
                BigDecimal("6.00"),
                BigDecimal("10.00"),
                BigDecimal("0.25")
            ),
            AttributeDefinition(
                "flavor",
                "Flavor",
                SensoryScaleType.Q_SCALE_6_TO_10,
                BigDecimal("6.00"),
                BigDecimal("10.00"),
                BigDecimal("0.25")
            ),
            AttributeDefinition(
                "aftertaste",
                "Aftertaste",
                SensoryScaleType.Q_SCALE_6_TO_10,
                BigDecimal("6.00"),
                BigDecimal("10.00"),
                BigDecimal("0.25")
            ),
            AttributeDefinition(
                "acidity",
                "Acidity",
                SensoryScaleType.Q_SCALE_6_TO_10,
                BigDecimal("6.00"),
                BigDecimal("10.00"),
                BigDecimal("0.25")
            ),
            AttributeDefinition(
                "body",
                "Body",
                SensoryScaleType.Q_SCALE_6_TO_10,
                BigDecimal("6.00"),
                BigDecimal("10.00"),
                BigDecimal("0.25")
            ),
            AttributeDefinition(
                "balance",
                "Balance",
                SensoryScaleType.Q_SCALE_6_TO_10,
                BigDecimal("6.00"),
                BigDecimal("10.00"),
                BigDecimal("0.25")
            ),
            AttributeDefinition(
                "uniformity",
                "Uniformity (5 Cups)",
                SensoryScaleType.Q_SCALE_6_TO_10,
                BigDecimal("6.00"),
                BigDecimal("10.00"),
                BigDecimal("0.25")
            ),
            AttributeDefinition(
                "clean_cup",
                "Clean Cup (5 Cups)",
                SensoryScaleType.Q_SCALE_6_TO_10,
                BigDecimal("6.00"),
                BigDecimal("10.00"),
                BigDecimal("0.25")
            ),
            AttributeDefinition(
                "sweetness",
                "Sweetness (5 Cups)",
                SensoryScaleType.Q_SCALE_6_TO_10,
                BigDecimal("6.00"),
                BigDecimal("10.00"),
                BigDecimal("0.25")
            ),
            AttributeDefinition(
                "overall",
                "Overall Cupper Score",
                SensoryScaleType.Q_SCALE_6_TO_10,
                BigDecimal("6.00"),
                BigDecimal("10.00"),
                BigDecimal("0.25")
            )
        )
    )

    val QUICK_BARISTA_DIAL_IN = CuppingTemplate(
        type = CuppingTemplateType.QUICK_BARISTA_DIAL_IN,
        title = "Barista Dial-In Fast Cupping",
        description = "Fast 6-parameter dial-in sensory sheet on a 1-10 continuous scale for cafe baristas.",
        attributes = listOf(
            AttributeDefinition(
                "sweetness",
                "Sweetness",
                SensoryScaleType.ORDINAL_1_TO_10,
                BigDecimal("1.0"),
                BigDecimal("10.0"),
                BigDecimal("0.5")
            ),
            AttributeDefinition(
                "acidity",
                "Acidity Clarity",
                SensoryScaleType.ORDINAL_1_TO_10,
                BigDecimal("1.0"),
                BigDecimal("10.0"),
                BigDecimal("0.5")
            ),
            AttributeDefinition(
                "bitterness",
                "Bitterness Balance",
                SensoryScaleType.ORDINAL_1_TO_10,
                BigDecimal("1.0"),
                BigDecimal("10.0"),
                BigDecimal("0.5")
            ),
            AttributeDefinition(
                "body",
                "Body & Mouthfeel",
                SensoryScaleType.ORDINAL_1_TO_10,
                BigDecimal("1.0"),
                BigDecimal("10.0"),
                BigDecimal("0.5")
            ),
            AttributeDefinition(
                "balance",
                "Extraction Balance",
                SensoryScaleType.ORDINAL_1_TO_10,
                BigDecimal("1.0"),
                BigDecimal("10.0"),
                BigDecimal("0.5")
            ),
            AttributeDefinition(
                "aftertaste",
                "Finish & Length",
                SensoryScaleType.ORDINAL_1_TO_10,
                BigDecimal("1.0"),
                BigDecimal("10.0"),
                BigDecimal("0.5")
            )
        )
    )

    val PRODUCTION_QC = CuppingTemplate(
        type = CuppingTemplateType.PRODUCTION_QC,
        title = "Roastery Production QC",
        description = "Production batch release protocol checking consistency, flavor profile, and taints.",
        attributes = listOf(
            AttributeDefinition(
                "profile_match",
                "Profile Fidelity",
                SensoryScaleType.ORDINAL_1_TO_10,
                BigDecimal("1.0"),
                BigDecimal("10.0"),
                BigDecimal("0.5")
            ),
            AttributeDefinition(
                "clean_cup",
                "Cleanliness & Defects",
                SensoryScaleType.ORDINAL_1_TO_10,
                BigDecimal("1.0"),
                BigDecimal("10.0"),
                BigDecimal("0.5")
            ),
            AttributeDefinition(
                "roast_development",
                "Roast Development",
                SensoryScaleType.ORDINAL_1_TO_10,
                BigDecimal("1.0"),
                BigDecimal("10.0"),
                BigDecimal("0.5")
            ),
            AttributeDefinition(
                "overall_commercial",
                "Release Approval Score",
                SensoryScaleType.ORDINAL_1_TO_10,
                BigDecimal("1.0"),
                BigDecimal("10.0"),
                BigDecimal("0.5")
            )
        )
    )

    val ESPRESSO_SENSORY = CuppingTemplate(
        type = CuppingTemplateType.ESPRESSO_SENSORY,
        title = "Espresso Extraction Sensory",
        description = "Specialized tactile and aromatic evaluation for straight espresso shots.",
        attributes = listOf(
            AttributeDefinition(
                "crema_texture",
                "Crema Persistence & Tactile",
                SensoryScaleType.ORDINAL_1_TO_10,
                BigDecimal("1.0"),
                BigDecimal("10.0"),
                BigDecimal("0.5")
            ),
            AttributeDefinition(
                "aroma_intensity",
                "Aroma Intensity",
                SensoryScaleType.ORDINAL_1_TO_10,
                BigDecimal("1.0"),
                BigDecimal("10.0"),
                BigDecimal("0.5")
            ),
            AttributeDefinition(
                "acidity_structure",
                "Acidity Structure",
                SensoryScaleType.ORDINAL_1_TO_10,
                BigDecimal("1.0"),
                BigDecimal("10.0"),
                BigDecimal("0.5")
            ),
            AttributeDefinition(
                "sweetness_core",
                "Sweetness Core",
                SensoryScaleType.ORDINAL_1_TO_10,
                BigDecimal("1.0"),
                BigDecimal("10.0"),
                BigDecimal("0.5")
            ),
            AttributeDefinition(
                "viscosity_mouthfeel",
                "Viscosity & Weight",
                SensoryScaleType.ORDINAL_1_TO_10,
                BigDecimal("1.0"),
                BigDecimal("10.0"),
                BigDecimal("0.5")
            ),
            AttributeDefinition(
                "finish_lingering",
                "Aftertaste & Lingering",
                SensoryScaleType.ORDINAL_1_TO_10,
                BigDecimal("1.0"),
                BigDecimal("10.0"),
                BigDecimal("0.5")
            )
        )
    )

    fun getTemplate(type: CuppingTemplateType): CuppingTemplate = when (type) {
        CuppingTemplateType.SPECIALTY_SCA_EQUIVALENT -> SPECIALTY_STANDARD
        CuppingTemplateType.QUICK_BARISTA_DIAL_IN -> QUICK_BARISTA_DIAL_IN
        CuppingTemplateType.PRODUCTION_QC -> PRODUCTION_QC
        CuppingTemplateType.ESPRESSO_SENSORY -> ESPRESSO_SENSORY
    }
}

// ============================================================================
// 3. Cupping Session, Evaluators & Masking (S2-056, S2-057, S2-058)
// ============================================================================

enum class EvaluatorRole {
    LEAD_CUPPER,
    Q_GRADER,
    GUEST_EVALUATOR,
    BARISTA
}

data class Evaluator(
    val id: String,
    val name: String,
    val role: EvaluatorRole
)

enum class MaskingMode {
    OPEN_LABEL, // Roaster, bean, process visible
    SINGLE_BLIND_EVALUATOR, // Evaluators see code; Lead sees true identity
    DOUBLE_BLIND_PANEL // All parties see code until controlled reveal
}

data class CuppingSample(
    val sampleId: String,
    // e.g. "Sample #A1", "Bowl #03"
    val blindCode: String,
    val actualBeanId: String?,
    val beanName: String,
    val roaster: String?,
    val origin: String?,
    val process: String?,
    val roastDateEpoch: Long?,
    val tablePosition: Int,
    val isRevealed: Boolean = false
)

enum class SessionStatus {
    DRAFT,
    OPEN,
    CLOSED,
    REOPENED
}

data class AuditEntry(
    val timestamp: Long,
    val editorId: String,
    val action: String,
    val previousStateSummary: String,
    val newStateSummary: String,
    val reason: String
)

data class DefectRecord(
    // 2 points deduction per cup
    val taintCupCount: Int = 0,
    // 4 points deduction per cup
    val faultCupCount: Int = 0,
    val defectDescription: String = ""
)

data class EvaluationSheet(
    val id: String,
    val sessionId: String,
    val sampleId: String,
    val evaluatorId: String,
    val origin: EpistemologicalOrigin = EpistemologicalOrigin.HUMAN_OBSERVATION,
    val attributeScores: Map<String, BigDecimal>,
    val descriptors: List<String>,
    val defects: DefectRecord = DefectRecord(),
    val totalScore: BigDecimal,
    val notes: String = "",
    val submittedAt: Long
)

data class CuppingSession(
    val id: String,
    val title: String,
    val location: String,
    val template: CuppingTemplate,
    val maskingMode: MaskingMode,
    val status: SessionStatus,
    val evaluators: List<Evaluator>,
    val samples: List<CuppingSample>,
    val sheets: List<EvaluationSheet>,
    val auditTrail: List<AuditEntry>,
    val createdAt: Long,
    val closedAt: Long? = null
)

// ============================================================================
// 4. Consensus Analytics & Reports (S2-059, S2-060)
// ============================================================================

data class AttributeConsensus(
    val attributeKey: String,
    val mean: BigDecimal,
    val median: BigDecimal,
    val stdDev: BigDecimal,
    // 0.0 to 1.0
    val agreementIndex: BigDecimal,
    val outlierEvaluatorIds: List<String>
)

data class DescriptorConcordance(
    val descriptor: String,
    val selectionCount: Int,
    // 0.0 to 1.0
    val selectionFrequency: BigDecimal,
    val isConsensus: Boolean
)

data class EvaluatorOverlapPair(
    val evaluatorAId: String,
    val evaluatorBId: String,
    val jaccardSimilarity: BigDecimal
)

data class ConsensusAnalytics(
    val sampleId: String,
    val blindCode: String,
    val finalConsensusScore: BigDecimal,
    val attributeConsensus: Map<String, AttributeConsensus>,
    val descriptorCloud: List<DescriptorConcordance>,
    val overlapMatrix: List<EvaluatorOverlapPair>,
    val overallPanelAgreementIndex: BigDecimal
)

data class QcReportReceipt(
    val sessionId: String,
    val generatedAt: Long,
    val sha256Fingerprint: String,
    val markdownReport: String,
    val csvData: String
)

// ============================================================================
// 5. Physical Green & Roasted QC (S2-061)
// ============================================================================

enum class GreenDefectType(val isPrimary: Boolean, val penaltyWeight: Int, val defectName: String) {
    FULL_BLACK(true, 1, "Full Black Bean"),
    FULL_SOUR(true, 1, "Full Sour Bean"),
    DRIED_POD(true, 1, "Dried Pod / Cherry"),
    LARGE_STONE_STICK(true, 1, "Large Foreign Object / Stone"),
    SEVERE_INSECT_DAMAGE(true, 1, "Severe Insect Hole Damage"),
    PARTIAL_BLACK(false, 3, "Partial Black Bean"),
    PARTIAL_SOUR(false, 3, "Partial Sour Bean"),
    PARCHMENT(false, 5, "Parchment Bean"),
    FLOATER(false, 5, "Floater Bean"),
    BROKEN_CHIPPED(false, 5, "Broken / Chipped Bean"),
    SLIGHT_INSECT_DAMAGE(false, 10, "Slight Insect Damage")
}

data class GreenCoffeeDefectEntry(
    val defectType: GreenDefectType,
    val rawCount: Int
)

data class PhysicalQcRecord(
    val id: String,
    val sampleId: String,
    // standard 350g green sample
    val sampleMassGrams: BigDecimal = BigDecimal("350.0"),
    // standard target 10.0% - 12.0%
    val moisturePercentage: BigDecimal,
    // standard target 0.50 - 0.65
    val waterActivityAw: BigDecimal,
    val bulkDensityGPerL: BigDecimal,
    // e.g. 18 -> 45.0%
    val screenDistribution: Map<Int, BigDecimal> = emptyMap(),
    val defects: List<GreenCoffeeDefectEntry> = emptyList(),
    val totalPrimaryDefects: Int,
    val totalSecondaryDefects: Int,
    val calculatedGrade: String,
    val roastedAgtronWholeBean: BigDecimal,
    val roastedAgtronGround: BigDecimal,
    // Ground - WholeBean
    val roastDeltaAgtron: BigDecimal
)

// ============================================================================
// 6. Roast-Rest Sensory Lifecycle (S2-062)
// ============================================================================

enum class DegassingPhase(val label: String, val sensoryCharacteristics: String) {
    TOO_FRESH_CO2_INTERFERENCE(
        "Fresh / Degassing",
        "High CO2 concentration, turbulent extraction, sharp carbonic acidity, muted aromatic nuances"
    ),
    PEAK_FLAVOR_WINDOW(
        "Peak Maturity",
        "Optimal degassing balance, maximum aromatic clarity, fully developed sweetness, velvety mouthfeel"
    ),
    STABLE_POST_PEAK("Post-Peak Stable", "Balanced and clean, gentle softening of high-tone volatile floral/fruity notes"),
    STALE_OXIDIZED("Stale / Oxidized", "Loss of aromatic vibrancy, dry cardboard, papery finish, oxidized lipid character")
}

data class RoastRestObservation(
    val id: String,
    val bagId: String,
    val roastDateEpoch: Long,
    val observationDateEpoch: Long,
    val daysPostRoast: Int,
    val cuppingScore: BigDecimal,
    val observedAroma: BigDecimal,
    val observedAcidity: BigDecimal,
    val observedSweetness: BigDecimal,
    val perceivedCarbonicAcid: Boolean,
    val qualitativeNotes: String,
    val observedPhase: DegassingPhase
)

data class RestCurvePrediction(
    val bagId: String,
    val estimatedPeakWindowStartDay: Int,
    val estimatedPeakWindowEndDay: Int,
    val expectedStalingDay: Int,
    val provenanceBadge: ProvenanceBadge
)
