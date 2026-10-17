# Coffee Creator — Sprint 03: Manual Brewing & Grinder Intelligence

**Status:** Proposed implementation specification; NOT implemented or tested  
**Date:** 17 October 2026  
**Scope:** Additive enhancement; preserve all Sprint 01/02 features, data, navigation and contracts.

## 1. Mission and non-negotiable constraints

Build an offline-first, equipment-aware manual brew workbench: user chooses bean, hand grinder, dripper, filter, dose and water; the app provides **model-specific starting grind guidance**, stepwise brewing, timer, measurement capture, and iterative dial-in based on actual results. No grind setting is a universally correct composition. Do not infer taste, TDS, EY, particle size or flow from clicks alone.

- NEVER remove existing features, records, data fields, recipe revisions, sensory wheels, champion recipes, RAG or AI workflows.
- Use additive Room migrations, stable IDs, seed provenance and safe rollback/backup; never destructive migration or `fallbackToDestructiveMigration`.
- All core brewing flows work in airplane mode without an AI model.
- Manufacturer guidance and model-specific dial-in notes are **starting ranges**; preserve raw user-provided ranges as unverified until validated.
- The supplied grinder table includes electric grinders; preserve them in the equipment catalog but distinguish HAND from ELECTRIC.
- Never mix click count, dial number, rotations, microsteps or arbitrary markings without explicit model-specific conversion and zero calibration.
- Do not copy copyrighted recipe text, manufacturer diagrams, video frames or brand artwork.

## 2. Source audit and scientific basis

| Evidence | What it supports | URL |
|---|---|---|
| Official 1Zpresso grind charts | Model-specific notation and reference ranges | https://1zpresso.coffee/grind-setting/ |
| Official 1Zpresso manuals | Calibration, adjustment type, model differences | https://1zpresso.coffee/manual/ |
| Official Comandante FAQ | C40 starting ranges and click zero | https://comandantegrinder.com/pages/faq |
| Comandante C40 manual | Use ranges as a starting point, dial in by taste | https://www.comandantegrinder.com/downloads/c40_manual_EN.pdf |
| Hario V60 expert guide | 15 g / 250 g, medium-fine, 92–96°C, ~2:30 | https://www.hario.co.uk/pages/brew-guides-v60-expert |
| Hario Tetsu Kasuya | Documented 4:6 method, 20 g / 300 mL, coarse, 92°C | https://www.hario-europe.com/blogs/hario-community/v60-ambassadors-tetsu-kasuya |
| Hario Pete Licata | 18 g / 300 mL, medium-fine, 94°C | https://www.hario-europe.com/blogs/hario-community/v60-ambassadors-pete-licata |
| Particle-size research | Grind particle distributions affect extraction; not simple universal conversion | https://doi.org/10.1007/s00217-023-04210-3 |

**Video reference:** https://youtu.be/UKGkM8xJj60 — direct content/transcript could not be verified in this research pass (YouTube fetch throttled). Mark video-derived features `PENDING_VIDEO_REVIEW`; do not attribute specific claims to this video until an agent reviews it with timestamped evidence.

## 3. Supplied equipment seed dataset and provenance

Input: `Pasted text.txt` (44 grinder rows), `Pasted text (2).txt` (26 dripper rows). Treat as USER_SUPPLIED_UNVERIFIED, not verified manufacturer specifications. Retain original source row, raw fields and import timestamp.

### 3.1 Grinder inventory — exact user-supplied ranges

