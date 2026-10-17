package com.coffeecreator.app.core.ble

import com.coffeecreator.app.core.brew.BrewFilterEngine
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

private const val SIMULATED_CONNECTION_STEP_DELAY_MS = 50L

/**
 * Deterministic simulated scale driver for offline demonstration,
 * hardware-less testing, and JVM test suite execution.
 */
class SimulatedBleScaleDriver(
    override val device: BleScaleDevice = BleScaleDevice(
        id = "sim-scale-01",
        name = "Coffee Creator Smart Scale (Simulated)",
        macAddress = "AA:BB:CC:DD:EE:FF",
        protocolType = ScaleProtocolType.SIMULATED,
        rssi = -42,
        batteryPercent = 95
    )
) : BleScaleDriver {

    private val _connectionState = MutableStateFlow(ScaleConnectionState.DISCONNECTED)
    override val connectionState: StateFlow<ScaleConnectionState> = _connectionState.asStateFlow()

    private val _weightStream = MutableSharedFlow<ScaleWeightSample>(replay = 1, extraBufferCapacity = 64)
    override val weightStream: Flow<ScaleWeightSample> = _weightStream.asSharedFlow()

    private var tareOffsetGrams = 0.0
    private var currentNetGrams = 0.0
    private var prevFilteredGrams = 0.0
    private var lastSampleMonoMs = System.currentTimeMillis()
    private val recentSamples = ArrayDeque<Double>(10)
    private val slidingRegressionWindow = ArrayDeque<Pair<Long, Double>>(10)
    private var streamingJob: Job? = null

    override suspend fun connect(): Result<Unit> = runCatching {
        _connectionState.value = ScaleConnectionState.CONNECTING
        delay(SIMULATED_CONNECTION_STEP_DELAY_MS)
        _connectionState.value = ScaleConnectionState.CONNECTED
        delay(SIMULATED_CONNECTION_STEP_DELAY_MS)
        _connectionState.value = ScaleConnectionState.READY
    }

    override suspend fun disconnect() {
        streamingJob?.cancel()
        _connectionState.value = ScaleConnectionState.DISCONNECTED
    }

    override suspend fun tare(): Result<Unit> = runCatching {
        tareOffsetGrams += currentNetGrams
        currentNetGrams = 0.0
        prevFilteredGrams = 0.0
        recentSamples.clear()
        slidingRegressionWindow.clear()
        emitCurrent(0.0, DiscontinuityType.TARE_STEP)
    }

    /**
     * Programmatically feed a weight update (e.g. from UI slider or test runner).
     */
    fun feedWeight(weightGrams: Double) {
        val now = System.currentTimeMillis()
        val dt = (now - lastSampleMonoMs).coerceAtLeast(1L)
        lastSampleMonoMs = now
        currentNetGrams = (weightGrams - tareOffsetGrams).coerceAtLeast(0.0)

        recentSamples.addLast(currentNetGrams)
        if (recentSamples.size > 8) recentSamples.removeFirst()

        slidingRegressionWindow.addLast(Pair(now, currentNetGrams))
        if (slidingRegressionWindow.size > 8) slidingRegressionWindow.removeFirst()

        val (filtered, discontinuity) = BrewFilterEngine.filterScaleSample(
            rawWeight = currentNetGrams,
            prevFiltered = prevFilteredGrams,
            timeDeltaMs = dt,
            recentSamples = recentSamples.toList()
        )
        prevFilteredGrams = filtered

        val flowRate = BrewFilterEngine.calculateRollingFlowRate(slidingRegressionWindow.toList())

        val sample = ScaleWeightSample(
            timestampMonoMs = now,
            rawWeightGrams = currentNetGrams,
            filteredWeightGrams = filtered,
            flowRateGramsPerSec = flowRate,
            isArtifact = discontinuity != null,
            discontinuity = discontinuity
        )
        _weightStream.tryEmit(sample)
    }

    private fun emitCurrent(weightGrams: Double, discontinuity: DiscontinuityType? = null) {
        val now = System.currentTimeMillis()
        val sample = ScaleWeightSample(
            timestampMonoMs = now,
            rawWeightGrams = weightGrams,
            filteredWeightGrams = weightGrams,
            flowRateGramsPerSec = 0.0,
            isArtifact = discontinuity != null,
            discontinuity = discontinuity
        )
        _weightStream.tryEmit(sample)
    }
}
