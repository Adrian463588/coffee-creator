# Phased Execution Tasks: E2E ADB Device Verification (`e2e-adb-device-verification`)

## Phase 1: Environment & Hardware Pre-flight Audit
- [x] **Task 1.1: Verify Physical ADB Connection & Device Identifiers**
  - Target: `RRCN3008VYE device product:z3sxxx model:SM_G988B device:z3s` (Samsung Galaxy S20 Ultra 5G)
  - ABI: `arm64-v8a`, OS: Android 13 (API 33, OneUI 5.1)
- [x] **Task 1.2: Screen & Power Keep-Awake**
  - Set device awake and keep screen active during test execution.

## Phase 2: Code Alignment & Build Verification
- [x] **Task 2.1: Navigation & BackHandler Invariant**
  - Implement `BackHandler(enabled = route != "Home") { navigate("Home") }` in `CoffeeApp.kt`.
- [x] **Task 2.2: Journal Recipe Revisions Persistence**
  - Ensure `state.recipes` are rendered in `JournalTab.BREWS` with `journal:edit:${recipe.id}` testTag.
- [ ] **Task 2.3: Build & Install Debug Binaries**
  - Assemble and install debug + test APKs onto Samsung device (`RRCN3008VYE`).

## Phase 3: Interactive & Automated E2E Journeys Execution
- [ ] **Task 3.1: Execute Journey 1 (Navigation & Layout Integrity)**
- [ ] **Task 3.2: Execute Journey 2 (Recipe Formulation & Scaling Math)**
- [ ] **Task 3.3: Execute Journey 3 (Bag Inventory & Tactile Controls)**
- [ ] **Task 3.4: Execute Journey 4 (Active Chronometer & Telemetry)**
- [ ] **Task 3.5: Execute Journey 5 (Journal Logbook & Cafe Cup Manual Entry)**
- [ ] **Task 3.6: Execute Journey 6 (Sensory Cupping & Flavor Wheel)**

## Phase 4: Crash, ANR & StrictMode Logcat Health Audit (Journey 7)
- [ ] **Task 4.1: Logcat Real-Time Error Sweep (0 FATAL, 0 ANR, 0 StrictMode)**
- [ ] **Task 4.2: Process Memory & PSS Stability Check**

## Phase 5: Automated Instrumented Test Suite (Journey 8)
- [ ] **Task 5.1: Execute `ManualJourneyTest` on Connected Device**
- [ ] **Task 5.2: Execute `BrewSectionTest` on Connected Device**
- [ ] **Task 5.3: Verify 100% Pass Rate**
