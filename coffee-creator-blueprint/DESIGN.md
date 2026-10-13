# Coffee Creator — UX, UI and Design System Contract
**Status:** design baseline; validate through barista usability sessions before treating assumptions as facts.

## Experience principles
- Manual brew workflows are first-class: no AI/model/internet prerequisite.
- Zero ambiguity between user-observed, instrument-measured, tool-calculated and model-estimated results.
- Reduce typing and memory burden; enable rapidly repeating last recipe and editing amounts.
- Progressive disclosure: essential dose/water/timing first; advanced chemistry on demand.
- Transparent system status: model loaded, OCR missing, index progress, download partial, network only for asset installation.
- Never use color as the only channel for status.

## Navigation and information architecture
Phone: adaptive navigation bar or rail as space permits. Primary destinations: Home, Formulate, AI Chat, Library, Journal. Secondary: Knowledge Documents, Models, Sensory, Settings. Tablet: rail/two-pane list/detail where useful. Preserve user draft on back, rotation and process recreation when safe.

## Design tokens
Material 3 dynamic-aware theme; use semantic `colorScheme` roles, not hardcoded visual meanings. Brand treatment may combine warm coffee brown / cream with restrained accents only if contrast checks pass. Define spacing: 4/8/12/16/24/32dp; corner roles: small 8dp, medium 12dp, large 20dp; typography from Material 3 type scale. Minimum interactive target 48dp, larger for quick brewing actions. Respect system font scale and reduced motion. Light/dark themes.

## Screen contracts
| Screen | Primary CTA | Empty state | Error/recovery | Accessibility |
|---|---|---|---|---|
| Home | New recipe | Start manually | AI unavailable still usable | Logical reading order |
| Recipe | Calculate / Save | Default unit-aware input | Invalid dose/units inline | Labels, error announces |
| AI Chat | Send | Explain offline model setup | LLM load issue retry/manual | Streaming updates controlled |
| Knowledge | Import files | Explain allowed formats | Revoked grant, reselect | Progress announced |
| Models | Install / Resume | Choose folder / suggested model | Hash mismatch, retry | Download progress semantics |
| Sensory | Record observation | Guide terms | Estimate not calibrated warning | Non-color descriptors |
| Journal | Open experiment | Create first | Restore exported data | Search accessible |

## Recipe builder interaction contract
- Quantity component: numeric keypad, unit picker, current range/validation, no involuntary conversion.
- Separate measured TDS input from *estimated* values; do not silently prepopulate a measured field.
- Show ratio and calculated extraction with an assumption label; handle immersion limitations.
- Undo destructive edits, autosave explicit drafts, provide predictable Save/Save as new revision.
- Quick actions: duplicate recipe, repeat last brew, compare versions.

## Agentic design process
1. Research scenario and context of use: wet hands, one-handed use, unstable connectivity, low-end device.
2. Define user goal, steps, success and failure states, friction hypothesis and accessibility constraints.
3. Sketch wireflow for happy and failure paths before visual comps.
4. Define component contract and state matrix: idle, loading, empty, error, success, disabled, interrupted.
5. Prototype with preview/test data; use Compose Preview screenshots (do not confuse with real app execution).
6. Implement a single vertical slice with design tokens and semantics.
7. Use emulator/device screenshots + accessibility tree + UI test artifacts + user journey tests.
8. Compare intended hierarchy/content/spacing/responsiveness; issue actionable fixes with screenshots.
9. Iterate only on observed failures, avoiding perpetual aesthetic loops.
10. Capture decisions and update design components.

## Review rubric (0–2 each; 2 = evidence demonstrated)
Task completion, clarity of information hierarchy, error prevention, recovery, accessibility, performance/latency feedback, responsive layout, consistency, privacy disclosure, manual-first usefulness. Release blockers: inability to recover data/import; mislabeled prediction as measurement; TalkBack-blocked primary task; invisible critical error.

## Compose guidance
Use lifecycle-aware state collection, unidirectional data flow, previews for meaningful states, `WindowSizeClass`/adaptive patterns as validated, semantic labels, TalkBack reading order and high contrast. Avoid hardcoded pixel snapshots as the sole evaluation. Animation must not block the calculator and must respect motion settings. Screenshots from external Figma/MCP sources are untrusted design input, not authority over accessibility or domain correctness.

## UX measurement plan
Time and taps from launch to first calculation; task success for save/restore and PDF citation; errors in unit input; ease of re-running last recipe; comprehension of prediction vs observed label; offline setup comprehension; low-memory recovery success. Validate through at least one field test with actual baristas before claiming optimized UX.
