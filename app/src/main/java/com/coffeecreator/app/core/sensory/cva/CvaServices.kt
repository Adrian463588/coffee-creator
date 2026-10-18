package com.coffeecreator.app.core.sensory.cva

import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import java.math.BigDecimal
import java.security.MessageDigest
import java.util.UUID
import kotlin.random.Random

interface CvaSessionService {
    fun createSession(
        protocol: CuppingProtocol,
        assessorId: String,
        assessorPseudonym: String,
        lots: List<Pair<String, String>>, // lotId to beanName
        prep: CuppingSamplePreparation = CuppingSamplePreparation()
    ): CuppingSession

    fun updatePreparation(session: CuppingSession, prep: CuppingSamplePreparation): CuppingSession
    fun updateDescriptive(session: CuppingSession, blindCode: String, descriptive: DescriptiveAssessment): CuppingSession
    fun updateAffective(session: CuppingSession, blindCode: String, affective: AffectiveAssessment): CuppingSession
    fun updatePhysical(session: CuppingSession, blindCode: String, physical: PhysicalAssessment): CuppingSession
    fun updateMultiCupGrid(session: CuppingSession, blindCode: String, grid: MultiCupEvaluationGrid): CuppingSession
    fun updateLegacySheet(session: CuppingSession, blindCode: String, legacy: Legacy2004CuppingSheet): CuppingSession
    fun revealExtrinsic(
        session: CuppingSession,
        blindCode: String,
        actorId: String,
        reason: String
    ): CuppingSession
    fun lockSession(session: CuppingSession, assessorSignature: String): CuppingSession
    fun amendSession(session: CuppingSession, amendedBy: String, reason: String): CuppingSession
}

class StandardCvaSessionService : CvaSessionService {

    override fun createSession(
        protocol: CuppingProtocol,
        assessorId: String,
        assessorPseudonym: String,
        lots: List<Pair<String, String>>,
        prep: CuppingSamplePreparation
    ): CuppingSession {
        require(lots.size <= 900) { "At most 900 samples can use unique three-digit blind codes." }
        val spec = when (protocol) {
            CuppingProtocol.SCA_CVA_CURRENT -> ProtocolSpec.SCA_CVA
            CuppingProtocol.SCA_2004_LEGACY -> ProtocolSpec.SCA_2004
            CuppingProtocol.CUSTOM_RESEARCH -> ProtocolSpec.CUSTOM
        }

        // Generate distinct 3-digit randomized blind codes (e.g. "824", "419")
        val random = Random(System.currentTimeMillis())
        val usedCodes = mutableSetOf<String>()
        val samples = lots.map { (lotId, beanName) ->
            var code: String
            do {
                code = "%03d".format(random.nextInt(100, 1000))
            } while (code in usedCodes)
            usedCodes.add(code)

            val blindSample = BlindSample(
                sampleId = "sample_${UUID.randomUUID()}",
                blindCode = code,
                actualLotId = lotId,
                actualBeanName = beanName,
                revealState = BlindRevealState.CONCEALED
            )
            SampleAssessment(blindSample = blindSample)
        }

        val session = CuppingSession(
            id = "cva_session_${UUID.randomUUID()}",
            protocolSpec = spec,
            assessorId = assessorId,
            assessorPseudonym = assessorPseudonym,
            createdAtEpochMs = System.currentTimeMillis(),
            state = SessionState.DRAFT,
            samplePreparation = prep,
            samples = samples
        )
        return session.copy(currentSha256Checksum = session.computeSha256())
    }

    override fun updatePreparation(session: CuppingSession, prep: CuppingSamplePreparation): CuppingSession {
        require(session.state != SessionState.LOCKED) { "Cannot modify locked session without creating an amendment." }
        val updated = session.copy(samplePreparation = prep)
        return updated.copy(currentSha256Checksum = updated.computeSha256())
    }

