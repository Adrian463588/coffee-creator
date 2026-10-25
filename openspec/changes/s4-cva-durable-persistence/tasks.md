# Tasks: durable Sprint 04 CVA sessions

- [x] 1. Add BDD-first repository tests for typed session create/update/list/reopen, ID stability, invalid checksums and assessment/preparation bounds, non-amended history rejection, immutable locked rows, rehashed invalid amendment snapshots, invalid current-revision JSON/CSV export, and preservation of legacy S2 records.
- [x] 2. Add v4 backup tests for export/restore, old v2/v3 fixture import, duplicate/malformed/tampered CVA records, rehashed invalid assessment payloads, exact counts, and atomic rejection.
- [x] 3. Implement the narrow CVA repository contract on existing Room record storage; avoid SQL schema changes and generic settings limits, and validate nested assessment bounds through the shared pure domain validator.
- [x] 4. Extend backup codec and repository snapshot/restore paths with additive CVA session handling.
- [ ] 5. Add ViewModel state/actions for draft persistence, saved-session lookup, compare, lock/amend, and export errors/success.
- [ ] 6. Wire the CVA Compose section through immutable state and callbacks; preserve all existing S2 routes and controls, unknown values, and custom protocol.
- [ ] 7. Add domain/UI mapping tests for every CVA assessment field and ensure no unset field becomes a measured value or official score.
- [x] 8. Run focused tests, all JVM tests, `lintDebug`, Detekt, KtLint, and `assembleDebug`; report existing failures without suppressions.
- [ ] 9. Independently review the diff and run available Compose/repository instrumentation in a disposable environment; mark physical device and SAF-provider cases NOT_RUN if unavailable.
- [x] 10. Update QA matrix and execution evidence; keep release verdict NOT_READY until all P0/P1 and device gates pass.
- [ ] 11. Commit and push this scoped branch after final diff review; do not publish a release while product gates remain NOT_READY.
