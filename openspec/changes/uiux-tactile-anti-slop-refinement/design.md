# Technical Design: Tactile Precision Lab & Microinteractions

## Architectural Boundaries
- Pure presentation changes localized to `com.coffeecreator.app.ui.screens.CoffeeApp.kt`.
- Zero changes to pure JVM domain engines, calculation models, Room database schemas, or ViewModels.
- 100% preservation of all Compose `Modifier.testTag(...)` contracts.

## Reusable Microinteraction Primitives
1. `Modifier.tactilePress(pressedScale: Float = 0.96f)`:
   - Uses `remember { MutableInteractionSource() }` and `interactionSource.collectIsPressedAsState()`.
   - Smoothly animates scale with `spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)`.
   - Applied to interactive chips, primary buttons, and action controls.
2. `LivePulseIndicator(isActive: Boolean, modifier: Modifier, color: Color)`:
   - Infinite transition breathing pulse: scale `1.0f` to `1.5f`, alpha `0.8f` to `0.0f`.
   - Provides clear visual status indication ("ARMED" vs "ACTIVE BREWING").

## Component Refactoring
### 1. Library Filter Bar
- Single horizontal scrollable row with `Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())`.
- Standardized leading icons:
  - Beans & Bags: `Icons.Default.Coffee`
  - Grinders & Gear: `Icons.Default.Settings`
  - Water Chemistry: `Icons.Default.WaterDrop`
  - Ingredients: `Icons.Default.Science`
  - Green & Roast QC: `Icons.Default.Spa`
  - Import / Export: `Icons.Default.ImportExport`
- 48dp minimum touch target, tactile click feedback.

### 2. Unified Precision Laboratory Console
- Outer container: `Surface` with `RoundedCornerShape(16.dp)`, outline border, and subtle container elevation.
- Header: Live pulse dot + state label + scale connection indicator.
- Integrated Telemetry Display (`testTag("timer:display")`):
  - Primary elapsed time (%02d:%02d) with monospace tabular numbers (`fontFeatureSettings = "tnum"`).
  - Secondary live mass readout (`g`) and rolling flow rate (`g/s`).
- Step Progression & Proximity Badge:
  - Preserves `testTag("timer:step_card")` and `testTag("timer:proximity_badge")`.
- Scale Action Row:
  - Preserves `testTag("timer:scale_card")`, `timer:connect_scale`, `timer:disconnect_scale`, `timer:tare_scale`, and `timer:sim_pour_*`.
- Primary & Secondary Execution Controls:
  - Full-width 54dp primary CTA (`testTag("timer:toggle")`) with tactile scale effect.
  - Aligned secondary actions row (`testTag("timer:skip_step")`, `testTag("timer:restart_step")`, `testTag("timer:reset")`, `testTag("timer:finish_and_log")`).
- Preference Toggle Row:
  - `timer:toggle_awake`, `timer:toggle_sound`, `timer:toggle_haptics`.