| # | Grinder | Mechanism | Coarse / Tubruk | Medium / V60 | Fine / Espresso |
|---:|---|---|---|---|---|
| 1 | 1Zpresso J-Ultra | Putaran (External) | 3.5 - 4.5 Putaran | 2.5 - 3.5 Putaran | 1.0 - 1.6 Putaran |
| 2 | 1Zpresso JX | Putaran (Internal) | 2.8 - 3.3 Putaran | 1.9 - 2.4 Putaran | 0.7 - 1.1 Putaran |
| 3 | 1Zpresso JX-Pro / JX-Pro S | Putaran (Top) | 4.2 - 5.0 Putaran | 2.9 - 3.6 Putaran | 1.0 - 1.6 Putaran |
| 4 | 1Zpresso K-Pro / K-Plus / K-Max | Nomor (External) | Nomor 7.0 - 8.5 | Nomor 5.5 - 7.0 | Nomor 3.0 - 4.0 |
| 5 | 1Zpresso K-Ultra | Nomor (External) | Nomor 8 - 9 | Nomor 6 - 7.5 | Nomor 3 - 4.5 |
| 6 | 1Zpresso Q Air / Q2 | Klik (Internal) | 22 - 26 Klik | 15 - 20 Klik | 10 - 14 Klik |
| 7 | 1Zpresso X-Pro / X-Ultra | Putaran (External) | 2.0 - 2.4 Putaran | 1.2 - 1.5 Putaran | 0.3 - 0.5 Putaran |
| 8 | 1Zpresso ZP6 Special | Nomor (External) | Nomor 6.0 - 7.5 | Nomor 3.5 - 5.5 | 0 (Tidak disarankan) |
| 9 | Breville Smart Grinder Pro | Setting (Electric) | Setting 50 - 60 | Setting 30 - 45 | Setting 1 - 25 |
| 10 | Comandante C40 MK4 | Klik (Internal) | 25 - 32 Klik | 18 - 24 Klik | 10 - 15 Klik |
| 11 | Comandante C60 Baracuda | Klik (Internal) | 35 - 45 Klik | 20 - 30 Klik | 10 - 18 Klik |
| 12 | Etzinger etz-I | Angka (External) | Angka 18 - 22 | Angka 12 - 16 | Angka 4 - 8 |
| 13 | Fellow Ode Gen 2 | Setting (Electric) | Setting 8 - 10 | Setting 4 - 6 | Setting 2 - 3 |
| 14 | Hario Canister (C-20) | Notch (Internal) | 4 - 5 Notch | 2 - 3 Notch | 1 Notch |
| 15 | Hario Mini Slim+ | Klik (Internal) | 10 - 13 Klik | 7 - 10 Klik | 3 - 5 Klik |
| 16 | Hario Skerton Pro | Notch (Internal) | 8 - 10 Notch | 5 - 7 Notch | 1 - 4 Notch |
| 17 | Kingrinder K6 | Klik (External) | 90 - 120 Klik | 60 - 90 Klik | 30 - 50 Klik |
| 18 | Kingrinder P0/P1/P2 | Klik (Internal) | 35 - 45 Klik | 20 - 30 Klik | 15 - 20 Klik |
| 19 | Kinu M47 | Putaran (External) | 4.0 - 5.0 Putaran | 2.5 - 3.5 Putaran | 0.8 - 1.2 Putaran |
| 20 | Latina Sumba / Sumbawa | Klik (Internal) | 10 - 14 Klik | 7 - 10 Klik | 3 - 5 Klik |
| 21 | Latina Sumo | Klik (Internal) | 20 - 25 Klik | 15 - 20 Klik | 8 - 12 Klik |
| 22 | Mavo Phantox Pro | Angka (External) | Nomor 10 - 12 | Nomor 6 - 10 | Nomor 1 - 4 |
| 23 | Mazzer Omega | Angka (External) | Angka 9 - 11 | Angka 6 - 8 | Angka 1 - 3 |
| 24 | MHW-3BOMBER Blade R3 | Klik (External) | 110 - 165 Klik | 90 - 130 Klik | 20 - 70 Klik |
| 25 | OE Lido 3 / OG | Mark (External) | 12 - 15 Mark | 6 - 10 Mark | 2 - 4 Mark |
| 26 | Pietro (Flat Burr) | Angka (Vertical) | Angka 7 - 9 | Angka 5 - 7 | Angka 1 - 2.5 |
| 27 | Porlex Mini II | Klik (Internal) | 10 - 13 Klik | 7 - 9 Klik | 3 - 5 Klik |
| 28 | Starseeker Edge / Edge+ | Klik (External) | 80 - 100 Klik | 50 - 70 Klik | 20 - 40 Klik |
| 29 | Timemore C2 | Klik (Internal) | 25 - 30 Klik | 13 - 24 Klik | 2 - 12 Klik |
| 30 | Timemore C3 | Klik (Internal) | 21 - 24 Klik | 11 - 20 Klik | 2 - 10 Klik |
| 31 | Timemore C3 ESP | Putaran (Internal) | 2.4 - 3 | 1.2 - 2 | 0.5 - 1 |
| 32 | Timemore C3 ESP Pro | Putaran | 2.4 - 3 Putaran | 1.4 - 2 Putaran | 0.6 - 1.2 Putaran |
| 33 | Timemore C5 | Klik (Internal) | 15 - 16 Klik | 9 - 14 Klik | 2 - 8 Klik |
| 34 | Timemore C5 ESP PRO | Klik (Internal) | 110 - 120 Klik | 75 - 100 Klik | 25 - 60 Klik |
| 35 | Timemore C5 Pro | Klik (Internal) | 23 - 30 Klik | 14 - 22 Klik | 5 - 12 Klik |
| 36 | Timemore Chestnut X | Mayor (Internal) | 20 - 24 Mayor | 14 - 18 Mayor | 6 - 10 Mayor |
| 37 | Timemore Nano / Plus | Klik (Internal) | 20 - 24 Klik | 14 - 18 Klik | 10 - 12 Klik |
| 38 | Timemore S3 | Angka (External) | Angka 7.5 - 9.0 | Angka 4.5 - 6.5 | Angka 1.5 - 3.0 |
| 39 | Timemore Sculptor 064S / 078S | Angka (Stepless) | Angka 6.5 - 8.0 | Angka 4.0 - 6.0 | Angka 1.0 - 2.5 |
| 40 | Timemore Slim Plus | Klik (Internal) | 22 - 26 Klik | 15 - 20 Klik | 10 - 14 Klik |
| 41 | Timemore x Millab M01 | Klik (External) | 9 - 10 Klik | 5 - 8 Klik | 1 - 4 Klik |
| 42 | Varia Hand Grinder | Klik (External) | 90 - 110 Klik | 60 - 85 Klik | 20 - 40 Klik |
| 43 | Vesper VS-3 FOLD | Klik (External) | 80 - 92 Klik | 54 - 72 Klik | 15 - 40 Kilk |
| 44 | Wacaco Exagrind | Putaran/Klik | 1.5 - 2.0 Putaran | 1.0 - 1.3 Putaran | 0 - 20 Klik |

