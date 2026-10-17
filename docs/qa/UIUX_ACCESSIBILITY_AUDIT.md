# UI/UX and accessibility audit

**Baseline:** `6d429df9299d438daefbdac769b5300988d83f9c`, app 1.14.5. This is a source-level review. No screenshot was captured or inspected for this audit, and no runtime TalkBack, switch access, 200% font, theme, tablet, or physical-device journey was run.

## Route inventory and per-screen states

The NavHost in `app/src/main/java/com/coffeecreator/app/ui/screens/CoffeeApp.kt:545–565` declares nine destinations. State entries below distinguish code inspection from visual/runtime acceptance.

| Route | Primary user work | Source/state review | Required acceptance still open |
|---|---|---|---|
| Home | Choose feature and continue main journeys | Route is present; no route-specific screenshot/semantics run. Loading/error behavior was not confirmed here. | TalkBack labels/order, 48dp bounds, narrow/expanded layout, offline cold start, no-model recovery. |
| Formulate | Edit/scale recipe, validation, copy/save | Recipe and calculation ViewModel/domain paths exist; old manual workflow remains connected to repository. | Decimal locale, invalid units, error recovery, destructive-action undo, TalkBack form labels, process recreation. |
| Library | Catalog/search beans, gear and ingredients | Catalog models and ViewModel paths exist; explicit S2 catalog test markers are present. | Empty/search/error state visual review, large-text row actions, image/photo/OCR provenance, contrast and touch bounds. |
| Journal | Browse/search/compare and link brew history | Journal ViewModel and durable repository record path exist. | Empty-history state, charts read order, export file receipt, 200% fonts and large dataset scroll. |
| Sensory | Legacy flavor wheel/cupping plus Sprint 04 dashboard, Learn, Practice, CVA, Green QC, Operations | Legacy cupping has ViewModel persistence. Five new S4 sections are called without state/view-model callbacks (`CoffeeApp.kt:6330–6356`). Multiple form values and statuses are prefilled local state; exports/lock report success without durable output (defects D-001–D-003). | Every S4 section requires durable draft/save/relaunch tests, permissioned reveal, true blank/unknown states, actual export artifact, form semantics, human observation controls, TalkBack, and small-screen operation. |
| Knowledge | Browse/import/search local reference documents | Document picker, Room/FTS, vector retrieval, citation and abstention code exists. | SAF revoked grant/provider failure, citation source/page, prompt-injection tests on device, no-evidence state, long document accessibility. |
| Models | Choose/import/download/verify/load local model assets | User-selected SAF directory and user-initiated asset-transfer source exists. | Real provider range/ETag/seek and checksum matrix, regrant after reinstall, low storage, actual model load/cancel/RAM and no-model state. |
| AI Chat | Local inference and allowlisted typed tool use | ViewModel displays no-model fallback; tool execution is in core code. | Actual model/device inference and cancellation, citations, write confirmation, process recreation, long response scrolling and semantics. |
| Settings | Privacy, profile, units, localization and storage | Settings destination and theme/accessibility paths exist in source. | No-login/offline review, permission revoke/restore, reset/data-deletion consent, localized input, TalkBack and font scaling. |

The route inventory does not imply each route is exercised by Compose UI tests. Instrumented `ManualJourneyTest` has source coverage for primary navigation, but it was not run because it installs into and mutates the app package, and no disposable test profile/device was established.

## Accessibility evidence

| Check | Evidence | Result |
|---|---|---|
| 48dp touch targets | Compose source uses shared tactile component primitives in places; no per-action bounds scan or device measurement captured. | NOT_RUN / NOT_PROVEN |
| TalkBack semantics and traversal | No automated S4 semantics test found; no screen reader journey performed. | NOT_RUN |
| 200% font | `CompactBrewLocalTest.kt` source defines a 360×480dp, `fontScale=2f` Brew surface. This test passed in the fresh JVM suite, but covers Brew only. | PARTIAL; other routes NOT_RUN |
| Compact/medium/expanded layout | Adaptive navigation source exists. No full route matrix/screenshot comparison run. | NOT_RUN |
| Contrast and non-color cues | Theme tokens/components exist; no contrast report or per-state manual review captured. | NOT_RUN |
| Reduced motion | No reduced-motion manual test captured. | NOT_RUN |
| Keyboard/switch access | No test captured. | NOT_RUN |
| Language/locale input | Localized strings and decimal handling have source coverage; no full device locale/input run captured. | NOT_RUN |
| Accessibility on S4 forms | Current S4 forms need durable state and verified fields first; no semantics journey found. | BLOCKED by D-001–D-003/D-008 |

## Per-screen state matrix

The PRD/AGENTS require loading, empty, offline, error, success, and recovery states wherever applicable. Source existence is not enough to mark a state complete. The following states are all **NOT_RUN** in this audit:

| Route | Loading | Empty | Offline/no model | Error | Success | Recovery |
|---|---|---|---|---|---|---|
| Home | NOT_RUN | NOT_RUN | NOT_RUN | NOT_RUN | NOT_RUN | NOT_RUN |
| Formulate | NOT_RUN | NOT_RUN | Source says manual mode | NOT_RUN | NOT_RUN | NOT_RUN |
| Library | NOT_RUN | NOT_RUN | NOT_RUN | NOT_RUN | NOT_RUN | NOT_RUN |
| Journal | NOT_RUN | NOT_RUN | NOT_RUN | NOT_RUN | NOT_RUN | NOT_RUN |
| Sensory legacy | NOT_RUN | NOT_RUN | NOT_RUN | NOT_RUN | NOT_RUN | NOT_RUN |
| Sensory Sprint 04 | Local Compose state exists; runtime not run | Blank vs prefilled state is defective for Green QC | Not run | NOT_RUN | Several success notices are false outcomes | Durable recovery not wired |
| Knowledge | NOT_RUN | NOT_RUN | Source says local/offline | NOT_RUN | NOT_RUN | SAF revoke not run |
| Models | NOT_RUN | NOT_RUN | No model fallback exists in source | NOT_RUN | NOT_RUN | SAF regrant not run |
| AI Chat | NOT_RUN | NOT_RUN | No-model fallback exists in source | NOT_RUN | NOT_RUN | Cancellation/process recreation not run |
| Settings | NOT_RUN | NOT_RUN | NOT_RUN | NOT_RUN | NOT_RUN | Permission recovery not run |

## Screenshot and design review

README contains twelve image references labelled as Samsung S20 Ultra screens. Their origin, capture method, exact build commit, and whether every image shows live data were not independently attested in this audit. They are not treated as new screenshot regression evidence. No visual claim is marked verified from filenames, checked boxes, or existing release text.

The design audit should use `DESIGN.md` Material 3 tokens and Compose semantics. Web-specific React/Tailwind snippets in the 2026 best-practice file do not directly establish Android compliance. The UI requests a compact, one-handed barista flow; source tests and manual evidence need to cover frequent operations, safe data entry, empty/unknown values, and a clear recovery action.

## Minimal UI/accessibility follow-up

1. Establish disposable app data and a clean emulator/API matrix; do not use the installed profile.
2. Add semantics-level Compose tests for all primary routes and every S4 action, asserting state, persisted data and actual export output.
3. Re-run compact 360×480/fontScale 2 plus medium/expanded windows for all core routes; measure touch bounds and run TalkBack/keyboard/switch access.
4. Capture before/after screenshots from the exact tested commit; pair them with test logs and screen state IDs.
5. Do not mark S4 as complete until blank/unknown measurements, confirmed consent, durable drafts, exports, and standards-mode gating work end to end.
