# BDD Scenarios: Epic S2-E01 (Coffee Bean, Ingredient & Equipment Foundation)

## Feature S2-001: Bean Agronomic CRUD & Provenance
```gherkin
Scenario: Create coffee bean record with complete agronomic metadata
  Given the barista operates offline in airplane mode
  When the barista creates a bean with roaster "Sey Coffee", name "Worka Sakaro", origin "Ethiopia", process "WASHED", roastDate "2026-10-01", roastLevel "LIGHT"
  Then the bean record is saved with unique id
  And all agronomic and roast fields match the input values exactly.
```

## Feature S2-002: Bag Inventory Lifecycle
```gherkin
Scenario: Auto-deduct bean inventory on brew completion
  Given a bean bag with status "ACTIVE", package mass 250.0g, and remaining mass 180.0g
  When a brew session consumes 18.0g of this bean
  Then remaining mass is updated to 162.00g
  And the bag status remains "ACTIVE".

Scenario: Transition bag status to FINISHED on final dose depletion
  Given a bean bag with status "ACTIVE" and remaining mass 15.0g
  When a brew session consumes 15.0g of this bean
  Then remaining mass is updated to 0.00g
  And bag status automatically transitions to "FINISHED".

Scenario: Prevent brew deduction from a FROZEN bag
  Given a bean bag with status "FROZEN" in the freezer vault
  When a brew attempts to deduct 20.0g from this bag
  Then the deduction is rejected with error "FROZEN_BAG_CANNOT_BREW".
```

## Feature S2-003: Roast-Rest Calculator
```gherkin
Scenario: Calculate freshness state for light roast washed coffee
  Given a LIGHT roast WASHED bean roasted on "2026-10-01"
  When evaluated on "2026-10-04" without freezing
  Then effective rest days is 3
  And freshness state is "TOO_FRESH_DEGASSING".

Scenario: Freezing pauses the aging clock
  Given a LIGHT roast NATURAL bean roasted on "2026-09-01"
  And placed in freezer on "2026-09-05" (4 days aged)
  And thawed on "2026-10-05" (frozen for 30 days)
  When evaluated on "2026-10-10"
  Then effective rest days is 9
  And freshness state is "PEAK_FLAVOR".
```

## Feature S2-004: Grinder Calibration
```gherkin
Scenario: Register manual conical burr hand grinder with clicks
  When the barista registers a grinder with brand "Comandante", model "C40 MK4", unit "CLICKS", burrType "CONICAL", diameter 39.0mm
  Then the grinder is saved with burr profile "39.0mm CONICAL STAINLESS_STEEL".
```

## Feature S2-005: Equipment Catalog
```gherkin
Scenario: Register dripper with material and flow parameters
  When the barista adds equipment category "DRIPPER", brand "Hario", model "V60 Ceramic 02"
  Then the dripper is cataloged under category "DRIPPER".
```

## Feature S2-006: Water Mineral Profile
```gherkin
Scenario: Convert raw mineral ppm to SCA Total Hardness and Alkalinity
  Given raw water ion measurements Ca2+ 20.00 ppm, Mg2+ 10.00 ppm, HCO3- 48.77 ppm, Na+ 5.00 ppm
  When water profile is formulated
  Then Total Hardness is exactly 91.13 ppm CaCO3
  And Total Alkalinity is exactly 40.00 ppm CaCO3
  And calculated TDS is 83.77 ppm.
```

## Feature S2-007: Food-Grade Ingredient Catalog
```gherkin
Scenario: Register safe food-grade ingredient with density and allergens
  When the barista adds ingredient "Oat Milk Barista Edition" with density 1.03 g/mL and allergens "Gluten, Oats"
  Then ingredient is stored as food-grade verified.

Scenario: Reject non-food grade chemical addition
  When the barista attempts to add ingredient with foodGradeVerified false
  Then domain validation blocks registration with "NON_FOOD_GRADE_FORBIDDEN".
```

## Feature S2-008: Bag Offline OCR
```gherkin
Scenario: Parse bag label text into structured bean draft
  Given a bag photo URI "content://media/external/images/101"
  When offline OCR text containing roaster, origin, process, and roast date is parsed
  Then structured draft is extracted with confidence scores
  And requires user confirmation before saving.
```

## Feature S2-009: Bulk Import/Export & Conflicts
```gherkin
Scenario Outline: Import with conflict resolution strategy
  Given an existing bean "Worka Sakaro" with id "bean-101"
  And incoming import payload containing bean with id "bean-101"
  When import executes with strategy "<strategy>"
  Then database reflects <action>.

  Examples:
    | strategy         | action                                        |
    | OVERWRITE        | existing bean updated in place                |
    | KEEP_BOTH_RENAME | existing bean preserved, new bean renamed     |
    | SKIP_DUPLICATE   | incoming record discarded, existing untouched |
```

## Feature S2-010: Offline Search & Favorites
```gherkin
Scenario: Filter catalog by multiple tags and favorites
  Given catalog with beans having tags and favorite flags
  When filtered by favorite and tag
  Then matching items are returned instantly offline.
```

## Feature S2-011: Green Coffee Physical QC
```gherkin
Scenario: Evaluate specialty grade green coffee lot
  Given a green sample with moisture 10.80%, aw 0.54, 0 Cat 1 defects, 3 Cat 2 defects
  When evaluated
  Then total defect points is 0.60
  And lot is marked as Specialty Grade Eligible.
```

## Feature S2-012: Roast Batch Curves & Blends
```gherkin
Scenario: Compute Development Time Ratio (DTR) and validate blend sum
  Given a roast batch with duration 600s and first crack 480s
  When metrics are calculated
  Then DTR is exactly 20.00%.
```
