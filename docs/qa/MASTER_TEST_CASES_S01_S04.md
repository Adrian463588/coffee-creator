# Master BDD Test Cases — Sprint 01–04

All cases below are BDD-oriented test designs, not executed tests. Their status is **NOT_RUN** until a receipt is recorded. A source marker, unrelated test report, or screenshot does not change that status. Several dimensions still need case-specific fixture values, observable oracles, and justified N/A decisions before execution; no dimension coverage is credited from these prompts alone. Ten legacy rows record historical screen/data preservation checks in addition to the 209 blueprint IDs.

## How to execute safely

Use a disposable seeded app profile and a copy of each supported historical database. Keep the installed user profile, database, SAF trees, and model files untouched. Start offline except for cases explicitly testing a user-requested transfer. Capture device/build identity, test report, logs, and before/after IDs for every stateful case.

## Cross-cutting cases

### TC-CROSS-01 — Airplane mode and no-model degradation

- Linked requirement: All offline requirements
- Title: Airplane mode and no-model degradation
- Priority: P0 unless the linked requirement is explicitly lower.
- Preconditions: pristine disposable seeded profile; record baseline manifest; use local-only fixtures.
- Fixtures: exact fixture named by the linked specification; no live user data.
- Test steps (BDD): Given the stated environment, when with the app started in airplane mode and no local llm installed, complete catalog lookup, manual recipe, brew timer, journal, sensory observation, and cupping, then manual paths remain usable; AI actions state unavailable and never fabricate output.
- Expected result: manual paths remain usable; AI actions state unavailable and never fabricate output.
- Actual result: Not executed.
- Execution environment: Not run; device/profile-specific.
- Automation type: Compose UI + instrumented device.
- Screenshot/log/test-report evidence: Not generated; see docs/qa/TEST_EXECUTION_REPORT.md.
- Result: NOT_RUN.
- Defect ID: See docs/qa/DEFECT_REGISTER.csv after execution.

### TC-CROSS-02 — Persistence and process recreation

- Linked requirement: All stateful requirements
- Title: Persistence and process recreation
- Priority: P0 unless the linked requirement is explicitly lower.
- Preconditions: pristine disposable seeded profile; record baseline manifest; use local-only fixtures.
- Fixtures: exact fixture named by the linked specification; no live user data.
- Test steps (BDD): Given the stated environment, when create one record of each affected kind, record ids/counts, background the app, rotate, force-stop only the disposable app, relaunch, and compare all values and relationships, then saved data remains exact; drafts recover according to spec; no duplicate submissions.
- Expected result: saved data remains exact; drafts recover according to spec; no duplicate submissions.
- Actual result: Not executed.
- Execution environment: Not run; device/profile-specific.
- Automation type: Instrumented Android + Room.
- Screenshot/log/test-report evidence: Not generated; see docs/qa/TEST_EXECUTION_REPORT.md.
- Result: NOT_RUN.
- Defect ID: See docs/qa/DEFECT_REGISTER.csv after execution.

### TC-CROSS-03 — Sequential database upgrade and backup compatibility

- Linked requirement: All persisted requirements
- Title: Sequential database upgrade and backup compatibility
- Priority: P0 unless the linked requirement is explicitly lower.
- Preconditions: pristine disposable seeded profile; record baseline manifest; use local-only fixtures.
- Fixtures: exact fixture named by the linked specification; no live user data.
- Test steps (BDD): Given the stated environment, when restore each supported schema fixture in sequence into a disposable database, run migrations, then verify stable ids, counts, row json, foreign keys, revisions, and seed overrides. decode schema-v2 and current backups containing non-empty recipes and chat messages, then additive migration, full compatibility, no destructive fallback or lost rows.
- Expected result: additive migration, full compatibility, no destructive fallback or lost rows.
- Actual result: Not executed.
- Execution environment: Not run; device/profile-specific.
- Automation type: Room migration + JVM/Android instrumentation.
- Screenshot/log/test-report evidence: Not generated; see docs/qa/TEST_EXECUTION_REPORT.md.
- Result: NOT_RUN.
- Defect ID: See docs/qa/DEFECT_REGISTER.csv after execution.

### TC-CROSS-04 — Navigation, back, and deep link

- Linked requirement: Every visible route and action
- Title: Navigation, back, and deep link
- Priority: P0 unless the linked requirement is explicitly lower.
- Preconditions: pristine disposable seeded profile; record baseline manifest; use local-only fixtures.
- Fixtures: exact fixture named by the linked specification; no live user data.
- Test steps (BDD): Given the stated environment, when visit every route, invoke each primary action, navigate back, and launch each documented deep link if any, then destination and transient state are correct; no undocumented route is assumed; no deep-link capability is marked PASS when none exists.
- Expected result: destination and transient state are correct; no undocumented route is assumed; no deep-link capability is marked PASS when none exists.
- Actual result: Not executed.
- Execution environment: Not run; device/profile-specific.
- Automation type: Compose UI + instrumented Android.
- Screenshot/log/test-report evidence: Not generated; see docs/qa/TEST_EXECUTION_REPORT.md.
- Result: NOT_RUN.
- Defect ID: See docs/qa/DEFECT_REGISTER.csv after execution.

### TC-CROSS-05 — TalkBack, touch targets, 200% font, and adaptive layout

- Linked requirement: All primary workflows
- Title: TalkBack, touch targets, 200% font, and adaptive layout
- Priority: P0 unless the linked requirement is explicitly lower.
- Preconditions: pristine disposable seeded profile; record baseline manifest; use local-only fixtures.
- Fixtures: exact fixture named by the linked specification; no live user data.
- Test steps (BDD): Given the stated environment, when run the critical brew and sensory journeys on compact, medium, and expanded windows at default and 200% font scale; use talkback and keyboard/switch navigation. measure actionable bounds, then reading order, labels, contrast, non-color cues, 48dp targets, and core actions remain operable.
- Expected result: reading order, labels, contrast, non-color cues, 48dp targets, and core actions remain operable.
- Actual result: Not executed.
- Execution environment: Not run; device/profile-specific.
- Automation type: Instrumented Android + manual TalkBack.
- Screenshot/log/test-report evidence: Not generated; see docs/qa/TEST_EXECUTION_REPORT.md.
- Result: NOT_RUN.
- Defect ID: See docs/qa/DEFECT_REGISTER.csv after execution.

### TC-CROSS-06 — Untrusted files and prompt injection

- Linked requirement: Import, AI, backup, and model features
- Title: Untrusted files and prompt injection
- Priority: P0 unless the linked requirement is explicitly lower.
- Preconditions: pristine disposable seeded profile; record baseline manifest; use local-only fixtures.
- Fixtures: exact fixture named by the linked specification; no live user data.
- Test steps (BDD): Given the stated environment, when import malformed pdf/archive/json and a document containing instructions to ignore policy, reveal secrets, or call a network tool. query with and without evidence, then bounded parser errors, document remains data, allowlisted tools only, no arbitrary execution/network, citations only to retrieved source spans, abstention when unsupported.
- Expected result: bounded parser errors, document remains data, allowlisted tools only, no arbitrary execution/network, citations only to retrieved source spans, abstention when unsupported.
- Actual result: Not executed.
- Execution environment: Not run; device/profile-specific.
- Automation type: JVM adversarial tests + instrumented SAF.
- Screenshot/log/test-report evidence: Not generated; see docs/qa/TEST_EXECUTION_REPORT.md.
- Result: NOT_RUN.
- Defect ID: See docs/qa/DEFECT_REGISTER.csv after execution.

### TC-CROSS-07 — Units, uncertainty, and measurement authority

- Linked requirement: All measurement and calculation features
- Title: Units, uncertainty, and measurement authority
- Priority: P0 unless the linked requirement is explicitly lower.
- Preconditions: pristine disposable seeded profile; record baseline manifest; use local-only fixtures.
- Fixtures: exact fixture named by the linked specification; no live user data.
- Test steps (BDD): Given the stated environment, when run golden/boundary inputs including 18g dose × 270g beverage × 1.30% measured tds, zero/negative dose, density-free ml↔g, unmeasured tds/ey, and user-observed vs predicted sensory values, then 19.50% approximate EY for the golden case; invalid conversions are rejected; unknown remains unknown; estimates are labeled.
- Expected result: 19.50% approximate EY for the golden case; invalid conversions are rejected; unknown remains unknown; estimates are labeled.
- Actual result: Not executed.
- Execution environment: Not run; device/profile-specific.
- Automation type: JVM golden/property tests.
- Screenshot/log/test-report evidence: Not generated; see docs/qa/TEST_EXECUTION_REPORT.md.
- Result: NOT_RUN.
- Defect ID: See docs/qa/DEFECT_REGISTER.csv after execution.

### TC-CROSS-08 — URI grant loss and provider capability

- Linked requirement: SAF import/export/model/backup features
- Title: URI grant loss and provider capability
- Priority: P0 unless the linked requirement is explicitly lower.
- Preconditions: pristine disposable seeded profile; record baseline manifest; use local-only fixtures.
- Fixtures: exact fixture named by the linked specification; no live user data.
- Test steps (BDD): Given the stated environment, when use a disposable saf provider with persistable grants, revoke permission, switch to a provider without seek support, and retry, then regrant/recovery is explicit, no path inferred from URI, partial file is never published as Installed, and unsupported resume restarts safely.
- Expected result: regrant/recovery is explicit, no path inferred from URI, partial file is never published as Installed, and unsupported resume restarts safely.
- Actual result: Not executed.
- Execution environment: Not run; device/profile-specific.
- Automation type: Instrumented Android + manual provider matrix.
- Screenshot/log/test-report evidence: Not generated; see docs/qa/TEST_EXECUTION_REPORT.md.
- Result: NOT_RUN.
- Defect ID: See docs/qa/DEFECT_REGISTER.csv after execution.

### TC-CROSS-09 — Unsupported and disconnected scale

- Linked requirement: BLE-dependent requirements
- Title: Unsupported and disconnected scale
- Priority: P0 unless the linked requirement is explicitly lower.
- Preconditions: pristine disposable seeded profile; record baseline manifest; use local-only fixtures.
- Fixtures: exact fixture named by the linked specification; no live user data.
- Test steps (BDD): Given the stated environment, when without a compatible scale, open brew and try scale actions; then disconnect a supported test fixture during a real hardware run, then manual weighing remains available, simulated readings are never labeled real, and unsupported BLE acceptance remains BLOCKED.
- Expected result: manual weighing remains available, simulated readings are never labeled real, and unsupported BLE acceptance remains BLOCKED.
- Actual result: Not executed.
- Execution environment: Not run; device/profile-specific.
- Automation type: JVM simulator + compatible BLE hardware required.
- Screenshot/log/test-report evidence: Not generated; see docs/qa/TEST_EXECUTION_REPORT.md.
- Result: NOT_RUN.
- Defect ID: See docs/qa/DEFECT_REGISTER.csv after execution.

### TC-CROSS-10 — Transfer and inference interruption

- Linked requirement: Download and local inference features
- Title: Transfer and inference interruption
- Priority: P0 unless the linked requirement is explicitly lower.
- Preconditions: pristine disposable seeded profile; record baseline manifest; use local-only fixtures.
- Fixtures: exact fixture named by the linked specification; no live user data.
- Test steps (BDD): Given the stated environment, when for a user-requested model download, interrupt at several byte offsets, change etag, return invalid content-range, cancel, and simulate low storage. load/cancel an actual compatible model under a recorded device memory profile, then resume only on validated identity/range, verified SHA/size before publication, cancellation releases resources, and unsupported/OOM outcomes recover without app crash.
- Expected result: resume only on validated identity/range, verified SHA/size before publication, cancellation releases resources, and unsupported/OOM outcomes recover without app crash.
- Actual result: Not executed.
- Execution environment: Not run; device/profile-specific.
- Automation type: Transfer unit tests + real SAF/provider and device AI.
- Screenshot/log/test-report evidence: Not generated; see docs/qa/TEST_EXECUTION_REPORT.md.
- Result: NOT_RUN.
- Defect ID: See docs/qa/DEFECT_REGISTER.csv after execution.

### TC-CROSS-11 — Measured startup, scrolling, timer, and AI memory

- Linked requirement: Performance claims
- Title: Measured startup, scrolling, timer, and AI memory
- Priority: P0 unless the linked requirement is explicitly lower.
- Preconditions: pristine disposable seeded profile; record baseline manifest; use local-only fixtures.
- Fixtures: exact fixture named by the linked specification; no live user data.
- Test steps (BDD): Given the stated environment, when run cold start, screen scroll, timer drift, and inference memory measurements on a recorded release build/device; repeat enough samples to report distribution and conditions, then report measurements and uncertainty only; do not invent budgets or infer sensory quality.
- Expected result: report measurements and uncertainty only; do not invent budgets or infer sensory quality.
- Actual result: Not executed.
- Execution environment: Not run; device/profile-specific.
- Automation type: Macrobenchmark/device instrumentation.
- Screenshot/log/test-report evidence: Not generated; see docs/qa/TEST_EXECUTION_REPORT.md.
- Result: NOT_RUN.
- Defect ID: See docs/qa/DEFECT_REGISTER.csv after execution.

### TC-CROSS-12 — Network, consent, deletion, and local logs

- Linked requirement: Privacy and settings
- Title: Network, consent, deletion, and local logs
- Priority: P0 unless the linked requirement is explicitly lower.
- Preconditions: pristine disposable seeded profile; record baseline manifest; use local-only fixtures.
- Fixtures: exact fixture named by the linked specification; no live user data.
- Test steps (BDD): Given the stated environment, when capture network activity during airplane/offline use and a user-requested transfer; inspect manifest, dependencies, logs, export/delete behavior and explicit consent, then no telemetry/cloud inference; asset networking only after user action; deletion/export effects are accurate and local.
- Expected result: no telemetry/cloud inference; asset networking only after user action; deletion/export effects are accurate and local.
- Actual result: Not executed.
- Execution environment: Not run; device/profile-specific.
- Automation type: Static security review + instrumented/manual.
- Screenshot/log/test-report evidence: Not generated; see docs/qa/TEST_EXECUTION_REPORT.md.
- Result: NOT_RUN.
- Defect ID: See docs/qa/DEFECT_REGISTER.csv after execution.

## Cross-sprint end-to-end journeys

### E2E-01

- Linked requirement: cross-sprint (see linked FR/S2/GR/BR/S4/EDU rows in RTM).
- Title: Home → Bean → Grinder → Dripper → Recipe → Timer → Journal → Roda Rasa → Cupping → Report → Backup → Restore
- Priority: P0.
- Preconditions: disposable seeded profile plus immutable baseline snapshot; all referenced source IDs recorded.
- Fixtures: only verified fixtures/catalog rows; actual user samples are required for sensory or instrument measurements.
- Test steps (BDD): Given a clean disposable profile and the stated starting route, when the operator follows Home → Bean → Grinder → Dripper → Recipe → Timer → Journal → Roda Rasa → Cupping → Report → Backup → Restore, then All linked records and IDs persist through export/restore; history is immutable.
- Expected result: All linked records and IDs persist through export/restore; history is immutable.
- Actual result: Not executed.
- Execution environment: Not run; disposable app profile and full device matrix not available.
- Automation type: Compose UI + repository/Room instrumentation; manual sensory/device steps where required.
- Screenshot/log/test-report evidence: Not generated.
- Result: NOT_RUN.
- Defect ID: See defect register.

### E2E-02

- Linked requirement: cross-sprint (see linked FR/S2/GR/BR/S4/EDU rows in RTM).
- Title: Resep Juara → filter and inspect source → duplicate → change dose → scale staged pours → brew offline → compare → sensory note
- Priority: P0.
- Preconditions: disposable seeded profile plus immutable baseline snapshot; all referenced source IDs recorded.
- Fixtures: only verified fixtures/catalog rows; actual user samples are required for sensory or instrument measurements.
- Test steps (BDD): Given a clean disposable profile and the stated starting route, when the operator follows Resep Juara → filter and inspect source → duplicate → change dose → scale staged pours → brew offline → compare → sensory note, then Original template stays immutable; copied revision and journal preserve attribution and units.
- Expected result: Original template stays immutable; copied revision and journal preserve attribution and units.
- Actual result: Not executed.
- Execution environment: Not run; disposable app profile and full device matrix not available.
- Automation type: Compose UI + repository/Room instrumentation; manual sensory/device steps where required.
- Screenshot/log/test-report evidence: Not generated.
- Result: NOT_RUN.
- Defect ID: See defect register.

### E2E-03

- Linked requirement: cross-sprint (see linked FR/S2/GR/BR/S4/EDU rows in RTM).
- Title: Roda Rasa → search descriptor → select → save → reopen → reuse in CVA descriptive
- Priority: P0.
- Preconditions: disposable seeded profile plus immutable baseline snapshot; all referenced source IDs recorded.
- Fixtures: only verified fixtures/catalog rows; actual user samples are required for sensory or instrument measurements.
- Test steps (BDD): Given a clean disposable profile and the stated starting route, when the operator follows Roda Rasa → search descriptor → select → save → reopen → reuse in CVA descriptive, then Descriptor identity and meaning remain stable; existing observation is not overwritten.
- Expected result: Descriptor identity and meaning remain stable; existing observation is not overwritten.
- Actual result: Not executed.
- Execution environment: Not run; disposable app profile and full device matrix not available.
- Automation type: Compose UI + repository/Room instrumentation; manual sensory/device steps where required.
- Screenshot/log/test-report evidence: Not generated.
- Result: NOT_RUN.
- Defect ID: See defect register.

### E2E-04

- Linked requirement: cross-sprint (see linked FR/S2/GR/BR/S4/EDU rows in RTM).
- Title: CVA → blind samples → descriptive → affective → permissioned extrinsic reveal → lock → amend with reason → export → reopen/compare
- Priority: P0.
- Preconditions: disposable seeded profile plus immutable baseline snapshot; all referenced source IDs recorded.
- Fixtures: only verified fixtures/catalog rows; actual user samples are required for sensory or instrument measurements.
- Test steps (BDD): Given a clean disposable profile and the stated starting route, when the operator follows CVA → blind samples → descriptive → affective → permissioned extrinsic reveal → lock → amend with reason → export → reopen/compare, then Blindness audit, immutable revisions, standards provenance, and real file exports are verifiable.
- Expected result: Blindness audit, immutable revisions, standards provenance, and real file exports are verifiable.
- Actual result: Not executed.
- Execution environment: Not run; disposable app profile and full device matrix not available.
- Automation type: Compose UI + repository/Room instrumentation; manual sensory/device steps where required.
- Screenshot/log/test-report evidence: Not generated.
- Result: NOT_RUN.
- Defect ID: See defect register.

### E2E-05

- Linked requirement: cross-sprint (see linked FR/S2/GR/BR/S4/EDU rows in RTM).
- Title: Green lot QC → actual defects/moisture with instrument metadata → link sample to cupping → local report
- Priority: P0.
- Preconditions: disposable seeded profile plus immutable baseline snapshot; all referenced source IDs recorded.
- Fixtures: only verified fixtures/catalog rows; actual user samples are required for sensory or instrument measurements.
- Test steps (BDD): Given a clean disposable profile and the stated starting route, when the operator follows Green lot QC → actual defects/moisture with instrument metadata → link sample to cupping → local report, then Only measured values are recorded; unsupported inputs remain explicit unknown/out-of-scope states.
- Expected result: Only measured values are recorded; unsupported inputs remain explicit unknown/out-of-scope states.
- Actual result: Not executed.
- Execution environment: Not run; disposable app profile and full device matrix not available.
- Automation type: Compose UI + repository/Room instrumentation; manual sensory/device steps where required.
- Screenshot/log/test-report evidence: Not generated.
- Result: NOT_RUN.
- Defect ID: See defect register.

### E2E-06

- Linked requirement: cross-sprint (see linked FR/S2/GR/BR/S4/EDU rows in RTM).
- Title: Airplane mode + no model + no BLE → manual brew → timer → sensory observation → cupping → export
- Priority: P0.
- Preconditions: disposable seeded profile plus immutable baseline snapshot; all referenced source IDs recorded.
- Fixtures: only verified fixtures/catalog rows; actual user samples are required for sensory or instrument measurements.
- Test steps (BDD): Given a clean disposable profile and the stated starting route, when the operator follows Airplane mode + no model + no BLE → manual brew → timer → sensory observation → cupping → export, then Every manual path works without model/BLE/network and does not invent measurements.
- Expected result: Every manual path works without model/BLE/network and does not invent measurements.
- Actual result: Not executed.
- Execution environment: Not run; disposable app profile and full device matrix not available.
- Automation type: Compose UI + repository/Room instrumentation; manual sensory/device steps where required.
- Screenshot/log/test-report evidence: Not generated.
- Result: NOT_RUN.
- Defect ID: See defect register.

### E2E-07

- Linked requirement: cross-sprint (see linked FR/S2/GR/BR/S4/EDU rows in RTM).
- Title: Upgrade an old Room fixture through every supported schema version
- Priority: P0.
- Preconditions: disposable seeded profile plus immutable baseline snapshot; all referenced source IDs recorded.
- Fixtures: only verified fixtures/catalog rows; actual user samples are required for sensory or instrument measurements.
- Test steps (BDD): Given a clean disposable profile and the stated starting route, when the operator follows Upgrade an old Room fixture through every supported schema version, then IDs/counts/revisions/templates/taxonomy/cuppings/documents/manifests/foreign keys remain valid.
- Expected result: IDs/counts/revisions/templates/taxonomy/cuppings/documents/manifests/foreign keys remain valid.
- Actual result: Not executed.
- Execution environment: Not run; disposable app profile and full device matrix not available.
- Automation type: Compose UI + repository/Room instrumentation; manual sensory/device steps where required.
- Screenshot/log/test-report evidence: Not generated.
- Result: NOT_RUN.
- Defect ID: See defect register.

### E2E-08

- Linked requirement: cross-sprint (see linked FR/S2/GR/BR/S4/EDU rows in RTM).
- Title: Background, rotate, process death, low storage, grant revoke, malformed file, failed download, cancelled inference
- Priority: P0.
- Preconditions: disposable seeded profile plus immutable baseline snapshot; all referenced source IDs recorded.
- Fixtures: only verified fixtures/catalog rows; actual user samples are required for sensory or instrument measurements.
- Test steps (BDD): Given a clean disposable profile and the stated starting route, when the operator follows Background, rotate, process death, low storage, grant revoke, malformed file, failed download, cancelled inference, then Recovery is explicit and no durable record or asset is corrupted or partially published.
- Expected result: Recovery is explicit and no durable record or asset is corrupted or partially published.
- Actual result: Not executed.
- Execution environment: Not run; disposable app profile and full device matrix not available.
- Automation type: Compose UI + repository/Room instrumentation; manual sensory/device steps where required.
- Screenshot/log/test-report evidence: Not generated.
- Result: NOT_RUN.
- Defect ID: See defect register.

### E2E-09

- Linked requirement: cross-sprint (see linked FR/S2/GR/BR/S4/EDU rows in RTM).
- Title: Switch language/theme, 200% font, TalkBack, narrow and expanded layouts
- Priority: P0.
- Preconditions: disposable seeded profile plus immutable baseline snapshot; all referenced source IDs recorded.
- Fixtures: only verified fixtures/catalog rows; actual user samples are required for sensory or instrument measurements.
- Test steps (BDD): Given a clean disposable profile and the stated starting route, when the operator follows Switch language/theme, 200% font, TalkBack, narrow and expanded layouts, then Primary actions remain accessible and user data remains unchanged.
- Expected result: Primary actions remain accessible and user data remains unchanged.
- Actual result: Not executed.
- Execution environment: Not run; disposable app profile and full device matrix not available.
- Automation type: Compose UI + repository/Room instrumentation; manual sensory/device steps where required.
- Screenshot/log/test-report evidence: Not generated.
- Result: NOT_RUN.
- Defect ID: See defect register.

### E2E-10

- Linked requirement: cross-sprint (see linked FR/S2/GR/BR/S4/EDU rows in RTM).
- Title: Export/import roundtrip and repeat seed import with conflicting local edits
- Priority: P0.
- Preconditions: disposable seeded profile plus immutable baseline snapshot; all referenced source IDs recorded.
- Fixtures: only verified fixtures/catalog rows; actual user samples are required for sensory or instrument measurements.
- Test steps (BDD): Given a clean disposable profile and the stated starting route, when the operator follows Export/import roundtrip and repeat seed import with conflicting local edits, then Roundtrip is lossless; conflict handling is explicit; import is idempotent and preserves user changes.
- Expected result: Roundtrip is lossless; conflict handling is explicit; import is idempotent and preserves user changes.
- Actual result: Not executed.
- Execution environment: Not run; disposable app profile and full device matrix not available.
- Automation type: Compose UI + repository/Room instrumentation; manual sensory/device steps where required.
- Screenshot/log/test-report evidence: Not generated.
- Result: NOT_RUN.
- Defect ID: See defect register.

## Blueprint requirement cases

Each requirement below receives a linked BDD acceptance case with explicit checks for every requested dimension. Shared cases above provide common execution detail; mark an inapplicable dimension with its reason in the execution record.

### TC-REQ-FR-01 — FR-01: Coffee catalog

- Linked requirement: FR-01 (coffee-creator-blueprint/PRD.md:47).
- Title: Verify Coffee catalog and its failure/recovery behavior.
- Priority: P0.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Home → primary destination (see route inventory)'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for FR-01; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Coffee catalog', then Can search/filter/edit locally in airplane mode; missing facts are `unknown`, not silently invented; import validation prevents malformed records; schema migration retains data. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Can search/filter/edit locally in airplane mode; missing facts are `unknown`, not silently invented; import validation prevents malformed records; schema migration retains data.
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Can search/filter/edit locally in airplane mode; missing facts are `unknown`, not silently invented; import validation prevents malformed records; schema migration retains data.
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: No disposable profile; persistence not observed.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-FR-02 — FR-02: Manual formulation

- Linked requirement: FR-02 (coffee-creator-blueprint/PRD.md:51).
- Title: Verify Manual formulation and its failure/recovery behavior.
- Priority: P0.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Home → primary destination (see route inventory)'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for FR-02; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Manual formulation', then Save/copy/version a recipe; undo before destructive actions; unit consistency tested; invalid/zero/negative quantities rejected; TDS cannot be calculated from dose and water alone. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Save/copy/version a recipe; undo before destructive actions; unit consistency tested; invalid/zero/negative quantities rejected; TDS cannot be calculated from dose and water alone.
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Save/copy/version a recipe; undo before destructive actions; unit consistency tested; invalid/zero/negative quantities rejected; TDS cannot be calculated from dose and water alone.
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: No disposable profile; persistence not observed.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-FR-03 — FR-03: Calculation service

- Linked requirement: FR-03 (coffee-creator-blueprint/PRD.md:55).
- Title: Verify Calculation service and its failure/recovery behavior.
- Priority: P0.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Home → primary destination (see route inventory)'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for FR-03; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Calculation service', then Golden test: dose=18g, beverage=270g, TDS=1.30% yields 19.50%; all tools have validation, precision policy, tests and stable version identifiers; result includes method, assumptions and input provenance. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Golden test: dose=18g, beverage=270g, TDS=1.30% yields 19.50%; all tools have validation, precision policy, tests and stable version identifiers; result includes method, assumptions and input provenance.
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Golden test: dose=18g, beverage=270g, TDS=1.30% yields 19.50%; all tools have validation, precision policy, tests and stable version identifiers; result includes method, assumptions and input provenance.
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: No disposable profile; persistence not observed.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-FR-04 — FR-04: Sensory tracking and predictions

- Linked requirement: FR-04 (coffee-creator-blueprint/PRD.md:59).
- Title: Verify Sensory tracking and predictions and its failure/recovery behavior.
- Priority: P0 observations / P2 estimates.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Home → primary destination (see route inventory)'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for FR-04; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Sensory tracking and predictions', then Observed and predicted profiles differ visually and in persisted type; all estimates show basis and confidence/uncertainty; users can compare recipe revisions. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Observed and predicted profiles differ visually and in persisted type; all estimates show basis and confidence/uncertainty; users can compare recipe revisions.
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Observed and predicted profiles differ visually and in persisted type; all estimates show basis and confidence/uncertainty; users can compare recipe revisions.
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: No disposable profile; persistence not observed.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-FR-05 — FR-05: Local LLM chat

- Linked requirement: FR-05 (coffee-creator-blueprint/PRD.md:63).
- Title: Verify Local LLM chat and its failure/recovery behavior.
- Priority: P1.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Home → primary destination (see route inventory)'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for FR-05; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Local LLM chat', then Works in airplane mode after install; manual app survives LLM load failure; cancel promptly frees resources; no OOM crash on tested device tier; clear status for unsupported models. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Works in airplane mode after install; manual app survives LLM load failure; cancel promptly frees resources; no OOM crash on tested device tier; clear status for unsupported models.
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Works in airplane mode after install; manual app survives LLM load failure; cancel promptly frees resources; no OOM crash on tested device tier; clear status for unsupported models.
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: No disposable profile; persistence not observed.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-FR-06 — FR-06: Offline document RAG

- Linked requirement: FR-06 (coffee-creator-blueprint/PRD.md:67).
- Title: Verify Offline document RAG and its failure/recovery behavior.
- Priority: P1.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Home → primary destination (see route inventory)'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for FR-06; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Offline document RAG', then Import, restart, search and cite with airplane mode; repeated import deduplicates based on content hash; revoked URI access handled gracefully; unsupported types rejected clearly; no document instructions are promoted to system prompts; invalid/no evidence results in abstention. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Import, restart, search and cite with airplane mode; repeated import deduplicates based on content hash; revoked URI access handled gracefully; unsupported types rejected clearly; no document instructions are promoted to system prompts; invalid/no evidence results in abstention.
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Import, restart, search and cite with airplane mode; repeated import deduplicates based on content hash; revoked URI access handled gracefully; unsupported types rejected clearly; no document instructions are promoted to system prompts; invalid/no evidence results in abstention.
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: No disposable profile; persistence not observed.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: D-009. - Static verdict lead: PARTIAL.

### TC-REQ-FR-07 — FR-07: AI tool calls

- Linked requirement: FR-07 (coffee-creator-blueprint/PRD.md:71).
- Title: Verify AI tool calls and its failure/recovery behavior.
- Priority: P1.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Home → primary destination (see route inventory)'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for FR-07; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'AI tool calls', then Bad tool schema rejected without crash; exact Kotlin math results preserved; read-only tools cannot mutate data; no shell/browser/network tools offered to the runtime LLM. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Bad tool schema rejected without crash; exact Kotlin math results preserved; read-only tools cannot mutate data; no shell/browser/network tools offered to the runtime LLM.
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Bad tool schema rejected without crash; exact Kotlin math results preserved; read-only tools cannot mutate data; no shell/browser/network tools offered to the runtime LLM.
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: No disposable profile; persistence not observed.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-FR-08 — FR-08: Model/asset manager

- Linked requirement: FR-08 (coffee-creator-blueprint/PRD.md:75).
- Title: Verify Model/asset manager and its failure/recovery behavior.
- Priority: P2.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Home → primary destination (see route inventory)'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for FR-08; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Model/asset manager', then Interrupted transfer resumes only if remote identity and local partial data agree; ignore untrusted filenames/path traversal; file is marked Installed only after verification; after uninstall/reinstall existing published weights can be rediscovered by reselecting directory and regranting permission. App-private partial bytes and internal DB do not magically survive uninstall. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Interrupted transfer resumes only if remote identity and local partial data agree; ignore untrusted filenames/path traversal; file is marked Installed only after verification; after uninstall/reinstall existing published weights can be rediscovered by reselecting directory and regranting permission. App-private partial bytes and internal DB do not magically survive uninstall.
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Interrupted transfer resumes only if remote identity and local partial data agree; ignore untrusted filenames/path traversal; file is marked Installed only after verification; after uninstall/reinstall existing published weights can be rediscovered by reselecting directory and regranting permission. App-private partial bytes and internal DB do not magically survive uninstall.
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: No disposable profile; persistence not observed.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-FR-09 — FR-09: Backup and recovery

