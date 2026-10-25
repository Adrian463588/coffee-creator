package com.coffeecreator.app.core.brew

import android.content.Context
import android.media.AudioAttributes
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log

/**
 * Direct audio playback provider for brew step and target time cues.
 * Uses USAGE_ALARM attributes so time-critical barista cues play reliably.
 */
object BrewAudioPlayer {
    private const val TAG = "BrewAudioPlayer"
    private var activeRingtone: Ringtone? = null

    fun playCue(context: Context, kind: BrewTimerCueKind) {
        try {
            activeRingtone?.stop()
            val uri: Uri = when (kind) {
                BrewTimerCueKind.TARGET_TIME, BrewTimerCueKind.COMPLETED -> {
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                        ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                }
                BrewTimerCueKind.STEP_TRANSITION -> {
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                }
            } ?: return
            val ringtone = RingtoneManager.getRingtone(context.applicationContext, uri) ?: return
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ringtone.audioAttributes = AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build()
            }
            activeRingtone = ringtone
            ringtone.play()
        } catch (runtimeFailure: RuntimeException) {
            Log.w(TAG, "Failed to play audio cue: ${runtimeFailure.message}")
        }
    }

    fun playClick(context: Context) {
        try {
            activeRingtone?.stop()
            val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION) ?: return
            val ringtone = RingtoneManager.getRingtone(context.applicationContext, uri) ?: return
            ringtone.play()
        } catch (runtimeFailure: RuntimeException) {
            Log.w(TAG, "Failed to play click: ${runtimeFailure.message}")
        }
    }

    fun stop() {
        try {
            activeRingtone?.stop()
            activeRingtone = null
        } catch (runtimeFailure: RuntimeException) {
            Log.w(TAG, "Failed to stop audio: ${runtimeFailure.message}")
        }
    }
}
