# OpenSpec Proposal: Epic S2-E05 Deepening — Full Sensory Wheel and Professional Cupping

## 1. Executive Summary
Deepen and complete Epic S2-E05 (Sensory wheel and professional cupping) by:
1. Expanding `FlavorWheelTaxonomy.kt` to an exhaustive, dual-standard taxonomy (SCA / WCR Lexicon + Indonesian Roda Rasa Kopi): 9 primary categories, 26 subcategories, 90+ descriptors with TalkBack accessibility labels and full defect/taint markers (PTD, Phenolic, Baggy, Stinker).
2. Implementing an interactive, hardware-accelerated Compose Canvas Flavor Wheel Visualizer (`SensoryWheelVisualizer.kt`) with multi-ring concentric sunburst geometry, smooth touch-sector hit testing, category/subcategory focus, and high-contrast color tokens.
3. Implementing an interactive **Wheel Sensory Selector / Descriptor Picker Modal** integrated seamlessly into:
   - **Cupping Evaluation Scoring Dialog**: Evaluators tap on Wheel categories and chips to pick flavor descriptors rather than typing comma-separated text.
   - **Brew Journal**: Baristas pick sensory notes directly from the hierarchical wheel taxonomy.
4. Implementing **Dynamic Attribute Sliders in Cupping Evaluation Sheets**: Respecting the chosen `CuppingTemplate` (`SPECIALTY_SCA_EQUIVALENT` 10 attributes 6-10 scale; `QUICK_BARISTA_DIAL_IN` 6 attributes 1-10 scale; `PRODUCTION_QC` 4 attributes 1-10 scale; `ESPRESSO_SENSORY` 6 espresso attributes 1-10 scale).
5. Ensuring 100% offline, zero-cloud operations with cryptographic SHA-256 QC receipts and CSV/Markdown export.

## 2. Invariants & Epistemological Boundaries
- **Ephemeral AI vs Physical Reality**: Official cupping consensus scores strictly aggregate `HUMAN_OBSERVATION`. Uncalibrated AI/heuristic predictions are tagged with `[AI HEURISTIC]` badges and never mixed into arithmetic mean/median consensus.
- **Defect Penalties**: Specialty 100-pt protocol enforces standard deductions: Taint = -2 pts/cup, Fault = -4 pts/cup.
- **Accessible Dual-Path**: Every visual wheel segment has an equivalent, fully focusable, screen-reader-compliant accessible list item with minimum 48dp touch target.
