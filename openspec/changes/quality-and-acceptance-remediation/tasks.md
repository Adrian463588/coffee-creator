# Tasks: quality and acceptance remediation

- [x] Verify branch, commit, clean worktree, existing QA evidence, Detekt config, RTM denominator, and master BDD/E2E denominator.
- [x] Record current finding and acceptance baselines before code changes.
- [x] Review independent subagent inventories of Detekt findings and requirement-test traceability.
- [x] Choose one safe Detekt/KtLint batch from the measured findings; preserve all content and behavior.
- [x] Add or strengthen a regression assertion before any non-formatting refactor.
- [x] Run focused regression coverage, full JVM suite, Detekt, KtLint main/test checks, Android Lint, and debug assembly; record findings and artifacts.
- [ ] Recompute requirement-level evidence only for requirement-linked assertions actually executed; preserve device cases as `NOT_RUN`.
- [x] Update QA execution evidence and release verdict without inflating coverage; unresolved product defects remain open.
- [x] Complete independent read-only diff review; no likely feature/data-loss blocker found.
- [ ] Commit and push the scoped result after evidence review.
