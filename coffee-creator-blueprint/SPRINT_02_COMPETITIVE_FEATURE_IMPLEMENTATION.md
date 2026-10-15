# Coffee Creator — Sprint 02: Competitive Feature Integration Specification

**Document status:** Implementation-ready backlog / NOT proof of implementation  
**Research cutoff:** 15 October 2026  
**Platform:** Native Android, Kotlin 2.x, Jetpack Compose, offline-first  
**Companion specifications:** PRD.md, AGENTS.md, ARCHITECTURE.md, DESIGN.md, UIUX_ANTI_AI_SLOP.md  
**Owner:** Product + Android Architecture + QA

## 0. Executive summary

Sprint 02 is a *feature-integration epic* inspired by ten reference products: Beanconqueror, Filtru, Roda Rasa Kopi Indonesia, Timer.Coffee, Pourist, Coffee Lab AI, Cropster, BeanBench, Percolate and Coffee Book (sometimes written 'Kaffeebook' in the request). Coffee Creator is the target product, **not** an eleventh established competitor. Each borrowed *concept* must be independently implemented; do not copy commercial source code, branded graphics, official flavor wheel artwork or third-party recipe text without permission.

**Interpretation of “all features in Sprint 2”:** All eligible reference capabilities are enumerated and assigned Sprint 02 IDs, but the volume represents an epic with independently shippable slices, not a credible two-week commitment. P0/P1/P2 indicate sequencing *within* the Sprint 02 program. No item is Done before executable evidence exists. Features requiring cloud services (social feed, account sync, ecommerce, live roaster directory, remote AI) must be converted to offline equivalents or recorded as EXCLUDED, never silently reintroduced.

**Evidence labels:** VERIFIED = described in official product documentation or store listing linked below; ADAPTED = a Coffee Creator design derived from reference functionality; CONDITIONAL = depends on hardware/licensing/test results; UNVERIFIED = not enough source evidence to assert the competitor offers it.

## 1. Mandatory architectural invariants

1. No remote inference, analytics SDK, account, cloud sync, cloud RAG, or live backend required for app use. Network permitted **only** for user-initiated bundle/model downloads.
2. Manual brew, math, ingredient editing, sensory logging and local history work without an installed LLM.
3. Calculations are performed by pure typed Kotlin, never guessed by LLM. Measured versus estimated and observed versus predicted must be explicit.
4. Room for relational data and provenance; offline search with FTS; vector adapter (ObjectBox or validated alternative); ONNX embeddings; llama.cpp JNI. No unverified runtime feature claims.
5. SAF persists external model/assets but uninstall removes grants and app-private data; on reinstall reselect tree and import manifest. Recipes/history need explicit backup and restore.
6. No exact copying of proprietary product UI, graphic wheel, text, licensed photos, datasets or protected branding. Handle SCA form and wheel licensing before shipping exact reproductions.
7. Bluetooth is local communication, compatible with offline use; each device protocol must be explicitly validated.
8. All changes must follow AGENTS.md, accessibility and Compose state-hoisting. Every story has tests and verifiable done criteria.

## 2. Competitor feature audit (source-backed; not exhaustive reverse engineering)

### 2.1 Beanconqueror — precision logging and instrumentation
**Sources:** https://beanconqueror.com/ ; https://beanconqueror.com/blog/bluetooth-device-features/ ; https://beanconqueror.com/changelog/v7-0-0/ ; https://beanconqueror.com/changelog/v7-1-0/ ; https://beanconqueror.com/changelog/v6-0-0/

**VERIFIED advertised/documented capabilities:** brew overview and detailed logs; brewing statistics; beans/inventory and package-related management; grinder and preparation method records; user-customizable bean and brewing parameters; water recipes/parameters (including potassium); configurable brew graph and graph reference; import of a JSON brew graph; repeat last brew; smart scales over Bluetooth, multiple devices, pressure integration depending on hardware; live weight/flow graph; auto-start on first measured weight; configurable automatic espresso stop at flow threshold; tare; ignoring readings while swirling/stirring/removing brewer; first-drop tracking; brew aroma/flavor cupping; Brix-to-TDS conversion; roast-date and package workflow; Excel export; configurable time axis; localization including Indonesian; roast/green-coffee workflows reported in the competitor's broader materials. Hardware/device coverage varies by supported models and versions.

**ADAPT to Coffee Creator:** make manual recording first-class; implement generic BLE capability contracts before per-device adapters; do not assume every scale supports every control. Consider green-bean roasting a P2 research slice, not mandatory for professional barista MVP.

