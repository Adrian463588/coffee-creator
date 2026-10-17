# BDD Scenarios: Sprint 04 EPIC E — Pro Operations, Architecture, AI & Integration (S4-052 to S4-065)

## S4-052: Full Offline Operations
```gherkin
Feature: Full offline operations for sensory and cupping workflow
  Scenario: Running sensory academy, CVA session, and report export in airplane mode
    Given the device is operating strictly in airplane mode with zero internet connectivity
    When a user completes a 22-module academy lesson
    And initializes a 5-sample blind CVA cupping session
    And exports the completed evaluation report as native JSON and CSV
    Then all operations complete successfully without network timeout or error dialog
    And the exported report is saved locally with a valid SHA-256 seal
```

## S4-053: Additive Room Schema Migrations
```gherkin
Feature: Non-destructive database migrations
  Scenario: Preserving existing cupping sessions and relational links during database upgrade
    Given an existing database version containing saved cupping sessions and evaluation sheets
    When the database migrates additively to include operational panel links and accessibility profiles
    Then all historical cupping sessions, scores, and timestamps remain completely intact
    And all foreign keys reference valid parent entities without orphaned records
    And zero destructive fallback or table drop migrations occur
```

## S4-054: Sensory Taxonomy & Wheel Reuse
```gherkin
Feature: 221-descriptor flavor wheel reuse
  Scenario: Seamlessly selecting wheel descriptors in CVA descriptive form
    Given the authoritative 221-descriptor sensory wheel taxonomy
    When an assessor searches for "Jasmine" or "Melati" in the cupping descriptor dialog
    Then the system returns the exact reference descriptor with both English and Indonesian labels
    And links the descriptor to the active cupping sample without creating duplicate records
```

## S4-055: Cross-Domain Entity Linkage
```gherkin
Feature: Linking cupping session to bean, roast, recipe, and grinder
  Scenario: Cross-referencing cupping outcomes with production roasting and grinding
    Given an existing bean "Ethiopia Yirgacheffe Kochere" with lot ID "LOT-2026-042"
    And a roast batch "BATCH-902" and grinder "Comandante C40 MK4"
    When the user links the cupping session sample to the bean, roast batch, and grinder
    Then the linkage metadata is preserved in the cupping record
    And historical brew logs and recipe archives retain their original immutable state
```

## S4-056: Separation of Deterministic Engine and Local LLM
```gherkin
Feature: Deterministic calculation authority
  Scenario: Preventing LLM from fabricating cupping scores or defect calculations
    Given a cupping sheet with 16 attribute scores and 2 taints
    When the system calculates the final score and defect deduction
    Then the calculation is executed exclusively by the pure Kotlin deterministic rules engine
    And the local LLM is restricted to narrative explanation without overriding the mathematical score
```

## S4-057: Grounded AI Local Citations & No Fake Credentials
```gherkin
Feature: Local RAG evidence policy and anti-hallucination
  Scenario: Querying coffee sensory theory through local AI
    Given an offline AI assistant with indexed local SCA standard manuals
    When a barista asks "What is the standard water-to-coffee ratio for CVA cupping?"
    Then the AI cites the specific local document chunk, page, and standard citation
    And explicitly disclaims that it cannot award official SCA or Q Grader certificates
    And refuses to fabricate unmeasured laboratory data
```

## S4-058: Blind Panel Workflow & Role-Based Reveal
```gherkin
Feature: Blind cupping panel management
  Scenario: Cupping with anonymized blind codes until session lock
    Given a cupping panel with a Session Host and 3 Assessors
    When the Session Host generates randomized 3-digit blind codes for 4 coffee lots
    Then the Assessors see only the blind codes during tasting rounds
    And the true lot identities remain concealed until the session is formally locked
    And any premature unblinding triggers an audited UNBLINDED_PREMATURELY event
```

## S4-059: Panel Agreement & Calibration Analytics
```gherkin
Feature: Inter-rater statistical calibration
  Scenario: Calculating panel agreement metrics for 3 cuppers
    Given evaluation sheets from 3 cuppers on the same coffee sample
    When the consensus engine evaluates panel dispersion
    Then it calculates the Pearson correlation coefficient between assessor pairs
    And calculates the Mean Consensus Deviation from the panel mean
    And computes Fleiss' Kappa for categorical defect agreements with appropriate small-sample guards
```

## S4-060: Tamper-Evident Export & Offline Privacy Settings
```gherkin
Feature: Cryptographic audit exports
  Scenario: Exporting sealed sensory audit package with privacy scrub
    Given a completed cupping session with 5 assessors
    When the user exports the session with "Scrub Assessor PII" enabled
    Then assessor personal names and emails are anonymized to pseudonyms
    And the export file includes a SHA-256 seal verifying mathematical integrity
```

## S4-061: Bilingual Accessibility & Inclusive Semantics
```gherkin
Feature: Bilingual support and screen reader accessibility
  Scenario: Navigating sensory wheel and cupping forms with TalkBack
    Given the app language is set to Bahasa Indonesia
    When a visually impaired user navigates the sensory wheel and CVA forms
    Then all interactive elements provide localized TalkBack content descriptions
    And color-coded feedback includes redundant textual/icon indicators
    And touch targets strictly adhere to minimum 48dp dimensions
```

## S4-062: Sensory Limitations & Allergen Protections
```gherkin
Feature: Accommodation for sensory limitations and allergies
  Scenario: Enabling anosmia/hyposmia accommodation mode
    Given a user with temporary olfactory limitation
    When the user enables Anosmia Accommodation Mode in sensory settings
    Then the evaluation interface emphasizes basic taste intensities, body texture, and green physical grading
    And displays a non-clinical disclaimer stating the app does not diagnose medical impairments
    And presents allergen warnings for food-grade reference solutions
```

## S4-063: Controlled Template Versioning & Warnings
```gherkin
Feature: Protocol template schema versioning
  Scenario: Opening a cupping session evaluated under an older template revision
    Given a saved cupping session evaluated under protocol revision "v1.0"
    When the user views the session after a template schema update to "v2.0"
    Then the system displays a non-blocking version mismatch banner
    And preserves the original evaluation schema without forced data corruption
```

## S4-064: Evidence-Based Release Verification
```gherkin
Feature: Quality gate verification on real target hardware
  Scenario: Automated release gate execution
    Given the release candidate build v1.14.4
    When all JVM unit test suites execute
    And the APK installs cleanly on Samsung Galaxy S20 Ultra (RRCN3008VYE)
    Then 100% of unit tests pass with zero failures
    And zero user data loss occurs during ADB sidegrade
```

## S4-065: Explicit User Consent for Sharing
```gherkin
Feature: Zero unauthorized cloud transmission
  Scenario: Blocking automated cloud upload of supplier lots
    Given a registered green coffee lot with proprietary producer pricing
    When an export or sharing action is initiated
    Then the app requires explicit interactive user consent before writing to external storage
    And zero background network telemetry or cloud synchronization occurs
```
