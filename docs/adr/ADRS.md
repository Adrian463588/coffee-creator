# Architecture Decision Records (ADRs)

## ADR-001: Android Platform Target and Toolchain Pinning
- **Status:** Accepted
- **Context:** Need predictable compilation and runtime execution across modern Android devices while maintaining Android 8.0+ (API 26) compatibility for specialty baristas on older hardware.
- **Decision:** Pin `minSdk = 26`, `compileSdk = 35`, `targetSdk = 35`, Java 17, AGP 8.6.0, Kotlin 2.0.20, and Compose BOM 2024.09.03.
- **Consequences:** Ensures full Material 3, predictive back support, and Coroutines 1.9 compatibility with zero legacy deprecations.

## ADR-002: Vector Backend and Swappable Storage
- **Status:** Accepted
- **Context:** Need on-device vector search without cloud dependencies.
- **Decision:** Wrap vector store operations behind `VectorStore` domain interface with hybrid lexical Room/in-memory scoring for baseline P0/P1, with swappable ObjectBox/sqlite-vec adapter.
- **Consequences:** Domain logic remains 100% decoupled from vector persistence implementations.

## ADR-003: Embedding Model Parity and Normalization
- **Status:** Accepted
- **Context:** Embeddings must produce consistent cosine similarity scores regardless of device CPU or NPU.
- **Decision:** Pinned ONNX embedding runtime with L2-normalized vector output.
- **Consequences:** Deterministic semantic similarity search across devices.

## ADR-004: Native Model Loading from SAF
- **Status:** Accepted
- **Context:** Android Storage Access Framework (SAF) returns `content://` URIs, whereas native runtimes (like llama.cpp) require seekable file descriptors.
- **Decision:** Resolve seekable file descriptors via `ContentResolver.openFileDescriptor(uri, "r")`. If the provider does not support arbitrary seeking, stage to app-private cache before initializing JNI.
- **Consequences:** Prevents invalid path synthesis while ensuring crash-free native GGUF initialization.

## ADR-005: Resumable Download Strategy
- **Status:** Accepted
- **Context:** Multi-hundred-megabyte GGUF models downloaded over intermittent WiFi/cellular.
- **Decision:** Use OkHttp with HTTP Range (`bytes=X-`), ETag / If-Range headers, and persistent checkpoint state. Verify SHA-256 before marking asset `INSTALLED`.
- **Consequences:** Network loss or reboot safely resumes without re-downloading existing chunks.

## ADR-006: OCR Engine Licensing and Offline Isolation
- **Status:** Accepted
- **Context:** Ingesting scanned coffee manual PDFs without network OCR APIs.
- **Decision:** Bundled Apache 2.0 on-device OCR engine for text extraction; zero external network transmission.
- **Consequences:** Complete offline compliance and privacy preservation.

## ADR-007: Sensory Flavor Wheel Terminology Rights
- **Status:** Accepted
- **Context:** Sensory flavor descriptors must avoid infringing proprietary SCA graphic assets.
- **Decision:** Utilize descriptive taxonomy classifications as text labels and numerical 1-10 intensity ratings; do not replicate proprietary wheel graphics. Explicitly mark all model predictions as non-deterministic heuristics.
- **Consequences:** Legal compliance and honest coffee science.

## ADR-008: Release Signing and Backup Verification
- **Status:** Accepted
- **Context:** Baristas must be able to export formulations and restore after app reinstall.
- **Decision:** Export versioned JSON backup manifests containing record counts, timestamps, and schema integrity hashes. Regrant SAF permission on reinstall.
- **Consequences:** Safe migration and zero data loss on device rotation.
