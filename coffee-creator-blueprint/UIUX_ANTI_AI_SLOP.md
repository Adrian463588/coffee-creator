# Coffee Creator — Anti-AI-Slop UI/UX Methodology for Android Jetpack Compose

**Version:** 1.0 | **Research cutoff:** 15 October 2026 | **Status:** Normative design guidance, with hypotheses requiring field validation

**Applies to:** `:feature:*`, `:core:designsystem`, Compose previews, AI-generated screens, interaction flows, design review, and UI quality gates.

> **Meaning of “anti AI swap”:** Interpreted as **anti-AI-slop**: avoid generic, interchangeable AI-generated UIs. This is not an official Android methodology. The approach below synthesizes established UX methods with 2026 research on AI-generated design and Google Android design/testing guidance.

## 0. Sources, evidence and interpretation

- NN/g, *UX-Context Design: Using UX Knowledge to Inform AI-Generated Design* (24 Jul 2026): provide specific user/domain/design context to avoid average, generic AI output. https://www.nngroup.com/articles/ux-context-design/
- NN/g, *The Custodial Era of UX* (28 Aug 2026): AI increases UX debt without evaluation, shared judgment and quality controls. https://www.nngroup.com/articles/ai-ux-debt/
- NN/g, *State of UX in 2026* (16 Jan 2026): relevant, unobtrusive AI; trust, transparency and user control. https://www.nngroup.com/articles/state-of-ux-2026/
- Android Developers, *Material 3 in Compose*: Material 3 / Material 3 Expressive, tokens, theming, accessibility. https://developer.android.com/develop/ui/compose/designsystems/material3
- Android Developers, *Canonical adaptive layouts*: list-detail / supporting-pane patterns across phones and large screens. https://developer.android.com/develop/adaptive-apps/guides/canonical-layouts
- Android Developers, *Compose accessibility testing*: test semantics, contrast, targets and reading order; manual TalkBack remains essential. https://developer.android.com/develop/ui/compose/accessibility/testing
- Android Developers, *Compose Preview screenshot testing*: preview snapshots and golden-image comparison; APIs have been experimental and configuration has evolved. Verify AGP-specific setup. https://developer.android.com/studio/preview/compose-screenshot-testing
- Android Developers, *Compose performance*: baseline profiles, recomposition, benchmarking. https://developer.android.com/develop/ui/compose/performance
- Android Developers Blog (24 Sep 2026), *Build your way: Use any AI agent in Android Studio*: BYOA and Android-aware agent tools. https://android-developers.googleblog.com/2026/09/build-your-way-use-any-ai-agent-in-android-studio.html

**Evidence classification:** Google's Android documentation defines platform behavior and tooling. NN/g contributes UX research and interpretation. Product-specific tokens, screen hierarchy, targets and rubrics below are **Coffee Creator design decisions** to validate with users, not universal measured best practices.

## 1. Product-specific design thesis

**North star:** A professional barista can get from intent to an accurate, editable brew formulation in minimal cognitive steps, including when offline, wearing gloves, using one hand or working beside wet equipment.

**Principles (priority order):**
1. **Task-first, AI-second:** Recipe calculations, history, ingredient search and sensory logging work without an LLM. AI enriches context instead of gating access.
2. **Precision visibly trumps decoration:** Clearly distinguish measured, calculated, human observed, rule-estimated and ML-predicted values. Never invent a measurement.
3. **Contextual distinctiveness:** This must look and behave like a coffee lab workbench, not an arbitrary fintech/crypto/AI dashboard with coffee labels.
4. **Progressive disclosure:** Common inputs prominent; advanced concentration science, confidence intervals, and source metadata one level deeper.
5. **Small interactions are recoverable:** Persistent drafts, redo, undo, interruption recovery, visible units and sensible defaults.
6. **Calm visual language:** Hierarchy, alignment, typographic contrast and purposeful controls before gradients, decorative icons or animation.
7. **Accessible and responsive by design:** TalkBack, font scaling, compact/medium/expanded width, dark theme, non-color signals and minimum 48dp hit targets.
8. **Offline trust:** Explicit local processing status, storage choice, RAG source provenance and no suggestion that the app requires cloud access.

## 2. What qualifies as AI-slop, and how to detect it

