# Coffee Creator — Sprint 04: Professional Sensory, Cupping, CVA & Green Coffee Assessment

**Version:** 1.0 — 17 October 2026  
**Status:** Implementation-ready product specification; features and tests NOT yet executed  
**Platform:** Android / Kotlin / Jetpack Compose / offline-first  
**Dependencies:** Preserve Sprint 01–03, including existing sensory menu, flavor wheel, champion recipes, grinder and dripper catalog, RAG, local AI, and brewing history.  
**Naming decision:** User mentioned both “Sprint 4” and “Sprint 3”; this document is **Sprint 04**, an additive successor to the existing Sprint 03 Manual Brewing & Grinder Intelligence. Do not overwrite Sprint 03.

## 0. AI Agent master directive — paste into Codex / Claude Code / Antigravity

> Implement Sprint 04 as an **additive, offline-first** professional coffee sensory and cupping module. Inspect the actual repository and read `PRD.md`, `AGENTS.md`, `ARCHITECTURE.md`, `DESIGN.md`, `TASKS.md`, `SPRINT_02_COMPETITIVE_FEATURE_IMPLEMENTATION.md`, and `SPRINT_03_MANUAL_BREW_GRINDER_INTELLIGENCE.md` first. Inventory all existing routes, database entities, screens, forms, tests, exports, and sensory assets. Do not delete, rename without migration, or silently replace any existing functionality. Use the current **SCA Coffee Value Assessment (CVA)** as the default standards-oriented assessment, with the historic 2004 cupping form as a separately labeled **LEGACY** option. Implement accessible mobile-first digital data capture, objective/subjective separation, green physical assessment, aroma and taste training, blinded triangulation, examiner practice and evidence-backed education. Never claim the application or its practice scores provide official SCA/Q Grader/R Grader credentials. Use only licensed or original content; link to official standards and forms instead of reproducing copyrighted material without permission. Require provenance, protocol version, calibration, and immutable audit history. Tests must run offline. Report PASS/FAIL/BLOCKED with real evidence; never invent results.

## 1. Standards audit: what is current versus legacy

| Standard / framework | Status and implementation rule | Primary source |
|---|---|---|
| SCA Coffee Value Assessment (CVA) | **Default** modern framework with Physical, Descriptive, Affective, Extrinsic perspectives; preserve boundaries and methodology | https://sca.coffee/value-assessment/ |
| SCA-102 | Sample Preparation and Tasting Mechanics; standards-controlled workflow | https://sca.coffee/research/coffee-standards |
| SCA-103 | Descriptive Assessment; intensity and descriptors are not automatically quality scores | https://sca.coffee/value-assessment/ |
| SCA-104 | Affective Assessment; preference/quality impression recorded separately | https://sca.coffee/value-assessment/ |
| SCA-105 | Extrinsic Assessment; record traceability and context with explicit disclosure controls | https://sca.coffee/research/coffee-standards |
| SCA 2004 Cupping Protocol/Form | **LEGACY**, superseded for SCA cupping by CVA standards; useful only for historical comparison | https://sca.coffee/sca-news?category=Features |
| SCA 2004 washed Arabica green grading | Reference for physical assessment/defect training; verify applicability to specific coffee types and current documents | https://sca.coffee/education/how-to-prepare |
| SCA Coffee Skills — Sensory Skills Foundation/Intermediate/Professional | Curriculum inspiration, **not** a certification grant | https://education.sca.coffee/coffee-skills-program |
| SCA Q Grader course | Practice topics: CVA forms, olfactory categories, main tastes, triangulation, roasting defects, physical grading | https://sca.coffee/education/q-grader |
| R Grader / historic CQI credentials | Separate historical/program-specific reference. **Do not present as equivalent to the current SCA Q Grader or assume currently available certification**; verify authority and program status before naming a credential | https://www.coffeeinstitute.org/ |
| World Coffee Research Sensory Lexicon | Sensory terminology and reference preparation; review license and permitted reuse | https://worldcoffeeresearch.org/resources/sensory-lexicon |
| SCA Coffee Taster's Flavor Wheel | Descriptor-navigation inspiration; artwork is not automatically free to reproduce | https://sca.coffee/sca-news/how-to-use-the-flavor-wheel-in-eight-steps-ab2hy |
| Le Nez du Café 2.0, 60 aromas | Commercial aroma training kit updated in 2025; do not copy proprietary 60-vial catalogue, photos, exercises or packaging without license | https://www.editions-jeanlenoir.com/ |
| SCA + SCAI CVA Indonesia adoption | Indonesian industry relevance and localization | https://sca.coffee/sca-news/sca-and-scai-collaboration-arfrn |

