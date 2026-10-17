# Database migration and recovery audit

**Baseline:** `6d429df9299d438daefbdac769b5300988d83f9c`, app `1.14.5` / code 30. Database/user-data baseline was not read: no disposable seeded profile was available, and the installed app database and SAF folders were left untouched.

## Schema and migration inventory

| Item | Source state | Audit result |
|---|---|---|
| Room database | `CoffeeDatabase.kt:78`; version 2; entities `StoredRecord`, `StoredChunk`, `ChunkFts` | Current schema is a generic record/document store. No per-feature Room entity is defined for Sprint 04 session forms. |
| Migration | `CoffeeDatabase.kt:84–115`; registered `MIGRATION_1_2` only | Migration 1→2 moves legacy `kind='chunk'` records into document chunk/FTS structures. No `fallbackToDestructiveMigration` call was found. |
| Exported schemas | `app/schemas/2.json` is present; no schema-1 JSON found | Version 1 shape is represented only by the migration test fixture, not by a verified historical release artifact. |
| Seed data | `SeedData.kt`; seed version 3 | Upgrade/merge and user override tests exist. Current test suite re-run results are in `TEST_EXECUTION_REPORT.md`; test data are synthetic and do not represent the installed profile. |
| Backup format | `BackupCodec.kt:41`; current schema 3, accepts 2–3 | Version 2 adds missing top-level `estimates`; nested record shape is validated as current. Export is app-private JSON content selected by the user through SAF. |
| Repository | `DataRepository.kt:377`; snapshots decode generic record JSON | Current snapshots validate chat messages and records after Gson decoding. `ChatMessage.sourceChunkIds` was added with a Kotlin default, but old serialized rows omit that field. |
| Restore | `DataRepository.kt` / `BackupCodec.kt` | Validation occurs before transactional replacement; restore preserves destination ownership settings and local model rows. These properties have test source, not physical SAF/reinstall proof. |

## Data-preservation invariants

The audit did not read, count, export, restore, migrate, or delete any real user data. No test was run against the installed app package. Required assertions for a disposable fixture are:

- Every historic record keeps its stable `(kind,id)`, exact JSON payload, revisions, timestamps, ownership and source metadata.
- Existing recipe templates, grinder/dripper profiles, journal entries, flavor descriptor IDs, cupping data, document metadata/chunks, model manifests, settings and links retain counts and foreign-key integrity.
- Seed updates remain idempotent, preserve user overrides and deletions, and do not replace user-created IDs.
- Unsupported/corrupt imports fail before any good destination data is overwritten; restore transactions are atomic and report conflicts.
- No migration path uses destructive fallback. Every supported historic schema is exercised sequentially and from documented direct upgrade paths.
- SAF content is discovered only after the user selects/grants a directory. No URI is converted to a filesystem path. An uninstall/reinstall case must explicitly regrant SAF access and distinguish app-private state from user-owned files.

## Potential compatibility defect D-004 — reproduce before repair

`ChatMessage` now declares `sourceChunkIds: List<String> = emptyList()` (`Models.kt:389`). Old Room rows and schema-v2 backups can contain serialized `ChatMessage` objects without that field. Gson’s Kotlin-object construction may leave omitted non-null properties null when constructor defaults are bypassed; `BackupCodec.validateChatMessage()` dereferences `message.sourceChunkIds.size` (`BackupCodec.kt:234–242`). The v2 backup fixture in `RecoveryRegressionTest.kt:26–42` has empty collections and therefore does not exercise an old recipe/chat object. This is a **P1 compatibility risk pending a focused failing regression**, not proof that the installed database contains such a row or that user data has been lost.

Reproduction case: in a test-only isolated worktree, create a backup-v2 fixture and Room-v2 `StoredRecord` fixture with a valid historic recipe and a chat message lacking `sourceChunkIds`; decode/open it through the actual `BackupCodec` and `DataRepository` paths. Expected: fields receive safe explicit defaults, the app opens/restores, record IDs and payload semantics remain stable. Current suspected actual: null dereference or rejection at validation. Do not run this against the installed database.

## Migration test status

Repository source contains `DataRepositoryTest`, `ReferenceSeedMigrationTest`, `BackupCodecTest`, and `RecoveryRegressionTest`. The freshly executed `:app:testDebugUnitTest --rerun-tasks` passed; see its exact report summary in `TEST_EXECUTION_REPORT.md`. The run executes current synthetic migration/recovery fixtures, but does not establish that fixtures cover every historical schema or prove upgrade/regrant behavior on physical SAF providers. The full schema upgrade journey remains **NOT_RUN** until versioned historic fixtures are checked and run in a disposable Android database.

## Minimal repair test plan

1. Add failing old nested-record fixtures for schema-v2 backup and a pre-`sourceChunkIds` Room record; assert no crash, field defaults, stable IDs/counts, and round-trip semantics.
2. Add/retain explicit schema migration fixtures for every supported historic database version, with hashes/record manifests checked before and after each step.
3. Verify seed upgrade idempotence with user edits and soft-deleted reference rows.
4. Run backup/restore and SAF permission revocation/regrant on a disposable API 33 device/profile, preserving the installed user app untouched.
5. Only then implement a minimal additive decoder/migration; never delete historical rows or enable destructive fallback.
