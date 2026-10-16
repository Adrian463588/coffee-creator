Feature: Tactile Settings and Personalization Anti-Slop Invariants
  As a specialty coffee professional
  I want a precision industrial lab settings interface
  So that status telemetry is unambiguous, chips provide tactile feedback, and cards have concentric hairline borders.

  Background:
    Given Coffee Creator is running in local offline mode
    And the barista navigates to "Settings" or "Personalization"

  @anti_slop @degradation_badge
  Scenario: Degradation status banner guarantees single-line layout and concentric geometry
    Given the "Subsystem Degradation Status" card is displayed
    Then the status badge renders as a full-width telemetry banner below the section title
    And the badge container uses concentric corner radius of 6dp with a 1px hairline outline
    And the badge text enforces maxLines = 1, softWrap = false, and TextOverflow.Ellipsis
    And the badge height does not exceed 32dp, preventing bloated vertical bubble deformation

  @telemetry @data_density
  Scenario Outline: Subsystem telemetry renders in equal-weighted tabular status pills
    Given the Subsystem Degradation Status card is visible
    When the status of "<subsystem>" is evaluated
    Then the indicator is enclosed in a TelemetryStatusPill with a 6dp rounded surface
    And the text uses typography with fontFeatureSettings "tnum"
    And an indicator dot reflects semantic active/inactive state
    Examples:
      | subsystem | active_label | inactive_label |
      | LLM       | Active       | Manual         |
      | OCR       | Active       | Manual         |
      | BLE       | Connected    | Offline        |

  @tactile @haptics
  Scenario Outline: Tactile micro-depression on chips and buttons
    Given an interactive component "<component>" with testTag "<test_tag>" is visible
    When the barista taps "<component>"
    Then it depresses to exactly 0.97f scale using medium bouncy spring physics
    And triggers a subtle "TextHandleMove" haptic feedback
    And preserves a minimum touch target of 48dp
    Examples:
      | component             | test_tag                   |
      | Language Toggle (EN)  | settings:lang_en           |
      | Language Toggle (ID)  | settings:lang_id           |
      | Diagnostics Refresh   | settings:refresh_diag      |
      | Diagnostics Export    | settings:export_diag       |
      | Backup Export (SAF)   | settings:export            |
      | Backup Restore (SAF)  | settings:restore           |

  @hairline @concentric
  Scenario Outline: Concentric 1px hairline borders on settings cards
    Given the settings screen is rendered
    Then card "<card_tag>" has shape RoundedCornerShape(14.dp)
    And a 1px border stroke with outlineVariant color at 0.40f opacity
    Examples:
      | card_tag                  |
      | settings:language_card    |
      | settings:units_card       |
      | settings:degradation_card |
      | settings:diagnostics_card |
      | settings:help_card        |
