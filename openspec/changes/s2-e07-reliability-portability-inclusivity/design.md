# Architecture & System Design: Epic S2-E07

## 1. Domain Separation
- **Pure JVM Layer**: `com.coffeecreator.app.core.reliability` contains all mathematical unit conversion functions, circular buffer implementations, and backup verification logic without Android dependencies.
- **Data & Storage Layer**:
  - `AssetManager`: Manages SAF trees, manifest parsing, SHA-256 verification, and HTTP 206 resumable transfers.
  - `DataRepository`: Atomically serializes and deserializes records into Room database.
- **Presentation Layer**:
  - `CoffeeViewModel`: Exposes immutable `CoffeeUiState` with `userLanguage`, `unitPreferences`, `degradationState`, and `diagnosticBuffer`.
  - `CoffeeApp.kt`: Renders adaptive UI, Material 3 tokens, accessibility semantics, and language/unit controls.

## 2. Invariants & Mathematical Formulations
- **Temperature Invariant**:
  $$T_F = T_C \times \frac{9}{5} + 32, \quad T_C = (T_F - 32) \times \frac{5}{9}$$
  Identity invariant: $|T_C - \text{toCelsius}(\text{toFahrenheit}(T_C))| < 10^{-6}$.
- **Mass & Volume Invariants**:
  $1\text{ oz} \equiv 28.349523125\text{ g}$, $1\text{ fl oz} \equiv 29.5735295625\text{ mL}$.
  Density required: $m_g = V_{mL} \times \rho_{g/mL}$ where $\rho > 0$.
- **Circular Log Buffer Memory Bound**:
  Capacity $N = 500$, oldest items dropped on wrap-around, total memory $\le 512\text{ KB}$.
  PII regex filters wipe IPv4 addresses, storage paths, and query string secrets.
- **Graceful Degradation Hierarchy**:
  1. `FULL_SYSTEM`: All subsystems available.
  2. `MANUAL_TIMER`: BLE scale offline, virtual timer active.
  3. `MANUAL_OCR`: Camera/OCR offline, manual input active.
  4. `RULE_BASED_AI`: LLM absent, deterministic rules active.
  5. `OFFLINE_CORE`: Complete manual barista workstation.

## 3. Cryptographic Backup & Migration Schema
- Serialized payload checksum:
  $$\text{SHA256}(\text{recipes} + \text{beans} + \text{journal} + \text{cuppings} + \text{palate} + \text{settings})$$
- Verification gate: mismatch triggers immediate atomic rollback and preserves existing database state.
