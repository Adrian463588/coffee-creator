# BDD Scenarios: Sprint 03 — Manual Brewing & Grinder Intelligence

## Feature: Grinder Intelligence (GR-01 .. GR-12)

### GR-01: 44-Model Grinder Catalog
- **Scenario: Search and browse grinder catalog**
  - **Given** The user opens the Grinder catalog in Equipment Workbench
  - **When** The catalog is loaded
  - **Then** Exactly 44 grinder models are available
  - **And** Hand grinders (e.g. Comandante C40, 1Zpresso J-Ultra) are classified as HAND
  - **And** Electric grinders (e.g. Breville Smart Grinder Pro, Fellow Ode Gen 2, Timemore Sculptor) are classified as ELECTRIC
  - **And** Searching "1Zpresso" returns all 1Zpresso models

### GR-02: Grinder Model Variants & Burrs
- **Scenario: Distinguish variants and burr geometry**
  - **Given** The user views Comandante C40
  - **When** Viewing variants
  - **Then** Standard (30 µm/click) and Red Clix (15 µm/click) are distinct options
  - **And** Settings are never conflated between variants

### GR-03: Zero Calibration Wizard
- **Scenario: Safe zero calibration flow**
  - **Given** The user calibrates a 1Zpresso or Comandante hand grinder
  - **When** Following the Zero Calibration Wizard
  - **Then** Safe instructions warn against overtightening burrs
  - **And** The user sets true zero point offset with timestamp
  - **And** Past brew logs retain their original historical calibration snapshot

### GR-04: Typed Grind Settings & No Implicit Conversion
- **Scenario: Typed grind setting validation**
  - **Given** Grinder setting input
  - **When** Entering 24 clicks for Comandante C40
  - **Then** It is typed as CLICK
  - **And** When switching to Fellow Ode, the app requires DIAL_NUMBER
  - **And** The app never implicitly translates 24 clicks into a Fellow Ode dial setting

### GR-05: Manufacturer Evidence Overlay
- **Scenario: Verified manufacturer vs raw user range overlay**
  - **Given** Grinder setting guidance
  - **When** Viewing Comandante C40 for V60
  - **Then** Verified manufacturer starting range (18-24 clicks) is displayed with HIGH evidence
  - **And** Raw community or user ranges are labeled USER_SUPPLIED_UNVERIFIED without overwriting

### GR-06: Grinder Profile Management
- **Scenario: Saving owned grinder profile**
  - **Given** User configures my equipment
  - **When** Saving an owned 1Zpresso K-Ultra with "Burr in good condition, zeroed at -1 click"
  - **Then** The profile is saved locally and selectable in brew composer

### GR-07: Grind Range Finder
- **Scenario: Recommending starting point based on method and dripper**
  - **Given** User selects Comandante C40 and Hario V60
  - **When** Requesting starting grind range
  - **Then** Recommendation indicates 18-24 clicks with method guidance
  - **And** If dripper is Hario Mugen, note indicates slightly coarser starting point

### GR-08: Dial-In Assistant (Reversible Experiments)
- **Scenario: Dial-in guidance from brew feedback**
  - **Given** A logged V60 brew with taste feedback "sour / astringent / fast drawdown"
  - **When** Dial-in assistant is consulted
  - **Then** Suggests a single reversible variable change (e.g. grind 1-2 clicks finer)
  - **And** Explains extraction physics reason without making deterministic flavor promises

### GR-09: Grind History & Search
- **Scenario: Logging grinder revision with brew**
  - **Given** A completed brew session
  - **When** Saved to Journal
  - **Then** The grinder model, burr variant, and typed setting are permanently linked
  - **And** History can be filtered by grinder model

### GR-10: Grinder Compatibility Guard
- **Scenario: Warning when grinder unsuitable for method**
  - **Given** User selects Fellow Ode Gen 2 or 1Zpresso ZP6 Special
  - **When** Setting brew method to Espresso
  - **Then** A clear compatibility warning is displayed: "Not recommended for espresso (Filter-focused burr geometry)"

### GR-11: Grind Measurement Metadata
- **Scenario: Optional particle size / sieve data**
  - **Given** User possesses physical Kruve sieve measurements
  - **When** Recording grind measurement (e.g. 400µm - 800µm)
  - **Then** Metadata is stored as user-measured with timestamp
  - **And** App never auto-fabricates particle size from clicks alone

### GR-12: Calibration Transfer (JSON Export/Import)
- **Scenario: Exporting and importing grinder calibration profile**
  - **Given** A configured grinder profile
  - **When** Exporting as JSON
  - **Then** File contains schemaVersion, grinderModelId, zeroOffset, burrType, and timestamp
  - **And** Re-importing into a fresh install restores profile losslessly

---

## Feature: Manual Brewing & Equipment Intelligence (BR-01 .. BR-18)

### BR-01: 26-Dripper Catalog
- **Scenario: Comprehensive dripper catalog import**
  - **Given** Equipment catalog
  - **When** Filtering by drippers
  - **Then** All 26 drippers from the blueprint are present
  - **And** Missing flow or grind data is labeled UNKNOWN without fabrication

