# Provenance-safe user records

## Problem

Coffee Creator currently initializes user-facing journal, sensory, and inventory state with plausible but unobserved values. Brew completion also writes synthetic sensory scores, TDS, beverage mass, ratings, and equipment details. OCR and manual catalog entry fill missing package facts with defaults. These records can be exported or used by recommendations as if they were measurements.

## Change

Keep curated reference material available only through an explicit import action. A new install starts with empty personal history and inventory. Completing a timer records only event facts that were observed or supplied by the recipe, keeps unmeasured fields unknown, and never creates sensory evaluations. OCR preserves only parsed values and reports no calibrated confidence. Manual catalog entry creates no purchased bag unless package mass is provided. AI tools fail closed when required evidence is absent, and unknown ingredient safety is not treated as safe.

This change does not delete or rewrite existing user records, backups, SAF assets, or app databases.

## Acceptance criteria

- A fresh local profile has no fabricated sensory session, inventory, cafe visit, brew journal, or recipe history.
- Finishing a brew session does not create a human sensory observation, rating, measured TDS, beverage mass, dose actual, espresso yield actual, or puck-preparation fact unless that fact was independently recorded.
- OCR returns `null` for fields absent from recognized text and never invents confidence scores or a package size.
- Manual catalog entry preserves unknown attributes and creates a bag only when package mass is explicitly supplied and valid.
- Safety audit rejects ingredients with unknown or absent food-grade evidence.
- Inventory recommendation and recipe adjustment do not synthesize missing profiles, measurements, dates, or recipe inputs.
- Existing saved data remains untouched; curated import remains explicit and labeled.

## Out of scope

This change does not claim physical Bluetooth scale support, calibrated OCR confidence, calibrated sensory prediction, or adjudicated RAG answer quality.