- Linked requirement: FR-09 (coffee-creator-blueprint/PRD.md:79).
- Title: Verify Backup and recovery and its failure/recovery behavior.
- Priority: P2.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Home → primary destination (see route inventory)'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for FR-09; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Backup and recovery', then Fresh-install restore imports matching version or performs safe migration; unsupported/corrupt exports cannot overwrite existing good data; export deletion and privacy behavior explained. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Fresh-install restore imports matching version or performs safe migration; unsupported/corrupt exports cannot overwrite existing good data; export deletion and privacy behavior explained.
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Fresh-install restore imports matching version or performs safe migration; unsupported/corrupt exports cannot overwrite existing good data; export deletion and privacy behavior explained.
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: No disposable profile; persistence not observed.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: D-004. - Static verdict lead: PARTIAL.

### TC-REQ-FR-10 — FR-10: Settings, privacy and transparency

- Linked requirement: FR-10 (coffee-creator-blueprint/PRD.md:83).
- Title: Verify Settings, privacy and transparency and its failure/recovery behavior.
- Priority: P0-P2.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Home → primary destination (see route inventory)'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for FR-10; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Settings, privacy and transparency', then Zero operational network access under offline QA; no trackers/telemetry/remote inference libraries; personal docs never leave device through application runtime. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Zero operational network access under offline QA; no trackers/telemetry/remote inference libraries; personal docs never leave device through application runtime.
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Zero operational network access under offline QA; no trackers/telemetry/remote inference libraries; personal docs never leave device through application runtime.
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: No disposable profile; persistence not observed.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S2-001 — S2-001: Bean CRUD with roaster/origin/region/farm/producer/varietal/process/altitude/harvest/roast date

- Linked requirement: S2-001 (coffee-creator-blueprint/SPRINT_02_COMPETITIVE_FEATURE_IMPLEMENTATION.md:117).
- Title: Verify Bean CRUD with roaster/origin/region/farm/producer/varietal/process/altitude/harvest/roast date and its failure/recovery behavior.
- Priority: P0.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Library / Brew / Journal (exact screen action per row not independently traced)'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S2-001; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Bean CRUD with roaster/origin/region/farm/producer/varietal/process/altitude/harvest/roast date', then Bean CRUD with roaster/origin/region/farm/producer/varietal/process/altitude/harvest/roast date. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Bean CRUD with roaster/origin/region/farm/producer/varietal/process/altitude/harvest/roast date
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Bean CRUD with roaster/origin/region/farm/producer/varietal/process/altitude/harvest/roast date
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: No feature-specific physical acceptance recorded in this audit.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S2-002 — S2-002: Bag inventory: purchased/active/finished/frozen, remaining mass, package sizes

- Linked requirement: S2-002 (coffee-creator-blueprint/SPRINT_02_COMPETITIVE_FEATURE_IMPLEMENTATION.md:118).
- Title: Verify Bag inventory: purchased/active/finished/frozen, remaining mass, package sizes and its failure/recovery behavior.
- Priority: P0.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Library / Brew / Journal (exact screen action per row not independently traced)'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S2-002; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Bag inventory: purchased/active/finished/frozen, remaining mass, package sizes', then Bag inventory: purchased/active/finished/frozen, remaining mass, package sizes. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Bag inventory: purchased/active/finished/frozen, remaining mass, package sizes
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Bag inventory: purchased/active/finished/frozen, remaining mass, package sizes
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: No feature-specific physical acceptance recorded in this audit.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S2-003 — S2-003: Roast-rest calculator and freshness history with explainable heuristic

- Linked requirement: S2-003 (coffee-creator-blueprint/SPRINT_02_COMPETITIVE_FEATURE_IMPLEMENTATION.md:119).
- Title: Verify Roast-rest calculator and freshness history with explainable heuristic and its failure/recovery behavior.
- Priority: P1.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Library / Brew / Journal (exact screen action per row not independently traced)'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S2-003; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Roast-rest calculator and freshness history with explainable heuristic', then Roast-rest calculator and freshness history with explainable heuristic. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Roast-rest calculator and freshness history with explainable heuristic
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Roast-rest calculator and freshness history with explainable heuristic
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: No feature-specific physical acceptance recorded in this audit.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S2-004 — S2-004: Grinder records, calibration unit, clicks/RPM/burr profile (if known)

- Linked requirement: S2-004 (coffee-creator-blueprint/SPRINT_02_COMPETITIVE_FEATURE_IMPLEMENTATION.md:120).
- Title: Verify Grinder records, calibration unit, clicks/RPM/burr profile (if known) and its failure/recovery behavior.
- Priority: P0.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Library / Brew / Journal (exact screen action per row not independently traced)'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S2-004; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Grinder records, calibration unit, clicks/RPM/burr profile (if known)', then Grinder records, calibration unit, clicks/RPM/burr profile (if known). Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Grinder records, calibration unit, clicks/RPM/burr profile (if known)
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Grinder records, calibration unit, clicks/RPM/burr profile (if known)
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: No feature-specific physical acceptance recorded in this audit.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S2-005 — S2-005: Dripper, filter, kettle, scale, espresso machine, basket, puck-prep equipment catalog

- Linked requirement: S2-005 (coffee-creator-blueprint/SPRINT_02_COMPETITIVE_FEATURE_IMPLEMENTATION.md:121).
- Title: Verify Dripper, filter, kettle, scale, espresso machine, basket, puck-prep equipment catalog and its failure/recovery behavior.
- Priority: P0.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Library / Brew / Journal (exact screen action per row not independently traced)'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S2-005; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Dripper, filter, kettle, scale, espresso machine, basket, puck-prep equipment catalog', then Dripper, filter, kettle, scale, espresso machine, basket, puck-prep equipment catalog. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Dripper, filter, kettle, scale, espresso machine, basket, puck-prep equipment catalog
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Dripper, filter, kettle, scale, espresso machine, basket, puck-prep equipment catalog
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: No feature-specific physical acceptance recorded in this audit.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S2-006 — S2-006: Water mineral profile, hardness/alkalinity, user-defined water

- Linked requirement: S2-006 (coffee-creator-blueprint/SPRINT_02_COMPETITIVE_FEATURE_IMPLEMENTATION.md:122).
- Title: Verify Water mineral profile, hardness/alkalinity, user-defined water and its failure/recovery behavior.
- Priority: P1.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Library / Brew / Journal (exact screen action per row not independently traced)'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S2-006; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Water mineral profile, hardness/alkalinity, user-defined water', then Water mineral profile, hardness/alkalinity, user-defined water. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Water mineral profile, hardness/alkalinity, user-defined water
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Water mineral profile, hardness/alkalinity, user-defined water
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: No feature-specific physical acceptance recorded in this audit.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S2-007 — S2-007: Food-grade ingredient catalog with density, allergens and provenance

- Linked requirement: S2-007 (coffee-creator-blueprint/SPRINT_02_COMPETITIVE_FEATURE_IMPLEMENTATION.md:123).
- Title: Verify Food-grade ingredient catalog with density, allergens and provenance and its failure/recovery behavior.
- Priority: P0.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Library / Brew / Journal (exact screen action per row not independently traced)'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S2-007; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Food-grade ingredient catalog with density, allergens and provenance', then Food-grade ingredient catalog with density, allergens and provenance. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Food-grade ingredient catalog with density, allergens and provenance
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Food-grade ingredient catalog with density, allergens and provenance
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: No feature-specific physical acceptance recorded in this audit.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S2-008 — S2-008: Bag photo attachment + bundled offline OCR with manual correction

- Linked requirement: S2-008 (coffee-creator-blueprint/SPRINT_02_COMPETITIVE_FEATURE_IMPLEMENTATION.md:124).
- Title: Verify Bag photo attachment + bundled offline OCR with manual correction and its failure/recovery behavior.
- Priority: P1.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Library / Brew / Journal (exact screen action per row not independently traced)'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S2-008; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Bag photo attachment + bundled offline OCR with manual correction', then Bag photo attachment + bundled offline OCR with manual correction. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Bag photo attachment + bundled offline OCR with manual correction
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Bag photo attachment + bundled offline OCR with manual correction
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: No feature-specific physical acceptance recorded in this audit.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: D-010. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S2-009 — S2-009: Bulk manual import/export beans/equipment with conflict resolution

- Linked requirement: S2-009 (coffee-creator-blueprint/SPRINT_02_COMPETITIVE_FEATURE_IMPLEMENTATION.md:125).
- Title: Verify Bulk manual import/export beans/equipment with conflict resolution and its failure/recovery behavior.
- Priority: P1.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Library / Brew / Journal (exact screen action per row not independently traced)'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S2-009; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Bulk manual import/export beans/equipment with conflict resolution', then Bulk manual import/export beans/equipment with conflict resolution. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Bulk manual import/export beans/equipment with conflict resolution
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Bulk manual import/export beans/equipment with conflict resolution
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: No feature-specific physical acceptance recorded in this audit.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S2-010 — S2-010: Global offline search/filter/tag/favorites across beans and equipment

- Linked requirement: S2-010 (coffee-creator-blueprint/SPRINT_02_COMPETITIVE_FEATURE_IMPLEMENTATION.md:126).
- Title: Verify Global offline search/filter/tag/favorites across beans and equipment and its failure/recovery behavior.
- Priority: P0.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Library / Brew / Journal (exact screen action per row not independently traced)'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S2-010; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Global offline search/filter/tag/favorites across beans and equipment', then Global offline search/filter/tag/favorites across beans and equipment. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Global offline search/filter/tag/favorites across beans and equipment
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Global offline search/filter/tag/favorites across beans and equipment
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: No feature-specific physical acceptance recorded in this audit.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S2-011 — S2-011: Track green bean moisture/density/defects and sample lot status

- Linked requirement: S2-011 (coffee-creator-blueprint/SPRINT_02_COMPETITIVE_FEATURE_IMPLEMENTATION.md:127).
- Title: Verify Track green bean moisture/density/defects and sample lot status and its failure/recovery behavior.
- Priority: P2.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Library / Brew / Journal (exact screen action per row not independently traced)'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S2-011; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Track green bean moisture/density/defects and sample lot status', then Track green bean moisture/density/defects and sample lot status. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Track green bean moisture/density/defects and sample lot status
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Track green bean moisture/density/defects and sample lot status
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: No feature-specific physical acceptance recorded in this audit.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S2-012 — S2-012: Record roast batches, roast curves imported from file, blends

- Linked requirement: S2-012 (coffee-creator-blueprint/SPRINT_02_COMPETITIVE_FEATURE_IMPLEMENTATION.md:128).
- Title: Verify Record roast batches, roast curves imported from file, blends and its failure/recovery behavior.
- Priority: P2.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Library / Brew / Journal (exact screen action per row not independently traced)'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S2-012; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Record roast batches, roast curves imported from file, blends', then Record roast batches, roast curves imported from file, blends. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Record roast batches, roast curves imported from file, blends
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Record roast batches, roast curves imported from file, blends
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: No feature-specific physical acceptance recorded in this audit.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S2-013 — S2-013: Recipe create/edit/duplicate/archive/version, link beans/equipment

- Linked requirement: S2-013 (coffee-creator-blueprint/SPRINT_02_COMPETITIVE_FEATURE_IMPLEMENTATION.md:134).
- Title: Verify Recipe create/edit/duplicate/archive/version, link beans/equipment and its failure/recovery behavior.
- Priority: P0.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Formulate / Brew'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S2-013; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Recipe create/edit/duplicate/archive/version, link beans/equipment', then Recipe create/edit/duplicate/archive/version, link beans/equipment. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Recipe create/edit/duplicate/archive/version, link beans/equipment
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Recipe create/edit/duplicate/archive/version, link beans/equipment
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: No feature-specific physical acceptance recorded in this audit.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S2-014 — S2-014: Espresso, V60, AeroPress, Chemex, French press, cold brew, immersion, batch recipe types

- Linked requirement: S2-014 (coffee-creator-blueprint/SPRINT_02_COMPETITIVE_FEATURE_IMPLEMENTATION.md:135).
- Title: Verify Espresso, V60, AeroPress, Chemex, French press, cold brew, immersion, batch recipe types and its failure/recovery behavior.
- Priority: P0.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Formulate / Brew'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S2-014; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Espresso, V60, AeroPress, Chemex, French press, cold brew, immersion, batch recipe types', then Espresso, V60, AeroPress, Chemex, French press, cold brew, immersion, batch recipe types. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Espresso, V60, AeroPress, Chemex, French press, cold brew, immersion, batch recipe types
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Espresso, V60, AeroPress, Chemex, French press, cold brew, immersion, batch recipe types
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: No feature-specific physical acceptance recorded in this audit.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S2-015 — S2-015: User-defined custom brewing method and stages

- Linked requirement: S2-015 (coffee-creator-blueprint/SPRINT_02_COMPETITIVE_FEATURE_IMPLEMENTATION.md:136).
- Title: Verify User-defined custom brewing method and stages and its failure/recovery behavior.
- Priority: P0.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Formulate / Brew'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S2-015; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'User-defined custom brewing method and stages', then User-defined custom brewing method and stages. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: User-defined custom brewing method and stages
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: User-defined custom brewing method and stages
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: No feature-specific physical acceptance recorded in this audit.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S2-016 — S2-016: Editable ordered steps: action, start/relative time, target water, cumulative mass, temp, valve

- Linked requirement: S2-016 (coffee-creator-blueprint/SPRINT_02_COMPETITIVE_FEATURE_IMPLEMENTATION.md:137).
- Title: Verify Editable ordered steps: action, start/relative time, target water, cumulative mass, temp, valve and its failure/recovery behavior.
- Priority: P0.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Formulate / Brew'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S2-016; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Editable ordered steps: action, start/relative time, target water, cumulative mass, temp, valve', then Editable ordered steps: action, start/relative time, target water, cumulative mass, temp, valve. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Editable ordered steps: action, start/relative time, target water, cumulative mass, temp, valve
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Editable ordered steps: action, start/relative time, target water, cumulative mass, temp, valve
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: No feature-specific physical acceptance recorded in this audit.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S2-017 — S2-017: Ratio and dose scaling recomputes all pour targets exactly

- Linked requirement: S2-017 (coffee-creator-blueprint/SPRINT_02_COMPETITIVE_FEATURE_IMPLEMENTATION.md:138).
- Title: Verify Ratio and dose scaling recomputes all pour targets exactly and its failure/recovery behavior.
- Priority: P0.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Formulate / Brew'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S2-017; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Ratio and dose scaling recomputes all pour targets exactly', then Ratio and dose scaling recomputes all pour targets exactly. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Ratio and dose scaling recomputes all pour targets exactly
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Ratio and dose scaling recomputes all pour targets exactly
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: No feature-specific physical acceptance recorded in this audit.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S2-018 — S2-018: Typed quantities mg/g/mL/°C/°F and optional density-dependent conversion

- Linked requirement: S2-018 (coffee-creator-blueprint/SPRINT_02_COMPETITIVE_FEATURE_IMPLEMENTATION.md:139).
- Title: Verify Typed quantities mg/g/mL/°C/°F and optional density-dependent conversion and its failure/recovery behavior.
- Priority: P0.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Formulate / Brew'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S2-018; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Typed quantities mg/g/mL/°C/°F and optional density-dependent conversion', then Typed quantities mg/g/mL/°C/°F and optional density-dependent conversion. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Typed quantities mg/g/mL/°C/°F and optional density-dependent conversion
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Typed quantities mg/g/mL/°C/°F and optional density-dependent conversion
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: No feature-specific physical acceptance recorded in this audit.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S2-019 — S2-019: TDS and EY from measured dose, beverage mass, refractometer result

- Linked requirement: S2-019 (coffee-creator-blueprint/SPRINT_02_COMPETITIVE_FEATURE_IMPLEMENTATION.md:140).
- Title: Verify TDS and EY from measured dose, beverage mass, refractometer result and its failure/recovery behavior.
- Priority: P0.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Formulate / Brew'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S2-019; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'TDS and EY from measured dose, beverage mass, refractometer result', then TDS and EY from measured dose, beverage mass, refractometer result. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: TDS and EY from measured dose, beverage mass, refractometer result
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: TDS and EY from measured dose, beverage mass, refractometer result
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: No feature-specific physical acceptance recorded in this audit.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S2-020 — S2-020: Brix→TDS only with documented calibration equation/instrument profile

- Linked requirement: S2-020 (coffee-creator-blueprint/SPRINT_02_COMPETITIVE_FEATURE_IMPLEMENTATION.md:141).
- Title: Verify Brix→TDS only with documented calibration equation/instrument profile and its failure/recovery behavior.
- Priority: P1.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Formulate / Brew'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S2-020; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Brix→TDS only with documented calibration equation/instrument profile', then Brix→TDS only with documented calibration equation/instrument profile. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Brix→TDS only with documented calibration equation/instrument profile
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Brix→TDS only with documented calibration equation/instrument profile
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: No feature-specific physical acceptance recorded in this audit.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S2-021 — S2-021: Concentrate mass-balance dilution and solution dosing

- Linked requirement: S2-021 (coffee-creator-blueprint/SPRINT_02_COMPETITIVE_FEATURE_IMPLEMENTATION.md:142).
- Title: Verify Concentrate mass-balance dilution and solution dosing and its failure/recovery behavior.
- Priority: P0.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Formulate / Brew'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S2-021; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Concentrate mass-balance dilution and solution dosing', then Concentrate mass-balance dilution and solution dosing. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Concentrate mass-balance dilution and solution dosing
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Concentrate mass-balance dilution and solution dosing
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: No feature-specific physical acceptance recorded in this audit.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S2-022 — S2-022: Food-grade aroma concentrate percentage with active-vs-stock distinction

- Linked requirement: S2-022 (coffee-creator-blueprint/SPRINT_02_COMPETITIVE_FEATURE_IMPLEMENTATION.md:143).
- Title: Verify Food-grade aroma concentrate percentage with active-vs-stock distinction and its failure/recovery behavior.
- Priority: P0.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Formulate / Brew'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S2-022; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Food-grade aroma concentrate percentage with active-vs-stock distinction', then Food-grade aroma concentrate percentage with active-vs-stock distinction. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Food-grade aroma concentrate percentage with active-vs-stock distinction
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Food-grade aroma concentrate percentage with active-vs-stock distinction
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: No feature-specific physical acceptance recorded in this audit.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S2-023 — S2-023: Manual sensory target constraints and safe parameter validation

- Linked requirement: S2-023 (coffee-creator-blueprint/SPRINT_02_COMPETITIVE_FEATURE_IMPLEMENTATION.md:144).
- Title: Verify Manual sensory target constraints and safe parameter validation and its failure/recovery behavior.
- Priority: P1.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Formulate / Brew'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S2-023; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Manual sensory target constraints and safe parameter validation', then Manual sensory target constraints and safe parameter validation. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Manual sensory target constraints and safe parameter validation
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Manual sensory target constraints and safe parameter validation
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: No feature-specific physical acceptance recorded in this audit.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S2-024 — S2-024: Curated source-attributed starter recipes (licensed/independently authored)

- Linked requirement: S2-024 (coffee-creator-blueprint/SPRINT_02_COMPETITIVE_FEATURE_IMPLEMENTATION.md:145).
- Title: Verify Curated source-attributed starter recipes (licensed/independently authored) and its failure/recovery behavior.
- Priority: P1.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Formulate / Brew'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S2-024; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Curated source-attributed starter recipes (licensed/independently authored)', then Curated source-attributed starter recipes (licensed/independently authored). Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Curated source-attributed starter recipes (licensed/independently authored)
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Curated source-attributed starter recipes (licensed/independently authored)
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: No feature-specific physical acceptance recorded in this audit.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S2-025 — S2-025: Expert mode for custom brew metadata, first drop and grind setup

- Linked requirement: S2-025 (coffee-creator-blueprint/SPRINT_02_COMPETITIVE_FEATURE_IMPLEMENTATION.md:146).
- Title: Verify Expert mode for custom brew metadata, first drop and grind setup and its failure/recovery behavior.
- Priority: P1.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Formulate / Brew'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S2-025; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Expert mode for custom brew metadata, first drop and grind setup', then Expert mode for custom brew metadata, first drop and grind setup. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Expert mode for custom brew metadata, first drop and grind setup
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Expert mode for custom brew metadata, first drop and grind setup
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: No feature-specific physical acceptance recorded in this audit.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S2-026 — S2-026: Share recipe via exported JSON/PDF/text and Android Sharesheet, no cloud

- Linked requirement: S2-026 (coffee-creator-blueprint/SPRINT_02_COMPETITIVE_FEATURE_IMPLEMENTATION.md:147).
- Title: Verify Share recipe via exported JSON/PDF/text and Android Sharesheet, no cloud and its failure/recovery behavior.
- Priority: P1.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Formulate / Brew'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S2-026; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Share recipe via exported JSON/PDF/text and Android Sharesheet, no cloud', then Share recipe via exported JSON/PDF/text and Android Sharesheet, no cloud. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Share recipe via exported JSON/PDF/text and Android Sharesheet, no cloud
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Share recipe via exported JSON/PDF/text and Android Sharesheet, no cloud
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: No feature-specific physical acceptance recorded in this audit.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S2-027 — S2-027: Large accessible brew countdown/elapsed timer with background-safe timebase

- Linked requirement: S2-027 (coffee-creator-blueprint/SPRINT_02_COMPETITIVE_FEATURE_IMPLEMENTATION.md:153).
- Title: Verify Large accessible brew countdown/elapsed timer with background-safe timebase and its failure/recovery behavior.
- Priority: P0.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Brew'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S2-027; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Large accessible brew countdown/elapsed timer with background-safe timebase', then Large accessible brew countdown/elapsed timer with background-safe timebase. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Large accessible brew countdown/elapsed timer with background-safe timebase
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Large accessible brew countdown/elapsed timer with background-safe timebase
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: No feature-specific physical acceptance recorded in this audit.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S2-028 — S2-028: Step-by-step current/next pour, cumulative target, action instructions

- Linked requirement: S2-028 (coffee-creator-blueprint/SPRINT_02_COMPETITIVE_FEATURE_IMPLEMENTATION.md:154).
- Title: Verify Step-by-step current/next pour, cumulative target, action instructions and its failure/recovery behavior.
- Priority: P0.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Brew'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S2-028; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Step-by-step current/next pour, cumulative target, action instructions', then Step-by-step current/next pour, cumulative target, action instructions. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Step-by-step current/next pour, cumulative target, action instructions
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Step-by-step current/next pour, cumulative target, action instructions
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: No feature-specific physical acceptance recorded in this audit.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S2-029 — S2-029: Pause/resume/skip/restart and interrupted-session recovery

- Linked requirement: S2-029 (coffee-creator-blueprint/SPRINT_02_COMPETITIVE_FEATURE_IMPLEMENTATION.md:155).
- Title: Verify Pause/resume/skip/restart and interrupted-session recovery and its failure/recovery behavior.
- Priority: P0.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Brew'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S2-029; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Pause/resume/skip/restart and interrupted-session recovery', then Pause/resume/skip/restart and interrupted-session recovery. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Pause/resume/skip/restart and interrupted-session recovery
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Pause/resume/skip/restart and interrupted-session recovery
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: No feature-specific physical acceptance recorded in this audit.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S2-030 — S2-030: Haptic/audio/vibration cues; screen-awake opt-in and notification controls

- Linked requirement: S2-030 (coffee-creator-blueprint/SPRINT_02_COMPETITIVE_FEATURE_IMPLEMENTATION.md:156).
- Title: Verify Haptic/audio/vibration cues; screen-awake opt-in and notification controls and its failure/recovery behavior.
- Priority: P0.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Brew'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S2-030; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Haptic/audio/vibration cues; screen-awake opt-in and notification controls', then Haptic/audio/vibration cues; screen-awake opt-in and notification controls. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Haptic/audio/vibration cues; screen-awake opt-in and notification controls
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Haptic/audio/vibration cues; screen-awake opt-in and notification controls
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: No feature-specific physical acceptance recorded in this audit.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S2-031 — S2-031: Automatic completed-session logging and later manual edits

- Linked requirement: S2-031 (coffee-creator-blueprint/SPRINT_02_COMPETITIVE_FEATURE_IMPLEMENTATION.md:157).
- Title: Verify Automatic completed-session logging and later manual edits and its failure/recovery behavior.
- Priority: P0.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Brew'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S2-031; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Automatic completed-session logging and later manual edits', then Automatic completed-session logging and later manual edits. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Automatic completed-session logging and later manual edits
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Automatic completed-session logging and later manual edits
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: No feature-specific physical acceptance recorded in this audit.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S2-032 — S2-032: Bluetooth LE device abstraction, scan/pair/reconnect and permission UX

- Linked requirement: S2-032 (coffee-creator-blueprint/SPRINT_02_COMPETITIVE_FEATURE_IMPLEMENTATION.md:158).
- Title: Verify Bluetooth LE device abstraction, scan/pair/reconnect and permission UX and its failure/recovery behavior.
- Priority: P1.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Brew'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S2-032; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Bluetooth LE device abstraction, scan/pair/reconnect and permission UX', then Bluetooth LE device abstraction, scan/pair/reconnect and permission UX. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Bluetooth LE device abstraction, scan/pair/reconnect and permission UX
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Bluetooth LE device abstraction, scan/pair/reconnect and permission UX
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: No feature-specific physical acceptance recorded in this audit.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: D-005. - Static verdict lead: PARTIAL.

### TC-REQ-S2-033 — S2-033: Device-specific live weight + flow in g/s, graph persistence

- Linked requirement: S2-033 (coffee-creator-blueprint/SPRINT_02_COMPETITIVE_FEATURE_IMPLEMENTATION.md:159).
- Title: Verify Device-specific live weight + flow in g/s, graph persistence and its failure/recovery behavior.
- Priority: P1.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Brew'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S2-033; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Device-specific live weight + flow in g/s, graph persistence', then Device-specific live weight + flow in g/s, graph persistence. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Device-specific live weight + flow in g/s, graph persistence
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Device-specific live weight + flow in g/s, graph persistence
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: No feature-specific physical acceptance recorded in this audit.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: D-005. - Static verdict lead: PARTIAL.

### TC-REQ-S2-034 — S2-034: Tare, weight-triggered timer start, automatic espresso stop where supported

- Linked requirement: S2-034 (coffee-creator-blueprint/SPRINT_02_COMPETITIVE_FEATURE_IMPLEMENTATION.md:160).
- Title: Verify Tare, weight-triggered timer start, automatic espresso stop where supported and its failure/recovery behavior.
- Priority: P1.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Brew'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S2-034; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Tare, weight-triggered timer start, automatic espresso stop where supported', then Tare, weight-triggered timer start, automatic espresso stop where supported. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Tare, weight-triggered timer start, automatic espresso stop where supported
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Tare, weight-triggered timer start, automatic espresso stop where supported
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: No feature-specific physical acceptance recorded in this audit.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: D-005. - Static verdict lead: PARTIAL.

### TC-REQ-S2-035 — S2-035: Ignore transient scale reading during swirl/stir/remove and flag chart discontinuities

- Linked requirement: S2-035 (coffee-creator-blueprint/SPRINT_02_COMPETITIVE_FEATURE_IMPLEMENTATION.md:161).
- Title: Verify Ignore transient scale reading during swirl/stir/remove and flag chart discontinuities and its failure/recovery behavior.
- Priority: P1.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Brew'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S2-035; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Ignore transient scale reading during swirl/stir/remove and flag chart discontinuities', then Ignore transient scale reading during swirl/stir/remove and flag chart discontinuities. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Ignore transient scale reading during swirl/stir/remove and flag chart discontinuities
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Ignore transient scale reading during swirl/stir/remove and flag chart discontinuities
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: No feature-specific physical acceptance recorded in this audit.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: D-005. - Static verdict lead: PARTIAL.

### TC-REQ-S2-036 — S2-036: Target-weight proximity visual/haptic indicator

- Linked requirement: S2-036 (coffee-creator-blueprint/SPRINT_02_COMPETITIVE_FEATURE_IMPLEMENTATION.md:162).
- Title: Verify Target-weight proximity visual/haptic indicator and its failure/recovery behavior.
- Priority: P1.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Brew'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S2-036; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Target-weight proximity visual/haptic indicator', then Target-weight proximity visual/haptic indicator. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Target-weight proximity visual/haptic indicator
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Target-weight proximity visual/haptic indicator
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: No feature-specific physical acceptance recorded in this audit.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: D-005. - Static verdict lead: PARTIAL.

### TC-REQ-S2-037 — S2-037: On-device brew curve reference and comparison overlays

- Linked requirement: S2-037 (coffee-creator-blueprint/SPRINT_02_COMPETITIVE_FEATURE_IMPLEMENTATION.md:163).
- Title: Verify On-device brew curve reference and comparison overlays and its failure/recovery behavior.
- Priority: P2.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Brew'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S2-037; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'On-device brew curve reference and comparison overlays', then On-device brew curve reference and comparison overlays. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: On-device brew curve reference and comparison overlays
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: On-device brew curve reference and comparison overlays
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: No feature-specific physical acceptance recorded in this audit.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S2-038 — S2-038: Android ongoing notification or widget for active brew (platform compliant)

- Linked requirement: S2-038 (coffee-creator-blueprint/SPRINT_02_COMPETITIVE_FEATURE_IMPLEMENTATION.md:164).
- Title: Verify Android ongoing notification or widget for active brew (platform compliant) and its failure/recovery behavior.
- Priority: P1.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Brew'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S2-038; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Android ongoing notification or widget for active brew (platform compliant)', then Android ongoing notification or widget for active brew (platform compliant). Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Android ongoing notification or widget for active brew (platform compliant)
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Android ongoing notification or widget for active brew (platform compliant)
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: No feature-specific physical acceptance recorded in this audit.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S2-039 — S2-039: Detailed brew journal (dose, water, ratio, grind, temp, time, drawdown, TDS, taste, note, photo)

- Linked requirement: S2-039 (coffee-creator-blueprint/SPRINT_02_COMPETITIVE_FEATURE_IMPLEMENTATION.md:170).
- Title: Verify Detailed brew journal (dose, water, ratio, grind, temp, time, drawdown, TDS, taste, note, photo) and its failure/recovery behavior.
- Priority: P0.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Journal'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S2-039; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Detailed brew journal (dose, water, ratio, grind, temp, time, drawdown, TDS, taste, note, photo)', then Detailed brew journal (dose, water, ratio, grind, temp, time, drawdown, TDS, taste, note, photo). Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Detailed brew journal (dose, water, ratio, grind, temp, time, drawdown, TDS, taste, note, photo)
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Detailed brew journal (dose, water, ratio, grind, temp, time, drawdown, TDS, taste, note, photo)
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: No feature-specific physical acceptance recorded in this audit.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S2-040 — S2-040: Search/filter by bean/method/date/rating/tags/equipment

- Linked requirement: S2-040 (coffee-creator-blueprint/SPRINT_02_COMPETITIVE_FEATURE_IMPLEMENTATION.md:171).
- Title: Verify Search/filter by bean/method/date/rating/tags/equipment and its failure/recovery behavior.
- Priority: P0.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Journal'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S2-040; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Search/filter by bean/method/date/rating/tags/equipment', then Search/filter by bean/method/date/rating/tags/equipment. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Search/filter by bean/method/date/rating/tags/equipment
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Search/filter by bean/method/date/rating/tags/equipment
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: No feature-specific physical acceptance recorded in this audit.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S2-041 — S2-041: Side-by-side two brew sessions with deterministic numeric deltas

- Linked requirement: S2-041 (coffee-creator-blueprint/SPRINT_02_COMPETITIVE_FEATURE_IMPLEMENTATION.md:172).
- Title: Verify Side-by-side two brew sessions with deterministic numeric deltas and its failure/recovery behavior.
- Priority: P0.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Journal'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S2-041; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Side-by-side two brew sessions with deterministic numeric deltas', then Side-by-side two brew sessions with deterministic numeric deltas. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Side-by-side two brew sessions with deterministic numeric deltas
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Side-by-side two brew sessions with deterministic numeric deltas
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: No feature-specific physical acceptance recorded in this audit.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S2-042 — S2-042: Espresso target vs actual, last shot comparison and annotated dial-in

