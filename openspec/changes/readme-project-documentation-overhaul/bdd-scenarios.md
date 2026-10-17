# BDD Scenarios: README Project Documentation Overhaul

## Metadata
- **Change ID:** `readme-project-documentation-overhaul`
- **Specification:** Behavior-Driven Development (BDD) Acceptance Criteria
- **Syntax:** Standard Gherkin Syntax
- **Execution Style:** Caveman Terse / Verified Deterministic

## Feature: README Documentation Hub Verification

### Background:
  Given the Coffee Creator codebase repository root
  And the authoritative blueprint at `coffee-creator-blueprint/`
  And the release checksums at `SHA256SUMS.txt`
  And the Gradle build files `app/build.gradle.kts` and `gradle/libs.versions.toml`

### Scenario: Prominent v1.12.2 Download Callout and Exact Artifact Verification
  Given a barista or developer visits the GitHub repository README.md
  When reading the "Download & Quick Start" section
  Then the latest preview version is explicitly identified as `v1.12.2`
  And the direct download URL links to `https://github.com/Adrian463588/coffee-creator/releases/download/v1.12.2/coffee-creator-v1.12.2.apk`
  And the documented APK file size is `122 MB` (`122,249,625 bytes`)
  And the documented SHA-256 hash is `f7358795ea6ee155c030cc09d4c8a7bd7fbede06a12f7c533841fd3bc775c2a0`
  And a link to release notes `docs/releases/v1.12.2.md` is provided
  And a link to checksum file `SHA256SUMS.txt` is provided

### Scenario: Zero-Data-Loss ADB Installation Command Protocol
  Given a developer installing or updating the APK via Android Debug Bridge (ADB)
  When reviewing the installation instructions in README.md
  Then the primary installation command specifies `adb install --user 0 -r -d coffee-creator-v1.12.2.apk`
  And the documentation explicitly warns that `-r` replaces existing application while preserving data
  And the documentation explains that `-d` permits version code downgrade/reinstall without database wiping
  And the documentation warns that uninstalling the app revokes SAF storage grants and purges app-private databases

### Scenario: Complete Historical Versions Table Integrity (v1.0.0 through v1.12.2)
  Given the historical versions table in README.md
  When auditing all release entries
  Then every released version from `v1.0.0` through `v1.12.2` is represented in reverse chronological order
  And each row contains the version tag, release date, download link, release notes link, and key release highlights
  And all documented SHA-256 checksums match repository records:
    | Version | Expected SHA-256 Checksum Prefix |
    | v1.12.2 | f7358795ea6ee155c030cc09d4c8a7bd7fbede06a12f7c533841fd3bc775c2a0 |
    | v1.12.1 | 0b381b2367476201f4e1749af8fc8fbcc273b3f2ee107945331ca19eda392da0 |
    | v1.12.0 | 727e73b709fa594ccb7c65f1a27890e20c55ae622c27ae179a5e0c86ca8419b8 |
    | v1.11.0 | c63f74fcf3bc3461b24a6cdec9f4ac2b378b815c57b8449c05963852ccaa6f61 |
    | v1.10.2 | b5539ad55f333585dbdf76c1811ccfd19cd6399c10c6d325b080b4836d721f55 |
    | v1.10.1 | a8cfc5fccb4ab081f6d95d33f6b3ce94e030341927e3f6719ff221c617be0a42 |
    | v1.10.0 | d06e9c6bace080fb4e24cf34dfe66af21817e6fd2a18e9cd811c03f4c478fbc4 |
    | v1.9.6  | d3d28471fdf73702d7b9016776552bbfc3ce72989c5cb8552c9d0e207e352c6f |
    | v1.9.5  | d84569a5035fa1d9f0aaf7a7f697dd8d073bd72f90ecb4e15ed75474a58129a4 |
    | v1.9.4  | d5a0a24c3d4b2822fb5a06db421128e47bed6ee18e5a61e75836d5bd51de368f |
    | v1.9.3  | 04634335007971c79a219d7c77193cb111276f56f23820eeaefb76bfca812ebf |
    | v1.9.2  | ab068dea442fafbf44939cc96730387be5b1862e345d4c30733c41ed1aaea648 |
    | v1.9.1  | 917f9cdbab661c5d991a105047f6cff93aa64d37b8c0a06be826c380152bb921 |
    | v1.9.0  | 4801d10dbc143dfa8e7d0f47b252ecf955e571c0e6219ecc4a9e3cb8ebe5ab35 |
    | v1.8.0  | 1DC681387D6DE0E9EEF0B8F821B17B2E22269B2A8F4D3275E8C4594468037275 |
    | v1.7.0  | 1CB40B36E6DCB734B60656F1E3539AB8BE34722283C85B08B0EEB3FD54A1C9B7 |
    | v1.6.0  | 9b2060e67ff69d29aa90468dfd4b00d92f63f78778524f5e6961502764c4958b |
    | v1.5.0  | 99a32c636b467063d451e1a8b0a5dae5f8431ad5a648d3fc126816bc963bfc6f |
    | v1.4.0  | def99509dadd541a93ae5dfdd81085d1f7af7902ddb92ade38b07895f2bb7712 |
    | v1.3.0  | 78ca7128c350c40ab5ff3b4d8148ed09bc7a1a68a0212fc753896f4b91bccc89 |
    | v1.2.0  | 4c197fcee278508ee62ba57b3a8ac6524186c9f56ebcc07bce58914335bdf2eb |
    | v1.1.0  | e25474341ca5df7905118bfdd7f3e9a6e747b0b7a877e4a8817e0e6b279bcca8 |
    | v1.0.0  | 2843362ff29dd536da78f4263d5e2b66d1a98482c47c1376db037e3439ca02ff |