    override fun updateDescriptive(
        session: CuppingSession,
        blindCode: String,
        descriptive: DescriptiveAssessment
    ): CuppingSession {
        require(session.state != SessionState.LOCKED) { "Cannot modify locked session without creating an amendment." }
        val updatedSamples = session.samples.map { s ->
            if (s.blindSample.blindCode == blindCode) {
                s.copy(descriptive = descriptive, isCompleted = isSampleComplete(descriptive, s.affective))
            } else s
        }
        val updated = session.copy(samples = updatedSamples, state = SessionState.IN_PROGRESS)
        return updated.copy(currentSha256Checksum = updated.computeSha256())
    }

    override fun updateAffective(
        session: CuppingSession,
        blindCode: String,
        affective: AffectiveAssessment
    ): CuppingSession {
        require(session.state != SessionState.LOCKED) { "Cannot modify locked session without creating an amendment." }
        val updatedSamples = session.samples.map { s ->
            if (s.blindSample.blindCode == blindCode) {
                s.copy(affective = affective, isCompleted = isSampleComplete(s.descriptive, affective))
            } else s
        }
        val updated = session.copy(samples = updatedSamples, state = SessionState.IN_PROGRESS)
        return updated.copy(currentSha256Checksum = updated.computeSha256())
    }

    override fun updatePhysical(
        session: CuppingSession,
        blindCode: String,
        physical: PhysicalAssessment
    ): CuppingSession {
        require(session.state != SessionState.LOCKED) { "Cannot modify locked session without creating an amendment." }
        val updatedSamples = session.samples.map { s ->
            if (s.blindSample.blindCode == blindCode) {
                s.copy(physical = physical)
            } else s
        }
        val updated = session.copy(samples = updatedSamples)
        return updated.copy(currentSha256Checksum = updated.computeSha256())
    }

    override fun updateMultiCupGrid(
        session: CuppingSession,
        blindCode: String,
        grid: MultiCupEvaluationGrid
    ): CuppingSession {
        require(session.state != SessionState.LOCKED) { "Cannot modify locked session without creating an amendment." }
        val updatedSamples = session.samples.map { s ->
            if (s.blindSample.blindCode == blindCode) {
                s.copy(multiCupGrid = grid)
            } else s
        }
        val updated = session.copy(samples = updatedSamples)
        return updated.copy(currentSha256Checksum = updated.computeSha256())
    }

    override fun updateLegacySheet(
        session: CuppingSession,
        blindCode: String,
        legacy: Legacy2004CuppingSheet
    ): CuppingSession {
        require(session.state != SessionState.LOCKED) { "Cannot modify locked session without creating an amendment." }
        val updatedSamples = session.samples.map { s ->
            if (s.blindSample.blindCode == blindCode) {
                s.copy(legacySheet = legacy, isCompleted = true)
            } else s
        }
        val updated = session.copy(samples = updatedSamples, state = SessionState.IN_PROGRESS)
        return updated.copy(currentSha256Checksum = updated.computeSha256())
    }