### 2.2 Filtru — guided brewing and espresso experience
**Sources:** https://getfiltru.com/ ; https://getfiltru.com/brew-guides/espresso/

**VERIFIED:** guided brewing for V60, Chemex, Kalita, AeroPress, French press, Clever, moka pot, siphon, espresso, Turkish, Vietnamese, batch brew and cupping among others; step timing and target volume/weight; community recipes; brew diary with beans/ratios/taste notes; espresso shot tracking dose/yield/time/taste; extraction calculations; compatible BLE scale monitoring; live weight, flow and brew graphs; target-weight feedback; coffee bag scanner and captured details. Exact device features and subscriptions may differ.

**ADAPT:** minimal distraction live session, tactile and spoken optional cues, custom step editor, offline installed recipe templates; no community network required.

### 2.3 Roda Rasa Kopi Indonesia — sensory vocabulary and cupping
**Sources:** https://play.google.com/store/apps/details?id=com.coffee.seniman ; https://apps.apple.com/id/app/roda-rasa-kopi-indonesia/id1538393963

**VERIFIED:** Indonesian coffee flavor wheel; **82 sensory descriptions**; aroma/taste reference preparation recipes; digital SCA cupping form.

**ADAPT:** original artwork/taxonomy UX, descriptors with validated references, structured offline cupping. Do **not** presume its 82-item list or recipes are freely redistributable. SCA score sheet/form intellectual-property and latest evaluation protocols require separate review.

### 2.4 Timer.Coffee — guided routines and diary
**Sources:** https://www.timer.coffee/features/ ; https://www.timer.coffee/

**VERIFIED as of research:** 30 brewing methods, 40+ curated recipes; bloom/pour/steep timers, target amounts, audio/haptics, keep-screen-on, pause/resume; recipe scaling and personalized saved recipes; 4:6 style controls; bean cellar with photo, origin, roast date, processing and taste; optional bag scanning; auto-logged and manually edited brews; compare two brews; monthly diary/calendar and streak; search/filter/favorites/tags; offline function; optional device sync; share links; roaster directory and real-time public feed; Android active notification.

**ADAPT:** use bundled/licensed or independently authored recipe templates; replace optional cloud features with share/export via Android Sharesheet or local file. No account or real-time feed.

### 2.5 Pourist — contextual personal recipe generation
**Sources:** https://pourist.app/en ; https://pourist.app/en/about/faq ; https://pourist.app/en/recipes ; https://pourist.app/en/legal/terms

**VERIFIED:** bean bag image registration; bean archive; equipment catalog (publicly claims 110+ drippers, 290+ grinders, 100+ filters, 50+ waters); model-specific grinder click settings, roast/process/flavor notes, grind, dripper flow, filter and water mineral composition plus weather inputs in recipe generation; validated pour structure and temperature/ratio; personalized staged brew timer; recorded tasting feedback used to refine next recipe; equipment manager; curated/certified recipes with provenance; cafe log with photos; personal taste map; optional share cards; bean peak-time tracking and recipe notes in paid tier; account/online catalog and public sharing features.

**ADAPT:** device/weather *manual inputs* and local user catalog; offline deterministic constraints and heuristic/taste-history optimizer; offline OCR; no false promises about equivalent 2,000+ bean catalog or vendor recognition accuracy.

### 2.6 Coffee Lab AI — conversation to recipe
**Source:** https://apps.apple.com/id/app/coffee-lab-ai-brew-smarter/id6763360584

**VERIFIED:** photo recognition of coffee bag/dripper/filter; bean/equipment-informed AI pour-over recipe with temperature/ratio/pour stages; AI barista Q&A; stepwise brewing timer; automatically stored recipe history; equipment library.

**ADAPT:** all photo parsing on-device via bundled OCR (object classification only if actual model exists); chat-to-structured-recipe via schema validation, local document citations and deterministic math. Competitor's remote/local inference architecture is not verified; never mirror an assumed cloud design.

### 2.7 Cropster — professional quality and inventory
**Sources:** https://help.cropster.com/en_US/cupping/create-a-sensorial-session-in-cropster ; https://help.cropster.com/en_US/using-the-quality-module ; https://help.cropster.com/en_US/sample-management/managing-samples ; https://www.cropster.com/products/roast/features/ ; https://www.cropster.com/solutions/roasting-qc/

