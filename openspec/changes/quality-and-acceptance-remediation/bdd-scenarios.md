# BDD scenarios: quality and acceptance remediation

## Static-analysis gate

```gherkin
Feature: Keep production Kotlin static analysis actionable

  Scenario: Resolve Detekt findings without weakening rules
    Given the checked-in Detekt configuration has maxIssues set to zero
    When the production source is analyzed with the configured Gradle task
    Then Detekt reports zero weighted findings
    And no rule, threshold, exclusion, or baseline was weakened to obtain that result

  Scenario: Preserve behavior during style cleanup
    Given a Kotlin file contains a user-visible string or sourced catalog value
    When its formatting or length is corrected
    Then the exact displayed or stored value remains unchanged
    And the relevant regression test passes
```

## Requirement evidence gate

```gherkin
Feature: Distinguish requirement acceptance from aggregate unit tests

  Scenario: Record an executed requirement case
    Given a master case is linked to a requirement and a test assertion
    When the exact test target executes successfully and its report is archived
    Then only that case receives a passing execution status
    And the evidence records the command, environment, report, and assertion

  Scenario: Preserve unexecuted device acceptance
    Given a case requires ADB, instrumentation, SAF provider behavior, BLE, or physical sensory validation
    And the user has instructed that ADB device testing must not run
    When host unit tests pass
    Then the device case remains NOT_RUN or BLOCKED
    And no aggregate JVM result marks the 221 requirements or 297 cases complete
```
