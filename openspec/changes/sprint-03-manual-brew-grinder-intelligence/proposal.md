# Proposal: Sprint 03 — Manual Brewing & Grinder Intelligence

## 1. Problem Statement
Coffee extraction is governed by fluid dynamics, particle surface area, and thermal stability. In manual brewing, baristas and coffee enthusiasts encounter critical challenges:
1. **Grinder Non-Equivalence & Setting Ambiguity**: Clicks, rotations, and numbers differ dramatically across grinder architectures (e.g. Comandante 30 µm/click vs 1Zpresso J-Ultra 8 µm/click vs Fellow Ode stepped dial). Converting settings across models without physical calibration ruins extraction.
2. **Dripper Flow Discrepancies**: Different dripper geometries (cone, flat bed, trapezoid, immersion, hybrid with valve) exhibit radically different drainage and bypass behaviors. Assuming a universal "medium grind" leads to stalling, channeling, or hollow cups.
3. **Pseudoscience & Hallucinated Taste Claims**: Many digital tools claim deterministic flavor outcomes from grind settings alone, ignoring roast solubility, water composition, and pouring agitation.
4. **Data Loss During Upgrades**: Updates must never disrupt historical recipes, cupping logs, or inventory from Sprints 01 and 02.

## 2. Proposed Architecture & Solutions

### 2.1 Grinder Intelligence System (GR-01 .. GR-12)
- **44-Model Grinder Catalog (`GrinderIntelligenceCatalog`)**:
  - All 44 models imported losslessly with original raw strings (`rawValue`) and clean normalized values.
  - Classification: `HAND` vs `ELECTRIC`.
  - Typed setting representations: `CLICK`, `ROTATION`, `DIAL_NUMBER`, `MARK`, `NOTCH`, `STEPLESS`.
- **Model Variants & Burrs (`GR-02`)**:
  - Differentiates revisions, burrs (e.g., standard vs Red Clix on C40, heptagonal vs standard, titanium vs stainless).
- **Zero Calibration Wizard (`GR-03`)**:
  - Model-specific safe zeroing procedures (burr touch / handle lock vs dial alignment). Saves user calibration with timestamp and burr condition.
- **Strict Typed Settings & No Implicit Conversion (`GR-04`, `INV-GR-02`)**:
  - Grinder settings are strictly scoped to the specific grinder model. Implicit mathematical conversion between different models is forbidden.
- **Manufacturer Evidence Overlay (`GR-05`)**:
  - Distinct evidence classifications: `MANUFACTURER_VERIFIED`, `USER_SUPPLIED_UNVERIFIED`, `THIRD_PARTY`, `USER_CALIBRATED`, `CONFLICTED`. Conflicting manufacturer vs user ranges are preserved side-by-side.
- **Grinder Profile & Range Finder (`GR-06`, `GR-07`)**:
  - Profile tracking owned grinders, burr wear, custom notes. Method-based range finder (Espresso, Aeropress, V60/Pour Over, Chemex, Cold Brew, French Press, Cupping, Tubruk).
- **Dial-in Assistant (`GR-08`)**:
  - Systematic, reversible One-Variable-At-A-Time (OVAT) recommendations based on observed drawdown and taste (sour/under-extracted -> finer; bitter/astringent -> coarser). Explicitly avoids deterministic taste claims.
- **Grinder History & Compatibility Guard (`GR-09`, `GR-10`)**:
  - Links grinder revision and setting to every brew log. Explicit warning for unsupported methods (e.g. Fellow Ode Gen 2 or 1Zpresso ZP6 for espresso).
- **Grind Measurement & Calibration Transfer (`GR-11`, `GR-12`)**:
  - Optional Kruve sieve/micrometer metadata only if physically measured. JSON import/export with schema versioning for calibration profiles.

### 2.2 Manual Brewing & Equipment Intelligence (BR-01 .. BR-18)
- **26-Dripper Catalog (`DripperIntelligenceCatalog`)**:
  - All 26 source rows imported losslessly. Unknown parameters strictly labeled `UNKNOWN` (never fabricated).
- **Brewer Geometry & Flow Mode (`BR-02`)**:
  - Geometries: `CONE`, `FLAT_BOTTOM`, `TRAPEZOID`, `IMMERSION_HYBRID`, `NO_BYPASS`.
  - Flow classes: `FAST`, `MODERATE`, `RESTRICTED`, `VALVE_CONTROLLED`, `UNKNOWN` (qualitative, never invented `mL/s`).
  - Valve states: Explicit `OPEN` vs `CLOSED` modeling for Switch, Clever, Pulsar, Neo 01.
- **Filter Compatibility (`BR-03`)**:
  - Documented compatible paper/metal filters (e.g., Kalita Wave 155/185, V60 01/02/03, Chemex Bonded, Sibarist).
- **Manual Brew Composer & Staged Pours (`BR-04`, `BR-05`, `BR-06`)**:
  - Bean, grinder, dripper, filter, water mineral recipe, dose, ratio, temperature, and staged pour strategy.
  - Pure Kotlin deterministic mass calculations: bloom, multiple pulses, cumulative target masses, and duration.
- **Offline Brew Chronometer & Diagnostics (`BR-07`, `BR-08`, `BR-09`)**:
  - Monotonic clock timebase (`SystemClock.elapsedRealtime()`), manual weigh-in support, drawdown/clogging/channeling observation recording.
- **Sensory & Champion Integration (`BR-11`, `BR-12`, `BR-13`, `BR-14`)**:
  - Curated championship & traditional recipes (76+ recipes), 221-descriptor Roda Rasa sensory link, side-by-side A/B comparison.
- **Local AI & Accessibility (`BR-15`, `BR-16`, `BR-17`)**:
  - Offline typed Kotlin tools, 48dp touch targets, TalkBack semantics, high-contrast M3 tokens, video reference audit tag (`PENDING_VIDEO_REVIEW`).
- **Zero Data Loss Migration (`BR-18`)**:
  - Additive catalog integration; 100% preservation of all existing Sprint 01/02 data.

## 3. Invariants
- **INV-GR-01 (Lossless Catalog)**: All 44 grinders and 26 drippers from the blueprint must be preserved with raw and normalized text.
- **INV-GR-02 (No Fake Click Conversion)**: Never translate clicks between different grinder models.
- **INV-GR-03 (Filter-Only Espresso Guard)**: Warn if filter-oriented grinder is selected for espresso.
- **INV-BR-01 (Qualitative Flow Class)**: Never fabricate numeric `mL/s` flow rates for drippers.
- **INV-BR-02 (Valve State Modeling)**: Immersion hybrid drippers must clearly distinguish open vs closed valve stages.
- **INV-BR-03 (Zero Regression & Offline-First)**: Full functionality offline without cloud dependencies; no deletion of Sprint 01/02 records.
