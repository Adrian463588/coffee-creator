# OpenSpec Design: Epic S2-E04 — Brew Journal, Espresso Dial-In, and Local Analytics

## 1. Architectural Boundaries & Domain Isolation
All calculation and modeling rules reside in pure Kotlin JVM classes under `com.coffeecreator.app.core.journal`:
- Zero dependencies on Android framework (`android.*`), Room, or Compose.
- Exact decimal calculations with `BigDecimal` half-up rounding.
- Physical units enforced: mass in grams, duration in seconds, temperature in Celsius, concentration in % w/w.

## 2. Mathematical Models & Equations

### 2.1 One-Variable-at-a-Time (OVAT) Adjustment Model
Extraction yield:
$$EY\% = \frac{m_{\text{beverage}} \cdot TDS\%}{m_{\text{dose}}}$$

Decision Hierarchy:
1. **Channeling / Fracture**: Shot time $< 20\text{s}$ with flow $> 3.5\text{ g/s}$ or taste flags `SOUR` + `ASTRINGENT` $\implies$ Target `PUCK_PREPARATION` (WDT, uniform tamp).
2. **Under-Extracted / Sour**: $EY < 18.0\%$ or dominant sour $\implies$ Target `GRIND_SIZE` $\to$ `FINER` by 1 step.
3. **Over-Extracted / Bitter**: $EY > 22.0\%$ or harsh bitter/dry $\implies$ Target `GRIND_SIZE` $\to$ `COARSER` by 1 step.
4. **Weak / Low TDS**: $TDS < 1.15\%$ (filter) or $< 7.5\%$ (espresso) $\implies$ Target `BREW_RATIO_YIELD` $\to$ tighten ratio.
5. **Strong / High TDS**: $TDS > 1.55\%$ (filter) or $> 11.5\%$ (espresso) $\implies$ Target `BREW_RATIO_YIELD` $\to$ widen ratio or bypass.

### 2.2 Caffeine Mass Balance Equation
$$m_{\text{caffeine,mg}} = m_{\text{dose,g}} \cdot (w_{\text{caffeine}} \cdot 1000) \cdot \eta_{\text{extraction}}$$
Where:
- $w_{\text{arabica}} = 0.0120$ ($1.20\%$), $w_{\text{robusta}} = 0.0220$ ($2.20\%$).
- $\eta_{\text{nominal}} = 0.85$ ($85\%$), bounds $[0.80, 0.90]$.
- Mandatory disclaimer displayed on all UI surfaces.

### 2.3 Calendar, Streak, and Monthly Statistics
- Monotonic consecutive day walk with 1-day grace period for current day.
- Longest streak tracked over chronological set of unique active brewing dates.
- Monthly aggregations for total dose consumed (g), average rating, and top method.

### 2.4 Side-by-Side Comparison Calculus
Exact deltas: $\Delta \text{Dose}$, $\Delta \text{Water}$, $\Delta \text{Ratio}$, $\Delta \text{Time}$, $\Delta \text{TDS}$, $\Delta \text{EY}$, $\Delta \text{Rating}$.

### 2.5 3-Way Backup/Import Conflict Detection
Canonical record hash $H(r) = \text{SHA-256}(\text{canonicalJson}(r))$.
Comparison against local record gives `NEW`, `IDENTICAL`, `MODIFIED_REMOTE_NEWER`, `CONFLICT_LOCAL_NEWER`, or `CONFLICT_DIVERGENT`.
