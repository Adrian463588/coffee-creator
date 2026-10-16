package com.coffeecreator.app.core.ble

private const val DEFAULT_SCALE_RSSI = -60

/**
 * Discrete scale sample containing raw and filtered mass readings,
 * flow rate derivative in g/s, and physical artifact flags.
 */
data class ScaleWeightSample(
    val timestampMonoMs: Long,
    val rawWeightGrams: Double,
    val filteredWeightGrams: Double,
    val flowRateGramsPerSec: Double,
    val isArtifact: Boolean = false,
    val discontinuity: DiscontinuityType? = null
)

enum class DiscontinuityType {
    VESSEL_REMOVAL,
    AGITATION_ARTIFACT,
    SCALE_DISCONNECT,
    TARE_STEP
}

enum class ScaleConnectionState {
    DISCONNECTED,
    SCANNING,
    CONNECTING,
    CONNECTED,
    READY,
    ERROR
}

enum class ScaleProtocolType {
    GENERIC_GATT,
    ACAIA,
    FELICITA,
    DECENT,
    TIMEMORE,
    SIMULATED
}

data class BleScaleDevice(
    val id: String,
    val name: String,
    val macAddress: String,
    val protocolType: ScaleProtocolType = ScaleProtocolType.GENERIC_GATT,
    val rssi: Int = DEFAULT_SCALE_RSSI,
    val batteryPercent: Int? = null
)
