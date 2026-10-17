# OpenSpec Proposal: Sprint 04 EPIC B — Digital CVA Cupping & Coffee Grading (S4-011 to S4-026)

## Context & Objectives
Implement the complete, production-grade Digital CVA (Coffee Value Assessment) cupping engine and coffee grading module in Coffee Creator.
Integrates the complete 221-descriptor Sensory Wheel (84 SCA/WCR reference + 137 expanded regional/Indonesian descriptors) directly into the cupping sheet and provides a 1-click bridge to the Brew Journal.

### Core Standards & Architecture:
- **SCA CVA Standards**: SCA-102 (Sample Prep & Tasting Mechanics), SCA-103 (Descriptive Assessment), SCA-104 (Affective Assessment), SCA-105 (Extrinsic Assessment).
- **Historic 2004 SCA Cupping Form**: Preserved exclusively as a labeled `SCA_2004_LEGACY` option with zero automated score conversion.
- **Sensory Wheel Full Integration**: 221 descriptors across 9 primary categories and 35 subcategories mapped directly into the descriptive cupping form.
- **Cryptographic Auditability**: SHA-256 seal on session lock, immutable amendment revision history ($R+1$), and premature unblinding audit logs (`UNBLINDED_PREMATURELY_AUDITED`).
- **Tactile Barista UI**: Offline-first, one-handed mobile ergonomics, touch targets $\ge 48\text{dp}$, concentric geometry $R_{\text{inner}} = \max(0, R_{\text{outer}} - \text{Padding})$, 1px hairline borders (`alpha = 0.40f`).

## Scope & Story Breakdown (S4-011 – S4-026)
1. **S4-011: Multi-Sample Session & Controlled Prep Metadata**: Multi-sample setup (1–10+ samples, 5 cups/bowls standard), cupper pseudonym, lot IDs, equipment (grinder, kettle, bowls), and prep parameters (water TDS, temperature, grind microns, ratio).
2. **S4-012: Protocol Selector & Prominent Banners**: Protocol selector (`SCA_CVA_CURRENT`, `SCA_2004_LEGACY`, `CUSTOM_RESEARCH`) with visible warning badges and isolated calculations.
3. **S4-013: Digital Physical Form**: Agtron/RoastQuik roast color (bean & ground), quakers count in 100g, moisture % with uncertainty ($\pm 0.2\%$, `NOT_MEASURED` guard), green defect count, and sieve distribution.
4. **S4-014: Digital Descriptive Form & 221-Descriptor Wheel Integration**: 0.0 to 5.0 intensity scales (fragrance, aroma, flavor, aftertaste, acidity, sweetness, bitterness, mouthfeel, finish). Direct selection from 221-descriptor wheel. NO total score.
5. **S4-015: Digital Affective Form**: Hedonic 6.00 to 10.00 scale (in 0.25 steps) across fragrance, aroma, flavor, aftertaste, acidity, sweetness, mouthfeel, overall, minus defect deductions. Strictly decoupled from descriptive intensity.
6. **S4-016: Digital Extrinsic Form**: Origin, region, farm/station, producer, altitude (MASL), variety, processing, harvest, certifications, price/kg, and traceability URL.
7. **S4-017: Extrinsic Blinding & Premature Unblind Audit**: Blinds metadata as `[CONCEALED]` during active tasting. Early unblinding requires explicit reason and logs `UNBLINDED_PREMATURELY_AUDITED`.
8. **S4-018: Guided Cupping Stepper & Timing**: Step-by-step prep: dry fragrance, 4:00 min crust bloom, crust break (3 stirs), hot round (~70°C), warm round (~60°C), cool round (~40°C), and non-blocking haptic cues.
9. **S4-019: Autosave, Recovery & Undo**: Real-time autosave to Room, process death recovery with `SavedStateHandle`, and undo/redo history stack.
10. **S4-020: Mobile Ergonomics & Tactile Barista UI**: Thumb-reachable bottom actions, $\ge 48\text{dp}$ touch targets, tabular numerals (`tnum`), tactile press compression (`scale = 0.97f`).
11. **S4-021: 5-Cup Multi-Cup Evaluation Grid**: 5-bowl grid per sample, cup-level defect classification (Taint -2 pts vs Fault -4 pts), uniformity & clean cup score tracking.
12. **S4-022: Field Validation & Strict Protocol Calculations**: Protocol-defined bounds only; no invented descriptive scores; legacy 100-pt scale isolated.
13. **S4-023: Cryptographic Lock & Amendment History**: SHA-256 seal on lock. Amendments preserve original snapshot and log versioned revisions.
14. **S4-024: Native Data Export (JSON/CSV/PDF)**: App-native export with protocol version, mapping version, and legal disclaimer disclaiming official SCA certification.
15. **S4-025: Side-by-Side Multi-Coffee Comparison**: A/B sample comparison, multi-axis sensory radar comparing Descriptive axes, difference deltas, and evidence confidence indicators (`MEASURED`, `OBSERVED`, `REPORTED`).
16. **S4-026: Historical 2004 Cupping Form Archive**: Dedicated legacy 100-point view with amber warning banner; no automated conversion to CVA.
17. **Coffee Grader / Brew Journal Integration**: 1-click action to export cupping sample directly into `BrewJournalEntry` with sensory scores, taste notes, and prep parameters.

## Invariants & Non-Goals
- ZERO cloud APIs or remote inference. 100% offline-first.
- NO claims of official SCA, Q-Grader, or CQI certification grant.
- NO invented descriptive total scores (CVA descriptive has NO total score).
- NO silent or lossy conversion between legacy 2004 scores and CVA.
- NO destructive migrations: Sprint 01–03 recipes, grinders, and logs remain 100% intact.
