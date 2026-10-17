Feature: 2026 Tactile Microinteractions and Precision Typography

  Background:
    Given Coffee Creator is running on Android device or native Robolectric host
    And the application theme is active in light or dark mode

  Scenario: Shared Tactile Button Press and Micro-Depression
    Given a user is interacting with any button on the screen
    When the user presses down on "TactileButton", "TactileOutlinedButton", or "TactileFilterChip"
    Then the component scale animates smoothly towards 0.97f using Spring.DampingRatioMediumBouncy
    And when the click triggers, a subtle haptic confirmation is emitted via HapticFeedbackType.TextHandleMove
    And when the user releases, the scale springs back to 1.0f

  Scenario: Concentric Nested Card Geometry and Hairline Border
    Given a "TactileCard" rendered with outer radius 12.dp and internal padding 16.dp
    Then the card border is a crisp 1px stroke with MaterialTheme.colorScheme.outlineVariant alpha 0.40f
    And any nested child surface computes its concentric radius as max(0.dp, 12.dp - 16.dp) = 0.dp
    And when the outer radius is 14.dp with padding 8.dp, the nested radius is 6.dp

  Scenario: Tabular Monospace Figures for Live Telemetry and Timers
    Given the Active Brew Timer is running or CVA cupping scores are being updated
    When the elapsed timer, flow rate (g/s), live mass (g), or cupping score text updates
    Then the Text component styles include fontFeatureSettings = "tnum"
    And numeric characters maintain uniform tabular glyph widths without horizontal layout jitter

  Scenario: Accessible Hit Target for Green Defect Increment and Decrement
    Given the user is on the Green Coffee Grading section
    When inspecting the increment and decrement buttons for Category 1 and Category 2 defects
    Then each interactive button has an accessible touch target of at least 48dp by 48dp
    And tapping either button emits subtle haptic feedback
    And the count text updates with tabular figures ("tnum")

  Scenario: 5-Bowl Multi-Cup Grid Tactile Feedback
    Given the user is cupping coffee on the 5-Bowl Multi-Cup Grid
    When the user taps on any of the 5 cup surfaces
    Then the cup surface performs a spring micro-depression
    And emits a subtle haptic confirmation
    And cycles state from Clean to Taint to Fault
    And updates Clean Cup and Uniformity scores formatted with "tnum"