**Critical QA flags:** Some values use incompatible reference frames, and models differ in zero point, adjustment steps and burr sets. Examples requiring manufacturer confirmation: 1Zpresso Q-series model-specific click counts, Comandante C60 vs C40, Timemore ESP versions, and espresso entries for filter-only grinders (e.g. ZP6 Special / Fellow Ode Gen 2). Never display these as verified settings. Preserve source typos in `rawValue` but normalize separately for display.

### 3.2 Dripper inventory — enriched research fields (26 entries)

**Provenance:** Names and original geometry come from the user table. Flow and grind columns below are **research-enriched qualitative starting points**, not measured flow rates or universal grinder clicks. Confidence HIGH = direct manufacturer evidence for key flow/grind claim; MEDIUM = partial manufacturer evidence or conservative brewing extrapolation; LOW = unverified model-specific behavior. A LOW row must not be promoted to a verified recommendation in-app.

| # | Dripper | Geometry (input; corrected where supported) | Flow behavior / input | Grind recommendation / input (relative to V60) | Evidence |
|---:|---|---|---|---|---|
| 1 | 1Zpresso Dripper 01 | Cone | Belum terverifikasi | Medium; awal setara V60 (hipotesis) | LOW |
| 2 | April Brewer | Flat | Terkontrol; tergantung filter dan pouring | Medium–coarse; dial-in dengan resep April | MEDIUM |
| 3 | Cafec Flower Dripper | Cone | Relatif lancar/cepat; flower ribs menjaga celah udara | Medium–fine sampai medium (awal) | HIGH |
| 4 | Chemex 6-Cup | Cone | Cenderung lambat/terhambat filter bonded tebal | Medium–coarse (resmi Chemex) | HIGH |
| 5 | Clever Dripper | Immersion / trapezoid | Tertahan saat steep; mengalir saat valve dibuka | Medium hingga medium–coarse (awal) | MEDIUM |
| 6 | CRYSTAL EYE VECTOR | Cone | Belum terverifikasi untuk varian VECTOR | Medium; hipotesis V60, perlu validasi | LOW |
| 7 | Fellow Stagg X | Flat | Terkontrol; lubang/pola dasar membantu drainase | Medium sampai medium–fine (awal) | MEDIUM |
| 8 | Hario V60 | Cone 60° | Bebas/relatif cepat; sangat sensitif teknik tuang | Medium–fine sampai medium (baseline) | HIGH |
| 9 | Hario V60 Mugen | Cone | Lebih lambat/terbatas daripada V60 | Sedikit lebih kasar daripada V60 (resmi Hario) | HIGH |
| 10 | Immersion Dripper Switch | Cone + valve / immersion hybrid | Valve tutup: 0; valve buka: perkolasi V60 | Medium; sedikit lebih kasar dari V60 untuk steep 2 menit | HIGH |
| 11 | Kalita Wave 155 | Flat | Terkontrol lewat 3 lubang dan filter wave | Medium (awal) | HIGH |
| 12 | Kono Meimon | Cone | Terkontrol; bergantung rib/filter dan tuangan | Medium (awal; uji dibanding V60) | LOW |
| 13 | Melitta 1x4 | Trapezoid | Terkontrol/relatif lambat; outlet bervariasi | Medium (awal) | LOW |
| 14 | NextLevel Pulsar | Flat + valve / no-bypass | Valve-adjustable; dapat berhenti total | Medium hingga medium–coarse (awal) | MEDIUM |
| 15 | OREA 01 | Flat bottom | Bergantung varian dasar/filter; belum diverifikasi | Medium (awal) | LOW |
| 16 | Orea V3 | Flat | Relatif cepat; bergantung filter/negotiator | Medium sampai medium–coarse (awal) | MEDIUM |
| 17 | Orea V4 | Flat / interchangeable bottom | FAST/CLASSIC/OPEN/APEX menghasilkan flow berbeda | Medium–coarse baseline; CLASSIC lebih kasar, APEX lebih halus vs FAST pada resep tertentu | HIGH |
| 18 | Orea V4 Wide | Flat / interchangeable bottom | Bergantung base dan filter; geometri wide mengubah drain | Sedikit lebih kasar daripada V4 Narrow untuk resep sepadan (resmi OREA) | HIGH |
| 19 | Origami S | Cone; kompatibel filter cone/wave | Berubah signifikan menurut filter cone vs wave | Medium; kalibrasi ulang per filter | MEDIUM |
| 20 | Timemore B75 | Flat 75° | Cepat; 20 channel flow | Medium; fine-tune menurut drawdown | HIGH |
| 21 | Timemore Crystal Eye | Cone | Ribbed cone; laju spesifik varian belum tervalidasi | Medium (awal; hipotesis) | LOW |
| 22 | Timemore Crystal Eye Dripper 01 | Cone | Belum terverifikasi terpisah dari Crystal Eye | Medium (awal; hipotesis) | LOW |
| 23 | Timemore Crystal Eye Glass Dripper | Cone | Belum terverifikasi khusus material/varian | Medium (awal; hipotesis) | LOW |
| 24 | Timemore Crystal Eye Turbo Dripper | Cone | Didesain beraliran cepat (perlu verifikasi spesifikasi) | Medium–fine sampai medium (hipotesis) | LOW |
| 25 | UFO V3 01 | Cone 80° | Belum terverifikasi | Medium (hipotesis; tidak ada konversi pasti V60) | LOW |
| 26 | V60 Dripper Neo 01 + Switch | Cone + valve / immersion hybrid | Switch tutup: 0; buka: tergantung Neo dan filter | Medium; sesuaikan mode immersion/percolation | LOW |

