# BDD Scenarios: Tactile Industrial & Precision Coffee Lab UI/UX Invariants

Feature: Tactile Industrial & Precision Coffee Lab UI/UX Invariants
  As a professional barista and sensory researcher
  I want physical micro-feedback, concentric geometries, and high tabular data density
  So that interactions are tactile, reliable under bench vibration, and free of AI-slop layout jitter.

  Background:
    Given the Coffee Creator application is running in local offline mode
    And the display follows the Precision Coffee Lab design system

  @microinteraction @haptics
  Scenario Outline: Tactile micro-depression and haptic click on interactive targets
    Given an interactive component "<component>" is displayed on screen
    When the barista touches and depresses "<component>"
    Then the component scales down to exactly 0.97f using medium bouncy spring physics
    And the device triggers a subtle "TextHandleMove" haptic confirmation
    When the barista releases the touch
    Then the component springs back to 1.0f scale without layout shift or frame stall
    Examples:
      | component           | test_tag                   |
      | Primary CTA Button  | home:new                   |
      | Scale Tare Button   | timer:tare_scale           |
      | Filter Chip         | brew:section:Recipe        |
      | Dropdown Selector   | form:method                |
      | Navigation Tile     | home:nav:AI Chat           |

  @geometry @concentric
  Scenario: Concentric nested corner radius mathematical alignment
    Given a container card with outer radius R_outer and internal padding P
    When child elements or badges are rendered inside the container
    Then the inner radius R_inner is computed strictly as max(0, R_outer - P)
    And no arbitrary mismatched inner corner curves are rendered
    And the card boundary is rendered with a 1px solid hairline outline at 0.40f alpha

  @data_density @anti_slop
  Scenario Outline: Precision laboratory tabular figures without jitter
    Given the active brew chronometer or formulation workbench is visible
    When the telemetry value for "<metric>" updates rapidly
    Then the displayed number is formatted with fontFeatureSettings "tnum"
    And proportional digit widths do not cause horizontal layout trembling
    And the accompanying label enforces single-line constraints with maxLines 1
    Examples:
      | metric            | unit  | format     |
      | Elapsed Timer     | mm:ss | %02d:%02d  |
      | Live Scale Mass   | g     | %.1f g     |
      | Rolling Flow Rate | g/s   | %.2f g/s   |
      | Extraction Yield  | %     | %.2f %     |
      | Brew Ratio        | 1:X   | 1:%.1f     |

  @accessibility @ergonomics
  Scenario: Minimum touch target and text wrapping safety
    Given any interactive button, filter chip, or dropdown selector
    When rendered under standard or 2.0x accessibility font scale
    Then its vertical hit target is at least 48dp
    And label text enforces maxLines = 1 with TextOverflow.Ellipsis
    And no button deforms into an asymmetric circular bubble
