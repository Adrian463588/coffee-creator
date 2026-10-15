# Coffee Creator — implementation and acceptance ledger

Updated 2026-10-15. PRD.md remains authoritative. Checked implementation items
do not imply every physical-device, scientific or production gate passed.
This replaces prototype claims that incorrectly treated simulated AI/download/
storage and an APK build as complete acceptance. Original v1.0.0 history and
release remain available. No user records were removed.

## Specification and architecture

- [x] Read fourteen blueprint Markdown documents before application code;
  architecture, invariants and BDD contracts in `docs/IMPLEMENTATION_PLAN.md`
  and `openspec/changes/verified-android-implementation/`.
- [x] Separate planner, domain, Android, native/RAG, UX/data and QA work in
  isolated worktrees with one integrator; preserve history and user storage.
- [x] Compose Material3, Hilt, lifecycle StateFlow, pure Kotlin engine/model,
  Room/FTS4, Proto DataStore and adapter boundaries in a modular monolith.
- [x] Pin actual build/dependency revisions and document ADRs/licenses, including
  ObjectBox native's separate binary license.

## Implemented feature slices

- [x] FR-01: durable editable/searchable bean/ingredient catalog;34 reference cards
  on fresh install, including32 sourced WCR varieties. Taxonomy is separate from
  purchased lots; unverified lot/process/roast facts stay Unknown.
- [x] FR-01: source URLs/hashes/reuse restrictions and transactional versioned
  seed upgrades preserving user edits, ID collisions, deletions and restore.
- [x] FR-02: manual draft, optional measured TDS/beverage fields, immutable save/
  copy/revisions and journal comparison. Brew has chip-selected Recipe,
  Measurements, Ingredients and Tools cards.
- [x] FR-03: validated ratio, approximate EY, dilution, mass concentration,
  density conversion and supplier/food-grade/allergen constraints. Results
  retain exact inputs, units, method, assumptions and provenance.
- [x] FR-04: persisted human sensory observations, separately typed uncalibrated
  estimates with uncertainty and original radar artwork.
- [x] FR-05: pinned real CPU llama.cpp GGUF stream/cancel/load, bounded context,
  editable personas and memory/architecture/control-token safeguards.
- [x] FR-06: bounded SAF TXT/text-PDF/offline English OCR, deduplication,
  retained provenance, actual ONNX embeddings, Room FTS4/ObjectBox retrieval,
  source inspection and no-evidence abstention.
- [x] FR-07: allowlisted validated Kotlin tools; generated quantities require
  input review and proposed recipe writes require confirmation. No model shell.
- [x] FR-08: explicit HTTPS transfers, Range/identity checkpoints, size/SHA-verified
  staged SAF publication, cancellation and rediscovery. Pinned Qwen/Kafi/Gemma4
  catalog artifacts; no LLM weights bundled.
- [x] FR-09: complete schema3 backup, safe schema2 migration, validation before
  transactional replacement, explicit SAF export and permission recovery.
- [x] FR-10: local preferences/personas, optional AI, offline operation, no cloud
  inference/telemetry, cleartext blocked and automatic backup disabled.

## Executed evidence and distribution

Exact commands/results and requirement mapping: `docs/VERIFICATION.md`.
Historical physical evidence is separate from current local tests.

- [x] Domain golden/boundary, tool/schema, backup, embedding contract, retrieval,
  transfer-policy and pinned model-catalog JVM regressions.
- [x] Real local Room migration/restore/seed preservation and Compose activity
  section switching/recreation, compact font2 and SAF-required actions.
- [x] Dataset source audit:32 primary source records match checked facts.
- [x] Host GGUF structural, Gemma text-format and prompt-boundary checks:83 passed.
- [x] Earlier physical native/ONNX/OCR/SAF/repository/journey tests executed;
  later failed RAG retest retained without replacing it with host proof.
- [x] Final aggregate static/JVM/build gates:110/110 tests;83 host C++ checks;
  debug/androidTest/benchmark APK builds and16KiB alignment/signature/checksum.
- [ ] Remote CI and downloadable release verification; tick after command evidence.

## Remaining production acceptance

User instruction: test without ADB device. These stay open. Compiling authored
instrumented tests does not execute them.

- [ ] Current Android RAG retest: last physical direct facts39/40 failed;
  subsequent host reproduction40/40 is regression evidence, not Android proof.
- [ ] Generated-answer faithfulness and natural-language tool-selection accuracy
  with independently adjudicated questions/domain reviewers.
- [ ] Current Brew/model UI and ONNX/native adapters on physical devices;
  all layout tiers, TalkBack, dark mode, reduced motion and field usability.
- [ ] Full Kafi/Gemma transfer, SAF rediscovery and actual inference on suitable
  RAM tiers. Gemma GGUF is3.11GB, different from the2.58GB LiteRT artifact.
- [ ] Real HTTP interruption/reboot/low-storage and multiple SAF providers.
- [ ] Full uninstall/reinstall/regrant/restore on an expendable environment.
  No user installation was erased for testing.
- [ ] Successful startup/frame macrobenchmark and generated Baseline Profile.
  Signed benchmark APK compilation alone is insufficient.
- [ ] Reference-device budgets, broader ABI/API/RAM compatibility, independent
  network audit and barista/sensory-panel validation.
- [ ] Production signing/Play distribution. Current release is a debug preview.

Do not mark the entire specification complete while acceptance gates remain open.
