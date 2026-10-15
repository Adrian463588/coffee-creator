# Coffee Creator implementation contract

Date: 2026-10-14 (Asia/Jakarta). Authority: all 14 Markdown files in
`coffee-creator-blueprint`, especially PRD, AGENTS, DESIGN and ARCHITECTURE.

The existing v1.0.0 contains simulated inference/downloads, volatile storage,
invented measurements/citations and a count-only backup. Its completed checklist
is not acceptance evidence. This change replaces those paths with real behavior.

## Workflow and ownership

Use SPEC / CONTRACT / failing BDD TEST / IMPLEMENT / VERIFY / independent REVIEW.
Follow OpenSpec proposal/design/tasks artifacts and BMAD planner, architect,
developer and reviewer roles. References inspected on 2026-10-14:
https://github.com/Fission-AI/OpenSpec and
https://github.com/bmad-code-org/BMAD-METHOD . These are authoring methods;
neither they nor RTK/Ponytail/Caveman are application dependencies.

One integrator owns Gradle, navigation, UI and integration. Domain, persistence
and native-runtime work use isolated Git worktrees and explicit file ownership.
Read-only audits can run against the same checkout. No concurrent shared edits.

## Architecture and contracts

- Modular monolith: pure Kotlin `core/model` and `core/engine`; Android Room
  persistence in `core/data`; SAF/downloads in `core/filesystem`; local adapters
  and grounded retrieval in `core/ai`; Compose / ViewModel presentation in `ui`.
- Immutable screen state, intents and lifecycle-aware StateFlow collection.
  Constructor injection; one application-owned dependency graph. No mutable
  global repository. IO/native work on background dispatchers.
- Room transactionally stores actual records, document chunks and search index.
  Settings and draft state persist; schema migrations cannot erase good data.
- Decimal locale input, explicit measurement provenance and nullable absent
  measurements. Unit wrappers and deterministic BigDecimal calculations share
  one validation boundary. No inferred TDS or grams/mL conversion without density.
- Actual GGUF native inference behind a pinned llama.cpp adapter; serialized
  sessions, cancellation, bounded context/tokens and memory admission checks.
  A missing/unsupported model has an actionable unavailable state.
- Documents remain untrusted data. Bounded TXT/PDF/OCR extraction preserves
  actual page indices; content hashes deduplicate. Lexical/vector fusion,
  source-bound citations, conflicts and no-evidence abstention are explicit.
- Typed allowlisted tools reject missing, extra, nonfinite and out-of-range
  arguments. Step/time budgets. Saving a generated draft requires a UI approval.
- SAF operates only on real granted content URIs. Verified private staging,
  validated HTTP Range / identity, durable checkpoints and size/SHA-256 checks
  precede publication. Reinstall discovery requires a new directory grant.
- Backup contains complete versioned payload and checksum; validate all records
  before a single transaction. Corrupt/unsupported backups leave good data intact.

## Ordered implementation slices and BDD acceptance

1. **Foundation / FR-10:** pin compatible dependencies/native revision; real
   lint/detekt/ktlint/test/build gates, offline privacy defaults and CI. Given no
   assets/account/network, launch and manual navigation remain usable.
2. **FR-03 domain:** golden 18g / 270g / measured 1.30% gives approximate 19.50%;
   ratio, dilution, concentration and density conversions. Reject NaN/infinity,
   invalid units, impossible fractions, missing density and double rounding.
   Immersion retains an explicit method limitation; all results carry version,
   exact inputs, units, assumptions and provenance.
3. **FR-01 / FR-02 persistence:** provenance-tracked seed taxonomy without
   fabricated lots/measurements; custom beans/ingredients, search/filter/edit;
   recipe ingredients and safety limits, save/copy/new revision/history. Given
   restart/process death, saved data survives. Draft fields survive rotation.
   Delete requires confirmation or an undo affordance.
4. **FR-04 observations:** recipe-linked descriptors, intensity, assessor/panel
   and notes; compare revisions. Estimates are visibly distinct, uncalibrated,
   with their basis and uncertainty rather than invented confidence percentages.
5. **FR-09 backup:** SAF export/restore complete catalog, recipes, observations,
   chat and source metadata. Given a corrupt checksum/schema, restore fails
   without mutation. Explain exports and models have separate ownership.
6. **FR-06 RAG:** real file picker/TXT/text PDF, optional bundled offline OCR,
   deduplication, reindex/remove/revoked-grant recovery; persistent chunks/FTS,
   local embeddings and citations that open the original or stored chunk.
   Test direct, cross-document, conflict, numeric, injection and no-evidence cases.
7. **FR-08 assets:** real directory picker, import/manifest verification,
   explicit download, pause/resume/cancel/failed/low-storage/reboot states;
   checksum failures never install; rediscovery validates published manifests.
   Test HTTP 200 restart, valid/invalid 206, changed ETag and corrupt partial data.
8. **FR-05 / FR-07 AI:** actual load/stream/cancel/unload, personas/parameters,
   prompt presets, exact tool output cards, generated draft confirmation. Test
   malformed tools, unknown tools, cancellation and unsupported models.
9. **UI / DESIGN:** nine working screens, five primary destinations and secondary
   routes; semantic M3 tokens, light/dark, 48dp controls, responsive phone/rail,
   large font, loading/empty/error/recovery. Progress reflects executed work.
10. **Quality / delivery:** run unit/BDD, Room/SAF integration, Compose UI and
    physical ADB offline journeys; inspect screenshots; record device/API/ABI,
    crash logs and timing. Independent review. Only then commit/push and publish
    a versioned debug APK with changelog, checksum and historical download links.

## Regression and crash prevention

Tests precede deterministic changes. Validate imports completely before writes;
use database transactions, bounded reads, exclusive indexing/inference/download
sessions, cancellation propagation and lifecycle cleanup. Never swallow coroutine
cancellation. Guard native allocation/unsupported ABI, memory and malformed
models. Downloads use private partial bytes and remove incomplete published
files. No runtime telemetry/cloud APIs, inferred SAF paths, synthetic evidence,
global reset, destructive migration or broad lint suppression.

## Verification evidence and rollback

Record every executed command and counts in `docs/VERIFICATION.md`; checklist
entries stay pending until their acceptance layer executes. Device-only and
provider/model matrix checks are distinct from JVM/build checks. Performance and
sensory accuracy are measured only; no production-grade claim from a debug build.

Git began clean on `main`, remote `Adrian463588/coffee-creator`, historical
release v1.0.0. Keep that history. Allowlist changed source/docs; keep downloaded
weights, SDK paths, caches and local signing secrets ignored. Reversible source
rollback uses the prior commit; user-owned SAF files/data are never removed by
developer cleanup. User already authorized commit, push and GitHub debug release.