### Scenario: Technology Stack Matrix Coverage
  Given the Technology Stack Matrix in README.md
  When evaluating the listed architectural components
  Then exact pinned library versions are documented:
    - Presentation: Jetpack Compose BOM `2024.09.03`, Material 3, Navigation Compose `2.8.3`
    - Language: Kotlin `2.0.20`, Coroutines `1.9.0`
    - DI: Dagger Hilt `2.52` with KSP `2.0.20-1.0.25`
    - Storage: Room `2.6.1` with SQLite FTS4, ObjectBox `4.3.1`, Proto DataStore `1.1.1`
    - Offline AI: llama.cpp JNI, ONNX Runtime Android `1.21.1`, Tesseract4Android `4.9.0`, PDFBox `2.0.27.0`
    - BLE: Android BLE GATT Client for smart scales
    - Quality: Detekt `1.23.7`, Ktlint `1.3.1`, Robolectric `4.14.1`

### Scenario: Modular Clean Architecture Directory Tree Parity
  Given the Directory Tree section in README.md
  When inspecting the documented hierarchy
  Then the pure Kotlin science engine is highlighted under `core/engine/` and `core/model/`
  And the offline AI and vector search are placed under `core/ai/`
  And the BLE scale integration is placed under `core/ble/`
  And the Room database and DAOs are placed under `core/data/`
  And the 2026 Tactile Industrial UI screens and theme are placed under `ui/screens/` and `ui/theme/`

### Scenario: Deep Feature Matrix & 7-Epic Coverage
  Given the Feature Matrix in README.md
  When checking Epic coverage
  Then all 7 Epics are explicitly documented with IDs and capabilities:
    - S2-E01: Agronomy, multi-state bag inventory, water ion balance, gear catalog, OCR bag scanner
    - S2-E02: Recipe composer, Hamilton-Hare pour scaling, optical Brix-to-TDS refractometer lab
    - S2-E03: Active brew timer, BLE scale protocols, rolling flow rate (g/s), swirl filter
    - S2-E04: Brew journal, espresso dial-in progression, single-variable OVAT suggestions, streak
    - S2-E05: Sensory wheel (221 descriptors), CVA cupping protocol, multi-temperature timeline
    - S2-E06: Personal palate quiz, Bayesian taste adaptation, grounded AI barista chat with GGUF
    - S2-E07: SAF persistent model manifest, resumable downloads, cryptographic backup, bilingual EN/ID

### Scenario: Curated 80 World Champion Catalog and Verification Badges Documentation
  Given the Catalog documentation in README.md
  When examining the recipe breakdown
  Then the 80 curated recipes are categorized into:
    - 21 Competition recipes (WBrC & WAC winners and podium routines)
    - 14 Signature recipes (James Hoffmann, Tetsu Kasuya, Scott Rao, Lance Hedrick, etc.)
    - 45 Traditional & Heritage recipes (Kopi Tubruk, Kopi Joss, Cà Phê Sữa Đá, Turkish Cezve, etc.)
  And the 4 verification levels (`OFFICIAL`, `DOCUMENTED_RECREATION`, `SECONDARY_ADAPTATION`, `NEEDS_VERIFICATION`) are defined

### Scenario: 221 Sensory Wheel Descriptors & Bilingual Taxonomy Coverage
  Given the Sensory Wheel documentation in README.md
  When reviewing descriptor taxonomy
  Then the 221 total descriptors are documented across 9 primary categories:
    - Floral, Fruity, Sweet, Nutty/Cocoa, Spices, Herbal/Vegetal, Sour/Fermented, Earthy/Savory, Other/Defects
  And the composition of 84 SCA reference descriptors plus 137 custom Indonesian and global descriptors is specified
  And the bilingual lookup capability (`name` and `idLabel`) is highlighted

### Scenario: 2026 Tactile Industrial UI & Anti-AI Slop Primitives Documentation
  Given the UI/UX documentation in README.md
  When reading the Design System section
  Then the 2026 Tactile Industrial primitives are detailed:
    - Spring micro-depression physics (`scale = 0.97f`) on press with haptic confirmation
    - Concentric nested corner geometry formula: $R_{inner} = \max(0, R_{outer} - Padding)$
    - Single-line constraints (`maxLines = 1`, `softWrap = false`) preventing distorted bubble cards
    - Tabular monospace numbers (`tnum`) for jitter-free live chronometers and scale telemetry
    - 1px crisp hairline borders (`alpha = 0.40f`) on solid surface backgrounds

### Scenario: Mermaid User Journey Flowchart Syntax & Path Verification
  Given the Mermaid User Journey Diagram in README.md
  When parsed by a GitHub-compatible Markdown renderer
  Then the syntax executes as a valid `flowchart TD`
  And the flow connects:
    - Bean Selection & Bag Stock -> Recipe Formulation -> BLE Scale Connect & Live Brew Session -> Sensory Evaluation & CVA Cupping -> One-Variable-At-A-Time (OVAT) Dial-In Feedback Loop

### Scenario: Mermaid System Architecture Diagram Boundary Verification
  Given the Mermaid Architecture Diagram in README.md
  When inspecting the component layers
  Then the syntax executes as a valid `flowchart TD`
  And UI Composables dispatch events to ViewModel StateFlow
  And ViewModel invokes Pure Kotlin Science Engine
  And Repository boundaries isolate Room SQLite, ObjectBox HNSW, and Android SAF
  And Local hardware and AI engines interface through BLE Scale Adapters and llama.cpp JNI / ONNX Runtime
