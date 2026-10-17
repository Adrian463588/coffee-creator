# Implementation Tasks: UI/UX Tactile & Precision Refinement (v1.14.5)

## Phase 1: Shared Tactile Primitives [COMPLETED]
- [x] Create `app/src/main/java/com/coffeecreator/app/ui/components/TactileComponents.kt`:
  - [x] Export `TactileButton`, `TactileOutlinedButton`, `TactileFilledTonalButton`, `TactileTextButton`, `TactileFilterChip`.
  - [x] Export `Modifier.tactileClickable(enabled, scaleDown, onClick)`.
  - [x] Export `concentricInnerRadius(outerRadius, padding)` and `concentricShape(...)`.
  - [x] Export `TactileCard(...)` with $1\text{px}$ hairline border, surfaceContainerLow/surfaceVariant, and concentric padding.

## Phase 2: Core Shell & Workbench Refactoring [COMPLETED]
- [x] Refactor `CoffeeApp.kt`:
  - [x] Delegate tactile primitives to `com.coffeecreator.app.ui.components.*`.
  - [x] Add tactile spring depression and sleek pill indicator to `NavigationBarItem` / `NavigationRailItem`.
  - [x] Enforce `tnum` on numeric `Field(...)` inputs.
  - [x] Replace `BrewCard` with `TactileCard`.
  - [x] Apply `tnum` to step progression, cumulative targets, and proximity badges.
  - [x] Convert Bag Inventory items to `TactileCard` with concentric shape $R_{inner} = 4\text{dp}$.

## Phase 3: Sensory Sprint 04 Screens Refactoring [COMPLETED]
- [x] Refactor `SensorySprint04Screens.kt`:
  - [x] Upgrade 5-bowl grid cups with `Modifier.tactileClickable`.
  - [x] Apply `fontFeatureSettings = "tnum"` to cupping total score, 10 attribute sliders, MCD, and panel consensus metrics.
  - [x] Expand Green Defect Counter buttons to $48\text{dp}$ touch target + haptic feedback + `tnum` count.
  - [x] Replace raw `Button` with `TactileButton` in training drills and academy quizzes.
  - [x] Replace raw `Card` with `TactileCard` across lessons, consensus cards, and physical QC cards.

## Phase 4: Wheel Visualizer & Brew Section Polish [COMPLETED]
- [x] Refactor `SensoryWheelVisualizer.kt`:
  - [x] Replace dialog action buttons with `TactileButton` / `TactileTextButton`.
  - [x] Apply tactile scale depression to filter chips.
- [x] Refactor `BrewSection.kt`:
  - [x] Verified 48dp height and spring scale physics in `BrewSectionTabs`.

## Phase 5: Build, Test & Hardware Verification Gate [COMPLETED]
- [x] Bump version to `versionCode = 30`, `versionName = "1.14.5"`.
- [x] Run `./gradlew testDebugUnitTest assembleDebug`.
- [x] Install on physical target device `RRCN3008VYE` with zero data loss (`adb install -r -d`).
- [x] Capture physical screen evidence.
- [x] Verify logcat for zero exceptions.

## Phase 6: Documentation, Commit, Tag & GitHub Release [COMPLETED]
- [x] Create `docs/releases/v1.14.5.md`.
- [x] Prepend entry in `SHA256SUMS.txt`.
- [x] Update `README.md` latest release badge, download links, and Historical Versions archive.
- [x] Ensure GitHub repo description and topics are up to date via `gh repo edit`.
- [x] Git commit, tag `v1.14.5`, push `origin main --tags`.
- [x] Publish GitHub release with `gh release create v1.14.5`.
