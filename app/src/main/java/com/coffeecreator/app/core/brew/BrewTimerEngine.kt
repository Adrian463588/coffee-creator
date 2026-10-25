package com.coffeecreator.app.core.brew

import com.coffeecreator.app.core.model.BrewStep
import com.coffeecreator.app.core.model.Recipe

internal const val BREW_TIMER_MILLISECONDS_PER_SECOND = 1_000L

data class BrewTimerPreferences(
    val keepScreenOn: Boolean = false,
    val audioEnabled: Boolean = true,
    val hapticsEnabled: Boolean = true,
    val notificationEnabled: Boolean = true
)

data class BrewTimerCheckpoint(
    val session: BrewSessionState,
    val elapsedMilliseconds: Long,
    val referenceMonotonicMilliseconds: Long,
    val bootCount: Int,
    val targetCueSent: Boolean = false,
    val cueSequence: Long = 0L
)

data class BrewTimerStorage(
    val schemaVersion: Int = 1,
    val preferences: BrewTimerPreferences = BrewTimerPreferences(),
    val active: BrewTimerCheckpoint? = null,
    val pendingCompletions: List<BrewSessionState> = emptyList()
)

enum class BrewTimerCueKind { STEP_TRANSITION, TARGET_TIME, COMPLETED }

data class BrewTimerCue(val id: String, val kind: BrewTimerCueKind, val title: String, val message: String)

data class BrewTimerTick(val checkpoint: BrewTimerCheckpoint, val cue: BrewTimerCue? = null)

/** One deterministic timeline shared by foreground UI, recovery, and Android notification delivery. */
object BrewTimerEngine {
    fun prepare(
        recipe: Recipe,
        preferences: BrewTimerPreferences,
        sessionId: String,
        nowMonotonicMilliseconds: Long,
        bootCount: Int
    ): BrewTimerCheckpoint {
        require(recipe.brewTimeSeconds > 0 && recipe.brewSteps.all { it.durationSeconds > 0 }) {
            "Brew timing must contain positive durations."
        }
        val steps = recipe.brewSteps.ifEmpty {
            listOf(
                BrewStep(
                    stepNumber = 1,
                    description = if (isManualEspresso(recipe)) "Manual extraction" else "Continuous brew",
                    targetWaterGrams = recipe.waterGrams,
                    durationSeconds = recipe.brewTimeSeconds,
                    action = if (isManualEspresso(recipe)) "Extract" else "Brew",
                    cumulativeMassGrams = recipe.waterGrams
                )
            )
        }
        val session = BrewSessionState(
            sessionId = sessionId,
            recipe = recipe.copy(brewSteps = steps),
            status = BrewExecutionStatus.READY_ARMED,
            keepScreenOn = preferences.keepScreenOn,
            audioEnabled = preferences.audioEnabled,
            hapticsEnabled = preferences.hapticsEnabled,
            notificationEnabled = preferences.notificationEnabled
        )
        return BrewTimerCheckpoint(project(session, 0L), 0L, nowMonotonicMilliseconds, bootCount)
    }

    fun isManualEspresso(recipe: Recipe): Boolean = recipe.brewMethod.contains("espresso", ignoreCase = true)

    fun durationMilliseconds(recipe: Recipe): Long = recipe.brewSteps.sumOf { it.durationSeconds.toLong() } *
        BREW_TIMER_MILLISECONDS_PER_SECOND

    fun start(checkpoint: BrewTimerCheckpoint, nowMonotonicMilliseconds: Long): BrewTimerCheckpoint = checkpoint.copy(
        session = checkpoint.session.copy(status = BrewExecutionStatus.RUNNING),
        referenceMonotonicMilliseconds = nowMonotonicMilliseconds
    )

    fun pause(checkpoint: BrewTimerCheckpoint, nowMonotonicMilliseconds: Long): BrewTimerCheckpoint {
        val elapsed = elapsedMilliseconds(checkpoint, nowMonotonicMilliseconds)
        return checkpoint.copy(
            session = project(checkpoint.session.copy(status = BrewExecutionStatus.PAUSED), elapsed),
            elapsedMilliseconds = elapsed,
            referenceMonotonicMilliseconds = nowMonotonicMilliseconds
        )
    }