**VERIFIED:** quality module with sensorial, physical, sessions; blind/non-blind/double-blind cupping and configurable score sheets; multiple samples/evaluators, guest cuppers, sessions draft/open/closed, comparison and exports; sample tracking and green grading (moisture, density, screen size, defects); roast and post-roast blend quality; lots, warehouse inventory/traceability; comparative scores, descriptors, spread/deviation; roast profiles/reference curves, roast schedule and AI-powered roasting predictions, integrations with roast machines (product-wide, not standalone offline mobile guarantees).

**ADAPT:** offline single-device cupping sessions and multiple assessor identities entered locally; blind masking to minimize bias; offline export/report; green/roasted lots in limited catalog. Exclude machine-linked production planning, real-time team collaboration, cross-site stock automation and machine AI prediction unless a separate Sprint 02 subproject is funded.

### 2.8 BeanBench — local-first coffee diary and precision
**Sources:** https://beanbench.coffee/app/ ; https://beanbench.coffee/

**VERIFIED:** 13 built-in method types plus custom; dose/water/ratio/grind/grinder/temperature/time/drawdown/notes; structured taste attributes, ratings and flavor tags; refractometer TDS and extraction yield with explicitly labeled estimated beverage mass; 25 guided recipes and haptics; timed pour targets; espresso target-vs-actual comparison, last-shot context and one-change coaching; templates with equipment and pressure prep details; Acaia/Decent live scale flow and saved curves; roast/rest tracking; brew/taste analytics; CSV/JSON export and Beanconqueror import; local-first with optional iCloud features. Homepage also advertises cafe check-ins, streaks and badges. Some July 2026 'upcoming' claims require revalidation before treating them as already shipped.

**ADAPT:** fully local analytics, reproducible measured/estimated calculations and optional explicit full backup.

### 2.9 Percolate — personal taste model
**Source:** https://www.percolateapp.com/

**VERIFIED:** coffee shelf grouped owned/wishlist/finished; roaster, origin, process, roast and elevation fields; bag label scanning; structured aroma/acidity/body/flavor/overall cupping notes; repeat ratings over bag lifecycle; flavor preference quiz; evolving personal palate profile from logged cups; coffee recommendations by mood, flavor, origin and roast; private by default; optional shopping links and sharing/community averages.

**ADAPT:** offline shelf, palate vector/profile based on entered tasting data, explainable nearest-match recommendations; no store links needing network, no community aggregates.

### 2.10 Coffee Book — coffee journal and ergonomics
**Source:** https://apps.apple.com/us/app/coffee-book-brew-timer/id1512681263

**VERIFIED:** brew journal with 20+ variables, equipment and bean details; espresso dial-in; recipe library and brewing guides (AeroPress, V60, Chemex, espresso, French press, cold brew, Origami, Switch etc.); photo journal; searchable history; iOS lock-screen widgets, Siri Shortcuts, Apple Health caffeine integration, and iOS/iPadOS/Vision support. App listing descriptions have mixed privacy/storage claims: do not assume data is entirely on-device.

**ADAPT:** Android timer notification and optional widget; export; manual optional caffeine *estimate* (no healthcare claims or exact extraction prediction). Apple-only functionality is not transferable directly.

## 3. Sprint 02 feature backlog

Legend: `P0` necessary foundation; `P1` planned advanced capability; `P2` stretch or separately gated; `V` verified reference pattern; `A` Coffee Creator adaptation; `C` conditional external hardware/content. All IDs belong to Sprint 02 even if slices require multiple iterations.

### Epic S2-E01 — Coffee bean, ingredient and equipment foundation

| ID | Feature / behavior | Priority | Reference | Type |
|---|---|---|---|---|
| S2-001 | Bean CRUD with roaster/origin/region/farm/producer/varietal/process/altitude/harvest/roast date | P0 | Beanconqueror, Pourist, Percolate | V/A |
| S2-002 | Bag inventory: purchased/active/finished/frozen, remaining mass, package sizes | P0 | Beanconqueror, Percolate | V/A |
| S2-003 | Roast-rest calculator and freshness history with explainable heuristic | P1 | BeanBench, Pourist | V/A |
| S2-004 | Grinder records, calibration unit, clicks/RPM/burr profile (if known) | P0 | Beanconqueror, Pourist | V/A |
| S2-005 | Dripper, filter, kettle, scale, espresso machine, basket, puck-prep equipment catalog | P0 | Filtru, Coffee Lab AI, Pourist | V/A |
| S2-006 | Water mineral profile, hardness/alkalinity, user-defined water | P1 | Beanconqueror, Pourist | V/A |
| S2-007 | Food-grade ingredient catalog with density, allergens and provenance | P0 | Coffee Creator original | A |
| S2-008 | Bag photo attachment + bundled offline OCR with manual correction | P1 | Timer.Coffee, Pourist, Percolate | V/A |
| S2-009 | Bulk manual import/export beans/equipment with conflict resolution | P1 | BeanBench, Beanconqueror | V/A |
| S2-010 | Global offline search/filter/tag/favorites across beans and equipment | P0 | Timer.Coffee, BeanBench | V/A |
| S2-011 | Track green bean moisture/density/defects and sample lot status | P2 | Cropster | V/A |
| S2-012 | Record roast batches, roast curves imported from file, blends | P2 | Cropster, Beanconqueror | V/A |

