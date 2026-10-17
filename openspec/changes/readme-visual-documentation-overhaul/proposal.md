# Proposal: README Visual Documentation Overhaul (v1.14.5 + Tactile Industrial 2026)

## Metadata
- **Change ID:** `readme-visual-documentation-overhaul`
- **Target File:** `README.md`
- **Version Target:** `v1.14.5` (Sprint 04 Epic E + Tactile Industrial UI v1.14.5)
- **Target Device:** Samsung Galaxy S20 Ultra 5G (`SM-G988B`, Serial `RRCN3008VYE`, Android 13 / API 33, 1080x2400)
- **Methodology:** BMAD SDD / Anti-AI Slop / Caveman Terse
- **Status:** APPROVED FOR IMPLEMENTATION

## Context & Motivation
Current README contains rich textual details but lacks modern visual evidence, menu screenshot galleries, and high-scanability layout. Sprint 03 (Grinders & Drippers) and Sprint 04 (Sensory Academy, Digital CVA Cupping, Sensory Training, Green Grading, Panel Calibration) introduced major architectural capabilities that must be visually showcased directly from the test device.

## Documentation Architecture Principles
1. **Typography & Scanability:**
   - Clear visual hierarchy: H1 (Hero Title), H2 (Major Pillars/Sections), H3 (Domain Features).
   - OpenType tabular numerals (`tnum`) across performance figures, test metrics, and checksums.
   - Minimal clutter: Replace prose walls with high-density comparison tables, semantic bullet chips, and structured visual cards.
2. **Visual Hierarchy & Screenshot Galleries:**
   - 12 high-resolution screenshots from connected physical S20 Ultra (`RRCN3008VYE`).
   - Grouped into thematic visual galleries using HTML grid tables with centered alignment, rounded borders, and consistent aspect ratios.
   - Captions detailing exact UI interactions (e.g. spring depression `0.97f`, `48dp` defect counters, concentric cards).
3. **Collapsible Deep Dives:**
   - Maintain instant scanability on mobile & web GitHub readers.
   - Use `<details><summary>` for deep release archives (v1.0.0 through v1.14.4), detailed directory trees, and mathematical formulas.
4. **Direct APK Download & Safety:**
   - Prominent v1.14.5 release card with direct GitHub Releases download link, verified SHA-256 hash, and safe zero-data-loss ADB command (`adb -s RRCN3008VYE install --user 0 -r -d`).
5. **Diagram Compatibility:**
   - Pure GitHub-compatible Mermaid diagrams (`flowchart TD`) for Barista User Journey and Clean Architecture data flow.

## Target Device Screenshot Inventory
1. `s20_ultra_v1145_01_home.png`: Home workbench, brand bottom nav, offline status.
2. `s20_ultra_v1145_02_brew_recipe.png`: Brew recipe composer, ratio slider, method selection.
3. `s20_ultra_v1145_03_active_timer.png`: Live chronometer, target mass, BLE scale flow rate telemetry.
4. `s20_ultra_v1145_04_grinders_drippers.png`: Sprint 03 44-grinder & 26-dripper intelligence, valve toggle.
5. `s20_ultra_v1145_05_bag_inventory.png`: Bean agronomy & bag freeze/thaw stock with compact action buttons.
6. `s20_ultra_v1145_06_champion_recipes.png`: 80 World Champion catalog in Journal.
7. `s20_ultra_v1145_07_sensory_wheel.png`: Interactive 221-descriptor sunburst wheel with category chips.
8. `s20_ultra_v1145_08_cva_cupping.png`: CVA cupping sheet, 5-bowl multi-cup grid, 10-attribute sliders.
9. `s20_ultra_v1145_09_sensory_training.png`: Triangle discrimination drill, balanced permutations, binomial stats.
10. `s20_ultra_v1145_10_green_grading.png`: 350g green defect grading, 48dp counters, screen size distribution.
11. `s20_ultra_v1145_11_panel_calibration.png`: Inter-rater calibration, Pearson r, Fleiss Kappa, panel MCD.
12. `s20_ultra_v1145_12_settings_degradation.png`: Degradation banner, privacy settings, local model storage.