- Linked requirement: S2-042 (coffee-creator-blueprint/SPRINT_02_COMPETITIVE_FEATURE_IMPLEMENTATION.md:173).
- Title: Verify Espresso target vs actual, last shot comparison and annotated dial-in and its failure/recovery behavior.
- Priority: P0.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Journal'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S2-042; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Espresso target vs actual, last shot comparison and annotated dial-in', then Espresso target vs actual, last shot comparison and annotated dial-in. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Espresso target vs actual, last shot comparison and annotated dial-in
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Espresso target vs actual, last shot comparison and annotated dial-in
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: No feature-specific physical acceptance recorded in this audit.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S2-043 — S2-043: One-variable-at-a-time adjustment suggestions with reasoning

- Linked requirement: S2-043 (coffee-creator-blueprint/SPRINT_02_COMPETITIVE_FEATURE_IMPLEMENTATION.md:174).
- Title: Verify One-variable-at-a-time adjustment suggestions with reasoning and its failure/recovery behavior.
- Priority: P1.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Journal'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S2-043; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'One-variable-at-a-time adjustment suggestions with reasoning', then One-variable-at-a-time adjustment suggestions with reasoning. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: One-variable-at-a-time adjustment suggestions with reasoning
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: One-variable-at-a-time adjustment suggestions with reasoning
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: No feature-specific physical acceptance recorded in this audit.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S2-044 — S2-044: Interactive charts for weight/time/flow/EY/TDS and filtering by bean

- Linked requirement: S2-044 (coffee-creator-blueprint/SPRINT_02_COMPETITIVE_FEATURE_IMPLEMENTATION.md:175).
- Title: Verify Interactive charts for weight/time/flow/EY/TDS and filtering by bean and its failure/recovery behavior.
- Priority: P1.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Journal'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S2-044; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Interactive charts for weight/time/flow/EY/TDS and filtering by bean', then Interactive charts for weight/time/flow/EY/TDS and filtering by bean. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Interactive charts for weight/time/flow/EY/TDS and filtering by bean
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Interactive charts for weight/time/flow/EY/TDS and filtering by bean
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: No feature-specific physical acceptance recorded in this audit.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S2-045 — S2-045: Calendar, streak and monthly stats computed locally

- Linked requirement: S2-045 (coffee-creator-blueprint/SPRINT_02_COMPETITIVE_FEATURE_IMPLEMENTATION.md:176).
- Title: Verify Calendar, streak and monthly stats computed locally and its failure/recovery behavior.
- Priority: P1.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Journal'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S2-045; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Calendar, streak and monthly stats computed locally', then Calendar, streak and monthly stats computed locally. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Calendar, streak and monthly stats computed locally
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Calendar, streak and monthly stats computed locally
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: No feature-specific physical acceptance recorded in this audit.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S2-046 — S2-046: Brew template save/reuse and repeat last brew

- Linked requirement: S2-046 (coffee-creator-blueprint/SPRINT_02_COMPETITIVE_FEATURE_IMPLEMENTATION.md:177).
- Title: Verify Brew template save/reuse and repeat last brew and its failure/recovery behavior.
- Priority: P0.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Journal'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S2-046; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Brew template save/reuse and repeat last brew', then Brew template save/reuse and repeat last brew. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Brew template save/reuse and repeat last brew
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Brew template save/reuse and repeat last brew
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: No feature-specific physical acceptance recorded in this audit.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S2-047 — S2-047: CSV/JSON export, full backup and import conflict preview

- Linked requirement: S2-047 (coffee-creator-blueprint/SPRINT_02_COMPETITIVE_FEATURE_IMPLEMENTATION.md:178).
- Title: Verify CSV/JSON export, full backup and import conflict preview and its failure/recovery behavior.
- Priority: P0.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Journal'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S2-047; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'CSV/JSON export, full backup and import conflict preview', then CSV/JSON export, full backup and import conflict preview. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: CSV/JSON export, full backup and import conflict preview
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: CSV/JSON export, full backup and import conflict preview
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: No feature-specific physical acceptance recorded in this audit.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S2-048 — S2-048: Beanconqueror export importer via documented/test fixture mapping

- Linked requirement: S2-048 (coffee-creator-blueprint/SPRINT_02_COMPETITIVE_FEATURE_IMPLEMENTATION.md:179).
- Title: Verify Beanconqueror export importer via documented/test fixture mapping and its failure/recovery behavior.
- Priority: P1.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Journal'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S2-048; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Beanconqueror export importer via documented/test fixture mapping', then Beanconqueror export importer via documented/test fixture mapping. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Beanconqueror export importer via documented/test fixture mapping
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Beanconqueror export importer via documented/test fixture mapping
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: No feature-specific physical acceptance recorded in this audit.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S2-049 — S2-049: Cafe cup journal with optional photos and manual cafe information (no live map)

- Linked requirement: S2-049 (coffee-creator-blueprint/SPRINT_02_COMPETITIVE_FEATURE_IMPLEMENTATION.md:180).
- Title: Verify Cafe cup journal with optional photos and manual cafe information (no live map) and its failure/recovery behavior.
- Priority: P2.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Journal'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S2-049; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Cafe cup journal with optional photos and manual cafe information (no live map)', then Cafe cup journal with optional photos and manual cafe information (no live map). Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Cafe cup journal with optional photos and manual cafe information (no live map)
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Cafe cup journal with optional photos and manual cafe information (no live map)
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: No feature-specific physical acceptance recorded in this audit.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S2-050 — S2-050: Manual optional caffeine consumption estimate with uncertainty label

- Linked requirement: S2-050 (coffee-creator-blueprint/SPRINT_02_COMPETITIVE_FEATURE_IMPLEMENTATION.md:181).
- Title: Verify Manual optional caffeine consumption estimate with uncertainty label and its failure/recovery behavior.
- Priority: P2.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Journal'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S2-050; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Manual optional caffeine consumption estimate with uncertainty label', then Manual optional caffeine consumption estimate with uncertainty label. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Manual optional caffeine consumption estimate with uncertainty label
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Manual optional caffeine consumption estimate with uncertainty label
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: No feature-specific physical acceptance recorded in this audit.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S2-051 — S2-051: Hierarchical, searchable original flavor wheel/taxonomy, accessibility list alternative

- Linked requirement: S2-051 (coffee-creator-blueprint/SPRINT_02_COMPETITIVE_FEATURE_IMPLEMENTATION.md:187).
- Title: Verify Hierarchical, searchable original flavor wheel/taxonomy, accessibility list alternative and its failure/recovery behavior.
- Priority: P0.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S2-051; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Hierarchical, searchable original flavor wheel/taxonomy, accessibility list alternative', then Hierarchical, searchable original flavor wheel/taxonomy, accessibility list alternative. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Hierarchical, searchable original flavor wheel/taxonomy, accessibility list alternative
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Hierarchical, searchable original flavor wheel/taxonomy, accessibility list alternative
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: No feature-specific physical acceptance recorded in this audit.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S2-052 — S2-052: Descriptor definitions + safe aroma/taste reference exercises with attribution

- Linked requirement: S2-052 (coffee-creator-blueprint/SPRINT_02_COMPETITIVE_FEATURE_IMPLEMENTATION.md:188).
- Title: Verify Descriptor definitions + safe aroma/taste reference exercises with attribution and its failure/recovery behavior.
- Priority: P1.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S2-052; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Descriptor definitions + safe aroma/taste reference exercises with attribution', then Descriptor definitions + safe aroma/taste reference exercises with attribution. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Descriptor definitions + safe aroma/taste reference exercises with attribution
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Descriptor definitions + safe aroma/taste reference exercises with attribution
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: No feature-specific physical acceptance recorded in this audit.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S2-053 — S2-053: Aroma, acidity, sweetness, body, bitterness, aftertaste, balance sliders/notes

- Linked requirement: S2-053 (coffee-creator-blueprint/SPRINT_02_COMPETITIVE_FEATURE_IMPLEMENTATION.md:189).
- Title: Verify Aroma, acidity, sweetness, body, bitterness, aftertaste, balance sliders/notes and its failure/recovery behavior.
- Priority: P0.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S2-053; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Aroma, acidity, sweetness, body, bitterness, aftertaste, balance sliders/notes', then Aroma, acidity, sweetness, body, bitterness, aftertaste, balance sliders/notes. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Aroma, acidity, sweetness, body, bitterness, aftertaste, balance sliders/notes
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Aroma, acidity, sweetness, body, bitterness, aftertaste, balance sliders/notes
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: No feature-specific physical acceptance recorded in this audit.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S2-054 — S2-054: Distinguish human observation, AI hypothesis, calibrated model prediction

- Linked requirement: S2-054 (coffee-creator-blueprint/SPRINT_02_COMPETITIVE_FEATURE_IMPLEMENTATION.md:190).
- Title: Verify Distinguish human observation, AI hypothesis, calibrated model prediction and its failure/recovery behavior.
- Priority: P0.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S2-054; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Distinguish human observation, AI hypothesis, calibrated model prediction', then Distinguish human observation, AI hypothesis, calibrated model prediction. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Distinguish human observation, AI hypothesis, calibrated model prediction
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Distinguish human observation, AI hypothesis, calibrated model prediction
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: No feature-specific physical acceptance recorded in this audit.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S2-055 — S2-055: Cupping sheet templates; exact SCA copyrighted forms only if licensed

- Linked requirement: S2-055 (coffee-creator-blueprint/SPRINT_02_COMPETITIVE_FEATURE_IMPLEMENTATION.md:191).
- Title: Verify Cupping sheet templates; exact SCA copyrighted forms only if licensed and its failure/recovery behavior.
- Priority: P1.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S2-055; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Cupping sheet templates; exact SCA copyrighted forms only if licensed', then Cupping sheet templates; exact SCA copyrighted forms only if licensed. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Cupping sheet templates; exact SCA copyrighted forms only if licensed
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Cupping sheet templates; exact SCA copyrighted forms only if licensed
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: No feature-specific physical acceptance recorded in this audit.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S2-056 — S2-056: Cupping sessions with samples and locally assigned evaluator identities

- Linked requirement: S2-056 (coffee-creator-blueprint/SPRINT_02_COMPETITIVE_FEATURE_IMPLEMENTATION.md:192).
- Title: Verify Cupping sessions with samples and locally assigned evaluator identities and its failure/recovery behavior.
- Priority: P1.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S2-056; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Cupping sessions with samples and locally assigned evaluator identities', then Cupping sessions with samples and locally assigned evaluator identities. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Cupping sessions with samples and locally assigned evaluator identities
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Cupping sessions with samples and locally assigned evaluator identities
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: No feature-specific physical acceptance recorded in this audit.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S2-057 — S2-057: Blind, non-blind and double-blind masking with controlled reveal

- Linked requirement: S2-057 (coffee-creator-blueprint/SPRINT_02_COMPETITIVE_FEATURE_IMPLEMENTATION.md:193).
- Title: Verify Blind, non-blind and double-blind masking with controlled reveal and its failure/recovery behavior.
- Priority: P1.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S2-057; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Blind, non-blind and double-blind masking with controlled reveal', then Blind, non-blind and double-blind masking with controlled reveal. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Blind, non-blind and double-blind masking with controlled reveal
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Blind, non-blind and double-blind masking with controlled reveal
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: No feature-specific physical acceptance recorded in this audit.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S2-058 — S2-058: Draft/open/closed statuses, score submission, reopen with audit log

- Linked requirement: S2-058 (coffee-creator-blueprint/SPRINT_02_COMPETITIVE_FEATURE_IMPLEMENTATION.md:194).
- Title: Verify Draft/open/closed statuses, score submission, reopen with audit log and its failure/recovery behavior.
- Priority: P1.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S2-058; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Draft/open/closed statuses, score submission, reopen with audit log', then Draft/open/closed statuses, score submission, reopen with audit log. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Draft/open/closed statuses, score submission, reopen with audit log
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Draft/open/closed statuses, score submission, reopen with audit log
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: No feature-specific physical acceptance recorded in this audit.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S2-059 — S2-059: Compare score distributions, descriptors, evaluator agreement/spread

- Linked requirement: S2-059 (coffee-creator-blueprint/SPRINT_02_COMPETITIVE_FEATURE_IMPLEMENTATION.md:195).
- Title: Verify Compare score distributions, descriptors, evaluator agreement/spread and its failure/recovery behavior.
- Priority: P1.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S2-059; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Compare score distributions, descriptors, evaluator agreement/spread', then Compare score distributions, descriptors, evaluator agreement/spread. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Compare score distributions, descriptors, evaluator agreement/spread
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Compare score distributions, descriptors, evaluator agreement/spread
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: No feature-specific physical acceptance recorded in this audit.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S2-060 — S2-060: Generate offline PDF/CSV QC report including provenance

- Linked requirement: S2-060 (coffee-creator-blueprint/SPRINT_02_COMPETITIVE_FEATURE_IMPLEMENTATION.md:196).
- Title: Verify Generate offline PDF/CSV QC report including provenance and its failure/recovery behavior.
- Priority: P1.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S2-060; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Generate offline PDF/CSV QC report including provenance', then Generate offline PDF/CSV QC report including provenance. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Generate offline PDF/CSV QC report including provenance
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Generate offline PDF/CSV QC report including provenance
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: No feature-specific physical acceptance recorded in this audit.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S2-061 — S2-061: Roast/green physical QC fields and link to samples

- Linked requirement: S2-061 (coffee-creator-blueprint/SPRINT_02_COMPETITIVE_FEATURE_IMPLEMENTATION.md:197).
- Title: Verify Roast/green physical QC fields and link to samples and its failure/recovery behavior.
- Priority: P2.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S2-061; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Roast/green physical QC fields and link to samples', then Roast/green physical QC fields and link to samples. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Roast/green physical QC fields and link to samples
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Roast/green physical QC fields and link to samples
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: No feature-specific physical acceptance recorded in this audit.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S2-062 — S2-062: Multiple taste observations per bag over roast-rest cycle

- Linked requirement: S2-062 (coffee-creator-blueprint/SPRINT_02_COMPETITIVE_FEATURE_IMPLEMENTATION.md:198).
- Title: Verify Multiple taste observations per bag over roast-rest cycle and its failure/recovery behavior.
- Priority: P1.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S2-062; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Multiple taste observations per bag over roast-rest cycle', then Multiple taste observations per bag over roast-rest cycle. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Multiple taste observations per bag over roast-rest cycle
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Multiple taste observations per bag over roast-rest cycle
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: No feature-specific physical acceptance recorded in this audit.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S2-063 — S2-063: Offline personal palate quiz + editable preference profile

- Linked requirement: S2-063 (coffee-creator-blueprint/SPRINT_02_COMPETITIVE_FEATURE_IMPLEMENTATION.md:204).
- Title: Verify Offline personal palate quiz + editable preference profile and its failure/recovery behavior.
- Priority: P1.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'AI Chat / Formulate / Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S2-063; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Offline personal palate quiz + editable preference profile', then Offline personal palate quiz + editable preference profile. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Offline personal palate quiz + editable preference profile
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Offline personal palate quiz + editable preference profile
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: No feature-specific physical acceptance recorded in this audit.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S2-064 — S2-064: Personal taste profile computed from logged sensory evidence

- Linked requirement: S2-064 (coffee-creator-blueprint/SPRINT_02_COMPETITIVE_FEATURE_IMPLEMENTATION.md:205).
- Title: Verify Personal taste profile computed from logged sensory evidence and its failure/recovery behavior.
- Priority: P1.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'AI Chat / Formulate / Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S2-064; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Personal taste profile computed from logged sensory evidence', then Personal taste profile computed from logged sensory evidence. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Personal taste profile computed from logged sensory evidence
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Personal taste profile computed from logged sensory evidence
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: No feature-specific physical acceptance recorded in this audit.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S2-065 — S2-065: Explainable local bean/method/recipe recommendations from available inventory

- Linked requirement: S2-065 (coffee-creator-blueprint/SPRINT_02_COMPETITIVE_FEATURE_IMPLEMENTATION.md:206).
- Title: Verify Explainable local bean/method/recipe recommendations from available inventory and its failure/recovery behavior.
- Priority: P1.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'AI Chat / Formulate / Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S2-065; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Explainable local bean/method/recipe recommendations from available inventory', then Explainable local bean/method/recipe recommendations from available inventory. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Explainable local bean/method/recipe recommendations from available inventory
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Explainable local bean/method/recipe recommendations from available inventory
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: No feature-specific physical acceptance recorded in this audit.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S2-066 — S2-066: Brewing feedback loop that proposes a single controlled recipe adjustment

- Linked requirement: S2-066 (coffee-creator-blueprint/SPRINT_02_COMPETITIVE_FEATURE_IMPLEMENTATION.md:207).
- Title: Verify Brewing feedback loop that proposes a single controlled recipe adjustment and its failure/recovery behavior.
- Priority: P1.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'AI Chat / Formulate / Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S2-066; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Brewing feedback loop that proposes a single controlled recipe adjustment', then Brewing feedback loop that proposes a single controlled recipe adjustment. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Brewing feedback loop that proposes a single controlled recipe adjustment
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Brewing feedback loop that proposes a single controlled recipe adjustment
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: No feature-specific physical acceptance recorded in this audit.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S2-067 — S2-067: Camera/OCR to draft bean/equipment metadata with explicit corrections

- Linked requirement: S2-067 (coffee-creator-blueprint/SPRINT_02_COMPETITIVE_FEATURE_IMPLEMENTATION.md:208).
- Title: Verify Camera/OCR to draft bean/equipment metadata with explicit corrections and its failure/recovery behavior.
- Priority: P1.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'AI Chat / Formulate / Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S2-067; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Camera/OCR to draft bean/equipment metadata with explicit corrections', then Camera/OCR to draft bean/equipment metadata with explicit corrections. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Camera/OCR to draft bean/equipment metadata with explicit corrections
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Camera/OCR to draft bean/equipment metadata with explicit corrections
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: No feature-specific physical acceptance recorded in this audit.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: D-010. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S2-068 — S2-068: Local AI barista chat for questions on extraction, water, gear and techniques

- Linked requirement: S2-068 (coffee-creator-blueprint/SPRINT_02_COMPETITIVE_FEATURE_IMPLEMENTATION.md:209).
- Title: Verify Local AI barista chat for questions on extraction, water, gear and techniques and its failure/recovery behavior.
- Priority: P0.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'AI Chat / Formulate / Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S2-068; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Local AI barista chat for questions on extraction, water, gear and techniques', then Local AI barista chat for questions on extraction, water, gear and techniques. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Local AI barista chat for questions on extraction, water, gear and techniques
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Local AI barista chat for questions on extraction, water, gear and techniques
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: No feature-specific physical acceptance recorded in this audit.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S2-069 — S2-069: Tool-driven generation of typed recipe draft based on beans/gear/constraints

- Linked requirement: S2-069 (coffee-creator-blueprint/SPRINT_02_COMPETITIVE_FEATURE_IMPLEMENTATION.md:210).
- Title: Verify Tool-driven generation of typed recipe draft based on beans/gear/constraints and its failure/recovery behavior.
- Priority: P0.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'AI Chat / Formulate / Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S2-069; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Tool-driven generation of typed recipe draft based on beans/gear/constraints', then Tool-driven generation of typed recipe draft based on beans/gear/constraints. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Tool-driven generation of typed recipe draft based on beans/gear/constraints
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Tool-driven generation of typed recipe draft based on beans/gear/constraints
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: No feature-specific physical acceptance recorded in this audit.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S2-070 — S2-070: All tool args schema validated; math performed by domain engine; user confirms save

- Linked requirement: S2-070 (coffee-creator-blueprint/SPRINT_02_COMPETITIVE_FEATURE_IMPLEMENTATION.md:211).
- Title: Verify All tool args schema validated; math performed by domain engine; user confirms save and its failure/recovery behavior.
- Priority: P0.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'AI Chat / Formulate / Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S2-070; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'All tool args schema validated; math performed by domain engine; user confirms save', then All tool args schema validated; math performed by domain engine; user confirms save. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: All tool args schema validated; math performed by domain engine; user confirms save
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: All tool args schema validated; math performed by domain engine; user confirms save
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: No feature-specific physical acceptance recorded in this audit.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S2-071 — S2-071: RAG answers cite local uploaded document page/section; no-evidence abstention

- Linked requirement: S2-071 (coffee-creator-blueprint/SPRINT_02_COMPETITIVE_FEATURE_IMPLEMENTATION.md:212).
- Title: Verify RAG answers cite local uploaded document page/section; no-evidence abstention and its failure/recovery behavior.
- Priority: P0.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'AI Chat / Formulate / Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S2-071; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'RAG answers cite local uploaded document page/section; no-evidence abstention', then RAG answers cite local uploaded document page/section; no-evidence abstention. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: RAG answers cite local uploaded document page/section; no-evidence abstention
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: RAG answers cite local uploaded document page/section; no-evidence abstention
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: No feature-specific physical acceptance recorded in this audit.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: D-009. - Static verdict lead: PARTIAL.

### TC-REQ-S2-072 — S2-072: Local barista persona presets + safe custom system prompt controls

- Linked requirement: S2-072 (coffee-creator-blueprint/SPRINT_02_COMPETITIVE_FEATURE_IMPLEMENTATION.md:213).
- Title: Verify Local barista persona presets + safe custom system prompt controls and its failure/recovery behavior.
- Priority: P1.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'AI Chat / Formulate / Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S2-072; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Local barista persona presets + safe custom system prompt controls', then Local barista persona presets + safe custom system prompt controls. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Local barista persona presets + safe custom system prompt controls
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Local barista persona presets + safe custom system prompt controls
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: No feature-specific physical acceptance recorded in this audit.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S2-073 — S2-073: Optional weather inputs are user supplied; never require live weather API

- Linked requirement: S2-073 (coffee-creator-blueprint/SPRINT_02_COMPETITIVE_FEATURE_IMPLEMENTATION.md:214).
- Title: Verify Optional weather inputs are user supplied; never require live weather API and its failure/recovery behavior.
- Priority: P2.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'AI Chat / Formulate / Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S2-073; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Optional weather inputs are user supplied; never require live weather API', then Optional weather inputs are user supplied; never require live weather API. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Optional weather inputs are user supplied; never require live weather API
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Optional weather inputs are user supplied; never require live weather API
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: No feature-specific physical acceptance recorded in this audit.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S2-074 — S2-074: Ingredient/allergen warnings and provenance; never assume perfume is food-safe

- Linked requirement: S2-074 (coffee-creator-blueprint/SPRINT_02_COMPETITIVE_FEATURE_IMPLEMENTATION.md:215).
- Title: Verify Ingredient/allergen warnings and provenance; never assume perfume is food-safe and its failure/recovery behavior.
- Priority: P0.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'AI Chat / Formulate / Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S2-074; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Ingredient/allergen warnings and provenance; never assume perfume is food-safe', then Ingredient/allergen warnings and provenance; never assume perfume is food-safe. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Ingredient/allergen warnings and provenance; never assume perfume is food-safe
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Ingredient/allergen warnings and provenance; never assume perfume is food-safe
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: No feature-specific physical acceptance recorded in this audit.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S2-075 — S2-075: SAF persistent model/asset manifest, checksum scan and reinstall rediscovery

- Linked requirement: S2-075 (coffee-creator-blueprint/SPRINT_02_COMPETITIVE_FEATURE_IMPLEMENTATION.md:221).
- Title: Verify SAF persistent model/asset manifest, checksum scan and reinstall rediscovery and its failure/recovery behavior.
- Priority: P0.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Models / Settings / app-wide recovery'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S2-075; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'SAF persistent model/asset manifest, checksum scan and reinstall rediscovery', then SAF persistent model/asset manifest, checksum scan and reinstall rediscovery. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: SAF persistent model/asset manifest, checksum scan and reinstall rediscovery
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: SAF persistent model/asset manifest, checksum scan and reinstall rediscovery
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: No feature-specific physical acceptance recorded in this audit.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S2-076 — S2-076: User-initiated resumable model/OCR download with network resume, ETag and range

- Linked requirement: S2-076 (coffee-creator-blueprint/SPRINT_02_COMPETITIVE_FEATURE_IMPLEMENTATION.md:222).
- Title: Verify User-initiated resumable model/OCR download with network resume, ETag and range and its failure/recovery behavior.
- Priority: P0.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Models / Settings / app-wide recovery'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S2-076; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'User-initiated resumable model/OCR download with network resume, ETag and range', then User-initiated resumable model/OCR download with network resume, ETag and range. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: User-initiated resumable model/OCR download with network resume, ETag and range
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: User-initiated resumable model/OCR download with network resume, ETag and range
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: No feature-specific physical acceptance recorded in this audit.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S2-077 — S2-077: Explicit full backup/restore for recipes, journal, cuppings, profiles, settings

- Linked requirement: S2-077 (coffee-creator-blueprint/SPRINT_02_COMPETITIVE_FEATURE_IMPLEMENTATION.md:223).
- Title: Verify Explicit full backup/restore for recipes, journal, cuppings, profiles, settings and its failure/recovery behavior.
- Priority: P0.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Models / Settings / app-wide recovery'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S2-077; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Explicit full backup/restore for recipes, journal, cuppings, profiles, settings', then Explicit full backup/restore for recipes, journal, cuppings, profiles, settings. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Explicit full backup/restore for recipes, journal, cuppings, profiles, settings
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Explicit full backup/restore for recipes, journal, cuppings, profiles, settings
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: No feature-specific physical acceptance recorded in this audit.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: D-004. - Static verdict lead: PARTIAL.

### TC-REQ-S2-078 — S2-078: On-device language EN/ID, localized units, offline help and error recovery

- Linked requirement: S2-078 (coffee-creator-blueprint/SPRINT_02_COMPETITIVE_FEATURE_IMPLEMENTATION.md:224).
- Title: Verify On-device language EN/ID, localized units, offline help and error recovery and its failure/recovery behavior.
- Priority: P1.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Models / Settings / app-wide recovery'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S2-078; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'On-device language EN/ID, localized units, offline help and error recovery', then On-device language EN/ID, localized units, offline help and error recovery. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: On-device language EN/ID, localized units, offline help and error recovery
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: On-device language EN/ID, localized units, offline help and error recovery
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: No feature-specific physical acceptance recorded in this audit.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S2-079 — S2-079: Adaptive Compose phone/tablet layouts, font scale, TalkBack and touch targets

- Linked requirement: S2-079 (coffee-creator-blueprint/SPRINT_02_COMPETITIVE_FEATURE_IMPLEMENTATION.md:225).
- Title: Verify Adaptive Compose phone/tablet layouts, font scale, TalkBack and touch targets and its failure/recovery behavior.
- Priority: P0.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Models / Settings / app-wide recovery'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S2-079; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Adaptive Compose phone/tablet layouts, font scale, TalkBack and touch targets', then Adaptive Compose phone/tablet layouts, font scale, TalkBack and touch targets. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Adaptive Compose phone/tablet layouts, font scale, TalkBack and touch targets
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Adaptive Compose phone/tablet layouts, font scale, TalkBack and touch targets
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: No feature-specific physical acceptance recorded in this audit.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S2-080 — S2-080: On-device activity diagnostics and opt-in local export; no telemetry SDK

- Linked requirement: S2-080 (coffee-creator-blueprint/SPRINT_02_COMPETITIVE_FEATURE_IMPLEMENTATION.md:226).
- Title: Verify On-device activity diagnostics and opt-in local export; no telemetry SDK and its failure/recovery behavior.
- Priority: P1.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Models / Settings / app-wide recovery'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S2-080; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'On-device activity diagnostics and opt-in local export; no telemetry SDK', then On-device activity diagnostics and opt-in local export; no telemetry SDK. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: On-device activity diagnostics and opt-in local export; no telemetry SDK
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: On-device activity diagnostics and opt-in local export; no telemetry SDK
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: No feature-specific physical acceptance recorded in this audit.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S2-081 — S2-081: No LLM/no OCR/no BLE graceful degradation paths

- Linked requirement: S2-081 (coffee-creator-blueprint/SPRINT_02_COMPETITIVE_FEATURE_IMPLEMENTATION.md:227).
- Title: Verify No LLM/no OCR/no BLE graceful degradation paths and its failure/recovery behavior.
- Priority: P0.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Models / Settings / app-wide recovery'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S2-081; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'No LLM/no OCR/no BLE graceful degradation paths', then No LLM/no OCR/no BLE graceful degradation paths. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: No LLM/no OCR/no BLE graceful degradation paths
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: No LLM/no OCR/no BLE graceful degradation paths
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: No feature-specific physical acceptance recorded in this audit.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S2-082 — S2-082: Regression/performance matrix (offline, process death, uninstall/reinstall, low RAM)

- Linked requirement: S2-082 (coffee-creator-blueprint/SPRINT_02_COMPETITIVE_FEATURE_IMPLEMENTATION.md:228).
- Title: Verify Regression/performance matrix (offline, process death, uninstall/reinstall, low RAM) and its failure/recovery behavior.
- Priority: P0.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Models / Settings / app-wide recovery'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S2-082; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Regression/performance matrix (offline, process death, uninstall/reinstall, low RAM)', then Regression/performance matrix (offline, process death, uninstall/reinstall, low RAM). Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Regression/performance matrix (offline, process death, uninstall/reinstall, low RAM)
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Regression/performance matrix (offline, process death, uninstall/reinstall, low RAM)
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: No feature-specific physical acceptance recorded in this audit.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-GR-01 — GR-01: 44-model grinder catalog

- Linked requirement: GR-01 (coffee-creator-blueprint/SPRINT_03_MANUAL_BREW_GRINDER_INTELLIGENCE.md:150).
- Title: Verify 44-model grinder catalog and its failure/recovery behavior.
- Priority: P0.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Library / Brew'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for GR-01; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes '44-model grinder catalog', then All imported rows searchable, source/status visible, electric/hand correctly classified. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: All imported rows searchable, source/status visible, electric/hand correctly classified
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: All imported rows searchable, source/status visible, electric/hand correctly classified
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: No compatible physical grinder measurement/device test recorded.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-GR-02 — GR-02: Grinder model variants

- Linked requirement: GR-02 (coffee-creator-blueprint/SPRINT_03_MANUAL_BREW_GRINDER_INTELLIGENCE.md:151).
- Title: Verify Grinder model variants and its failure/recovery behavior.
- Priority: P0.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Library / Brew'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for GR-02; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Grinder model variants', then Distinct model, revision, burr, axle, dial/zero reference; never merge incompatible units. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Distinct model, revision, burr, axle, dial/zero reference; never merge incompatible units
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Distinct model, revision, burr, axle, dial/zero reference; never merge incompatible units
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: No compatible physical grinder measurement/device test recorded.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-GR-03 — GR-03: Zero calibration wizard

- Linked requirement: GR-03 (coffee-creator-blueprint/SPRINT_03_MANUAL_BREW_GRINDER_INTELLIGENCE.md:152).
- Title: Verify Zero calibration wizard and its failure/recovery behavior.
- Priority: P0.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Library / Brew'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for GR-03; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Zero calibration wizard', then Shows safe model-specific instructions and saves calibration with timestamp. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Shows safe model-specific instructions and saves calibration with timestamp
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Shows safe model-specific instructions and saves calibration with timestamp
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: No compatible physical grinder measurement/device test recorded.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-GR-04 — GR-04: Typed grind settings

- Linked requirement: GR-04 (coffee-creator-blueprint/SPRINT_03_MANUAL_BREW_GRINDER_INTELLIGENCE.md:153).
- Title: Verify Typed grind settings and its failure/recovery behavior.
- Priority: P0.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Library / Brew'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for GR-04; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Typed grind settings', then Clicks/rotations/dial/marks typed, validation, no implicit cross-model conversion. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Clicks/rotations/dial/marks typed, validation, no implicit cross-model conversion
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Clicks/rotations/dial/marks typed, validation, no implicit cross-model conversion
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: No compatible physical grinder measurement/device test recorded.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-GR-05 — GR-05: Manufacturer evidence overlay

