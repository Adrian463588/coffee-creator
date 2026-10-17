# Tasks: Sprint 04 EPIC B — Digital CVA Cupping (S4-011 to S4-026)

## Phase 1: OpenSpec Specification Baseline [DONE]
- [x] Create `proposal.md` defining CVA 4-perspective architecture, 221-descriptor wheel integration, and 1-click brew journal bridge.
- [x] Create `bdd-scenarios.md` with complete Gherkin scenarios for S4-011 through S4-026.
- [x] Create `tasks.md` tracking execution across pure domain engine, tests, UI, and hardware verification.

## Phase 2: Pure Domain Engine Extensions (`com.coffeecreator.app.core.sensory.cva.*`)
- [ ] Extend `CvaModels.kt`:
  - [ ] Add equipment metadata to `CuppingSamplePreparation`: grinder model, burr set, burr size mm, grind setting/microns, kettle model, spout type, water bowl volume, vessel material.
  - [ ] Add `MultiCupEvaluationGrid`: 5-bowl state, cup-level defect classification (`Taint` vs `Fault`), intensity (1.0–5.0), and notes.
  - [ ] Add 221-descriptor taxonomy linkage to `DescriptiveAssessment` referencing `FlavorDescriptor` objects with category colors and accessibility labels.
  - [ ] Add `Legacy2004CuppingSheet` data model with 10 attributes, 5-box uniformity/clean cup/sweetness checkboxes, and 100-pt calculation.
  - [ ] Add `CuppingComparisonDelta` model computing A/B deltas, radar axes, and confidence metrics.
  - [ ] Add `toBrewJournalEntry` extension on `SampleAssessment` mapping directly to `BrewJournalEntry`.
- [ ] Extend `CvaServices.kt`:
  - [ ] Implement `CuppingStepperEngine` managing timing milestones (dry fragrance, 4:00 crust break, 8:00 hot, 12:00 warm, 18:00 cool).
  - [ ] Implement `CvaComparisonEngine` calculating multi-sample variance, radar coordinate projection, and descriptor overlap.
  - [ ] Implement `CvaValidationEngine` validating strict protocol boundaries (0.0–5.0 descriptive, 6.00–10.00 affective, 85–98°C water temp).
  - [ ] Enhance `CvaReportExportService` with complete JSON schema v1, CSV formatting, and app-native text/PDF reports.

## Phase 3: Unit Tests (TDD First)
- [ ] Extend `CvaDomainEngineTest.kt`:
  - [ ] Test S4-011: Multi-sample setup, equipment metadata, and 3-digit blind code collision prevention.
  - [ ] Test S4-012: Protocol selector banners and strict refusal of cross-protocol score conversion.
  - [ ] Test S4-013: Physical assessment Agtron, quakers count in 100g, moisture % with uncertainty, and `NOT_MEASURED` guard.
  - [ ] Test S4-014: Descriptive assessment with 221-descriptor selection and absence of total score.
  - [ ] Test S4-015: Affective assessment decoupling from descriptive intensities.
  - [ ] Test S4-016 & S4-017: Extrinsic concealing and `UNBLINDED_PREMATURELY_AUDITED` logging.
  - [ ] Test S4-018: Stepper timer milestone progression and temperature cue emissions.
  - [ ] Test S4-019: Session state serialization, process death restoration, and undo/redo stack.
  - [ ] Test S4-021: 5-cup evaluation grid, taint (-2) vs fault (-4) deductions, uniformity calculation.
  - [ ] Test S4-022: Protocol bounds validation.
  - [ ] Test S4-023: SHA-256 seal computation and versioned amendment history.
  - [ ] Test S4-024: Native JSON and CSV export verification.
  - [ ] Test S4-025: Side-by-side A/B comparison deltas and radar metrics.
  - [ ] Test S4-026: Legacy 2004 isolated 100-pt score calculation.
  - [ ] Test 1-click Brew Journal mapping (`SampleAssessment.toBrewJournalEntry`).
- [ ] Run `./gradlew.bat testDebugUnitTest` and achieve 100% green.

## Phase 4: UI/UX Implementation & Wheel Integration
- [ ] Refactor `SensoryCvaCuppingSection` in `SensorySprint04Screens.kt`:
  - [ ] Implement Protocol Selector with prominent warning badges (`SCA_CVA_CURRENT`, `SCA_2004_LEGACY`, `CUSTOM_RESEARCH`).
  - [ ] Build 5-Cup Multi-Cup Grid with interactive bowl toggles (Clean / Defective).
  - [ ] Integrate 221-Descriptor Sensory Wheel Sheet: hierarchical category drill-down (9 categories -> 35 subcategories -> 221 descriptors) with instant search.
  - [ ] Build Guided Cupping Stepper & Timer with 4:00 crust break audio/vibration cue and temperature prompts (70°C, 60°C, 40°C).
  - [ ] Build Side-by-Side Comparison modal with radar chart and delta comparisons.
  - [ ] Build 1-click "Log to Brew Journal" button with confirmation sheet.
  - [ ] Apply 2026 Tactile Industrial Design invariants:
    - Minimum touch targets $\ge 48\text{dp}$.
    - Concentric corners $R_{\text{inner}} = \max(0, R_{\text{outer}} - \text{Padding})$.
    - Hairline borders (`alpha = 0.40f`).
    - Tabular figures (`tnum`).
    - Tactile button press compression (`0.97f`).

## Phase 5: Hardware & Release Verification
- [ ] Execute `./gradlew.bat testDebugUnitTest`.
- [ ] Build release APK (`./gradlew.bat assembleDebug`).
- [ ] Install on physical target device `RRCN3008VYE` with zero data loss.
- [ ] Perform live physical cupping session run: timer break, 5-cup grid, 221 wheel descriptor tagging, SHA-256 lock, and 1-click journal export.
- [ ] Capture physical device screenshot on `RRCN3008VYE`.
- [ ] Deliver completion report with full test and hardware verification evidence.