**Compliance rule:** “SCA-aligned” means an implementation is informed by the identified public standards; **“official”, “SCA-certified”, “Q Grader certified” and “SCA-approved digital form” are prohibited claims without explicit authorization**. A visually similar homegrown mobile form is NOT an official form. For exact fields, scales, sample preparation, number of cups, tolerances, scoring and calibration procedures, consult the current official SCA protocol and PDFs at implementation time; never substitute guesses. Store `protocolVersion`, `formRevision`, `sourceURL`, `retrievedAt`, `licenseStatus`, and `fieldMappingVersion`.

## 2. User personas, workflows, and deliverables

- **Beginner:** study aroma, taste, acidity, body and cupping mechanics; use guided, non-graded practice.
- **Barista / roaster:** rapid offline sensory logs, coffee lot QC, comparison, defect records, roast profile links.
- **Professional cupper / buyer:** standardized session preparation, blind codes, CVA forms, traceable physical and sensory evidence, signed exports.
- **Trainer / Q Grader candidate:** timed drills, blinded triangles, reference taste solutions, calibration and longitudinal learning analytics; **practice only**.
- **Green coffee QC:** sampling, screen sizes, moisture, physical defects, grading reports and photos with provenance.

End-to-end journey: `Sensory Home → Learn / Practice / Cupping / Green Coffee QC / Reports → Choose protocol → Session setup → Blind randomization → Prepare → Timed assessment → Review → Lock → Export / Compare / Audit`.

## 3. Learning Academy: comprehensive modular syllabus

Each lesson must have learning objectives, original Indonesian and English content, glossary, diagrams created in-house, citations, practice, feedback, accessibility descriptions, estimated duration, and offline caching. Separate **scientific fact**, **industry convention**, **manufacturer recommendation**, and **training heuristic**.

