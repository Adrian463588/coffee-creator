# BDD Scenarios: Journal & Sensory Anti-Slop Tactile Navigation

@uiux @tactile @anti_slop @journal @sensory
Feature: Anti-Slop Single-Line Tactile Navigation for Journal and Sensory Modules

  Scenario: Journal top navigation displays in a single horizontal scrollable row with tactile feedback
    Given the user navigates to the "Journal" screen
    Then all 6 navigation tabs ("Brews & Logs", "Espresso Dial-In", "Compare", "Analytics & Streak", "Cafe Cups", "Export & Tools") are hosted within a single horizontally scrollable row
    And none of the navigation tabs wrap to a second or third line
    When the user presses the "Espresso Dial-In" tab chip
    Then the chip undergoes a spring tactile compression to scale 0.96f
    And upon release, the active tab switches to Dial-In with semi-bold typography and primary container color
    And testTag "journal:tab_dialin" remains fully accessible

  Scenario: Journal method filters display in a single horizontal scrollable row
    Given the user is on the "Brews & Logs" journal tab
    Then the method filters ("All", "V60", "Espresso", "AeroPress", "Chemex", "High Rated") are arranged in a single horizontal scrollable row
    And the method filter row does not wrap into multiple vertical lines
    When the user taps "V60"
    Then the active method filter updates to "V60" with tactile micro-depression feedback

  Scenario: Sensory navigation displays in a single horizontal scrollable row with tactile feedback
    Given the user navigates to the "Sensory" screen
    Then all 6 sensory tabs ("Flavor Wheel", "Accessible List", "Cupping Sessions", "Consensus & Radar", "Physical QC & Rest", "Calibration Lab") are hosted within a single horizontally scrollable row
    And none of the sensory tabs wrap to multiple lines
    When the user presses the "Consensus & Radar" tab chip
    Then the chip scales down by 4% with medium bouncy spring damping
    And the active tab switches to Consensus & Radar
    And testTag "sensory:tab_consensus" remains fully accessible

  Scenario: Sensory flavor taxonomy categories display in a single horizontal scrollable row
    Given the user is on the "Flavor Wheel" tab on the Sensory screen
    Then the category filter chips ("All", "Fruity", "Floral", "Sweet", "Nutty/Cocoa", "Spices", "Roasted", "Vegetal", "Sour/Fermented", "Other") are displayed in a single horizontal scrollable row
    And the categories do not form a multi-line staggered cluster
    When the user taps "Floral"
    Then the descriptors update to show floral notes with spring tactile animation