**Source links and scope of verification:**

- Hario Mugen: https://www.hario-usa.com/products/v60-one-pour-dripper-mugen — slower than V60; manufacturer recommends slightly coarser grind.
- Hario Switch: https://www.hario.co.uk/blogs/news/how-to-brew-with-the-hario-v60-switch — valve behavior and 15 g / 250 g, medium, steeping recipe.
- CAFEC Flower: https://www.cafec-jp.com/products/dripper/ — flower ribs and smooth/fast water circulation; no manufacturer-verified numeric grind settings.
- CHEMEX: https://chemexcoffeemaker.com/pages/filter-series-support-page — medium-coarse grind, thick bonded paper; https://chemexcoffeemaker.com/pages/faq — clogging diagnostics.
- Kalita Wave 155: https://kalitaofficial.com/product/kalita-wave-155-stainless-steel-coffee-dripper/ — flat bottom, three outlets, wave paper.
- Fellow Stagg X: https://fellowproducts.com/collections/coffee-makers-brewers/products/staggdripper — flat bottom and drainage features.
- Clever: https://cleverbrewing.coffee/products/clever-dripper-black — immersion steep-and-release mechanism.
- Timemore B75: https://www.timemore.com/collections/brew-serve/products/timemore-crystal-eye-b75-dripper-pctg — 75-degree flat geometry, 20 channels, fast flow.
- OREA V4: https://www.orea.uk/guides-v4 — recipes show bottom/filter effects and V4 Wide generally needing coarser grind than Narrow. **Do not treat OREA's `CLIX` as clicks on the user's grinder.**
- April Brewer: https://www.aprilcoffeeroasters.com/pages/arsosala-infos — flat-base recipes and grinder-specific numeric settings, not transferable to other grinders.

