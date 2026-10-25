# Journal replay and manual brew cues

Date: 2026-10-25 (Asia/Jakarta). Baseline: 5f223c6 on codex/s4-cva-persistence. Integrator: codex/brew-replay-cues. Main checkout contains unrelated uncommitted catalog work and is preserved.

## Why

User screenshots show a long ratio collapsing a five-column recipe summary, unnecessarily expanded step forms, and no direct Journal-to-timer path. Timed cue state currently has no Android consumer. Semi-automatic espresso use needs a manual stopwatch, not implied machine/BLE control.

## Changes

- One explicit replay handoff shared by saved recipes, historical journal entries, champion templates and repeat-last.
- Responsive recipe facts, clear Brew again/Start actions, restrained native feedback, progressive step editing.
- One monotonic timer controller with persisted checkpoint, foreground notification, haptic/sound/silent cue choices and manual espresso finish.
- Additive optional recipe snapshot on new journal entries; preserve existing rows, immutable originals, IDs and unknown measurements.

## Scope and release

PRD FR-02/FR-10; S2-027–031/038/046; BR-07/08/12. All unrelated destinations, standards, seeds and data retained. No dependency/signing upgrade, machine control, fabricated weight/TDS/rating, telemetry or network feature. User photos remain local reference material. User instruction against ADB remains active. A scoped debug prerelease may distribute tested fixes with explicit NOT_READY product status; no claim that open cross-sprint gates passed.

## Method

Use OpenSpec proposal/spec/design/tasks and BDD red/green checks. BMAD roles: root planner/integrator/replay, isolated UI agent, isolated timer agent, independent QA review. RTK/Ponytail/Caveman are authoring guidance only. Existing global android-jetpack-compose-expert skill is available; use its state/semantics guidance while ignoring outdated sample dependency versions.