### Epic S2-E02 — Recipe composer and deterministic brewing science

| ID | Feature / behavior | Priority | Reference | Type |
|---|---|---|---|---|
| S2-013 | Recipe create/edit/duplicate/archive/version, link beans/equipment | P0 | All brewing apps | V/A |
| S2-014 | Espresso, V60, AeroPress, Chemex, French press, cold brew, immersion, batch recipe types | P0 | Filtru, Timer.Coffee | V/A |
| S2-015 | User-defined custom brewing method and stages | P0 | BeanBench, Timer.Coffee | V/A |
| S2-016 | Editable ordered steps: action, start/relative time, target water, cumulative mass, temp, valve | P0 | Filtru, Pourist | V/A |
| S2-017 | Ratio and dose scaling recomputes all pour targets exactly | P0 | Timer.Coffee | V/A |
| S2-018 | Typed quantities mg/g/mL/°C/°F and optional density-dependent conversion | P0 | Coffee Creator | A |
| S2-019 | TDS and EY from measured dose, beverage mass, refractometer result | P0 | BeanBench, Filtru | V/A |
| S2-020 | Brix→TDS only with documented calibration equation/instrument profile | P1 | Beanconqueror | V/A |
| S2-021 | Concentrate mass-balance dilution and solution dosing | P0 | Coffee Creator | A |
| S2-022 | Food-grade aroma concentrate percentage with active-vs-stock distinction | P0 | Coffee Creator | A |
| S2-023 | Manual sensory target constraints and safe parameter validation | P1 | Pourist, Coffee Creator | V/A |
| S2-024 | Curated source-attributed starter recipes (licensed/independently authored) | P1 | Timer.Coffee, Pourist | V/A |
| S2-025 | Expert mode for custom brew metadata, first drop and grind setup | P1 | Beanconqueror | V/A |
| S2-026 | Share recipe via exported JSON/PDF/text and Android Sharesheet, no cloud | P1 | Timer.Coffee, Pourist | V/A |

### Epic S2-E03 — Active brew timer, BLE and live feedback

| ID | Feature / behavior | Priority | Reference | Type |
|---|---|---|---|---|
| S2-027 | Large accessible brew countdown/elapsed timer with background-safe timebase | P0 | Filtru, Timer.Coffee | V/A |
| S2-028 | Step-by-step current/next pour, cumulative target, action instructions | P0 | Filtru, Timer.Coffee | V/A |
| S2-029 | Pause/resume/skip/restart and interrupted-session recovery | P0 | Timer.Coffee | V/A |
| S2-030 | Haptic/audio/vibration cues; screen-awake opt-in and notification controls | P0 | BeanBench, Timer.Coffee | V/A |
| S2-031 | Automatic completed-session logging and later manual edits | P0 | Timer.Coffee | V/A |
| S2-032 | Bluetooth LE device abstraction, scan/pair/reconnect and permission UX | P1 | Beanconqueror, Filtru | C |
| S2-033 | Device-specific live weight + flow in g/s, graph persistence | P1 | Beanconqueror, Filtru | C |
| S2-034 | Tare, weight-triggered timer start, automatic espresso stop where supported | P1 | Beanconqueror | C |
| S2-035 | Ignore transient scale reading during swirl/stir/remove and flag chart discontinuities | P1 | Beanconqueror | C |
| S2-036 | Target-weight proximity visual/haptic indicator | P1 | Filtru, BeanBench | C |
| S2-037 | On-device brew curve reference and comparison overlays | P2 | Beanconqueror | C |
| S2-038 | Android ongoing notification or widget for active brew (platform compliant) | P1 | Timer.Coffee, Coffee Book | V/A |

### Epic S2-E04 — Brew journal, espresso dial-in, analytics