    fun seek(checkpoint: BrewTimerCheckpoint, elapsedMilliseconds: Long, nowMonotonicMilliseconds: Long): BrewTimerCheckpoint {
        val elapsed = elapsedMilliseconds.coerceAtLeast(0L)
        return checkpoint.copy(
            session = project(checkpoint.session, elapsed),
            elapsedMilliseconds = elapsed,
            referenceMonotonicMilliseconds = nowMonotonicMilliseconds,
            targetCueSent = elapsed >= durationMilliseconds(checkpoint.session.recipe)
        )
    }

    fun finish(checkpoint: BrewTimerCheckpoint, nowMonotonicMilliseconds: Long): BrewTimerCheckpoint {
        val elapsed = elapsedMilliseconds(checkpoint, nowMonotonicMilliseconds)
        return checkpoint.copy(
            session = project(checkpoint.session.copy(status = BrewExecutionStatus.COMPLETED), elapsed),
            elapsedMilliseconds = elapsed,
            referenceMonotonicMilliseconds = nowMonotonicMilliseconds
        )
    }

    fun tick(checkpoint: BrewTimerCheckpoint, nowMonotonicMilliseconds: Long): BrewTimerTick {
        if (checkpoint.session.status != BrewExecutionStatus.RUNNING) return BrewTimerTick(checkpoint)
        val duration = durationMilliseconds(checkpoint.session.recipe)
        val manualEspresso = isManualEspresso(checkpoint.session.recipe)
        val rawElapsed = elapsedMilliseconds(checkpoint, nowMonotonicMilliseconds)
        val elapsed = if (manualEspresso) rawElapsed else rawElapsed.coerceAtMost(duration)
        val completed = !manualEspresso && elapsed >= duration
        val session = project(
            checkpoint.session.copy(status = if (completed) BrewExecutionStatus.COMPLETED else BrewExecutionStatus.RUNNING),
            elapsed
        )
        val targetDue = elapsed >= duration && !checkpoint.targetCueSent
        val stepChanged = session.stepProgress?.stepIndex != checkpoint.session.stepProgress?.stepIndex
        val kind = when {
            targetDue && completed -> BrewTimerCueKind.COMPLETED
            targetDue -> BrewTimerCueKind.TARGET_TIME
            stepChanged -> BrewTimerCueKind.STEP_TRANSITION
            else -> null
        }
        val next = checkpoint.copy(
            session = session,
            elapsedMilliseconds = elapsed,
            referenceMonotonicMilliseconds = nowMonotonicMilliseconds,
            targetCueSent = checkpoint.targetCueSent || targetDue,
            cueSequence = checkpoint.cueSequence + if (kind == null) 0L else 1L
        )
        return BrewTimerTick(next, kind?.let { cue(next, it) })
    }

    fun restore(checkpoint: BrewTimerCheckpoint, nowMonotonicMilliseconds: Long, bootCount: Int): BrewTimerCheckpoint {
        val wasRunning = checkpoint.session.status == BrewExecutionStatus.RUNNING
        val sameBoot = bootCount >= 0 && checkpoint.bootCount == bootCount &&
            nowMonotonicMilliseconds >= checkpoint.referenceMonotonicMilliseconds
        val elapsed = if (wasRunning && sameBoot) {
            elapsedMilliseconds(checkpoint, nowMonotonicMilliseconds)
        } else {
            checkpoint.elapsedMilliseconds
        }
        val recoverable = wasRunning || checkpoint.session.status == BrewExecutionStatus.PAUSED
        val notice = if (recoverable) {
            if (wasRunning && !sameBoot) {
                "Recovered paused at the last saved elapsed time after a clock restart. Review, then Resume or Reset."
            } else {
                "Recovered paused. Review the current step, then Resume or Reset; earlier alerts will not replay."
            }
        } else {
            checkpoint.session.recoveryNotice
        }
        return checkpoint.copy(
            session = project(
                checkpoint.session.copy(
                    status = if (recoverable) BrewExecutionStatus.PAUSED else checkpoint.session.status,
                    recoveryNotice = notice
                ),
                elapsed
            ),
            elapsedMilliseconds = elapsed,
            referenceMonotonicMilliseconds = nowMonotonicMilliseconds,
            bootCount = bootCount,
            targetCueSent = checkpoint.targetCueSent || elapsed >= durationMilliseconds(checkpoint.session.recipe)
        )
    }

