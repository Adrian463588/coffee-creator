# OpenSpec & BDD Specification: Epic S2-E05 — Sensory Wheel and Professional Cupping

**Package Target:** `com.coffeecreator.app.core.sensory`  
**Architecture:** Pure Kotlin JVM Domain Layer (Zero Android/Compose/Room dependencies, exact Decimal arithmetic)  
**Methodology:** OpenSpec Spec-Driven Development (SDD) + Given-When-Then BDD  

---

## 1. Exact Mathematical Models & Algorithms

### 1.1 Multi-Evaluator Consensus & Dispersion Metrics

Given $N$ evaluators ($N \ge 2$) scoring sample $s$ across attribute $k \in \{1, \dots, K\}$:

#### 1. Arithmetic Mean ($\bar{x}_k$) and Median ($\tilde{x}_k$):
$$\bar{x}_k = \frac{1}{N} \sum_{i=1}^N x_{i,k}$$
$$\tilde{x}_k = \begin{cases} 
x_{\left(\frac{N+1}{2}\right), k} & \text{if } N \text{ is odd} \\
\frac{1}{2}\left(x_{\left(\frac{N}{2}\right), k} + x_{\left(\frac{N}{2}+1\right), k}\right) & \text{if } N \text{ is even}
\end{cases}$$
*(where $x_{(i),k}$ denotes the $i$-th order statistic).*

#### 2. Sample Standard Deviation ($s_k$) and Mean Absolute Deviation ($\text{MAD}_k$):
$$s_k = \sqrt{\frac{1}{N - 1} \sum_{i=1}^N \left(x_{i,k} - \bar{x}_k\right)^2}$$
$$\text{MAD}_k = \frac{1}{N} \sum_{i=1}^N \left| x_{i,k} - \tilde{x}_k \right|$$

#### 3. Inter-Rater Agreement Index ($\text{IRAI}_k$):
Bounded in $[0.0, 1.0]$, quantifying panel calibration against a maximum acceptable divergence tolerance $\sigma_{\text{threshold}}$ (set to $0.50$ points for standard Q-scale 6–10, and $1.00$ point for 1–10 scale):
$$\text{IRAI}_k = \max\left(0.0, \, 1.0 - \frac{s_k}{\sigma_{\text{threshold}}}\right)$$
Overall Panel Concordance:
$$\overline{\text{IRAI}} = \frac{1}{K} \sum_{k=1}^K \text{IRAI}_k$$

#### 4. Outlier Detection:
Evaluator $i$ on attribute $k$ is flagged as an outlier if:
$$\left| x_{i,k} - \bar{x}_k \right| > 2 \cdot s_k \quad \lor \quad \left| x_{i,k} - \tilde{x}_k \right| > 1.25 \cdot \text{IQR}_k$$

---

### 1.2 Descriptor Overlap Matrix & Panel Consensus Aggregation

Let $D_a$ and $D_b$ be the set of qualitative flavor descriptors chosen by Evaluators $a$ and $b$ for sample $s$:

#### 1. Pairwise Jaccard Descriptor Overlap:
$$J(E_a, E_b) = \frac{|D_a \cap D_b|}{|D_a \cup D_b|} \in [0.0, 1.0]$$

#### 2. Panel Consensus Descriptor Weight ($W_d$):
For each descriptor $d \in \bigcup_{i=1}^N D_i$:
$$W_d = \frac{1}{N} \sum_{i=1}^N \mathbb{I}(d \in D_i)$$
A descriptor $d$ is deemed a **Consensus Descriptor** if:
$$W_d \ge \theta_{\text{consensus}} \quad (\text{default } \theta_{\text{consensus}} = 0.50)$$

---

### 1.3 Sensory Score Aggregation & Cupping Formulas

#### 1. Standard Specialty Cupping (100-Point Q-Grade Equivalent):
Evaluated across 10 core attributes: Aroma, Flavor, Aftertaste, Acidity, Body, Balance, Uniformity, Clean Cup, Sweetness, Overall.
Each attribute is scored in $[6.00, 10.00]$ (0.25 tick increments) or 5-cup checklists (Uniformity, Clean Cup, Sweetness = 2 points per clean cup $\times$ 5 cups = max 10.00 points).

