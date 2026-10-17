Feature: Epic B - Digital CVA Cupping & Coffee Grading (S4-011 - S4-026)

  # ============================================================================
  # S4-011: Multi-sample Session Setup & Controlled Prep Metadata
  # ============================================================================
  Scenario: S4-011 - Creating a multi-sample session with controlled prep metadata
    Given an assessor initializes a new cupping session
    When the assessor enters cupper pseudonym "Q-Grader Hendra"
    And adds 3 coffee lots: "Lot #A1 - Gayo Natural", "Lot #B2 - Kerinci Washed", "Lot #C3 - Toraja Sapan"
    And specifies 5 cupping bowls per sample
    And records equipment metadata:
      | Equipment Type | Model / Specification       | Setting          |
      | Grinder        | Mahlkönig EK43S             | 850 microns      |
      | Kettle         | Fellow Stagg EKG Pro        | 93.5 Celsius     |
      | Bowls          | Ceramic 200mL Cupping Bowls | 5 bowls / sample |
    And records controlled preparation parameters:
      | Parameter        | Target Value | Unit     | Evidence Status |
      | Water TDS        | 125          | ppm      | MEASURED        |
      | Brew Temperature | 93.5         | Celsius  | MEASURED        |
      | Brew Ratio       | 0.055        | g/mL     | MEASURED        |
      | Dose per Bowl    | 11.0         | grams    | MEASURED        |
      | Water per Bowl   | 200.0        | mL       | MEASURED        |
    Then the session initializes 3 blind samples with distinct 3-digit opaque codes
    And all blind codes are formatted as three-digit strings between "100" and "999"
    And the session state is set to "DRAFT"
    And initial evidence status for prep metadata is marked as "MEASURED"

  # ============================================================================
  # S4-012: Protocol Selector & Visible Badges
  # ============================================================================
  Scenario Outline: S4-012 - Protocol selection and prominent warning disclaimers
    Given an assessor is creating a new cupping session
    When the assessor selects protocol "<protocol>"
    Then the active protocol is set to "<protocol>"
    And the screen displays badge text "<badge_text>"
    And the screen displays banner disclaimer "<disclaimer>"

    Examples:
      | protocol         | badge_text            | disclaimer                                                                                           |
      | SCA_CVA_CURRENT  | CVA 2024 STANDARD     | SCA-aligned research implementation. Not an official certification grant.                           |
      | SCA_2004_LEGACY  | LEGACY 2004 HISTORIC  | HISTORICAL LEGACY FORM: Superseded by CVA standards. Scores must not be equated to CVA evaluations. |
      | CUSTOM_RESEARCH  | CUSTOM RESEARCH LAB   | Custom sensory experiment. For internal laboratory calibration only.                                 |

  Scenario: S4-012 - Block automated score conversion from legacy to CVA
    Given a completed cupping session under protocol "SCA_2004_LEGACY" with score 87.25
    When the user attempts to view the session under "SCA_CVA_CURRENT"
    Then the app refuses automated conversion between legacy 100-point score and CVA affective scale
    And displays message "Cross-protocol score conversion is scientifically invalid and prohibited"

  # ============================================================================
  # S4-013: Digital Physical Form with Verified Official Field Mapping & Provenance
  # ============================================================================
  Scenario: S4-013 - Recording physical green and roast metrics with uncertainty
    Given an active cupping session under "SCA_CVA_CURRENT"
    And sample with blind code "518" is selected for Physical Assessment
    When the evaluator inputs physical metrics:
      | Field                 | Value           | Unit    | Uncertainty | Evidence Status |
      | Whole Bean Agtron     | 58.4            | Gourmet | +/- 0.5     | MEASURED        |
      | Ground Bean Agtron    | 65.2            | Gourmet | +/- 0.5     | MEASURED        |
      | Quakers Count in 100g | 1               | count   | exact       | OBSERVED        |
      | Moisture Percentage   | 10.8            | percent | +/- 0.2     | MEASURED        |
      | Sieve Screen 18+      | 72.5            | percent | +/- 0.5     | MEASURED        |
    Then the physical assessment records the exact Agtron readings and quaker tally
    And records moisture as 10.8% with meter uncertainty +/- 0.2%
    And if moisture is not measured, the field displays "NOT_MEASURED" and never defaults to 0.0%

  # ============================================================================
  # S4-014: Digital Descriptive Form & 221-Descriptor Wheel Integration
  # ============================================================================
  Scenario: S4-014 - Descriptive assessment with 221-descriptor wheel integration
    Given an active cupping session under "SCA_CVA_CURRENT"
    And sample with blind code "824" is active in the Descriptive Form
    When the evaluator sets the following intensity ratings:
      | Attribute              | Intensity | Scale Min | Scale Max |
      | Fragrance Dry          | 4.2       | 0.0       | 5.0       |
      | Fragrance Aroma Crust  | 4.5       | 0.0       | 5.0       |
      | Fragrance Crust Break  | 4.3       | 0.0       | 5.0       |
      | Flavor                 | 4.4       | 0.0       | 5.0       |
      | Aftertaste             | 3.8       | 0.0       | 5.0       |
      | Acidity                | 4.6       | 0.0       | 5.0       |
      | Sweetness              | 4.2       | 0.0       | 5.0       |
      | Mouthfeel              | 3.9       | 0.0       | 5.0       |
    And selects acidity character types "CITRIC" and "MALIC"
    And selects mouthfeel texture "SILKY"
    And opens the integrated 221-descriptor Sensory Wheel
    And selects descriptors:
      | Descriptor ID             | Name          | Category | Subcategory       | Color Hex |
      | fruity.citrus.lemon       | Lemon         | Fruity   | Citrus            | #FED330   |
      | fruity.stone_fruit.peach  | Peach         | Fruity   | Stone Fruit       | #FA8231   |
      | floral.floral.jasmine     | Jasmine       | Floral   | Floral            | #A55EEA   |
      | sweet.brown_sugar.honey   | Honey         | Sweet    | Brown Sugar       | #F7B731   |
      | indo_tropical.durian      | Durian        | Fruity   | Indo Tropical     | #F1C40F   |
    Then all 5 descriptors are attached to the sample descriptive record
    And each descriptor displays its taxonomy category, color chip, and accessibility label
    And the Descriptive Form displays NO total score or quality points

  # ============================================================================
  # S4-015: Digital Affective Form Decoupled from Descriptive Observations
  # ============================================================================
  Scenario: S4-015 - Affective hedonic evaluation separated from descriptive intensity
    Given an active cupping session under "SCA_CVA_CURRENT"
    And sample "824" has high descriptive acidity intensity of 4.8 / 5.0
    When the assessor opens the Affective Form for sample "824"
    Then the affective acidity score defaults to untouched neutral "6.00"
    And no quality score is automatically inferred from descriptive acidity 4.8
    When the assessor records affective hedonic scores:
      | Attribute   | Affective Score |
      | Fragrance   | 8.50            |
      | Flavor      | 8.75            |
      | Aftertaste  | 8.25            |
      | Acidity     | 9.00            |
      | Sweetness   | 8.75            |
      | Mouthfeel   | 8.25            |
      | Overall     | 8.75            |
    And logs 0 taints and 0 faults
    Then the total affective score calculates to 60.25 out of 70.00
    And the descriptive intensity values remain strictly unmodified

  # ============================================================================
  # S4-016 & S4-017: Extrinsic Form Blinding & Premature Unblind Audit
  # ============================================================================
  Scenario: S4-016 - Extrinsic metadata is concealed during active blind cupping
    Given an active cupping session with blind sample "419"
    When the assessor navigates to the Extrinsic tab before locking the session
    Then origin country, farm, producer, altitude, and variety are masked as "[CONCEALED]"
    And a lock badge indicates "Extrinsic metadata concealed to prevent sensory bias"

  Scenario: S4-017 - Host triggers premature reveal with mandatory audit logging
    Given an active cupping session with blind sample "419"
    When the session host taps "Reveal Extrinsic Metadata"
    Then the app presents a confirmation dialog warning of sensory bias
    And prompts for mandatory operator pseudonym and unblinding reason
    When the host submits pseudonym "Host Maria" and reason "Pre-auction trade sample client verification"
    Then the extrinsic metadata is revealed
    And an audit event "UNBLINDED_PREMATURELY_AUDITED" is permanently recorded
    And the audit record contains timestamp, operator "Host Maria", and reason "Pre-auction trade sample client verification"
    And all future exported reports prominently display the premature unblinding warning badge

  # ============================================================================
  # S4-018: Guided Cupping Stepper & Timing
  # ============================================================================
  Scenario: S4-018 - Protocol step-by-step cupping workflow and temperature reminders
    Given an active cupping session under "SCA_CVA_CURRENT"
    When the assessor initiates the "Guided Cupping Stepper"
    Then Step 1 guides dry grounds fragrance evaluation
    When hot water is poured and the assessor taps "Start Timer"
    Then a 4:00 minute countdown begins
    When the timer reaches 0:00 (4:00 elapsed)
    Then the app triggers a crust break reminder with haptic vibration
    And instructions display "Break crust with 3 gentle stirs using cupping spoon and evaluate aroma burst"
    When the timer advances to 8:00 (~70°C)
    Then the app prompts "Round 1: Hot Cupping (~70°C) - evaluate fragrance, aroma, flavor, aftertaste"
    When the timer advances to 12:00 (~60°C)
    Then the app prompts "Round 2: Warm Cupping (~60°C) - peak acidity, sweetness, and balance"
    When the timer advances to 18:00 (~40°C)
    Then the app prompts "Round 3: Cool Cupping (~40°C) - body texture, clean cup, defects, sweetness persistence"

  # ============================================================================
  # S4-019: Autosave Local Drafts, Interruption Recovery & Undo
  # ============================================================================
  Scenario: S4-019 - Crash recovery and undo action history
    Given an assessor is actively rating sample "305"
    And adjusts flavor intensity to 4.3 and adds descriptor "Jasmine"
    When the Android operating system kills the application process due to memory pressure
    And the user relaunches Coffee Creator
    Then the exact draft session is restored
    And sample "305" retains flavor intensity 4.3 and descriptor "Jasmine"
    When the assessor accidentally deletes descriptor "Jasmine"
    And taps "Undo"
    Then descriptor "Jasmine" is immediately restored to the active descriptor list

  # ============================================================================
  # S4-020: Mobile Ergonomics & Tactile Barista UI
  # ============================================================================
  Scenario: S4-020 - Verifying mobile ergonomics and tactile invariants
    Given an assessor is holding a mobile device one-handed during cupping
    When inspecting the cupping interface
    Then all interactive buttons, chips, and sliders have touch targets >= 48dp
    And primary actions are positioned in the bottom 40% thumb-zone
    And all numeric scores render with tabular numbers ("tnum")
    And card borders render with 1px hairline width and alpha 0.40f
    And button press triggers a tactile scale compression of 0.97f

  # ============================================================================
  # S4-021: 5-Cup Multi-Cup Evaluation Grid & Defects
  # ============================================================================
  Scenario: S4-021 - Multi-cup evaluation grid with defective cup deduction
    Given sample "602" has 5 evaluation cups
    When the cupper inspects the 5-cup grid
    And marks Cup 1, Cup 2, Cup 4, and Cup 5 as "CLEAN"
    And marks Cup 3 as "DEFECTIVE" with:
      | Defect Classification | Defect Character | Intensity | Description               |
      | TAINT                 | MOLDY            | 3.0       | Musty cellar mold in cup 3 |
    Then the Uniformity score records 4 clean cups out of 5 (8.0 / 10.0)
    And the Clean Cup score records 4 clean cups out of 5 (8.0 / 10.0)
    And the defect deduction calculates as 2.0 points for 1 taint cup
    And Cup 3 is visually flagged with an amber defect indicator in the multi-cup grid

  # ============================================================================
  # S4-022: Field Validation & Strict Protocol Calculations
  # ============================================================================
  Scenario Outline: S4-022 - Protocol input bounds validation
    Given an active cupping form under "<protocol>"
    When the user enters value "<value>" for field "<field>"
    Then the validation result is "<result>"

    Examples:
      | protocol        | field                 | value | result  |
      | SCA_CVA_CURRENT | fragranceIntensity    | 5.1   | INVALID |
      | SCA_CVA_CURRENT | fragranceIntensity    | 4.2   | VALID   |
      | SCA_CVA_CURRENT | flavorAffectiveScore  | 5.75  | INVALID |
      | SCA_CVA_CURRENT | flavorAffectiveScore  | 8.25  | VALID   |
      | SCA_CVA_CURRENT | brewWaterTemperature  | 105.0 | INVALID |
      | SCA_CVA_CURRENT | brewWaterTemperature  | 93.0  | VALID   |

  # ============================================================================
  # S4-023: Cryptographic Lock & Revision Amendment History
  # ============================================================================
  Scenario: S4-023 - Locking session with SHA-256 seal and creating versioned amendments
    Given all samples in session "CVA-2026-001" are fully evaluated
    When the lead cupper signs the session with pseudonym "Q-Grader Hendra"
    And executes "LOCK_SESSION"
    Then the session state transitions to "LOCKED"
    And a SHA-256 cryptographic seal is generated from the immutable session payload
    And direct editing of all fields is blocked
    When a re-tasting requires adjusting sample "824" aftertaste score
    And the cupper taps "Create Amendment"
    And enters mandatory reason "Re-cupped after water filtration cartridge flush"
    Then a new amendment record "AMENDMENT_R1" is created
    And the previous SHA-256 seal is preserved in the amendment history
    And the session state transitions to "AMENDED"

  # ============================================================================
  # S4-024: Native Data Export (JSON/CSV/PDF) & Compliance Notice
  # ============================================================================
  Scenario: S4-024 - Exporting native cupping report with legal disclaimer
    Given a locked cupping session "CVA-2026-001"
    When the user exports the session as JSON
    Then the JSON contains "$schema": "https://coffeecreator.app/schemas/cva-cupping-v1.json"
    And contains "protocolVersion": "2024.1"
    And contains "fieldMappingVersion": "1.0"
    And contains "sha256Checksum" matching the locked state seal
    And contains the mandatory legal disclaimer:
      """
      Informed by the public SCA Coffee Value Assessment standard.
      This is an independent application research report and not an official SCA certification.
      """

  # ============================================================================
  # S4-025: Side-by-Side Multi-Coffee Comparison & Confidence
  # ============================================================================
  Scenario: S4-025 - Comparing two cupping samples side-by-side
    Given a session with completed samples "Sample #402" and "Sample #819"
    When the assessor selects both samples for "Side-by-Side Comparison"
    Then the screen displays a multi-axis sensory radar comparing Descriptive axes
    And displays a delta comparison table:
      | Axis       | Sample #402 | Sample #819 | Delta |
      | Flavor     | 4.4         | 4.1         | +0.3  |
      | Acidity    | 4.6         | 3.8         | +0.8  |
      | Sweetness  | 4.2         | 4.5         | -0.3  |
      | Mouthfeel  | 3.8         | 4.2         | -0.4  |
    And each metric displays its epistemic evidence confidence badge ("OBSERVED")

  # ============================================================================
  # S4-026: Historical 2004 Cupping Form Archive & Separation
  # ============================================================================
  Scenario: S4-026 - Legacy 2004 cupping form isolated execution
    Given an assessor opens a historical cupping session under "SCA_2004_LEGACY"
    When evaluating sample "Legacy-01"
    Then the form presents the historic 10-attribute 100-point layout:
      | Attribute   | Score |
      | Fragrance   | 8.25  |
      | Flavor      | 8.50  |
      | Aftertaste  | 8.00  |
      | Acidity     | 8.50  |
      | Body        | 8.25  |
      | Balance     | 8.25  |
      | Uniformity  | 10.00 |
      | Clean Cup   | 10.00 |
      | Sweetness   | 10.00 |
      | Overall     | 8.25  |
    And computes total legacy score of 88.00 points
    And displays a persistent amber warning: "LEGACY 2004 FORM: Do not equate to modern CVA results"
    And disables any automated CVA conversion

  # ============================================================================
  # Coffee Grader / Brew Journal Integration
  # ============================================================================
  Scenario: Coffee Grader 1-click action to log sample directly to Brew Journal
    Given sample "824" has completed CVA descriptive and affective assessments
    And has descriptors "Lemon", "Peach", "Jasmine", "Honey", "Durian"
    And affective total score 60.25 (scaled rating 8.6 / 10.0)
    When the user taps "Log to Brew Journal as Graded Coffee"
    Then a new `BrewJournalEntry` is created in the database
    And `beanName` is set to the revealed bean name or blind code "Sample #824"
    And `brewMethod` is set to "Cupping (SCA/CVA)"
    And `rating` is set to 8.6
    And `tasteNotes` contains ["Lemon", "Peach", "Jasmine", "Honey", "Durian"]
    And `sensoryScores` contains mapped intensity scores
    And `tags` contains ["cupping", "cva", "graded"]
    And the user receives a confirmation toast with a direct link to the journal entry
