# Coffee Creator

Native Android coffee workbench: manual formulation, measured brew calculations,
versioned journals, sensory records, and optional offline AI over your own sources.
Kotlin, Jetpack Compose, Material 3, Coroutines/Flow and Clean Architecture boundaries.
No account, cloud inference or telemetry. Only an asset download you request uses
network access; models need a first-time install before natural-language chat.

## Download

[Download the latest debug preview APK (v1.10.0)](https://github.com/Adrian463588/coffee-creator/releases/download/v1.10.0/coffee-creator-v1.10.0.apk)
· [Release notes](docs/releases/v1.10.0.md)
· [SHA-256 checksums](https://github.com/Adrian463588/coffee-creator/releases/download/v1.10.0/SHA256SUMS.txt)

The distribution APK is debug-signed, targets Android 8/API 26+ and arm64-v8a/x86_64.
The physical test reference is Samsung SM-G988B, Android 13/API 33. Other device/RAM/
provider tiers are separate validation work; see [verification](docs/VERIFICATION.md).
Install an APK from GitHub using Android's explicit install permission. For ADB:

```powershell
adb install --user 0 -r coffee-creator-v1.10.0.apk
```

Replacement installation preserves app records when the signing key matches.
Uninstall removes private records/grants; export a backup first if you want them back.

| Version | Date | APK | History |
|---|---|---|---|
| v1.10.0 | 2026-10-17 | [Download](https://github.com/Adrian463588/coffee-creator/releases/download/v1.10.0/coffee-creator-v1.10.0.apk) | [Notes](docs/releases/v1.10.0.md) · Offline bean/equipment label capture and review, conservative OCR drafts, durable record restore, verified Kafi/Gemma model catalog, evidence-safe extraction calculations, and keyboard-safe validation |
| v1.9.6 | 2026-10-16 | [Download](https://github.com/Adrian463588/coffee-creator/releases/download/v1.9.6/coffee-creator-v1.9.6.apk) | [Notes](docs/releases/v1.9.6.md) · Tactile Industrial & Precision Coffee Lab UI/UX Refinement: standardized 0.97f spring micro-depression physics & subtle haptic feedback across buttons/chips/dropdowns, jitter-free tabular monospace figures (tnum) on timers and telemetry, 1px solid hairline borders (0.40f alpha), concentric nested corner geometry, and 100% JVM/device verification |
| v1.9.5 | 2026-10-16 | [Historical APK](https://github.com/Adrian463588/coffee-creator/releases/download/v1.9.5/coffee-creator-v1.9.5.apk) | [Notes](docs/releases/v1.9.5.md) · Epic S2-E05 Deepening: Complete interactive multi-ring Compose Canvas Sensory Wheel (`SensoryWheelVisualizer`), touch-arc polar hit detection & category zoom, expanded 90+ SCA/WCR & Roda Rasa taxonomy across 9 categories and 26 subcategories with dedicated commercial defect/taint catalog, multi-select `FlavorWheelPickerDialog` integrated into cupping and journal notes, dynamic cupping attribute scoring across all 4 templates, and 100% JVM & physical device verification |
| v1.9.4 | 2026-10-16 | [Historical APK](https://github.com/Adrian463588/coffee-creator/releases/download/v1.9.4/coffee-creator-v1.9.4.apk) | [Notes](docs/releases/v1.9.4.md) · Verified E2E ADB Device Release: BackHandler sub-screen navigation, restored journal recipe revision list, FlowRow cupping action buttons with single-line constraint, auto-logged sensory observation persistence & exception safety, 100% connected & unit test pass on Samsung SM-G988B |
| v1.9.3 | 2026-10-16 | [Historical APK](https://github.com/Adrian463588/coffee-creator/releases/download/v1.9.3/coffee-creator-v1.9.3.apk) | [Notes](docs/releases/v1.9.3.md) · Anti-Slop Ergonomics & Microinteraction Refinement: encapsulated bag inventory Surface container (1px hairline border, tabular mass digits, status badge, single-line TactileButtons), modernized action headers (`Cafe Cup Logbook`, `Cupping Sessions`, `Roast-Rest Kinetics`) with weighted title columns and single-line `TactileButton` (`RoundedCornerShape(8.dp)`), zero multi-line wrapping into circular/oval button blobs |
| v1.9.2 | 2026-10-16 | [Historical APK](https://github.com/Adrian463588/coffee-creator/releases/download/v1.9.2/coffee-creator-v1.9.2.apk) | [Notes](docs/releases/v1.9.2.md) · Anti-Slop UI/UX Tactile Navigation Refinement: single-line horizontal scroll rows with dedicated M3 icons and spring micro-depression physics (`scale = 0.96f`) across Journal categories, Journal method filters, Sensory mode tabs, and Sensory flavor taxonomy categories; zero multi-row chip wrapping; 48dp+ touch target compliance |
| v1.9.1 | 2026-10-16 | [Historical APK](https://github.com/Adrian463588/coffee-creator/releases/download/v1.9.1/coffee-creator-v1.9.1.apk) | [Notes](docs/releases/v1.9.1.md) · Tactile Precision Lab & Anti-Slop UI/UX Refinement: single-row horizontally scrollable category bar with dedicated icons, unified precision laboratory timer & telemetry console (tabular monospace digits, live pulse LED, integrated mass & flow rate, 54dp tactile primary CTA, segmented secondary controls), scrollable brew section tabs, and zero regression |
| v1.9.0 | 2026-10-15 | [Historical APK](https://github.com/Adrian463588/coffee-creator/releases/download/v1.9.0/coffee-creator-v1.9.0.apk) | [Notes](docs/releases/v1.9.0.md) · Epic S2-E01: Coffee bean agronomic CRUD, multi-state bag inventory & dose deduction, roast-rest calculator & freeze pause, grinder calibration & burr profiles, multi-category gear catalog, water mineral chemistry ($Ca^{2+}, Mg^{2+}, Na^+, HCO_3^- \to GH/KH/TDS$), food-grade safety verification, offline OCR bag scanner, conflict-resolved JSON/CSV import/export, green coffee physical QC defect grading, and roast batches with RoR & DTR % |
| v1.8.0 | 2026-10-15 | [Historical APK](https://github.com/Adrian463588/coffee-creator/releases/download/v1.8.0/coffee-creator-v1.8.0.apk) | [Notes](docs/releases/v1.8.0.md) · Epic S2-E07: SAF persistent model manifest, resumable downloads (ETag/Range), cryptographic full backup/restore, bilingual EN/ID, localized physics units, adaptive Compose layouts, on-device scrubbed ring buffer diagnostics, and graceful degradation |
| v1.7.0 | 2026-10-15 | [Historical APK](https://github.com/Adrian463588/coffee-creator/releases/download/v1.7.0/coffee-creator-v1.7.0.apk) | [Notes](docs/releases/v1.7.0.md) · Epic S2-E06: Offline personal palate quiz, Bayesian taste adaptation, explainable inventory recommendations, single-variable OVAT feedback loop, OCR coffee bag scanner, grounded AI barista chat, schema-validated tools, persona presets, weather/altitude boiling point thermodynamics, and food-grade safety & allergen warnings |
| v1.6.0 | 2026-10-15 | [Historical APK](https://github.com/Adrian463588/coffee-creator/releases/download/v1.6.0/coffee-creator-v1.6.0.apk) | [Notes](docs/releases/v1.6.0.md) · Epic S2-E05: Hierarchical flavor wheel, accessible list alternative, safe food-grade calibration reference lab, blind cupping sessions with controlled reveal, multi-evaluator consensus & IRAI agreement, physical green/roasted QC, and roast-rest degassing curve |
| v1.5.0 | 2026-10-15 | [Historical APK](https://github.com/Adrian463588/coffee-creator/releases/download/v1.5.0/coffee-creator-v1.5.0.apk) | [Notes](docs/releases/v1.5.0.md) · Epic S2-E04: Brew journal, espresso dial-in progression, single-variable OVAT suggestions, side-by-side numeric deltas, calendar streak, caffeine estimation, and Beanconqueror JSON import |
| v1.4.0 | 2026-10-15 | [Historical APK](https://github.com/Adrian463588/coffee-creator/releases/download/v1.4.0/coffee-creator-v1.4.0.apk) | [Notes](docs/releases/v1.4.0.md) · Epic S2-E03: Active brew timer, BLE scale abstraction, rolling flow rate (g/s), swirl artifact filter, auto-start/stop, proximity cues, and journal logging |
| v1.3.0 | 2026-10-15 | [Historical APK](https://github.com/Adrian463588/coffee-creator/releases/download/v1.3.0/coffee-creator-v1.3.0.apk) | [Notes](docs/releases/v1.3.0.md) · Epic S2-E02: Recipe composer, Hamilton-Hare pour scaling, optical Brix→TDS refractometer lab, expert dial-in, and offline Android Sharesheet |
| v1.2.0 | 2026-10-15 | [Historical APK](https://github.com/Adrian463588/coffee-creator/releases/download/v1.2.0/coffee-creator-v1.2.0.apk) | [Notes](docs/releases/v1.2.0.md) · Modern UI cards, FlowRow chip Brew navigation, custom bean/method inputs, curated library (10 recipes, 8 single-origins, 5 sensory logs) |
| v1.1.0 | 2026-10-15 | [Historical APK](https://github.com/Adrian463588/coffee-creator/releases/download/v1.1.0/coffee-creator-v1.1.0.apk) | [Notes](docs/releases/v1.1.0.md) · Real Room/FTS4 records, local llama.cpp GGUF/ONNX RAG, SAF storage |
| v1.0.0 | 2026-10-13 | [Historical APK](https://github.com/Adrian463588/coffee-creator/releases/download/v1.0.0/coffee-creator-v1.0.0.apk) | Prototype; AI/download/storage paths were simulated. Prefer 1.9.6. |

v1.10.0 APK verification: 122,338,672 bytes, SHA256: `d06e9c6bace080fb4e24cf34dfe66af21817e6fd2a18e9cd811c03f4c478fbc4`.
v1.9.6 APK verification: 121,486,374 bytes, SHA256: `d3d28471fdf73702d7b9016776552bbfc3ce72989c5cb8552c9d0e207e352c6f`.
Historical 1.9.5 SHA256: `d84569a5035fa1d9f0aaf7a7f697dd8d073bd72f90ecb4e15ed75474a58129a4`.
Historical 1.9.4 SHA256: `d5a0a24c3d4b2822fb5a06db421128e47bed6ee18e5a61e75836d5bd51de368f`.
Historical 1.9.3 SHA256: `04634335007971c79a219d7c77193cb111276f56f23820eeaefb76bfca812ebf`.
Historical 1.9.2 SHA256: `ab068dea442fafbf44939cc96730387be5b1862e345d4c30733c41ed1aaea648`.
Historical 1.9.1 SHA256: `917f9cdbab661c5d991a105047f6cff93aa64d37b8c0a06be826c380152bb921`.
Historical 1.9.0 SHA256: `4801d10dbc143dfa8e7d0f47b252ecf955e571c0e6219ecc4a9e3cb8ebe5ab35`.
Historical 1.8.0 SHA256: `1DC681387D6DE0E9EEF0B8F821B17B2E22269B2A8F4D3275E8C4594468037275`.
Historical 1.7.0 SHA256: `1CB40B36E6DCB734B60656F1E3539AB8BE34722283C85B08B0EEB3FD54A1C9B7`.
Historical 1.6.0 SHA256: `9b2060e67ff69d29aa90468dfd4b00d92f63f78778524f5e6961502764c4958b`.
Historical 1.5.0 SHA256: `99a32c636b467063d451e1a8b0a5dae5f8431ad5a648d3fc126816bc963bfc6f`.
Historical 1.4.0 SHA256: `def99509dadd541a93ae5dfdd81085d1f7af7902ddb92ade38b07895f2bb7712`.
Historical 1.3.0 SHA256: `78ca7128c350c40ab5ff3b4d8148ed09bc7a1a68a0212fc753896f4b91bccc89`.
Historical 1.2.0 SHA256: `4c197fcee278508ee62ba57b3a8ac6524186c9f56ebcc07bce58914335bdf2eb`.
Historical 1.0.0 SHA256: `2843362ff29dd536da78f4263d5e2b66d1a98482c47c1376db037e3439ca02ff`.
[All releases](https://github.com/Adrian463588/coffee-creator/releases).

Latest local gates for v1.10.0: 244 JVM tests passed, KtLint, Detekt, Android lint,
debug APK and androidTest APK builds passed. Nineteen focused tests ran on a Samsung
SM-G988B in airplane mode. The verification report records setup-blocked SAF/model
checks and product acceptance still needing real label photos, accessibility review,
and independent RAG/field validation; see the [requirement checklist and evidence](docs/VERIFICATION.md).

## Screenshots

Actual local Robolectric/Compose renders from isolated BDD tests, not physical-device
captures. Test recipe values are authored QA inputs, not measured user brews.
These screenshots are local Robolectric/Compose renders, not current physical-device captures.

| Home | Formulation | Models |
|---|---|---|
| ![Home](docs/screenshots/home.png) | ![Formulation](docs/screenshots/formulation.png) | ![Models](docs/screenshots/models.png) |

## Features

- Search/edit local beans and ingredients; 34 sourced reference cards on a fresh install,
 including 32 newly researched varieties with per-field source receipts. Lot facts remain
 Unknown. See [dataset provenance and reuse restrictions](docs/DATASET.md).
- Brew uses Recipe, Measurements, Ingredients and Tools chips with separate cards,
 persistent input/selection, compact/large-text scrolling, destructive draft Undo
 and a visible Save/ratio bar.
- Mass/volume formulation with density, food-grade/supplier limits and allergen acknowledgement.
- Immutable recipe revisions/copy/compare, timer and human sensory annotations.
- BigDecimal Kotlin math: ratio, approximate extraction, dilution, mass ppm and density conversions.
 18 g dose/270 g measured beverage/1.30% measured TDS gives 19.50% approximate extraction;
 dose/water alone cannot infer TDS. Additives/immersion have explicit limitations.
- Estimated sensory direction has a separately persisted type, basis and uncertainty,
 with no calibrated confidence claim or proprietary SCA graphic.
- Streaming/cancellable local GGUF inference through pinned CPU llama.cpp, editable personas
 and bounded parameters. Missing/unsupported models leave manual workflows usable.
- Real TXT/text-PDF import, bundled offline English OCR, deduplication, ONNX embeddings,
 Room FTS4/ObjectBox HNSW hybrid retrieval and inspectable local sources. No evidence abstains.
- Allowlisted typed tools preserve exact Kotlin outputs. LLM-proposed quantities require
 review; generated recipe saves require confirmation. No runtime shell/browser/network tools.
- User-selected SAF model directory, strict manifest/size/SHA verification, private staging,
 HTTP Range/identity checkpoints, pause/cancel/retry and verified bundle rediscovery.
- Download catalog: Qwen2.5 0.5B (491.40 MB), Kafi Llama3.2 1B (807.69 MB),
 Gemma4 E2B IT Q4_K_M (3.11 GB). All require a selected SAF folder. Actual immutable
 artifacts, licenses, text-only constraints and validation status: [model guide](docs/MODELS.md).
- Complete integrity-checked plaintext SAF backup/restore of records, chat and source metadata.
 Export includes recent draft input; restore replaces stale active/autosaved drafts safely.
 External models and exports are independently owned; deleting records does not delete them.
- Adaptive phone/rail navigation, light/dark, large controls, local diagnostics and privacy settings.

## Architecture

```text
Compose UI -> Hilt ViewModel -> pure model/engine + repository/adapter contracts
  core/model, core/engine: typed quantities, validation, exact scientific math
  core/data: Room records/chunks/FTS4, preserving migrations, transactional backups
  core/ai: llama.cpp JNI, ONNX WordPiece/pooling, ObjectBox VectorStore, typed tools
  core/filesystem: SAF, bounded PDF/Tesseract OCR, WorkManager/OkHttp, Proto DataStore
  ui: immutable lifecycle-aware StateFlow, nine screens, Material3 tokens
  benchmark: physical Startup/FrameTiming/Baseline Profile tests
```

Grouped modular monolith; split modules only for a measured boundary/build benefit.
The [blueprint](coffee-creator-blueprint/PRD.md) governs features. Read [AGENTS](AGENTS.md),
[plan](docs/IMPLEMENTATION_PLAN.md), [ADRs](docs/adr/ADRS.md) and [OpenSpec change](openspec/changes/verified-android-implementation/).
BDD/SDD uses Given/When/Then contracts and planner/developer/independent-review phases,
informed by [OpenSpec](https://github.com/Fission-AI/OpenSpec) and
[BMAD](https://github.com/bmad-code-org/BMAD-METHOD). RTK/Ponytail/Caveman guide authoring only.

## Build and tests

Install JDK 21 (bytecode 17), Android SDK 35/build-tools 35, NDK 27.0.12077973, CMake 3.22.1
and Python3. Set ANDROID_HOME or an ignored local.properties. Android Studio can open
this repository; the Gradle wrapper pins 8.9. Bundled assets are hash-verified and
reproducibly fetched before Gradle; no LLM weights are committed or included in APK.

```powershell
python scripts/prepare_assets.py
.\gradlew.bat :app:lintDebug :app:detekt :app:ktlintCheck :app:testDebugUnitTest :app:assembleDebug :app:assembleDebugAndroidTest
```

For physical offline AI tests, prepare and stage the actual test GGUF according to
[OFFLINE_AI_DEVICE_TEST](docs/OFFLINE_AI_DEVICE_TEST.md), then install the debug and
androidTest APKs with replacement install and run the runner. Existing app/user
records must be preserved. Benchmark instructions/evidence are in verification.
CI runs pinned static/unit/build gates; physical-device outcomes are not inferred
from CI. Re-run regression gates before release. Release CLI workflow is
`scripts/publish_release.ps1`; notes/checksums and source target must match the APK.
Local JVM tests include actual Compose activity/Room execution through Robolectric;
native guard/format tests also run on the host. See [complete evidence and checklist](docs/VERIFICATION.md).

Optional development-only source audit (requires internet; never runs in the app):

```powershell
python -m pip install -r scripts/requirements-research.txt
python scripts/research_reference_catalog.py --verify
```

## Privacy, limits and licenses

No operational network is required after assets are installed. The runtime has
no remote AI API, telemetry or document uploads. Source grants can be revoked;
retained chunks remain inspectable. Automatic Android backups are disabled.
Explicit exports contain personal plaintext records and a corruption checksum,
not encryption/authentication. Keep exports private; regrant SAF after reinstall.

Only verified compatible manifests may be loaded. Tiny models can give incorrect
answers; generated text is labeled unverified and source inspection remains essential.
The latest airplane-mode RAG fixture run on Samsung SM-G988B scored 40/40 direct facts,
Recall@5 0.944444, 10/10 correct abstentions, and 20/20 deterministic numeric cases.
It is an authored fixture, not independent adjudication; generated-answer faithfulness
and LLM tool selection remain unmeasured. The on-device OCR test used generated label
images, not real coffee packaging. Kafi/Gemma transfer and inference, SAF provider
recovery with a fresh user grant, TalkBack/font-scale review, and field sensory testing
remain pending. This debug preview does not claim all production acceptance gates passed.

See [third-party assets and licenses](docs/THIRD_PARTY.md). Do not infer an application
license from dependency licenses. Contributions must preserve provenance, scientific
validation, security, accessibility and the blueprint requirements.
