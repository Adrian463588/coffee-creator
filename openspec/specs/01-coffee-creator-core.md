# OpenSpec: Coffee Creator Android Jetpack Compose

## Metadata
- **Target**: Native Android Jetpack Compose App (`coffee-creator`)
- **Scope**: P0, P1, P2 features from blueprint (`PRD.md`, `ARCHITECTURE.md`, `DESIGN.md`, `TASKS.md`)
- **Primary Directive**: 100% offline-first barista workstation with manual-first formulation, deterministic pure Kotlin calculation engine, sensory observation/estimation, local hybrid RAG with citations, typed tool execution, and user-owned SAF asset management.

## Invariants
1. **Offline Independence**: Manual brewing calculator, catalog, sensory tools, and journal must work 100% without an AI model or internet connection.
2. **Measurement vs Prediction Integrity**: Distinct types and labels for instrument-measured (TDS), tool-calculated (Extraction Yield), and model-estimated (Flavor profile) values. No silent fabrication of TDS or deterministic sensory claims.
3. **Deterministic Authority**: Kotlin calculation engine is the sole source of truth for math; LLM cannot override calculation tool output.
4. **Data Isolation & Privacy**: Zero analytics/telemetry SDKs, zero background network calls. Network allowed only during explicit, user-initiated model asset downloads.
5. **Least-Privilege Storage**: User-owned assets managed via Android Storage Access Framework (SAF); no hardcoded filesystem path synthesis from `content://` URIs.

## BDD Acceptance Scenarios

### Feature 1: Deterministic Coffee Science Calculations (FR-03)
- **Scenario: Golden Extraction Yield calculation**
  - **Given** Dose is 18.0g, beverage mass is 270.0g, and measured TDS is 1.30%
  - **When** Calculation engine computes extraction yield
  - **Then** Extraction yield is exactly 19.50%
  - **And** Result includes method label, assumptions, and provenance metadata

- **Scenario: Validation of invalid inputs**
  - **Given** Dose is 0.0g or negative, or beverage mass is negative
  - **When** Calculation engine is invoked
  - **Then** Validation error is returned rejecting invalid inputs

- **Scenario: Brew water-to-coffee ratio calculation**
  - **Given** Dose is 15.0g and total water is 250.0g
  - **When** Brew ratio is calculated
  - **Then** Ratio is 1:16.67 with explicit mass units

### Feature 2: Coffee & Ingredient Catalog with Provenance (FR-01)
- **Scenario: Seed catalog offline search and filtering**
  - **Given** App is running in offline airplane mode with curated seed beans
  - **When** User searches by origin "Ethiopia" or process "Washed"
  - **Then** Matching local beans show provenance and Unknown for unavailable facts

- **Scenario: Custom coffee bean entry and validation**
  - **Given** User inputs new coffee bean with origin, variety, and roast level
  - **When** User saves bean record
  - **Then** Record is persisted locally and immediately available in Recipe Builder

### Feature 3: Manual Recipe Formulation & Versioning (FR-02)
- **Scenario: Create and save versioned brew recipe**
  - **Given** Selected bean, brew method "V60", 18g dose, 300g water, 93°C temperature
  - **When** User saves the formulation
  - **Then** Recipe is assigned version 1 and stored in local database
  - **And** Recipe appears in Brew Journal

- **Scenario: Recipe cloning and modification**
  - **Given** Existing recipe v1
  - **When** User duplicates recipe and adjusts water to 270g
  - **Then** New version or clone is created with updated ratios without overwriting v1

### Feature 4: Sensory Tracking & Descriptors (FR-04)
- **Scenario: Recording observed tasting notes**
  - **Given** Brewed coffee recipe in journal
  - **When** User inputs observed sensory intensities (Aroma=8, Acidity=7, Sweetness=8, Body=6)
  - **Then** Observed sensory profile is persisted as human descriptive observation

- **Scenario: Overlaying rule-based sensory estimate**
  - **Given** A recipe with specific origin and roast level
  - **When** Sensory view renders comparison
  - **Then** Estimated profile is visually distinct from observed profile
  - **And** Explicit disclaimer displays stating estimate is non-deterministic and uncalibrated

### Feature 5: Offline Local LLM Chat & Typed Tools (FR-05, FR-07)
- **Scenario: Allowlisted tool execution**
  - **Given** LLM assistant generates a tool call for `calculate_extraction_yield`
  - **When** Tool executor validates typed arguments (dose=18, beverage=270, tds=1.30)
  - **Then** Deterministic Kotlin engine computes 19.50%
  - **And** Exact Kotlin calculation result card is returned to chat session

- **Scenario: Write tool confirmation gate**
  - **Given** Assistant suggests `save_recipe`
  - **When** Tool call is evaluated
  - **Then** Explicit user confirmation prompt is required before database mutation

### Feature 6: Offline Document RAG & Hybrid Retrieval (FR-06)
- **Scenario: Document indexing and citation retrieval**
  - **Given** Imported coffee brewing text document chunked and indexed
  - **When** User queries brewing temperature guidance
  - **Then** Hybrid retrieval returns relevant chunks with document title and page/chunk citation
  - **And** When no relevant evidence is found, assistant explicitly abstains

### Feature 7: Model & Asset Management with SAF (FR-08)
- **Scenario: Checkpoint and verification of model asset**
  - **Given** Model manifest with SHA-256 and expected size
  - **When** A real user-requested transfer validates bytes, range identity and SHA-256
  - **Then** Asset is marked "Installed" only after checksum and size verification pass
  - **And** State supports pause, resume, and recovery

### Feature 8: Settings, Privacy & Backup (FR-09, FR-10)
- **Scenario: Offline privacy audit**
  - **Given** Normal application usage across all screens
  - **When** Network monitor inspects outgoing traffic
  - **Then** Zero network calls are dispatched
- **Scenario: Export and restore backup**
  - **Given** Saved recipes, catalog, and sensory records
  - **When** User triggers JSON backup export
  - **Then** Structured, version-validated manifest and payload are generated

## Verification Gates
- Gate 1: Pure Kotlin Engine unit tests pass 100% (including 18g/270g/1.30% golden case).
- Gate 2: Catalog, recipe repository, and database unit tests pass.
- Gate 3: UI state and Compose architecture verification.
- Gate 4: Android build & assembleDebug produces valid APK.
- Gate 5: GitHub repository, README, release, and download links verified.