    override fun revealExtrinsic(
        session: CuppingSession,
        blindCode: String,
        actorId: String,
        reason: String
    ): CuppingSession {
        require(session.state != SessionState.LOCKED) { "Cannot reveal a locked session without creating an amendment." }
        require(actorId.isNotBlank()) { "Assessor identity is required to reveal blinded sample identity." }
        require(actorId.trim() == session.assessorId) { "Only the assigned session assessor can reveal sample identity." }
        require(reason.isNotBlank()) { "An explicit reason is required to reveal blinded sample identity." }
        val target = session.samples.firstOrNull { it.blindSample.blindCode == blindCode }
            ?: throw IllegalArgumentException("Unknown blind sample code.")
        require(target.blindSample.revealState == BlindRevealState.CONCEALED &&
                target.blindSample.revealAuditHistory.orEmpty().isEmpty()) {
            "Sample identity has already been revealed; its audit record cannot be overwritten."
        }
        val now = System.currentTimeMillis()
        val updatedSamples = session.samples.map { s ->
            if (s.blindSample.blindCode == blindCode) {
                val updatedBlind = s.blindSample.copy(
                    revealState = BlindRevealState.REVEALED_AUDITED,
                    revealTimestampEpochMs = now,
                    revealReason = reason.trim(),
                    revealAuditHistory = s.blindSample.revealAuditHistory.orEmpty() + RevealAuditRecord(
                        auditId = "reveal_${UUID.randomUUID()}",
                        actorId = actorId.trim(),
                        reason = reason.trim(),
                        revealedAtEpochMs = now
                    )
                )
                s.copy(blindSample = updatedBlind)
            } else s
        }
        val updated = session.copy(samples = updatedSamples)
        return updated.copy(currentSha256Checksum = updated.computeSha256())
    }

    override fun lockSession(session: CuppingSession, assessorSignature: String): CuppingSession {
        require(session.state != SessionState.LOCKED) { "Session is already locked." }
        require(assessorSignature.isNotBlank()) { "Assessor signature is required to lock session." }
        require(session.samplePreparation.preparationEvidence in setOf(
            EvidenceStatus.MEASURED,
            EvidenceStatus.OBSERVED,
            EvidenceStatus.REPORTED
        )) { "Preparation evidence must be recorded before locking." }
        require(session.samples.isNotEmpty() && session.samples.all { sample ->
            sample.isCompleted && isSampleComplete(sample.descriptive, sample.affective)
        }) {
            "Every sample needs completed descriptive and affective observations before locking."
        }
        val now = System.currentTimeMillis()
        val locked = session.copy(
            state = SessionState.LOCKED,
            lockedAtEpochMs = now,
            assessorSignature = assessorSignature.trim()
        )
        return locked.copy(currentSha256Checksum = locked.computeSha256())
    }

    override fun amendSession(session: CuppingSession, amendedBy: String, reason: String): CuppingSession {
        require(session.state == SessionState.LOCKED) { "Session must be locked prior to amendment." }
        require(amendedBy.isNotBlank()) { "Assessor identity is required for amendment." }
        require(reason.isNotBlank()) { "Mandatory reason required for amendment." }
        val now = System.currentTimeMillis()
        val prevChecksum = session.currentSha256Checksum
        val amendment = SessionAmendment(
            amendmentId = "amend_${UUID.randomUUID()}",
            revision = session.amendmentHistory.size + 1,
            amendedAtEpochMs = now,
            amendedBy = amendedBy,
            reason = reason,
            previousSnapshotSha256 = prevChecksum.ifBlank { session.computeSha256() },
            previousSnapshotJson = session.toSnapshotJson()
        )
        val amended = session.copy(
            state = SessionState.AMENDED,
            amendmentHistory = session.amendmentHistory + amendment
        )
        return amended.copy(currentSha256Checksum = amended.computeSha256())
    }

    private fun isSampleComplete(descriptive: DescriptiveAssessment, affective: AffectiveAssessment): Boolean =
        CvaValidationEngine.isCompleteAssessment(descriptive, affective)
}

// ============================================================================
// S4-018: Guided Cupping Stepper & Timing Engine
// ============================================================================

