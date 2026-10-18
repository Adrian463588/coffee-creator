package com.coffeecreator.app.core.sensory.cva

import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import java.math.BigDecimal
import java.security.MessageDigest

abstract class CvaDomainEngineTestSupport {
    protected val service = StandardCvaSessionService()

    protected fun completedSession(): CuppingSession {
        val session = service.createSession(
            protocol = CuppingProtocol.CUSTOM_RESEARCH,
            assessorId = "assessor-1",
            assessorPseudonym = "Cup 1",
            lots = listOf("lot-1" to "Lot 1")
        )
        return completeSample(session)
    }

    protected fun sha256(value: String): String {
        return MessageDigest.getInstance("SHA-256")
            .digest(value.toByteArray(Charsets.UTF_8))
            .joinToString("") { "%02x".format(it) }
    }

    protected fun canonicalJson(element: JsonElement): String =
        GsonBuilder().serializeNulls().disableHtmlEscaping().create().toJson(sortJson(element))

    private fun sortJson(element: JsonElement): JsonElement = when {
        element.isJsonObject -> JsonObject().apply {
            element.asJsonObject.entrySet().sortedBy { it.key }.forEach { (key, value) -> add(key, sortJson(value)) }
        }
        element.isJsonArray -> JsonArray().apply { element.asJsonArray.forEach { add(sortJson(it)) } }
        else -> element
    }

    protected fun completeSample(session: CuppingSession): CuppingSession {
        val blindCode = session.samples.single().blindSample.blindCode
        val prepared = service.updatePreparation(
            session,
            session.samplePreparation.copy(preparationEvidence = EvidenceStatus.MEASURED)
        )
        val descriptive = service.updateDescriptive(
            prepared,
            blindCode,
            DescriptiveAssessment(flavorIntensity = 4.0, evidenceStatus = EvidenceStatus.OBSERVED)
        )
        return service.updateAffective(
            descriptive,
            blindCode,
            AffectiveAssessment(
                fragranceAffectiveScore = BigDecimal("8.00"),
                flavorAffectiveScore = BigDecimal("8.00"),
                aftertasteAffectiveScore = BigDecimal("8.00"),
                acidityAffectiveScore = BigDecimal("8.00"),
                sweetnessAffectiveScore = BigDecimal("8.00"),
                mouthfeelAffectiveScore = BigDecimal("8.00"),
                overallAffectiveScore = BigDecimal("8.00"),
                evidenceStatus = EvidenceStatus.OBSERVED
            )
        )
    }
}
