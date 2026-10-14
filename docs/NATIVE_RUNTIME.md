# FR-05 native runtime contract

Scope: real, offline GGUF load / token streaming / cancellation / unload. Owner:
`codex/native`; only the JNI adapter, Kotlin engine, native tests and this note.
The integrator owns Gradle, model staging, coordinator, ViewModels and UI.

Before implementation, acceptance is defined as follows:

- Given a missing, malformed, oversized or unsupported model, loading reports a
  recoverable error. No canned answer substitutes for model inference.
- Given insufficient available device RAM or Android low-memory state, loading
  is rejected before allocating llama.cpp weights or context.
- Given a supported private staged GGUF, load uses its real tokenizer and chat
  template, then streams decoded model-generated UTF-8 bytes on a worker thread.
- Given cancellation, the CPU abort callback stops decoding. Context and sampler
  are freed in `finally`; loaded weights remain available until explicit unload.
- Given parallel requests, one Mutex serializes load, generation and unload.
  Cancellation uses an atomic flag, independent of the native operation lock.
- Given unload during cancellation, native registry ownership prevents stale-handle
  access and releases the model after any active JNI call returns.
- No HTTP, subprocess, shell, GPU backend or document execution is compiled into
  this adapter. Documents remain untrusted context supplied by the coordinator.

Planned checks: JVM BDD tests for admission, options, role limits and fragmented
UTF-8; native parser checks for malicious counts/lengths/tensors; Android NDK
arm64-v8a and x86_64 compilation; physical-device load/stream/cancel/unload and
airplane-mode acceptance after integration. Build success is not inference proof.

Rollback: revert the scoped native commit. User-owned models and app records are
outside this slice and must not be deleted.

## Integration API

`NativeLlmEngine(Context)` exposes `initialized`, `status`,
`suspend load(File, contextSize = 1024)`,
`generate(systemPrompt, userPrompt, history, options): Flow<String>`, `cancel()`,
`suspend unload()` and `suspend close()`. History accepts only user/assistant
roles. Options bound output, temperature, top-p, top-k and seed. Deltas are real
decoded tokens, not full repeated responses.

The caller must validate the model manifest size/SHA-256, stage the SAF file into
app-private storage and keep it immutable while loaded. A content URI is never
converted to a guessed path. Generation uses a model-declared supported chat
template; unsupported/missing templates are errors. No model weights ship here.

## Runtime pin, license and compatibility

Official upstream: https://github.com/ggml-org/llama.cpp . API and Android guide
were inspected on 2026-10-14. Exact source revision:
`3fb47677eaf0ebb569a1f94c756dcf58b8584cc0` (0.6.0-dev). CMake fetches its immutable
archive and verifies SHA-256
`a377fa5191fecc49044d160fb16b245ccc69b2a75d70799396f728b490b966fe`.
Upstream MIT copyright/permission text is retained in
`app/src/main/cpp/THIRD_PARTY_LICENSES.txt`; the integrator must include it in the
APK's third-party notices. Weight licenses are separate and model-specific.

CPU targets: arm64-v8a (Arm v8 baseline) and x86_64 (no AVX/FMA requirement),
Android API 26+, NDK 27.0.12077973 and CMake 3.22.1. GGUF v2/v3 single-file dense
decoder architectures: Llama, Qwen2, Gemma/Gemma2 and Phi3, with a recognized
model-declared chat template. Tensor types: F32/F16/BF16, classic Q4/Q5/Q8_0 and
Q2_K through Q6_K. Experimental IQ/TQ/MoE, split models and encoder/multimodal
models are rejected explicitly. This is a bounded small-model runtime, not an
all-GGUF compatibility promise.

Limits: <=2 GiB file, <=4096 tensors/metadata entries, <=64 MiB metadata,
<=262144 vocabulary/array entries, <=4096 hidden width, <=64 layers, <=4 native
threads, context 256–1024 and output 1–256 tokens (default 128). Admission reserves
at least 512 MiB for Android/UI, accounts for model bytes +25%, and checks
model-specific KV dimensions before native allocation. This reduces allocation
risk; concurrent system pressure still requires device validation. No absolute
OOM-free guarantee is inferred from a build or static bound.

