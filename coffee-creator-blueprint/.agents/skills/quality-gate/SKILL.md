---
name: quality-gate
description: Use before integrating changes, shipping previews, or declaring a feature complete.
---

# Android Release Quality Gate

## Preconditions
Read `AGENTS.md` and the directly related PRD/architecture/design contracts. Only load other reference files when the task requires them.

## Required workflow
Inspect scope, requirements and changes. Run available Gradle lint, detekt, ktlint, testDebugUnitTest, assembleDebug, connected tests and performance checks where environment supports. Review privacy/network permissions, native library ABI, model licensing, SAF source access, prompt injections and calculation golden fixtures. State PASS/FAIL/NOT RUN with exact commands and artifact evidence. Never invent test success.

## Completion report
- Scope/requirement
- Files changed
- Checks executed (PASS/FAIL/NOT RUN)
- Offline/privacy/a11y/science risks
- Blockers or ADR needed
