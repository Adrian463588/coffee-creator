# Implementation Tasks: UI/UX Tactile Microinteraction Full-Max Refinement

- [ ] **Task 1: Core Tactile Primitives Expansion (`CoffeeApp.kt`)**
  - Add `TactileFilledTonalButton` with spring compression (`scale = 0.97f`), haptics, and min 48dp height.
  - Add `TactileTextButton` with spring compression, haptics, and min 48dp height.
  - Add `Modifier.tactilePress()` reusable modifier for clickable surfaces/cards.
  - Enforce `maxLines = 1, softWrap = false, overflow = TextOverflow.Ellipsis` on button content helpers.

- [ ] **Task 2: Bottom Navigation & Home Screen Microinteractions**
  - Add haptic feedback and top hairline border to `NavigationBar` and `NavigationRail`.
  - Replace Home screen raw `Button` ("New recipe", "Continue offline") with `TactileButton`.
  - Upgrade Workbench modules to `Modifier.tactilePress()` with uniform 1px hairline borders.

- [ ] **Task 3: Brew & Active Timer Screen Refinement**
  - Upgrade floating bottom bar ("Save recipe", ratio button) to tactile buttons.
  - Upgrade Active Timer controls (BLE scale buttons, simulation increments, segmented controls "Skip", "Restart", "Reset", "Finish & Log") to tactile buttons with 48dp touch targets.
  - Upgrade toggle chips ("Screen Awake", "Audio", "Haptics") to `TactileFilterChip`.
  - Upgrade recipe dose scaling buttons (`0.5x`, `1.5x`, `2.0x`) to tactile buttons with 48dp touch targets.

- [ ] **Task 4: Library, Journal, Sensory, Chat, Models & Settings Screens Refinement**
  - Library: Upgrade header buttons, action buttons, and filter chips to tactile components.
  - Journal: Upgrade quick action buttons ("⚡ Repeat Last", "World (80)", "Export CSV") and filter chips to 48dp tactile components.
  - Chat: Upgrade persona preset buttons and recommendation chips to tactile components.
  - Models & Settings: Upgrade directory pickers, download buttons, and backup export/restore buttons to tactile components.

- [ ] **Task 5: Quality Gates & Physical Device Verification**
  - Run `./gradlew.bat ktlintCheck` (zero style errors).
  - Run `./gradlew.bat testDebugUnitTest` (all unit tests pass).
  - Bump version to `1.12.2` (`versionCode = 22`).
  - Assemble APK and install to Samsung Galaxy S20 Ultra (`SM-G988B`) via `adb install -r -d`.
  - Verify tactile microinteractions on physical device with live taps.
  - Update documentation, release notes, commit, push, and create GitHub release.
