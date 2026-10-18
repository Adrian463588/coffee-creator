# Test execution report

**Date:** 2026-10-18 (Asia/Jakarta)

**Source baseline:** `6d429df9299d438daefbdac769b5300988d83f9c`, `main`, app `1.14.5` / versionCode 30.
**Scope:** source build, JVM tests, static checks, and debug APK assembly. No application install or user data/device interaction was performed.

## Commands and results

| Command | Result | Evidence / notes |
|---|---|---|
| `rtk proxy .\gradlew.bat projects` | PASS | `:app`, `:benchmark`; build successful. |
| `rtk proxy .\gradlew.bat tasks --all` | PASS | Confirmed actual task names before running gates. |
| `rtk proxy .\gradlew.bat :app:testDebugUnitTest --rerun-tasks` | **PASS** | Fresh run; `BUILD SUCCESSFUL in 3m 46s`, all 46 actionable tasks executed. Parsed JUnit XML: **39 suites, 322 tests, 0 failures, 0 errors, 0 skipped**. Reports: `app/build/test-results/testDebugUnitTest/TEST-*.xml`; HTML: `app/build/reports/tests/testDebugUnitTest/index.html`. |
| `rtk proxy .\gradlew.bat :app:lintDebug` | PASS WITH WARNINGS | `BUILD SUCCESSFUL in 2m 44s`. Lint XML: `app/build/reports/lint-results-debug.xml`; **158 findings**, 105 warnings and 53 informational. Top IDs: 75 `GradleDependency`, 53 `AutoboxingStateCreation`, 11 `DefaultLocale`, 6 `AndroidGradlePluginVersion`, 5 `IconLauncherShape`, 3 `TrustAllX509TrustManager` (in transitive Bouncy Castle JAR), and 5 other findings. Gradle task passes; lint is not clean. |
| `rtk proxy .\gradlew.bat :app:detekt` | **FAIL** | `Analysis failed with 586 weighted issues`. Report: `app/build/reports/detekt/detekt.xml` / `detekt.txt`. Findings include 454 `MaxLineLength`, 69 `MagicNumber`, 17 `UnusedPrivateProperty`, 12 `WildcardImport`, 11 locale/exception findings, and other rules. |
| `rtk proxy .\gradlew.bat :app:ktlintCheck` | **FAIL** | `ktlintTestSourceSetCheck` fails on S4 test source formatting (comments in argument lists, long whitespace/lines, argument layout). Report: `app/build/reports/ktlint/ktlintTestSourceSetCheck/ktlintTestSourceSetCheck.txt`. |
| `rtk proxy .\gradlew.bat :app:ktlintMainSourceSetCheck` | **FAIL** | Production Kotlin has extensive import/unused/wildcard/line-length/brace/spacing violations, including `CoffeeApp.kt` and `SensorySprint04Screens.kt`. Report: `app/build/reports/ktlint/ktlintMainSourceSetCheck/ktlintMainSourceSetCheck.txt`. |
| `rtk proxy .\gradlew.bat :app:assembleDebug` | **PASS** | `BUILD SUCCESSFUL in 22s`. Debug APK: `app/build/outputs/apk/debug/app-debug.apk`, size 124,343,226 bytes, SHA-256 `f80291aa17948520b7acf27634c40307cfbbbd39c0d233c1b9c53869626d8249`. Not installed or published. |
| `rtk proxy .\gradlew.bat :app:dependencyInsight --configuration debugRuntimeClasspath --dependency bcpkix-jdk15to18` | PASS | `bcpkix-jdk15to18:1.72` is runtime-transitive through `com.tom-roush:pdfbox-android:2.0.27.0`. Lint flagged trust-manager classes inside that JAR. This identifies a dependency review item, not proof the app invokes an insecure trust manager. |
| `rtk proxy adb devices -l` | PASS, metadata only | One device attached: Samsung `SM-G988B`, serial `RRCN3008VYE`, Android 13/API 33. |
| `rtk proxy emulator -list-avds` | PASS, no AVD configured | No disposable emulator available. |
| `:app:connectedDebugAndroidTest` | **BLOCKED / NOT_RUN** | Not run. `ManualJourneyTest` uses the production app repository; no disposable seeded profile/device is available. Running instrumentation on the installed user app could mutate user records. |

The first combined command `rtk proxy .\gradlew.bat :app:testDebugUnitTest :app:lintDebug :app:detekt :app:ktlintCheck :app:assembleDebug` stopped at Detekt failure before all later gates completed. Unit tests, Lint, KtLint, and APK assembly were then run independently as above. Build-only success does not override failed style gates or device blockers.

## Test-case execution coverage

