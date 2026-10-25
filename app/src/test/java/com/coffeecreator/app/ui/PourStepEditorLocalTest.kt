package com.coffeecreator.app.ui

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.coffeecreator.app.core.model.BrewStep
import com.coffeecreator.app.ui.screens.PourStepCard
import com.coffeecreator.app.ui.theme.CoffeeCreatorTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(AndroidJUnit4::class)
@Config(sdk = [33], qualifiers = "w320dp-h891dp-xhdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class PourStepEditorLocalTest {
    @get:Rule val compose = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun givenPourPlan_whenOpeningStep_thenOnlyChosenEditorIsShownAndCanCollapse() {
        val expanded = mutableStateOf(false)
        compose.setContent {
            CoffeeCreatorTheme(dynamicColor = false) {
                Surface(color = MaterialTheme.colorScheme.background) {
                    Column(Modifier.verticalScroll(rememberScrollState()).padding(16.dp)) {
                        PourStepCard(step(), 0, expanded.value, { expanded.value = !expanded.value }, {}, {}, {})
                    }
                }
            }
        }
        compose.onNodeWithTag("step:water:0").assertDoesNotExist()
        compose.onNodeWithTag("step:edit:0").assertHeightIsAtLeast(48.dp).performClick()
        compose.onNodeWithTag("step:water:0").performScrollTo().assertIsDisplayed()
        compose.onNodeWithTag("step:edit:0").performScrollTo().performClick()
        compose.onNodeWithTag("step:water:0").assertDoesNotExist()
    }

    @Test
    fun givenStepWaterInput_whenClearedAndCorrected_thenInvalidTextIsRetainedAndDecimalCommaRecovers() {
        val saved = mutableStateOf(step())
        var valid = true
        compose.setContent {
            CoffeeCreatorTheme(dynamicColor = false) {
                Column(Modifier.verticalScroll(rememberScrollState()).padding(16.dp)) {
                    PourStepCard(saved.value, 0, true, {}, { saved.value = it }, {}, { valid = it })
                }
            }
        }
        compose.onNodeWithTag("step:water:0").performScrollTo().performTextReplacement("")
        compose.onNodeWithText("Enter zero or a positive mass in grams.").assertIsDisplayed()
        compose.runOnIdle {
            assertFalse(valid)
            assertEquals(60.0, saved.value.targetWaterGrams, 0.0)
        }
        compose.onNodeWithTag("step:water:0").performTextReplacement("60,5")
        compose.onNodeWithTag("step:water:0").assertTextContains("60,5")
        compose.runOnIdle {
            assertTrue(valid)
            assertEquals(60.5, saved.value.targetWaterGrams, 0.0)
        }
    }

    private fun step() = BrewStep(
        stepNumber = 1,
        description = "Wet the grounds evenly.",
        targetWaterGrams = 60.0,
        durationSeconds = 45,
        action = "Bloom"
    )
}
