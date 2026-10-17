# Change Proposal: `fix-timer-scale-session-crash`

## 1. Context & Problem Statement
When a user launches the app or navigates to the **Brew** tab and clicks **Connect Scale**, **Tare (0.0g)**, or **Start Session**, or clicks **Start timer** / **Reset timer** in the Recipe tab without having previously entered a recipe name in the draft, the application immediately crashes with a fatal force-close exception:
```
java.lang.IllegalArgumentException: Recipe name cannot be blank.
  at com.coffeecreator.app.core.engine.DomainValidation.requireText(DomainValidation.kt:196)
  at com.coffeecreator.app.core.engine.DomainValidation.validateRecipe(DomainValidation.kt:53)
  at com.coffeecreator.app.ui.viewmodel.RecipeDraft.build(RecipeDraft.kt:63)
  at com.coffeecreator.app.ui.viewmodel.CoffeeViewModelRecipeKt.buildRecipe(CoffeeViewModelRecipe.kt:267)
  at com.coffeecreator.app.ui.viewmodel.CoffeeViewModelBrewSessionKt.createDefaultSession(CoffeeViewModelBrewSession.kt:449)
```

Furthermore:
- In `CoffeeViewModelBrewSession.kt:32-36`, `initBrewSession()` falls back to `mutableState.value.recipes.firstOrNull() ?: buildRecipe()`. On a fresh install or clean state where no recipes are saved yet, the fallback directly calls `buildRecipe()`, crashing the process on `Dispatchers.Main.immediate`.
- In `tareScale()`, if `it.brewSession == null`, the operation fails silently or leaves the session uninitialized.
- In `CoffeeApp.kt`, Recipe Timer buttons (`Start timer`, `Reset timer`) use raw buttons without tactile micro-depression physics, lack single-line constraints, and lack explicit `testTag` identifiers (`recipe:toggle_timer`, `recipe:reset_timer`).

## 2. Architecture & Design Principles (BMAD / OpenSpec SDD)
1. **5-Tier Resilient Recipe Resolution (`resolveActiveRecipe`)**:
   - Tier 1: Validated `targetRecipe` if passed explicitly.
   - Tier 2: Safe draft build synthesizing `"Draft $effectiveMethod"` if `draft.fields["name"]` is blank.
   - Tier 3: Validated first saved recipe from `mutableState.value.recipes.firstOrNull()`.
   - Tier 4: First verified recipe from `WorldChampionRecipeCatalog.entries.firstOrNull()?.toRecipe()`.
   - Tier 5: Guaranteed valid static `defaultFallbackRecipe()` (15g dose, 250g water, 93°C, 2 brew steps).
2. **Defensive Coroutine Execution**:
   - `connectMockScale()`, `startBrewSession()`, `toggleTimer()`, `tareScale()`, and `resetBrewSession()` wrapped in safe execution blocks.
   - Any unexpected error updates `diagnosticBuffer` and friendly UI notice instead of crashing the process.
3. **Safe Tare Initialization**:
   - If `brewSession == null` when `tareScale()` is triggered, initialize a clean default session zeroed at 0.0g.
4. **Tactile Industrial UI**:
   - Recipe Timer buttons migrated to `TactileButton` and `TactileOutlinedButton` with `testTag` support.