$$\text{GrossScore} = \sum_{k=1}^{10} x_k$$

**Defect Deduction:**
$$\text{DefectPenalty} = \sum_{j=1}^{C_{\text{taint}}} 2.00 + \sum_{m=1}^{C_{\text{fault}}} 4.00$$
*(Taint: noticeable off-flavor but drinkable, 2 pts/cup; Fault: overwhelming unpalatable defect, 4 pts/cup).*

$$\text{FinalQScore} = \max\left(0.00, \, \text{GrossScore} - \text{DefectPenalty}\right)$$

Grade Classification:
- $\text{FinalQScore} \ge 90.00$: **Outstanding / Specialty Grade 1 (Super Specialty)**
- $85.00 \le \text{FinalQScore} < 90.00$: **Excellent / Specialty Grade 1**
- $80.00 \le \text{FinalQScore} < 85.00$: **Very Good / Specialty Grade 1**
- $\text{FinalQScore} < 80.00$: **Commercial / Below Specialty Grade**

#### 2. Quick Barista Dial-In Composite Score (1–10 Scale):
Weighted composite over Aroma ($w_1=0.15$), Acidity ($w_2=0.25$), Sweetness ($w_3=0.30$), Body ($w_4=0.15$), Aftertaste ($w_5=0.15$):
$$\text{DialInScore} = \sum_{k=1}^5 w_k \cdot x_k$$

#### 3. Production QC Pass/Fail Rule:
Target profile $\mathbf{T} = [T_1, \dots, T_K]$. Sample consensus $\mathbf{\bar{x}} = [\bar{x}_1, \dots, \bar{x}_K]$.
$$\text{MaxDeviation} = \max_{k=1\dots K} |\bar{x}_k - T_k|$$
$$\text{Verdict} = \begin{cases} 
\text{PASS} & \text{if } \text{MaxDeviation} \le \delta_{\text{tol}} \text{ and Defects} = 0 \\
\text{FAIL} & \text{otherwise}
\end{cases} \quad (\delta_{\text{tol}} = 0.50\text{ pts})$$

---

### 1.4 Roast-Rest Degassing & Sensory Quality Heuristic Kinetics

Let $t$ be elapsed days post-roast ($t \ge 0$).

#### 1. $\text{CO}_2$ Desorption Decay (Exponential Kinetics):
$$C_{\text{CO}_2}(t) = C_0 \cdot \exp(-k_{\text{degas}} \cdot t)$$
Where:
- Light roast: $k_{\text{degas}} \approx 0.12\text{ day}^{-1}$, $C_0 = 100\%$
- Medium roast: $k_{\text{degas}} \approx 0.20\text{ day}^{-1}$, $C_0 = 100\%$
- Dark roast: $k_{\text{degas}} \approx 0.35\text{ day}^{-1}$, $C_0 = 100\%$

#### 2. Volatile Aroma & Oxidation Staling Decay:
$$V_{\text{aroma}}(t) = V_0 \cdot \exp(-k_{\text{ox}} \cdot t)$$
Where:
- Valve bag (ambient unopened): $k_{\text{ox}} \approx 0.015\text{ day}^{-1}$
- Open bag (daily exposure): $k_{\text{ox}} \approx 0.065\text{ day}^{-1}$
- Hermetic frozen ($-18^\circ\text{C}$): $k_{\text{ox}} \approx 0.001\text{ day}^{-1}$

