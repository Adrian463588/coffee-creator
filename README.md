# ☕ Coffee Creator

[![Platform](https://img.shields.io/badge/Platform-Android%2026%2B-green.svg)](https://developer.android.com)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose%20M3-brown.svg)](https://developer.android.com/jetpack/compose)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin%202.0-blue.svg)](https://kotlinlang.org)
[![Offline First](https://img.shields.io/badge/Privacy-100%25%20Offline-orange.svg)](#privacy--offline-architecture)
[![License](https://img.shields.io/badge/License-Apache%202.0-lightgrey.svg)](LICENSE)

> **Coffee Creator** is a privacy-preserving, 100% offline-capable professional barista workstation built natively for Android using Jetpack Compose and Material 3. Designed for head baristas, coffee R&D chemists, roasters, and trainers.

---

## 📥 Download Application

Get the latest installable Android APK directly:

| Version | Release Date | Direct APK Download | Release Notes | Package Hash (SHA-256) |
| :--- | :--- | :--- | :--- | :--- |
| **v1.0.0 (Latest)** | 2026-10-13 | [📥 Download coffee-creator-v1.0.0.apk](https://github.com/Adrian463588/coffee-creator/releases/download/v1.0.0/coffee-creator-v1.0.0.apk) | [GitHub Release v1.0.0](https://github.com/Adrian463588/coffee-creator/releases/tag/v1.0.0) | `2843362FF29DD536DA78F4263D5E2B66D1A98482C47C1376DB037E3439CA02FF` |

*(All historical versions preserved in [GitHub Releases](https://github.com/Adrian463588/coffee-creator/releases)).*

---

## 📜 Version History & Changelog

### [v1.0.0] - 2026-10-13 (Production Baseline)
- **Manual Formulation & Science Engine (FR-02, FR-03)**:
  - Pure Kotlin deterministic calculation engine with `BigDecimal` arithmetic.
  - Golden test verified: **18.0g dose**, **270.0g beverage mass**, **1.30% measured TDS** $\implies$ exactly **19.50% Extraction Yield**.
  - Brew ratio calculations, mass-fraction weighted dilution, concentration (ppm and % w/w), volume-to-mass conversions with density check.
  - Interactive multi-step brew guide & timer with schedule breakdown, live seconds counter, pause/reset, bypass water, and agitation routine.
- **Coffee Catalog & Blend Management (FR-01)**:
  - Curated single origin lots + House Blend formulation (Colombia Castillo 60% + Ethiopia Chelchele 40% summing to 100%).
  - Degas days, roast date tracking, density, and allergen food-grade compliance.
  - Deterministic blend ratio validation (`validateBlendRatios`).
- **Sensory Evaluation & Radar Polygon Chart (FR-04)**:
  - 8 cupping attributes (Aroma, Acidity, Sweetness, Bitterness, Body, Clarity, Finish, Balance) on 1-10 scale.
  - Interactive Jetpack Compose Canvas Radar/Spider chart comparing empirical observations with directional heuristics.
  - Explicit uncalibrated disclaimer on all model-generated estimates.
- **Local On-Device AI Chat & 9 Harmonized Typed Tools (FR-05, FR-07)**:
  - Persona selector (Head Barista, Coffee R&D, Sensory Evaluator, Coffee Trainer).
  - Exact 9 allowlisted typed Kotlin tools: `calculate_extraction_yield`, `calculate_brew_ratio`, `calculate_dilution`, `calculate_concentration_ppm`, `convert_volume_to_mass`, `search_knowledge`, `estimate_sensory_profile`, `get_coffee_details`, `save_recipe`.
  - Confirmed write gate for `save_recipe` requiring explicit user card approval.
- **Offline Document Hybrid RAG (FR-06)**:
  - Local hybrid lexical and chunk retrieval over SCA brewing manuals and water quality standards.
  - Grounded source citations (`[Title, Page, Chunk]`) with strict abstention when evidence is missing.
- **SAF Asset Manager & Backups (FR-08, FR-09, FR-10)**:
  - User-owned models via Android Storage Access Framework (SAF). Resumable downloads, checksum validation.
  - JSON backup manifest export and restore with integrity validation.
  - 100% offline, zero telemetry, zero background network.

---

## 🏛️ Architecture & Spec-Driven Development (OpenSpec / BMAD)

Coffee Creator is built following **OpenSpec Spec-Driven Development (SDD)** and **Behavior-Driven Development (BDD)**.

```
openspec/specs/01-coffee-creator-core.md
├── Invariants (100% Offline, Deterministic Math, Least Privilege SAF)
├── BDD Given-When-Then Scenarios (FR-01 through FR-10)
└── Verification Gates (Unit Tests, Golden Math Suite, assembleDebug)
```

### Module Structure
- `com.coffeecreator.app`:
  - `core.model`: Pure domain value objects (`CoffeeBean`, `Recipe`, `CalculationResult`, `SensoryObservation`, `KnowledgeDocument`, `ModelAsset`).
  - `core.engine`: Deterministic `CoffeeScienceEngine` and `SensoryRulesEngine`.
  - `core.data`: Reactive `DataRepository` and `SeedData`.
  - `core.ai`: `ToolExecutor`, `HybridRagEngine`, `LocalLlmCoordinator`.
  - `ui.theme`: Material 3 Coffee Theme (Espresso `#4E342E`, Crema `#8D6E63`, Caramel `#C78D4E`).
  - `ui.screens`: 9 core screen implementations (`HomeScreen`, `RecipeBuilderScreen`, `CoffeeLibraryScreen`, `SensoryScreen`, `AiChatScreen`, `KnowledgeLibraryScreen`, `ModelManagerScreen`, `BrewJournalScreen`, `SettingsScreen`).
  - `ui.navigation`: Adaptive Navigation Bar and Top App Bar.

---

## 🧮 Coffee Science Formulations

1. **Extraction Yield (%)**:
   $$\text{Extraction Yield (\%)} = \frac{\text{Beverage Mass (g)} \times \text{Measured TDS (\%)}}{\text{Dose (g)}}$$
2. **Brew Ratio**:
   $$\text{Ratio} = 1 : \left(\frac{\text{Total Water Mass (g)}}{\text{Coffee Dose (g)}}\right)$$
3. **Mass-Fraction Weighted Dilution**:
   $$M_{\text{final}} = M_{\text{initial}} + M_{\text{water}}, \quad \text{TDS}_{\text{final}} = \frac{M_{\text{initial}} \times \text{TDS}_{\text{initial}}}{M_{\text{final}}}$$
4. **Concentration**:
   $$\text{ppm} = \left(\frac{\text{Solute Mass}}{\text{Solution Mass}}\right) \times 10^6 = (\% \text{ w/w}) \times 10{,}000$$

---

## 🔒 Privacy & Offline Guarantee

- **Zero Remote Inference**: Model execution runs exclusively on-device.
- **Zero Telemetry**: No third-party analytics or tracker SDKs.
- **Controlled Network Scope**: Network permission is reserved solely for user-initiated GGUF model asset downloads.
- **Storage Ownership**: User assets live in user-selected directories via Android SAF.

---

## 🛠️ Build & Verification

```bash
# Run 100% of unit tests and golden science calculations
./gradlew testDebugUnitTest

# Build debug APK
./gradlew assembleDebug
```
