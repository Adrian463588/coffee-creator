# BDD Scenarios: README Visual Documentation Overhaul

Feature: High-Density Visual Documentation and Release Accessibility

  Background:
    Given the Coffee Creator GitHub repository root
    And the latest release artifact "coffee-creator-v1.14.5.apk"
    And the physical Samsung Galaxy S20 Ultra device "RRCN3008VYE" connected via ADB

  Scenario: Direct Download Card Verification
    Given a user visits the GitHub README.md
    When reading the Download & Installation section
    Then the latest version is explicitly labeled "v1.14.5"
    And the APK download URL links directly to "https://github.com/Adrian463588/coffee-creator/releases/download/v1.14.5/coffee-creator-v1.14.5.apk"
    And the documented file size is "117.3 MB" (123,039,143 bytes)
    And the documented SHA-256 checksum is "447ccdde1cafc1799bb6a11af4e4c32a1344a4e11708beea64ec1fc2621a0f09"
    And the zero-data-loss installation command specifies "adb -s RRCN3008VYE install --user 0 -r -d coffee-creator-v1.14.5.apk"

  Scenario: High-Scanability Visual Layout and Screenshot Matrix
    Given a user scrolling through README.md
    When reviewing the visual feature galleries
    Then 12 verified Samsung Galaxy S20 Ultra device screenshots are displayed across 4 themed galleries:
      | Gallery ID | Gallery Name | Screenshots Included |
      | G1 | Workbench & Extraction Lab | s20_ultra_v1145_01_home.png, s20_ultra_v1145_02_brew_recipe.png, s20_ultra_v1145_03_active_timer.png |
      | G2 | Gear Intelligence & Inventory | s20_ultra_v1145_04_grinders_drippers.png, s20_ultra_v1145_05_bag_inventory.png, s20_ultra_v1145_06_champion_recipes.png |
      | G3 | Professional Sensory & Cupping | s20_ultra_v1145_07_sensory_wheel.png, s20_ultra_v1145_08_cva_cupping.png, s20_ultra_v1145_09_sensory_training.png |
      | G4 | Physical QC & System Integrity | s20_ultra_v1145_10_green_grading.png, s20_ultra_v1145_11_panel_calibration.png, s20_ultra_v1145_12_settings_degradation.png |
    And each screenshot uses standardized HTML thumbnail rendering with rounded borders and clear descriptive captions
    And no broken image links exist under "docs/screenshots/"

  Scenario: GitHub Mermaid Diagram Rendering Integrity
    Given the README.md viewed on GitHub web and mobile
    When rendering the User Journey Flowchart and System Architecture Diagram
    Then both diagrams use valid "flowchart TD" syntax
    And all node labels with special characters or parentheses are strictly enclosed in quotes
    And zero raw HTML tags exist inside diagram node definitions
    And both diagrams render without parsing errors

  Scenario: Collapsible Information Density Architecture
    Given voluminous historical data and deep directory listings
    When navigating the README.md
    Then the Historical Release Archive (v1.0.0 to v1.14.4) is contained in a collapsible `<details>` section
    And the full repository directory tree is contained in a collapsible `<details>` section
    And the primary page body remains concise, high-impact, and immediately scannable

  Scenario: Technology Stack and Architectural Boundaries
    Given the Modern Technology Stack section
    When evaluating the documented dependencies
    Then all core frameworks list pinned versions:
      | Component | Framework / Library | Version |
      | Presentation | Jetpack Compose BOM | 2024.09.03 |
      | Language | Kotlin | 2.0.20 |
      | Dependency Injection | Dagger Hilt & KSP | 2.52 / 2.0.20-1.0.25 |
      | Database | AndroidX Room & FTS4 | 2.6.1 |
      | Vector Index | ObjectBox Android | 4.3.1 |
      | Local Inference | llama.cpp JNI | CPU GGUF |
      | Embeddings | ONNX Runtime | 1.21.1 |
      | OCR | Tesseract4Android / PDFBox | 4.9.0 / 2.0.27.0 |
    And the test suite is verified as 322 passing JVM tests
