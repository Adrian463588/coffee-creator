package com.coffeecreator.app.ui

import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.coffeecreator.app.core.recipes.champion.WorldChampionRecipeCatalog
import com.coffeecreator.app.ui.screens.ChampionRecipeCard
import com.coffeecreator.app.ui.screens.ChampionRecipeFacts
import com.coffeecreator.app.ui.theme.CoffeeCreatorTheme
import java.io.File
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/** Host-rendered Compose evidence; not a physical-device screenshot or TalkBack acceptance. */
@RunWith(AndroidJUnit4::class)
@Config(sdk = [33], qualifiers = "w320dp-h891dp-xhdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class ChampionRecipeLayoutLocalTest {
    @get:Rule val compose = createAndroidComposeRule<ComponentActivity>()
    private val recipe = WorldChampionRecipeCatalog.entries.first { it.id == "comp-george-stanica-aeropress" }

    @Test
    fun givenLongBypassRatio_whenCompactLight_thenEveryFactFitsWithoutOverlap() {
        renderFacts(dark = false, fontScale = 1f)
        assertFactsFit()
        capture("champion-facts-compact-light")
    }

    @Test
    fun givenLongBypassRatio_whenCompactDarkAndLargeFont_thenEveryFactFitsWithoutOverlap() {
        renderFacts(dark = true, fontScale = 2f)
        assertFactsFit()
        capture("champion-facts-compact-dark-font200")
    }

    @Test
    fun givenChampionCard_whenCompactDark_thenBrewAndDetailsHaveAccessibleTargets() {
        compose.setContent {
            CoffeeCreatorTheme(darkTheme = true, dynamicColor = false) {
                Surface(color = MaterialTheme.colorScheme.background) {
                    Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(16.dp)) {
                        ChampionRecipeCard(recipe, false, {}, {}, {}, {})
                    }
                }
            }
        }
        capture("champion-card-compact-dark-top")
        compose.onNodeWithTag("champion:brew:${recipe.id}").performScrollTo().assertHeightIsAtLeast(48.dp)
        compose.onNodeWithTag("champion:save:${recipe.id}").assertHeightIsAtLeast(48.dp)
        compose.onNodeWithTag("champion:steps:${recipe.id}").performScrollTo().assertHeightIsAtLeast(48.dp)
        capture("champion-card-compact-dark-actions")
    }

    private fun renderFacts(dark: Boolean, fontScale: Float) {
        RuntimeEnvironment.setFontScale(fontScale)
        compose.setContent {
            CoffeeCreatorTheme(darkTheme = dark, dynamicColor = false) {
                Surface(color = MaterialTheme.colorScheme.background) {
                    Column(Modifier.fillMaxWidth().padding(16.dp)) { ChampionRecipeFacts(recipe) }
                }
            }
        }
    }

    private fun assertFactsFit() {
        val factValues = listOf("18.0 g", "100.0 g", "96.0 °C", "02:15", recipe.ratio)
        val container = compose.onNodeWithTag("champion:facts:${recipe.id}").fetchSemanticsNode().boundsInRoot
        val bounds = factValues.map { value ->
            val node = compose.onNodeWithText(value)
            val layouts = mutableListOf<TextLayoutResult>()
            node.performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
            assertEquals("Text must expose one layout: $value", 1, layouts.size)
            val layout = layouts.single()
            assertFalse("Text must not overflow height: $value", layout.didOverflowHeight)
            assertFalse("Text must not be ellipsized: $value", layout.isLineEllipsized(0))
            node.fetchSemanticsNode().boundsInRoot.also {
                assertTrue("Fact must have positive width: $value", it.width > 0f)
                assertTrue("Fact stays inside content width: $value", it.left >= container.left && it.right <= container.right)
            }
        }
        bounds.forEachIndexed { index, rect ->
            bounds.drop(index + 1).forEach { other -> assertFalse("Recipe facts must not overlap.", rect.overlaps(other)) }
        }
    }

    private fun capture(name: String) {
        val directory = File(checkNotNull(System.getProperty("coffee.screenshotDir")))
        check(directory.isDirectory || directory.mkdirs())
        compose.waitForIdle()
        compose.runOnUiThread {
            val view = compose.activity.window.decorView
            val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
            view.draw(Canvas(bitmap))
            File(directory, "$name.png").outputStream().use { check(bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)) }
            bitmap.recycle()
        }
    }

    @After
    fun tearDown() {
        RuntimeEnvironment.setFontScale(1f)
    }
}
