# Coffee Creator — Product Requirements Document (PRD)

**Status:** Implementation baseline | **Version:** 1.0 | **Date:** 2026-10-13 | **Platforms:** Native Android, phone and tablet | **Owner:** Product / Engineering

## 1. Product vision
Build a privacy-preserving, 100% offline-capable professional barista workstation for recipe formulation, coffee-science calculations, sensory recording and evidence-grounded AI assistance. After optional first-time model/asset installation, inference, search, recipe editing and scientific computations require no network or account. The app may access the network **only** when a person explicitly initiates an asset download/update; no telemetry, cloud inference, authentication backend, or remote database.

### Product principles
1. **Manual-first:** Coffee calculator, catalog, brewing journal and sensory annotations remain usable with no LLM installed.
2. **Measured ≠ calculated ≠ predicted:** Show provenance/method next to every numerical or sensory result.
3. **Deterministic authority:** LLM delegates all calculations to validated Kotlin tools; generated arithmetic cannot override tool output.
4. **Offline means offline:** All everyday flows operate with airplane mode enabled.
5. **User-owned assets:** Models and optional exports may reside in a chosen SAF directory; users retain control.
6. **Honest sensory science:** Flavor-wheel terms are descriptive classifications, not deterministic physical equations.

## 2. Personas and primary jobs
- **Head barista:** Iterate dose/yield/TDS quickly, compare recipe versions, record tasting notes.
- **Coffee R&D specialist:** Design blends/concentrates, maintain ingredient provenance and limits, compare experiments.
- **Roaster / sensory evaluator:** Track origins/varieties/processes, produce repeatable observations with vocabulary mapping.
- **Coffee trainer:** Ask grounded questions over imported manuals and recipe notes, inspect citations.

## 3. Goals, non-goals and release gates
### Outcomes
- Manual formulation works without a downloaded model.
- All grounded AI answers expose clickable local document citations where evidence exists.
- The LLM can propose recipes and invoke typed calculation tools.
- Large model assets can pause/resume and recover safely from connection loss/reboot.
- Verified model bundles in a user-selected shared SAF location can be rediscovered after reinstall (user must regrant directory access).
- All core interactions are accessible, responsive, and usable with wet-hands/rapid-brew workflows.

### Non-goals for MVP
- Remote model/API inference, cloud sync, user accounts or analytics SDKs.
- Autonomous internet browsing during chat.
- Guaranteed laboratory-quality sensory prediction without calibration data.
- Reproducing proprietary SCA artwork without appropriate rights.
- Bluetooth scales, smart machines, ingredient purchasing, marketplace integrations, AR, social features or multi-agent runtime inside the Android app.
- Ingesting every conceivable document type or every global coffee ingredient on day one.

### Release stages
- **P0 / MVP:** Manual recipe calculator, coffee/ingredient catalog, save/history, accessible Compose UI, offline baseline, basic SAF export/import.
- **P1:** GGUF local inference, persona, document import (text-based PDFs/TXT), embeddings, local retrieval, citations, trusted tools.
- **P2:** OCR, expanded file formats, resumable SAF asset downloads and reinstall recovery, sensory observation / cautious estimation.
- **P3:** Calibrated sensory ML (only after labeled data and validation), advanced formulation workflows and performance optimizations.

## 4. Functional requirements and acceptance tests

### FR-01 Coffee catalog (P0)
Maintain beans (origin, country/region, producer, variety, process, altitude, roast), brewing methods, equipment, ingredients, densities if known, allergens, food-grade status, provenance and optional supplier usage limits. Curated, versioned seed data + custom user entries; never claim exhaustive worldwide coverage.
**Acceptance:** Can search/filter/edit locally in airplane mode; missing facts are `unknown`, not silently invented; import validation prevents malformed records; schema migration retains data.

### FR-02 Manual formulation (P0)
Recipes with ingredients, mass/volume quantities, dose, brew water, measured beverage mass, measured beverage TDS, yield, temperature, pressure, brew time, brew method and equipment metadata. Decimal locale input supported. Canonical typed quantities; convert volume-to-mass only if density is provided.
**Acceptance:** Save/copy/version a recipe; undo before destructive actions; unit consistency tested; invalid/zero/negative quantities rejected; TDS cannot be calculated from dose and water alone.

