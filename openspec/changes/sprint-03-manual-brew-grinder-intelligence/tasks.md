# Tasks: Sprint 03 (Manual Brewing & Grinder Intelligence)

## Phase 1: Baseline Audit & Evidence Verification
- [x] Record baseline database schema, record counts, and UI routes (Sprint 01/02 state)
- [x] Audit `Pasted text.txt` (44 grinders) and `Pasted text (2).txt` (26 drippers) for exact fields and typos
- [x] Execute video reference audit on `https://youtu.be/UKGkM8xJj60`; generate timestamped evidence inventory or maintain `PENDING_VIDEO_REVIEW` flag
- [x] Create test fixtures for 44 grinders and 26 drippers with immutable source receipts

## Phase 2: Domain Engine & Contracts (Pure Kotlin JVM)
- [ ] Define grinder domain enums and models (`GrinderType`, `AdjustmentUnit`, `EvidenceStatus`, `GrinderModelRecord`, `GrinderSetting`, `UserGrinderProfile`) in `app/src/main/java/com/coffeecreator/app/core/equipment/GrinderModels.kt`
- [ ] Define dripper domain enums and models (`BrewerGeometry`, `FlowClass`, `FlowMode`, `ValveState`, `DripperModelRecord`, `FilterCompatibility`) in `app/src/main/java/com/coffeecreator/app/core/equipment/DripperModels.kt`
- [ ] Implement `GrindGuidanceEngine`: model-specific starting range resolver with zero cross-model conversion
- [ ] Implement `ManualBrewScalingEngine`: deterministic dose/water ratio calculations, staged pour planning, and mass conservation validation
- [ ] Implement `DialInHeuristicEngine`: reversible single-variable recommendation logic based on brew time and sensory feedback
- [ ] Write pure JVM unit test suite in `app/src/test/java/com/coffeecreator/app/core/equipment/EquipmentEngineTest.kt` covering TC-304, TC-308, TC-309, TC-310, TC-311, DRIP-002, DRIP-003, DRIP-004, DRIP-005, DRIP-008

## Phase 3: Data Layer, Seed Provenance & Additive Migration (Room)
- [ ] Create seed record datasets in `ReferenceCatalogRecords.kt` / `EquipmentSeedData.kt` containing all 44 grinders and 26 drippers with raw source fields retained
- [ ] Integrate grinder models, dripper models, user grinder profiles, and brew diagnostics into catalog models
- [ ] Verify migration preserves all existing beans, recipes, sensory observations, document chunks, and FTS4 tables
- [ ] Write tests covering TC-301, TC-318, TC-319, DRIP-001, DRIP-010

## Phase 4: Repositories & Use Cases
- [ ] Expose grinder and dripper catalog in `CoffeeViewModel` / `CoffeeViewModelCatalog`
- [ ] Update recipe and brew journal flows to link equipment snapshots and brew diagnostics
- [ ] Add allowlisted typed tools (`lookup_grinder_range`, `calculate_brew_ratio`, `recommend_dial_in_step`) with strict schema validation
- [ ] Verify offline RAG policies reject hallucinated grind settings

## Phase 5: UI/UX Jetpack Compose Workbench & Offline Timer
- [ ] Implement Grinder Catalog UI with search, Hand/Electric filter, and model variant detail card
- [ ] Implement Zero Calibration Wizard with model-specific safety warnings and offset capture
- [ ] Implement Dripper Catalog UI with geometry, valve state toggles (Switch/Pulsar), and OREA V4 bottom selector
- [ ] Implement Manual Brew Composer with equipment picker, ratio calculator, and staged pour planner
- [ ] Connect Offline Brew Timer with monotonic clock recovery, haptic cues, audio chimes, and manual weigh-in logging
- [ ] Implement Post-Brew Diagnostics and Dial-In Assistant screen with Indonesian Roda Rasa sensory tagging
- [ ] Implement A/B Brew Comparison for dial-in iterations
- [ ] Verify Material 3 styling, 48dp minimum touch targets, TalkBack labels, and 200% font scaling across all screens

## Phase 6: QA Verification Gates & BDD Test Execution
- [ ] Execute automated JVM test suite (`./gradlew testDebugUnitTest`)
- [ ] Verify all 44 grinder rows searchable and electric models properly classified (TC-301, TC-302)
- [ ] Verify unknown drippers do not show fake flow rates (TC-303, DRIP-002)
- [ ] Verify Comandante clicks never convert to 1Zpresso clicks (TC-304, DRIP-008)
- [ ] Verify zero calibration update preserves old brew records (TC-305)
- [ ] Verify timer backgrounding and rotation maintain monotonic accuracy (TC-312, TC-313)
- [ ] Verify airplane mode functionality with zero network traffic (TC-314)
- [ ] Verify manual brewing without AI model loaded (TC-315)
- [ ] Verify talkback and font scaling compliance (TC-320)
- [ ] Mark any untested physical device tests as `NOT_RUN` (TC-324)

## Phase 7: Traceability, Documentation & Release Readiness
- [ ] Generate traceability linking GR-01..12 and BR-01..18 to code and tests
- [ ] Bump application version to `v1.13.0` (versionCode `23`)
- [ ] Build release APK, compute SHA-256, update download links and release notes
- [ ] Commit and push to GitHub
