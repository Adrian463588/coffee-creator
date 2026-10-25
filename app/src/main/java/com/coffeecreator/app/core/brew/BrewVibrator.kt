package com.coffeecreator.app.core.brew

import android.content.Context
import android.media.AudioAttributes
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log

/**
 * Direct hardware haptic feedback for coffee brewing cues.
 * Uses USAGE_ALARM attributes so time-critical barista cues (step transitions,
 * target shot times) physically vibrate the device reliably.
 */
object BrewVibrator {
    private const val TAG = "BrewVibrator"

    // Step transition: 2 crisp pulses (total ~480ms)
    private const val STEP_DELAY_MS = 0L
    private const val STEP_PULSE_1_MS = 180L
    private const val STEP_GAP_MS = 100L
    private const val STEP_PULSE_2_MS = 200L

    // Target time: 3 decisive pulses (total ~950ms)
    private const val TARGET_DELAY_MS = 0L
    private const val TARGET_PULSE_1_MS = 250L
    private const val TARGET_GAP_1_MS = 100L
    private const val TARGET_PULSE_2_MS = 250L
    private const val TARGET_GAP_2_MS = 100L
    private const val TARGET_PULSE_3_MS = 350L

    // Completed: 1 solid confirmation pulse
    private const val COMPLETED_PULSE_MS = 400L

    // Click: quick 40ms pulse
    private const val CLICK_PULSE_MS = 40L

    private val STEP_PATTERN = longArrayOf(STEP_DELAY_MS, STEP_PULSE_1_MS, STEP_GAP_MS, STEP_PULSE_2_MS)
    private val TARGET_PATTERN = longArrayOf(
        TARGET_DELAY_MS,
        TARGET_PULSE_1_MS,
        TARGET_GAP_1_MS,
        TARGET_PULSE_2_MS,
        TARGET_GAP_2_MS,
        TARGET_PULSE_3_MS
    )
    private val COMPLETED_PATTERN = longArrayOf(0L, COMPLETED_PULSE_MS)

    @Suppress("DEPRECATION")
    fun getVibrator(context: Context): Vibrator? = try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            manager?.defaultVibrator
        } else {
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    } catch (runtimeFailure: RuntimeException) {
        Log.w(TAG, "Failed to obtain Vibrator service: ${runtimeFailure.message}")
        null
    }

    fun vibrateCue(context: Context, kind: BrewTimerCueKind) {
        val vibrator = getVibrator(context)?.takeIf { it.hasVibrator() } ?: return
        try {
            val pattern = when (kind) {
                BrewTimerCueKind.STEP_TRANSITION -> STEP_PATTERN
                BrewTimerCueKind.TARGET_TIME -> TARGET_PATTERN
                BrewTimerCueKind.COMPLETED -> COMPLETED_PATTERN
            }
            vibratePattern(vibrator, pattern)
        } catch (runtimeFailure: RuntimeException) {
            Log.w(TAG, "Failed to vibrate for cue $kind: ${runtimeFailure.message}")
        }
    }

    @Suppress("DEPRECATION")
    fun vibrateClick(context: Context) {
        val vibrator = getVibrator(context)?.takeIf { it.hasVibrator() } ?: return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val effect = VibrationEffect.createOneShot(CLICK_PULSE_MS, VibrationEffect.DEFAULT_AMPLITUDE)
                val audioAttributes = AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build()
                vibrator.vibrate(effect, audioAttributes)
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(CLICK_PULSE_MS)
            }
        } catch (runtimeFailure: RuntimeException) {
            Log.w(TAG, "Failed to vibrate click: ${runtimeFailure.message}")
        }
    }

    @Suppress("DEPRECATION")
    private fun vibratePattern(vibrator: Vibrator, pattern: LongArray) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val effect = VibrationEffect.createWaveform(pattern, -1)
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build()
            vibrator.vibrate(effect, audioAttributes)
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(pattern, -1)
        }
    }
}