- Linked requirement: GR-05 (coffee-creator-blueprint/SPRINT_03_MANUAL_BREW_GRINDER_INTELLIGENCE.md:154).
- Title: Verify Manufacturer evidence overlay and its failure/recovery behavior.
- Priority: P0.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Library / Brew'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for GR-05; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Manufacturer evidence overlay', then Show verified source alongside raw user range; conflicting values both retained. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Show verified source alongside raw user range; conflicting values both retained
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Show verified source alongside raw user range; conflicting values both retained
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: No compatible physical grinder measurement/device test recorded.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-GR-06 — GR-06: Grinder profile

- Linked requirement: GR-06 (coffee-creator-blueprint/SPRINT_03_MANUAL_BREW_GRINDER_INTELLIGENCE.md:155).
- Title: Verify Grinder profile and its failure/recovery behavior.
- Priority: P0.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Library / Brew'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for GR-06; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Grinder profile', then Save owned grinder, burr condition, custom setting, calibration notes. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Save owned grinder, burr condition, custom setting, calibration notes
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Save owned grinder, burr condition, custom setting, calibration notes
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: No compatible physical grinder measurement/device test recorded.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-GR-07 — GR-07: Grind range finder

- Linked requirement: GR-07 (coffee-creator-blueprint/SPRINT_03_MANUAL_BREW_GRINDER_INTELLIGENCE.md:156).
- Title: Verify Grind range finder and its failure/recovery behavior.
- Priority: P0.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Library / Brew'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for GR-07; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Grind range finder', then Filter by model + brewer + method; recommend starting point only if evidence exists. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Filter by model + brewer + method; recommend starting point only if evidence exists
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Filter by model + brewer + method; recommend starting point only if evidence exists
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: No compatible physical grinder measurement/device test recorded.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-GR-08 — GR-08: Dial-in assistant

- Linked requirement: GR-08 (coffee-creator-blueprint/SPRINT_03_MANUAL_BREW_GRINDER_INTELLIGENCE.md:157).
- Title: Verify Dial-in assistant and its failure/recovery behavior.
- Priority: P1.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Library / Brew'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for GR-08; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Dial-in assistant', then User feedback and brew time drive reversible finer/coarser experiments, not deterministic taste claims. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: User feedback and brew time drive reversible finer/coarser experiments, not deterministic taste claims
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: User feedback and brew time drive reversible finer/coarser experiments, not deterministic taste claims
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: No compatible physical grinder measurement/device test recorded.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-GR-09 — GR-09: Grind history

- Linked requirement: GR-09 (coffee-creator-blueprint/SPRINT_03_MANUAL_BREW_GRINDER_INTELLIGENCE.md:158).
- Title: Verify Grind history and its failure/recovery behavior.
- Priority: P0.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Library / Brew'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for GR-09; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Grind history', then Each brew records grinder revision, settings and result; searchable comparisons. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Each brew records grinder revision, settings and result; searchable comparisons
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Each brew records grinder revision, settings and result; searchable comparisons
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: No compatible physical grinder measurement/device test recorded.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-GR-10 — GR-10: Grinder compatibility

- Linked requirement: GR-10 (coffee-creator-blueprint/SPRINT_03_MANUAL_BREW_GRINDER_INTELLIGENCE.md:159).
- Title: Verify Grinder compatibility and its failure/recovery behavior.
- Priority: P1.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Library / Brew'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for GR-10; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Grinder compatibility', then Warn when grinder is not suitable or recommendation unverified. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Warn when grinder is not suitable or recommendation unverified
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Warn when grinder is not suitable or recommendation unverified
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: No compatible physical grinder measurement/device test recorded.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-GR-11 — GR-11: Grind measurement

- Linked requirement: GR-11 (coffee-creator-blueprint/SPRINT_03_MANUAL_BREW_GRINDER_INTELLIGENCE.md:160).
- Title: Verify Grind measurement and its failure/recovery behavior.
- Priority: P2.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Library / Brew'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for GR-11; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Grind measurement', then Optional sieve/particle size metadata only if actually measured. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Optional sieve/particle size metadata only if actually measured
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Optional sieve/particle size metadata only if actually measured
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: No compatible physical grinder measurement/device test recorded.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-GR-12 — GR-12: Calibration transfer

- Linked requirement: GR-12 (coffee-creator-blueprint/SPRINT_03_MANUAL_BREW_GRINDER_INTELLIGENCE.md:161).
- Title: Verify Calibration transfer and its failure/recovery behavior.
- Priority: P1.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Library / Brew'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for GR-12; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Calibration transfer', then Export/import profile with schema version and original raw values. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Export/import profile with schema version and original raw values
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Export/import profile with schema version and original raw values
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: No compatible physical grinder measurement/device test recorded.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-BR-01 — BR-01: 26-dripper catalog

- Linked requirement: BR-01 (coffee-creator-blueprint/SPRINT_03_MANUAL_BREW_GRINDER_INTELLIGENCE.md:162).
- Title: Verify 26-dripper catalog and its failure/recovery behavior.
- Priority: P0.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Brew / Journal / Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for BR-01; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes '26-dripper catalog', then All 26 source rows imported, missing data stays UNKNOWN. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: All 26 source rows imported, missing data stays UNKNOWN
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: All 26 source rows imported, missing data stays UNKNOWN
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: BLE only for BR-07/16/18-adjacent cases; no compatible scale connected for acceptance.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-BR-02 — BR-02: Brewer geometry/mode

- Linked requirement: BR-02 (coffee-creator-blueprint/SPRINT_03_MANUAL_BREW_GRINDER_INTELLIGENCE.md:163).
- Title: Verify Brewer geometry/mode and its failure/recovery behavior.
- Priority: P0.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Brew / Journal / Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for BR-02; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Brewer geometry/mode', then Cone, flat, trapezoid, immersion, hybrid; distinguish valve open/closed. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Cone, flat, trapezoid, immersion, hybrid; distinguish valve open/closed
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Cone, flat, trapezoid, immersion, hybrid; distinguish valve open/closed
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: BLE only for BR-07/16/18-adjacent cases; no compatible scale connected for acceptance.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-BR-03 — BR-03: Filter compatibility

- Linked requirement: BR-03 (coffee-creator-blueprint/SPRINT_03_MANUAL_BREW_GRINDER_INTELLIGENCE.md:164).
- Title: Verify Filter compatibility and its failure/recovery behavior.
- Priority: P1.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Brew / Journal / Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for BR-03; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Filter compatibility', then Filter shape/size/material compatibility with documented source. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Filter shape/size/material compatibility with documented source
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Filter shape/size/material compatibility with documented source
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: BLE only for BR-07/16/18-adjacent cases; no compatible scale connected for acceptance.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-BR-04 — BR-04: Manual brew composer

- Linked requirement: BR-04 (coffee-creator-blueprint/SPRINT_03_MANUAL_BREW_GRINDER_INTELLIGENCE.md:165).
- Title: Verify Manual brew composer and its failure/recovery behavior.
- Priority: P0.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Brew / Journal / Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for BR-04; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Manual brew composer', then Select bean, grinder, dripper, filter, dose, water, temp, pour strategy. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Select bean, grinder, dripper, filter, dose, water, temp, pour strategy
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Select bean, grinder, dripper, filter, dose, water, temp, pour strategy
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: BLE only for BR-07/16/18-adjacent cases; no compatible scale connected for acceptance.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-BR-05 — BR-05: Ratio & dose scaling

- Linked requirement: BR-05 (coffee-creator-blueprint/SPRINT_03_MANUAL_BREW_GRINDER_INTELLIGENCE.md:166).
- Title: Verify Ratio & dose scaling and its failure/recovery behavior.
- Priority: P0.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Brew / Journal / Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for BR-05; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Ratio & dose scaling', then Typed Kotlin calculation; exact units, immutable source recipe. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Typed Kotlin calculation; exact units, immutable source recipe
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Typed Kotlin calculation; exact units, immutable source recipe
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: BLE only for BR-07/16/18-adjacent cases; no compatible scale connected for acceptance.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-BR-06 — BR-06: Bloom and staged pours

- Linked requirement: BR-06 (coffee-creator-blueprint/SPRINT_03_MANUAL_BREW_GRINDER_INTELLIGENCE.md:167).
- Title: Verify Bloom and staged pours and its failure/recovery behavior.
- Priority: P0.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Brew / Journal / Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for BR-06; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Bloom and staged pours', then Absolute/cumulative targets, editable durations, mass validation. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Absolute/cumulative targets, editable durations, mass validation
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Absolute/cumulative targets, editable durations, mass validation
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: BLE only for BR-07/16/18-adjacent cases; no compatible scale connected for acceptance.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-BR-07 — BR-07: Offline brew timer

- Linked requirement: BR-07 (coffee-creator-blueprint/SPRINT_03_MANUAL_BREW_GRINDER_INTELLIGENCE.md:168).
- Title: Verify Offline brew timer and its failure/recovery behavior.
- Priority: P0.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Brew / Journal / Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for BR-07; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Offline brew timer', then Pause/resume, interruption recovery, haptic/sound cues, screen-awake optional. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Pause/resume, interruption recovery, haptic/sound cues, screen-awake optional
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Pause/resume, interruption recovery, haptic/sound cues, screen-awake optional
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: BLE only for BR-07/16/18-adjacent cases; no compatible scale connected for acceptance.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: D-006. - Static verdict lead: PARTIAL.

### TC-REQ-BR-08 — BR-08: Manual weigh-ins

- Linked requirement: BR-08 (coffee-creator-blueprint/SPRINT_03_MANUAL_BREW_GRINDER_INTELLIGENCE.md:169).
- Title: Verify Manual weigh-ins and its failure/recovery behavior.
- Priority: P0.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Brew / Journal / Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for BR-08; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Manual weigh-ins', then Allow no BLE; measured weights clearly distinguished from targets. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Allow no BLE; measured weights clearly distinguished from targets
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Allow no BLE; measured weights clearly distinguished from targets
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: BLE only for BR-07/16/18-adjacent cases; no compatible scale connected for acceptance.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-BR-09 — BR-09: Brew diagnostics

- Linked requirement: BR-09 (coffee-creator-blueprint/SPRINT_03_MANUAL_BREW_GRINDER_INTELLIGENCE.md:170).
- Title: Verify Brew diagnostics and its failure/recovery behavior.
- Priority: P1.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Brew / Journal / Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for BR-09; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Brew diagnostics', then Record drawdown, clogging, bypass, channeling observations without fake inference. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Record drawdown, clogging, bypass, channeling observations without fake inference
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Record drawdown, clogging, bypass, channeling observations without fake inference
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: BLE only for BR-07/16/18-adjacent cases; no compatible scale connected for acceptance.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-BR-10 — BR-10: Water & temperature

- Linked requirement: BR-10 (coffee-creator-blueprint/SPRINT_03_MANUAL_BREW_GRINDER_INTELLIGENCE.md:171).
- Title: Verify Water & temperature and its failure/recovery behavior.
- Priority: P1.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Brew / Journal / Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for BR-10; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Water & temperature', then User-entered mineral profile and temp; unknown values remain unknown. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: User-entered mineral profile and temp; unknown values remain unknown
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: User-entered mineral profile and temp; unknown values remain unknown
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: BLE only for BR-07/16/18-adjacent cases; no compatible scale connected for acceptance.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-BR-11 — BR-11: Recipe methods

- Linked requirement: BR-11 (coffee-creator-blueprint/SPRINT_03_MANUAL_BREW_GRINDER_INTELLIGENCE.md:172).
- Title: Verify Recipe methods and its failure/recovery behavior.
- Priority: P0.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Brew / Journal / Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for BR-11; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Recipe methods', then V60, Kalita, Chemex, Switch, Clever, Origami, immersion, 4:6, pulse/continuous. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: V60, Kalita, Chemex, Switch, Clever, Origami, immersion, 4:6, pulse/continuous
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: V60, Kalita, Chemex, Switch, Clever, Origami, immersion, 4:6, pulse/continuous
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: BLE only for BR-07/16/18-adjacent cases; no compatible scale connected for acceptance.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-BR-12 — BR-12: Championship templates

- Linked requirement: BR-12 (coffee-creator-blueprint/SPRINT_03_MANUAL_BREW_GRINDER_INTELLIGENCE.md:173).
- Title: Verify Championship templates and its failure/recovery behavior.
- Priority: P1.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Brew / Journal / Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for BR-12; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Championship templates', then Verified attribution, year, source and permitted paraphrase; versioned originals. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Verified attribution, year, source and permitted paraphrase; versioned originals
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Verified attribution, year, source and permitted paraphrase; versioned originals
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: BLE only for BR-07/16/18-adjacent cases; no compatible scale connected for acceptance.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-BR-13 — BR-13: Sensory feedback

- Linked requirement: BR-13 (coffee-creator-blueprint/SPRINT_03_MANUAL_BREW_GRINDER_INTELLIGENCE.md:174).
- Title: Verify Sensory feedback and its failure/recovery behavior.
- Priority: P0.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Brew / Journal / Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for BR-13; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Sensory feedback', then Link observation to brew and Roda Rasa vocabulary without overwriting legacy records. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Link observation to brew and Roda Rasa vocabulary without overwriting legacy records
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Link observation to brew and Roda Rasa vocabulary without overwriting legacy records
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: BLE only for BR-07/16/18-adjacent cases; no compatible scale connected for acceptance.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-BR-14 — BR-14: Experiment compare

- Linked requirement: BR-14 (coffee-creator-blueprint/SPRINT_03_MANUAL_BREW_GRINDER_INTELLIGENCE.md:175).
- Title: Verify Experiment compare and its failure/recovery behavior.
- Priority: P1.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Brew / Journal / Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for BR-14; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Experiment compare', then A/B comparisons, control variable and note changes. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: A/B comparisons, control variable and note changes
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: A/B comparisons, control variable and note changes
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: BLE only for BR-07/16/18-adjacent cases; no compatible scale connected for acceptance.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-BR-15 — BR-15: Local AI assistant

- Linked requirement: BR-15 (coffee-creator-blueprint/SPRINT_03_MANUAL_BREW_GRINDER_INTELLIGENCE.md:176).
- Title: Verify Local AI assistant and its failure/recovery behavior.
- Priority: P1.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Brew / Journal / Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for BR-15; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Local AI assistant', then Optional RAG grounding and typed Kotlin tools; no unverified source claims. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Optional RAG grounding and typed Kotlin tools; no unverified source claims
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Optional RAG grounding and typed Kotlin tools; no unverified source claims
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: BLE only for BR-07/16/18-adjacent cases; no compatible scale connected for acceptance.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-BR-16 — BR-16: Accessibility

- Linked requirement: BR-16 (coffee-creator-blueprint/SPRINT_03_MANUAL_BREW_GRINDER_INTELLIGENCE.md:177).
- Title: Verify Accessibility and its failure/recovery behavior.
- Priority: P0.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Brew / Journal / Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for BR-16; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Accessibility', then 48dp targets, TalkBack, 200% font, offline/error/empty states. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: 48dp targets, TalkBack, 200% font, offline/error/empty states
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: 48dp targets, TalkBack, 200% font, offline/error/empty states
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: BLE only for BR-07/16/18-adjacent cases; no compatible scale connected for acceptance.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-BR-17 — BR-17: Video-reference audit

- Linked requirement: BR-17 (coffee-creator-blueprint/SPRINT_03_MANUAL_BREW_GRINDER_INTELLIGENCE.md:178).
- Title: Verify Video-reference audit and its failure/recovery behavior.
- Priority: P1.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Brew / Journal / Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for BR-17; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Video-reference audit', then Timestamped verified feature inventory from requested video before implementation. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Timestamped verified feature inventory from requested video before implementation
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Timestamped verified feature inventory from requested video before implementation
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: BLE only for BR-07/16/18-adjacent cases; no compatible scale connected for acceptance.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: D-007. - Static verdict lead: BLOCKED.

### TC-REQ-BR-18 — BR-18: Migration & preservation

- Linked requirement: BR-18 (coffee-creator-blueprint/SPRINT_03_MANUAL_BREW_GRINDER_INTELLIGENCE.md:179).
- Title: Verify Migration & preservation and its failure/recovery behavior.
- Priority: P0.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Brew / Journal / Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for BR-18; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Migration & preservation', then All Sprint 01/02 data/flows intact after additive migration. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: All Sprint 01/02 data/flows intact after additive migration
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: All Sprint 01/02 data/flows intact after additive migration
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: BLE only for BR-07/16/18-adjacent cases; no compatible scale connected for acceptance.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-EDU-01 — EDU-01: Coffee botany: Coffea arabica, canephora, varieties, cultivars, terroir, altitude, processing, roast; Learning outcome: Identify provenance and avoid taste stereotypes

- Linked requirement: EDU-01 (coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:50).
- Title: Verify Coffee botany: Coffea arabica, canephora, varieties, cultivars, terroir, altitude, processing, roast; Learning outcome: Identify provenance and avoid taste stereotypes and its failure/recovery behavior.
- Priority: P1 (educational content).
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Sensory → Learn'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for EDU-01; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Coffee botany: Coffea arabica, canephora, varieties, cultivars, terroir, altitude, processing, roast; Learning outcome: Identify provenance and avoid taste stereotypes', then Identify provenance and avoid taste stereotypes. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Identify provenance and avoid taste stereotypes
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Identify provenance and avoid taste stereotypes
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: Human sensory exercises require user-owned physical references; not run.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-EDU-02 — EDU-02: From cherry to green: harvesting, washed/natural/honey, fermentation, drying, storage, green defects; Learning outcome: Process comparison; record observations vs assumptions

- Linked requirement: EDU-02 (coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:51).
- Title: Verify From cherry to green: harvesting, washed/natural/honey, fermentation, drying, storage, green defects; Learning outcome: Process comparison; record observations vs assumptions and its failure/recovery behavior.
- Priority: P1 (educational content).
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Sensory → Learn'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for EDU-02; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'From cherry to green: harvesting, washed/natural/honey, fermentation, drying, storage, green defects; Learning outcome: Process comparison; record observations vs assumptions', then Process comparison; record observations vs assumptions. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Process comparison; record observations vs assumptions
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Process comparison; record observations vs assumptions
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: Human sensory exercises require user-owned physical references; not run.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-EDU-03 — EDU-03: Sensory science: stimulus, thresholds, intensity, adaptation, fatigue, bias, repeatability, sensitivity vs specificity; Learning outcome: Perception/bias quiz

- Linked requirement: EDU-03 (coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:52).
- Title: Verify Sensory science: stimulus, thresholds, intensity, adaptation, fatigue, bias, repeatability, sensitivity vs specificity; Learning outcome: Perception/bias quiz and its failure/recovery behavior.
- Priority: P1 (educational content).
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Sensory → Learn'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for EDU-03; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Sensory science: stimulus, thresholds, intensity, adaptation, fatigue, bias, repeatability, sensitivity vs specificity; Learning outcome: Perception/bias quiz', then Perception/bias quiz. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Perception/bias quiz
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Perception/bias quiz
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: Human sensory exercises require user-owned physical references; not run.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-EDU-04 — EDU-04: Olfactory anatomy: orthonasal and retronasal smell, receptors, neural processing, aroma vs fragrance; Learning outcome: Smell-first and retronasal exercise

- Linked requirement: EDU-04 (coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:53).
- Title: Verify Olfactory anatomy: orthonasal and retronasal smell, receptors, neural processing, aroma vs fragrance; Learning outcome: Smell-first and retronasal exercise and its failure/recovery behavior.
- Priority: P1 (educational content).
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Sensory → Learn'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for EDU-04; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Olfactory anatomy: orthonasal and retronasal smell, receptors, neural processing, aroma vs fragrance; Learning outcome: Smell-first and retronasal exercise', then Smell-first and retronasal exercise. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Smell-first and retronasal exercise
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Smell-first and retronasal exercise
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: Human sensory exercises require user-owned physical references; not run.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-EDU-05 — EDU-05: Gustatory anatomy: **five** widely recognized basic tastes—sweet, sour, salty, bitter, umami; “acid” is not a sixth taste; sourness is a taste and perceived acidity is broader; Learning outcome: Blind taste recognition

- Linked requirement: EDU-05 (coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:54).
- Title: Verify Gustatory anatomy: **five** widely recognized basic tastes—sweet, sour, salty, bitter, umami; “acid” is not a sixth taste; sourness is a taste and perceived acidity is broader; Learning outcome: Blind taste recognition and its failure/recovery behavior.
- Priority: P1 (educational content).
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Sensory → Learn'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for EDU-05; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Gustatory anatomy: **five** widely recognized basic tastes—sweet, sour, salty, bitter, umami; “acid” is not a sixth taste; sourness is a taste and perceived acidity is broader; Learning outcome: Blind taste recognition', then Blind taste recognition. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Blind taste recognition
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Blind taste recognition
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: Human sensory exercises require user-owned physical references; not run.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-EDU-06 — EDU-06: Trigeminal/mouthfeel: astringency, tactile dryness, viscosity, body, temperature, chemesthesis; Learning outcome: Texture discrimination

- Linked requirement: EDU-06 (coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:55).
- Title: Verify Trigeminal/mouthfeel: astringency, tactile dryness, viscosity, body, temperature, chemesthesis; Learning outcome: Texture discrimination and its failure/recovery behavior.
- Priority: P1 (educational content).
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Sensory → Learn'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for EDU-06; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Trigeminal/mouthfeel: astringency, tactile dryness, viscosity, body, temperature, chemesthesis; Learning outcome: Texture discrimination', then Texture discrimination. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Texture discrimination
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Texture discrimination
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: Human sensory exercises require user-owned physical references; not run.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-EDU-07 — EDU-07: Organic acids: citric, malic, tartaric, acetic, lactic, phosphoric, quinic; acid chemistry vs perceived sourness; safety and reference preparation; Learning outcome: Match acid references, record confidence; never claim a taste uniquely identifies a molecule

- Linked requirement: EDU-07 (coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:56).
- Title: Verify Organic acids: citric, malic, tartaric, acetic, lactic, phosphoric, quinic; acid chemistry vs perceived sourness; safety and reference preparation; Learning outcome: Match acid references, record confidence; never claim a taste uniquely identifies a molecule and its failure/recovery behavior.
- Priority: P1 (educational content).
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Sensory → Learn'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for EDU-07; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Organic acids: citric, malic, tartaric, acetic, lactic, phosphoric, quinic; acid chemistry vs perceived sourness; safety and reference preparation; Learning outcome: Match acid references, record confidence; never claim a taste uniquely identifies a molecule', then Match acid references, record confidence; never claim a taste uniquely identifies a molecule. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Match acid references, record confidence; never claim a taste uniquely identifies a molecule
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Match acid references, record confidence; never claim a taste uniquely identifies a molecule
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: Human sensory exercises require user-owned physical references; not run.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-EDU-08 — EDU-08: Coffee aromatic chemistry: volatile compounds, roasting, processing, contamination, off-notes; Learning outcome: Aroma-family categorization

- Linked requirement: EDU-08 (coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:57).
- Title: Verify Coffee aromatic chemistry: volatile compounds, roasting, processing, contamination, off-notes; Learning outcome: Aroma-family categorization and its failure/recovery behavior.
- Priority: P1 (educational content).
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Sensory → Learn'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for EDU-08; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Coffee aromatic chemistry: volatile compounds, roasting, processing, contamination, off-notes; Learning outcome: Aroma-family categorization', then Aroma-family categorization. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Aroma-family categorization
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Aroma-family categorization
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: Human sensory exercises require user-owned physical references; not run.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-EDU-09 — EDU-09: Aroma training: **Le Nez du Café 60** compatibility as a *user-owned physical kit*; independent generic aroma taxonomy and reference IDs; no unlicensed proprietary kit content; Learning outcome: Randomized vial recognition and spaced repetition

- Linked requirement: EDU-09 (coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:58).
- Title: Verify Aroma training: **Le Nez du Café 60** compatibility as a *user-owned physical kit*; independent generic aroma taxonomy and reference IDs; no unlicensed proprietary kit content; Learning outcome: Randomized vial recognition and spaced repetition and its failure/recovery behavior.
- Priority: P1 (educational content).
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Sensory → Learn'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for EDU-09; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Aroma training: **Le Nez du Café 60** compatibility as a *user-owned physical kit*; independent generic aroma taxonomy and reference IDs; no unlicensed proprietary kit content; Learning outcome: Randomized vial recognition and spaced repetition', then Randomized vial recognition and spaced repetition. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Randomized vial recognition and spaced repetition
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Randomized vial recognition and spaced repetition
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: Human sensory exercises require user-owned physical references; not run.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-EDU-10 — EDU-10: WCR lexicon, SCA flavor-wheel concepts, descriptor hierarchy, cross-cultural localization; Learning outcome: Descriptor-to-evidence mapping

- Linked requirement: EDU-10 (coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:59).
- Title: Verify WCR lexicon, SCA flavor-wheel concepts, descriptor hierarchy, cross-cultural localization; Learning outcome: Descriptor-to-evidence mapping and its failure/recovery behavior.
- Priority: P1 (educational content).
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Sensory → Learn'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for EDU-10; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'WCR lexicon, SCA flavor-wheel concepts, descriptor hierarchy, cross-cultural localization; Learning outcome: Descriptor-to-evidence mapping', then Descriptor-to-evidence mapping. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Descriptor-to-evidence mapping
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Descriptor-to-evidence mapping
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: Human sensory exercises require user-owned physical references; not run.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-EDU-11 — EDU-11: Sample prep: roast control, grinding, water, dose, vessels, time, cleaning, cross-contamination; Learning outcome: Pre-flight compliance checklist

- Linked requirement: EDU-11 (coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:60).
- Title: Verify Sample prep: roast control, grinding, water, dose, vessels, time, cleaning, cross-contamination; Learning outcome: Pre-flight compliance checklist and its failure/recovery behavior.
- Priority: P1 (educational content).
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Sensory → Learn'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for EDU-11; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Sample prep: roast control, grinding, water, dose, vessels, time, cleaning, cross-contamination; Learning outcome: Pre-flight compliance checklist', then Pre-flight compliance checklist. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Pre-flight compliance checklist
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Pre-flight compliance checklist
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: Human sensory exercises require user-owned physical references; not run.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-EDU-12 — EDU-12: SCA CVA philosophy: physical/descriptive/affective/extrinsic, bias minimization, form differences; Learning outcome: Route observations to correct form

- Linked requirement: EDU-12 (coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:61).
- Title: Verify SCA CVA philosophy: physical/descriptive/affective/extrinsic, bias minimization, form differences; Learning outcome: Route observations to correct form and its failure/recovery behavior.
- Priority: P1 (educational content).
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Sensory → Learn'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for EDU-12; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'SCA CVA philosophy: physical/descriptive/affective/extrinsic, bias minimization, form differences; Learning outcome: Route observations to correct form', then Route observations to correct form. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Route observations to correct form
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Route observations to correct form
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: Human sensory exercises require user-owned physical references; not run.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-EDU-13 — EDU-13: CVA descriptive: fragrance, aroma, flavor, aftertaste, acidity, sweetness, mouthfeel, descriptors and intensities as specified by current form; Learning outcome: Mock descriptive session

- Linked requirement: EDU-13 (coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:62).
- Title: Verify CVA descriptive: fragrance, aroma, flavor, aftertaste, acidity, sweetness, mouthfeel, descriptors and intensities as specified by current form; Learning outcome: Mock descriptive session and its failure/recovery behavior.
- Priority: P1 (educational content).
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Sensory → Learn'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for EDU-13; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'CVA descriptive: fragrance, aroma, flavor, aftertaste, acidity, sweetness, mouthfeel, descriptors and intensities as specified by current form; Learning outcome: Mock descriptive session', then Mock descriptive session. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Mock descriptive session
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Mock descriptive session
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: Human sensory exercises require user-owned physical references; not run.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-EDU-14 — EDU-14: CVA affective: preferences, quality impressions and appropriate scaling per current standard; Learning outcome: Independent affective session

- Linked requirement: EDU-14 (coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:63).
- Title: Verify CVA affective: preferences, quality impressions and appropriate scaling per current standard; Learning outcome: Independent affective session and its failure/recovery behavior.
- Priority: P1 (educational content).
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Sensory → Learn'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for EDU-14; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'CVA affective: preferences, quality impressions and appropriate scaling per current standard; Learning outcome: Independent affective session', then Independent affective session. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Independent affective session
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Independent affective session
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: Human sensory exercises require user-owned physical references; not run.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-EDU-15 — EDU-15: CVA extrinsic: origin, processing, certification claims, traceability, contextual value, data privacy; Learning outcome: Evidence-quality classification

- Linked requirement: EDU-15 (coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:64).
- Title: Verify CVA extrinsic: origin, processing, certification claims, traceability, contextual value, data privacy; Learning outcome: Evidence-quality classification and its failure/recovery behavior.
- Priority: P1 (educational content).
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Sensory → Learn'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for EDU-15; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'CVA extrinsic: origin, processing, certification claims, traceability, contextual value, data privacy; Learning outcome: Evidence-quality classification', then Evidence-quality classification. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Evidence-quality classification
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Evidence-quality classification
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: Human sensory exercises require user-owned physical references; not run.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-EDU-16 — EDU-16: Green Arabica classification: sampling, screen size, defects, moisture, physical analysis, classification systems and scope; Learning outcome: Defect-identification practice

- Linked requirement: EDU-16 (coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:65).
- Title: Verify Green Arabica classification: sampling, screen size, defects, moisture, physical analysis, classification systems and scope; Learning outcome: Defect-identification practice and its failure/recovery behavior.
- Priority: P1 (educational content).
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Sensory → Learn'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for EDU-16; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Green Arabica classification: sampling, screen size, defects, moisture, physical analysis, classification systems and scope; Learning outcome: Defect-identification practice', then Defect-identification practice. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Defect-identification practice
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Defect-identification practice
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: Human sensory exercises require user-owned physical references; not run.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-EDU-17 — EDU-17: Triangulation: 3 coded samples, two alike/one different, forced-choice, blinding, randomization, repeated trials, chance baseline 1/3; Learning outcome: Blind triangle drill

- Linked requirement: EDU-17 (coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:66).
- Title: Verify Triangulation: 3 coded samples, two alike/one different, forced-choice, blinding, randomization, repeated trials, chance baseline 1/3; Learning outcome: Blind triangle drill and its failure/recovery behavior.
- Priority: P1 (educational content).
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Sensory → Learn'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for EDU-17; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Triangulation: 3 coded samples, two alike/one different, forced-choice, blinding, randomization, repeated trials, chance baseline 1/3; Learning outcome: Blind triangle drill', then Blind triangle drill. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Blind triangle drill
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Blind triangle drill
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: Human sensory exercises require user-owned physical references; not run.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-EDU-18 — EDU-18: Calibration and panel management: reference solutions, assessor repeatability, agreement, fatigue, session environment; Learning outcome: Personal calibration trend

- Linked requirement: EDU-18 (coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:67).
- Title: Verify Calibration and panel management: reference solutions, assessor repeatability, agreement, fatigue, session environment; Learning outcome: Personal calibration trend and its failure/recovery behavior.
- Priority: P1 (educational content).
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Sensory → Learn'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for EDU-18; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Calibration and panel management: reference solutions, assessor repeatability, agreement, fatigue, session environment; Learning outcome: Personal calibration trend', then Personal calibration trend. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Personal calibration trend
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Personal calibration trend
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: Human sensory exercises require user-owned physical references; not run.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-EDU-19 — EDU-19: Roast defects and incorrect sample preparation: under/overdevelopment, baked/scorched, contamination, confounding; Learning outcome: Distinguish observed attribute from inferred cause

- Linked requirement: EDU-19 (coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:68).
- Title: Verify Roast defects and incorrect sample preparation: under/overdevelopment, baked/scorched, contamination, confounding; Learning outcome: Distinguish observed attribute from inferred cause and its failure/recovery behavior.
- Priority: P1 (educational content).
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Sensory → Learn'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for EDU-19; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Roast defects and incorrect sample preparation: under/overdevelopment, baked/scorched, contamination, confounding; Learning outcome: Distinguish observed attribute from inferred cause', then Distinguish observed attribute from inferred cause. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Distinguish observed attribute from inferred cause
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Distinguish observed attribute from inferred cause
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: Human sensory exercises require user-owned physical references; not run.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-EDU-20 — EDU-20: Professional reporting, ethics, accessibility, panel bias, data governance, traceability, inter-lab comparability; Learning outcome: Audit and reporting assignment

