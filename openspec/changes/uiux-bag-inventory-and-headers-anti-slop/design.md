# Technical Design: UI/UX Anti-Slop Ergonomics & Microinteraction Refinement

## Architectural Boundaries
- Changes localized strictly to Compose presentation components in `com.coffeecreator.app.ui.screens.CoffeeApp.kt`.
- Zero changes to pure JVM domain engines, calculation equations, SQLite/Room DAOs, or ViewModels.
- 100% preservation of all existing Compose `Modifier.testTag(...)` contracts.

## Reusable Microinteraction Primitives

### 1. `TactileButton` (Refined)
- Location: `CoffeeApp.kt`
- Target Scale: `0.96f` on press with `Spring.DampingRatioMediumBouncy` and `Spring.StiffnessLow`.
- Default Shape: `RoundedCornerShape(8.dp)` (replaces M3 capsule shape).
- Padding: `contentPadding: PaddingValues = ButtonDefaults.ContentPadding` (configurable for compact rows).

### 2. `TactileOutlinedButton` (New Primitive)
- Location: `CoffeeApp.kt`
- Target Scale: `0.96f` on press with `Spring.DampingRatioMediumBouncy`.
- Default Shape: `RoundedCornerShape(8.dp)`.
- Border: `ButtonDefaults.outlinedButtonBorder(enabled)`.
- Padding: `contentPadding: PaddingValues = ButtonDefaults.ContentPadding`.

## Component Implementations

### 1. Encapsulated Bag Inventory Item
- Container: `Surface(shape = RoundedCornerShape(10.dp), color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f), border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)))`.
- Mass Readout: `Text("${bag.remainingMassGrams}g / ${bag.packageMassGrams}g", style = MaterialTheme.typography.bodyMedium.copy(fontFeatureSettings = "tnum", fontWeight = FontWeight.SemiBold))`.
- Status Chip: Pill badge with `bag.status.name`.
- Actions:
  - Freeze/Thaw: `TactileOutlinedButton` with `Text(..., maxLines = 1, softWrap = false)`, `height(34.dp)`, compact padding.
  - Brew Deduction: `TactileButton` with `Text("Brew -18g", maxLines = 1, softWrap = false)`, `height(34.dp)`, compact padding.

### 2. Action Headers
- **Cafe Cup Logbook**: Title `Modifier.weight(1f)` + subtitle `Manual entry · Zero cloud maps` + `TactileButton` with `Icon(Icons.Default.Add)` + `"Add Cup"`, `shape = RoundedCornerShape(8.dp)`, `height(36.dp)`.
- **Cupping Sessions**: Title `Modifier.weight(1f)` + subtitle `SCA / CoE protocol evaluations` + `TactileButton` with `Icon(Icons.Default.Add)` + `"New Session"`, `testTag("sensory:btn_new_session")`.
- **Roast-Rest Kinetics**: Title `Modifier.weight(1f)` + subtitle `S2-062 degassing curve & logs` + `TactileButton` with `Icon(Icons.Default.Add)` + `"Log Obs"`.
