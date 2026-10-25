package com.coffeecreator.app.core.data

import com.coffeecreator.app.core.model.InputLimits
import com.coffeecreator.app.core.sensory.cva.BlindRevealState
import com.coffeecreator.app.core.sensory.cva.CuppingSession
import com.coffeecreator.app.core.sensory.cva.CvaValidationEngine
import com.coffeecreator.app.core.sensory.cva.EvidenceStatus
import com.coffeecreator.app.core.sensory.cva.SampleAssessment
import com.coffeecreator.app.core.sensory.cva.SessionAmendment
import com.coffeecreator.app.core.sensory.cva.SessionState
import com.google.gson.JsonParseException

/** Storage-bound validation for CVA records and amendment provenance. */
internal object CvaSessionStorageValidator {
    private const val MAX_CVA_SAMPLES = 900
    private const val MAX_PROTOCOL_VERSION_CHARS = 64
    private const val SHA256_HEX_LENGTH = 64
    private val SHA256_REGEX = Regex("[0-9a-f]{$SHA256_HEX_LENGTH}")

    fun validate(session: CuppingSession) {
        validateMetadata(session)
        validateSamples(session)
        validateState(session)
        validatePreparation(session)
        validateAmendments(session)
        validateChecksumAndSize(session)
    }

    private fun validateMetadata(session: CuppingSession) {
        BackupCodec.validateId(session.id)
        require(session.assessorId.isNotBlank() && session.assessorId.length <= 128) {
            "CVA assessor identifier is invalid."
        }
        require(session.assessorPseudonym.isNotBlank() && session.assessorPseudonym.length <= 128) {
            "CVA assessor label is invalid."
        }
        require(session.createdAtEpochMs >= 0L && session.samples.size <= MAX_CVA_SAMPLES) {
            "CVA session metadata is outside supported bounds."
        }
        val protocol = session.protocolSpec
        require(
            protocol.id.isNotBlank() && protocol.id.length <= 128 &&
                protocol.version.isNotBlank() && protocol.version.length <= MAX_PROTOCOL_VERSION_CHARS
        ) {
            "CVA protocol metadata is invalid."
        }
        require(
            protocol.sourceUrl.length <= InputLimits.METADATA_CHARS &&
                protocol.fieldMappingVersion.length <= MAX_PROTOCOL_VERSION_CHARS &&
                protocol.licenseStatus.length <= 256
        ) {
            "CVA protocol provenance is outside supported bounds."
        }
        require(protocol.officialDisclaimer.isNotBlank() && protocol.officialDisclaimer.length <= 512) {
            "CVA protocol disclaimer is required."
        }
    }

    private fun validateSamples(session: CuppingSession) {
        require(session.samples.map { it.blindSample.sampleId }.toSet().size == session.samples.size) {
            "CVA sample identifiers must be unique."
        }
        require(session.samples.map { it.blindSample.blindCode }.toSet().size == session.samples.size) {
            "CVA blind codes must be unique."
        }
        session.samples.forEach(::validateSample)
    }

    private fun validateSample(sample: SampleAssessment) {
        validateBlindSample(sample)
        validateAssessment(sample)
    }

    private fun validateBlindSample(sample: SampleAssessment) {
        val blind = sample.blindSample
        BackupCodec.validateId(blind.sampleId)
        BackupCodec.validateId(blind.actualLotId)
        require(blind.blindCode.matches(Regex("[0-9]{3}"))) { "CVA blind codes must be three digits." }
        require(blind.actualBeanName.length <= 256) { "CVA sample label is too long." }
        when (blind.revealState) {
            BlindRevealState.CONCEALED -> require(
                blind.revealTimestampEpochMs == null && blind.revealReason == null &&
                    blind.revealAuditHistory.isEmpty()
            ) {
                "Concealed sample contains reveal evidence."
            }
            BlindRevealState.REVEALED_AUDITED -> requireAuditedReveal(sample)
            BlindRevealState.REVEALED_PUBLIC -> require(blind.revealAuditHistory.isNotEmpty()) {
                "Public reveal requires a retained audit record."
            }
        }
        require(blind.revealAuditHistory.size <= 100) { "Too many CVA reveal audit records." }
        require(blind.revealAuditHistory.map { it.auditId }.toSet().size == blind.revealAuditHistory.size) {
            "CVA reveal audit identifiers must be unique."
        }
        blind.revealAuditHistory.forEach {
            BackupCodec.validateId(it.auditId)
            require(
                it.actorId.isNotBlank() && it.actorId.length <= 128 &&
                    it.reason.isNotBlank() && it.reason.length <= 512 && it.revealedAtEpochMs >= 0L
            ) {
                "CVA reveal audit record is invalid."
            }
        }
    }

    private fun requireAuditedReveal(sample: SampleAssessment) {
        val blind = sample.blindSample
        val last = blind.revealAuditHistory.lastOrNull()
        require(
            last != null && blind.revealTimestampEpochMs == last.revealedAtEpochMs &&
                blind.revealReason == last.reason && last.actorId.isNotBlank() && last.reason.isNotBlank()
        ) {
            "Audited reveal evidence is incomplete."
        }
    }

