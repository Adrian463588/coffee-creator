# BDD Scenarios: Sprint 04 Sensory Academy, CVA Cupping, Training Lab & Green Grading

Feature: Epic A - Sensory Academy & Scientific Knowledge Base (S4-001 - S4-010)
  Scenario: S4-001 - Accessing the Sensory Learning Academy Dashboard
    Given the app is launched in offline airplane mode
    When the user navigates to "Sensory & Cupping -> Academy"
    Then 22 modular syllabus lessons (EDU-01 to EDU-22) are listed
    And each lesson displays estimated duration, curriculum category, and offline cache status
    And navigation provides screen-reader accessibility labels

  Scenario: S4-003 - Scientific correction of the Five Basic Tastes
    Given the user opens module "EDU-05: Gustatory Anatomy & Five Basic Tastes"
    When reviewing the basic taste classification
    Then exactly five basic tastes are presented: Sweet, Sour, Salty, Bitter, and Umami
    And the module states explicitly that "Acid" is NOT a distinct sixth basic taste
    And the text explains that sourness is the gustatory modal sense while perceived acidity is a multimodal sensation

  Scenario: S4-004 - Consulting the Organic Acid Atlas
    Given the user navigates to the "Organic Acid Atlas" in EDU-07
    When inspecting Citric, Malic, Tartaric, Acetic, Lactic, Phosphoric, and Quinic acids
    Then each acid provides chemical formula, pKa1, typical sensory cues, and coffee occurrence
    And safe handling warnings prohibit consuming concentrated laboratory reagents
    And food-grade reference preparation concentrations (grams per liter) are provided with measurement uncertainty

  Scenario: S4-006 - Le Nez du Café 60 Physical Kit Companion Privacy
    Given the user opens the "Aroma 60 Kit Companion" (EDU-09)
    When configuring user-owned physical aroma vials
    Then the app provides generic sensory taxonomy descriptors
    And the app allows the user to input custom vial identifier codes
    And no proprietary text, guide photos, or licensed artwork from Editions Jean Lenoir are bundled without license

Feature: Epic B - Digital CVA Cupping Engine & Perspectives (S4-011 - S4-026)
  Scenario: S4-011 & S4-012 - Session creation with Protocol selection
    Given an assessor initializes a new cupping session
    When selecting protocol "SCA_CVA_CURRENT"
    Then the session enforces 4 separate perspectives: Physical, Descriptive, Affective, Extrinsic
    And when selecting "SCA_2004_LEGACY", the session displays an isolated historical legacy disclaimer
    And the app blocks automatic score conversion between legacy 2004 and CVA protocols

  Scenario: S4-014 & S4-015 - Perspective separation (Descriptive vs Affective)
    Given an active cupping session under "SCA_CVA_CURRENT"
    And blind sample code "824" is selected
    When the cupper logs a descriptive acidity intensity of 4.2 with descriptor "Citric"
    Then the observation is saved exclusively to the Descriptive assessment
    And the affective acidity preference score remains untouched
    And no automated affective score is inferred from descriptive intensity

  Scenario: S4-016 & S4-017 - Extrinsic data blinding and audit reveal
    Given a blinded cupping session with sample "419"
    When the cupper views the sample details during active tasting
    Then origin, farm name, and processing method are masked as "[CONCEALED]"
    When the session host requests an emergency reveal before session completion
    Then the app prompts for an explicit reason
    And logs an immutable reveal audit record with timestamp and operator pseudonym

  Scenario: S4-019 & S4-023 - Autosave, lock, and amendment history
    Given an assessor is recording cupping notes on mobile
    When the Android system terminates the app process due to low memory
    Then upon relaunch, the exact draft state and slider positions are restored without data loss
    When the assessor completes and signs the session with "LOCK_SESSION"
    Then the session state changes to LOCKED and generates a SHA-256 integrity hash
    And subsequent edits require creating a versioned AMENDMENT with mandatory justification

  Scenario: S4-024 - App-native JSON/CSV Export
    Given a completed and locked CVA cupping session
    When the user exports the session data
    Then the generated JSON export includes protocolVersion, fieldMappingVersion, and SHA-256 checksum
    And the report is formatted in the app's native schema without imitating proprietary SCA paperwork