#### 3. Sensory Quality Index Rest-Curve ($S(t)$):
Models the rise to peak sensory window (as interfering $\text{CO}_2$ leaves) followed by decline (due to staling / lipid oxidation):
$$S(t) = S_{\text{pot}} \cdot \left(1 - \exp\left(-\frac{t}{\tau_{\text{degas}}}\right)\right) \cdot \exp\left(-\frac{t}{\tau_{\text{stale}}}\right)$$
Parameters by roast level:
- Light: $\tau_{\text{degas}} = 7.0\text{ days}$, $\tau_{\text{stale}} = 45.0\text{ days}$ $\implies$ Peak at $t_{\text{peak}} \approx 14\text{–}21\text{ days}$.
- Medium: $\tau_{\text{degas}} = 4.0\text{ days}$, $\tau_{\text{stale}} = 35.0\text{ days}$ $\implies$ Peak at $t_{\text{peak}} \approx 7\text{–}14\text{ days}$.
- Dark: $\tau_{\text{degas}} = 2.0\text{ days}$, $\tau_{\text{stale}} = 25.0\text{ days}$ $\implies$ Peak at $t_{\text{peak}} \approx 3\text{–}7\text{ days}$.

Peak Sensory Window condition:
$$W_{\text{peak}} = \left\{ t \;\middle|\; S(t) \ge 0.95 \cdot S(t_{\text{peak}}) \right\}$$

---

## 2. Pure Kotlin Domain Models & Service Interfaces

