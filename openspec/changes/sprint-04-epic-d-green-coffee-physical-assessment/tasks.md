# Tasks: Sprint 04 EPIC D — Green Coffee Physical Assessment (S4-041 to S4-051)

## Phase 1: OpenSpec Specification Baseline [DONE]
- [x] Formulate `proposal.md` with complete mathematical models (defect equivalency, ISO 4150 sieve distribution, bulk density, moisture uncertainty, buyer contract matrix, and missing data representation), pure Kotlin domain architecture, and epistemological invariants.
- [x] Formulate `bdd-scenarios.md` with Gherkin Given-When-Then specifications for all 11 stories (S4-041 through S4-051).
- [x] Formulate `tasks.md` defining strict phased implementation gates.

## Phase 2: Pure Domain Engine Extensions (`com.coffeecreator.app.core.sensory.greengrading.*`)
- [ ] Extend `GreenGradingModels.kt`:
  - [ ] Add `ChainOfCustody` model tracking exporter, importer, warehouse, crop harvest year, transport ID, ambient temp, and ambient RH (S4-041).
  - [ ] Add `GradingStandardRef` tracking standard name, version, source citation, and supported species/methods (S4-042).
  - [ ] Extend `DefectTallyRecord` to include `evidencePhotoUris: List<String>` and nullable `equivalentFullDefects` for unverified standards (S4-043).
  - [ ] Add `SieveAnalysisResult` and `ShakerMethod` with total recovered mass, mass balance recovery check ($\Delta M \le 1.5\text{g}$), and cumulative retention $\ge \text{Screen } S$ (S4-044).
  - [ ] Extend `MoistureAssessment` with `MoistureMeasurementMethod`, `meterSerialNumber`, and strict nullable `NOT_MEASURED` guard (S4-045).
  - [ ] Add `WaterActivityAssessment`, `BulkDensityAssessment`, `GreenBeanColor`, and `GreenBeanOdor` (S4-046).
  - [ ] Enhance `StandardGreenGradingEngine.evaluateWashedArabicaGrade()` with strict species/process guard returning `UNDETERMINED_OUT_OF_SCOPE` and odor disqualification (S4-047).
  - [ ] Add `DualSpecialtyEvaluation` model decoupling physical grade from sensory CVA outcome (S4-048).
  - [ ] Add `BuyerSpecificationContract`, `ParameterTolerance`, `ContractEvaluationResult`, and lot comparison engine (S4-049).
  - [ ] Add `GreenQcExportPayload`, `GreenQcJsonSerializer`, and `GreenQcCsvExporter` with SHA-256 seal (S4-050).
  - [ ] Add `MeasurementStatus` enum (`NOT_MEASURED`, `UNKNOWN`, `OUT_OF_SCOPE`, `PENDING_REVIEW`) (S4-051).

## Phase 3: Unit & Property Tests (TDD First)
- [ ] Extend `GreenGradingEngineTest.kt` with comprehensive test cases:
  - [ ] `testChainOfCustodyAndLotCatalog()`
  - [ ] `testDefectTaxonomyAndEquivalencyRatioIntegrity()`
  - [ ] `testPhotoUriAttachmentRetention()`
  - [ ] `testIso4150SieveDistributionAndMassBalanceMismatch()`
  - [ ] `testMoistureUncertaintyAndNotMeasuredHandling()`
  - [ ] `testWaterActivityAndBulkDensityCalculation()`
  - [ ] `testFoulOdorImmediateDisqualification()`
  - [ ] `testSca2004GradeBoundariesAndDisqualification()`
  - [ ] `testOutOfScopeSpeciesAndProcessGuards()`
  - [ ] `testEpistemologicalDecouplingPhysicalVsCva()`
  - [ ] `testBuyerSpecificationContractPassFail()`
  - [ ] `testAuditTrailJsonCsvSerializationAndSha256()`
  - [ ] `testMissingDataStatesWithoutFabrication()`
- [ ] Run test suite `./gradlew.bat testDebugUnitTest` and achieve 100% green.

## Phase 4: UI/UX Implementation in Jetpack Compose
- [ ] Enhance `SensoryGreenGradingSection` in `SensorySprint04Screens.kt`:
  - [ ] Green Lot & Chain of Custody setup card (species, process, origin, lot ID, sample mass, exporter, importer, warehouse).
  - [ ] Primary Category 1 Defect counter with photo attachment button, thumbnail row, and instant disqualification banner.
  - [ ] Secondary Category 2 Defect counter with weighted ratio badges, decrement/increment, and photo attachments.
  - [ ] ISO 4150 Sieve Screen distribution card (Screens 20, 18, 16, 14, Pan) with mass balance recovery feedback.
  - [ ] Moisture & Physical Properties card (moisture %, meter ID/serial, cal-date, method, temperature, $a_w$, bulk density g/L, color, odor).
  - [ ] Epistemological Decoupling Banner (Physical Grade vs Sensory CVA Score side-by-side).
  - [ ] Buyer Contract Tolerance evaluation panel (live parameter PASS/FAIL/FLAGGED badges).
  - [ ] Audit Trail & Export panel (Export JSON, Export CSV, SHA-256 seal display).
  - [ ] Neutral slate badges for `NOT_MEASURED`, `UNKNOWN`, `OUT_OF_SCOPE`, and `PENDING_REVIEW`.
- [ ] Ensure 2026 Tactile Industrial Design tokens, $\ge 48\text{dp}$ touch targets, hairline borders (`alpha = 0.40f`), and one-handed barista ergonomics.

## Phase 5: Hardware & Device Verification
- [ ] Run full test suite: `./gradlew.bat testDebugUnitTest`.
- [ ] Bump version to `versionCode = 28`, `versionName = "1.14.3"`.
- [ ] Build release APK: `./gradlew.bat assembleDebug`.
- [ ] Install on physical target device `RRCN3008VYE` with zero data loss (`adb install -r -d`).
- [ ] Capture screenshot verification of Green Coffee Physical QC section.
- [ ] Update `README.md`, `SHA256SUMS.txt`, and release documentation.
- [ ] Commit, push to `origin/main`, tag `v1.14.3`, and publish GitHub release.
