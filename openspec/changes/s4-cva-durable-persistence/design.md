# Design: durable Sprint 04 CVA sessions

## Context

The app uses Room v2 with a generic `records` table keyed by `(kind,id)` and canonical JSON payloads. ObjectBox is a separate vector index. `DataRepository` centralizes transactional mutation, snapshot publication, and full backup/restore. CVA already has pure domain models, checksum logic, transition validation, comparison, and export formatting, but its Compose section is local-state-only. Legacy S2 `core.sensory.CuppingSession` is already persisted separately and must not be reused or overwritten.

## Goals / Non-Goals

**Goals:** durable S4 CVA session create/update/load/list, validated lock/amend, compare from persisted records, exact SAF export, v4 backup round-trip with v2/v3 compatibility, and source-compatible offline custom research.

**Non-Goals:** Room table normalization or FK claims; Green QC/photo persistence; authenticated assessor identity; panel workflows; official current-standard CVA scoring; external release-body edits; UI redesign.

## Decisions

- Add typed `CvaSessionRepository` contract in the data/domain boundary and implement records as `kind = cva_session` in the existing Room table. Validate record ID, aggregate size, checksum, transitions, assessment bounds, nested amendment snapshot content, and lot references at the repository boundary. Share pure assessment-range validation with report export so a self-consistent hash cannot legitimize invalid prior evidence. No Room schema version change is needed because the table shape is unchanged; database-open regression tests will prove old rows survive.
- Extend `BackupPayload` to schema v4 with a `cvaSessions` collection. Decode v2/v3 by validating their original key set, counts, and checksum before adding the new empty collection in memory. Keep the v2/v3 wire-format hash calculation unchanged. Restore validates every session before the existing write transaction; a legacy backup cannot erase CVA rows that did not exist in its format.
- The repository and backup contract store typed `CuppingSession` aggregates in this slice. Compose remains disabled for save/reopen/compare/lock/export until a versioned field-presence mapping can preserve its `NaN`, `-1`, null, and raw physical-input states without coercion to domain defaults.
- The next UI slice must use immutable ViewModel state/actions and never access Room directly. Draft autosave may debounce repository writes, while lock/amend/export stay explicit user actions.
- Export only a repository-loaded session using existing CVA report formatters and the tested SAF writer. Re-read the session by stable ID before export so unsaved UI state cannot be mislabeled as persisted.
- Keep the current-standard option visible but disabled with provenance explanation until an authorized mapping/license artifact passes review. The default remains `CUSTOM_RESEARCH`.

## Risks / Trade-offs

- The current CVA form has many local Compose fields and must be mapped to the typed aggregate without changing their meaning. Each section needs field-to-domain golden tests; unset values must stay unknown rather than becoming zero.
- Aggregate storage alone does not solve the draft mapping gap: `SampleAssessment` uses numeric defaults and cannot distinguish untouched zero/false values from recorded values. The UI follow-up must add a typed presence representation and migration-compatible tests before wiring persistence.
- A complete amendment snapshot may be large. Apply existing backup/session size bounds and reject oversized writes before any mutation.
- Stored lot IDs are not backed by SQL foreign keys. Repository validation must distinguish known legacy lot references from intentionally redacted/unlinked samples; it must not delete or rename a lot to satisfy linkage.
- The existing actor ID is caller-supplied. This slice preserves that limitation and must not present it as authenticated authorization; reveal stays unavailable unless the flow can meet the accepted permission contract.
- UI process death and SAF document-provider behavior require instrumentation/manual acceptance. Host tests alone cannot claim those layers.

## Rollback

Revert only this scoped branch/commit. Because persistence adds record kinds and backup fields without SQL migration, rollback leaves stored rows and v4 backups intact; older binaries may reject v4 backups, so do not change README/release claims until a forward-compatible decision or compatibility gate is recorded.
