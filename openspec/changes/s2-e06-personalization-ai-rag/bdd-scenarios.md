# OpenSpec BDD Scenarios: Epic S2-E06 — Personalization, Local AI, and RAG Integration

## Feature Scenarios (Stories S2-063 through S2-074)

### S2-063: Offline Personal Palate Quiz and Profile Persistence
```gherkin
Feature: Offline Personal Palate Quiz and Profile Persistence (S2-063)
  Scenario: Calibrate initial palate vector from complete 5-question quiz
    Given the app is running in offline airplane mode
    When the user completes the palate quiz with:
      | Question               | Answer                   |
      | Roast Preference       | LIGHT                    |
      | Acidity Tolerance      | HIGH_ENTHUSIAST          |
      | Sweetness Priority     | CORE_CRUCIAL             |
      | Bitterness Sensitivity | AVERSE                   |
      | Body Preference        | TEA_LIKE_DELICATE        |
    Then the resulting palate vector coordinates are:
      | Dimension  | Value |
      | Roast      | 0.10  |
      | Acidity    | 0.80  |
      | Sweetness  | 0.75  |
      | Bitterness | 0.10  |
      | Body       | 0.15  |
    And the initial evidence confidence weight is 5.0
    And the preference profile persists locally in encrypted app preferences.

  Scenario: User manually edits baseline preference sliders without retaking quiz
    Given an existing saved palate profile
    When the user edits the "Acidity Tolerance" slider to 0.90
    And saves the updated profile
    Then the palate vector acidity coordinate updates immediately to 0.90
    And the prior confidence weight remains intact
    And no network request is dispatched.
```

### S2-064: Bayesian Moving Average Taste Profile Update
```gherkin
Feature: Bayesian Moving Average Taste Profile Update (S2-064)
  Scenario: Qualified cupping session updates palate vector via Bayesian EMA
    Given an initial palate vector of [Roast: 0.50, Acidity: 0.50, Sweetness: 0.50, Bitterness: 0.50, Body: 0.50]
    And cumulative evidence weight of 5.0
    When a cupping session is logged with rating 9.00 and sensory observations:
      | Dimension  | Score (1-10) | Normalized Value |
      | Roast      | 2.00         | 0.11             |
      | Acidity    | 9.50         | 0.94             |
      | Sweetness  | 9.00         | 0.89             |
      | Bitterness | 2.00         | 0.11             |
      | Body       | 3.00         | 0.22             |
    Then the session qualifies for palate update because rating 9.00 >= 7.50
    And the observation weight is 0.667
    And the updated acidity coordinate shifts towards the observation
    And the new cumulative evidence weight becomes 5.667
    And the qualified session counter increments to 1.

  Scenario: Sub-threshold session is rejected from modifying palate vector
    Given an active palate profile with qualified session count 4
    When a brew journal entry is submitted with rating 6.50
    Then the session is rejected from updating the Bayesian palate profile
    And the palate vector coordinates remain unchanged
    And the session count remains exactly 4.
```

### S2-065: Explainable Local Bean and Recipe Recommendations
```gherkin
Feature: Explainable Local Bean and Recipe Recommendations (S2-065)
  Scenario: Rank inventory beans with peak degassing and gear alignment
    Given a user palate profile favoring high acidity and light roast
    And an inventory containing:
      | Bean Name             | Roast Date    | Acidity Score | Roast Level |
      | Ethiopia Yirgacheffe  | 12 days ago   | 0.90          | LIGHT       |
      | Colombia Huila Dark   | 45 days ago   | 0.30          | DARK        |
      | Kenya Nyeri Fresh     | 2 days ago    | 0.95          | LIGHT       |
    And the user has a V60 brewer in equipment inventory
    When inventory recommendations are requested for brew method "V60"
    Then "Ethiopia Yirgacheffe" is ranked #1 with composite score > 0.90
    And the explanation text confirms "Matched with palate profile"
    And the explanation confirms "Peak flavor window"
    And "Kenya Nyeri Fresh" is penalized by degassing factor due to excess CO2
    And "Colombia Huila Dark" is penalized by oxidative staling decay.
```

### S2-066: OVAT Brewing Feedback Loop Single Adjustment
```gherkin
Feature: OVAT Brewing Feedback Loop Single Adjustment (S2-066)
  Scenario: Fast sour brew triggers grind adjustment finer holding all else constant
    Given a previous V60 brew with 15.0g dose, 250.0g water, 93.0C temp, 18 clicks, target 2m35s, actual 2m10s
    When the user submits sensory feedback "SOUR_UNDER_EXTRACTED"
    Then the engine diagnoses fast channel under-extraction
    And the proposed adjustment is strictly "Grind Size from 18 to 17 clicks"
    And the expected drawdown delta is "+20s"
    And dose (15.0g), water (250.0g), and temperature (93.0C) remain strictly unchanged.

  Scenario: Normal duration sour brew triggers temperature increase
    Given a previous Kalita Wave brew with actual duration matching target
    When the user logs sensory feedback "SOUR_UNDER_EXTRACTED"
    Then the engine diagnoses thermodynamic under-extraction
    And proposes adjusting strictly "Water Temperature by +2.0°C"
    And grind size and ratio remain identical to the previous brew.
```