### FR-03 Calculation service (P0)
Pure Kotlin/JVM deterministic functions: water:coffee ratio; approximate extraction yield from measured beverage mass × measured TDS% / dose; mass-fraction weighted dilution; concentration (% w/w and ppm by mass); density-based conversions; yield and uncertainty labeling. Support method-specific extraction assumptions, especially immersion and retention.
**Acceptance:** Golden test: dose=18g, beverage=270g, TDS=1.30% yields 19.50%; all tools have validation, precision policy, tests and stable version identifiers; result includes method, assumptions and input provenance.

### FR-04 Sensory tracking and predictions (P0 observations / P2 estimates)
Users assign standardized or licensed sensory vocabulary descriptors and intensity, with notes and panel/assessor metadata. Optional application-owned flavor-wheel visualization must not reproduce restricted proprietary wheel graphics. A rules-based *estimated direction* can be shown only with explicit uncertainty/limitations; no claim of exact flavor physics from origin/process alone.
**Acceptance:** Observed and predicted profiles differ visually and in persisted type; all estimates show basis and confidence/uncertainty; users can compare recipe revisions.

### FR-05 Local LLM chat (P1)
User-imported GGUF-compatible quantized model, streaming token display, cancellation, prompt presets and editable personas. One inference session coordinator manages RAM limits and lifecycle. Native adapter uses pinned llama.cpp interface; no remote calls.
**Acceptance:** Works in airplane mode after install; manual app survives LLM load failure; cancel promptly frees resources; no OOM crash on tested device tier; clear status for unsupported models.

### FR-06 Offline document RAG (P1)
SAF `OpenDocument`/tree import; explicit persisted grants where supported; text extraction for TXT and text PDFs first. Incremental normalization, document headings/pages, chunking and local embeddings; Room metadata + vector store with a swappable adapter. Combine lexical (Room FTS4) and vector retrieval; citations link to source chunk/page. Optional offline OCR for scanned documents (P2).
**Acceptance:** Import, restart, search and cite with airplane mode; repeated import deduplicates based on content hash; revoked URI access handled gracefully; unsupported types rejected clearly; no document instructions are promoted to system prompts; invalid/no evidence results in abstention.

### FR-07 AI tool calls (P1)
An allowlisted, typed tool registry for `calculate_brew_ratio`, `calculate_extraction_yield`, `calculate_dilution`, `convert_units`, `lookup_ingredient`, `lookup_coffee_origin`, `search_documents`, `estimate_sensory_profile` and `recommend_recipe`. Validate all structured arguments; max steps/time; deterministic outputs; confirmed write action for `save_recipe`.
**Acceptance:** Bad tool schema rejected without crash; exact Kotlin math results preserved; read-only tools cannot mutate data; no shell/browser/network tools offered to the runtime LLM.

### FR-08 Model/asset manager (P2)
Model manifest: identifier, revision, compatible runtime, format, expected size, SHA-256, download URL, legal/license notes, tokenizer support and status. Explicit user-initiated downloads; resumable ranges, ETag/If-Range, Content-Range verification, checksum and staging/publish into a user-chosen SAF document tree. Support pause/resume/cancel/low-storage/reboot. Compatible SAF provider direct partial-write path only after capability verification; otherwise internal random-access staging followed by verified publication.
**Acceptance:** Interrupted transfer resumes only if remote identity and local partial data agree; ignore untrusted filenames/path traversal; file is marked Installed only after verification; after uninstall/reinstall existing published weights can be rediscovered by reselecting directory and regranting permission. App-private partial bytes and internal DB do not magically survive uninstall.

### FR-09 Backup and recovery (P2)
Optional explicit export/import of recipes, user catalog, chat history and source metadata into a user-owned SAF location; schema/versioned manifest and integrity validation. Do not imply external model persistence also preserves internal records.
**Acceptance:** Fresh-install restore imports matching version or performs safe migration; unsupported/corrupt exports cannot overwrite existing good data; export deletion and privacy behavior explained.

