package com.coffeecreator.app.ui.viewmodel

import android.os.SystemClock
import androidx.lifecycle.viewModelScope
import com.coffeecreator.app.core.ble.ScaleConnectionState
import com.coffeecreator.app.core.ble.ScaleWeightSample
import com.coffeecreator.app.core.ble.SimulatedBleScaleDriver
import com.coffeecreator.app.core.brew.AudioCueType
import com.coffeecreator.app.core.brew.BrewExecutionStatus
import com.coffeecreator.app.core.brew.BrewFilterEngine
import com.coffeecreator.app.core.brew.BrewSessionSnapshot
import com.coffeecreator.app.core.brew.BrewSessionState
import com.coffeecreator.app.core.brew.BrewStepProgress
import com.coffeecreator.app.core.brew.HapticCueType
import com.coffeecreator.app.core.brew.ProximityState
import com.coffeecreator.app.core.engine.DomainValidation
import com.coffeecreator.app.core.journal.BrewCompletionRecordFactory
import com.coffeecreator.app.core.model.BrewStep
import com.coffeecreator.app.core.model.Ingredient
import com.coffeecreator.app.core.model.Recipe
import com.coffeecreator.app.core.reliability.LogLevel
import com.coffeecreator.app.core.reliability.ModuleStatus
import java.util.UUID
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val AUTO_START_WEIGHT_THRESHOLD_GRAMS = 0.5
private const val ESPRESSO_AUTO_STOP_MINIMUM_YIELD_GRAMS = 5.0
private const val ESPRESSO_AUTO_STOP_FLOW_RATE_GRAMS_PER_SECOND = 0.20
private const val ESPRESSO_AUTO_STOP_SUSTAINED_DURATION_MS = 1_500L

fun defaultFallbackRecipe(): Recipe = Recipe(
    id = "default-fallback-v60",
    name = "Standard V60 Pour Over",
    brewMethod = "Pour Over (V60)",
    equipment = "Hario V60",
    doseGrams = 15.0,
    waterGrams = 250.0,
    targetRatio = "1:16.7",
    temperatureCelsius = 93.0,
    brewTimeSeconds = 180,
    grindSize = "Medium-Fine",
    brewSteps = listOf(
        BrewStep(
            stepNumber = 1,
            description = "Bloom",
            targetWaterGrams = 50.0,
            durationSeconds = 45,
            action = "Pour",
            startOffsetSeconds = 0,
            cumulativeMassGrams = 50.0
        ),
        BrewStep(
            stepNumber = 2,
            description = "Main Pour",
            targetWaterGrams = 200.0,
            durationSeconds = 135,
            action = "Pour",
            startOffsetSeconds = 45,
            cumulativeMassGrams = 250.0
        )
    ),
    notes = "Standard fallback pour over recipe.",
    revisionGroupId = "default-fallback-v60"
)

fun resolveActiveRecipe(
    targetRecipe: Recipe? = null,
    draft: RecipeDraft? = null,
    savedRecipes: List<Recipe> = emptyList(),
    ingredients: List<Ingredient> = emptyList()
): Recipe {
    if (targetRecipe != null) {
        val validated = runCatching {
            DomainValidation.validateRecipe(targetRecipe, ingredients)
            targetRecipe
        }.getOrNull()
        if (validated != null) return validated
    }

    if (draft != null) {
        val safeDraftRecipe = runCatching {
            val mergedFields = RecipeDraft.defaultFields() + draft.fields
            val rawName = mergedFields["name"]?.trim().orEmpty()
            val effectiveMethod = if (mergedFields["method"] == "Custom" && !mergedFields["customMethod"].isNullOrBlank()) {
                mergedFields.getValue("customMethod").trim()
            } else {
                mergedFields["method"]?.takeIf(String::isNotBlank) ?: "Pour Over (V60)"
            }
            val synthesizedName = if (rawName.isBlank()) "Draft $effectiveMethod" else rawName
            val safeDraft = draft.copy(fields = mergedFields + ("name" to synthesizedName))
            safeDraft.build(ingredients)
        }.getOrNull()
        if (safeDraftRecipe != null) return safeDraftRecipe
    }

    val savedRecipe = savedRecipes.firstOrNull()?.let { recipe ->
        runCatching {
            DomainValidation.validateRecipe(recipe, ingredients)
            recipe
        }.getOrNull()
    }
    if (savedRecipe != null) return savedRecipe

    return defaultFallbackRecipe()
}