enum class CuppingMilestone(val title: String, val secondsTarget: Int, val promptNote: String) {
    DRY_FRAGRANCE("Dry Grounds Fragrance", 0, "Evaluate dry aroma immediately after grinding (within 15 min of grinding)."),
    WATER_POUR("Hot Water Pour (93.5°C)", 30, "Pour 200mL boiling water (93.5°C) to rim; let steep undisturbed for 4:00 min."),
    CRUST_BREAK("Crust Break & Aroma Burst", 240, "At 4:00 min, break crust with 3 gentle stirs using cupping spoon. Sniff deep aroma burst."),
    HOT_CUPPING("Round 1: Hot Cupping (~70°C)", 480, "Skim foam. At 8:00-10:00 min (~70°C), slurp sample to evaluate flavor, aftertaste, and acidity."),
    WARM_CUPPING("Round 2: Warm Cupping (~60°C)", 720, "At 10:00-15:00 min (~60°C), evaluate peak sweetness, acidity character, and balance."),
    COOL_CUPPING("Round 3: Cool Cupping (~40°C)", 1080, "At 18:00-25:00 min (~40°C), evaluate body texture, clean cup, defects, and sweetness persistence.")
}

object CuppingStepperEngine {
    val ALL_MILESTONES = CuppingMilestone.values().toList()

    fun getActiveMilestone(elapsedSeconds: Int): CuppingMilestone {
        return when {
            elapsedSeconds < 30 -> CuppingMilestone.DRY_FRAGRANCE
            elapsedSeconds < 240 -> CuppingMilestone.WATER_POUR
            elapsedSeconds < 480 -> CuppingMilestone.CRUST_BREAK
            elapsedSeconds < 720 -> CuppingMilestone.HOT_CUPPING
            elapsedSeconds < 1080 -> CuppingMilestone.WARM_CUPPING
            else -> CuppingMilestone.COOL_CUPPING
        }
    }
}

// ============================================================================
// S4-025: Side-by-Side Multi-Coffee Comparison Engine
// ============================================================================

object CvaComparisonEngine {

    fun compareSamples(sampleA: SampleAssessment, sampleB: SampleAssessment): SampleComparisonDelta {
        val listA = sampleA.descriptive.getAllDescriptorNames()
        val listB = sampleB.descriptive.getAllDescriptorNames()

        val shared = listA.intersect(listB.toSet()).toList()
        val uniqueA = listA.minus(listB.toSet())
        val uniqueB = listB.minus(listA.toSet())

        return SampleComparisonDelta(
            sampleCodeA = sampleA.blindSample.blindCode,
            sampleCodeB = sampleB.blindSample.blindCode,
            flavorDelta = sampleA.descriptive.flavorIntensity - sampleB.descriptive.flavorIntensity,
            acidityDelta = sampleA.descriptive.acidityIntensity - sampleB.descriptive.acidityIntensity,
            sweetnessDelta = sampleA.descriptive.sweetnessIntensity - sampleB.descriptive.sweetnessIntensity,
            mouthfeelDelta = sampleA.descriptive.mouthfeelIntensity - sampleB.descriptive.mouthfeelIntensity,
            aftertasteDelta = sampleA.descriptive.aftertasteIntensity - sampleB.descriptive.aftertasteIntensity,
            sharedDescriptors = shared,
            uniqueDescriptorsA = uniqueA,
            uniqueDescriptorsB = uniqueB
        )
    }
}

// ============================================================================
// S4-022: Protocol Validation Engine
// ============================================================================

object CvaValidationEngine {
    private const val MAX_CUP_COUNT = 5
    private const val MIN_CUP_INDEX = 1
    private const val MAX_CUP_INDEX = 5
    private const val MAX_CUP_DEFECT_INTENSITY = 5.0
    private val UNRECORDED_SCORE = BigDecimal.ZERO
    private val LEGACY_MIN_SCORE = BigDecimal("6.00")
    private val LEGACY_MAX_SCORE = BigDecimal("10.00")

