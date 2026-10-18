# Proposal: durable Sprint 04 CVA sessions

## Why

Sprint 04 CVA edits currently live in Compose state. Users cannot reliably save/reopen a session, compare persisted assessments, lock/amend a durable record, or export a stored revision. This is the remaining P0 data-preservation gap D-003.

## What Changes

- Persist typed CVA session aggregates in the existing Room `records(kind,id,json)` table through the repository boundary.
- Include CVA sessions in an additive backup schema v4 while continuing to import v2/v3 backups unchanged.
- Connect CVA screen actions to ViewModel/repository operations for draft save/resume, compare, lock/amend, and SAF export of persisted data.
- Keep legacy S2 cupping records and their IDs/data on their existing storage path.
- Keep `SCA_CVA_CURRENT` visibly gated until the field mapping and license evidence are verified; custom research remains usable.

Green QC record persistence, panel authorization/statistics, SQL normalization, and external release edits are separate changes.

## Capabilities

### Modified Capabilities

- `s4-cva-sessions`: retain CVA sessions across process recreation and backup/restore, with immutable locked revisions and explicit amendment history.
- `backup-restore`: add CVA session records without losing compatibility with supported v2/v3 payloads.
