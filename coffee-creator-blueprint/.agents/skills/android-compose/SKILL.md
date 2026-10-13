---
name: android-compose
description: Use when implementing Kotlin/Compose UI, ViewModels, accessibility and lifecycle behavior.
---

# Android Compose Delivery

## Preconditions
Read `AGENTS.md` and the directly related PRD/architecture/design contracts. Only load other reference files when the task requires them.

## Required workflow
Read DESIGN.md, PRD screen flow and architecture. Specify UiState/Intent and state matrix. Implement previews, semantic labels and 48dp targets. Validate adaptive phone/tablet, text scaling and state restoration. Execute lint/build/Compose UI tests; inspect real emulator screenshot when available. Output exact evidence and missing checks.

## Completion report
- Scope/requirement
- Files changed
- Checks executed (PASS/FAIL/NOT RUN)
- Offline/privacy/a11y/science risks
- Blockers or ADR needed
