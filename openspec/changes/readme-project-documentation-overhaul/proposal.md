# Proposal: README Project Documentation Overhaul

## Metadata
- **Change ID:** `readme-project-documentation-overhaul`
- **Target:** `README.md`
- **Role:** Technical Writer, OpenSpec Architect, Android Systems Specialist
- **Methodology:** BMAD (Breakthrough Method for Agile AI-Driven Development) & OpenSpec SDD
- **Style:** Caveman Ultra-Terse / High Density / Zero-Fluff
- **Status:** DRAFT / PENDING REVIEW

## 1. Problem Statement
Current `README.md` is dense, wall-of-text narrative. Weak visual hierarchy. Missing modern SVG shields/badges matrix. Missing Mermaid user journey flowchart. Missing Mermaid system architecture diagram. Lacks structured tech stack matrix across all 7 architectural layers. Deep feature matrix for 7 Epics, 80 World Champion recipes, and 221 Sensory Wheel descriptors is fragmented. 2026 Tactile Industrial UI design rules (`TactileButton`, 0.97f spring physics, `tnum` figures, concentric radii) undocumented. Historical release table incomplete without all verified hashes.

## 2. Objectives
Transform `README.md` into definitive, authoritative, developer- and barista-friendly documentation hub on GitHub.
1. **Hero & Modern Badges**: Platform, Kotlin 2.0.20, Compose BOM 2024.09.03, MinSdk 26 / TargetSdk 35, Privacy 100% Offline, Zero Telemetry, License Apache 2.0, Architecture Clean/Hilt.
2. **Prominent v1.12.2 Download Callout**: Direct download link, file size (122,249,625 bytes ~ 122 MB), SHA-256 (`f7358795ea6ee155c030cc09d4c8a7bd7fbede06a12f7c533841fd3bc775c2a0`), exact ADB zero-data-loss installation command (`adb install --user 0 -r -d coffee-creator-v1.12.2.apk`).
3. **Comprehensive Historical Versions Table**: Complete audit from v1.0.0 through v1.12.2 with verified release dates, direct APK links, release notes links, and SHA-256 hashes.
4. **Technology Stack Matrix**: Structured table across Presentation, Architecture, Storage, Coffee Science Engine, Offline AI, Hardware (BLE), Reliability.
5. **Modular Clean Architecture Directory Tree**: ASCII hierarchy reflecting actual `com.coffeecreator.app` codebase structure.
6. **Deep Feature Matrix**: Full coverage of 7 Epics (S2-E01 through S2-E07) + 80 World Champion Catalog (21 Competition, 14 Signature, 45 Traditional) + 221 Sensory Wheel Descriptors (9 categories, bilingual EN/ID) + 2026 Tactile Industrial UI primitives.
7. **Mermaid Diagrams**:
   - User Journey Flowchart (`flowchart TD`): Bean & Inventory -> Recipe Composer -> BLE Live Brew -> Sensory Cupping -> OVAT Dial-In Loop.
   - System Architecture Diagram (`flowchart TD`): UI Composables -> ViewModel/StateFlow -> Pure Kotlin Science Engine -> Room DB & SAF -> BLE Scale & Local llama.cpp/ONNX.
8. **Privacy & Security Invariants**: Hard air-gap assertions, zero telemetry, local plaintext SAF backups, food-grade chemical bounds.
9. **Build, Lint & Test Protocol**: Exact reproducible terminal commands for assets, lint, detekt, ktlint, tests, and APK builds.
10. **BMAD / OpenSpec SDD Verification Gates**: Verification checklist.

## 3. Mandatory Invariants
- **INV-DOC-01 (Hash Parity)**: All release sizes and SHA-256 hashes MUST match `SHA256SUMS.txt` and `docs/verification/` exactly. Zero fabricated checksums.
- **INV-DOC-02 (Air-Gap & Zero Telemetry)**: Explicitly declare app requires zero internet access for core workflows; network permitted solely for user-initiated GGUF/OCR downloads.
- **INV-DOC-03 (Zero Data Loss Command)**: Document `-r -d` flags for ADB install to prevent catastrophic user database wipes during development updates.
- **INV-DOC-04 (Architecture Isolation)**: Pure Kotlin JVM math engine (`core/engine`, `core/model`) must be explicitly documented as free of Android, Room, or UI dependencies.
- **INV-DOC-05 (Mermaid Compatibility)**: All Mermaid diagram syntax must be 100% GitHub-compatible (`flowchart TD` or `graph TD`), without illegal HTML tags or unescaped characters.
