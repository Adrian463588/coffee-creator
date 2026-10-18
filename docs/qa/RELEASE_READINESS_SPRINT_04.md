# Sprint 04 release readiness and cross-sprint scorecard

> **Remediation follow-up (2026-10-18):** The original audit below is preserved as the baseline. A narrow QA/UI remediation passed 366/366 JVM tests and built a local debug APK, but Detekt (534 weighted issues), main-source KtLint, durable S4 persistence, device acceptance, and other P0/P1 gates remain unresolved. Requirement acceptance remains 0/221 and master test execution 0/297. Verdict remains **NOT_READY**. See [REMEDIATION_2026-10-18.md](REMEDIATION_2026-10-18.md) and [TEST_EXECUTION_REPORT.md](TEST_EXECUTION_REPORT.md#remediation-follow-up--2026-10-18).

**Audit baseline:** branch `main`, HEAD `6d429df9299d438daefbdac769b5300988d83f9c`, tree `b714b993b253b7d1cad388f906403a4085b50539`; current app `1.14.5` / versionCode 30; latest tag `v1.14.5` (`c483c91`). Audit docs are the only intended working-tree changes. No app source or user data was changed.

## Required summary table

“Implemented verified” means full acceptance has evidence at the required layer, not that a class or test source exists. The table's test column counts 221 planned `TC-REQ-*` cases: 209 blueprint IDs, ten historical shipped-feature checks, and two user-requested release-delivery checks. None ran. The separate fresh JVM suite result is stated below.

| Sprint / epic | Requirements | Implemented verified | Implemented unverified | Partial | Missing | Broken | Blocked | Spec conflict | Tests pass / fail / not-run | P0 defects | Evidence |
|---|---:|---:|---:|---:|---:|---:|---:|---:|---|---:|---|
| Sprint 01 / PRD | 10 | 0 | 8 | 2 | 0 | 0 | 0 | 0 | 0 / 0 / 10 | 0 | RTM FR-01–10; unit suite aggregate passed; D-004/D-009 |
| Sprint 02 / S2-001–082 | 82 | 0 | 75 | 7 | 0 | 0 | 0 | 0 | 0 / 0 / 82 | 0 | RTM; catalog/recipe/brew/journal/sensory/RAG suites in aggregate; D-005/D-010 |
| Sprint 03 / GR-01–12 | 12 | 0 | 12 | 0 | 0 | 0 | 0 | 0 | 0 / 0 / 12 | 0 | RTM; grinder/dripper unit suite passed in aggregate; row-level cases not run |
| Sprint 03 / BR-01–18 | 18 | 0 | 16 | 1 | 0 | 0 | 1 | 0 | 0 / 0 / 18 | 0 | RTM; TC-301–324; D-006/D-007 |
| Sprint 04 / S4-001–065 | 65 | 0 | 29 | 22 | 0 | 9 | 5 | 0 | 0 / 0 / 65 | 3 | RTM; CVA/green/training unit suites passed in aggregate; D-001–003, D-008, D-011 |
| Sprint 04 / EDU-01–22 | 22 | 0 | 22 | 0 | 0 | 0 | 0 | 0 | 0 / 0 / 22 | 0 | RTM; curriculum structure test passed in aggregate; D-012 |
| Historical shipped features | 10 | 0 | 9 | 0 | 0 | 0 | 0 | 1 | 0 / 0 / 10 | 1 | RTM LEGACY-01–10; nine route candidates present but behavior parity NOT_RUN; catalog identity conflict D-013 |
| Release / delivery | 2 | 0 | 0 | 1 | 0 | 1 | 0 | 0 | 0 / 0 / 2 | 1 | RTM DELIVERY-01/02; APK metadata mismatch D-014; release-claim contradiction D-015 |
| **Total** | **221** | **0** | **171** | **33** | **0** | **10** | **6** | **1** | **0 / 0 / 221** | **5** | RTM, master cases, execution report; all 297 master cases NOT_RUN |

The blueprint denominator is 209 IDs; the RTM adds ten historical shipped-feature rows and two delivery rows, for 221 total. The 171 `IMPLEMENTED_UNVERIFIED` rows and one `SPEC_CONFLICT` are not counted as complete. “Missing” is zero because a current source candidate or identifiable historical feature exists; this does not mean required behavior passes or that historical data parity is established. S4 `BROKEN` and `BLOCKED` rows contain specific reachable actions or an explicit missing standards gate.

## Coverage and evidence denominators

- **Specification completeness:** 209/209 numbered blueprint IDs found (100% source inventory); source ranges have no numbering gaps. No numbered ID in the audited specifications was excluded. Ten additional historical features are separately listed in the RTM.
- **Implementation acceptance:** 0/221 fully verified (0%): 209 blueprint IDs, ten legacy checks, and two delivery checks. Matrix verdict counts: 171 `IMPLEMENTED_UNVERIFIED`, 33 `PARTIAL`, 10 `BROKEN`, 6 `BLOCKED`, and one `SPEC_CONFLICT`.
- **Master acceptance execution:** 0/297 BDD/E2E/spec test cases executed (0%): 221 requirement cases + 12 cross-cutting + 10 end-to-end + 24 Sprint 03 TC + 30 Sprint 04 QA cases. The connected/device cases were not run.
- **Requirement test marker candidates:** source text contains an explicit ID marker for 106/209 blueprint rows (50.7%). This heuristic does not verify that the assertion tests the requirement; no requirement is marked passed from a marker. The other 103 blueprint rows, ten legacy rows, and two delivery rows have no explicit marker; untagged tests may exist. All 221 `TC-REQ-*` cases are NOT_RUN.
- **Existing JVM suite:** 322/322 cases passed (100% of cases executed) in the fresh Gradle run. It is not 322/209 requirement acceptance and does not prove device, SAF, AI generation, BLE, accessibility, or sensory outcomes.
- **Automation coverage:** not calculable as a repository-wide percentage. No coverage report was run, and the 297 acceptance cases have not been implemented/executed as a complete automated suite. Do not convert the 106 source marker candidates into an automation-coverage claim.
- **Case-design limit:** the 297 entries are BDD-oriented test designs, not all execution-ready scripts. Some still require row-specific fixture values, observable oracles, and justified N/A decisions from a disposable profile, authorized sources, or physical hardware. Those dimensions receive no execution or automation credit until completed and evidenced.

## Quality gates

| Gate | Exact command | Result |
|---|---|---|
| Module discovery | `rtk proxy .\gradlew.bat projects` | PASS; `:app`, `:benchmark` |
| Task discovery | `rtk proxy .\gradlew.bat tasks --all` | PASS; tasks identified before running checks |
| Unit tests | `rtk proxy .\gradlew.bat :app:testDebugUnitTest --rerun-tasks` | **PASS**; fresh execution, 39 suites / 322 tests / 0 failures / 0 errors / 0 skipped |
| Android Lint | `rtk proxy .\gradlew.bat :app:lintDebug` | Gradle task **PASS**, but 158 findings remain: 105 warnings and 53 informational. Includes 75 GradleDependency, 53 AutoboxingStateCreation, 11 DefaultLocale, 6 AGP version, 5 icon shape, 3 transitive TrustAllX509TrustManager, and other warnings. This is not a clean lint report. |
| Detekt | `rtk proxy .\gradlew.bat :app:detekt` | **FAIL**; 586 weighted findings: 454 MaxLineLength, 69 MagicNumber, 17 UnusedPrivateProperty, 12 WildcardImport, 11 locale/exception findings, and remaining rules. |
| KtLint all sources | `rtk proxy .\gradlew.bat :app:ktlintCheck` | **FAIL**; test-source formatting violations, including S4 test comments in argument lists and overlong triangle test lines. |
| KtLint production source | `rtk proxy .\gradlew.bat :app:ktlintMainSourceSetCheck` | **FAIL**; 2,115 report lines, with import/unused/wildcard/line-length/spacing/brace violations across large Compose files. |
| Debug build | `rtk proxy .\gradlew.bat :app:assembleDebug` | **PASS**, built debug APK; see `TEST_EXECUTION_REPORT.md`. The APK was not installed or published. |
| Device tests | `:app:connectedDebugAndroidTest` | **BLOCKED / NOT_RUN**; connected Samsung has installed user app, no disposable profile. Instrumentation includes `ManualJourneyTest` using the production graph, so running against it could mutate user records. No AVD is configured. |
| Physical UX/AI/SAF/BLE | Manual and device-only suites | **NOT_RUN**; no real local model, SAF test tree, scale, disposable profile, or sensory panel fixture was supplied. |

The first combined Gradle command stopped after Detekt failed, so tests were forced fresh and lint/build were rerun separately. `BUILD SUCCESSFUL` is not used to erase linter findings.

## Migration and preservation results

Room is schema v2 with one 1→2 migration; backup schema 3 accepts 2–3. The unit suite including repository, seed, backup, and recovery tests passed. No historical schema-1 artifact exists, no user-data snapshot was captured, and no physical SAF backup/regrant/restore was run. D-004 remains a focused compatibility risk: older nested chat/recipe records are not covered by the current schema-v2 fixture. No destructive migration call was found. File-level history showed no deleted app source/test paths from v1.13.1 through v1.14.5, but this is not record-level preservation proof.

## Device matrix

| Device / environment | App state | Action | Verdict |
|---|---|---|---|
| Samsung SM-G988B, Android 13/API 33, ADB serial `RRCN3008VYE` | Installed package `1.14.5` / versionCode 30 | Read-only metadata only; did not install, launch, query databases, inspect SAF files, clear data, or run tests. | Connected but acceptance NOT_RUN; no disposable profile. |
| Android emulator | No configured AVD (`emulator -list-avds` returned none). | No emulator run. | BLOCKED / NOT_RUN. |
| Local Gradle host | Windows, JDK 21, Android Gradle Plugin 8.6.0 | JVM tests, Lint, Detekt, KtLint, and debug assembly as listed. | Test/build evidence only; does not substitute for device acceptance. |

## Release gate

**Verdict: NOT_READY.** Five P0 findings remain: false measured/provenance values in Green QC/panel data (D-001), Sprint 04 sessions and journal action are not durably persisted (D-003), current-standard CVA labeling lacks the required verified field/license map (D-008), the historical curated recipe corpus lacks stable-ID/content reconciliation (D-013), and public v1.14.3/v1.14.4 release claims conflict with the source at those tags (D-015). There are also P1 false-success exports/lock (D-002), a consent default (D-011), data compatibility risk (D-004), missing BLE/timer/video/OCR/RAG/device acceptance (D-005–D-010), missing content provenance register (D-012), and v1.14.5 download metadata drift (D-014). No production release should be created from this audit baseline.

The public release history needs a separate claims review. The v1.14.3 notes claim instrument-grounded Green QC measurements and SHA-sealed exports, while tagged source initializes measurement values and creates a UUID-derived pseudo-hash. The v1.14.4 notes claim durable Sprint 04 records, verified restore, and one-click journal logging, while tagged source still uses local Compose state and has no journal persistence callback. These are recorded as D-015; no release note or code was changed during this audit.

The latest existing public release remains [v1.14.5](https://github.com/Adrian463588/coffee-creator/releases/tag/v1.14.5), asset [coffee-creator-v1.14.5.apk](https://github.com/Adrian463588/coffee-creator/releases/download/v1.14.5/coffee-creator-v1.14.5.apk), 123,036,057 bytes per the GitHub release asset API, SHA-256 `447ccdde1cafc1799bb6a11af4e4c32a1344a4e11708beea64ec1fc2621a0f09`. The committed `docs/releases/v1.14.5.md` currently reports 123,039,143 bytes and points its curl command to `murchid21/CoffeCreator`; this metadata discrepancy is D-014. Existing README has historical version links, project description, and topics. This QA audit does not create a new release or replace the current asset.

## Minimal repair order

1. Reconcile every v1.9.5 curated-recipe ID and field with current catalogs and backups; preserve verified history and represent unsupported measurements as unknown. Do not restore synthetic test data.
2. Remove false measurements/default scores, make all S4 observation fields blank/unknown until entered, and default export consent to false.
3. Add durable S4 session/draft models and repository/ViewModel actions; connect “Log to Brew Journal” to an immutable linked record.
4. Replace placeholder export/lock success with atomic SAF file output and a digest over exact canonical export bytes; add export/import roundtrip tests.
5. Verify and register current CVA field mapping/license; gate `SCA_CVA_CURRENT` until accepted, with clearly labeled `CUSTOM_RESEARCH` fallback.
6. Add migration/backward-compatible nested JSON fixtures and prove no data loss in copied historical databases.
7. Resolve Detekt/KtLint without suppressions; clear or disposition Android Lint warnings, especially locale and dependency security findings.
8. Run full connected test matrix on disposable emulator/device and complete actual SAF, model, accessibility, offline, and required hardware acceptance.
9. Correct the v1.14.5 release note's repository owner and asset size from verified GitHub metadata, recheck URL/digest, then qualify or correct v1.14.3/v1.14.4 historical behavior claims against tagged source and evidence (D-014/D-015).

Fixes must be separate, narrow worktree slices with failing regression tests first, then focused and full regression runs. Preserve all legacy workflows and data through each slice.