| ID | Feature / behavior | Priority | Reference | Type |
|---|---|---|---|---|
| S2-039 | Detailed brew journal (dose, water, ratio, grind, temp, time, drawdown, TDS, taste, note, photo) | P0 | BeanBench, Beanconqueror | V/A |
| S2-040 | Search/filter by bean/method/date/rating/tags/equipment | P0 | Timer.Coffee | V/A |
| S2-041 | Side-by-side two brew sessions with deterministic numeric deltas | P0 | Timer.Coffee, Cropster | V/A |
| S2-042 | Espresso target vs actual, last shot comparison and annotated dial-in | P0 | BeanBench, Filtru | V/A |
| S2-043 | One-variable-at-a-time adjustment suggestions with reasoning | P1 | BeanBench | V/A |
| S2-044 | Interactive charts for weight/time/flow/EY/TDS and filtering by bean | P1 | Beanconqueror, BeanBench | V/A |
| S2-045 | Calendar, streak and monthly stats computed locally | P1 | Timer.Coffee, BeanBench | V/A |
| S2-046 | Brew template save/reuse and repeat last brew | P0 | Beanconqueror, BeanBench | V/A |
| S2-047 | CSV/JSON export, full backup and import conflict preview | P0 | BeanBench | V/A |
| S2-048 | Beanconqueror export importer via documented/test fixture mapping | P1 | BeanBench | C |
| S2-049 | Cafe cup journal with optional photos and manual cafe information (no live map) | P2 | Pourist, BeanBench | V/A |
| S2-050 | Manual optional caffeine consumption estimate with uncertainty label | P2 | Coffee Book | V/A |

### Epic S2-E05 — Sensory wheel and professional cupping

| ID | Feature / behavior | Priority | Reference | Type |
|---|---|---|---|---|
| S2-051 | Hierarchical, searchable original flavor wheel/taxonomy, accessibility list alternative | P0 | Roda Rasa, Percolate | V/A |
| S2-052 | Descriptor definitions + safe aroma/taste reference exercises with attribution | P1 | Roda Rasa | V/A |
| S2-053 | Aroma, acidity, sweetness, body, bitterness, aftertaste, balance sliders/notes | P0 | BeanBench, Percolate | V/A |
| S2-054 | Distinguish human observation, AI hypothesis, calibrated model prediction | P0 | Coffee Creator | A |
| S2-055 | Cupping sheet templates; exact SCA copyrighted forms only if licensed | P1 | Roda Rasa, Cropster | C |
| S2-056 | Cupping sessions with samples and locally assigned evaluator identities | P1 | Cropster | V/A |
| S2-057 | Blind, non-blind and double-blind masking with controlled reveal | P1 | Cropster | V/A |
| S2-058 | Draft/open/closed statuses, score submission, reopen with audit log | P1 | Cropster | V/A |
| S2-059 | Compare score distributions, descriptors, evaluator agreement/spread | P1 | Cropster | V/A |
| S2-060 | Generate offline PDF/CSV QC report including provenance | P1 | Cropster | V/A |
| S2-061 | Roast/green physical QC fields and link to samples | P2 | Cropster | V/A |
| S2-062 | Multiple taste observations per bag over roast-rest cycle | P1 | Percolate | V/A |

### Epic S2-E06 — Personalization, local AI and RAG integration

| ID | Feature / behavior | Priority | Reference | Type |
|---|---|---|---|---|
| S2-063 | Offline personal palate quiz + editable preference profile | P1 | Percolate | V/A |
| S2-064 | Personal taste profile computed from logged sensory evidence | P1 | Percolate | V/A |
| S2-065 | Explainable local bean/method/recipe recommendations from available inventory | P1 | Percolate, Pourist | V/A |
| S2-066 | Brewing feedback loop that proposes a single controlled recipe adjustment | P1 | Pourist, BeanBench | V/A |
| S2-067 | Camera/OCR to draft bean/equipment metadata with explicit corrections | P1 | Coffee Lab AI, Pourist | V/A |
| S2-068 | Local AI barista chat for questions on extraction, water, gear and techniques | P0 | Coffee Lab AI | V/A |
| S2-069 | Tool-driven generation of typed recipe draft based on beans/gear/constraints | P0 | Coffee Lab AI, Pourist | V/A |
| S2-070 | All tool args schema validated; math performed by domain engine; user confirms save | P0 | Coffee Creator | A |
| S2-071 | RAG answers cite local uploaded document page/section; no-evidence abstention | P0 | Coffee Creator | A |
| S2-072 | Local barista persona presets + safe custom system prompt controls | P1 | Coffee Creator | A |
| S2-073 | Optional weather inputs are user supplied; never require live weather API | P2 | Pourist | A |
| S2-074 | Ingredient/allergen warnings and provenance; never assume perfume is food-safe | P0 | Coffee Creator | A |

