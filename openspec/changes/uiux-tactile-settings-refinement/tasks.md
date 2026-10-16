# Phased Execution Tasks: `uiux-tactile-settings-refinement`

- [x] **Phase 1: Telemetry Pill & Degradation Card Architecture**
  - Implement reusable private `TelemetryStatusPill` in `CoffeeApp.kt` (concentric 6dp shape, 1px hairline border, status dot, `tnum`).
  - Redesign `settings:degradation_card`: replace unconstrained header Row with structured title + full-width single-line status banner (`maxLines = 1`, `softWrap = false`).
  - Replace raw text row with 3x `weight(1f)` `TelemetryStatusPill` row (LLM, OCR, BLE).

- [x] **Phase 2: Settings Tactile Chips & Buttons Migration**
  - Migrate `settings:lang_en` and `settings:lang_id` from `FilterChip` to `TactileFilterChip`.
  - Migrate 8 brewing physics unit chips (Celsius/Fahrenheit, Grams/Ounces, mL/fl oz, masl/ft) to `TactileFilterChip`.
  - Migrate Diagnostics buttons (`settings:refresh_diag`, `settings:export_diag`) to `TactileOutlinedButton` / `TactileButton`.
  - Migrate Help topics (`help:${topic.id}`) and Backup buttons (`settings:export`, `settings:restore`) to `TactileButton` / `TactileOutlinedButton`.

- [x] **Phase 3: Personalization Tactile Migration**
  - Migrate Barista Persona chips (Q-Grader, Friendly, Chemist, Custom) to `TactileFilterChip`.
  - Migrate Palate Quiz dialog chips (Roast, Acidity, Sweetness, Bitterness, Body) to `TactileFilterChip`.
  - Migrate Palate Profile action buttons ("Take/Retake Quiz", "+ Acidity Bias") to `TactileButton` / `TactileOutlinedButton`.
  - Migrate Inventory recommendation method chips and OVAT sensory feedback chips to `TactileFilterChip`.

- [x] **Phase 4: Concentric Hairline Borders**
  - Apply `shape = RoundedCornerShape(14.dp)` and `border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.40f))` across all cards in Settings and Personalization.

- [x] **Phase 5: Verification & Contract Check**
  - Verify zero testTag breakage against existing test suites.
  - Run `./gradlew.bat testDebugUnitTest` to validate 210 tests pass.
  - Assemble debug APK and verify on Samsung Galaxy S20 Ultra (`SM-G988B`).
