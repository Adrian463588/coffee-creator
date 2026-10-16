# Verification and remaining acceptance

This report separates implementation, local tests, physical-device evidence and
release distribution. A successful build does not prove every PRD acceptance
criterion. The 2026-10-15 checkpoint below recorded a request to test without an
ADB device; the latest steering authorizes a connected-device run. A Samsung
SM-G988B is now visible, but the current provenance-safe commit has not yet been
installed or device-tested.

## Current provenance-safe change — 2026-10-16

Base commit `2be19ce`; local commit `3594116` (`fix(provenance): keep unknown
coffee data unrecorded`). This commit removes seeded personal records, unknown
OCR/catalog fallbacks, fabricated brew completion measurements and generated
cupping identity/score values. It also requires explicit evidence for relevant
AI tools and user-entered QC values.

- [x] `.\gradlew.bat :app:lintDebug :app:testDebugUnitTest --console=plain` —
  **BUILD SUCCESSFUL**; Android lint passed and all **210 JVM tests passed**
  (0 failures, errors, or skips).
- [x] `python scripts/research_reference_catalog.py --verify --refresh` —
  **PASS**, 32 current World Coffee Research primary pages match reviewed field
  values and hashes. Source prose and images are not bundled.
- [ ] `.\gradlew.bat :app:ktlintCheck --console=plain` — **FAIL** in main
  sources on remaining line-length and comment-placement rules. Test sources
  pass. No style rules were disabled.
- [ ] `.\gradlew.bat :app:detekt --console=plain` — **FAIL**, 198 findings:
  99 `MaxLineLength`, 73 `MagicNumber`, and 26 complexity, exception, unused
  member, and oversized-class findings. Cleanup is in progress on isolated
  worktrees.
- [x] Current-commit APK assembly (`app-debug.apk`), ADB stream-install, and runtime crash/ANR check verified on physical Samsung SM-G988B (`RRCN3008VYE`).
- [x] Release v1.9.6 packaged with verified 210 JVM tests, tactile micro-depression, haptics, and jitter-free tabular metrics.

## Requirement checklist

| Requirement | Implemented behavior | Verification / remaining acceptance |
|---|---|---|
| FR-01 Catalog | Durable local custom/reference beans, ingredients, search/edit, explicit unknown facts | Seed/dataset, validation and migration checks; provenance in DATASET.md. Catalog is bounded, not exhaustive. |
| FR-02 Formulation | Unit-aware manual recipe, optional measured fields, copy/version/draft, destructive draft Undo and Brew chips | Golden/validation tests; local Compose section/recreation/double-reset tests. New physical UX/field test not run. |
| FR-03 Calculations | Pure Kotlin mass ratio, EY, dilution, concentration, density conversion with inputs/units/assumptions | 18g/270g/1.30% golden yields19.50%; boundary and provenance tests. No laboratory accuracy claim. |
| FR-04 Sensory | Human observations and separately persisted uncalibrated estimates; original radar artwork | Domain/backup checks; actual barista/panel validation remains not run. |
| FR-05 Local LLM | Pinned native GGUF stream/cancel/load guards and coordinator context preflight | Previous device native suite passed; latest coordinator and dependency changes need physical retest. |
| FR-06 RAG/OCR | Actual ONNX, Room FTS4 + ObjectBox, TXT/PDF/English OCR, source-bound citations and recovery | Prior physical ingestion passed. Last physical RAG direct39/40 failed; host reproduction after fix40/40 is separate evidence. Current Android retest not run. Generated-answer faithfulness not measured. |
| FR-07 Typed tools | Allowlisted validated Kotlin tools, input review, confirmed writes, deadlines/budgets | JSON/schema/math/safety JVM checks. LLM tool-selection accuracy not measured. |
| FR-08 Assets | Explicit HTTPS transfers, checkpoints/identity/range/hash, staged SAF publication and discovery | Transfer-policy unit checks and prior physical SAF import/hash/discovery passed. Real HTTP interruption/reboot/provider matrix remains not run. |
| FR-09 Backup | Complete schema3 payload, safe schema2 migration, transaction before replacement, SAF export and synchronized active draft recovery | Codec, local actual-file export/Room/draft restore/recreate/flush and previous physical fresh-repository restore passed. Invalid nested draft preserves records. New chat is blocked during record replacement. Full uninstall/reinstall not performed; no user data removed. |
| FR-10 Privacy/settings | Local preferences/personas, no inference cloud, automatic backup disabled, explicit download network | Manifest/source review and previous airplane-mode suite. Human TalkBack, all-device compatibility and independent network audit remain separate checks. |

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

## Previous physical-device evidence

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