    fun isCompleteAssessment(descriptive: DescriptiveAssessment, affective: AffectiveAssessment): Boolean {
        val descriptiveIntensities = listOf(
            descriptive.fragranceDryIntensity,
            descriptive.fragranceAromaCrustIntensity,
            descriptive.fragranceBreakIntensity,
            descriptive.flavorIntensity,
            descriptive.aftertasteIntensity,
            descriptive.aftertastePersistence,
            descriptive.acidityIntensity,
            descriptive.sweetnessIntensity,
            descriptive.bitternessIntensity,
            descriptive.mouthfeelIntensity,
            descriptive.finishDuration
        )
        val affectiveScores = listOf(
            affective.fragranceAffectiveScore,
            affective.flavorAffectiveScore,
            affective.aftertasteAffectiveScore,
            affective.acidityAffectiveScore,
            affective.sweetnessAffectiveScore,
            affective.mouthfeelAffectiveScore,
            affective.overallAffectiveScore
        )
        return descriptive.evidenceStatus == EvidenceStatus.OBSERVED &&
            affective.evidenceStatus == EvidenceStatus.OBSERVED &&
            descriptiveIntensities.all(::validateDescriptiveIntensity) &&
            descriptiveIntensities.any { it > 0.0 } &&
            affectiveScores.all(::validateAffectiveScore) &&
            affective.defectDeduction >= BigDecimal.ZERO && affective.totalScore > BigDecimal.ZERO
    }

    fun validateDescriptiveIntensity(value: Double): Boolean {
        return value in 0.0..5.0
    }

    fun validateAffectiveScore(score: BigDecimal): Boolean {
        return score in BigDecimal("6.00")..BigDecimal("10.00")
    }

    fun isValidAssessment(sample: SampleAssessment): Boolean {
        return isValidPhysicalAssessment(sample.physical) &&
            isValidDescriptiveAssessment(sample.descriptive) &&
            isValidAffectiveAssessment(sample.affective) &&
            isValidMultiCupGrid(sample.multiCupGrid) &&
            isValidLegacySheet(sample.legacySheet)
    }

    fun isValidPreparation(preparation: CuppingSamplePreparation): Boolean {
        val requiredInputsArePositive = listOf(
            preparation.waterTdsPpm,
            preparation.brewRatio,
            preparation.doseGrams,
            preparation.waterVolumeMl
        ).all { it.isFinite() && it > 0.0 }
        val agtronIsValid = preparation.roastLevelAgtron?.let { it.isFinite() && it in 0.0..100.0 } ?: true
        return requiredInputsArePositive &&
            validateWaterTemperature(preparation.waterTempCelsius) &&
            preparation.cupCount > 0 && preparation.roastRestDays >= 0 &&
            (preparation.roastDateEpochMs == null || preparation.roastDateEpochMs >= 0L) &&
            agtronIsValid &&
            (preparation.grindMicrons == null || preparation.grindMicrons > 0) &&
            (preparation.equipment.grindMicrons == null || preparation.equipment.grindMicrons > 0) &&
            (preparation.equipment.bowlCountPerSample == null || preparation.equipment.bowlCountPerSample > 0)
    }

    fun isValidLockedSession(session: CuppingSession): Boolean {
        return session.state == SessionState.LOCKED &&
            session.lockedAtEpochMs != null && session.lockedAtEpochMs >= session.createdAtEpochMs &&
            !session.assessorSignature.isNullOrBlank() && session.samples.isNotEmpty() &&
            session.amendmentHistory.isEmpty() &&
            session.samplePreparation.preparationEvidence in
            setOf(EvidenceStatus.MEASURED, EvidenceStatus.OBSERVED, EvidenceStatus.REPORTED) &&
            isValidPreparation(session.samplePreparation) &&
            session.samples.all { sample ->
                sample.isCompleted && isValidAssessment(sample) &&
                    isCompleteAssessment(sample.descriptive, sample.affective)
            }
    }

    private fun isValidPhysicalAssessment(physical: PhysicalAssessment): Boolean =
        (physical.quakersCountIn100g == null || physical.quakersCountIn100g >= 0) &&
            listOfNotNull(
                physical.beanRoastColorAgtron,
                physical.groundRoastColorAgtron,
                physical.moisturePercent,
                physical.moistureUncertaintyPercent
            ).all { it.isFinite() && it in 0.0..100.0 }

