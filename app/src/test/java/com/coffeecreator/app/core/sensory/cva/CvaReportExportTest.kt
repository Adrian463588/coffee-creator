package com.coffeecreator.app.core.sensory.cva

import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class CvaReportExportTest : CvaDomainEngineTestSupport() {

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
        assertEquals(
            listOf(
                "SessionId",
                "Protocol",
                "Assessor",
                "BlindCode",
                "Revealed",
                "ActualBean",
                "FlavorIntensity",
                "AcidityIntensity",
                "SweetnessIntensity",
                "MouthfeelIntensity",
                "CleanCups",
                "AffectiveScore",
                "PreparationJson",
                "AssessmentJson",
                "AmendmentsJson",
                "RowPayloadSha256"
            ).joinToString(","),
            csv.lineSequence().first()
        )
        assertTrue(csv.contains(session.id))
        assertTrue(csv.contains("\"Evaluator \"\"A\"\"\nCafé\""))
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
}