### S2-067: Camera OCR Bean Metadata Parser with Review Gate
```gherkin
Feature: Camera OCR Bean Metadata Parser with Review Gate (S2-067)
  Scenario: Parse bag label text lines into typed coffee bean metadata
    Given OCR recognized text lines with Roaster, Origin, Variety, Process, Roast Date, and Net Weight
    When the OCR line parser processes the lines
    Then the parsed draft extracts Country, Variety, Process, Roast Date, and Weight
    And all extracted field confidences meet threshold
    And the review gate presents pre-filled editable inputs.

  Scenario: Missing or low-confidence fields flag mandatory user review
    Given OCR recognized text with unreadable roast date
    When the line parser finishes execution
    Then roastDateIso is null
    And requiresExplicitUserReview is true
    And saving to local database is disabled until user manually edits.
```

### S2-068: Grounded Local AI Barista Chat
```gherkin
Feature: Grounded Local AI Barista Chat (S2-068)
  Scenario: Barista chat invokes domain math tool during extraction query
    Given an offline chat session with the "Specialty Q-Grader" persona
    When the user asks "I brewed 18g coffee and got 270g beverage with 1.35% TDS. What is my EY?"
    Then the assistant generates a structured tool call for calculate_extraction_yield
    And the domain engine executes the calculation yielding exactly 20.25% EY
    And zero network connections were initiated.
```

### S2-069: Tool-Driven Recipe Generation with Constraints
```gherkin
Feature: Tool-Driven Recipe Generation with Constraints (S2-069)
  Scenario: Assistant generates typed V60 recipe draft for light roast Ethiopian
    Given an allowlisted tool "recommend_recipe"
    When the assistant executes the tool with dose 15.0g and water 250.0g
    Then the domain engine validates ratio 1:16.67 within allowed range
    And sets water temperature and pour steps
    And returns a typed Recipe draft rather than unstructured text.
```

### S2-070: Tool Argument Schema Validation and Write Confirmation
```gherkin
Feature: Tool Argument Schema Validation and Write Confirmation (S2-070)
  Scenario: Reject malformed tool request with negative dose
    Given the tool "calculate_extraction_yield"
    When an LLM attempts calling with doseGrams = -15.0
    Then the schema validator rejects the call with validation error.

  Scenario: Save recipe tool mandates explicit user confirmation modal
    Given a valid recipe generated by the AI barista
    When the tool "save_recipe" is executed
    Then the tool result returns "requiresUserConfirmation = true"
    And the recipe is NOT committed to Room database without user confirmation.
```

### S2-071: RAG Document Chunk Citation and Strict Abstention
```gherkin
Feature: RAG Document Chunk Citation and Strict Abstention (S2-071)
  Scenario: Grounded answer cites local document page and chunk
    Given an indexed document in local storage
    When the user queries a factual coffee question supported by the document
    Then the assistant answer includes exact citation "[DocumentTitle, page X, chunk Y]".

  Scenario: Strict abstention when evidence score is below threshold
    Given an ungrounded or speculative question with no matching documents
    When hybrid retrieval yields low relevance
    Then the RAG engine triggers strict abstention and refuses to speculate.
```

### S2-072: Barista Persona Presets and Sanitized Controls
```gherkin
Feature: Barista Persona Presets and Sanitized Controls (S2-072)
  Scenario: Selecting Specialty Q-Grader preset formats tone technically
    Given the persona selector
    When the user selects "Specialty Q-Grader"
    Then the active system prompt instructs SCA sensory protocol and physical extraction physics.

  Scenario: Custom persona input is length-capped and injection-sanitized
    Given a user attempting to set a custom system prompt containing prompt injection delimiters
    When processed by PersonaSanitizer
    Then injection delimiters are stripped and length is clamped to 300 characters.
```

### S2-073: Offline Weather Inputs and Altitude Boiling Point
```gherkin
Feature: Offline Weather Inputs and Altitude Boiling Point (S2-073)
  Scenario: Calculate boiling point and clamp brew temperature in Mexico City
    Given an elevation of 2240 meters entered manually by the user
    When the altitude boiling point is computed
    Then the boiling point is calculated as 92.53°C
    And when a recipe requests water temperature 95.0°C
    Then the engine automatically clamps water temperature to 92.03°C (T_boil - 0.50°C)
    And zero network or weather API calls are performed.
```

### S2-074: Food-Grade Ingredient Safety and Allergen Warnings
```gherkin
Feature: Food-Grade Ingredient Safety and Allergen Warnings (S2-074)
  Scenario: Block perfumery aroma oil lacking food-grade certification
    Given an ingredient with foodGrade = false
    When the safety audit is executed
    Then the safety level is flagged as "CRITICAL_NON_FOOD_PROHIBITED"
    And beverage recipe creation is blocked.

  Scenario: Enforce explicit acknowledgment for food-grade allergen additives
    Given a certified food-grade Hazelnut extract with declared allergen "Tree Nuts"
    When the recipe is prepared
    Then the safety level returns "WARNING_ALLERGEN_PRESENT"
    And saving requires the user to acknowledge allergen presence.
```
