# OpenSpec Proposal: Sprint 04 EPIC D — Green Coffee Physical Assessment (S4-041 to S4-051)

## Context & Objectives
Implement a production-grade, offline-first Green Coffee Physical Assessment and QC Grading engine in Coffee Creator.
Grounds physical assessment in rigorous international physical standards (SCA Green Coffee Defect Handbook 2004/2013, ISO 4150:2010 green coffee size analysis, ISO 6669:1995 green coffee bulk density, ISO 6673 / dielectric moisture measurement, ISO 21807 water activity, and SNI 01-2907-2008):
1. **Lot Catalog & Chain of Custody (S4-041)**: Complete botanical, geographic, harvest, lot identification, default 350.0g sample mass, and multi-tier chain of custody (exporter, importer, warehouse, transport).
2. **Defect Taxonomy & Standard Attribution (S4-042, S4-043)**: Primary (Category 1) vs Secondary (Category 2) defect taxonomy with verified standard/version/source attribution, defect counting, mathematically guarded equivalent weighting, and evidence photo attachments.
3. **Screen Size Distribution Analysis (S4-044)**: ISO 4150 sieve screens (Screen 20 down to below 12 / pan), mechanical/manual shaker duration, mass in grams, fraction percentages, cumulative retention, and mass balance recovery checks ($|\sum m_i - M_0| \le 1.5\text{g}$).
4. **Moisture & Physical Properties (S4-045, S4-046)**: Moisture percentage with meter ID, serial number, calibration date, method (dielectric capacitance vs loss-on-drying), sample temperature, uncertainty ($\pm 0.2\%$), water activity ($a_w \le 0.70$ mold safety threshold), bulk density (ISO 6669 g/L), color, odor, and foreign matter.
5. **Deterministic Washed Arabica Rule Engine (S4-047)**: Versioned thresholds for Specialty Grade 1 down to Off Grade 5; strict refusal and `UNDETERMINED_OUT_OF_SCOPE` guard for unsupported species (e.g. Canephora) or processes (e.g. Natural/Dry) under historical washed criteria.
6. **Epistemological Decoupling (S4-048)**: Strict separation of green physical grade from sensory CVA cupping outcomes; specialty status requires dual independent qualification, never conflating physical defects with cup scores.
7. **Buyer Specification Contract & Comparison (S4-049)**: Configurable buyer tolerance contracts (PASS/FAIL/FLAGGED per parameter) and multi-lot supplier historical performance tracking.
8. **Audit Trail Export & Missing Data Integrity (S4-050, S4-051)**: Lossless JSON (RFC 8259) and tabular CSV (RFC 4180) exports with calibration metadata, assessor timestamps, photo URIs, and SHA-256 seal. Epistemologically honest missing states: `NOT_MEASURED`, `UNKNOWN`, `OUT_OF_SCOPE`, and `PENDING_REVIEW` with ZERO fabricated data.

## Scientific Framework & Mathematical Models

### 1. Defect Equivalency & Tier Aggregation Model (S4-042, S4-043)
In the SCA 2004 Washed Arabica defect grading standard, defects are tallied from a $350.0\text{ g}$ sample of green coffee:
For each defect type $d \in \mathcal{D}$ with raw count $C_d \in \mathbb{N}_0$ and standard equivalent ratio $R_d \in \mathbb{N}_{\ge 1}$:
$$E_d = \begin{cases} 
\frac{C_d}{R_d} & \text{if standard supports equivalent weighting} \\
C_d & \text{if standard requires raw count only} \\
\text{null} & \text{if equivalency unsupported}
\end{cases}$$
Category 1 (Primary Full Defects, $R_d = 1$ except Severe Insect $R = 5$):
$$\text{Total}_{\text{Cat1}} = \sum_{d \in \mathcal{D}_{\text{Cat1}}} E_d$$
Category 2 (Secondary Defects, $R_d \in \{3, 5, 10\}$):
$$\text{Total}_{\text{Cat2}} = \sum_{d \in \mathcal{D}_{\text{Cat2}}} E_d$$
Total Full Defect Equivalents:
$$\text{Total}_{\text{Defects}} = \text{Total}_{\text{Cat1}} + \text{Total}_{\text{Cat2}}$$
- Invariant: If a standard does not define equivalent weighting (e.g., custom raw buyer specification or unverified standard), $E_d$ MUST NOT be fabricated.

