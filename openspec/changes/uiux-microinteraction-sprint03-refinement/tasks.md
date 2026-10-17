# Tasks: UI/UX Tactile Microinteraction & Anti-AI-Slop Refinement (Sprint 03)

## Phase 1: OpenSpec Specification & Invariant Baseline [DONE]
- [x] Create `proposal.md` defining microinteraction standards and anti-AI-slop invariants.
- [x] Create `bdd-scenarios.md` with Given-When-Then criteria for badge pills, 3-tile readout, valve toggle, and calibration wizard.
- [x] Create `tasks.md` tracking phased implementation.

## Phase 2: Core Microinteraction Primitives (`CoffeeApp.kt`) [DONE]
- [x] Implement `MicroBadgePill` composable:
  - Background tint + 1px hairline border (`alpha = 0.40f`).
  - Strict single-line text (`maxLines = 1, softWrap = false, overflow = TextOverflow.Ellipsis`).
  - Semantic color palette (Emerald, Amber, Purple, Bronze, Slate, Error Red).
- [x] Implement `GrindRangeTile` and 3-tile range grid:
  - Concentric corner geometry $R_{\text{inner}} = \max(0, R_{\text{outer}} - \text{Padding})$.
  - Tabular monospace numbers (`fontFeatureSettings = "tnum"`).
  - "Filter Only" indicator for non-espresso grinders.
- [x] Implement `InteractiveValveSwitch` composable:
  - Dual-state segmented pill (`OPEN` / `CLOSED`).
  - Animated thumb with `Spring.DampingRatioMediumBouncy`.
  - Haptic feedback on tap (`TextHandleMove`).
- [x] Implement `ZeroCalibrationAccordion` container:
  - Numbered step cards (1, 2, 3...) inside hairline container.
  - Tactile close button with spring compression.

## Phase 3: Screen Integration [DONE]
- [x] Refactor Grinder Catalog tab (`CoffeeApp.kt` lines ~2905-2942):
  - Replace raw bracket string with `MicroBadgePill`s.
  - Insert 3-tile monospace grind range readout.
  - Replace raw zero instructions with structured numbered procedure container.
- [x] Refactor Dripper Catalog tab (`CoffeeApp.kt` lines ~3090-3150):
  - Replace raw bracket string with `MicroBadgePill`s.
  - Replace plain text valve button with animated segmented valve switch pill.
  - Embed structured filter compatibility callout.
  - Separate long qualitative drain rate from grind baseline text to prevent letter-by-letter wrapping.
- [x] Refactor Grinder Profiles & Calibration (`CoffeeApp.kt` lines ~3260-3285):
  - Replace raw text details with structured badge pills and hairline card.
- [x] Refactor Brew / Recipe Composer equipment telemetry (`CoffeeApp.kt` lines ~1505-1540):
  - Replace bracket string with structured telemetry callout and `MicroBadgePill`.
- [x] Refactor Journal Espresso Dial-In progression (`CoffeeApp.kt` lines ~4960-5020):
  - Elevated OVAT recommendation with `MicroBadgePill` and 3-tile metrics.

## Phase 4: Quality Gates & Physical Device Verification [DONE]
- [x] Run `./gradlew.bat testDebugUnitTest` (all 244 unit tests pass).
- [x] Bump version to `v1.13.1` (`versionCode = 24`) in `app/build.gradle.kts`.
- [x] Assemble debug APK (`./gradlew.bat assembleDebug`).
- [x] Copy to `coffee-creator-v1.13.1.apk` and compute SHA-256 hash (`5b0a0f542cc2e0513b73fd430af555d44032f61810c1f3a62e2c3ff312ee233f`).
- [x] Install without data loss (`adb -s RRCN3008VYE install -r -d coffee-creator-v1.13.1.apk`).
- [x] Verify tactile microinteractions, zero text wrapping, and zero data loss on physical Samsung Galaxy S20 Ultra (`RRCN3008VYE`).
- [x] Update `SHA256SUMS.txt`, `README.md`, and create `docs/releases/v1.13.1.md`.
- [x] Commit, push, and create GitHub release `v1.13.1`.
- [x] Provide final acceptance checklist in Caveman/BMAD format.
