# OpenSpec Proposal: Sprint 04 — Professional Sensory, Cupping, CVA & Green Coffee Assessment

## Context & Objectives
Implement an additive, offline-first professional coffee sensory, cupping, training, and green coffee assessment module in Coffee Creator. 
Strictly aligns with:
- **SCA Coffee Value Assessment (CVA)** as modern default standard (Physical, Descriptive, Affective, Extrinsic).
- **Historic 2004 SCA Cupping Form** preserved exclusively as a labeled **LEGACY** option with zero cross-protocol score conversion.
- **Learning Academy**: 22 modular lessons (EDU-01 to EDU-22) with learning objectives, bilingual EN/ID content, in-house ASCII diagrams, citations, glossary, practice, and accessibility notes.
- **Scientific Corrections**: Teach all **five** recognized basic tastes (Sweet, Sour, Salty, Bitter, Umami). Clarify that "acid" is not a sixth taste; sourness is a gustatory taste while perceived acidity is a multimodal perception.
- **Sensory Training Lab**: 6-permutation balanced Triangle testing with concealed keys and exact cumulative Binomial probability calculation $P(X \ge k) = \sum_{i=k}^n \binom{n}{i} (1/3)^i (2/3)^{n-i}$, basic taste reference solutions, and roast defect recognition.
- **Green Coffee Physical Assessment**: 350g sample defect tally for SCA Washed Arabica 2004 reference (Category 1 primary defects vs Category 2 secondary defects), screen size sieve distribution, moisture % with uncertainty, and out-of-scope guards for Canephora/Natural.
- **Tactile UI/UX**: Anti-AI Slop, 1px hairline borders (`alpha = 0.40f`), concentric corner geometry $R_{\text{inner}} = \max(0, R_{\text{outer}} - \text{Padding})$, single-line bounds, and tactile scale compression (`0.97f`).

## Non-Goals & Invariants
- NO cloud APIs or remote inference. 100% offline-first.
- NO false claims of official SCA, Q-Grader, or R-Grader certification. App provides practice tools only.
- NO destructive database migration. Sprint 01-03 recipes, grinders, drippers, and logs remain 100% intact.
- NO copyrighted proprietary artwork or Le Nez du Café 60 licensed text. Kit companion uses generic taxonomy + user-owned vial IDs.
