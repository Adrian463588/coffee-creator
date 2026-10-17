# Feature preservation diff, Sprint 01–04

**Baseline:** source tree at `6d429df9299d438daefbdac769b5300988d83f9c`; versioned app `1.14.5`. Review is static Git history and current code. No application database, user data, or SAF content was read. Behavioral preservation still needs disposable-fixture execution.

## Release history evidence

Public release notes were also compared with source at their exact tags. The v1.14.3 and v1.14.4 notes overstate Green QC measurement provenance, export hashing, durable Sprint 04 persistence, restore verification, and journal logging relative to tagged code (P0 D-015). The v1.14.5 APK release itself remains published, but its local release note has a different repository owner in the download command and a different size than the current GitHub asset API (P1 D-014). These are documentation/release-claim findings; this audit did not edit the existing public release or README.

| Tag range | Shipped milestone indicated by commit history | Preservation evidence |
|---|---|---|
| `v1.3.0`–`v1.9.0` | Sprint 02 recipe/math, timer, journal, sensory/cupping, AI/RAG, reliability, and catalog epics | Tags exist. They identify feature introduction, not proof every behavior remains correct today. |
| `v1.10.0`–`v1.10.2` | Offline label/OCR and later UX refinement | Feature commits and test files remain in current source; real-label device acceptance remains open. |
| `v1.11.0` | 80 world champion/signature/traditional recipe catalog | `WorldChampionRecipeCatalog.kt` and an 80-row assertion test remain. The fresh unit suite passed the catalog test. Individual attribution/license/source accuracy still needs row-level review. |
| `v1.12.0`–`v1.12.2` | 221-descriptor flavor wheel and UI refinements | `FlavorWheelTaxonomy.kt` and 221-count test remain; descriptor provenance/rights are not fully evidenced. |
| `v1.13.0`–`v1.13.1` | Sprint 03 manual brewing and grinder intelligence | Grinder/dripper models, catalog test, timer/repository flows remain. `PENDING_VIDEO_REVIEW` remains for BR-17. |
| `v1.14.0`–`v1.14.4` | Sprint 04 academy/CVA/training/green QC/pro operations | Domain models/engines and unit tests were added. New UI screens are reachable but fail material persistence/export/provenance checks listed in the defect register. |
| `v1.14.5` | Tactile/visual refinement and README showcase | Tag commit `c483c91`; this audit baseline is one docs/screenshots commit later. |

## Path-level diff checks

- Reviewed all 31 version tags and all 30 adjacent tag ranges under `app/src/main`, `app/src/test`, `app/src/androidTest`, and `app/src/sharedTest`. Two ranges have source/test path removals:
  - `v1.0.0..v1.1.0`: `HomeScreen.kt`, `RecipeBuilderScreen.kt`, `AiChatScreen.kt`, `CoffeeLibraryScreen.kt`, `BrewJournalScreen.kt`, `KnowledgeLibraryScreen.kt`, `ModelManagerScreen.kt`, `SensoryScreen.kt`, `SettingsScreen.kt`, and `ui/navigation/Screen.kt` were deleted as individual files. The current unified `CoffeeApp.kt` contains corresponding Home, Formulate, AI Chat, Library, Journal, Knowledge, Models, Sensory, and Settings destinations. This is a source consolidation; destination presence is visible, but behavior parity is **NOT_RUN**.
  - `v1.9.5..v1.9.6`: production `core/data/CuratedCoffeeCatalog.kt` and `CuratedCoffeeCatalogTest.kt` were removed. Similar recipe-shaped content moved to `app/src/test/.../BrewScenarioFixtures.kt`, whose source explicitly says fixtures must never enter app state/seeds. Current `SeedData.sampleRecipes` is empty and a separate 80-item static World Champion catalog exists, but no stable-ID/content reconciliation proves it replaces every earlier recipe. This is tracked as P0 D-013 / `SPEC_CONFLICT`; do not copy synthetic fixtures or unsupported historical measurements back into production.
  - The other 28 adjacent tag ranges have zero deleted app source/test paths. File-level absence does not prove behavior/data parity.
