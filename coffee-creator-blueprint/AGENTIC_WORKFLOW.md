# Coffee Creator — Agentic SDLC Operating Model (October 2026)

## Decision
Use **Spec-Driven Development + Contract-First Vertical Slices + Test-Driven Development + Independent Verification**. Optional lightweight BMAD/OpenSpec conventions may help structure discovery and change proposals, but neither is proof of code correctness. Maximize unattended execution for reversible tasks; keep policy/signing/data destruction/license decisions gated. One orchestrator; specialized agents as scoped roles, not unrestricted autonomous teammates.

## Evidence hierarchy
1. Product requirement and user-accepted spec.
2. Instrumented test / executed tool output / measurements.
3. Code and reproducible local documentation.
4. Official platform docs and pinned dependency APIs.
5. Third-party examples (verify licenses and freshness).
6. LLM assertions, which alone are not evidence.

## Loop: SPEC -> CONTRACT -> TEST -> IMPLEMENT -> VERIFY -> REVIEW -> INTEGRATE
1. **Spec:** Planner selects one PRD requirement and defines goal, non-goals and error states.
2. **Contract:** Architect describes interfaces, invariants, data flow, licenses, rollback and device constraints.
3. **Test:** QA/science agent prepares golden and adversarial tests; UI agent prepares screen-state matrix and journeys.
4. **Implement:** Android / AI developer works only in scoped files or isolated worktree.
5. **Verify:** Run formatting, lint, tests, emulator UI, screenshot/semantics inspection, offline checks.
6. **Review:** Independent reviewer checks correctness, overengineering, generated-code smells, unit physics, security, license and perf.
7. **Integrate:** Merge after green gates and write decision/status; failures become next focused tickets, not an unbounded loop.

## Agent delegation table
| Agent | Input | Output | Completion evidence |
|---|---|---|---|
| Orchestrator/PM | PRD, roadmap | Task plan, milestones | Acceptance mapping |
| Android Architect | Contracts, code graph | ADRs/interfaces | Dependency check |
| Compose Designer | DESIGN, journey | Component specs/previews | Screenshots + semantics |
| Android Implementer | Scoped ticket | Kotlin code/tests | Lint/build/tests |
| On-device AI engineer | RAG/model interfaces | Native/ONNX adapters and tests | Physical inference/evals |
| Coffee Science engineer | Equations/source evidence | Pure math and golden fixtures | Reference parity |
| QA/Security reviewer | Diff + failure matrix | Defects and blocker verdict | Run artifacts |
| Integrator | Reviewed worktrees | Conflict-free main | CI green |

## Parallel work without collisions
- Separate Git worktrees for independent slices and limit file ownership. Shared API contracts agreed before coding.
- Example safe parallelism: :engine calculation tests, :feature design previews, :ai model spike, :core filesystem provider tests.
- Avoid parallel edits to `libs.versions.toml`, root Gradle, same Room migration or a common contract file. Single integrator owns these.
- Agents must record tests and blocker conditions in scoped task note. No fabricated status and no automatic force push.

## Autonomous execution boundary
Auto-approve read/search, isolated source edits in worktree, test execution and disposable emulators. Require approval for destructive directory operations, license-changing dependencies, access to private credentials, data exports, model publishing, production release/signing or irreversible DB migrations. No agent can loosen offline/privacy constraints to pass tests.

## UI/UX agentic methodology
RESEARCH (persona/context & device) -> MAP (user jobs, wireflow, failures) -> SPECIFY (tokens/semantic components) -> PROTOTYPE (Compose Preview) -> IMPLEMENT (UDF) -> INSPECT (emulator screenshots, hierarchy, accessibility) -> TEST (journey/navigation/rotation/font scale) -> REFINE (measured defects) -> FREEZE (accepted design contract).

## Recommended tasks for tools
- Android Studio BYOA / Gemini agent: project diagnosis, emulator interaction, Compose preview, UI journey.
- Codex or Claude Code: multi-file implementation, tests, constrained refactor, code review.
- Gradle / Android Lint / Detekt / ktlint: actual gates.
- adb / emulator / Maestro: deterministic device execution and smoke journeys.
- Paparazzi or Roborazzi: screenshot regressions after compatibility verification.
- Macrobenchmark / Baseline Profiles: measure startup/scroll under realistic builds.
- Figma MCP: design tokens/assets as read-only input; never override accessibility.
- Context7 or official docs MCP: documentation lookup while developing, not bundled runtime.
- GitHub MCP: repository references/issues/PR workflows, least privilege.

## Example task contract
```
PRD: FR-03
Goal: extraction yield calculation pure Kotlin
Inputs: dose mass, beverage mass, measured TDS% (explicit units)
Output: BigDecimal percentage, method label, assumptions
Tests: 18g/270g/1.3% => 19.50%; zero dose rejected; negative inputs rejected; repeatability
Allowed edits: engine/* and engine tests only
Blocked: UI, schema migrations, dependencies
Exit gate: tests + review; document actual command results
```

## Stop conditions
Stop and report blocker rather than loop endlessly when: source is ambiguous, license is unknown, compatible runtime cannot be verified, no physical device is available for a device-only claim, sensitive/deletion decision arises, or the same failure recurs after bounded diagnosis. Partial verified progress is better than fabricated green status.