### 2. ISO 4150 Screen Size Fraction & Mass Balance Model (S4-044)
For initial test sample mass $M_0$ (nominally $350.0\text{ g}$) and sieve fractions $i \in \{20, 19, 18, 17, 16, 15, 14, 13, 12, \text{Pan}\}$ with measured fraction mass $m_i \ge 0$:
1. Total Recovered Mass:
$$M_{\text{recovered}} = \sum_{i} m_i$$
2. Mass Balance Recovery & Sieve Loss Check:
$$\Delta M = |M_{\text{recovered}} - M_0|$$
$$\text{Recovery Rate } (\%) = \left(\frac{M_{\text{recovered}}}{M_0}\right) \times 100\%$$
- Guard: If $\Delta M > 1.5\text{ g}$ (or $|100\% - \text{Recovery}| > 0.5\%$), trigger `MASS_BALANCE_MISMATCH_WARNING`.
3. Mass Percentage per Fraction:
$$P_i = \left(\frac{m_i}{M_{\text{recovered}}}\right) \times 100\%$$
4. Cumulative Retention Percentage $\ge \text{Screen } S$:
$$P_{\ge S} = \sum_{j \ge S} P_j$$
(e.g., Screen 16+ retention $= \sum_{j=16}^{20} P_j$).

### 3. Bulk Density Model (ISO 6669, S4-046)
Green coffee bulk density is determined by free-fall filling of a standardized container of volume $V_{\text{cylinder}} = 1.000\text{ L}$ (or calibrated container volume):
$$\rho_{\text{bulk}} = \frac{m_{\text{coffee}}}{V_{\text{cylinder}}} \quad [\text{g/L}]$$
- Density Classifications:
  - High Density / Strictly Hard Bean (SHB): $\rho_{\text{bulk}} \ge 700.0\text{ g/L}$
  - Medium Density: $650.0\text{ g/L} \le \rho_{\text{bulk}} < 700.0\text{ g/L}$
  - Low Density / Soft Bean / Insect-Damaged: $\rho_{\text{bulk}} < 650.0\text{ g/L}$

### 4. Moisture Content & Water Activity Safety Model (S4-045, S4-046)
1. Moisture Content $MC$ (%):
$$MC_{\text{compensated}} = MC_{\text{raw}} + \alpha_T \cdot (T_{\text{sample}} - 20.0^\circ\text{C})$$
Standard Specialty Washed Arabica window: $9.0\% \le MC \le 13.0\%$ (ideal storage $10.0\% - 12.0\%$).
- Epistemological Rule: If $MC$ is null, it is strictly `NOT_MEASURED`. Never default to 0.0% or 11.0%.
2. Water Activity $a_w$ (ISO 21807):
Safe storage boundary: $a_w \le 0.700$ at $25^\circ\text{C}$ to suppress *Aspergillus ochraceus* / *Penicillium verrucosum* Ochratoxin A synthesis.
Ideal specialty target: $0.520 \le a_w \le 0.650$.

### 5. Deterministic Historical Grading Engine (S4-047)
For a $350.0\text{ g}$ sample under `SCA_WASHED_ARABICA_2004_REFERENCE`:
$$\text{Grade} = \begin{cases}
\text{SPECIALTY\_GRADE\_1} & \text{if } \text{Species}=\text{Arabica} \land \text{Process}=\text{Washed} \land \text{Total}_{\text{Cat1}} = 0 \land \text{Total}_{\text{Cat2}} \le 5.0 \land MC \in [9.0, 13.0] \land \text{Odor}=\text{Clean} \\
\text{PREMIUM\_GRADE\_2} & \text{if } \text{Species}=\text{Arabica} \land \text{Process}=\text{Washed} \land \text{Total}_{\text{Defects}} \le 8.0 \land MC \in [9.0, 13.0] \\
\text{EXCHANGE\_GRADE\_3} & \text{if } \text{Species}=\text{Arabica} \land \text{Process}=\text{Washed} \land 9.0 \le \text{Total}_{\text{Defects}} \le 23.0 \land MC \in [9.0, 13.0] \\
\text{BELOW\_STANDARD\_GRADE\_4} & \text{if } \text{Species}=\text{Arabica} \land \text{Process}=\text{Washed} \land 24.0 \le \text{Total}_{\text{Defects}} \le 86.0 \\
\text{OFF\_GRADE\_5} & \text{if } \text{Species}=\text{Arabica} \land \text{Process}=\text{Washed} \land \text{Total}_{\text{Defects}} > 86.0 \\
\text{UNDETERMINED\_OUT\_OF\_SCOPE} & \text{if } \text{Species} \ne \text{Arabica} \lor \text{Process} \ne \text{Washed} \lor M_0 \ne 350.0\text{g}
\end{cases}$$

