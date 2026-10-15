package com.coffeecreator.app.core.reliability

import java.math.BigDecimal
import java.math.RoundingMode
import java.security.MessageDigest
import com.coffeecreator.app.core.ai.personalization.ElevationUnit

// ============================================================================
// 1. Localization & Unit Conversion Models (S2-078)
// ============================================================================

enum class AppLanguage(val code: String, val displayName: String) {
    ENGLISH("en", "English"),
    INDONESIAN("id", "Bahasa Indonesia");

    companion object {
        fun fromCode(code: String): AppLanguage =
            entries.find { it.code.equals(code, ignoreCase = true) } ?: ENGLISH
    }
}

enum class TemperatureUnit(val symbol: String) {
    CELSIUS("°C"),
    FAHRENHEIT("°F")
}

enum class MassUnit(val symbol: String) {
    GRAMS("g"),
    OUNCES("oz")
}

enum class VolumeUnit(val symbol: String) {
    MILLILITERS("mL"),
    FLUID_OUNCES("fl oz")
}

data class UserUnitPreferences(
    val language: AppLanguage = AppLanguage.ENGLISH,
    val temperatureUnit: TemperatureUnit = TemperatureUnit.CELSIUS,
    val massUnit: MassUnit = MassUnit.GRAMS,
    val volumeUnit: VolumeUnit = VolumeUnit.MILLILITERS,
    val elevationUnit: ElevationUnit = ElevationUnit.METERS
)

object UnitConverter {
    private val OZ_TO_GRAMS = BigDecimal("28.349523125")
    private val FL_OZ_TO_ML = BigDecimal("29.5735295625")
    private val FT_TO_METERS = BigDecimal("0.3048")
    private val NINE = BigDecimal("9")
    private val FIVE = BigDecimal("5")
    private val THIRTY_TWO = BigDecimal("32")

    fun celsiusToFahrenheit(celsius: Double): Double {
        val c = BigDecimal.valueOf(celsius)
        return c.multiply(NINE).divide(FIVE, 6, RoundingMode.HALF_UP).add(THIRTY_TWO).toDouble()
    }

    fun fahrenheitToCelsius(fahrenheit: Double): Double {
        val f = BigDecimal.valueOf(fahrenheit)
        return f.subtract(THIRTY_TWO).multiply(FIVE).divide(NINE, 6, RoundingMode.HALF_UP).toDouble()
    }

    fun gramsToOunces(grams: Double): Double {
        require(grams >= 0.0) { "Mass must be non-negative" }
        return BigDecimal.valueOf(grams).divide(OZ_TO_GRAMS, 6, RoundingMode.HALF_UP).toDouble()
    }

    fun ouncesToGrams(ounces: Double): Double {
        require(ounces >= 0.0) { "Mass must be non-negative" }
        return BigDecimal.valueOf(ounces).multiply(OZ_TO_GRAMS).setScale(4, RoundingMode.HALF_UP).toDouble()
    }

    fun mlToFluidOunces(ml: Double): Double {
        require(ml >= 0.0) { "Volume must be non-negative" }
        return BigDecimal.valueOf(ml).divide(FL_OZ_TO_ML, 6, RoundingMode.HALF_UP).toDouble()
    }

    fun fluidOuncesToMl(flOz: Double): Double {
        require(flOz >= 0.0) { "Volume must be non-negative" }
        return BigDecimal.valueOf(flOz).multiply(FL_OZ_TO_ML).setScale(4, RoundingMode.HALF_UP).toDouble()
    }

    fun volumeToMassWithDensity(ml: Double, densityGramsPerMl: Double): Double {
        require(ml >= 0.0) { "Volume must be non-negative" }
        require(densityGramsPerMl > 0.0) { "Density must be strictly positive" }
        return BigDecimal.valueOf(ml).multiply(BigDecimal.valueOf(densityGramsPerMl))
            .setScale(4, RoundingMode.HALF_UP).toDouble()
    }

    fun metersToFeet(meters: Double): Double {
        return BigDecimal.valueOf(meters).divide(FT_TO_METERS, 4, RoundingMode.HALF_UP).toDouble()
    }

