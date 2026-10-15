# Change Proposal: UI/UX Tactile Industrial & Precision Coffee Lab (Anti-Slop)

## Problem Statement
The current mobile UI exhibits two primary layout inefficiencies ("AI-slop"):
1. **Library / Workbench Category Navigation**: A staggered 3-row `FlowRow` of 6 category chips consumes excessive vertical viewport height (~150dp), causing cognitive friction and visual clutter.
2. **Brew Chronometer & Scale Console**: The active brewing timer splits elapsed time, scale readings, and control buttons across disconnected cards, creating awkward whitespace and button clustering.

## Proposed Solution
Refine both areas using minimal-diff Jetpack Compose enhancements conforming to the 2026 Tactile Industrial & Precision Coffee Lab design system:
1. **Horizontal Scrollable Filter Bar**: Replace staggered `FlowRow` with a single-line horizontally scrollable row featuring dedicated iconography, animated selection states, and tactile micro-press feedback (`0.96f` scale).
2. **Unified Precision Laboratory Console**: Combine the digital timer, live mass/flow-rate telemetry, BLE scale actions, and execution controls into an integrated instrument cluster card with a breathing live pulse indicator (`LivePulseIndicator`) and full-width 54dp primary CTA with tactile feedback.

## User Impact
- Preserves 100% of existing functionality, ViewModel bindings, and automated UI test tags.
- Reduces vertical clutter by ~60% on the Library screen.
- Elevates the Brew Timer to professional laboratory standards with clear tactile responsiveness.