```kotlin
package com.coffeecreator.app.core.sensory

import java.math.BigDecimal

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
// 3. Epistemological Origin & Provenance Badges (S2-054)
// ============================================================================

enum class EpistemologicalOrigin {
    HUMAN_OBSERVATION,      // Empirical score directly logged by human evaluator
    AI_HEURISTIC_HYPOTHESIS,// Rule-based inference from origins/roast parameters
    CALIBRATED_PREDICTION   // Statistical/ML prediction with confidence intervals
}

data class ProvenanceBadge(
    val origin: EpistemologicalOrigin,
    val authorOrModelId: String,
    val confidenceLower: BigDecimal? = null,
    val confidenceUpper: BigDecimal? = null,
    val confidenceLevelPercent: Int? = null, // e.g., 95%
    val methodDisclaimer: String,
    val timestamp: Long
)

// ============================================================================
// 4. Sensory Sliders, Attributes & Templates (S2-053, S2-055)
// ============================================================================

enum class SensoryScaleType {
    Q_SCALE_6_TO_10,        // 6.00 to 10.00 in 0.25 steps
    ORDINAL_1_TO_10,        // 1.0 to 10.0 continuous/step
    INTENSITY_0_TO_5,       // 0 to 5 perceived intensity
    CHECKLIST_5_CUPS        // 5 bowls (0 to 5 checked)
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

// ============================================================================
// 5. Cupping Session, Evaluators & Masking (S2-056, S2-057, S2-058)
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
    OPEN_LABEL,             // Roaster, bean, process visible
    SINGLE_BLIND_EVALUATOR, // Evaluators see code; Lead sees true identity
    DOUBLE_BLIND_PANEL      // All parties see code until controlled reveal
}

data class CuppingSample(
    val sampleId: String,
    val blindCode: String,     // e.g. "Sample #A3", "Bowl #07"
    val actualBeanId: String,
    val roaster: String,
    val origin: String,
    val process: String,
    val roastDate: Long,
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
    val taintCupCount: Int = 0, // 2 pts deduction per cup
    val faultCupCount: Int = 0, // 4 pts deduction per cup
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
// 6. Consensus Analytics & Reports (S2-059, S2-060)
// ============================================================================

data class AttributeConsensus(
    val attributeKey: String,
    val mean: BigDecimal,
    val median: BigDecimal,
    val stdDev: BigDecimal,
    val agreementIndex: BigDecimal, // 0.0 to 1.0
    val outlierEvaluatorIds: List<String>
)

data class DescriptorConcordance(
    val descriptor: String,
    val selectionCount: Int,
    val selectionFrequency: BigDecimal, // 0.0 to 1.0
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
// 7. Physical Green & Roasted QC (S2-061)
// ============================================================================

enum class GreenDefectType(val isPrimary: Boolean, val penaltyWeight: Int) {
    FULL_BLACK(true, 1),
    FULL_SOUR(true, 1),
    DRIED_POD(true, 1),
    LARGE_STONE_STICK(true, 1),
    SEVERE_INSECT_DAMAGE(true, 1),
    PARTIAL_BLACK(false, 3),
    PARTIAL_SOUR(false, 3),
    PARCHMENT(false, 5),
    FLOATER(false, 5),
    BROKEN_CHIPPED(false, 5),
    SLIGHT_INSECT_DAMAGE(false, 10)
}

data class GreenCoffeeDefectEntry(
    val defectType: GreenDefectType,
    val rawCount: Int
)

data class PhysicalQcRecord(
    val id: String,
    val sampleId: String,
    val sampleMassGrams: BigDecimal, // standard 350g green sample
    val moisturePercentage: BigDecimal, // standard 10.0% - 12.0%
    val waterActivityAw: BigDecimal, // standard 0.50 - 0.65
    val bulkDensityGPerL: BigDecimal,
    val screenDistribution: Map<Int, BigDecimal>, // e.g. 18 -> 45.0%
    val defects: List<GreenCoffeeDefectEntry>,
    val totalPrimaryDefects: Int,
    val totalSecondaryDefects: Int,
    val calculatedGrade: String,
    val roastedAgtronGourmetWholeBean: BigDecimal,
    val roastedAgtronGourmetGround: BigDecimal,
    val roastDeltaAgtron: BigDecimal // Ground - WholeBean
)

// ============================================================================
// 8. Roast-Rest Sensory Lifecycle (S2-062)
// ============================================================================

enum class DegassingPhase {
    TOO_FRESH_CO2_INTERFERENCE, // Sharp carbonic acid, excessive bubbling, uneven extraction
    PEAK_FLAVOR_WINDOW,         // Optimal clarity, fully open aromatics, maximum sweetness
    STABLE_POST_PEAK,           // Good balance, slight decline in delicate top notes
    STALE_OXIDIZED              // Paper, cardboard, flat acidity, loss of sweetness
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

// ============================================================================
// 9. Pure Domain Service Interfaces
// ============================================================================

interface FlavorTaxonomyService {
    fun getFullTaxonomy(): FlavorWheelTaxonomy
    fun searchDescriptors(query: String): List<FlavorDescriptor>
    fun getAccessibleTableRepresentation(): List<FlavorDescriptor>
}

interface CalibrationReferenceService {
    fun getStandardExercises(): List<CalibrationReferenceExercise>
    fun calculatePreparationRecipe(
        exerciseId: String,
        batchVolumeMl: BigDecimal
    ): Map<String, BigDecimal>
}

interface CuppingSessionManager {
    fun createSession(
        title: String,
        templateType: CuppingTemplateType,
        maskingMode: MaskingMode,
        evaluators: List<Evaluator>,
        samples: List<CuppingSample>
    ): CuppingSession

    fun submitSheet(sessionId: String, sheet: EvaluationSheet): CuppingSession
    fun closeSession(sessionId: String): CuppingSession
    fun reopenSession(sessionId: String, editorId: String, reason: String): CuppingSession
    fun revealSamples(sessionId: String): CuppingSession
}

interface SensoryConsensusEngine {
    fun calculateConsensus(session: CuppingSession, sampleId: String): ConsensusAnalytics
}

interface SensoryQcExporter {
    fun generateOfflineReport(
        session: CuppingSession,
        physicalQc: Map<String, PhysicalQcRecord> = emptyMap()
    ): QcReportReceipt
}

interface PhysicalQcService {
    fun gradeGreenLot(
        moisture: BigDecimal,
        waterActivity: BigDecimal,
        density: BigDecimal,
        defects: List<GreenCoffeeDefectEntry>
    ): String
}

interface RoastRestSensoryTracker {
    fun recordObservation(observation: RoastRestObservation)
    fun predictRestCurve(
        roastDegree: String,
        process: String,
        packagingType: String
    ): RestCurvePrediction
}
```

---

## 3. Complete Gherkin BDD Scenarios (Stories S2-051 to S2-062)