    private fun isValidDescriptiveAssessment(descriptive: DescriptiveAssessment): Boolean {
        val intensities = listOf(
            descriptive.fragranceDryIntensity,
            descriptive.fragranceAromaCrustIntensity,
            descriptive.fragranceBreakIntensity,
            descriptive.flavorIntensity,
            descriptive.aftertasteIntensity,
            descriptive.aftertastePersistence,
            descriptive.acidityIntensity,
            descriptive.sweetnessIntensity,
            descriptive.bitternessIntensity,
            descriptive.mouthfeelIntensity,
            descriptive.finishDuration
        )
        return intensities.all(::validateDescriptiveIntensity) &&
            descriptive.cupDefects.all { defect ->
                defect.cupIndex in MIN_CUP_INDEX..MAX_CUP_INDEX &&
                    defect.intensity.isFinite() && defect.intensity in 1.0..MAX_CUP_DEFECT_INTENSITY
            }
    }

    private fun isValidAffectiveAssessment(affective: AffectiveAssessment): Boolean {
        val scores = listOf(
            affective.fragranceAffectiveScore,
            affective.flavorAffectiveScore,
            affective.aftertasteAffectiveScore,
            affective.acidityAffectiveScore,
            affective.sweetnessAffectiveScore,
            affective.mouthfeelAffectiveScore,
            affective.overallAffectiveScore
        )
        return scores.all { it == UNRECORDED_SCORE || validateAffectiveScore(it) } &&
            affective.defectDeduction >= UNRECORDED_SCORE && affective.totalScore >= UNRECORDED_SCORE
    }

    private fun isValidMultiCupGrid(grid: MultiCupEvaluationGrid): Boolean {
        return grid.cups.size <= MAX_CUP_COUNT &&
            grid.cups.map { it.cupIndex }.toSet().size == grid.cups.size &&
            grid.cups.all { cup ->
                cup.cupIndex in MIN_CUP_INDEX..MAX_CUP_INDEX &&
                    cup.defectIntensity.isFinite() && cup.defectIntensity in 0.0..MAX_CUP_DEFECT_INTENSITY
            }
    }

    private fun isValidLegacySheet(sheet: Legacy2004CuppingSheet): Boolean {
        val scores = listOf(
            sheet.fragranceScore,
            sheet.flavorScore,
            sheet.aftertasteScore,
            sheet.acidityScore,
            sheet.bodyScore,
            sheet.balanceScore,
            sheet.overallScore
        )
        val checkboxGroups = listOf(sheet.uniformityCheckboxes, sheet.cleanCupCheckboxes, sheet.sweetnessCheckboxes)
        return scores.all { score ->
            (score == UNRECORDED_SCORE || score in LEGACY_MIN_SCORE..LEGACY_MAX_SCORE) && score.scale() <= 2
        } && checkboxGroups.all { it.size == MAX_CUP_COUNT } &&
            sheet.taintCupCount in 0..MAX_CUP_COUNT && sheet.faultCupCount in 0..MAX_CUP_COUNT &&
            sheet.taintCupCount + sheet.faultCupCount <= MAX_CUP_COUNT
    }

    fun validateWaterTemperature(celsius: Double): Boolean {
        return celsius in 85.0..99.0
    }
}

// ============================================================================
// S4-024: Native Data Export Service
// ============================================================================

object CvaReportExportService {
    private val gson = GsonBuilder().serializeNulls().disableHtmlEscaping().create()

    fun exportToNativeJson(session: CuppingSession): String {
        requireValidCurrentPreparation(session)
        val report = exportPayload(session)
        val checksum = sha256(canonicalJson(report))
        return canonicalJson(report.toMutableMap().apply {
            put("checksumSha256", checksum)
            put(
                "checksumScope",
                "SHA-256 of canonical JSON payload excluding checksumSha256 and checksumScope; not a digital signature."
            )
        })
    }

