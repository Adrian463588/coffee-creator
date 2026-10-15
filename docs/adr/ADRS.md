# Architecture decisions — reviewed 2026-10-15

Implementation choices are distinct from acceptance evidence in VERIFICATION.md.

## ADR-001: pinned platform

min/compile/target SDK26/35/35; AGP8.6.0, Kotlin2.0.20, Compose BOM2024.09.03,
Gradle8.9, Java17 bytecode (local JDK21), NDK27.0.12077973 and CMake3.22.1.
arm64-v8a and x86_64 are compiled. Physical reference: Samsung SM-G988B/API33.
Dependency-update/target-API lint advisories stay visible; no evergreen-version claim.
Other API/RAM/provider tiers need their own runs before compatibility claims.

## ADR-002: persistent records and vector adapter

Room2.6.1 owns records/chunks/FTS4 with a preserving1-to-2 migration. ObjectBox4.3.1
owns derived384-dimensional MiniLM vectors with cosine HNSW behind VectorStore.
Cache keys bind chunk ID, text SHA and model revision; stale keys are reconciled,
restore/delete clear the cache and reindex rebuilds it. Writes across the two stores
are idempotent, not a distributed transaction. ObjectBox's plugin uses its code
processor; Room/Hilt use KSP. Preserve generated schema UIDs in objectbox-models.
Java bindings are Apache2.0; native database terms are listed in THIRD_PARTY.md.
sqlite-vec is a future adapter investigation, not a shipped component.

## ADR-003: actual embedding parity

Pinned quantized all-MiniLM-L6-v2 ONNX/vocabulary assets, WordPiece, masked mean
pooling and L2 normalization on ORT CPU. Three vectors and token IDs compare with
an independently executed host CPU reference at tolerance1e-4. This proves parity
on that corpus/device, not general retrieval or multilingual accuracy.

ONNX Runtime Android is now pinned1.21.1: ELF inspection found1.19.2's core/JNI
libraries only4KiB aligned. Official Maven1.21.1 is the smallest checked release
with16KiB aligned/congruent PT_LOAD segments on both selected ABIs (1.21.0 JNI
still failed). AAR SHA25630e594a4b9246fe3ca25768570e90f71e6d33ceb7b7dd72f92dcd7c267611d3f.
Existing Java APIs were checked. Isolated host ORT1.21.1 passed three parity cases
at maximum error2.9802322387695312e-8 versus1e-4 tolerance. This does not substitute
for Android runtime retesting. Source: https://github.com/microsoft/onnxruntime/releases/tag/v1.21.1.

## ADR-004: native runtime and prompt boundaries

Pinned llama.cpp3fb47677eaf0ebb569a1f94c756dcf58b8584cc0 with verified source archive;
CPU-only JNI, no server/network/shell/dynamic backend. GGUF header/tensor validation,
memory admission, serialized inference, cancellation, lifecycle/low-memory unload.
SAF input becomes a bounded hash-verified private working copy, never an invented
content-URI filesystem path. Context<=1024/output<=256; actual tokenizer preflight
reduces history/source context and rejects oversized questions recoverably. Escape
CONTROL/USER_DEFINED vocabulary spellings in all message content before trusted
role template rendering. Bound reserved token count/bytes. Sources are untrusted.
LLM proposed math requires review of each named quantity/unit before execution;
matching a set of numbers cannot establish measurement provenance.

## ADR-005: asset transfer

Explicit user-requested WorkManager foreground transfer/OkHttp default TLS. Private
random-access staging; owned SAF publication names, final size/SHA re-read, then
manifest. Resume validates revision, partial hash, identity,206 and Content-Range;
200 restarts. Pause/cancel interrupt sockets and serialize cleanup with imports.
Interrupted verification requires retry. Platform quotas can interrupt transfers;
durable checkpoints support user Resume, not a promise of unlimited execution.
API29 foreground-service type is guarded. Uninstall removes private records,
checkpoints and grants; external rediscovery requires a new tree selection.

## ADR-006: offline bounded document ingestion

