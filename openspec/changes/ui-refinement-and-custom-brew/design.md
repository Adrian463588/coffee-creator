# Design Specification: UI Refinement, Custom Inputs, and Sourced Datasets

## Architecture & Boundaries
1. **Presentation Layer (`com.coffeecreator.app.ui`)**:
   - `BrewSectionTabs`: Single-row horizontally scrollable chip group with icons and state description semantics.
   - `Formulate` screen: Each brew section (Recipe, Measurements, Ingredients, Tools) renders as a distinct, encapsulated card with concentric radii ($R_{inner} = R_{outer} - P$).
   - `Recipe` Card: Contains dedicated input fields for custom coffee bean and custom brew method. Selecting "Custom bean..." or typing into the custom bean field links to a persisted bean in `DataRepository`. Selecting "Custom method..." or typing allows free-form brewing apparatus naming.
   - `Home` screen: Polished navigation cards/grid with tactile micro-depression (`active:scale(0.98)` / pressed state styling) and explicit icons (`Icons.Default.Chat`, `Icons.Default.MenuBook`, etc.).
2. **Data Layer (`com.coffeecreator.app.core.data`)**:
   - `SeedData`: Sourced, real-world datasets for beans, recipes, and sensory journal observations.
   - Preserves offline invariants, transactional database insertion, and idempotency.
3. **Scientific Invariants (`com.coffeecreator.app.core.engine`)**:
   - Mass balance: $EY\% = (\text{Beverage Mass} \times \text{TDS}\%) / \text{Dose}$.
   - Exact units: grams (g), percentage (% w/w), degrees Celsius (°C), seconds.
   - Zero fabricated sensor claims: estimates are distinctly labeled from human observations.
