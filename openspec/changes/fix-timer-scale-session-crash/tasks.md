# Phased Execution Tasks: `fix-timer-scale-session-crash`

- [x] **Phase 0: Investigation & Crash Root Cause Analysis**
  - Identified `DomainValidation.requireText` blank name check throwing `IllegalArgumentException` on `Dispatchers.Main.immediate`.
  - Identified fallback in `initBrewSession()` calling unprotected `buildRecipe()`.
  - Identified null session state during `tareScale()`.

- [ ] **Phase 1: Resilient Recipe Resolution Architecture**
  - Implement `resolveActiveRecipe(targetRecipe: Recipe? = null): Recipe` in `CoffeeViewModelBrewSession.kt` with 5-tier fallback cascade.
  - Implement static `defaultFallbackRecipe(): Recipe` with valid V60 defaults and two pour steps.
  - Update `createDefaultSession()` and `initBrewSession()` to use `resolveActiveRecipe()`.

- [ ] **Phase 2: Safe Coroutine Hardening & Diagnostics**
  - Wrap `connectMockScale()` in safe coroutine execution block.
  - Wrap `startBrewSession()`, `toggleTimer()`, `tareScale()`, and `resetBrewSession()` in guarded blocks.
  - Ensure `tareScale()` initializes a default session if `brewSession == null`.

- [ ] **Phase 3: Recipe Timer Tactile & Anti-Slop Upgrades**
  - Upgrade `CoffeeApp.kt` Recipe Timer buttons (`Start timer`, `Reset timer`) to `TactileButton` and `TactileOutlinedButton` with `testTag` hooks.
  - Enforce `maxLines = 1` and `softWrap = false`.

- [ ] **Phase 4: Unit & Reliability Verification**
  - Add unit test cases in `ActiveBrewTimerBleTest.kt` verifying all 5 BDD scenarios.
  - Run `./gradlew.bat testDebugUnitTest` and verify 100% pass.
  - Run `./gradlew.bat ktlintCheck`.

- [ ] **Phase 5: Device Installation & Verification**
  - Build `assembleDebug`.
  - Install to Samsung Galaxy S20 Ultra (`SM-G988B`) via `adb install -r -d`.
  - Perform live tap tests on "Connect Scale", "Tare (0.0g)", "Start Session" and Recipe Timer buttons. Verify zero crashes.

- [ ] **Phase 6: Release & Push**
  - Bump version to `v1.12.1` (`versionCode = 21`).
  - Update `docs/releases/v1.12.1.md`, `SHA256SUMS.txt`, `README.md`.
  - Git commit and push, create GitHub release.
