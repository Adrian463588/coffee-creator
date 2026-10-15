package com.coffeecreator.app.ui

import androidx.compose.ui.test.assertDoesNotExist
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextReplacement
import com.coffeecreator.app.CoffeeApplication
import com.coffeecreator.app.MainActivity
import org.junit.Rule
import org.junit.Test

class BrewSectionTest {
    @get:Rule val rule = createAndroidComposeRule<MainActivity>()

    @Test
    fun givenBrew_whenSelectingChips_thenOnlySelectedGroupAndSaveRemainReachable() {
        openBrew()
        listOf("Recipe", "Measurements", "Ingredients", "Tools").forEach { selected ->
            rule.selectBrewSection(selected)
            rule.onNodeWithTag("brew:section:$selected").assertIsSelected()
            rule.onNodeWithTag("brew:content:$selected").assertIsDisplayed()
            listOf("Recipe", "Measurements", "Ingredients", "Tools").filter { it != selected }.forEach { hidden ->
                rule.onNodeWithTag("brew:content:$hidden").assertDoesNotExist()
            }
            rule.onNodeWithTag("form:save").assertIsDisplayed()
            rule.onNodeWithTag("form:ratio").assertIsDisplayed()
        }
    }

    @Test
    fun givenToolInput_whenSwitchingSectionsAndRecreating_thenValueAndSelectionSurvive() {
        openBrew()
        rule.selectBrewSection("Tools")
        rule.onNodeWithTag("page:Formulate").performScrollToNode(hasTestTag("science:initialMassGrams"))
        rule.onNodeWithTag("science:initialMassGrams").performScrollTo().performTextReplacement("246")
        rule.selectBrewSection("Recipe")
        rule.onNodeWithTag("science:initialMassGrams").assertDoesNotExist()
        rule.selectBrewSection("Tools")
        rule.onNodeWithTag("page:Formulate").performScrollToNode(hasTestTag("science:initialMassGrams"))
        rule.onNodeWithTag("science:initialMassGrams").assertTextContains("246")
        rule.activityRule.scenario.recreate()
        rule.onNodeWithTag("page:Formulate").performScrollToIndex(0)
        rule.onNodeWithTag("brew:section:Tools").assertIsSelected()
        rule.onNodeWithTag("page:Formulate").performScrollToNode(hasTestTag("science:initialMassGrams"))
        rule.onNodeWithTag("science:initialMassGrams").assertTextContains("246")
    }

    @Test
    fun givenBrew_whenUsingSystemBack_thenNavigationReturnsHome() {
        openBrew()
        rule.runOnUiThread { rule.activity.onBackPressedDispatcher.onBackPressed() }
        rule.onNodeWithTag("page:Home").assertIsDisplayed()
    }

    private fun openBrew() {
        rule.waitUntil(15_000) { (rule.activity.application as CoffeeApplication).graph.repository.ready.value }
        rule.onNodeWithTag("nav:Home").performClick()
        rule.onNodeWithTag("nav:Formulate").performClick()
    }
}

internal fun ComposeContentTestRule.selectBrewSection(section: String) {
    onNodeWithTag("page:Formulate").performScrollToIndex(0)
    val chip = hasTestTag("brew:section:$section")
    if (onAllNodes(chip and hasAnyAncestor(hasScrollAction())).fetchSemanticsNodes().isNotEmpty()) {
        onNode(chip).performScrollTo()
    }
    onNode(chip).performClick()
}
