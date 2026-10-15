# Change Proposal: UI/UX Anti-Slop Tactile Navigation Refinement (Journal & Sensory)

## Problem Statement
Following the visual inspection of device captures:
1. **Journal Screen**: Two clusters of stacked chips consume ~35% of the upper vertical viewport:
   - 6 category tabs (`Brews & Logs`, `Espresso Dial-In`, `Compare (Select)`, `Analytics & Streak`, `Cafe Cups`, `Export & Tools`) wrap across 3 uneven rows.
   - 6 method filters (`All`, `V60`, `Espresso`, `AeroPress`, `Chemex`, `High Rated`) wrap across 2 uneven rows.
2. **Sensory Screen**: Staggered chips wrap across multiple rows:
   - 6 sensory mode tabs (`Flavor Wheel`, `Accessible List`, `Cupping Sessions`, `Consensus & Radar`, `Physical QC & Rest`, `Calibration Lab`) wrap across 3 rows.
   - 10 flavor wheel taxonomy categories wrap across 3 rows.
3. This visual clutter ("AI-slop") causes cognitive strain, misaligned touch targets, and severe vertical layout pushing on 20:9 mobile displays.

## Proposed Solution
Apply the precision 2026 Tactile Industrial & Precision Coffee Lab standard:
1. **Journal Category & Filter Rows**:
   - Replace staggered `FlowRow` category tabs with single-line `Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()))`.
   - Wrap each tab in `TactileFilterChip` with dedicated M3 icons, medium-bouncy spring micro-depression (`scale = 0.96f`), and minimum 48dp touch targets.
   - Replace staggered `FlowRow` method filters with single-line horizontal scrollable row with `TactileFilterChip` and 44dp+ touch targets.
2. **Sensory Category & Taxonomy Filter Rows**:
   - Replace staggered `FlowRow` sensory tabs with single-line horizontal scrollable row with `TactileFilterChip`.
   - Replace staggered `FlowRow` flavor taxonomy filters with single-line horizontal scrollable row with `TactileFilterChip`.
3. **Preserve Invariants & Accessibility**:
   - Preserve 100% test tags (`journal:tab_*`, `journal:query`, `journal:show_archived`, `sensory:tab_*`, `sensory:wheel_search`, etc.).
   - Zero modifications to Room DAOs, ViewModels, or calculation math.
   - Meets Android M3 touch target and TalkBack accessibility standards.
