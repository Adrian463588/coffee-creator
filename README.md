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

| Version | Release Date | Download Link | Package Hash (SHA-256) |
| :--- | :--- | :--- | :--- |
| **v1.0.0 (Latest)** | 2026-10-13 | [Download v1.0.0 APK](./release/coffee-creator-v1.0.0.apk) | `78639EEAACB73D6EEF6295EF2FA34C39677187D02979E80F9B0768248E36FD81` |

*(Also available under [GitHub Releases](../../releases)).*

---

## 📜 Version History & Changelog

### [v1.0.0] - 2026-10-13 (Baseline Release)
- **Manual Formulation & Science Engine (FR-02, FR-03)**:
  - Pure Kotlin deterministic calculation engine with `BigDecimal` arithmetic.
  - Golden test verified: **18.0g dose**, **270.0g beverage mass**, **1.30% measured TDS** $\implies$ exactly **19.50% Extraction Yield**.
  - Brew ratio calculations, mass-fraction weighted dilution, concentration (ppm and % w/w), volume-to-mass conversions with density check.
- **Coffee Catalog & Provenance (FR-01)**:
  - Curated seed lots (Ethiopia Yirgacheffe Chelchele, Colombia Huila Pitalito, Kenya Nyeri Hill SL28, Costa Rica Tarrazú, Indonesia Sumatra Gayo).
  - Custom bean creation with origin, altitude, variety, process, and roast level metadata. Missing data tracked as `unknown`.
- **Sensory Evaluation & Overlay (FR-04)**:
  - 10-point scale cupping sliders across Aroma, Acidity, Sweetness, Body, Finish, Balance.
  - Directional rule-based heuristic estimation overlay with explicit uncalibrated disclaimer.
- **Local On-Device AI Chat & Tool Router (FR-05, FR-07)**:
  - Persona selector (Head Barista, Coffee R&D, Sensory Evaluator, Coffee Trainer).
  - Allowlisted registry of 9 typed tools + confirmed write gate for `save_recipe`.
- **Offline Document RAG (FR-06)**:
  - Local hybrid lexical and chunk retrieval over SCA brewing manuals and water quality standards.
  - Grounded source citations (`[Title, Page, Chunk]`) with strict abstention when evidence is missing.
- **SAF Asset Manager & Backups (FR-08, FR-09, FR-10)**:
  - User-owned models via Android Storage Access Framework (SAF). Resumable downloads, checksum validation.
  - JSON backup manifest export and restore with integrity validation.

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
