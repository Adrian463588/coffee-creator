# OpenSpec Proposal: Epic S2-E05 — Sensory Wheel and Professional Cupping

## Objective
Implement an offline-first, mathematically grounded sensory analysis and professional coffee cupping system. Features include an original hierarchical and searchable flavor wheel taxonomy, an accessible screen-reader table alternative, safe food-grade calibration reference guides, standard sensory scoring with epistemological certainty badges (Human vs AI Hypothesis vs Calibrated Model), cupping session management with blind sample masking, immutable audit logging, multi-evaluator consensus statistics, physical green/roasted QC, and roast-rest degassing lifecycle curves.

## Scope (Stories S2-051 through S2-062)
- **S2-051**: Hierarchical, searchable original flavor wheel/taxonomy with an accessible list/table alternative.
- **S2-052**: Descriptor definitions with safe food-grade calibration reference preparation exercises (Citric, Malic, Sucrose, Quinine) and food-safety boundaries.
- **S2-053**: Standard sensory sliders (Aroma, Acidity, Sweetness, Body, Bitterness, Aftertaste, Clean Cup, Balance, Overall on 1-10 or 6-10 Q-scale) with qualitative notes.
- **S2-054**: Epistemological distinction between: (1) Human Observation, (2) AI/Heuristic Hypothesis, and (3) Calibrated Model Prediction with confidence bounds.
- **S2-055**: Cupping sheet templates: Standard Specialty (100-pt), Quick Barista Dial-In, Production QC, and Espresso Cupping.
- **S2-056**: Cupping sessions with multiple samples and locally assigned evaluator identities.
- **S2-057**: Blind, non-blind, and double-blind masking with controlled reveal.
- **S2-058**: Session lifecycle state machine: DRAFT -> OPEN -> CLOSED -> REOPENED with immutable audit log.
- **S2-059**: Multi-evaluator consensus analytics: Mean, Median, Standard Deviation (spread / inter-rater agreement index), and Jaccard descriptor overlap.
- **S2-060**: Offline CSV and Markdown QC report generation with cryptographic SHA-256 fingerprint receipts.
- **S2-061**: Physical QC integration: Green coffee grading (moisture %, water activity, defects, screen size, density) and roasted Agtron color.
- **S2-062**: Roast-rest sensory lifecycle tracking: Multiple dated taste observations per bag over degassing curve and peak flavor window.

## Non-Goals
- No remote cloud sync or multi-device WebRTC syncing (single-device offline-first evaluation).
- No copyright infringement of proprietary SCA form layouts (uses licensed/original independent templates).
- No ungrounded flavor predictions from AI without explicit heuristic disclaimers.