### S2-051: Hierarchical Searchable Flavor Wheel & Accessible List Alternative
```gherkin
Feature: Hierarchical Searchable Flavor Wheel and Accessible List (S2-051)
  As a professional cupper or visually impaired barista
  I want to navigate and search sensory descriptors hierarchically or via an accessible list
  So that I can log exact flavor attributes without reliance on non-accessible visual wheel graphics.

  Scenario: Hierarchical search finds nested descriptor with color token
    Given a loaded standard flavor taxonomy
    When the user searches for descriptor "Grapefruit"
    Then the system returns category "Fruity", subcategory "Citrus Fruit", descriptor "Grapefruit"
    And the returned descriptor has color token "#F9A825"
    And the accessibility description reads "Grapefruit: vibrant citrus note with gentle pleasant bitterness".

  Scenario: Accessible table view renders with TalkBack headers
    Given the sensory taxonomy containing 110 descriptors across 9 primary categories
    When the user switches to "Accessible List View"
    Then the screen presents a linear list grouped by category headers
    And each entry provides accessible role semantics and touch target >= 48dp
    And no interactive elements depend solely on color hue for identification.
```

### S2-052: Descriptor Reference Calibration Exercises & Food Safety Boundaries
```gherkin
Feature: Descriptor Definitions and Food-Safe Reference Calibration (S2-052)
  As a coffee evaluator calibrating my palate
  I want safe, food-grade chemical reference recipes
  So that I can calibrate sensory perception of acidity, sweetness, and bitterness safely and accurately.

  Scenario: Prepare Citric Acid calibration solution for lemon acidity
    Given an evaluator requests a calibration recipe for "ACIDITY_CITRIC"
    When target volume is set to 1000 mL
    Then the calculated food-grade citric acid mass is exactly 0.50 g
    And the system mandates chemical grade "FOOD_GRADE_FCC"
    And a prominent safety warning indicates "Use only distilled food-grade water; discard after 24 hours".

  Scenario: Enforce strict safety boundary on bitterness reference
    Given a calibration exercise for "BITTERNESS" using Quinine Monohydrochloride
    When the recipe is displayed
    Then target concentration is strictly 0.005 g/L (5 mg/L)
    And maximum daily tasting limit is flagged as 50 mL
    And an explicit contraindication disclaimer states "Warning: Do not consume if pregnant or allergic to cinchona alkaloids".
```

### S2-053: Standard Sensory Sliders & Qualitative Descriptor Notes
```gherkin
Feature: Standard Sensory Sliders and Qualitative Notes (S2-053)
  As a cupper evaluating a cup
  I want standardized continuous/stepped sliders and qualitative note fields
  So that I can capture ordinal intensity and specific descriptive nuances simultaneously.

  Scenario: Record Specialty Q-Scale attributes with qualitative descriptors
    Given an active cupping sheet for "Sample #B1" on Q-scale 6.00 to 10.00
    When the evaluator sets Aroma to 8.25, Acidity to 8.50, Sweetness to 8.75, Body to 8.00, Balance to 8.25
    And adds qualitative descriptors "Bergamot", "Jasmine", "Meyer Lemon"
    And writes note "Bright phosphoric sparkle with tea-like floral finish"
    Then the values persist without floating point drift
    And the attribute scores are strictly validated in step increments of 0.25.
```

### S2-054: Epistemological Tripartition: Human vs Heuristic vs Calibrated Model
```gherkin
Feature: Epistemological Distinction and Provenance Badges (S2-054)
  As a coffee researcher
  I want clear distinction between human observation, heuristic hypothesis, and calibrated prediction
  So that uncalibrated AI assumptions are never confused with empirical cupper truth.

  Scenario: Display human observation with empirical badge
    Given an empirical score sheet submitted by Q-Grader "Q-4981"
    When viewed on the sensory analytics screen
    Then the scores display a green badge labeled "Empirical Cupper Score"
    And the origin is set to "HUMAN_OBSERVATION" with 100% confidence.

  Scenario: Display AI rule-based hypothesis with mandatory uncertainty disclaimer
    Given an automated recipe draft proposing a flavor profile from bean origin
    When the estimated sensory radar is rendered
    Then the profile line is drawn dashed
    And a prominent badge displays "Uncalibrated Heuristic Rule"
    And the label states "Heuristic assumption; validate by observation. No measured laboratory accuracy claimed."
    And the estimate is prohibited from being written to the official cupping journal table.

  Scenario: Display calibrated statistical model with bounded confidence intervals
    Given a calibrated ML regression model predicting acidity from roast Agtron and density
    When the prediction is returned
    Then the value is displayed as "8.20 [7.90 - 8.50] (95% CI)"
    And the badge indicates "Calibrated Statistical Model v1.2".
```

