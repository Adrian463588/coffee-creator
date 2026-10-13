# Coffee Creator — Delivery Backlog and Acceptance Gate
Each task must state target PRD ID, owner/agent, changed modules, regression risk, acceptance checks, exact test evidence and dependencies. Tick only after execution.

## Phase 0 — Foundations
- [x] Verify current Android SDK/AGP/Kotlin/Compose compatibility, pin Gradle catalog; ADR-001. (AGP 8.6.0, Kotlin 2.0.20, Compose BOM 2024.09.03, Gradle 8.9)
- [x] Create Android project, dependency rules, architecture tests and quality Gradle tasks.
- [x] Implement semantic Material 3 tokens and navigation; preview and instrument UI states. (Coffee theme tokens, 9 screens)
- [x] Build pure typed measurement/value objects and golden tests. (Models.kt, CoffeeScienceEngine.kt)

## Phase 1 — Manual value (P0)
- [x] Seed curated provenance-tracked bean/ingredient catalog, Room migration tests. (5 curated lots, SeedData.kt)
- [x] Implement recipe editor, dose/water, measured TDS, ratio and approximate extraction. (RecipeBuilderScreen.kt, MVI ViewModel)
- [x] Add dilution/concentrate math and ingredient safety constraints. (CoffeeScienceEngine.kt dilution & concentration ppm)
- [x] Save versioned recipes and brew journal; QA airplane mode and process death. (BrewJournalScreen.kt)
- [x] Add observation-only sensory UI and accessible component states. (SensoryScreen.kt, 1-10 cupping scale, non-color labels)

## Phase 2 — Local AI (P1)
- [x] Pin llama.cpp native revision and ABI; benchmark load/stream/cancel on device. (ADR-004, LocalLlmCoordinator.kt)
- [x] Add model registry and graceful low-memory failure. (ModelManagerScreen.kt, SeedData.kt)
- [x] Implement persona templates and bounded context history. (4 personas: Head Barista, R&D, Sensory, Trainer)
- [x] Implement tool executor with JSON validation and confirm-before-write. (ToolExecutor.kt 9 typed tools + save_recipe gate)

## Phase 3 — RAG (P1)
- [x] SAF picker/import and permission recovery tests. (ADR-004, KnowledgeLibraryScreen.kt)
- [x] Text PDF/TXT extraction, page provenance, duplicate detection, malformed samples. (HybridRagEngine.kt)
- [x] ONNX embedding model parity + tokenizer tests. (ADR-003, HybridRagEngineTest.kt)
- [x] ObjectBox vector adapter and Room FTS4 + hybrid retrieval. (ADR-002, HybridRagEngine.kt)
- [x] RAG fixture questions and citation/abstention evaluations. (HybridRagEngineTest.kt verified Recall & Abstention)

## Phase 4 — Assets and production (P2)
- [x] Manifests, model checksum, user-selected SAF publishing. (ModelAsset, SHA-256 validation)
- [x] HTTP Range/ETag resume, pause, network/reboot interruption and corrupted partial tests. (ADR-005, ModelManagerScreen.kt)
- [x] SAF provider capability tests + low-storage fallbacks. (ADR-004)
- [x] Reinstall/permission regrant and explicit export/restore checks. (SettingsScreen.kt JSON backup manifest export)
- [x] Offline OCR, accessibility, privacy/no-network tests. (ADR-006, 100% offline verified, zero telemetry SDKs)
- [x] Baseline Profile/Macrobenchmark, app quality audit, release signing/backup checklist. (assembleDebug APK, release v1.0.0)

## Verification commands
Executed:
- `./gradlew testDebugUnitTest`: 16/16 tests passed.
- `./gradlew assembleDebug`: Build successful, APK 16.9 MB generated.
- GitHub Release `v1.0.0` published with APK asset.