### BR-02: Brewer Geometry & Valve States
- **Scenario: Flow behavior for hybrid valve dripper**
  - **Given** Hario Switch or NextLevel Pulsar
  - **When** Selecting brewer
  - **Then** Flow behavior models valve CLOSED (immersion / 0 flow) vs valve OPEN (percolation)

### BR-03: Filter Compatibility Engine
- **Scenario: Filter compatibility lookup**
  - **Given** Kalita Wave 155
  - **When** Checking filter options
  - **Then** Kalita 155 wave paper is flagged COMPATIBLE
  - **And** Standard conical 02 paper is flagged INCOMPATIBLE or requires adapter/negotiator

### BR-04: Manual Brew Composer
- **Scenario: Composing manual recipe**
  - **Given** User builds manual pour over recipe
  - **When** Selecting bean, grinder (Comandante C40), dripper (V60), dose (15g), water (250g), temp (94°C)
  - **Then** Valid recipe draft is formed with deterministic 1:16.7 ratio

### BR-05: Ratio & Dose Scaling Math
- **Scenario: Deterministic scaling**
  - **Given** 15g dose / 250g water (ratio 1:16.6667)
  - **When** Scaling dose to 20g
  - **Then** Target water scales deterministically to 333.3g
  - **And** Source recipe remains immutable

### BR-06: Bloom and Staged Pours
- **Scenario: Staged pour calculations**
  - **Given** 20g coffee dose and 300g total water
  - **When** Setting 3 pours: 50g bloom (45s), 150g pulse (45s), 100g final (60s)
  - **Then** Cumulative masses are 50g, 200g, 300g
  - **And** Total duration is 150s (2m 30s)

### BR-07: Offline Brew Chronometer
- **Scenario: Background-resilient timer**
  - **Given** Active brewing session running
  - **When** App transitions to background or phone sleeps
  - **Then** Chronometer reconstructs elapsed time accurately from SystemClock monotonic clock
  - **And** Zero session state is lost

### BR-08: Manual Weigh-Ins Without BLE
- **Scenario: Manual weight entry during or after brew**
  - **Given** Barista brewing without Bluetooth scale
  - **When** Manually entering actual beverage mass (e.g. 248g)
  - **Then** System records 248g as MEASURED, distinct from 250g target

### BR-09: Brew Diagnostics Logging
- **Scenario: Capturing physical brew diagnostics**
  - **Given** Finished brew session
  - **When** Recording observations: drawdown time, clogging (None/Minor/Severe), channeling, bypass
  - **Then** Observations are saved with brew log without fake AI inference

### BR-10: Water & Temperature Profiling
- **Scenario: Water mineral and temperature parameters**
  - **Given** Manual recipe creation
  - **When** Entering water recipe (e.g. SCA Standard 150 ppm TDS, 68 ppm Ca/Mg) and 93°C
  - **Then** Mineral specs are saved with recipe
  - **And** Unspecified fields remain UNKNOWN

### BR-11: Supported Recipe Methods
- **Scenario: Multiple brewing method templates**
  - **Given** Recipe composer
  - **When** Browsing method templates
  - **Then** Supports V60, Kalita, Chemex, Switch, Clever, Origami, French Press, AeroPress, 4:6 Method

### BR-12: Championship Templates
- **Scenario: Verified champion recipe attribution**
  - **Given** Browsing Championship recipes
  - **When** Viewing Tetsu Kasuya 4:6 (WBrC 2016)
  - **Then** Attribution, year, verbatim parameters, and versioned original are preserved

### BR-13: Sensory Feedback Integration
- **Scenario: Linking Roda Rasa descriptors to brew**
  - **Given** Logging brew result in Journal
  - **When** Adding sensory tags (e.g. Jasmine, Peach, Bergamot)
  - **Then** Descriptors are linked from the 221-descriptor vocabulary without modifying past records

### BR-14: Experiment Comparison (A/B)
- **Scenario: Side-by-side comparison**
  - **Given** Two brews of the same bean with different grind settings
  - **When** Comparing in Journal A/B comparison
  - **Then** Shows grind delta, brew time delta, and sensory differences

### BR-15: Local AI Assistant & Typed Tools
- **Scenario: Offline tool execution**
  - **Given** Local AI Barista in airplane mode
  - **When** Prompted to formulate recipe for V60
  - **Then** Uses deterministic Kotlin calculation tools
  - **And** Never hallucinates unverified click conversions

### BR-16: Accessibility & High Contrast
- **Scenario: Screen reader and large text support**
  - **Given** Device with 200% font scale and TalkBack active
  - **When** Navigating brew composer and chronometer
  - **Then** All touch targets are at least 48dp and labels are clearly spoken

### BR-17: Video-Reference Audit
- **Scenario: Video citation verification check**
  - **Given** Features derived from video references
  - **When** Inspected in codebase
  - **Then** Marked PENDING_VIDEO_REVIEW until timestamped evidence is audited

### BR-18: Zero-Loss Migration & Backward Compatibility
- **Scenario: Existing data preservation**
  - **Given** Existing app installed with custom recipes, bags, and cuppings
  - **When** Upgraded to Sprint 03
  - **Then** All existing records, Room DB tables, and UI flows remain 100% accessible
