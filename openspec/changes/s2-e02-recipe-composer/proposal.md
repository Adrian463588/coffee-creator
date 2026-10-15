# Proposal: Epic S2-E02 — Recipe Composer and Deterministic Brewing Science

## Motivation
Specialty coffee formulation requires exact, deterministic physical calculations, customizable multi-step pouring routines, precise scaling without rounding loss, optical refractometer conversions, and offline recipe portability. While basic recipe saving and direct EY calculation exist, baristas need a comprehensive recipe composer supporting standard and custom brew methods, editable ordered steps (action, time, pour target, temperature, valve), exact-sum ratio scaling, Brix-to-TDS instrument profiles, concentrate mass-balance dilution, safe food-grade aroma dosing, expert brew dial-in metadata, and zero-cloud sharing via Android Sharesheet.

## Scope (S2-013 through S2-026)
- **S2-013**: Recipe create, edit, duplicate, archive, versioning, and linking beans/equipment.
- **S2-014**: Presets for Espresso, V60, AeroPress, Chemex, French press, Cold brew, Immersion, Batch brew, Moka pot, Siphon, Cupping.
- **S2-015**: User-defined custom brew methods and extraction stages.
- **S2-016**: Ordered steps: action, start/relative time, target water, cumulative mass, temperature, valve status.
- **S2-017**: Ratio and dose scaling with Hamilton-Hare exact-sum apportionment and monotonicity preservation.
- **S2-018**: Typed physical quantities (mg/g/mL/°C/°F) and density-dependent volume-to-mass conversions.
- **S2-019**: Direct mass-balance extraction yield (EY) and TDS with explicit measured vs estimated flags.
- **S2-020**: Optical Brix-to-TDS conversion with documented calibration profiles (Standard Coffee 0.85, Atago PAL-COFFEE 0.84, VST LAB Coffee III 0.857).
- **S2-021**: Concentrate mass-balance dilution and solution dosing calculations.
- **S2-022**: Food-grade aroma concentrate dosing with stock dosage rate vs active aromatic compound (ppm) distinction and supplier limits.
- **S2-023**: Safe parameter boundaries (temperature 0-100°C, dose 1-500g, water 10-10,000g, pressure 0-15 bar) and sensory target constraints.
- **S2-024**: Curated source-attributed starter recipes for major brewing methods.
- **S2-025**: Expert dial-in mode (first drop time, grinder model & setting, water GH/KH mineral ppm, puck prep).
- **S2-026**: Zero-cloud recipe export (JSON & formatted text) and Android Sharesheet integration.

## Non-Goals
- Cloud synchronization or remote recipe databases.
- Automated Bluetooth scale/hardware streaming (deferred to Epic S2-E03).
- Subjective AI flavor predictions presented as measured facts.
