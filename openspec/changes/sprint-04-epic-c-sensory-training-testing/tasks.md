# Tasks: Sprint 04 EPIC C — Sensory Training & Testing (S4-027 to S4-040)

## Phase 1: OpenSpec Specification Baseline [DONE]
- [x] Formulate `proposal.md` with complete mathematical models (Binomial probability, ASTM E679 BET, Sensory SM-2, Confusion Matrix, Brier score), Kotlin domain architecture, and non-certification safety rules.
- [x] Formulate `bdd-scenarios.md` with Gherkin Given-When-Then specifications for all 14 stories (S4-027 through S4-040).
- [x] Formulate `tasks.md` defining strict phased implementation gates.

## Phase 2: Pure Domain Engine Extensions (`com.coffeecreator.app.core.sensory.training.*`)
- [ ] Extend `TrainingModels.kt`:
  - [ ] Add `TriangleSession` and `TrainingSessionState` tracking seed, operator, assessor, washout interval, and ambient conditions (S4-027, S4-029).
  - [ ] Add `AscendingConcentrationStep`, `TasteThresholdResult`, and ASTM E679 BET calculator (S4-032).
  - [ ] Add `AromaFamily`, `OlfactoryVialReference`, `AromaConfusionEntry`, and `AromaSpacedRepetitionCard` (S4-033, S4-034).
  - [ ] Add `OrganicAcidType` enum (Citric, Malic, Tartaric, Acetic, Phosphoric, Quinic, Lactic) and `OrganicAcidTrial` (S4-035).
  - [ ] Add `RoastDefectTrial` with mandatory control coffee reference and causal caveat (S4-036).
  - [ ] Add `MouthfeelAttribute` and `TactileTrial` with serving temperature validation (S4-037).
  - [ ] Add `ComparativeDrillKind`, `ComparativeTrial`, and Friedman ranking engine (S4-038).
  - [ ] Add `SensorySkillPillar`, `SkillReadinessMetric`, `SensoryReadinessDashboard`, and `PracticalExerciseVerification` (S4-039, S4-040).
- [ ] Extend `TriangleStatisticsEngine.kt`:
  - [ ] Implement `calculatePairedComparisonPValue(n: Int, k: Int)` for $p_0 = 1/2$.
  - [ ] Implement `calculateBestEstimateThreshold(steps: List<AscendingConcentrationStep>): TasteThresholdResult`.
  - [ ] Implement `updateSpacedRepetition(card: AromaSpacedRepetitionCard, qualityRating: Int): AromaSpacedRepetitionCard`.
  - [ ] Implement `computeAromaConfusionMatrix(trials: List<Pair<String, String>>): List<AromaConfusionEntry>`.
  - [ ] Implement `calculateBrierScore(predictions: List<Pair<Int, Boolean>>): Double`.
  - [ ] Implement `verifyPracticalSensoryPassGuard(verification: PracticalExerciseVerification): Boolean`.

## Phase 3: Unit & Property Tests (TDD First)
- [ ] Extend `TriangleStatisticsEngineTest.kt` to test all 14 stories.
- [ ] Execute `./gradlew.bat testDebugUnitTest` and achieve 100% green.

## Phase 4: UI/UX Implementation (Sensory Training Screens)
- [ ] Expand `SensoryTrainingLabSection` in `SensorySprint04Screens.kt`:
  - [ ] Triangle Discrimination Drill (interactive 3-cup selector, live permutation, confidence rating, seed, and trial history).
  - [ ] Basic Taste & Ascending Concentration Thresholds (step-by-step 3-AFC entry, detection vs recognition threshold display, non-clinical disclaimer).
  - [ ] Olfactory Family Identification & Aroma Memory (physical vial blind codes, hidden key reveal, confusion matrix, spaced repetition interval).
  - [ ] Organic Acid Comparative Tasting (Citric vs Malic vs Tartaric vs Acetic vs Lactic vs Phosphoric vs Quinic with safe food-grade preparation).
  - [ ] Roast Defect & Tactile Discrimination (Control baseline, temperature & TDS controls).
  - [ ] Paired Comparison (2-AFC) & Ranking Exercises (distinct from triangle).
  - [ ] Personalized Non-Certification Readiness Dashboard (6-pillar calibration radar, longitudinal summary, non-certification banner).
- [ ] Ensure 2026 Tactile Industrial Design tokens, minimum 48dp touch targets, and hairline borders.

## Phase 5: Hardware & Device Verification
- [ ] Run full test suite: `./gradlew.bat testDebugUnitTest`.
- [ ] Bump version to `versionCode = 27`, `versionName = "1.14.2"`.
- [ ] Build release APK: `./gradlew.bat assembleDebug`.
- [ ] Install on physical target device `RRCN3008VYE` with zero data loss (`adb install -r -d`).
- [ ] Capture screenshot verification of Training Lab section.
- [ ] Update `README.md`, `SHA256SUMS.txt`, and release documentation.
- [ ] Commit, push to `origin/main`, tag `v1.14.2`, and publish GitHub release.
