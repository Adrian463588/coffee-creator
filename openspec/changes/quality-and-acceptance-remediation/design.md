# Design: quality and acceptance remediation

## Workflow

Use the repository's spec-first loop: inventory -> exact acceptance contract -> regression test -> minimal edit -> focused test -> full JVM/static regression -> independent review. This follows the OpenSpec proposal/spec/tasks progression and the repository's BMAD-style role separation; the methodology is process guidance, not correctness evidence.

## Static-analysis strategy

Keep `config/detekt.yml` and all KtLint rules unchanged. Group current findings by rule and path, then fix in bounded batches:

1. Remove unused declarations and wildcard imports; preserve API visibility and reflection/serialization behavior.
2. Reformat Kotlin with the configured formatter only after reviewing its diff; keep user-visible strings and catalog/source values byte-for-byte stable.
3. Replace magic numbers only with local names that preserve the exact domain meaning and value; add/retain boundary tests for scientific and sensory calculations.
4. Extract complexity from oversized domain functions/classes only where the resulting collaborators express existing behavior and can be covered by existing/new JVM tests.
5. Re-run Detekt after each batch. Never suppress findings wholesale or alter thresholds.

## Acceptance-evidence strategy

The RTM is the claim ledger. For each attempted host-testable case, verify test source and assertion against the requirement, execute the exact Gradle test target, and record XML/log evidence. A test-name marker or general unit-suite pass is only a candidate until assertion relevance is verified. Keep Compose/SAF/native/BLE/physical-sensory checks `NOT_RUN` or `BLOCKED` when their required environment is absent or explicitly disallowed.

## Safety and rollback

Use the isolated `codex/s4-cva-persistence` worktree and preserve current commits and QA baseline files. Stage only files listed by the reviewed diff. If formatting changes user-visible strings, seeded records, scientific outputs, or unrelated feature code, revert that individual file change and narrow the batch. Do not touch installed app data or remote release assets.
