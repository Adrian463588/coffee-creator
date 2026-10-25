package com.coffeecreator.app.ui

import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.coffeecreator.app.CoffeeApplication
import com.coffeecreator.app.MainActivity
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(AndroidJUnit4::class)
@Config(sdk = [33], qualifiers = "w360dp-h480dp-xhdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class CompactBrewLocalTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    @Test
    fun givenShortViewportAndLargeText_whenSelectingLastChip_thenBodyAndSaveRemainReachable() {
        RuntimeEnvironment.setFontScale(2f)
        compose.activityRule.scenario.recreate()
        val repository = (compose.activity.application as CoffeeApplication).graph.repository
        compose.waitUntil(20_000) { repository.ready.value }
        compose.onNodeWithTag("nav:Formulate").performClick()
        compose.waitUntil(5_000) {
            compose.onAllNodesWithTag("page:Formulate").fetchSemanticsNodes().isNotEmpty()
        }
        compose.onNodeWithTag("page:Formulate").assertExists()
        compose.onNodeWithTag("brew:section:Tools").performScrollTo().performClick().assertIsSelected()
        compose.onNodeWithTag("brew:content:Tools").assertExists()
        compose.onNodeWithTag("form:save").assertExists()
    }

    @After
    fun tearDown() {
        RuntimeEnvironment.setFontScale(1f)
    }
}
