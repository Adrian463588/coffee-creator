package com.coffeecreator.app.ui

import android.os.Looper
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.coffeecreator.app.CoffeeApplication
import com.coffeecreator.app.core.journal.BrewJournalEntry
import com.coffeecreator.app.core.model.Recipe
import com.coffeecreator.app.ui.viewmodel.CoffeeViewModel
import com.coffeecreator.app.ui.viewmodel.editRecipe
import com.coffeecreator.app.ui.viewmodel.prepareDraftForBrew
import com.coffeecreator.app.ui.viewmodel.prepareRecipeForBrew
import com.coffeecreator.app.ui.viewmodel.repeatLastBrew
import com.coffeecreator.app.ui.viewmodel.startBrewSession
import com.coffeecreator.app.ui.viewmodel.updateField
import kotlinx.coroutines.flow.update
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [33])
class JournalReplayLocalTest {
    @Test
    fun givenCompletedRecipeWithMeasurements_whenRepeatingLastBrew_thenNewSessionDoesNotReuseMeasuredResults() = withModel { model ->
        val recipe = Recipe(
            id = "measured-v60",
            name = "V60 with previous measurements",
            beverageMassGrams = 270.0,
            measuredTdsPercent = 1.3,
            firstDropSeconds = 8
        )
        val entry = BrewJournalEntry(
            id = "previous-brew",
            recipeId = recipe.id,
            brewMethod = recipe.brewMethod,
            doseGrams = recipe.doseGrams,
            waterGrams = recipe.waterGrams,
            brewDurationSeconds = 180
        )
        model.mutableState.update { it.copy(recipes = listOf(recipe), journalEntries = listOf(entry)) }

        model.repeatLastBrew()

        val repeated = requireNotNull(model.state.value.brewSession).recipe
        assertEquals(recipe.doseGrams, repeated.doseGrams, 0.0)
        assertNull(repeated.measuredTdsPercent)
        assertNull(repeated.beverageMassGrams)
        assertNull(repeated.firstDropSeconds)
        assertEquals(listOf(entry), model.state.value.journalEntries)
    }

    @Test
    fun givenActiveBrew_whenAnotherRecipeIsPrepared_thenSessionAndDraftArePreserved() = withModel { model ->
        val original = Recipe(id = "active", name = "Active V60")
        assertTrue(model.prepareRecipeForBrew(original))
        model.startBrewSession()
        val session = requireNotNull(model.state.value.brewSession)
        val draft = model.state.value.draft

        assertFalse(model.prepareRecipeForBrew(original.copy(id = "other", name = "Other")))

        assertEquals(session.sessionId, model.state.value.brewSession?.sessionId)
        assertEquals(original.id, model.state.value.brewSession?.recipe?.id)
        assertEquals(draft, model.state.value.draft)
        assertTrue(model.state.value.notice.orEmpty().contains("active"))
    }

    @Test
    fun givenEditedDraftAndOldReadyTimer_whenPreparingDraft_thenTimerUsesCurrentPlan() = withModel { model ->
        val original = Recipe(id = "draft-v60", name = "V60")
        assertTrue(model.prepareRecipeForBrew(original))
        model.updateField("dose", "20")
        model.updateField("water", "300")

        assertTrue(model.prepareDraftForBrew())

        assertEquals(20.0, model.state.value.brewSession!!.recipe.doseGrams, 0.0)
        assertEquals(300.0, model.state.value.brewSession!!.recipe.waterGrams, 0.0)
        assertTrue(model.state.value.journalEntries.isEmpty())
    }

    @Test
    fun givenInvalidDraftTime_whenPreparingTimer_thenShowsRecoveryWithoutReplacingSession() = withModel { model ->
        val original = Recipe(id = "valid", name = "Valid V60")
        assertTrue(model.prepareRecipeForBrew(original))
        val session = model.state.value.brewSession
        model.editRecipe(original)
        model.updateField("seconds", "not-seconds")

        assertFalse(model.prepareDraftForBrew())

        assertEquals(session, model.state.value.brewSession)
        assertTrue(model.state.value.notice.orEmpty().contains("whole seconds"))
    }

    private fun withModel(check: (CoffeeViewModel) -> Unit) {
        val app = ApplicationProvider.getApplicationContext<CoffeeApplication>()
        val store = ViewModelStore()
        val owner = object : ViewModelStoreOwner {
            override val viewModelStore = store
        }
        val model = ViewModelProvider(
            owner,
            object : ViewModelProvider.Factory {
                override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T =
                    modelClass.cast(CoffeeViewModel(app.graph, SavedStateHandle()))!!
            }
        )[CoffeeViewModel::class.java]
        try {
            val deadline = System.currentTimeMillis() + 20_000L
            while (!model.state.value.ready && System.currentTimeMillis() < deadline) {
                shadowOf(Looper.getMainLooper()).idle()
                Thread.sleep(10)
            }
            assertTrue("Application must finish loading before replay", model.state.value.ready)
            check(model)
        } finally {
            store.clear()
        }
    }
}