    fun exportToNativeCsv(session: CuppingSession): String {
        requireValidCurrentPreparation(session)
        return buildString {
            append("SessionId,Protocol,Assessor,BlindCode,Revealed,ActualBean,FlavorIntensity,AcidityIntensity,SweetnessIntensity,MouthfeelIntensity,CleanCups,AffectiveScore,PreparationJson,AssessmentJson,AmendmentsJson,RowPayloadSha256\n")
            val amendmentsJson = gson.toJson(exportableAmendments(session))
            session.samples.forEach { s ->
                val revealed = s.blindSample.revealState == BlindRevealState.REVEALED_AUDITED
                val actual = if (revealed) s.blindSample.actualBeanName else "CONCEALED"
                val safeSample = exportableSample(s)
                val payloadCells = listOf(
                    session.id,
                    session.protocolSpec.protocol.name,
                    session.assessorPseudonym,
                    s.blindSample.blindCode,
                    revealed.toString(),
                    actual,
                    s.descriptive.flavorIntensity.toString(),
                    s.descriptive.acidityIntensity.toString(),
                    s.descriptive.sweetnessIntensity.toString(),
                    s.descriptive.mouthfeelIntensity.toString(),
                    s.multiCupGrid.cleanCupCount.toString(),
                    s.affective.totalScore.toPlainString(),
                    gson.toJson(session.samplePreparation),
                    gson.toJson(safeSample),
                    amendmentsJson
                )
                val payload = payloadCells.joinToString(",", transform = ::csvCell) + "\n"
                append(payloadCells.joinToString(",", transform = ::csvCell))
                    .append(',')
                    .append(csvCell(sha256(payload)))
                    .append('\n')
            }
        }
    }

    private fun requireValidCurrentPreparation(session: CuppingSession) {
        require(CvaValidationEngine.isValidPreparation(session.samplePreparation)) {
            "Current session preparation is invalid; export was stopped."
        }
    }

    private fun exportPayload(session: CuppingSession): Map<String, Any?> = linkedMapOf(
        "\$schema" to "https://coffeecreator.app/schemas/cva-cupping-v1.json",
        "sessionId" to session.id,
        "protocol" to session.protocolSpec.protocol.name,
        "protocolVersion" to session.protocolSpec.version,
        "fieldMappingVersion" to session.protocolSpec.fieldMappingVersion,
        "protocolSpec" to session.protocolSpec,
        "disclaimer" to session.protocolSpec.officialDisclaimer,
        "assessor" to session.assessorPseudonym,
        "createdAtEpochMs" to session.createdAtEpochMs,
        "lockedAtEpochMs" to session.lockedAtEpochMs,
        "state" to session.state.name,
        "preparation" to session.samplePreparation,
        "samples" to session.samples.map(::exportableSample),
        "amendments" to exportableAmendments(session)
    )

    private fun exportableSample(sample: SampleAssessment): SampleAssessment {
        val isRevealed = sample.blindSample.revealState == BlindRevealState.REVEALED_AUDITED
        val blindSample = if (isRevealed) {
            sample.blindSample.copy(revealAuditHistory = sample.blindSample.revealAuditHistory.orEmpty())
        } else {
            sample.blindSample.copy(
                actualLotId = "",
                actualBeanName = "",
                revealState = BlindRevealState.CONCEALED,
                revealTimestampEpochMs = null,
                revealReason = null,
                revealAuditHistory = emptyList()
            )
        }
        return sample.copy(
            blindSample = blindSample,
            physical = sample.physical.copy(greenLotIdRef = sample.physical.greenLotIdRef.takeIf { isRevealed }),
            extrinsic = sample.extrinsic.takeIf { isRevealed }
        )
    }

