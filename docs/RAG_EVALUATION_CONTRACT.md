# Offline RAG fixture and device evaluation contract

Scope: PRD FR-03, FR-06, FR-07 and section 10. This work owns only
`app/src/androidTest/assets/rag-evaluation.json`,
`app/src/androidTest/java/com/coffeecreator/app/core/ai/RagEvaluationDeviceTest.kt`
and this document. Production code, user documents, application databases and
seed data are outside the change.

## Plan and acceptance

1. Author exactly 100 questions: 40 direct facts, 20 cross-document, 20 numerical
   tool cases, 10 source conflicts and 10 meaningful unanswerable questions.
2. Freeze source text, required chunk IDs, answer expectations, numeric arguments
   and expected tool values in the test asset. Describe provenance explicitly.
3. Stage actual TXT files, read them through the debug FileProvider, derive their
   chunks and TXT citations, then persist them in an isolated Room/FTS4 database.
4. Copy and hash-check bundled ONNX/vocabulary assets, index with actual ONNX
   embeddings and a UUID-named ObjectBox store, then close and reopen storage.
5. Run all questions offline, report retrieval/abstention/tool measurements, and
   check source integrity, direct-fact retrieval and deterministic tool results.
6. Close all owned resources and remove only this run's files/database/cache.

Rollback removes these three test/documentation files or reverts their scoped
commit. It never resets application records or deletes user-selected SAF data.

## Fixture provenance and limits

This is an **authored QA fixture**, not an independently adjudicated barista,
supplier, scientific or production corpus. Its factual paragraphs restate the
repository's PRD/AGENTS calculation and provenance contracts; method caveats also
follow the existing `CoffeeScienceEngine` contract. The test freezes those
paragraphs rather than fetching documents or evaluating against mutable sources.
Expected answer text is a review aid, not a generated answer or an LLM grader.

The fixture contains 31 TXT documents and 61 chunks, including one explicitly
untrusted prompt-injection sample. Retrieval/citation checks keep its malicious
page claim as source text; they do not establish LLM prompt-injection resistance.
Conflict notes deliberately disagree on invented QA planning values and are
marked as test data in their titles and text. They test whether both pieces of
evidence are retrieved; they establish no real brewing recommendation. Numeric
inputs are authored golden test inputs, not laboratory measurements. No fixture
document, conflict value or answer is application seed data.

The JSON's source text is materialized as UTF-8 TXT in
`files/qa-sources/rag-<UUID>/`. The existing **debug-only** FileProvider authority
`com.coffeecreator.app.qa.files` supplies real readable `content://` URIs. Chunks
are consecutive source paragraphs separated by two LF characters, with stable
IDs `<documentId>:chunk:<zero-based index>`. Every document has `pageCount=0` and
every chunk has `pageNumber=0`; citations use the model's TXT citation formatter.
This tests source integrity and local stored citations, not SAF permission
regrant, PDF/OCR parsing or a user tapping a citation.

## Measurement definitions

All retrieval queries request at most five chunks. Exact required chunk sets are
frozen before evaluation. Alternate evidence must be reviewed and versioned in
the fixture rather than silently broadening gold labels after observing results.

- **Recall@5:** macro mean of `|retrieved IDs intersect required IDs| / |required
  IDs|` over the 90 answerable questions, also reported per category. Cross-document
  and conflict cases require evidence from two distinct documents. A second
  measurement reports complete required-set retrieval per category.
- **Retrieved-source precision:** relevant retrieved chunk count divided by
  retrieved chunk count. Report micro precision both for answerable questions and
  for all 100 questions; evidence returned for unanswerable questions contributes
  zero relevant chunks to the latter. An empty denominator is JSON `null`.
  These labels measure retrieval relevance, not generated-answer faithfulness.
- **Citation integrity:** every citation exactly matches its returned stored
  chunk, and that chunk exactly matches actual fixture text/title/index/page.
  Extra, invented, mismatched or page-numbered TXT citations fail the test.
- **Abstention:** report true abstentions / 10 unanswerable questions, false
  abstentions / 90 answerable questions, and abstention precision and recall.
  An empty precision denominator is `null`. Unsupported near-domain questions
  remain in the fixture even if broad lexical/semantic matches return evidence.
- **Numerical tools:** explicitly supplied fixture tool requests execute through
  actual typed `ToolExecutor` calls. Report correct numerical cases, incorrect
  arithmetic cases, execution/output failures and metadata failures separately.
  Expected decimal outputs are frozen golden values; tolerance is `1e-9`.
  This does not test an LLM selecting or constructing the right tool request.
- **Time:** index time and each retrieval duration are measured locally. Report
  median and nearest-rank p95, with device/API/ABI, fixture/model/vocabulary hashes
  and storage reopen details. Warm queries include real embedding and retrieval;
  model copy/load and initial index are excluded from query times. No device
  speed or memory tier is promised.
- **Generated-answer citation faithfulness:** `not_measured`. No LLM generation,
  answer adjudication, semantic answer correctness or conflict resolution runs
  in this test. That release acceptance layer requires a separate independently
  adjudicated domain corpus and actual generated answers.

The summary logs measured values before asserting acceptance. Source integrity,
40/40 direct-fact source hits and 20/20 correct typed tool outputs are executable
fixture checks. Cross-document/conflict recall, precision and abstention are
reported without inventing unapproved production thresholds; all observed misses
remain visible. Passing these fixture checks alone does not establish FR-06
no-evidence behavior or production retrieval quality.

## Device protocol and evidence

Before running, install the integrated debug app and test APK with the actual
bundled MiniLM assets. Enable airplane mode and disable Wi-Fi/Ethernet/network
transports. The test checks airplane mode and an absent active network at setup,
between queries and completion; it never changes device settings or downloads
anything. These preconditions are not a packet capture or a whole-app network
audit. The test uses no HTTP/cloud/LLM adapters or application dependency graph.

The fixture pins MiniLM revision `751bff37182d3f1213fa05d7196b954e230abad9`, ONNX
SHA-256 `afdb6f1a0e45b715d0bb9b11772f032c399babd23bfc31fed1c170afc848bdb1` and
vocabulary SHA-256
`07eced375cec144d27c900241f3e339478dec958f92fddbc551f295c992038a3`.
Missing/mismatched assets fail; no embedding fallback exists. The reference
embedding-parity instrumented test remains a separate acceptance check.

The test limits fixture input size, source/question counts and result count.
Coroutine budgets are 180 seconds for indexing, 30 seconds per query and 600
seconds overall. ONNX native calls may finish before coroutine cancellation can
be observed; these budgets are cooperative, not native thread termination.

Run from the integrated checkout, for example:

```powershell
.\gradlew.bat connectedDebugAndroidTest '-Pandroid.testInstrumentationRunnerArguments.class=com.coffeecreator.app.core.ai.RagEvaluationDeviceTest'
```

`CoffeeCreatorRagEval` logcat entries contain one source/setup receipt, one result
per question and one summary. The summary is also sent through instrumentation
status key `rag_evaluation_summary`. Capture those receipts and the actual test
report before publishing numbers. Fixture/model hashes identify the run inputs;
the UUID identifies only isolated runtime storage.

The test owns a UUID Room database, a UUID ObjectBox name under
`files/objectbox/`, and a UUID fixture directory. It verifies these destinations
did not already exist, closes stores before cleanup and verifies canonical
ownership before deletion. Parent directories, application defaults, models
staged by other tests and external/user files are preserved.

**Execution status at authoring:** Android compilation, instrumentation,
physical-device retrieval, benchmark values and independent domain adjudication
have not been run by this QA authoring task. The integrator owns those checks.