| Module ID | Curriculum and subtopics | Exercise / mastery evidence |
|---|---|---|
| EDU-01 | Coffee botany: Coffea arabica, canephora, varieties, cultivars, terroir, altitude, processing, roast | Identify provenance and avoid taste stereotypes |
| EDU-02 | From cherry to green: harvesting, washed/natural/honey, fermentation, drying, storage, green defects | Process comparison; record observations vs assumptions |
| EDU-03 | Sensory science: stimulus, thresholds, intensity, adaptation, fatigue, bias, repeatability, sensitivity vs specificity | Perception/bias quiz |
| EDU-04 | Olfactory anatomy: orthonasal and retronasal smell, receptors, neural processing, aroma vs fragrance | Smell-first and retronasal exercise |
| EDU-05 | Gustatory anatomy: **five** widely recognized basic tastes—sweet, sour, salty, bitter, umami; “acid” is not a sixth taste; sourness is a taste and perceived acidity is broader | Blind taste recognition |
| EDU-06 | Trigeminal/mouthfeel: astringency, tactile dryness, viscosity, body, temperature, chemesthesis | Texture discrimination |
| EDU-07 | Organic acids: citric, malic, tartaric, acetic, lactic, phosphoric, quinic; acid chemistry vs perceived sourness; safety and reference preparation | Match acid references, record confidence; never claim a taste uniquely identifies a molecule |
| EDU-08 | Coffee aromatic chemistry: volatile compounds, roasting, processing, contamination, off-notes | Aroma-family categorization |
| EDU-09 | Aroma training: **Le Nez du Café 60** compatibility as a *user-owned physical kit*; independent generic aroma taxonomy and reference IDs; no unlicensed proprietary kit content | Randomized vial recognition and spaced repetition |
| EDU-10 | WCR lexicon, SCA flavor-wheel concepts, descriptor hierarchy, cross-cultural localization | Descriptor-to-evidence mapping |
| EDU-11 | Sample prep: roast control, grinding, water, dose, vessels, time, cleaning, cross-contamination | Pre-flight compliance checklist |
| EDU-12 | SCA CVA philosophy: physical/descriptive/affective/extrinsic, bias minimization, form differences | Route observations to correct form |
| EDU-13 | CVA descriptive: fragrance, aroma, flavor, aftertaste, acidity, sweetness, mouthfeel, descriptors and intensities as specified by current form | Mock descriptive session |
| EDU-14 | CVA affective: preferences, quality impressions and appropriate scaling per current standard | Independent affective session |
| EDU-15 | CVA extrinsic: origin, processing, certification claims, traceability, contextual value, data privacy | Evidence-quality classification |
| EDU-16 | Green Arabica classification: sampling, screen size, defects, moisture, physical analysis, classification systems and scope | Defect-identification practice |
| EDU-17 | Triangulation: 3 coded samples, two alike/one different, forced-choice, blinding, randomization, repeated trials, chance baseline 1/3 | Blind triangle drill |
| EDU-18 | Calibration and panel management: reference solutions, assessor repeatability, agreement, fatigue, session environment | Personal calibration trend |
| EDU-19 | Roast defects and incorrect sample preparation: under/overdevelopment, baked/scorched, contamination, confounding | Distinguish observed attribute from inferred cause |
| EDU-20 | Professional reporting, ethics, accessibility, panel bias, data governance, traceability, inter-lab comparability | Audit and reporting assignment |
| EDU-21 | Legacy 2004 SCA form vs current CVA: why the methodology changed, no cross-protocol score equivalence | Correctly label historic results |
| EDU-22 | Q Grader / R Grader pathways: program ownership, exam topic overview, official training vs app practice | Credential literacy assessment |

**Scientific correction:** The user listed “empat basic rasa” but named **five**: salt, acid/sour, sweet, bitter and umami. Teach all five; include “acid” as sour/acidity terminology with proper distinctions.

## 4. Feature backlog — epics, requirements and acceptance criteria

### EPIC A — Sensory Academy & Knowledge (S4-001–S4-010)

| ID | P | Requirement / acceptance |
|---|---|---|
| S4-001 | P0 | Sensory dashboard: Learn, Practice, CVA Cupping, Green QC, Results, Protocol Library, Settings |
| S4-002 | P0 | 22 bilingual offline modules with lesson source and version; accessible navigation |
| S4-003 | P0 | Five-taste interactive lessons, taste intensity and difference between chemical and perceived acidity |
| S4-004 | P1 | Organic acid atlas with descriptions, safety notices, reference prep metadata and uncertainty |
| S4-005 | P0 | Aroma taxonomy browser, aroma family tags, orthonasal/retronasal exercises |
| S4-006 | P1 | Le Nez du Café 60 *physical kit companion* with configurable user-owned vial identifiers; license gate for any official list/media |
| S4-007 | P1 | Flavor wheel bridge to Sprint 02; no duplicate or replacement of existing wheel, support licensing and original graphics |
| S4-008 | P1 | Quizzes, spaced repetition, difficulty progression, explanations and per-module evidence |
| S4-009 | P1 | Q Grader-style **practice** learning track; non-official badges only |
| S4-010 | P0 | Content provenance registry, version pinning, offline availability and rights audit |

### EPIC B — Digital CVA Cupping (S4-011–S4-026)

