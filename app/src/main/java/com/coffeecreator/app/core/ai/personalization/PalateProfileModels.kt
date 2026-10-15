package com.coffeecreator.app.core.ai.personalization

import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate

// ============================================================================
// 1. Palate Quiz & Preference Profile Models (S2-063, S2-064)
// ============================================================================

enum class RoastPreference(val vectorValue: Double) {
    LIGHT(0.10),
    MEDIUM_LIGHT(0.30),
    MEDIUM(0.50),
    MEDIUM_DARK(0.75),
    DARK(0.95)
}

enum class AcidityTolerance(val vectorValue: Double) {
    LOW_SENSITIVE(0.15),
    MODERATE_BALANCED(0.50),
    HIGH_ENTHUSIAST(0.80),
    CITRIC_PHOSPHORIC_LOVER(0.95)
}

enum class SweetnessPriority(val vectorValue: Double) {
    SUBTLE(0.25),
    MODERATE(0.50),
    CORE_CRUCIAL(0.75),
    MAXIMUM_PURSUIT(1.00)
}

enum class BitternessSensitivity(val vectorValue: Double) {
    AVERSE(0.10),
    MODERATE_TOLERANCE(0.40),
    ENJOY_COMPLEX_BITTER(0.70),
    DARK_ITALIAN_STYLE(0.95)
}

enum class BodyPreference(val vectorValue: Double) {
    TEA_LIKE_DELICATE(0.15),
    SILKY_MEDIUM(0.50),
    SYRUPY_ROUND(0.75),
    HEAVY_CREAMY_FULL(0.95)
}

data class PalateQuizAnswers(
    val roast: RoastPreference = RoastPreference.MEDIUM_LIGHT,
    val acidity: AcidityTolerance = AcidityTolerance.HIGH_ENTHUSIAST,
    val sweetness: SweetnessPriority = SweetnessPriority.CORE_CRUCIAL,
    val bitterness: BitternessSensitivity = BitternessSensitivity.AVERSE,
    val body: BodyPreference = BodyPreference.SILKY_MEDIUM
)

data class PalateVector(
    val roast: Double,
    val acidity: Double,
    val sweetness: Double,
    val bitterness: Double,
    val body: Double
) {
    init {
        listOf(roast, acidity, sweetness, bitterness, body).forEach { v ->
            require(v in 0.0..1.0 && v.isFinite()) { "Vector coordinate must be bounded in [0.0, 1.0]" }
        }
    }

    fun toDoubleArray(): DoubleArray = doubleArrayOf(roast, acidity, sweetness, bitterness, body)
}

data class PalateProfile(
    val id: String = "default_palate",
    val vector: PalateVector = PalateVector(0.30, 0.80, 0.75, 0.20, 0.50),
    val cumulativeEvidenceWeight: Double = 5.0, // Prior beta_0
    val qualifiedSessionCount: Int = 0,
    val varianceVector: PalateVector = PalateVector(0.04, 0.04, 0.04, 0.04, 0.04),
    val isQuizCompleted: Boolean = false,
    val lastUpdatedEpochMs: Long = System.currentTimeMillis()
)

// ============================================================================
// 2. Bayesian Sensory Evidence Update Engine (S2-064)
// ============================================================================

data class PersonalSensoryEvidence(
    val roast: Double,
    val acidity: Double,
    val sweetness: Double,
    val bitterness: Double,
    val body: Double,
    val rating: Double, // Qualified sessions: >= 7.50
    val daysAgo: Double = 0.0
) {
    init {
        require(rating in 7.5..10.0) { "Only qualified sensory sessions (>= 7.50) update personal palate." }
    }
}

// ============================================================================
// 3. Explainable Inventory Recommendation Engine (S2-065)
// ============================================================================

data class BeanInventoryCandidate(
    val beanId: String,
    val beanName: String,
    val sensoryProfile: PalateVector,
    val roastDate: LocalDate,
    val availableMassGrams: Double
)

data class RecommendationItem(
    val beanId: String,
    val beanName: String,
    val compositeScore: Double,
    val cosineSimilarity: Double,
    val freshnessFactor: Double,
    val roastAgeDays: Long,
    val explanationText: String
)

// ============================================================================
// 4. OVAT Brewing Feedback Loop Engine (S2-066)
// ============================================================================

enum class SensoryFeedbackDiagnosis {
    SOUR_UNDER_EXTRACTED,
    BITTER_OVER_EXTRACTED,
    WEAK_WATERY,
    HARSH_CONCENTRATED,
    BALANCED
}

data class OvatAdjustmentProposal(
    val variableAdjusted: String,
    val previousValue: String,
    val proposedValue: String,
    val diagnosticReason: String,
    val expectedDrawdownDeltaSeconds: Int,
    val modifiedRecipePayload: Map<String, Any>
)

// ============================================================================
// 5. OCR Coffee Bag Metadata Line Parser (S2-067)
// ============================================================================

data class ParsedBeanDraft(
    val roaster: String?,
    val originCountry: String?,
    val region: String?,
    val variety: String?,
    val process: String?,
    val roastDateIso: String?,
    val elevationMasl: Int?,
    val packageWeightGrams: Int?,
    val fieldConfidences: Map<String, Double>,
    val requiresExplicitUserReview: Boolean
)

// ============================================================================
// 6. Offline Weather & Altitude Boiling Point Calculator (S2-073)
// ============================================================================

enum class ElevationUnit { METERS, FEET }

data class WeatherParameters(
    val elevationMeters: Double = 0.0,
    val ambientTempCelsius: Double = 22.0,
    val relativeHumidityPercent: Double = 50.0,
    val calculatedBoilingPointCelsius: Double = 100.0
)

// ============================================================================
// 7. Food-Grade Safety & Allergen Validator (S2-074)
// ============================================================================

enum class IngredientSafetyLevel {
    SAFE_FOOD_GRADE,
    WARNING_ALLERGEN_PRESENT,
    CRITICAL_NON_FOOD_PROHIBITED
}

data class SafetyAuditResult(
    val level: IngredientSafetyLevel,
    val flags: List<String>,
    val permitsBeverageConsumption: Boolean
)

// ============================================================================
// 8. Barista Persona Presets (S2-072)
// ============================================================================

enum class BaristaPersonaPreset(val id: String, val title: String, val baseSystemPrompt: String) {
    SPECIALTY_Q_GRADER(
        "specialty_q_grader",
        "Specialty Q-Grader",
        "Rigorous technical coffee evaluator. Grounds analysis on SCA protocol, physical extraction kinetics, TDS, and organic acid balance."
    ),
    FRIENDLY_HOME_BARISTA(
        "friendly_home_barista",
        "Friendly Home Barista",
        "Encouraging kitchen mentor. Explains brewing dial-in intuitively without gatekeeping jargon. Focuses on daily consistency."
    ),
    RD_COFFEE_CHEMIST(
        "rd_coffee_chemist",
        "R&D Coffee Chemist",
        "Scientific extraction researcher. Emphasizes thermodynamics, degassing mass-transfer kinetics, water ionic content, and solubility."
    )
}