    private fun exportableAmendments(session: CuppingSession): List<Map<String, Any?>> =
        session.amendmentHistory.map { amendment ->
            val previousSnapshot = runCatching {
                gson.fromJson(amendment.previousSnapshotJson, CuppingSession::class.java)
            }.getOrNull() ?: throw IllegalArgumentException(
                "Amendment ${amendment.amendmentId} has no valid previous snapshot; export was stopped."
            )
            require(
                previousSnapshot.id == session.id && CvaValidationEngine.isValidLockedSession(previousSnapshot)
            ) {
                "Amendment ${amendment.amendmentId} previous snapshot is invalid or does not match this session."
            }
            val verifiedSourceChecksum = runCatching {
                require(previousSnapshot.currentSha256Checksum == amendment.previousSnapshotSha256)
                require(previousSnapshot.toSnapshotJson() == amendment.previousSnapshotJson)
                previousSnapshot.computeSha256()
            }.getOrElse {
                throw IllegalArgumentException(
                    "Amendment ${amendment.amendmentId} previous snapshot could not be verified.",
                    it
                )
            }
            require(verifiedSourceChecksum == amendment.previousSnapshotSha256) {
                "Amendment ${amendment.amendmentId} previous snapshot checksum does not match."
            }
            val safePreviousSnapshot = previousSnapshot?.let(::exportableSnapshot)
            linkedMapOf(
                "amendmentId" to amendment.amendmentId,
                "revision" to amendment.revision,
                "amendedAtEpochMs" to amendment.amendedAtEpochMs,
                "amendedBy" to amendment.amendedBy,
                "reason" to amendment.reason,
                "sourceSnapshotSha256" to amendment.previousSnapshotSha256,
                "previousSnapshotSha256" to safePreviousSnapshot?.let { sha256(canonicalJson(it)) },
                "previousSnapshot" to safePreviousSnapshot
            )
        }

    private fun exportableSnapshot(session: CuppingSession): Map<String, Any?> = linkedMapOf(
        "sessionId" to session.id,
        "protocol" to session.protocolSpec.protocol.name,
        "protocolVersion" to session.protocolSpec.version,
        "fieldMappingVersion" to session.protocolSpec.fieldMappingVersion,
        "protocolSpec" to session.protocolSpec,
        "assessor" to session.assessorPseudonym,
        "createdAtEpochMs" to session.createdAtEpochMs,
        "lockedAtEpochMs" to session.lockedAtEpochMs,
        "state" to session.state.name,
        "preparation" to session.samplePreparation,
        "samples" to session.samples.map(::exportableSample),
        "amendments" to session.amendmentHistory.map { amendment ->
            mapOf(
                "amendmentId" to amendment.amendmentId,
                "revision" to amendment.revision,
                "amendedAtEpochMs" to amendment.amendedAtEpochMs,
                "amendedBy" to amendment.amendedBy,
                "reason" to amendment.reason
            )
        }
    )

    private fun sha256(value: String): String =
        MessageDigest.getInstance("SHA-256")
            .digest(value.toByteArray(Charsets.UTF_8))
            .joinToString("") { "%02x".format(it) }

    private fun canonicalJson(value: Any): String = canonicalize(gson.toJsonTree(value))

    private fun canonicalize(element: JsonElement): String = gson.toJson(sortKeys(element))

    private fun sortKeys(element: JsonElement): JsonElement = when {
        element.isJsonObject -> JsonObject().apply {
            element.asJsonObject.entrySet().sortedBy { it.key }.forEach { (key, value) -> add(key, sortKeys(value)) }
        }
        element.isJsonArray -> JsonArray().apply { element.asJsonArray.forEach { add(sortKeys(it)) } }
        else -> element
    }

    private fun csvCell(value: String): String {
        val firstMeaningful = value.trimStart().firstOrNull()
        val safeValue = if (firstMeaningful in listOf('=', '+', '-', '@', '\t', '\r')) "'$value" else value
        return "\"${safeValue.replace("\"", "\"\"")}\""
    }
}
