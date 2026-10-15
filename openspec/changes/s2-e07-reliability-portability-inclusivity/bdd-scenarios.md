# BDD Scenarios: Epic S2-E07 (Stories S2-075 through S2-082)

## Feature: SAF Persistent Model Manifest and Reinstall Rediscovery (S2-075)
  Scenario: Reinstall rediscovery scans existing SAF directory and reinstalls verified models
    Given the app has been fresh reinstalled and internal databases are empty
    And a user-selected SAF document tree contains valid GGUF binary and coffee-asset.json manifest
    When the user selects the SAF directory and triggers discovery scan
    Then the asset manager verifies file size and SHA-256 digest
    And registers the model as INSTALLED without downloading.

  Scenario: Corrupted or tampered published weights are rejected during scan
    Given a user SAF directory containing altered GGUF weights
    When the asset manager runs the rediscovery scan
    Then the corrupted asset is rejected from installation
    And an explicit checksum mismatch error is recorded.

## Feature: Resumable Model Asset Transfer with ETag and Range Verification (S2-076)
  Scenario: Interrupted transfer safely resumes with HTTP 206 and matching ETag
    Given an active download with 20 MB written and valid ETag checkpoint
    When network drops and reconnects
    And user initiates download resume
    Then request includes Range "bytes=20000000-" and If-Range headers
    And server returns HTTP 206
    And remaining bytes are appended until verified.

  Scenario: Upstream ETag modification triggers fresh transfer restart
    Given a partial download with old ETag
    When server responds with HTTP 200 and new ETag
    Then partial bytes are discarded and stream restarts from offset 0.

## Feature: Explicit Cryptographic Full Backup and Restore (S2-077)
  Scenario: Full database export produces verified cryptographic JSON archive
    Given an active database with recipes, beans, journal entries, and cupping sessions
    When user exports full backup
    Then archive contains valid manifest and SHA-256 payload digest.

  Scenario: Atomic restore rejects tampered backup without corrupting existing database
    Given existing local application records
    When user attempts restore with modified payload content
    Then checksum mismatch is detected
    And restore aborts with CorruptPayload error leaving existing records intact.

## Feature: On-Device Language and Localized Units (S2-078)
  Scenario: Dynamic language switch between English and Bahasa Indonesia
    Given UI is displaying in English ("Brew Ratio")
    When user switches language to Bahasa Indonesia
    Then UI updates immediately to "Rasio Seduh" without app restart.

  Scenario: Localized unit conversion maintains roundtrip invariant without drift
    Given target temperature 93.0°C and dose 18.0g
    When converted to Fahrenheit and Ounces
    Then values display as 199.4°F and 0.635 oz
    When converted back to Metric
    Then values return exactly to 93.0°C and 18.0g without drift.

## Feature: Adaptive Compose Layouts and Inclusive Accessibility (S2-079)
  Scenario: Adaptive layout responds to Compact (<600dp) vs Expanded (>=840dp) screen width
    Given a screen width < 600dp
    Then layout uses single-column scroll with bottom navigation
    When screen width is >= 840dp
    Then layout switches to permanent NavigationRail and dual-pane master-detail.

  Scenario: Touch target boundaries comply with 48dp minimum
    Given interactive buttons and controls
    Then bounding touch area is at least 48dp by 48dp.

## Feature: On-Device Activity Diagnostics and Bounded Ring Buffer (S2-080)
  Scenario: Circular log buffer caps memory usage and overwrites oldest entries at capacity
    Given an empty buffer with capacity 500
    When 550 events are logged
    Then buffer holds exactly 500 entries in FIFO sequence and memory stays <= 512KB.

  Scenario: PII and sensitive parameters are scrubbed before insertion
    When an error logs an IP address, storage path, or secret token
    Then scrubbed entry masks sensitive fields with redaction tokens.

## Feature: Graceful Degradation Across Subsystems (S2-081)
  Scenario: Missing local LLM model degrades gracefully to deterministic rules
    Given no local LLM model is installed
    When user views Recipe Formulation
    Then badge displays "Deterministic Science Rules (No Local LLM)"
    And manual brewing calculations remain fully operational.

  Scenario: Disconnected BLE scale degrades to manual virtual timer
    Given no Bluetooth scale connected
    When brew timer opens
    Then status badge displays "Manual Scale & Timer (BLE Offline)"
    And manual progress controls allow logging brew yield.

## Feature: Regression and Performance Matrix (S2-082)
  Scenario: Process death and recreation during an active brew session
    Given an active brew session at elapsed time 65 seconds
    When process is killed and recreated
    Then state restores from SavedStateHandle using monotonic nanoTime delta.

  Scenario: 15-minute continuous brew soak test proves zero timer drift
    Given timer running continuously for 900 seconds
    When compared against monotonic clock
    Then cumulative drift is less than 50 milliseconds.