### S2-055: Cupping Sheet Templates (Specialty, Dial-In, QC, Espresso)
```gherkin
Feature: Cupping Sheet Templates (S2-055)
  As a coffee lab manager
  I want tailored templates for Specialty Cupping, Barista Dial-In, Production QC, and Espresso
  So that evaluation formats match the exact operational context.

  Scenario: Calculate 100-point Specialty Cupping score with taints and faults
    Given a Specialty Cupping template with 10 attributes scored at 8.00 each
    And 1 cup exhibits a "Taint" (-2.00) and 1 cup exhibits a "Fault" (-4.00)
    When the final score is calculated
    Then the gross score is 80.00
    And the defect deduction is 6.00
    And the net final Q-score is 74.00
    And the classification is "Commercial / Below Specialty Grade".

  Scenario: Evaluate Production QC pass/fail against target tolerance
    Given a Production QC template with target profile Acidity=8.00, Sweetness=8.50, Body=7.50
    When actual sample consensus is Acidity=8.25, Sweetness=8.40, Body=7.60 with 0 defects
    Then maximum deviation is 0.25 (<= 0.50 tolerance)
    And the production verdict is "PASS".
```

### S2-056: Multi-Sample Cupping Session & Evaluator Identities
```gherkin
Feature: Multi-Sample Cupping Session and Evaluator Identities (S2-056)
  As a head roaster
  I want to create a cupping session with multiple samples and assigned evaluators
  So that each panelist scores independently under local identities.

  Scenario: Initialize session with Lead Cupper and multiple lots
    Given roaster creates session "Weekly Production QC - Week 42"
    When 5 coffee samples are registered and 3 evaluators assigned (1 Lead Cupper, 2 Baristas)
    Then the session status is initialized to "DRAFT"
    And 5 distinct evaluation sheets are prepared per evaluator.
```

### S2-057: Blind, Non-Blind, and Double-Blind Masking with Controlled Reveal
```gherkin
Feature: Sensory Masking and Controlled Reveal (S2-057)
  As a competition judge or lab coordinator
  I want randomized code tags masking sample identities
  So that evaluators remain unbiased during cupping until an authorized reveal.

  Scenario: Single-blind masking hides bean origins from panelists
    Given a session configured in "SINGLE_BLIND_EVALUATOR" mode
    When panelists open their cupping screens
    Then Sample 1 is displayed only as "Sample #C4" and Sample 2 as "Sample #A7"
    And roaster name, country of origin, and processing method are masked with null strings.

  Scenario: Controlled reveal permanently unmasks sample metadata
    Given a blinded session in status "OPEN" with all sheets submitted
    When the Lead Cupper executes "Reveal All Samples" and confirms the security prompt
    Then all samples transition to isRevealed = true
    And true bean identities ("Ethiopia Yirgacheffe Washed", "Colombia Geisha Natural") are displayed
    And the reveal timestamp is permanently recorded in the session metadata.
```

### S2-058: Session Lifecycle State Machine & Audit Trail
```gherkin
Feature: Session Lifecycle State Machine and Immutable Audit Trail (S2-058)
  As a quality compliance officer
  I want strict session lifecycle gates (DRAFT -> OPEN -> CLOSED -> REOPENED)
  So that cupping scores cannot be modified silently after closing.

  Scenario: Reopening closed session requires reason and writes immutable audit entry
    Given a cupping session in status "CLOSED" with locked scores
    When the Lead Cupper triggers "Reopen Session" with reason "Transcription typo on Sample #C4 Acidity"
    Then the session status becomes "REOPENED"
    And an immutable audit entry is appended with editorId, timestamp, and justification reason
    And when Acidity is changed from 7.50 to 8.50, previous score 7.50 is archived in the audit snapshot.
```

