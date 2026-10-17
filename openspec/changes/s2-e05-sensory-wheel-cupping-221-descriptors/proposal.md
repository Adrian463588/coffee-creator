# OpenSpec Proposal: Complete 221-Descriptor Sensory Wheel & Professional Journal Cupping (Epic S2-E05)

## 1. Executive Summary
Deepen Epic S2-E05 (Sensory wheel and professional cupping) to full production specification:
1. **Exhaustive 221-Descriptor Dual-Standard Taxonomy**:
   - 9 Primary Families: Floral, Fruity, Sour/Fermented, Green/Vegetative, Other/Defects, Roasted, Spices, Nutty/Cocoa, Sweet.
   - 84 official SCA Flavor Wheel reference descriptors with bilingual labels (`en` and `id_label`).
   - 137 curated expanded descriptors: Indonesian tropical fruits (salak, manggis, durian, rambutan, sirsak, calamansi, etc.), temperate fruits, specialty flowers, sweets/desserts (gula aren, klepon, dodol), regional spices (pandan, sereh, kapulaga, jahe), nuts/grains, anaerobic fermentation notes, earthy/woody, and commercial off-notes / defects (potato defect, phenolic, rubber, etc.).
   - Explicit flag `isOfficialReference` distinguishing official wheel terminology from local/regional extensions.
2. **16 Cupping Attributes & CVA Descriptive Template**:
   - Support for 16 cupping attributes: Fragrance, Aroma, Flavor, Aftertaste, Acidity Intensity, Acidity Quality, Sweetness Intensity, Bitterness Intensity, Saltiness Intensity, Body Intensity, Body Texture, Astringency, Finish Duration, Temperature, Defects, Overall Impression.
   - Dynamic evaluation with multi-phase temperature observation timeline (Dry Fragrance, Wet Aroma, Crust Break, Hot Cupping ~70°C, Warm Cupping ~60°C, Cool Cupping ~40°C, Final Aftertaste).
3. **Full Sensory Wheel Integration with Brew & Journal Cupping**:
   - Direct interactive Flavor Wheel Picker Dialog with instant search, category filtering, and bilingual lookup.
   - 1-Click "Log Cupping Sample to Journal" action creating an authenticated `BrewJournalEntry` with sample bean details, score, and sensory descriptor notes.
   - Seamless editing of tasting notes on any journal brew entry using the complete 221-descriptor picker.
4. **Offline Trust & Cryptographic Provenance**:
   - Distinguish `HUMAN_OBSERVATION` vs `AI_HEURISTIC_HYPOTHESIS`.
   - Immutable audit logs for reopening sessions.
   - Cryptographic SHA-256 QC receipts and offline CSV/Markdown export.

## 2. Invariants & Epistemological Boundaries
- **No Uncalibrated Guesses in Consensus**: Official cupping consensus arithmetic strictly computes over empirical `HUMAN_OBSERVATION` scores.
- **Defect Deductions**: Specialty 100-pt protocol enforces standard deductions: Taint = -2 pts/cup, Fault = -4 pts/cup.
- **Accessible Dual-Path**: Every visual wheel segment has an equivalent, fully focusable, screen-reader-compliant accessible list item with minimum 48dp touch target.
- **Offline Guarantee**: All 221 descriptors and cupping math execute 100% locally with zero cloud or network dependencies.