### FR-10 Settings, privacy and transparency (P0-P2)
Model selection, generation parameters bounded to compatible ranges, persona edits, storage location, offline status, local-only diagnostics, data deletion and accessible controls. Explain explicit download networking and file grants.
**Acceptance:** Zero operational network access under offline QA; no trackers/telemetry/remote inference libraries; personal docs never leave device through application runtime.

## 5. Primary user journeys
1. **New device:** Launch → explain offline mode → create manual recipe immediately → optionally install model/embeddings → choose SAF destination → verify/download → chat offline.
2. **Manual brewing:** Select bean + brew method → enter dose/water → (optional) measured beverage and TDS → calculate → record sensory notes → save version → compare.
3. **Grounded RAG:** Select document → see extraction/indexing progress → ask coffee question → hybrid retrieve → response cites local source title/page/chunk → inspect original document.
4. **Agent-assisted recipe:** Ask for target profile → retrieve ingredient constraints → LLM requests Kotlin tools → validated calculation → draft recipe + estimated sensory direction → user confirms Save.
5. **Interrupted download:** User starts model → network drops → checkpoint persists → reconnect → validate remote revision/range → resume → checksum → publish → load.
6. **Reinstall:** Reinstall app → pick previous SAF directory → validate manifest/assets → repopulate local asset registry → restore optional exported application records.

## 6. Navigation and screen inventory
- Home: quick recipe / quick chat, device readiness, latest experiments.
- Recipe Builder: ingredient editor, unit-aware numeric entry, measurements, instant deterministic results, comparisons.
- Coffee Library: beans, processing, origins, ingredients, validated metadata.
- Sensory: interactive descriptors, observed vs predicted overlay, confidence and method.
- AI Chat: persona, model status, streamed answer, cited documents, tool result cards.
- Knowledge Library: import/reindex/remove, index progress, citations, OCR state.
- Model Manager: catalogue/installed/paused/failed, SAF selection and recover.
- Brew Journal: experiments and repeatable measurements.
- Settings: privacy, accessibility, local storage, export/restore, persona settings.

## 7. Technical constraints and decisions
- Android target/min SDK to be pinned in an ADR after compatibility benchmarking; do not guess evergreen versions.
- Kotlin 2.x, AGP compatible version, Java 17, Compose BOM, Material 3, Navigation Compose (Nav 3 only after stable compatibility validation), Hilt/KSP, Coroutines/Flow, Room FTS4, Proto DataStore.
- llama.cpp behind `LocalLlmEngine`; ONNX Runtime Mobile behind `EmbeddingEngine`; ObjectBox behind `VectorStore` with sqlite-vec investigation documented separately.
- ContentResolver/SAF for user-controlled files; never turn `content://` into invented paths.
- WorkManager + OkHttp with explicit user-initiated long transfer strategy; no assumption that WorkManager runs unbounded on recent Android versions.
- Compile C++ via supported NDK/CMake toolchain; pin native revision and ABI; inspect license of every model/data/library.
- Secure default: document content is untrusted RAG data, not executable instructions; no native shell available to model.

## 8. Domain entities
`CoffeeBean`, `ProcessingMethod`, `Ingredient`, `IngredientComposition`, `IngredientConstraint`, `Recipe`, `RecipeLine`, `BrewMeasurement`, `CalculationResult`, `SensoryObservation`, `SensoryEstimate`, `Document`, `DocumentChunk`, `EmbeddingRecord`, `Conversation`, `ChatMessage`, `Persona`, `ModelAsset`, `DownloadCheckpoint`, `BackupManifest`.

Every measurement carries units; each estimate includes algorithm/version, source, assumptions and uncertainty. Persist exact decimal input where appropriate; floating point allowed only within calibrated numerical kernels with tolerances.

