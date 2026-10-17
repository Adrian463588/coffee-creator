package com.coffeecreator.app.core.sensory.cva

import java.math.BigDecimal
import java.security.MessageDigest

enum class CuppingProtocol {
    SCA_CVA_CURRENT,
    SCA_2004_LEGACY,
    CUSTOM_RESEARCH
}

enum class AssessmentPerspective {
    PHYSICAL,
    DESCRIPTIVE,
    AFFECTIVE,
    EXTRINSIC
}

enum class EvidenceStatus {
    MEASURED,
    OBSERVED,
    REPORTED,
    INFERRED,
    UNKNOWN,
    NOT_MEASURED,
    OUT_OF_SCOPE,
    PENDING_REVIEW
}

enum class SessionState {
    DRAFT,
    PREPARED,
    IN_PROGRESS,
    REVIEW,
    LOCKED,
    AMENDED
}

enum class BlindRevealState {
    CONCEALED,
    REVEALED_AUDITED,
    REVEALED_PUBLIC
}

data class ProtocolSpec(
    val id: String,
    val protocol: CuppingProtocol,
    val version: String,
    val sourceUrl: String,
    val retrievedAt: String,
    val fieldMappingVersion: String,
    val licenseStatus: String,
    val officialDisclaimer: String = "SCA-aligned research implementation. Not an official certification grant."
) {
    companion object {
        val SCA_CVA = ProtocolSpec(
            id = "cva_current_2024",
            protocol = CuppingProtocol.SCA_CVA_CURRENT,
            version = "2024.1",
            sourceUrl = "https://sca.coffee/value-assessment/",
            retrievedAt = "2026-10-17",
            fieldMappingVersion = "1.0",
            licenseStatus = "Public Standards-aligned",
            officialDisclaimer = "Informed by the public SCA Coffee Value Assessment standard. Not an official certification grant."
        )

        val SCA_2004 = ProtocolSpec(
            id = "sca_legacy_2004",
            protocol = CuppingProtocol.SCA_2004_LEGACY,
            version = "2004.1",
            sourceUrl = "https://sca.coffee/sca-news?category=Features",
            retrievedAt = "2026-10-17",
            fieldMappingVersion = "1.0",
            licenseStatus = "Historical Standard Reference",
            officialDisclaimer = "HISTORICAL LEGACY FORM: Superseded by CVA standards. Scores must not be equated to modern CVA evaluations."
        )

        val CUSTOM = ProtocolSpec(
            id = "custom_lab_2026",
            protocol = CuppingProtocol.CUSTOM_RESEARCH,
            version = "1.0",
            sourceUrl = "local://custom",
            retrievedAt = "2026-10-17",
            fieldMappingVersion = "1.0",
            licenseStatus = "User Defined",
            officialDisclaimer = "Custom sensory experiment. For internal laboratory calibration only."
        )
    }
}

data class CuppingSamplePreparation(
    val roastLevelAgtron: Double? = null,
    val roastDateEpochMs: Long? = null,
    val waterTdsPpm: Double? = null,
    val waterTempCelsius: Double = 93.0,
    val grindMicrons: Int? = null,
    val brewRatio: Double = 0.055, // 55g/L standard SCA
    val doseGrams: Double = 11.0,
    val waterVolumeMl: Double = 200.0,
    val cupCount: Int = 5,
    val preparationEvidence: EvidenceStatus = EvidenceStatus.MEASURED
)

data class BlindSample(
    val sampleId: String,
    val blindCode: String, // 3-digit opaque code e.g. "824"
    val actualLotId: String,
    val actualBeanName: String = "",
    val revealState: BlindRevealState = BlindRevealState.CONCEALED,
    val revealTimestampEpochMs: Long? = null,
    val revealReason: String? = null
)

data class PhysicalAssessment(
    val beanRoastColorAgtron: Double? = null,
    val groundRoastColorAgtron: Double? = null,
    val greenLotIdRef: String? = null,
    val quakersCountIn100g: Int? = null,
    val defectNotes: String = "",
    val evidenceStatus: EvidenceStatus = EvidenceStatus.NOT_MEASURED
)

enum class AcidityType {
    CITRIC,
    MALIC,
    TARTARIC,
    PHOSPHORIC,
    ACETIC,
    LACTIC,
    QUINIC
}

enum class BodyTextureType {
    WATERY,
    TEA_LIKE,
    SILKY,
    VELVETY,
    CREAMY,
    SYRUPY,
    OILY,
    CHALKY,
    ASTRINGENT
}

data class CupDefectRecord(
    val cupIndex: Int, // 1 to 5
    val defectType: String, // e.g. "Taint", "Fault", "Mold", "Ferment"
    val intensity: Double, // 1.0 to 5.0
    val description: String
)

