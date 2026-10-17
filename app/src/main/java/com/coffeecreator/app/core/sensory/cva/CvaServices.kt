package com.coffeecreator.app.core.sensory.cva

import java.math.BigDecimal
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
        val samples = lots.map { (lotId, beanName) ->
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
                affective.totalScore > BigDecimal.ZERO
    }
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

    fun validateDescriptiveIntensity(value: Double): Boolean {
        return value in 0.0..5.0
    }

    fun validateAffectiveScore(score: BigDecimal): Boolean {
        return score in BigDecimal("6.00")..BigDecimal("10.00")
    }

    fun validateWaterTemperature(celsius: Double): Boolean {
        return celsius in 85.0..99.0
    }
}

// ============================================================================
// S4-024: Native Data Export Service
// ============================================================================

object CvaReportExportService {

    fun exportToNativeJson(session: CuppingSession): String {
        return buildString {
            append("{\n")
            append("  \"\$schema\": \"https://coffeecreator.app/schemas/cva-cupping-v1.json\",\n")
            append("  \"sessionId\": \"${session.id}\",\n")
            append("  \"protocol\": \"${session.protocolSpec.protocol.name}\",\n")
            append("  \"protocolVersion\": \"${session.protocolSpec.version}\",\n")
            append("  \"fieldMappingVersion\": \"${session.protocolSpec.fieldMappingVersion}\",\n")
            append("  \"disclaimer\": \"${session.protocolSpec.officialDisclaimer}\",\n")
            append("  \"assessor\": \"${session.assessorPseudonym}\",\n")
            append("  \"createdAtEpochMs\": ${session.createdAtEpochMs},\n")
            append("  \"lockedAtEpochMs\": ${session.lockedAtEpochMs ?: "null"},\n")
            append("  \"state\": \"${session.state.name}\",\n")
            append("  \"checksumSha256\": \"${session.currentSha256Checksum}\",\n")
            append("  \"preparation\": {\n")
            append("    \"grinderModel\": \"${session.samplePreparation.equipment.grinderModel}\",\n")
            append("    \"waterTdsPpm\": ${session.samplePreparation.waterTdsPpm},\n")
            append("    \"waterTempCelsius\": ${session.samplePreparation.waterTempCelsius},\n")
            append("    \"doseGrams\": ${session.samplePreparation.doseGrams},\n")
            append("    \"waterVolumeMl\": ${session.samplePreparation.waterVolumeMl},\n")
            append("    \"brewRatio\": ${session.samplePreparation.brewRatio}\n")
            append("  },\n")
            append("  \"samples\": [\n")
            session.samples.forEachIndexed { i, s ->
                val resolvedDesc = s.descriptive.getResolvedDescriptors().map { it.name }.ifEmpty { s.descriptive.descriptors }
                append("    {\n")
                append("      \"blindCode\": \"${s.blindSample.blindCode}\",\n")
                append("      \"revealed\": ${s.blindSample.revealState != BlindRevealState.CONCEALED},\n")
                if (s.blindSample.revealState != BlindRevealState.CONCEALED) {
                    append("      \"actualBeanName\": \"${s.blindSample.actualBeanName}\",\n")
                }
                append("      \"multiCupCleanCount\": ${s.multiCupGrid.cleanCupCount},\n")
                append("      \"uniformityScore\": ${s.multiCupGrid.uniformityScore},\n")
                append("      \"cleanCupScore\": ${s.multiCupGrid.cleanCupScore},\n")
                append("      \"descriptive\": {\n")
                append("        \"flavorIntensity\": ${s.descriptive.flavorIntensity},\n")
                append("        \"acidityIntensity\": ${s.descriptive.acidityIntensity},\n")
                append("        \"sweetnessIntensity\": ${s.descriptive.sweetnessIntensity},\n")
                append("        \"mouthfeelIntensity\": ${s.descriptive.mouthfeelIntensity},\n")
                append("        \"finishDuration\": ${s.descriptive.finishDuration},\n")
                append("        \"descriptors\": [${resolvedDesc.joinToString { "\"$it\"" }}]\n")
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
            append("SessionId,Protocol,Assessor,BlindCode,Revealed,ActualBean,FlavorIntensity,AcidityIntensity,SweetnessIntensity,MouthfeelIntensity,CleanCups,AffectiveScore,Checksum\n")
            session.samples.forEach { s ->
                val revealed = s.blindSample.revealState != BlindRevealState.CONCEALED
                val actual = if (revealed) s.blindSample.actualBeanName else "CONCEALED"
                append("${session.id},${session.protocolSpec.protocol.name},${session.assessorPseudonym},${s.blindSample.blindCode},$revealed,\"$actual\",${s.descriptive.flavorIntensity},${s.descriptive.acidityIntensity},${s.descriptive.sweetnessIntensity},${s.descriptive.mouthfeelIntensity},${s.multiCupGrid.cleanCupCount},${s.affective.totalScore},${session.currentSha256Checksum}\n")
            }
        }
    }
}