**Not manufacturer-verified in this research pass:** 1Zpresso Dripper 01, CRYSTAL EYE VECTOR, Kono Meimon, Melitta 1x4 variant, NextLevel Pulsar exact flow/grind, OREA 01, OREA V3 exact flow, Origami S exact flow, Timemore Crystal Eye variants, UFO V3 01, V60 Neo 01 + Switch exact hybrid behavior. Keep qualitative values flagged LOW/MEDIUM; request model-specific official source before claiming verified.

**Implementation requirements for these new fields:**

1. Persist `flowBehavior`, `flowClass` (`FAST|MODERATE|RESTRICTED|VALVE_CONTROLLED|UNKNOWN`), `flowMode` (`PERCOLATION|IMMERSION|HYBRID`), `grindBaselineRelativeToV60`, `grindText`, `filterType`, `bottomVariant`, `manufacturerSourceUrl`, `evidenceStatus`, and `evidenceCheckedAt`.
2. `flowClass` is a qualitative classification, **not a physical flow-rate measurement**. Never display a made-up `mL/s`, drawdown time, or particle size in micrometers.
3. `grindText` is a starting category. Convert to grinder clicks **only** through a verified model-specific grinder chart and recorded calibration; otherwise show a non-numeric suggestion.
4. For valve drippers, show flow for **open and closed states**; for OREA V4 require selection of bottom and filter before applying relative grind adjustments.
5. Seed updates must be additive and idempotent. Preserve user overrides, past brew snapshots, recipe versions, and all previous Sprint 01/02 functionality.
6. Add acceptance tests: `DRIP-001` all 26 rows imported; `DRIP-002` no unknown flow silently assigned numeric rate; `DRIP-003` Mugen coarser vs V60; `DRIP-004` Switch valve open/closed; `DRIP-005` OREA V4 Wide relative adjustment and bottom variant; `DRIP-006` B75 geometry flat; `DRIP-007` LOW evidence clearly marked; `DRIP-008` no click conversion across grinder models; `DRIP-009` offline search and filtering; `DRIP-010` user override survives catalog update.

## 4. Feature requirements / implementation stories

