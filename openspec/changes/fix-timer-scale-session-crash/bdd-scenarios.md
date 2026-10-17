# BDD Gherkin Specification: `fix-timer-scale-session-crash`

```gherkin
Feature: Zero-Crash Resilience for Active Brew Chronometer, BLE Scale Connection, and Recipe Timer
  As a specialty coffee barista
  I want the timer, chronometer, and scale connections to function flawlessly on fresh installs and unnamed drafts
  So that formulation workflows never crash or corrupt active sessions.

  Background:
    Given Coffee Creator is running on Android
    And the application has no prior saved recipes (clean install state)

  @reliability @scale @zero_crash
  Scenario: Fresh install / blank recipe draft -> Connect Scale succeeds without crash
    Given the barista has not entered a recipe name in the draft
    And mutableState draft name is empty ""
    And the saved recipe library is empty
    When the barista taps "Connect Scale" with testTag "timer:connect_scale"
    Then the application does not crash
    And the simulated BLE scale driver connects successfully
    And a valid BrewSessionState is initialized using synthesized draft name "Draft Pour Over (V60)"
    And the scale connection status becomes "READY"
    And incoming scale weight samples stream into the active session

  @reliability @chronometer @zero_crash
  Scenario: Fresh install / blank recipe draft -> Start Session succeeds and arms chronometer
    Given the barista navigates directly to the Brew screen
    And the current brew session is uninitialized
    And the draft recipe name is blank ""
    When the barista taps the primary session toggle with testTag "timer:toggle"
    Then the application does not crash
    And a resilient brew session is initialized with valid non-empty brew steps
    And the session status transitions to "RUNNING"
    And the chronometer accumulates elapsed seconds smoothly on each tick

  @reliability @recipe_timer @tactile
  Scenario: Recipe tab -> Start timer and Reset timer succeed with unnamed draft
    Given the barista is on the Recipe Composer screen
    And the draft name field is empty
    When the barista taps "Start timer" with testTag "recipe:toggle_timer"
    Then the button depresses to 0.97f scale with medium bouncy spring physics
    And triggers a tactile haptic click
    And the timer starts running without throwing IllegalArgumentException
    When the barista taps "Reset timer" with testTag "recipe:reset_timer"
    Then the session resets to 00:00 elapsed time
    And the timer running state becomes false

  @reliability @fallback @corrupt_draft
  Scenario: Corrupt / malformed draft quantities gracefully fall back to verified default V60 recipe
    Given the recipe draft contains invalid dose "-15.0" and malformed water "invalid_number"
    And the saved recipe library is empty
    When initBrewSession is triggered
    Then the draft validation failure is caught gracefully
    And the session resolves to the guaranteed default V60 recipe with 15.0g dose and 250.0g water
    And the brew session is armed with 2 valid pour steps (Bloom 50g, Main Pour 200g)
    And no uncaught exception is thrown

  @reliability @scale @tare
  Scenario: Tare command on uninitialized session safely initializes session at zero mass without error
    Given the brew session is currently null
    When the barista taps "Tare (0.0g)" with testTag "timer:tare_scale"
    Then the scale driver issues a tare command
    And a new brew session is initialized with currentWeightGrams = 0.0 and currentFlowRateGps = 0.0
    And the application remains responsive without null pointer or unhandled state errors
```
