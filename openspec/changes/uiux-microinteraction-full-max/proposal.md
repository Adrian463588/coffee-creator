# OpenSpec Proposal: UI/UX Tactile Microinteraction Full-Max Refinement

## Context
Following the stabilization of the zero-crash timer and BLE scale flows in v1.12.1, the application requires a comprehensive UI/UX microinteraction pass across all 8 core screens (`Home`, `Formulate/Brew`, `AI Chat`, `Library`, `Journal`, `Sensory`, `Models`, and `Settings`).
Currently, several interactive controls use raw Material 3 buttons, unstyled pills, or non-tactile touch targets lacking physical press scale compression, spring physics, subtle haptics, and accessible 48dp touch heights. Additionally, text wrapping on buttons risks producing distorted circular/oval blobs (AI-slop).

## Objectives
1. **Microinteraction Full-Max**:
   - Provide tactile micro-depression (`scale = 0.97f` on press with `Spring.DampingRatioMediumBouncy`) across all interactive touch targets.
   - Provide instant tactile haptic feedback (`HapticFeedbackType.TextHandleMove`) on touch down.
   - Add tactile feedback and top hairline separation to the bottom navigation bar and navigation rail.
2. **Anti-AI-Slop Geometry & Typography**:
   - Guarantee 1px hairline borders (`BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.40f))`).
   - Enforce concentric nested corner radius geometry: $R_{\text{inner}} = \max(0, R_{\text{outer}} - \text{Padding})$.
   - Enforce single-line text constraints (`maxLines = 1`, `softWrap = false`, `overflow = TextOverflow.Ellipsis`) on all buttons, badges, and chips to prevent awkward multiline wrapping.
   - Apply tabular monospace figures (`fontFeatureSettings = "tnum"`) on all live telemetry, timers, masses, and ratios to prevent layout jitter.
3. **Ergonomic Accessibility**:
   - Guarantee minimum 48dp accessible touch target heights (`heightIn(min = 48.dp)`) across all action buttons, scale multipliers, and filter chips.
4. **Minimal Diff (Ponytail)**:
   - Implement reusable drop-in tactile primitives (`TactileFilledTonalButton`, `TactileTextButton`, `Modifier.tactilePress`) with zero modification to underlying ViewModel flows, data models, or test tag contracts.

## Non-Goals
- Altering core brewing physics equations, database schemas, or AI inference adapters.
- Introducing remote telemetry or third-party UI dependencies.
