# Tasks: quality and acceptance remediation

- [x] Verify branch, commit, clean worktree, existing QA evidence, Detekt config, RTM denominator, and master BDD/E2E denominator.
- [x] Record current finding and acceptance baselines before code changes.
- [x] Review independent subagent inventories of Detekt findings and requirement-test traceability.
- [x] Choose one safe Detekt/KtLint batch from the measured findings; preserve all content and behavior.
- [x] Add or strengthen a regression assertion before any non-formatting refactor.
- [ ] Run focused tests, full JVM suite, Detekt, KtLint main/test checks, lint, and debug assembly as appropriate.
- [ ] Recompute requirement-level evidence only for requirement-linked assertions actually executed; preserve device cases as `NOT_RUN`.
- [ ] Update QA execution evidence, defect status, and release verdict without inflating coverage.
- [ ] Independent diff review; commit and push the scoped result if all executed gates are recorded accurately.
