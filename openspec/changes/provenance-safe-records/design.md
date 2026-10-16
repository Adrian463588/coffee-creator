# Design: provenance-safe user records

## Boundaries

- Pure parsing and recommendation preconditions remain in `core/catalog`, `core/ai`, and domain helpers.
- The ViewModel coordinates local repository writes and user-visible state; Compose remains a state consumer.
- Source-backed reference catalog import remains separate from personal history and inventory.
- Existing database and backup schemas stay backward compatible. No existing row is migrated or removed.

## Data rules

- Missing extraction fields are represented as `null`; OCR heuristic confidence is unknown (`null`) unless a calibrated model supplies it.
- Timer duration and recipe settings are plan/event context, not measurements of beverage mass, TDS, taste, or puck preparation.
- A connected scale reading is usable only when the active source is identified as a real device. Simulator samples are never persisted as physical measurements.
- Tool output requires complete, validated inputs. Unknown safety evidence fails closed.
- Manual catalog fields preserve unknown values. Inventory package/remaining mass must be explicitly supplied.

## Recovery and compatibility

- Do not clear storage, reset preferences, rewrite imported records, or remove SAF assets.
- New and existing users retain their current database. This change only stops automatic transient sample injection and stops creating synthetic records.
- Curated imports remain an explicit opt-in and stay visibly identified as curated reference content.

## Verification

- Add failing JVM tests for missing OCR facts, invalid weights, unknown safety, and evidence-required AI tools.
- Add deterministic event-record tests ensuring a completed timer cannot fabricate observations or measured values.
- Add ViewModel/UI assertions for empty personal history on a fresh profile and explicit curated import.
- Run unit tests, lint, detekt, ktlint, debug build, Android test assembly, and the available connected-device suites.
- Reinstall with `adb install -r` only; do not uninstall or clear user state. Verify current records and SAF grant remain intact.
