# OpenSpec Proposal: Tactile Settings & Personalization Anti-Slop Refinement

## Context & Problem
In `SettingsScreen` (under "Subsystem Degradation Status" card):
1. **Unconstrained Row Wrap**: Badge containing `badgeText` ("Offline Barista Workstation" / "Stasiun Barista Offline") shares an unconstrained horizontal `Row(SpaceBetween)` with the card title. The title consumes ~210dp, leaving ~90-130dp, which forces the 27-character badge text to wrap into 2 lines ("Offline Barista\nWorkstation").
2. **Bloated Bubble Blob**: Inside `Surface(shape = RoundedCornerShape(12.dp))`, the 2-line wrapped text inflates the badge into a ~45dp tall bubble blob, visually disjointed and misaligned with the header.
3. **Unstyled Telemetry**: Telemetry text (`LLM: — Manual   OCR: — Manual   BLE: — Offline`) is plain text lacking tabular figures (`tnum`) and industrial pill enclosure.
4. **Interactive Component Debt**:
   - `SettingsScreen` language toggles (`EN`/`ID`) and brewing units (Celsius/Fahrenheit, Grams/Ounces, mL/fl oz, masl/ft) use raw `FilterChip` (no spring micro-depression, no haptic click, no 48dp target).
   - `PersonalizationScreen` persona presets, palate quiz dialog (5 categories), and OVAT chips use raw `FilterChip`.
   - Diagnostics and Backup action buttons use raw `Button` / `OutlinedButton`.
   - Cards lack 1px hairline border (`outlineVariant 0.40f`) and concentric geometry ($R_{inner} = \max(0, R_{outer} - P)$).

## 2026 Tactile Industrial Principles
- **Concentric Geometry**: Outer Card $R_{outer} = 14\text{dp}$, $P = 16\text{dp} \implies$ child banner/pill $R_{inner} = 6\text{dp}$.
- **Hairline Tactility**: 1px solid border (`MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.40f)`).
- **Tabular Precision**: `fontFeatureSettings = "tnum"` on telemetry metrics to eliminate horizontal jitter.
- **Physical Micro-Depression**: `scale = 0.97f` with `Spring.DampingRatioMediumBouncy` and `TextHandleMove` haptic feedback on all interactive targets.
- **Single-Line Invariant**: `maxLines = 1, softWrap = false, overflow = TextOverflow.Ellipsis` on all status badges and action chips.

## Invariant Bounds
- Badge text guaranteed single-line in full-width industrial telemetry banner.
- Telemetry indicators structured as 3 equal-weighted pills (`weight(1f)`) with status indicator dot.
- 100% testTag preservation (`settings:degradation_card`, `settings:lang_en`, `settings:lang_id`, `settings:units_card`, `settings:diagnostics_card`, etc.).
