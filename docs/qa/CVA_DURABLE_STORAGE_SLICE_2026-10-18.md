# CVA durable-storage slice verification — 2026-10-18

**Worktree / branch:** `D:\SEMESTER13\KodeProject\CoffeCreator-worktrees\s4-cva-persistence`, `codex/s4-cva-persistence`
**Base SHA:** `e861739573fb98bc4f01b658e9830dc5a44a9c01`
**App baseline:** `1.14.5` / versionCode `30`
**Disposition:** slice tests/build pass with existing static-check failures; overall product **NOT_READY**.

## Scope and SDD/BDD plan

OpenSpec change: [`openspec/changes/s4-cva-durable-persistence`](../../openspec/changes/s4-cva-durable-persistence/). The slice keeps CVA aggregates in the existing Room `records` table, adds v4 backup support while preserving v2/v3 behavior, and rejects invalid or tampered CVA evidence atomically. It makes no schema migration and does not connect the unsafe Compose form to persistence.

Execution followed test-first scenarios: save/reopen preserves stable IDs and unrelated records; invalid assessment and typed preparation bounds are rejected; legacy backup restore preserves newer CVA rows; amendment history is limited to AMENDED revisions; invalid prior snapshots and invalid current revisions are rejected at JSON/CSV export even when canonical checksums are recomputed.

## Changed behavior

- Added a typed CVA repository contract and Room-backed create/update/find/list operations. Existing S2 records remain in their current storage families.
- Added CVA sessions to additive backup schema v4. Decoding v2/v3 retains the original wire hash/count validation; restore of v2/v3 does not delete CVA rows they cannot represent.
- Shared pure domain checks cover assessment intensity, affective score, quaker count, cup index/intensity, and legacy score/count bounds. Repository, backup decode, amendment snapshot validation, and current-session JSON/CSV export use these checks.
- Shared preparation validation checks finite positive water TDS, brew ratio, dose, and volume; 85–99 °C temperature; positive cup count; non-negative roast-rest days/date; 0–100 Agtron; and positive optional grinder/bowl counts. No protocol-specific upper tolerance is invented without an authorized source.
- Amendment history is rejected outside AMENDED sessions and validates the previous locked snapshot’s canonical JSON, source checksum, assessment/preparation fields, and lock evidence.
- No Room schema version change, destructive migration, app uninstall, data clear, device install, SAF grant mutation, app network requests, or release signing change occurred.

## Regression evidence

Before the fixes, `givenInvalidCvaFields_whenSaved_thenRejectedAtomically`, `givenRehashedInvalidAmendmentSnapshot_whenExported_thenDomainValidationRejectsIt`, and `givenInvalidCurrentPreparation_whenExported_thenRejected` failed at expected rejection assertions. Follow-up BDD regressions also reproduced accepted non-AMENDED history and invalid preparation values through repository create/update, rehashed v4 backup decode, and current/historical amendment export. All focused scenarios pass after shared validation was extended; the full 383-test suite also passes.

## Executed checks

| Command | Result | Evidence |
|---|---|---|
| `.\gradlew.bat :app:testDebugUnitTest` | **PASS** — 42 suites, 383 tests, 0 failures, 0 errors, 0 skipped | [`cva-unit-tests.log`](evidence/cva-unit-tests.log), archived [JUnit XML and manifest](evidence/cva-unit-test-results/manifest.txt) |
| `.\gradlew.bat :app:testDebugUnitTest --tests com.coffeecreator.app.core.data.BackupCodecTest --tests com.coffeecreator.app.core.data.CvaRepoTest --tests com.coffeecreator.app.core.sensory.cva.CvaDomainEngineTest` | **PASS** | Gradle result observed; suite output in `app/build/reports/tests/testDebugUnitTest/` |
| Focused repository, rehashed-backup, amendment-snapshot, and current-revision JSON/CSV export regressions | **PASS** | [`cva-preparation-regressions.log`](evidence/cva-preparation-regressions.log), [`cva-export-regressions.log`](evidence/cva-export-regressions.log); archived [JUnit XML and manifest](evidence/cva-unit-test-results/manifest.txt) |
| `.\gradlew.bat :app:lintDebug` | **PASS WITH FINDINGS** — 120 findings: 94 warnings, 26 informational | [`cva-lint-debug.log`](evidence/cva-lint-debug.log), `app/build/reports/lint-results-debug.xml` |
| `.\gradlew.bat :app:detekt` | **FAIL** — 534 weighted findings across the existing codebase; no findings in `CvaSessionStorageValidator.kt` or the changed `CvaServices.kt` validation block | [`cva-detekt.log`](evidence/cva-detekt.log), `app/build/reports/detekt/detekt.txt` |
| `.\gradlew.bat :app:ktlintTestSourceSetCheck` | **PASS** | [`cva-ktlint-tests.log`](evidence/cva-ktlint-tests.log) |
| `.\gradlew.bat :app:ktlintMainSourceSetCheck` | **FAIL** — 2,115-line repository report; existing violations remain. After formatting the added guard functions, no current KtLint diagnostics map to the modified validation or export guard lines; existing violations in unchanged service code remain | [`cva-ktlint-main.log`](evidence/cva-ktlint-main.log), `app/build/reports/ktlint/ktlintMainSourceSetCheck/ktlintMainSourceSetCheck.txt` |
| ` .\gradlew.bat :app:assembleDebug` | **PASS** — 106,778,097 bytes, SHA-256 `14a32a0f8244aafe96d0f986efb46b6217fea19c0085c7ce86a3f4b3b6bc6203` | [`cva-assemble-debug.log`](evidence/cva-assemble-debug.log), [`cva-apk-integrity.log`](evidence/cva-apk-integrity.log), `app/build/outputs/apk/debug/app-debug.apk` |
| `.\gradlew.bat :app:connectedDebugAndroidTest`, ADB install/smoke | **NOT_RUN by user instruction** | No device operations were issued |
| `git diff --check` | **PASS** after final documentation update | This branch |

Android Lint’s Gradle task completion does not mean a clean lint report. Detekt and main-source KtLint prevent a clean static quality gate. The test task uses host/Robolectric fixtures; it does not prove Android lifecycle, SAF provider, accessibility, native AI, BLE, or physical-device behavior.

## Preservation, migrations, and release gate

Repository tests reopen a Room database and compare IDs/values for legacy bean, ingredient, recipe, observation, and S2 cupping settings. Backup tests cover v4 round trip, old v2/v3 imports, invalid CVA decode, and atomic restore behavior. There is no new SQL schema, so this change adds no migration path. Historic schema fixture migration, an actual SAF document provider, and process-death instrumentation remain **NOT_RUN**.

The CVA Compose form still has no ViewModel persistence callbacks. Its `NaN`, `-1`, nullable legacy fields, raw text, and shared per-screen controls cannot be losslessly mapped to the typed session model yet. Save, compare, lock, journal-link, and export therefore remain unavailable in the UI; D-003 stays a P0 release blocker. Full acceptance remains 0/221 requirement-level acceptance cases and 0/297 master BDD/E2E cases executed. This slice adds passing unit regressions but does not claim complete blueprint coverage.

No new GitHub release is eligible. Keep the existing public release as-is; this branch push is not a product release. Overall verdict remains **NOT_READY** until the UI mapping/persistence journey, outstanding P0/P1 defects, full lint cleanup, and required device/SAF/migration gates are resolved.