| Failure smell | Detection question | Required correction |
|---|---|---|
| Identical rounded-card grid | Is every concept represented as the same card? | Use task-appropriate controls: ingredient rows, quantity inputs, source excerpts, charts, panels. |
| Decorative gradient/glass surfaces | Does decoration communicate status or hierarchy? | Remove unless it has a documented semantic role and passes contrast. |
| Vague marketing copy | Can users tell what tapping it will do? | Replace “Unlock flavor intelligence” with “Calculate extraction yield”. |
| Fake intelligence/confidence | Is this predicted without data? | Display provenance/method or omit estimate. |
| Stock imagery everywhere | Does every screen show coffee beans even where users need data? | Keep photography for editorial onboarding / catalog, never displace essential task data. |
| Overexplained onboarding | Must user dismiss many slides before calculating? | Enable **Try manual recipe** immediately; use contextual hints. |
| Too many primary CTAs | Can the user identify the next best action in 3 seconds? | One obvious primary action per state. |
| Uniform spacing without hierarchy | Are critical data and secondary metadata equally prominent? | Introduce purposeful typographic scale and semantic grouping. |
| Animated progress without progress data | Is shimmer used to hide stalls or errors? | Display stage, cancellation, ETA only if grounded, retry guidance. |
| Form conflates grams and mL | Are users required to remember physical conversions? | Use unit-aware numerical controls and density-aware conversions. |

Do **not** automatically outlaw gradients, cards, custom icons or animation: forbid **unjustified repetition** and visual artifacts lacking purpose.

## 3. Research-led UX workflow: continuous discovery, not AI aesthetic roulette

### Step A — Context and research
- Target: head baristas, specialty brewers, roasters, beverage R&D.
- Conduct 5–8 initial **formative** task observations across experience levels if feasible; sample size is a research planning heuristic, not proof of representativeness. Expand for segment differences.
- Observe bench lighting, gloves, hand use, equipment proximity, language and measurement conventions.
- Capture the user job, context, present workaround, risk, data needs and success signal for every task.
- Keep raw observations separate from AI synthesis; do not synthesize invented participant evidence.

### Step B — Task model and information architecture
Document jobs-to-be-done and cognitive task analysis, then storyboard **happy path, error path and recovery path**:
- Create brew recipe -> edit dose/water -> calculate -> save -> brew -> record actual TDS -> record tasting -> duplicate.
- Search a coffee origin / ingredient -> check source -> compare -> add to formulation.
- Import PDF through SAF -> offline OCR if needed -> index -> ask grounded question -> inspect document citation.
- Download model -> lose connection -> resume -> verify -> launch -> reinstall -> reselect SAF tree -> rediscover assets.

**IA:** Home / Formulate / AI Chat / Library / Journal; Knowledge, Sensory and Model Manager accessible contextually and through settings/library. Validate bottom navigation versus rail by window class and usage.

### Step C — Low-fidelity flow before high-fidelity
For each screen require a wireflow including: user goal, entry point, primary action, main information hierarchy, empty/loading/error states, back behavior, keyboard behavior, and accessibility route. Generate **at least two meaningfully different layout hypotheses**; compare on task efficiency instead of novelty. Select one and record ADR-like rationale.

### Step D — UI specification before implementation
Create tokens, component interfaces, realistic sample data and a state matrix. No production Compose screen without approved screen contract. Avoid pixel-perfect static screenshots as the only source of truth.

### Step E — Closed-loop execution
**Specify → Compose Preview → Emulator/physical-device render → Capture screenshot + accessibility tree → Compare with spec and golden → UI test → Fix → Re-test → Record decision.**

AI agent must not claim visual verification unless the screen was actually rendered and inspected. External screenshot critique is not a substitute for live TalkBack, orientation and interaction tests.

### Step F — Validate and learn
Measure task completion, errors, navigation mistakes, retries, comprehension of uncertainty, SUS/SEQ when useful, and qualitative friction. Add recurring failures to this design file and to automated tests. Ship a design only after evidence or clear documented provisional rationale.

## 4. Coffee Creator visual identity and design system

**Direction:** **Precision Coffee Laboratory** — warm, tactile, restrained, data-dense when appropriate, and fundamentally utilitarian.

