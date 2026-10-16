# Design: offline bag photo OCR

## Flow and boundaries

- Compose offers MediaStore camera capture on Android 10+ and `OpenDocument` image selection on supported Android versions. Older Android versions keep picker and manual-text paths. The image remains a granted `content://` URI; persist document read grants where supported.
- The ViewModel dispatches the selected URI to an Android-owned OCR/import service on an I/O dispatcher and exposes immutable loading, success, error, and cancellation state.
- The service validates MIME type and bounded byte size through `ContentResolver`, decodes with sampling to a maximum 1600-pixel edge, runs the already bundled Tesseract English data, and releases bitmap/engine resources in `finally` blocks.
- OCR output feeds conservative bean and equipment draft parsers. Review forms are the only path to saved records; a photo URI is attached only while its provider grant remains readable. Equipment category and unobserved parameters are never guessed from OCR text.
- No URI is converted to a filesystem path. OCR work never runs during Compose composition or on the main thread.

## Safety and recovery

- Accept only explicitly supported image MIME types; reject malformed, oversized, unreadable, or unsupported inputs with a recoverable message.
- Bound compressed bytes and decoded dimensions before allocating a full-resolution bitmap. Check coroutine cancellation between read, decode, and recognition stages.
- Keep package mass and roast date unknown when OCR did not observe them. Do not assign a numeric confidence score without a calibrated model.
- If access is revoked or the provider is unavailable, keep the editable recognized text if already produced, mark the photo unavailable, and ask the user to capture/select it again before attaching it.
- No camera permission, remote request, or telemetry is introduced.

## Verification

- JVM tests continue to cover parsing, absent facts, invalid mass, and explicit corrections.
- Instrumented tests cover bundled OCR from a local content URI, unsupported/revoked and oversized inputs, bitmap cleanup after OCR cancellation, and removal of a canceled pending MediaStore row. These tests are added but were not run in this isolated slice.
- Compose/ViewModel state transitions, lint, unit tests, and real-camera/picker flows remain for integrator verification; source review is not runtime acceptance.
- Device acceptance uses real local bean and equipment label photos from the system camera/picker, checks both review/save flows, confirms URI access after app restart while grants remain valid, and checks airplane-mode operation.
