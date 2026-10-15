package com.coffeecreator.app.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToNode
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.coffeecreator.app.MainActivity
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
        compose.onNodeWithTag("nav:Formulate").performClick()
        compose.onNodeWithTag("page:Formulate").assertIsDisplayed()
            .performScrollToNode(hasTestTag("brew:section:Tools"))
        compose.onNodeWithTag("brew:section:Tools").performScrollTo().performClick().assertIsSelected()
        compose.onNodeWithTag("brew:content:Tools").assertIsDisplayed()
        compose.onNodeWithTag("form:save").assertIsDisplayed()
    }
}
