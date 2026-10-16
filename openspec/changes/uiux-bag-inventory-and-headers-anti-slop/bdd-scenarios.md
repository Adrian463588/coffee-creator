# BDD Scenarios: UI/UX Anti-Slop Ergonomics & Microinteraction Refinement

@uiux @anti_slop @tactile @bag_inventory @action_headers
Feature: UI/UX Anti-Slop Ergonomics & Microinteraction Refinement

  Background:
    Given the app is launched on an Android device or native Robolectric host
    And the user is in the manual offline coffee workbench

  # ---------------------------------------------------------------------------
  # BAG INVENTORY ERGONOMICS (LIBRARY SCREEN)
  # ---------------------------------------------------------------------------

  Scenario: Bag inventory item displays within encapsulated hairline surface
    Given the user navigates to the "Library" screen
    And selects the "Beans & Bags" tab
    And a bean with at least one inventory bag is displayed
    Then each bag item is rendered inside an encapsulated Surface container
    And the Surface container has a 1px hairline outline border
    And the Surface container has rounded corners with radius 10dp
    And the bag remaining and total mass are formatted with tabular numbers ("tnum")
    And the bag status is displayed in a dedicated status chip

  Scenario: Bag action buttons maintain single-line text and 8dp corner radius
    Given a bag is displayed in the Beans & Bags inventory list
    Then the "Brew -18g" button text is constrained to exactly 1 line with softWrap disabled
    And the "Freeze" or "Thaw" outlined button text is constrained to exactly 1 line
    And both buttons have a corner radius of 8dp
    And neither button expands into a vertical oval or circular bubble
    And both buttons are horizontally aligned on the same vertical center line

  Scenario: Bag action buttons trigger spring tactile micro-depression on press
    Given a bag is displayed with status "OPENED" and remaining mass greater than 18g
    When the user presses the "Brew -18g" button
    Then the button animates its scale to 0.96f with medium bouncy spring damping
    When the user releases the button
    Then the button springs back to scale 1.0f
    And 18g is deducted from the bag inventory

  Scenario: Bag freeze toggle triggers tactile outlined micro-depression
    Given a bag is displayed with status "OPENED"
    When the user presses the "Freeze" button
    Then the outlined button compresses to scale 0.96f
    When released, the bag status transitions to "FROZEN"
    And the button label updates to "Thaw" on a single line

  # ---------------------------------------------------------------------------
  # ACTION HEADERS (JOURNAL & SENSORY)
  # ---------------------------------------------------------------------------

  Scenario: Journal Cafe Cup header prevents horizontal crowding and button wrapping
    Given the user navigates to the "Journal" screen
    And selects the "Cafe Cups" tab
    Then the header title "Cafe Cup Logbook" occupies weighted space with weight 1.0
    And the subtitle "Manual entry · Zero cloud maps" is rendered below the title
    And the action button displays an Add icon with concise text "Add Cup" on a single line
    And the action button has corner radius 8dp without pill/circle balloon distortion
    When the user clicks "Add Cup"
    Then the Log Specialty Cafe Visit form dialog opens

  Scenario: Sensory Cupping Sessions header maintains testTag and single-line button
    Given the user navigates to the "Sensory" screen
    And selects the "Cupping Sessions" tab
    Then the header title "Cupping Sessions" is rendered with weight 1.0
    And the action button with testTag "sensory:btn_new_session" displays "New Session"
    And the button text does not wrap into multiple lines
    And the button retains corner radius 8dp with tactile micro-depression
    When the user clicks "New Session"
    Then the Create Cupping Session dialog opens

  Scenario: Sensory Degassing Curve header renders concise action button
    Given the user is on the "Physical QC & Rest" tab on the Sensory screen
    Then the header displays title "Roast-Rest Kinetics" and subtitle "S2-062 degassing curve & logs"
    And the action button displays "Log Obs" on a single line
    And the action button does not distort into a vertical oval
    When the user clicks "Log Obs"
    Then the Log Degassing Observation dialog opens

  # ---------------------------------------------------------------------------
  # ACCESSIBILITY & FONT SCALE INVARIANTS
  # ---------------------------------------------------------------------------

  Scenario: Extreme font scaling (2.0x) does not cause circular bubble distortion
    Given the system accessibility font scale is set to 2.0x
    When the user views Bag Inventory, Journal Cafe Cups, and Sensory Cupping Sessions
    Then all action buttons maintain corner radius 8dp
    And button texts remain single line with text truncation rather than vertical height expansion
    And touch targets maintain a minimum 48dp bounding box for accessibility
    And header titles truncate gracefully with ellipsis rather than pushing buttons offscreen
