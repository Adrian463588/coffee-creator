# BDD Acceptance Scenarios: Epic S2-E02

## Feature: Recipe Lifecycle & Entity Linking (S2-013)
```gherkin
Scenario: Create and version a recipe linked to bean and grinder
  Given an active bean "Ethiopia Yirgacheffe Washed" with id "bean-eth-01" exists
  When the barista creates a recipe named "Morning V60" with dose 15.0g, water 250.0g, method "V60"
  And links bean "bean-eth-01" and equipment "Comandante C40"
  Then the recipe is saved with version 1 and an auto-generated revisionGroupId
  And the recipe is marked active.

Scenario: Archive and duplicate recipe
  Given an active recipe "Morning V60" version 1 with id "recipe-v60-01"
  When the barista duplicates the recipe as "Morning V60 Copy"
  Then a new recipe record is created with version 1 and a new revisionGroupId.
  When the barista archives "recipe-v60-01"
  Then the recipe status is updated to archived.
```

## Feature: Standard Brew Method Support (S2-014) & Custom Stages (S2-015)
```gherkin
Scenario: Validate standard and custom brew methods
  Given a recipe with method "Espresso"
  When dose is 18.0g, water is 36.0g, and pressure is 9.0 bar
  Then the recipe is valid.
  Given a custom brew method named "Hario Switch Hybrid"
  When stages are "Open Percolation", "Closed Immersion", "Drawdown"
  Then the custom method and stages persist correctly in the recipe draft.
```

## Feature: Editable Ordered Pour Steps (S2-016)
```gherkin
Scenario: Configure ordered brew steps with action and valve state
  Given a recipe with dose 15.0g and total water 250.0g
  When the barista adds steps:
    | step | action | duration | targetWater | cumulativeMass | tempC | valve |
    | 1    | Bloom  | 45       | 50.0        | 50.0           | 93.0  | N/A   |
    | 2    | Pour 1 | 30       | 100.0       | 150.0          | 93.0  | N/A   |
    | 3    | Pour 2 | 45       | 100.0       | 250.0          | 93.0  | N/A   |
  Then cumulativeMass is strictly monotonic non-decreasing
  And the sum of targetWater equals 250.0g exactly.
```

## Feature: Exact Pour Scaling Engine (S2-017)
```gherkin
Scenario: Scale four-pour V60 recipe from 300g to 450g water
  Given a recipe with water 300.0g and steps: 50.0g, 50.0g, 100.0g, 100.0g
  When the barista scales total water to 450.0g
  Then scaled steps are 75.0g, 75.0g, 150.0g, 150.0g
  And cumulative masses are 75.0g, 150.0g, 300.0g, 450.0g
  And sum of all scaled steps equals 450.0g exactly.

Scenario: Scale with remainder using Hamilton-Hare apportionment
  Given a recipe with water 250.0g and 3 pour steps: 50.0g, 100.0g, 100.0g
  When the barista scales total water to 333.3g
  Then individual scaled steps sum to exactly 333.3g with zero rounding drift.
```

## Feature: Typed Physical Quantities & Conversion (S2-018)
```gherkin
Scenario: Convert volume to mass with verified density
  Given a volume of 250.0 mL and density of 1.05 g/mL
  When converted to mass
  Then the mass is exactly 262.50 g.
  When converted without density
  Then conversion is rejected with density required error.

Scenario: Convert temperature between Celsius and Fahrenheit
  Given a temperature of 93.0 °C
  When converted to Fahrenheit
  Then the result is 199.4 °F.
```

## Feature: Extraction Yield & TDS Calculation (S2-019)
```gherkin
Scenario: Direct mass balance extraction yield
  Given dose 18.0g, beverage mass 270.0g, measured TDS 1.30%
  When EY is calculated
  Then EY is 19.50%
  And beverage mass is flagged as measured.
```

## Feature: Optical Brix to TDS Conversion (S2-020)
```gherkin
Scenario Outline: Convert Brix to TDS with calibration profile
  Given a refractometer reading of <brix>% Brix
  When converted using profile "<profile>"
  Then the resulting TDS is <expected_tds>%.

  Examples:
    | brix | profile            | expected_tds |
    | 1.53 | Standard Coffee    | 1.3005       |
    | 1.55 | Atago PAL-COFFEE   | 1.3020       |
    | 1.52 | VST LAB Coffee III | 1.3026       |
```

## Feature: Concentrate Dilution & Solution Dosing (S2-021)
```gherkin
Scenario: Dilute cold brew concentrate to ready-to-drink target
  Given 500.0g cold brew concentrate at 4.50% TDS
  When 1000.0g dilution water is added
  Then total mass is 1500.0g and final TDS is 1.50%.
```

## Feature: Food-Grade Aroma Solution Dosage (S2-022)
```gherkin
Scenario: Food-grade aroma dosing with active vs stock distinction
  Given 0.30g food-grade aroma stock (active compound 2.50% w/w) added to 270.0g beverage
  When evaluated
  Then total mass is 270.30g, stock dosage is 0.11% w/w, and active compound is 27.75 ppm.
```

## Feature: Recipe Parameter Validation & Safe Bounds (S2-023)
```gherkin
Scenario: Enforce physical parameter bounds
  When temperature is -5.0°C or 105.0°C, then rejected.
  When dose is 0.5g or 550.0g, then rejected.
  When water is 5.0g or 15,000.0g, then rejected.
  When pressure is negative or exceeds 15.0 bar, then rejected.
```

## Feature: Curated Starter Recipes (S2-024)
```gherkin
Scenario: Verify curated starter recipes catalog
  Given clean database initialization
  Then 10 curated recipes are loaded with complete step lists and source attribution.
```

## Feature: Expert Mode Brew Metadata (S2-025)
```gherkin
Scenario: Record expert dial-in parameters
  Given first drop 6.5s, grinder Comandante 18 clicks, water 70 ppm GH, 35 ppm KH, 20g bypass
  When saved with recipe
  Then all expert fields persist accurately.
```

## Feature: Offline Recipe Sharing (S2-026)
```gherkin
Scenario: Export recipe to JSON and Barista Text Card
  Given an active recipe
  When exported to JSON
  Then valid structured recipe JSON is produced.
  When exported to Text Card
  Then formatted barista recipe summary is produced for Android Sharesheet.
```
