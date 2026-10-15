# BDD Acceptance Criteria: Epic S2-E04 — Brew Journal, Espresso Dial-In, and Local Analytics

## S2-039: Detailed Brew Journal Entry
```gherkin
Feature: Detailed Brew Journal Entry (S2-039)
  Scenario: Create a complete manual brew journal entry with refractometry and photo URI
    Given an existing coffee bean "Kenya Kamwangi Washed" with id "bean-kenya-01"
    When the barista logs a brew with dose 15.0g, water 250.0g, duration 195s, TDS 1.35%, beverage 220.0g
    Then the computed extraction yield is exactly 19.80%
    And the computed brew ratio is exactly 1:16.67
    And the photo URI and sensory descriptors persist faithfully.
```

## S2-040: Search and Multi-Dimensional Filter
```gherkin
Feature: Search and Multi-Dimensional Filter (S2-040)
  Scenario: Filter brews by method and minimum rating threshold
    Given multiple brew journal entries across methods V60, Espresso, AeroPress
    When the barista searches with method "V60" and minRating 8.5
    Then only V60 brews with rating >= 8.5 are returned.
```

## S2-041: Side-by-Side Comparison with Deterministic Deltas
```gherkin
Feature: Side-by-Side Two Brew Session Comparison (S2-041)
  Scenario: Compare two pour-over brews with numeric deltas
    Given Brew A (dose 15.0g, water 250.0g, 180s, TDS 1.30%, beverage 220.0g, rating 7.5)
    And Brew B (dose 16.0g, water 250.0g, 205s, TDS 1.42%, beverage 218.0g, rating 8.8)
    When comparing Brew A and Brew B
    Then dose delta is +1.00g, duration delta is +25s, TDS delta is +0.12%, rating delta is +1.30.
```

## S2-042: Espresso Target vs Actual and Dial-In History
```gherkin
Feature: Espresso Target vs Actual Dial-In History (S2-042)
  Scenario: Evaluate espresso shot kinematics against planned targets
    Given target dose 18.0g, target yield 36.0g, target duration 28s
    When actual shot is 18.2g dose, 40.5g yield, 22s duration
    Then yield delta is +4.50g, duration delta is -6s, and actual mean flow rate is 1.84 g/s.
```

## S2-043: One-Variable-at-a-Time (OVAT) Adjustment Suggestions
```gherkin
Feature: One-Variable-at-a-Time (OVAT) Adjustment Suggestions (S2-043)
  Scenario: Recommend grinding finer for sour and fast espresso shot
    Given an espresso brew with actual yield 36.0g in 19 seconds and sour taste
    When evaluated by the OVAT adjustment engine
    Then the recommendation targets "GRIND_SIZE" with direction "FINER".
```

## S2-044: Interactive Charts Data Models & Aggregation
```gherkin
Feature: Interactive Charts Data Models & Aggregation (S2-044)
  Scenario: Aggregate high-frequency BLE scale points into 1-second interval flow series
    Given a stream of raw weight samples spanning 5.0 seconds
    When aggregated into 1-second buckets
    Then 5 uniform data points are produced with median weight and OLS flow rate.
```

## S2-045: Calendar, Brew Streak & Local Monthly Statistics
```gherkin
Feature: Calendar, Brew Streak & Monthly Statistics (S2-045)
  Scenario: Compute active streak with today included
    Given brews on dates: 2026-10-12, 2026-10-13, 2026-10-14, 2026-10-15 (today)
    When computing streak
    Then current streak is 4 days and longest streak is at least 4 days.
```

## S2-046: Brew Template Save/Reuse & Repeat Last Brew
```gherkin
Feature: Brew Template Save/Reuse & Repeat Last Brew (S2-046)
  Scenario: Quick repeat last brew
    Given a completed journal entry with dose 18.0g and water 300.0g
    When triggering "Repeat Last Brew"
    Then a new brew session is populated with identical dose and water.
```

## S2-047: CSV/JSON Export & Import Conflict Preview
```gherkin
Feature: CSV/JSON Export & Import Conflict Preview (S2-047)
  Scenario: Detect conflict during JSON backup restoration
    Given local record with hash "hash-aaa" and incoming record with hash "hash-bbb"
    When inspecting import conflict preview
    Then the record is marked as conflicting and requires user resolution.
```

## S2-048: Beanconqueror Export Importer via Schema Mapping
```gherkin
Feature: Beanconqueror Export Importer (S2-048)
  Scenario: Map Beanconqueror export JSON to domain models
    Given a valid Beanconqueror JSON export
    When DocumentImporter parses the payload
    Then CoffeeBean and BrewJournalEntry entities are created with exact units and mapped ratings.
```

## S2-049: Cafe Cup Journal (Manual Entry, Zero Remote Maps)
```gherkin
Feature: Cafe Cup Journal (S2-049)
  Scenario: Log an artisan espresso at a local roastery
    When recording a cafe cup with cafeName, drinkName, price, and rating
    Then the cafe entry is persisted locally without network or map SDK access.
```

## S2-050: Manual Optional Caffeine Consumption Estimate
```gherkin
Feature: Manual Optional Caffeine Consumption Estimate (S2-050)
  Scenario: Estimate caffeine intake from single-origin Arabica brew
    Given dose 18.0g of 100% Arabica coffee
    When computing caffeine estimate
    Then nominal estimate is 183.6 mg with uncertainty range [172.8 mg - 194.4 mg]
    And prominent scientific disclaimer is displayed.
```
