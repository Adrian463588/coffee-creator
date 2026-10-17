# OpenSpec Proposal: Sprint 04 EPIC E — Pro Operations, Architecture, AI & Integration (S4-052 to S4-065)

## Context & Objectives
Implement a robust, production-grade, offline-first operational and architectural backbone for Coffee Creator's sensory, cupping, and coffee evaluation suites:
1. **Full Offline Architecture (S4-052)**: Zero cloud dependencies, zero external network calls; complete local learning, session setup, cupping assessment, and local report exports.
2. **Non-Destructive Room Migrations (S4-053)**: Stable UUID primary keys, explicit foreign key constraints with safe cascade policies, automated backup restoration verification.
3. **Sensory Taxonomy & Wheel Integration (S4-054)**: Seamless reuse of the 221-descriptor flavor wheel and recipe libraries across cupping sheets and journal entries without duplicate definitions.
4. **Cross-Domain Provenance Linkage (S4-055)**: Immutable bi-directional linkage between Cupping Sessions, Coffee Beans, Roast Batches, Manual Brew Recipes, and Grinder/Dripper Sessions.
5. **Deterministic Engine vs Local LLM Separation (S4-056, S4-057)**: Pure Kotlin deterministic engines strictly own scoring, statistics, defect deductions, and physics calculations. The local LLM acts exclusively as an explanatory tutor, citing local knowledge chunks (`RagEvidencePolicy`) with zero hallucinated scores, fake certifications, or ungrounded claims.
6. **Blind Panel Workflow & Role-Based Access (S4-058)**: Clear separation of Session Host vs Evaluator/Assessor roles. Cryptographically blinded sample codes (`#382`, `#914`), concealed true lot identities until session lock, and tamper-evident early-unblinding audit logging (`UNBLINDED_PREMATURELY_AUDITED`).
7. **Panel Agreement & Inter-Rater Calibration Analytics (S4-059)**:
   - **Pearson Product-Moment Correlation ($r$)**:
     $$r = \frac{\sum (x_i - \bar{x})(y_i - \bar{y})}{\sqrt{\sum (x_i - \bar{x})^2 \sum (y_i - \bar{y})^2}}$$
   - **Mean Consensus Deviation ($\bar{\Delta}$)**:
     $$\bar{\Delta} = \frac{1}{N}\sum_{i=1}^N |x_i - \mu_{\text{panel}}|$$
   - **Fleiss' Generalized Multi-Rater Kappa ($\kappa$)** for categorical attribute assignments (Clean vs Taint vs Fault):
     $$\kappa = \frac{\bar{P} - \bar{P}_e}{1 - \bar{P}_e}$$
     with Landis-Koch agreement benchmarks (Poor $<0$, Slight $0.00-0.20$, Fair $0.21-0.40$, Moderate $0.41-0.60$, Substantial $0.61-0.80$, Almost Perfect $0.81-1.00$).
8. **Cryptographic Export/Import & Privacy Retention (S4-060, S4-065)**:
   - Local JSON/CSV backups sealed with SHA-256 integrity hashes.
   - Zero automatic cloud syncing; strictly requires user consent (`isConsentGranted = true`) for local export and file sharing.
   - PII privacy toggle to scrub cupper names and emails from shared evaluation packages.
9. **Bilingual & Inclusive Accessibility (S4-061, S4-062)**:
   - Full bilingual Indonesian (ID) and English (EN) labels for sensory descriptors and cupping terminology.
   - Semantic accessibility for smell/taste limitations (e.g. anosmia, parosmia, ageusia) emphasizing tactile mouthfeel and basic taste intensity.
   - Food-grade allergen checklists (sulfites, dairy, nuts in flavor references) with prominent non-clinical disclaimers.
10. **Controlled Template Schema & Migration Warnings (S4-063)**:
    - Versioned protocol schemas (`schemaVersion`, `formRevision`, `sourceUrl`).
    - Mismatch detection warning banner if evaluating with legacy or updated form templates.
11. **Evidence-Based Quality Gates (S4-064)**:
    - 100% green JVM unit tests, zero-data-loss installation on real hardware (`RRCN3008VYE`).

---

## Domain Architecture & Pure Kotlin Models

Located under `com.coffeecreator.app.core.sensory.operations`:
- `PanelRole`: `SESSION_HOST`, `BLIND_ASSESSOR`, `INDEPENDENT_EVALUATOR`.
- `CuppingDomainLinkage`: Links session sample to `beanId`, `roastBatchId`, `recipeId`, `grinderId`, and `dripperId`.
- `PanelAgreementStatistics`: Computes Pearson $r$, Mean Consensus Deviation, and Fleiss' Kappa with small-sample guards.
- `SensoryAccessibilityProfile`: Manages sensory accommodations (Anosmia mode, Hyposmia, Taste threshold mode) and Allergen alerts.
- `OperationalBackupPayload`: Standardized JSON audit export with cryptographic SHA-256 seal.
- `ConsentPolicy`: Guard requiring explicit opt-in confirmation for data sharing.
