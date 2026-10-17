# OpenSpec Proposal: UI/UX Tactile Microinteraction Full-Max & Anti-AI-Slop Refinement (Sprint 03 Surfaces)

## Context
Following the completion of Sprint 03 features (44-model Grinder Catalog, 26 Dripper Catalog, Recipe Composer equipment selectors, and Dial-In Assistant), visual inspection on Samsung Galaxy S20 Ultra (`SM-G988B`) revealed opportunities to eliminate remaining "AI-slop" text patterns and elevate UI/UX microinteractions:
1. **Raw Text Brackets**: Cards in 44 Grinders and 26 Drippers display bracketed strings like `[HAND] · [ROTATION] · [MANUFACTURER_VERIFIED]` and `[CONE_60] · [Flow: FAST]` as raw text inside `InfoCard.detail`, instead of structured, semantic `MicroBadgePill`s.
2. **Grind Range Layout**: Coarse, Medium, Fine ranges are plain text strings rather than a structured 3-tile data readout with OpenType tabular digits (`fontFeatureSettings = "tnum"`).
3. **Interactive Valve Switch**: Valve toggle button displays plain text (`Valve: OPEN (Tap to CLOSE)`) rather than an animated segmented switch with visual status indicators.
4. **Zero Calibration Accordion**: Step instructions render as raw bulleted strings rather than a numbered tactile procedure container.
5. **Dial-In Assistant in Brew**: Advice data exists in `CoffeeUiState.currentDialInAdvice`, but lacks a dedicated tactile advice card with single-tap grind adjustments in the Brew workbench.

## Objectives
1. **MicroBadgePill Primitives**:
   - Reusable badge component with 1px hairline border, subtle background, semantic status colors (Emerald for Verified, Amber for User/Unverified, Cyan/Sky for Fast flow, Purple for Electric grinder / Hybrid flow, Bronze for Hand grinder).
   - Strict single-line constraint (`maxLines = 1`, `softWrap = false`, `overflow = TextOverflow.Ellipsis`).
2. **Precision Laboratory 3-Tile Range Readout**:
   - Monospace tabular figures (`tnum`) for coarse, medium, and fine ranges.
   - Filter-only indicator badge for non-espresso grinders (e.g. Fellow Ode Gen 2, 1Zpresso ZP6 Special).
3. **Tactile Interactive Valve Switch**:
   - Animated segmented toggle pill (`OPEN (Percolation)` vs `CLOSED (Immersion)`) with physical scale compression (`scale = 0.97f`) and haptic feedback.
4. **Step-by-Step Zero Calibration Wizard**:
   - Numbered steps (1, 2, 3...) in a dedicated hairline surface with concentric corner geometry and tactile close affordance.
5. **Filter Paper Fit Check Callout**:
   - Real-time compatibility indicator card with instant visual feedback and recommendation note.
6. **Dial-In Assistant Card in Brew**:
   - Contextual advice card in `BrewSection.Recipe` & `BrewSection.Timer` with quick action buttons (`Adjust Finer`, `Adjust Coarser`, `Keep Setting`, `Log Diagnostic`).
7. **Concentric Geometry & Anti-AI Slop Invariants**:
   - $R_{\text{inner}} = \max(0, R_{\text{outer}} - \text{Padding})$.
   - 1px hairline border `outlineVariant.copy(alpha = 0.40f)`.
   - 48dp minimum accessible touch targets.

## Non-Goals
- Changing pure Kotlin domain logic, calculation formulas, or database schemas.
- Adding third-party UI dependencies.
