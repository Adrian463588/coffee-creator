# Proposal: Close verified QA defects and refine interaction feedback

## Why

The S01–S04 baseline audit at `49097a9` found fabricated Green QC measurements, non-durable S4 forms, misleading export/consent and release claims, and incomplete accessibility/UX verification. These gaps can mislead users or lose records. The Brew flow also needs clearer menu filtering and action feedback without a visual redesign.

## Scope

Use small, independently reviewable slices: remove invented measurement defaults; make user-entered S4 records durable where existing storage contracts permit; gate unverified standards and mark simulations accurately; default sharing consent off; repair verified release metadata; refine high-frequency Brew interactions; add provenance/recovery tests. Keep historic user data, routes, identifiers and existing features intact.

Items requiring licensed standards, historical source authorization, supported BLE/OCR hardware, a disposable device profile, or inaccessible video evidence remain explicitly blocked. Do not fabricate substitute evidence or data.

## Invariants

- No destructive migration, database wipe, uninstall, production-profile mutation, SAF-folder mutation, fabricated measured data, unsupported standard claim, or hidden feature.
- Additive Room migrations only; preserve IDs, revisions, journal links, backups and export compatibility.
- Manual brew remains usable without AI, network, BLE or consent to sharing.
- UI changes stay within existing Compose components and theme tokens; motion conveys state/feedback, respects accessibility, and never substitutes for text or semantics.
- A defect closes only when its regression test and relevant verification pass; device/licensing blockers remain `BLOCKED`.

## Workflow

Apply OpenSpec's proposal/spec/tasks flow and BMAD's investigate-plan-build-review loop. Use BDD acceptance scenarios below; implement tests before fixes; review through a separate agent/worktree; run focused tests before full gates. RTK/Ponytail/Caveman remain authoring guidance, not runtime dependencies.