The master contains **297 BDD-oriented test designs**: 209 blueprint requirement cases, ten historical shipped-feature preservation cases, two user-requested release-delivery cases, 12 cross-cutting, ten E2E, 24 Sprint 03 TC cases, and 30 Sprint 04 QA cases. All 297 are `NOT_RUN` because these acceptance flows did not execute. Each of the 221 requirement/legacy/delivery cases contains 11 dimension prompts, but the prompts do not always name concrete row-specific fixtures, observable oracles, or a justified N/A. Treat them as a test-design backlog requiring that detail before execution, not as completed coverage.

The RTM finds an explicit source-text marker candidate for 106/209 requirement IDs. This search does not verify that the matched assertion actually tests that requirement (for example, `FR-01` also appears in a blend-ratio test name), so marker matches are not counted as requirement passes. The 103 remaining requirements have no explicit ID marker; untagged tests may exist. Instrumented device cases were not run. As a result:

- Blueprint requirement-level BDD cases executed: **0/209 (0%)**.
- Blueprint, legacy, and delivery requirement cases executed: **0/221 (0%)**.
- Full master acceptance cases executed: **0/297 (0%)**.
- Existing JVM unit suite: **322/322 passed (100% of executed suite)**.
- Explicit story-ID marker candidates: **106/209 (50.7%)**; assertion relevance is not verified, so this is not requirement traceability, code coverage, or acceptance coverage.
- Automation coverage across applicable acceptance dimensions: **not calculable**; no repository coverage report or full automated acceptance mapping was produced.

## Device, offline, and test-data safety

Only ADB device/package metadata were read before the test run. The Samsung device remained on its installed v1.14.5 app. No test APK was installed, no app was launched, and no Room records, SAF grants/files, local model assets, or app-private data were accessed. No screenshots were captured. All JVM tests used their configured host fixtures; connected migration, SAF, native inference, photo capture, TalkBack, BLE, and physical sensory tests remain NOT_RUN or BLOCKED.

## Final execution result

The app compiles, the debug artifact assembles, and all 322 fresh JVM tests pass. Android Lint completes with warnings; Detekt and KtLint fail. Device/UI/SAF/AI/BLE and full requirement-level BDD acceptance are not proven. Release disposition is **NOT_READY**; see [RELEASE_READINESS_SPRINT_04.md](RELEASE_READINESS_SPRINT_04.md).

## Remediation follow-up — 2026-10-18

The audit baseline above is preserved as originally recorded. On remediation source baseline `4d850496effe61669adbc32c71c2b19039fb07bd`, the fresh JVM suite passed **366/366** tests (41 suites). Android Lint completed with **120 findings** (94 warnings, 26 informational); Detekt still fails with **534 weighted issues**; `ktlintTestSourceSetCheck` passes after an import-order correction, while `ktlintMainSourceSetCheck` still fails. Debug assembly passed: 106,630,827-byte local APK, SHA-256 `2A6D1D713F72A00A8A61DE89FA8A239EE3F483F436086721F21590923E1866D0`. ADB/instrumentation were not run per the user's instruction. Requirement-level execution remains **0/221** and master BDD/E2E execution remains **0/297**. This does not supersede the **NOT_READY** disposition. Full details: [REMEDIATION_2026-10-18.md](REMEDIATION_2026-10-18.md).

## Scoped CVA storage follow-up — 2026-10-18

Branch `codex/s4-cva-persistence` (base `e861739573fb98bc4f01b658e9830dc5a44a9c01`) adds Room-backed typed CVA sessions, additive backup v4 compatibility, assessment/preparation bounds, strict amendment history, and current-revision JSON/CSV export validation. Full JVM tests: **42 suites / 383 tests / 0 failures / 0 errors / 0 skipped**. `lintDebug`: task passes with 120 findings (94 warnings, 26 informational). Detekt: **FAIL**, 534 weighted findings. Test-source KtLint: **PASS**. Main-source KtLint: **FAIL**, 2,115-line repository report; the modified validation and export guard lines no longer appear in the report. `assembleDebug`: **PASS**, local APK 106,778,097 bytes, SHA-256 `14a32a0f8244aafe96d0f986efb46b6217fea19c0085c7ce86a3f4b3b6bc6203`. ADB and instrumentation remain NOT_RUN per user instruction. The UI session workflow is not wired; requirement-level acceptance remains **0/221**, master BDD/E2E **0/297**, and release verdict **NOT_READY**. D-016 through D-020 are fixed on this branch. Full commands and evidence: [CVA_DURABLE_STORAGE_SLICE_2026-10-18.md](CVA_DURABLE_STORAGE_SLICE_2026-10-18.md).