### Tokens: starting proposal, not fixed law
- **Core surfaces:** warm light neutral / near-charcoal dark; warm coffee-brown accent used sparingly for actions; secondary muted botanical/olive accent for observations; status colors must use semantic Material roles and text/icon redundancies.
- **Typography:** Material 3 type scale; numbers are tabular where supported; align decimal precision; use monospace only for IDs / diagnostics. No brand font dependency until font licensing, performance and readability are tested.
- **Spacing:** 4dp baseline; tokens 4 / 8 / 12 / 16 / 24 / 32dp; choose size by visual hierarchy, not blanket padding.
- **Shape:** small 8dp, medium 12dp, large 20dp, pill for chips only when applicable; do not turn every section into a card.
- **Motion:** feedback and spatial continuity only, honor accessibility/animation settings, do not animate scientific values in ways that mislead.
- **Iconography:** consistent Material symbols (or licensed coherent alternative), always pair unfamiliar glyphs with visible labels.
- **Contrast:** target WCAG 2.2 AA minimums where applicable (4.5:1 normal text, 3:1 large text and meaningful non-text UI). Use automated checks plus real devices.
- **Inputs:** target >=48dp, clear focus indicator, IME numeric support, unit suffix always visible, explicit decimals/localization.
- **Brand distinction test:** If the logo and “Coffee Creator” label are removed, the recipe workbench should still be recognizable as a specialized coffee formulation tool.

### Compose implementation rules
- Theme with `MaterialTheme(colorScheme, typography, shapes)` and mapped semantic tokens; avoid raw hex values in features.
- Adopt Material 3 Expressive selectively for high-signal controls/behavior; do not use maximal expressiveness everywhere.
- Hoist state into ViewModel with immutable UiState; expose callbacks; composables must not calculate extraction physics.
- Use `collectAsStateWithLifecycle`, stable list keys, state preservation and process-death recovery for drafts.
- Use `WindowSizeClass` / adaptive pane scaffolds and test foldable/window resizing; avoid device-model-only branching.
- Respect edge-to-edge and window insets; no clipped action under system navigation or IME.
- Provide previews for light/dark, font scale, error, loading, long text and device widths.
- Show validation inline before submit when possible; announce errors in accessibility semantics; do not rely on color only.
- During LLM token streaming, update UI at an appropriate cadence, maintain scroll position without forcibly stealing control, and provide Stop generation.

## 5. Screen-by-screen experience contracts

### Formulation workbench (highest priority)
**Primary task:** Change quantities; see precise recalculated values; save safely.
- Primary canvas: recipe name/method and essential mass-based fields, ratio and key measured/calculated outcomes.
- Input row: ingredient, amount, unit, optional density, validity. Tap target friendly, clear decimal keypad, undo.
- Secondary panel: assumptions, sensory logging, aroma concentrate safety notes, version comparison.
- Never label extraction yield “measured” when derived from measured TDS and mass using an approximation; explain limitations for immersion.
- Empty state: create from scratch or repeat previous. Offline and without LLM.
- Failure: invalid units, negative amounts, absent TDS, missing density, range warnings.

### Offline AI assistant
- Above composer: model name/loading state and simple Local-only indicator, not an oversized hero.
- User questions show response, cited source document and page/chunk when available, and separate calculation results.
- Disclose tool-derived numbers vs model explanation. Include copy to recipe, but require confirmation before mutations.
- No evidence: “I couldn't verify that from your selected documents,” with an action to search/import.
- Model unavailable: explain and offer Manual Formulate, Model Manager, retry; do not dead-end.
- Streaming: cancel, follow output without unexpected focus jumps, restore interrupted conversation.

### Knowledge library and RAG
- Display source, import date, pages, indexing state and retry cause.
- SAF permission revoked: reselect source, preserve indexing metadata until resolved.
- PDF answers link to citation passage and source position where supported; never invent links/pages.

### Model and asset manager
- Explicit model size, storage estimate, device compatibility, download stage, pause/resume and integrity verification.
- Separate partial transfer from verified installed asset; explain reinstall folder reauthorization.
- Network requirements only during explicit downloads. No spinner without recovery text.

### Sensory wheel & journal
- Wheel maps descriptor vocabulary and supports manual recording; never implies the wheel is a physics equation.
- Distinguish **Observed / Calculated / Estimated** using label + icon + shape/style, not color alone.
- Provide accessible list/table alternative to radial visualization, zoom and descriptive screen-reader labels.
- If official SCA artwork/data is reused, document licensing and redistribution rights; otherwise build an original taxonomy with appropriate attribution.

## 6. State matrix and non-happy-path checklist