## 9. Non-functional requirements
- **Privacy:** On-device local content; network only on explicit asset download/update; no telemetry SDK.
- **Availability:** Manual core remains functional without installed AI, OCR or embeddings; safe recover from process death.
- **Reliability:** Transactional writes, idempotent imports, durable checkpoint state, no partially installed model marked ready.
- **Security:** No broad all-files permission; least-privilege SAF; native library and model checksums; defensive PDF/OCR ingestion; MCP is development-only, never shipped in app.
- **Performance targets (provisional, validate in benchmarks):** manual calculation p95 <100ms on reference device; navigation frame time judged by Macrobenchmark; no main-thread I/O; retrieval/TTFT/tokens/s measured per RAM tier, not guaranteed a priori.
- **Accessibility:** TalkBack semantics, 48dp targets, adaptive layouts, system text scaling, contrast, keyboard controls, reduced-motion considerations.
- **Battery/thermal:** Model loading and background work bounded; monitor thermal throttling; prevent duplicate concurrent indexing and inference overload.
- **Licenses:** Verify weights, redistributability, curated ingredient dataset and SCA lexicon/wheel usage rights before bundling.

## 10. Evidence-backed success metrics and evaluation
Track **local test metrics** rather than cloud analytics: recipe calculation golden-case pass rate 100%; unit tests all pass; RAG Recall@5 and citation precision on an adjudicated question set; invalid tool-call rejection 100%; interrupted-download recovery on a matrix of local/remote SAF providers; zero unexpected network requests during offline scenario; zero critical accessibility defects; crash-free automated soak suite on supported devices.

Create a 100-question coffee RAG fixture: 40 direct facts, 20 cross-document, 20 numerical/tool-needed, 10 conflicting sources, 10 unanswerable. Report Recall@5, citation faithfulness, abstention quality and incorrect arithmetic separately. Baselines must be measured, not invented.

## 11. Dependencies and risks
- Native GGUF compatibility and device RAM: resolve with per-device prototypes before promising model tiers.
- ONNX tokenizer/pooling correctness: golden embedding tests and reference parity.
- SAF persistence: regrant required after reinstall; provider-specific seek behavior; staging space constraint.
- Coffee chemistry versus sensory perception: no exact flavor-wheel predictions without labeled empirical data.
- Ingredient safety: only food-grade additives, supplier-specific concentration limits, warnings and allergen metadata.
- Third-party model licensing: verify every downloadable model version and permitted use.
- Large PDF/OCR memory: streaming / bounded processing; corrupt-file isolation.

## 12. Delivery milestones / Definition of Done
- **M1 Core:** manual recipe/catalog/science works offline; schema tests and goldens pass.
- **M2 Local LLM:** inference load/stream/cancel works on physical reference phone and low-RAM safe state verified.
- **M3 RAG:** indexed docs with citations; golden retrieval and injection tests; no network.
- **M4 Formulation assistant:** validated tools and personas; explicit save confirmation; sensory uncertainty.
- **M5 Production:** model manager/reinstall recovery, backups, accessibility, end-to-end tests, app startup/performance, release audit.

**Each feature is complete only when** implementation, design states, unit/integration/UI tests, accessibility review, documented failure modes, lint/format/static analysis, build and regression checks pass. Do not mark a test as passed without executing it and collecting output.

## 13. Open decisions / ADR triggers
Choose minSdk/targetSdk against real devices; choose ObjectBox license vs sqlite-vec integration; pin model/runtime/embedding revisions; decide release-friendly OCR engine and redistribution; set benchmark device matrix; determine legal approach to SCA terminology/graphics; decide backup encryption and export retention.

## 14. References
- Android Architecture: https://developer.android.com/topic/architecture
- Compose accessibility: https://developer.android.com/develop/ui/compose/accessibility
- SAF: https://developer.android.com/guide/topics/providers/document-provider
- WorkManager: https://developer.android.com/topic/libraries/architecture/workmanager
- llama.cpp: https://github.com/ggml-org/llama.cpp
- ObjectBox: https://github.com/objectbox/objectbox-java
- sqlite-vec: https://github.com/asg017/sqlite-vec
