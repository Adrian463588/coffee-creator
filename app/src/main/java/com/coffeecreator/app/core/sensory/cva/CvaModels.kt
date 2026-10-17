package com.coffeecreator.app.core.sensory.cva

import com.coffeecreator.app.core.journal.BrewJournalEntry
import com.coffeecreator.app.core.sensory.FlavorDescriptor
import com.coffeecreator.app.core.sensory.SensoryTaxonomyRegistry
import java.math.BigDecimal
import java.math.RoundingMode
import java.security.MessageDigest
import java.util.UUID

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

// ============================================================================
// S4-011: Equipment & Controlled Preparation Metadata
// ============================================================================

data class CuppingEquipment(
    val grinderModel: String = "Mahlkönig EK43S",
    val burrType: String = "98mm Cast Sweet",
    val grindMicrons: Int = 850,
    val grindSettingDescription: String = "Setting 8.5 (70-75% passing 850µm / 20 mesh)",
    val kettleModel: String = "Fellow Stagg EKG Pro",
    val waterSpoutType: String = "Gooseneck Flow-Restricted",
    val cuppingBowlType: String = "Tempered Ceramic 200mL",
    val bowlCountPerSample: Int = 5,
    val cuppingSpoonMaterial: String = "Silver-Plated Ergonomic Deep-Bowl"
)

