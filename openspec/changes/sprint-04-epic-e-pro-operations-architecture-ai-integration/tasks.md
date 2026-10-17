# Tasks: Sprint 04 EPIC E — Pro Operations, Architecture, AI, Integration (S4-052 to S4-065)

## Phase 1: OpenSpec Specification Baseline [DONE]
- [x] Formulate `proposal.md` defining mathematical models (Pearson, Spearman, MCD, Cohen Kappa, Fleiss Kappa), pure Kotlin domain models, service contracts under `com.coffeecreator.app.core.sensory.operations`, and anti-AI slop invariants.
- [x] Formulate `bdd-scenarios.md` with Gherkin Given-When-Then specifications covering all 14 stories (S4-052 through S4-065).
- [x] Formulate `tasks.md` defining phased implementation gates.

## Phase 2: Pure Domain Operations Engine (`com.coffeecreator.app.core.sensory.operations.*`) [DONE]
- [x] Create `OperationsModels.kt`:
  - [x] `SensoryPanelRole` enum (`SESSION_HOST`, `LEAD_CUPPER`, `ASSESSOR`, `OBSERVER`).
  - [x] `BlindSessionStatus` enum (`SETUP`, `CONCEALED_TASTING`, `DELIBERATION`, `REVEALED`, `AUDITED_LOCKED`).
  - [x] `AssessorProfile` data class with pseudonym, role, calibration date, and anonymized code (S4-058).
  - [x] `BlindSampleMapping` data class with 3-digit randomized code, actual coffee/roast IDs, and reveal audit fields (S4-058).
  - [x] `CuppingHistoricalLinkage` data class binding cupping results to bean, roast batch, brew recipe, and grinder setting with immutable snapshot flag (S4-055).
  - [x] `InterRaterStats` and `PanelAgreementReport` data classes capturing Pearson, Spearman, MCD, and Fleiss Kappa (S4-059).
  - [x] `TemplateVersion` data class with major/minor version, schema JSON, and deprecation metadata (S4-063).
  - [x] `SensoryAccessibilityProfile` data class for anosmia/parosmia accommodation and allergen alerts (S4-062).
  - [x] `DataExportConsent` data class for privacy-first sharing controls (S4-065).
- [x] Implement `PanelOperationsEngine.kt`:
  - [x] `calculatePearson()` with sample size $\ge 2$ and zero-variance guards ($s_x^2 > 0 \land s_y^2 > 0$).
  - [x] `calculateSpearman()` with fractional rank calculation for tied values.
  - [x] `calculateMeanConsensusDeviation()` for individual and panel-level consensus dispersion.
  - [x] `calculateFleissKappa()` with multi-rater nominal category support and Landis-Koch interpretation labeling.
  - [x] `generateBlindCodes()` with cryptographic 3-digit sample code generator with collision prevention and balanced shuffling.
  - [x] Role-based reveal permissions and tamper-evident premature reveal logging (`UNBLINDED_PREMATURELY_AUDITED`).
  - [x] Immutable historical linkage manager.
  - [x] Local citation verifier and deterministic rule validation decoupling math from LLM.

## Phase 3: Unit & Property Tests (TDD First) [DONE]
- [x] Create `PanelOperationsEngineTest.kt`:
  - [x] `testPearsonCorrelationGoldenCases()` (positive, negative, and zero correlation).
  - [x] `testPearsonCorrelationZeroVarianceGuardReturnsNull()`.
  - [x] `testSpearmanRankCorrelationWithTiedRanks()`.
  - [x] `testMeanConsensusDeviationCalculation()`.
  - [x] `testFleissKappaPerfectConsensusAndChanceCases()`.
  - [x] `testFleissKappaOutOfScopeSingleRaterGuard()`.
  - [x] `testRandom3DigitBlindCodesUniqueAndBalanced()`.
  - [x] `testAssessorBlockedFromSampleIdentityBeforeReveal()`.
  - [x] `testPrematureRevealGeneratesAuditRecord()`.
  - [x] `testLinkagePreservesHistoricalRecipeByteForByte()`.
  - [x] `testDeterministicMathOverridesLlmHallucination()`.
  - [x] `testLocalCitationVerifierRejectsUngroundedClaim()`.
  - [x] `testConsentEnforcesPiiRedaction()`.
- [x] Run test suite `./gradlew.bat testDebugUnitTest` and achieve 100% green.

## Phase 4: UI/UX Implementation in Jetpack Compose [DONE]
- [x] Add `SensoryOperationsSection` in `SensorySprint04Screens.kt`:
  - [x] Host vs Assessor role switcher and Blind Tasting Session setup with big 3-digit badges.
  - [x] Panel Agreement & Calibration card: Pearson $r$, Spearman $\rho$, Mean Consensus Deviation (MCD), Fleiss' Kappa ($\kappa$), and Landis-Koch rating badge.
  - [x] Historical Linkage card: Link cupping evaluation to Coffee Bean, Roast Batch, Manual Recipe, and Grinder.
  - [x] Accessibility & Allergen Settings: Toggle switches for Anosmia / Parosmia mode and Allergen checklist with non-clinical disclaimer.
  - [x] Privacy & Consent Export card: Checkboxes for PII redaction (supplier pricing, cupper real names) and local SHA-256 sealed JSON export.
- [x] Add Tab / Navigation in `SensoryScreen` or sub-tabs: Ensure accessible, single-row tactile filter chips, $\ge 48\text{dp}$ touch targets, hairline borders.

## Phase 5: Hardware & Device Verification Gate [DONE]
- [x] Bump version code to `29`, `versionName = "1.14.4"`.
- [x] Run `./gradlew.bat testDebugUnitTest assembleDebug`.
- [x] Install on physical target device `RRCN3008VYE` with zero data loss (`adb install -r -d`).
- [x] Verify offline operations, blind tasting, panel calibration stats, and UI on real hardware.
- [x] Capture physical screen evidence.

## Phase 6: Documentation, Commit, Tag & GitHub Release [IN PROGRESS]
- [x] Create `docs/releases/v1.14.4.md`.
- [x] Prepend entry in `SHA256SUMS.txt`.
- [x] Update `README.md` latest release badge, download links, and Historical Versions archive.
- [ ] Stage, commit `feat(sensory): sprint 04 epic e pro operations architecture ai integration (v1.14.4)`.
- [ ] Tag `v1.14.4`, `git push origin main --tags`.
- [ ] Publish GitHub release via `gh release create`.
