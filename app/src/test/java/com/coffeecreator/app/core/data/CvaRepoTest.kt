package com.coffeecreator.app.core.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.coffeecreator.app.core.model.RecipeLine
import com.coffeecreator.app.core.sensory.cva.AffectiveAssessment
import com.coffeecreator.app.core.sensory.cva.BlindSample
import com.coffeecreator.app.core.sensory.cva.CupDefectCategory
import com.coffeecreator.app.core.sensory.cva.CuppingSession
import com.coffeecreator.app.core.sensory.cva.DescriptiveAssessment
import com.coffeecreator.app.core.sensory.cva.EvidenceStatus
import com.coffeecreator.app.core.sensory.cva.Legacy2004CuppingSheet
import com.coffeecreator.app.core.sensory.cva.MultiCupEvaluationGrid
import com.coffeecreator.app.core.sensory.cva.ProtocolSpec
import com.coffeecreator.app.core.sensory.cva.SampleAssessment
import com.coffeecreator.app.core.sensory.cva.SessionState
import com.coffeecreator.app.core.sensory.cva.SingleCupEvaluation
import com.coffeecreator.app.core.sensory.cva.StandardCvaSessionService
import com.google.gson.JsonObject
import java.math.BigDecimal
import java.util.UUID
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertThrows
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class CvaRepoTest {
    @Test
    fun givenDraft_whenReopened_thenStable() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val databaseName = "cva-reopen-${UUID.randomUUID()}.db"
        val source = BackupCodecTest.completePayload()
        val legacyBean = source.beans.single().copy(id = "legacy-bean")
        val legacyIngredient = source.ingredients.single().copy(id = "legacy-ingredient")
        val legacyRecipe = source.recipes.single().copy(
            id = "legacy-recipe",
            revisionGroupId = "legacy-recipe",
            beanId = legacyBean.id,
            lines = listOf(RecipeLine(legacyIngredient.id, 10.0)),
            createdAt = 1L,
            updatedAt = 1L
        )
        val legacyObservation = source.observations.single().copy(
            id = "legacy-observation",
            recipeId = legacyRecipe.id,
            timestamp = 2L
        )
        val legacyRecords = listOf(
            StoredRecord("bean", legacyBean.id, RecordJson.gson.toJson(legacyBean)),
            StoredRecord("ingredient", legacyIngredient.id, RecordJson.gson.toJson(legacyIngredient)),
            StoredRecord("recipe", legacyRecipe.id, RecordJson.gson.toJson(legacyRecipe)),
            StoredRecord("observation", legacyObservation.id, RecordJson.gson.toJson(legacyObservation)),
            StoredRecord("setting", "cuppings", "\"legacy-s2-session\"")
        )
        try {
            val database = openDatabase(context, databaseName)
            database.dao().putRecords(legacyRecords)
            val repository = DataRepository(context, database)
            repository.initialize()
            val baseline = BackupCodec.decode(repository.exportBackupJson()).payload

            val session = cvaDraft()
            repository.createCvaSession(session)
            repository.close()

            val reopenedDatabase = openDatabase(context, databaseName)
            val reopened = DataRepository(context, reopenedDatabase)
            reopened.initialize()

            assertEquals(session, reopened.findCvaSession(session.id))
            assertEquals(session, reopened.cvaSessions.value.single())
            assertEquals(legacyRecords, legacyRecords.map { reopenedDatabase.dao().record(it.kind, it.id) })
            val restoredPayload = BackupCodec.decode(reopened.exportBackupJson()).payload
            assertEquals(session, restoredPayload.cvaSessions.single())
            assertEquals(baseline.beans, restoredPayload.beans)
            assertEquals(baseline.ingredients, restoredPayload.ingredients)
            assertEquals(baseline.recipes, restoredPayload.recipes)
            assertEquals(baseline.observations, restoredPayload.observations)
            reopened.close()
        } finally {
            context.deleteDatabase(databaseName)
        }
    }

    @Test
    fun failedWritesPreserveOriginal() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val databaseName = "cva-atomic-${UUID.randomUUID()}.db"
        try {
            val repository = DataRepository(context, openDatabase(context, databaseName))
            repository.initialize()
            val original = cvaDraft()
            repository.createCvaSession(original)
            val invalidTemperature = original.copy(
                samplePreparation = original.samplePreparation.copy(waterTempCelsius = 120.0)
            ).let { it.copy(currentSha256Checksum = it.computeSha256()) }

            assertThrows(IllegalArgumentException::class.java) {
                runBlocking { repository.updateCvaSession(invalidTemperature) }
            }

            assertThrows(IllegalArgumentException::class.java) {
                runBlocking { repository.createCvaSession(original) }
            }
            assertThrows(IllegalArgumentException::class.java) {
                runBlocking { repository.updateCvaSession(cvaDraft("missing-session")) }
            }
            assertThrows(IllegalArgumentException::class.java) {
                runBlocking { repository.updateCvaSession(original.copy(currentSha256Checksum = "0".repeat(64))) }
            }
            val corruptedBackup = RecordJson.gson.fromJson(repository.exportBackupJson(), JsonObject::class.java)
            corruptedBackup["payload"].asJsonObject["cvaSessions"].asJsonArray[0].asJsonObject
                .getAsJsonArray("samples")[0].asJsonObject.getAsJsonObject("blindSample")
                .addProperty("actualBeanName", "tampered")
            assertThrows(IllegalArgumentException::class.java) {
                runBlocking { repository.restoreBackupJson(RecordJson.gson.toJson(corruptedBackup)) }
            }

            assertEquals(listOf(original), repository.cvaSessions.value)
            assertEquals(original, repository.findCvaSession(original.id))
            repository.close()
        } finally {
            context.deleteDatabase(databaseName)
        }
    }

    @Test
    fun givenInvalidCvaFields_whenSaved_thenRejectedAtomically() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val databaseName = "cva-assessment-validation-${UUID.randomUUID()}.db"
        val repository = DataRepository(context, openDatabase(context, databaseName))
        try {
            repository.initialize()
            val original = cvaDraft()
            val sample = original.samples.single()
            val invalidSessions = listOf(
                original.copy(samples = listOf(sample.copy(physical = sample.physical.copy(quakersCountIn100g = -1)))),
                original.copy(
                    samples = listOf(sample.copy(multiCupGrid = MultiCupEvaluationGrid(cups = listOf(SingleCupEvaluation(6)))))
                ),
                original.copy(
                    samples = listOf(
                        sample.copy(
                            multiCupGrid = MultiCupEvaluationGrid(
                                cups = listOf(
                                    SingleCupEvaluation(
                                        cupIndex = 1,
                                        isClean = false,
                                        defectCategory = CupDefectCategory.TAINT,
                                        defectIntensity = 6.0
                                    )
                                )
                            )
                        )
                    )
                ),
                original.copy(samples = listOf(sample.copy(legacySheet = Legacy2004CuppingSheet(fragranceScore = BigDecimal("11.00"))))),
                original.copy(
                    samples = listOf(
                        sample.copy(
                            legacySheet = Legacy2004CuppingSheet(
                                taintCupCount = Int.MAX_VALUE,
                                faultCupCount = Int.MAX_VALUE
                            )
                        )
                    )
                ),
                original.copy(samplePreparation = original.samplePreparation.copy(cupCount = 0)),
                original.copy(samplePreparation = original.samplePreparation.copy(roastRestDays = -1)),
                original.copy(samplePreparation = original.samplePreparation.copy(roastLevelAgtron = 101.0)),
                original.copy(samplePreparation = original.samplePreparation.copy(roastDateEpochMs = -1L)),
                original.copy(samplePreparation = original.samplePreparation.copy(grindMicrons = 0)),
                original.copy(
                    samplePreparation = original.samplePreparation.copy(
                        equipment = original.samplePreparation.equipment.copy(grindMicrons = -1)
                    )
                ),
                original.copy(
                    samplePreparation = original.samplePreparation.copy(
                        equipment = original.samplePreparation.equipment.copy(bowlCountPerSample = 0)
                    )
                )
            ).map { session ->
                session.copy(currentSha256Checksum = "").let { it.copy(currentSha256Checksum = it.computeSha256()) }
            }

            invalidSessions.forEach { invalid ->
                assertThrows(IllegalArgumentException::class.java) {
                    runBlocking { repository.createCvaSession(invalid) }
                }
                assertEquals(null, repository.findCvaSession(invalid.id))
                assertEquals(emptyList<CuppingSession>(), repository.cvaSessions.value)
            }
        } finally {
            repository.close()
            context.deleteDatabase(databaseName)
        }
    }

    @Test
    fun givenLockedCva_whenAmended_thenPriorSnapshotRemainsVerified() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val databaseName = "cc-cva-amend-${UUID.randomUUID()}.db"
        try {
            val repository = DataRepository(context, openDatabase(context, databaseName))
            repository.initialize()
            val initial = cvaDraft()
            repository.createCvaSession(initial)
            val service = StandardCvaSessionService()
            val complete = initial.copy(
                samplePreparation = initial.samplePreparation.copy(preparationEvidence = EvidenceStatus.MEASURED),
                state = SessionState.IN_PROGRESS,
                samples = initial.samples.map { sample ->
                    sample.copy(
                        descriptive = DescriptiveAssessment(flavorIntensity = 1.0, evidenceStatus = EvidenceStatus.OBSERVED),
                        affective = AffectiveAssessment(
                            fragranceAffectiveScore = BigDecimal("7.00"),
                            flavorAffectiveScore = BigDecimal("7.00"),
                            aftertasteAffectiveScore = BigDecimal("7.00"),
                            acidityAffectiveScore = BigDecimal("7.00"),
                            sweetnessAffectiveScore = BigDecimal("7.00"),
                            mouthfeelAffectiveScore = BigDecimal("7.00"),
                            overallAffectiveScore = BigDecimal("7.00"),
                            evidenceStatus = EvidenceStatus.OBSERVED
                        ),
                        isCompleted = true
                    )
                }
            ).let { it.copy(currentSha256Checksum = it.computeSha256()) }
            repository.updateCvaSession(complete)

            val locked = service.lockSession(complete, "A1")
            repository.updateCvaSession(locked)
            val invalidRewrite = locked.copy(
                state = SessionState.IN_PROGRESS,
                lockedAtEpochMs = null,
                assessorSignature = null
            ).let { it.copy(currentSha256Checksum = it.computeSha256()) }
            assertThrows(IllegalArgumentException::class.java) {
                runBlocking { repository.updateCvaSession(invalidRewrite) }
            }

            val amended = service.amendSession(locked, "assessor-1", "Correct a transcription error")
            repository.updateCvaSession(amended)

            val invalidPreviousSnapshot = locked.copy(
                samples = locked.samples.map { sample ->
                    sample.copy(affective = sample.affective.copy(fragranceAffectiveScore = BigDecimal("11.00")))
                }
            ).let { it.copy(currentSha256Checksum = it.computeSha256()) }
            val forgedAmendment = amended.amendmentHistory.single().copy(
                previousSnapshotSha256 = invalidPreviousSnapshot.currentSha256Checksum,
                previousSnapshotJson = invalidPreviousSnapshot.toSnapshotJson()
            )
            val forgedSession = amended.copy(amendmentHistory = listOf(forgedAmendment))
                .let { it.copy(currentSha256Checksum = it.computeSha256()) }

            assertThrows(IllegalArgumentException::class.java) {
                runBlocking { repository.updateCvaSession(forgedSession) }
            }

            assertEquals(SessionState.AMENDED, repository.findCvaSession(initial.id)?.state)
            assertEquals(amended, repository.findCvaSession(initial.id))
            assertEquals(locked.toSnapshotJson(), amended.amendmentHistory.single().previousSnapshotJson)
            assertEquals(amended, BackupCodec.decode(repository.exportBackupJson()).payload.cvaSessions.single())
            repository.close()
        } finally {
            context.deleteDatabase(databaseName)
        }
    }

    @Test
    fun givenDraftHistory_whenWrite_rejected() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val databaseName = "cva-history-${UUID.randomUUID()}.db"
        val repository = DataRepository(context, openDatabase(context, databaseName))
        try {
            repository.initialize()
            val amended = amendedCvaSession("cva-create-history")
            val forgedDraft = amended.copy(
                state = SessionState.DRAFT,
                lockedAtEpochMs = null,
                assessorSignature = null
            ).let { it.copy(currentSha256Checksum = it.computeSha256()) }

            assertThrows(IllegalArgumentException::class.java) {
                runBlocking { repository.createCvaSession(forgedDraft) }
            }
            assertEquals(null, repository.findCvaSession(forgedDraft.id))

            val original = cvaDraft("cva-update-history")
            repository.createCvaSession(original)
            val forgedUpdate = amendedCvaSession(original.id).copy(
                state = SessionState.PREPARED,
                lockedAtEpochMs = null,
                assessorSignature = null
            ).let { it.copy(currentSha256Checksum = it.computeSha256()) }

            assertThrows(IllegalArgumentException::class.java) {
                runBlocking { repository.updateCvaSession(forgedUpdate) }
            }
            assertEquals(original, repository.findCvaSession(original.id))
        } finally {
            repository.close()
            context.deleteDatabase(databaseName)
        }
    }

    @Test
    fun legacyRestorePreservesCva() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val databaseName = "cva-legacy-restore-${UUID.randomUUID()}.db"
        try {
            val repository = DataRepository(context, openDatabase(context, databaseName))
            repository.initialize()
            val existing = cvaDraft()
            repository.createCvaSession(existing)

            repository.restoreBackupJson(legacyV3Backup())

            assertEquals(listOf(existing), repository.cvaSessions.value)
            assertNotNull(repository.findCvaSession(existing.id))
            repository.close()
        } finally {
            context.deleteDatabase(databaseName)
        }
    }

    @Test
    fun v4RestoreReplacesCva() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val databaseName = "cc-cva-v4-restore-${UUID.randomUUID()}.db"
        try {
            val repository = DataRepository(context, openDatabase(context, databaseName))
            repository.initialize()
            val expected = cvaDraft("cva-before-backup")
            repository.createCvaSession(expected)
            val backup = repository.exportBackupJson()
            repository.createCvaSession(cvaDraft("cva-after-backup"))

            repository.restoreBackupJson(backup)

            assertEquals(4, BackupCodec.decode(backup).manifest.schemaVersion)
            assertEquals(listOf(expected), repository.cvaSessions.value)
            repository.close()
        } finally {
            context.deleteDatabase(databaseName)
        }
    }

    private fun openDatabase(context: Context, name: String): CoffeeDatabase =
        Room.databaseBuilder(context, CoffeeDatabase::class.java, name).apply {
            context.getDatabasePath(name).parentFile?.mkdirs()
        }
            .addMigrations(CoffeeDatabase.MIGRATION_1_2)
            .build()

    private fun amendedCvaSession(id: String): CuppingSession {
        val draft = cvaDraft(id)
        val complete = draft.copy(
            state = SessionState.IN_PROGRESS,
            samplePreparation = draft.samplePreparation.copy(preparationEvidence = EvidenceStatus.MEASURED),
            samples = draft.samples.map { sample ->
                sample.copy(
                    descriptive = DescriptiveAssessment(flavorIntensity = 1.0, evidenceStatus = EvidenceStatus.OBSERVED),
                    affective = AffectiveAssessment(
                        fragranceAffectiveScore = BigDecimal("7.00"),
                        flavorAffectiveScore = BigDecimal("7.00"),
                        aftertasteAffectiveScore = BigDecimal("7.00"),
                        acidityAffectiveScore = BigDecimal("7.00"),
                        sweetnessAffectiveScore = BigDecimal("7.00"),
                        mouthfeelAffectiveScore = BigDecimal("7.00"),
                        overallAffectiveScore = BigDecimal("7.00"),
                        evidenceStatus = EvidenceStatus.OBSERVED
                    ),
                    isCompleted = true
                )
            }
        ).let { it.copy(currentSha256Checksum = it.computeSha256()) }
        val service = StandardCvaSessionService()
        val locked = service.lockSession(complete, "A1")
        return service.amendSession(locked, "assessor-1", "Correct a transcription error")
    }

    private fun cvaDraft(id: String = "cva-repository-session"): CuppingSession {
        val unsigned = CuppingSession(
            id = id,
            protocolSpec = ProtocolSpec.CUSTOM,
            assessorId = "assessor-1",
            assessorPseudonym = "A1",
            createdAtEpochMs = 10L,
            state = SessionState.DRAFT,
            samples = listOf(
                SampleAssessment(
                    blindSample = BlindSample(
                        sampleId = "sample-1",
                        blindCode = "384",
                        actualLotId = "lot-1",
                        actualBeanName = "Unknown lot"
                    )
                )
            )
        )
        return unsigned.copy(currentSha256Checksum = unsigned.computeSha256())
    }

    private fun legacyV3Backup(): String {
        val envelope = RecordJson.gson.fromJson(BackupCodec.encode(BackupCodecTest.completePayload()), JsonObject::class.java)
        val payload = envelope["payload"].asJsonObject
        payload.remove("cvaSessions")
        val manifest = envelope["manifest"].asJsonObject
        val counts = manifest["recordCounts"].asJsonObject
        counts.remove("cvaSessions")
        manifest.addProperty("schemaVersion", 3)
        manifest.addProperty("payloadSha256", BackupCodec.sha256(RecordJson.gson.toJson(payload)))
        return RecordJson.gson.toJson(envelope)
    }
}