internal fun CoffeeViewModel.resolveActiveRecipe(targetRecipe: Recipe? = null): Recipe {
    return resolveActiveRecipe(
        targetRecipe = targetRecipe,
        draft = mutableState.value.draft,
        savedRecipes = mutableState.value.recipes,
        ingredients = mutableState.value.ingredients
    )
}

internal fun createDefaultBrewSession(
    recipe: Recipe,
    scaleDevice: com.coffeecreator.app.core.ble.BleScaleDevice? = null,
    scaleConnectionState: ScaleConnectionState = ScaleConnectionState.DISCONNECTED
): BrewSessionState {
    val steps = recipe.brewSteps.ifEmpty {
        listOf(
            BrewStep(
                stepNumber = 1,
                description = "Continuous extraction",
                targetWaterGrams = recipe.waterGrams,
                durationSeconds = recipe.brewTimeSeconds,
                action = "Pour",
                startOffsetSeconds = 0,
                cumulativeMassGrams = recipe.waterGrams
            )
        )
    }
    val activeRecipe = recipe.copy(brewSteps = steps)
    val firstStep = steps.first()
    val nextStep = if (steps.size > 1) steps[1] else null
    val progress = BrewStepProgress(
        stepIndex = 0,
        totalSteps = steps.size,
        currentStep = firstStep,
        nextStep = nextStep,
        stepElapsedSeconds = 0,
        stepRemainingSeconds = firstStep.durationSeconds,
        stepTargetWaterGrams = firstStep.targetWaterGrams,
        cumulativeTargetWaterGrams = firstStep.cumulativeMassGrams,
        pouredInStepGrams = 0.0,
        remainingToCumulativeTargetGrams = firstStep.cumulativeMassGrams,
        proximityState = ProximityState.NORMAL
    )
    return BrewSessionState(
        sessionId = UUID.randomUUID().toString(),
        recipe = activeRecipe,
        status = BrewExecutionStatus.READY_ARMED,
        totalElapsedSeconds = 0,
        currentWeightGrams = 0.0,
        currentFlowRateGps = 0.0,
        stepProgress = progress,
        liveSamples = emptyList(),
        autoStartArmed = true,
        scaleDevice = scaleDevice,
        scaleConnectionState = scaleConnectionState
    )
}

private fun CoffeeViewModel.createDefaultSession(targetRecipe: Recipe? = null): BrewSessionState {
    val recipe = resolveActiveRecipe(targetRecipe)
    return createDefaultBrewSession(recipe)
}

fun CoffeeViewModel.initBrewSession(targetRecipe: Recipe? = null) {
    val session = createDefaultSession(targetRecipe)
    prevStepIndex = 0
    lowFlowStartMonoMs = 0L
    mutableState.update {
        it.copy(
            brewSession = session,
            timerSeconds = 0,
            timerRunning = false,
            brewCurveRmse = null
        )
    }
}

fun CoffeeViewModel.startBrewSession() {
    try {
        if (mutableState.value.brewSession == null) {
            initBrewSession()
        }
        val session = mutableState.value.brewSession ?: return
        timerOrigin = SystemClock.elapsedRealtime() - mutableState.value.timerSeconds * MILLIS_PER_SECOND
        mutableState.update {
            it.copy(
                timerRunning = true,
                brewSession = session.copy(status = BrewExecutionStatus.RUNNING)
            )
        }
        timer?.cancel()
        timer = viewModelScope.launch {
            while (true) {
                delay(TIMER_REFRESH_MS)
                val now = SystemClock.elapsedRealtime()
                val elapsed = ((now - timerOrigin) / MILLIS_PER_SECOND).toInt()
                updateSessionTick(elapsed)
            }
        }
    } catch (e: Exception) {
        if (e is kotlinx.coroutines.CancellationException) throw e
        diagnosticBuffer.log(LogLevel.ERROR, "TIMER", "Failed to start brew session: ${e.message}")
        mutableState.update { it.copy(notice = "Error starting session: ${e.message}") }
    }
}

fun CoffeeViewModel.pauseBrewSession() {
    timer?.cancel()
    mutableState.update {
        it.copy(
            timerRunning = false,
            brewSession = it.brewSession?.copy(status = BrewExecutionStatus.PAUSED)
        )
    }
}

fun CoffeeViewModel.resumeBrewSession() {
    startBrewSession()
}

