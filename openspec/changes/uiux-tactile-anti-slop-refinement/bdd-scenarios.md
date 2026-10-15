# BDD Scenarios: UI/UX Tactile Industrial & Precision Coffee Lab

## Feature: Single-Row Horizontal Scrollable Category Navigation
  As a barista or roaster navigating the Library catalog
  I want a single-row horizontally scrollable category bar with recognizable icons
  So that vertical screen estate is conserved and touch targets remain accessible and responsive

  Scenario: Browsing catalog categories horizontally
    Given the user is on the "Library" catalog tab
    When the user inspects the category selector
    Then all 6 categories ("Beans & Bags", "Grinders & Gear", "Water Chemistry", "Ingredients", "Green & Roast QC", "Import / Export") appear in a single horizontally scrollable row
    And each category chip has a dedicated leading icon and at least 48dp touch height
    When the user taps on "Water Chemistry"
    Then the chip responds with a subtle tactile compression animation
    And the view switches to display water recipes and mineral profile builders

## Feature: Unified Precision Laboratory Brew Chronometer
  As a barista executing a pour-over or espresso recipe
  I want an integrated digital chronometer and live scale telemetry console
  So that elapsed time, weight, flow rate, and control buttons are unified in a single high-contrast instrument cluster

  Scenario: Active brew telemetry display and tactile controls
    Given a recipe is armed or actively brewing
    When the user views the "Timer" section
    Then the chronometer displays elapsed time formatted with tabular monospace digits
    And the live weight in grams and flow rate in g/s are clearly visible in the telemetry cluster
    And a live pulse indicator animates when the timer is actively running
    And all automated test tags ("timer:display", "timer:step_card", "timer:scale_card", "timer:toggle", "timer:finish_and_log") remain present and responsive

  Scenario: Tactile primary action button interaction
    Given the brew session is ready or paused
    When the user presses the primary "Start Session" / "Resume Session" button
    Then the button visually responds with tactile spring physics (scale 0.96f)
    And the timer starts or resumes monotonic elapsed time tracking
