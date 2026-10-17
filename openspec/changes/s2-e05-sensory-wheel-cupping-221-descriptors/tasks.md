# OpenSpec Tasks: Epic S2-E05 Deepening — Complete 221-Descriptor Sensory Wheel & Professional Journal Cupping

- [ ] **Task 1: Complete 221-Descriptor Taxonomy & Domain Contracts (S2-051, S2-052)**
  - Update `FlavorDescriptor` with `idLabel: String? = null` and `isOfficialReference: Boolean = true` for backward compatibility.
  - In `FlavorWheelDefaultCategories.kt`, embed all 221 descriptors across 9 primary families (84 official reference + 137 expanded local Indonesian & global descriptors).
  - Update `SensoryTaxonomyRegistry` search to support bilingual search (English `name` and Indonesian `idLabel`) and query filtering by official vs extension.

- [ ] **Task 2: 16 Cupping Attributes & Multi-Phase Temperature Models (S2-053, S2-055, S2-056)**
  - Add `CVA_DESCRIPTIVE_16_ATTRIBUTE` template to `StandardCuppingTemplates` and `CuppingTemplateType`.
  - Add `CuppingPhase` and `CuppingPhaseObservation` models for recording evaluations along the temperature curve (hot 70°C, warm 60°C, cool 40°C, room 25°C).
  - Add `toBrewJournalEntry()` converter on `EvaluationSheet` and `CuppingSample` for direct 1-click logging to Journal.

- [ ] **Task 3: ViewModel & UI Enhancements (S2-051, S2-056, S2-039)**
  - Add `logCuppingSampleToJournal(sessionId, sampleId)` in `CoffeeViewModelCupping.kt`.
  - In `CoffeeApp.kt`, add "Log to Journal" action button to revealed cupping samples.
  - Enhance `FlavorWheelPickerDialog` with bilingual labels, official reference badge/indicator, and instant tag selection.

- [ ] **Task 4: Verification & Quality Gates**
  - Update `ActiveSensoryCuppingTest.kt` to verify:
    * Total descriptor count is exactly 221.
    * 84 official wheel reference descriptors + 137 local extension descriptors.
    * Bilingual lookup (`name` and `idLabel`).
    * 16-attribute template scoring and multi-phase temperature observations.
    * Cupping sample to `BrewJournalEntry` conversion.
  - Run `./gradlew.bat testDebugUnitTest` and `./gradlew.bat assembleDebug`.

- [ ] **Task 5: Device Installation & Delivery**
  - Install updated APK on connected Samsung Galaxy S20 Ultra (`-r -d`).
  - Capture screen verification.
  - Commit, push, and create GitHub release v1.12.0.