fun CoffeeViewModel.skipBrewStep() {
    val session = mutableState.value.brewSession ?: return
    val steps = session.recipe.brewSteps
    val currentIdx = session.stepProgress?.stepIndex ?: 0
    if (currentIdx + 1 < steps.size) {
        val nextIdx = currentIdx + 1
        var accumulatedTime = 0
        for (i in 0 until nextIdx) {
            accumulatedTime += steps[i].durationSeconds
        }
        timerOrigin = SystemClock.elapsedRealtime() - accumulatedTime * MILLIS_PER_SECOND
        updateSessionTick(accumulatedTime)
    } else {
        finishBrewSession()
    }
}

fun CoffeeViewModel.restartBrewStep() {
    val session = mutableState.value.brewSession ?: return
    val steps = session.recipe.brewSteps
    val currentIdx = session.stepProgress?.stepIndex ?: 0
    var stepStartTime = 0
    for (i in 0 until currentIdx) {
        stepStartTime += steps[i].durationSeconds
    }
    timerOrigin = SystemClock.elapsedRealtime() - stepStartTime * MILLIS_PER_SECOND
    updateSessionTick(stepStartTime)
}

fun CoffeeViewModel.resetBrewSession() {
    timer?.cancel()
    mutableState.update {
        it.copy(
            timerSeconds = 0,
            timerRunning = false,
            brewSession = it.brewSession?.copy(
                status = BrewExecutionStatus.READY_ARMED,
                totalElapsedSeconds = 0,
                currentWeightGrams = 0.0,
                currentFlowRateGps = 0.0,
                liveSamples = emptyList()
            )
        )
    }
}

fun CoffeeViewModel.finishBrewSession() {
    timer?.cancel()
    val session = mutableState.value.brewSession ?: return
    val elapsed = mutableState.value.timerSeconds
    viewModelScope.launch {
        runCatching {
            if (graph.repository.findRecipeById(session.recipe.id) == null) {
                graph.repository.saveRecipe(session.recipe)
            }
        }
    }

    val idealPoints = BrewFilterEngine.generateIdealCurvePoints(session.recipe.brewSteps)
    val actualPoints = session.liveSamples.map {
        Pair(((it.timestampMonoMs - session.liveSamples.first().timestampMonoMs) / 1000).toInt(), it.filteredWeightGrams)
    }
    val rmse = if (scaleDriver !is SimulatedBleScaleDriver && actualPoints.isNotEmpty()) {
        BrewFilterEngine.calculateCurveRmse(actualPoints, idealPoints)
    } else {
        null
    }
    val journalEntry = BrewCompletionRecordFactory.fromTimerCompletion(
        recipe = session.recipe,
        elapsedSeconds = elapsed,
        beanName = mutableState.value.beans.find { it.id == session.recipe.beanId }?.name
    )

    mutableState.update {
        val updatedJournal = listOf(journalEntry) + it.journalEntries.filter { j -> j.id != journalEntry.id }
        it.copy(
            timerRunning = false,
            brewCurveRmse = rmse,
            brewSession = session.copy(status = BrewExecutionStatus.COMPLETED),
            journalEntries = updatedJournal,
            notice = "Timer completed. Recipe and elapsed time were logged; measurements and sensory notes remain blank."
        )
    }
    refreshStreakAndStats()
}

private fun CoffeeViewModel.updateSessionTick(elapsedSeconds: Int) {
    val currentSession = mutableState.value.brewSession ?: return
    val steps = currentSession.recipe.brewSteps
    val totalDuration = steps.sumOf { it.durationSeconds }

    if (elapsedSeconds >= totalDuration && currentSession.status == BrewExecutionStatus.RUNNING) {
        finishBrewSession()
        return
    }

    var stepIdx = 0
    var accTime = 0
    for (i in steps.indices) {
        val step = steps[i]
        if (elapsedSeconds < accTime + step.durationSeconds || i == steps.lastIndex) {
            stepIdx = i
            break
        }
        accTime += step.durationSeconds
    }

    val step = steps[stepIdx]
    val stepElapsed = (elapsedSeconds - accTime).coerceAtLeast(0)
    val stepRemaining = (step.durationSeconds - stepElapsed).coerceAtLeast(0)
    val nextStep = if (stepIdx + 1 < steps.size) steps[stepIdx + 1] else null

    var haptic: HapticCueType? = null
    var audio: AudioCueType? = null

    if (stepIdx != prevStepIndex) {
        prevStepIndex = stepIdx
        haptic = HapticCueType.STEP_TRANSITION
        audio = AudioCueType.CHIME
    } else if (stepRemaining in 1..3) {
        haptic = HapticCueType.COUNTDOWN_WARNING
        audio = AudioCueType.TICK
    }

    val proximity = BrewFilterEngine.calculateProximity(currentSession.currentWeightGrams, step.cumulativeMassGrams)
    if (proximity == ProximityState.TARGET_MATCHED && currentSession.stepProgress?.proximityState != ProximityState.TARGET_MATCHED) {
        haptic = HapticCueType.TARGET_MATCHED
    }

    val progress = BrewStepProgress(
        stepIndex = stepIdx,
        totalSteps = steps.size,
        currentStep = step,
        nextStep = nextStep,
        stepElapsedSeconds = stepElapsed,
        stepRemainingSeconds = stepRemaining,
        stepTargetWaterGrams = step.targetWaterGrams,
        cumulativeTargetWaterGrams = step.cumulativeMassGrams,
        pouredInStepGrams = currentSession.currentWeightGrams,
        remainingToCumulativeTargetGrams = (step.cumulativeMassGrams - currentSession.currentWeightGrams).coerceAtLeast(0.0),
        proximityState = proximity
    )

    mutableState.update {
        it.copy(
            timerSeconds = elapsedSeconds,
            activeHapticCue = haptic ?: it.activeHapticCue,
            activeAudioCue = audio ?: it.activeAudioCue,
            brewSession = currentSession.copy(
                totalElapsedSeconds = elapsedSeconds,
                stepProgress = progress
            )
        )
    }
}

