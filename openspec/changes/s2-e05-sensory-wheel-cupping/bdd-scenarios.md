# OpenSpec BDD Scenarios: Epic S2-E05 — Sensory Wheel and Professional Cupping

## 3. Complete Gherkin BDD Scenarios (Stories S2-051 to S2-062)

### S2-051: Hierarchical Searchable Flavor Wheel & Accessible List Alternative
```gherkin
Feature: Hierarchical Searchable Flavor Wheel and Accessible List (S2-051)
  As a professional cupper or visually impaired barista
  I want to navigate and search sensory descriptors hierarchically or via an accessible list
  So that I can log exact flavor attributes without reliance on non-accessible visual wheel graphics.

  Scenario: Hierarchical search finds nested descriptor with color token
    Given a loaded standard flavor taxonomy
    When the user searches for descriptor "Grapefruit"
    Then the system returns category "Fruity", subcategory "Citrus Fruit", descriptor "Grapefruit"
    And the returned descriptor has color token "#F9A825"
    And the accessibility description reads "Grapefruit: vibrant citrus note with gentle pleasant bitterness".

  Scenario: Accessible table view renders with TalkBack headers
    Given the sensory taxonomy containing 110 descriptors across 9 primary categories
    When the user switches to "Accessible List View"
    Then the screen presents a linear list grouped by category headers
    And each entry provides accessible role semantics and touch target >= 48dp
    And no interactive elements depend solely on color hue for identification.
```

### S2-052: Descriptor Reference Calibration Exercises & Food Safety Boundaries
```gherkin
Feature: Descriptor Definitions and Food-Safe Reference Calibration (S2-052)
  As a coffee evaluator calibrating my palate
  I want safe, food-grade chemical reference recipes
  So that I can calibrate sensory perception of acidity, sweetness, and bitterness safely and accurately.

  Scenario: Prepare Citric Acid calibration solution for lemon acidity
    Given an evaluator requests a calibration recipe for "ACIDITY_CITRIC"
    When target volume is set to 1000 mL
    Then the calculated food-grade citric acid mass is exactly 0.50 g
    And the system mandates chemical grade "FOOD_GRADE_FCC"
    And a prominent safety warning indicates "Use only distilled food-grade water; discard after 24 hours".

  Scenario: Enforce strict safety boundary on bitterness reference
    Given a calibration exercise for "BITTERNESS" using Quinine Monohydrochloride
    When the recipe is displayed
    Then target concentration is strictly 0.005 g/L (5 mg/L)
    And maximum daily tasting limit is flagged as 50 mL
    And an explicit contraindication disclaimer states "Warning: Do not consume if pregnant or allergic to cinchona alkaloids".
```

### S2-053: Standard Sensory Sliders & Qualitative Descriptor Notes
```gherkin
Feature: Standard Sensory Sliders and Qualitative Notes (S2-053)
  As a cupper evaluating a cup
  I want standardized continuous/stepped sliders and qualitative note fields
  So that I can capture ordinal intensity and specific descriptive nuances simultaneously.

  Scenario: Record Specialty Q-Scale attributes with qualitative descriptors
    Given an active cupping sheet for "Sample #B1" on Q-scale 6.00 to 10.00
    When the evaluator sets Aroma to 8.25, Acidity to 8.50, Sweetness to 8.75, Body to 8.00, Balance to 8.25
    And adds qualitative descriptors "Bergamot", "Jasmine", "Meyer Lemon"
    And writes note "Bright phosphoric sparkle with tea-like floral finish"
    Then the values persist without floating point drift
    And the attribute scores are strictly validated in step increments of 0.25.
```

