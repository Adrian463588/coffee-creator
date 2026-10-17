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
- [x] Remote source CI passed; public/latest v1.1.0 APK and checksum file downloaded
  without authentication and verified against the build. README/history/topics/
  description complete. Exact release/source receipt in docs/VERIFICATION.md.

## Sprint 03: Manual Brewing & Grinder Intelligence (`v1.13.0`)

- [x] **GR-01**: Curated 44-model grinder catalog searchable by brand, model, and mechanism (`HAND` vs `ELECTRIC`).
- [x] **GR-02**: Grinder model variants, burr geometry, axle bearings, and typed adjustment units; zero cross-model conversion.
- [x] **GR-03**: Safe model-specific zero calibration wizard with timestamped calibration offset storage.
- [x] **GR-04**: Typed grind settings with mechanism-aware input validation.
- [x] **GR-05**: Manufacturer evidence overlay displaying verified ranges alongside community settings without overwriting.
- [x] **GR-06**: Owned grinder profile persistence tracking burr wear and custom zero offset.
- [x] **GR-07**: Grind range finder filtering by grinder model, brewer, and brew method.
- [x] **GR-08**: Dial-in assistant recommending reversible single-click finer/coarser experiments from user taste feedback.
- [x] **GR-09**: Grind history logging recording grinder revision, offset, and dial setting in brew journal.
- [x] **GR-10**: Grinder compatibility guard warning when filter-specialized grinders are selected for espresso.
- [x] **GR-11**: Optional Kruve sieve/laser particle size measurement fields.
- [x] **GR-12**: Calibration profile export and import via versioned JSON schema.
- [x] **BR-01**: Curated 26-dripper catalog with unknown facts preserved as `UNKNOWN`.
- [x] **BR-02**: Brewer geometry, flow mode, and interactive valve toggle (`OPEN` vs `CLOSED`).
- [x] **BR-03**: Filter paper compatibility matrix checking shape and size fit.
- [x] **BR-04**: Manual brew composer integrating bean, grinder, dripper, filter, water, and pour plan.
- [x] **BR-05**: Ratio scaling with pure Kotlin precision calculation and immutable recipe revisions.
- [x] **BR-06**: Bloom and staged pour planner with mass validation.
- [x] **BR-07**: Tabular countdown/elapsed brew chronometer with pause/resume and haptics.
- [x] **BR-08**: Manual weigh-ins supporting setups without BLE scales.
- [x] **BR-09**: Brew diagnostics recording drawdown, clogging, bypass, and channeling observations.
- [x] **BR-10**: User water mineral chemistry and temperature tracking.
- [x] **BR-11**: Support for 9 manual brew methods and pour strategies.
- [x] **BR-12**: 80 World Champion recipes linked to active brew timer and template library.
- [x] **BR-13**: 221-descriptor flavor wheel integration and Roda Rasa vocabulary.
- [x] **BR-14**: Side-by-side A/B brew comparison with deterministic delta calculus.
- [x] **BR-15**: Typed Kotlin AI tools: `lookup_grinder_range`, `recommend_dial_in_step`, `lookup_dripper_geometry`.
- [x] **BR-16**: 48dp touch targets, TalkBack accessibility, and high-density Samsung display layout.
- [x] **BR-17**: Timestamped video reference audit documented in OpenSpec proposal.
- [x] **BR-18**: Additive migration preserving all Sprint 01 and Sprint 02 user records.
- [x] **Hardware Gate**: Designated ADB device `RRCN3008VYE` (Samsung Galaxy S20 Ultra 5G / `SM-G988B`) verified with zero-data-loss installation (`adb -s RRCN3008VYE install -r -d`).

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
