# BDD Scenarios: UI/UX Tactile Microinteraction & Anti-AI-Slop Refinement (Sprint 03)

## Feature: Tactile Microinteractions & Anti-AI Slop on Sprint 03 Surfaces

### Scenario 1: Semantic MicroBadgePills replace raw bracket strings in Grinder Catalog
Given the user navigates to "Library" -> "Grinders & Gear" -> "44 Grinders Catalog"
When the grinder catalog is rendered
Then no item displays unparsed bracket strings like `[HAND]` or `[MANUFACTURER_VERIFIED]`
And each grinder card displays structured `MicroBadgePill`s:
  | Pill Type    | Value                   | Color Semantic               |
  | Grinder Type | HAND / ELECTRIC         | Bronze (Crema) / Purple      |
  | Dial Unit    | ROTATION / CLICKS / ... | Slate / Steel                |
  | Verification | VERIFIED / USER         | Emerald (Pass) / Amber       |
And each badge pill strictly enforces single-line text bounds (`maxLines = 1`, `softWrap = false`).

### Scenario 2: Structured 3-Tile Monospace Grind Range Readout
Given the user inspects a grinder in the catalog (e.g., Comandante C40 MK4)
When the grind range section is displayed
Then coarse, medium, and fine ranges are presented in a structured 3-tile grid
And all numeric values are rendered with tabular monospace figures (`fontFeatureSettings = "tnum"`)
And grinders marked `espressoCapable = false` display an amber "Filter Only" warning badge on the fine tile.

### Scenario 3: Numbered Step-by-Step Zero Calibration Wizard
Given the user taps "Zero Calibration Guide" on a calibrated grinder (e.g., Timemore Chestnut C2)
When the calibration accordion expands
Then each step is displayed inside an encapsulated container with numbered indicator (1, 2, 3...)
And concentric corner geometry is enforced: R_step = max(0, R_card - padding)
And the card provides a tactile "Got it / Close" action with scale compression (`0.97f`) and haptic feedback
And the guide cleanly collapses without layout jitter or orphaned white space.

### Scenario 4: Animated Segmented Valve Switch on Hybrid Drippers
Given the user views a hybrid brewer in the 26-dripper catalog (e.g., Hario Switch or Clever Dripper)
When the brewer card is rendered
Then an interactive segmented valve toggle pill is displayed showing active state
When the user taps the valve toggle
Then the valve state switches between OPEN (Percolation) and CLOSED (Immersion)
And the switch animates thumb position smoothly with medium-bouncy spring
And tactile haptic feedback (`TextHandleMove`) is dispatched on touch down
And the effective flow advice and brew plan update accordingly.

### Scenario 5: Filter Paper Fit Check Callout
Given the user selects a dripper and tests filter paper compatibility
When "Check Filter Fit" is toggled
Then a tactile callout card appears with 1px hairline border
And a MicroBadgePill displays COMPATIBLE (Green), ADAPTATION (Amber), or INCOMPATIBLE (Red)
And explanatory recommendation notes are displayed with single-line chip selectors.

### Scenario 6: Dial-In Assistant Advice Card in Brew Workbench
Given an active or completed brew session with recorded drawdown time or sensory feedback
When the user inspects the Brew screen
Then the Dial-In Assistant card displays calculated advice
And tabular monospace digits show elapsed seconds vs target seconds
And tactile quick action buttons ("Adjust Finer", "Adjust Coarser", "Keep Setting", "Log Diagnostic") allow one-tap adjustments to recipe grind parameters.

### Scenario 7: Anti-AI Slop Single-Line Text Invariant
Given any screen displaying Grinder Catalog, Dripper Catalog, Recipe Composer, or Dial-In Assistant
When text elements (buttons, badge pills, category tabs, filter chips) are rendered
Then all buttons and chips enforce `maxLines = 1`, `softWrap = false`, and `overflow = TextOverflow.Ellipsis`
And no button or chip expands vertically into a circular or tall oval blob.
