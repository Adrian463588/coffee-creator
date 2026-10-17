Feature: Epic D - Green Coffee Physical Assessment Engine (S4-041 to S4-051)

  # ============================================================================
  # S4-041: Green Lot Catalog & Chain of Custody
  # ============================================================================
  Scenario: S4-041 - Register complete green coffee lot with chain of custody
    Given a green coffee quality manager creates a new green lot entry
    When recording the lot catalog metadata:
      | Field                  | Value                               |
      | Lot Identifier         | LOT-2026-ETH-YIRG-042              |
      | Lot Name               | Yirgacheffe Kochere Grade 1 Micro   |
      | Species                | Coffea Arabica                     |
      | Processing Method      | Fully Washed                       |
      | Origin Country         | Ethiopia                           |
      | Origin Region          | Gedeo Zone, Kochere                |
      | Producer / Washing Stn | Boji Washing Station               |
      | Crop Harvest Year      | 2025/2026                          |
      | Sample Mass (grams)    | 350.0                              |
      | Exporter               | Bole Specialty Coffee Exporters    |
      | Importer               | Nordic Coffee Importers Hamburg    |
      | Warehouse Location     | Port of Hamburg Terminal Shed 4    |
    Then the green lot record is saved with sample mass 350.0 grams
    And the species is verified as "COFFEA_ARABICA"
    And the processing method is verified as "WASHED"
    And the chain of custody captures exporter, importer, and warehouse metadata

  # ============================================================================
  # S4-042: Defect Taxonomy & Standards Attribution
  # ============================================================================
  Scenario: S4-042 - Verify defect taxonomy and standard attribution
    Given the quality manager selects standard "SCA Washed Arabica 2004 Reference"
    When inspecting the standard's physical defect taxonomy
    Then the standard attribution lists:
      | Attribute        | Value                                            |
      | Standard Name    | SCA Washed Arabica 2004 Reference                |
      | Source Document  | SCAA Green Coffee Defect Handbook (2004 edition) |
      | Valid Species    | Coffea Arabica                                   |
      | Valid Processes  | Fully Washed                                     |
    And the Category 1 Primary defects include:
      | Defect Name           | Standard Ratio | Severity        |
      | Full Black            | 1 bean = 1 full| Severe defect   |
      | Full Sour             | 1 bean = 1 full| Severe defect   |
      | Dried Cherry / Pod    | 1 bean = 1 full| Pulping defect  |
      | Fungus Damaged        | 1 bean = 1 full| Mold hazard     |
      | Foreign Matter        | 1 item = 1 full| Physical hazard |
      | Severe Insect Damage  | 5 beans = 1 full| Boring hazard   |
    And the Category 2 Secondary defects include:
      | Defect Name           | Standard Ratio | Severity        |
      | Partial Black         | 3 beans = 1 full| Secondary       |
      | Partial Sour          | 3 beans = 1 full| Secondary       |
      | Parchment Bean        | 5 beans = 1 full| Secondary       |
      | Floater Bean          | 5 beans = 1 full| Secondary       |
      | Immature / Unripe     | 5 beans = 1 full| Secondary       |
      | Withered Bean         | 5 beans = 1 full| Secondary       |
      | Shell / Elephant Ear  | 5 beans = 1 full| Secondary       |
      | Broken / Chipped / Cut| 5 beans = 1 full| Secondary       |
      | Hull / Husk Piece     | 5 beans = 1 full| Secondary       |
      | Slight Insect Damage  | 10 beans = 1 full| Secondary      |

  # ============================================================================
  # S4-043: Defect Recording, Count, Equivalent Weighting, & Photo Attachments
  # ============================================================================
  Scenario: S4-043 - Record defect tally with verified equivalent weighting and photo URI
    Given an assessor inspects a 350.0g sample of washed Arabica under standard "SCA Washed Arabica 2004 Reference"
    When the assessor tallies defects:
      | Defect Type          | Raw Count | Attached Photo URI                    |
      | Partial Black        | 6         | content://coffee.qc/photos/pblack.jpg |
      | Broken / Chipped     | 10        | content://coffee.qc/photos/broken.jpg |
      | Slight Insect Damage | 20        | content://coffee.qc/photos/insect.jpg |
    Then the calculated equivalent full defects are:
      | Defect Type          | Raw Count | Ratio | Equivalent Full Defects |
      | Partial Black        | 6         | 3     | 2.0                     |
      | Broken / Chipped     | 10        | 5     | 2.0                     |
      | Slight Insect Damage | 20        | 10    | 2.0                     |
    And the total Category 2 full defect equivalents equal 6.0
    And each defect record retains its associated evidence photo content URI

  Scenario: S4-043 - Refuse equivalent weighting when using unverified or raw-count standard
    Given an assessor inspects a green coffee lot under standard "CUSTOM_RAW_COUNT_ONLY"
    When the assessor records 15 broken beans
    Then the system displays raw count 15
    And the equivalent weighting is marked as "EQUIVALENCE_UNSUPPORTED"
    And no arbitrary equivalent ratio is invented

  # ============================================================================
  # S4-044: Green Sample Prep, Weighing, & Sieve Distribution (ISO 4150)
  # ============================================================================
  Scenario: S4-044 - Sieve size distribution analysis with mass balance recovery check
    Given a calibrated balance with tare weight 0.0g
    And an initial green coffee test sample mass of 350.0 grams
    When the assessor runs the sample through an ISO 4150 sieve stack for 3 minutes
    And enters the weighed fraction masses:
      | Sieve Screen | Hole Size 64th Inch | Hole Size mm | Measured Mass (g) |
      | Screen 18    | 18                  | 7.14         | 175.0             |
      | Screen 17    | 17                  | 6.75         | 87.5              |
      | Screen 16    | 16                  | 6.35         | 70.0              |
      | Screen 15    | 15                  | 5.95         | 14.0              |
      | Pan (< 15)   | 0                   | 0.00         | 3.5               |
    Then the total recovered mass is calculated as 350.0 grams
    And the mass balance mismatch is 0.0 grams
    And the calculated fraction percentages are:
      | Sieve Screen | Percentage of Total |
      | Screen 18    | 50.0 %              |
      | Screen 17    | 25.0 %              |
      | Screen 16    | 20.0 %              |
      | Screen 15    | 4.0 %               |
      | Pan (< 15)   | 1.0 %               |
    And the cumulative retention for Screen 16 and above is 95.0 %
    And no mass balance warning is triggered

  Scenario: S4-044 - Mass balance mismatch warning triggered when beans are lost
    Given an initial green coffee test sample mass of 350.0 grams
    When the recorded sieve fraction masses sum to 346.5 grams
    Then the mass balance mismatch is 3.5 grams
    And the system triggers a "MASS_BALANCE_MISMATCH_WARNING" indicating spilled beans or scale calibration error

  # ============================================================================
  # S4-045: Moisture Assessment & Calibration Integrity
  # ============================================================================
  Scenario: S4-045 - Valid moisture content measurement with equipment metadata
    Given a moisture meter with model "Sinar AP 6060" and serial number "SIN-2024-8841"
    And the meter calibration date was "2026-08-15"
    When the assessor measures the washed Arabica sample at 21.5 Celsius
    And enters moisture reading 10.8 % with uncertainty ±0.2 %
    Then the moisture record captures:
      | Field                 | Value                 |
      | Moisture Percent      | 10.8 %                |
      | Measurement Method    | DIELECTRIC_CAPACITANCE|
      | Meter Model           | Sinar AP 6060         |
      | Meter Serial Number   | SIN-2024-8841         |
      | Sample Temperature    | 21.5 °C               |
      | Uncertainty           | ±0.2 %                |
      | Is Measured           | true                  |
    And the moisture is verified within the standard range of 9.0% to 13.0%

  Scenario: S4-045 - Unmeasured moisture strictly defaults to NOT_MEASURED
    Given an assessor creates a green QC record without access to a moisture meter
    When no moisture percentage is entered
    Then the moisture percentage is stored as null
    And the UI displays status pill "NOT_MEASURED" in neutral slate
    And the system NEVER defaults moisture to 0.0% or 11.0%
    And any physical grade evaluation notes that moisture is unverified

  # ============================================================================
  # S4-046: Water Activity, Bulk Density, Color, Odor, & Foreign Matter
  # ============================================================================
  Scenario: S4-046 - Complete physical assessment parameters
    Given a green sample with measured water activity and density
    When the assessor records:
      | Parameter            | Value                                   |
      | Water Activity (aw)  | 0.585 at 24.0 °C                       |
      | Bulk Density         | 710.0 g/L (ISO 6669 free-fall cylinder) |
      | Color Observation    | BLUISH_GREEN                            |
      | Odor Observation     | CLEAN_FRESH_VEGETAL                     |
      | Foreign Matter Note  | Zero foreign matter detected            |
    Then the water activity is evaluated as SAFE (below 0.700 threshold)
    And the bulk density is classified as "HIGH_DENSITY_SHB" (>= 700 g/L)
    And the color and odor pass standard organoleptic inspection

  Scenario: S4-046 - Foul or mouldy odor immediately disqualifies sample
    Given a green lot sample with zero physical defects
    When the assessor records odor observation "MOULDY_EARTHY"
    Then the physical evaluation triggers immediate disqualification "ODOR_CONTAMINATION_DISQUALIFIED"
    And the sample cannot be certified as specialty coffee regardless of defect count

  # ============================================================================
  # S4-047: SCA 2004 Washed Arabica Rule Engine & Out-of-Scope Guard
  # ============================================================================
  Scenario: S4-047 - Qualifying Specialty Grade 1 Washed Arabica
    Given a 350.0g sample of Coffea Arabica, Fully Washed
    And Category 1 Full Defects = 0.0
    And Category 2 Secondary Full Defect Equivalents = 3.0 (from 9 partial black beans)
    And measured moisture = 11.2 % (within 9.0% - 13.0%)
    And odor is "CLEAN_FRESH_VEGETAL"
    When the Standard Green Grading Engine evaluates the sample
    Then the resulting grade is "SPECIALTY_GRADE_1"
    And the sample is marked as "isSpecialtyPhysicalEligible = true"
    And the diagnostic notes confirm 0 Cat 1 defects and <= 5 Cat 2 equivalents

  Scenario: S4-047 - Disqualification by a single Category 1 defect
    Given a 350.0g sample of Coffea Arabica, Fully Washed
    And the assessor identifies 1 Full Black bean (Category 1 Primary Defect)
    And Category 2 Secondary Defect Equivalents = 0.0
    And measured moisture = 11.0 %
    When the Standard Green Grading Engine evaluates the sample
    Then the resulting grade is downgraded to "PREMIUM_GRADE_2"
    And the sample is marked as "isSpecialtyPhysicalEligible = false"
    And the evaluation note states "Category 1 defects present (1.0). Disqualified from Specialty Grade 1."

  Scenario: S4-047 - Out-of-Scope guard refuses Robusta or Natural under Washed Arabica standard
    Given a 350.0g green coffee sample with species "COFFEA_CANEPHORA" and process "WASHED"
    When the assessor requests evaluation under "SCA Washed Arabica 2004 Reference"
    Then the engine returns grade "UNDETERMINED_OUT_OF_SCOPE"
    And "isSpecialtyPhysicalEligible" is false
    And the diagnostic note states "Standard only valid for Coffea Arabica washed process. Robusta / Canephora requires CQI Fine Robusta or Buyer Spec."

  # ============================================================================
  # S4-048: Decoupled Physical Grade from Sensory CVA Cupping Outcome
  # ============================================================================
  Scenario: S4-048 - Independence of physical grade and cup score
    Given a green lot with physical grade "SPECIALTY_GRADE_1"
    When the lot is roasted and cupped under CVA yielding an affective score of 76.5 points (woody, past-crop)
    Then the system maintains:
      | Dimension        | Status                         |
      | Physical Grade   | Specialty Grade 1 (Physical)   |
      | Sensory CVA Score| 76.50 (Commercial Tier)        |
      | Joint Specialty  | NOT_QUALIFIED_SPECIALTY        |
    And the system displays banner "Physical grade does NOT guarantee cup quality. Specialty status requires dual independent qualification."

  Scenario: S4-048 - High cup score cannot erase physical Category 1 defects
    Given a green lot with 1 Full Sour bean (Category 1 Defect, Physical Grade 2)
    When the lot cups at 86.0 points on CVA
    Then the system refuses to mark the physical grade as Grade 1
    And displays "Physical Grade: Premium Grade 2 | Sensory Score: 86.00"

  # ============================================================================
  # S4-049: Buyer Specification Contract Pass/Fail Evaluation & Lot Comparison
  # ============================================================================
  Scenario: S4-049 - Evaluate lot against strict Buyer Specification Contract
    Given a Buyer Contract with specifications:
      | Parameter                  | Target Threshold       |
      | Max Category 1 Full Defects| 0                      |
      | Max Category 2 Equivalents | 4.0                    |
      | Moisture Content Range     | 10.0 % to 12.0 %       |
      | Max Water Activity         | 0.620                  |
      | Min Screen 16+ Retention   | 85.0 %                 |
      | Min Bulk Density           | 680.0 g/L              |
    When evaluating a lot with:
      | Parameter                  | Measured Value         |
      | Category 1 Defects         | 0                      |
      | Category 2 Equivalents     | 2.0                    |
      | Moisture Content           | 11.1 %                 |
      | Water Activity             | 0.590                  |
      | Screen 16+ Retention       | 92.0 %                 |
      | Bulk Density               | 705.0 g/L              |
    Then the contract evaluation result is "ACCEPTED"
    And all 6 parameters evaluate to "PASS"

  Scenario: S4-049 - Contract rejection on moisture out of buyer tolerance
    Given the Buyer Contract requiring moisture between 10.0% and 12.0%
    When a lot has moisture 12.6%
    Then parameter "Moisture Content" evaluates to "FAIL"
    And the overall contract status is "REJECTED"
    And the contract report highlights moisture deviation (+0.6% over tolerance)

  # ============================================================================
  # S4-050: Green QC Export with Audit Trail (JSON & CSV)
  # ============================================================================
  Scenario: S4-050 - Export locked green QC report with complete audit trail
    Given a finalized and locked green QC evaluation for lot "LOT-2026-ETH-YIRG-042"
    And the assessor was "Q-Grader Marcus (Cert #4092)"
    When the user exports the green QC assessment to JSON and CSV
    Then the exported JSON payload contains:
      | JSON Field                  | Expected Content                                   |
      | schema_version              | "1.0.0"                                            |
      | lot_id                      | "LOT-2026-ETH-YIRG-042"                            |
      | standard_applied            | "SCA Washed Arabica 2004 Reference"                |
      | assessor_pseudonym          | "Q-Grader Marcus (Cert #4092)"                     |
      | equipment_calibration       | Moisture meter, scale, and aw meter metadata       |
      | evidence_photos             | Array of valid photo content URIs                  |
      | sha256_audit_seal           | Cryptographic 64-character hex hash                |
    And the exported CSV includes standardized tabular columns matching ISO and SCA lab reporting

  # ============================================================================
  # S4-051: Explicit Missing Data Representation
  # ============================================================================
  Scenario Outline: S4-051 - Epistemologically honest display of missing data states
    Given a green coffee sample evaluation
    When field "<field_name>" has condition "<condition>"
    Then the system displays state "<displayed_state>"
    And no fabricated numerical value is displayed

    Examples:
      | field_name       | condition                                | displayed_state   |
      | moisture         | Assessor skipped moisture test           | NOT_MEASURED      |
      | water_activity   | No aw meter available in field lab       | NOT_MEASURED      |
      | historical_grade | Imported lot lacks historical record     | UNKNOWN           |
      | washed_grade     | Robusta sample evaluated on washed Arabica| OUT_OF_SCOPE      |
      | defect_tally     | Entry submitted by apprentice grader     | PENDING_REVIEW    |
