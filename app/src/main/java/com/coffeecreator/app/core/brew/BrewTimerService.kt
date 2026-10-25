package com.coffeecreator.app.core.brew

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.os.SystemClock
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.coffeecreator.app.AppGraph
import com.coffeecreator.app.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.math.abs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

private const val ACTIVE_BREW_NOTIFICATION_ID = 42_010
private const val BREW_STEP_NOTIFICATION_ID = 42_011
private const val ACTIVE_BREW_CHANNEL_ID = "active-manual-brew"
private const val BREW_SESSION_ID_EXTRA = "brew-session-id"
private const val WAKE_LOCK_MARGIN_MILLISECONDS = 1_000L
private const val WAKE_LOCK_REPOSITION_TOLERANCE_MILLISECONDS = 1_500L

/** User-started local timer only. Never controls or connects to an espresso machine. */
@AndroidEntryPoint
class BrewTimerService : Service() {
    @Inject lateinit var graph: AppGraph
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private var observation: Job? = null
    private var wakeLock: PowerManager.WakeLock? = null
    private var wakeLockDeadline = 0L

    override fun onCreate() {
        super.onCreate()
        createChannels()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val current = graph.brewTimer.state.value.session
        try {
            val notification = ongoingNotification(current)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                startForeground(ACTIVE_BREW_NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
            } else {
                startForeground(ACTIVE_BREW_NOTIFICATION_ID, notification)
            }
        } catch (runtimeFailure: RuntimeException) {
            Log.w("BrewTimer", "Foreground notification unavailable: ${runtimeFailure.javaClass.simpleName}")
            graph.brewTimer.reportDeliveryFailure("Background timer unavailable. Keep this screen open; manual timing continues.")
            stopSelf()
            return START_NOT_STICKY
        }
        serviceScope.launch {
            graph.brewTimer.awaitReady()
            handleAction(intent)
            if (!notificationsAllowed() && graph.brewTimer.state.value.session?.notificationEnabled == true) {
                graph.brewTimer.reportDeliveryFailure(
                    "Notifications are off. Manual timing and visual steps continue; sound and vibration cues are unavailable."
                )
            }
            if (observation == null) {
                observation = launch { graph.brewTimer.state.collect(::render) }
            }
        }
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        releaseWakeLock()
        serviceScope.cancel()
        super.onDestroy()
    }

    private fun handleAction(intent: Intent?) {
        val session = graph.brewTimer.state.value.session ?: return
        val expectedSessionId = intent?.getStringExtra(BREW_SESSION_ID_EXTRA)
        if (expectedSessionId != null && expectedSessionId != session.sessionId) return
        when (intent?.action) {
            ACTION_PAUSE -> graph.brewTimer.pause()
            ACTION_RESUME -> graph.brewTimer.start()
            ACTION_SKIP -> graph.brewTimer.skipStep()
            ACTION_FINISH -> graph.brewTimer.finish()
        }
    }

    private fun render(state: BrewTimerRuntimeState) {
        try {
            renderTimerState(state)
        } catch (runtimeFailure: RuntimeException) {
            Log.w("BrewTimer", "Timer notification delivery unavailable: ${runtimeFailure.javaClass.simpleName}")
            graph.brewTimer.reportDeliveryFailure("Timer alerts are unavailable. Keep the timer screen open for visual steps.")
            stopTimerService()
        }
    }

    private fun renderTimerState(state: BrewTimerRuntimeState) {
        val session = state.session ?: return stopTimerService()
        state.cue?.let { cue ->
            if (graph.brewTimer.claimCue(cue) && session.notificationEnabled && notificationsAllowed()) {
                postNotification(BREW_STEP_NOTIFICATION_ID, cueNotification(session, cue))
            }
        }
        if (session.status != BrewExecutionStatus.RUNNING) {
            stopTimerService()
        } else {
            holdWakeLockUntilTarget(session)
            // A foreground notification is required even when drawer notifications were denied.
            if (notificationsAllowed()) {
                postNotification(ACTIVE_BREW_NOTIFICATION_ID, ongoingNotification(session))
            }
        }
    }

    private fun stopTimerService() {
        releaseWakeLock()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun holdWakeLockUntilTarget(session: BrewSessionState) {
        val remaining = BrewTimerEngine.durationMilliseconds(session.recipe) -
            session.totalElapsedSeconds.toLong() * BREW_TIMER_MILLISECONDS_PER_SECOND
        val deadline = SystemClock.elapsedRealtime() + remaining
        if (wakeLock != null && abs(deadline - wakeLockDeadline) <= WAKE_LOCK_REPOSITION_TOLERANCE_MILLISECONDS) return
        releaseWakeLock()
        if (remaining <= 0L) return
        val powerManager = getSystemService(PowerManager::class.java) ?: return
        wakeLockDeadline = deadline
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "$packageName:brew-timer").apply {
            setReferenceCounted(false)
            acquire(remaining + WAKE_LOCK_MARGIN_MILLISECONDS)
        }
    }

