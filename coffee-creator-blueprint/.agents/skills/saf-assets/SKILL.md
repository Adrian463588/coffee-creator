---
name: saf-assets
description: Use for model/bundle downloads, resumable HTTP transfers, SAF storage and reinstall recovery.
---

# SAF Persistent Asset Manager

## Preconditions
Read `AGENTS.md` and the directly related PRD/architecture/design contracts. Only load other reference files when the task requires them.

## Required workflow
Model explicit state transitions. Test Range 206, Content-Range, ETag/If-Range, SHA256, retries, pause/reboot, low storage, unsupported seek providers, checksum failure and publish. SAF URI is not a filesystem path. Reinstall requires user regrant; app-private checkpoints do not survive uninstall. Confirm WorkManager and foreground transfer rules for target SDK.

## Completion report
- Scope/requirement
- Files changed
- Checks executed (PASS/FAIL/NOT RUN)
- Offline/privacy/a11y/science risks
- Blockers or ADR needed
