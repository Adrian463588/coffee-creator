# BDD Scenarios: E2E ADB Device Verification (Samsung SM-G988B)

@e2e @navigation @layout
Feature: Journey 1 - Core Navigation & Layout Integrity across App Sections
  As a barista operating Coffee Creator on physical hardware
  I want seamless navigation across all 6 core sections
  So that layout elements remain accessible, responsive, and untruncated at 120Hz

  Background:
    Given the Coffee Creator application "com.coffeecreator.app" is installed on device "SM-G988B"
    And the app process is launched and initial database seed is ready

  Scenario: Navigating all 6 primary sections via Bottom Navigation and Menu
    Given the user is on the "Home" screen with testTag "page:Home"
    When the user taps the bottom navigation item "Brew" with testTag "nav:Formulate"
    Then the screen transitions to "page:Formulate" displaying the Brew Workbench
    When the user selects the "Timer" section chip with testTag "brew:section:Timer"
    Then the unified chronometer console with testTag "timer:display" is displayed
    When the user taps the bottom navigation item "Library" with testTag "nav:Library"
    Then the screen transitions to "page:Library" displaying catalog categories
    When the user taps the bottom navigation item "Journal" with testTag "nav:Journal"
    Then the screen transitions to "page:Journal" displaying brew logbooks
    When the user taps the top action menu with testTag "nav:more"
    And the user selects "Sensory" with testTag "nav:Sensory"
    Then the screen transitions to "page:Sensory" displaying the flavor wheel and cupping tabs
    When the user taps the bottom navigation item "Home" with testTag "nav:Home"
    Then the screen returns to "page:Home" without visual artifact or navigation stack corruption

  Scenario: Layout integrity and touch responsiveness under screen rotation
    Given the user is on "page:Formulate"
    When the device orientation is rotated from portrait to landscape
    Then all navigation items remain clickable and touch targets measure at least 48dp in height
    And input fields with tags "form:name", "form:dose", and "form:water" remain visible without text clipping
    When the device is restored to portrait orientation
    Then the layout smoothly adapts without configuration crash or state destruction

---

@e2e @formulation @math
Feature: Journey 2 - Recipe Formulation & Scaling Math
  As a coffee developer designing a brew profile
  I want to input dose, water, and measured extraction variables
  So that brew ratio and extraction yield are computed with mathematical precision

  Background:
    Given the user navigates to "page:Formulate"
    And the "Recipe" section chip "brew:section:Recipe" is selected

  Scenario: Creating recipe draft, calculating ratio, and dynamic recipe scaling
    When the user inputs "S20 QA Geisha Natural" into recipe name field "form:name"
    And the user inputs "18.0" into dose field "form:dose"
    And the user inputs "300.0" into brew water field "form:water"
    Then the ratio readout button "form:ratio" displays "Ratio 1:16.7"
    When the user taps the "1.5x" scaling button with testTag "form:scale:1.5"
    Then the dose field "form:dose" updates to "27.0"
    And the brew water field "form:water" updates to "450.0"
    And the ratio readout button "form:ratio" maintains "Ratio 1:16.7"
    When the user taps the "0.5x" scaling button with testTag "form:scale:0.5"
    Then the dose field "form:dose" updates to "13.5"
    And the brew water field "form:water" updates to "225.0"
    When the user resets dose to "18.0" and water to "300.0"
    Then the values are restored to original baseline

  Scenario: Entering measured refractometer data and verifying Extraction Yield invariant
    Given the recipe has dose "18.0" g and water "300.0" g
    When the user selects the "Measurements" section chip "brew:section:Measurements"
    And the user inputs "270.0" into beverage mass field "form:beverage"
    And the user inputs "1.35" into measured TDS field "form:tds"
    Then the calculated science card displays extraction yield exactly equal to "20.25%"
    And the calculation summary explicitly labels the result as "Calculated ratio 1:16.7"
    When the user taps the "Save" button with testTag "form:save"
    Then the recipe is committed to the local repository as an immutable revision
    And no SQLite constraint violation or data write failure occurs

