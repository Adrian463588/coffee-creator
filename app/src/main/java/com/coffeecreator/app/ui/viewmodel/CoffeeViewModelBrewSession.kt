package com.coffeecreator.app.ui.viewmodel

import androidx.lifecycle.viewModelScope
import com.coffeecreator.app.core.ble.ScaleConnectionState
import com.coffeecreator.app.core.brew.BrewExecutionStatus
import com.coffeecreator.app.core.brew.BrewSessionSnapshot
import com.coffeecreator.app.core.brew.BrewSessionState
import com.coffeecreator.app.core.brew.BrewTimerEngine
import com.coffeecreator.app.core.brew.BrewTimerPreferences
import com.coffeecreator.app.core.brew.BrewTimerRuntimeState
import com.coffeecreator.app.core.engine.DomainValidation
import com.coffeecreator.app.core.journal.BrewCompletionRecordFactory
import com.coffeecreator.app.core.model.BrewStep
import com.coffeecreator.app.core.model.Ingredient
import com.coffeecreator.app.core.model.Recipe
import com.coffeecreator.app.core.reliability.LogLevel
import com.coffeecreator.app.core.reliability.ModuleStatus
import java.util.UUID
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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
        DomainValidation.validateRecipe(targetRecipe, ingredients)
        return targetRecipe
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
            val safeDraft = draft.copy(
                fields = mergedFields + ("name" to if (rawName.isBlank()) "Draft $effectiveMethod" else rawName)
            )
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
    return savedRecipe ?: defaultFallbackRecipe()
}

internal fun CoffeeViewModel.resolveActiveRecipe(targetRecipe: Recipe? = null): Recipe = resolveActiveRecipe(
    targetRecipe = targetRecipe,
    draft = mutableState.value.draft,
    savedRecipes = mutableState.value.recipes,
    ingredients = mutableState.value.ingredients
)

internal fun createDefaultBrewSession(
    recipe: Recipe,
    scaleDevice: com.coffeecreator.app.core.ble.BleScaleDevice? = null,
    scaleConnectionState: ScaleConnectionState = ScaleConnectionState.DISCONNECTED
): BrewSessionState = BrewTimerEngine.prepare(recipe, BrewTimerPreferences(), UUID.randomUUID().toString(), 0L, -1).session.copy(
    scaleDevice = scaleDevice,
    scaleConnectionState = scaleConnectionState
)

/** False always preserves the active brew and source recipe; an explicit invalid recipe never falls back. */
fun CoffeeViewModel.initBrewSession(targetRecipe: Recipe? = null): Boolean {
    return try {
        val recipe = resolveActiveRecipe(targetRecipe)
        val prepared = graph.brewTimer.prepare(recipe)
        syncBrewTimerState(graph.brewTimer.state.value)
        prepared
    } catch (boundaryFailure: IllegalArgumentException) {
        mutableState.update { it.copy(notice = "Cannot prepare this recipe: ${boundaryFailure.message}. Existing brew retained.") }
        false
    }
}

internal fun CoffeeViewModel.observeBrewTimer() {
    graph.brewTimer.acknowledgeCompletions(mutableState.value.journalEntries.map { it.id }.toSet())
    viewModelScope.launch {
        graph.brewTimer.state.collect { timerState ->
            syncBrewTimerState(timerState)
            for (completed in timerState.pendingCompletions) {
                val journalId = "timer:${completed.sessionId}"
                if (mutableState.value.journalEntries.none { it.id == journalId }) {
                    saveTimerCompletion(completed, journalId)
                }
            }
        }
    }
}