- Linked requirement: EDU-20 (coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:69).
- Title: Verify Professional reporting, ethics, accessibility, panel bias, data governance, traceability, inter-lab comparability; Learning outcome: Audit and reporting assignment and its failure/recovery behavior.
- Priority: P1 (educational content).
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Sensory → Learn'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for EDU-20; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Professional reporting, ethics, accessibility, panel bias, data governance, traceability, inter-lab comparability; Learning outcome: Audit and reporting assignment', then Audit and reporting assignment. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Audit and reporting assignment
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Audit and reporting assignment
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: Human sensory exercises require user-owned physical references; not run.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-EDU-21 — EDU-21: Legacy 2004 SCA form vs current CVA: why the methodology changed, no cross-protocol score equivalence; Learning outcome: Correctly label historic results

- Linked requirement: EDU-21 (coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:70).
- Title: Verify Legacy 2004 SCA form vs current CVA: why the methodology changed, no cross-protocol score equivalence; Learning outcome: Correctly label historic results and its failure/recovery behavior.
- Priority: P1 (educational content).
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Sensory → Learn'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for EDU-21; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Legacy 2004 SCA form vs current CVA: why the methodology changed, no cross-protocol score equivalence; Learning outcome: Correctly label historic results', then Correctly label historic results. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Correctly label historic results
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Correctly label historic results
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: Human sensory exercises require user-owned physical references; not run.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-EDU-22 — EDU-22: Q Grader / R Grader pathways: program ownership, exam topic overview, official training vs app practice; Learning outcome: Credential literacy assessment

- Linked requirement: EDU-22 (coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:71).
- Title: Verify Q Grader / R Grader pathways: program ownership, exam topic overview, official training vs app practice; Learning outcome: Credential literacy assessment and its failure/recovery behavior.
- Priority: P1 (educational content).
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Sensory → Learn'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for EDU-22; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Q Grader / R Grader pathways: program ownership, exam topic overview, official training vs app practice; Learning outcome: Credential literacy assessment', then Credential literacy assessment. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Credential literacy assessment
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Credential literacy assessment
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: Human sensory exercises require user-owned physical references; not run.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S4-001 — S4-001: Sensory dashboard: Learn, Practice, CVA Cupping, Green QC, Results, Protocol Library, Settings

- Linked requirement: S4-001 (coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:81).
- Title: Verify Sensory dashboard: Learn, Practice, CVA Cupping, Green QC, Results, Protocol Library, Settings and its failure/recovery behavior.
- Priority: P0.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S4-001; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Sensory dashboard: Learn, Practice, CVA Cupping, Green QC, Results, Protocol Library, Settings', then Sensory dashboard: Learn, Practice, CVA Cupping, Green QC, Results, Protocol Library, Settings. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Sensory dashboard: Learn, Practice, CVA Cupping, Green QC, Results, Protocol Library, Settings
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Sensory dashboard: Learn, Practice, CVA Cupping, Green QC, Results, Protocol Library, Settings
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: Physical sensory / instrument tests require actual user-provided samples and gear.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S4-002 — S4-002: 22 bilingual offline modules with lesson source and version; accessible navigation

- Linked requirement: S4-002 (coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:82).
- Title: Verify 22 bilingual offline modules with lesson source and version; accessible navigation and its failure/recovery behavior.
- Priority: P0.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S4-002; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes '22 bilingual offline modules with lesson source and version; accessible navigation', then 22 bilingual offline modules with lesson source and version; accessible navigation. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: 22 bilingual offline modules with lesson source and version; accessible navigation
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: 22 bilingual offline modules with lesson source and version; accessible navigation
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: Physical sensory / instrument tests require actual user-provided samples and gear.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S4-003 — S4-003: Five-taste interactive lessons, taste intensity and difference between chemical and perceived acidity

- Linked requirement: S4-003 (coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:83).
- Title: Verify Five-taste interactive lessons, taste intensity and difference between chemical and perceived acidity and its failure/recovery behavior.
- Priority: P0.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S4-003; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Five-taste interactive lessons, taste intensity and difference between chemical and perceived acidity', then Five-taste interactive lessons, taste intensity and difference between chemical and perceived acidity. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Five-taste interactive lessons, taste intensity and difference between chemical and perceived acidity
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Five-taste interactive lessons, taste intensity and difference between chemical and perceived acidity
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: Physical sensory / instrument tests require actual user-provided samples and gear.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S4-004 — S4-004: Organic acid atlas with descriptions, safety notices, reference prep metadata and uncertainty

- Linked requirement: S4-004 (coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:84).
- Title: Verify Organic acid atlas with descriptions, safety notices, reference prep metadata and uncertainty and its failure/recovery behavior.
- Priority: P1.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S4-004; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Organic acid atlas with descriptions, safety notices, reference prep metadata and uncertainty', then Organic acid atlas with descriptions, safety notices, reference prep metadata and uncertainty. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Organic acid atlas with descriptions, safety notices, reference prep metadata and uncertainty
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Organic acid atlas with descriptions, safety notices, reference prep metadata and uncertainty
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: Physical sensory / instrument tests require actual user-provided samples and gear.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S4-005 — S4-005: Aroma taxonomy browser, aroma family tags, orthonasal/retronasal exercises

- Linked requirement: S4-005 (coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:85).
- Title: Verify Aroma taxonomy browser, aroma family tags, orthonasal/retronasal exercises and its failure/recovery behavior.
- Priority: P0.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S4-005; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Aroma taxonomy browser, aroma family tags, orthonasal/retronasal exercises', then Aroma taxonomy browser, aroma family tags, orthonasal/retronasal exercises. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Aroma taxonomy browser, aroma family tags, orthonasal/retronasal exercises
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Aroma taxonomy browser, aroma family tags, orthonasal/retronasal exercises
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: Physical sensory / instrument tests require actual user-provided samples and gear.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S4-006 — S4-006: Le Nez du Café 60 *physical kit companion* with configurable user-owned vial identifiers; license gate for any official list/media

- Linked requirement: S4-006 (coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:86).
- Title: Verify Le Nez du Café 60 *physical kit companion* with configurable user-owned vial identifiers; license gate for any official list/media and its failure/recovery behavior.
- Priority: P1.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S4-006; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Le Nez du Café 60 *physical kit companion* with configurable user-owned vial identifiers; license gate for any official list/media', then Le Nez du Café 60 *physical kit companion* with configurable user-owned vial identifiers; license gate for any official list/media. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Le Nez du Café 60 *physical kit companion* with configurable user-owned vial identifiers; license gate for any official list/media
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Le Nez du Café 60 *physical kit companion* with configurable user-owned vial identifiers; license gate for any official list/media
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: Physical sensory / instrument tests require actual user-provided samples and gear.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S4-007 — S4-007: Flavor wheel bridge to Sprint 02; no duplicate or replacement of existing wheel, support licensing and original graphics

- Linked requirement: S4-007 (coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:87).
- Title: Verify Flavor wheel bridge to Sprint 02; no duplicate or replacement of existing wheel, support licensing and original graphics and its failure/recovery behavior.
- Priority: P1.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S4-007; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Flavor wheel bridge to Sprint 02; no duplicate or replacement of existing wheel, support licensing and original graphics', then Flavor wheel bridge to Sprint 02; no duplicate or replacement of existing wheel, support licensing and original graphics. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Flavor wheel bridge to Sprint 02; no duplicate or replacement of existing wheel, support licensing and original graphics
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Flavor wheel bridge to Sprint 02; no duplicate or replacement of existing wheel, support licensing and original graphics
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: Physical sensory / instrument tests require actual user-provided samples and gear.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S4-008 — S4-008: Quizzes, spaced repetition, difficulty progression, explanations and per-module evidence

- Linked requirement: S4-008 (coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:88).
- Title: Verify Quizzes, spaced repetition, difficulty progression, explanations and per-module evidence and its failure/recovery behavior.
- Priority: P1.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S4-008; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Quizzes, spaced repetition, difficulty progression, explanations and per-module evidence', then Quizzes, spaced repetition, difficulty progression, explanations and per-module evidence. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Quizzes, spaced repetition, difficulty progression, explanations and per-module evidence
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Quizzes, spaced repetition, difficulty progression, explanations and per-module evidence
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: Physical sensory / instrument tests require actual user-provided samples and gear.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S4-009 — S4-009: Q Grader-style **practice** learning track; non-official badges only

- Linked requirement: S4-009 (coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:89).
- Title: Verify Q Grader-style **practice** learning track; non-official badges only and its failure/recovery behavior.
- Priority: P1.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S4-009; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Q Grader-style **practice** learning track; non-official badges only', then Q Grader-style **practice** learning track; non-official badges only. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Q Grader-style **practice** learning track; non-official badges only
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Q Grader-style **practice** learning track; non-official badges only
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: Physical sensory / instrument tests require actual user-provided samples and gear.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S4-010 — S4-010: Content provenance registry, version pinning, offline availability and rights audit

- Linked requirement: S4-010 (coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:90).
- Title: Verify Content provenance registry, version pinning, offline availability and rights audit and its failure/recovery behavior.
- Priority: P0.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S4-010; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Content provenance registry, version pinning, offline availability and rights audit', then Content provenance registry, version pinning, offline availability and rights audit. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Content provenance registry, version pinning, offline availability and rights audit
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Content provenance registry, version pinning, offline availability and rights audit
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: Physical sensory / instrument tests require actual user-provided samples and gear.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: D-012. - Static verdict lead: PARTIAL.

### TC-REQ-S4-011 — S4-011: Create multi-sample session, cupper identity/pseudonym, date, lot IDs, cup count, equipment and controlled prep metadata

- Linked requirement: S4-011 (coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:96).
- Title: Verify Create multi-sample session, cupper identity/pseudonym, date, lot IDs, cup count, equipment and controlled prep metadata and its failure/recovery behavior.
- Priority: P0.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S4-011; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Create multi-sample session, cupper identity/pseudonym, date, lot IDs, cup count, equipment and controlled prep metadata', then Create multi-sample session, cupper identity/pseudonym, date, lot IDs, cup count, equipment and controlled prep metadata. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Create multi-sample session, cupper identity/pseudonym, date, lot IDs, cup count, equipment and controlled prep metadata
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Create multi-sample session, cupper identity/pseudonym, date, lot IDs, cup count, equipment and controlled prep metadata
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: Physical sensory / instrument tests require actual user-provided samples and gear.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: D-003. - Static verdict lead: PARTIAL.

### TC-REQ-S4-012 — S4-012: Protocol selector `SCA_CVA_CURRENT`, `SCA_2004_LEGACY`, `CUSTOM_RESEARCH`; visibly distinguish

- Linked requirement: S4-012 (coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:97).
- Title: Verify Protocol selector `SCA_CVA_CURRENT`, `SCA_2004_LEGACY`, `CUSTOM_RESEARCH`; visibly distinguish and its failure/recovery behavior.
- Priority: P0.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S4-012; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Protocol selector `SCA_CVA_CURRENT`, `SCA_2004_LEGACY`, `CUSTOM_RESEARCH`; visibly distinguish', then Protocol selector `SCA_CVA_CURRENT`, `SCA_2004_LEGACY`, `CUSTOM_RESEARCH`; visibly distinguish. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Protocol selector `SCA_CVA_CURRENT`, `SCA_2004_LEGACY`, `CUSTOM_RESEARCH`; visibly distinguish
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Protocol selector `SCA_CVA_CURRENT`, `SCA_2004_LEGACY`, `CUSTOM_RESEARCH`; visibly distinguish
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: Physical sensory / instrument tests require actual user-provided samples and gear.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: D-008. - Static verdict lead: BLOCKED.

### TC-REQ-S4-013 — S4-013: Digital **Physical** form with verified official field mapping and provenance

- Linked requirement: S4-013 (coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:98).
- Title: Verify Digital **Physical** form with verified official field mapping and provenance and its failure/recovery behavior.
- Priority: P0.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S4-013; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Digital **Physical** form with verified official field mapping and provenance', then Digital **Physical** form with verified official field mapping and provenance. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Digital **Physical** form with verified official field mapping and provenance
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Digital **Physical** form with verified official field mapping and provenance
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: Physical sensory / instrument tests require actual user-provided samples and gear.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: D-008. - Static verdict lead: BLOCKED.

### TC-REQ-S4-014 — S4-014: Digital **Descriptive** form with official field definitions, units, scales, required/optional fields

- Linked requirement: S4-014 (coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:99).
- Title: Verify Digital **Descriptive** form with official field definitions, units, scales, required/optional fields and its failure/recovery behavior.
- Priority: P0.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S4-014; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Digital **Descriptive** form with official field definitions, units, scales, required/optional fields', then Digital **Descriptive** form with official field definitions, units, scales, required/optional fields. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Digital **Descriptive** form with official field definitions, units, scales, required/optional fields
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Digital **Descriptive** form with official field definitions, units, scales, required/optional fields
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: Physical sensory / instrument tests require actual user-provided samples and gear.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: D-008. - Static verdict lead: BLOCKED.

### TC-REQ-S4-015 — S4-015: Digital **Affective** form separated from descriptive observations

- Linked requirement: S4-015 (coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:100).
- Title: Verify Digital **Affective** form separated from descriptive observations and its failure/recovery behavior.
- Priority: P0.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S4-015; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Digital **Affective** form separated from descriptive observations', then Digital **Affective** form separated from descriptive observations. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Digital **Affective** form separated from descriptive observations
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Digital **Affective** form separated from descriptive observations
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: Physical sensory / instrument tests require actual user-provided samples and gear.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: D-008. - Static verdict lead: BLOCKED.

### TC-REQ-S4-016 — S4-016: Digital **Extrinsic** form with traceability, sources, permissioned reveal and disclosure policy

- Linked requirement: S4-016 (coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:101).
- Title: Verify Digital **Extrinsic** form with traceability, sources, permissioned reveal and disclosure policy and its failure/recovery behavior.
- Priority: P0.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S4-016; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Digital **Extrinsic** form with traceability, sources, permissioned reveal and disclosure policy', then Digital **Extrinsic** form with traceability, sources, permissioned reveal and disclosure policy. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Digital **Extrinsic** form with traceability, sources, permissioned reveal and disclosure policy
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Digital **Extrinsic** form with traceability, sources, permissioned reveal and disclosure policy
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: Physical sensory / instrument tests require actual user-provided samples and gear.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: D-008. - Static verdict lead: BLOCKED.

### TC-REQ-S4-017 — S4-017: Prevent extrinsic reveal before blinded sensory assessment unless explicitly permitted and logged

- Linked requirement: S4-017 (coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:102).
- Title: Verify Prevent extrinsic reveal before blinded sensory assessment unless explicitly permitted and logged and its failure/recovery behavior.
- Priority: P0.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S4-017; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Prevent extrinsic reveal before blinded sensory assessment unless explicitly permitted and logged', then Prevent extrinsic reveal before blinded sensory assessment unless explicitly permitted and logged. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Prevent extrinsic reveal before blinded sensory assessment unless explicitly permitted and logged
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Prevent extrinsic reveal before blinded sensory assessment unless explicitly permitted and logged
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: Physical sensory / instrument tests require actual user-provided samples and gear.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: D-003. - Static verdict lead: PARTIAL.

### TC-REQ-S4-018 — S4-018: Step-by-step prep, fragrance/aroma, crust break, tasting rounds, timing, temperature and reminders from selected protocol

- Linked requirement: S4-018 (coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:103).
- Title: Verify Step-by-step prep, fragrance/aroma, crust break, tasting rounds, timing, temperature and reminders from selected protocol and its failure/recovery behavior.
- Priority: P0.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S4-018; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Step-by-step prep, fragrance/aroma, crust break, tasting rounds, timing, temperature and reminders from selected protocol', then Step-by-step prep, fragrance/aroma, crust break, tasting rounds, timing, temperature and reminders from selected protocol. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Step-by-step prep, fragrance/aroma, crust break, tasting rounds, timing, temperature and reminders from selected protocol
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Step-by-step prep, fragrance/aroma, crust break, tasting rounds, timing, temperature and reminders from selected protocol
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: Physical sensory / instrument tests require actual user-provided samples and gear.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: D-003. - Static verdict lead: PARTIAL.

### TC-REQ-S4-019 — S4-019: Autosave local drafts, interruption recovery, undo and no-data-loss after app process death

- Linked requirement: S4-019 (coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:104).
- Title: Verify Autosave local drafts, interruption recovery, undo and no-data-loss after app process death and its failure/recovery behavior.
- Priority: P0.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S4-019; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Autosave local drafts, interruption recovery, undo and no-data-loss after app process death', then Autosave local drafts, interruption recovery, undo and no-data-loss after app process death. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Autosave local drafts, interruption recovery, undo and no-data-loss after app process death
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Autosave local drafts, interruption recovery, undo and no-data-loss after app process death
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: Physical sensory / instrument tests require actual user-provided samples and gear.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: D-003. - Static verdict lead: PARTIAL.

### TC-REQ-S4-020 — S4-020: Phone-friendly one-handed form entry, progressive disclosure, large tap targets, offline haptics optional

- Linked requirement: S4-020 (coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:105).
- Title: Verify Phone-friendly one-handed form entry, progressive disclosure, large tap targets, offline haptics optional and its failure/recovery behavior.
- Priority: P0.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S4-020; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Phone-friendly one-handed form entry, progressive disclosure, large tap targets, offline haptics optional', then Phone-friendly one-handed form entry, progressive disclosure, large tap targets, offline haptics optional. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Phone-friendly one-handed form entry, progressive disclosure, large tap targets, offline haptics optional
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Phone-friendly one-handed form entry, progressive disclosure, large tap targets, offline haptics optional
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: Physical sensory / instrument tests require actual user-provided samples and gear.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: D-003. - Static verdict lead: PARTIAL.

### TC-REQ-S4-021 — S4-021: Multi-cup notes, inconsistencies, taints/faults and sample-level records per selected official schema

- Linked requirement: S4-021 (coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:106).
- Title: Verify Multi-cup notes, inconsistencies, taints/faults and sample-level records per selected official schema and its failure/recovery behavior.
- Priority: P0.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S4-021; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Multi-cup notes, inconsistencies, taints/faults and sample-level records per selected official schema', then Multi-cup notes, inconsistencies, taints/faults and sample-level records per selected official schema. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Multi-cup notes, inconsistencies, taints/faults and sample-level records per selected official schema
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Multi-cup notes, inconsistencies, taints/faults and sample-level records per selected official schema
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: Physical sensory / instrument tests require actual user-provided samples and gear.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: D-003. - Static verdict lead: PARTIAL.

### TC-REQ-S4-022 — S4-022: Field validation, protocol-specific calculations **only if defined in the current official standard**; no invented total score

- Linked requirement: S4-022 (coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:107).
- Title: Verify Field validation, protocol-specific calculations **only if defined in the current official standard**; no invented total score and its failure/recovery behavior.
- Priority: P0.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S4-022; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Field validation, protocol-specific calculations **only if defined in the current official standard**; no invented total score', then Field validation, protocol-specific calculations **only if defined in the current official standard**; no invented total score. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Field validation, protocol-specific calculations **only if defined in the current official standard**; no invented total score
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Field validation, protocol-specific calculations **only if defined in the current official standard**; no invented total score
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: Physical sensory / instrument tests require actual user-provided samples and gear.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: D-003. - Static verdict lead: PARTIAL.

### TC-REQ-S4-023 — S4-023: Lock/sign a completed form, preserve original, create amendments with reasons and revision history

- Linked requirement: S4-023 (coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:108).
- Title: Verify Lock/sign a completed form, preserve original, create amendments with reasons and revision history and its failure/recovery behavior.
- Priority: P0.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S4-023; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Lock/sign a completed form, preserve original, create amendments with reasons and revision history', then Lock/sign a completed form, preserve original, create amendments with reasons and revision history. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Lock/sign a completed form, preserve original, create amendments with reasons and revision history
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Lock/sign a completed form, preserve original, create amendments with reasons and revision history
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: Physical sensory / instrument tests require actual user-provided samples and gear.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: D-002. - Static verdict lead: BROKEN.

### TC-REQ-S4-024 — S4-024: PDF/CSV/JSON export of **app-native** reports; official form appearance/export only with explicit license/permission

- Linked requirement: S4-024 (coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:109).
- Title: Verify PDF/CSV/JSON export of **app-native** reports; official form appearance/export only with explicit license/permission and its failure/recovery behavior.
- Priority: P0.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S4-024; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'PDF/CSV/JSON export of **app-native** reports; official form appearance/export only with explicit license/permission', then PDF/CSV/JSON export of **app-native** reports; official form appearance/export only with explicit license/permission. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: PDF/CSV/JSON export of **app-native** reports; official form appearance/export only with explicit license/permission
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: PDF/CSV/JSON export of **app-native** reports; official form appearance/export only with explicit license/permission
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: Physical sensory / instrument tests require actual user-provided samples and gear.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: D-002. - Static verdict lead: BROKEN.

### TC-REQ-S4-025 — S4-025: Compare coffees side-by-side with protocol-aware compatible metrics and confidence labels

- Linked requirement: S4-025 (coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:110).
- Title: Verify Compare coffees side-by-side with protocol-aware compatible metrics and confidence labels and its failure/recovery behavior.
- Priority: P1.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S4-025; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Compare coffees side-by-side with protocol-aware compatible metrics and confidence labels', then Compare coffees side-by-side with protocol-aware compatible metrics and confidence labels. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Compare coffees side-by-side with protocol-aware compatible metrics and confidence labels
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Compare coffees side-by-side with protocol-aware compatible metrics and confidence labels
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: Physical sensory / instrument tests require actual user-provided samples and gear.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: D-003. - Static verdict lead: PARTIAL.

### TC-REQ-S4-026 — S4-026: Legacy 2004 cupping form archived and independently labeled; never silently convert legacy score to CVA

- Linked requirement: S4-026 (coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:111).
- Title: Verify Legacy 2004 cupping form archived and independently labeled; never silently convert legacy score to CVA and its failure/recovery behavior.
- Priority: P1.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S4-026; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Legacy 2004 cupping form archived and independently labeled; never silently convert legacy score to CVA', then Legacy 2004 cupping form archived and independently labeled; never silently convert legacy score to CVA. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Legacy 2004 cupping form archived and independently labeled; never silently convert legacy score to CVA
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Legacy 2004 cupping form archived and independently labeled; never silently convert legacy score to CVA
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: Physical sensory / instrument tests require actual user-provided samples and gear.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: D-003. - Static verdict lead: PARTIAL.

### TC-REQ-S4-027 — S4-027: Blind triangle: exactly 3 samples, 2 same + 1 different; randomize all 6 position permutations

- Linked requirement: S4-027 (coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:117).
- Title: Verify Blind triangle: exactly 3 samples, 2 same + 1 different; randomize all 6 position permutations and its failure/recovery behavior.
- Priority: P0.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S4-027; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Blind triangle: exactly 3 samples, 2 same + 1 different; randomize all 6 position permutations', then Blind triangle: exactly 3 samples, 2 same + 1 different; randomize all 6 position permutations. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Blind triangle: exactly 3 samples, 2 same + 1 different; randomize all 6 position permutations
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Blind triangle: exactly 3 samples, 2 same + 1 different; randomize all 6 position permutations
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: Physical sensory / instrument tests require actual user-provided samples and gear.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S4-028 — S4-028: No correct answer revealed until response submitted; blind labels distinct from coffee identities

- Linked requirement: S4-028 (coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:118).
- Title: Verify No correct answer revealed until response submitted; blind labels distinct from coffee identities and its failure/recovery behavior.
- Priority: P0.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S4-028; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'No correct answer revealed until response submitted; blind labels distinct from coffee identities', then No correct answer revealed until response submitted; blind labels distinct from coffee identities. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: No correct answer revealed until response submitted; blind labels distinct from coffee identities
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: No correct answer revealed until response submitted; blind labels distinct from coffee identities
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: Physical sensory / instrument tests require actual user-provided samples and gear.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S4-029 — S4-029: Track trials, randomization seed, operator, washout, correct response, guess confidence and conditions

- Linked requirement: S4-029 (coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:119).
- Title: Verify Track trials, randomization seed, operator, washout, correct response, guess confidence and conditions and its failure/recovery behavior.
- Priority: P0.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S4-029; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Track trials, randomization seed, operator, washout, correct response, guess confidence and conditions', then Track trials, randomization seed, operator, washout, correct response, guess confidence and conditions. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Track trials, randomization seed, operator, washout, correct response, guess confidence and conditions
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Track trials, randomization seed, operator, washout, correct response, guess confidence and conditions
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: Physical sensory / instrument tests require actual user-provided samples and gear.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S4-030 — S4-030: Statistics: binomial test against p=1/3 for independent comparable trials; warn about dependence and repeated learning

- Linked requirement: S4-030 (coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:120).
- Title: Verify Statistics: binomial test against p=1/3 for independent comparable trials; warn about dependence and repeated learning and its failure/recovery behavior.
- Priority: P1.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S4-030; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Statistics: binomial test against p=1/3 for independent comparable trials; warn about dependence and repeated learning', then Statistics: binomial test against p=1/3 for independent comparable trials; warn about dependence and repeated learning. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Statistics: binomial test against p=1/3 for independent comparable trials; warn about dependence and repeated learning
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Statistics: binomial test against p=1/3 for independent comparable trials; warn about dependence and repeated learning
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: Physical sensory / instrument tests require actual user-provided samples and gear.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S4-031 — S4-031: Five basic-taste identification practice with reference solution metadata, concentrations, safety checks and blind IDs

- Linked requirement: S4-031 (coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:121).
- Title: Verify Five basic-taste identification practice with reference solution metadata, concentrations, safety checks and blind IDs and its failure/recovery behavior.
- Priority: P0.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S4-031; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Five basic-taste identification practice with reference solution metadata, concentrations, safety checks and blind IDs', then Five basic-taste identification practice with reference solution metadata, concentrations, safety checks and blind IDs. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Five basic-taste identification practice with reference solution metadata, concentrations, safety checks and blind IDs
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Five basic-taste identification practice with reference solution metadata, concentrations, safety checks and blind IDs
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: Physical sensory / instrument tests require actual user-provided samples and gear.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S4-032 — S4-032: Taste intensity and thresholds: forced-choice/ascending concentration methods; do not infer clinical sensory impairment

- Linked requirement: S4-032 (coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:122).
- Title: Verify Taste intensity and thresholds: forced-choice/ascending concentration methods; do not infer clinical sensory impairment and its failure/recovery behavior.
- Priority: P1.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S4-032; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Taste intensity and thresholds: forced-choice/ascending concentration methods; do not infer clinical sensory impairment', then Taste intensity and thresholds: forced-choice/ascending concentration methods; do not infer clinical sensory impairment. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Taste intensity and thresholds: forced-choice/ascending concentration methods; do not infer clinical sensory impairment
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Taste intensity and thresholds: forced-choice/ascending concentration methods; do not infer clinical sensory impairment
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: Physical sensory / instrument tests require actual user-provided samples and gear.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S4-033 — S4-033: Olfactory family identification with physical vials/food references and hidden answer key

- Linked requirement: S4-033 (coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:123).
- Title: Verify Olfactory family identification with physical vials/food references and hidden answer key and its failure/recovery behavior.
- Priority: P0.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S4-033; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Olfactory family identification with physical vials/food references and hidden answer key', then Olfactory family identification with physical vials/food references and hidden answer key. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Olfactory family identification with physical vials/food references and hidden answer key
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Olfactory family identification with physical vials/food references and hidden answer key
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: Physical sensory / instrument tests require actual user-provided samples and gear.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S4-034 — S4-034: Aroma memory and spaced-repetition sessions; confusion matrix and confidence calibration

- Linked requirement: S4-034 (coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:124).
- Title: Verify Aroma memory and spaced-repetition sessions; confusion matrix and confidence calibration and its failure/recovery behavior.
- Priority: P1.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S4-034; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Aroma memory and spaced-repetition sessions; confusion matrix and confidence calibration', then Aroma memory and spaced-repetition sessions; confusion matrix and confidence calibration. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Aroma memory and spaced-repetition sessions; confusion matrix and confidence calibration
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Aroma memory and spaced-repetition sessions; confusion matrix and confidence calibration
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: Physical sensory / instrument tests require actual user-provided samples and gear.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S4-035 — S4-035: Organic-acid comparative tastings with trained supervision, safe food-grade preparations and source-controlled concentrations

- Linked requirement: S4-035 (coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:125).
- Title: Verify Organic-acid comparative tastings with trained supervision, safe food-grade preparations and source-controlled concentrations and its failure/recovery behavior.
- Priority: P1.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S4-035; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Organic-acid comparative tastings with trained supervision, safe food-grade preparations and source-controlled concentrations', then Organic-acid comparative tastings with trained supervision, safe food-grade preparations and source-controlled concentrations. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Organic-acid comparative tastings with trained supervision, safe food-grade preparations and source-controlled concentrations
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Organic-acid comparative tastings with trained supervision, safe food-grade preparations and source-controlled concentrations
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: Physical sensory / instrument tests require actual user-provided samples and gear.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S4-036 — S4-036: Roast defect recognition practice; include control samples and note uncertain causal inference

- Linked requirement: S4-036 (coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:126).
- Title: Verify Roast defect recognition practice; include control samples and note uncertain causal inference and its failure/recovery behavior.
- Priority: P1.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S4-036; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Roast defect recognition practice; include control samples and note uncertain causal inference', then Roast defect recognition practice; include control samples and note uncertain causal inference. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Roast defect recognition practice; include control samples and note uncertain causal inference
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Roast defect recognition practice; include control samples and note uncertain causal inference
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: Physical sensory / instrument tests require actual user-provided samples and gear.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S4-037 — S4-037: Tactile/mouthfeel discrimination; water/temperature and serving controls

- Linked requirement: S4-037 (coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:127).
- Title: Verify Tactile/mouthfeel discrimination; water/temperature and serving controls and its failure/recovery behavior.
- Priority: P1.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S4-037; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Tactile/mouthfeel discrimination; water/temperature and serving controls', then Tactile/mouthfeel discrimination; water/temperature and serving controls. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Tactile/mouthfeel discrimination; water/temperature and serving controls
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Tactile/mouthfeel discrimination; water/temperature and serving controls
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: Physical sensory / instrument tests require actual user-provided samples and gear.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S4-038 — S4-038: Blind paired comparisons and ranking exercises; label distinct from triangle tests

- Linked requirement: S4-038 (coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:128).
- Title: Verify Blind paired comparisons and ranking exercises; label distinct from triangle tests and its failure/recovery behavior.
- Priority: P1.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S4-038; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Blind paired comparisons and ranking exercises; label distinct from triangle tests', then Blind paired comparisons and ranking exercises; label distinct from triangle tests. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Blind paired comparisons and ranking exercises; label distinct from triangle tests
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Blind paired comparisons and ranking exercises; label distinct from triangle tests
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: Physical sensory / instrument tests require actual user-provided samples and gear.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S4-039 — S4-039: Personalized non-certification readiness dashboard by skill category, longitudinal practice history

- Linked requirement: S4-039 (coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:129).
- Title: Verify Personalized non-certification readiness dashboard by skill category, longitudinal practice history and its failure/recovery behavior.
- Priority: P1.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S4-039; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Personalized non-certification readiness dashboard by skill category, longitudinal practice history', then Personalized non-certification readiness dashboard by skill category, longitudinal practice history. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Personalized non-certification readiness dashboard by skill category, longitudinal practice history
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Personalized non-certification readiness dashboard by skill category, longitudinal practice history
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: Physical sensory / instrument tests require actual user-provided samples and gear.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S4-040 — S4-040: Never auto-mark a practical sensory exercise PASS based solely on reading or virtual quizzes; require actual human observation

- Linked requirement: S4-040 (coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:130).
- Title: Verify Never auto-mark a practical sensory exercise PASS based solely on reading or virtual quizzes; require actual human observation and its failure/recovery behavior.
- Priority: P0.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S4-040; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Never auto-mark a practical sensory exercise PASS based solely on reading or virtual quizzes; require actual human observation', then Never auto-mark a practical sensory exercise PASS based solely on reading or virtual quizzes; require actual human observation. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Never auto-mark a practical sensory exercise PASS based solely on reading or virtual quizzes; require actual human observation
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Never auto-mark a practical sensory exercise PASS based solely on reading or virtual quizzes; require actual human observation
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: Physical sensory / instrument tests require actual user-provided samples and gear.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S4-041 — S4-041: Green lot catalog with species, process, origin, producer, harvest, lot, sample mass, chain of custody

