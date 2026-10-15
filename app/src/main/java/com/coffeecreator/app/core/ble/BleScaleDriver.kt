package com.coffeecreator.app.core.ble

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/**
 * Universal hardware driver interface for Bluetooth smart scales.
 */
interface BleScaleDriver {
    val device: BleScaleDevice
    val connectionState: StateFlow<ScaleConnectionState>
    val weightStream: Flow<ScaleWeightSample>

    suspend fun connect(): Result<Unit>
    suspend fun disconnect()
    suspend fun tare(): Result<Unit>
}
