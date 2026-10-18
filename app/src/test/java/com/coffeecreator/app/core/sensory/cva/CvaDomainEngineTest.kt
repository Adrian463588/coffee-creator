package com.coffeecreator.app.core.sensory.cva

import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class CvaDomainEngineTest : CvaDomainEngineTestSupport() {

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
        assertTrue(session.id.matches(Regex("cva_session_[0-9a-f-]{36}")))
        session.samples.forEach { sample ->
            assertTrue(sample.blindSample.sampleId.matches(Regex("sample_[0-9a-f-]{36}")))
        }
        assertTrue(session.currentSha256Checksum.isNotBlank())
    }
    @Test
    fun givenBlindCodeCapacity_whenSessionCreated_thenAllCodesRemainUnique() {
        val lots = (0 until 900).map { index -> "lot_$index" to "Coffee $index" }

        val session = service.createSession(
            protocol = CuppingProtocol.CUSTOM_RESEARCH,
            assessorId = "assessor-1",
            assessorPseudonym = "Panelist",
            lots = lots
        )

        val blindCodes = session.samples.map { it.blindSample.blindCode }
        assertEquals(900, blindCodes.size)
        assertEquals(900, blindCodes.toSet().size)
    }
    @Test
    fun givenMoreThanBlindCodeCapacity_whenSessionCreated_thenRejected() {
        val lots = (0..900).map { index -> "lot_$index" to "Coffee $index" }

        assertThrows(IllegalArgumentException::class.java) {
            service.createSession(
                protocol = CuppingProtocol.CUSTOM_RESEARCH,
                assessorId = "assessor-1",
                assessorPseudonym = "Panelist",
                lots = lots
            )
        }
    }
    @Test
    fun givenPreparationOrNotesChange_whenHashComputed_thenDigestChanges() {
        val session = service.createSession(
            protocol = CuppingProtocol.CUSTOM_RESEARCH,
            assessorId = "assessor-1",
            assessorPseudonym = "Cup 1",
            lots = listOf("lot-1" to "Lot 1")
        )
        val changedPreparation = session.copy(
            samplePreparation = session.samplePreparation.copy(waterTempCelsius = 91.0)
        )
        val changedNotes = session.copy(
            samples = session.samples.map { sample ->
                sample.copy(descriptive = sample.descriptive.copy(sensoryNotes = "citrus"))
            }
        )

        assertNotEquals(session.computeSha256(), changedPreparation.computeSha256())
        assertNotEquals(session.computeSha256(), changedNotes.computeSha256())
        assertEquals(session.computeSha256(), session.computeSha256())
    }
    @Test
    fun givenNewAssessments_whenConstructed_thenEvidenceStartsUnknown() {
        assertEquals(EvidenceStatus.NOT_MEASURED, CuppingSamplePreparation().preparationEvidence)
        assertEquals(EvidenceStatus.UNKNOWN, DescriptiveAssessment().evidenceStatus)
        assertEquals(EvidenceStatus.UNKNOWN, AffectiveAssessment().evidenceStatus)
        assertEquals(BigDecimal.ZERO, AffectiveAssessment().totalScore)
        assertEquals("", CuppingEquipment().grinderModel)
    }
    @Test
    fun givenNoCompletedSamples_whenLockRequested_thenLockIsRejected() {
        val session = service.createSession(
            protocol = CuppingProtocol.CUSTOM_RESEARCH,
            assessorId = "assessor-1",
            assessorPseudonym = "Cup 1",
            lots = listOf("lot-1" to "Lot 1")
        )

        assertThrows(IllegalArgumentException::class.java) {
            service.lockSession(session, "assessor confirmation")
        }
    }
    @Test
    fun givenLockedSession_whenExtrinsicRevealRequested_thenRevealIsRejected() {
        val session = completedSession()
        val locked = service.lockSession(session, "assessor confirmation")

        assertThrows(IllegalArgumentException::class.java) {
            service.revealExtrinsic(locked, locked.samples.first().blindSample.blindCode, "assessor-1", "Review")
        }
    }
    @Test
    fun givenAlreadyRevealedSample_whenRevealRequestedAgain_thenOriginalRevealAuditCannotBeOverwritten() {
        val session = completedSession()
        val code = session.samples.single().blindSample.blindCode
        val revealed = service.revealExtrinsic(session, code, "assessor-1", "Approved review")

        assertThrows(IllegalArgumentException::class.java) {
            service.revealExtrinsic(revealed, code, "assessor-2", "Replace original reason")
        }
    }
    @Test
    fun givenDifferentAssessor_whenRevealRequested_thenRevealIsRejected() {
        val session = completedSession()
        val code = session.samples.single().blindSample.blindCode

        assertThrows(IllegalArgumentException::class.java) {
            service.revealExtrinsic(session, code, "other-assessor", "Unauthorized request")
        }
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

        // Affective remains unknown until the assessor enters observations.
        assertEquals(EvidenceStatus.UNKNOWN, sample.affective.evidenceStatus)
        assertEquals(BigDecimal.ZERO, sample.affective.acidityAffectiveScore)
        assertEquals(BigDecimal.ZERO, sample.affective.totalScore)
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
            uniformityCheckboxes = listOf(true, true, true, true, true),
            cleanCupCheckboxes = listOf(true, true, true, true, true),
            sweetnessCheckboxes = listOf(true, true, true, true, true),
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
    fun givenNewLegacySheet_whenConstructed_thenScoresAndCupObservationsRemainUnknown() {
        val sheet = Legacy2004CuppingSheet()

        assertEquals(BigDecimal.ZERO, sheet.fragranceScore)
        assertEquals(BigDecimal.ZERO, sheet.totalScore)
        assertTrue(sheet.uniformityCheckboxes.none { it })
        assertTrue(sheet.cleanCupCheckboxes.none { it })
        assertTrue(sheet.sweetnessCheckboxes.none { it })
    }
    @Test
    fun testCuppingStepperMilestones() {
        assertEquals(0, CuppingMilestone.DRY_FRAGRANCE.secondsTarget)
        assertEquals(30, CuppingMilestone.WATER_POUR.secondsTarget)
        assertEquals(240, CuppingMilestone.CRUST_BREAK.secondsTarget)
        assertEquals(480, CuppingMilestone.HOT_CUPPING.secondsTarget)
        assertEquals(720, CuppingMilestone.WARM_CUPPING.secondsTarget)
        assertEquals(1080, CuppingMilestone.COOL_CUPPING.secondsTarget)
        assertEquals(CuppingMilestone.DRY_FRAGRANCE, CuppingStepperEngine.getActiveMilestone(10))
        assertEquals(
            CuppingMilestone.DRY_FRAGRANCE,
            CuppingStepperEngine.getActiveMilestone(29)
        )
        assertEquals(
            CuppingMilestone.WATER_POUR,
            CuppingStepperEngine.getActiveMilestone(CuppingMilestone.WATER_POUR.secondsTarget)
        )
        assertEquals(CuppingMilestone.WATER_POUR, CuppingStepperEngine.getActiveMilestone(239))
        assertEquals(
            CuppingMilestone.CRUST_BREAK,
            CuppingStepperEngine.getActiveMilestone(CuppingMilestone.CRUST_BREAK.secondsTarget)
        )
        assertEquals(CuppingMilestone.CRUST_BREAK, CuppingStepperEngine.getActiveMilestone(479))
        assertEquals(
            CuppingMilestone.HOT_CUPPING,
            CuppingStepperEngine.getActiveMilestone(CuppingMilestone.HOT_CUPPING.secondsTarget)
        )
        assertEquals(CuppingMilestone.HOT_CUPPING, CuppingStepperEngine.getActiveMilestone(719))
        assertEquals(
            CuppingMilestone.WARM_CUPPING,
            CuppingStepperEngine.getActiveMilestone(CuppingMilestone.WARM_CUPPING.secondsTarget)
        )
        assertEquals(CuppingMilestone.WARM_CUPPING, CuppingStepperEngine.getActiveMilestone(1079))
        assertEquals(
            CuppingMilestone.COOL_CUPPING,
            CuppingStepperEngine.getActiveMilestone(CuppingMilestone.COOL_CUPPING.secondsTarget)
        )
    }
}
