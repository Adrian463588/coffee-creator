package com.coffeecreator.app.core.brew

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BrewVibratorTest {
    private val context: Context = ApplicationProvider.getApplicationContext()

    @Test
    fun givenContext_whenGetVibrator_thenReturnsNonNullService() {
        val vibrator = BrewVibrator.getVibrator(context)
        assertNotNull(vibrator)
    }

    @Test
    fun givenStepTransitionCue_whenVibrateCue_thenExecutesWithoutException() {
        BrewVibrator.vibrateCue(context, BrewTimerCueKind.STEP_TRANSITION)
    }

    @Test
    fun givenTargetTimeCue_whenVibrateCue_thenExecutesWithoutException() {
        BrewVibrator.vibrateCue(context, BrewTimerCueKind.TARGET_TIME)
    }

    @Test
    fun givenCompletedCue_whenVibrateCue_thenExecutesWithoutException() {
        BrewVibrator.vibrateCue(context, BrewTimerCueKind.COMPLETED)
    }

    @Test
    fun givenClickAction_whenVibrateClick_thenExecutesWithoutException() {
        BrewVibrator.vibrateClick(context)
    }

    @Test
    fun givenAudioCues_whenPlayed_thenExecuteWithoutException() {
        BrewAudioPlayer.playCue(context, BrewTimerCueKind.STEP_TRANSITION)
        BrewAudioPlayer.playCue(context, BrewTimerCueKind.TARGET_TIME)
        BrewAudioPlayer.playCue(context, BrewTimerCueKind.COMPLETED)
        BrewAudioPlayer.playClick(context)
        BrewAudioPlayer.stop()
    }
}