Feature: Epic C - Sensory Training & Triangle Testing Engine (S4-027 - S4-040)
  Scenario: S4-027 & S4-028 - 6-Permutation balanced Triangle test
    Given a trainer sets up a blind triangle drill with 6 trials
    When the session is generated
    Then the presentation permutations are balanced across AAB, ABA, BAA, BBA, BAB, and ABB
    And each cup receives an opaque 3-digit randomized code
    And the odd-cup answer key remains concealed on the assessor device until final submission

  Scenario: S4-030 - Exact Binomial Distribution probability calculus
    Given an assessor completes a triangle test series of n=18 trials
    When the assessor correctly identifies k=10 odd cups
    Then the system computes the exact cumulative binomial probability P(X >= 10) for p=1/3
    And the resulting p-value is calculated as approximately 0.0377
    And the app marks the result as statistically significant at alpha = 0.05
    And the screen displays mandatory caveats on sample independence and sensory fatigue
    And the app displays no claim of official SCA or Q-Grader credential certification

  Scenario: S4-031 - Basic Taste Reference Solution Preparation
    Given a trainee prepares reference solutions for 5 basic tastes
    When viewing preparation guidelines for Sucrose, Citric Acid, NaCl, Caffeine, and MSG
    Then exact concentrations (g/L) for detection, low, medium, and high intensities are displayed
    And water specifications mandate odourless demineralized or filtered water (<30 ppm TDS)
    And a safety warning forbids using unverified chemicals or lab-grade toxic compounds

Feature: Epic D - Green Coffee Physical Assessment (S4-041 - S4-051)
  Scenario: S4-041 & S4-043 - Equivalent defect calculation on 350g sample
    Given a 350g sample of washed Coffea Arabica green lot "GT-2026-01"
    When the QC inspector records the following defects:
      | Defect Name         | Tier       | Bean Count |
      | Full Black          | Category 1 | 1          |
      | Severe Insect       | Category 1 | 5          |
      | Partial Sour        | Category 2 | 6          |
      | Broken / Chipped    | Category 2 | 10         |
    Then the engine computes Category 1 full defects as 2.0 (1 full black + 1 equivalent severe insect)
    And computes Category 2 full defects as 4.0 (2 equivalent partial sour + 2 equivalent broken)
    And evaluates total full defect equivalents as 6.0
    And determines the coffee is not eligible for Specialty Grade 1 due to presence of Category 1 defects

  Scenario: S4-044 & S4-045 - Sieve distribution and moisture measurement
    Given a 350g green sample subjected to sieve analysis
    When screen masses are recorded for screens 18, 17, 16, 15, and 14
    Then the app calculates percentage retained on each screen summing to 100%
    When moisture is measured at 11.2% with meter uncertainty +/- 0.2%
    Then the moisture is recorded as MEASURED with uncertainty
    And if moisture is not measured, the app displays NOT_MEASURED and never defaults to 0.0%

  Scenario: S4-047 - Out-of-scope guard for Canephora / Natural processing
    Given a green lot with species "COFFEA_CANEPHORA" (Robusta)
    When running the SCA Washed Arabica 2004 physical grading engine
    Then the engine flags the evaluation as "UNDETERMINED_OUT_OF_SCOPE"
    And explains that the 2004 washed Arabica rule engine cannot be validly applied to Canephora

Feature: Epic E - Pro Operations, Additive Migrations, AI & Accessibility (S4-052 - S4-065)
  Scenario: S4-052 & S4-053 - Zero data loss database migration from Sprint 03
    Given an active device database containing Sprint 01-03 brew logs, grinders, and recipes
    When the app upgrades to the Sprint 04 schema
    Then all previous database entities and foreign keys remain intact
    And new CVA cupping tables and fields are added additively with nullable defaults
    And automated migration regression tests pass with zero data loss
