package com.coffeecreator.app.core.brew

import com.coffeecreator.app.core.ble.BleScaleDevice
import com.coffeecreator.app.core.ble.ScaleConnectionState
import com.coffeecreator.app.core.ble.ScaleWeightSample
import com.coffeecreator.app.core.model.BrewStep
import com.coffeecreator.app.core.model.Recipe

enum class BrewExecutionStatus {
    IDLE,
    READY_ARMED,
    RUNNING,
    PAUSED,
    COMPLETED,
    ABORTED
}

enum class ProximityState {
    NORMAL,
    APPROACHING_10G,
    IMMINENT_2G,
    TARGET_MATCHED,
    OVERSHOOT
}

enum class HapticCueType {
    STEP_TRANSITION,
    COUNTDOWN_WARNING,
    TARGET_MATCHED,
    FLOW_STOP,
    TAP
}

enum class AudioCueType {
    TICK,
    CHIME,
    ALERT
}

data class BrewStepProgress(
    val stepIndex: Int,
    val totalSteps: Int,
    val currentStep: BrewStep,
    val nextStep: BrewStep?,
    val stepElapsedSeconds: Int,
    val stepRemainingSeconds: Int,
    val stepTargetWaterGrams: Double,
    val cumulativeTargetWaterGrams: Double,
    val pouredInStepGrams: Double,
    val remainingToCumulativeTargetGrams: Double,
    val proximityState: ProximityState
)

data class BrewSessionState(
    val sessionId: String,
    val recipe: Recipe,
    val status: BrewExecutionStatus = BrewExecutionStatus.IDLE,
    val totalElapsedSeconds: Int = 0,
    val currentWeightGrams: Double = 0.0,
    val currentFlowRateGps: Double = 0.0,
    val stepProgress: BrewStepProgress? = null,
    val liveSamples: List<ScaleWeightSample> = emptyList(),
    val scaleDevice: BleScaleDevice? = null,
    val scaleConnectionState: ScaleConnectionState = ScaleConnectionState.DISCONNECTED,
    val autoStartArmed: Boolean = false,
    val autoStopDetected: Boolean = false,
    val keepScreenOn: Boolean = false,
    val audioEnabled: Boolean = true,
    val hapticsEnabled: Boolean = true,
    val notificationEnabled: Boolean = true,
    val recoveryNotice: String? = null,
    val manualWeightRecorded: Boolean = false,
    val manualWeighIns: List<ManualBrewWeighIn> = emptyList()
)

/** Human-entered scale readings; these are not inferred beverage mass or a live flow measurement. */
data class ManualBrewWeighIn(val elapsedSeconds: Int, val weightGrams: Double)

data class BrewSessionSnapshot(
    val sessionId: String,
    val recipeId: String,
    val status: BrewExecutionStatus,
    val currentStepIndex: Int,
    val accumulatedElapsedSeconds: Int,
    val lastWeightGrams: Double,
    val sampleCount: Int
)

data class BrewNotificationPayload(
    val title: String,
    val subText: String,
    val body: String,
    val elapsedSeconds: Int,
    val isRunning: Boolean,
    val canPause: Boolean,
    val canSkip: Boolean
)
