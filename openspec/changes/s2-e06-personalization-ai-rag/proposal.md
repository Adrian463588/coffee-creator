# OpenSpec Proposal: Epic S2-E06 — Personalization, Local AI, and RAG Integration

## Objective
Implement an offline-first, mathematically grounded coffee personalization and local AI assistant engine. Features include a 5-question offline personal palate quiz, Bayesian exponential moving average taste profile updates from verified sensory observations, explainable bean and recipe inventory recommendations, single-variable brewing feedback adjustments, camera/OCR bean metadata parsing with explicit review gates, grounded on-device barista chat with strict no-evidence abstention, tool-driven recipe drafting with schema validation, customizable barista personas with prompt injection sanitization, user-supplied offline weather/altitude boiling point thermodynamics, and strict food-grade ingredient/allergen safety gates.

## Scope (Stories S2-063 through S2-074)
- **S2-063**: Offline personal palate quiz (5 questions) + editable preference profile persisted in local encrypted settings.
- **S2-064**: Personal taste profile computed dynamically from logged sensory evidence using Bayesian exponential moving average (rating >= 7.5 threshold).
- **S2-065**: Explainable local bean/method/recipe recommendations from available inventory (cosine similarity * degassing freshness * gear availability).
- **S2-066**: Brewing feedback loop that proposes a single controlled recipe adjustment (OVAT - One-Variable-At-A-Time) with physical extraction diagnosis.
- **S2-067**: Camera/OCR to draft bean/equipment metadata with explicit user corrections and review gate.
- **S2-068**: Grounded local AI barista chat for extraction, water, gear, and techniques with allowlisted tools.
- **S2-069**: Tool-driven generation of typed recipe draft based on beans/gear/constraints.
- **S2-070**: All tool arguments schema validated; math executed by pure domain engine; write operations require explicit user confirmation.
- **S2-071**: RAG answers cite local uploaded document page/chunk; strict no-evidence abstention when ungrounded.
- **S2-072**: Local barista persona presets (Specialty Q-Grader, Friendly Barista, R&D Chemist) + safe custom system prompt controls.
- **S2-073**: Optional weather and altitude inputs are user-supplied (zero network/weather APIs); calculates altitude-adjusted boiling point $T_{\text{boil}} = 100 - (h/300)^\circ\text{C}$ and clamps brew water temperature.
- **S2-074**: Ingredient/allergen warnings and food-grade provenance; rejects non-food perfumery chemicals with safety boundaries.

## Non-Goals
- No remote cloud AI or external telemetry APIs.
- No automatic silent database writes by LLM without explicit user confirmation.
- No live GPS or third-party weather API calls.
- No speculative, uncalibrated flavor predictions presented as empirical facts.