    fun feetToMeters(feet: Double): Double {
        return BigDecimal.valueOf(feet).multiply(FT_TO_METERS).setScale(2, RoundingMode.HALF_UP).toDouble()
    }

    fun altitudeBoilingPointCelsius(elevationMeters: Double): Double {
        val elevation = Math.max(0.0, elevationMeters)
        val reduction = elevation / 300.0
        return BigDecimal.valueOf(100.0 - reduction).setScale(2, RoundingMode.HALF_UP).toDouble()
    }
}

// ============================================================================
// 2. Circular Bounded Diagnostic Log Buffer (S2-080)
// ============================================================================

enum class LogLevel { DEBUG, INFO, WARN, ERROR }

data class DiagnosticLogEntry(
    val sequenceId: Long,
    val timestamp: Long,
    val level: LogLevel,
    val tag: String,
    val message: String,
    val exceptionSummary: String? = null
)

class DiagnosticLogBuffer(private val capacity: Int = 500) {
    private val buffer = arrayOfNulls<DiagnosticLogEntry>(capacity)
    private var writeCount: Long = 0L
    private val lock = Any()

    companion object {
        private val IPV4_REGEX = Regex("""\b\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}\b""")
        private val PATH_REGEX = Regex("""/data/user/\d+/[a-zA-Z0-9._-]+""")
        private val SECRET_REGEX = Regex("""(?i)(key|secret|token|password)=[^&\s]+""")

        fun scrub(input: String): String {
            return input
                .replace(IPV4_REGEX, "[REDACTED_IP]")
                .replace(PATH_REGEX, "[LOCAL_STORAGE]")
                .replace(SECRET_REGEX, "$1=[REDACTED]")
                .take(512)
        }
    }

    fun log(level: LogLevel, tag: String, rawMessage: String, error: Throwable? = null) {
        synchronized(lock) {
            val scrubbed = scrub(rawMessage)
            val exceptionSummary = error?.let { "${it.javaClass.simpleName}: ${scrub(it.message ?: "")}".take(256) }
            val entry = DiagnosticLogEntry(
                sequenceId = writeCount,
                timestamp = System.currentTimeMillis(),
                level = level,
                tag = tag.take(32),
                message = scrubbed,
                exceptionSummary = exceptionSummary
            )
            val index = (writeCount % capacity).toInt()
            buffer[index] = entry
            writeCount++
        }
    }

    fun snapshot(): List<DiagnosticLogEntry> {
        synchronized(lock) {
            val count = writeCount
            val size = count.coerceAtMost(capacity.toLong()).toInt()
            val result = ArrayList<DiagnosticLogEntry>(size)
            for (i in 0 until size) {
                val physicalIndex = ((count - size + i) % capacity).toInt()
                buffer[physicalIndex]?.let { result.add(it) }
            }
            return result
        }
    }

    fun clear() {
        synchronized(lock) {
            for (i in 0 until capacity) buffer[i] = null
            writeCount = 0L
        }
    }

    fun exportFormattedString(): String {
        val entries = snapshot()
        val sb = StringBuilder()
        sb.append("# Coffee Creator Diagnostic Log (Local Opt-in Export)\n")
        sb.append("Total Events: ${entries.size} (Capacity: $capacity)\n")
        sb.append("Generated At: ${System.currentTimeMillis()}\n")
        sb.append("Telemetry Status: ZERO_EXTERNAL_TELEMETRY (Offline On-Device Only)\n\n")
        entries.forEach { e ->
            sb.append("[#${e.sequenceId}] [${e.level}] [${e.tag}] ${e.message}")
            if (e.exceptionSummary != null) {
                sb.append(" -> Error: ${e.exceptionSummary}")
            }
            sb.append("\n")
        }
        return sb.toString()
    }
}

// ============================================================================
// 3. Graceful Degradation State Machine (S2-081)
// ============================================================================

enum class ModuleStatus {
    AVAILABLE,
    UNAVAILABLE_MISSING,
    UNAVAILABLE_ERROR,
    DISABLED_BY_USER
}

