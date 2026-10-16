# Implementation tasks

- [ ] Add failing tests for MIME/byte/dimension bounds, URI errors, cancellation cleanup, and parser preservation of unknown facts.
- [ ] Extract or reuse the bundled Tesseract OCR adapter behind a bounded Android service without exposing filesystem paths.
- [ ] Add system camera capture and SAF image selection with lifecycle-aware ViewModel loading/error/retry state.
- [ ] Add bean and equipment draft parsers; require explicit equipment category and preserve unknown measurements/safety fields.
- [ ] Attach the granted photo URI to confirmed records and preserve the manual correction gate.
- [ ] Add Android coverage for OCR state transitions and verify lint, Detekt, KtLint, and all JVM tests.
- [ ] Run airplane-mode device acceptance with a real locally selected bag image; preserve app data and unrelated SAF files.
- [ ] Update the requirement checklist and release notes from observed evidence only.