### S2-054: Epistemological Tripartition: Human vs Heuristic vs Calibrated Model
```gherkin
Feature: Epistemological Distinction and Provenance Badges (S2-054)
  As a coffee researcher
  I want clear distinction between human observation, heuristic hypothesis, and calibrated prediction
  So that uncalibrated AI assumptions are never confused with empirical cupper truth.

  Scenario: Display human observation with empirical badge
    Given an empirical score sheet submitted by Q-Grader "Q-4981"
    When viewed on the sensory analytics screen
    Then the scores display a green badge labeled "Empirical Cupper Score"
    And the origin is set to "HUMAN_OBSERVATION" with 100% confidence.

  Scenario: Display AI rule-based hypothesis with mandatory uncertainty disclaimer
    Given an automated recipe draft proposing a flavor profile from bean origin
    When the estimated sensory radar is rendered
    Then the profile line is drawn dashed
    And a prominent badge displays "Uncalibrated Heuristic Rule"
    And the label states "Heuristic assumption; validate by observation. No measured laboratory accuracy claimed."
    And the estimate is prohibited from being written to the official cupping journal table.

  Scenario: Display calibrated statistical model with bounded confidence intervals
    Given a calibrated ML regression model predicting acidity from roast Agtron and density
    When the prediction is returned
    Then the value is displayed as "8.20 [7.90 - 8.50] (95% CI)"
    And the badge indicates "Calibrated Statistical Model v1.2".
```

### S2-055: Cupping Sheet Templates (Specialty, Dial-In, QC, Espresso)
```gherkin
Feature: Cupping Sheet Templates (S2-055)
  As a coffee lab manager
  I want tailored templates for Specialty Cupping, Barista Dial-In, Production QC, and Espresso
  So that evaluation formats match the exact operational context.

  Scenario: Calculate 100-point Specialty Cupping score with taints and faults
    Given a Specialty Cupping template with 10 attributes scored at 8.00 each
    And 1 cup exhibits a "Taint" (-2.00) and 1 cup exhibits a "Fault" (-4.00)
    When the final score is calculated
    Then the gross score is 80.00
    And the defect deduction is 6.00
    And the net final Q-score is 74.00
    And the classification is "Commercial / Below Specialty Grade".

  Scenario: Evaluate Production QC pass/fail against target tolerance
    Given a Production QC template with target profile Acidity=8.00, Sweetness=8.50, Body=7.50
    When actual sample consensus is Acidity=8.25, Sweetness=8.40, Body=7.60 with 0 defects
    Then maximum deviation is 0.25 (<= 0.50 tolerance)
    And the production verdict is "PASS".
```

### S2-056: Multi-Sample Cupping Session & Evaluator Identities
```gherkin
Feature: Multi-Sample Cupping Session and Evaluator Identities (S2-056)
  As a head roaster
  I want to create a cupping session with multiple samples and assigned evaluators
  So that each panelist scores independently under local identities.

  Scenario: Initialize session with Lead Cupper and multiple lots
    Given roaster creates session "Weekly Production QC - Week 42"
    When 5 coffee samples are registered and 3 evaluators assigned (1 Lead Cupper, 2 Baristas)
    Then the session status is initialized to "DRAFT"
    And 5 distinct evaluation sheets are prepared per evaluator.
```

### S2-057: Blind, Non-Blind, and Double-Blind Masking with Controlled Reveal
```gherkin
Feature: Sensory Masking and Controlled Reveal (S2-057)
  As a competition judge or lab coordinator
  I want randomized code tags masking sample identities
  So that evaluators remain unbiased during cupping until an authorized reveal.

  Scenario: Single-blind masking hides bean origins from panelists
    Given a session configured in "SINGLE_BLIND_EVALUATOR" mode
    When panelists open their cupping screens
    Then Sample 1 is displayed only as "Sample #C4" and Sample 2 as "Sample #A7"
    And roaster name, country of origin, and processing method are masked with null strings.

  Scenario: Controlled reveal permanently unmasks sample metadata
    Given a blinded session in status "OPEN" with all sheets submitted
    When the Lead Cupper executes "Reveal All Samples" and confirms the security prompt
    Then all samples transition to isRevealed = true
    And true bean identities ("Ethiopia Yirgacheffe Washed", "Colombia Geisha Natural") are displayed
    And the reveal timestamp is permanently recorded in the session metadata.
```

### S2-058: Session Lifecycle State Machine & Audit Trail
```gherkin
Feature: Session Lifecycle State Machine and Immutable Audit Trail (S2-058)
  As a quality compliance officer
  I want strict session lifecycle gates (DRAFT -> OPEN -> CLOSED -> REOPENED)
  So that cupping scores cannot be modified silently after closing.

  Scenario: Reopening closed session requires reason and writes immutable audit entry
    Given a cupping session in status "CLOSED" with locked scores
    When the Lead Cupper triggers "Reopen Session" with reason "Transcription typo on Sample #C4 Acidity"
    Then the session status becomes "REOPENED"
    And an immutable audit entry is appended with editorId, timestamp, and justification reason
    And when Acidity is changed from 7.50 to 8.50, previous score 7.50 is archived in the audit snapshot.
```

