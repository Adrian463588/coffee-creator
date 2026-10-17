# BDD Scenarios: Complete 221-Descriptor Sensory Wheel & Professional Journal Cupping (Epic S2-E05)

## Feature: Complete 221-Descriptor Hierarchical Flavor Wheel Taxonomy (S2-051)
  Scenario: Querying descriptors across 9 primary categories with official vs local distinction
    Given the default sensory taxonomy is initialized
    When an evaluator queries for "Jasmine" or "Gula Aren" or "Salak" or "Potato Defect"
    Then the system returns exact matches with primary category, subcategory, hex color, and bilingual notes
    And the total descriptor count across all 9 categories is exactly 221
    And exactly 84 descriptors are marked as official wheel references
    And exactly 137 descriptors are marked as expanded local extensions

  Scenario: Bilingual search in Indonesian and English
    Given the default sensory taxonomy is initialized
    When an evaluator queries for "teh hitam" or "jeruk nipis" or "kemangi"
    Then the search returns "Black Tea", "Lime", and "Basil" respectively matching the Indonesian label
    And the TalkBack accessibility string announces both terms clearly

  Scenario: Accessible TalkBack alternative for visual flavor wheel
    Given an evaluator using a screen reader opens the Accessible List tab
    When inspecting any flavor descriptor card
    Then the card semantics stateDescription contains the full name, sensory cue, and color description
    And the touch target height is at least 48dp

## Feature: 16 Cupping Attributes & CVA Descriptive Template (S2-053, S2-055)
  Scenario: Scoring a sample using the 16-Attribute CVA Descriptive Template
    Given an open cupping session with template "CVA_DESCRIPTIVE_16_ATTRIBUTE"
    When an evaluator opens the score sample dialog
    Then 16 sensory attributes are displayed: Fragrance, Aroma, Flavor, Aftertaste, Acidity Intensity, Acidity Character, Sweetness Intensity, Bitterness Intensity, Saltiness Intensity, Body Thickness, Body Texture, Astringency, Balance, Clean Cup, Uniformity, Overall Impression
    And the intensity scales allow continuous values from 0.0 to 15.0
    When scores are submitted, the total score is computed deterministically

  Scenario: Scoring a sample using Specialty 100-Point Protocol with Defects
    Given an open cupping session with template "SPECIALTY_SCA_EQUIVALENT"
    When an evaluator inputs scores averaging 8.5 per attribute across 10 attributes
    And logs 1 taint cup and 1 fault cup
    Then the defect deduction is 6.00 points (1*2 + 1*4)
    And the final total cupping score is exactly 79.00 / 100

## Feature: Multi-Phase Temperature Cupping Observations (S2-053, S2-056)
  Scenario: Logging sensory observations across cupping temperature curve
    Given an evaluator tasting sample "Sample #A1"
    When the evaluator logs an observation at Hot Cupping phase (70°C) with "Jasmine" and intensity 8.0
    And logs an observation at Warm Cupping phase (60°C) with "Peach" and intensity 7.5
    And logs an observation at Cool Cupping phase (40°C) with "Honey" and intensity 9.0
    Then all 3 temperature-phase observations are persisted chronologically on the evaluation sheet

## Feature: 1-Click Log Cupping Sample to Brew Journal (S2-056, S2-039)
  Scenario: Converting a scored cupping sample to a Journal cup entry
    Given a cupping sample with revealed bean "Yirgacheffe Gedeb", roaster "Onyx", and score 88.50
    And the sample has flavor descriptors ["Jasmine", "Peach", "Bergamot"]
    When the evaluator taps "Log to Journal"
    Then a new BrewJournalEntry is created in the user's permanent journal history
    And the journal entry contains bean name "Yirgacheffe Gedeb", brew method "Cupping", rating 8.85, and taste notes ["Jasmine", "Peach", "Bergamot"]

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