### Epic S2-E07 — Reliability, data portability, inclusivity

| ID | Feature / behavior | Priority | Reference | Type |
|---|---|---|---|---|
| S2-075 | SAF persistent model/asset manifest, checksum scan and reinstall rediscovery | P0 | Coffee Creator | A |
| S2-076 | User-initiated resumable model/OCR download with network resume, ETag and range | P0 | Coffee Creator | A |
| S2-077 | Explicit full backup/restore for recipes, journal, cuppings, profiles, settings | P0 | BeanBench, Coffee Creator | V/A |
| S2-078 | On-device language EN/ID, localized units, offline help and error recovery | P1 | Beanconqueror, Pourist | V/A |
| S2-079 | Adaptive Compose phone/tablet layouts, font scale, TalkBack and touch targets | P0 | Coffee Creator design | A |
| S2-080 | On-device activity diagnostics and opt-in local export; no telemetry SDK | P1 | Coffee Creator | A |
| S2-081 | No LLM/no OCR/no BLE graceful degradation paths | P0 | Coffee Creator | A |
| S2-082 | Regression/performance matrix (offline, process death, uninstall/reinstall, low RAM) | P0 | Coffee Creator | A |

## 4. Stories with example Gherkin acceptance criteria

### ST-S2-01 Ingredient/bean library
**Given** airplane mode and an empty library, **when** the barista creates a bean with origin, processing, roast date and 250 g package, **then** the record persists across app restart and can be found with local search. **And** editing package consumption cannot yield negative remaining mass.

### ST-S2-02 Ratio scaling
**Given** a four-pour 18 g / 300 g recipe with targets 50, 100, 200, 300 g, **when** user scales water to 450 g, **then** cumulative targets become 75, 150, 300, 450 g (subject to documented rounding), remain monotonic and sum to 450 g. **And** any changed ratio is clearly displayed.

### ST-S2-03 TDS / extraction yield
**Given** measured dose 18.0 g, beverage 270 g, TDS 1.30%, **when** EY is calculated, **then** mass-balance approximate EY is 19.50%. **When** dose is zero or negative, **then** validation blocks the calculation. An estimated beverage mass must never be labeled measured.

### ST-S2-04 Guided brewing
**Given** an offline recipe with timed bloom and pours, **when** the screen is paused/resumed or the UI rotates, **then** timer elapsed time follows a monotonic clock and each target step remains consistent. A recovered process does not fabricate elapsed steps.

### ST-S2-05 Espresso dial-in
**Given** two shots on the same bean and equipment, **when** compared, **then** dose, yield, time and taste deltas are correct. Suggested grind changes are explicitly heuristic and require user confirmation.

### ST-S2-06 Sensory cupping and blind sessions
**Given** three samples in a blinded session, **when** an evaluator scores them, **then** bean identities remain hidden until reveal; reopening changes the session audit trail. Original score inputs remain traceable.

### ST-S2-07 Offline AI recipe draft
**Given** a valid local model, local knowledge, bean and grinder, **when** a user asks for a floral V60, **then** chatbot may propose a structured recipe with source-backed claims and validated numerical values. **And** no network request is performed. **And** saving always needs an explicit action.

### ST-S2-08 Import/export
**Given** a database with 20 brews and 3 cupping sessions, **when** user exports to SAF and imports into a fresh installation, **then** all supported relationships and measurements are restored; duplicate/conflicting IDs appear in a preview, not silently overwritten.

### ST-S2-09 BLE failure fallback
**Given** BLE is off or device disconnected, **when** a brew starts, **then** manual timer/weight recording continues and an actionable connection state is shown; no screen crash occurs.

### ST-S2-10 Model reinstall
**Given** verified GGUF in a user-selected shared Documents SAF tree, **when** app is uninstalled/reinstalled and user reselects the tree, **then** asset manager scans manifest and verifies hash before registering the model; internal conversation/history is not presumed to survive absent user backup.

## 5. Data and interface contracts