| ID | Feature | Acceptance criteria | Priority |
|---|---|---|---|
| GR-01 | 44-model grinder catalog | All imported rows searchable, source/status visible, electric/hand correctly classified | P0 |
| GR-02 | Grinder model variants | Distinct model, revision, burr, axle, dial/zero reference; never merge incompatible units | P0 |
| GR-03 | Zero calibration wizard | Shows safe model-specific instructions and saves calibration with timestamp | P0 |
| GR-04 | Typed grind settings | Clicks/rotations/dial/marks typed, validation, no implicit cross-model conversion | P0 |
| GR-05 | Manufacturer evidence overlay | Show verified source alongside raw user range; conflicting values both retained | P0 |
| GR-06 | Grinder profile | Save owned grinder, burr condition, custom setting, calibration notes | P0 |
| GR-07 | Grind range finder | Filter by model + brewer + method; recommend starting point only if evidence exists | P0 |
| GR-08 | Dial-in assistant | User feedback and brew time drive reversible finer/coarser experiments, not deterministic taste claims | P1 |
| GR-09 | Grind history | Each brew records grinder revision, settings and result; searchable comparisons | P0 |
| GR-10 | Grinder compatibility | Warn when grinder is not suitable or recommendation unverified | P1 |
| GR-11 | Grind measurement | Optional sieve/particle size metadata only if actually measured | P2 |
| GR-12 | Calibration transfer | Export/import profile with schema version and original raw values | P1 |
| BR-01 | 26-dripper catalog | All 26 source rows imported, missing data stays UNKNOWN | P0 |
| BR-02 | Brewer geometry/mode | Cone, flat, trapezoid, immersion, hybrid; distinguish valve open/closed | P0 |
| BR-03 | Filter compatibility | Filter shape/size/material compatibility with documented source | P1 |
| BR-04 | Manual brew composer | Select bean, grinder, dripper, filter, dose, water, temp, pour strategy | P0 |
| BR-05 | Ratio & dose scaling | Typed Kotlin calculation; exact units, immutable source recipe | P0 |
| BR-06 | Bloom and staged pours | Absolute/cumulative targets, editable durations, mass validation | P0 |
| BR-07 | Offline brew timer | Pause/resume, interruption recovery, haptic/sound cues, screen-awake optional | P0 |
| BR-08 | Manual weigh-ins | Allow no BLE; measured weights clearly distinguished from targets | P0 |
| BR-09 | Brew diagnostics | Record drawdown, clogging, bypass, channeling observations without fake inference | P1 |
| BR-10 | Water & temperature | User-entered mineral profile and temp; unknown values remain unknown | P1 |
| BR-11 | Recipe methods | V60, Kalita, Chemex, Switch, Clever, Origami, immersion, 4:6, pulse/continuous | P0 |
| BR-12 | Championship templates | Verified attribution, year, source and permitted paraphrase; versioned originals | P1 |
| BR-13 | Sensory feedback | Link observation to brew and Roda Rasa vocabulary without overwriting legacy records | P0 |
| BR-14 | Experiment compare | A/B comparisons, control variable and note changes | P1 |
| BR-15 | Local AI assistant | Optional RAG grounding and typed Kotlin tools; no unverified source claims | P1 |
| BR-16 | Accessibility | 48dp targets, TalkBack, 200% font, offline/error/empty states | P0 |
| BR-17 | Video-reference audit | Timestamped verified feature inventory from requested video before implementation | P1 |
| BR-18 | Migration & preservation | All Sprint 01/02 data/flows intact after additive migration | P0 |

## 5. Initial brewing templates (grounded examples)

| Template | Coffee | Water | Temperature | Grind | Source |
|---|---:|---:|---|---|---|
| Hario V60 expert | 15 g | 250 g target water | 92–96°C | medium-fine | Hario V60 expert guide |
| Tetsu Kasuya 4:6 | 20 g | 300 mL stated in source | 92°C | coarse | Hario Europe interview |
| Pete Licata V60 | 18 g | 300 mL stated in source | 94°C | medium-fine | Hario Europe interview |

Use `targetWaterMassG` only where source or actual weighing supports mass; keep manufacturer-provided mL as volume unless density is specified. Recipe-to-grinder settings must be a separate mapping with `confidence`, `source`, `calibration`, `filter`, `bean`, and `revision`. No arbitrary mapping from V60 grind adjective to a precise click value.

### Example interactive workflow

1. Choose bean lot, roast, dose, dripper, filter and manual grinder model.
2. Display source-backed starting grind range or `Not verified`; prompt safe calibration/zero check.
3. Choose recipe template or custom pour plan; calculate ratio and cumulative pour masses deterministically.
4. Start offline timer; manually log weights/timestamps and optional sensory/drawdown observations.
5. Save immutable brew and grinder-setting snapshot; propose ONE next variable to change, with reason and uncertainty.
6. Compare A/B results; let user accept or discard adjustment.

## 6. Data model / contracts

```kotlin
enum class GrinderType { HAND, ELECTRIC }
enum class AdjustmentUnit { CLICK, ROTATION, DIAL_NUMBER, MARK, NOTCH, STEPLESS }
enum class EvidenceStatus { USER_SUPPLIED_UNVERIFIED, MANUFACTURER_VERIFIED, THIRD_PARTY, USER_CALIBRATED, CONFLICTED }
data class GrinderSetting(val modelId: String, val unit: AdjustmentUnit, val raw: String, val normalized: String?, val zeroReference: String?, val calibrationId: String?)
data class GrindGuidance(val grinderModelId: String, val brewMethodId: String, val rangeText: String?, val evidenceStatus: EvidenceStatus, val sourceUrl: String?, val caveats: List<String>)
data class BrewEquipmentSnapshot(val grinderModelId: String?, val grinderSetting: GrinderSetting?, val dripperId: String?, val filterId: String?, val revision: Int)
```

