package com.coffeecreator.app.core.sensory.cva

import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CvaDomainEngineTest {

    private val service = StandardCvaSessionService()

    @Test
    fun testSessionCreationWithProtocolsAndBlindCodes() {
        val lots = listOf("lot_01" to "Kenya Nyeri AA", "lot_02" to "Ethiopia Guji Washed")
        val session = service.createSession(
            protocol = CuppingProtocol.SCA_CVA_CURRENT,
            assessorId = "usr_01",
            assessorPseudonym = "Lead Q-Grader",
            lots = lots
        )

        assertEquals(CuppingProtocol.SCA_CVA_CURRENT, session.protocolSpec.protocol)
        assertEquals("2024.1", session.protocolSpec.version)
        assertEquals(SessionState.DRAFT, session.state)
        assertEquals(2, session.samples.size)

        // Verify blind codes are 3-digit opaque strings
        session.samples.forEach { s ->
            assertEquals(3, s.blindSample.blindCode.length)
            assertEquals(BlindRevealState.CONCEALED, s.blindSample.revealState)
        }
        assertTrue(session.currentSha256Checksum.isNotBlank())
    }

    @Test
    fun testPerspectiveSeparationDescriptiveVsAffective() {
        val lots = listOf("lot_01" to "Colombia Huila")
        val session = service.createSession(
            protocol = CuppingProtocol.SCA_CVA_CURRENT,
            assessorId = "usr_01",
            assessorPseudonym = "Lead Taster",
            lots = lots
        )
        val blindCode = session.samples.first().blindSample.blindCode

        // Update descriptive acidity
        val desc = DescriptiveAssessment(
            acidityIntensity = 4.2,
            acidityTypes = listOf(AcidityType.CITRIC, AcidityType.MALIC),
            flavorIntensity = 4.0,
            descriptors = listOf("Lemon", "Green Apple")
        )
        val updated = service.updateDescriptive(session, blindCode, desc)
        val sample = updated.samples.first { it.blindSample.blindCode == blindCode }

        // Descriptive is saved
        assertEquals(4.2, sample.descriptive.acidityIntensity, 0.001)
        assertEquals(2, sample.descriptive.descriptors.size)

        // Affective remains uncalculated/untouched default (no automatic inference)
        assertEquals(BigDecimal("6.00"), sample.affective.acidityAffectiveScore)
        assertEquals(BigDecimal("42.00"), sample.affective.totalScore)
    }

    @Test
    fun testExtrinsicAuditReveal() {
        val lots = listOf("lot_01" to "Panama Geisha Natural")
        val session = service.createSession(
            protocol = CuppingProtocol.SCA_CVA_CURRENT,
            assessorId = "usr_01",
            assessorPseudonym = "Host",
            lots = lots
        )
        val blindCode = session.samples.first().blindSample.blindCode

        val revealed = service.revealExtrinsic(session, blindCode, "Commercial client pre-auction verification")
        val sample = revealed.samples.first { it.blindSample.blindCode == blindCode }

        assertEquals(BlindRevealState.REVEALED_AUDITED, sample.blindSample.revealState)
        assertEquals("Commercial client pre-auction verification", sample.blindSample.revealReason)
        assertNotNull(sample.blindSample.revealTimestampEpochMs)
    }

    @Test
    fun testLockAndAmendmentWorkflow() {
        val lots = listOf("lot_01" to "Sumatra Mandheling")
        val session = service.createSession(
            protocol = CuppingProtocol.SCA_CVA_CURRENT,
            assessorId = "usr_01",
            assessorPseudonym = "Assessor A",
            lots = lots
        )
        val locked = service.lockSession(session, "Digital Signature: Assessor A")
        assertEquals(SessionState.LOCKED, locked.state)
        assertNotNull(locked.lockedAtEpochMs)

        // Attempting to modify locked session throws exception
        try {
            service.updatePreparation(locked, CuppingSamplePreparation(waterTempCelsius = 95.0))
            assertTrue("Expected IllegalArgumentException", false)
        } catch (e: IllegalArgumentException) {
            assertTrue(e.message!!.contains("Cannot modify locked session"))
        }

        // Amending creates versioned revision
        val amended = service.amendSession(locked, "Assessor A", "Correcting typo in grind size documentation")
        assertEquals(SessionState.AMENDED, amended.state)
        assertEquals(1, amended.amendmentHistory.size)
        assertEquals("Correcting typo in grind size documentation", amended.amendmentHistory.first().reason)
    }

    @Test
    fun testExportToNativeJsonAndCsv() {
        val lots = listOf("lot_01" to "Guatemala Antigua")
        val session = service.createSession(
            protocol = CuppingProtocol.SCA_CVA_CURRENT,
            assessorId = "usr_01",
            assessorPseudonym = "Evaluator",
            lots = lots
        )
        val json = CvaReportExportService.exportToNativeJson(session)
        assertTrue(json.contains("cva-cupping-v1.json"))
        assertTrue(json.contains(session.id))
        assertTrue(json.contains("SCA_CVA_CURRENT"))

        val csv = CvaReportExportService.exportToNativeCsv(session)
        assertTrue(csv.contains("SessionId,Protocol,Assessor,BlindCode"))
        assertTrue(csv.contains(session.id))
    }
}
