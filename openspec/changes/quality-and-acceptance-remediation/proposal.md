# Proposal: quality and acceptance remediation

## Problem

The current source baseline still fails Detekt with 534 weighted findings (438 `MaxLineLength`, 61 `MagicNumber`, 12 `WildcardImport`, and 23 findings across other rules). Main-source KtLint also fails. The repository keeps `maxIssues: 0`; the gates must be fixed without changing thresholds, excluding production code, adding a baseline, or hiding findings.

The QA matrix contains 221 requirement rows and 297 master acceptance cases. None of those requirement-level cases has completed acceptance evidence. The 383 passing JVM tests are a separate suite and do not establish cross-sprint journeys. ADB and connected instrumentation remain `NOT_RUN` by user instruction.

## Goal

Reduce static-analysis findings through reviewed, behavior-preserving edits; preserve exact coffee/scientific data and all shipped features; and improve requirement-to-test evidence only where a real test can be executed and its assertion can be shown to match the requirement.

## Non-goals

- Do not weaken Detekt/KtLint rules, add blanket suppressions, or mark a mapped test as acceptance evidence without inspecting and executing it.
- Do not change product behavior, menus, routes, seeds, protocol data, storage, migrations, or export formats as a side effect of formatting.
- Do not run ADB/instrumentation, install an APK, access user storage, or publish a release.
- Do not mark all 221 requirements or 297 master cases complete based on aggregate JVM results.

## Acceptance criteria

1. The unchanged Detekt configuration reports zero weighted findings.
2. Main-source KtLint and test-source KtLint pass without reducing their configured checks.
3. All JVM tests pass after each semantic refactor; test totals and XML reports are recorded.
4. Every changed long literal/data entry preserves exact value; seed/catalog checks and targeted snapshots pass.
5. Requirement and master-test statuses change only when exact test ID, assertion, command, and fresh execution evidence are recorded. Device-only cases stay `NOT_RUN`.
6. No route, feature, test, seeded record, or historical data is removed or downgraded.
7. Release remains `NOT_READY` while P0/P1, offline/security, migration, or device-only gates remain open.
