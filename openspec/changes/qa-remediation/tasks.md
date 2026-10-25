# Tasks: QA remediation and interaction polish

- [ ] R1. Verify source-of-truth, audit defects, exact working tree, Android variants/toolchain and module/test boundaries; keep baseline manifest immutable.
- [ ] R2. Add failing unit/Compose regression tests for D-001 unknown measurement defaults, D-011 default-off consent, D-002 false export/hash success and BLE simulation labeling.
- [ ] R3. Implement minimal truth-state, consent and feedback fixes; retain all routes and manual/offline workflows.
- [ ] R4. Add nested schema-v2 ChatMessage/Recipe backup fixtures; reproduce D-004 and add a compatible, atomic decoder only if expected legacy shape is established.
- [ ] R5. Inventory S4 and brew timer fields/actions; design schema migration from checked-in schema and supported backups; add migration/round-trip/recovery tests before additive persistence implementation.
- [ ] R6. Gate current CVA claims until source/license mapping is reviewed; create S4 lesson provenance register without copying protected content.
- [ ] R7. Compare historical recipe IDs/content from tag history against current catalog; preserve only authorized material; leave unsupported records explicitly unresolved.
- [ ] R8. Correct D-014 asset owner/size/digest from canonical release API; add verified factual errata for D-015 claims, with old release-body edits only after exact wording review.
- [ ] R9. Make minimal Brew UI changes: one menu chip controls one card group; preserve local input; add concise state feedback and meaningful, non-continuous transitions; no decorative redesign.
- [ ] R10. Test reduced motion, semantics, 48dp targets, large font and compact/expanded layouts with available automation; mark device-only cases blocked when no disposable profile exists.
- [ ] R11. Run focused tests, `:app:lintDebug`, detekt, ktlint, JVM suite and assemble; fix introduced issues and existing linter failures in separately reviewable commits without blanket suppressions.
- [ ] R12. Independent subagent review, diff/feature-preservation review, regenerate QA counts/evidence, verify remote commit and release asset metadata. Publish a new release only if acceptance gates actually pass; otherwise push branch and report NOT_READY.
- [x] R13. Add BDD regressions for CVA checksum coverage, lock/reveal/amendment audit, full identifiers, safe export encoding, and measured-only journal mapping.
- [x] R14. Implement the smallest additive domain fixes; preserve legacy records and keep unsaved CVA UI actions unavailable until Room/backup persistence is implemented and migration-tested.
- [x] R15. Re-run focused and full JVM suites, lint/build gates, independent review, and update a dated QA addendum with exact evidence and remaining blockers.

## Rollback

Revert only the remediation commit(s) in this worktree; retain the audit docs and baseline commit. Never reset, uninstall, clear app data, revert user records, or use destructive migration.

