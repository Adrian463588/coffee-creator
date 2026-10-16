# Implementation tasks

- [ ] Add regression tests proving fresh initialization has no personal history or palate evidence, and rating-only journal saves do not invent sensory evidence.
- [ ] Add failing save/reopen and full-backup round-trip tests for all durable user-created journal, cupping, cafe-cup, QC, roast-rest, inventory, equipment, water, green-lot, and roast-batch records.
- [x] Add tests proving brew completion stores only supported timer facts.
- [x] Add OCR tests for missing/invalid/explicit package mass and unknown confidence.
- [x] Add tool tests for unknown ingredient safety and absent recommendation/feedback evidence.
- [x] Add BDD regressions for manual preferences, malformed imports, unsupported balanced claims, supplier limits, and safe temperatures.
- [x] Enforce the corresponding evidence and safety boundaries in the domain engines and AI tool adapter.
- [x] Remove automatic personal-history and inventory seed injection without deleting stored records.
- [x] Remove synthetic brew completion measurements and sensory observations.
- [x] Make OCR/manual catalog paths preserve unknowns and require explicit package mass for inventory.
- [x] Make AI tools require user-backed evidence and fail closed when unavailable.
- [ ] Persist user-created workbench records during normal use, hydrate them after repository restart, and include them in the full backup/restore contract.
- [ ] Review simulator labeling and ensure simulated values cannot be represented as physical measurements.
- [ ] Pass Detekt and ktlint without reducing configured checks; current run reports 198 Detekt findings and main-source ktlint failures.
- [x] Run Android lint and all JVM unit tests; record exact results in `docs/VERIFICATION.md` after final gate runs.
- [ ] Run the connected-device UI/SAF tests after the final build; preserve app data and SAF-owned folders.
- [ ] Update verification/release docs to match observed evidence and keep unresolved claims unchecked.
