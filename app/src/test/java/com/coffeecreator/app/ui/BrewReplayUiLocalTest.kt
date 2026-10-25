package com.coffeecreator.app.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.lifecycle.ViewModelProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.coffeecreator.app.CoffeeApplication
import com.coffeecreator.app.MainActivity
import com.coffeecreator.app.core.brew.BrewExecutionStatus
import com.coffeecreator.app.core.journal.BrewJournalEntry
import com.coffeecreator.app.core.model.BrewStep
import com.coffeecreator.app.core.model.Recipe
import com.coffeecreator.app.ui.viewmodel.CoffeeViewModel
import com.coffeecreator.app.ui.viewmodel.saveJournalEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/** Local framework/Compose journeys; physical notification and TalkBack behavior remain device gates. */
@RunWith(AndroidJUnit4::class)
@Config(sdk = [33], qualifiers = "w411dp-h891dp-xhdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class BrewReplayUiLocalTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    @Test
    fun givenChampionRecipe_whenChoosingBrew_thenReadyTimerIsSelectedWithoutStarting() {
        openJournal()
        val previousJournalCount = compose.runOnIdle {
            ViewModelProvider(compose.activity)[CoffeeViewModel::class.java].state.value.journalEntries.size
        }
        compose.onNodeWithTag("page:Journal").performScrollToNode(hasTestTag("journal:nav_champion_recipes"))
        compose.onNodeWithTag("journal:nav_champion_recipes").performClick()
        compose.onNodeWithTag("page:Journal").performScrollToNode(hasTestTag("champion:brew:comp-george-peng-solo"))
        compose.onNodeWithTag("champion:brew:comp-george-peng-solo").performClick()
        compose.onNodeWithTag("brew:section:Timer").assertIsSelected()
        compose.onNodeWithTag("timer:toggle").assertIsDisplayed()
        compose.onNodeWithText("Start Session").assertIsDisplayed()
        compose.runOnIdle {
            val state = ViewModelProvider(compose.activity)[CoffeeViewModel::class.java].state.value
            assertEquals(BrewExecutionStatus.READY_ARMED, state.brewSession?.status)
            assertEquals(state.draft.id, state.brewSession?.recipe?.id)
            assertFalse(state.timerRunning)
            assertEquals(previousJournalCount, state.journalEntries.size)
        }
    }

    @Test
    fun givenSavedV60Recipe_whenTappingRecipe_thenReadyTimerOpensDirectly() {
        val repository = (compose.activity.application as CoffeeApplication).graph.repository
        compose.waitUntil(20_000) { repository.ready.value }
        val recipe = Recipe(
            id = "ui-replay-v60",
            name = "V60 repeatable morning brew",
            doseGrams = 20.0,
            waterGrams = 300.0,
            brewTimeSeconds = 60,
            brewSteps = listOf(
                BrewStep(1, "Bloom", targetWaterGrams = 60.0, durationSeconds = 30, action = "Bloom"),
                BrewStep(2, "Main pour", targetWaterGrams = 240.0, durationSeconds = 30, cumulativeMassGrams = 300.0)
            )
        )
        runBlocking(Dispatchers.IO) { repository.saveRecipe(recipe) }
        openJournal()
        val title = "${recipe.name} · v${recipe.version}"
        compose.onNodeWithTag("page:Journal").performScrollToNode(hasText(title))
        compose.onNodeWithText(title).performClick()
        compose.onNodeWithTag("brew:section:Timer").assertIsSelected()
        compose.onNodeWithTag("timer:toggle").assertIsDisplayed()
        compose.onNodeWithText("Start Session").assertIsDisplayed()
        val saved = repository.recipes.value.single { it.id == recipe.id }
        assertEquals(recipe.id, saved.id)
        assertEquals(recipe.name, saved.name)
        assertEquals(recipe.doseGrams, saved.doseGrams, 0.001)
        assertEquals(recipe.waterGrams, saved.waterGrams, 0.001)
        assertEquals(recipe.brewSteps, saved.brewSteps)
        compose.runOnIdle {
            val state = ViewModelProvider(compose.activity)[CoffeeViewModel::class.java].state.value
            assertEquals(BrewExecutionStatus.READY_ARMED, state.brewSession?.status)
            assertEquals(recipe.id, state.brewSession?.recipe?.id)
            assertFalse(state.timerRunning)
        }
    }

    @Test
    fun givenManualEspressoRecipe_whenReplayed_thenMachineIsNamedAndNoBleControlBlocksTheTimer() {
        val repository = (compose.activity.application as CoffeeApplication).graph.repository
        compose.waitUntil(20_000) { repository.ready.value }
        val recipe = Recipe(
            id = "ui-manual-espresso",
            name = "Manual Appia shot",
            brewMethod = "Espresso",
            equipment = "Nuova Simonelli Appia",
            doseGrams = 18.0,
            waterGrams = 36.0,
            brewTimeSeconds = 30
        )
        runBlocking(Dispatchers.IO) { repository.saveRecipe(recipe) }
        openJournal()
        compose.onNodeWithTag("page:Journal").performScrollToNode(hasText("${recipe.name} · v${recipe.version}"))
        compose.onNodeWithText("${recipe.name} · v${recipe.version}").performClick()
        compose.onNodeWithTag("brew:section:Timer").assertIsSelected()
        compose.onNodeWithText("Manual espresso timer").assertIsDisplayed()
        compose.onNodeWithText("Nuova Simonelli Appia").assertIsDisplayed()
        compose.onNodeWithTag("timer:toggle").assertIsDisplayed()
        compose.onNodeWithTag("timer:connect_scale").assertDoesNotExist()
        compose.onNodeWithTag("timer:scale_card").assertDoesNotExist()
        compose.onNodeWithTag("timer:manual_weight_value").assertDoesNotExist()
        compose.runOnIdle {
            val state = ViewModelProvider(compose.activity)[CoffeeViewModel::class.java].state.value
            assertEquals(null, state.brewSession?.scaleDevice)
            assertFalse(state.timerRunning)
        }
    }

    @Test
    fun givenLegacyJournalWithoutSavedRecipe_whenTappingBrew_thenManualTimerOpensAndOriginalMeasurementsRemainHistorical() {
        val model = ViewModelProvider(compose.activity)[CoffeeViewModel::class.java]
        compose.waitUntil(20_000) { model.state.value.ready }
        val historical = BrewJournalEntry(
            id = "ui-legacy-journal",
            beanName = "Recorded morning coffee",
            brewMethod = "Pour Over (V60)",
            doseGrams = 18.0,
            waterGrams = 300.0,
            brewDurationSeconds = 180,
            beverageMassGrams = 270.0,
            measuredTdsPercent = 1.3
        )
        compose.runOnUiThread { model.saveJournalEntry(historical) }
        openJournal()
        compose.onNodeWithTag("page:Journal").performScrollToNode(hasTestTag("journal:repeat:${historical.id}"))
        compose.onNodeWithTag("journal:repeat:${historical.id}").performClick()
        compose.onNodeWithTag("brew:section:Timer").assertIsSelected()
        compose.runOnIdle {
            val state = model.state.value
            assertEquals(historical, state.journalEntries.single { it.id == historical.id })
            assertEquals(null, state.brewSession?.recipe?.beverageMassGrams)
            assertEquals(null, state.brewSession?.recipe?.measuredTdsPercent)
            assertEquals("18.0", state.draft.fields["dose"])
            assertEquals("300.0", state.draft.fields["water"])
        }
    }

    private fun openJournal() {
        val repository = (compose.activity.application as CoffeeApplication).graph.repository
        compose.waitUntil(20_000) { repository.ready.value }
        compose.onNodeWithTag("nav:Journal").performClick()
        compose.onNodeWithTag("page:Journal").assertIsDisplayed()
    }
}
