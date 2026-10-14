# Physical offline AI acceptance contract

Suite: `com.coffeecreator.app.core.ai.OfflineAiDeviceTest`.
Authoring owner: `codex/native`; integrator owns Gradle/APKs and ADB execution.
This note is a test contract. No device result is asserted before execution.

Prerequisites: supported Android API/ABI and adequate free RAM; target APK includes
the verified MiniLM ONNX and vocabulary assets; test APK includes
`embedding-parity.json`. The integrator stages the verified 491,400,032-byte Qwen
GGUF at target-context `filesDir/native-test/qwen2.5-0.5b-instruct-q4_k_m.gguf`.
Tests verify asset SHA-256 before inference. Run with airplane mode enabled;
network isolation is an independently observed device gate, not inferred from
this source file.

- MiniLM: compare exact WordPiece IDs/attention masks and all 384 actual vector
  dimensions for each of three real host-reference texts; maximum absolute
  error <=0.0001. Log measured per-case error/elapsed time locally.
- GGUF: actual load and streamed nonblank text, measured load time and TTFT;
  cancel an active 256-token-budget generation after its first emitted delta,
  require cancellation completion <=5 seconds, then unload/reload/generate/unload.
  No language accuracy or coffee expertise is claimed by a nonblank-output check.
- Malformed GGUF: reject its header with an actionable failure state; engine
  remains uninitialized and can unload. This verifies engine/process survival;
  manual UI navigation is covered by the integrator's separate UI journeys.

Resource ownership: each test creates a unique `filesDir/offline-ai-<UUID>`
directory and deletes only its exact generated model/vocabulary/malformed files.
No recursive deletion, application graph, database, user catalog, chat history or
user-granted SAF location is accessed. The integrator-owned Qwen file is preserved.
All engine handles/sessions close in `finally`, including cancellation failures.

Execution status: **NOT RUN** in the isolated worktree. Per integrator request,
this agent does not run Gradle or ADB concurrently with the integration build.
Acceptance requires the integrator's actual instrumentation output and locally
captured `CoffeeCreatorDeviceTest` receipts.