- `git diff --name-status v1.13.1..v1.14.5 -- app/src/main app/src/test app/src/androidTest app/src/sharedTest` showed added Sprint 04 files and modifications to `CoffeeApp.kt`/`SensoryWheelVisualizer.kt`; **no deleted source or test paths** in that comparison.
- `git diff --name-status v1.14.5..HEAD` showed README and screenshot additions plus OpenSpec documentation; it showed **no app source/test changes** after the v1.14.5 tag.
- Current NavHost has nine routes (`Home`, `Formulate`, `Library`, `Journal`, `Sensory`, `Knowledge`, `Models`, `AI Chat`, `Settings`). No route deletion was found in the v1.13.1→v1.14.5 source diff.
- Current Room schema is version 2 with a generic records table. It does not provide relational per-feature storage for new Sprint 04 screens. That is a data-persistence gap, not evidence that the older generic records have been deleted.

## Preservation ledger

| Feature/data family | Current evidence | Status |
|---|---|---|
| Legacy recipe create/copy/version and deterministic math | `CoffeeViewModelRecipe.kt`, `RecipeComposerEngine`, `CoffeeScienceEngine`; JVM tests passed | Implemented at source/test layer; UI/device and real record preservation unverified. |
| Bean/equipment catalogs and custom entries | `CatalogModels`, `CoffeeViewModelCatalog`, catalog test suites | Candidate implementation remains; user record counts/IDs unavailable without disposable profile. |
| Grinder/dripper records and settings | `GrinderIntelligenceModels`, `DripperIntelligenceModels`, tests | Source retained; real calibration and migration snapshots not run. |
| 80 champion recipe templates | `WorldChampionRecipeCatalog.kt`, `WorldChampionRecipeCatalogTest` | Count assertion passed in fresh JVM suite; rights/source/history for each row not audited individually. |
| 221 flavor descriptors and saved sensory records | `FlavorWheelTaxonomy.kt`, legacy cupping ViewModel, sensory tests | Source retained; 221 assertion passed; origin rights and old stored descriptor IDs need migration fixture comparison. |
| Legacy cupping, journal, backup/restore | `CoffeeViewModelCupping`, `CoffeeViewModelJournal`, `BackupCodec`, repository tests | Older paths remain connected; old nested JSON compatibility risk D-004 and physical SAF recovery remain open. |
| Local RAG and model assets | local Room/FTS/ObjectBox and llama.cpp/ONNX source; SAF manager and tests | Code retained; no current device-bound RAG/model or SAF restore receipt. |
| Sprint 04 session and green records | Domain models/tests exist; Compose sections hold local state without repository callbacks | **Not proven preserved/persisted**; current state is not connected to durable session records (D-003). |
| App routes | Nine routes declared in `CoffeeApp.kt` | No missing current main route found; deep links were not declared in NavHost. |
| Historical top-level screens | Nine user screens were consolidated from separate files into the unified navigation source between v1.0.0 and v1.1.0 | Current destination names exist; old action/state parity remains NOT_RUN in `TC-REQ-LEGACY-01..09`. |
| Historical curated recipe corpus | Production `CuratedCoffeeCatalog.kt` was removed in v1.9.6 and similar records now exist only as synthetic test fixtures; current production has a separate 80-template catalog | Stable recipe-ID/content/provenance parity is unresolved; strict no-loss and no-fabricated-measurement rules conflict until row-level review (D-013). |

## Before/after record snapshot status

The task required a disposable seeded profile snapshot before baseline comparison. None was available. Therefore no counts, record IDs, JSON/CSV exports, recipe/cupping fixtures, SAF grants, or local model lists were captured. We did not access the installed user profile to fill this gap. A “no path deleted” result is not a substitute for record-level before/after proof.

## Reproducible preservation suite

Run `TC-CROSS-02`, `TC-CROSS-03`, `TC-CROSS-08`, `TC-318`, `TC-319`, `TC-322`, and `E2E-01`/`E2E-07` from [MASTER_TEST_CASES_S01_S04.md](MASTER_TEST_CASES_S01_S04.md) on copied historical schemas and a disposable seeded profile. Compare ordered record manifests with stable IDs, payload hashes, source links, and exported file checksums. Required result is zero lost or rewritten legacy rows and explicit migration/restore report. These cases are **NOT_RUN** on the installed user app.
