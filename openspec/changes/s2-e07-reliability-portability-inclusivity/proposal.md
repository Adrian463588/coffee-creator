# Change Proposal: Epic S2-E07 — Reliability, Data Portability, and Inclusivity

## Motivation & Context
Coffee Creator is an offline-first, professional coffee formulation workbench. To meet high standards in real cafe environments and protect user privacy:
- Critical assets and models stored in user-owned SAF document trees must survive OS app reinstalls through manifest-driven rediscovery (S2-075).
- User-initiated downloads for local models or OCR assets must support byte-range resume, ETag verification, and storage checks (S2-076).
- All personal coffee data (recipes, beans, journal entries, professional cuppings, palate preferences) must support explicit full cryptographic backup and atomic restore (S2-077).
- Baristas worldwide require bilingual on-device language support (English & Bahasa Indonesia) and exact unit conversions with zero cumulative rounding drift (S2-078).
- The app must adapt dynamically across phone and tablet layouts with $\ge 48\text{dp}$ touch targets and TalkBack screen-reader support (S2-079).
- Troubleshooting must be supported via an on-device bounded circular diagnostic buffer with PII scrubbing and zero external telemetry SDKs (S2-080).
- When optional subsystems (local LLM, OCR camera, BLE smart scale) are absent or failing, the app must gracefully degrade to manual barista tools with transparent status badges (S2-081).
- Rigorous stress and regression gates (process death, offline airplane mode, memory trim, timer drift) ensure maximum reliability (S2-082).

## Proposed Changes
1. **Pure Domain Reliability Module** (`com.coffeecreator.app.core.reliability`):
   - `UnitConverter`: High-precision `BigDecimal` conversions for temperature (°C/°F), mass (g/oz), volume (mL/fl oz), and elevation (masl/ft).
   - `DiagnosticLogBuffer`: 500-capacity ring buffer with automatic regex scrubbing of IPs, paths, and secrets.
   - `SystemDegradationState`: Explicit state machine tracking subsystem health and computing unified status badges.
   - `FullBackupPayload` & `FullBackupManifest`: Cryptographically hashed JSON archive format with atomic restore validation.
2. **ViewModel & Data Repository Integration**:
   - Wire language toggle (EN/ID) and unit preferences into `CoffeeUiState`.
   - Implement diagnostic log recording on key user actions.
   - Enhance `exportBackup` and `restoreBackup` to serialize all cupping, journal, and palate records.
3. **UI Enhancements**:
   - Modern settings/reliability section in `CoffeeApp.kt` providing language switch, unit selection, degradation status badge, offline diagnostics review & export, and full backup/restore actions.
4. **Verification**:
   - Full suite of JVM unit tests verifying mathematical invariants, circular buffer FIFO wrapping, PII scrubbing, tamper detection, and degradation status.