For **every** route, specify and preview: Initial; Empty; Loading; Partial; Ready; Validation Error; Recoverable Failure; Irrecoverable Failure; Permission Revoked; Offline; Low Memory; Rotation / Multi-window; Large Font; Screen Reader; Process Restart.

Additional journeys: download stops at 83%; document OCR unavailable; corrupt GGUF; embedding model changed requiring reindex; storage filled mid-copy; long ingredient names; locale comma decimal; low-light barista environment; dynamic color; foldable hinge obstruction.

## 7. Agent roles and responsibility separation

| Agent | Inputs | Required output | Mandatory evidence |
|---|---|---|---|
| UX Researcher | PRD, task logs, actual user observations | problem framing, JTBD, friction map | quotes/observations referenced, unknowns clearly marked |
| Information Architect | JTBD, domain entities | navigation, wireflow, information hierarchy | scenario mapping, error route |
| UI Art Director | DESIGN.md, tokens, approved wireflows | component contracts, visual variants | rationale for differentiation and accessibility |
| Compose Developer | approved component contracts, Kotlin architecture | stateful Compose implementation | previews, compile/lint results |
| Accessibility Auditor | rendered app, TalkBack/test tree | a11y defect report | device/settings and reproduction |
| Visual Regression QA | golden images, renders | screenshot comparison, difference triage | artifacts and deviation severity |
| Usability Evaluator | protocol, participant consent | task outcomes, friction ledger | actual session evidence; no AI-invented participants |

**Prevent agent collusion:** the same agent should not both generate a screen and independently approve its visual quality. Reviewing agent should inspect real renders and the UX contract. Parallel tasks get non-overlapping file ownership / worktrees. Human review is mandatory for research claims and high-risk changes.

## 8. AI agent execution contract — copy into task prompts

```text
You are implementing Coffee Creator's Android UI. Read PRD.md, AGENTS.md,
DESIGN.md, and UIUX_ANTI_AI_SLOP.md before coding.

1. State the user job, context, measurable success and error/recovery paths.
2. Consult approved theme tokens and existing components before inventing new ones.
3. Produce a wireflow/state matrix and two genuinely different layout options.
4. Explain the selected option in relation to actual barista tasks.
5. Implement only the approved vertical slice in Jetpack Compose.
6. Supply previews for empty, loaded, error, dark, large font and compact/expanded.
7. Run Android Lint, tests, accessibility checks and screenshot comparisons.
8. Inspect actual emulator/device render and list detected differences.
9. Fix defects, re-run checks and report evidence with file paths/results.
10. If tools/emulator unavailable, explicitly report NOT VERIFIED; never fabricate screenshots or passing tests.

Forbidden: random gradient/glass/card boilerplate; fake confidence, fabricated
measurements, unapproved new design tokens, inaccessible icon-only interactions,
cloud dependency in core workflows, and claiming visual QA without renders.
```

## 9. Design review rubric and release gates

Score each 0 / 1 / 2 (**0 missing, 1 partially evidenced, 2 evidenced**) across 12 dimensions (24 max): task-fit; hierarchy; action clarity; specificity / anti-slop; efficiency; precision/provenance; error prevention; recovery; accessibility; adaptability; performance feedback; visual consistency.

- **Prototype gate:** >=18/24 with no 0 on task-fit, precision, or accessibility; provisional heuristic.
- **Release gate:** >=21/24 and no critical defect; thresholds are project policy, **not published scientific standards**.
- **Release blockers:** inaccessible core task; impossible recovery after interruption; misleading predicted-as-measured value; lost recipe draft; destructive action without confirmation; completely nonfunctional offline route; clipped core controls at tested form factors.
- Keep review outcomes in `docs/ux/reviews/YYYY-MM-DD-<screen>.md` with screenshot paths, accessibility logs, defects, owner, status and verified retest.

**Golden tests:** use Android's supported screenshot infrastructure for the installed AGP version; the standalone Compose Preview screenshot plugin guidance has changed around AGP 9.5.0-alpha03. Do not copy obsolete Gradle instructions without checking the current Android Developers page.

**Accessibility automation:** Compose `enableAccessibilityChecks()` is available in supported testing versions, but does not replace TalkBack/Switch Access manual verification.

**Performance:** use Macrobenchmark for startup/jank on actual target devices, and Baseline Profiles only when measured useful. Report measured latency rather than invented targets or success claims.