enum class ActiveStatusBadge(val labelEn: String, val labelId: String, val isDegraded: Boolean) {
    FULL_SYSTEM("All Systems Nominal", "Semua Sistem Aktif", false),
    MANUAL_TIMER("Manual Scale & Timer (BLE Offline)", "Timbangan & Timer Manual (BLE Mati)", true),
    MANUAL_OCR("Manual Metadata Entry (OCR Offline)", "Input Manual (OCR Kamera Nonaktif)", true),
    RULE_BASED_AI("Deterministic Science Rules (No Local LLM)", "Aturan Sains Deterministik (Tanpa Model AI)", true),
    OFFLINE_CORE("Offline Barista Workstation", "Stasiun Barista Offline", false)
}

data class SystemDegradationState(
    val llmStatus: ModuleStatus = ModuleStatus.UNAVAILABLE_MISSING,
    val ocrStatus: ModuleStatus = ModuleStatus.UNAVAILABLE_MISSING,
    val bleStatus: ModuleStatus = ModuleStatus.UNAVAILABLE_MISSING
) {
    val activeBadge: ActiveStatusBadge
        get() = when {
            llmStatus == ModuleStatus.AVAILABLE && ocrStatus == ModuleStatus.AVAILABLE && bleStatus == ModuleStatus.AVAILABLE ->
                ActiveStatusBadge.FULL_SYSTEM
            bleStatus != ModuleStatus.AVAILABLE && llmStatus == ModuleStatus.AVAILABLE ->
                ActiveStatusBadge.MANUAL_TIMER
            ocrStatus != ModuleStatus.AVAILABLE && llmStatus == ModuleStatus.AVAILABLE ->
                ActiveStatusBadge.MANUAL_OCR
            llmStatus != ModuleStatus.AVAILABLE && bleStatus == ModuleStatus.AVAILABLE ->
                ActiveStatusBadge.RULE_BASED_AI
            else ->
                ActiveStatusBadge.OFFLINE_CORE
        }
}

// ============================================================================
// 4. Backup & Restore Data Contract (S2-077)
// ============================================================================

data class FullBackupManifest(
    val formatVersion: Int = 1,
    val appVersion: String,
    val createdAt: Long,
    val recipeCount: Int,
    val journalCount: Int,
    val cuppingCount: Int,
    val payloadSha256: String
)

data class FullBackupPayload(
    val manifest: FullBackupManifest,
    val recipesJson: String,
    val beansJson: String,
    val journalJson: String,
    val cuppingsJson: String,
    val userPreferencesJson: String
) {
    fun verifyIntegrity(): Boolean {
        val rawConcat = "$recipesJson$beansJson$journalJson$cuppingsJson$userPreferencesJson"
        val computedHash = MessageDigest.getInstance("SHA-256")
            .digest(rawConcat.toByteArray(Charsets.UTF_8))
            .joinToString("") { "%02x".format(it) }
        return computedHash.equals(manifest.payloadSha256, ignoreCase = true)
    }
}

sealed class RestoreResult {
    data class Success(val importedRecipes: Int, val importedJournals: Int, val importedCuppings: Int) : RestoreResult()
    data class CorruptPayload(val reason: String) : RestoreResult()
    data class IncompatibleVersion(val foundVersion: Int, val supportedVersion: Int) : RestoreResult()
}

// ============================================================================
// 5. Offline Help & Error Recovery Guides (S2-078)
// ============================================================================

data class OfflineHelpTopic(
    val id: String,
    val titleEn: String,
    val titleId: String,
    val summaryEn: String,
    val summaryId: String,
    val recoveryStepsEn: List<String>,
    val recoveryStepsId: List<String>
)

