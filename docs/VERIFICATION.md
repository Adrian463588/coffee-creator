# Verification and remaining acceptance

This report records evidence for the v1.10.0 candidate and keeps software gates,
device checks, and field acceptance separate. A successful build does not establish
every PRD acceptance criterion.

## Current candidate — 2026-10-17

- [x] `rtk proxy .\gradlew.bat :app:ktlintCheck :app:detekt :app:lintDebug :app:testDebugUnitTest :app:assembleDebug :app:assembleDebugAndroidTest --continue --console=plain --no-parallel --max-workers=1` — **BUILD SUCCESSFUL**; 120 actionable tasks, 244 JVM tests, zero failures/errors/skips. KtLint, Detekt and Android lint passed. Lint/compiler advisories remain visible; no blanket suppression was added.
- [x] `rtk proxy python scripts/research_reference_catalog.py --verify --refresh` — **PASS**; 32 reviewed World Coffee Research variety records match the current primary pages and stored receipts. It does not establish that the bounded catalog is exhaustive.
- [x] `rtk proxy python scripts/verify_apk.py app/build/outputs/apk/debug/app-debug.apk` — **PASS**; APK ZIP structure and 20 native libraries for arm64-v8a/x86_64 meet the repository's 16 KiB alignment checks. Static inspection is not runtime proof.
- [x] Debug APK: 122,338,672 bytes, SHA-256 `d06e9c6bace080fb4e24cf34dfe66af21817e6fd2a18e9cd811c03f4c478fbc4`; package version 1.10.0, code 16, minSdk 26, targetSdk 35. Debug signing only; no production signing claim.
- [x] Physical device: Samsung SM-G988B (`RRCN3008VYE`), Android 13/API 33, Owner user 0. Replaced with `adb install --user 0 -r` (**Success**); no uninstall or app-data clear. `MainActivity` reached `RESUMED`, app process remained active, and `adb logcat -d -t 2000 -s AndroidRuntime:E` returned no errors after launch. Airplane mode was enabled (`settings get global airplane_mode_on = 1`). Instrumentation was invoked through `adb shell am instrument --user 0` because the Gradle `-Pandroid.testInstrumentationRunnerArguments.*` property conflicts with the pinned Protobuf Gradle plugin's `sourceSets` configuration.
- [x] 19 focused device test methods passed: `ManualJourneyTest` 4, `BrewSectionTest` 3, `CatalogPhotoCaptureDeviceTest` 1, `OfflineImageOcrDeviceTest` 4, `DocumentDeviceTest` 4, `RagEvaluationDeviceTest` 1, and two `OfflineAiDeviceTest` cases (verified MiniLM embeddings and malformed-GGUF recovery).
- [x] On-device authored RAG fixture: 100 questions (90 answerable), direct facts 40/40, Recall@5 0.944444, cross-document 0.85, numeric/tool-needed 0.90, conflict cases 1.0, correct abstentions 10/10, false abstentions 0, deterministic numeric cases 20/20. Fixture SHA-256 `d67ae1172a0618b896ac20630fe3ee730b8b200c5a83729f3f3f0aa333da7b8d`. Airplane mode and no active network were recorded. This is a project-authored fixture, not an independently adjudicated answer-quality study; generated-answer faithfulness and LLM tool-selection accuracy were not measured.
- [ ] Real bag/equipment photo acceptance remains pending. Device OCR coverage used generated test images; no human-captured coffee packaging was supplied or inspected. Correction, attachment retention and camera-to-record behavior still need a real label journey.
- [ ] Current SAF checksum/import acceptance was blocked before the operation because no persistable permission grant exists for the QA tree URI. No SAF-owned file or user data was changed. Obtain a user-selected QA folder grant before rerunning asset/backup provider scenarios.
- [ ] Qwen native generation, Kafi/Gemma transfer and inference are unverified on this device. The Qwen test GGUF is absent from the device; verified MiniLM embedding and malformed-GGUF recovery tests do not prove LLM generation or model catalog downloads.
- [ ] Manual TalkBack, font-scale/expanded-layout review, macrobenchmark, independently adjudicated RAG faithfulness, and barista/sensory field evaluation remain outstanding.

## PRD requirement checklist