| ID | P | Requirement / acceptance |
|---|---|---|
| S4-011 | P0 | Create multi-sample session, cupper identity/pseudonym, date, lot IDs, cup count, equipment and controlled prep metadata |
| S4-012 | P0 | Protocol selector `SCA_CVA_CURRENT`, `SCA_2004_LEGACY`, `CUSTOM_RESEARCH`; visibly distinguish |
| S4-013 | P0 | Digital **Physical** form with verified official field mapping and provenance |
| S4-014 | P0 | Digital **Descriptive** form with official field definitions, units, scales, required/optional fields |
| S4-015 | P0 | Digital **Affective** form separated from descriptive observations |
| S4-016 | P0 | Digital **Extrinsic** form with traceability, sources, permissioned reveal and disclosure policy |
| S4-017 | P0 | Prevent extrinsic reveal before blinded sensory assessment unless explicitly permitted and logged |
| S4-018 | P0 | Step-by-step prep, fragrance/aroma, crust break, tasting rounds, timing, temperature and reminders from selected protocol |
| S4-019 | P0 | Autosave local drafts, interruption recovery, undo and no-data-loss after app process death |
| S4-020 | P0 | Phone-friendly one-handed form entry, progressive disclosure, large tap targets, offline haptics optional |
| S4-021 | P0 | Multi-cup notes, inconsistencies, taints/faults and sample-level records per selected official schema |
| S4-022 | P0 | Field validation, protocol-specific calculations **only if defined in the current official standard**; no invented total score |
| S4-023 | P0 | Lock/sign a completed form, preserve original, create amendments with reasons and revision history |
| S4-024 | P0 | PDF/CSV/JSON export of **app-native** reports; official form appearance/export only with explicit license/permission |
| S4-025 | P1 | Compare coffees side-by-side with protocol-aware compatible metrics and confidence labels |
| S4-026 | P1 | Legacy 2004 cupping form archived and independently labeled; never silently convert legacy score to CVA |

### EPIC C — Sensory Training and Testing (S4-027–S4-040)

| ID | P | Requirement / acceptance |
|---|---|---|
| S4-027 | P0 | Blind triangle: exactly 3 samples, 2 same + 1 different; randomize all 6 position permutations |
| S4-028 | P0 | No correct answer revealed until response submitted; blind labels distinct from coffee identities |
| S4-029 | P0 | Track trials, randomization seed, operator, washout, correct response, guess confidence and conditions |
| S4-030 | P1 | Statistics: binomial test against p=1/3 for independent comparable trials; warn about dependence and repeated learning |
| S4-031 | P0 | Five basic-taste identification practice with reference solution metadata, concentrations, safety checks and blind IDs |
| S4-032 | P1 | Taste intensity and thresholds: forced-choice/ascending concentration methods; do not infer clinical sensory impairment |
| S4-033 | P0 | Olfactory family identification with physical vials/food references and hidden answer key |
| S4-034 | P1 | Aroma memory and spaced-repetition sessions; confusion matrix and confidence calibration |
| S4-035 | P1 | Organic-acid comparative tastings with trained supervision, safe food-grade preparations and source-controlled concentrations |
| S4-036 | P1 | Roast defect recognition practice; include control samples and note uncertain causal inference |
| S4-037 | P1 | Tactile/mouthfeel discrimination; water/temperature and serving controls |
| S4-038 | P1 | Blind paired comparisons and ranking exercises; label distinct from triangle tests |
| S4-039 | P1 | Personalized non-certification readiness dashboard by skill category, longitudinal practice history |
| S4-040 | P0 | Never auto-mark a practical sensory exercise PASS based solely on reading or virtual quizzes; require actual human observation |

### EPIC D — Green Coffee Physical Assessment (S4-041–S4-051)

| ID | P | Requirement / acceptance |
|---|---|---|
| S4-041 | P0 | Green lot catalog with species, process, origin, producer, harvest, lot, sample mass, chain of custody |
| S4-042 | P0 | Physical defect taxonomy and primary/secondary categories with standard/version/source attribution |
| S4-043 | P0 | Record defect type, count, equivalent weighting **only where supported by selected verified standard**, evidence photo |
| S4-044 | P0 | Green sample prep and weighing, sieve/screen-size entry with units and test methodology |
| S4-045 | P0 | Moisture percentage with meter ID, calibration date, method, temperature and uncertainty; no invented readings |
| S4-046 | P1 | Water activity, density, color, odor, foreign matter and additional QC observations if measured |
| S4-047 | P0 | Rule engine for washed Arabica historical grading with **versioned** and sourced thresholds; refuse unsupported species/process claims |
| S4-048 | P0 | Distinguish green physical grade from sensory CVA outcome; never equate “specialty” solely with a numeric score |
| S4-049 | P1 | Lot comparison, supplier history, acceptance limits configured by buyer and standards |
| S4-050 | P1 | Green QC export with equipment calibration, assessor, photos, standard version and audit trail |
| S4-051 | P0 | Show `NOT_MEASURED`, `UNKNOWN`, `OUT_OF_SCOPE`, `PENDING_REVIEW` instead of fabricated values |