- Linked requirement: S4-041 (coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:136).
- Title: Verify Green lot catalog with species, process, origin, producer, harvest, lot, sample mass, chain of custody and its failure/recovery behavior.
- Priority: P0.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S4-041; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Green lot catalog with species, process, origin, producer, harvest, lot, sample mass, chain of custody', then Green lot catalog with species, process, origin, producer, harvest, lot, sample mass, chain of custody. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Green lot catalog with species, process, origin, producer, harvest, lot, sample mass, chain of custody
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Green lot catalog with species, process, origin, producer, harvest, lot, sample mass, chain of custody
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: Physical sensory / instrument tests require actual user-provided samples and gear.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: D-003. - Static verdict lead: PARTIAL.

### TC-REQ-S4-042 — S4-042: Physical defect taxonomy and primary/secondary categories with standard/version/source attribution

- Linked requirement: S4-042 (coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:137).
- Title: Verify Physical defect taxonomy and primary/secondary categories with standard/version/source attribution and its failure/recovery behavior.
- Priority: P0.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S4-042; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Physical defect taxonomy and primary/secondary categories with standard/version/source attribution', then Physical defect taxonomy and primary/secondary categories with standard/version/source attribution. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Physical defect taxonomy and primary/secondary categories with standard/version/source attribution
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Physical defect taxonomy and primary/secondary categories with standard/version/source attribution
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: Physical sensory / instrument tests require actual user-provided samples and gear.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: D-003. - Static verdict lead: PARTIAL.

### TC-REQ-S4-043 — S4-043: Record defect type, count, equivalent weighting **only where supported by selected verified standard**, evidence photo

- Linked requirement: S4-043 (coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:138).
- Title: Verify Record defect type, count, equivalent weighting **only where supported by selected verified standard**, evidence photo and its failure/recovery behavior.
- Priority: P0.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S4-043; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Record defect type, count, equivalent weighting **only where supported by selected verified standard**, evidence photo', then Record defect type, count, equivalent weighting **only where supported by selected verified standard**, evidence photo. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Record defect type, count, equivalent weighting **only where supported by selected verified standard**, evidence photo
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Record defect type, count, equivalent weighting **only where supported by selected verified standard**, evidence photo
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: Physical sensory / instrument tests require actual user-provided samples and gear.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: D-003. - Static verdict lead: PARTIAL.

### TC-REQ-S4-044 — S4-044: Green sample prep and weighing, sieve/screen-size entry with units and test methodology

- Linked requirement: S4-044 (coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:139).
- Title: Verify Green sample prep and weighing, sieve/screen-size entry with units and test methodology and its failure/recovery behavior.
- Priority: P0.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S4-044; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Green sample prep and weighing, sieve/screen-size entry with units and test methodology', then Green sample prep and weighing, sieve/screen-size entry with units and test methodology. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Green sample prep and weighing, sieve/screen-size entry with units and test methodology
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Green sample prep and weighing, sieve/screen-size entry with units and test methodology
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: Physical sensory / instrument tests require actual user-provided samples and gear.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: D-003. - Static verdict lead: PARTIAL.

### TC-REQ-S4-045 — S4-045: Moisture percentage with meter ID, calibration date, method, temperature and uncertainty; no invented readings

- Linked requirement: S4-045 (coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:140).
- Title: Verify Moisture percentage with meter ID, calibration date, method, temperature and uncertainty; no invented readings and its failure/recovery behavior.
- Priority: P0.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S4-045; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Moisture percentage with meter ID, calibration date, method, temperature and uncertainty; no invented readings', then Moisture percentage with meter ID, calibration date, method, temperature and uncertainty; no invented readings. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Moisture percentage with meter ID, calibration date, method, temperature and uncertainty; no invented readings
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Moisture percentage with meter ID, calibration date, method, temperature and uncertainty; no invented readings
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: Physical sensory / instrument tests require actual user-provided samples and gear.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: D-001. - Static verdict lead: BROKEN.

### TC-REQ-S4-046 — S4-046: Water activity, density, color, odor, foreign matter and additional QC observations if measured

- Linked requirement: S4-046 (coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:141).
- Title: Verify Water activity, density, color, odor, foreign matter and additional QC observations if measured and its failure/recovery behavior.
- Priority: P1.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S4-046; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Water activity, density, color, odor, foreign matter and additional QC observations if measured', then Water activity, density, color, odor, foreign matter and additional QC observations if measured. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Water activity, density, color, odor, foreign matter and additional QC observations if measured
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Water activity, density, color, odor, foreign matter and additional QC observations if measured
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: Physical sensory / instrument tests require actual user-provided samples and gear.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: D-001. - Static verdict lead: BROKEN.

### TC-REQ-S4-047 — S4-047: Rule engine for washed Arabica historical grading with **versioned** and sourced thresholds; refuse unsupported species/process claims

- Linked requirement: S4-047 (coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:142).
- Title: Verify Rule engine for washed Arabica historical grading with **versioned** and sourced thresholds; refuse unsupported species/process claims and its failure/recovery behavior.
- Priority: P0.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S4-047; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Rule engine for washed Arabica historical grading with **versioned** and sourced thresholds; refuse unsupported species/process claims', then Rule engine for washed Arabica historical grading with **versioned** and sourced thresholds; refuse unsupported species/process claims. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Rule engine for washed Arabica historical grading with **versioned** and sourced thresholds; refuse unsupported species/process claims
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Rule engine for washed Arabica historical grading with **versioned** and sourced thresholds; refuse unsupported species/process claims
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: Physical sensory / instrument tests require actual user-provided samples and gear.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: D-003. - Static verdict lead: PARTIAL.

### TC-REQ-S4-048 — S4-048: Distinguish green physical grade from sensory CVA outcome; never equate “specialty” solely with a numeric score

- Linked requirement: S4-048 (coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:143).
- Title: Verify Distinguish green physical grade from sensory CVA outcome; never equate “specialty” solely with a numeric score and its failure/recovery behavior.
- Priority: P0.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S4-048; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Distinguish green physical grade from sensory CVA outcome; never equate “specialty” solely with a numeric score', then Distinguish green physical grade from sensory CVA outcome; never equate “specialty” solely with a numeric score. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Distinguish green physical grade from sensory CVA outcome; never equate “specialty” solely with a numeric score
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Distinguish green physical grade from sensory CVA outcome; never equate “specialty” solely with a numeric score
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: Physical sensory / instrument tests require actual user-provided samples and gear.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: D-003. - Static verdict lead: PARTIAL.

### TC-REQ-S4-049 — S4-049: Lot comparison, supplier history, acceptance limits configured by buyer and standards

- Linked requirement: S4-049 (coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:144).
- Title: Verify Lot comparison, supplier history, acceptance limits configured by buyer and standards and its failure/recovery behavior.
- Priority: P1.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S4-049; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Lot comparison, supplier history, acceptance limits configured by buyer and standards', then Lot comparison, supplier history, acceptance limits configured by buyer and standards. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Lot comparison, supplier history, acceptance limits configured by buyer and standards
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Lot comparison, supplier history, acceptance limits configured by buyer and standards
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: Physical sensory / instrument tests require actual user-provided samples and gear.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: D-003. - Static verdict lead: PARTIAL.

### TC-REQ-S4-050 — S4-050: Green QC export with equipment calibration, assessor, photos, standard version and audit trail

- Linked requirement: S4-050 (coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:145).
- Title: Verify Green QC export with equipment calibration, assessor, photos, standard version and audit trail and its failure/recovery behavior.
- Priority: P1.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S4-050; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Green QC export with equipment calibration, assessor, photos, standard version and audit trail', then Green QC export with equipment calibration, assessor, photos, standard version and audit trail. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Green QC export with equipment calibration, assessor, photos, standard version and audit trail
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Green QC export with equipment calibration, assessor, photos, standard version and audit trail
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: Physical sensory / instrument tests require actual user-provided samples and gear.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: D-002. - Static verdict lead: BROKEN.

### TC-REQ-S4-051 — S4-051: Show `NOT_MEASURED`, `UNKNOWN`, `OUT_OF_SCOPE`, `PENDING_REVIEW` instead of fabricated values

- Linked requirement: S4-051 (coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:146).
- Title: Verify Show `NOT_MEASURED`, `UNKNOWN`, `OUT_OF_SCOPE`, `PENDING_REVIEW` instead of fabricated values and its failure/recovery behavior.
- Priority: P0.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S4-051; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Show `NOT_MEASURED`, `UNKNOWN`, `OUT_OF_SCOPE`, `PENDING_REVIEW` instead of fabricated values', then Show `NOT_MEASURED`, `UNKNOWN`, `OUT_OF_SCOPE`, `PENDING_REVIEW` instead of fabricated values. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Show `NOT_MEASURED`, `UNKNOWN`, `OUT_OF_SCOPE`, `PENDING_REVIEW` instead of fabricated values
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Show `NOT_MEASURED`, `UNKNOWN`, `OUT_OF_SCOPE`, `PENDING_REVIEW` instead of fabricated values
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: Physical sensory / instrument tests require actual user-provided samples and gear.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: D-001. - Static verdict lead: BROKEN.

### TC-REQ-S4-052 — S4-052: Full offline operation: learn, session setup, assessment, local export and retrieval

- Linked requirement: S4-052 (coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:152).
- Title: Verify Full offline operation: learn, session setup, assessment, local export and retrieval and its failure/recovery behavior.
- Priority: P0.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S4-052; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Full offline operation: learn, session setup, assessment, local export and retrieval', then Full offline operation: learn, session setup, assessment, local export and retrieval. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Full offline operation: learn, session setup, assessment, local export and retrieval
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Full offline operation: learn, session setup, assessment, local export and retrieval
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: Physical sensory / instrument tests require actual user-provided samples and gear.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S4-053 — S4-053: Additive Room schema migrations; stable UUID, FK, no destructive migration, restore tests

- Linked requirement: S4-053 (coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:153).
- Title: Verify Additive Room schema migrations; stable UUID, FK, no destructive migration, restore tests and its failure/recovery behavior.
- Priority: P0.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S4-053; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Additive Room schema migrations; stable UUID, FK, no destructive migration, restore tests', then Additive Room schema migrations; stable UUID, FK, no destructive migration, restore tests. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Additive Room schema migrations; stable UUID, FK, no destructive migration, restore tests
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Additive Room schema migrations; stable UUID, FK, no destructive migration, restore tests
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: Physical sensory / instrument tests require actual user-provided samples and gear.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S4-054 — S4-054: Reuse Sprint 02 sensory taxonomy, recipe library and existing flavor wheel rather than replacing

- Linked requirement: S4-054 (coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:154).
- Title: Verify Reuse Sprint 02 sensory taxonomy, recipe library and existing flavor wheel rather than replacing and its failure/recovery behavior.
- Priority: P0.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S4-054; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Reuse Sprint 02 sensory taxonomy, recipe library and existing flavor wheel rather than replacing', then Reuse Sprint 02 sensory taxonomy, recipe library and existing flavor wheel rather than replacing. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Reuse Sprint 02 sensory taxonomy, recipe library and existing flavor wheel rather than replacing
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Reuse Sprint 02 sensory taxonomy, recipe library and existing flavor wheel rather than replacing
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: Physical sensory / instrument tests require actual user-provided samples and gear.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S4-055 — S4-055: Link cupping result to coffee bean, roast batch, manual brew recipe, grinder/dripper session without overwriting historical data

- Linked requirement: S4-055 (coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:155).
- Title: Verify Link cupping result to coffee bean, roast batch, manual brew recipe, grinder/dripper session without overwriting historical data and its failure/recovery behavior.
- Priority: P0.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S4-055; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Link cupping result to coffee bean, roast batch, manual brew recipe, grinder/dripper session without overwriting historical data', then Link cupping result to coffee bean, roast batch, manual brew recipe, grinder/dripper session without overwriting historical data. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Link cupping result to coffee bean, roast batch, manual brew recipe, grinder/dripper session without overwriting historical data
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Link cupping result to coffee bean, roast batch, manual brew recipe, grinder/dripper session without overwriting historical data
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: Physical sensory / instrument tests require actual user-provided samples and gear.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: D-003. - Static verdict lead: PARTIAL.

### TC-REQ-S4-056 — S4-056: Separate deterministic standards/rules engine from local LLM explanatory assistant

- Linked requirement: S4-056 (coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:156).
- Title: Verify Separate deterministic standards/rules engine from local LLM explanatory assistant and its failure/recovery behavior.
- Priority: P0.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S4-056; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Separate deterministic standards/rules engine from local LLM explanatory assistant', then Separate deterministic standards/rules engine from local LLM explanatory assistant. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Separate deterministic standards/rules engine from local LLM explanatory assistant
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Separate deterministic standards/rules engine from local LLM explanatory assistant
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: Physical sensory / instrument tests require actual user-provided samples and gear.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: D-003. - Static verdict lead: PARTIAL.

### TC-REQ-S4-057 — S4-057: AI responses cite local knowledge source; never generate fake scores, measurements or credential claims

- Linked requirement: S4-057 (coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:157).
- Title: Verify AI responses cite local knowledge source; never generate fake scores, measurements or credential claims and its failure/recovery behavior.
- Priority: P0.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S4-057; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'AI responses cite local knowledge source; never generate fake scores, measurements or credential claims', then AI responses cite local knowledge source; never generate fake scores, measurements or credential claims. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: AI responses cite local knowledge source; never generate fake scores, measurements or credential claims
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: AI responses cite local knowledge source; never generate fake scores, measurements or credential claims
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: Physical sensory / instrument tests require actual user-provided samples and gear.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: D-001. - Static verdict lead: BROKEN.

### TC-REQ-S4-058 — S4-058: Blind panel workflow: session host, assessor, role-based reveal, anonymized sample codes

- Linked requirement: S4-058 (coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:158).
- Title: Verify Blind panel workflow: session host, assessor, role-based reveal, anonymized sample codes and its failure/recovery behavior.
- Priority: P1.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S4-058; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Blind panel workflow: session host, assessor, role-based reveal, anonymized sample codes', then Blind panel workflow: session host, assessor, role-based reveal, anonymized sample codes. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Blind panel workflow: session host, assessor, role-based reveal, anonymized sample codes
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Blind panel workflow: session host, assessor, role-based reveal, anonymized sample codes
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: Physical sensory / instrument tests require actual user-provided samples and gear.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: D-003. - Static verdict lead: PARTIAL.

### TC-REQ-S4-059 — S4-059: Calibration and panel agreement dashboard, inter-rater statistics only where applicable

- Linked requirement: S4-059 (coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:159).
- Title: Verify Calibration and panel agreement dashboard, inter-rater statistics only where applicable and its failure/recovery behavior.
- Priority: P1.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S4-059; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Calibration and panel agreement dashboard, inter-rater statistics only where applicable', then Calibration and panel agreement dashboard, inter-rater statistics only where applicable. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Calibration and panel agreement dashboard, inter-rater statistics only where applicable
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Calibration and panel agreement dashboard, inter-rater statistics only where applicable
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: Physical sensory / instrument tests require actual user-provided samples and gear.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: D-003. - Static verdict lead: PARTIAL.

### TC-REQ-S4-060 — S4-060: Full data export/import, migration, backups, checksums, privacy and offline retention settings

- Linked requirement: S4-060 (coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:160).
- Title: Verify Full data export/import, migration, backups, checksums, privacy and offline retention settings and its failure/recovery behavior.
- Priority: P0.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S4-060; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Full data export/import, migration, backups, checksums, privacy and offline retention settings', then Full data export/import, migration, backups, checksums, privacy and offline retention settings. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Full data export/import, migration, backups, checksums, privacy and offline retention settings
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Full data export/import, migration, backups, checksums, privacy and offline retention settings
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: Physical sensory / instrument tests require actual user-provided samples and gear.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: D-002. - Static verdict lead: BROKEN.

### TC-REQ-S4-061 — S4-061: Indonesian and English UI, glossary, screen reader, color-independent feedback, large text support

- Linked requirement: S4-061 (coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:161).
- Title: Verify Indonesian and English UI, glossary, screen reader, color-independent feedback, large text support and its failure/recovery behavior.
- Priority: P0.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S4-061; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Indonesian and English UI, glossary, screen reader, color-independent feedback, large text support', then Indonesian and English UI, glossary, screen reader, color-independent feedback, large text support. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Indonesian and English UI, glossary, screen reader, color-independent feedback, large text support
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Indonesian and English UI, glossary, screen reader, color-independent feedback, large text support
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: Physical sensory / instrument tests require actual user-provided samples and gear.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S4-062 — S4-062: Accessibility for smell/taste limitations and allergies; avoid medical diagnosis

- Linked requirement: S4-062 (coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:162).
- Title: Verify Accessibility for smell/taste limitations and allergies; avoid medical diagnosis and its failure/recovery behavior.
- Priority: P0.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S4-062; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Accessibility for smell/taste limitations and allergies; avoid medical diagnosis', then Accessibility for smell/taste limitations and allergies; avoid medical diagnosis. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Accessibility for smell/taste limitations and allergies; avoid medical diagnosis
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Accessibility for smell/taste limitations and allergies; avoid medical diagnosis
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: Physical sensory / instrument tests require actual user-provided samples and gear.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S4-063 — S4-063: Controlled form template updates with offline cache, migration and version mismatch warnings

- Linked requirement: S4-063 (coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:163).
- Title: Verify Controlled form template updates with offline cache, migration and version mismatch warnings and its failure/recovery behavior.
- Priority: P1.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S4-063; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'Controlled form template updates with offline cache, migration and version mismatch warnings', then Controlled form template updates with offline cache, migration and version mismatch warnings. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: Controlled form template updates with offline cache, migration and version mismatch warnings
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: Controlled form template updates with offline cache, migration and version mismatch warnings
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: Physical sensory / instrument tests require actual user-provided samples and gear.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S4-064 — S4-064: QA automated/unit/UI tests, device testing, evidence-based release gate

- Linked requirement: S4-064 (coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:164).
- Title: Verify QA automated/unit/UI tests, device testing, evidence-based release gate and its failure/recovery behavior.
- Priority: P0.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S4-064; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'QA automated/unit/UI tests, device testing, evidence-based release gate', then QA automated/unit/UI tests, device testing, evidence-based release gate. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: QA automated/unit/UI tests, device testing, evidence-based release gate
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: QA automated/unit/UI tests, device testing, evidence-based release gate
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: Physical sensory / instrument tests require actual user-provided samples and gear.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: None assigned before execution. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-S4-065 — S4-065: User-consent-based sharing, no automatic cloud upload of supplier lots or assessor results

- Linked requirement: S4-065 (coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:165).
- Title: Verify User-consent-based sharing, no automatic cloud upload of supplier lots or assessor results and its failure/recovery behavior.
- Priority: P1.
- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route 'Sensory'; airplane mode unless the requirement is an explicit user-requested transfer.
- Fixtures: documented, provenance-bearing values for S4-065; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.
- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes 'User-consent-based sharing, no automatic cloud upload of supplier lots or assessor results', then User-consent-based sharing, no automatic cloud upload of supplier lots or assessor results. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.
- Dimension checks:
  - Happy path: execute the row-specific action and confirm the acceptance criterion: User-consent-based sharing, no automatic cloud upload of supplier lots or assessor results
  - Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.
  - Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.
  - UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.
  - Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.
  - Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.
  - Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.
  - Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.
  - Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.
  - Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.
  - Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.
- Expected result: User-consent-based sharing, no automatic cloud upload of supplier lots or assessor results
- Actual result: Not executed; see static finding in the feature inventory when applicable.
- Execution environment: Not run; hardware need: Physical sensory / instrument tests require actual user-provided samples and gear.
- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.
- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.
- Result: NOT_RUN. - Defect ID: D-011. - Static verdict lead: BROKEN.

## Shipped legacy preservation cases

These ten entries are derived from deleted production paths in tagged history. The current route shell has matching destinations, but that source-level correspondence does not establish behavior or data parity.

### TC-REQ-LEGACY-01 — Home screen

- Linked requirement: shipped legacy feature LEGACY-01; historical source `app/src/main/java/com/coffeecreator/app/ui/screens/HomeScreen.kt`; Git range `v1.0.0..v1.1.0`.
- Title: Verify preservation of Home screen.
- Priority: P0 under the no-feature-loss/no-data-loss audit invariant.
- Preconditions: build the current supported app and use a disposable seeded profile; inspect historical source at its exact tagged revision; never migrate or edit the installed user app.
- Fixtures: documented route behavior from `app/src/main/java/com/coffeecreator/app/ui/screens/HomeScreen.kt`; for recipe corpus, a read-only v1.9.5 source manifest and copied backup fixture with IDs/content hashes.
- Test steps (BDD): Given the historical behavior and a current disposable profile, when the user enters the current `Home` destination and performs each historical primary action, then Current Home destination remains reachable and preserves the primary actions documented by the old screen; verify parity on a disposable profile.
- Dimension checks:
  - Happy path: exercise each old primary action with a valid, provenance-bearing fixture and compare the old/current acceptance outcome.
  - Negative/boundary: exercise each applicable historical boundary and invalid state; record N/A only when the historical action has no input boundary.
  - Invalid input: submit malformed or unsupported values on each applicable form; static routes with no input are N/A.
  - UI states: verify empty, loading, success, offline, error and recovery states for the current destination; mark unavailable states N/A only with source rationale.
  - Offline/no-model: perform the manual workflow in airplane mode with no model installed; the legacy feature must remain usable or clearly report unavailable AI.
  - Persistence/relaunch: save a representative record, relaunch the disposable app, and compare IDs, payloads and links; for recipe history, compare every old catalog identity.
  - Navigation/back/deep-link: enter from documented navigation, use Back, reopen; deep-link is N/A because none is declared.
  - Migration/regression: use copied supported historic records and compare ordered IDs/counts/content hashes before and after; no app data reset.
  - Accessibility: verify TalkBack, focus order, 48dp targets, contrast and 200% font using TC-CROSS-05.
  - Security: inspect local-only behavior, permissions, consent and export ownership where relevant using TC-CROSS-06/12.
  - Performance: measure route entry/scroll or catalog search on a recorded build/device using TC-CROSS-11; do not invent a threshold.
- Expected result: Current Home destination remains reachable and preserves the primary actions documented by the old screen; verify parity on a disposable profile.
- Actual result: NOT_EXECUTED; static history/source observation only.
- Execution environment: no disposable profile or historical database fixture; device test NOT_RUN.
- Automation type: Git-history manifest comparison + JVM/repository tests + Compose/instrumented tests on disposable profile.
- Screenshot/log/test-report evidence: Not generated; see `FEATURE_PRESERVATION_DIFF.md`.
- Result: NOT_RUN. - Defect ID: None assigned pending route parity. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-LEGACY-02 — Recipe builder screen

- Linked requirement: shipped legacy feature LEGACY-02; historical source `app/src/main/java/com/coffeecreator/app/ui/screens/RecipeBuilderScreen.kt`; Git range `v1.0.0..v1.1.0`.
- Title: Verify preservation of Recipe builder screen.
- Priority: P0 under the no-feature-loss/no-data-loss audit invariant.
- Preconditions: build the current supported app and use a disposable seeded profile; inspect historical source at its exact tagged revision; never migrate or edit the installed user app.
- Fixtures: documented route behavior from `app/src/main/java/com/coffeecreator/app/ui/screens/RecipeBuilderScreen.kt`; for recipe corpus, a read-only v1.9.5 source manifest and copied backup fixture with IDs/content hashes.
- Test steps (BDD): Given the historical behavior and a current disposable profile, when the user enters the current `Formulate` destination and performs each historical primary action, then Current Formulate destination preserves manual recipe creation, validation and saved-recipe navigation from the old screen.
- Dimension checks:
  - Happy path: exercise each old primary action with a valid, provenance-bearing fixture and compare the old/current acceptance outcome.
  - Negative/boundary: exercise each applicable historical boundary and invalid state; record N/A only when the historical action has no input boundary.
  - Invalid input: submit malformed or unsupported values on each applicable form; static routes with no input are N/A.
  - UI states: verify empty, loading, success, offline, error and recovery states for the current destination; mark unavailable states N/A only with source rationale.
  - Offline/no-model: perform the manual workflow in airplane mode with no model installed; the legacy feature must remain usable or clearly report unavailable AI.
  - Persistence/relaunch: save a representative record, relaunch the disposable app, and compare IDs, payloads and links; for recipe history, compare every old catalog identity.
  - Navigation/back/deep-link: enter from documented navigation, use Back, reopen; deep-link is N/A because none is declared.
  - Migration/regression: use copied supported historic records and compare ordered IDs/counts/content hashes before and after; no app data reset.
  - Accessibility: verify TalkBack, focus order, 48dp targets, contrast and 200% font using TC-CROSS-05.
  - Security: inspect local-only behavior, permissions, consent and export ownership where relevant using TC-CROSS-06/12.
  - Performance: measure route entry/scroll or catalog search on a recorded build/device using TC-CROSS-11; do not invent a threshold.
- Expected result: Current Formulate destination preserves manual recipe creation, validation and saved-recipe navigation from the old screen.
- Actual result: NOT_EXECUTED; static history/source observation only.
- Execution environment: no disposable profile or historical database fixture; device test NOT_RUN.
- Automation type: Git-history manifest comparison + JVM/repository tests + Compose/instrumented tests on disposable profile.
- Screenshot/log/test-report evidence: Not generated; see `FEATURE_PRESERVATION_DIFF.md`.
- Result: NOT_RUN. - Defect ID: None assigned pending route parity. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-LEGACY-03 — AI chat screen

- Linked requirement: shipped legacy feature LEGACY-03; historical source `app/src/main/java/com/coffeecreator/app/ui/screens/AiChatScreen.kt`; Git range `v1.0.0..v1.1.0`.
- Title: Verify preservation of AI chat screen.
- Priority: P0 under the no-feature-loss/no-data-loss audit invariant.
- Preconditions: build the current supported app and use a disposable seeded profile; inspect historical source at its exact tagged revision; never migrate or edit the installed user app.
- Fixtures: documented route behavior from `app/src/main/java/com/coffeecreator/app/ui/screens/AiChatScreen.kt`; for recipe corpus, a read-only v1.9.5 source manifest and copied backup fixture with IDs/content hashes.
- Test steps (BDD): Given the historical behavior and a current disposable profile, when the user enters the current `AI Chat` destination and performs each historical primary action, then Current AI Chat destination preserves local-model status, conversation and manual recovery behavior from the old screen.
- Dimension checks:
  - Happy path: exercise each old primary action with a valid, provenance-bearing fixture and compare the old/current acceptance outcome.
  - Negative/boundary: exercise each applicable historical boundary and invalid state; record N/A only when the historical action has no input boundary.
  - Invalid input: submit malformed or unsupported values on each applicable form; static routes with no input are N/A.
  - UI states: verify empty, loading, success, offline, error and recovery states for the current destination; mark unavailable states N/A only with source rationale.
  - Offline/no-model: perform the manual workflow in airplane mode with no model installed; the legacy feature must remain usable or clearly report unavailable AI.
  - Persistence/relaunch: save a representative record, relaunch the disposable app, and compare IDs, payloads and links; for recipe history, compare every old catalog identity.
  - Navigation/back/deep-link: enter from documented navigation, use Back, reopen; deep-link is N/A because none is declared.
  - Migration/regression: use copied supported historic records and compare ordered IDs/counts/content hashes before and after; no app data reset.
  - Accessibility: verify TalkBack, focus order, 48dp targets, contrast and 200% font using TC-CROSS-05.
  - Security: inspect local-only behavior, permissions, consent and export ownership where relevant using TC-CROSS-06/12.
  - Performance: measure route entry/scroll or catalog search on a recorded build/device using TC-CROSS-11; do not invent a threshold.
- Expected result: Current AI Chat destination preserves local-model status, conversation and manual recovery behavior from the old screen.
- Actual result: NOT_EXECUTED; static history/source observation only.
- Execution environment: no disposable profile or historical database fixture; device test NOT_RUN.
- Automation type: Git-history manifest comparison + JVM/repository tests + Compose/instrumented tests on disposable profile.
- Screenshot/log/test-report evidence: Not generated; see `FEATURE_PRESERVATION_DIFF.md`.
- Result: NOT_RUN. - Defect ID: None assigned pending route parity. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-LEGACY-04 — Coffee library screen

- Linked requirement: shipped legacy feature LEGACY-04; historical source `app/src/main/java/com/coffeecreator/app/ui/screens/CoffeeLibraryScreen.kt`; Git range `v1.0.0..v1.1.0`.
- Title: Verify preservation of Coffee library screen.
- Priority: P0 under the no-feature-loss/no-data-loss audit invariant.
- Preconditions: build the current supported app and use a disposable seeded profile; inspect historical source at its exact tagged revision; never migrate or edit the installed user app.
- Fixtures: documented route behavior from `app/src/main/java/com/coffeecreator/app/ui/screens/CoffeeLibraryScreen.kt`; for recipe corpus, a read-only v1.9.5 source manifest and copied backup fixture with IDs/content hashes.
- Test steps (BDD): Given the historical behavior and a current disposable profile, when the user enters the current `Library` destination and performs each historical primary action, then Current Library destination preserves catalog browse/search/edit behavior and user-owned entries from the old screen.
- Dimension checks:
  - Happy path: exercise each old primary action with a valid, provenance-bearing fixture and compare the old/current acceptance outcome.
  - Negative/boundary: exercise each applicable historical boundary and invalid state; record N/A only when the historical action has no input boundary.
  - Invalid input: submit malformed or unsupported values on each applicable form; static routes with no input are N/A.
  - UI states: verify empty, loading, success, offline, error and recovery states for the current destination; mark unavailable states N/A only with source rationale.
  - Offline/no-model: perform the manual workflow in airplane mode with no model installed; the legacy feature must remain usable or clearly report unavailable AI.
  - Persistence/relaunch: save a representative record, relaunch the disposable app, and compare IDs, payloads and links; for recipe history, compare every old catalog identity.
  - Navigation/back/deep-link: enter from documented navigation, use Back, reopen; deep-link is N/A because none is declared.
  - Migration/regression: use copied supported historic records and compare ordered IDs/counts/content hashes before and after; no app data reset.
  - Accessibility: verify TalkBack, focus order, 48dp targets, contrast and 200% font using TC-CROSS-05.
  - Security: inspect local-only behavior, permissions, consent and export ownership where relevant using TC-CROSS-06/12.
  - Performance: measure route entry/scroll or catalog search on a recorded build/device using TC-CROSS-11; do not invent a threshold.
- Expected result: Current Library destination preserves catalog browse/search/edit behavior and user-owned entries from the old screen.
- Actual result: NOT_EXECUTED; static history/source observation only.
- Execution environment: no disposable profile or historical database fixture; device test NOT_RUN.
- Automation type: Git-history manifest comparison + JVM/repository tests + Compose/instrumented tests on disposable profile.
- Screenshot/log/test-report evidence: Not generated; see `FEATURE_PRESERVATION_DIFF.md`.
- Result: NOT_RUN. - Defect ID: None assigned pending route parity. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-LEGACY-05 — Brew journal screen