## Embedding contract

`OnnxEmbeddingEngine(modelFile: File, vocabFile: File)` is constructed on an IO
dispatcher. `suspend embed(text): FloatArray` uses the actual local ONNX CPU
session; `close()` releases the session and must also run off the UI thread.
ORT's process-wide environment is library-owned and is not closed by this adapter.
The integrator verifies assets before construction and uses ONNX Runtime Android
1.19.2. Input: BERT uncased WordPiece, CLS/SEP/PAD, max 256 tokens; fixed model
lengths are padded with a matching attention mask. Output: float32
`last_hidden_state` masked mean pooling (including non-pad special tokens) or
`sentence_embedding`, followed by L2 normalization. Invalid shapes, nonfinite or
zero embeddings fail rather than substituting a synthetic vector.

Selected asset contract: Xenova/all-MiniLM-L6-v2 immutable revision
`751bff37182d3f1213fa05d7196b954e230abad9`, quantized ONNX size 22,972,370 bytes,
SHA-256 `afdb6f1a0e45b715d0bb9b11772f032c399babd23bfc31fed1c170afc848bdb1`.
The caller owns vocabulary/model verification, local staging and asset licenses.
Tokenizer/pooling JVM checks below are not model reference parity.

## Verification

- **PASS, expected red phase:** `gradlew.bat testDebugUnitTest --tests
  '*NativeRuntimeContractTest' --console=plain` failed before implementation with
  unresolved native policy/decoder types; the same targeted check subsequently
  passed (9 tests).
- **PASS, expected red phase:** `gradlew.bat testDebugUnitTest --tests
  '*EmbeddingContractTest' --console=plain` failed before implementation with
  unresolved tokenizer/pooling types.
- **PASS:** `gradlew.bat -I .cxx/native-check.gradle testDebugUnitTest --tests
  '*NativeRuntimeContractTest' --tests '*EmbeddingContractTest' --console=plain`:
  16 tests, 0 skipped/failures/errors. The ignored init script adds only the
  integrator-owned pinned ORT dependency for this isolated branch's checks.
- **PASS:** `g++ -std=c++17 -Wall -Wextra -Werror
  app/src/main/cpp/tests/gguf_guard_test.cpp -o .cxx/gguf_guard_test.exe`, then
  `.cxx/gguf_guard_test.exe .cxx/guard-fixture.gguf`: 9 structural/adversarial
  checks. Parser fixtures are never loaded as inference models.
- **PASS:** Android NDK arm64-v8a CMake Release build of `coffee_llama`; pinned
  source audit archive SHA-256 verified with `certutil -hashfile ... SHA256`.
- **PASS:** `cmake --build .cxx/native-check-x86 --target coffee_llama -j 6`:
  x86_64 Release shared library compiled. `llvm-readelf -l` on the arm64 library
  shows 0x4000 LOAD alignment; packaged native dependencies still need APK review.
- **PASS, structural validation only:** actual verified Qwen2.5-0.5B Instruct
  Q4_K_M file (491,400,032 bytes), SHA-256
  `74a4da8c9fdbcd15bd1f6d01d621410d31c6fc00986f5eb687824e7b93d7a9db`, accepted by
  the bounded GGUF guard: hidden width 896, 24 layers, 14 attention heads,
  2 KV heads. This was a host parser check, not successful native inference.
- **PASS, host reference generation:** actual quantized ONNX CPU inference for
  three requested coffee texts generated
  `app/src/androidTest/assets/embedding-parity.json`: 384 dimensions per text,
  exact real tokenizer IDs and model/vocabulary/tokenizer SHA-256 provenance.
  Reference ONNX Runtime 1.23.2, tokenizers 0.22.1, Torch 2.6.0+cu124 on CPU;
  no padding for dynamic input, attention-mask float32 mean/L2. JSON tolerance
  is 0.0001; Android parity remains a separate physical acceptance check.
- **PENDING:** ONNX physical reference parity and native model
  load/stream/cancel/unload, low-RAM and airplane-mode checks
  after app integration. Device speed/cancellation latency require real receipts.