### EPIC E — Pro Operations, Architecture, AI, Integration (S4-052–S4-065)

| ID | P | Requirement / acceptance |
|---|---|---|
| S4-052 | P0 | Full offline operation: learn, session setup, assessment, local export and retrieval |
| S4-053 | P0 | Additive Room schema migrations; stable UUID, FK, no destructive migration, restore tests |
| S4-054 | P0 | Reuse Sprint 02 sensory taxonomy, recipe library and existing flavor wheel rather than replacing |
| S4-055 | P0 | Link cupping result to coffee bean, roast batch, manual brew recipe, grinder/dripper session without overwriting historical data |
| S4-056 | P0 | Separate deterministic standards/rules engine from local LLM explanatory assistant |
| S4-057 | P0 | AI responses cite local knowledge source; never generate fake scores, measurements or credential claims |
| S4-058 | P1 | Blind panel workflow: session host, assessor, role-based reveal, anonymized sample codes |
| S4-059 | P1 | Calibration and panel agreement dashboard, inter-rater statistics only where applicable |
| S4-060 | P0 | Full data export/import, migration, backups, checksums, privacy and offline retention settings |
| S4-061 | P0 | Indonesian and English UI, glossary, screen reader, color-independent feedback, large text support |
| S4-062 | P0 | Accessibility for smell/taste limitations and allergies; avoid medical diagnosis |
| S4-063 | P1 | Controlled form template updates with offline cache, migration and version mismatch warnings |
| S4-064 | P0 | QA automated/unit/UI tests, device testing, evidence-based release gate |
| S4-065 | P1 | User-consent-based sharing, no automatic cloud upload of supplier lots or assessor results |

## 5. Digital CVA mobile form: interaction and data model

### 5.1 Design principles

- Use **one mobile form per assessment perspective**, not one giant scoring sheet. Provide a session switcher with progress and incomplete-field indicators.
- Freeze sample identity as anonymous `blindCode` until sensory observations are committed. Distinguish **objective descriptors/intensities** from **subjective affective preference**.
- Exact numerical scales, terminology and required-field rules MUST be transcribed only after verifying the applicable official CVA document and license; store the document revision. Do not hardcode unverified 0–10 or 80-point thresholds.
- On phone: pinned blind code, progress indicator, large sliders or stepper only if scale is valid, editable notes, autosave, offline undo, break timer and temperature prompts. Provide a table view for tablet.
- Export app-native structured data, and separately link to/download original official SCA PDFs from official website when user requests reference. An app-created form must not visually masquerade as SCA's official document.

### 5.2 Suggested entities (illustrative Kotlin domain schema)

```kotlin
enum class ProtocolFamily { CVA, SCA_2004_LEGACY, CUSTOM }
enum class AssessmentKind { PHYSICAL, DESCRIPTIVE, AFFECTIVE, EXTRINSIC }
enum class EvidenceStatus { MEASURED, OBSERVED, REPORTED, INFERRED, UNKNOWN, NOT_MEASURED }
enum class SessionState { DRAFT, PREPARED, IN_PROGRESS, REVIEW, LOCKED, AMENDED }

data class ProtocolSpec(
    val id: String, val family: ProtocolFamily, val version: String,
    val sourceUrl: String, val retrievedAt: String,
    val fieldMappingVersion: String, val licenseStatus: String
)
data class CuppingSession(
    val id: String, val protocolId: String, val assessorId: String,
    val createdAt: String, val state: SessionState,
    val sampleIds: List<String>, val isBlind: Boolean
)
data class AssessmentRecord(
    val id: String, val sessionId: String, val blindSampleId: String,
    val kind: AssessmentKind, val schemaVersion: String,
    val fieldValuesJson: String, val revision: Int,
    val lockedAt: String?, val evidenceStatus: EvidenceStatus
)
data class PhysicalMeasurement(
    val id: String, val lotId: String, val type: String,
    val value: Double?, val unit: String?, val methodId: String?,
    val instrumentId: String?, val calibrationId: String?,
    val evidenceStatus: EvidenceStatus
)
```

