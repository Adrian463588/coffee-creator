# Architecture & Design: Epic S2-E01 (Bean, Ingredient & Equipment Foundation)

## 1. Mathematical Specifications

### 1.1 Freshness & Degassing Kinetics
Effective rest days $t_{\text{eff}}$ accounts for storage state:
$$t_{\text{eff}} = \max\left(0, (\text{Date}_{\text{eval}} - \text{Date}_{\text{roast}}) - \sum \Delta t_{\text{frozen}}\right)$$
If a bag is currently frozen, aging is paused ($k_{\text{aging}} \approx 0$).

State bands:
- `TOO_FRESH_DEGASSING`: $t_{\text{eff}} < t_{\text{peak\_start}}$
- `PEAK_FLAVOR`: $t_{\text{peak\_start}} \le t_{\text{eff}} \le t_{\text{peak\_end}}$
- `STABLE_MATURE`: $t_{\text{peak\_end}} < t_{\text{eff}} \le t_{\text{stale}}$
- `STALE_OXIDIZED`: $t_{\text{eff}} > t_{\text{stale}}$

### 1.2 Water Chemistry Equations
- Total Hardness ($GH$ in ppm $\text{CaCO}_3$):
  $$GH = 2.4973 \cdot [Ca^{2+}] + 4.1179 \cdot [Mg^{2+}]$$
- Total Alkalinity ($KH$ in ppm $\text{CaCO}_3$):
  $$KH = 0.82016 \cdot [HCO_3^-]$$
- Estimated TDS:
  $$\text{TDS} = [Ca^{2+}] + [Mg^{2+}] + [Na^+] + [K^+] + [HCO_3^-]$$

### 1.3 Green Physical QC & Defects
$$\text{DefectPoints} = \text{Cat1Defects} \times 1.0 + \text{Cat2Defects} \times 0.20$$
Specialty Grade 1: 0 Cat 1 defects, $\le 5.0$ Cat 2 points, Moisture $10.0-12.0\%$, $a_w < 0.65$.

### 1.4 Roast Curve & DTR
$$\text{DTR}\% = \left(\frac{t_{\text{drop}} - t_{\text{FC}}}{t_{\text{drop}}}\right) \times 100\%$$
$$\text{RoR}_i = \frac{T_i - T_{i-1}}{t_i - t_{i-1}} \times 60 \quad (^\circ\text{C/min})$$

## 2. Component Structure
- `com.coffeecreator.app.core.catalog`:
  - `CatalogModels.kt`: Pure data structures with immutable properties and `BigDecimal` fields.
  - `RoastRestCalculator.kt`: Pure heuristic computation engine.
  - `WaterChemistryCalculator.kt`: Molar conversion and preset provider.
  - `InventoryDeductionEngine.kt`: Safe non-negative bag mass deduction.
  - `CatalogImportExportEngine.kt`: Export payload serialization and conflict-resolving importer.
- `CoffeeViewModel.kt`: StateFlow exposing `beanCatalog`, `equipmentCatalog`, `grinders`, `waters`, `greenLots`, `roastBatches`.
- `CoffeeApp.kt`: Multi-tab Library screen: Beans & Inventory, Grinders & Gear, Water Chemistry, Green & Roast QC, and Bulk Data Tools.