object OfflineHelpCatalog {
    val topics = listOf(
        OfflineHelpTopic(
            id = "ble_disconnect",
            titleEn = "Bluetooth Scale Disconnection",
            titleId = "Koneksi Timbangan Bluetooth Terputus",
            summaryEn = "What to do if your smart scale disconnects mid-brew.",
            summaryId = "Langkah pemulihan saat koneksi timbangan pintar terputus saat menyeduh.",
            recoveryStepsEn = listOf(
                "App automatically engages Manual Timer fallback without pausing elapsed time.",
                "Verify Bluetooth and Location permissions are enabled in Android Settings.",
                "Ensure scale battery is charged and within 2 meters.",
                "Tap 'Reconnect Scale' in the Brew screen or enter final yield mass manually."
            ),
            recoveryStepsId = listOf(
                "Aplikasi otomatis beralih ke mode Timer Manual tanpa menghentikan waktu seduh.",
                "Pastikan izin Bluetooth dan Lokasi aktif di Pengaturan Android.",
                "Pastikan baterai timbangan mencukupi dan berjarak maksimal 2 meter.",
                "Tekan tombol 'Sambungkan Ulang' atau masukkan massa hasil seduh secara manual."
            )
        ),
        OfflineHelpTopic(
            id = "ocr_failure",
            titleEn = "Camera Bag Label Scan Unrecognized",
            titleId = "Pemindaian Label Kantong Kopi Gagal",
            summaryEn = "Troubleshooting unreadable or partial coffee bag text.",
            summaryId = "Panduan mengatasi label kantong kopi yang buram atau tidak terbaca.",
            recoveryStepsEn = listOf(
                "Ensure adequate ambient lighting and lay the coffee bag flat without glare.",
                "Hold phone steady 15-20 cm from the label text.",
                "Use the 'Review & Edit' draft card to manually correct roaster, origin, or variety fields.",
                "All bean records can be created 100% manually without using camera OCR."
            ),
            recoveryStepsId = listOf(
                "Pastikan pencahayaan cukup dan ratakan permukaan kantong kopi agar tidak silau.",
                "Pegang ponsel stabil berjarak 15-20 cm dari teks label.",
                "Gunakan kartu draf 'Tinjau & Ubah' untuk mengoreksi nama roaster, asal, atau varietas.",
                "Data biji kopi dapat ditambahkan secara manual tanpa memerlukan OCR kamera."
            )
        ),
        OfflineHelpTopic(
            id = "altitude_boiling",
            titleEn = "High-Altitude Brewing Thermodynamics",
            titleId = "Termodinamika Seduh di Ketinggian",
            summaryEn = "Water boils below 100°C at high elevation.",
            summaryId = "Titik didih air turun di bawah 100°C pada dataran tinggi.",
            recoveryStepsEn = listOf(
                "At higher elevations, lower atmospheric pressure lowers water's boiling point by ~1°C per 300 meters.",
                "Set your elevation in Settings or Chat to automatically clamp kettle temperature targets.",
                "Never attempt to boil water above the local boiling point; it causes excessive steaming and thermal inaccuracy."
            ),
            recoveryStepsId = listOf(
                "Pada dataran tinggi, tekanan atmosfer yang lebih rendah menurunkan titik didih air sebesar ~1°C per 300 meter.",
                "Atur ketinggian Anda di Pengaturan atau Chat untuk membatasi target suhu air seduh.",
                "Jangan mencoba memanaskan air di atas titik didih lokal karena menimbulkan uap berlebih."
            )
        ),
        OfflineHelpTopic(
            id = "saf_restore",
            titleEn = "Restoring Data After Reinstall",
            titleId = "Memulihkan Data Setelah Instal Ulang",
            summaryEn = "Recovering local models and records without data loss.",
            summaryId = "Memulihkan model lokal dan catatan tanpa kehilangan data.",
            recoveryStepsEn = listOf(
                "Uninstalling the app clears app-private storage and OS permissions.",
                "Export a full backup (.json) to your Documents folder before uninstalling.",
                "After reinstalling, grant access to your model directory to rediscover GGUF weights.",
                "Use 'Restore Full Backup' in Settings to restore all recipes, journal entries, and cuppings."
            ),
            recoveryStepsId = listOf(
                "Menghapus aplikasi akan menghapus penyimpanan privat aplikasi dan izin OS.",
                "Ekspor cadangan penuh (.json) ke folder Dokumen sebelum menghapus aplikasi.",
                "Setelah instal ulang, berikan akses ke folder model untuk mendeteksi kembali file GGUF.",
                "Gunakan 'Pulihkan Cadangan' di Pengaturan untuk memulihkan seluruh resep, jurnal, dan cupping."
            )
        )
    )
}
