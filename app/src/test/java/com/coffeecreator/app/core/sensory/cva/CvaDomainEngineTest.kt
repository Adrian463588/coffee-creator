package com.coffeecreator.app.core.sensory.cva

import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.math.BigDecimal
import java.security.MessageDigest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
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
        assertTrue(session.id.matches(Regex("cva_session_[0-9a-f-]{36}")))
        session.samples.forEach { sample ->
            assertTrue(sample.blindSample.sampleId.matches(Regex("sample_[0-9a-f-]{36}")))
        }
        assertTrue(session.currentSha256Checksum.isNotBlank())
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
    fun testExportToNativeJsonAndCsv() {
        val lots = listOf("lot_01" to "Guatemala Antigua")
        val session = service.createSession(
            protocol = CuppingProtocol.SCA_CVA_CURRENT,
            assessorId = "usr_01",
            assessorPseudonym = "Evaluator \"A\"\nCafé",
            lots = lots
        )
        val json = CvaReportExportService.exportToNativeJson(session)
        assertTrue(json.contains("cva-cupping-v1.json"))
        assertTrue(json.contains(session.id))
        assertTrue(json.contains("SCA_CVA_CURRENT"))
        assertFalse(json.contains("Guatemala Antigua"))
        assertEquals("Evaluator \"A\"\nCafé", JsonParser.parseString(json).asJsonObject.get("assessor").asString)

        val csv = CvaReportExportService.exportToNativeCsv(session)
        assertTrue(csv.contains("SessionId,Protocol,Assessor,BlindCode"))
        assertTrue(csv.contains(session.id))
        assertTrue(csv.contains("\"Evaluator \"\"A\"\"\nCafé\""))
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

    @Test
    fun givenExportedReport_whenChecksumFieldsAreRemoved_thenCanonicalPayloadMatchesDigest() {
        val session = completedSession()
        val report = JsonParser.parseString(CvaReportExportService.exportToNativeJson(session)).asJsonObject
        val digest = report.get("checksumSha256").asString
        report.remove("checksumSha256")
        report.remove("checksumScope")

        assertEquals(sha256(canonicalJson(report)), digest)
    }

    @Test
    fun givenConcealedSampleWithGreenLotReference_whenExported_thenIdentityAndGreenLotReferenceAreRedacted() {
        val session = completedSession()
        val code = session.samples.single().blindSample.blindCode
        val withGreenLot = service.updatePhysical(
            session,
            code,
            PhysicalAssessment(greenLotIdRef = "private-green-lot-id", evidenceStatus = EvidenceStatus.MEASURED)
        )
        val json = CvaReportExportService.exportToNativeJson(withGreenLot)

        assertFalse(json.contains(session.samples.single().blindSample.actualLotId))
        assertFalse(json.contains("private-green-lot-id"))
    }

    @Test
    fun givenLegacySampleWithoutRevealState_whenExported_thenIdentityDefaultsToConcealed() {
        val session = completedSession()
        val sample = session.samples.single()
        val legacyBlindSample = GsonBuilder().create().fromJson(
            """{"sampleId":"sample-old","blindCode":"123","actualLotId":"private-lot","actualBeanName":"Private coffee"}""",
            BlindSample::class.java
        )
        val legacySession = session.copy(samples = listOf(sample.copy(blindSample = legacyBlindSample)))
        val json = CvaReportExportService.exportToNativeJson(legacySession)

        assertFalse(json.contains("private-lot"))
        assertFalse(json.contains("Private coffee"))
    }

    @Test
    fun givenAmendedSession_whenExported_thenPriorSnapshotIsIncludedAndBlindIdentityRemainsRedacted() {
        val locked = service.lockSession(completedSession(), "assessor confirmation")
        val amended = service.amendSession(locked, "assessor-1", "Corrected notes")
        val json = CvaReportExportService.exportToNativeJson(amended)
        val amendment = JsonParser.parseString(json).asJsonObject.getAsJsonArray("amendments").first().asJsonObject
        val priorSnapshot = amendment.getAsJsonObject("previousSnapshot")

        assertNotNull(priorSnapshot)
        assertTrue(priorSnapshot.getAsJsonArray("samples").size() == 1)
        assertFalse(json.contains(locked.samples.single().blindSample.actualLotId))
        assertNotEquals(
            locked.computeSha256(),
            amendment.get("previousSnapshotSha256").asString
        )
        assertEquals(
            sha256(canonicalJson(priorSnapshot)),
            amendment.get("previousSnapshotSha256").asString
        )
        assertEquals(
            locked.currentSha256Checksum,
            amendment.get("sourceSnapshotSha256").asString
        )
    }

    @Test
    fun givenAmendmentWithoutValidSnapshot_whenExported_thenIncompleteAuditIsRejected() {
        val session = completedSession().copy(
            amendmentHistory = listOf(
                SessionAmendment(
                    amendmentId = "amend_invalid",
                    revision = 1,
                    amendedAtEpochMs = 1L,
                    amendedBy = "assessor-1",
                    reason = "Imported malformed legacy record",
                    previousSnapshotSha256 = "abc123",
                    previousSnapshotJson = "{invalid"
                )
            )
        )

        assertThrows(IllegalArgumentException::class.java) {
            CvaReportExportService.exportToNativeJson(session)
        }
    }

    @Test
    fun givenTamperedAmendmentSnapshot_whenExported_thenChecksumMismatchIsRejected() {
        val locked = service.lockSession(completedSession(), "assessor confirmation")
        val amended = service.amendSession(locked, "assessor-1", "Corrected notes")
        val amendment = amended.amendmentHistory.single()
        val tamperedSnapshot = JsonParser.parseString(amendment.previousSnapshotJson).asJsonObject.apply {
            addProperty("assessorPseudonym", "tampered assessor")
        }
        val tamperedSession = amended.copy(
            amendmentHistory = listOf(amendment.copy(previousSnapshotJson = tamperedSnapshot.toString()))
        )

        assertThrows(IllegalArgumentException::class.java) {
            CvaReportExportService.exportToNativeJson(tamperedSession)
        }
    }

    @Test
    fun givenAlteredAmendmentSnapshotChecksum_whenExported_thenStoredProofIsRejected() {
        val locked = service.lockSession(completedSession(), "assessor confirmation")
        val amended = service.amendSession(locked, "assessor-1", "Corrected notes")
        val amendment = amended.amendmentHistory.single()
        val snapshotWithAlteredChecksum = JsonParser.parseString(amendment.previousSnapshotJson).asJsonObject.apply {
            addProperty("currentSha256Checksum", "0".repeat(64))
        }
        val tamperedSession = amended.copy(
            amendmentHistory = listOf(amendment.copy(previousSnapshotJson = snapshotWithAlteredChecksum.toString()))
        )

        assertThrows(IllegalArgumentException::class.java) {
            CvaReportExportService.exportToNativeJson(tamperedSession)
        }
    }

    @Test
    fun givenRehashedInvalidAmendmentSnapshot_whenExported_thenDomainValidationRejectsIt() {
        val locked = service.lockSession(completedSession(), "assessor confirmation")
        val amended = service.amendSession(locked, "assessor-1", "Corrected notes")
        val originalAmendment = amended.amendmentHistory.single()
        val invalidSnapshot = locked.copy(
            samples = locked.samples.map { sample ->
                sample.copy(affective = sample.affective.copy(fragranceAffectiveScore = BigDecimal("11.00")))
            }
        ).let { it.copy(currentSha256Checksum = it.computeSha256()) }
        val forgedAmendment = originalAmendment.copy(
            previousSnapshotSha256 = invalidSnapshot.currentSha256Checksum,
            previousSnapshotJson = invalidSnapshot.toSnapshotJson()
        )
        val forgedExport = amended.copy(amendmentHistory = listOf(forgedAmendment))
            .let { it.copy(currentSha256Checksum = it.computeSha256()) }

        assertThrows(IllegalArgumentException::class.java) {
            CvaReportExportService.exportToNativeJson(forgedExport)
        }
    }

    @Test
    fun givenInvalidPreparation_whenAmendmentExported_thenRejected() {
        val locked = service.lockSession(completedSession(), "assessor confirmation")
        val amended = service.amendSession(locked, "assessor-1", "Corrected notes")
        val originalAmendment = amended.amendmentHistory.single()
        val invalidPreparations = listOf(
            locked.samplePreparation.copy(waterTdsPpm = -1.0),
            locked.samplePreparation.copy(doseGrams = -1.0),
            locked.samplePreparation.copy(cupCount = 0),
            locked.samplePreparation.copy(roastRestDays = -1),
            locked.samplePreparation.copy(roastLevelAgtron = 101.0),
            locked.samplePreparation.copy(roastDateEpochMs = -1L),
            locked.samplePreparation.copy(grindMicrons = 0),
            locked.samplePreparation.copy(equipment = locked.samplePreparation.equipment.copy(grindMicrons = -1)),
            locked.samplePreparation.copy(equipment = locked.samplePreparation.equipment.copy(bowlCountPerSample = 0))
        )

        invalidPreparations.forEach { preparation ->
            val invalidSnapshot = locked.copy(samplePreparation = preparation)
                .let { it.copy(currentSha256Checksum = it.computeSha256()) }
            val forgedAmendment = originalAmendment.copy(
                previousSnapshotSha256 = invalidSnapshot.currentSha256Checksum,
                previousSnapshotJson = invalidSnapshot.toSnapshotJson()
            )
            val forgedExport = amended.copy(amendmentHistory = listOf(forgedAmendment))
                .let { it.copy(currentSha256Checksum = it.computeSha256()) }

            assertThrows(IllegalArgumentException::class.java) {
                CvaReportExportService.exportToNativeJson(forgedExport)
            }
        }
    }

    @Test
    fun givenInvalidCurrentPreparation_whenExported_thenRejected() {
        val locked = service.lockSession(completedSession(), "assessor confirmation")
        val amended = service.amendSession(locked, "assessor-1", "Corrected notes")
        val invalidCurrentRevision = amended.copy(
            samplePreparation = amended.samplePreparation.copy(cupCount = 0)
        ).let { it.copy(currentSha256Checksum = it.computeSha256()) }

        assertThrows(IllegalArgumentException::class.java) {
            CvaReportExportService.exportToNativeJson(invalidCurrentRevision)
        }
        assertThrows(IllegalArgumentException::class.java) {
            CvaReportExportService.exportToNativeCsv(invalidCurrentRevision)
        }
    }

    @Test
    fun givenCsvExport_whenChecksumColumnIsRemoved_thenRowPayloadMatchesDigest() {
        val csv = CvaReportExportService.exportToNativeCsv(completedSession())
        val lines = csv.trimEnd().split('\n')
        val dataRow = lines[1]
        val checksumColumnSeparator = dataRow.lastIndexOf(",\"")
        val rowPayload = dataRow.substring(0, checksumColumnSeparator) + "\n"
        val checksum = dataRow.substring(checksumColumnSeparator + 2).trimEnd('"')

        assertEquals(sha256(rowPayload), checksum)
    }

    private fun completedSession(): CuppingSession {
        val session = service.createSession(
            protocol = CuppingProtocol.CUSTOM_RESEARCH,
            assessorId = "assessor-1",
            assessorPseudonym = "Cup 1",
            lots = listOf("lot-1" to "Lot 1")
        )
        return completeSample(session)
    }

    private fun sha256(value: String): String {
        return MessageDigest.getInstance("SHA-256")
            .digest(value.toByteArray(Charsets.UTF_8))
            .joinToString("") { "%02x".format(it) }
    }

    private fun canonicalJson(element: JsonElement): String =
        GsonBuilder().serializeNulls().disableHtmlEscaping().create().toJson(sortJson(element))

    private fun sortJson(element: JsonElement): JsonElement = when {
        element.isJsonObject -> JsonObject().apply {
            element.asJsonObject.entrySet().sortedBy { it.key }.forEach { (key, value) -> add(key, sortJson(value)) }
        }
        element.isJsonArray -> JsonArray().apply { element.asJsonArray.forEach { add(sortJson(it)) } }
        else -> element
    }

    private fun completeSample(session: CuppingSession): CuppingSession {
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
