# OpenSpec Tasks: 2026 Tactile Industrial & Precision Coffee Lab UI/UX Refinement

- [x] **Task 1: Core Tactile Modifiers & Haptic Primitives**
  - Update `TactileFilterChip`, `TactileButton`, and `TactileOutlinedButton` with `scale = 0.97f`, `Spring.DampingRatioMediumBouncy`, and `LocalHapticFeedback.current.performHapticFeedback(HapticFeedbackType.TextHandleMove)`.
  - Enforce `Modifier.heightIn(min = 48.dp)` for accessible touch targets.
  - Implement `Select` dropdown using `TactileOutlinedButton` with rotation arrow animation and concentric border.

- [x] **Task 2: Tabular Monospace Figures (FontFeatureSettings "tnum")**
  - Update Active Brew chronometer display (`mm:ss`) to use tabular figures.
  - Update live scale mass (`%.1f g`) and flow rate (`%.2f g/s`) readouts with `fontFeatureSettings = "tnum"`.
  - Update extraction yield (`%.2f %`) and brew ratio (`1:%.1f`) readouts.

- [x] **Task 3: Concentric Geometry & Hairline Borders**
  - Standardize card hairline border strokes to `BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.40f))`.
  - Ensure nested elements adhere to $R_{\text{inner}} = \max(0, R_{\text{outer}} - P)$.

- [x] **Task 4: Quality Gates & Verification**
  - Verify all JVM unit tests pass 100% (`./gradlew.bat testDebugUnitTest`).
  - Verify build compilation (`./gradlew.bat assembleDebug`).
  - Verify on connected physical ADB device (`Samsung Galaxy S20 Ultra 5G`, `SM-G988B`).

- [x] **Task 5: Version Bump, Git Commit, Push & GitHub Release**
  - Bump to `v1.9.6` (`versionCode = 15`, `versionName = "1.9.6"`).
  - Update `README.md`, `SHA256SUMS.txt`, and release notes `docs/releases/v1.9.6.md`.
  - Commit with conventional commit, push to `origin/main`, and publish GitHub Release `v1.9.6`.
