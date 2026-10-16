# Offline bag photo OCR

## Why

Sprint 02 requires capturing or selecting coffee and equipment label photos, running bundled offline OCR, and letting the user correct the resulting drafts. The current screen only accepts pasted text, so S2-008 and S2-067 are incomplete.

## Scope

- Capture or select a local image through Android's system UI and retain access only through a granted `content://` URI; never derive a filesystem path.
- Run the bundled English OCR locally with bounded input and bitmap memory, cancellation, and visible errors.
- Add conservative bean and equipment draft parsing with explicit human correction; unknown facts stay unknown and no inventory is created without confirmed package and remaining mass.
- Preserve offline behavior and do not add a camera permission, cloud OCR, or network dependency.

## Out of scope

- Claims of calibrated OCR confidence or guaranteed recognition accuracy.
- Automatic inventory creation, cloud synchronization, or arbitrary URI-to-path conversion.

## Acceptance

- Add executable tests for accepted/rejected image inputs, size bounds, cancellation/recovery, and OCR-to-draft behavior.
- Manually exercise real locally captured/selected bean and equipment label images on the connected Android device; verify correction, attachment retention, and no network access.
- Existing catalog and SAF records survive replace-install; never uninstall or clear app data for this change.
