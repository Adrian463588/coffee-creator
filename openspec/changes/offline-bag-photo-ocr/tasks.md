# Implementation tasks

- [x] Add JVM tests for MIME/byte/dimension bounds, parser preservation of unknown facts, and explicit equipment-label fields.
- [x] Reuse bundled Tesseract through a bounded Android service without exposing filesystem paths.
- [x] Add system camera capture and SAF image selection with ViewModel loading/error/retry state.
- [x] Add bean and equipment draft parsers; require explicit equipment category and preserve unknown measurements/safety fields.
- [x] Attach readable granted photo URIs to confirmed records and preserve the manual correction gate.
- [x] Add instrumented source coverage for local OCR, invalid/revoked inputs, oversize rejection, bitmap cancellation cleanup, and canceled camera-row cleanup.
- [ ] Run lint, Detekt, KtLint, and all JVM tests. Not run in this isolated slice; integrator owns Gradle verification.
- [ ] Run airplane-mode device acceptance with real locally selected bean and equipment label photos; preserve app data and unrelated SAF files.
- [ ] Update the requirement checklist and release notes from observed evidence only.

Source implementation and test cases are present. No Gradle or device command was run in this worktree, so no execution result is claimed.
