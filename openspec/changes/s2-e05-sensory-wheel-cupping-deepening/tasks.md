# OpenSpec Tasks: Epic S2-E05 Deepening — Full Sensory Wheel and Professional Cupping

- [x] **Task 1: Comprehensive Flavor Wheel Taxonomy (S2-051, S2-052)**
  - Update `FlavorWheelTaxonomy.kt` with complete 9-category, 26-subcategory, 90+ descriptor catalog based on WCR Lexicon + Indonesian Roda Rasa Kopi.
  - Include Defect/Taint category for professional cupping QC (Potato defect, Phenolic, Rubber, Musty, Baggy).
  - Add utility methods for category hierarchy lookups and quick descriptor queries.

- [x] **Task 2: Interactive Compose Sensory Wheel Visualizer (`SensoryWheelVisualizer.kt`)**
  - Implement a canvas-based multi-ring / concentric sunburst wheel visualizer.
  - Support category focus, arc drawing with category and subcategory color tokens, center label, and smooth interactive touch hit-testing.
  - Implement accessible alternative triggers.

- [x] **Task 3: Interactive Flavor Wheel Descriptor Picker Dialog**
  - Build `FlavorWheelPickerDialog` allowing baristas and cuppers to visually browse or filter Category -> Subcategory -> Descriptors and toggle them as active chips.
  - Integrate into `Score Sample` dialog in `CoffeeApp.kt`.
  - Integrate into Brew Journal tasting notes editing.

- [x] **Task 4: Dynamic Template Attribute Sliders in Cupping Dialog (S2-053, S2-055)**
  - Replace hardcoded 10-slider list with dynamic attribute iteration based on `activeSession.template.attributes`.
  - Calculate total score dynamically using `CuppingAnalyticsEngine.calculateTotalCuppingScore`.

- [x] **Task 5: Verification & Quality Gates**
  - Update/extend unit tests in `ActiveSensoryCuppingTest.kt` to cover the full taxonomy (9 categories, 90+ descriptors), dynamic scoring for all 4 templates, and defect deductions.
  - Run `./gradlew.bat testDebugUnitTest` and `./gradlew.bat assembleDebug`.

- [x] **Task 6: Delivery & Release**
  - Bump version to `v1.9.5` (`versionCode = 14`, `versionName = "1.9.5"`).
  - Create release notes `docs/releases/v1.9.5.md` and update `README.md`.
  - Commit, push, and create GitHub Release `v1.9.5`.