**Persistence requirements:** Room for relational metadata, encrypted local storage where appropriate, SAF for media/exports, optional immutable revision snapshots, no vendor lock-in, migrations tested from every supported database version. The sample-preparation parameters are stored per session as structured records with explicit units, timestamps and operator notes. Preserve measured vs calculated vs predicted distinctions inherited from Sprint 01–03.

### 5.3 Form field-mapping acceptance workflow

1. Download or open official CVA forms and protocols from SCA; log retrieval date, revision, URL, terms and permission.
2. Create `CVA_FIELD_MAPPING.md`: official label, official definition, official scale/unit, required status, app field, data type, constraints, validation, calculation, source page.
3. Two-person independent verification for critical field mapping; store screenshots or document references privately where allowed.
4. Run golden fixtures for all official worked examples where publicly licensed; otherwise build original synthetic cases and mark as synthetic.
5. Block `SCA_CVA_CURRENT` production labeling if mapping cannot be verified; offer `CUSTOM_RESEARCH` instead.

## 6. Sensory training engine and assessment validity

### 6.1 Triangulation algorithm

For each trial choose reference coffee A and contrasting coffee B; randomly assign one of `AAB, ABA, BAA, BBA, BAB, ABB` with balanced positions across trials. Present opaque three-digit codes. Operator knows the key; participant does not. Capture the odd cup choice, timestamp, confidence and experimental controls. For independent trials with n attempts and k correct, chance probability under null is `P(X >= k) = Σ_{i=k..n} C(n,i)(1/3)^i(2/3)^(n-i)`. Do **not** interpret p-value as probability that participant has a special sensory talent; check sample independence and avoid optional stopping.

### 6.2 Taste reference solutions

Support metadata fields: taste target, food-grade chemical/ingredient, mass, solution volume or mass, concentration unit, water source, batch, maker, prep time, expiry, allergens, sanitation, source method, supervisor, verification status. **Do not embed arbitrary chemical recipes or concentrations as “official SCA exam solutions”** without verifying the actual official method. Provide training recipes only when backed by a reliable source and safe food-grade ingredients; warn against ingestion of laboratory-grade chemicals or unknown aroma-vial liquids.

### 6.3 Le Nez du Café 60

Implement two tiers: (a) **generic original aroma learning** with licensed/original descriptors and user-supplied household food references; (b) **user-owned 60-vial physical kit companion** allowing the user to type or import their vial IDs and labels for personal practice. A branded preset, complete proprietary list, product photographs or copied guide exercises requires documented licensing. No phone can smell a sample: require a real physical reference and a human response. Track familiarity, confusion, spaced repetition and kit edition/version.

### 6.4 Green grading scope

Green bean defects, equivalency factors, screen-size and acceptance criteria are **standard- and species-specific**. The old washed Arabica classification must be clearly scoped, not applied to robusta/canephora or all processing methods without verification. Support `SCA_WASHED_ARABICA_2004_REFERENCE`, `BUYER_SPEC`, `CUSTOM_LAB` as distinct profiles. A result may be `UNDETERMINED` if sample mass, defect categories, calibration, species or method is unknown. Physical analysis must not automatically generate a CVA affective result.

## 7. Mobile UI/UX wireflow

```text
Sensory & Cupping
├── Learn: Sensory Science | Taste | Aroma | Organic Acids | Green Coffee | Protocols
├── Practice: Basic Taste | Aroma 60 | Triangulation | Roast Defects | Calibration
├── Cupping Sessions
│   ├── New Session → Protocol → Coffee Lots → Blind Codes → Preparation
│   ├── Descriptive Form → Affective Form → (permissioned) Extrinsic Form
│   ├── Physical Assessment (linked to green lot, if applicable)
│   └── Review → Lock → App-native Report → Export
├── Green Coffee QC: Lot → Sample → Defects → Sieve → Moisture → Review
├── Analytics: History | Repeatability | Training Progress | Panel Comparison
└── Standards Library: Official links | Version | Rights | Legacy explainer
```

