package com.coffeecreator.app.ui

import android.content.pm.ActivityInfo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextReplacement
import com.coffeecreator.app.CoffeeApplication
import com.coffeecreator.app.MainActivity
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class ManualJourneyTest {
    @get:Rule val rule = createAndroidComposeRule<MainActivity>()
    private val repository get() = (rule.activity.application as CoffeeApplication).graph.repository

    @Test
    fun givenNoModel_whenGoldenRecipeSavedAndEdited_thenMeasurementsAndVersionsRemainExact() {
        waitReady()
        val name = "QA golden ${UUID.randomUUID()}"
        val previousDraft = repository.settings.value["draft"]
        try {
            rule.onNodeWithTag("home:new").performClick()
            field("name", name)
            field("dose", "18")
            field("water", "300")
            field("beverage", "270")
            field("tds", "1.30")
            rule.onNodeWithTag("form:save").performClick()
            rule.waitUntil(10_000) { repository.recipes.value.any { it.name == name } }
            val first = repository.recipes.value.first { it.name == name }
            assertEquals(1.3, first.measuredTdsPercent ?: -1.0, 0.0)
            assertEquals(270.0, first.beverageMassGrams ?: -1.0, 0.0)
            rule.onNodeWithTag("nav:Journal").performClick()
            rule.onNodeWithTag("page:Journal").performScrollToNode(hasTestTag("journal:edit:${first.id}"))
            rule.onNodeWithTag("journal:edit:${first.id}").performClick()
            field("water", "275")
            rule.onNodeWithTag("form:save").performClick()
            rule.waitUntil(10_000) { repository.recipes.value.count { it.revisionGroupId == first.revisionGroupId } == 2 }
            val revisions = repository.recipes.value.filter { it.revisionGroupId == first.revisionGroupId }.sortedBy { it.version }
            assertEquals(300.0, revisions[0].waterGrams, 0.0)
            assertEquals(275.0, revisions[1].waterGrams, 0.0)
            assertEquals(first, revisions[0])
        } finally {
            restoreAfterScenario(previousDraft, name)
        }
    }

    @Test
    fun givenDraftWithAbsentMeasurements_whenActivityRecreates_thenInputSurvivesAndTdsStaysAbsent() {
        waitReady()
        val previousDraft = repository.settings.value["draft"]
        val title = "QA rotation ${UUID.randomUUID()}"
        try {
            rule.onNodeWithTag("home:new").performClick()
            field("name", title)
            field("water", "271")
            rule.activityRule.scenario.recreate()
            rule.waitUntil(10_000) { repository.ready.value }
            rule.onNodeWithTag("nav:Formulate").performClick()
            rule.selectBrewSection("Recipe")
            rule.onNodeWithTag("page:Formulate").performScrollToNode(hasTestTag("form:name"))
            rule.onNodeWithText(title).assertIsDisplayed()
            field("beverage", "")
            field("tds", "")
            rule.onNodeWithTag("form:save").performClick()
            rule.waitUntil(10_000) { repository.recipes.value.any { it.name == title } }
            val recipe = repository.recipes.value.first { it.name == title }
            assertEquals(null, recipe.measuredTdsPercent)
            assertEquals(null, recipe.beverageMassGrams)
        } finally {
            restoreAfterScenario(previousDraft, title)
        }
    }

    @Test
    fun givenInvalidDose_whenSaveRequested_thenRejectedWithoutDataWriteOrCrash() {
        waitReady()
        val title = "QA invalid ${UUID.randomUUID()}"
        val previousDraft = repository.settings.value["draft"]
        try {
            rule.onNodeWithTag("home:new").performClick()
            field("name", title)
            field("dose", "NaN")
            rule.onNodeWithTag("form:save").performClick()
            rule.onNodeWithText("Enter a decimal number without grouping separators.").assertIsDisplayed()
            assertTrue(repository.recipes.value.none { it.name == title })
        } finally {
            restoreAfterScenario(previousDraft, title)
        }
    }

    @Test
    fun givenCompactOrLandscapeLayout_whenNavigating_thenNineDestinationsRemainReachable() {
        waitReady()
        for (orientation in listOf(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)) {
            rule.activity.requestedOrientation = orientation
            rule.waitForIdle()
            listOf("Home", "Formulate", "AI Chat", "Library", "Journal").forEach { route ->
                rule.onNodeWithTag("nav:$route").assertIsEnabled().performClick()
                rule.onNodeWithTag("page:$route").assertIsDisplayed()
            }
            listOf("Knowledge", "Models", "Sensory", "Settings").forEach { route ->
                rule.onNodeWithTag("nav:more").performClick()
                rule.onNodeWithTag("nav:$route").performClick()
                rule.onNodeWithTag("page:$route").assertIsDisplayed()
            }
        }
        rule.activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }

    private fun waitReady() {
        rule.waitUntil(15_000) { repository.ready.value }
        rule.onNodeWithTag("nav:Home").performClick()
        rule.onNodeWithTag("home:new").assertIsEnabled()
    }

    private fun restoreAfterScenario(previousDraft: String?, ownedName: String) {
        val application = rule.activity.application as CoffeeApplication
        val localRepository = application.graph.repository
        rule.activityRule.scenario.close()
        runBlocking(Dispatchers.IO) {
            application.awaitBackgroundWrites()
            localRepository.recipes.value.filter { it.name == ownedName }.forEach { localRepository.deleteRecipe(it.id) }
            localRepository.saveSetting("draft", previousDraft.orEmpty())
        }
    }

    private fun field(key: String, value: String) {
        val section = if (key in setOf("beverage", "tds", "temperature", "pressure", "seconds", "bypass", "agitation")) {
            "Measurements"
        } else {
            "Recipe"
        }
        rule.selectBrewSection(section)
        rule.onNodeWithTag("page:Formulate").performScrollToNode(hasTestTag("form:$key"))
        rule.onNodeWithTag("form:$key").performScrollTo().performTextReplacement(value)
    }
}
