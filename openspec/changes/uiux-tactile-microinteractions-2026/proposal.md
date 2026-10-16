# OpenSpec Proposal: 2026 Tactile Industrial & Precision Coffee Lab UI/UX Refinement

## Context & Objectives
In accordance with the modern 2026 UI/UX Design System Specification (`panduan_best_practice_ui_ux_dan_design_system_2026.md`, `UIUX_ANTI_AI_SLOP.md`), this change elevates the tactile microinteractions and visual ergonomics of Coffee Creator across all screens:
1. **Physical Micro-Depression**: Standardize tactile spring compression (`scale = 0.97f`, `Spring.DampingRatioMediumBouncy`) across all interactive targets (`TactileButton`, `TactileOutlinedButton`, `TactileFilterChip`, `Select` dropdowns, and clickable cards).
2. **Subtle Haptic Confirmation**: Trigger `LocalHapticFeedback.current.performHapticFeedback(HapticFeedbackType.TextHandleMove)` on interactive clicks (chips, buttons, dropdown selectors).
3. **Concentric Nested Corner Radii**: Enforce $R_{\text{inner}} = \max(0, R_{\text{outer}} - P)$ and 1px hairline outline borders (`MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.40f)`).
4. **Tabular Monospace Metrics**: Enforce `fontFeatureSettings = "tnum"` on timers, masses, flow rates, TDS, and ratios to prevent horizontal jitter during rapid live sensor updates.
5. **Ergonomic Safety & Accessibility**: Enforce 48dp minimum vertical touch targets and single-line constraints (`maxLines = 1, softWrap = false, overflow = TextOverflow.Ellipsis`) on all button labels and chips.