### 6. Epistemological Decoupling Model (S4-048)
Physical Green Grade and Sensory CVA Cupping Outcome belong to orthogonal evaluative dimensions:
$$\text{SpecialtyEligible}_{\text{Physical}} = (\text{Grade} == \text{SPECIALTY\_GRADE\_1})$$
$$\text{SpecialtyEligible}_{\text{Sensory}} = (\text{CvaAffectiveScore} \ge 80.00 \land \text{DefectFree} == \text{true})$$
$$\text{FullSpecialtyCertified} = \text{SpecialtyEligible}_{\text{Physical}} \land \text{SpecialtyEligible}_{\text{Sensory}}$$
- Hard Guard: An 88.0 sensory score CANNOT overwrite a Category 1 Full Black bean. Conversely, a Grade 1 physical score CANNOT grant specialty status to a woody, flat past-crop coffee scoring 75.0 points.

### 7. Buyer Contract Tolerance Model (S4-049)
A Buyer Contract defines tolerance limits $\mathcal{K} = \langle \text{Cat1}_{\max}, \text{Cat2}_{\max}, MC_{\min}, MC_{\max}, a_{w,\max}, \text{Ret}_{\min}, \rho_{\min} \rangle$.
Each measured parameter evaluates to:
$$\phi(x_k) = \begin{cases}
\text{PASS} & \text{if } x_k \text{ conforms to contract bounds} \\
\text{FLAGGED} & \text{if } x_k \text{ is marginal within calibration uncertainty} \\
\text{FAIL} & \text{if } x_k \text{ violates contract bounds} \\
\text{NOT\_EVALUATED} & \text{if } x_k \text{ is NOT\_MEASURED}
\end{cases}$$
Overall contract acceptance:
$$\Phi = \begin{cases}
\text{ACCEPTED} & \text{if } \forall k, \phi(x_k) == \text{PASS} \\
\text{REJECTED} & \text{if } \exists k, \phi(x_k) == \text{FAIL} \\
\text{CONDITIONAL} & \text{if } \exists k, (\phi(x_k) == \text{FLAGGED} \lor \phi(x_k) == \text{NOT\_EVALUATED}) \land \forall j, \phi(x_j) \ne \text{FAIL}
\end{cases}$$

## Pure Domain Architecture (`com.coffeecreator.app.core.sensory.greengrading`)
Pure Kotlin / JVM module without Android, Room, or UI dependencies:
- `GreenGradingModels.kt`:
  - `GreenCoffeeSpecies`: Arabica, Canephora, Liberica, Eugenioides.
  - `GreenProcessingMethod`: Washed, Natural, Honey, WetHulled, Anaerobic, Experimental.
  - `ChainOfCustody`: Exporter, Importer, Warehouse, HarvestCropYear, TransportId, AmbientTemp, AmbientRH.
  - `GradingStandardRef`: Name, Version, SourceCitation, SupportedSpecies, SupportedProcesses.
  - `DefectTier`: Primary (Category 1) vs Secondary (Category 2).
  - `GreenDefectType`: 16 standard types with official ratio, description, and photo URIs.
  - `ScreenSieveNumber`: Screen 20 down to Below 12 / Pan with exact 64th-inch and mm dimensions.
  - `SieveDistribution`: Raw masses, calculated percentages, mass balance check, and cumulative retention.
  - `MoistureAssessment`: Nullable percentage, method enum, meter ID, calibration date, temperature, uncertainty, and `isMeasured` property.
  - `WaterActivityAssessment`: Nullable $a_w$, chamber temperature, device ID, calibration date.
  - `BulkDensityAssessment`: Container volume, sample mass, calculated g/L.
  - `PhysicalGradingEvaluation`: Final grade, specialty eligibility, diagnostic notes, standard applied, and disclaimer.
  - `BuyerSpecificationContract` & `ContractEvaluationResult`: Contract parameters, parameter-level pass/fail, and overall lot acceptance.
  - `GreenQcExportPayload`: Full audit export structure with SHA-256 seal.
- `StandardGreenGradingEngine.kt`:
  - Deterministic implementation of SCA 2004 grading, sieve distribution calculation, bulk density calculation, and contract evaluation.

## Invariants & Non-Goals
1. **Zero Hallucination / Anti-AI Slop**: No estimated moisture, water activity, or defect counts generated by LLM. Missing values strictly stay `NOT_MEASURED`.
2. **Offline-First**: All calculations, evaluations, photo URI linking, and exports function 100% offline in airplane mode.
3. **Epistemological Integrity**: Physical grade $\ne$ Sensory score. Never equate numeric cupping points with green physical quality.
4. **Standard Scoping**: Refuse unsupported species or process methods under historical washed standards.
5. **No Credential Fabrication**: Clear disclaimer stating that the app is an educational/laboratory tool and does not confer official SCA or CQI certification.
