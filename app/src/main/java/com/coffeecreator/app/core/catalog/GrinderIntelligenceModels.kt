package com.coffeecreator.app.core.catalog

import java.util.Locale

private const val SLOW_BREW_TIME_DELTA_THRESHOLD_SECONDS = 45
private const val FAST_BREW_TIME_DELTA_THRESHOLD_SECONDS = -40

// ============================================================================
// Sprint 03: Grinder Intelligence Domain Models & Catalog (GR-01 .. GR-12)
// Pure Kotlin/JVM, zero Android UI dependencies.
// ============================================================================

enum class GrinderType {
    HAND,
    ELECTRIC
}

enum class AdjustmentUnit {
    CLICK,
    ROTATION,
    DIAL_NUMBER,
    MARK,
    NOTCH,
    STEPLESS
}

enum class EvidenceStatus {
    USER_SUPPLIED_UNVERIFIED,
    MANUFACTURER_VERIFIED,
    THIRD_PARTY,
    USER_CALIBRATED,
    CONFLICTED
}

data class GrinderVariant(
    val variantId: String,
    val name: String,
    val burrType: String,
    val micronsPerClick: Double?,
    val notes: String = ""
)

data class GrinderModelRecord(
    val id: String,
    val brand: String,
    val modelName: String,
    val type: GrinderType,
    val adjustmentUnit: AdjustmentUnit,
    // e.g. "Putaran (External)", "Klik (Internal)"
    val adjustmentMechanism: String,
    val rawCoarseRange: String,
    val rawMediumRange: String,
    val rawFineRange: String,
    val normalizedCoarse: String = rawCoarseRange,
    val normalizedMedium: String = rawMediumRange,
    val normalizedFine: String = rawFineRange,
    val isEspressoCapable: Boolean = true,
    val evidenceStatus: EvidenceStatus = EvidenceStatus.USER_SUPPLIED_UNVERIFIED,
    val manufacturerSourceUrl: String? = null,
    val verifiedRangeMedium: String? = null,
    val variants: List<GrinderVariant> = emptyList(),
    val safeZeroInstructions: List<String> = emptyList(),
    val caveats: List<String> = emptyList()
)

data class GrinderSetting(
    val grinderModelId: String,
    val unit: AdjustmentUnit,
    val rawValue: String,
    val normalizedValue: String? = null,
    val zeroReferencePoint: String? = null,
    val calibrationProfileId: String? = null
)

data class UserGrinderProfile(
    val id: String,
    val grinderModelId: String,
    val customName: String,
    val selectedVariantId: String? = null,
    val zeroCalibrationOffset: Double = 0.0,
    // "NEW", "SEASONED", "WORN"
    val burrCondition: String = "SEASONED",
    val calibrationNotes: String = "",
    val calibratedAtEpochMs: Long = System.currentTimeMillis()
)

data class GrindGuidanceResult(
    val grinderModelId: String,
    val method: String,
    val recommendedRange: String,
    val evidenceStatus: EvidenceStatus,
    val manufacturerRange: String? = null,
    val isVerified: Boolean,
    val caveats: List<String> = emptyList()
)

data class GrinderCompatibilityResult(
    val isCompatible: Boolean,
    val warningMessage: String? = null
)

data class DialInAdvice(
    // e.g. "Adjust 1-2 clicks coarser"
    val proposedAction: String,
    val controlVariable: String = "Grind Size",
    val scientificReason: String,
    // Never make deterministic flavor promises.
    val isDeterministic: Boolean = false
)

data class GrindMeasurementEntry(
    val measuredAtEpochMs: Long = System.currentTimeMillis(),
    val panMicrons: Int? = null,
    val panMassGrams: Double? = null,
    val topMicrons: Int? = null,
    val topMassGrams: Double? = null,
    val measurementDevice: String = "Kruve Sifter",
    val notes: String = ""
)

object GrinderIntelligenceCatalog {

    val ALL_44_GRINDERS: List<GrinderModelRecord> = GrinderCatalogSeed.records

    fun searchGrinders(query: String, filterType: GrinderType? = null): List<GrinderModelRecord> {
        val q = query.trim().lowercase(Locale.ROOT)
        return ALL_44_GRINDERS.filter { record ->
            val matchesType = filterType == null || record.type == filterType
            val matchesQuery = q.isEmpty() ||
                record.modelName.lowercase(Locale.ROOT).contains(q) ||
                record.brand.lowercase(Locale.ROOT).contains(q) ||
                record.adjustmentMechanism.lowercase(Locale.ROOT).contains(q)
            matchesType && matchesQuery
        }
    }

    fun getGrinderById(id: String): GrinderModelRecord? {
        return ALL_44_GRINDERS.firstOrNull { it.id == id }
    }

    fun getStartingRange(grinderId: String, method: String): GrindGuidanceResult {
        val grinder = getGrinderById(grinderId)
            ?: return GrindGuidanceResult(
                grinderModelId = grinderId,
                method = method,
                recommendedRange = "Model not found",
                evidenceStatus = EvidenceStatus.USER_SUPPLIED_UNVERIFIED,
                isVerified = false,
                caveats = listOf("Unknown grinder model")
            )

        val m = method.lowercase(Locale.ROOT)
        val range = when {
            m.contains("espresso") || m.contains("fine") || m.contains("ristretto") -> grinder.normalizedFine
            m.contains("coarse") ||
                m.contains("cold brew") ||
                m.contains("french press") ||
                m.contains("tubruk") -> grinder.normalizedCoarse
            else -> grinder.normalizedMedium // Pour Over, V60, Kalita, Aeropress, Cupping default to medium
        }

        return GrindGuidanceResult(
            grinderModelId = grinder.id,
            method = method,
            recommendedRange = range,
            evidenceStatus = grinder.evidenceStatus,
            manufacturerRange = grinder.verifiedRangeMedium,
            isVerified = grinder.evidenceStatus == EvidenceStatus.MANUFACTURER_VERIFIED,
            caveats = grinder.caveats
        )
    }

