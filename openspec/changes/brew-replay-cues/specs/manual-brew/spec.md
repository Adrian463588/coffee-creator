# Manual replay and timer acceptance

BRUX-01: Given a journal entry linked to a recipe, when its main content or Brew again is tapped, then the matching plan opens in Timer ready to Start, without a new recipe/journal write.

BRUX-02: Given a new journal snapshot and a later edited recipe, when replayed, then original steps/dose/equipment persist; TDS, beverage mass, first-drop measurement and rating are not fabricated for the new session.

BRUX-03: Given a legacy entry without snapshot or a deleted source, when replayed, then historical quantities/equipment are preserved in an explicit reconstructed plan; unknown observations remain unknown. Invalid nonfinite/nonpositive required values show recovery without substituting a different recipe.

BRUX-04: Given a running or paused brew, when another recipe is tapped, then current session ID/time/progress survive and a visible message explains how to finish it first. Existing draft can be undone after a valid replay.

BRUX-05: Given a long champion title/bypass ratio, compact width or 200% font, when rendered, then facts do not overlap, ratio remains readable, primary action stays reachable, source attribution and all secondary actions remain available.

BRUX-06: Given a step plan, when summary is opened/edited/cancelled, then all fields remain reachable with valid focus/labels; editor does not require scrolling through every expanded card before Start. Invalid/empty numeric entry is recoverable.

BRUX-07: Given timed pours and 250ms polling, when an exact boundary is crossed, then exactly one selected step cue fires, next action/target is shown, and no duplicate cue appears on the same second. Pause/resume excludes paused time; restart/reset update current step correctly.

BRUX-08: Given Xiaomi semi-automatic, Nuova Simonelli Appia, Ferratti Ferro or another manual espresso setup, when Start is tapped without BLE, then elapsed timer runs; target time cues once and continues until Finish; actual recorded duration matches finish, no machine control or invented yield.

BRUX-09: Given sound/vibration/silent notification preferences or denied notification permission, when a step becomes due, then Android delivery follows preferences/platform settings and timing remains usable. Background service and UI observe one clock; cancelled/finished timer stops notifications/resources.

BRUX-10: Given rotation/process recreation/legacy JSON, when reopened, then plan/settings/checkpoint recover without overwriting historical records or replaying stale alerts; reboot shows a paused recovery state. Device-only timing delivery and TalkBack remain NOT_RUN until actually exercised.
