# BDD Scenarios: UI/UX Tactile Microinteraction Full-Max Refinement

## Feature: Tactile Microinteractions & Anti-Slop Geometry

  Scenario: Tactile spring micro-depression and subtle haptic on primary buttons
    Given the user is interacting with any primary action button (e.g., "New recipe", "Save recipe", "Start Session")
    When the user presses down on the button
    Then the button scales down to 0.97f with medium bouncy spring physics
    And an immediate subtle TextHandleMove haptic feedback is dispatched
    And upon release, the button smoothly springs back to 1.0f scale without layout shift

  Scenario: Secondary actions and tactile tonal buttons
    Given the user interacts with secondary action controls (e.g., "Repeat Last", "World (80)", "Export CSV")
    When the user taps the tactile filled tonal button or outlined button
    Then the button exhibits a 0.97f press scale and haptic feedback
    And the button touch target height is at least 48dp

  Scenario: Anti-slop single-line label constraint prevents distorted button shapes
    Given any tactile button, chip, or status badge displayed in compact viewports or with large font scaling
    When rendered with long text labels
    Then the text enforces maxLines = 1, softWrap = false, and TextOverflow.Ellipsis
    And the button geometry never expands vertically into an awkward tall oval or circular bubble blob

  Scenario: Tabular monospace figures eliminate horizontal telemetry jitter
    Given the Active Brew timer or BLE live scale is streaming weight or elapsed seconds
    When values change rapidly (e.g., 00:01 to 00:02, or 12.3 g/s)
    Then numbers are rendered with fontFeatureSettings = "tnum"
    And adjacent labels, units, and buttons remain completely static with zero horizontal layout jitter

  Scenario: Bottom navigation tactile response and hairline division
    Given the application scaffold displays the bottom NavigationBar
    When the user selects a different destination tab (e.g. Brew, Journal, Library)
    Then a subtle TextHandleMove haptic feedback confirms the touch
    And the bottom navigation bar is separated from scrollable content by a 1px hairline border
