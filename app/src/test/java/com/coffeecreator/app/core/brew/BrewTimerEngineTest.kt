package com.coffeecreator.app.core.brew

import com.coffeecreator.app.core.model.BrewStep
import com.coffeecreator.app.core.model.Recipe
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class BrewTimerEngineTest {
    private val recipe = Recipe(
        id = "v60-timer-fixture",
        name = "Manual V60 fixture",
        brewTimeSeconds = 90,
        brewSteps = listOf(
            BrewStep(1, "Bloom", 50.0, 45, cumulativeMassGrams = 50.0),
            BrewStep(2, "Second pour", 100.0, 30, cumulativeMassGrams = 150.0),
            BrewStep(3, "Drawdown", 0.0, 15, action = "Wait", cumulativeMassGrams = 150.0)
        )
    )

    private fun ready(source: Recipe = recipe) = BrewTimerEngine.prepare(source, BrewTimerPreferences(), "session-1", 1_000L, 7)

    @Test
    fun givenBloom_whenItsDeadlineArrives_thenSecondPourAndOneCueBecomeCurrent() {
        val running = BrewTimerEngine.start(ready(), 1_000L)
        val boundary = BrewTimerEngine.tick(running, 46_000L)

        assertEquals(45, boundary.checkpoint.session.totalElapsedSeconds)
        assertEquals(1, boundary.checkpoint.session.stepProgress?.stepIndex)
        assertEquals(30, boundary.checkpoint.session.stepProgress?.stepRemainingSeconds)
        assertEquals(BrewTimerCueKind.STEP_TRANSITION, boundary.cue?.kind)
        assertTrue(requireNotNull(boundary.cue).message.contains("Second pour"))

        var checkpoint = boundary.checkpoint
        for (quarterSecond in 1..4) {
            val next = BrewTimerEngine.tick(checkpoint, 46_000L + quarterSecond * 250L)
            assertNull(next.cue)
            checkpoint = next.checkpoint
        }
    }

    @Test
    fun givenDelayedTickCrossingTwoSteps_whenCatchingUp_thenOnlyCurrentStepCueIsEmitted() {
        val tick = BrewTimerEngine.tick(BrewTimerEngine.start(ready(), 1_000L), 77_000L)
        assertEquals(2, tick.checkpoint.session.stepProgress?.stepIndex)
        assertEquals("Drawdown", tick.checkpoint.session.stepProgress?.currentStep?.description)
        assertEquals(BrewTimerCueKind.STEP_TRANSITION, tick.cue?.kind)
    }

    @Test
    fun givenFractionalElapsed_whenPausedAndResumed_thenPauseGapIsExcludedWithoutTruncation() {
        val running = BrewTimerEngine.start(ready(), 1_000L)
        val paused = BrewTimerEngine.pause(running, 2_750L)
        assertEquals(1_750L, paused.elapsedMilliseconds)
        assertEquals(BrewExecutionStatus.PAUSED, paused.session.status)

        val resumed = BrewTimerEngine.start(paused, 40_000L)
        val tick = BrewTimerEngine.tick(resumed, 40_250L)
        assertEquals(2_000L, tick.checkpoint.elapsedMilliseconds)
        assertEquals(2, tick.checkpoint.session.totalElapsedSeconds)
        assertNull(tick.cue)
    }

    @Test
    fun givenPausedSecondStep_whenSeekingToItsStart_thenPauseStateAndFullCountdownRemain() {
        val running = BrewTimerEngine.start(ready(), 1_000L)
        val paused = BrewTimerEngine.pause(running, 56_000L)
        val restarted = BrewTimerEngine.seek(paused, 45_000L, 80_000L)
        assertEquals(BrewExecutionStatus.PAUSED, restarted.session.status)
        assertEquals(45, restarted.session.totalElapsedSeconds)
        assertEquals(30, restarted.session.stepProgress?.stepRemainingSeconds)
    }

    @Test
    fun givenFilterTimer_whenComplete_thenExactTargetTimeAndSingleCompletionCueAreReturned() {
        val completed = BrewTimerEngine.tick(BrewTimerEngine.start(ready(), 1_000L), 91_250L)
        assertEquals(BrewExecutionStatus.COMPLETED, completed.checkpoint.session.status)
        assertEquals(90, completed.checkpoint.session.totalElapsedSeconds)
        assertEquals(90_000L, completed.checkpoint.elapsedMilliseconds)
        assertEquals(BrewTimerCueKind.COMPLETED, completed.cue?.kind)
        assertNull(BrewTimerEngine.tick(completed.checkpoint, 92_000L).cue)
    }

    @Test
    fun givenManualEspresso_whenTargetTimePasses_thenCueOnceAndContinueUntilHumanFinishes() {
        val espresso = Recipe(
            id = "manual-espresso",
            name = "Semi-automatic espresso fixture",
            brewMethod = "Espresso",
            equipment = "User-owned semi-automatic machine",
            waterGrams = 36.0,
            brewTimeSeconds = 30
        )
        val atTarget = BrewTimerEngine.tick(BrewTimerEngine.start(ready(espresso), 1_000L), 31_000L)
        assertEquals(BrewExecutionStatus.RUNNING, atTarget.checkpoint.session.status)
        assertEquals(BrewTimerCueKind.TARGET_TIME, atTarget.cue?.kind)
        assertNull(atTarget.checkpoint.session.scaleDevice)
        assertFalse(atTarget.checkpoint.session.autoStartArmed)

        val beyondTarget = BrewTimerEngine.tick(atTarget.checkpoint, 38_500L)
        assertEquals(37, beyondTarget.checkpoint.session.totalElapsedSeconds)
        assertEquals(BrewExecutionStatus.RUNNING, beyondTarget.checkpoint.session.status)
        assertNull(beyondTarget.cue)
        val completed = BrewTimerEngine.finish(beyondTarget.checkpoint, 39_000L)
        assertEquals(38, completed.session.totalElapsedSeconds)
        assertEquals(BrewExecutionStatus.COMPLETED, completed.session.status)
    }

    @Test
    fun givenRunningCheckpoint_whenProcessRestoresOnSameBoot_thenRecoverPausedWithoutStaleCue() {
        val running = BrewTimerEngine.start(ready(), 1_000L)
        val restored = BrewTimerEngine.restore(running, 51_000L, 7)
        assertEquals(50, restored.session.totalElapsedSeconds)
        assertEquals(1, restored.session.stepProgress?.stepIndex)
        assertEquals(BrewExecutionStatus.PAUSED, restored.session.status)
        assertNotNull(restored.session.recoveryNotice)

        val resumed = BrewTimerEngine.start(restored, 80_000L)
        assertNull(BrewTimerEngine.tick(resumed, 80_250L).cue)
    }

    @Test
    fun givenOldUptimeAfterDeviceReboot_whenRestoring_thenNeverSubtractUnrelatedClock() {
        val checkpoint = BrewTimerEngine.pause(BrewTimerEngine.start(ready(), 1_000L), 13_500L)
            .copy(session = ready().session.copy(status = BrewExecutionStatus.RUNNING))
        val restored = BrewTimerEngine.restore(checkpoint, 500L, 8)
        assertEquals(12_500L, restored.elapsedMilliseconds)
        assertEquals(BrewExecutionStatus.PAUSED, restored.session.status)
        assertTrue(requireNotNull(restored.session.recoveryNotice).contains("saved"))
    }

    @Test
    fun givenNoExplicitScreenAwakeChoice_whenPreparingTimer_thenScreenAwakeIsOptIn() {
        assertFalse(ready().session.keepScreenOn)
    }

    @Test(expected = IllegalArgumentException::class)
    fun givenInvalidStepDuration_whenPreparingTimer_thenRejectRatherThanStartingBrokenSchedule() {
        ready(recipe.copy(brewSteps = listOf(BrewStep(1, "Invalid", 50.0, 0))))
    }
}
