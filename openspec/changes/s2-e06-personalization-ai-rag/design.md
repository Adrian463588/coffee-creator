# OpenSpec Design: Epic S2-E06 — Personalization, Local AI, and RAG Integration

## 1. Architectural Boundaries & Domain Isolation
All personalization, Bayesian updates, recommendation scoring, feedback loop diagnostics, OCR parsing, altitude thermodynamics, and food safety engines reside in pure Kotlin/JVM classes under `com.coffeecreator.app.core.ai.personalization`:
- Pure Kotlin JVM, zero dependencies on Android framework (`android.*`), Room, or Compose.
- Exact decimal mathematics with `BigDecimal` half-up rounding.
- Physical units enforced: mass in grams, duration in seconds, temperature in Celsius, elevation in meters.

## 2. Mathematical Models & Equations

### 2.1 Palate Vector Space & Quiz Baseline Mapping (S2-063)
The personal palate profile is a normalized 5D vector:
$$\mathbf{p} = [p_{\text{roast}}, p_{\text{acidity}}, p_{\text{sweetness}}, p_{\text{bitterness}}, p_{\text{body}}]^T \in [0.00, 1.00]^5$$
- Initial prior mean: $\mathbf{\mu}_0 = \mathbf{p}_{\text{quiz}}$.
- Initial prior pseudo-count weight: $W_0 = 5.0$.
- Initial prior variance per dimension: $\sigma_{0,k}^2 = 0.040$.

### 2.2 Bayesian Exponential Moving Average Profile Update (S2-064)
- Triggered solely on sessions with rating $R_t \ge 7.50$ (or Q-score $\ge 80.00$).
- Recency weighting with exponential decay ($\tau = 90.0\text{ days}$):
  $$w_t = \left(\frac{R_t - 7.00}{3.00}\right) \cdot \exp\left(-\frac{\Delta t}{\tau}\right)$$
- Sequential update:
  $$W_t = W_{t-1} + w_t$$
  $$\mathbf{\mu}_t = \left(1 - \frac{w_t}{W_t}\right) \mathbf{\mu}_{t-1} + \left(\frac{w_t}{W_t}\right) \mathbf{x}_t$$
  $$\sigma_{t,k}^2 = \left(1 - \frac{w_t}{W_t}\right) \sigma_{t-1,k}^2 + \left(\frac{w_t}{W_t}\right) (x_{t,k} - \mu_{t,k})^2$$

### 2.3 Inventory Recommendation Scoring (S2-065)
$$\text{Score}(b, m) = \text{Sim}_{\text{cosine}}(\mathbf{\mu}_{\text{palate}}, \mathbf{s}_b) \cdot F_{\text{freshness}}(t_{\text{roast}}) \cdot A_{\text{gear}}(m)$$
- Cosine similarity: $\frac{\mathbf{\mu} \cdot \mathbf{s}_b}{\|\mathbf{\mu}\| \|\mathbf{s}_b\|}$.
- Degassing freshness:
  $$F(t) = \begin{cases}
  0.75 + 0.25(t/5) & t < 5\text{ days (excess CO}_2) \\
  1.00 & 5 \le t \le 28\text{ days (peak window)} \\
  \max(0.40, \exp(-(t-28)/30)) & t > 28\text{ days (staling decay)}
  \end{cases}$$
- Gear factor: $1.0$ if required brewer & grinder exist, else $0.0$.

### 2.4 OVAT Brewing Feedback Loop (S2-066)
Adjusts strictly one physical variable based on sensory defect diagnosis:
- Fast sour ($\Delta t < -15\text{s}$): Grind 1 click finer (dose, temp, water held constant).
- Normal sour ($|\Delta t| \le 15\text{s}$): Increase temp $+2.0^\circ\text{C}$ (clamped to $T_{\text{boil}} - 0.5^\circ\text{C}$).
- Slow bitter ($\Delta t > +15\text{s}$): Grind 1 click coarser.
- Normal bitter ($|\Delta t| \le 15\text{s}$): Decrease temp $-2.0^\circ\text{C}$.
- Weak/watery: Increase dose $+1.0\text{g}$ to tighten ratio.
- Harsh/concentrated: Increase water $+20\text{g}$ to loosen ratio.

### 2.5 Altitude-Adjusted Water Boiling Point (S2-073)
$$T_{\text{boil}}(h_m) = 100.00^\circ\text{C} - \left(\frac{h_m}{300.0}\right)$$
$$T_{\text{brew\_max}} = T_{\text{boil}}(h_m) - 0.50^\circ\text{C}$$
Target brew temperatures exceeding $T_{\text{brew\_max}}$ are clamped with a physical safety warning.

### 2.6 Food-Grade Ingredient Safety Gate (S2-074)
- Substance must have verified FCC / USP food-grade status (`foodGrade == true`).
- Banned cosmetic/perfumery chemical blacklist (DPG, DEP, synthetic musks, denatured alcohol).
- Allergen warning trigger and explicit user acknowledgment token before save.
