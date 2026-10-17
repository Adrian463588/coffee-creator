# Coffee Creator S01–S04 Independent QA Audit Plan

**Audit mode:** read-only discovery and evidence collection; no production-code, installed-app, database, SAF, or release mutation.

**Baseline:** `6d429df9299d438daefbdac769b5300988d83f9c` (`main`, equal to `origin/main`), app `1.14.5` / code `30`.
**Source of truth:** `coffee-creator-blueprint/` Markdown requirements, in the order required by the audit request. `TASKS.md`, README files, code comments, release pages, and agent assertions are claims to verify.

## Objective and invariants

Audit every specified FR-01–FR-10, S2, GR, BR, S4, and EDU requirement, plus shipped legacy behavior and the user's repository/release delivery requirements, against current code, tests, schemas, history, and actual execution evidence. Keep implementation status separate from test status. Preserve all production features, records, device state, source documents, and release history. No repair or refactor occurs during this audit; defects receive reproducible cases and a minimal repair plan for a separately approved, isolated worktree.

The Samsung SM-G988B (`RRCN3008VYE`, API 33) is visible over ADB, but no disposable seeded profile has yet been established. It will remain untouched until test bootstrap and data isolation are proven. Any device-only case without a safe disposable environment is `BLOCKED` or `NOT_RUN`, never `PASS`.

## Method

Use a specification-first loop: freeze baseline → inventory → trace requirements → design BDD cases → verify test isolation → execute safe existing tests → triage defects → independently review reports. OpenSpec's proposal/spec/design/tasks/verify structure and BMAD's plan/build/review separation inform the audit artifacts; neither process state nor checked boxes counts as correctness evidence. RTK, Caveman, and Ponytail are authoring guidance only and are not runtime dependencies.

Method references checked on 2026-10-18:

- [OpenSpec workflows](https://github.com/Fission-AI/OpenSpec/blob/main/docs/workflows.md)
- [OpenSpec spec-driven schema](https://github.com/Fission-AI/OpenSpec/blob/main/docs-lab/reference/schemas/spec-driven/index.md)
- [BMAD Build a Change](https://github.com/bmad-code-org/BMAD-METHOD/blob/main/docs/build/build-a-change.md)
- [Agent Skills format](https://github.com/agentskills/agentskills/blob/main/docs/specification.mdx)

## Phases and gates

1. **Freeze baseline:** commit/branch/status, toolchain and variants, SDK/device inventory, Room and backup schema versions, release history, CI receipts, and source-tree checksum. Do not access or export the installed app's user database or SAF documents.
2. **Inventory:** enumerate routes/deep links, Compose screens/actions, ViewModels/use cases, repositories/DAOs/entities/migrations, catalogs/seeds, exports/backups, workers, permissions, network surfaces, model/RAG/tool adapters, and tests.
3. **Preservation review:** compare source and tests across available sprint tags/commits; identify removals, renames, altered IDs/fields/seeds, navigation changes, and unsupported historical claims.
4. **Traceability:** map each exact source requirement, every observed legacy feature, and user-requested release-delivery conditions to implementation symbols, storage, navigation, relevant test IDs/layers, device dependencies, evidence, defect, and separate implementation/test verdicts. Record numbering gaps and source conflicts explicitly.
5. **BDD test design:** create requirement-linked happy, negative/boundary, invalid, state/recovery, offline/no-model, persistence/relaunch, navigation, migration, accessibility, security, and performance cases where applicable. State why any dimension is not applicable.
6. **Safe execution:** inspect fixtures and test runners before execution. Run only tests proven isolated from the installed app's data. Save command, exit code, XML/log/screenshots, environment, SHA, and result. Connected tests remain blocked if they can clear, replace, or mutate a non-disposable package/data profile.
7. **Independent triage:** classify P0–P3; state exact reproduction and smallest additive fix; identify required focused/full regression suites. Do not implement fixes in this audit.
8. **Review and delivery:** reconcile agent findings against source and tool output; compute explicit requirement/test denominators; publish a verdict and blockers; commit/push only the audit evidence files after verifying no production-source changes.

## Deliverables

1. `FEATURE_INVENTORY_S01_S04.md`
2. `REQUIREMENTS_TRACEABILITY_MATRIX.csv`
3. `MASTER_TEST_CASES_S01_S04.md`
4. `TEST_EXECUTION_REPORT.md`
5. `FEATURE_PRESERVATION_DIFF.md`
6. `DATABASE_MIGRATION_AUDIT.md`
7. `UIUX_ACCESSIBILITY_AUDIT.md`
8. `SCIENTIFIC_AND_STANDARDS_AUDIT.md`
9. `DEFECT_REGISTER.csv`
10. `RELEASE_READINESS_SPRINT_04.md`

Every deliverable must distinguish observed facts, source claims, inferences, and unexecuted checks. Exact denominators are derived from the documents, including numbering gaps; no percentage may hide blocked or unrun cases.

## Rollback / stop conditions

This phase writes only the requested QA reports. Stop device execution if test bootstrap touches installed user data, requests an unsafe permission, modifies a user-owned SAF tree, or requires uninstall/clear-data. Stop a standards-conformance verdict where licensed/current field mappings are not verifiable. Report environment blockers without replacing physical evidence with host-only results.
