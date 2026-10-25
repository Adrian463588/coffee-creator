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
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.coffeecreator.app.CoffeeApplication
import com.coffeecreator.app.MainActivity
import com.coffeecreator.app.core.model.BrewStep
import com.coffeecreator.app.core.model.Recipe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
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
        compose.onNodeWithTag("page:Journal").performScrollToNode(hasTestTag("journal:nav_champion_recipes"))
        compose.onNodeWithTag("journal:nav_champion_recipes").performClick()
        compose.onNodeWithTag("page:Journal").performScrollToNode(hasTestTag("champion:brew:comp-george-peng-solo"))
        compose.onNodeWithTag("champion:brew:comp-george-peng-solo").performClick()
        compose.onNodeWithTag("brew:section:Timer").assertIsSelected()
        compose.onNodeWithTag("timer:toggle").assertIsDisplayed()
        compose.onNodeWithText("Start Session").assertIsDisplayed()
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
                BrewStep(1, description = "Bloom", targetWaterGrams = 60.0, durationSeconds = 30, action = "Bloom"),
                BrewStep(2, description = "Main pour", targetWaterGrams = 240.0, durationSeconds = 30, cumulativeMassGrams = 300.0)
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
    }

    private fun openJournal() {
        val repository = (compose.activity.application as CoffeeApplication).graph.repository
        compose.waitUntil(20_000) { repository.ready.value }
        compose.onNodeWithTag("nav:Journal").performClick()
        compose.onNodeWithTag("page:Journal").assertIsDisplayed()
    }
}
