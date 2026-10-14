package com.coffeecreator.app.core.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.test.platform.app.InstrumentationRegistry
import com.coffeecreator.app.core.model.ChatMessage
import com.coffeecreator.app.core.model.CoffeeBean
import com.coffeecreator.app.core.model.DocumentChunk
import com.coffeecreator.app.core.model.Ingredient
import com.coffeecreator.app.core.model.KnowledgeDocument
import com.coffeecreator.app.core.model.ModelAsset
import com.coffeecreator.app.core.model.Recipe
import com.coffeecreator.app.core.model.RecipeLine
import com.coffeecreator.app.core.model.SensoryObservation
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.UUID

class DataRepositoryTest {
    private val context: Context = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun givenSavedRecipeAndDraft_whenDatabaseReopens_thenDataAndRevisionHistorySurvive() = runBlocking {
        val name = "persistence-${UUID.randomUUID()}.db"
        val repository = DataRepository(context, CoffeeDatabase.open(context, name))
        try {
            repository.initialize()
            val bean = CoffeeBean("user-bean", "User-owned bean", "Unknown", "Unknown")
            repository.addBean(bean)
            val first = repository.saveRecipe(Recipe("", "First brew", beanId = bean.id))
            val second = repository.saveRecipe(first.copy(name = "Second brew", waterGrams = 275.0))
            repository.saveSetting("recipe.draft", "18,5")
            assertNotEquals(first.id, second.id)
            assertEquals(first.revisionGroupId, second.revisionGroupId)
            assertEquals(2, second.version)
            repository.close()

            val reopened = DataRepository(context, CoffeeDatabase.open(context, name))
            try {
                reopened.initialize()
                assertEquals(bean, reopened.findBeanById(bean.id))
                assertEquals(first, reopened.findRecipeById(first.id))
                assertEquals(second, reopened.findRecipeById(second.id))
                assertEquals("18,5", reopened.settings.value["recipe.draft"])
            } finally {
                reopened.close()
            }
        } finally {
            repository.close()
            context.deleteDatabase(name)
        }
    }

    @Test
    fun givenGoodRecords_whenCorruptBackupIsRestored_thenExistingRecordsRemainIntact() = runBlocking {
        val name = "backup-${UUID.randomUUID()}.db"
        val repository = DataRepository(context, CoffeeDatabase.open(context, name))
        try {
            repository.initialize()
            val recipe = repository.saveRecipe(Recipe("", "Preserved brew"))
            val backup = repository.exportBackupJson()
            val result = runCatching { repository.restoreBackupJson(backup.replace("Preserved brew", "Corrupted brew")) }
            assertTrue(result.isFailure)
            assertEquals(recipe, repository.findRecipeById(recipe.id))
            assertEquals(1, repository.recipes.value.size)

            repository.restoreBackupJson(backup)
            assertEquals(recipe, repository.findRecipeById(recipe.id))
        } finally {
            repository.close()
            context.deleteDatabase(name)
        }
    }

    @Test
    fun givenIndexedDocument_whenRestartedAndSearched_thenFtsReturnsRealStoredChunkAndDeduplicates() = runBlocking {
        val name = "fts-${UUID.randomUUID()}.db"
        val repository = DataRepository(context, CoffeeDatabase.open(context, name))
        try {
            repository.initialize()
            val text = "Test guidance discusses kettle temperature and filtration."
            val document = KnowledgeDocument(
                "doc-1", "Test guidance", "content://test.documents/document/1", "TXT",
                pageCount = 0, chunkCount = 1, contentHash = BackupCodec.sha256(text), importedAt = 1L,
            )
            val chunk = DocumentChunk("chunk-1", document.id, document.title, 0, 1, text)
            repository.addDocumentWithChunks(document, listOf(chunk))
            val duplicate = repository.addDocumentWithChunks(
                document.copy(id = "doc-duplicate"),
                listOf(chunk.copy(id = "chunk-duplicate", documentId = "doc-duplicate")),
            )
            assertEquals(document.id, duplicate.id)
            assertEquals(listOf(chunk), repository.lexicalSearch("temperature OR \" kettle"))
            repository.close()

            val reopened = DataRepository(context, CoffeeDatabase.open(context, name))
            try {
                reopened.initialize()
                assertEquals(1, reopened.documents.value.size)
                assertEquals(listOf(chunk), reopened.lexicalSearch("temperature"))
                reopened.removeDocument(document.id)
                assertTrue(reopened.lexicalSearch("temperature").isEmpty())
            } finally {
                reopened.close()
            }
        } finally {
            repository.close()
            context.deleteDatabase(name)
        }
    }

