Feature: Epic C - Sensory Training & Testing Engine (S4-027 to S4-040)

  # ============================================================================
  # S4-027: Blind Triangle: Exactly 3 Samples, 2 Same + 1 Different, 6 Permutations
  # ============================================================================
  Scenario: S4-027 - Balanced generation of all 6 triangle permutations
    Given a sensory trainer initializes a triangle testing session with 6 trials
    And the reference coffee is "Ethiopia Sidama Washed"
    And the contrast coffee is "Ethiopia Sidama Natural"
    When the session engine generates the trial sequence with seed 987654L
    Then exactly 6 trials are created
    And the trials represent all 6 presentation orders: "AAB", "ABA", "BAA", "BBA", "BAB", and "ABB"
    And each trial contains exactly 3 opaque cups
    And each cup has a unique 3-digit randomized label between "100" and "999"
    And no 3-digit label is duplicated within the same session

  # ============================================================================
  # S4-028: Concealed Key & Separation of Blind Labels from Coffee Identities
  # ============================================================================
  Scenario: S4-028 - Correct odd cup remains concealed until submission
    Given an active triangle testing session with trial order "ABA"
    And the blind sample codes for the trial are "382", "714", and "905"
    When the assessor views the trial on their testing screen
    Then the screen displays only the opaque codes "382", "714", and "905"
    And the true coffee identities "Sidama Washed" and "Sidama Natural" are masked as "[CONCEALED]"
    And the correct odd cup index (1 / code "714") is not revealed in the UI state
    When the assessor selects cup "382" with confidence rating 4 and submits
    Then the system evaluates the response as incorrect
    And only then reveals that cup "714" was the odd sample

  # ============================================================================
  # S4-029: Detailed Trial Tracking & Experimental Conditions
  # ============================================================================
  Scenario: S4-029 - Tracking complete trial metadata, washout, and confidence
    Given a trainer configures a triangle testing drill
    When recording the session setup:
      | Field                    | Value                  |
      | Session Seed             | 5544332211L            |
      | Trainer Pseudonym        | Instructor Sarah       |
      | Assessor Pseudonym       | Trainee Alex           |
      | Ambient Room Temperature | 22.5 Celsius           |
      | Water TDS                | 120 ppm                |
      | Palate Washout Interval  | 45 seconds             |
    And the assessor submits trial 1 with choice index 2 and confidence 5
    Then the trial record captures:
      | Field                    | Stored Value           |
      | Submitted Index          | 2                      |
      | Confidence Rating        | 5                      |
      | Washout Interval         | 45 seconds             |
      | Timestamp Epoch          | Captured               |
      | Latency Duration         | Measured ms            |

  # ============================================================================
  # S4-030: Statistics: Binomial Test against p=1/3 with Methodological Warnings
  # ============================================================================
  Scenario Outline: S4-030 - Exact cumulative binomial p-value calculation
    Given an assessor completes a triangle series of <total_n> independent trials
    When the assessor achieves <correct_k> correct answers
    Then the system computes the exact cumulative binomial p-value P(X >= k) for p=1/3
    And the calculated p-value is approximately <expected_p>
    And the result significance at alpha 0.05 is <is_sig_05>
    And the result significance at alpha 0.01 is <is_sig_01>
    And the evaluation includes caveat "<expected_caveat>"
    And the evaluation displays disclaimer "Practice drill only: does NOT confer official SCA or CQI Q-Grader credential."

    Examples:
      | total_n | correct_k | expected_p | is_sig_05 | is_sig_01 | expected_caveat                                                                     |
      | 6       | 5         | 0.0178     | true      | false     | Assumes independent trials: repeated testing incurs sensory adaptation.            |
      | 6       | 2         | 0.6804     | false     | false     | Statistical significance does NOT prove superior cupping ability.                  |
      | 18      | 10        | 0.0433     | true      | false     | Assumes independent trials: repeated testing incurs sensory adaptation.            |
      | 4       | 4         | 0.0123     | true      | false     | Small sample size (n < 6): statistical power is severely constrained.              |

  # ============================================================================
  # S4-031: Five Basic-Taste Identification Practice & Safety Checks
  # ============================================================================
  Scenario: S4-031 - Basic taste solution preparation and chemical safety checks
    Given an apprentice accesses the Basic Taste Reference Preparation module
    When reviewing the reference recipes for all 5 basic tastes
    Then the module lists exactly: Sweet (Sucrose), Sour (Citric Acid), Salty (NaCl), Bitter (Caffeine), Umami (MSG)
    And displays standard concentrations in grams per liter for:
      | Taste  | Substance       | Detection (g/L) | Low (g/L) | Medium (g/L) | High (g/L) |
      | SWEET  | Sucrose         | 2.0             | 6.0       | 12.0         | 24.0       |
      | SOUR   | Citric Acid     | 0.15            | 0.35      | 0.70         | 1.40       |
      | SALTY  | Sodium Chloride | 0.20            | 0.70      | 1.50         | 3.00       |
      | BITTER | Caffeine USP    | 0.05            | 0.15      | 0.30         | 0.60       |
      | UMAMI  | MSG Food Grade  | 0.10            | 0.30      | 0.80         | 1.80       |
    And mandates solvent water specification "Clean, demineralized or odorless filtered water (<30 ppm TDS)"
    And displays mandatory safety warning "Use food-grade FCC/USP ingredients only. Do not ingest concentrated dry reagents."
    And warns that Sucrose solutions must be discarded after 24 hours to prevent microbial growth

  # ============================================================================
  # S4-032: Taste Intensity & Thresholds (Ascending Concentration Series)
  # ============================================================================
  Scenario: S4-032 - Ascending concentration thresholding with non-clinical disclaimer
    Given a sensory calibration session for "SOUR" taste threshold
    When the app presents a 3-AFC ascending concentration series:
      | Step | Concentration (g/L) | User Distinguishes from Water | User Identifies Taste |
      | 1    | 0.05                | No                            | None                  |
      | 2    | 0.10                | Yes                           | None                  |
      | 3    | 0.20                | Yes                           | SOUR                  |
      | 4    | 0.40                | Yes                           | SOUR                  |
    Then the engine computes the Detection Threshold as 0.10 g/L
    And computes the Recognition Threshold as 0.20 g/L
    And the screen displays mandatory disclaimer:
      """
      Educational threshold test only. Does not infer medical or clinical gustatory impairment.
      """

  # ============================================================================
  # S4-033: Olfactory Family Identification & Hidden Answer Key
  # ============================================================================
  Scenario: S4-033 - Blind sniffing drill with physical reference vials
    Given an assessor conducts an olfactory identification drill with 4 physical vials
    When the system assigns blind codes "412", "689", "105", and "833"
    Then the target aroma families remain concealed on the assessor screen
    When the assessor evaluates vial "412" and selects "Enzymatic (Floral)"
    And evaluates vial "689" and selects "Dry Distillation (Spicy)"
    And submits the completed olfactory sheet
    Then the app compares submitted answers against the hidden key
    And displays performance breakdown by aroma family
    And stores custom user vial labels without bundling proprietary copyrighted kit text

  # ============================================================================
  # S4-034: Aroma Memory, Spaced Repetition, Confusion Matrix & Calibration
  # ============================================================================
  Scenario: S4-034 - Updating spaced-repetition intervals and confusion matrix
    Given an assessor reviews aroma vial "ROASTED_HAZELNUT"
    And previous review card had interval 3.0 days and ease factor 2.50
    When the assessor identifies the vial incorrectly as "ALMOND" with confidence rating 4
    Then the confusion matrix increments cell [ROASTED_HAZELNUT, ALMOND] by 1
    And the spaced repetition engine calculates new ease factor 2.36
    And resets interval to 1.0 day for immediate review
    And updates the assessor's Brier score confidence calibration metric

  # ============================================================================
  # S4-035: Organic-Acid Comparative Tastings with Food-Grade Controls
  # ============================================================================
  Scenario: S4-035 - Organic acid comparative evaluation under supervision
    Given a supervised comparative tasting of organic acids in neutral mild brew
    When tasting 3 coded samples dosed with food-grade acids:
      | Blind Code | Target Acid | Dosed Concentration | Food-Grade Verified |
      | 301        | CITRIC      | 0.50 g/L            | Yes                 |
      | 302        | MALIC       | 0.40 g/L            | Yes                 |
      | 303        | PHOSPHORIC  | 0.20 g/L            | Yes                 |
    And supervisor "Q-Instructor Maya" co-signs the session
    When the assessor identifies sample 301 as "Citric - crisp lemon", 302 as "Malic - green apple", and 303 as "Phosphoric - sparkling cola"
    Then the system logs all 3 evaluations as verified practical trials
    And confirms that no lab-grade chemical was utilized
    And includes note stating that perceived acidity in brew is multimodal and cannot be reduced solely to pH

  # ============================================================================
  # S4-036: Roast Defect Recognition Practice with Control Baseline
  # ============================================================================
  Scenario: S4-036 - Roast defect drill requires control sample and notes causal limits
    Given a roast defect recognition flight prepared from lot "Colombia Huila"
    When the trainer sets up the evaluation flight
    Then the flight must include at least one calibrated "CONTROL" sample (optimal roast)
    And includes blind samples for "UNDERDEVELOPED", "BAKED", and "SCORCHED"
    When the assessor identifies blind cup "512" as "BAKED"
    Then the app reveals the roast profile notes (stalled RoR during Maillard)
    And displays the causal inference caveat:
      """
      Sensory profile reflects multi-variable roast dynamics; does not prove single roast flaw without roaster curves.
      """

  # ============================================================================
  # S4-037: Tactile / Mouthfeel Discrimination under Serving Controls
  # ============================================================================
  Scenario: S4-037 - Mouthfeel discrimination with temperature and water controls
    Given a mouthfeel evaluation drill comparing "Paper Filtered" vs "Metal Mesh Filtered"
    When setting up serving conditions:
      | Parameter           | Target Value  | Monitored Status |
      | Serving Temperature | 58.0 Celsius  | CONTROLLED       |
      | Water Composition   | 110 ppm TDS   | CONTROLLED       |
      | Vessel Material     | Glazed Ceramic| UNIFORM          |
    And assessor submits perception: "Sample B displays higher particulate body and oily viscosity"
    Then the trial is recorded with verified serving temperature 58.0°C
    And flags any evaluation conducted outside the 40°C–65°C range as invalid due to temperature numbness

  # ============================================================================
  # S4-038: Blind Paired Comparisons & Ranking Exercises
  # ============================================================================
  Scenario: S4-038 - Paired comparison 2-AFC evaluation distinct from triangle
    Given an assessor performs a 2-AFC paired comparison: "Which sample has higher perceived sweetness?"
    And samples are blinded as "118" and "882"
    When the assessor selects "882"
    Then the evaluation uses null hypothesis p=1/2 instead of p=1/3
    And the data model is typed as ComparativeTrial and isolated from TriangleTrial
    And ranking series of 4 coffees are analyzed with Friedman rank sum ordering

  # ============================================================================
  # S4-039: Personalized Non-Certification Readiness Dashboard
  # ============================================================================
  Scenario: S4-039 - Readiness dashboard displays 6 skill pillars with legal disclaimer
    Given an assessor accesses the "Sensory Readiness Dashboard"
    When loading longitudinal performance history across all completed practical drills
    Then the dashboard displays readiness calibration metrics across 6 pillars:
      | Pillar                  | Completed Practical Trials | Calibration Score |
      | Triangle Discrimination | 24                         | 82.5%             |
      | Basic Taste Thresholds  | 15                         | 90.0%             |
      | Olfactory Recognition   | 36                         | 78.0%             |
      | Organic Acids           | 12                         | 85.0%             |
      | Roast Defects           | 18                         | 75.0%             |
      | Tactile Mouthfeel       | 10                         | 80.0%             |
    And renders a 6-axis radar visualization of skill calibration
    And displays the persistent non-certification disclaimer banner:
      """
      Coffee Creator Sensory Lab provides self-directed practice only. Does NOT confer official SCA or CQI Q-Grader certification.
      """

  # ============================================================================
  # S4-040: Human Observation Mandate (Never Auto-Mark PASS from Quizzes)
  # ============================================================================
  Scenario: S4-040 - Virtual quiz completion does not mark practical drill as PASS
    Given a user completes reading module "EDU-05: Gustatory Anatomy" and scores 100% on the theoretical quiz
    When the system updates the curriculum progress
    Then the theoretical knowledge badge is marked as "COMPLETED"
    But the practical sensory readiness metric for "Basic Taste Thresholds" remains unchanged
    And the app prohibits auto-marking any practical sensory drill as PASS without verified physical tasting or human co-signature
