# Scientific and standards audit

**Baseline:** `6d429df9299d438daefbdac769b5300988d83f9c`. The current JVM suite freshly passed, but no physical samples, refractometer, moisture meter, water-activity instrument, sensory panel, or SCA license was supplied. Code and domain tests do not establish that a reading occurred or a person perceived a descriptor.

## Coffee calculation contracts

| Claim | Source and audit evidence | Finding |
|---|---|---|
| Approximate extraction yield (EY) | `CoffeeScienceEngine.kt:19–66`; golden case in `CoffeeScienceEngineTest.kt:10–25`; run under `:app:testDebugUnitTest --rerun-tasks` passed. | Formula uses measured beverage mass × measured TDS% / dose and labels cup-only mass-balance assumptions. Golden result 18g × 270g × 1.30% / 18g = **19.50% approximate EY**. Inputs are caller-supplied as measured; instrument method and uncertainty are not supplied by this calculation result. |
| Invalid/negative math | `CoffeeScienceEngineTest.kt` and domain validation suites. | JVM tests passed for covered zero/negative cases; no UI locale or physical refractometer input was exercised. |
| Units/density | Typed `Mass`, `Volume`, `Density`, `MassFractionPercent` and conversion code in core. | Domain test coverage passed in aggregate; broad property tests and every UI conversion remain unverified. Never imply mL↔g conversion without a density. |
| TDS vs estimate | PRD FR-02/03/09; science engine requires a measured TDS input. | Source contract is aligned; no actual TDS instrument value was measured in this audit. |
| Sensory output | `SensoryRulesEngine` separates observations and estimates in domain models. | Unit source exists and passed as part of the suite; no sensory claim was validated with a human panel. Any estimated/predicted value must show uncertainty and basis. |

## Sensory taxonomy and education

- Current wheel source/test expects **221** descriptors partitioned into **84** reference-origin descriptors and **137** local extensions. The unit test that asserts the total ran in the fresh JVM suite. `FlavorDescriptor` does not provide per-descriptor source/license provenance, so vocabulary-level origin and rights remain unresolved.
- The Sprint 03 document mentions a competitor’s **82 descriptions**; this is not the same field or taxonomy as this app’s 221 entries. Counts alone cannot establish equivalence. The code/test does not establish comparable descriptor identity, hierarchy, definitions, or rights.
- `AcademyLessons.kt` contains 22 bilingual lesson records; `AcademyCurriculumTest` iterates the complete lesson ID set and ran in the fresh JVM suite. This verifies structure and test assertions, not every coffee-science sentence, translation, rights status, quiz learning, or human sensory mastery.
- Generic taste/aroma exercises can train app interactions, but reading a lesson or selecting an answer in software cannot be reported as real olfactory/taste performance. Le Nez du Café content remains a user-owned physical-kit companion only; no proprietary kit list or artwork may be copied without rights.
- Triangle statistics implement all six three-position permutations and a binomial model with chance baseline `p=1/3`; domain tests ran. A real trial still requires physical samples, concealed codes, operator, washout, and timestamped participant submission.

## CVA and SCA standards

The SCA [CVA overview](https://sca.coffee/value-assessment/) identifies Physical, Descriptive, Affective, and Extrinsic assessments and links standards SCA-103, SCA-104, and SCA-105. Its [adoption announcement](https://sca.coffee/sca-news?category=Features) says standards SCA-102, SCA-103, and SCA-104 supersede the 2004 cupping protocol and form. SCA course material describes 15-point descriptive intensity, CATA, and a 9-point affective scale ([CVA for Cuppers course](https://education.sca.coffee/cva-for-cuppers-course)). The SCA forms page says current downloadable forms are available, while page copyright remains reserved. Download access does not license a third party to reproduce form appearance or redistribute assets.

Findings:

- The app has `SCA_CVA_CURRENT`, `SCA_2004_LEGACY`, and `CUSTOM_RESEARCH` protocol values in code, but the required `CVA_FIELD_MAPPING.md` is absent. The current protocol model uses version label `2024.1`; descriptive intensity code includes a 0–5 range. Exact field definitions, unit, scale, required/optional status, calculations, page/source, and permitted use were not verified from the official PDFs (the static PDF endpoint was inaccessible in this research pass).
- Sprint 04 expressly says to block production `SCA_CVA_CURRENT` labeling when mapping is unverified and to offer `CUSTOM_RESEARCH`. Therefore current standards-conformance behavior is **BLOCKED** (D-008). This is not an assertion that every domain field is wrong; it is a missing mandatory evidence gate.
- CVA describes four complementary assessments rather than a single quality score. Descriptive observations must not become preference scores. Any affective score calculation must be independently verified against SCA-104 and labeled with method/version; do not imply an official SCA grade or certification.
- Legacy 2004 records must stay explicitly historical and must not be converted into current CVA totals. Exact current mappings and any proprietary fields/artwork require legal/standards review before shipping.

## Green coffee physical assessment

`GreenGradingModels.kt` provides a versioned deterministic engine, unsupported-scope states, and tests for no-data/unknown conditions. The freshly executed JVM suite passed its existing green grading tests, including unmeasured moisture handling. This does not prove a green sample was actually measured or validate a real instrument’s accuracy/calibration.

Current UI contradicts that domain policy: Green QC initializes lot/producer/equipment details, moisture `10.8`, water activity `0.585`, density `710.0`, and the associated measured flags as local prefilled state (`SensorySprint04Screens.kt:2708–2763`). Green export also uses a hardcoded assessor and discards its computed output. Treat these as fabricated until user measurement entry with provenance is connected; do not export them as fact. See D-001 and D-002.

Before any score/grade is shown, the audit needs the source standard/revision, species/process scope, actual instrument/method/calibration/uncertainty, sample mass, defect taxonomy and evidence. An out-of-scope species/process must remain `OUT_OF_SCOPE`/`UNDETERMINED`; a physical green grade must never be conflated with sensory CVA quality or a “specialty” label.

## Provenance and licensing gates

Required source records are not complete: no `SENSORY_CONTENT_SOURCE_REGISTER.md`, `CVA_FIELD_MAPPING.md`, or `SPRINT_04_GAP_ANALYSIS.md` was found. Maintain a content register for lesson/descriptor references, translations, model licenses, and standards/form use. Do not copy protected flavor-wheel art, SCA form facsimiles, championship recipe text, or proprietary aroma-kit catalogs without permission. For recipe templates, verify winner/year/event source and permitted paraphrase; an 80-row count is not proof of attribution.

## Limits and next tests

This audit did not perform sensory evaluation, standards conformity testing, licensed-document review, equipment calibration, physical green grading, or independently adjudicated RAG fact checking. The next acceptance suite must combine pure-JVM golden/boundary tests with verified standard mappings, a real blank-state UI, source-bound measurement entry, export round-trip, sample provenance, and supervised human tests. Preserve the current measured/calculated/predicted/unknown distinctions throughout.
