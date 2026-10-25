# Design and implementation plan

## User task and visual direction

Barista with one hand occupied: find a previous brew, check dose and target, begin timer, hear/feel the next pour. Primary actions: Brew again in Journal, Start/Pause/Finish in timer. Alternatives considered: (A) existing cards with responsive facts and restrained hierarchy; (B) compact rows plus new detail screen. Choose A for minimal diff and preserved actions. Dose, water and time form the visual signature; no decorative pulse or per-second TalkBack announcement.

Reuse existing MaterialTheme typography/color/shape and 4/8/12/16/24dp spacing. Facts wrap to available width and font scale, ratio gets its own full-width label/value. Controls have at least 48dp targets; native ripple, meaningful selected/expanded semantics; no fixed text height or forced tiny type. Step summary expands into existing fields and keeps a direct Continue to timer action. Empty, invalid numeric input, denied-notification and recovery states retain manual operation.

## Contracts and ownership

- Root: core/journal/BrewReplayFactory.kt, BrewJournalModels.kt (optional recipeSnapshot), BrewCompletionRecordFactory.kt, ui/viewmodel/CoffeeViewModelJournal.kt, replay tests, planning/QA/release docs and version metadata.
- UI agent (separate codex/brew-replay-ui worktree): CoffeeApp.kt, BrewSection.kt, focused UI components and host Compose tests. No domain calculations or timer service files.
- Timer agent (separate codex/brew-step-cues worktree): core/brew runtime/engine/service and session model, CoffeeViewModelBrewSession.kt, minimal CoffeeViewModel/AppGraph observer wiring, PreferenceStore/proto, Manifest, timer tests. No CoffeeApp or Journal edits.
- Root integrates commits sequentially and owns conflict resolution. No two agents edit a shared file concurrently.

Replay UI calls repeatJournalBrew(entry): Boolean, prepareRecipeForBrew(recipe): Boolean, brewChampionRecipe(recipe): Boolean and repeatLastBrew(): Boolean. True means exactly one recipe was loaded into draft and ready timer; UI navigates to Formulate with Timer selected. False preserves active brew and exposes actionable notice. No auto-start from card tap. Existing initBrewSession API can return Boolean while old callers ignore result; active RUNNING/PAUSED session must not be overwritten.

Historical replay precedence: optional immutable journal recipe snapshot, matching saved version or stable champion ID, finally legacy entry fields with an explicitly reconstructed single-stage plan. Clear measuredTdsPercent/beverageMassGrams/firstDropSeconds for the new brew, retain plan/equipment/quantities/stages, and never mutate/save the original. New completed journal entries retain their source plan. Legacy JSON absent snapshot remains valid.

Timer: single graph-owned controller, SystemClock monotonic time, additive preference/checkpoint storage. Foreground service consumes same controller and delivers per-boundary cues; no second elapsed-time authority. Deduplicate by session/cue cursor, suppress stale cues on restore, preserve pause fraction and step-reset state. Process recovery is explicitly paused; reboot never subtracts stale uptime. Notifications use platform-compliant specialUse FGS, explicit Start/Resume only; permission denial degrades visibly, manual timing continues. App channel selection respects user sound/vibration settings and platform DND. Espresso continues beyond target until Finish, records actual elapsed only, never automatically controls or connects a machine. Existing optional scale feature remains distinct and cannot block manual timing or claim a simulated hardware connection.

## Verification and rollback

Before production edits, reproduce at least one current replay/UI failure and one timer timing/cue failure with host tests; archive decisive logs. Then focused tests, actual Gradle task discovery, full JVM/Compose host suite, KtLint, Detekt, Android Lint and assembleDebug. Capture/inspect host-rendered compact light/dark, long ratio and 200% font states; label host renders accurately. Device vibration/sound/service survival, TalkBack, BLE and full migration acceptance remain NOT_RUN without device execution. No ADB commands.

No Room SQL migration; JSON field additive. Validate legacy no-snapshot decode and snapshot backup roundtrip/reopen. Maintain recipe seed ID counts and original source strings. Rollback via reverting scoped commits; no storage wipe, destructive migration, uninstall, reset, force push or signing changes.

## References

User guides: C:/Users/HP OMEN/Downloads/JETPACK_COMPOSE_UIUX_BEST_PRACTICES_ANTI_AI_SLOP.md and ANDROID_JETPACK_COMPOSE_UIUX_BEST_PRACTICES.md. These guide design; user task and blueprint invariants retain priority.

- https://github.com/Fission-AI/OpenSpec/blob/main/docs-lab/reference/schemas/spec-driven/index.md
- https://github.com/bmad-code-org/BMAD-METHOD
- https://developer.android.com/develop/ui/compose/accessibility/api-defaults
- https://developer.android.com/develop/ui/compose/layouts/flow
- https://developer.android.com/develop/background-work/services/fgs/service-types#special-use
- https://developer.android.com/develop/ui/views/notifications/notification-permission
