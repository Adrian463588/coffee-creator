package com.coffeecreator.app.core.brew

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.coffeecreator.app.core.filesystem.PreferenceStore
import com.coffeecreator.app.core.journal.BrewCompletionRecordFactory
import com.coffeecreator.app.core.model.Recipe
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class BrewTimerControllerTest {
    private lateinit var context: Context
    private lateinit var preferences: PreferenceStore
    private lateinit var controller: BrewTimerController
    private var clock = 1_000L
    private val recipe = Recipe(id = "source-v60", name = "Exact source recipe", brewTimeSeconds = 90)

    @Before
    fun setUp() = runBlocking {
        context = ApplicationProvider.getApplicationContext()
        preferences = PreferenceStore(context)
        preferences.setBrewTimerState("")
        controller = newController()
        controller.awaitReady()
    }

    @After
    fun tearDown() = runBlocking {
        controller.flush()
        controller.close()
    }

    @Test
    fun givenActiveOrPausedBrew_whenAnotherRecipeIsPrepared_thenItsIdentityAndElapsedArePreserved() {
        assertTrue(controller.prepare(recipe))
        controller.start()
        clock += 1_750L
        controller.tick()
        val running = requireNotNull(controller.state.value.session)

        assertFalse(controller.prepare(recipe.copy(id = "unrelated", name = "Other recipe")))
        assertEquals(running.sessionId, controller.state.value.session?.sessionId)
        controller.pause()
        assertFalse(controller.prepare(recipe))
        assertEquals(1, controller.state.value.session?.totalElapsedSeconds)
        assertEquals(BrewExecutionStatus.PAUSED, controller.state.value.session?.status)
    }

    @Test
    fun givenCuePreferencesAndRunningRecipe_whenControllerIsRecreated_thenFullPlanAndPausedTimeRecover() = runBlocking {
        controller.updatePreferences {
            it.copy(
                audioEnabled = false,
                hapticsEnabled = false,
                notificationEnabled = true,
                keepScreenOn = true
            )
        }
        controller.prepare(recipe)
        controller.start()
        clock += 12_750L
        controller.flush()
        controller.close()

        controller = newController()
        controller.awaitReady()
        val recovered = requireNotNull(controller.state.value.session)
        assertEquals(recipe.id, recovered.recipe.id)
        assertEquals(recipe.name, recovered.recipe.name)
        assertEquals(12, recovered.totalElapsedSeconds)
        assertEquals(BrewExecutionStatus.PAUSED, recovered.status)
        assertTrue(recovered.keepScreenOn)
        assertTrue(recovered.notificationEnabled)
        assertFalse(recovered.audioEnabled)
        assertFalse(recovered.hapticsEnabled)
        assertNull(controller.state.value.cue)
        assertNotNull(recovered.recoveryNotice)
    }

    @Test
    fun givenCompletedBrewAndNewPreparation_whenRecreated_thenUnloggedCompletionCannotBeOverwritten() = runBlocking {
        controller.prepare(recipe)
        controller.start()
        clock += 23_000L
        controller.recordManualWeight(123.4)
        controller.finish()
        controller.finish()
        val completed = controller.state.value.pendingCompletions.single()
        assertTrue(controller.prepare(recipe.copy(id = "next-v60", name = "Next recipe")))
        controller.flush()
        controller.close()

        controller = newController()
        controller.awaitReady()
        assertEquals("next-v60", controller.state.value.session?.recipe?.id)
        val pending = controller.state.value.pendingCompletions.single()
        assertEquals(completed.sessionId, pending.sessionId)
        assertEquals(recipe.name, pending.recipe.name)
        assertEquals(23, pending.totalElapsedSeconds)
        assertEquals(123.4, pending.manualWeighIns.single().weightGrams, 0.0)
        val journal = BrewCompletionRecordFactory.fromTimerCompletion(pending.recipe, pending.totalElapsedSeconds, null)
        assertNull(journal.beverageMassGrams)
        assertNull(journal.measuredTdsPercent)

        controller.acknowledgeCompletions(setOf("wrong-id"))
        assertEquals(1, controller.state.value.pendingCompletions.size)
        controller.acknowledgeCompletions(setOf("timer:${pending.sessionId}"))
        assertTrue(controller.state.value.pendingCompletions.isEmpty())
    }

    @Test
    fun givenManualScaleEntry_whenValueIsInvalid_thenPreviousHumanReadingAndZeroFlowAreRetained() {
        controller.prepare(recipe)
        assertTrue(controller.recordManualWeight(50.0))
        for (invalid in listOf(Double.NaN, Double.POSITIVE_INFINITY, -0.1, 20_000.1)) {
            assertFalse(controller.recordManualWeight(invalid))
        }
        val session = requireNotNull(controller.state.value.session)
        assertTrue(session.manualWeightRecorded)
        assertEquals(50.0, session.currentWeightGrams, 0.0)
        assertEquals(0.0, session.currentFlowRateGps, 0.0)
        assertEquals(1, session.manualWeighIns.size)
        assertNull(session.scaleDevice)
    }

    @Test
    fun givenStepCue_whenTwoConsumersClaimIt_thenOnlyOneDeliveryIsAllowed() {
        val cue = BrewTimerCue("session:1", BrewTimerCueKind.STEP_TRANSITION, "Pour", "Next pour")
        assertTrue(controller.claimCue(cue))
        assertFalse(controller.claimCue(cue))
    }

    @Test
    fun givenUnreadableCheckpoint_whenStartingManually_thenOriginalRecoveryDataIsRetained() = runBlocking {
        controller.close()
        val original = "{invalid-checkpoint"
        preferences.setBrewTimerState(original)
        controller = newController()
        controller.awaitReady()
        assertNotNull(controller.state.value.recoveryNotice)
        assertTrue(controller.prepare(recipe))
        controller.flush()
        assertEquals(original, preferences.data.first().brewTimerState)
    }

    private fun newController() = BrewTimerController(context, preferences, now = { clock }, currentBootCount = { 7 })
}