Strict UTF8 TXT and PDFBox Android text extraction with bounded byte/page/text/chunk
budgets; oversized/encrypted/malformed inputs return errors. PDF output uses a
bounded writer. Bounded PdfRenderer images feed bundled Tesseract4Android and
pinned tessdata_fast English; other languages are not promised. Retained chunks
remain inspectable after source grants disappear. PDFBox's BouncyCastle dependency
contains TLS-manager lint findings; the application never installs those managers
or a custom TrustManager in its HTTP client. Findings stay visible for review.

## ADR-007: sensory provenance

Versioned generic taxonomy seeds, no invented lots/densities/certificates/measurements.
Observations and estimates have separate persisted types/kinds. Estimates show
basis/method/uncertainty, null numeric confidence and uncalibrated ordinal direction.
Additives suppress coffee-only EY assumptions. No proprietary SCA wheel artwork.
User field research remains necessary before claiming optimized barista usability.

## ADR-008: backup/privacy/release

Complete plaintext SAF backup payload with counts/schema/SHA corruption check,
validated before one Room transaction. Schema3 adds estimates; schema2 migrates
with an empty estimate collection. Asset/grant/checkpoint/vector caches are separate.
Disable automatic cloud/device backups. Exports/models need independent deletion.
Distribute debug-signed APK as requested; no Play signing/production keystore claim.
Do not commit weights or signing secrets. Preserve version history and checksums.

## ADR-009: module/quality boundaries

One application module groups pure domain/engine, repositories, AI/filesystem and
Compose presentation; an independent benchmark module provides a measurable test
boundary. Hilt constructor injection/immutable StateFlow, IO outside composition.
Detekt remains active with zero allowed findings; explicit facade/Composable budgets
avoid speculative module splitting. Boundary exception handlers rethrow cancellation
and expose recovery. No blanket suppression or disabled lint/format gates. Authoring
methods OpenSpec/BMAD/RTK/Ponytail/Caveman are not Android runtime dependencies.
CI uses the validated Ubuntu24.04 host explicitly; floating ubuntu-latest warned
of an upcoming major OS migration. SDK setup explicitly requests platform-tools
and pinned build packages, avoiding the removed legacy tools package. Action
runtime deprecation advisories remain visible; application quality gates stay active.

## ADR-010: local Android verification without ADB

The user's final test instruction permits no current connected-device run.
Use test-only Robolectric4.14.1 native graphics/SQLite on SDK33 to execute the real
Compose activity and isolated Room repositories locally. This is local framework
evidence, separate from physical inference/SAF/performance. Short descriptive BDD
method names avoid Windows temporary database paths exceeding MAX_PATH; test
assertions and native SQLite behavior remain enabled. Render actual decor views
to PNG and label these local renders. Device test cleanup first closes the
activity and awaits its app-scoped writes before restoring the prior draft.

## ADR-011: text-only Gemma4 and model metadata ownership

Keep the existing immutable llama.cpp revision, which includes Gemma4 support.
Specialize the official pinned Google text-only turn template; reject unsupported
architectures/control markers and cap context, output, metadata and per-layer
arrays. Every dimension key must match the exact declared architecture after all
metadata is read, independent of key order. Foreign/nested prefixes must never
override RAM bounds. Host regressions prove parsing/formatting, not successful
inference. Full Kafi/Gemma downloads/inference remain untested. Use actual GGUF
size3,106,738,272bytes; the approximately2.58GB LiteRT file is a different format.

## ADR-012: draft recovery and record replacement

Keep a single previous destructive draft edit in SavedStateHandle; no-op resets
preserve it. Later edits/save clear it. Saved immutable recipe revisions remain
independent. Validate restored draft fields before any records are replaced.
Cancel/join the debounce writer and drain queued lifecycle writes around export,
restore and whole-record deletion. Synchronize active draft, SavedStateHandle and
autosave state before writes resume; discard obsolete Undo and pending AI edits.
Export first saves the current draft so a fast export cannot omit recent input.
Keep the record/draft restore operation separate from derived native vector-cache
invalidation so the real Room/state recovery is locally testable without claiming
host execution of Android JNI. Confirm permanent record deletion explicitly.
Close/reopen the write gate on Main, serialized with lifecycle check/enqueue.
Complete bounded record replacement and state synchronization if the caller is
cancelled after commit. Reject new chat/tool starts while records are busy so
late generation writes cannot overwrite restored conversation history.
