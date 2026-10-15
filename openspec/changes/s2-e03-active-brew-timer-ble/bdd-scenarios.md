# BDD Acceptance Scenarios: Epic S2-E03 — Active Brew Timer, BLE and Live Feedback

## Feature: Monotonic Brew Timer Engine (S2-027)
```gherkin
Scenario: Maintain exact monotonic elapsed time across backgrounding and device sleep
  Given an active brew session with recipe "V60 3-Pour"
  When the barista starts the timer at monotonic timestamp 10000 ms
  And the app is moved to background for 15000 ms of real time
  And returns to foreground at monotonic timestamp 25000 ms
  Then the timer displays elapsed time "00:15.0" exactly
  And no wall-clock time alteration can advance or rewind the brew duration.
```

## Feature: Brew Step Execution Guidance (S2-028)
```gherkin
Scenario: Step progression displaying current, next pour and cumulative target
  Given a recipe with 3 steps: Bloom (50g/45s), Pour 1 (100g/30s, cum 150g), Pour 2 (100g/45s, cum 250g)
  When the brew session is on step 1 with 40.0g poured
  Then current step is "Bloom", target is 50.0g, and next step preview is "Pour 1"
  When elapsed time advances past 45 seconds
  Then current step automatically transitions to step 2 "Pour 1"
  And remaining water to cumulative target displays 110.0g.
```

## Feature: Session Flow Control & Persistence Recovery (S2-029)
```gherkin
Scenario: Pause and resume without temporal drift
  Given an active timer running at 20.0s
  When the barista taps "Pause"
  And waits 10 seconds of wall-clock time
  And taps "Resume"
  Then the elapsed timer resumes immediately from 20.0s without jumping forward.

Scenario: Interrupted session recovery from state snapshot
  Given an active brew on step 2 with 120.0g cumulative weight and 65 seconds elapsed
  When the session snapshot is serialized and restored
  Then the restored session retains step 2, elapsed time 65 seconds, and 120.0g weight.
```

## Feature: Sensory Cues & Display Management (S2-030)
```gherkin
Scenario: Trigger haptic cues and screen-awake flag
  Given audio cues and haptic feedback are enabled
  When step remaining duration reaches 3, 2, and 1 seconds
  Then countdown warning triggers
  And when active brewing is running, screen-awake flag is active.
```

## Feature: Automatic Journal Logging (S2-031)
```gherkin
Scenario: Auto-log brew to Journal on finish with post-brew editing
  Given a completed brew of recipe "Kenya Nyeri V60" with 195s elapsed and 250.0g water
  When the barista finishes the brew session
  Then a new Journal entry is created linking recipeId and actual duration
  And the barista can edit tasting notes or measured TDS later in the Journal.
```

## Feature: Bluetooth Scale Abstraction & Simulated Driver (S2-032)
```gherkin
Scenario: Simulated scale provider for offline and headless testing
  Given a SimulatedBleScaleDriver with initial weight 0.0g
  When connected to the brew session
  Then connection state transitions to READY
  And simulated weight samples stream at 10 Hz.
```

## Feature: Weight & Rolling Flow Rate Derivation (S2-033)
```gherkin
Scenario: Calculate flow rate via rolling least-squares regression
  Given a stream of scale samples: 0.0g@0ms, 1.0g@200ms, 2.0g@400ms, 3.0g@600ms, 4.0g@800ms, 5.0g@1000ms
  When evaluated at 1000 ms
  Then the computed rolling flow rate is 5.0 g/s (±0.05 g/s)
  And sample is appended to the session time-series buffer.
```

## Feature: Scale Commands & Threshold Automation (S2-034)
```gherkin
Scenario: Weight-triggered auto-start
  Given brew session is armed for auto-start with zero initial weight
  When kettle water touches the coffee bed producing weight 0.8g
  Then the session automatically transitions to RUNNING from 0 seconds.

Scenario: Automatic espresso flow-stop detection
  Given an active Espresso brew running for 22.0 seconds with yield 34.5g
  When flow rate drops below 0.20 g/s for 1.5 seconds
  Then the runner automatically triggers session completion.
```

## Feature: Transient Scale Artifact Rejection (S2-035)
```gherkin
Scenario: Ignore negative weight dip during V60 carafe swirl
  Given an active pour-over at 150.0g cumulative weight
  When the barista lifts the carafe to swirl causing weight drop > 5g in < 250ms
  Then the artifact filter flags VESSEL_REMOVAL
  And the filtered weight holds steady at 150.0g until carafe is returned.
```

## Feature: Target-Weight Proximity Alerts (S2-036)
```gherkin
Scenario Outline: Trigger progressive proximity states as target is approached
  Given step cumulative target is 250.0g
  When current filtered weight reaches <current_weight>g
  Then proximity state is "<expected_state>".

  Examples:
    | current_weight | expected_state   |
    | 235.0          | NORMAL           |
    | 242.0          | APPROACHING_10G  |
    | 248.5          | IMMINENT_2G      |
    | 250.1          | TARGET_MATCHED   |
    | 252.5          | OVERSHOOT        |
```

## Feature: Real-Time & Historical Brew Curve Overlays (S2-037)
```gherkin
Scenario: Overlay actual brew curve against ideal target curve
  Given a recipe with planned pour steps generating an ideal target curve
  When brewing progresses to 60 seconds
  Then the UI computes the real-time root-mean-square error (RMSE) between actual and ideal points.
```

## Feature: Android Ongoing Notification Model (S2-038)
```gherkin
Scenario: Ongoing notification model displays synchronized timer and controls
  Given an active brew session
  When notification model is generated
  Then it provides title, current step, elapsed time, current mass, and actions.
```