    private fun releaseWakeLock() {
        wakeLock?.let { if (it.isHeld) it.release() }
        wakeLock = null
    }

    private fun ongoingNotification(session: BrewSessionState?): Notification {
        val builder = NotificationCompat.Builder(this, ACTIVE_BREW_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle(session?.recipe?.name ?: "Preparing manual brew timer")
            .setContentText(
                session?.stepProgress?.let { progress ->
                    "Step ${progress.stepIndex + 1}/${progress.totalSteps}: ${progress.currentStep.description}"
                } ?: "Manual timer"
            )
            .setContentIntent(openTimerIntent())
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setSilent(true)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
        if (session != null) {
            builder.setWhen(System.currentTimeMillis() - session.totalElapsedSeconds.toLong() * BREW_TIMER_MILLISECONDS_PER_SECOND)
                .setUsesChronometer(session.status == BrewExecutionStatus.RUNNING)
                .addAction(android.R.drawable.ic_media_pause, "Pause", actionIntent(ACTION_PAUSE, session))
                .addAction(android.R.drawable.ic_media_next, "Next step", actionIntent(ACTION_SKIP, session))
                .addAction(android.R.drawable.ic_menu_save, "Finish", actionIntent(ACTION_FINISH, session))
        }
        return builder.build()
    }

    private fun cueNotification(session: BrewSessionState, cue: BrewTimerCue): Notification =
        NotificationCompat.Builder(this, cueChannelId(session.audioEnabled, session.hapticsEnabled))
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle(cue.title)
            .setContentText(cue.message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(cue.message))
            .setContentIntent(openTimerIntent())
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
            .build()

    private fun openTimerIntent(): PendingIntent = PendingIntent.getActivity(
        this,
        0,
        Intent(this, MainActivity::class.java).putExtra(EXTRA_OPEN_BREW_TIMER, true)
            .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP),
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    private fun actionIntent(action: String, session: BrewSessionState): PendingIntent = PendingIntent.getService(
        this,
        action.hashCode(),
        Intent(this, BrewTimerService::class.java).setAction(action).putExtra(BREW_SESSION_ID_EXTRA, session.sessionId),
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    private fun notificationsAllowed(): Boolean = NotificationManagerCompat.from(this).areNotificationsEnabled() &&
        (
            Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
            )

    private fun postNotification(id: Int, notification: Notification) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        getSystemService(NotificationManager::class.java).notify(id, notification)
    }

    private fun createChannels() {
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(
            NotificationChannel(ACTIVE_BREW_CHANNEL_ID, "Active brew timer", NotificationManager.IMPORTANCE_LOW).apply {
                description = "Quiet ongoing status for a brew you started."
                setSound(null, null)
                enableVibration(false)
            }
        )
        for (audio in listOf(false, true)) {
            for (haptics in listOf(false, true)) {
                manager.createNotificationChannel(cueChannel(audio, haptics))
            }
        }
    }

    private fun cueChannel(audio: Boolean, haptics: Boolean): NotificationChannel {
        val mode = listOfNotNull(if (audio) "sound" else null, if (haptics) "vibration" else null).joinToString(" + ")
            .ifBlank { "silent" }
        return NotificationChannel(
            cueChannelId(audio, haptics),
            "Brew steps · $mode",
            if (audio || haptics) NotificationManager.IMPORTANCE_DEFAULT else NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Pour-step and target-time cues. Android notification and Do Not Disturb settings apply."
            enableVibration(haptics)
            if (haptics) vibrationPattern = longArrayOf(0L, CUE_VIBRATION_PULSE_MS, CUE_VIBRATION_GAP_MS, CUE_VIBRATION_PULSE_MS)
            setSound(
                if (audio) RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION) else null,
                AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build()
            )
        }
    }

    companion object {
        private const val CUE_VIBRATION_PULSE_MS = 150L
        private const val CUE_VIBRATION_GAP_MS = 100L
        const val EXTRA_OPEN_BREW_TIMER = "com.coffeecreator.app.OPEN_BREW_TIMER"
        private const val ACTION_PAUSE = "com.coffeecreator.app.brew.PAUSE"
        private const val ACTION_RESUME = "com.coffeecreator.app.brew.RESUME"
        private const val ACTION_SKIP = "com.coffeecreator.app.brew.SKIP"
        private const val ACTION_FINISH = "com.coffeecreator.app.brew.FINISH"

        fun start(context: Context) {
            ContextCompat.startForegroundService(context, Intent(context, BrewTimerService::class.java))
        }

        fun stop(context: Context) {
            context.stopService(Intent(context, BrewTimerService::class.java))
        }

        internal fun cueChannelId(audio: Boolean, haptics: Boolean): String = "brew-steps-sound-$audio-vibration-$haptics"
    }
}
