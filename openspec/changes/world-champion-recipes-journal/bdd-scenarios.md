# BDD Scenarios: World Champion Recipes in Journal

## Feature: World Champion & Signature Recipe Catalog

### Scenario: Browsing and Filtering World Champion Recipes
  Given the barista opens the Journal screen
  When the barista selects the "World Recipes" tab
  Then 76 curated coffee recipes are displayed
  And category filter chips "All", "Competition", "Signature", and "Traditional" are visible
  When the barista filters by "Competition"
  Then only championship recipes with verified podium metadata are shown
  And each card displays a verification status badge with color coding

### Scenario: Direct 1-Tap Logging to Brew Journal
  Given the barista is viewing the "Tetsu Kasuya 4:6 Method" recipe card
  When the barista clicks "Log to Journal"
  Then a new BrewJournalEntry is created with dose 20.0g, water 300.0g, and method "Pour Over (V60)"
  And the entry is tagged with "champion-catalog", "competition", and "4:6"
  And a tactile haptic confirmation is triggered
  And the entry appears immediately in the "Brews & Logs" timeline

### Scenario: Arming a Champion Recipe to the Active Brew Timer
  Given the barista is viewing the "Martin Wölfl Orea V4 Apex" recipe
  When the barista clicks "Brew on Timer"
  Then the active timer session is initialized with 4 pour steps (60g, 70g, 70g, 70g)
  And the total brew target is set to 270.0g at 93°C
  And the app navigates smoothly to the Timer console

### Scenario: Saving a Champion Recipe as a Reusable Template
  Given the barista is viewing "James Hoffmann Ultimate V60"
  When the barista clicks "Save as Template"
  Then a new Recipe revision is added to the user's Recipe Composer library
  And the ordered pour steps, grind settings, and equipment are preserved