| Requirement | Implemented behavior | Evidence and remaining acceptance |
|---|---|---|
| FR-01 Catalog | Local bean/ingredient editing and search; unknown lot facts remain unknown; sourced variety references | 32 primary WCR records reverified; validation and persistence unit tests pass. Dataset is bounded, not exhaustive. |
| FR-02 Formulation | Unit-aware recipe entry, saved revisions/drafts, measurement provenance, separate Brew chips/cards | JVM/Compose BDD tests pass; keyboard now closes before save validation so errors remain visible. Real barista workflow review remains pending. |
| FR-03 Calculations | Deterministic typed ratio, dilution, concentration, density and approximate extraction | Golden case 18 g dose / 270 g beverage / measured 1.30% TDS = approximate 19.50% EY; boundary and provenance tests pass. No lab accuracy claim. |
| FR-04 Sensory | Human-entered observations and visibly uncalibrated estimates | Domain/persistence tests pass. Sensory panel and repeatability validation remain pending. |
| FR-05 Local LLM | Optional local GGUF adapter with bounds, cancellation, unsupported-model recovery | Malformed-GGUF recovery passed on device; actual Qwen generation/cancel/memory-pressure acceptance not run on this candidate. Manual mode remains available without a model. |
| FR-06 RAG/OCR | Local TXT/PDF/OCR ingestion, hybrid retrieval, source-linked citations and abstention | TXT/PDF/scanned-PDF and authored RAG device cases pass in airplane mode. Fixture scores above; real label photos and independent answer-faithfulness review remain pending. |
| FR-07 Typed tools | Allowlisted validated Kotlin actions, bounded inputs and confirmation for writes | Schema, range and deterministic calculation JVM tests pass. Model tool-selection accuracy remains unmeasured. |
| FR-08 Models/assets | Explicit transfers into a user-selected SAF folder with identity/range/hash validation | Catalog metadata and transfer policy tests pass. Current SAF provider/download/restore matrix blocked by missing user grant; no Kafi/Gemma transfer is claimed. |
| FR-09 Backup | Versioned, checksum-validated backup and transactional restore with draft synchronization | JVM codec/Room/restore tests pass; current physical SAF restore not run because no tree grant. No uninstall/reinstall or user-data deletion was performed. |
| FR-10 Privacy/settings | Offline-first workflows, no telemetry/cloud inference, explicit user-requested asset network | Airplane-mode RAG/device checks pass; source/manifest review passes. Independent network audit and human accessibility/device matrix remain pending. |

## Historical local gates — 2026-10-15

- [x] Aggregate Gradle command below: **BUILD SUCCESSFUL**,207 tasks,
  detekt/ktlint/lint/JVM/debug/androidTest/benchmark APK builds.
- [x] **110/110 JVM tests**,21 suites, zero failures/errors/skips. Includes eight
  real Compose activity cases and eight shared real Room cases on Robolectric.
- [x] **83 host C++ checks**: GGUF64, Gemma text-format13, prompt-boundary6,
  compiled with `g++ -std=c++17 -Wall -Wextra -Werror`. Structural fixtures never
  execute inference. Foreign/nested architecture dimensions fail closed.
- [x] Dataset research verification:32 primary WCR records match checked facts.
- [x] Actual ONNX Runtime1.21.1 host parity: three pinned vectors, max error
  2.9802322387695312e-8 against1e-4 tolerance. Separate from Android execution.
- [x] Debug APK: **127,647,021bytes**, SHA256
  `e25474341ca5df7905118bfdd7f3e9a6e747b0b7a877e4a8817e0e6b279bcca8`.
  ZIP integrity and all20 native libraries in both ABIs pass16KiB ELF checks.
  Build-tools35 `zipalign -c -P 16 -v 4` and `apksigner verify` pass.
- [x] Debug certificate SHA256
  `fc6cc5cec1eb93984bc0cb0842b6d2f9c4bb6549f97617d42f457dc8983b55b4`
  matches the downloaded historical v1.0.0 release. `aapt2 dump badging` confirms
  applicationId, versionCode2/versionName1.1.0, API26–35 and both declared ABIs.
- [x] Independent final review; no remaining actionable findings in the reviewed
  draft/model/SAF/release changes. `git diff --check` and publication scan pass.
  Existing three historical APK blobs are preserved; the current historical APK
  equals origin/main byte-for-byte. No new APK/LLM weights/signing secrets staged.
