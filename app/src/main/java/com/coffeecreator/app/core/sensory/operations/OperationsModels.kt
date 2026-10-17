package com.coffeecreator.app.core.sensory.operations

import java.util.UUID

/**
 * Roles in a structured blind or calibrated coffee sensory panel (S4-058).
 */
enum class SensoryPanelRole(val labelEn: String, val labelId: String) {
    SESSION_HOST("Session Host", "Penyelenggara Sesi"),
    LEAD_CUPPER("Lead Cupper / Moderator", "Ketua Cupper / Moderator"),
    ASSESSOR("Cupping Assessor", "Penilai / Cupper"),
    OBSERVER("Observer", "Pengamat")
}

/**
 * Status lifecycle of a blind sensory cupping session.
 */
enum class BlindSessionStatus(val labelEn: String, val labelId: String) {
    SETUP("Session Setup", "Pengaturan Sesi"),
    CONCEALED_TASTING("Blind Tasting in Progress", "Sesi Buta Berlangsung"),
    DELIBERATION("Panel Deliberation", "Musyawarah Panel"),
    REVEALED("Identities Revealed", "Identitas Terbuka"),
    AUDITED_LOCKED("Locked & Audited", "Terkunci & Diaudit")
}

/**
 * Assessor profile with pseudonym and calibration history (S4-058, S4-059).
 */
data class AssessorProfile(
    val id: String = UUID.randomUUID().toString(),
    val pseudonym: String,
    val role: SensoryPanelRole = SensoryPanelRole.ASSESSOR,
    val experienceYears: Int = 0,
    val calibratedDateEpoch: Long? = null,
    val anonymizedCode: String = "ASSESSOR-" + id.take(4).uppercase()
)

/**
 * Anonymized blind sample mapping with 3-digit randomized code (S4-058).
 */
data class BlindSampleMapping(
    val blindCode: String, // 3-digit code e.g. "482"
    val actualCoffeeBeanId: String,
    val actualRoastBatchId: String? = null,
    val actualRecipeId: String? = null,
    val isRevealed: Boolean = false,
    val revealedAtEpoch: Long? = null,
    val revealedByAssessorId: String? = null,
    val revealReason: String? = null
)

/**
 * Immutable historical link connecting cupping results to green beans, roast batches, recipes, and equipment (S4-055).
 * Guarantees zero mutation to historical recipe or bean entries.
 */
data class CuppingHistoricalLinkage(
    val linkageId: String = UUID.randomUUID().toString(),
    val assessmentId: String,
    val coffeeBeanId: String,
    val roastBatchId: String? = null,
    val manualBrewRecipeId: String? = null,
    val grinderModel: String? = null,
    val grinderSetting: String? = null,
    val dripperEquipment: String? = null,
    val createdAtEpoch: Long = System.currentTimeMillis(),
    val isImmutableSnapshot: Boolean = true
)

/**
 * Pairwise inter-rater correlation statistics between two assessors (S4-059).
 */
data class InterRaterStats(
    val raterAId: String,
    val raterBId: String,
    val sampleCount: Int,
    val pearsonCorrelation: Double?,
    val spearmanCorrelation: Double?,
    val meanDifference: Double,
    val isStatisticallySignificant: Boolean = false
)

/**
 * Comprehensive panel agreement and calibration report (S4-059).
 */
data class PanelAgreementReport(
    val sessionId: String,
    val sampleId: String,
    val attributeKey: String,
    val raterCount: Int,
    val consensusMean: Double,
    val standardDeviation: Double,
    val panelMeanConsensusDeviation: Double,
    val assessorMcds: Map<String, Double> = emptyMap(),
    val interRaterPairwiseStats: List<InterRaterStats> = emptyList(),
    val fleissKappa: Double? = null,
    val categoricalAgreementLabel: String? = null,
    val caveats: List<String> = emptyList(),
    val isHighCalibration: Boolean = panelMeanConsensusDeviation <= 0.35
)

/**
 * Versioned sensory evaluation template schema (S4-063).
 */
data class TemplateVersion(
    val templateId: String,
    val name: String,
    val versionMajor: Int,
    val versionMinor: Int,
    val schemaJson: String,
    val sha256Checksum: String,
    val isDeprecated: Boolean = false,
    val deprecationReason: String? = null
) {
    val versionTag: String get() = "v$versionMajor.$versionMinor"
}

/**
 * Sensory accessibility accommodation profile and allergen alerts (S4-062).
 * Displays mandatory non-clinical disclaimers.
 */
data class SensoryAccessibilityProfile(
    val assessorId: String,
    val hasAnosmia: Boolean = false,
    val hasAgeusia: Boolean = false,
    val hasParosmia: Boolean = false,
    val sensoryLimitationNotes: String = "",
    val allergenAlerts: List<String> = emptyList(),
    val colorBlindMode: Boolean = false,
    val highContrastEnabled: Boolean = false,
    val screenReaderOptimized: Boolean = false,
    val disclaimerAcknowledged: Boolean = false
) {
    companion object {
        const val MEDICAL_DISCLAIMER =
            "DISCLAIMER: Sensory accessibility settings are for testing accommodations only. " +
            "This application does not provide clinical diagnosis or medical evaluation for olfactory or gustatory impairments."
    }
}

/**
 * Privacy and user consent envelope for data export and sharing (S4-060, S4-065).
 */
data class DataExportConsent(
    val exportId: String = UUID.randomUUID().toString(),
    val consentGranted: Boolean,
    val timestampEpoch: Long = System.currentTimeMillis(),
    val includeSupplierLotPii: Boolean = false,
    val includeAssessorRealNames: Boolean = false,
    val targetUri: String = "local://exports/cupping_backup.json",
    val payloadSha256: String = ""
)

/**
 * Verification of citations in local RAG assistant responses (S4-057).
 */
data class CitationVerificationResult(
    val isValid: Boolean,
    val matchedCitations: List<String>,
    val ungroundedClaims: List<String>,
    val disclaimer: String = NON_CERTIFICATION_DISCLAIMER
) {
    companion object {
        const val NON_CERTIFICATION_DISCLAIMER =
            "DISCLAIMER: Coffee Creator is an independent educational and laboratory tool. " +
            "It does NOT confer official SCA or CQI Q Grader / R Grader certification."
    }
}

/**
 * Verification that deterministic math engine has absolute authority over LLM output (S4-056).
 */
data class DeterministicValidationResult(
    val isDecoupled: Boolean,
    val engineCalculation: Double,
    val reportedClaim: Double?,
    val discrepancyDetected: Boolean,
    val badge: String = if (discrepancyDetected) "DETERMINISTIC_ENGINE_OVERRIDE" else "DETERMINISTIC_ENGINE"
)
