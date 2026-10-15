# OpenSpec Tasks: Epic S2-E05 — Sensory Wheel and Professional Cupping

- [x] **Task 1: Core Sensory Domain Models & Calculus**
  - Implement `FlavorWheelTaxonomy.kt` under `com.coffeecreator.app.core.sensory`:
    - 9 primary categories (Fruity, Floral, Sweet, Nutty/Cocoa, Spices, Roasted, Cereal, Fermented, Green/Vegetative), secondary subcategories, 80+ descriptors.
    - Descriptor definitions and safe food-grade calibration reference formulas (Citric, Malic, Sucrose, Quinine) with food safety boundaries (S2-051, S2-052).
  - Implement `CuppingModels.kt`:
    - Standard sensory score attributes (Aroma, Acidity, Sweetness, Body, Bitterness, Aftertaste, Cleanliness, Balance, Overall) (S2-053).
    - Epistemological distinction: `HUMAN_OBSERVATION`, `AI_HEURISTIC_HYPOTHESIS`, `CALIBRATED_PREDICTION` with confidence bounds and provenance (S2-054).
    - Cupping sheet templates: Standard Specialty (100-pt), Quick Barista Dial-In, Production QC, Espresso Cupping (S2-055).
    - Multi-sample cupping session with evaluator assignment (S2-056).
    - Masking modes: `NONE`, `BLIND`, `DOUBLE_BLIND` with controlled reveal status (S2-057).
    - Session state machine: `DRAFT` -> `OPEN` -> `CLOSED` -> `REOPENED` with immutable audit trail (S2-058).
    - Physical QC record: moisture, water activity, defects, screen size, density, Agtron color (S2-061).
    - Roast-rest observation: rest days, taste notes, and degassing/staling phase tracking (S2-062).
  - Implement `CuppingAnalyticsEngine.kt`:
    - Multi-evaluator consensus: arithmetic mean, median, standard deviation, inter-rater agreement index (IRAI), outlier detection (S2-059).
    - Descriptor overlap matrix with pairwise Jaccard similarity (S2-059).
    - Offline CSV and Markdown QC report generation with cryptographic SHA-256 fingerprint receipts (S2-060).
    - Roast-rest degassing decay, open maturity index, and peak sensory window predictions (S2-062).

- [x] **Task 2: Repository & ViewModel Integration**
  - Extend `CoffeeViewModel.kt` with sensory session management, evaluator assignments, score recording, blind masking/reveal, consensus calculation, physical QC, and roast-rest tracking.
  - Expose state flows for active sessions, selected template, and calibration references.

- [x] **Task 3: Compose UI Presentation (Anti-AI Slop & Tactile Industrial)**
  - Upgrade `Sensory` tab in `CoffeeApp.kt`:
    - Filter chips: "Flavor Wheel", "Accessible List", "Cupping Sessions", "Consensus & Radar", "Physical QC & Rest", "Calibration Lab".
    - Interactive searchable flavor wheel taxonomy + high-contrast accessible table.
    - Blind session cards with masked code tags (`Sample #A1`) and reveal button.
    - Radar/slider scoring screen with epistemological certainty badges.
    - Multi-evaluator consensus dashboard with IRAI index, outliers, and descriptor overlap.
    - Physical QC green/roasted evaluation forms.
    - Roast-rest degassing curve visualizer and peak window badge.
    - Calibration lab with safe ppm/mass recipes and disclaimers.

- [x] **Task 4: Automated Testing & Verification**
  - Create `ActiveSensoryCuppingTest.kt` in `app/src/test/java/com/coffeecreator/app/core/sensory/` covering all 12 stories S2-051 through S2-062.
  - Run `./gradlew testDebugUnitTest` (170/170 tests green) and `./gradlew assembleDebug` (121.73 MB APK).

- [x] **Task 5: GitHub Release v1.6.0 & Documentation**
  - Update `README.md` with release notes, APK download link, and historical table.
  - Stage, commit, push to GitHub, and create release `v1.6.0` via `gh release create`.
