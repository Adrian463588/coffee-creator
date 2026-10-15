package com.coffeecreator.app.core.filesystem

import android.net.Uri
import androidx.core.content.FileProvider
import androidx.documentfile.provider.DocumentFile
import androidx.test.platform.app.InstrumentationRegistry
import com.coffeecreator.app.core.data.CoffeeDatabase
import com.coffeecreator.app.core.data.DataRepository
import com.coffeecreator.app.core.model.ModelStatus
import com.coffeecreator.app.core.model.Recipe
import java.io.File
import java.util.UUID
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** Requires an actual system-picker grant to an empty dedicated CoffeeCreatorQA folder. */
class SafAssetDeviceTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val treeUri = Uri.parse(
        InstrumentationRegistry.getArguments().getString("qaTree")
            ?: "content://com.android.externalstorage.documents/tree/primary%3ADownload%2FCoffeeCreatorQA-20261015-00"
    )

    @Test fun givenRealGgufAndGrantedSaf_whenPublishedThenFreshRegistryRescans_thenSizeAndHashAreVerified() = runBlocking<Unit> {
        ownedScope { directory, repository, manager ->
            val source = File(context.filesDir, "native-test/qwen2.5-0.5b-instruct-q4_k_m.gguf")
            assertTrue(source.isFile)
            val input = source.copyTo(File(directory, "qwen.gguf"))
            val asset = BuiltInAssets.qwen.copy(id = "qa-qwen-${directory.name}")
            manager.selectDirectory(treeUri)
            repository.upsertModel(asset)
            manager.importModel(asset, FileProvider.getUriForFile(context, "com.coffeecreator.app.qa.files", input))
            val installed = repository.models.value.single { it.id == asset.id }
            assertEquals(ModelStatus.INSTALLED, installed.status)
            assertEquals(asset.expectedSizeBytes, installed.downloadedBytes)
            assertTrue(installed.localUri?.startsWith("content://") == true)
            val freshName = "saf-fresh-${directory.name}.db"
            val fresh = DataRepository(context, CoffeeDatabase.open(context, freshName))
            try {
                fresh.initialize()
                val recovery = AssetManager(context, fresh)
                assertTrue(recovery.selectDirectory(treeUri) >= 1)
                assertEquals(installed.sha256, fresh.models.value.single { it.id == asset.id }.sha256)
                assertEquals(ModelStatus.INSTALLED, fresh.models.value.single { it.id == asset.id }.status)
                val working = recovery.stageForInference(fresh.models.value.single { it.id == asset.id })
                assertEquals(asset.sha256, AssetManager.sha256(working))
                assertTrue(working.delete())
            } finally {
                fresh.close()
                context.deleteDatabase(freshName)
            }
        }
    }

    @Test fun givenWrongChecksum_whenImportAttempted_thenNeverInstalledAndNoPartialBundlePublished() = runBlocking<Unit> {
        ownedScope { directory, repository, manager ->
            manager.selectDirectory(treeUri)
            val file = File(directory, "corrupt.gguf").apply { writeText("GGUFbad!") }
            val asset = BuiltInAssets.qwen.copy(
                id = "qa-corrupt-${directory.name}",
                expectedSizeBytes = file.length(),
                sha256 = "0".repeat(64)
            )
            repository.upsertModel(asset)
            assertTrue(
                runCatching {
                    manager.importModel(asset, FileProvider.getUriForFile(context, "com.coffeecreator.app.qa.files", file))
                }.isFailure
            )
            assertEquals(ModelStatus.FAILED, repository.models.value.single { it.id == asset.id }.status)
            assertFalse(repository.models.value.any { it.id == asset.id && it.status == ModelStatus.INSTALLED })
        }
    }

    @Test fun givenCompleteRecords_whenExportedThroughSafAndRestored_thenRecordsAndAbsentMeasurementsSurvive() = runBlocking<Unit> {
        ownedScope { _, repository, manager ->
            manager.selectDirectory(treeUri)
            val recipe = repository.saveRecipe(Recipe("", "SAF recovery test"))
            val tree = DocumentFile.fromTreeUri(context, treeUri) ?: error("Missing tree")
            val export = tree.createFile("application/json", "qa-${UUID.randomUUID()}.json") ?: error("Cannot create SAF export")
            context.contentResolver.openOutputStream(export.uri, "wt")!!.bufferedWriter().use { it.write(repository.exportBackupJson()) }
            val read = context.contentResolver.openInputStream(export.uri)!!.bufferedReader().use { it.readText() }
            val freshName = "saf-backup-${UUID.randomUUID()}.db"
            val fresh = DataRepository(context, CoffeeDatabase.open(context, freshName))
            try {
                fresh.initialize()
                fresh.restoreBackupJson(read)
                assertEquals(recipe, fresh.recipes.value.single())
                assertEquals(null, fresh.recipes.value.single().measuredTdsPercent)
            } finally {
                fresh.close()
                context.deleteDatabase(freshName)
            }
        }
    }

    private suspend fun ownedScope(block: suspend (File, DataRepository, AssetManager) -> Unit) {
        require(treeUri.toString().contains("CoffeeCreatorQA-20261015-00")) { "Use the explicitly owned QA tree only." }
        val tree = DocumentFile.fromTreeUri(context, treeUri) ?: error("Choose the QA tree in Models before tests")
        val before = tree.listFiles().map { it.uri }.toSet()
        val id = UUID.randomUUID().toString()
        val directory = File(context.filesDir, "qa-sources/$id").apply { mkdirs() }
        val databaseName = "saf-$id.db"
        val repository = DataRepository(context, CoffeeDatabase.open(context, databaseName))
        try {
            repository.initialize()
            block(directory, repository, AssetManager(context, repository))
        } finally {
            repository.close()
            context.deleteDatabase(databaseName)
            tree.listFiles().filter { it.uri !in before }.forEach { check(it.delete()) }
            check(directory.canonicalPath.startsWith(File(context.filesDir, "qa-sources").canonicalPath + File.separator))
            directory.deleteRecursively()
            File(context.filesDir, "asset-staging").listFiles().orEmpty().filter {
                it.name.startsWith("qa-qwen-$id-") || it.name.startsWith("qa-corrupt-$id-")
            }.forEach { it.delete() }
        }
    }
}
