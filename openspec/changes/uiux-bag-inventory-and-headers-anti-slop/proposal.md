# Change Proposal: UI/UX Anti-Slop Ergonomics & Microinteraction Refinement (Bag Inventory & Action Headers)

## Problem Statement
Visual inspection of live Android device captures and user feedback revealed three layout regressions ("AI-slop"):
1. **Bag Inventory Squeeze in Library (`CoffeeApp.kt:1725-1755`)**:
   - An unconstrained row inside the bean card had an unweighted left column.
   - Squeezed action buttons forced `"Brew -18g"` to wrap into 2 lines (`Brew\n-18g`), inflating M3's default pill capsule vertically into an awkward tall oval/circle (~64dp+), misaligning with `"Freeze"`.
   - Items lacked container encapsulation, reading as an unstyled raw list.
2. **Action Header Horizontal Space Cannibalization (`CoffeeApp.kt:2680, 3120, 3510`)**:
   - Long title `Cafe Cup Logbook (Manual, Zero Maps)` consumed >75% row width, forcing `+ Add Cafe Cup` button into 3-line wrap (`+ Add\nCafe\nCup`) inside M3's pill shape, creating an accidental giant circular blob.
   - Similar risk on `Cupping Sessions` and `Roast-Rest Degassing Curve (S2-062)`.
3. **Microinteraction Inconsistency**:
   - `TactileButton` had `scale = 0.97f` vs `TactileFilterChip` `scale = 0.96f`.
   - Missing `TactileOutlinedButton` primitive.
   - Inconsistent button corner radii causing distorted bubble shapes.

## Proposed Solution
Apply the 2026 Tactile Industrial & Precision Coffee Lab standard:
1. **Encapsulated Bag Inventory Item**:
   - Wrap each bag in a sleek `Surface` with 1px hairline border, `RoundedCornerShape(10.dp)`.
   - Tabular monospace numbers for mass readout (`fontFeatureSettings = "tnum"`).
   - Dedicated status chip badge (`[ACTIVE]`, `[FROZEN]`, etc.).
   - Sleek `TactileOutlinedButton` ("Freeze"/"Thaw") and `TactileButton` ("Brew -18g") with strict single-line constraints and `RoundedCornerShape(8.dp)`.
2. **Action Headers with Single-Line Tactile Buttons**:
   - Title weighted with `Modifier.weight(1f)` plus secondary subtitle.
   - Action buttons use `Icon(Icons.Default.Add)` + concise labels ("Add Cup", "New Session", "Log Obs") with strict `maxLines = 1` and `softWrap = false`.
   - Standardized `RoundedCornerShape(8.dp)` prevents circular bubble distortion under any screen width or font scale.
3. **Microinteractions**:
   - Standardized spring micro-depression `scale = 0.96f` on press across all tactile buttons and chips.