### S2-059: Multi-Evaluator Consensus Analytics & Descriptor Overlap
```gherkin
Feature: Multi-Evaluator Consensus Analytics (S2-059)
  As a sensory panel leader
  I want mean, median, standard deviation, agreement index, and descriptor overlap matrix
  So that I can identify panel calibration drift and consensus flavor notes.

  Scenario: Compute panel consensus statistics and flag outlier evaluator
    Given 4 evaluators score Acidity for "Sample #A1" as: 8.50, 8.50, 8.75, 6.50
    When consensus analytics engine executes
    Then sample mean is 8.06 and median is 8.50
    And sample standard deviation is 1.05
    And the Inter-Rater Agreement Index is 0.00 (< 0.50 acceptable threshold)
    And evaluator scoring 6.50 is flagged as an "OUTLIER".

  Scenario: Calculate descriptor overlap matrix using Jaccard similarity
    Given Evaluator A logs descriptors ["Peach", "Jasmine", "Bergamot"]
    And Evaluator B logs descriptors ["Jasmine", "Bergamot", "Honey", "Black Tea"]
    When pairwise overlap is calculated
    Then intersection count is 2 and union count is 5
    And Jaccard similarity between Evaluator A and B is exactly 0.40.
```

### S2-060: Offline CSV & Markdown QC Report Generation with Provenance Receipts
```gherkin
Feature: Offline QC Report Generation with Provenance Receipts (S2-060)
  As a quality assurance director
  I want to export complete session reports in offline Markdown and CSV formats
  So that session data is auditable, portable, and verifiable without network connectivity.

  Scenario: Export offline report bundle with SHA-256 fingerprint
    Given a completed cupping session with 4 samples, consensus stats, and audit entries
    When the user requests "Export QC Report"
    Then the system generates a formatted Markdown file and a CSV file locally
    And computes a cryptographic SHA-256 fingerprint over the session JSON payload
    And embeds the fingerprint receipt and app engine version at the footer of the report
    And zero network requests are dispatched.
```

### S2-061: Physical Green & Roasted Coffee QC Integration
```gherkin
Feature: Physical Green and Roasted Coffee QC (S2-061)
  As a green coffee buyer and roaster
  I want to log moisture, water activity aw, density, defect counts, and Agtron roast color
  So that physical quality attributes link directly to cupping samples.

  Scenario: Evaluate 350g green coffee sample for specialty grade eligibility
    Given a green sample with moisture 11.2%, water activity 0.56 aw, bulk density 685 g/L
    And defect count: 0 Primary defects and 3 Partial Black (Secondary defects = 1 full defect equivalent)
    When evaluated by the Physical QC Service
    Then calculated grade is "Specialty Grade 1"
    And moisture and water activity are flagged as "WITHIN_SAFE_STORAGE_LIMITS".

  Scenario: Record roast color Agtron delta
    Given a roasted sample with Agtron Gourmet Whole Bean = 65.0 and Ground = 78.0
    When physical QC is saved
    Then roast color delta is +13.0 Agtron points
    And the roast homogeneity is classified as "MODERATE_ROAST_CORE_DELTA".
```

### S2-062: Roast-Rest Sensory Lifecycle & Degassing Peak Window Tracking
```gherkin
Feature: Roast-Rest Sensory Lifecycle Tracking (S2-062)
  As a barista managing bag freshness
  I want to track multiple cuppings over the degassing lifespan of a roast
  So that I can identify the true peak flavor window and detect staling decline.

  Scenario: Identify too-fresh coffee with carbonic acid interference
    Given a light roast coffee bag roasted 2 days ago
    When the barista logs an observation with cupping score 82.00 and notes carbonic bubbling
    Then the system classifies the phase as "TOO_FRESH_CO2_INTERFERENCE"
    And the rest-curve heuristic predicts peak flavor window between Day 10 and Day 21.

  Scenario: Detect staling decline beyond peak window
    Given a bag roasted 60 days ago stored in an opened valve bag
    When evaluated at score 78.50 with notes "Cardboard, flat acidity, loss of floral aromatics"
    Then the observation phase is recorded as "STALE_OXIDIZED"
    And the system logs the staling onset date for that bean profile.
```

