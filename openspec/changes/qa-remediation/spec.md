# Specification: S01–S04 QA remediation and Brew interaction polish

## Green QC measurement truth (D-001)

### Scenario: new lot has no measurements
Given a new Green QC record with no user measurements or instrument evidence
When the record is opened
Then measured moisture, water activity, density, defect counts and calibration show `NOT_MEASURED`/`UNKNOWN`
And no numeric measurement, pass/fail classification, or source citation is invented.

### Scenario: save an actual measurement
Given a user enters a supported value and its method/source metadata
When the value is validated and saved
Then the persisted value retains unit, method, timestamp, operator/source and measurement status
And invalid or out-of-scope inputs remain unsaved with an actionable error.

## S4 persistence and export (D-002, D-003, D-006)

### Scenario: recover an S4 draft
Given a user edits a cupping or Green QC draft
When the app is recreated or the process is interrupted
Then the draft reopens with the same stable ID, values, unknown states and links
And existing recipes, journals and observations remain unchanged.

### Scenario: export and lock
Given a valid persisted record
When the user locks or exports it
Then the app writes the canonical record through the user-selected document destination
And any displayed SHA-256 equals the exact exported bytes
And cancel/failure is reported without claiming success or changing the source record.

## Standards, consent, provenance and device claims (D-005, D-007–D-012)

### Scenario: unverified CVA mapping
Given no authorized, current source mapping is checked in
When a user opens the current-standard protocol
Then the existing entry remains visible with a clear verification status
And it cannot produce an official score or claim compliance; custom research remains available.

### Scenario: new sharing preference
Given a clean profile with no recorded consent
When Pro Operations opens
Then sharing is off until explicit opt-in and each export shows its scope.

### Scenario: simulated hardware or unverified evidence
Given only a simulated BLE driver, synthetic OCR fixture, or unreviewed video is available
When the user opens the related flow
Then the UI labels the limitation and keeps the manual/offline path available.

## Brew interaction and motion

### Scenario: choose a Brew menu
Given the Brew screen contains menu chips
When the user selects a chip
Then only that menu's cards are shown, the selected state is visibly and accessibly announced, and switching back restores the previous menu without losing form state.

### Scenario: receive action feedback
Given the user selects, expands, starts, pauses or saves a Brew action
When the action succeeds or fails
Then concise visual and semantic feedback reflects the actual state; errors remain actionable; no decorative motion implies that data was saved.

### Scenario: reduced motion and assistive technology
Given system animation reduction or TalkBack is enabled
When the user completes the same Brew journey
Then essential information and state changes remain available without relying on motion/color, controls meet 48dp targets, and no continuous/decorative animation runs.

## Release truth and preservation (D-004, D-013–D-015)

### Scenario: historical backup compatibility
Given a real legacy backup containing nested chat and recipe records
When it is validated and restored in a disposable database
Then supported missing fields are explicitly defaulted, stable IDs and content survive, and failures are atomic.

### Scenario: historical recipe corpus
Given a source-authorized historical catalog and its exact identifiers
When old and current catalog manifests are compared
Then every removed/changed recipe is traced or marked unresolved; test fixtures never become production recipes.

### Scenario: release artifact documentation
Given canonical GitHub tag and asset metadata
When README/release history is checked
Then repository owner, URL, version, byte size and verified digest match the asset; claims unsupported by tagged code are clearly corrected, not repeated.

