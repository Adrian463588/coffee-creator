# Coffee Creator — Delivery Backlog and Acceptance Gate
Each task must state target PRD ID, owner/agent, changed modules, regression risk, acceptance checks, exact test evidence and dependencies. Tick only after execution.

## Phase 0 — Foundations
- [ ] Verify current Android SDK/AGP/Kotlin/Compose compatibility, pin Gradle catalog; ADR-001.
- [ ] Create Android project, dependency rules, architecture tests and quality Gradle tasks.
- [ ] Implement semantic Material 3 tokens and navigation; preview and instrument UI states.
- [ ] Build pure typed measurement/value objects and golden tests.

## Phase 1 — Manual value (P0)
- [ ] Seed curated provenance-tracked bean/ingredient catalog, Room migration tests.
- [ ] Implement recipe editor, dose/water, measured TDS, ratio and approximate extraction.
- [ ] Add dilution/concentrate math and ingredient safety constraints.
- [ ] Save versioned recipes and brew journal; QA airplane mode and process death.
- [ ] Add observation-only sensory UI and accessible component states.

## Phase 2 — Local AI (P1)
- [ ] Pin llama.cpp native revision and ABI; benchmark load/stream/cancel on device.
- [ ] Add model registry and graceful low-memory failure.
- [ ] Implement persona templates and bounded context history.
- [ ] Implement tool executor with JSON validation and confirm-before-write.

## Phase 3 — RAG (P1)
- [ ] SAF picker/import and permission recovery tests.
- [ ] Text PDF/TXT extraction, page provenance, duplicate detection, malformed samples.
- [ ] ONNX embedding model parity + tokenizer tests.
- [ ] ObjectBox vector adapter and Room FTS4 + hybrid retrieval.
- [ ] RAG fixture questions and citation/abstention evaluations.

## Phase 4 — Assets and production (P2)
- [ ] Manifests, model checksum, user-selected SAF publishing.
- [ ] HTTP Range/ETag resume, pause, network/reboot interruption and corrupted partial tests.
- [ ] SAF provider capability tests + low-storage fallbacks.
- [ ] Reinstall/permission regrant and explicit export/restore checks.
- [ ] Offline OCR, accessibility, privacy/no-network tests.
- [ ] Baseline Profile/Macrobenchmark, app quality audit, release signing/backup checklist.

## Verification commands
Discover actual tasks first: `./gradlew tasks`. Then `./gradlew lint detekt ktlintCheck testDebugUnitTest assembleDebug`; execute connected tests on emulator/device when available. Note when SDK/runtime is absent; never mark as passed without logs.
