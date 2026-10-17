# Proposal: 2026 Tactile Industrial & Precision Coffee Lab UI/UX Refinement

## Context & Problem Statement
Coffee Creator requires professional-grade, tactile, anti-AI-slop interface engineering matching the 2026 Interface Specification (`panduan_best_practice_ui_ux_dan_design_system_2026.md`) and `DESIGN.md`. Currently, tactile primitives (`TactileButton`, `TactileOutlinedButton`, `TactileFilterChip`) are private in `CoffeeApp.kt`, leaving `SensorySprint04Screens.kt`, `SensoryWheelVisualizer.kt`, and multiple sections in `CoffeeApp.kt` using raw, un-animated M3 components. Furthermore, number updates cause horizontal layout shifting due to absent tabular font feature settings (`tnum`), and green grading buttons violate the 48dp accessibility touch target.

## Architectural Changes
1. Extract shared tactile primitives and helpers to `com.coffeecreator.app.ui.components.TactileComponents.kt`.
2. Introduce `Modifier.tactileClickable(...)` with micro-depression scale ($0.97f$), medium bouncy spring, and subtle haptic feedback.
3. Introduce `TactileCard(...)` with $1\text{px}$ crisp hairline border (`MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.40f)`) and mathematical concentric inner radius calculation ($R_{inner} = \max(0, R_{outer} - P)$).
4. Update `CoffeeApp.kt`, `SensorySprint04Screens.kt`, `BrewSection.kt`, and `SensoryWheelVisualizer.kt` to consume shared tactile primitives.
5. Apply `fontFeatureSettings = "tnum"` across all live timers, scale readings, flow rates, cupping scores, and defect counters.
6. Fix green defect counter hitboxes to ensure $\ge 48\text{dp}$ touch target compliance.

## Invariants Preserved
- Pure domain logic remains uncoupled from Compose.
- All existing Compose test tags (`nav:*`, `page:*`, `form:*`, `sensory:*`, `timer:*`) remain 100% identical.
- Offline-first: Zero network calls, zero external web fonts.
