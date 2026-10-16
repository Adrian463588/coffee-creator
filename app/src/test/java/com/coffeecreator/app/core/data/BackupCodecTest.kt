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
        assertEquals(null, decoded.payload.recipes.single().measuredTdsPercent)
        assertEquals(null, decoded.payload.recipes.single().beverageMassGrams)
        assertFalse(encoded.contains("VALID_INTEGRITY_CHECK_OK"))
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
            BackupCodec.decode(encoded.replace("\"schemaVersion\":3", "\"schemaVersion\":99"))
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
