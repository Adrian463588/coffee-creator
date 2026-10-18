# Specification: durable Sprint 04 CVA sessions

## Requirements

### Requirement: CVA session persistence

The system SHALL store CVA session aggregates by stable ID through the data repository, and SHALL reload the same aggregate after repository/database recreation. Saving a draft SHALL not modify S2 cupping records, recipes, brew journal entries, or sensory observations.

#### Scenario: resume an edited draft

Given an editable CVA draft with stable session and sample IDs, recorded observations, unknown states, and provenance
When it is saved and the repository is reopened
Then the same IDs and values are returned exactly once
And all pre-existing S2 cupping, recipe, journal, and observation IDs and values remain unchanged.

#### Scenario: reject invalid or duplicate writes

Given a stored CVA session
When a create uses an existing ID, an update uses an absent ID, a checksum is invalid, or the payload exceeds supported bounds
Then the write fails atomically and the stored session and unrelated records remain unchanged.

#### Scenario: reject out-of-range nested assessment values

Given a CVA session contains a negative quaker count, an invalid multi-cup index or intensity, or an out-of-range legacy score
When the session is saved or decoded from a rehashed v4 backup
Then assessment validation rejects the payload before storage mutation
And an existing valid session remains unchanged.

### Requirement: immutable lock and amendment history

The system SHALL persist only domain-validated session transitions. A locked session SHALL reject ordinary updates and reveal changes. An amendment SHALL preserve the exact previous snapshot and checksum and SHALL require a non-empty actor identifier and reason.

#### Scenario: lock and reopen

Given a complete session with valid preparation evidence
When the assigned workflow locks it and the database is reopened
Then the locked state, timestamp, signature text, and checksum match the saved record
And ordinary mutation is rejected.

#### Scenario: reject out-of-range preparation temperature

Given a stored session with valid preparation values
When an update or import supplies a water temperature outside the existing 85–99 °C validation range
Then the write is rejected atomically
And the previously stored session remains unchanged.

#### Scenario: amend without rewriting history

Given a locked session
When an amendment is created with an actor and reason
Then the original canonical snapshot and checksum remain verifiable in amendment history
And an invalid or tampered previous snapshot is rejected during export.

#### Scenario: reject a rewritten amendment checksum field

Given an amendment snapshot whose stored checksum field or canonical JSON no longer matches its recorded checksum
When the session is exported or restored
Then integrity validation rejects the amendment
And no export or restore reports success.

#### Scenario: reject a rehashed invalid prior assessment

Given a locked amendment snapshot contains an invalid assessment value but its canonical JSON and checksum are recomputed
When the amendment is saved, restored, or exported
Then domain validation rejects the prior snapshot even though its checksum is internally consistent.

#### Scenario: reject amendment history on a non-amended session

Given a draft or in-progress session carries otherwise valid amendment history
When the session is created, updated, or restored
Then validation rejects the session before storage mutation
And no non-amended state can add, alter, or remove amendment history.

#### Scenario: reject invalid preparation in an amendment snapshot

Given a locked amendment snapshot has invalid TDS, brew ratio, dose, water volume, temperature, cup count, roast-rest days, roast date, Agtron, grind size, or bowl count
When its amendment is exported or restored
Then the shared preparation validator rejects the prior snapshot
And a recomputed checksum does not make invalid measurements or metadata acceptable.

### Requirement: compare and export persisted sessions

The system SHALL derive comparisons and exports from repository-loaded records, never from an unsaved UI-only sample. Blind identity SHALL remain concealed unless a valid audited reveal is present. A user-selected SAF destination SHALL receive the exact exported bytes; cancellation or write failure SHALL not claim success or alter the source session.

#### Scenario: compare persisted sessions

Given two saved sessions with matching comparison dimensions
When the user compares them
Then the comparison engine receives those stored versions and the comparison includes no concealed lot identity.

#### Scenario: export a persisted session

Given a saved session revision
When the user exports JSON or CSV to a chosen destination
Then the file reflects the stored protocol revision, observations, evidence statuses, reveal audit, and amendment history supported by that format
And export success is shown only after exact-byte write verification succeeds.

#### Scenario: reject export of invalid current preparation

Given an amended session whose current preparation contains an invalid typed value
When JSON or CSV export is requested
Then both export formats reject the session before producing output
And valid preparation in its historical amendment snapshot does not make the current revision exportable.

### Requirement: additive backup compatibility

The system SHALL export CVA sessions in backup schema v4 and SHALL preserve v2/v3 import behavior. Backup integrity, collection shape, IDs, and counts SHALL be validated before any repository mutation.

#### Scenario: round-trip v4 and import older versions

Given a v4 backup containing CVA sessions and an existing S2 data baseline
When it is restored and re-exported
Then CVA IDs, snapshots, provenance, and existing records are preserved
And v2/v3 backups import with an empty CVA collection.

#### Scenario: restore older backup without deleting newer CVA records

Given a stored CVA session and a v2/v3 backup that predates CVA support
When the user restores that older backup
Then the legacy backup is imported using its original checksum and record counts
And the existing CVA session remains byte-for-byte unchanged because the legacy format cannot represent it.

#### Scenario: reject a corrupted v4 backup atomically

Given a backup with a modified CVA payload, invalid checksum, duplicate session ID, or malformed session record
When restore is requested
Then validation rejects it before any existing record is changed.

### Requirement: current protocol standards gate

The system SHALL keep `SCA_CVA_CURRENT` unavailable for scored production sessions until an authorized field mapping and licensing evidence are recorded. `CUSTOM_RESEARCH` SHALL remain clearly labeled and usable offline.

#### Scenario: unverified current protocol

Given no approved current field mapping/license manifest
When a user opens CVA session setup
Then the current protocol is labeled unverified and cannot produce an official score or compliance claim
And custom research sessions remain available.
