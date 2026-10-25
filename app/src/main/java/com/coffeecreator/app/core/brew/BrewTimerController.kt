package com.coffeecreator.app.core.brew

import android.content.Context
import android.os.SystemClock
import android.provider.Settings
import android.util.Log
import com.coffeecreator.app.core.engine.JsonInputPolicy
import com.coffeecreator.app.core.filesystem.PreferenceStore
import com.coffeecreator.app.core.model.InputLimits
import com.coffeecreator.app.core.model.Recipe
import com.google.gson.Gson
import java.util.UUID
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

private const val BREW_TIMER_REFRESH_MILLISECONDS = 250L
private const val MAX_MANUAL_SCALE_WEIGHT_GRAMS = 20_000.0

data class BrewTimerRuntimeState(
    val session: BrewSessionState? = null,
    val pendingCompletions: List<BrewSessionState> = emptyList(),
    val cue: BrewTimerCue? = null,
    val preferences: BrewTimerPreferences = BrewTimerPreferences(),
    val ready: Boolean = false,
    val recoveryNotice: String? = null
)

private data class TimerStorageWrite(val storage: BrewTimerStorage?, val completion: CompletableDeferred<Unit>? = null)

/** Application-owned timer. Activity/ViewModel recreation never creates another clock or cue producer. */
class BrewTimerController(
    private val context: Context,
    private val preferences: PreferenceStore,
    private val now: () -> Long = SystemClock::elapsedRealtime,
    private val currentBootCount: () -> Int = {
        Settings.Global.getInt(context.contentResolver, Settings.Global.BOOT_COUNT, -1)
    },
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
) {
    private val lock = Any()
    private val gson = Gson()
    private val mutableState = MutableStateFlow(BrewTimerRuntimeState())
    val state: StateFlow<BrewTimerRuntimeState> = mutableState.asStateFlow()
    private val ready = CompletableDeferred<Unit>()
    private val writes = Channel<TimerStorageWrite>(Channel.UNLIMITED)
    private var active: BrewTimerCheckpoint? = null
    private var ticker: Job? = null
    private var claimedCueId: String? = null
    private var storageWritable = true

    init {
        scope.launch { writeCheckpoints() }
        scope.launch { restoreStoredTimer() }
    }

    suspend fun awaitReady() = ready.await()

    suspend fun flush() {
        awaitReady()
        val completed = CompletableDeferred<Unit>()
        writes.send(TimerStorageWrite(null, completed))
        completed.await()
    }

    fun prepare(recipe: Recipe): Boolean = synchronized(lock) {
        if (!mutableState.value.ready) {
            reportNotice("Restoring the saved timer. Try Brew again when recovery finishes.")
            return@synchronized false
        }
        if (active?.session?.status in setOf(BrewExecutionStatus.RUNNING, BrewExecutionStatus.PAUSED)) {
            reportNotice("An active brew is preserved. Finish it or Reset before preparing another recipe.")
            return@synchronized false
        }
        if (mutableState.value.pendingCompletions.size >= InputLimits.COLLECTION_ITEMS) {
            reportNotice("Completed brews are waiting to be saved. Open Journal before starting another brew.")
            return@synchronized false
        }
        active = BrewTimerEngine.prepare(recipe, mutableState.value.preferences, UUID.randomUUID().toString(), now(), currentBootCount())
        publish(cue = null)
        persist()
        true
    }

    fun start() {
        synchronized(lock) {
            var checkpoint = active ?: return
            if (checkpoint.session.status == BrewExecutionStatus.RUNNING) return
            if (checkpoint.session.status in setOf(BrewExecutionStatus.COMPLETED, BrewExecutionStatus.ABORTED)) {
                if (mutableState.value.pendingCompletions.size >= InputLimits.COLLECTION_ITEMS) {
                    reportNotice("Completed brews are waiting to be saved. Open Journal before starting another brew.")
                    return
                }
                checkpoint = BrewTimerEngine.prepare(
                    checkpoint.session.recipe,
                    mutableState.value.preferences,
                    UUID.randomUUID().toString(),
                    now(),
                    currentBootCount()
                )
            }
            active = BrewTimerEngine.start(checkpoint, now())
            publish(cue = null)
            persist()
            ticker?.cancel()
            ticker = scope.launch {
                while (true) {
                    delay(BREW_TIMER_REFRESH_MILLISECONDS)
                    tick()
                }
            }
        }
        try {
            BrewTimerService.start(context)
        } catch (runtimeFailure: RuntimeException) {
            Log.w("BrewTimer", "Foreground start unavailable: ${runtimeFailure.javaClass.simpleName}")
            reportDeliveryFailure("Background timer unavailable. Keep this screen open; manual timing continues.")
        }
    }

    fun pause() {
        synchronized(lock) {
            val checkpoint = active ?: return
            if (checkpoint.session.status != BrewExecutionStatus.RUNNING) return
            active = BrewTimerEngine.pause(checkpoint, now())
            ticker?.cancel()
            publish(cue = null)
            persist()
        }
        BrewTimerService.stop(context)
    }

    fun skipStep() {
        synchronized(lock) {
            val checkpoint = active ?: return
            val steps = checkpoint.session.recipe.brewSteps
            val index = checkpoint.session.stepProgress?.stepIndex ?: 0
            if (index == steps.lastIndex) {
                finish()
            } else {
                val target = steps.take(index + 1).sumOf { it.durationSeconds.toLong() } * BREW_TIMER_MILLISECONDS_PER_SECOND
                active = BrewTimerEngine.seek(checkpoint, target, now())
                publish(cue = null)
                persist()
            }
        }
    }

    fun restartStep() = synchronized(lock) {
        val checkpoint = active ?: return@synchronized
        val index = checkpoint.session.stepProgress?.stepIndex ?: 0
        val target = checkpoint.session.recipe.brewSteps.take(index).sumOf { it.durationSeconds.toLong() } *
            BREW_TIMER_MILLISECONDS_PER_SECOND
        active = BrewTimerEngine.seek(checkpoint, target, now())
        publish(cue = null)
        persist()
    }

    fun reset() {
        synchronized(lock) {
            val checkpoint = active ?: return
            ticker?.cancel()
            active = BrewTimerEngine.prepare(
                checkpoint.session.recipe,
                mutableState.value.preferences,
                UUID.randomUUID().toString(),
                now(),
                currentBootCount()
            )
            publish(cue = null)
            persist()
        }
        BrewTimerService.stop(context)
    }

    fun finish() {
        synchronized(lock) {
            val checkpoint = active ?: return
            if (checkpoint.session.status == BrewExecutionStatus.COMPLETED) return
            active = BrewTimerEngine.finish(checkpoint, now())
            ticker?.cancel()
            addCompletion(requireNotNull(active).session)
            publish(cue = null)
            persist()
        }
        BrewTimerService.stop(context)
    }

    fun tick() = synchronized(lock) {
        val checkpoint = active ?: return@synchronized
        val result = BrewTimerEngine.tick(checkpoint, now())
        active = result.checkpoint
        val newlyCompleted = checkpoint.session.status != BrewExecutionStatus.COMPLETED &&
            result.checkpoint.session.status == BrewExecutionStatus.COMPLETED
        if (newlyCompleted) {
            addCompletion(result.checkpoint.session)
            ticker?.cancel()
        }
        publish(cue = result.cue ?: mutableState.value.cue)
        if (result.cue != null || newlyCompleted) persist()
    }

    fun updatePreferences(transform: (BrewTimerPreferences) -> BrewTimerPreferences) = synchronized(lock) {
        val updated = transform(mutableState.value.preferences)
        mutableState.value = mutableState.value.copy(preferences = updated)
        active = active?.let { checkpoint ->
            checkpoint.copy(
                session = checkpoint.session.copy(
                    keepScreenOn = updated.keepScreenOn,
                    audioEnabled = updated.audioEnabled,
                    hapticsEnabled = updated.hapticsEnabled,
                    notificationEnabled = updated.notificationEnabled
                )
            )
        }
        publish()
        persist()
    }

    fun recordManualWeight(weightGrams: Double): Boolean = synchronized(lock) {
        val checkpoint = active ?: return@synchronized false
        if (!weightGrams.isFinite() || weightGrams !in 0.0..MAX_MANUAL_SCALE_WEIGHT_GRAMS) {
            reportNotice("Enter a manual scale reading between 0 and 20,000 g.")
            return@synchronized false
        }
        if (checkpoint.session.status in setOf(BrewExecutionStatus.COMPLETED, BrewExecutionStatus.ABORTED) ||
            checkpoint.session.manualWeighIns.size >= InputLimits.COLLECTION_ITEMS
        ) {
            reportNotice("This session cannot accept another scale reading. Start a new brew or keep notes in Journal.")
            return@synchronized false
        }
        val elapsed = BrewTimerEngine.pause(checkpoint, now()).elapsedMilliseconds
        val projected = BrewTimerEngine.project(checkpoint.session, elapsed)
        val reading = ManualBrewWeighIn(projected.totalElapsedSeconds, weightGrams)
        val updated = projected.copy(
            currentWeightGrams = weightGrams,
            currentFlowRateGps = 0.0,
            manualWeightRecorded = true,
            manualWeighIns = projected.manualWeighIns + reading
        )
        active = checkpoint.copy(
            session = BrewTimerEngine.project(updated, elapsed),
            elapsedMilliseconds = elapsed,
            referenceMonotonicMilliseconds = now()
        )
        publish()
        persist()
        true
    }

    fun acknowledgeCompletions(savedJournalIds: Set<String>) = synchronized(lock) {
        val pending = mutableState.value.pendingCompletions
        val remaining = pending.filterNot { "timer:${it.sessionId}" in savedJournalIds }
        if (remaining.size != pending.size) {
            mutableState.value = mutableState.value.copy(pendingCompletions = remaining)
            persist()
        }
    }

    fun claimCue(cue: BrewTimerCue): Boolean = synchronized(lock) {
        if (cue.id == claimedCueId) return@synchronized false
        claimedCueId = cue.id
        true
    }

    fun reportDeliveryFailure(message: String) = synchronized(lock) {
        active = active?.let { it.copy(session = it.session.copy(recoveryNotice = message)) }
        reportNotice(message)
        publish()
    }

    fun restoreLegacySnapshot(snapshot: BrewSessionSnapshot, recipe: Recipe): Boolean = synchronized(lock) {
        if (!prepare(recipe)) return@synchronized false
        val checkpoint = requireNotNull(active)
        active = BrewTimerEngine.seek(
            checkpoint,
            snapshot.accumulatedElapsedSeconds.coerceAtLeast(0).toLong() * BREW_TIMER_MILLISECONDS_PER_SECOND,
            now()
        )
            .let {
                it.copy(
                    session = it.session.copy(
                        sessionId = snapshot.sessionId,
                        status = if (snapshot.status == BrewExecutionStatus.RUNNING) BrewExecutionStatus.PAUSED else snapshot.status,
                        recoveryNotice = "Restored a timer summary. Review the current step before resuming; " +
                            "scale samples were not restored."
                    )
                )
            }
        publish(cue = null)
        persist()
        true
    }

    /** Only test owners close a controller. Activity destruction must leave the active timer intact. */
    fun close() {
        ticker?.cancel()
        scope.cancel()
    }

    private suspend fun restoreStoredTimer() {
        try {
            val json = preferences.data.first().brewTimerState
            val stored = if (json.isBlank()) {
                BrewTimerStorage()
            } else {
                JsonInputPolicy.validateDepth(json)
                gson.fromJson(json, BrewTimerStorage::class.java).also(::validateStoredTimer)
            }
            synchronized(lock) {
                active = stored.active?.let { BrewTimerEngine.restore(it, now(), currentBootCount()) }
                mutableState.value = BrewTimerRuntimeState(
                    session = active?.session,
                    pendingCompletions = stored.pendingCompletions,
                    preferences = stored.preferences,
                    ready = true
                )
            }
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (boundaryFailure: Exception) {
            Log.w("BrewTimer", "Saved timer recovery unavailable: ${boundaryFailure.javaClass.simpleName}")
            synchronized(lock) {
                storageWritable = false
                mutableState.value = mutableState.value.copy(
                    ready = true,
                    recoveryNotice = "Saved timer could not be read. Its data is retained; new manual timing will not overwrite it."
                )
            }
        } finally {
            ready.complete(Unit)
        }
    }

    private fun validateStoredTimer(storage: BrewTimerStorage) {
        require(storage.schemaVersion == 1) { "Unsupported timer checkpoint version." }
        require(storage.pendingCompletions.size <= InputLimits.COLLECTION_ITEMS) { "Too many pending timer completions." }
        val checkpoints = listOfNotNull(storage.active)
        checkpoints.forEach { checkpoint ->
            require(checkpoint.elapsedMilliseconds >= 0L && checkpoint.referenceMonotonicMilliseconds >= 0L) {
                "Invalid timer clock checkpoint."
            }
        }
        (checkpoints.map { it.session } + storage.pendingCompletions).forEach { session ->
            require(session.sessionId.isNotBlank() && session.recipe.id.isNotBlank() && session.recipe.name.isNotBlank()) {
                "Invalid saved timer identity."
            }
            require(session.recipe.brewSteps.isNotEmpty() && session.recipe.brewSteps.all { it.durationSeconds > 0 }) {
                "Invalid saved timer schedule."
            }
            require(session.manualWeighIns.all { it.elapsedSeconds >= 0 && it.weightGrams.isFinite() && it.weightGrams >= 0.0 }) {
                "Invalid saved manual scale reading."
            }
        }
    }

    private suspend fun writeCheckpoints() {
        for (write in writes) {
            try {
                if (write.storage != null) preferences.setBrewTimerState(gson.toJson(write.storage))
                write.completion?.complete(Unit)
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (boundaryFailure: Exception) {
                reportDeliveryFailure("Timer recovery could not be saved. Manual timing continues; keep this screen open.")
                write.completion?.completeExceptionally(boundaryFailure)
            }
        }
    }

    private fun addCompletion(session: BrewSessionState) {
        val pending = mutableState.value.pendingCompletions
        if (pending.none { it.sessionId == session.sessionId }) {
            mutableState.value = mutableState.value.copy(pendingCompletions = pending + session)
        }
    }

    private fun publish(cue: BrewTimerCue? = mutableState.value.cue) {
        mutableState.value = mutableState.value.copy(session = active?.session, cue = cue)
    }

    private fun reportNotice(message: String) {
        mutableState.value = mutableState.value.copy(recoveryNotice = message)
    }

    private fun persist() {
        if (!storageWritable) return
        val current = mutableState.value
        writes.trySend(
            TimerStorageWrite(
                BrewTimerStorage(preferences = current.preferences, active = active, pendingCompletions = current.pendingCompletions)
            )
        )
    }
}