---

@e2e @inventory @tactile
Feature: Journey 3 - Bag Inventory & Tactile Controls
  As a roastery inventory manager
  I want to track coffee bags, toggle freeze states, and deduct brew doses
  So that bag mass is tracked accurately with tactile button responsiveness

  Background:
    Given the user navigates to "page:Library"
    And the category chip "Beans & Bags" is selected

  Scenario: Toggling bag Freeze/Thaw state and deducting brew mass
    Given an active coffee bag with remaining mass "250.00g / 250.00g" and badge "ACTIVE"
    When the user taps the "Freeze" button with tactile depression
    Then the bag status badge updates to "FROZEN"
    And the freeze indicator displays "❄ Frozen"
    And the button text switches from "Freeze" to "Thaw"
    And the "Brew -18g" button becomes disabled
    When the user taps the "Thaw" button
    Then the bag status badge returns to "ACTIVE"
    And the "Brew -18g" button is re-enabled
    When the user taps the "Brew -18g" button
    Then the remaining mass immediately decrements to "232.00g / 250.00g"
    And the tactile button executes a spring compression animation (scale 0.96f)
    When the user navigates away to "Home" and returns to "Library"
    Then the bag remaining mass persists as "232.00g / 250.00g"

---

@e2e @timer @telemetry
Feature: Journey 4 - Active Chronometer & Live Telemetry
  As a barista pulling a manual shot or pour-over
  I want an integrated digital timer, live flow telemetry, and scale controls
  So that brew execution is monitored with millisecond precision

  Background:
    Given the user navigates to "page:Formulate"
    And the user selects the "Timer" section chip "brew:section:Timer"

  Scenario: Running chronometer, connecting mock scale, and reading flow telemetry
    Given the chronometer console "timer:display" shows elapsed time "00:00" and status "READY / ARMED"
    When the user taps the primary CTA button "timer:toggle" labeled "Start Session"
    Then the live pulse indicator "LivePulseIndicator" starts breathing animation
    And the status readout displays "ACTIVE BREWING"
    And the elapsed time increments monotonically ("00:01", "00:02", ...) using monospace digits
    When the user taps "Connect Scale" with testTag "timer:connect_scale"
    Then the scale badge displays "MOCK SCALE READY"
    When the user taps "Tare (0.0g)" with testTag "timer:tare_scale"
    Then the current mass readout displays "0.0 g"
    When the user feeds simulated weight delta "+25g" with testTag "timer:sim_pour_25"
    Then the current mass increases to "25.0 g"
    And the flow rate readout displays positive telemetry in "g/s"
    When the user taps the primary CTA button "timer:toggle" labeled "Pause Session"
    Then the timer stops incrementing and status switches to "PAUSED"
    When the user taps "Reset" with testTag "timer:reset"
    Then the chronometer resets to "00:00" and status returns to "READY / ARMED"

---

@e2e @journal @cafecup
Feature: Journey 5 - Journal Logbook & Cafe Cup Manual Entry
  As a specialty coffee enthusiast visiting artisan cafes
  I want to manually log cafe drinks with price, roaster, and rating
  So that my coffee journey is preserved locally without cloud dependency

  Background:
    Given the user navigates to "page:Journal"
    When the user selects the "Cafe Cups" tab with testTag "journal:tab_cafecups"

  Scenario: Logging a new cafe cup and asserting persistent list entry
    Given the Cafe Cup logbook view is displayed
    When the user taps the "Add Cup" button
    Then the cafe visit logging form appears
    When the user enters "Onyx Coffee Lab" into cafe name field "cafecup:name"
    And the user enters "Southern Weather Blend" into drink field "cafecup:drink"
    And the user enters "Onyx Coffee" into roaster field "cafecup:roaster"
    And the user enters "5.50" into price field "cafecup:price"
    And the user enters "9.2" into rating field "cafecup:rating"
    And the user enters "Notes of candied walnut, milk chocolate, and plum" into tasting notes field "cafecup:notes"
    And the user taps "Save Cafe Cup"
    Then the form closes and the new entry appears in the cafe cup list
    And the card displays "Southern Weather Blend @ Onyx Coffee Lab"
    And the card displays "Rating: ⭐ 9.2/10" and roaster "Onyx Coffee"
    When the activity is recreated via configuration change
    Then the logged cafe cup entry remains displayed and intact