- Linked requirement: shipped legacy feature LEGACY-05; historical source `app/src/main/java/com/coffeecreator/app/ui/screens/BrewJournalScreen.kt`; Git range `v1.0.0..v1.1.0`.
- Title: Verify preservation of Brew journal screen.
- Priority: P0 under the no-feature-loss/no-data-loss audit invariant.
- Preconditions: build the current supported app and use a disposable seeded profile; inspect historical source at its exact tagged revision; never migrate or edit the installed user app.
- Fixtures: documented route behavior from `app/src/main/java/com/coffeecreator/app/ui/screens/BrewJournalScreen.kt`; for recipe corpus, a read-only v1.9.5 source manifest and copied backup fixture with IDs/content hashes.
- Test steps (BDD): Given the historical behavior and a current disposable profile, when the user enters the current `Journal` destination and performs each historical primary action, then Current Journal destination preserves saved brew history, search and record links from the old screen.
- Dimension checks:
  - Happy path: exercise each old primary action with a valid, provenance-bearing fixture and compare the old/current acceptance outcome.
  - Negative/boundary: exercise each applicable historical boundary and invalid state; record N/A only when the historical action has no input boundary.
  - Invalid input: submit malformed or unsupported values on each applicable form; static routes with no input are N/A.
  - UI states: verify empty, loading, success, offline, error and recovery states for the current destination; mark unavailable states N/A only with source rationale.
  - Offline/no-model: perform the manual workflow in airplane mode with no model installed; the legacy feature must remain usable or clearly report unavailable AI.
  - Persistence/relaunch: save a representative record, relaunch the disposable app, and compare IDs, payloads and links; for recipe history, compare every old catalog identity.
  - Navigation/back/deep-link: enter from documented navigation, use Back, reopen; deep-link is N/A because none is declared.
  - Migration/regression: use copied supported historic records and compare ordered IDs/counts/content hashes before and after; no app data reset.
  - Accessibility: verify TalkBack, focus order, 48dp targets, contrast and 200% font using TC-CROSS-05.
  - Security: inspect local-only behavior, permissions, consent and export ownership where relevant using TC-CROSS-06/12.
  - Performance: measure route entry/scroll or catalog search on a recorded build/device using TC-CROSS-11; do not invent a threshold.
- Expected result: Current Journal destination preserves saved brew history, search and record links from the old screen.
- Actual result: NOT_EXECUTED; static history/source observation only.
- Execution environment: no disposable profile or historical database fixture; device test NOT_RUN.
- Automation type: Git-history manifest comparison + JVM/repository tests + Compose/instrumented tests on disposable profile.
- Screenshot/log/test-report evidence: Not generated; see `FEATURE_PRESERVATION_DIFF.md`.
- Result: NOT_RUN. - Defect ID: None assigned pending route parity. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-LEGACY-06 — Knowledge library screen

- Linked requirement: shipped legacy feature LEGACY-06; historical source `app/src/main/java/com/coffeecreator/app/ui/screens/KnowledgeLibraryScreen.kt`; Git range `v1.0.0..v1.1.0`.
- Title: Verify preservation of Knowledge library screen.
- Priority: P0 under the no-feature-loss/no-data-loss audit invariant.
- Preconditions: build the current supported app and use a disposable seeded profile; inspect historical source at its exact tagged revision; never migrate or edit the installed user app.
- Fixtures: documented route behavior from `app/src/main/java/com/coffeecreator/app/ui/screens/KnowledgeLibraryScreen.kt`; for recipe corpus, a read-only v1.9.5 source manifest and copied backup fixture with IDs/content hashes.
- Test steps (BDD): Given the historical behavior and a current disposable profile, when the user enters the current `Knowledge` destination and performs each historical primary action, then Current Knowledge destination preserves local document import, indexing, citation and revoked-grant recovery behavior.
- Dimension checks:
  - Happy path: exercise each old primary action with a valid, provenance-bearing fixture and compare the old/current acceptance outcome.
  - Negative/boundary: exercise each applicable historical boundary and invalid state; record N/A only when the historical action has no input boundary.
  - Invalid input: submit malformed or unsupported values on each applicable form; static routes with no input are N/A.
  - UI states: verify empty, loading, success, offline, error and recovery states for the current destination; mark unavailable states N/A only with source rationale.
  - Offline/no-model: perform the manual workflow in airplane mode with no model installed; the legacy feature must remain usable or clearly report unavailable AI.
  - Persistence/relaunch: save a representative record, relaunch the disposable app, and compare IDs, payloads and links; for recipe history, compare every old catalog identity.
  - Navigation/back/deep-link: enter from documented navigation, use Back, reopen; deep-link is N/A because none is declared.
  - Migration/regression: use copied supported historic records and compare ordered IDs/counts/content hashes before and after; no app data reset.
  - Accessibility: verify TalkBack, focus order, 48dp targets, contrast and 200% font using TC-CROSS-05.
  - Security: inspect local-only behavior, permissions, consent and export ownership where relevant using TC-CROSS-06/12.
  - Performance: measure route entry/scroll or catalog search on a recorded build/device using TC-CROSS-11; do not invent a threshold.
- Expected result: Current Knowledge destination preserves local document import, indexing, citation and revoked-grant recovery behavior.
- Actual result: NOT_EXECUTED; static history/source observation only.
- Execution environment: no disposable profile or historical database fixture; device test NOT_RUN.
- Automation type: Git-history manifest comparison + JVM/repository tests + Compose/instrumented tests on disposable profile.
- Screenshot/log/test-report evidence: Not generated; see `FEATURE_PRESERVATION_DIFF.md`.
- Result: NOT_RUN. - Defect ID: None assigned pending route parity. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-LEGACY-07 — Model manager screen

- Linked requirement: shipped legacy feature LEGACY-07; historical source `app/src/main/java/com/coffeecreator/app/ui/screens/ModelManagerScreen.kt`; Git range `v1.0.0..v1.1.0`.
- Title: Verify preservation of Model manager screen.
- Priority: P0 under the no-feature-loss/no-data-loss audit invariant.
- Preconditions: build the current supported app and use a disposable seeded profile; inspect historical source at its exact tagged revision; never migrate or edit the installed user app.
- Fixtures: documented route behavior from `app/src/main/java/com/coffeecreator/app/ui/screens/ModelManagerScreen.kt`; for recipe corpus, a read-only v1.9.5 source manifest and copied backup fixture with IDs/content hashes.
- Test steps (BDD): Given the historical behavior and a current disposable profile, when the user enters the current `Models` destination and performs each historical primary action, then Current Models destination preserves user-requested asset selection, verification, progress and recovery behavior.
- Dimension checks:
  - Happy path: exercise each old primary action with a valid, provenance-bearing fixture and compare the old/current acceptance outcome.
  - Negative/boundary: exercise each applicable historical boundary and invalid state; record N/A only when the historical action has no input boundary.
  - Invalid input: submit malformed or unsupported values on each applicable form; static routes with no input are N/A.
  - UI states: verify empty, loading, success, offline, error and recovery states for the current destination; mark unavailable states N/A only with source rationale.
  - Offline/no-model: perform the manual workflow in airplane mode with no model installed; the legacy feature must remain usable or clearly report unavailable AI.
  - Persistence/relaunch: save a representative record, relaunch the disposable app, and compare IDs, payloads and links; for recipe history, compare every old catalog identity.
  - Navigation/back/deep-link: enter from documented navigation, use Back, reopen; deep-link is N/A because none is declared.
  - Migration/regression: use copied supported historic records and compare ordered IDs/counts/content hashes before and after; no app data reset.
  - Accessibility: verify TalkBack, focus order, 48dp targets, contrast and 200% font using TC-CROSS-05.
  - Security: inspect local-only behavior, permissions, consent and export ownership where relevant using TC-CROSS-06/12.
  - Performance: measure route entry/scroll or catalog search on a recorded build/device using TC-CROSS-11; do not invent a threshold.
- Expected result: Current Models destination preserves user-requested asset selection, verification, progress and recovery behavior.
- Actual result: NOT_EXECUTED; static history/source observation only.
- Execution environment: no disposable profile or historical database fixture; device test NOT_RUN.
- Automation type: Git-history manifest comparison + JVM/repository tests + Compose/instrumented tests on disposable profile.
- Screenshot/log/test-report evidence: Not generated; see `FEATURE_PRESERVATION_DIFF.md`.
- Result: NOT_RUN. - Defect ID: None assigned pending route parity. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-LEGACY-08 — Sensory screen

- Linked requirement: shipped legacy feature LEGACY-08; historical source `app/src/main/java/com/coffeecreator/app/ui/screens/SensoryScreen.kt`; Git range `v1.0.0..v1.1.0`.
- Title: Verify preservation of Sensory screen.
- Priority: P0 under the no-feature-loss/no-data-loss audit invariant.
- Preconditions: build the current supported app and use a disposable seeded profile; inspect historical source at its exact tagged revision; never migrate or edit the installed user app.
- Fixtures: documented route behavior from `app/src/main/java/com/coffeecreator/app/ui/screens/SensoryScreen.kt`; for recipe corpus, a read-only v1.9.5 source manifest and copied backup fixture with IDs/content hashes.
- Test steps (BDD): Given the historical behavior and a current disposable profile, when the user enters the current `Sensory` destination and performs each historical primary action, then Current Sensory destination preserves legacy wheel, observation and cupping entry points without changing saved descriptor IDs.
- Dimension checks:
  - Happy path: exercise each old primary action with a valid, provenance-bearing fixture and compare the old/current acceptance outcome.
  - Negative/boundary: exercise each applicable historical boundary and invalid state; record N/A only when the historical action has no input boundary.
  - Invalid input: submit malformed or unsupported values on each applicable form; static routes with no input are N/A.
  - UI states: verify empty, loading, success, offline, error and recovery states for the current destination; mark unavailable states N/A only with source rationale.
  - Offline/no-model: perform the manual workflow in airplane mode with no model installed; the legacy feature must remain usable or clearly report unavailable AI.
  - Persistence/relaunch: save a representative record, relaunch the disposable app, and compare IDs, payloads and links; for recipe history, compare every old catalog identity.
  - Navigation/back/deep-link: enter from documented navigation, use Back, reopen; deep-link is N/A because none is declared.
  - Migration/regression: use copied supported historic records and compare ordered IDs/counts/content hashes before and after; no app data reset.
  - Accessibility: verify TalkBack, focus order, 48dp targets, contrast and 200% font using TC-CROSS-05.
  - Security: inspect local-only behavior, permissions, consent and export ownership where relevant using TC-CROSS-06/12.
  - Performance: measure route entry/scroll or catalog search on a recorded build/device using TC-CROSS-11; do not invent a threshold.
- Expected result: Current Sensory destination preserves legacy wheel, observation and cupping entry points without changing saved descriptor IDs.
- Actual result: NOT_EXECUTED; static history/source observation only.
- Execution environment: no disposable profile or historical database fixture; device test NOT_RUN.
- Automation type: Git-history manifest comparison + JVM/repository tests + Compose/instrumented tests on disposable profile.
- Screenshot/log/test-report evidence: Not generated; see `FEATURE_PRESERVATION_DIFF.md`.
- Result: NOT_RUN. - Defect ID: None assigned pending route parity. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-LEGACY-09 — Settings screen

- Linked requirement: shipped legacy feature LEGACY-09; historical source `app/src/main/java/com/coffeecreator/app/ui/screens/SettingsScreen.kt`; Git range `v1.0.0..v1.1.0`.
- Title: Verify preservation of Settings screen.
- Priority: P0 under the no-feature-loss/no-data-loss audit invariant.
- Preconditions: build the current supported app and use a disposable seeded profile; inspect historical source at its exact tagged revision; never migrate or edit the installed user app.
- Fixtures: documented route behavior from `app/src/main/java/com/coffeecreator/app/ui/screens/SettingsScreen.kt`; for recipe corpus, a read-only v1.9.5 source manifest and copied backup fixture with IDs/content hashes.
- Test steps (BDD): Given the historical behavior and a current disposable profile, when the user enters the current `Settings` destination and performs each historical primary action, then Current Settings destination preserves privacy, accessibility, storage, export and restore controls.
- Dimension checks:
  - Happy path: exercise each old primary action with a valid, provenance-bearing fixture and compare the old/current acceptance outcome.
  - Negative/boundary: exercise each applicable historical boundary and invalid state; record N/A only when the historical action has no input boundary.
  - Invalid input: submit malformed or unsupported values on each applicable form; static routes with no input are N/A.
  - UI states: verify empty, loading, success, offline, error and recovery states for the current destination; mark unavailable states N/A only with source rationale.
  - Offline/no-model: perform the manual workflow in airplane mode with no model installed; the legacy feature must remain usable or clearly report unavailable AI.
  - Persistence/relaunch: save a representative record, relaunch the disposable app, and compare IDs, payloads and links; for recipe history, compare every old catalog identity.
  - Navigation/back/deep-link: enter from documented navigation, use Back, reopen; deep-link is N/A because none is declared.
  - Migration/regression: use copied supported historic records and compare ordered IDs/counts/content hashes before and after; no app data reset.
  - Accessibility: verify TalkBack, focus order, 48dp targets, contrast and 200% font using TC-CROSS-05.
  - Security: inspect local-only behavior, permissions, consent and export ownership where relevant using TC-CROSS-06/12.
  - Performance: measure route entry/scroll or catalog search on a recorded build/device using TC-CROSS-11; do not invent a threshold.
- Expected result: Current Settings destination preserves privacy, accessibility, storage, export and restore controls.
- Actual result: NOT_EXECUTED; static history/source observation only.
- Execution environment: no disposable profile or historical database fixture; device test NOT_RUN.
- Automation type: Git-history manifest comparison + JVM/repository tests + Compose/instrumented tests on disposable profile.
- Screenshot/log/test-report evidence: Not generated; see `FEATURE_PRESERVATION_DIFF.md`.
- Result: NOT_RUN. - Defect ID: None assigned pending route parity. - Static verdict lead: IMPLEMENTED_UNVERIFIED.

### TC-REQ-LEGACY-10 — Curated recipe corpus and stable records

- Linked requirement: shipped legacy feature LEGACY-10; historical source `app/src/main/java/com/coffeecreator/app/core/data/CuratedCoffeeCatalog.kt; app/src/main/java/com/coffeecreator/app/core/data/SeedData.kt; app/src/main/java/com/coffeecreator/app/core/recipes/champion/WorldChampionRecipeCatalog.kt`; Git range `v1.9.5..v1.9.6`.
- Title: Verify preservation of Curated recipe corpus and stable records.
- Priority: P0 under the no-feature-loss/no-data-loss audit invariant.
- Preconditions: build the current supported app and use a disposable seeded profile; inspect historical source at its exact tagged revision; never migrate or edit the installed user app.
- Fixtures: documented route behavior from `app/src/main/java/com/coffeecreator/app/core/data/CuratedCoffeeCatalog.kt; app/src/main/java/com/coffeecreator/app/core/data/SeedData.kt; app/src/main/java/com/coffeecreator/app/core/recipes/champion/WorldChampionRecipeCatalog.kt`; for recipe corpus, a read-only v1.9.5 source manifest and copied backup fixture with IDs/content hashes.
- Test steps (BDD): Given the historical behavior and a current disposable profile, when the user enters the current `Library / Formulate` destination and performs each historical primary action, then Reconcile every v1.9.5 recipe ID, step and source with current catalogs and historical imports; retain verified recipe content, mark unsupported measurements unknown, and do not inject synthetic test fixtures into production.
- Dimension checks:
  - Happy path: exercise each old primary action with a valid, provenance-bearing fixture and compare the old/current acceptance outcome.
  - Negative/boundary: exercise each applicable historical boundary and invalid state; record N/A only when the historical action has no input boundary.
  - Invalid input: submit malformed or unsupported values on each applicable form; static routes with no input are N/A.
  - UI states: verify empty, loading, success, offline, error and recovery states for the current destination; mark unavailable states N/A only with source rationale.
  - Offline/no-model: perform the manual workflow in airplane mode with no model installed; the legacy feature must remain usable or clearly report unavailable AI.
  - Persistence/relaunch: save a representative record, relaunch the disposable app, and compare IDs, payloads and links; for recipe history, compare every old catalog identity.
  - Navigation/back/deep-link: enter from documented navigation, use Back, reopen; deep-link is N/A because none is declared.
  - Migration/regression: use copied supported historic records and compare ordered IDs/counts/content hashes before and after; no app data reset.
  - Accessibility: verify TalkBack, focus order, 48dp targets, contrast and 200% font using TC-CROSS-05.
  - Security: inspect local-only behavior, permissions, consent and export ownership where relevant using TC-CROSS-06/12.
  - Performance: measure route entry/scroll or catalog search on a recorded build/device using TC-CROSS-11; do not invent a threshold.
- Expected result: Reconcile every v1.9.5 recipe ID, step and source with current catalogs and historical imports; retain verified recipe content, mark unsupported measurements unknown, and do not inject synthetic test fixtures into production.
- Actual result: NOT_EXECUTED; static history/source observation only.
- Execution environment: no disposable profile or historical database fixture; device test NOT_RUN.
- Automation type: Git-history manifest comparison + JVM/repository tests + Compose/instrumented tests on disposable profile.
- Screenshot/log/test-report evidence: Not generated; see `FEATURE_PRESERVATION_DIFF.md`.
- Result: NOT_RUN. - Defect ID: D-013. - Static verdict lead: SPEC_CONFLICT.

## Existing named test procedures from sprint specs

### TC-301 — Import source tables

- Linked requirement: Sprint 03; see coffee-creator-blueprint/SPRINT_03_MANUAL_BREW_GRINDER_INTELLIGENCE.md:217.
- Title: Import source tables.
- Priority: P0/P1 per linked story; verify exact priority in the requirement matrix.
- Preconditions: disposable seeded profile; use only fixture/source files with provenance.
- Fixtures: values specified in the sprint case; hardware and legal resources only when supplied/authorized.
- Test steps (BDD): Given the specified preconditions, when the operator performs 'Import source tables', then 44 grinder and 26 dripper rows present, raw values retained.
- Expected result: 44 grinder and 26 dripper rows present, raw values retained.
- Actual result: Not executed during this audit.
- Execution environment: Not run.
- Automation type: Unit, repository, Compose, instrumented, or manual as appropriate to the case.
- Screenshot/log/test-report evidence: Not generated.
- Result: NOT_RUN. - Defect ID: See defect register.

### TC-302 — Filter hand-only

- Linked requirement: Sprint 03; see coffee-creator-blueprint/SPRINT_03_MANUAL_BREW_GRINDER_INTELLIGENCE.md:218.
- Title: Filter hand-only.
- Priority: P0/P1 per linked story; verify exact priority in the requirement matrix.
- Preconditions: disposable seeded profile; use only fixture/source files with provenance.
- Fixtures: values specified in the sprint case; hardware and legal resources only when supplied/authorized.
- Test steps (BDD): Given the specified preconditions, when the operator performs 'Filter hand-only', then Electric grinders excluded from hand-only filter, not deleted.
- Expected result: Electric grinders excluded from hand-only filter, not deleted.
- Actual result: Not executed during this audit.
- Execution environment: Not run.
- Automation type: Unit, repository, Compose, instrumented, or manual as appropriate to the case.
- Screenshot/log/test-report evidence: Not generated.
- Result: NOT_RUN. - Defect ID: See defect register.

### TC-303 — Unknown dripper flow

- Linked requirement: Sprint 03; see coffee-creator-blueprint/SPRINT_03_MANUAL_BREW_GRINDER_INTELLIGENCE.md:219.
- Title: Unknown dripper flow.
- Priority: P0/P1 per linked story; verify exact priority in the requirement matrix.
- Preconditions: disposable seeded profile; use only fixture/source files with provenance.
- Fixtures: values specified in the sprint case; hardware and legal resources only when supplied/authorized.
- Test steps (BDD): Given the specified preconditions, when the operator performs 'Unknown dripper flow', then No invented recommendation; UI displays Unknown.
- Expected result: No invented recommendation; UI displays Unknown.
- Actual result: Not executed during this audit.
- Execution environment: Not run.
- Automation type: Unit, repository, Compose, instrumented, or manual as appropriate to the case.
- Screenshot/log/test-report evidence: Not generated.
- Result: NOT_RUN. - Defect ID: See defect register.

### TC-304 — Model-specific click input

- Linked requirement: Sprint 03; see coffee-creator-blueprint/SPRINT_03_MANUAL_BREW_GRINDER_INTELLIGENCE.md:220.
- Title: Model-specific click input.
- Priority: P0/P1 per linked story; verify exact priority in the requirement matrix.
- Preconditions: disposable seeded profile; use only fixture/source files with provenance.
- Fixtures: values specified in the sprint case; hardware and legal resources only when supplied/authorized.
- Test steps (BDD): Given the specified preconditions, when the operator performs 'Model-specific click input', then C40 clicks never equated to J-Ultra clicks.
- Expected result: C40 clicks never equated to J-Ultra clicks.
- Actual result: Not executed during this audit.
- Execution environment: Not run.
- Automation type: Unit, repository, Compose, instrumented, or manual as appropriate to the case.
- Screenshot/log/test-report evidence: Not generated.
- Result: NOT_RUN. - Defect ID: See defect register.

### TC-305 — Zero calibration change

- Linked requirement: Sprint 03; see coffee-creator-blueprint/SPRINT_03_MANUAL_BREW_GRINDER_INTELLIGENCE.md:221.
- Title: Zero calibration change.
- Priority: P0/P1 per linked story; verify exact priority in the requirement matrix.
- Preconditions: disposable seeded profile; use only fixture/source files with provenance.
- Fixtures: values specified in the sprint case; hardware and legal resources only when supplied/authorized.
- Test steps (BDD): Given the specified preconditions, when the operator performs 'Zero calibration change', then Old brews retain historical setting snapshot.
- Expected result: Old brews retain historical setting snapshot.
- Actual result: Not executed during this audit.
- Execution environment: Not run.
- Automation type: Unit, repository, Compose, instrumented, or manual as appropriate to the case.
- Screenshot/log/test-report evidence: Not generated.
- Result: NOT_RUN. - Defect ID: See defect register.

### TC-306 — Conflicting source ranges

- Linked requirement: Sprint 03; see coffee-creator-blueprint/SPRINT_03_MANUAL_BREW_GRINDER_INTELLIGENCE.md:222.
- Title: Conflicting source ranges.
- Priority: P0/P1 per linked story; verify exact priority in the requirement matrix.
- Preconditions: disposable seeded profile; use only fixture/source files with provenance.
- Fixtures: values specified in the sprint case; hardware and legal resources only when supplied/authorized.
- Test steps (BDD): Given the specified preconditions, when the operator performs 'Conflicting source ranges', then Both values and provenance retained; no silent overwrite.
- Expected result: Both values and provenance retained; no silent overwrite.
- Actual result: Not executed during this audit.
- Execution environment: Not run.
- Automation type: Unit, repository, Compose, instrumented, or manual as appropriate to the case.
- Screenshot/log/test-report evidence: Not generated.
- Result: NOT_RUN. - Defect ID: See defect register.

### TC-307 — Unsupported espresso grinder

- Linked requirement: Sprint 03; see coffee-creator-blueprint/SPRINT_03_MANUAL_BREW_GRINDER_INTELLIGENCE.md:223.
- Title: Unsupported espresso grinder.
- Priority: P0/P1 per linked story; verify exact priority in the requirement matrix.
- Preconditions: disposable seeded profile; use only fixture/source files with provenance.
- Fixtures: values specified in the sprint case; hardware and legal resources only when supplied/authorized.
- Test steps (BDD): Given the specified preconditions, when the operator performs 'Unsupported espresso grinder', then Compatibility warning, no invented espresso setting.
- Expected result: Compatibility warning, no invented espresso setting.
- Actual result: Not executed during this audit.
- Execution environment: Not run.
- Automation type: Unit, repository, Compose, instrumented, or manual as appropriate to the case.
- Screenshot/log/test-report evidence: Not generated.
- Result: NOT_RUN. - Defect ID: See defect register.

### TC-308 — Brew 15g/250g

- Linked requirement: Sprint 03; see coffee-creator-blueprint/SPRINT_03_MANUAL_BREW_GRINDER_INTELLIGENCE.md:224.
- Title: Brew 15g/250g.
- Priority: P0/P1 per linked story; verify exact priority in the requirement matrix.
- Preconditions: disposable seeded profile; use only fixture/source files with provenance.
- Fixtures: values specified in the sprint case; hardware and legal resources only when supplied/authorized.
- Test steps (BDD): Given the specified preconditions, when the operator performs 'Brew 15g/250g', then Ratio = 1:16.6667, displayed with rounding policy.
- Expected result: Ratio = 1:16.6667, displayed with rounding policy.
- Actual result: Not executed during this audit.
- Execution environment: Not run.
- Automation type: Unit, repository, Compose, instrumented, or manual as appropriate to the case.
- Screenshot/log/test-report evidence: Not generated.
- Result: NOT_RUN. - Defect ID: See defect register.

### TC-309 — Brew 20g/300g

- Linked requirement: Sprint 03; see coffee-creator-blueprint/SPRINT_03_MANUAL_BREW_GRINDER_INTELLIGENCE.md:225.
- Title: Brew 20g/300g.
- Priority: P0/P1 per linked story; verify exact priority in the requirement matrix.
- Preconditions: disposable seeded profile; use only fixture/source files with provenance.
- Fixtures: values specified in the sprint case; hardware and legal resources only when supplied/authorized.
- Test steps (BDD): Given the specified preconditions, when the operator performs 'Brew 20g/300g', then Ratio = 1:15, staged pours sum to 300g.
- Expected result: Ratio = 1:15, staged pours sum to 300g.
- Actual result: Not executed during this audit.
- Execution environment: Not run.
- Automation type: Unit, repository, Compose, instrumented, or manual as appropriate to the case.
- Screenshot/log/test-report evidence: Not generated.
- Result: NOT_RUN. - Defect ID: See defect register.

### TC-310 — Invalid 0g dose

- Linked requirement: Sprint 03; see coffee-creator-blueprint/SPRINT_03_MANUAL_BREW_GRINDER_INTELLIGENCE.md:226.
- Title: Invalid 0g dose.
- Priority: P0/P1 per linked story; verify exact priority in the requirement matrix.
- Preconditions: disposable seeded profile; use only fixture/source files with provenance.
- Fixtures: values specified in the sprint case; hardware and legal resources only when supplied/authorized.
- Test steps (BDD): Given the specified preconditions, when the operator performs 'Invalid 0g dose', then Validation prevents division by zero.
- Expected result: Validation prevents division by zero.
- Actual result: Not executed during this audit.
- Execution environment: Not run.
- Automation type: Unit, repository, Compose, instrumented, or manual as appropriate to the case.
- Screenshot/log/test-report evidence: Not generated.
- Result: NOT_RUN. - Defect ID: See defect register.

### TC-311 — Volume vs mass

- Linked requirement: Sprint 03; see coffee-creator-blueprint/SPRINT_03_MANUAL_BREW_GRINDER_INTELLIGENCE.md:227.
- Title: Volume vs mass.
- Priority: P0/P1 per linked story; verify exact priority in the requirement matrix.
- Preconditions: disposable seeded profile; use only fixture/source files with provenance.
- Fixtures: values specified in the sprint case; hardware and legal resources only when supplied/authorized.
- Test steps (BDD): Given the specified preconditions, when the operator performs 'Volume vs mass', then No automatic mL→g conversion without density.
- Expected result: No automatic mL→g conversion without density.
- Actual result: Not executed during this audit.
- Execution environment: Not run.
- Automation type: Unit, repository, Compose, instrumented, or manual as appropriate to the case.
- Screenshot/log/test-report evidence: Not generated.
- Result: NOT_RUN. - Defect ID: See defect register.

### TC-312 — Timer app background

- Linked requirement: Sprint 03; see coffee-creator-blueprint/SPRINT_03_MANUAL_BREW_GRINDER_INTELLIGENCE.md:228.
- Title: Timer app background.
- Priority: P0/P1 per linked story; verify exact priority in the requirement matrix.
- Preconditions: disposable seeded profile; use only fixture/source files with provenance.
- Fixtures: values specified in the sprint case; hardware and legal resources only when supplied/authorized.
- Test steps (BDD): Given the specified preconditions, when the operator performs 'Timer app background', then Elapsed time reconstructed from monotonic clock; no lost brew.
- Expected result: Elapsed time reconstructed from monotonic clock; no lost brew.
- Actual result: Not executed during this audit.
- Execution environment: Not run.
- Automation type: Unit, repository, Compose, instrumented, or manual as appropriate to the case.
- Screenshot/log/test-report evidence: Not generated.
- Result: NOT_RUN. - Defect ID: See defect register.

### TC-313 — Rotate during brew

- Linked requirement: Sprint 03; see coffee-creator-blueprint/SPRINT_03_MANUAL_BREW_GRINDER_INTELLIGENCE.md:229.
- Title: Rotate during brew.
- Priority: P0/P1 per linked story; verify exact priority in the requirement matrix.
- Preconditions: disposable seeded profile; use only fixture/source files with provenance.
- Fixtures: values specified in the sprint case; hardware and legal resources only when supplied/authorized.
- Test steps (BDD): Given the specified preconditions, when the operator performs 'Rotate during brew', then Stage/targets/state preserved.
- Expected result: Stage/targets/state preserved.
- Actual result: Not executed during this audit.
- Execution environment: Not run.
- Automation type: Unit, repository, Compose, instrumented, or manual as appropriate to the case.
- Screenshot/log/test-report evidence: Not generated.
- Result: NOT_RUN. - Defect ID: See defect register.

### TC-314 — Airplane mode

- Linked requirement: Sprint 03; see coffee-creator-blueprint/SPRINT_03_MANUAL_BREW_GRINDER_INTELLIGENCE.md:230.
- Title: Airplane mode.
- Priority: P0/P1 per linked story; verify exact priority in the requirement matrix.
- Preconditions: disposable seeded profile; use only fixture/source files with provenance.
- Fixtures: values specified in the sprint case; hardware and legal resources only when supplied/authorized.
- Test steps (BDD): Given the specified preconditions, when the operator performs 'Airplane mode', then Catalog, timer, saving, compare all work.
- Expected result: Catalog, timer, saving, compare all work.
- Actual result: Not executed during this audit.
- Execution environment: Not run.
- Automation type: Unit, repository, Compose, instrumented, or manual as appropriate to the case.
- Screenshot/log/test-report evidence: Not generated.
- Result: NOT_RUN. - Defect ID: See defect register.

### TC-315 — No AI model

- Linked requirement: Sprint 03; see coffee-creator-blueprint/SPRINT_03_MANUAL_BREW_GRINDER_INTELLIGENCE.md:231.
- Title: No AI model.
- Priority: P0/P1 per linked story; verify exact priority in the requirement matrix.
- Preconditions: disposable seeded profile; use only fixture/source files with provenance.
- Fixtures: values specified in the sprint case; hardware and legal resources only when supplied/authorized.
- Test steps (BDD): Given the specified preconditions, when the operator performs 'No AI model', then Manual brew remains fully functional.
- Expected result: Manual brew remains fully functional.
- Actual result: Not executed during this audit.
- Execution environment: Not run.
- Automation type: Unit, repository, Compose, instrumented, or manual as appropriate to the case.
- Screenshot/log/test-report evidence: Not generated.
- Result: NOT_RUN. - Defect ID: See defect register.

### TC-316 — Source recipe copy

- Linked requirement: Sprint 03; see coffee-creator-blueprint/SPRINT_03_MANUAL_BREW_GRINDER_INTELLIGENCE.md:232.
- Title: Source recipe copy.
- Priority: P0/P1 per linked story; verify exact priority in the requirement matrix.
- Preconditions: disposable seeded profile; use only fixture/source files with provenance.
- Fixtures: values specified in the sprint case; hardware and legal resources only when supplied/authorized.
- Test steps (BDD): Given the specified preconditions, when the operator performs 'Source recipe copy', then Edit creates user revision, original template immutable.
- Expected result: Edit creates user revision, original template immutable.
- Actual result: Not executed during this audit.
- Execution environment: Not run.
- Automation type: Unit, repository, Compose, instrumented, or manual as appropriate to the case.
- Screenshot/log/test-report evidence: Not generated.
- Result: NOT_RUN. - Defect ID: See defect register.

### TC-317 — Sensory feedback

- Linked requirement: Sprint 03; see coffee-creator-blueprint/SPRINT_03_MANUAL_BREW_GRINDER_INTELLIGENCE.md:233.
- Title: Sensory feedback.
- Priority: P0/P1 per linked story; verify exact priority in the requirement matrix.
- Preconditions: disposable seeded profile; use only fixture/source files with provenance.
- Fixtures: values specified in the sprint case; hardware and legal resources only when supplied/authorized.
- Test steps (BDD): Given the specified preconditions, when the operator performs 'Sensory feedback', then Observed descriptor never represented as predicted.
- Expected result: Observed descriptor never represented as predicted.
- Actual result: Not executed during this audit.
- Execution environment: Not run.
- Automation type: Unit, repository, Compose, instrumented, or manual as appropriate to the case.
- Screenshot/log/test-report evidence: Not generated.
- Result: NOT_RUN. - Defect ID: See defect register.

