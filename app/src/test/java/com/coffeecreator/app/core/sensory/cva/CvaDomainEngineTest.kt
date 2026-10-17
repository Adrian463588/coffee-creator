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
    fun testControlledPreparationAndEquipmentMetadata() {
        val equip = CuppingEquipment(
            grinderModel = "Mahlkönig EK43S",
            burrType = "98mm Cast Sweet",
            grindMicrons = 850,
            kettleModel = "Fellow Stagg EKG Pro",
            bowlCountPerSample = 5
        )
        val prep = CuppingSamplePreparation(
            equipment = equip,
            waterTdsPpm = 125.0,
            waterTempCelsius = 93.5,
            doseGrams = 11.0,
            waterVolumeMl = 200.0,
            brewRatio = 0.055
        )
        val session = service.createSession(
            protocol = CuppingProtocol.SCA_CVA_CURRENT,
            assessorId = "usr_01",
            assessorPseudonym = "Q-Grader",
            lots = listOf("lot_01" to "Geisha"),
            prep = prep
        )

        assertEquals(125.0, session.samplePreparation.waterTdsPpm, 0.001)
        assertEquals(93.5, session.samplePreparation.waterTempCelsius, 0.001)
        assertEquals("Mahlkönig EK43S", session.samplePreparation.equipment.grinderModel)
        assertEquals(5, session.samplePreparation.equipment.bowlCountPerSample)
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
    fun test221DescriptorWheelIntegration() {
        val lots = listOf("lot_01" to "Panama Boquete")
        val session = service.createSession(
            protocol = CuppingProtocol.SCA_CVA_CURRENT,
            assessorId = "usr_01",
            assessorPseudonym = "Lead Taster",
            lots = lots
        )
        val blindCode = session.samples.first().blindSample.blindCode

        val desc = DescriptiveAssessment(
            flavorIntensity = 4.5,
            selectedDescriptorIds = listOf("desc_lemon", "desc_peach", "desc_jasmine", "desc_honey")
        )
        val updated = service.updateDescriptive(session, blindCode, desc)
        val sample = updated.samples.first { it.blindSample.blindCode == blindCode }

        assertEquals(4, sample.descriptive.selectedDescriptorIds.size)
        assertTrue(sample.descriptive.selectedDescriptorIds.contains("desc_lemon"))
        assertTrue(sample.descriptive.selectedDescriptorIds.contains("desc_jasmine"))
    }

    @Test
    fun testMultiCupEvaluationGridAndDefectPenalties() {
        val lots = listOf("lot_01" to "Ethiopia Sidama")
        val session = service.createSession(
            protocol = CuppingProtocol.SCA_CVA_CURRENT,
            assessorId = "usr_01",
            assessorPseudonym = "Cupper",
            lots = lots
        )
        val blindCode = session.samples.first().blindSample.blindCode

        // 5-cup grid: 4 clean, 1 taint (cup 3 moldy taint)
        val cups = listOf(
            SingleCupEvaluation(cupIndex = 1, isClean = true),
            SingleCupEvaluation(cupIndex = 2, isClean = true),
            SingleCupEvaluation(
                cupIndex = 3,
                isClean = false,
                defectCategory = CupDefectCategory.TAINT,
                defectCharacter = CupDefectCharacter.MOLDY_MUSTY,
                defectIntensity = 3.0,
                notes = "Musty taint in bowl 3"
            ),
            SingleCupEvaluation(cupIndex = 4, isClean = true),
            SingleCupEvaluation(cupIndex = 5, isClean = true)
        )
        val grid = MultiCupEvaluationGrid(cups = cups)

        assertEquals(4, grid.cleanCupCount)
        assertEquals(BigDecimal("8.00"), grid.uniformityScore)
        assertEquals(BigDecimal("8.00"), grid.cleanCupScore)
        assertEquals(BigDecimal("2.00"), grid.totalDefectDeduction) // 1 taint = -2.00

        val updated = service.updateMultiCupGrid(session, blindCode, grid)
        val sample = updated.samples.first { it.blindSample.blindCode == blindCode }
        assertEquals(4, sample.multiCupGrid.cleanCupCount)
        assertEquals(BigDecimal("2.00"), sample.multiCupGrid.totalDefectDeduction)
    }

    @Test
    fun testLegacy2004CuppingSheetCalculation() {
        val legacy = Legacy2004CuppingSheet(
            fragranceScore = BigDecimal("8.50"),
            flavorScore = BigDecimal("8.75"),
            aftertasteScore = BigDecimal("8.25"),
            acidityScore = BigDecimal("8.50"),
            bodyScore = BigDecimal("8.25"),
            balanceScore = BigDecimal("8.50"),
            uniformityCheckboxes = listOf(true, true, true, true, true), // 10.0
            cleanCupCheckboxes = listOf(true, true, true, true, true),   // 10.0
            sweetnessCheckboxes = listOf(true, true, true, true, true),  // 10.0
            overallScore = BigDecimal("8.75"),
            taintCupCount = 0,
            faultCupCount = 0
        )

        // 8.50 + 8.75 + 8.25 + 8.50 + 8.25 + 8.50 + 10.0 + 10.0 + 10.0 + 8.75 = 89.50
        assertEquals(BigDecimal("89.50"), legacy.totalScore)
        assertEquals(BigDecimal("10"), legacy.uniformityScore)
        assertEquals(BigDecimal("10"), legacy.cleanCupScore)
    }

    @Test
    fun testCuppingStepperMilestones() {
        assertEquals(CuppingMilestone.DRY_FRAGRANCE, CuppingStepperEngine.getActiveMilestone(10))
        assertEquals(CuppingMilestone.WATER_POUR, CuppingStepperEngine.getActiveMilestone(60))
        assertEquals(CuppingMilestone.CRUST_BREAK, CuppingStepperEngine.getActiveMilestone(245))
        assertEquals(CuppingMilestone.HOT_CUPPING, CuppingStepperEngine.getActiveMilestone(500))
        assertEquals(CuppingMilestone.WARM_CUPPING, CuppingStepperEngine.getActiveMilestone(800))
        assertEquals(CuppingMilestone.COOL_CUPPING, CuppingStepperEngine.getActiveMilestone(1200))
    }

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
            flavorAffectiveScore = BigDecimal("8.75"),
            acidityAffectiveScore = BigDecimal("8.75"),
            overallAffectiveScore = BigDecimal("8.75")
        )
        var s = service.updateDescriptive(session, blindCode, desc)
        s = service.updateAffective(s, blindCode, aff)
        val sample = s.samples.first()

        val journalEntry = sample.toBrewJournalEntry(s)

        assertNotNull(journalEntry.id)
        assertTrue(journalEntry.brewMethod.contains("Cupping"))
        assertEquals(3, journalEntry.tasteNotes.size)
        assertTrue(journalEntry.tasteNotes.contains("Red Currant"))
        assertTrue(journalEntry.tags.contains("cupping"))
        assertTrue(journalEntry.tags.contains("cva"))
        assertTrue(journalEntry.rating!! >= 6.0)
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