data class CuppingSamplePreparation(
    val equipment: CuppingEquipment = CuppingEquipment(),
    val roastLevelAgtron: Double? = null,
    val roastDateEpochMs: Long? = null,
    val roastRestDays: Int = 3,
    val waterTdsPpm: Double = 125.0, // SCA standard: 75-150 ppm
    val waterTempCelsius: Double = 93.5, // SCA standard: 92.2 - 94.4°C
    val grindMicrons: Int? = 850,
    val brewRatio: Double = 0.055, // 55g/L standard SCA (1:18.18)
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
    val moisturePercent: Double? = null,
    val moistureUncertaintyPercent: Double = 0.2,
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

// ============================================================================
// S4-021: 5-Cup Multi-Cup Evaluation Grid & Defects
// ============================================================================

enum class CupDefectCategory(val penaltyPerCup: BigDecimal) {
    NONE(BigDecimal.ZERO),
    TAINT(BigDecimal("2.00")), // Noticeable off-flavor that does not overwhelm
    FAULT(BigDecimal("4.00"))  // Overwhelming, repulsive defect (mold, phenolic, potato)
}

enum class CupDefectCharacter(val labelEn: String, val labelId: String) {
    PHENOLIC_CHEMICAL("Phenolic / Chemical", "Fenolik / Kimia"),
    MOLDY_MUSTY("Moldy / Musty", "Berjamur / Lembap"),
    POTATO_DEFECT("Potato Defect (PTD)", "Cacat Kentang (PTD)"),
    FERMENTED_SOUR("Over-fermented / Vinegar", "Terfermentasi / Cuka"),
    BAGGY_JUTE("Baggy / Jute fiber", "Karung Goni / Apek"),
    QUAKER_PEANUTTY("Quaker / Grassy peanut", "Quaker / Kacang mentah")
}

data class SingleCupEvaluation(
    val cupIndex: Int, // 1 to 5
    val isClean: Boolean = true,
    val defectCategory: CupDefectCategory = CupDefectCategory.NONE,
    val defectCharacter: CupDefectCharacter? = null,
    val defectIntensity: Double = 0.0, // 1.0 to 5.0
    val notes: String = ""
)

data class MultiCupEvaluationGrid(
    val cups: List<SingleCupEvaluation> = (1..5).map { SingleCupEvaluation(cupIndex = it) }
) {
    val cleanCupCount: Int get() = cups.count { it.isClean && it.defectCategory == CupDefectCategory.NONE }
    val uniformityScore: BigDecimal get() = BigDecimal(cleanCupCount * 2).setScale(2, RoundingMode.HALF_UP)
    val cleanCupScore: BigDecimal get() = BigDecimal(cleanCupCount * 2).setScale(2, RoundingMode.HALF_UP)
    val totalDefectDeduction: BigDecimal get() = cups.fold(BigDecimal.ZERO) { acc, c -> acc + c.defectCategory.penaltyPerCup }
}

data class CupDefectRecord(
    val cupIndex: Int, // 1 to 5
    val defectType: String, // e.g. "Taint", "Fault", "Mold", "Ferment"
    val intensity: Double, // 1.0 to 5.0
    val description: String
)

// ============================================================================
// S4-014: Descriptive Assessment with 221-Descriptor Sensory Wheel
// ============================================================================

data class DescriptiveAssessment(
    val fragranceDryIntensity: Double = 0.0,        // 0.0 to 5.0
    val fragranceAromaCrustIntensity: Double = 0.0, // 0.0 to 5.0
    val fragranceBreakIntensity: Double = 0.0,      // 0.0 to 5.0
    val flavorIntensity: Double = 0.0,             // 0.0 to 5.0
    val aftertasteIntensity: Double = 0.0,         // 0.0 to 5.0
    val aftertastePersistence: Double = 0.0,       // 0.0 to 5.0
    val acidityIntensity: Double = 0.0,            // 0.0 to 5.0
    val acidityTypes: List<AcidityType> = emptyList(),
    val sweetnessIntensity: Double = 0.0,          // 0.0 to 5.0
    val bitternessIntensity: Double = 0.0,         // 0.0 to 5.0
    val mouthfeelIntensity: Double = 0.0,          // 0.0 to 5.0
    val mouthfeelBodyType: BodyTextureType = BodyTextureType.SILKY,
    val finishDuration: Double = 0.0,              // 0.0 to 5.0
    val descriptors: List<String> = emptyList(),
    val selectedDescriptorIds: List<String> = emptyList(), // References 221-descriptor wheel IDs
    val cupDefects: List<CupDefectRecord> = emptyList(),
    val sensoryNotes: String = "",
    val evidenceStatus: EvidenceStatus = EvidenceStatus.OBSERVED
) {
    // Resolved descriptors from the 221-descriptor SensoryTaxonomyRegistry
    fun getResolvedDescriptors(): List<FlavorDescriptor> {
        val all = SensoryTaxonomyRegistry.getAllDescriptors()
        val fromIds = selectedDescriptorIds.mapNotNull { id -> all.firstOrNull { it.id == id } }
        val fromNames = descriptors.mapNotNull { name -> all.firstOrNull { it.name.equals(name, ignoreCase = true) } }
        return (fromIds + fromNames).distinctBy { it.id }
    }

    fun getAllDescriptorNames(): List<String> {
        val all = SensoryTaxonomyRegistry.getAllDescriptors()
        val fromIds = selectedDescriptorIds.mapNotNull { id ->
            all.firstOrNull { it.id == id }?.name ?: id
        }
        return (fromIds + descriptors).distinct()
    }
}

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

// ============================================================================
// S4-026: Historic SCA 2004 Legacy Cupping Model
// ============================================================================

data class Legacy2004CuppingSheet(
    val fragranceScore: BigDecimal = BigDecimal("8.00"), // 6.00 to 10.00
    val flavorScore: BigDecimal = BigDecimal("8.00"),
    val aftertasteScore: BigDecimal = BigDecimal("8.00"),
    val acidityScore: BigDecimal = BigDecimal("8.00"),
    val bodyScore: BigDecimal = BigDecimal("8.00"),
    val balanceScore: BigDecimal = BigDecimal("8.00"),
    val uniformityCheckboxes: List<Boolean> = List(5) { true }, // 2.0 pts each
    val cleanCupCheckboxes: List<Boolean> = List(5) { true },   // 2.0 pts each
    val sweetnessCheckboxes: List<Boolean> = List(5) { true },  // 2.0 pts each
    val overallScore: BigDecimal = BigDecimal("8.00"),
    val taintCupCount: Int = 0, // -2 pts each
    val faultCupCount: Int = 0, // -4 pts each
    val notes: String = ""
) {
    val uniformityScore: BigDecimal get() = BigDecimal(uniformityCheckboxes.count { it } * 2)
    val cleanCupScore: BigDecimal get() = BigDecimal(cleanCupCheckboxes.count { it } * 2)
    val sweetnessScore: BigDecimal get() = BigDecimal(sweetnessCheckboxes.count { it } * 2)
    val defectDeduction: BigDecimal get() = BigDecimal(taintCupCount * 2 + faultCupCount * 4)

    val totalScore: BigDecimal get() = fragranceScore + flavorScore + aftertasteScore +
            acidityScore + bodyScore + balanceScore + uniformityScore +
            cleanCupScore + sweetnessScore + overallScore - defectDeduction
}

// ============================================================================
// S4-025: Side-by-Side Comparison Delta Model
// ============================================================================

data class SampleComparisonDelta(
    val sampleCodeA: String,
    val sampleCodeB: String,
    val flavorDelta: Double,
    val acidityDelta: Double,
    val sweetnessDelta: Double,
    val mouthfeelDelta: Double,
    val aftertasteDelta: Double,
    val sharedDescriptors: List<String>,
    val uniqueDescriptorsA: List<String>,
    val uniqueDescriptorsB: List<String>
)

data class SampleAssessment(
    val blindSample: BlindSample,
    val physical: PhysicalAssessment = PhysicalAssessment(),
    val descriptive: DescriptiveAssessment = DescriptiveAssessment(),
    val affective: AffectiveAssessment = AffectiveAssessment(),
    val multiCupGrid: MultiCupEvaluationGrid = MultiCupEvaluationGrid(),
    val legacySheet: Legacy2004CuppingSheet = Legacy2004CuppingSheet(),
    val extrinsic: ExtrinsicAssessment? = null,
    val isCompleted: Boolean = false
) {
    // 1-Click Brew Journal Integration Bridge
    fun toBrewJournalEntry(
        session: CuppingSession,
        ratingOverride: Double? = null,
        customNotes: String = ""
    ): BrewJournalEntry {
        val displayName = if (blindSample.revealState != BlindRevealState.CONCEALED) {
            blindSample.actualBeanName.ifBlank { "Lot ${blindSample.actualLotId}" }
        } else {
            "Sample #${blindSample.blindCode}"
        }

        // Map 221-wheel descriptors into tasteNotes
        val descriptorsList = descriptive.getAllDescriptorNames()

        // Calculate rating on 1.0 to 10.0 scale from affective score (e.g. 60.25 / 7 = ~8.6)
        val calculatedRating = ratingOverride ?: (affective.totalScore.toDouble() / 7.0).coerceIn(1.0, 10.0)

        val sensoryScoresMap = mapOf(
            "aroma" to (descriptive.fragranceAromaCrustIntensity * 2.0).toInt().coerceIn(1, 10),
            "acidity" to (descriptive.acidityIntensity * 2.0).toInt().coerceIn(1, 10),
            "sweetness" to (descriptive.sweetnessIntensity * 2.0).toInt().coerceIn(1, 10),
            "body" to (descriptive.mouthfeelIntensity * 2.0).toInt().coerceIn(1, 10),
            "finish" to (descriptive.aftertasteIntensity * 2.0).toInt().coerceIn(1, 10)
        )

        val compiledNotes = buildString {
            append("[CVA Cupping: ${session.protocolSpec.protocol.name}] ")
            if (blindSample.revealState == BlindRevealState.REVEALED_AUDITED) {
                append("(Revealed: ${blindSample.revealReason}) ")
            }
            if (extrinsic != null && blindSample.revealState != BlindRevealState.CONCEALED) {
                append("Origin: ${extrinsic.originCountry}, ${extrinsic.region ?: ""}. Variety: ${extrinsic.variety ?: ""}. ")
            }
            if (descriptive.sensoryNotes.isNotBlank()) {
                append("Descriptive notes: ${descriptive.sensoryNotes}. ")
            }
            if (affective.notes.isNotBlank()) {
                append("Affective notes: ${affective.notes}. ")
            }
            if (customNotes.isNotBlank()) {
                append(customNotes)
            }
        }.trim()

        return BrewJournalEntry(
            id = UUID.randomUUID().toString(),
            beanId = blindSample.actualLotId,
            beanName = displayName,
            brewMethod = "Cupping (${session.protocolSpec.protocol.name})",
            equipment = session.samplePreparation.equipment.grinderModel,
            doseGrams = session.samplePreparation.doseGrams,
            waterGrams = session.samplePreparation.waterVolumeMl,
            beverageMassGrams = session.samplePreparation.waterVolumeMl * 0.95,
            waterTemperatureCelsius = session.samplePreparation.waterTempCelsius,
            brewDurationSeconds = 240, // 4:00 min crust break
            drawdownSeconds = null,
            rating = (calculatedRating * 10.0).toInt() / 10.0,
            tasteNotes = descriptorsList,
            sensoryScores = sensoryScoresMap,
            generalNotes = compiledNotes,
            tags = listOf("cupping", "cva", "graded", if (blindSample.revealState != BlindRevealState.CONCEALED) "revealed" else "blind")
        )
    }
}

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
