package com.coffeecreator.app.core.sensory.cva

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
    fun revealExtrinsic(session: CuppingSession, blindCode: String, reason: String): CuppingSession
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
        val spec = when (protocol) {
            CuppingProtocol.SCA_CVA_CURRENT -> ProtocolSpec.SCA_CVA
            CuppingProtocol.SCA_2004_LEGACY -> ProtocolSpec.SCA_2004
            CuppingProtocol.CUSTOM_RESEARCH -> ProtocolSpec.CUSTOM
        }

        // Generate distinct 3-digit randomized blind codes (e.g. "824", "419")
        val random = Random(System.currentTimeMillis())
        val usedCodes = mutableSetOf<String>()
        val samples = lots.mapIndexed { idx, (lotId, beanName) ->
            var code: String
            do {
                code = "%03d".format(random.nextInt(100, 1000))
            } while (code in usedCodes)
            usedCodes.add(code)

            val blindSample = BlindSample(
                sampleId = "sample_${UUID.randomUUID().toString().take(8)}",
                blindCode = code,
                actualLotId = lotId,
                actualBeanName = beanName,
                revealState = BlindRevealState.CONCEALED
            )
            SampleAssessment(blindSample = blindSample)
        }

        val session = CuppingSession(
            id = "cva_session_${UUID.randomUUID().toString().take(8)}",
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

    override fun revealExtrinsic(
        session: CuppingSession,
        blindCode: String,
        reason: String
    ): CuppingSession {
        require(reason.isNotBlank()) { "An explicit reason is required to reveal blinded sample identity." }
        val now = System.currentTimeMillis()
        val updatedSamples = session.samples.map { s ->
            if (s.blindSample.blindCode == blindCode) {
                val updatedBlind = s.blindSample.copy(
                    revealState = BlindRevealState.REVEALED_AUDITED,
                    revealTimestampEpochMs = now,
                    revealReason = reason
                )
                s.copy(blindSample = updatedBlind)
            } else s
        }
        val updated = session.copy(samples = updatedSamples)
        return updated.copy(currentSha256Checksum = updated.computeSha256())
    }

    override fun lockSession(session: CuppingSession, assessorSignature: String): CuppingSession {
        require(assessorSignature.isNotBlank()) { "Assessor signature is required to lock session." }
        val now = System.currentTimeMillis()
        val locked = session.copy(
            state = SessionState.LOCKED,
            lockedAtEpochMs = now
        )
        return locked.copy(currentSha256Checksum = locked.computeSha256())
    }

    override fun amendSession(session: CuppingSession, amendedBy: String, reason: String): CuppingSession {
        require(session.state == SessionState.LOCKED) { "Session must be locked prior to amendment." }
        require(reason.isNotBlank()) { "Mandatory reason required for amendment." }
        val now = System.currentTimeMillis()
        val prevChecksum = session.currentSha256Checksum
        val amendment = SessionAmendment(
            amendmentId = "amend_${UUID.randomUUID().toString().take(8)}",
            revision = session.amendmentHistory.size + 1,
            amendedAtEpochMs = now,
            amendedBy = amendedBy,
            reason = reason,
            previousSnapshotSha256 = prevChecksum
        )
        val amended = session.copy(
            state = SessionState.AMENDED,
            amendmentHistory = session.amendmentHistory + amendment
        )
        return amended.copy(currentSha256Checksum = amended.computeSha256())
    }

    private fun isSampleComplete(descriptive: DescriptiveAssessment, affective: AffectiveAssessment): Boolean {
        return (descriptive.flavorIntensity > 0.0 || descriptive.fragranceDryIntensity > 0.0) &&
                affective.totalScore > java.math.BigDecimal.ZERO
    }
}

object CvaReportExportService {

    fun exportToNativeJson(session: CuppingSession): String {
        return buildString {
            append("{\n")
            append("  \"schema\": \"https://coffeecreator.app/schemas/cva-cupping-v1.json\",\n")
            append("  \"sessionId\": \"${session.id}\",\n")
            append("  \"protocol\": \"${session.protocolSpec.protocol.name}\",\n")
            append("  \"protocolVersion\": \"${session.protocolSpec.version}\",\n")
            append("  \"disclaimer\": \"${session.protocolSpec.officialDisclaimer}\",\n")
            append("  \"assessor\": \"${session.assessorPseudonym}\",\n")
            append("  \"createdAtEpochMs\": ${session.createdAtEpochMs},\n")
            append("  \"lockedAtEpochMs\": ${session.lockedAtEpochMs ?: "null"},\n")
            append("  \"state\": \"${session.state.name}\",\n")
            append("  \"checksumSha256\": \"${session.currentSha256Checksum}\",\n")
            append("  \"samples\": [\n")
            session.samples.forEachIndexed { i, s ->
                append("    {\n")
                append("      \"blindCode\": \"${s.blindSample.blindCode}\",\n")
                append("      \"revealed\": ${s.blindSample.revealState != BlindRevealState.CONCEALED},\n")
                if (s.blindSample.revealState != BlindRevealState.CONCEALED) {
                    append("      \"actualBeanName\": \"${s.blindSample.actualBeanName}\",\n")
                }
                append("      \"descriptive\": {\n")
                append("        \"flavorIntensity\": ${s.descriptive.flavorIntensity},\n")
                append("        \"acidityIntensity\": ${s.descriptive.acidityIntensity},\n")
                append("        \"sweetnessIntensity\": ${s.descriptive.sweetnessIntensity},\n")
                append("        \"mouthfeelIntensity\": ${s.descriptive.mouthfeelIntensity},\n")
                append("        \"descriptors\": [${s.descriptive.descriptors.joinToString { "\"$it\"" }}]\n")
                append("      },\n")
                append("      \"affective\": {\n")
                append("        \"totalScore\": ${s.affective.totalScore},\n")
                append("        \"notes\": \"${s.affective.notes}\"\n")
                append("      }\n")
                append("    }${if (i < session.samples.size - 1) "," else ""}\n")
            }
            append("  ]\n")
            append("}")
        }
    }

    fun exportToNativeCsv(session: CuppingSession): String {
        return buildString {
            append("SessionId,Protocol,Assessor,BlindCode,Revealed,ActualBean,FlavorIntensity,AcidityIntensity,SweetnessIntensity,MouthfeelIntensity,AffectiveScore,Checksum\n")
            session.samples.forEach { s ->
                val revealed = s.blindSample.revealState != BlindRevealState.CONCEALED
                val actual = if (revealed) s.blindSample.actualBeanName else "CONCEALED"
                append("${session.id},${session.protocolSpec.protocol.name},${session.assessorPseudonym},${s.blindSample.blindCode},$revealed,\"$actual\",${s.descriptive.flavorIntensity},${s.descriptive.acidityIntensity},${s.descriptive.sweetnessIntensity},${s.descriptive.mouthfeelIntensity},${s.affective.totalScore},${session.currentSha256Checksum}\n")
            }
        }
    }
}