Room entities also require stable keys, source provenance, model revision, seed version, created/updated time, user override and soft-delete semantics. Implement schema migration tests and seed merge tests; never overwrite user modifications.

## 7. QA acceptance checklist and test cases

| Test ID | Procedure | Expected |
|---|---|
| TC-301 | Import source tables | 44 grinder and 26 dripper rows present, raw values retained |
| TC-302 | Filter hand-only | Electric grinders excluded from hand-only filter, not deleted |
| TC-303 | Unknown dripper flow | No invented recommendation; UI displays Unknown |
| TC-304 | Model-specific click input | C40 clicks never equated to J-Ultra clicks |
| TC-305 | Zero calibration change | Old brews retain historical setting snapshot |
| TC-306 | Conflicting source ranges | Both values and provenance retained; no silent overwrite |
| TC-307 | Unsupported espresso grinder | Compatibility warning, no invented espresso setting |
| TC-308 | Brew 15g/250g | Ratio = 1:16.6667, displayed with rounding policy |
| TC-309 | Brew 20g/300g | Ratio = 1:15, staged pours sum to 300g |
| TC-310 | Invalid 0g dose | Validation prevents division by zero |
| TC-311 | Volume vs mass | No automatic mL→g conversion without density |
| TC-312 | Timer app background | Elapsed time reconstructed from monotonic clock; no lost brew |
| TC-313 | Rotate during brew | Stage/targets/state preserved |
| TC-314 | Airplane mode | Catalog, timer, saving, compare all work |
| TC-315 | No AI model | Manual brew remains fully functional |
| TC-316 | Source recipe copy | Edit creates user revision, original template immutable |
| TC-317 | Sensory feedback | Observed descriptor never represented as predicted |
| TC-318 | Upgrade existing DB | Old recipes, RAG documents, models, journal and wheel remain accessible |
| TC-319 | Duplicate seed import | Idempotent import; user overrides survive |
| TC-320 | TalkBack & large text | Primary brew journey operable without clipping |
| TC-321 | Prompt injection in imported document | No execution of document instructions or remote request |
| TC-322 | Reinstall recovery | Explicit backup/restore and SAF regrant, no false survival claims |
| TC-323 | Video reference | No video feature marked verified without timestamp and evidence |
| TC-324 | No device available | Instrumented/device tests marked NOT_RUN, never PASS |

## 8. Agent workflow and release gates

`DISCOVER → BASELINE SNAPSHOT → SOURCE AUDIT → CONTRACT → TEST FIRST → ADDITIVE IMPLEMENTATION → MIGRATION/REGRESSION → DEVICE VERIFICATION → REVIEW → INTEGRATE`.

Before changes, record baseline navigation, features, schema, data counts and existing test results. Require approval for destructive changes, third-party content/license issues and production releases.

**Release blockers:** lost legacy features/data; fabricated manufacturer settings; unsafe grinder zero instructions; ungrounded sensory claims; timer losing an active session; broken offline manual workflow; unverified recipes attributed to champions; false test PASS.

Evidence for every requirement: test ID, source revision, command, result, device/API, screenshot/log when applicable, and reviewer. Status only `PASS`, `FAIL`, `BLOCKED`, `NOT_RUN`, `NOT_APPLICABLE` (with rationale).

## 9. Ready-to-paste agent prompt

```text
Act as Coffee Creator Sprint 03 PM + Android Architect + Coffee Science + independent QA.
Read PRD.md, AGENTS.md, ARCHITECTURE.md, DESIGN.md, TASKS.md, SPRINT_02_COMPETITIVE_FEATURE_IMPLEMENTATION.md and this Sprint 03 specification.
Audit repository first without editing. Inventory every existing feature and persistence schema.
Implement GR-01..GR-12 and BR-01..BR-18 in small additive vertical slices; prioritize P0.
Import all 44 grinder and 26 dripper rows losslessly as unverified source data.
Cross-check manufacturer manuals and record evidence; never invent click settings or video details.
Write failing tests first; run actual Gradle/JVM/Compose/instrumented tests when available.
Preserve old UI routes, database records, saved recipes, sensory and RAG functions.
Produce REQUIREMENT_TRACEABILITY_SPRINT03.md, TEST_RESULTS_SPRINT03.md, MIGRATION_REPORT_SPRINT03.md and DEFECTS_SPRINT03.md.
Do not claim completion without executed evidence. Stop on unresolved license, migration, or hardware-only test blockers.
```