    private fun validateAssessment(sample: SampleAssessment) {
        require(CvaValidationEngine.isValidAssessment(sample)) { "CVA assessment contains out-of-range values." }
        require(sample.affective.totalScore.scale() <= 8) { "CVA affective values have excessive precision." }
        require(
            sample.descriptive.sensoryNotes.length <= BackupCodec.MAX_MESSAGE_CHARS &&
                sample.affective.notes.length <= BackupCodec.MAX_MESSAGE_CHARS
        ) {
            "CVA notes exceed the supported size limit."
        }
    }

    private fun validateState(session: CuppingSession) {
        val prepEvidence = session.samplePreparation.preparationEvidence
        require(session.state == SessionState.AMENDED || session.amendmentHistory.isEmpty()) {
            "Only amended CVA sessions may contain amendment history."
        }
        if (session.state in setOf(SessionState.LOCKED, SessionState.AMENDED)) {
            require(
                session.lockedAtEpochMs != null && session.lockedAtEpochMs >= session.createdAtEpochMs &&
                    !session.assessorSignature.isNullOrBlank()
            ) {
                "Locked CVA session requires a timestamp and assessor confirmation."
            }
            require(
                prepEvidence in setOf(EvidenceStatus.MEASURED, EvidenceStatus.OBSERVED, EvidenceStatus.REPORTED) &&
                    session.samples.isNotEmpty() && session.samples.all {
                        it.isCompleted && CvaValidationEngine.isCompleteAssessment(it.descriptive, it.affective)
                    }
            ) {
                "Locked CVA session requires completed descriptive and affective evidence for every sample."
            }
            if (session.state == SessionState.AMENDED) {
                require(session.amendmentHistory.isNotEmpty()) { "Amended CVA session requires amendment history." }
            }
        } else {
            require(session.lockedAtEpochMs == null && session.assessorSignature == null) {
                "Unlocked CVA session cannot retain a lock timestamp or signature."
            }
        }
    }

    private fun validatePreparation(session: CuppingSession) {
        require(CvaValidationEngine.isValidPreparation(session.samplePreparation)) {
            "CVA preparation values must be finite and positive, with water temperature between 85 and 99 Celsius."
        }
    }

    private fun validateAmendments(session: CuppingSession) {
        require(session.amendmentHistory.size <= 100) { "Too many CVA amendments." }
        require(session.amendmentHistory.map { it.amendmentId }.toSet().size == session.amendmentHistory.size) {
            "CVA amendment identifiers must be unique."
        }
        session.amendmentHistory.forEachIndexed { index, amendment ->
            validateAmendment(session, amendment, expectedRevision = index + 1)
        }
    }

    private fun validateAmendment(session: CuppingSession, amendment: SessionAmendment, expectedRevision: Int) {
        BackupCodec.validateId(amendment.amendmentId)
        require(
            amendment.revision == expectedRevision && amendment.amendedAtEpochMs >= session.createdAtEpochMs &&
                amendment.amendedBy.isNotBlank() && amendment.amendedBy.length <= 128 &&
                amendment.reason.isNotBlank() && amendment.reason.length <= 512 &&
                SHA256_REGEX.matches(amendment.previousSnapshotSha256) &&
                amendment.previousSnapshotJson.length <= BackupCodec.MAX_CVA_SESSION_CHARS
        ) {
            "CVA amendment history is invalid."
        }
        val previous = parsePreviousSnapshot(amendment.previousSnapshotJson)
        require(
            previous.id == session.id && previous.state == SessionState.LOCKED &&
                previous.currentSha256Checksum == amendment.previousSnapshotSha256 &&
                previous.computeSha256() == amendment.previousSnapshotSha256 &&
                previous.toSnapshotJson() == amendment.previousSnapshotJson
        ) {
            "CVA amendment snapshot failed integrity validation."
        }
        validatePreviousSnapshot(previous)
    }

    private fun validatePreviousSnapshot(previous: CuppingSession) {
        require(previous.amendmentHistory.isEmpty()) { "Locked CVA source snapshot cannot contain amendments." }
        validateMetadata(previous)
        validateSamples(previous)
        validateState(previous)
        validatePreparation(previous)
        validateChecksumAndSize(previous)
    }

    private fun parsePreviousSnapshot(json: String): CuppingSession = try {
        RecordJson.gson.fromJson(json, CuppingSession::class.java)
            ?: throw IllegalArgumentException("CVA amendment snapshot is empty.")
    } catch (failure: JsonParseException) {
        throw IllegalArgumentException("CVA amendment snapshot is malformed.", failure)
    }

    private fun validateChecksumAndSize(session: CuppingSession) {
        require(
            SHA256_REGEX.matches(session.currentSha256Checksum) &&
                session.currentSha256Checksum == session.computeSha256()
        ) {
            "CVA session checksum is invalid."
        }
        require(session.toSnapshotJson().length <= BackupCodec.MAX_CVA_SESSION_CHARS) {
            "CVA session exceeds the supported size limit."
        }
    }
}
