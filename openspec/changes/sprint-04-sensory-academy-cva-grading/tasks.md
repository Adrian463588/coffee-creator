# Tasks: Sprint 04 Sensory Academy, CVA Cupping, Training Lab & Green Grading

## Phase 1: OpenSpec Specification & Invariant Baseline [DONE]
- [x] Create `proposal.md` with CVA perspectives, 5-taste scientific correction, and green grading scope.
- [x] Create `bdd-scenarios.md` with Gherkin scenarios for Epics A, B, C, D, and E.
- [x] Create `tasks.md` tracking execution across domain, tests, UI, and hardware verification.

## Phase 2: Pure Domain Engine Implementation (`com.coffeecreator.app.core.sensory.*`) [IN_PROGRESS]
- [ ] Create `CvaModels.kt` and `CvaServices.kt` (`cva` package):
  - Protocol specifications (`SCA_CVA_CURRENT`, `SCA_2004_LEGACY`, `CUSTOM_RESEARCH`).
  - 4 perspective models: `PhysicalAssessment`, `DescriptiveAssessment`, `AffectiveAssessment`, `ExtrinsicAssessment`.
  - Blind sample coding and audit reveal tracking.
  - Session amendment with SHA-256 state hashing.
  - Native JSON and CSV export formatters.
- [ ] Create `AcademyModels.kt`, `AcademyLessons.kt`, and `OrganicAcidAtlas.kt` (`academy` package):
  - 22 comprehensive lessons (EDU-01 to EDU-22) with bilingual EN/ID content, objectives, in-house ASCII diagrams, glossary, quiz questions, accessibility notes, and citations.
  - Scientific correction: 5 basic tastes (Sweet, Sour, Salty, Bitter, Umami). "Acid" is not a 6th taste!
  - Organic acid atlas (Citric, Malic, Tartaric, Acetic, Lactic, Phosphoric, Quinic).
  - Aroma taxonomy browser and Le Nez du Café 60 companion (generic taxonomy + custom vial IDs).
- [ ] Create `TrainingModels.kt` and `TriangleStatisticsEngine.kt` (`training` package):
  - 6-permutation balanced Triangle generator (`AAB, ABA, BAA, BBA, BAB, ABB`).
  - Exact Binomial upper-tail cumulative probability: $P(X \ge k) = \sum_{i=k}^n \binom{n}{i} (1/3)^i (2/3)^{n-i}$.
  - Basic taste reference solution guidelines (Sucrose, Citric, NaCl, Caffeine, MSG).
  - Roast defect reference catalog (Underdeveloped, Overdeveloped, Baked, Scorched, Quaker).
- [ ] Create `GreenGradingModels.kt` and `StandardGreenGradingEngine.kt` (`greengrading` package):
  - Category 1 primary defects vs Category 2 secondary defects with equivalent ratios.
  - 350g sample washed Arabica SCA 2004 reference grading rule engine.
  - Sieve distribution (Screens 20 to below 12).
  - Moisture assessment with uncertainty and `NOT_MEASURED` guard.
  - Out-of-scope guard for Canephora/Natural.

## Phase 3: Domain Unit Tests (TDD)
- [ ] Write `CvaDomainEngineTest.kt` verifying 4 perspectives, protocol disclaimers, lock/amendment, and exports.
- [ ] Write `AcademyCurriculumTest.kt` verifying all 22 lessons, 5 basic tastes, organic acids, and quiz logic.
- [ ] Write `TriangleStatisticsEngineTest.kt` verifying 6 permutations, binomial p-values, and caveats.
- [ ] Write `GreenGradingEngineTest.kt` verifying 350g defect tally, specialty grade eligibility, and out-of-scope guards.
- [ ] Run `./gradlew.bat testDebugUnitTest` and achieve 100% green.

## Phase 4: UI Integration & Microinteraction Refinement
- [ ] Add additive Sensory screen tabs in `CoffeeApp.kt`:
  - `Academy (22 Modules)` (`sensory:tab_academy`) with expandable module cards, bilingual toggle, ASCII diagrams, and quiz checkers.
  - `Digital CVA Cupping` (`sensory:tab_cva`) with 4 perspective switchers, blind code management, and lock/export buttons.
  - `Training Lab` (`sensory:tab_training`) with Triangle drill generator, hidden odd-cup reveal, and binomial significance readout.
  - `Green Coffee QC` (`sensory:tab_green_qc`) with 350g defect counter, sieve calculator, and moisture input.
  - Preserve all existing tabs (`Flavor Wheel`, `Accessible List`, `Cupping Sessions`, `Consensus & Radar`, `Physical QC & Rest`, `Calibration Lab`).
- [ ] Apply 2026 Tactile Industrial & Anti-AI Slop invariants:
  - 1px hairline borders (`alpha = 0.40f`).
  - Concentric corner geometry $R_{\text{inner}} = \max(0, R_{\text{outer}} - \text{Padding})$.
  - Tabular monospace numbers (`fontFeatureSettings = "tnum"`).
  - Tactile micro-depression (`scale = 0.97f`) on all buttons and chips.

## Phase 5: Hardware Verification & GitHub Release
- [ ] Run `./gradlew.bat testDebugUnitTest`.
- [ ] Bump version to `v1.14.0` (`versionCode = 25`) in `app/build.gradle.kts`.
- [ ] Build debug APK (`./gradlew.bat assembleDebug`).
- [ ] Copy to `coffee-creator-v1.14.0.apk` and compute SHA-256 checksum.
- [ ] Install on target ADB device `RRCN3008VYE` with zero data loss (`adb -s RRCN3008VYE install -r -d coffee-creator-v1.14.0.apk`).
- [ ] Capture physical device screenshot on `RRCN3008VYE` confirming UI and zero data loss.
- [ ] Update `SHA256SUMS.txt`, `README.md`, and create `docs/releases/v1.14.0.md`.
- [ ] Commit, push to GitHub, and publish release `v1.14.0`.
