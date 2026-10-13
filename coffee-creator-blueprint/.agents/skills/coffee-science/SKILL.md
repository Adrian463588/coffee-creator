---
name: coffee-science
description: Use for recipe math, unit conversion, extraction, TDS, dilution and ingredient formulation.
---

# Coffee Science Verification

## Preconditions
Read `AGENTS.md` and the directly related PRD/architecture/design contracts. Only load other reference files when the task requires them.

## Required workflow
Start with source equation, measurement definitions, typed units and domain assumptions. Create golden/boundary/property tests before implementation. Do not infer measured TDS or deterministic wheel attributes. Track food-grade aromatic limits and uncertain supplier metadata. Keep Kotlin JVM pure. Report result provenance, precision and validation evidence.

## Completion report
- Scope/requirement
- Files changed
- Checks executed (PASS/FAIL/NOT RUN)
- Offline/privacy/a11y/science risks
- Blockers or ADR needed