fun CoffeeViewModel.connectMockScale() {
    val simDriver = SimulatedBleScaleDriver()
    scaleDriver = simDriver
    mutableState.update { it.copy(isScaleScanning = true) }
    viewModelScope.launch {
        try {
            simDriver.connect()
            mutableState.update {
                it.copy(
                    isScaleScanning = false,
                    brewSession = (it.brewSession ?: createDefaultSession()).copy(
                        scaleDevice = simDriver.device,
                        scaleConnectionState = ScaleConnectionState.READY
                    ),
                    notice = "Connected to ${simDriver.device.name}"
                )
            }
            scaleJob?.cancel()
            scaleJob = launch {
                simDriver.weightStream.collect { sample ->
                    onScaleSampleReceived(sample)
                }
            }
            updateDegradationState(ble = ModuleStatus.AVAILABLE)
            diagnosticBuffer.log(LogLevel.INFO, "BLE", "Connected to ${simDriver.device.name}")
        } catch (e: Exception) {
            if (e is kotlinx.coroutines.CancellationException) throw e
            mutableState.update {
                it.copy(
                    isScaleScanning = false,
                    notice = "Scale connection failed: ${e.message}"
                )
            }
            diagnosticBuffer.log(LogLevel.ERROR, "BLE", "Scale connection error: ${e.message}")
        }
    }
}

fun CoffeeViewModel.disconnectScale() {
    viewModelScope.launch {
        scaleJob?.cancel()
        scaleDriver?.disconnect()
        scaleDriver = null
        updateDegradationState(ble = ModuleStatus.UNAVAILABLE_MISSING)
        diagnosticBuffer.log(LogLevel.INFO, "BLE", "Scale disconnected")
        mutableState.update {
            it.copy(
                brewSession = it.brewSession?.copy(
                    scaleDevice = null,
                    scaleConnectionState = ScaleConnectionState.DISCONNECTED
                )
            )
        }
    }
}

fun CoffeeViewModel.tareScale() {
    viewModelScope.launch {
        try {
            scaleDriver?.tare()
            mutableState.update {
                it.copy(
                    brewSession = (it.brewSession ?: createDefaultSession()).copy(
                        currentWeightGrams = 0.0,
                        currentFlowRateGps = 0.0
                    )
                )
            }
        } catch (e: Exception) {
            if (e is kotlinx.coroutines.CancellationException) throw e
            diagnosticBuffer.log(LogLevel.ERROR, "SCALE", "Tare error: ${e.message}")
        }
    }
}

fun CoffeeViewModel.feedSimulatedWeight(grams: Double) {
    (scaleDriver as? SimulatedBleScaleDriver)?.feedWeight(grams)
}

