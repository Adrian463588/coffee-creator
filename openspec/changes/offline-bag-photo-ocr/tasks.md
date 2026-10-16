# Implementation tasks

- [x] Add JVM tests for MIME/byte/dimension bounds, parser preservation of unknown facts, and explicit equipment-label fields.
- [x] Reuse bundled Tesseract through a bounded Android service without exposing filesystem paths.
- [x] Add system camera capture and SAF image selection with ViewModel loading/error/retry state.
- [x] Add bean and equipment draft parsers; require explicit equipment category and preserve unknown measurements/safety fields.
- [x] Attach readable granted photo URIs to confirmed records and preserve the manual correction gate.
- [x] Add instrumented source coverage for local OCR, invalid/revoked inputs, oversize rejection, bitmap cancellation cleanup, and canceled camera-row cleanup.
- [x] Run lint, Detekt, KtLint, and all JVM tests. Integrator result: aggregate v1.10.0 gates passed; 244 JVM tests, zero failures/errors/skips. See [`docs/VERIFICATION.md`](../../../docs/VERIFICATION.md).
- [ ] Run airplane-mode device acceptance with real locally selected bean and equipment label photos; preserve app data and unrelated SAF files.
- [x] Update the requirement checklist and release notes from observed evidence only; generated-image OCR coverage is explicitly distinguished from real packaging-photo acceptance.

## Integration and release follow-up

- [x] Fix keyboard panning that hid invalid-form feedback; retain the visible-error BDD assertion.
- [x] Record current on-device test counts, APK checksum, dataset provenance check and blocked SAF setup in the verification report.
- [ ] Re-run real-image/camera-to-record acceptance after an authorized QA image and SAF tree grant are available.

Source implementation and test cases are present. No Gradle or device command was run in this worktree, so no execution result is claimed.