### S2-059: Multi-Evaluator Consensus Analytics & Descriptor Overlap
```gherkin
Feature: Multi-Evaluator Consensus Analytics (S2-059)
  As a sensory panel leader
  I want mean, median, standard deviation, agreement index, and descriptor overlap matrix
  So that I can identify panel calibration drift and consensus flavor notes.

  Scenario: Compute panel consensus statistics and flag outlier evaluator
    Given 4 evaluators score Acidity for "Sample #A1" as: 8.50, 8.50, 8.75, 6.50
    When consensus analytics engine executes
    Then sample mean is 8.06 and median is 8.50
    And sample standard deviation is 1.05
    And the Inter-Rater Agreement Index is 0.00 (< 0.50 acceptable threshold)
    And evaluator scoring 6.50 is flagged as an "OUTLIER".

  Scenario: Calculate descriptor overlap matrix using Jaccard similarity
    Given Evaluator A logs descriptors ["Peach", "Jasmine", "Bergamot"]
    And Evaluator B logs descriptors ["Jasmine", "Bergamot", "Honey", "Black Tea"]
    When pairwise overlap is calculated
    Then intersection count is 2 and union count is 5
    And Jaccard similarity between Evaluator A and B is exactly 0.40.
```

### S2-060: Offline CSV & Markdown QC Report Generation with Provenance Receipts
```gherkin
Feature: Offline QC Report Generation with Provenance Receipts (S2-060)
  As a quality assurance director
  I want to export complete session reports in offline Markdown and CSV formats
  So that session data is auditable, portable, and verifiable without network connectivity.

  Scenario: Export offline report bundle with SHA-256 fingerprint
    Given a completed cupping session with 4 samples, consensus stats, and audit entries
    When the user requests "Export QC Report"
    Then the system generates a formatted Markdown file and a CSV file locally
    And computes a cryptographic SHA-256 fingerprint over the session JSON payload
    And embeds the fingerprint receipt and app engine version at the footer of the report
    And zero network requests are dispatched.
```

### S2-061: Physical Green & Roasted Coffee QC Integration
```gherkin
Feature: Physical Green and Roasted Coffee QC (S2-061)
  As a green coffee buyer and roaster
  I want to log moisture, water activity aw, density, defect counts, and Agtron roast color
  So that physical quality attributes link directly to cupping samples.

  Scenario: Evaluate 350g green coffee sample for specialty grade eligibility
    Given a green sample with moisture 11.2%, water activity 0.56 aw, bulk density 685 g/L
    And defect count: 0 Primary defects and 3 Partial Black (Secondary defects = 1 full defect equivalent)
    When evaluated by the Physical QC Service
    Then calculated grade is "Specialty Grade 1"
    And moisture and water activity are flagged as "WITHIN_SAFE_STORAGE_LIMITS".

  Scenario: Record roast color Agtron delta
    Given a roasted sample with Agtron Gourmet Whole Bean = 65.0 and Ground = 78.0
    When physical QC is saved
    Then roast color delta is +13.0 Agtron points
    And the roast homogeneity is classified as "MODERATE_ROAST_CORE_DELTA".
```

### S2-062: Roast-Rest Sensory Lifecycle & Degassing Peak Window Tracking
```gherkin
Feature: Roast-Rest Sensory Lifecycle Tracking (S2-062)
  As a barista managing bag freshness
  I want to track multiple cuppings over the degassing lifespan of a roast
  So that I can identify the true peak flavor window and detect staling decline.

  Scenario: Identify too-fresh coffee with carbonic acid interference
    Given a light roast coffee bag roasted 2 days ago
    When the barista logs an observation with cupping score 82.00 and notes carbonic bubbling
    Then the system classifies the phase as "TOO_FRESH_CO2_INTERFERENCE"
    And the rest-curve heuristic predicts peak flavor window between Day 10 and Day 21.

  Scenario: Detect staling decline beyond peak window
    Given a bag roasted 60 days ago stored in an opened valve bag
    When evaluated at score 78.50 with notes "Cardboard, flat acidity, loss of floral aromatics"
    Then the observation phase is recorded as "STALE_OXIDIZED"
    And the system logs the staling onset date for that bean profile.
```

