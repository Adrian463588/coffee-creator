package com.coffeecreator.app.ui

import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Looper
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextReplacement
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.coffeecreator.app.CoffeeApplication
import com.coffeecreator.app.MainActivity
import com.coffeecreator.app.core.data.BackupCodec
import com.coffeecreator.app.core.filesystem.BuiltInAssets
import com.coffeecreator.app.ui.viewmodel.CoffeeViewModel
import com.coffeecreator.app.ui.viewmodel.RecipeDraft
import com.google.gson.Gson
import java.io.File
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/** Local Android framework/Compose execution; does not prove physical-device behavior. */
@RunWith(AndroidJUnit4::class)
@Config(sdk = [33], qualifiers = "w411dp-h891dp-xhdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class BrewLocalTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    @Test
    fun givenRealLocalActivity_whenOpeningManualWorkbench_thenRenderHomeAndBrewWithoutModel() {
        compose.onNodeWithTag("page:Home").assertIsDisplayed()
        capture("home")
        compose.onNodeWithTag("nav:Formulate").performClick()
        compose.onNodeWithTag("page:Formulate").assertIsDisplayed()
        capture("formulation")
    }

    @Test
    fun givenManualDraft_whenSwitchingSectionsAndRecreating_thenValuesAndSelectedSectionSurvive() {
        val repository = (compose.activity.application as CoffeeApplication).graph.repository
        compose.waitUntil(20_000) { repository.ready.value }
        compose.onNodeWithTag("nav:Formulate").performClick()
        field("name", "Local manual brew")
        field("dose", "18")
        select("Measurements")
        compose.onNodeWithTag("form:dose").assertDoesNotExist()
        field("beverage", "270")
        field("tds", "1.30")
        select("Ingredients")
        compose.onNodeWithTag("form:tds").assertDoesNotExist()
        select("Tools")
        compose.onNodeWithTag("brew:section:Tools").assertIsSelected()
        select("Measurements")
        compose.activityRule.scenario.recreate()
        compose.onNodeWithTag("brew:section:Measurements").assertIsSelected()
        compose.onNodeWithTag("form:save").assertIsDisplayed().performClick()
        compose.waitUntil(20_000) { repository.recipes.value.any { it.name == "Local manual brew" } }
        val recipe = repository.recipes.value.first { it.name == "Local manual brew" }
        assertEquals(18.0, recipe.doseGrams, 0.0)
        assertEquals(270.0, recipe.beverageMassGrams ?: -1.0, 0.0)
        assertEquals(1.3, recipe.measuredTdsPercent ?: -1.0, 0.0)
        capture("measurements")
        compose.onNodeWithTag("nav:Library").performClick()
        compose.onNodeWithTag("page:Library").assertIsDisplayed()
        capture("library")
        compose.onNodeWithTag("nav:more").performClick()
        compose.onNodeWithTag("nav:Models").performClick()
        compose.onNodeWithTag("page:Models").assertIsDisplayed()
        capture("models")
    }

    private fun select(section: String) {
        try {
            compose.onNodeWithTag("page:Formulate").performScrollToIndex(0)
        } catch (_: Throwable) {}
        val node = compose.onNodeWithTag("brew:section:$section")
        try {
            node.performScrollTo()
        } catch (_: Throwable) {}
        node.performClick()
        compose.onNodeWithTag("brew:content:$section").assertIsDisplayed()
    }

    @Test
    fun givenUnsavedDraft_whenNewRecipeThenUndo_thenOriginalFieldsSurviveRecreation() {
        compose.onNodeWithTag("nav:Formulate").performClick()
        field("name", "Keep my draft")
        field("dose", "22")
        compose.onNodeWithTag("page:Formulate").performScrollToNode(hasTestTag("form:new"))
        compose.onNodeWithTag("form:new").performClick()
        compose.onNodeWithTag("form:new").performClick()
        compose.activityRule.scenario.recreate()
        compose.onNodeWithText("Undo draft change").assertIsDisplayed().performClick()
        compose.onNodeWithTag("page:Formulate").performScrollToNode(hasTestTag("form:name"))
        compose.onNodeWithTag("form:name").assertTextContains("Keep my draft")
        compose.onNodeWithTag("page:Formulate").performScrollToNode(hasTestTag("form:dose"))
        compose.onNodeWithTag("form:dose").assertTextContains("22")
        compose.onNodeWithText("Undo draft change").assertDoesNotExist()
    }

    @Test
    fun givenBackupDraft_whenRestoringOverActiveDraft_thenFlushAndRecreateKeepRestoredDraft() {
        val app = compose.activity.application as CoffeeApplication
        val repository = app.graph.repository
        compose.waitUntil(20_000) { repository.ready.value }
        compose.onNodeWithTag("nav:Formulate").performClick()
        field("name", "Backup draft")
        val model = ViewModelProvider(compose.activity)[CoffeeViewModel::class.java]
        val file = File(compose.activity.cacheDir, "owned-records-backup.json")
        awaitMain { model.state.value.ready && !model.state.value.busy }
        compose.runOnUiThread { model.exportBackup(Uri.fromFile(file), compose.activity.contentResolver) }
        awaitMain { !model.state.value.busy && file.exists() }
        assertEquals(null, model.state.value.error)
        val json = file.readText()
        assertEquals(
            "Backup draft",
            Gson().fromJson(BackupCodec.decode(json).payload.settings["draft"], RecipeDraft::class.java).fields["name"]
        )
        field("name", "Stale active draft")
        compose.runOnUiThread {
            model.newRecipe()
            model.flushDraft()
        }
        // Exercise the same record/draft restore used by the SAF action. Android
        // ObjectBox/JNI cache invalidation remains a separate device-only gate.
        val restore = compose.runOnUiThread { model.viewModelScope.async { model.restoreRecords(json) } }
        awaitMain { restore.isCompleted }
        runBlocking { restore.await() }
        compose.runOnUiThread { model.flushDraft() }
        runBlocking { app.awaitBackgroundWrites() }
        assertEquals("Backup draft", Gson().fromJson(repository.settings.value["draft"], RecipeDraft::class.java).fields["name"])
        compose.activityRule.scenario.recreate()
        compose.onNodeWithText("Undo draft change").assertDoesNotExist()
        compose.onNodeWithTag("page:Formulate").performScrollToNode(hasTestTag("form:name"))
        compose.onNodeWithTag("form:name").assertTextContains("Backup draft")
        check(file.delete())
    }

    @Test
    fun givenInvalidDraftInsideValidBackup_whenRestoring_thenExistingRecordsAndDraftRemain() {
        val app = compose.activity.application as CoffeeApplication
        val model = ViewModelProvider(compose.activity)[CoffeeViewModel::class.java]
        awaitMain { model.state.value.ready && !model.state.value.busy }
        compose.onNodeWithTag("nav:Formulate").performClick()
        field("name", "Retain active draft")
        val before = app.graph.repository.beans.value
        val invalid = runBlocking {
            val payload = BackupCodec.decode(app.graph.repository.exportBackupJson()).payload
            BackupCodec.encode(payload.copy(settings = payload.settings + ("draft" to "{\"fields\":{}}")))
        }
        assertTrue(runBlocking { runCatching { model.restoreRecords(invalid) }.isFailure })
        assertEquals(before, app.graph.repository.beans.value)
        compose.onNodeWithTag("form:name").assertTextContains("Retain active draft")
    }

    private fun awaitMain(condition: () -> Boolean) {
        compose.waitUntil(20_000) {
            shadowOf(Looper.getMainLooper()).idle()
            compose.mainClock.advanceTimeByFrame()
            condition()
        }
    }

    @Test
    fun givenRecordRestoreInFlight_whenSendingChat_thenExistingChatCannotBeOverwritten() {
        val model = ViewModelProvider(compose.activity)[CoffeeViewModel::class.java]
        awaitMain { model.state.value.ready && !model.state.value.busy }
        val before = model.state.value.messages
        val invalidFile = File(compose.activity.cacheDir, "owned-invalid-backup.json").also { it.writeText("{}") }
        compose.runOnUiThread {
            model.restoreBackup(Uri.fromFile(invalidFile), compose.activity.contentResolver)
            assertTrue(model.state.value.busy)
            model.sendChat("This must wait until the records operation finishes.")
        }
        awaitMain { !model.state.value.busy && !model.state.value.generating }
        assertEquals(before, model.state.value.messages)
        check(invalidFile.delete())
    }

    @Test
    fun givenVerifiedCatalogWithoutSafGrant_whenOpeningModels_thenDownloadsRemainDisabled() {
        val repository = (compose.activity.application as CoffeeApplication).graph.repository
        compose.waitUntil(20_000) { repository.models.value.size == 3 }
        compose.onNodeWithTag("nav:more").performClick()
        compose.onNodeWithTag("nav:Models").performClick()
        BuiltInAssets.catalog.forEach { asset ->
            compose.onNodeWithTag("page:Models").performScrollToNode(hasTestTag("models:download:${asset.id}"))
            compose.onNodeWithTag("models:download:${asset.id}").performScrollTo().assertIsNotEnabled()
        }
        assertEquals(null, repository.safDirectoryUri.value)
    }

    private fun field(key: String, value: String) {
        compose.onNodeWithTag("page:Formulate").performScrollToNode(hasTestTag("form:$key"))
        compose.onNodeWithTag("form:$key").performScrollTo().performTextReplacement(value)
    }

    private fun capture(name: String) {
        val output = File(checkNotNull(System.getProperty("coffee.screenshotDir")), "$name.png")
        check(output.parentFile?.isDirectory == true || output.parentFile?.mkdirs() == true)
        compose.waitForIdle()
        compose.runOnUiThread {
            val view = compose.activity.window.decorView
            check(view.width > 0 && view.height > 0)
            val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
            view.draw(Canvas(bitmap))
            output.outputStream().use { stream ->
                check(bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream))
            }
            bitmap.recycle()
        }
    }
}