- [x] [Remote source CI](https://github.com/Adrian463588/coffee-creator/actions/runs/38722136701)
  passed on Ubuntu24.04 at release commit
  `fd9e5ed7a9ef5281038f7df07786ef21ec1634cf`.
- [x] [v1.1.0 release](https://github.com/Adrian463588/coffee-creator/releases/tag/v1.1.0)
  was public at this historical checkpoint; it is not the current latest release.
  The current public latest is [v1.9.5](https://github.com/Adrian463588/coffee-creator/releases/tag/v1.9.5),
  verified from GitHub on 2026-10-16. Historical releases remain available.

Machine-readable executed receipts: [local gates](verification/local-gates-2026-10-15.json),
[ORT alignment](verification/ort-1.21.1.json), [host parity](verification/ort-host-parity-1.21.1.json),
[model metadata](verification/model-catalog-receipts.json). Screenshots in README
are real local native-graphics renders. They were visually inspected, including
warm semantic navigation colors and separate Brew cards.
Distribution evidence: [release receipt](verification/release-v1.1.0.json).
Later runner-pinning/documentation changes preserve the application tree
`6b20675b17f8c94f5a064394b58c45a3c1ac683d`; no application code changed after release.

Lint completed with **zero errors,85 warnings,one informational finding**:
75 dependency advisories,6 AGP advisories,1 target-API advisory,3 transitive
BouncyCastle TrustAll warnings,1 Compose state autoboxing informational finding.
No warning was suppressed to obtain a release. Dependency/target upgrades require
their own compatibility validation; debug preview does not claim Play compliance.

## Historical physical-device evidence (older candidates)

Reference: Samsung SM-G988B, Android13/API33, arm64-v8a. Tests used replace-install
for user0 and isolated UUID databases/owned QA files. No uninstall, global reset
or user-record deletion. A real system SAF grant was used for an explicitly owned
QA folder. Native GGUF was the actual verified Qwen2.5-0.5B Q4_K_M file, not a fake
executor. ONNX/OCR ran on-device in airplane mode.

- A previous full run passed21/21: native/embedding3, persistent repository6,
  document ingestion4, Compose journeys4, SAF asset/backup3, RAG fixture1.
  Its RAG assertions were too weak for personal facts (0/10 abstentions), so this
  pass was not accepted as full RAG readiness.
- Tightened abstention and FTS tests exposed regressions. Last focused run:
  repository6/6, RAG0/1; direct39/40, true abstentions10/10, false abstentions1,
  recall@5=0.9222222222, typed numeric20/20. This remains a failed device gate.
- Subsequent general inflection/ownership policy fixes reproduced40/40 direct,
  10/10 personal abstentions, zero false abstentions and recall@5=0.9444444444 on
  host Python with the actual pinned ONNX and SQLite FTS4. This is an algorithm
  reproduction, not an Android run or independently adjudicated research result.
- Fixture:100 authored contract questions,31 source files/61 chunks; SHA256
  d67ae1172a0618b896ac20630fe3ee730b8b200c5a83729f3f3f0aa333da7b8d.
  Generated-answer faithfulness and natural-language tool selection were not measured.
- Android C++ prompt escaping executable passed6 cases previously. Actual native
  load/stream/cancel and three embedding vectors passed the earlier device suite.
- Macrobenchmark test APK initially failed installation because it was unsigned.
  Debug signing was fixed and APK rebuilt; no successful benchmark run followed.
  Startup/frame metrics and a generated Baseline Profile are therefore not claimed.

## Reproducible commands

Use JDK21, pinned SDK/NDK/CMake from README and verify assets first:

```powershell
python scripts/prepare_assets.py
.\gradlew.bat :app:ktlintFormat
.\gradlew.bat :app:detekt :app:ktlintCheck :app:lintDebug :app:testDebugUnitTest :app:assembleDebug :app:assembleDebugAndroidTest :app:assembleBenchmark :benchmark:assembleBenchmark --console=plain
python scripts/verify_apk.py app/build/outputs/apk/debug/app-debug.apk
git diff --check
```

Formatting and build gates run sequentially. Instrumented tests and macrobenchmarks
are authored/compiled separately from execution. Do not run commands that uninstall
or clear application data merely to obtain green results.

## Review limits

- Lint dependency/target API advisories stay visible. PDFBox's transitive
  BouncyCastle contains TrustAllX509TrustManager warnings; the app HTTP client
  uses default TLS and never installs these trust managers. See ADR-006.
- App uses a debug signature as requested, without a production signing-key claim.
- APK structural alignment/signature/checksum evidence does not establish runtime
  compatibility on every ABI, RAM tier, Android version or SAF provider.
- User data/SAF grants are never implied to survive uninstall. Explicit backups
  and regrant/revalidation are required. Plaintext exports need user custody.
