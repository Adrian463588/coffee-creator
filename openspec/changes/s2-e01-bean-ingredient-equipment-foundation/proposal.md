# Change: S2-E01 — Coffee bean, ingredient and equipment foundation

## Why
Specialty coffee formulation and dialing in relies on exact, reproducible agricultural, chemical, and physical inputs. Prior to this epic, bean records lacked detailed bag inventory tracking, rest-date freshness heuristics, grinder geometry (burr profiles and zero offsets), water mineral ion chemistry (Ca2+, Mg2+, alkalinity as CaCO3), green coffee physical QC, roast curves, and conflict-resolving bulk import/export.

## What Changes
1. **Bean Agronomic CRUD & Provenance (S2-001)**: Roaster, origin country, region, farm, producer, varietal, process, altitude masl, harvest date, roast date, roast level.
2. **Bag Inventory Lifecycle (S2-002)**: Statuses (`PURCHASED`, `ACTIVE`, `FINISHED`, `FROZEN`), package sizes, remaining mass (g), automatic brew deduction, sub-zero freeze pause tracking.
3. **Roast-Rest Calculator (S2-003)**: Explainable degassing and oxidation heuristic based on roast level and processing method; freezing pauses effective age.
4. **Grinder Calibration Records (S2-004)**: Clicks, microns, stepless degrees, numerical marks, RPM, burr profiles (conical/flat, diameter mm, material, zero-point offset).
5. **Equipment Catalog (S2-005)**: Drippers, filters, kettles, scales, espresso machines, portafilter baskets, puck-prep tools.
6. **Water Chemistry Engine (S2-006)**: Calcium, Magnesium, Sodium, Bicarbonate ppm converted to Total Hardness and Alkalinity (ppm CaCO3) and calculated TDS, plus SCA/Rao/Hendon presets.
7. **Food-Grade Ingredient Catalog (S2-007)**: Density (g/mL), allergens, supplier max % limit, CAS/datasheet provenance, non-food-grade gate.
8. **Bag Offline OCR (S2-008)**: Image URI attachment with bundled heuristic parser extracting fields with confidence scores and mandatory user confirmation.
9. **Bulk Import/Export with Conflict Resolution (S2-009)**: JSON/CSV payload import/export with `OVERWRITE`, `KEEP_BOTH_RENAME`, and `SKIP_DUPLICATE` strategies.
10. **Global Offline Search & Favorites (S2-010)**: Instant search across beans, equipment, tags, and favorites.
11. **Green Coffee Physical QC (S2-011)**: Moisture %, bulk density (g/L), water activity (aw), Category 1 & 2 defects, screen size distribution, sample lot status.
12. **Roast Batches & Curve Data (S2-012)**: Charge temp, drop temp, duration, first crack, DTR %, Rate-of-Rise (RoR) data points, blend formulation summing to 100%.

## Non-Goals
- No cloud synchronization, remote roaster feeds, or external weather/market APIs.
- No ungrounded AI flavor claims without human sensory evidence.
