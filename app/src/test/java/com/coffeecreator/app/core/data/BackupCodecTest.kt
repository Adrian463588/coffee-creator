package com.coffeecreator.app.core.data

import com.coffeecreator.app.core.model.AssistantPersona
import com.coffeecreator.app.core.model.ChatMessage
import com.coffeecreator.app.core.model.CoffeeBean
import com.coffeecreator.app.core.model.DocumentChunk
import com.coffeecreator.app.core.model.Ingredient
import com.coffeecreator.app.core.model.KnowledgeDocument
import com.coffeecreator.app.core.model.Recipe
import com.coffeecreator.app.core.model.RecipeLine
import com.coffeecreator.app.core.model.SensoryObservation
import com.coffeecreator.app.core.sensory.cva.BlindSample
import com.coffeecreator.app.core.sensory.cva.CuppingSession
import com.coffeecreator.app.core.sensory.cva.ProtocolSpec
import com.coffeecreator.app.core.sensory.cva.SampleAssessment
import com.coffeecreator.app.core.sensory.cva.SessionState
import com.google.gson.JsonObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class BackupCodecTest {
    @Test
    fun givenCompleteRecords_whenExportedAndDecoded_thenEveryRecordAndAbsentMeasurementSurvives() {
        val payload = completePayload()
        val encoded = BackupCodec.encode(payload, exportedAt = 42L)
        val decoded = BackupCodec.decode(encoded)

        assertEquals(payload, decoded.payload)
        assertEquals(42L, decoded.manifest.exportedAt)
        assertEquals(1, decoded.manifest.recordCounts["recipes"])
        assertEquals(1, decoded.manifest.recordCounts["chatMessages"])
        assertEquals(4, decoded.manifest.schemaVersion)
        assertEquals(null, decoded.payload.recipes.single().measuredTdsPercent)
        assertEquals(null, decoded.payload.recipes.single().beverageMassGrams)
        assertFalse(encoded.contains("VALID_INTEGRITY_CHECK_OK"))
    }

    @Test
    fun givenCvaSessionWithUnknownEvidence_whenExportedAndDecoded_thenStableIdsAndValuesSurvive() {
        val session = cvaSession()
        val payload = completePayload().copy(cvaSessions = listOf(session))

        val encoded = BackupCodec.encode(payload, exportedAt = 88L)
        val decoded = BackupCodec.decode(encoded)

        assertEquals(session, decoded.payload.cvaSessions.single())
        assertEquals(1, decoded.manifest.recordCounts["cvaSessions"])
    }

    @Test
    fun givenV2AndV3Backups_whenDecoded_thenCvaCollectionIsEmptyAndLegacyChecksumRemainsValid() {
        listOf(2, 3).forEach { version ->
            val envelope = RecordJson.gson.fromJson(BackupCodec.encode(completePayload()), JsonObject::class.java)
            val payload = envelope["payload"].asJsonObject
            payload.remove("cvaSessions")
            if (version == 2) payload.remove("estimates")
            val manifest = envelope["manifest"].asJsonObject
            val counts = manifest["recordCounts"].asJsonObject
            counts.remove("cvaSessions")
            if (version == 2) counts.remove("estimates")
            manifest.addProperty("schemaVersion", version)
            manifest.addProperty("payloadSha256", BackupCodec.sha256(RecordJson.gson.toJson(payload)))

            val decoded = BackupCodec.decode(RecordJson.gson.toJson(envelope))

            assertTrue(decoded.payload.cvaSessions.isEmpty())
            assertEquals(version, decoded.manifest.schemaVersion)
        }
    }

    @Test
    fun givenDuplicateOrTamperedCvaSessions_whenEncoded_thenBackupIsRejected() {
        val session = cvaSession()
        assertThrows(IllegalArgumentException::class.java) {
            BackupCodec.encode(completePayload().copy(cvaSessions = listOf(session, session)))
        }
        assertThrows(IllegalArgumentException::class.java) {
            BackupCodec.encode(completePayload().copy(cvaSessions = listOf(session.copy(currentSha256Checksum = "0".repeat(64)))))
        }
    }

    @Test
    fun givenRehashedBackupWithInvalidCvaMeasurement_whenDecoded_thenItIsRejected() {
        val validEnvelope = RecordJson.gson.fromJson(
            BackupCodec.encode(completePayload().copy(cvaSessions = listOf(cvaSession()))),
            JsonObject::class.java
        )
        val payload = validEnvelope["payload"].asJsonObject
        val sessionJson = payload["cvaSessions"].asJsonArray[0].asJsonObject
        sessionJson.getAsJsonArray("samples")[0].asJsonObject.getAsJsonObject("physical")
            .addProperty("quakersCountIn100g", -1)
        val invalidSession = RecordJson.gson.fromJson(sessionJson, CuppingSession::class.java)
            .let { it.copy(currentSha256Checksum = "").let { unsigned -> unsigned.copy(currentSha256Checksum = unsigned.computeSha256()) } }
        payload.getAsJsonArray("cvaSessions").set(0, RecordJson.gson.toJsonTree(invalidSession))
        validEnvelope["manifest"].asJsonObject.addProperty(
            "payloadSha256",
            BackupCodec.sha256(RecordJson.gson.toJson(payload))
        )

        assertThrows(IllegalArgumentException::class.java) {
            BackupCodec.decode(RecordJson.gson.toJson(validEnvelope))
        }
    }

    @Test
    fun givenRehashedBackupWithInvalidPreparation_whenDecoded_thenRejected() {
        val validPreparation = cvaSession().samplePreparation
        val invalidPreparations = listOf(
            validPreparation.copy(cupCount = 0),
            validPreparation.copy(roastRestDays = -1),
            validPreparation.copy(roastLevelAgtron = 101.0),
            validPreparation.copy(roastDateEpochMs = -1L),
            validPreparation.copy(grindMicrons = 0),
            validPreparation.copy(equipment = validPreparation.equipment.copy(grindMicrons = -1)),
            validPreparation.copy(equipment = validPreparation.equipment.copy(bowlCountPerSample = 0))
        )

        invalidPreparations.forEach { preparation ->
            val envelope = RecordJson.gson.fromJson(
                BackupCodec.encode(completePayload().copy(cvaSessions = listOf(cvaSession()))),
                JsonObject::class.java
            )
            val payload = envelope["payload"].asJsonObject
            val invalidSession = cvaSession().copy(samplePreparation = preparation, currentSha256Checksum = "")
                .let { it.copy(currentSha256Checksum = it.computeSha256()) }
            payload.getAsJsonArray("cvaSessions").set(0, RecordJson.gson.toJsonTree(invalidSession))
            envelope["manifest"].asJsonObject.addProperty(
                "payloadSha256",
                BackupCodec.sha256(RecordJson.gson.toJson(payload))
            )

            assertThrows(IllegalArgumentException::class.java) {
                BackupCodec.decode(RecordJson.gson.toJson(envelope))
            }
        }
    }

    @Test
    fun givenMalformedDuplicateOrTamperedCvaPayload_whenDecoded_thenItIsRejected() {
        val payload = completePayload().copy(cvaSessions = listOf(cvaSession()))
        val validEnvelope = RecordJson.gson.fromJson(BackupCodec.encode(payload), JsonObject::class.java)
        val duplicateEnvelope = validEnvelope.deepCopy()
        val duplicatePayload = duplicateEnvelope["payload"].asJsonObject
        val sessions = duplicatePayload["cvaSessions"].asJsonArray
        sessions.add(sessions[0].deepCopy())
        duplicateEnvelope["manifest"].asJsonObject["recordCounts"].asJsonObject.addProperty("cvaSessions", 2)
        duplicateEnvelope["manifest"].asJsonObject.addProperty(
            "payloadSha256",
            BackupCodec.sha256(RecordJson.gson.toJson(duplicatePayload))
        )
        assertThrows(IllegalArgumentException::class.java) { BackupCodec.decode(RecordJson.gson.toJson(duplicateEnvelope)) }

        val malformedEnvelope = validEnvelope.deepCopy()
        val malformedPayload = malformedEnvelope["payload"].asJsonObject
        malformedPayload["cvaSessions"].asJsonArray[0].asJsonObject
            .getAsJsonObject("protocolSpec").addProperty("protocol", "UNSUPPORTED_PROTOCOL")
        malformedEnvelope["manifest"].asJsonObject.addProperty(
            "payloadSha256",
            BackupCodec.sha256(RecordJson.gson.toJson(malformedPayload))
        )
        assertThrows(IllegalArgumentException::class.java) { BackupCodec.decode(RecordJson.gson.toJson(malformedEnvelope)) }

        val tamperedEnvelope = validEnvelope.deepCopy()
        tamperedEnvelope["payload"].asJsonObject["cvaSessions"].asJsonArray[0].asJsonObject
            .addProperty("assessorPseudonym", "changed without a new checksum")
        assertThrows(IllegalArgumentException::class.java) { BackupCodec.decode(RecordJson.gson.toJson(tamperedEnvelope)) }
    }

    @Test
    fun givenDurableWorkbenchSettings_whenExportedAndDecoded_thenEverySettingSurvives() {
        val settings = mapOf(
            "appearance.darkTheme" to "true",
            "draft" to "{\"doseGrams\":15.0}",
            "cuppings" to "[]",
            "journal_entries" to "[]",
            "palate_profile" to "{\"qualifiedSessionCount\":2}",
            "weather_parameters" to "{\"elevationMeters\":1200}",
            "user_units" to "{\"mass\":\"GRAMS\"}",
            "user_language" to "id"
        )

        val decoded = BackupCodec.decode(
            BackupCodec.encode(completePayload().copy(settings = settings), exportedAt = 84L)
        )

        assertEquals(settings, decoded.payload.settings)
        assertEquals(settings.size, decoded.manifest.recordCounts["settings"])
    }

    @Test
    fun givenChangedPayload_whenDecoded_thenChecksumMismatchIsRejected() {
        val encoded = BackupCodec.encode(completePayload())
        assertThrows(IllegalArgumentException::class.java) {
            BackupCodec.decode(encoded.replace("Original Recipe", "Altered Recipe"))
        }
    }

    @Test
    fun givenUnsupportedSchema_whenDecoded_thenNoMigrationIsInvented() {
        val encoded = BackupCodec.encode(completePayload())
        assertThrows(IllegalArgumentException::class.java) {
            BackupCodec.decode(encoded.replace("\"schemaVersion\":4", "\"schemaVersion\":99"))
        }
    }

    @Test
    fun givenDuplicateIds_whenEncoded_thenAmbiguousRecordsAreRejected() {
        val payload = completePayload()
        assertThrows(IllegalArgumentException::class.java) {
            BackupCodec.encode(payload.copy(beans = payload.beans + payload.beans))
        }
    }

    @Test
    fun givenDanglingIngredientOrObservation_whenEncoded_thenRelationsAreRejected() {
        val payload = completePayload()
        assertThrows(IllegalArgumentException::class.java) {
            BackupCodec.encode(payload.copy(ingredients = emptyList()))
        }
        assertThrows(IllegalArgumentException::class.java) {
            BackupCodec.encode(payload.copy(recipes = emptyList()))
        }
    }

    @Test
    fun givenWrongCountsOrNonJson_whenDecoded_thenImportIsRejected() {
        val encoded = BackupCodec.encode(completePayload())
        assertThrows(IllegalArgumentException::class.java) {
            BackupCodec.decode(encoded.replace("\"recipes\":1", "\"recipes\":2"))
        }
        assertThrows(IllegalArgumentException::class.java) { BackupCodec.decode("not a backup") }
    }

    @Test
    fun givenMissingCollections_whenDecoded_thenMissingDataIsNotSilentlyDefaulted() {
        assertThrows(IllegalArgumentException::class.java) {
            BackupCodec.decode("{\"manifest\":{},\"payload\":{}}")
        }
    }

    @Test
    fun givenTooLargeInput_whenDecoded_thenItIsRejectedBeforeParsing() {
        assertThrows(IllegalArgumentException::class.java) {
            BackupCodec.decode(" ".repeat(BackupCodec.MAX_BACKUP_CHARS + 1))
        }
    }

    @Test
    fun givenCoercedNumericStringWithMatchingChecksum_whenDecoded_thenSchemaStillRejectsIt() {
        val tree = RecordJson.gson.fromJson(BackupCodec.encode(completePayload()), JsonObject::class.java)
        val payload = tree["payload"].asJsonObject
        payload["recipes"].asJsonArray[0].asJsonObject.addProperty("doseGrams", "18")
        tree["manifest"].asJsonObject.addProperty("payloadSha256", BackupCodec.sha256(RecordJson.gson.toJson(payload)))
        assertThrows(IllegalArgumentException::class.java) { BackupCodec.decode(RecordJson.gson.toJson(tree)) }
    }

    @Test
    fun givenRuntimeCacheOrOwnershipKeys_whenEncoded_thenTheyCannotEnterRecordsBackup() {
        listOf("vector:minilm-afdb6f1a:chunk-1", "checkpoint:model-1", "assetDiscoveryError").forEach { key ->
            assertThrows(IllegalArgumentException::class.java) {
                BackupCodec.encode(completePayload().copy(settings = mapOf(key to "private runtime metadata")))
            }
        }
    }

    @Test
    fun given384DimensionalFloatVector_whenSerialized_thenSettingsLimitCanStoreIt() {
        val serialized = RecordJson.gson.toJson(FloatArray(384) { index -> kotlin.math.sin(index.toDouble()).toFloat() })
        assertTrue(serialized.length < BackupCodec.MAX_SETTING_CHARS)
        BackupCodec.validateSetting("vector:minilm-afdb6f1a:chunk-1", serialized)
    }

    @Test
    fun givenOversizedSetting_whenValidated_thenItIsRejected() {
        assertThrows(IllegalArgumentException::class.java) {
            BackupCodec.validateSetting("draft", "x".repeat(BackupCodec.MAX_SETTING_CHARS + 1))
        }
    }

    companion object {
        private fun cvaSession(): CuppingSession {
            val unsigned = CuppingSession(
                id = "cva-session-1",
                protocolSpec = ProtocolSpec.CUSTOM,
                assessorId = "assessor-1",
                assessorPseudonym = "A1",
                createdAtEpochMs = 1L,
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

        fun completePayload(): BackupPayload {
            val content = "Storage test document supplied by the test."
            val document = KnowledgeDocument(
                id = "document-1",
                title = "Storage test reference",
                sourceUri = "content://test.documents/document/1",
                fileType = "TXT",
                pageCount = 0,
                chunkCount = 1,
                contentHash = BackupCodec.sha256(content),
                importedAt = 1L
            )
            return BackupPayload(
                beans = listOf(CoffeeBean("bean-1", "User bean", "Unknown", "Unknown")),
                ingredients = listOf(Ingredient("ingredient-1", "User water", foodGrade = true, provenance = "User supplied")),
                recipes = listOf(
                    Recipe(
                        id = "recipe-1",
                        name = "Original Recipe",
                        beanId = "bean-1",
                        beverageMassGrams = null,
                        measuredTdsPercent = null,
                        lines = listOf(RecipeLine("ingredient-1", 10.0)),
                        createdAt = 1L,
                        updatedAt = 1L
                    )
                ),
                observations = listOf(
                    SensoryObservation(
                        "observation-1", "recipe-1", aroma = 8, acidity = 7,
                        sweetness = 8, body = 6, finish = 7, balance = 8,
                        assessor = "Test assessor", timestamp = 2L
                    )
                ),
                documents = listOf(document),
                chunks = listOf(DocumentChunk("chunk-1", document.id, document.title, 0, 1, content)),
                chatMessages = listOf(ChatMessage("message-1", "user", "User conversation", timestamp = 3L)),
                personas = listOf(AssistantPersona("persona-1", "Custom persona", "User editable", "Answer with evidence.")),
                settings = mapOf("appearance.darkTheme" to "true", "recipe.draft" to "{\"dose\":\"18,5\"}")
            )
        }
    }
}