- `CoffeeBean(id, roaster, origin, region, producer, variety, process, roastDate, packageMassMg, remainingMassMg, provenance)`
- `Equipment(id, type, brand, model, calibrationProfileId, capabilities)`
- `WaterProfile(id, source, hardness, alkalinity, mineralConcentrations, provenance)`
- `Recipe(id, version, method, beanId, equipmentIds, doseMg, targetWaterMg, steps, safetyConstraints)`
- `BrewStep(id, order, action, startOffsetMs, durationMs, cumulativeTargetMg, waterTemperatureC)`
- `BrewSession(id, recipeVersionId, startedAt, measurements, results, notes, estimatedFlags)`
- `SensoryObservation(id, brewId, assessorId, descriptorId, intensity, sourceType, timestamp)`
- `CuppingSession(id, mode, status, samples, assessors, revealState, auditEvents)`
- `Measurement(id, value, unit, instrument, measuredAt, isEstimated, uncertainty)`
- `VectorEvidence(id, documentId, page, section, chunkId, embeddingModelVersion)`
- `ModelAsset(id, contentUri, checksum, format, compatibility, state)`

**Do not** persist a computed float or inferred note as an observed value without recording calculation origin/model version. For BLE curves use chunked/compressed records; cap RAM and sampling frequency. Room migrations must be tested from all shipped versions.

## 6. Sprint sequencing / dependency map

- **Slice 2A — Foundation / P0:** S2-001,002,004,005,007,010,013–019,021,022,027–031,039–042,046,047,051,053,054,068–071,074–077,079,081,082. Do not begin LLM work until deterministic recipe contracts are stable.
- **Slice 2B — Professional workflow / P1:** S2-003,006,008,009,020,023–026,032–036,038,043–045,048,052,055–060,062–067,072,078,080. BLE per-device support and licensed forms remain conditional.
- **Slice 2C — Advanced research / P2:** S2-011,012,037,049,050,061,073, selected Cropster-like roast/production workflows. Do not claim completed until separate technical/scientific validations pass.

**Excluded from the fully offline product:** proprietary backend synchronization, app login, public community feed, remote marketplace, live roaster directory, paid subscription mirroring, iCloud/Apple Health/Siri integrations, real-time weather API, remote AI recipes, automatic commercial roast machine orchestration, direct licensed SCA imagery without permission.

## 7. Agentic implementation policy

1. Planning agent turns each `S2-NNN` feature into an issue with dependency mapping, UI state contract, test fixture and Done conditions.
2. UX agent creates low-fidelity flow, failure-state matrix, accessibility semantics, Compose Preview variants and screenshot targets; anti-AI-slop design rules apply.
3. Domain agent defines typed, pure Kotlin contracts and tests for units, ratios, physical constraints, uncertainty and ingredient safety.
4. Data agent adds Room entities/DAOs/migrations, import/export contract tests and provenance records.
5. AI agent adds structured tools/RAG, no-network assertions, citation tests and prompt injection defense; cannot edit coffee science formulas without domain review.
6. Device agent handles BLE/SAF/OCR only using mocked adapters and at least one tested real-device/provider matrix.
7. QA agent independently executes test evidence, reviews rendered screenshots, checks accessibility, tests failure recovery and reports findings.
8. Integrator merges independent worktrees only after green quality gates. No agent may self-certify an unexecuted test.

**Required per-story artifacts:** `spec.md`, `acceptance.feature` or equivalent tests, implementation, screenshot evidence for UI stories, automated results, known limitations, source/license notes and `CHANGELOG.md` entry.

### Quality gates

```bash
./gradlew lint
./gradlew detekt
./gradlew ktlintCheck
./gradlew testDebugUnitTest
./gradlew assembleDebug
# plus project-specific connectedAndroidTest, screenshot tests and Macrobenchmark suite
```

Pass/fail thresholds must be configured in CI; task names depend on actual Gradle modules. Measure on device: first-screen startup, scroll jank, memory peak during LLM, ingestion throughput, timer drift, BLE reconnect, OCR extraction accuracy, and RAG retrieval. Keep threshold definitions in `QUALITY_GATES.md`; do not invent measured performance in status updates.

## 8. UX architecture and screen inventory

- Dashboard: current bag, last successful brew, quick start, setup state, offline badges.
- Beans & Equipment: shelf (owned/active/frozen/finished/wishlist), add by OCR, editable metadata, filters and calibration.
- Recipe Lab: expert numerical form and beginner progressive-disclosure form; ratio calculator; safe concentration and dilution; water profile.
- Brew Runner: one dominant timer, one target, upcoming step, accessible audio/haptic, pause/resume and manual entry fallback.
- Espresso Dial-In: target-vs-actual shot table, last-shot context, controlled single recommendation.
- Journal: timeline/calendar, before-after comparisons, charts and local search.
- Sensory Lab: wheel + accessible descriptor list, tasting log, cupping sessions, masked IDs and aggregate charts.
- AI Barista: streaming local chat, citations, typed recipe draft, checkable calculations, persona toggle.
- Knowledge & Assets: SAF imports, indexing, model download verification, resume/recover, restore.
- Settings & Data: EN/ID, density/unit preferences, accessibility, backup/import/export and licensing.