**UX pain-point controls:** avoid repeated data entry; auto-carry only verified session metadata; never auto-fill sensory scores; show blank vs zero distinctly; autosave on each field; tactile feedback optional; big touch targets; contrast compliant; resume session in 2 taps; prevent accidental reveal; offer temperature/time cues without blocking data entry; quick-swap samples while preserving unsaved notes; allow left-handed use; offline banner only if an online resource is actually needed.

## 8. QA acceptance test matrix (minimum release suite)

| ID | Test | Expected outcome |
|---|---|---|
| QA-001 | Upgrade DB from Sprint 01/02/03 with existing recipes, sensory and equipment | Zero data loss, stable IDs |
| QA-002 | Airplane mode, new CVA session, enter data, export | Fully functional offline |
| QA-003 | App killed mid-form and relaunched | Exact draft restored |
| QA-004 | Select modern CVA protocol | Only verified current field mapping shown |
| QA-005 | Select 2004 legacy | Explicit LEGACY banner and isolated scoring |
| QA-006 | Compare 2004 and CVA result | No false numeric equivalence |
| QA-007 | Attempt descriptive score from affective field | Rejected / prevented |
| QA-008 | Reveal origin before blind assessment | Permission denied or explicitly audited |
| QA-009 | Enter invalid official-scale value | Validation blocks invalid value |
| QA-010 | Missing official form schema revision | Production standards mode blocked, custom fallback labeled |
| QA-011 | Lock assessment then edit | New amendment revision; original preserved |
| QA-012 | Create 6 triangle trials | All six permutations supported and balanced |
| QA-013 | Participant before triangle submit | Correct answer not exposed |
| QA-014 | Triangle statistical golden cases | Binomial calculation correct, caveats visible |
| QA-015 | Five-taste lesson | Sweet, sour, salty, bitter, umami all present |
| QA-016 | Organic-acid quiz | Does not assert specific acid from sourness alone |
| QA-017 | Aroma kit without license | Generic/custom mode works, proprietary list not copied |
| QA-018 | Aroma test without physical reference | Not marked as actual olfactory performance |
| QA-019 | Green grade for unknown species/method | Out-of-scope/undetermined, no fabricated grade |
| QA-020 | Missing moisture measurement | `NOT_MEASURED`, never default zero |
| QA-021 | Defect tally changes after lock | Amendment/audit event required |
| QA-022 | Screen reader and large font on small Android device | All fields and controls accessible |
| QA-023 | Export and re-import JSON | Lossless roundtrip with versions and units |
| QA-024 | Source and license metadata missing | Asset/standard release gate fails |
| QA-025 | Q Grader practice screen | No official credential claim or certificate |
| QA-026 | Existing flavor wheel, recipe, grinder/dripper menus | All remain functional and linked |
| QA-027 | Two assessors blind same coffee | Separate identity, no cross-contamination |
| QA-028 | Phone rotated or low memory | No loss or duplicate submission |
| QA-029 | Offline RAG assistant asked for unmeasured TDS or grade | Explicit uncertainty; no invented measurement |
| QA-030 | Delete user data or export | Privacy, consent, retention rules enforced |

### Gherkin examples

```gherkin
Feature: CVA forms do not conflate descriptive and affective assessments
  Scenario: Enter a descriptive observation
    Given a cupping session using a verified current CVA protocol
    And the sample identity is blind
    When I record a descriptive aroma observation
    Then the observation is saved to the descriptive assessment only
    And no affective quality score is automatically inferred

Feature: Offline triangle test
  Scenario: A blinded odd-one-out trial
    Given the phone is in airplane mode
    And a trainer has prepared two identical and one different coffee sample
    When a participant submits their odd-cup choice
    Then the answer is evaluated against a concealed key
    And the trial records the sample order and timestamp
    And the application does not award official Q Grader certification

Feature: Historical data preservation
  Scenario: Migrating a Sprint 03 database
    Given an existing database containing grinder profiles, dripper profiles and cupping notes
    When the Sprint 04 migration completes
    Then all existing entities retain their IDs and contents
    And all new optional assessment fields are nullable or safely defaulted
    And no destructive migration is used
```