## 10. Core UX metrics and evaluation protocol

| Metric | How to measure | Acceptance approach |
|---|---|---|
| Time to first valid formulation | Recorded task duration | establish baseline with professional baristas then improve |
| Task success | Completed, correct, saved recipe | compare iterations, segment by experience |
| Unit-entry error rate | wrong unit, decimal, invalid value | zero silent unit conversions; track surfaced errors |
| Confidence comprehension | distinguish observation/calculation/estimate in short test | must be correct for safety-relevant flows |
| Model-less task success | full journey in airplane mode without LLM | 100% of documented manual tasks |
| Recovery after interruption | resume download / rediscover folder / restore draft | repeatable instrumentation and usability tests |
| Accessibility completion | TalkBack users complete priority flows | no severe blocker |
| Visual regressions | screenshot baseline diffs | every unapproved change investigated |

Do not claim a low pain-point UX until confirmed through task evidence. For small qualitative studies, emphasize observed issues over false statistical certainty. Follow-up experiments can use controlled A/B or within-subject comparisons when appropriate.

## 11. MCP, tools and skills for agentic UI/UX

**Use only integrations available and authorized in the development environment.** MCP tools are development infrastructure; Coffee Creator's shipped runtime remains 100% offline-first.

| Tool / integration | Use in pipeline | Guardrail |
|---|---|---|
| Android Studio agent/BYOA | edit/diagnose code, run project-aware actions | agent never certifies own visuals without artifacts |
| Android emulator + ADB | exercise gestures, screenshots, logs, orientation, font scale | prefer device matrix; do not rely on emulator alone |
| Compose Preview | render component states rapidly | preview != full integration run |
| Compose UI Test / Accessibility Test Framework | semantics, user actions, label/contrast checks | TalkBack manual verification still needed |
| Compose Preview Screenshot / AGP suites | golden screenshots | verify current AGP/plugin compatibility |
| Figma MCP (optional) | read actual design nodes, spacing, components | do not infer token intent from pixels alone |
| Playwright MCP | only web-based documentation/prototype review | not a substitute for Android native device testing |
| GitHub/Git MCP | PR reviews, ownership, ADR tracking | least privilege and trusted repositories |
| Context7/docs MCP (optional) | current library docs | validate against official Android docs and pinned versions |
| Gradle + Detekt/KtLint/Android Lint | static build gates | report exact task status, no invented success |
| Macrobenchmark / Baseline Profile tools | startup/jank evidence | benchmark realistic release builds |

**Suggested contextual skills:** `ux-research-synthesis`, `android-compose-design-system`, `compose-screen-contract`, `accessibility-audit`, `screenshot-visual-regression`, `barista-workflow-evaluation`. Each skill should specify trigger, inputs, procedure, output evidence and stop conditions; use progressive disclosure, not an enormous master prompt.

## 12. Implementation sequence for the next sprint

1. Freeze this document as UX contract; reconcile with `DESIGN.md` (the latter remains the token authority; this file adds anti-slop process and QA gates).
2. Write three realistic persona/task scenarios from **actual** barista interviews or label as unvalidated assumptions.
3. Prototype recipe input and calculation first, with error/recovery and manual-offline path.
4. Build shared numerical entry, provenance label, source citation and progress components.
5. Add chat and knowledge only after manual workbench passes its first field evaluation.
6. Add model manager and demonstrate Wi-Fi interruption and SAF reauthorization.
7. Capture golden screenshots, TalkBack findings and first UX decision log before expanding component inventory.

## 13. Final definition of done for an AI-designed screen

- [ ] Solves a documented barista task and states the user benefit.
- [ ] Matches information architecture and supported navigation.
- [ ] Uses existing semantic theme tokens; new tokens are justified.
- [ ] Presents accurate units, provenance and measured-vs-estimated states.
- [ ] Has empty/loading/error/interrupted/offline states.
- [ ] Works at compact and expanded widths, light/dark and large fonts.
- [ ] Has accessible labels, focus order, clear targets and TalkBack evidence.
- [ ] Passed actual Compose/Gradle build and relevant UI tests (or labeled unverified).
- [ ] Actual render was reviewed and screenshot differences triaged.
- [ ] Domain calculations stay outside composables.
- [ ] User research assumptions are marked and future validation is tracked.
- [ ] No gratuitous generic AI-generated dashboard decorations.

**End of document.**
