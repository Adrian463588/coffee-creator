# Specification and behavior-driven delivery

Primary workflow references reviewed 2026-10-15:
[OpenSpec](https://github.com/Fission-AI/OpenSpec) and
[BMad Method](https://github.com/bmad-code-org/BMAD-METHOD).

Coffee Creator applies their spec-first, role-separated workflow through the
local blueprint, proposal/design/tasks artifacts, scoped worktree plans,
Given/When/Then tests, independent agents and one integrator. No claim is made
that a framework CLI was installed or its full generated workflow executed.
These are development methods, not app runtime dependencies.

1. PRD and AGENTS define invariants; DESIGN defines interaction constraints.
2. IMPLEMENTATION_PLAN.md and UX_DATA_PLAN.md define scope before application code.
3. Isolated agents own domain, persistence, native runtime, UI, data and evaluation
   slices. The integrator reviews and cherry-picks scoped conventional commits.
4. Deterministic regression tests preserve quantity units, measurement provenance,
   transactional recovery, literal FTS, source integrity and tool restrictions.
5. Static analysis, JVM tests, Android builds, local Android/Compose execution,
   physical-device runs, RAG evaluation and distribution are separate gates.
6. VERIFICATION.md reports executed outcomes and outstanding acceptance; an
   unchecked requirement remains unchecked despite a successful APK build.

RTK, Ponytail and Caveman follow the contributor's installed authoring guidance.
Only the existing RTK executable is used as a development command proxy. None is
added to Android dependencies or represented as a missing runtime component.