## 9. Implementation sequencing and definition of done

**Phase 0 — repo audit & standards verification:** inventory existing screens/DB and compare SCA official PDF form revisions, document license status and exact mappings. Deliver `SPRINT_04_GAP_ANALYSIS.md`, `CVA_FIELD_MAPPING.md`, `SENSORY_CONTENT_SOURCE_REGISTER.md`.

**Phase 1 — schema and protocol engine:** additive migrations, protocol-versioned forms, evidence status, sample identity/blinding, immutable revisions. Add tests before UI.

**Phase 2 — mobile CVA:** four perspectives, prep workflow, autosave, phone UX, exports, protocol-scoped validation.

**Phase 3 — learning academy:** 22 lessons, original diagrams, five basic tastes, organic acid science, aroma kit companion, offline quizzes.

**Phase 4 — practice lab:** triangle engine, reference solutions, blind tasting, aroma recognition, calibrated analytics.

**Phase 5 — green QC:** lot/defect/sieve/moisture workflows, rule scope, audit exports, photos.

**Phase 6 — professional release:** accessibility, regression, privacy, provenance/license audit, physical-device QA, offline cold start and backup/restore.

### Mandatory release gates

- [ ] All 65 backlog stories individually marked `DONE / BLOCKED / DEFERRED` with commit and test evidence.
- [ ] Current official SCA form field mapping verified; otherwise do not market as standards-conformant.
- [ ] No copied proprietary SCA wheel graphics, Le Nez 60 material, handbook or unlicensed form facsimiles.
- [ ] No false Q Grader/R Grader/SCA credential claims.
- [ ] No existing Sprint 01–03 feature, database record, recipe, sensory asset or route removed.
- [ ] All 30 QA cases executed or explicitly marked blocked with reason and reproduction details.
- [ ] On-device test of offline workflow, screen readers, process death, export and re-import.
- [ ] Clear evidence of human sensory observation for actual taste/aroma/triangle performance.
- [ ] Protocol version, field mapping, calibration and evidence provenance included in exported reports.

## 10. Primary references and further reading

1. SCA — Coffee Value Assessment overview, protocol and **official downloadable forms**: https://sca.coffee/value-assessment/
2. SCA — Published coffee standards: https://sca.coffee/research/coffee-standards
3. SCA — Official Q Grader course and training components: https://sca.coffee/education/q-grader
4. SCA — How to prepare for physical assessment and roasting exam: https://sca.coffee/education/how-to-prepare
5. SCA — Coffee Skills Program (Sensory Skills): https://education.sca.coffee/coffee-skills-program
6. SCA — Coffee Taster's Flavor Wheel usage and lexicon: https://sca.coffee/sca-news/how-to-use-the-flavor-wheel-in-eight-steps-ab2hy
7. SCA — Science of five basic tastes: https://sca.coffee/sca-news/coffee-decoded-7-smellwords
8. SCA — Coffee Sensory and Cupping Handbook publication: https://sca.coffee/sca-news/read/announcement/just-published-the-new-coffee-sensory-and-cupping-handbook-2yg3l
9. SCA — Indonesia CVA partnership: https://sca.coffee/sca-news/sca-and-scai-collaboration-arfrn
10. World Coffee Research — Sensory Lexicon: https://worldcoffeeresearch.org/resources/sensory-lexicon
11. Éditions Jean Lenoir — Le Nez du Café, proprietary training kit: https://www.editions-jeanlenoir.com/
12. CQI — historical Q/R program information and scope verification: https://www.coffeeinstitute.org/
13. SCA — CVA familiarization exercises: https://sca.coffee/sca-news/cva-in-action-activities-9g33z
14. SCA — 2024 standards superseding the 2004 cupping form: https://sca.coffee/sca-news?category=Features

**Research limitation:** This specification uses published SCA overview, education and standards pages; it does **not** purport to reproduce or validate every field in every official downloadable PDF, nor every official exam preparation concentration. Exact form mapping and legally permitted use are explicit blocking implementation tasks. Do not fabricate numeric SCA tolerances, scores, sample mass or defect equivalence factors.
