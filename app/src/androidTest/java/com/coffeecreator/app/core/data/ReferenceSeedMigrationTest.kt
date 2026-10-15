package com.coffeecreator.app.core.data

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import com.coffeecreator.app.core.model.CoffeeBean
import com.coffeecreator.app.core.model.Recipe
import java.util.UUID
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ReferenceSeedMigrationTest {
    private val context: Context = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun givenVersionTwoEditsAndDeletion_whenReferenceUpgradeRuns_thenUserRecordsAndDeletionsSurvive() = runBlocking {
        val name = "reference-upgrade-${UUID.randomUUID()}.db"
        val database = CoffeeDatabase.open(context, name)
        val edited = CoffeeBean("reference-arabica-ethiopia", "My edited reference", provenance = "User edit")
        val collision = CoffeeBean("reference-wcr-bourbon", "My existing custom bean", provenance = "User record")
        val recipe = Recipe("my-recipe", "My saved recipe", beanId = edited.id)
        val rows = listOf(
            StoredRecord("meta", "initialized", "2"),
            StoredRecord("bean", edited.id, RecordJson.gson.toJson(edited)),
            StoredRecord("bean", collision.id, RecordJson.gson.toJson(collision)),
            StoredRecord("recipe", recipe.id, RecordJson.gson.toJson(recipe)),
            StoredRecord("setting", "recipe.draft", RecordJson.gson.toJson("My retained draft")),
            StoredRecord("persona", SeedData.personas.first().id, RecordJson.gson.toJson(SeedData.personas.first()))
        )
        val repository = DataRepository(context, database)
        try {
            database.dao().putRecords(rows)
            repository.initialize()
            assertEquals(edited, repository.findBeanById(edited.id))
            assertEquals(collision, repository.findBeanById(collision.id))
            assertEquals(recipe, repository.findRecipeById(recipe.id))
            assertEquals("My retained draft", repository.settings.value["recipe.draft"])
            assertNull(repository.findBeanById("reference-arabica-yemen"))
            assertEquals(33, repository.beans.value.size)
            repository.deleteBean("reference-wcr-caturra")
            repository.close()

            val reopened = DataRepository(context, CoffeeDatabase.open(context, name))
            try {
                reopened.initialize()
                assertNull(reopened.findBeanById("reference-wcr-caturra"))
                assertNull(reopened.findBeanById("reference-arabica-yemen"))
                assertEquals(collision, reopened.findBeanById(collision.id))
                assertEquals(32, reopened.beans.value.size)
            } finally {
                reopened.close()
            }
        } finally {
            repository.close()
            context.deleteDatabase(name)
        }
    }

    @Test
    fun givenDeletedReferenceInBackup_whenRestoredAndReopened_thenExplicitRestoreIsNotUndoneBySeeding() = runBlocking {
        val sourceName = "reference-source-${UUID.randomUUID()}.db"
        val destinationName = "reference-destination-${UUID.randomUUID()}.db"
        val source = DataRepository(context, CoffeeDatabase.open(context, sourceName))
        val destination = DataRepository(context, CoffeeDatabase.open(context, destinationName))
        try {
            source.initialize()
            source.deleteBean("reference-wcr-bourbon")
            val backup = source.exportBackupJson()
            destination.initialize()
            assertTrue(destination.findBeanById("reference-wcr-bourbon") != null)
            destination.restoreBackupJson(backup)
            assertNull(destination.findBeanById("reference-wcr-bourbon"))
            destination.close()
            val reopened = DataRepository(context, CoffeeDatabase.open(context, destinationName))
            try {
                reopened.initialize()
                assertNull(reopened.findBeanById("reference-wcr-bourbon"))
                assertEquals(source.beans.value, reopened.beans.value)
                reopened.deleteApplicationRecords()
            } finally {
                reopened.close()
            }
            val afterDeletion = DataRepository(context, CoffeeDatabase.open(context, destinationName))
            try {
                afterDeletion.initialize()
                assertTrue(afterDeletion.beans.value.isEmpty())
            } finally {
                afterDeletion.close()
            }
        } finally {
            source.close()
            destination.close()
            context.deleteDatabase(sourceName)
            context.deleteDatabase(destinationName)
        }
    }
}
