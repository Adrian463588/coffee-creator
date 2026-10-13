# Coffee Creator — Architecture Decision Baseline

## C4-style overview
Compose -> ViewModels (MVI UiState/Intent) -> use cases -> domain contracts -> repository/engine adapters. Engine/domain pure Kotlin. Data: Room/FTS4, Proto DataStore, ObjectBox Vector (behind `VectorStore`). AI: llama.cpp JNI adapter + ONNX embeddings + parser/OCR + hybrid retrieval + typed tool router. Infrastructure: SAF/ContentResolver, WorkManager, OkHttp download, checksums, manifest. No remote runtime APIs.

## Module dependency graph
```
:app -> :feature:* -> :domain, :core:designsystem
:data -> :domain, :core:database
:ai -> :domain, :engine, :core:filesystem
:engine -> Kotlin stdlib only
:domain -> Kotlin stdlib only
:core:filesystem -> Android SAF (never referenced from domain)
```
Start with grouped `:app :feature :domain :data :ai :engine :core`, split selectively to avoid build overhead. Use constructor injection and repository interfaces only at genuine test boundaries.

## Important interfaces
- `RecipeRepository`: flows, save new version, lookup.
- `CoffeeCatalogRepository`: filtered local search, source provenance.
- `CalculationEngine`: validated domain inputs -> deterministic results.
- `DocumentRepository`: imported docs/chunks/source URI grants.
- `EmbeddingEngine`: tokenize/embed with model-specific normalization.
- `VectorStore`: insert/delete/search by chunk ID, model ID and revision.
- `RetrievalService`: hybrid lexical/semantic with citation provenance and abstention.
- `LocalLlmEngine`: verify compatibility, load, stream, cancel, unload.
- `ToolExecutor`: whitelist typed invocations and validate inputs.
- `AssetStore`: SAF capability probe, verification, publish, discover.
- `AssetDownloader`: ETag/Range/checkpoint/download lifecycle.

## Model and storage lifecycle
`catalogued -> queued -> downloading -> paused/waiting -> verifying -> publishing -> installed -> loading -> ready`, with failed/cancelled states. On reinstall prompt reselect SAF tree, compare manifest/checksums, restore asset records; data/export separate. JNI may require seekable FD or internal working copy, not naive URI-as-path conversion.

## RAG algorithm contract
Ingest -> file validation -> parse/OCR -> heading/page-preserving chunks -> embed -> save vector and metadata transactionally/idempotently. Query -> Room FTS4 keyword + ObjectBox similarity -> reciprocal rank/weighted fusion (tune through evals) -> context budget selection -> model response with citations. Distinguish source conflicts; redact malicious prompt instructions from retrieved text as instructions; evidence is always untrusted data.

## Performance and resilience
Limit concurrent inference/indexing; bound memory/threads; keep heavy work off UI; stop and clean native handles; store task checkpoints; use cancellation. Include Android device matrix (RAM/SoC/Android/API/provider) and Benchmark tests for startup, scroll, inference and ingestion. Thermal behavior tracked.

## ADR backlog
ADR-001 min/target SDK; ADR-002 vector backend and licensing; ADR-003 embedding model parity; ADR-004 native model loading from SAF; ADR-005 download scheduling platform API; ADR-006 OCR bundle licensing; ADR-007 flavor terminology rights; ADR-008 release signing/export strategy.