    @Test
    fun givenVersionOneRecords_whenRoomMigratesToVersionTwo_thenRecipesAndChunksArePreserved() = runBlocking {
        val name = "migration-${UUID.randomUUID()}.db"
        val file = context.getDatabasePath(name)
        file.parentFile?.mkdirs()
        val recipe = Recipe("migration-recipe", "Preserved version-one recipe")
        val content = "Migration test retains this filtration reference."
        val document = KnowledgeDocument(
            "migration-document", "Migration reference", "content://test.documents/document/migration", "TXT",
            pageCount = 0, chunkCount = 1, contentHash = BackupCodec.sha256(content), importedAt = 1L,
        )
        val chunk = DocumentChunk("migration-chunk", document.id, document.title, 0, 1, content)
        try {
            SQLiteDatabase.openOrCreateDatabase(file, null).use { legacy ->
                legacy.execSQL("CREATE TABLE records(kind TEXT NOT NULL, id TEXT NOT NULL, json TEXT NOT NULL, PRIMARY KEY(kind, id))")
                val rows = listOf(
                    StoredRecord("meta", "initialized", "1"),
                    StoredRecord("recipe", recipe.id, RecordJson.gson.toJson(recipe)),
                    StoredRecord("persona", SeedData.personas.first().id, RecordJson.gson.toJson(SeedData.personas.first())),
                    StoredRecord("document", document.id, RecordJson.gson.toJson(document)),
                    StoredRecord("chunk", chunk.id, RecordJson.gson.toJson(chunk)),
                )
                rows.forEach { row -> legacy.execSQL("INSERT INTO records(kind, id, json) VALUES (?, ?, ?)", arrayOf(row.kind, row.id, row.json)) }
                legacy.version = 1
            }
            val migrated = DataRepository(context, CoffeeDatabase.open(context, name))
            try {
                migrated.initialize()
                assertEquals(recipe, migrated.findRecipeById(recipe.id))
                assertEquals(listOf(chunk), migrated.lexicalSearch("filtration"))
                assertTrue(migrated.ready.value)
            } finally {
                migrated.close()
            }
        } finally {
            context.deleteDatabase(name)
        }
    }

    @Test
    fun givenGoodRecipe_whenInvalidRevisionIsSaved_thenTransactionPreservesOriginal() = runBlocking {
        val name = "validation-${UUID.randomUUID()}.db"
        val repository = DataRepository(context, CoffeeDatabase.open(context, name))
        try {
            repository.initialize()
            val first = repository.saveRecipe(Recipe("", "Preserved recipe"))
            assertTrue(runCatching { repository.saveRecipe(first.copy(doseGrams = Double.NaN)) }.isFailure)
            assertEquals(listOf(first), repository.recipes.value)
            repository.close()
            val reopened = DataRepository(context, CoffeeDatabase.open(context, name))
            try {
                reopened.initialize()
                assertEquals(listOf(first), reopened.recipes.value)
            } finally {
                reopened.close()
            }
        } finally {
            repository.close()
            context.deleteDatabase(name)
        }
    }

    @Test
    fun givenCompleteExport_whenRestoredOnFreshDatabase_thenRecordsSurviveAndAssetOwnershipStaysSeparate() = runBlocking {
        val sourceName = "source-${UUID.randomUUID()}.db"
        val destinationName = "destination-${UUID.randomUUID()}.db"
        val source = DataRepository(context, CoffeeDatabase.open(context, sourceName))
        val destination = DataRepository(context, CoffeeDatabase.open(context, destinationName))
        try {
            source.initialize()
            source.addIngredient(Ingredient("water-1", "User water", foodGrade = true, provenance = "User verified label"))
            val recipe = source.saveRecipe(Recipe("", "User formulation", lines = listOf(RecipeLine("water-1", 10.0))))
            val observation = SensoryObservation(
                "observation-1", recipe.id, aroma = 8, acidity = 7, sweetness = 8,
                body = 6, finish = 7, balance = 8, assessor = "Test assessor",
                tastingDescriptors = listOf("Fruit"), descriptorIntensities = mapOf("Fruit" to 6),
            )
            source.saveSensoryObservation(observation)
            val messages = listOf(ChatMessage("message-1", "user", "User-owned conversation"))
            source.saveChatMessages(messages)
            val persona = source.personas.value.first().copy(systemPrompt = "My editable local persona instructions.")
            source.updatePersona(persona)
            source.selectPersona(persona)
            source.saveSetting("recipe.draft", "18,5")
            source.setSafDirectoryUri("content://test.documents/tree/source")
            val sourceOnlyModel = ModelAsset(
                "catalog-only-model", "Test metadata", "test-revision", expectedSizeBytes = 1L,
                sha256 = BackupCodec.sha256("a"), downloadUrl = "", license = "Test fixture; no bundled weights",
            )
            source.upsertModel(sourceOnlyModel)
            val backup = source.exportBackupJson()

            destination.initialize()
            destination.setSafDirectoryUri("content://test.documents/tree/destination")
            destination.restoreBackupJson(backup)
            assertEquals(source.recipes.value, destination.recipes.value)
            assertEquals(source.ingredients.value, destination.ingredients.value)
            assertEquals(listOf(observation), destination.sensoryObservations.value)
            assertEquals(messages, destination.chatMessages.value)
            assertEquals(persona, destination.selectedPersona.value)
            assertEquals("18,5", destination.settings.value["recipe.draft"])
            assertEquals("content://test.documents/tree/destination", destination.safDirectoryUri.value)
            assertTrue(destination.models.value.isEmpty())
        } finally {
            source.close()
            destination.close()
            context.deleteDatabase(sourceName)
            context.deleteDatabase(destinationName)
        }
    }
}