data class DescriptiveAssessment(
    val fragranceDryIntensity: Double = 0.0,      // 0.0 to 5.0
    val fragranceAromaCrustIntensity: Double = 0.0, // 0.0 to 5.0
    val fragranceBreakIntensity: Double = 0.0,      // 0.0 to 5.0
    val flavorIntensity: Double = 0.0,             // 0.0 to 5.0
    val aftertasteIntensity: Double = 0.0,         // 0.0 to 5.0
    val aftertastePersistence: Double = 0.0,       // 0.0 to 5.0
    val acidityIntensity: Double = 0.0,            // 0.0 to 5.0
    val acidityTypes: List<AcidityType> = emptyList(),
    val sweetnessIntensity: Double = 0.0,          // 0.0 to 5.0
    val mouthfeelIntensity: Double = 0.0,          // 0.0 to 5.0
    val mouthfeelBodyType: BodyTextureType = BodyTextureType.SILKY,
    val descriptors: List<String> = emptyList(),
    val cupDefects: List<CupDefectRecord> = emptyList(),
    val sensoryNotes: String = "",
    val evidenceStatus: EvidenceStatus = EvidenceStatus.OBSERVED
)

data class AffectiveAssessment(
    val fragranceAffectiveScore: BigDecimal = BigDecimal("6.00"), // 6.00 to 10.00
    val flavorAffectiveScore: BigDecimal = BigDecimal("6.00"),
    val aftertasteAffectiveScore: BigDecimal = BigDecimal("6.00"),
    val acidityAffectiveScore: BigDecimal = BigDecimal("6.00"),
    val sweetnessAffectiveScore: BigDecimal = BigDecimal("6.00"),
    val mouthfeelAffectiveScore: BigDecimal = BigDecimal("6.00"),
    val overallAffectiveScore: BigDecimal = BigDecimal("6.00"),
    val defectDeduction: BigDecimal = BigDecimal.ZERO,
    val notes: String = "",
    val evidenceStatus: EvidenceStatus = EvidenceStatus.OBSERVED
) {
    val totalScore: BigDecimal
        get() = fragranceAffectiveScore + flavorAffectiveScore + aftertasteAffectiveScore +
                acidityAffectiveScore + sweetnessAffectiveScore + mouthfeelAffectiveScore +
                overallAffectiveScore - defectDeduction
}

data class ExtrinsicAssessment(
    val originCountry: String,
    val region: String? = null,
    val farmOrCoop: String? = null,
    val producer: String? = null,
    val altitudeMetersMin: Int? = null,
    val altitudeMetersMax: Int? = null,
    val variety: String? = null,
    val processingMethod: String? = null,
    val harvestYear: String? = null,
    val certifications: List<String> = emptyList(),
    val purchasePricePerKg: BigDecimal? = null,
    val currency: String? = null,
    val traceabilityUrl: String? = null,
    val evidenceStatus: EvidenceStatus = EvidenceStatus.REPORTED
)

data class SampleAssessment(
    val blindSample: BlindSample,
    val physical: PhysicalAssessment = PhysicalAssessment(),
    val descriptive: DescriptiveAssessment = DescriptiveAssessment(),
    val affective: AffectiveAssessment = AffectiveAssessment(),
    val extrinsic: ExtrinsicAssessment? = null,
    val isCompleted: Boolean = false
)

data class SessionAmendment(
    val amendmentId: String,
    val revision: Int,
    val amendedAtEpochMs: Long,
    val amendedBy: String,
    val reason: String,
    val previousSnapshotSha256: String
)

data class CuppingSession(
    val id: String,
    val protocolSpec: ProtocolSpec,
    val assessorId: String,
    val assessorPseudonym: String,
    val createdAtEpochMs: Long,
    val lockedAtEpochMs: Long? = null,
    val state: SessionState = SessionState.DRAFT,
    val samplePreparation: CuppingSamplePreparation = CuppingSamplePreparation(),
    val samples: List<SampleAssessment> = emptyList(),
    val amendmentHistory: List<SessionAmendment> = emptyList(),
    val currentSha256Checksum: String = ""
) {
    fun computeSha256(): String {
        val payload = buildString {
            append(id).append("|")
            append(protocolSpec.id).append("|")
            append(assessorId).append("|")
            append(createdAtEpochMs).append("|")
            append(state.name).append("|")
            samples.forEach { s ->
                append(s.blindSample.blindCode).append(":")
                append(s.descriptive.flavorIntensity).append(":")
                append(s.affective.totalScore).append(";")
            }
        }
        val digest = MessageDigest.getInstance("SHA-256")
        return digest.digest(payload.toByteArray(Charsets.UTF_8)).joinToString("") { "%02x".format(it) }
    }
}
