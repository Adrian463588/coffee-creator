# Tasks: README Visual Documentation Overhaul

- [ ] **Phase 1: Samsung Galaxy S20 Ultra Screenshot Capture**
  - [ ] Connect and verify ADB authorization on device `RRCN3008VYE` (`SM-G988B`).
  - [ ] Capture 12 UI screens to `docs/screenshots/<filename>`:
    - [ ] `s20_ultra_v1145_01_home.png` (Home screen with tactile nav)
    - [ ] `s20_ultra_v1145_02_brew_recipe.png` (Recipe composer & ratio slider)
    - [ ] `s20_ultra_v1145_03_active_timer.png` (Active chronometer & flow rate)
    - [ ] `s20_ultra_v1145_04_grinders_drippers.png` (44 grinders & 26 drippers)
    - [ ] `s20_ultra_v1145_05_bag_inventory.png` (Bean agronomy & freeze/thaw)
    - [ ] `s20_ultra_v1145_06_champion_recipes.png` (80 World Champion catalog)
    - [ ] `s20_ultra_v1145_07_sensory_wheel.png` (221-descriptor sunburst wheel)
    - [ ] `s20_ultra_v1145_08_cva_cupping.png` (Digital CVA cupping form & 5-bowl grid)
    - [ ] `s20_ultra_v1145_09_sensory_training.png` (Triangle discrimination drill)
    - [ ] `s20_ultra_v1145_10_green_grading.png` (350g green defect grading & 48dp counters)
    - [ ] `s20_ultra_v1145_11_panel_calibration.png` (Inter-rater calibration & panel MCD)
    - [ ] `s20_ultra_v1145_12_settings_degradation.png` (Degradation banner & local models)
  - [ ] Verify all 12 screenshot files exist, are valid non-empty PNGs, and render crisply.

- [ ] **Phase 2: README Structure & Content Overhaul**
  - [ ] Update hero badges (Kotlin 2.0.20, Compose BOM, 322 Tests, Offline-First, v1.14.5).
  - [ ] Upgrade latest download callout to v1.14.5 with exact SHA-256 and zero-data-loss command.
  - [ ] Insert 4 themed screenshot gallery tables (3 images per row, 32% width, rounded borders).
  - [ ] Verify and update Mermaid diagrams (User Journey and Clean Architecture).
  - [ ] Update Technology Stack matrix with exact pinned versions.
  - [ ] Refactor Historical Releases table into collapsible `<details>` section.
  - [ ] Refactor Directory Tree into collapsible `<details>` section.
  - [ ] Update feature matrix for Sprint 03 & Sprint 04 capabilities.

- [ ] **Phase 3: Quality Gates & Verification**
  - [ ] Verify image links render properly in GitHub Markdown preview.
  - [ ] Verify Mermaid syntax passes Markdown linter without diagram errors.
  - [ ] Verify zero file modifications to code/engine/ui layers.
  - [ ] Check git status and stage documentation assets.