private fun CoffeeViewModel.onScaleSampleReceived(sample: ScaleWeightSample) {
    val currentSession = mutableState.value.brewSession ?: return

    // Auto-start check
    if (currentSession.status == BrewExecutionStatus.READY_ARMED && currentSession.autoStartArmed) {
        if (sample.rawWeightGrams >= AUTO_START_WEIGHT_THRESHOLD_GRAMS) {
            startBrewSession()
        }
    }

    val updatedSamples = (currentSession.liveSamples + sample).takeLast(2000)
    val currentStep = currentSession.stepProgress?.currentStep
    val targetWater = currentStep?.cumulativeMassGrams ?: currentSession.recipe.waterGrams
    val proximity = BrewFilterEngine.calculateProximity(sample.filteredWeightGrams, targetWater)

    // Espresso auto-stop check (< 0.20 g/s sustained for >= 1.5s after 5g yield)
    var autoStopped = false
    if (currentSession.status == BrewExecutionStatus.RUNNING &&
        currentSession.recipe.brewMethod.contains("Espresso", ignoreCase = true) &&
        sample.filteredWeightGrams >= ESPRESSO_AUTO_STOP_MINIMUM_YIELD_GRAMS &&
        sample.flowRateGramsPerSec < ESPRESSO_AUTO_STOP_FLOW_RATE_GRAMS_PER_SECOND
    ) {
        if (lowFlowStartMonoMs == 0L) {
            lowFlowStartMonoMs = sample.timestampMonoMs
        } else if (sample.timestampMonoMs - lowFlowStartMonoMs >= ESPRESSO_AUTO_STOP_SUSTAINED_DURATION_MS) {
            autoStopped = true
            finishBrewSession()
        }
    } else {
        lowFlowStartMonoMs = 0L
    }

    if (!autoStopped) {
        mutableState.update {
            it.copy(
                brewSession = currentSession.copy(
                    currentWeightGrams = sample.filteredWeightGrams,
                    currentFlowRateGps = sample.flowRateGramsPerSec,
                    liveSamples = updatedSamples,
                    stepProgress = currentSession.stepProgress?.copy(
                        pouredInStepGrams = sample.filteredWeightGrams,
                        remainingToCumulativeTargetGrams = (targetWater - sample.filteredWeightGrams).coerceAtLeast(0.0),
                        proximityState = proximity
                    )
                )
            )
        }
    }
}

fun CoffeeViewModel.toggleKeepScreenOn() {
    mutableState.update {
        it.copy(brewSession = it.brewSession?.copy(keepScreenOn = !(it.brewSession?.keepScreenOn ?: true)))
    }
}

fun CoffeeViewModel.toggleSound() {
    mutableState.update {
        it.copy(brewSession = it.brewSession?.copy(audioEnabled = !(it.brewSession?.audioEnabled ?: true)))
    }
}

fun CoffeeViewModel.toggleHaptics() {
    mutableState.update {
        it.copy(brewSession = it.brewSession?.copy(hapticsEnabled = !(it.brewSession?.hapticsEnabled ?: true)))
    }
}

fun CoffeeViewModel.clearActiveCues() {
    mutableState.update { it.copy(activeHapticCue = null, activeAudioCue = null) }
}

fun CoffeeViewModel.exportBrewSessionSnapshot(): BrewSessionSnapshot? {
    val session = mutableState.value.brewSession ?: return null
    return BrewSessionSnapshot(
        sessionId = session.sessionId,
        recipeId = session.recipe.id,
        status = session.status,
        currentStepIndex = session.stepProgress?.stepIndex ?: 0,
        accumulatedElapsedSeconds = mutableState.value.timerSeconds,
        lastWeightGrams = session.currentWeightGrams,
        sampleCount = session.liveSamples.size
    )
}

fun CoffeeViewModel.restoreBrewSession(snapshot: BrewSessionSnapshot) {
    val recipe = mutableState.value.recipes.find { it.id == snapshot.recipeId }
        ?: resolveActiveRecipe()
    initBrewSession(recipe)
    mutableState.update {
        it.copy(
            timerSeconds = snapshot.accumulatedElapsedSeconds,
            brewSession = it.brewSession?.copy(
                sessionId = snapshot.sessionId,
                status = snapshot.status,
                currentWeightGrams = snapshot.lastWeightGrams
            )
        )
    }
}

fun CoffeeViewModel.toggleTimer() {
    try {
        if (mutableState.value.brewSession == null) {
            initBrewSession()
        }
        if (mutableState.value.timerRunning) {
            pauseBrewSession()
        } else {
            startBrewSession()
        }
    } catch (e: Exception) {
        if (e is kotlinx.coroutines.CancellationException) throw e
        diagnosticBuffer.log(LogLevel.ERROR, "TIMER", "Failed to toggle timer: ${e.message}")
        mutableState.update { it.copy(notice = "Error toggling timer: ${e.message}") }
    }
}

fun CoffeeViewModel.resetTimer() {
    try {
        resetBrewSession()
    } catch (e: Exception) {
        if (e is kotlinx.coroutines.CancellationException) throw e
        diagnosticBuffer.log(LogLevel.ERROR, "TIMER", "Failed to reset timer: ${e.message}")
    }
}
