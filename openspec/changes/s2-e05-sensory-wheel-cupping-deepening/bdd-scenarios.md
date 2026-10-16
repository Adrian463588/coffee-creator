# BDD Scenarios: Epic S2-E05 Deepening — Full Sensory Wheel and Professional Cupping

## Feature: Complete Hierarchical Flavor Wheel Taxonomy (S2-051)
  Scenario: Querying descriptors across 9 primary categories including defects
    Given the default sensory taxonomy is initialized
    When an evaluator queries for "Jasmine" or "Gula Aren" or "Potato Defect"
    Then the system returns exact matches with primary category, subcategory, hex color, and sensory notes
    And the total category count is exactly 9 with 26 subcategories and over 90 descriptors

  Scenario: Accessible TalkBack alternative for visual flavor wheel
    Given an evaluator using a screen reader opens the Accessible List tab
    When inspecting any flavor descriptor card
    Then the card semantics stateDescription contains the full name, sensory cue, and color description
    And the touch target height is at least 48dp

## Feature: Dynamic Cupping Sheet Templates (S2-053, S2-055)
  Scenario: Scoring a sample using the Espresso Sensory Template
    Given an open cupping session with template "ESPRESSO_SENSORY"
    When an evaluator opens the score sample dialog
    Then 6 dynamic attributes are displayed: Crema, Aroma Intensity, Acidity Structure, Sweetness Core, Viscosity, Finish
    And the scale allows values from 1.0 to 10.0 in 0.5 increments
    When scores are submitted, the total score is computed as the exact average of all 6 attributes

  Scenario: Scoring a sample using Specialty 100-Point Protocol with Defects
    Given an open cupping session with template "SPECIALTY_SCA_EQUIVALENT"
    When an evaluator inputs scores averaging 8.5 per attribute across 10 attributes
    And logs 1 taint cup and 1 fault cup
    Then the defect deduction is 6.00 points (1*2 + 1*4)
    And the final total cupping score is exactly 79.00 / 100

## Feature: Interactive Wheel Descriptor Picker in Cupping Dialog (S2-051, S2-056)
  Scenario: Selecting descriptors via Flavor Wheel Picker instead of free typing
    Given an evaluator scoring sample "Sample #A1"
    When the evaluator taps "Pick from Flavor Wheel"
    And selects "Fruity" -> "Citrus Fruit" -> "Grapefruit" and "Stone Fruit" -> "Peach"
    Then "Grapefruit" and "Peach" are added to the sample's descriptor list
    And the selected descriptors are visible as colored chips

## Feature: Multi-Evaluator Consensus & IRAI Index (S2-059)
  Scenario: Computing panel consensus and inter-rater agreement
    Given 3 evaluators have scored "Sample #A1"
    When the session consensus is computed
    Then the arithmetic mean, median, standard deviation, and IRAI are calculated per attribute
    And evaluators with deviations greater than 2 sigma are flagged as outliers
    And the pairwise Jaccard similarity matrix reflects shared descriptors between cuppers

## Feature: Cryptographic QC Report Export (S2-060)
  Scenario: Generating offline report with SHA-256 fingerprint
    Given a completed cupping session with 2 samples
    When the lead cupper taps "Export QC"
    Then a Markdown report and CSV string are generated
    And a valid 64-character SHA-256 hex digest is computed over the entire report text
    And no network connection is used during generation
