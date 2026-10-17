# Tasks: World Champion Recipes in Journal

- [ ] **Phase 1: Pure Domain Modeling & Catalog**
  - Implement `ChampionRecipeModels.kt`: `RecipeCategory`, `RecipeVerificationStatus`, `CompetitionPodium`, `ChampionPourStep`, `ChampionRecipe`.
  - Implement `WorldChampionRecipeCatalog.kt`: full 76 curated recipes with parameters, steps, and provenance.
  - Implement unit conversion tests in `WorldChampionRecipeCatalogTest.kt`.

- [ ] **Phase 2: ViewModel Operations**
  - Add `logChampionRecipeToJournal` in `CoffeeViewModelJournal.kt`.
  - Add `brewChampionRecipe` to arm active timer session.
  - Add `saveChampionRecipeAsTemplate` to save to repository.

- [ ] **Phase 3: Journal UI Integration**
  - Add `JournalTab.CHAMPION_RECIPES` ("World Recipes") to `JournalTab` in `CoffeeApp.kt`.
  - Implement searchable & category-filtered catalog view.
  - Add tactile cards with verification badges, expandable steps, and action buttons (`Log to Journal`, `Brew on Timer`, `Save as Template`).

- [ ] **Phase 4: Quality Gates & Device Verification**
  - Execute `./gradlew.bat testDebugUnitTest` (all tests pass).
  - Execute `./gradlew.bat assembleDebug`.
  - Reinstall APK on connected device (`adb install -r -d`).
  - Capture device screenshots verifying UI rendering.

- [ ] **Phase 5: Release & GitHub Publication**
  - Bump version to `1.11.0` (`versionCode = 19`).
  - Update `README.md`, `SHA256SUMS.txt`, `docs/releases/v1.11.0.md`.
  - Commit, push, and create GitHub release.