### TC-318 — Upgrade existing DB

- Linked requirement: Sprint 03; see coffee-creator-blueprint/SPRINT_03_MANUAL_BREW_GRINDER_INTELLIGENCE.md:234.
- Title: Upgrade existing DB.
- Priority: P0/P1 per linked story; verify exact priority in the requirement matrix.
- Preconditions: disposable seeded profile; use only fixture/source files with provenance.
- Fixtures: values specified in the sprint case; hardware and legal resources only when supplied/authorized.
- Test steps (BDD): Given the specified preconditions, when the operator performs 'Upgrade existing DB', then Old recipes, RAG documents, models, journal and wheel remain accessible.
- Expected result: Old recipes, RAG documents, models, journal and wheel remain accessible.
- Actual result: Not executed during this audit.
- Execution environment: Not run.
- Automation type: Unit, repository, Compose, instrumented, or manual as appropriate to the case.
- Screenshot/log/test-report evidence: Not generated.
- Result: NOT_RUN. - Defect ID: See defect register.

### TC-319 — Duplicate seed import

- Linked requirement: Sprint 03; see coffee-creator-blueprint/SPRINT_03_MANUAL_BREW_GRINDER_INTELLIGENCE.md:235.
- Title: Duplicate seed import.
- Priority: P0/P1 per linked story; verify exact priority in the requirement matrix.
- Preconditions: disposable seeded profile; use only fixture/source files with provenance.
- Fixtures: values specified in the sprint case; hardware and legal resources only when supplied/authorized.
- Test steps (BDD): Given the specified preconditions, when the operator performs 'Duplicate seed import', then Idempotent import; user overrides survive.
- Expected result: Idempotent import; user overrides survive.
- Actual result: Not executed during this audit.
- Execution environment: Not run.
- Automation type: Unit, repository, Compose, instrumented, or manual as appropriate to the case.
- Screenshot/log/test-report evidence: Not generated.
- Result: NOT_RUN. - Defect ID: See defect register.

### TC-320 — TalkBack & large text

- Linked requirement: Sprint 03; see coffee-creator-blueprint/SPRINT_03_MANUAL_BREW_GRINDER_INTELLIGENCE.md:236.
- Title: TalkBack & large text.
- Priority: P0/P1 per linked story; verify exact priority in the requirement matrix.
- Preconditions: disposable seeded profile; use only fixture/source files with provenance.
- Fixtures: values specified in the sprint case; hardware and legal resources only when supplied/authorized.
- Test steps (BDD): Given the specified preconditions, when the operator performs 'TalkBack & large text', then Primary brew journey operable without clipping.
- Expected result: Primary brew journey operable without clipping.
- Actual result: Not executed during this audit.
- Execution environment: Not run.
- Automation type: Unit, repository, Compose, instrumented, or manual as appropriate to the case.
- Screenshot/log/test-report evidence: Not generated.
- Result: NOT_RUN. - Defect ID: See defect register.

### TC-321 — Prompt injection in imported document

- Linked requirement: Sprint 03; see coffee-creator-blueprint/SPRINT_03_MANUAL_BREW_GRINDER_INTELLIGENCE.md:237.
- Title: Prompt injection in imported document.
- Priority: P0/P1 per linked story; verify exact priority in the requirement matrix.
- Preconditions: disposable seeded profile; use only fixture/source files with provenance.
- Fixtures: values specified in the sprint case; hardware and legal resources only when supplied/authorized.
- Test steps (BDD): Given the specified preconditions, when the operator performs 'Prompt injection in imported document', then No execution of document instructions or remote request.
- Expected result: No execution of document instructions or remote request.
- Actual result: Not executed during this audit.
- Execution environment: Not run.
- Automation type: Unit, repository, Compose, instrumented, or manual as appropriate to the case.
- Screenshot/log/test-report evidence: Not generated.
- Result: NOT_RUN. - Defect ID: See defect register.

### TC-322 — Reinstall recovery

- Linked requirement: Sprint 03; see coffee-creator-blueprint/SPRINT_03_MANUAL_BREW_GRINDER_INTELLIGENCE.md:238.
- Title: Reinstall recovery.
- Priority: P0/P1 per linked story; verify exact priority in the requirement matrix.
- Preconditions: disposable seeded profile; use only fixture/source files with provenance.
- Fixtures: values specified in the sprint case; hardware and legal resources only when supplied/authorized.
- Test steps (BDD): Given the specified preconditions, when the operator performs 'Reinstall recovery', then Explicit backup/restore and SAF regrant, no false survival claims.
- Expected result: Explicit backup/restore and SAF regrant, no false survival claims.
- Actual result: Not executed during this audit.
- Execution environment: Not run.
- Automation type: Unit, repository, Compose, instrumented, or manual as appropriate to the case.
- Screenshot/log/test-report evidence: Not generated.
- Result: NOT_RUN. - Defect ID: See defect register.

### TC-323 — Video reference

- Linked requirement: Sprint 03; see coffee-creator-blueprint/SPRINT_03_MANUAL_BREW_GRINDER_INTELLIGENCE.md:239.
- Title: Video reference.
- Priority: P0/P1 per linked story; verify exact priority in the requirement matrix.
- Preconditions: disposable seeded profile; use only fixture/source files with provenance.
- Fixtures: values specified in the sprint case; hardware and legal resources only when supplied/authorized.
- Test steps (BDD): Given the specified preconditions, when the operator performs 'Video reference', then No video feature marked verified without timestamp and evidence.
- Expected result: No video feature marked verified without timestamp and evidence.
- Actual result: Not executed during this audit.
- Execution environment: Not run.
- Automation type: Unit, repository, Compose, instrumented, or manual as appropriate to the case.
- Screenshot/log/test-report evidence: Not generated.
- Result: NOT_RUN. - Defect ID: See defect register.

### TC-324 — No device available

- Linked requirement: Sprint 03; see coffee-creator-blueprint/SPRINT_03_MANUAL_BREW_GRINDER_INTELLIGENCE.md:240.
- Title: No device available.
- Priority: P0/P1 per linked story; verify exact priority in the requirement matrix.
- Preconditions: disposable seeded profile; use only fixture/source files with provenance.
- Fixtures: values specified in the sprint case; hardware and legal resources only when supplied/authorized.
- Test steps (BDD): Given the specified preconditions, when the operator performs 'No device available', then Instrumented/device tests marked NOT_RUN, never PASS.
- Expected result: Instrumented/device tests marked NOT_RUN, never PASS.
- Actual result: Not executed during this audit.
- Execution environment: Not run.
- Automation type: Unit, repository, Compose, instrumented, or manual as appropriate to the case.
- Screenshot/log/test-report evidence: Not generated.
- Result: NOT_RUN. - Defect ID: See defect register.

### QA-001 — Upgrade DB from Sprint 01/02/03 with existing recipes, sensory and equipment

- Linked requirement: Sprint 04; see coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:259.
- Title: Upgrade DB from Sprint 01/02/03 with existing recipes, sensory and equipment.
- Priority: P0/P1 per linked story; verify exact priority in the requirement matrix.
- Preconditions: disposable seeded profile; use only fixture/source files with provenance.
- Fixtures: values specified in the sprint case; hardware and legal resources only when supplied/authorized.
- Test steps (BDD): Given the specified preconditions, when the operator performs 'Upgrade DB from Sprint 01/02/03 with existing recipes, sensory and equipment', then Zero data loss, stable IDs.
- Expected result: Zero data loss, stable IDs.
- Actual result: Not executed during this audit.
- Execution environment: Not run.
- Automation type: Unit, repository, Compose, instrumented, or manual as appropriate to the case.
- Screenshot/log/test-report evidence: Not generated.
- Result: NOT_RUN. - Defect ID: See defect register.

### QA-002 — Airplane mode, new CVA session, enter data, export

- Linked requirement: Sprint 04; see coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:260.
- Title: Airplane mode, new CVA session, enter data, export.
- Priority: P0/P1 per linked story; verify exact priority in the requirement matrix.
- Preconditions: disposable seeded profile; use only fixture/source files with provenance.
- Fixtures: values specified in the sprint case; hardware and legal resources only when supplied/authorized.
- Test steps (BDD): Given the specified preconditions, when the operator performs 'Airplane mode, new CVA session, enter data, export', then Fully functional offline.
- Expected result: Fully functional offline.
- Actual result: Not executed during this audit.
- Execution environment: Not run.
- Automation type: Unit, repository, Compose, instrumented, or manual as appropriate to the case.
- Screenshot/log/test-report evidence: Not generated.
- Result: NOT_RUN. - Defect ID: See defect register.

### QA-003 — App killed mid-form and relaunched

- Linked requirement: Sprint 04; see coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:261.
- Title: App killed mid-form and relaunched.
- Priority: P0/P1 per linked story; verify exact priority in the requirement matrix.
- Preconditions: disposable seeded profile; use only fixture/source files with provenance.
- Fixtures: values specified in the sprint case; hardware and legal resources only when supplied/authorized.
- Test steps (BDD): Given the specified preconditions, when the operator performs 'App killed mid-form and relaunched', then Exact draft restored.
- Expected result: Exact draft restored.
- Actual result: Not executed during this audit.
- Execution environment: Not run.
- Automation type: Unit, repository, Compose, instrumented, or manual as appropriate to the case.
- Screenshot/log/test-report evidence: Not generated.
- Result: NOT_RUN. - Defect ID: See defect register.

### QA-004 — Select modern CVA protocol

- Linked requirement: Sprint 04; see coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:262.
- Title: Select modern CVA protocol.
- Priority: P0/P1 per linked story; verify exact priority in the requirement matrix.
- Preconditions: disposable seeded profile; use only fixture/source files with provenance.
- Fixtures: values specified in the sprint case; hardware and legal resources only when supplied/authorized.
- Test steps (BDD): Given the specified preconditions, when the operator performs 'Select modern CVA protocol', then Only verified current field mapping shown.
- Expected result: Only verified current field mapping shown.
- Actual result: Not executed during this audit.
- Execution environment: Not run.
- Automation type: Unit, repository, Compose, instrumented, or manual as appropriate to the case.
- Screenshot/log/test-report evidence: Not generated.
- Result: NOT_RUN. - Defect ID: See defect register.

### QA-005 — Select 2004 legacy

- Linked requirement: Sprint 04; see coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:263.
- Title: Select 2004 legacy.
- Priority: P0/P1 per linked story; verify exact priority in the requirement matrix.
- Preconditions: disposable seeded profile; use only fixture/source files with provenance.
- Fixtures: values specified in the sprint case; hardware and legal resources only when supplied/authorized.
- Test steps (BDD): Given the specified preconditions, when the operator performs 'Select 2004 legacy', then Explicit LEGACY banner and isolated scoring.
- Expected result: Explicit LEGACY banner and isolated scoring.
- Actual result: Not executed during this audit.
- Execution environment: Not run.
- Automation type: Unit, repository, Compose, instrumented, or manual as appropriate to the case.
- Screenshot/log/test-report evidence: Not generated.
- Result: NOT_RUN. - Defect ID: See defect register.

### QA-006 — Compare 2004 and CVA result

- Linked requirement: Sprint 04; see coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:264.
- Title: Compare 2004 and CVA result.
- Priority: P0/P1 per linked story; verify exact priority in the requirement matrix.
- Preconditions: disposable seeded profile; use only fixture/source files with provenance.
- Fixtures: values specified in the sprint case; hardware and legal resources only when supplied/authorized.
- Test steps (BDD): Given the specified preconditions, when the operator performs 'Compare 2004 and CVA result', then No false numeric equivalence.
- Expected result: No false numeric equivalence.
- Actual result: Not executed during this audit.
- Execution environment: Not run.
- Automation type: Unit, repository, Compose, instrumented, or manual as appropriate to the case.
- Screenshot/log/test-report evidence: Not generated.
- Result: NOT_RUN. - Defect ID: See defect register.

### QA-007 — Attempt descriptive score from affective field

- Linked requirement: Sprint 04; see coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:265.
- Title: Attempt descriptive score from affective field.
- Priority: P0/P1 per linked story; verify exact priority in the requirement matrix.
- Preconditions: disposable seeded profile; use only fixture/source files with provenance.
- Fixtures: values specified in the sprint case; hardware and legal resources only when supplied/authorized.
- Test steps (BDD): Given the specified preconditions, when the operator performs 'Attempt descriptive score from affective field', then Rejected / prevented.
- Expected result: Rejected / prevented.
- Actual result: Not executed during this audit.
- Execution environment: Not run.
- Automation type: Unit, repository, Compose, instrumented, or manual as appropriate to the case.
- Screenshot/log/test-report evidence: Not generated.
- Result: NOT_RUN. - Defect ID: See defect register.

### QA-008 — Reveal origin before blind assessment

- Linked requirement: Sprint 04; see coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:266.
- Title: Reveal origin before blind assessment.
- Priority: P0/P1 per linked story; verify exact priority in the requirement matrix.
- Preconditions: disposable seeded profile; use only fixture/source files with provenance.
- Fixtures: values specified in the sprint case; hardware and legal resources only when supplied/authorized.
- Test steps (BDD): Given the specified preconditions, when the operator performs 'Reveal origin before blind assessment', then Permission denied or explicitly audited.
- Expected result: Permission denied or explicitly audited.
- Actual result: Not executed during this audit.
- Execution environment: Not run.
- Automation type: Unit, repository, Compose, instrumented, or manual as appropriate to the case.
- Screenshot/log/test-report evidence: Not generated.
- Result: NOT_RUN. - Defect ID: See defect register.

### QA-009 — Enter invalid official-scale value

- Linked requirement: Sprint 04; see coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:267.
- Title: Enter invalid official-scale value.
- Priority: P0/P1 per linked story; verify exact priority in the requirement matrix.
- Preconditions: disposable seeded profile; use only fixture/source files with provenance.
- Fixtures: values specified in the sprint case; hardware and legal resources only when supplied/authorized.
- Test steps (BDD): Given the specified preconditions, when the operator performs 'Enter invalid official-scale value', then Validation blocks invalid value.
- Expected result: Validation blocks invalid value.
- Actual result: Not executed during this audit.
- Execution environment: Not run.
- Automation type: Unit, repository, Compose, instrumented, or manual as appropriate to the case.
- Screenshot/log/test-report evidence: Not generated.
- Result: NOT_RUN. - Defect ID: See defect register.

### QA-010 — Missing official form schema revision

- Linked requirement: Sprint 04; see coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:268.
- Title: Missing official form schema revision.
- Priority: P0/P1 per linked story; verify exact priority in the requirement matrix.
- Preconditions: disposable seeded profile; use only fixture/source files with provenance.
- Fixtures: values specified in the sprint case; hardware and legal resources only when supplied/authorized.
- Test steps (BDD): Given the specified preconditions, when the operator performs 'Missing official form schema revision', then Production standards mode blocked, custom fallback labeled.
- Expected result: Production standards mode blocked, custom fallback labeled.
- Actual result: Not executed during this audit.
- Execution environment: Not run.
- Automation type: Unit, repository, Compose, instrumented, or manual as appropriate to the case.
- Screenshot/log/test-report evidence: Not generated.
- Result: NOT_RUN. - Defect ID: See defect register.

### QA-011 — Lock assessment then edit

- Linked requirement: Sprint 04; see coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:269.
- Title: Lock assessment then edit.
- Priority: P0/P1 per linked story; verify exact priority in the requirement matrix.
- Preconditions: disposable seeded profile; use only fixture/source files with provenance.
- Fixtures: values specified in the sprint case; hardware and legal resources only when supplied/authorized.
- Test steps (BDD): Given the specified preconditions, when the operator performs 'Lock assessment then edit', then New amendment revision; original preserved.
- Expected result: New amendment revision; original preserved.
- Actual result: Not executed during this audit.
- Execution environment: Not run.
- Automation type: Unit, repository, Compose, instrumented, or manual as appropriate to the case.
- Screenshot/log/test-report evidence: Not generated.
- Result: NOT_RUN. - Defect ID: See defect register.

### QA-012 — Create 6 triangle trials

- Linked requirement: Sprint 04; see coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:270.
- Title: Create 6 triangle trials.
- Priority: P0/P1 per linked story; verify exact priority in the requirement matrix.
- Preconditions: disposable seeded profile; use only fixture/source files with provenance.
- Fixtures: values specified in the sprint case; hardware and legal resources only when supplied/authorized.
- Test steps (BDD): Given the specified preconditions, when the operator performs 'Create 6 triangle trials', then All six permutations supported and balanced.
- Expected result: All six permutations supported and balanced.
- Actual result: Not executed during this audit.
- Execution environment: Not run.
- Automation type: Unit, repository, Compose, instrumented, or manual as appropriate to the case.
- Screenshot/log/test-report evidence: Not generated.
- Result: NOT_RUN. - Defect ID: See defect register.

### QA-013 — Participant before triangle submit

- Linked requirement: Sprint 04; see coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:271.
- Title: Participant before triangle submit.
- Priority: P0/P1 per linked story; verify exact priority in the requirement matrix.
- Preconditions: disposable seeded profile; use only fixture/source files with provenance.
- Fixtures: values specified in the sprint case; hardware and legal resources only when supplied/authorized.
- Test steps (BDD): Given the specified preconditions, when the operator performs 'Participant before triangle submit', then Correct answer not exposed.
- Expected result: Correct answer not exposed.
- Actual result: Not executed during this audit.
- Execution environment: Not run.
- Automation type: Unit, repository, Compose, instrumented, or manual as appropriate to the case.
- Screenshot/log/test-report evidence: Not generated.
- Result: NOT_RUN. - Defect ID: See defect register.

### QA-014 — Triangle statistical golden cases

- Linked requirement: Sprint 04; see coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:272.
- Title: Triangle statistical golden cases.
- Priority: P0/P1 per linked story; verify exact priority in the requirement matrix.
- Preconditions: disposable seeded profile; use only fixture/source files with provenance.
- Fixtures: values specified in the sprint case; hardware and legal resources only when supplied/authorized.
- Test steps (BDD): Given the specified preconditions, when the operator performs 'Triangle statistical golden cases', then Binomial calculation correct, caveats visible.
- Expected result: Binomial calculation correct, caveats visible.
- Actual result: Not executed during this audit.
- Execution environment: Not run.
- Automation type: Unit, repository, Compose, instrumented, or manual as appropriate to the case.
- Screenshot/log/test-report evidence: Not generated.
- Result: NOT_RUN. - Defect ID: See defect register.

### QA-015 — Five-taste lesson

- Linked requirement: Sprint 04; see coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:273.
- Title: Five-taste lesson.
- Priority: P0/P1 per linked story; verify exact priority in the requirement matrix.
- Preconditions: disposable seeded profile; use only fixture/source files with provenance.
- Fixtures: values specified in the sprint case; hardware and legal resources only when supplied/authorized.
- Test steps (BDD): Given the specified preconditions, when the operator performs 'Five-taste lesson', then Sweet, sour, salty, bitter, umami all present.
- Expected result: Sweet, sour, salty, bitter, umami all present.
- Actual result: Not executed during this audit.
- Execution environment: Not run.
- Automation type: Unit, repository, Compose, instrumented, or manual as appropriate to the case.
- Screenshot/log/test-report evidence: Not generated.
- Result: NOT_RUN. - Defect ID: See defect register.

### QA-016 — Organic-acid quiz

- Linked requirement: Sprint 04; see coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:274.
- Title: Organic-acid quiz.
- Priority: P0/P1 per linked story; verify exact priority in the requirement matrix.
- Preconditions: disposable seeded profile; use only fixture/source files with provenance.
- Fixtures: values specified in the sprint case; hardware and legal resources only when supplied/authorized.
- Test steps (BDD): Given the specified preconditions, when the operator performs 'Organic-acid quiz', then Does not assert specific acid from sourness alone.
- Expected result: Does not assert specific acid from sourness alone.
- Actual result: Not executed during this audit.
- Execution environment: Not run.
- Automation type: Unit, repository, Compose, instrumented, or manual as appropriate to the case.
- Screenshot/log/test-report evidence: Not generated.
- Result: NOT_RUN. - Defect ID: See defect register.

### QA-017 — Aroma kit without license

- Linked requirement: Sprint 04; see coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:275.
- Title: Aroma kit without license.
- Priority: P0/P1 per linked story; verify exact priority in the requirement matrix.
- Preconditions: disposable seeded profile; use only fixture/source files with provenance.
- Fixtures: values specified in the sprint case; hardware and legal resources only when supplied/authorized.
- Test steps (BDD): Given the specified preconditions, when the operator performs 'Aroma kit without license', then Generic/custom mode works, proprietary list not copied.
- Expected result: Generic/custom mode works, proprietary list not copied.
- Actual result: Not executed during this audit.
- Execution environment: Not run.
- Automation type: Unit, repository, Compose, instrumented, or manual as appropriate to the case.
- Screenshot/log/test-report evidence: Not generated.
- Result: NOT_RUN. - Defect ID: See defect register.

### QA-018 — Aroma test without physical reference

- Linked requirement: Sprint 04; see coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:276.
- Title: Aroma test without physical reference.
- Priority: P0/P1 per linked story; verify exact priority in the requirement matrix.
- Preconditions: disposable seeded profile; use only fixture/source files with provenance.
- Fixtures: values specified in the sprint case; hardware and legal resources only when supplied/authorized.
- Test steps (BDD): Given the specified preconditions, when the operator performs 'Aroma test without physical reference', then Not marked as actual olfactory performance.
- Expected result: Not marked as actual olfactory performance.
- Actual result: Not executed during this audit.
- Execution environment: Not run.
- Automation type: Unit, repository, Compose, instrumented, or manual as appropriate to the case.
- Screenshot/log/test-report evidence: Not generated.
- Result: NOT_RUN. - Defect ID: See defect register.

### QA-019 — Green grade for unknown species/method

- Linked requirement: Sprint 04; see coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:277.
- Title: Green grade for unknown species/method.
- Priority: P0/P1 per linked story; verify exact priority in the requirement matrix.
- Preconditions: disposable seeded profile; use only fixture/source files with provenance.
- Fixtures: values specified in the sprint case; hardware and legal resources only when supplied/authorized.
- Test steps (BDD): Given the specified preconditions, when the operator performs 'Green grade for unknown species/method', then Out-of-scope/undetermined, no fabricated grade.
- Expected result: Out-of-scope/undetermined, no fabricated grade.
- Actual result: Not executed during this audit.
- Execution environment: Not run.
- Automation type: Unit, repository, Compose, instrumented, or manual as appropriate to the case.
- Screenshot/log/test-report evidence: Not generated.
- Result: NOT_RUN. - Defect ID: See defect register.

### QA-020 — Missing moisture measurement

- Linked requirement: Sprint 04; see coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:278.
- Title: Missing moisture measurement.
- Priority: P0/P1 per linked story; verify exact priority in the requirement matrix.
- Preconditions: disposable seeded profile; use only fixture/source files with provenance.
- Fixtures: values specified in the sprint case; hardware and legal resources only when supplied/authorized.
- Test steps (BDD): Given the specified preconditions, when the operator performs 'Missing moisture measurement', then `NOT_MEASURED`, never default zero.
- Expected result: `NOT_MEASURED`, never default zero.
- Actual result: Not executed during this audit.
- Execution environment: Not run.
- Automation type: Unit, repository, Compose, instrumented, or manual as appropriate to the case.
- Screenshot/log/test-report evidence: Not generated.
- Result: NOT_RUN. - Defect ID: See defect register.

### QA-021 — Defect tally changes after lock

- Linked requirement: Sprint 04; see coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:279.
- Title: Defect tally changes after lock.
- Priority: P0/P1 per linked story; verify exact priority in the requirement matrix.
- Preconditions: disposable seeded profile; use only fixture/source files with provenance.
- Fixtures: values specified in the sprint case; hardware and legal resources only when supplied/authorized.
- Test steps (BDD): Given the specified preconditions, when the operator performs 'Defect tally changes after lock', then Amendment/audit event required.
- Expected result: Amendment/audit event required.
- Actual result: Not executed during this audit.
- Execution environment: Not run.
- Automation type: Unit, repository, Compose, instrumented, or manual as appropriate to the case.
- Screenshot/log/test-report evidence: Not generated.
- Result: NOT_RUN. - Defect ID: See defect register.

### QA-022 — Screen reader and large font on small Android device

- Linked requirement: Sprint 04; see coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:280.
- Title: Screen reader and large font on small Android device.
- Priority: P0/P1 per linked story; verify exact priority in the requirement matrix.
- Preconditions: disposable seeded profile; use only fixture/source files with provenance.
- Fixtures: values specified in the sprint case; hardware and legal resources only when supplied/authorized.
- Test steps (BDD): Given the specified preconditions, when the operator performs 'Screen reader and large font on small Android device', then All fields and controls accessible.
- Expected result: All fields and controls accessible.
- Actual result: Not executed during this audit.
- Execution environment: Not run.
- Automation type: Unit, repository, Compose, instrumented, or manual as appropriate to the case.
- Screenshot/log/test-report evidence: Not generated.
- Result: NOT_RUN. - Defect ID: See defect register.

### QA-023 — Export and re-import JSON

- Linked requirement: Sprint 04; see coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:281.
- Title: Export and re-import JSON.
- Priority: P0/P1 per linked story; verify exact priority in the requirement matrix.
- Preconditions: disposable seeded profile; use only fixture/source files with provenance.
- Fixtures: values specified in the sprint case; hardware and legal resources only when supplied/authorized.
- Test steps (BDD): Given the specified preconditions, when the operator performs 'Export and re-import JSON', then Lossless roundtrip with versions and units.
- Expected result: Lossless roundtrip with versions and units.
- Actual result: Not executed during this audit.
- Execution environment: Not run.
- Automation type: Unit, repository, Compose, instrumented, or manual as appropriate to the case.
- Screenshot/log/test-report evidence: Not generated.
- Result: NOT_RUN. - Defect ID: See defect register.

### QA-024 — Source and license metadata missing

- Linked requirement: Sprint 04; see coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:282.
- Title: Source and license metadata missing.
- Priority: P0/P1 per linked story; verify exact priority in the requirement matrix.
- Preconditions: disposable seeded profile; use only fixture/source files with provenance.
- Fixtures: values specified in the sprint case; hardware and legal resources only when supplied/authorized.
- Test steps (BDD): Given the specified preconditions, when the operator performs 'Source and license metadata missing', then Asset/standard release gate fails.
- Expected result: Asset/standard release gate fails.
- Actual result: Not executed during this audit.
- Execution environment: Not run.
- Automation type: Unit, repository, Compose, instrumented, or manual as appropriate to the case.
- Screenshot/log/test-report evidence: Not generated.
- Result: NOT_RUN. - Defect ID: See defect register.

### QA-025 — Q Grader practice screen

- Linked requirement: Sprint 04; see coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:283.
- Title: Q Grader practice screen.
- Priority: P0/P1 per linked story; verify exact priority in the requirement matrix.
- Preconditions: disposable seeded profile; use only fixture/source files with provenance.
- Fixtures: values specified in the sprint case; hardware and legal resources only when supplied/authorized.
- Test steps (BDD): Given the specified preconditions, when the operator performs 'Q Grader practice screen', then No official credential claim or certificate.
- Expected result: No official credential claim or certificate.
- Actual result: Not executed during this audit.
- Execution environment: Not run.
- Automation type: Unit, repository, Compose, instrumented, or manual as appropriate to the case.
- Screenshot/log/test-report evidence: Not generated.
- Result: NOT_RUN. - Defect ID: See defect register.

### QA-026 — Existing flavor wheel, recipe, grinder/dripper menus

- Linked requirement: Sprint 04; see coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:284.
- Title: Existing flavor wheel, recipe, grinder/dripper menus.
- Priority: P0/P1 per linked story; verify exact priority in the requirement matrix.
- Preconditions: disposable seeded profile; use only fixture/source files with provenance.
- Fixtures: values specified in the sprint case; hardware and legal resources only when supplied/authorized.
- Test steps (BDD): Given the specified preconditions, when the operator performs 'Existing flavor wheel, recipe, grinder/dripper menus', then All remain functional and linked.
- Expected result: All remain functional and linked.
- Actual result: Not executed during this audit.
- Execution environment: Not run.
- Automation type: Unit, repository, Compose, instrumented, or manual as appropriate to the case.
- Screenshot/log/test-report evidence: Not generated.
- Result: NOT_RUN. - Defect ID: See defect register.

### QA-027 — Two assessors blind same coffee

- Linked requirement: Sprint 04; see coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:285.
- Title: Two assessors blind same coffee.
- Priority: P0/P1 per linked story; verify exact priority in the requirement matrix.
- Preconditions: disposable seeded profile; use only fixture/source files with provenance.
- Fixtures: values specified in the sprint case; hardware and legal resources only when supplied/authorized.
- Test steps (BDD): Given the specified preconditions, when the operator performs 'Two assessors blind same coffee', then Separate identity, no cross-contamination.
- Expected result: Separate identity, no cross-contamination.
- Actual result: Not executed during this audit.
- Execution environment: Not run.
- Automation type: Unit, repository, Compose, instrumented, or manual as appropriate to the case.
- Screenshot/log/test-report evidence: Not generated.
- Result: NOT_RUN. - Defect ID: See defect register.

### QA-028 — Phone rotated or low memory

- Linked requirement: Sprint 04; see coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:286.
- Title: Phone rotated or low memory.
- Priority: P0/P1 per linked story; verify exact priority in the requirement matrix.
- Preconditions: disposable seeded profile; use only fixture/source files with provenance.
- Fixtures: values specified in the sprint case; hardware and legal resources only when supplied/authorized.
- Test steps (BDD): Given the specified preconditions, when the operator performs 'Phone rotated or low memory', then No loss or duplicate submission.
- Expected result: No loss or duplicate submission.
- Actual result: Not executed during this audit.
- Execution environment: Not run.
- Automation type: Unit, repository, Compose, instrumented, or manual as appropriate to the case.
- Screenshot/log/test-report evidence: Not generated.
- Result: NOT_RUN. - Defect ID: See defect register.

### QA-029 — Offline RAG assistant asked for unmeasured TDS or grade

- Linked requirement: Sprint 04; see coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:287.
- Title: Offline RAG assistant asked for unmeasured TDS or grade.
- Priority: P0/P1 per linked story; verify exact priority in the requirement matrix.
- Preconditions: disposable seeded profile; use only fixture/source files with provenance.
- Fixtures: values specified in the sprint case; hardware and legal resources only when supplied/authorized.
- Test steps (BDD): Given the specified preconditions, when the operator performs 'Offline RAG assistant asked for unmeasured TDS or grade', then Explicit uncertainty; no invented measurement.
- Expected result: Explicit uncertainty; no invented measurement.
- Actual result: Not executed during this audit.
- Execution environment: Not run.
- Automation type: Unit, repository, Compose, instrumented, or manual as appropriate to the case.
- Screenshot/log/test-report evidence: Not generated.
- Result: NOT_RUN. - Defect ID: See defect register.

### QA-030 — Delete user data or export

- Linked requirement: Sprint 04; see coffee-creator-blueprint/SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md:288.
- Title: Delete user data or export.
- Priority: P0/P1 per linked story; verify exact priority in the requirement matrix.
- Preconditions: disposable seeded profile; use only fixture/source files with provenance.
- Fixtures: values specified in the sprint case; hardware and legal resources only when supplied/authorized.
- Test steps (BDD): Given the specified preconditions, when the operator performs 'Delete user data or export', then Privacy, consent, retention rules enforced.
- Expected result: Privacy, consent, retention rules enforced.
- Actual result: Not executed during this audit.
- Execution environment: Not run.
- Automation type: Unit, repository, Compose, instrumented, or manual as appropriate to the case.
- Screenshot/log/test-report evidence: Not generated.
- Result: NOT_RUN. - Defect ID: See defect register.
