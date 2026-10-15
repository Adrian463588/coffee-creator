# BDD Acceptance Scenarios

## Scenario 1: Brew Screen Section Progressive Disclosure with Chips
- **GIVEN** the user is on the Brew formulation screen
- **WHEN** the user inspects the section selector
- **THEN** sections (Recipe, Measurements, Ingredients, Tools) are rendered as dedicated chips in a clear horizontal arrangement
- **AND** tapping each chip switches to that section's card without losing draft inputs
- **AND** touch targets meet the >= 48dp accessibility standard.

## Scenario 2: Custom Coffee Bean Input in Recipe Formulation
- **GIVEN** the user wants to brew a non-catalog coffee bean
- **WHEN** the user enters a custom bean name in the custom bean input field
- **THEN** the custom bean name is saved in the draft and created in the local catalog repository
- **AND** the recipe successfully saves referencing this bean without foreign-key or validation violation.

## Scenario 3: Custom Brew Method Input in Recipe Formulation
- **GIVEN** the user is using an alternative apparatus (e.g., "Flair 58", "Origami Dripper", "Kalita 185")
- **WHEN** the user selects or types a custom brew method in the brew method input field
- **THEN** the custom brew method name is stored in the recipe
- **AND** the recipe saves and renders in the journal with the custom method name.

## Scenario 4: Populated Verified Recipe Library and Journal
- **GIVEN** a freshly initialized or upgraded database
- **WHEN** the user opens the Library or Journal
- **THEN** verified world-standard recipes (Hoffmann V60, Kasuya 4:6, AeroPress Championship, 9-Bar Espresso, etc.) are available
- **AND** authentic specialty coffee beans (Ethiopia, Colombia, Panama Geisha, Kenya, etc.) are populated with genuine provenance
- **AND** sensory journal entries with valid measurements, extraction yields, and tasting descriptors are populated.