    fun project(session: BrewSessionState, elapsedMilliseconds: Long): BrewSessionState {
        val elapsedSeconds = (elapsedMilliseconds.coerceAtLeast(0L) / BREW_TIMER_MILLISECONDS_PER_SECOND)
            .coerceAtMost(Int.MAX_VALUE.toLong()).toInt()
        val steps = session.recipe.brewSteps
        require(steps.isNotEmpty()) { "A timer requires a brew step." }
        var stepStart = 0L
        var stepIndex = steps.lastIndex
        for (index in steps.indices) {
            if (elapsedSeconds.toLong() < stepStart + steps[index].durationSeconds || index == steps.lastIndex) {
                stepIndex = index
                break
            }
            stepStart += steps[index].durationSeconds
        }
        val current = steps[stepIndex]
        val stepElapsed = (elapsedSeconds.toLong() - stepStart).coerceAtLeast(0L).coerceAtMost(Int.MAX_VALUE.toLong()).toInt()
        val previousTarget = steps.getOrNull(stepIndex - 1)?.cumulativeMassGrams ?: 0.0
        return session.copy(
            totalElapsedSeconds = elapsedSeconds,
            stepProgress = BrewStepProgress(
                stepIndex = stepIndex,
                totalSteps = steps.size,
                currentStep = current,
                nextStep = steps.getOrNull(stepIndex + 1),
                stepElapsedSeconds = stepElapsed,
                stepRemainingSeconds = (current.durationSeconds - stepElapsed).coerceAtLeast(0),
                stepTargetWaterGrams = current.targetWaterGrams,
                cumulativeTargetWaterGrams = current.cumulativeMassGrams,
                pouredInStepGrams = (session.currentWeightGrams - previousTarget).coerceAtLeast(0.0),
                remainingToCumulativeTargetGrams = (current.cumulativeMassGrams - session.currentWeightGrams).coerceAtLeast(0.0),
                proximityState = if (session.manualWeightRecorded || session.scaleDevice != null) {
                    BrewFilterEngine.calculateProximity(session.currentWeightGrams, current.cumulativeMassGrams)
                } else {
                    ProximityState.NORMAL
                }
            )
        )
    }

    private fun elapsedMilliseconds(checkpoint: BrewTimerCheckpoint, nowMonotonicMilliseconds: Long): Long {
        val delta = if (checkpoint.session.status == BrewExecutionStatus.RUNNING) {
            (nowMonotonicMilliseconds - checkpoint.referenceMonotonicMilliseconds).coerceAtLeast(0L)
        } else {
            0L
        }
        return checkpoint.elapsedMilliseconds + delta
    }

    private fun cue(checkpoint: BrewTimerCheckpoint, kind: BrewTimerCueKind): BrewTimerCue {
        val session = checkpoint.session
        val title = when (kind) {
            BrewTimerCueKind.STEP_TRANSITION -> "Next brew step"
            BrewTimerCueKind.TARGET_TIME -> "Target shot time reached"
            BrewTimerCueKind.COMPLETED -> "Brew timer complete"
        }
        val message = when (kind) {
            BrewTimerCueKind.STEP_TRANSITION -> session.stepProgress?.let {
                "Step ${it.stepIndex + 1} of ${it.totalSteps}: ${it.currentStep.description}"
            }.orEmpty()
            BrewTimerCueKind.TARGET_TIME -> "Stop the machine manually when ready, then Finish in Coffee Creator."
            BrewTimerCueKind.COMPLETED -> session.recipe.name
        }
        return BrewTimerCue("${session.sessionId}:${checkpoint.cueSequence}", kind, title, message)
    }
}
