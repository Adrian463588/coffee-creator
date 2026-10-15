# Brew UX and verified reference dataset plan

Date: 2026-10-15. Requirements: PRD FR-01/02/03/06, DESIGN progressive disclosure,
and the user's request for separate Brew cards selected with chips and valid
internet-sourced data. This extends IMPLEMENTATION_PLAN.md before implementation.

1. Preserve domain validation, draft persistence, recipe revision ownership,
   measurement provenance, offline operation, and existing accessibility controls.
2. Brew wireflow: Home > Brew > Recipe (name, bean, method, dose/water, timer) >
   Measurements (optional measured beverage/TDS and process inputs) > Ingredients
   (food-grade/supplier/allergen controls) > Tools (dilution/concentration/density).
   A wrapping chip group switches the visible card group. Selection survives
   recreation; editing another group must retain entered values. Save and ratio
   stay discoverable. No measured field is populated from reference data.
3. Improve visual hierarchy using existing Material 3 semantic colors, spacing,
   rounded cards, readable headings, explicit empty states and >=48dp actions.
   Retain route/test identifiers and implement Navigation Compose with back state.
4. Research primary public sources at development time. Record exact source URLs,
   retrieval date, factual field mapping, source hashes and reuse/license terms.
   Prefer explicit open licenses. No indiscriminate copyrighted manual scraping,
   hidden-network runtime, invented supplier lots, densities or tasting scores.
5. Bundle a bounded, versioned reference dataset with per-record provenance.
   Separate reference varieties/methods from purchased lots and user measurements.
   Show source and unknown fields. Upgrade by stable identifiers without
   overwriting user edits or resurrecting intentionally deleted data.
6. BDD checks: chip selection reveals only its section; values survive switching
   and recreation; golden calculation/save remain accessible; catalog reference
   IDs and URLs are unique, source records match checked snapshots, malformed
   data fails closed, existing database contents survive dataset upgrade.
7. Integrator runs formatter, detekt, lint, JVM tests and debug builds sequentially.
   Run physical Compose journeys, offline RAG/SAF/native suites and benchmark when
   ADB reconnects. Capture actual screenshots; record all untested device tiers.
8. Finalize requirement checklist, third-party notices, README/history, then
   commit/push and publish a versioned debug APK with verified download checksum.

Parallel work uses isolated worktrees with one integrator. UI owns screen/theme
and UI tests; data research owns dataset/catalog migration and its tests; RAG
review owns retrieval changes/tests. No worker builds or uses ADB concurrently.

Current checkpoint: static/build gates and 83 JVM cases passed before these new
changes. Latest focused physical retest: 6/7 passed, direct-fact retrieval 39/40;
this is unresolved, not a passing release. ADB currently lists no devices.

Rollback: revert a scoped conventional commit. Never reset user storage, revoke
their grants, uninstall the app, replace measurements with reference data, or
rewrite Git history to hide a failed test.