private fun CoffeeViewModel.saveTimerCompletion(session: BrewSessionState, journalId: String) {
    if (!pendingTimerJournalWrites.add(journalId)) return
    viewModelScope.launch {
        try {
            if (graph.repository.findRecipeById(session.recipe.id) == null) graph.repository.saveRecipe(session.recipe)
            val entry = BrewCompletionRecordFactory.fromTimerCompletion(
                recipe = session.recipe,
                elapsedSeconds = session.totalElapsedSeconds,
                beanName = mutableState.value.beans.find { it.id == session.recipe.beanId }?.name
            ).let { original ->
                val readings = session.manualWeighIns.joinToString("\n") {
                    "Manual scale reading (not measured beverage mass): ${it.weightGrams} g at ${it.elapsedSeconds} s."
                }
                original.copy(
                    id = journalId,
                    generalNotes = listOf(original.generalNotes, readings).filter(String::isNotBlank).joinToString("\n")
                )
            }
            mutableState.update { current ->
                current.copy(
                    journalEntries = listOf(entry) + current.journalEntries.filterNot { it.id == journalId },
                    notice = "Brew time saved to Journal. Targets and manual scale readings are separate from beverage mass and TDS."
                )
            }
            refreshStreakAndStats()
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (boundaryFailure: Exception) {
            diagnosticBuffer.log(LogLevel.ERROR, "TIMER", "Completed brew is retained for retry.", boundaryFailure)
            mutableState.update {
                it.copy(
                    notice = "Completed brew is retained for retry. Journal save failed: ${boundaryFailure.message}"
                )
            }
        } finally {
            pendingTimerJournalWrites.remove(journalId)
        }
    }
}

private fun CoffeeViewModel.syncBrewTimerState(timerState: BrewTimerRuntimeState) {
    mutableState.update { current ->
        current.copy(
            brewSession = timerState.session,
            timerSeconds = timerState.session?.totalElapsedSeconds ?: 0,
            timerRunning = timerState.session?.status == BrewExecutionStatus.RUNNING,
            notice = timerState.recoveryNotice ?: current.notice
        )
    }
}

fun CoffeeViewModel.startBrewSession() {
    if (graph.brewTimer.state.value.session == null && !initBrewSession()) return
    graph.brewTimer.start()
    syncBrewTimerState(graph.brewTimer.state.value)
}

fun CoffeeViewModel.pauseBrewSession() {
    graph.brewTimer.pause()
    syncBrewTimerState(graph.brewTimer.state.value)
}

fun CoffeeViewModel.resumeBrewSession() = startBrewSession()

fun CoffeeViewModel.skipBrewStep() {
    graph.brewTimer.skipStep()
    syncBrewTimerState(graph.brewTimer.state.value)
}

fun CoffeeViewModel.restartBrewStep() {
    graph.brewTimer.restartStep()
    syncBrewTimerState(graph.brewTimer.state.value)
}

fun CoffeeViewModel.resetBrewSession() {
    graph.brewTimer.reset()
    syncBrewTimerState(graph.brewTimer.state.value)
}

fun CoffeeViewModel.finishBrewSession() {
    graph.brewTimer.finish()
    syncBrewTimerState(graph.brewTimer.state.value)
}

/** Kept as a compatibility action. No simulated driver is ever represented as connected user hardware. */
fun CoffeeViewModel.connectMockScale() {
    updateDegradationState(ble = ModuleStatus.UNAVAILABLE_MISSING)
    mutableState.update {
        it.copy(isScaleScanning = false, notice = "No compatible scale driver is installed. Use the manual timer and enter scale readings.")
    }
}

fun CoffeeViewModel.disconnectScale() {
    viewModelScope.launch {
        scaleJob?.cancel()
        scaleDriver?.disconnect()
        scaleDriver = null
        updateDegradationState(ble = ModuleStatus.UNAVAILABLE_MISSING)
        mutableState.update { it.copy(isScaleScanning = false, notice = "Manual brewing remains available without a connected scale.") }
    }
}

fun CoffeeViewModel.tareScale() {
    if (scaleDriver == null) {
        mutableState.update { it.copy(notice = "Tare your physical scale manually, then enter its displayed reading.") }
    } else {
        viewModelScope.launch { scaleDriver?.tare() }
    }
}

fun CoffeeViewModel.feedSimulatedWeight(grams: Double) {
    require(grams.isFinite()) { "Simulation input must be finite." }
    mutableState.update { it.copy(notice = "Scale simulation is available in tests only. Enter a measured reading manually.") }
}

fun CoffeeViewModel.recordManualWeight(weightGrams: Double): Boolean {
    if (graph.brewTimer.state.value.session == null && !initBrewSession()) return false
    val accepted = graph.brewTimer.recordManualWeight(weightGrams)
    syncBrewTimerState(graph.brewTimer.state.value)
    return accepted
}

private fun CoffeeViewModel.updateBrewTimerPreferences(transform: (BrewTimerPreferences) -> BrewTimerPreferences) {
    if (graph.brewTimer.state.value.session == null && !initBrewSession()) return
    graph.brewTimer.updatePreferences(transform)
    syncBrewTimerState(graph.brewTimer.state.value)
}

fun CoffeeViewModel.toggleKeepScreenOn() = updateBrewTimerPreferences { it.copy(keepScreenOn = !it.keepScreenOn) }
fun CoffeeViewModel.toggleSound() = updateBrewTimerPreferences { it.copy(audioEnabled = !it.audioEnabled) }
fun CoffeeViewModel.toggleHaptics() = updateBrewTimerPreferences { it.copy(hapticsEnabled = !it.hapticsEnabled) }
fun CoffeeViewModel.toggleBrewNotifications() = updateBrewTimerPreferences { it.copy(notificationEnabled = !it.notificationEnabled) }

fun CoffeeViewModel.clearActiveCues() {
    mutableState.update { it.copy(activeHapticCue = null, activeAudioCue = null) }
}

fun CoffeeViewModel.exportBrewSessionSnapshot(): BrewSessionSnapshot? {
    val session = graph.brewTimer.state.value.session ?: return null
    return BrewSessionSnapshot(
        sessionId = session.sessionId,
        recipeId = session.recipe.id,
        status = session.status,
        currentStepIndex = session.stepProgress?.stepIndex ?: 0,
        accumulatedElapsedSeconds = session.totalElapsedSeconds,
        lastWeightGrams = session.currentWeightGrams,
        sampleCount = session.liveSamples.size
    )
}

fun CoffeeViewModel.restoreBrewSession(snapshot: BrewSessionSnapshot) {
    val recipe = mutableState.value.recipes.find { it.id == snapshot.recipeId }
    if (recipe == null) {
        mutableState.update {
            it.copy(
                notice = "The saved recipe is unavailable. Existing brew retained; import its backup before recovery."
            )
        }
        return
    }
    graph.brewTimer.restoreLegacySnapshot(snapshot, recipe)
    syncBrewTimerState(graph.brewTimer.state.value)
}

fun CoffeeViewModel.toggleTimer() {
    if (graph.brewTimer.state.value.session?.status == BrewExecutionStatus.RUNNING) pauseBrewSession() else startBrewSession()
}

fun CoffeeViewModel.resetTimer() = resetBrewSession()
