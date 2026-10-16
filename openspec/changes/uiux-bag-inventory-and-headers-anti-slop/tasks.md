# Implementation Tasks: UI/UX Anti-Slop Ergonomics & Microinteraction Refinement

- [ ] **Phase 1: Primitives Refactoring**
  - [ ] Standardize `TactileButton` in `CoffeeApp.kt` to `scale = 0.96f`, `RoundedCornerShape(8.dp)`, and customizable `contentPadding`.
  - [ ] Introduce `TactileOutlinedButton` with `scale = 0.96f` and `RoundedCornerShape(8.dp)`.

- [ ] **Phase 2: Bag Inventory Surface Encapsulation**
  - [ ] Wrap bag item row in `Surface` with 1px outline border and `RoundedCornerShape(10.dp)`.
  - [ ] Render remaining/total mass in monospace tabular digits (`fontFeatureSettings = "tnum"`).
  - [ ] Add compact status chip badge.
  - [ ] Replace standard buttons with `TactileOutlinedButton` ("Freeze"/"Thaw") and `TactileButton` ("Brew -18g") with `maxLines = 1, softWrap = false`, `height(34.dp)`, compact padding.

- [ ] **Phase 3: Action Headers Modernization**
  - [ ] Journal Cafe Cups: Title `weight(1f)` + subtitle + `TactileButton` with `Icon(Add)` + `"Add Cup"`, `shape = RoundedCornerShape(8.dp)`.
  - [ ] Sensory Cupping Sessions: Title `weight(1f)` + subtitle + `TactileButton` with `Icon(Add)` + `"New Session"`, `shape = RoundedCornerShape(8.dp)`.
  - [ ] Sensory Degassing Curve: Title `weight(1f)` + subtitle + `TactileButton` with `Icon(Add)` + `"Log Obs"`, `shape = RoundedCornerShape(8.dp)`.

- [ ] **Phase 4: Quality & Verification Gates**
  - [ ] Execute `./gradlew.bat testDebugUnitTest` to ensure all 202 unit tests pass.
  - [ ] Bump version to `v1.9.3` (`versionCode = 12`).
  - [ ] Assemble debug APK `coffee-creator-v1.9.3.apk`.
  - [ ] Verify SHA-256 and update `SHA256SUMS.txt`.
  - [ ] Install on connected ADB device (`Samsung SM-G988B`) and capture screenshot verification.
  - [ ] Commit, push to GitHub, and create GitHub release `v1.9.3`.
