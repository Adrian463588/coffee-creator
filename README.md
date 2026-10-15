# Coffee Creator

Native Android coffee workbench: manual formulation, measured brew calculations,
versioned journals, sensory records, and optional offline AI over your own sources.
Kotlin, Jetpack Compose, Material3, Coroutines/Flow and Clean Architecture boundaries.
No account, cloud inference or telemetry. Only an asset download you request uses
network access; models need a first-time install before natural-language chat.

## Download

[Download the latest APK](https://github.com/Adrian463588/coffee-creator/releases/latest/download/coffee-creator-v1.1.0.apk)
· [Release notes](https://github.com/Adrian463588/coffee-creator/releases/tag/v1.1.0)
· [SHA-256 checksums](https://github.com/Adrian463588/coffee-creator/releases/download/v1.1.0/SHA256SUMS.txt)

The distribution APK is debug-signed, supports Android8/API26+ and arm64-v8a/x86_64.
The physical test reference is Samsung SM-G988B, Android13/API33. Other device/RAM/
provider tiers are separate validation work; see [verification](docs/VERIFICATION.md).
Install an APK from GitHub using Android's explicit install permission. For ADB:

```powershell
adb install --user 0 -r coffee-creator-v1.1.0.apk
```

Replacement installation preserves app records when the signing key matches.
Uninstall removes private records/grants; export a backup first if you want them back.

| Version | Date | APK | History |
|---|---|---|---|
| v1.1.0 | 2026-10-15 | [Download](https://github.com/Adrian463588/coffee-creator/releases/download/v1.1.0/coffee-creator-v1.1.0.apk) | [Notes](docs/releases/v1.1.0.md) |
| v1.0.0 | 2026-10-13 | [Historical APK](https://github.com/Adrian463588/coffee-creator/releases/download/v1.0.0/coffee-creator-v1.0.0.apk) | Prototype; AI/download/storage paths were simulated. Prefer1.1.0. |

Historical1.0.0 SHA256: `2843362ff29dd536da78f4263d5e2b66d1a98482c47c1376db037e3439ca02ff`.
[All releases](https://github.com/Adrian463588/coffee-creator/releases).

## Screenshots

Actual reference-device captures, not mockups. Empty/unavailable states reflect real state.

| Home | Formulation | Models |
|---|---|---|
| ![Home](docs/screenshots/home.png) | ![Formulation](docs/screenshots/formulation.png) | ![Models](docs/screenshots/models.png) |

## Features

- Search/edit local beans and ingredients; missing provenance/facts remain Unknown.
- Mass/volume formulation with density, food-grade/supplier limits and allergen acknowledgement.
- Immutable recipe revisions/copy/compare, timer and human sensory annotations.
- BigDecimal Kotlin math: ratio, approximate extraction, dilution, mass ppm and density conversions.
 18g dose/270g measured beverage/1.30% measured TDS gives19.50% approximate extraction;
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
- Complete integrity-checked plaintext SAF backup/restore of records, chat and source metadata.
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

Install JDK21 (bytecode17), Android SDK35/build-tools35, NDK27.0.12077973, CMake3.22.1
and Python3. Set ANDROID_HOME or an ignored local.properties. Android Studio can open
this repository; the Gradle wrapper pins8.9. Bundled assets are hash-verified and
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

## Privacy, limits and licenses

No operational network is required after assets are installed. The runtime has
no remote AI API, telemetry or document uploads. Source grants can be revoked;
retained chunks remain inspectable. Automatic Android backups are disabled.
Explicit exports contain personal plaintext records and a corruption checksum,
not encryption/authentication. Keep exports private; regrant SAF after reinstall.

Only verified compatible manifests may be loaded. Tiny models can give incorrect
answers; generated text is labeled unverified and source inspection remains essential.
The authored RAG fixture measures retrieval/tool contracts; independently adjudicated
answer faithfulness, broad provider/RAM/device coverage and field usability are
separate gates listed honestly in verification. No exact flavor prediction is promised.

See [third-party assets and licenses](docs/THIRD_PARTY.md). Do not infer an application
license from dependency licenses. Contributions must preserve provenance, scientific
validation, security, accessibility and the blueprint requirements.