Anti-generic UI requirements: no arbitrary gradient cards; establish visual hierarchy through task relevance; barista-oriented one-handed large action zones; do not embed unvalidated flavor claims as decorative charts; show measured/estimated badges in result detail (not repeated on every surface); include all states (empty/loading/error/permission denied/no model/offline/BLE lost).

## 9. Traceability matrix (reference -> Sprint 02 modules)

| Reference | Dominant capabilities | Sprint stories |
|---|---|---|
| Beanconqueror | detailed logging, beans/equipment, graphs, BLE, water, cupping | 001–006, 017–020, 025, 032–037, 039, 044, 046 |
| Filtru | focused guided brews, scale feedback, espresso, diary | 014, 016, 027–036, 039, 042 |
| Roda Rasa | sensory descriptors, reference education, cupping | 051–055 |
| Timer.Coffee | step timer, scaling, bean cellar, journal, notifications | 002, 008, 015–017, 024, 026–031, 038–041, 045 |
| Pourist | gear-based recipes, bag recognition, tasting feedback, cafe | 001, 004–006, 023, 024, 049, 063–067, 073 |
| Coffee Lab AI | scan, AI chat, recipes, gear, timer, history | 005, 008, 027, 031, 067–071 |
| Cropster | cupping sessions, physical QC, comparisons, lots | 011, 012, 055–061 |
| BeanBench | extraction science, espresso, trends, portable logs | 003, 019, 027, 030, 039–048, 050 |
| Percolate | shelf, structured cup ratings, palate recommendations | 002, 051–054, 062–065 |
| Coffee Book | rich journals, custom equipment, photo, espresso | 005, 013, 039, 042, 050 |

## 10. Definition of Done and release decision

A story is Done only when (1) user-visible workflow works on a clean install and airplane mode; (2) Room state and SAF boundaries are tested; (3) validation and scientific provenance work; (4) all applicable unit/instrumentation/UI tests pass; (5) screenshots are reviewed and accessibility checks pass; (6) UI remains usable without AI or BLE; (7) import/export avoids silent data loss; (8) security/license issues are resolved; (9) CI evidence is stored. 'Feature implemented' requires code and test evidence—not merely generated mockups or a README claim.

**Release gate:** All Slice 2A P0 stories passed; all unshipped P1/P2 clearly behind feature flags or omitted from UI. If the project explicitly requires P1/P2 all shipped in the same Sprint 02 release, expand the schedule/team rather than weaken these gates.

## 11. Research source index (official sources; reviewed 15 Oct 2026)

1. Beanconqueror: https://beanconqueror.com/ ; https://beanconqueror.com/blog/bluetooth-device-features/ ; https://beanconqueror.com/changelog/v7-0-0/ ; https://beanconqueror.com/changelog/v7-1-0/
2. Filtru: https://getfiltru.com/ ; https://getfiltru.com/brew-guides/espresso/
3. Roda Rasa: https://play.google.com/store/apps/details?id=com.coffee.seniman ; https://apps.apple.com/id/app/roda-rasa-kopi-indonesia/id1538393963
4. Timer.Coffee: https://www.timer.coffee/features/
5. Pourist: https://pourist.app/en/about/faq ; https://pourist.app/en ; https://pourist.app/en/recipes
6. Coffee Lab AI: https://apps.apple.com/id/app/coffee-lab-ai-brew-smarter/id6763360584
7. Cropster: https://help.cropster.com/en_US/cupping/create-a-sensorial-session-in-cropster ; https://help.cropster.com/en_US/using-the-quality-module ; https://www.cropster.com/products/roast/features/
8. BeanBench: https://beanbench.coffee/app/
9. Percolate: https://www.percolateapp.com/
10. Coffee Book: https://apps.apple.com/us/app/coffee-book-brew-timer/id1512681263

**Research limitations:** This inventory is comprehensive across publicly evidenced *relevant categories*, but cannot truthfully claim to list every hidden, experimental, discontinued or paywalled function of every competitor. In particular, device-specific BLE support, premium-only capabilities, license status, and whether newly advertised features are live require hands-on checks. Any later competitive discovery should append source URL, evidence date, and a new story ID rather than rewriting old claims.
