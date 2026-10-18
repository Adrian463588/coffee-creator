# QA remediation and interaction review — 2026-10-18

**Worktree:** `codex/coffee-remediation`
**Pre-change source:** `4d850496effe61669adbc32c71c2b19039fb07bd`
**Application version:** `1.14.5` / versionCode `30`
**Release disposition:** **NOT_READY**

## Scope and method

This is a dated follow-up to the read-only S01–S04 audit in [RELEASE_READINESS_SPRINT_04.md](RELEASE_READINESS_SPRINT_04.md). It preserves that baseline and its manifests. The remediation used the existing OpenSpec change `openspec/changes/qa-remediation`: reproduce domain defects with BDD-style regression tests, apply additive fixes, rerun affected and aggregate checks, and obtain independent code review. No database schema, migration, permission, route, or release-signing changes were made. The OpenSpec tasks R13–R15 record only the work completed in this slice; earlier broad acceptance and release tasks remain open.

## Implemented and reviewed

- CVA unknowns and unrecorded legacy values stay unknown; journal mapping accepts only recorded sensory observations and explicitly measured preparation values. It does not turn volume into mass or create a beverage-mass measurement.
- Blind sample identifiers use full UUIDs; unaudited exports conceal sample identity, and legacy missing reveal state defaults to concealed. Locking requires the session evidence and assessor confirmation expected by the domain validation.
- Amendment export validates the previous snapshot's stored checksum before export and calculates a checksum for the redacted export snapshot. Malformed or mismatched prior snapshots are rejected.
- Green QC validates supported measurements, preserves unknown/not-measured states, records optional green-color observations in JSON/CSV, and rejects invalid calibration dates. The UI labels export as a complete QC report and explains the required fields.
- SAF text output is isolated behind an injected URI writer, runs on an I/O dispatcher, and verifies written byte count and SHA-256. JVM tests cover exact UTF-8 payload success and provider failure; Android document-provider behavior remains unverified.
- Brew section selection gives haptic feedback only when the selected section changes. Sensory controls use explicit response state, stable action labels, compact wrapping protocol chips, accessible touch targets, and content-linked transitions. CVA save/compare/lock/export controls stay unavailable while durable session persistence is absent. No decorative continuous animation was introduced.

The scope does not solve Sprint 04 persistence: save, compare, lock, journal-link, and export acceptance remain blocked where a durable repository is required. The independent UI review found no removed route or ViewModel callback; it also confirmed that unavailable controls accurately expose that missing persistence rather than reporting fabricated success. The broader legacy recipe, standards, licensing, and provenance findings remain governed by the original feature-preservation audit.

The visual-only changes are limited to control sizing, selection feedback, compact chip flow, truthful control states, and content-linked transitions. The S4 screen diff is larger than a cosmetic-only diff because it also removes fabricated defaults and success feedback uncovered by D-001/D-002; no route was removed. Runtime appearance is not device-verified.

## Verification evidence

| Gate | Result | Evidence |
|---|---|---|
| ` .\gradlew.bat :app:testDebugUnitTest` | **PASS** — 41 suites, 366 tests, 0 failures/errors/skips | `app/build/test-results/testDebugUnitTest/TEST-*.xml` |
| ` .\gradlew.bat :app:lintDebug` | **PASS WITH FINDINGS** — 120 total: 94 warnings, 26 informational | `app/build/reports/lint-results-debug.xml` and `.html` |
| ` .\gradlew.bat :app:detekt` | **FAIL** — analysis reports 534 weighted issues | `app/build/reports/quality/quality-gates-final-2026-10-18.log`, `app/build/reports/detekt/` |
| ` .\gradlew.bat :app:ktlintTestSourceSetCheck` | **PASS** after correcting import ordering in the new UI test | `app/build/reports/ktlint/ktlintTestSourceSetCheck/` |
| ` .\gradlew.bat :app:ktlintMainSourceSetCheck` | **FAIL** — existing main-source violations remain; report is 2,115 lines, including 1,063 references to `SensorySprint04Screens.kt` | `app/build/reports/ktlint/ktlintMainSourceSetCheck/ktlintMainSourceSetCheck.txt` |
| ` .\gradlew.bat :app:assembleDebug` | **PASS** — local debug APK, 106,630,827 bytes, SHA-256 `2A6D1D713F72A00A8A61DE89FA8A239EE3F483F436086721F21590923E1866D0` | `app/build/outputs/apk/debug/app-debug.apk` |
| `git diff --check` | **PASS** | remediation worktree |
| Connected Android instrumentation / ADB | **NOT_RUN by user instruction** | No device interaction performed |
| Room migration and SAF-provider acceptance | **NOT_RUN** | No migration or actual document provider was exercised |

The aggregate command ` .\gradlew.bat --continue :app:testDebugUnitTest :app:lintDebug :app:detekt :app:ktlintCheck :app:assembleDebug` exited **1** because Detekt and main/test-source KtLint initially failed. The test-source import-order finding was fixed and ` .\gradlew.bat :app:ktlintTestSourceSetCheck` passed afterward; Detekt and main-source KtLint remain failing. The final test task reports 366 passing tests (up-to-date on its last rerun). The APK is a local debug-signed artifact, not a release-signed build; it was not installed, uploaded, or published.

These unit tests are not requirement acceptance. The audit's explicit denominators remain **0/221 requirement/legacy/delivery acceptance cases** and **0/297 master BDD/E2E cases executed**. Specification inventory completeness remains 209/209 numbered blueprint IDs. Automation coverage across all applicable requirements is not calculable from current evidence. See the original RTM and master cases for row-level statuses.

## Blockers and residual risk

- **P0, D-003:** Sprint 04 session drafts, amendments, and journal links lack durable repository persistence and migration/restore proof.
- **P0, D-008:** `SCA_CVA_CURRENT` mapping and licensed-source validation remain unverified; official scoring must remain unavailable until verified.
- **P0, D-013:** historical champion-recipe content and stable-ID/provenance reconciliation remain incomplete.
- **P0, D-015:** historical public-release claims still need tag-by-tag factual reconciliation.
- **P1:** historical nested-backup compatibility, full offline/device journeys, BLE/timer recovery, and content provenance remain unverified as listed in the baseline release-readiness report.
- **P2:** amendment `actorId` is supplied by the caller and is not authenticated or role-backed.
- Android lint has unresolved findings; Detekt and main-source KtLint are not clean. Device, accessibility, SAF, migration, and full acceptance suites were not run.

No new GitHub release is eligible from this result. Keep the existing `v1.14.5` as the latest public version until the release gates are resolved. Push of this remediation branch does not imply product release readiness.

## Research used for interaction decisions

The interaction pass applied Compose's documented accessibility semantics and minimum touch-target defaults, and used transitions only to reinforce state changes. It also followed WCAG guidance to avoid unnecessary interaction-triggered animation. General SDD workflow references: [OpenSpec spec-driven schema](https://github.com/Fission-AI/OpenSpec/blob/main/docs-lab/reference/schemas/spec-driven/index.md), [OpenSpec concepts](https://github.com/Fission-AI/OpenSpec/blob/main/docs/concepts.md), and [BMAD-METHOD](https://github.com/bmad-code-org/BMAD-METHOD). UI references: [Compose accessibility API defaults](https://developer.android.com/develop/ui/compose/accessibility/api-defaults), [Compose animation quick guide](https://developer.android.com/develop/ui/compose/animation/quick-guide), and [WCAG 2.2 animation from interactions](https://www.w3.org/WAI/WCAG22/Understanding/animation-from-interactions).
