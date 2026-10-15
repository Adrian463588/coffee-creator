# Design Specification: Epic S2-E02 — Recipe Composer & Deterministic Brewing Science

## Architecture Overview
Follows strict hexagonal/clean architecture:
- `core/model`: Pure Kotlin data classes and typed units (`BrewStep`, `Recipe`, `RefractometerProfile`, `AromaDosageResult`, `CalculationResult`).
- `core/engine`: Pure Kotlin domain science engine (`CoffeeScienceEngine`, `DomainValidation`) with zero Android dependencies, using `BigDecimal` arithmetic.
- `core/data`: Room database records (`StoredRecord`), schema migration safety, repository layer (`DataRepository`, `RecipeRepository`).
- `ui/viewmodel`: `CoffeeViewModel` and `RecipeDraft` managing immutable `CoffeeUiState`.
- `ui/screens`: Material 3 Jetpack Compose UI adhering to Anti-AI-Slop and tactile microinteraction principles.

## Mathematical Invariants
1. **Exact-Sum Pour Scaling**:
   Scaled pour steps use the Hamilton-Hare largest remainder algorithm with quantum $\delta = 0.1\text{ g}$.
   $$\sum_{i=1}^n w_{\text{new}, i} = W_{\text{target}}$$
   Cumulative targets $C_k = \sum_{i=1}^k w_{\text{new}, i}$ are strictly monotonic non-decreasing.
2. **Refractometer Brix $\to$ TDS**:
   $$TDS\% = \text{Brix}\% \times k_{\text{profile}}$$
   Profiles: Standard Coffee ($k = 0.8500$), Atago PAL-COFFEE ($k = 0.8400$), VST LAB Coffee III ($k = 0.8570$).
3. **Extraction Yield Mass Balance**:
   $$EY\% = \frac{m_{\text{beverage}} \cdot TDS\%}{m_{\text{dose}}}$$
   $EY\% \le 100.0\%$. Inferred beverage mass must never be marked as measured.
4. **Concentrate Mass Balance Dilution**:
   $$m_{\text{final}} = m_{\text{concentrate}} + m_{\text{water}}$$
   $$TDS_{\text{final}} = \frac{m_{\text{concentrate}} \cdot TDS_{\text{concentrate}}}{m_{\text{final}}}$$
5. **Food-Grade Aroma Dosing**:
   - Stock dosage: $(m_{\text{stock}} / m_{\text{beverage}}) \times 100\%$
   - Active compound: $(m_{\text{stock}} \times (w_{\text{active}} / 100) / m_{\text{beverage}}) \times 10^6\text{ ppm}$
   - Rejects non-food grade compounds or additions exceeding `supplierMaxPercent`.

## Offline Sharing & Export Contract
- **JSON Export**: Canonical structured JSON representation including metadata, steps, ratio, dose, and timestamp.
- **Barista Text Summary**: Formatted textual recipe card for easy sharing in messaging apps without cloud dependencies.
- **Android Sharesheet**: Invokes standard `Intent.ACTION_SEND` with `text/plain` or `application/json`.
