package com.coffeecreator.app.core.sensory.cva

import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class CvaSessionWorkflowTest : CvaDomainEngineTestSupport() {

    @Test
    fun testSideBySideComparisonEngine() {
        val sampleA = SampleAssessment(
            blindSample = BlindSample("s1", "101", "lot1", "Kenya AA"),
            descriptive = DescriptiveAssessment(
                flavorIntensity = 4.5,
                acidityIntensity = 4.6,
                sweetnessIntensity = 4.2,
                descriptors = listOf("Blackcurrant", "Grapefruit", "Cane Sugar")
            )
        )
        val sampleB = SampleAssessment(
            blindSample = BlindSample("s2", "202", "lot2", "Ethiopia Yirgacheffe"),
            descriptive = DescriptiveAssessment(
                flavorIntensity = 4.2,
                acidityIntensity = 4.0,
                sweetnessIntensity = 4.5,
                descriptors = listOf("Jasmine", "Grapefruit", "Peach")
            )
        )

        val delta = CvaComparisonEngine.compareSamples(sampleA, sampleB)
        assertEquals("101", delta.sampleCodeA)
        assertEquals("202", delta.sampleCodeB)
        assertEquals(0.3, delta.flavorDelta, 0.001)
        assertEquals(0.6, delta.acidityDelta, 0.001)
        assertEquals(-0.3, delta.sweetnessDelta, 0.001)
        assertEquals(listOf("Grapefruit"), delta.sharedDescriptors)
        assertEquals(setOf("Blackcurrant", "Cane Sugar"), delta.uniqueDescriptorsA.toSet())
        assertEquals(setOf("Jasmine", "Peach"), delta.uniqueDescriptorsB.toSet())
    }
    @Test
    fun testOneClickBrewJournalMapping() {
        val lots = listOf("lot_01" to "Rwanda Huye Mountain")
        val session = service.createSession(
            protocol = CuppingProtocol.SCA_CVA_CURRENT,
            assessorId = "usr_01",
            assessorPseudonym = "Head Cupper",
            lots = lots
        )
        val blindCode = session.samples.first().blindSample.blindCode

        val desc = DescriptiveAssessment(
            flavorIntensity = 4.4,
            acidityIntensity = 4.5,
            sweetnessIntensity = 4.3,
            mouthfeelIntensity = 3.8,
            aftertasteIntensity = 4.0,
            descriptors = listOf("Red Currant", "Blood Orange", "Black Tea")
        )
        val aff = AffectiveAssessment(
            fragranceAffectiveScore = BigDecimal("8.75"),
            flavorAffectiveScore = BigDecimal("8.75"),
            aftertasteAffectiveScore = BigDecimal("8.75"),
            acidityAffectiveScore = BigDecimal("8.75"),
            sweetnessAffectiveScore = BigDecimal("8.75"),
            mouthfeelAffectiveScore = BigDecimal("8.75"),
            overallAffectiveScore = BigDecimal("8.75"),
            evidenceStatus = EvidenceStatus.OBSERVED
        )
        var s = service.updatePreparation(
            session,
            session.samplePreparation.copy(preparationEvidence = EvidenceStatus.MEASURED)
        )
        s = service.updateDescriptive(s, blindCode, desc.copy(evidenceStatus = EvidenceStatus.OBSERVED))
        s = service.updateAffective(s, blindCode, aff)
        val sample = s.samples.first()

        val journalEntry = sample.toBrewJournalEntry(
            s,
            measuredWaterMassGrams = 198.0,
            measuredDurationSeconds = 240
        )

        assertNotNull(journalEntry.id)
        assertTrue(journalEntry.brewMethod.contains("Cupping"))
        assertEquals(3, journalEntry.tasteNotes.size)
        assertTrue(journalEntry.tasteNotes.contains("Red Currant"))
        assertTrue(journalEntry.tags.contains("cupping"))
        assertTrue(journalEntry.tags.contains("cva"))
        assertTrue(journalEntry.rating!! >= 6.0)
        assertEquals(8.8, journalEntry.rating!!, 0.0)
        assertEquals(198.0, journalEntry.waterGrams, 0.0)
        assertNull(journalEntry.beverageMassGrams)
    }
    @Test
    fun testProtocolBoundsValidation() {
        assertTrue(CvaValidationEngine.validateDescriptiveIntensity(4.5))
        assertFalse(CvaValidationEngine.validateDescriptiveIntensity(5.5))
        assertFalse(CvaValidationEngine.validateDescriptiveIntensity(-0.5))

        assertTrue(CvaValidationEngine.validateAffectiveScore(BigDecimal("8.50")))
        assertFalse(CvaValidationEngine.validateAffectiveScore(BigDecimal("5.50")))
        assertFalse(CvaValidationEngine.validateAffectiveScore(BigDecimal("10.50")))

        assertTrue(CvaValidationEngine.validateWaterTemperature(93.5))
        assertFalse(CvaValidationEngine.validateWaterTemperature(105.0))
        assertFalse(CvaValidationEngine.validateWaterTemperature(75.0))
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

        val revealed = service.revealExtrinsic(
            session,
            blindCode,
            "usr_01",
            "Commercial client pre-auction verification"
        )
        val sample = revealed.samples.first { it.blindSample.blindCode == blindCode }

        assertEquals(BlindRevealState.REVEALED_AUDITED, sample.blindSample.revealState)
        assertEquals("Commercial client pre-auction verification", sample.blindSample.revealReason)
        assertNotNull(sample.blindSample.revealTimestampEpochMs)
        assertEquals("usr_01", sample.blindSample.revealAuditHistory.single().actorId)
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
        val completed = completeSample(session)
        val locked = service.lockSession(completed, "assessor confirmation: Assessor A")
        assertEquals(SessionState.LOCKED, locked.state)
        assertNotNull(locked.lockedAtEpochMs)
        assertEquals("assessor confirmation: Assessor A", locked.assessorSignature)

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
        assertEquals(locked.toSnapshotJson(), amended.amendmentHistory.first().previousSnapshotJson)
    }
    @Test
    fun givenUnmeasuredPreparation_whenJournalBridgeRequested_thenEntryIsRejected() {
        val session = service.createSession(
            protocol = CuppingProtocol.CUSTOM_RESEARCH,
            assessorId = "assessor-1",
            assessorPseudonym = "Cup 1",
            lots = listOf("lot-1" to "Lot 1")
        )
        assertThrows(IllegalArgumentException::class.java) {
            session.samples.single().toBrewJournalEntry(
                session,
                measuredWaterMassGrams = 200.0,
                measuredDurationSeconds = 240
            )
        }
    }
    @Test
    fun givenIncompleteSample_whenJournalBridgeRequested_thenEntryIsRejected() {
        val session = service.updatePreparation(
            completedSession(),
            CuppingSamplePreparation(preparationEvidence = EvidenceStatus.MEASURED)
        )
        val incomplete = session.samples.single().copy(isCompleted = false)

        assertThrows(IllegalArgumentException::class.java) {
            incomplete.toBrewJournalEntry(session, measuredWaterMassGrams = 200.0, measuredDurationSeconds = 240)
        }
    }
    @Test
    fun givenBlindSample_whenJournalBridgeRequested_thenLotIdentityIsNotLinkedOrExposed() {
        val session = completedSession()
        val sample = session.samples.single()
        val entry = sample.toBrewJournalEntry(session, measuredWaterMassGrams = 200.0, measuredDurationSeconds = 240)

        assertNull(entry.beanId)
        assertEquals("Sample #${sample.blindSample.blindCode}", entry.beanName)
        assertFalse(entry.generalNotes.contains(sample.blindSample.actualLotId))
    }
    @Test
    fun givenInvalidRatingOverride_whenJournalBridgeRequested_thenEntryIsRejected() {
        val session = completedSession()
        val sample = session.samples.single()

        assertThrows(IllegalArgumentException::class.java) {
            sample.toBrewJournalEntry(
                session,
                measuredWaterMassGrams = 200.0,
                measuredDurationSeconds = 240,
                ratingOverride = Double.NaN
            )
        }
    }
}
