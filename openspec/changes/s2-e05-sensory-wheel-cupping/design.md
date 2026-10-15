# OpenSpec Design: Epic S2-E05 — Sensory Wheel and Professional Cupping

## 1. Architectural Boundaries & Domain Isolation
All sensory calculations, taxonomy hierarchies, and consensus algorithms are placed in pure Kotlin JVM classes under `com.coffeecreator.app.core.sensory`:
- Pure Kotlin JVM, zero dependencies on Android framework (`android.*`), Room, or Compose.
- Exact decimal mathematics with `BigDecimal` half-up rounding.
- Physical units enforced: mass in grams, duration in days, temperature in Celsius, moisture in %, water activity in $a_w$, density in g/L.

## 2. Mathematical Models & Equations

### 2.1 Multi-Evaluator Consensus & Dispersion Metrics (S2-059)
Given $N$ evaluators ($N \ge 2$) scoring sample $s$ across attribute $k \in \{1, \dots, K\}$:
- **Arithmetic Mean**: $\bar{x}_k = \frac{1}{N} \sum_{i=1}^N x_{i,k}$
- **Sample Standard Deviation**: $s_k = \sqrt{\frac{1}{N - 1} \sum_{i=1}^N (x_{i,k} - \bar{x}_k)^2}$
- **Inter-Rater Agreement Index (IRAI)**:
  $$\text{IRAI}_k = \max\left(0.0, 1.0 - \frac{s_k}{\sigma_{\text{threshold}}}\right)$$
  Where $\sigma_{\text{threshold}} = 0.50$ for Q-scale (6–10) and $1.00$ for 1–10 scale.
- **Outlier Flagging**: Evaluator $i$ is an outlier if $|x_{i,k} - \bar{x}_k| > 2 \cdot s_k$.
- **Descriptor Overlap (Jaccard)**:
  $$J(D_A, D_B) = \frac{|D_A \cap D_B|}{|D_A \cup D_B|}$$

### 2.2 Cupping Score Aggregation & Defect Deductions (S2-055)
Specialty standard Q-grade score:
$$\text{Score} = \sum_{k=1}^K x_k - \text{DefectDeduction}$$
Where $\text{DefectDeduction} = (\text{TaintCups} \times 2) + (\text{FaultCups} \times 4)$.

### 2.3 Roast-Rest Degassing & Staling Kinetics (S2-062)
Relative residual dissolved $\text{CO}_2$ concentration after $t$ rest days:
$$C_{\text{CO}_2}(t) = \exp\left(-\frac{t}{\tau_{\text{degas}}}\right)$$
Sensory maturity index:
$$M(t) = \left(1 - \exp\left(-\frac{t}{\tau_{\text{open}}}\right)\right) \cdot \exp\left(-\frac{t}{\tau_{\text{stale}}}\right)$$
Roast rest phase thresholds:
- $t < t_{\text{peak,start}}$: `FRESH_DEGASSING` (High $\text{CO}_2$, harsh crema, muted clarity)
- $t_{\text{peak,start}} \le t \le t_{\text{peak,end}}$: `PEAK_MATURITY` (Optimal clarity, sweetness, and balanced extraction)
- $t_{\text{peak,end}} < t \le t_{\text{stale}}$: `POST_PEAK` (Gentle oxidation, fading delicate aromatics)
- $t > t_{\text{stale}}$: `STALE` (Noticeable lipid oxidation, flat, papery or woody notes)

### 2.4 Epistemological Provenance Distinction (S2-054)
1. `HUMAN_OBSERVATION`: Empirical human score, confidence 1.0, solid line, no disclaimer.
2. `AI_HEURISTIC_HYPOTHESIS`: Uncalibrated directional rule, dashed line, mandatory disclaimer: *"Heuristic assumption; validate by observation. No measured laboratory accuracy claimed."*
3. `CALIBRATED_PREDICTION`: Calibrated statistical model with explicit confidence interval (e.g. `8.25 [7.95 - 8.55] (95% CI)`).