---

@e2e @sensory @flavorwheel
Feature: Journey 6 - Sensory Cupping & Flavor Wheel Navigation
  As a certified Q-Grader or coffee evaluator
  I want to explore the sensory taxonomy and record formal cupping sessions
  So that sensory evaluations adhere to standardized SCA/CoE protocols

  Background:
    Given the user opens the "Sensory" section with testTag "page:Sensory"

  Scenario: Exploring the flavor wheel taxonomy and filtering descriptors
    When the user selects the "Flavor Wheel" tab with testTag "sensory:tab_wheel"
    Then the flavor wheel search field "sensory:wheel_search" is displayed
    When the user types "Jasmine" into the search field
    Then the filtered descriptor list displays "Jasmine" under category "Floral"
    When the user selects the category filter chip "Fruity"
    Then the descriptors update to show fruit subcategories (Berry, Citrus, Stone Fruit)

  Scenario: Creating a formal cupping session and verifying session card
    When the user selects the "Cupping Sessions" tab with testTag "sensory:tab_sessions"
    And the user taps the "New Session" button with testTag "sensory:btn_new_session"
    Then the "New Cupping Session" dialog opens
    When the user enters "Ethiopia Sidama Benchmark 2026" into session title
    And the user enters "Lab Bench 1" into location
    And the user taps "Create Session"
    Then the dialog closes and a new cupping session card appears
    And the card displays title "Ethiopia Sidama Benchmark 2026"
    And the session status badge displays "OPEN"
    And blind-coded sample evaluation slots are initialized

---

@e2e @health @stability
Feature: Journey 7 - Crash, ANR & StrictMode Logcat Health
  As a mobile security auditor and QA engineer
  I want continuous Logcat monitoring during app operation
  So that uncaught exceptions, ANRs, and memory leaks are completely eliminated

  Scenario: Zero crash, ANR, and StrictMode violation during full journey execution
    Given ADB logcat buffer is cleared via "adb logcat -c"
    When the full suite of user journeys (Navigation, Formulate, Timer, Journal, Sensory, Library) is executed
    Then ADB logcat contains ZERO occurrences of "FATAL EXCEPTION"
    And ADB logcat contains ZERO occurrences of "AndroidRuntime: E"
    And ADB logcat contains ZERO occurrences of "ANR in com.coffeecreator.app"
    And ADB logcat contains ZERO StrictMode disk/network policy violations on main thread
    And the app process memory RSS/PSS remains stable below 250 MB without OutOfMemoryError

---

@e2e @instrumented @compose
Feature: Journey 8 - Automated Instrumented Compose Test Execution
  As a CI/CD automation engineer
  I want to run the on-device Compose instrumented tests via Gradle ADB
  So that regression-free performance is proven on physical ARM64 hardware

  Scenario: Executing connected Android instrumented test runner
    Given the device "RRCN3008VYE" is attached and unlocked
    When the command "./gradlew.bat connectedDebugAndroidTest" is executed
    Then the test suites "ManualJourneyTest" and "BrewSectionTest" execute on device
    And all Compose semantics checks, tag assertions, and recreation tests execute
    And the runner reports 100% tests PASSED with zero failures
    And the JUnit HTML report is generated in "app/build/reports/androidTests/connected/"