    fun checkCompatibility(grinderId: String, method: String): GrinderCompatibilityResult {
        val grinder = getGrinderById(grinderId) ?: return GrinderCompatibilityResult(true)
        val m = method.lowercase(Locale.ROOT)
        if (m.contains("espresso") && !grinder.isEspressoCapable) {
            return GrinderCompatibilityResult(
                isCompatible = false,
                warningMessage = "${grinder.modelName} burr geometry is optimized for filter brewing and is not recommended for espresso."
            )
        }
        return GrinderCompatibilityResult(isCompatible = true)
    }

    fun validateSetting(grinderId: String, rawInput: String): Boolean {
        if (rawInput.isBlank()) return false
        val grinder = getGrinderById(grinderId) ?: return true
        val trimmed = rawInput.trim()
        return when (grinder.adjustmentUnit) {
            AdjustmentUnit.CLICK, AdjustmentUnit.NOTCH -> {
                val num = trimmed.filter { it.isDigit() }.toIntOrNull()
                num != null && num >= 0
            }
            AdjustmentUnit.ROTATION, AdjustmentUnit.DIAL_NUMBER, AdjustmentUnit.STEPLESS, AdjustmentUnit.MARK -> {
                val clean = trimmed.replace(Regex("[^0-9.]"), "")
                val d = clean.toDoubleOrNull()
                d != null && d >= 0.0
            }
        }
    }

    fun dialInRecommendation(brewTimeSec: Int, targetTimeSec: Int = 180, sensoryFeedback: String = ""): DialInAdvice {
        val feedback = sensoryFeedback.lowercase(Locale.ROOT)
        val timeDelta = brewTimeSec - targetTimeSec

        return when {
            feedback.contains("bitter") || feedback.contains("astringent") || feedback.contains("harsh") ||
                timeDelta > SLOW_BREW_TIME_DELTA_THRESHOLD_SECONDS -> {
                DialInAdvice(
                    proposedAction = "Adjust grind 1-2 clicks / 0.2 rotations coarser",
                    scientificReason = "Drawdown duration or bitter/astringent extraction indicates over-extraction or bed clogging. " +
                        "A coarser grind reduces surface area and speeds percolation."
                )
            }
            feedback.contains("sour") || feedback.contains("watery") || feedback.contains("weak") ||
                timeDelta < FAST_BREW_TIME_DELTA_THRESHOLD_SECONDS -> {
                DialInAdvice(
                    proposedAction = "Adjust grind 1-2 clicks / 0.2 rotations finer",
                    scientificReason = "Fast drawdown or sour, hollow flavor indicates under-extraction. " +
                        "A finer grind increases contact surface area and retards fluid flow."
                )
            }
            else -> {
                DialInAdvice(
                    proposedAction = "Maintain current grind setting",
                    scientificReason = "Brew parameters and cup balance are within nominal extraction boundaries."
                )
            }
        }
    }

    fun exportProfileToJson(profile: UserGrinderProfile): String {
        return """
        {
          "schemaVersion": 1,
          "profileId": "${profile.id}",
          "grinderModelId": "${profile.grinderModelId}",
          "customName": "${profile.customName.replace("\"", "\\\"")}",
          "selectedVariantId": ${profile.selectedVariantId?.let { "\"$it\"" } ?: "null"},
          "zeroCalibrationOffset": ${profile.zeroCalibrationOffset},
          "burrCondition": "${profile.burrCondition}",
          "calibrationNotes": "${profile.calibrationNotes.replace("\"", "\\\"")}",
          "calibratedAtEpochMs": ${profile.calibratedAtEpochMs}
        }
        """.trimIndent()
    }

    fun importProfileFromJson(json: String): UserGrinderProfile {
        val idRegex = Regex("\"profileId\":\\s*\"([^\"]+)\"")
        val modelRegex = Regex("\"grinderModelId\":\\s*\"([^\"]+)\"")
        val nameRegex = Regex("\"customName\":\\s*\"([^\"]+)\"")
        val offsetRegex = Regex("\"zeroCalibrationOffset\":\\s*([0-9.-]+)")
        val conditionRegex = Regex("\"burrCondition\":\\s*\"([^\"]+)\"")

        val id = idRegex.find(json)?.groupValues?.get(1) ?: java.util.UUID.randomUUID().toString()
        val modelId = modelRegex.find(json)?.groupValues?.get(1) ?: "grinder-comandante-c40-mk4"
        val name = nameRegex.find(json)?.groupValues?.get(1) ?: "Imported Grinder"
        val offset = offsetRegex.find(json)?.groupValues?.get(1)?.toDoubleOrNull() ?: 0.0
        val condition = conditionRegex.find(json)?.groupValues?.get(1) ?: "SEASONED"

        return UserGrinderProfile(
            id = id,
            grinderModelId = modelId,
            customName = name,
            zeroCalibrationOffset = offset,
            burrCondition = condition
        )
    }
}
