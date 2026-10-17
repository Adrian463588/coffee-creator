package com.coffeecreator.app.core.catalog

import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Locale

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
    val adjustmentMechanism: String, // e.g. "Putaran (External)", "Klik (Internal)"
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
    val burrCondition: String = "SEASONED", // "NEW", "SEASONED", "WORN"
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
    val proposedAction: String, // e.g. "Adjust 1-2 clicks coarser"
    val controlVariable: String = "Grind Size",
    val scientificReason: String,
    val isDeterministic: Boolean = false // Never make deterministic flavor promises
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

    val ALL_44_GRINDERS: List<GrinderModelRecord> = listOf(
        GrinderModelRecord(
            id = "grinder-1zpresso-j-ultra",
            brand = "1Zpresso",
            modelName = "1Zpresso J-Ultra",
            type = GrinderType.HAND,
            adjustmentUnit = AdjustmentUnit.ROTATION,
            adjustmentMechanism = "Putaran (External)",
            rawCoarseRange = "3.5 - 4.5 Putaran",
            rawMediumRange = "2.5 - 3.5 Putaran",
            rawFineRange = "1.0 - 1.6 Putaran",
            isEspressoCapable = true,
            evidenceStatus = EvidenceStatus.MANUFACTURER_VERIFIED,
            manufacturerSourceUrl = "https://1zpresso.coffee/grind-setting/",
            verifiedRangeMedium = "2.5 - 3.5 Rotations",
            variants = listOf(
                GrinderVariant("j-ultra-std", "J-Ultra Titanium Coated 48mm", "Conical 48mm Titanium", 8.0)
            ),
            safeZeroInstructions = listOf(
                "Turn the adjustment dial clockwise until tight.",
                "Do NOT force past burr touch to prevent burr damage.",
                "Calibrate dial ring so '0' aligns with burr lock indicator."
            )
        ),
        GrinderModelRecord(
            id = "grinder-1zpresso-jx",
            brand = "1Zpresso",
            modelName = "1Zpresso JX",
            type = GrinderType.HAND,
            adjustmentUnit = AdjustmentUnit.ROTATION,
            adjustmentMechanism = "Putaran (Internal)",
            rawCoarseRange = "2.8 - 3.3 Putaran",
            rawMediumRange = "1.9 - 2.4 Putaran",
            rawFineRange = "0.7 - 1.1 Putaran",
            isEspressoCapable = true,
            evidenceStatus = EvidenceStatus.MANUFACTURER_VERIFIED,
            manufacturerSourceUrl = "https://1zpresso.coffee/grind-setting/",
            variants = listOf(
                GrinderVariant("jx-std", "JX Standard 48mm", "Conical 48mm Stainless Steel", 25.0)
            ),
            safeZeroInstructions = listOf(
                "Turn thumb wheel clockwise until burrs gently lock.",
                "Do not overtighten.",
                "Back off slightly until handle swings freely."
            )
        ),
        GrinderModelRecord(
            id = "grinder-1zpresso-jx-pro",
            brand = "1Zpresso",
            modelName = "1Zpresso JX-Pro / JX-Pro S",
            type = GrinderType.HAND,
            adjustmentUnit = AdjustmentUnit.ROTATION,
            adjustmentMechanism = "Putaran (Top)",
            rawCoarseRange = "4.2 - 5.0 Putaran",
            rawMediumRange = "2.9 - 3.6 Putaran",
            rawFineRange = "1.0 - 1.6 Putaran",
            isEspressoCapable = true,
            evidenceStatus = EvidenceStatus.MANUFACTURER_VERIFIED,
            manufacturerSourceUrl = "https://1zpresso.coffee/grind-setting/",
            variants = listOf(
                GrinderVariant("jx-pro-std", "JX-Pro Top Dial 48mm", "Conical 48mm Stainless Steel", 12.5)
            ),
            safeZeroInstructions = listOf(
                "Turn top dial clockwise until handle stops moving freely.",
                "Mark or adjust knurled nut to align '0' with calibration dot."
            )
        ),
        GrinderModelRecord(
            id = "grinder-1zpresso-k-pro",
            brand = "1Zpresso",
            modelName = "1Zpresso K-Pro / K-Plus / K-Max",
            type = GrinderType.HAND,
            adjustmentUnit = AdjustmentUnit.DIAL_NUMBER,
            adjustmentMechanism = "Nomor (External)",
            rawCoarseRange = "Nomor 7.0 - 8.5",
            rawMediumRange = "Nomor 5.5 - 7.0",
            rawFineRange = "Nomor 3.0 - 4.0",
            isEspressoCapable = true,
            evidenceStatus = EvidenceStatus.MANUFACTURER_VERIFIED,
            manufacturerSourceUrl = "https://1zpresso.coffee/grind-setting/",
            variants = listOf(
                GrinderVariant("k-series-k-burr", "K-Burr Heptagonal 48mm", "Conical 48mm Stainless Steel", 22.0)
            ),
            safeZeroInstructions = listOf(
                "Rotate external collar clockwise to burr lock.",
                "Press and hold burr axle while aligning collar zero indicator."
            )
        ),
        GrinderModelRecord(
            id = "grinder-1zpresso-k-ultra",
            brand = "1Zpresso",
            modelName = "1Zpresso K-Ultra",
            type = GrinderType.HAND,
            adjustmentUnit = AdjustmentUnit.DIAL_NUMBER,
            adjustmentMechanism = "Nomor (External)",
            rawCoarseRange = "Nomor 8 - 9",
            rawMediumRange = "Nomor 6 - 7.5",
            rawFineRange = "Nomor 3 - 4.5",
            isEspressoCapable = true,
            evidenceStatus = EvidenceStatus.MANUFACTURER_VERIFIED,
            manufacturerSourceUrl = "https://1zpresso.coffee/grind-setting/",
            variants = listOf(
                GrinderVariant("k-ultra-burr", "K-Ultra Heptagonal 48mm", "Conical 48mm Stainless Steel", 20.0)
            ),
            safeZeroInstructions = listOf(
                "Tighten external collar clockwise until tight.",
                "Align '0' with the index dot without forcing the thread."
            )
        ),
        GrinderModelRecord(
            id = "grinder-1zpresso-q-air",
            brand = "1Zpresso",
            modelName = "1Zpresso Q Air / Q2",
            type = GrinderType.HAND,
            adjustmentUnit = AdjustmentUnit.CLICK,
            adjustmentMechanism = "Klik (Internal)",
            rawCoarseRange = "22 - 26 Klik",
            rawMediumRange = "15 - 20 Klik",
            rawFineRange = "10 - 14 Klik",
            isEspressoCapable = false,
            evidenceStatus = EvidenceStatus.USER_SUPPLIED_UNVERIFIED,
            caveats = listOf("Lightweight travel grinder; heptagonal burr recommended for pour-over, tight espresso range.")
        ),
        GrinderModelRecord(
            id = "grinder-1zpresso-x-pro",
            brand = "1Zpresso",
            modelName = "1Zpresso X-Pro / X-Ultra",
            type = GrinderType.HAND,
            adjustmentUnit = AdjustmentUnit.ROTATION,
            adjustmentMechanism = "Putaran (External)",
            rawCoarseRange = "2.0 - 2.4 Putaran",
            rawMediumRange = "1.2 - 1.5 Putaran",
            rawFineRange = "0.3 - 0.5 Putaran",
            isEspressoCapable = true,
            evidenceStatus = EvidenceStatus.MANUFACTURER_VERIFIED,
            manufacturerSourceUrl = "https://1zpresso.coffee/grind-setting/"
        ),
        GrinderModelRecord(
            id = "grinder-1zpresso-zp6-special",
            brand = "1Zpresso",
            modelName = "1Zpresso ZP6 Special",
            type = GrinderType.HAND,
            adjustmentUnit = AdjustmentUnit.DIAL_NUMBER,
            adjustmentMechanism = "Nomor (External)",
            rawCoarseRange = "Nomor 6.0 - 7.5",
            rawMediumRange = "Nomor 3.5 - 5.5",
            rawFineRange = "0 (Tidak disarankan)",
            isEspressoCapable = false,
            evidenceStatus = EvidenceStatus.MANUFACTURER_VERIFIED,
            manufacturerSourceUrl = "https://1zpresso.coffee/grind-setting/",
            caveats = listOf("Filter-only specialist grinder; burr geometry produces ultra-low fines and is NOT recommended for espresso.")
        ),
        GrinderModelRecord(
            id = "grinder-breville-smart-grinder-pro",
            brand = "Breville",
            modelName = "Breville Smart Grinder Pro",
            type = GrinderType.ELECTRIC,
            adjustmentUnit = AdjustmentUnit.DIAL_NUMBER,
            adjustmentMechanism = "Setting (Electric)",
            rawCoarseRange = "Setting 50 - 60",
            rawMediumRange = "Setting 30 - 45",
            rawFineRange = "Setting 1 - 25",
            isEspressoCapable = true,
            evidenceStatus = EvidenceStatus.USER_SUPPLIED_UNVERIFIED,
            caveats = listOf("Electric grinder with 60 macro steps; internal top burr shims allow further calibration.")
        ),
        GrinderModelRecord(
            id = "grinder-comandante-c40-mk4",
            brand = "Comandante",
            modelName = "Comandante C40 MK4",
            type = GrinderType.HAND,
            adjustmentUnit = AdjustmentUnit.CLICK,
            adjustmentMechanism = "Klik (Internal)",
            rawCoarseRange = "25 - 32 Klik",
            rawMediumRange = "18 - 24 Klik",
            rawFineRange = "10 - 15 Klik",
            isEspressoCapable = true,
            evidenceStatus = EvidenceStatus.MANUFACTURER_VERIFIED,
            manufacturerSourceUrl = "https://comandantegrinder.com/pages/faq",
            verifiedRangeMedium = "18 - 24 Clicks (Standard)",
            variants = listOf(
                GrinderVariant("c40-std", "Standard Axle & Burr", "Nitro Blade 39mm Conical", 30.0),
                GrinderVariant("c40-red-clix", "Red Clix RX35 Upgrade", "Nitro Blade 39mm Conical", 15.0)
            ),
            safeZeroInstructions = listOf(
                "Turn the 3-wing clicker clockwise until the crank handle no longer falls under its own weight (Click 0).",
                "Do NOT overtighten past this point to protect burr cutting edges.",
                "Count clicks counter-clockwise starting from 0."
            )
        ),
        GrinderModelRecord(
            id = "grinder-comandante-c60-baracuda",
            brand = "Comandante",
            modelName = "Comandante C60 Baracuda",
            type = GrinderType.HAND,
            adjustmentUnit = AdjustmentUnit.CLICK,
            adjustmentMechanism = "Klik (Internal)",
            rawCoarseRange = "35 - 45 Klik",
            rawMediumRange = "20 - 30 Klik",
            rawFineRange = "10 - 18 Klik",
            isEspressoCapable = true,
            evidenceStatus = EvidenceStatus.USER_SUPPLIED_UNVERIFIED,
            caveats = listOf("Large 60mm Baracuda burr set; click resolution is not equivalent to C40.")
        ),
        GrinderModelRecord(
            id = "grinder-etzinger-etz-i",
            brand = "Etzinger",
            modelName = "Etzinger etz-I",
            type = GrinderType.HAND,
            adjustmentUnit = AdjustmentUnit.DIAL_NUMBER,
            adjustmentMechanism = "Angka (External)",
            rawCoarseRange = "Angka 18 - 22",
            rawMediumRange = "Angka 12 - 16",
            rawFineRange = "Angka 4 - 8",
            isEspressoCapable = true,
            evidenceStatus = EvidenceStatus.USER_SUPPLIED_UNVERIFIED
        ),
        GrinderModelRecord(
            id = "grinder-fellow-ode-gen-2",
            brand = "Fellow",
            modelName = "Fellow Ode Gen 2",
            type = GrinderType.ELECTRIC,
            adjustmentUnit = AdjustmentUnit.DIAL_NUMBER,
            adjustmentMechanism = "Setting (Electric)",
            rawCoarseRange = "Setting 8 - 10",
            rawMediumRange = "Setting 4 - 6",
            rawFineRange = "Setting 2 - 3",
            isEspressoCapable = false,
            evidenceStatus = EvidenceStatus.MANUFACTURER_VERIFIED,
            manufacturerSourceUrl = "https://fellowproducts.com/",
            caveats = listOf("Electric brew grinder with 64mm flat burrs; not engineered or recommended for espresso pressures.")
        ),
        GrinderModelRecord(
            id = "grinder-hario-canister-c20",
            brand = "Hario",
            modelName = "Hario Canister (C-20)",
            type = GrinderType.HAND,
            adjustmentUnit = AdjustmentUnit.NOTCH,
            adjustmentMechanism = "Notch (Internal)",
            rawCoarseRange = "4 - 5 Notch",
            rawMediumRange = "2 - 3 Notch",
            rawFineRange = "1 Notch",
            isEspressoCapable = false,
            evidenceStatus = EvidenceStatus.USER_SUPPLIED_UNVERIFIED
        ),
        GrinderModelRecord(
            id = "grinder-hario-mini-slim-plus",
            brand = "Hario",
            modelName = "Hario Mini Slim+",
            type = GrinderType.HAND,
            adjustmentUnit = AdjustmentUnit.CLICK,
            adjustmentMechanism = "Klik (Internal)",
            rawCoarseRange = "10 - 13 Klik",
            rawMediumRange = "7 - 10 Klik",
            rawFineRange = "3 - 5 Klik",
            isEspressoCapable = false,
            evidenceStatus = EvidenceStatus.USER_SUPPLIED_UNVERIFIED,
            caveats = listOf("Ceramic conical burrs; higher fines generation at coarse settings.")
        ),
        GrinderModelRecord(
            id = "grinder-hario-skerton-pro",
            brand = "Hario",
            modelName = "Hario Skerton Pro",
            type = GrinderType.HAND,
            adjustmentUnit = AdjustmentUnit.NOTCH,
            adjustmentMechanism = "Notch (Internal)",
            rawCoarseRange = "8 - 10 Notch",
            rawMediumRange = "5 - 7 Notch",
            rawFineRange = "1 - 4 Notch",
            isEspressoCapable = false,
            evidenceStatus = EvidenceStatus.USER_SUPPLIED_UNVERIFIED
        ),
        GrinderModelRecord(
            id = "grinder-kingrinder-k6",
            brand = "Kingrinder",
            modelName = "Kingrinder K6",
            type = GrinderType.HAND,
            adjustmentUnit = AdjustmentUnit.CLICK,
            adjustmentMechanism = "Klik (External)",
            rawCoarseRange = "90 - 120 Klik",
            rawMediumRange = "60 - 90 Klik",
            rawFineRange = "30 - 50 Klik",
            isEspressoCapable = true,
            evidenceStatus = EvidenceStatus.MANUFACTURER_VERIFIED,
            manufacturerSourceUrl = "https://www.kingrinder.com/",
            variants = listOf(
                GrinderVariant("k6-heptagonal", "Heptagonal 48mm Conical", "Stainless Steel 48mm", 16.0)
            )
        ),
        GrinderModelRecord(
            id = "grinder-kingrinder-p-series",
            brand = "Kingrinder",
            modelName = "Kingrinder P0/P1/P2",
            type = GrinderType.HAND,
            adjustmentUnit = AdjustmentUnit.CLICK,
            adjustmentMechanism = "Klik (Internal)",
            rawCoarseRange = "35 - 45 Klik",
            rawMediumRange = "20 - 30 Klik",
            rawFineRange = "15 - 20 Klik",
            isEspressoCapable = false,
            evidenceStatus = EvidenceStatus.USER_SUPPLIED_UNVERIFIED
        ),
        GrinderModelRecord(
            id = "grinder-kinu-m47",
            brand = "Kinu",
            modelName = "Kinu M47",
            type = GrinderType.HAND,
            adjustmentUnit = AdjustmentUnit.ROTATION,
            adjustmentMechanism = "Putaran (External)",
            rawCoarseRange = "4.0 - 5.0 Putaran",
            rawMediumRange = "2.5 - 3.5 Putaran",
            rawFineRange = "0.8 - 1.2 Putaran",
            isEspressoCapable = true,
            evidenceStatus = EvidenceStatus.MANUFACTURER_VERIFIED,
            variants = listOf(
                GrinderVariant("kinu-espresso", "Kinu Espresso Burr", "Conical 47mm Black Fusion", 10.0),
                GrinderVariant("kinu-pour-over", "Kinu Pour Over Burr", "Conical 47mm Black Fusion", 10.0)
            )
        ),
        GrinderModelRecord(
            id = "grinder-latina-sumba",
            brand = "Latina",
            modelName = "Latina Sumba / Sumbawa",
            type = GrinderType.HAND,
            adjustmentUnit = AdjustmentUnit.CLICK,
            adjustmentMechanism = "Klik (Internal)",
            rawCoarseRange = "10 - 14 Klik",
            rawMediumRange = "7 - 10 Klik",
            rawFineRange = "3 - 5 Klik",
            isEspressoCapable = false,
            evidenceStatus = EvidenceStatus.USER_SUPPLIED_UNVERIFIED
        ),
        GrinderModelRecord(
            id = "grinder-latina-sumo",
            brand = "Latina",
            modelName = "Latina Sumo",
            type = GrinderType.HAND,
            adjustmentUnit = AdjustmentUnit.CLICK,
            adjustmentMechanism = "Klik (Internal)",
            rawCoarseRange = "20 - 25 Klik",
            rawMediumRange = "15 - 20 Klik",
            rawFineRange = "8 - 12 Klik",
            isEspressoCapable = false,
            evidenceStatus = EvidenceStatus.USER_SUPPLIED_UNVERIFIED
        ),
        GrinderModelRecord(
            id = "grinder-mavo-phantox-pro",
            brand = "Mavo",
            modelName = "Mavo Phantox Pro",
            type = GrinderType.HAND,
            adjustmentUnit = AdjustmentUnit.DIAL_NUMBER,
            adjustmentMechanism = "Angka (External)",
            rawCoarseRange = "Nomor 10 - 12",
            rawMediumRange = "Nomor 6 - 10",
            rawFineRange = "Nomor 1 - 4",
            isEspressoCapable = true,
            evidenceStatus = EvidenceStatus.USER_SUPPLIED_UNVERIFIED
        ),
        GrinderModelRecord(
            id = "grinder-mazzer-omega",
            brand = "Mazzer",
            modelName = "Mazzer Omega",
            type = GrinderType.HAND,
            adjustmentUnit = AdjustmentUnit.DIAL_NUMBER,
            adjustmentMechanism = "Angka (External)",
            rawCoarseRange = "Angka 9 - 11",
            rawMediumRange = "Angka 6 - 8",
            rawFineRange = "Angka 1 - 3",
            isEspressoCapable = true,
            evidenceStatus = EvidenceStatus.MANUFACTURER_VERIFIED,
            variants = listOf(
                GrinderVariant("omega-fast", "Omega Fast Burrs", "Conical 47mm Carbon Steel", 33.3),
                GrinderVariant("omega-soft", "Omega Soft Burrs", "Conical 47mm Carbon Steel", 33.3)
            )
        ),
        GrinderModelRecord(
            id = "grinder-mhw-blade-r3",
            brand = "MHW-3BOMBER",
            modelName = "MHW-3BOMBER Blade R3",
            type = GrinderType.HAND,
            adjustmentUnit = AdjustmentUnit.CLICK,
            adjustmentMechanism = "Klik (External)",
            rawCoarseRange = "110 - 165 Klik",
            rawMediumRange = "90 - 130 Klik",
            rawFineRange = "20 - 70 Klik",
            isEspressoCapable = true,
            evidenceStatus = EvidenceStatus.USER_SUPPLIED_UNVERIFIED
        ),
        GrinderModelRecord(
            id = "grinder-oe-lido-3",
            brand = "Orphan Espresso",
            modelName = "OE Lido 3 / OG",
            type = GrinderType.HAND,
            adjustmentUnit = AdjustmentUnit.MARK,
            adjustmentMechanism = "Mark (External)",
            rawCoarseRange = "12 - 15 Mark",
            rawMediumRange = "6 - 10 Mark",
            rawFineRange = "2 - 4 Mark",
            isEspressoCapable = true,
            evidenceStatus = EvidenceStatus.USER_SUPPLIED_UNVERIFIED
        ),
        GrinderModelRecord(
            id = "grinder-pietro",
            brand = "Pietro",
            modelName = "Pietro (Flat Burr)",
            type = GrinderType.HAND,
            adjustmentUnit = AdjustmentUnit.DIAL_NUMBER,
            adjustmentMechanism = "Angka (Vertical)",
            rawCoarseRange = "Angka 7 - 9",
            rawMediumRange = "Angka 5 - 7",
            rawFineRange = "Angka 1 - 2.5",
            isEspressoCapable = true,
            evidenceStatus = EvidenceStatus.MANUFACTURER_VERIFIED,
            variants = listOf(
                GrinderVariant("pietro-b-modal", "B-Modal Flat Burrs 58mm", "Multipurpose Flat 58mm M340", 15.0),
                GrinderVariant("pietro-pro-brewing", "Pro Brewing Flat Burrs 58mm", "Unimodal Filter Flat 58mm M340", 15.0)
            )
        ),
        GrinderModelRecord(
            id = "grinder-porlex-mini-ii",
            brand = "Porlex",
            modelName = "Porlex Mini II",
            type = GrinderType.HAND,
            adjustmentUnit = AdjustmentUnit.CLICK,
            adjustmentMechanism = "Klik (Internal)",
            rawCoarseRange = "10 - 13 Klik",
            rawMediumRange = "7 - 9 Klik",
            rawFineRange = "3 - 5 Klik",
            isEspressoCapable = false,
            evidenceStatus = EvidenceStatus.USER_SUPPLIED_UNVERIFIED
        ),
        GrinderModelRecord(
            id = "grinder-starseeker-edge",
            brand = "Starseeker",
            modelName = "Starseeker Edge / Edge+",
            type = GrinderType.HAND,
            adjustmentUnit = AdjustmentUnit.CLICK,
            adjustmentMechanism = "Klik (External)",
            rawCoarseRange = "80 - 100 Klik",
            rawMediumRange = "50 - 70 Klik",
            rawFineRange = "20 - 40 Klik",
            isEspressoCapable = true,
            evidenceStatus = EvidenceStatus.USER_SUPPLIED_UNVERIFIED
        ),
        GrinderModelRecord(
            id = "grinder-timemore-c2",
            brand = "Timemore",
            modelName = "Timemore C2",
            type = GrinderType.HAND,
            adjustmentUnit = AdjustmentUnit.CLICK,
            adjustmentMechanism = "Klik (Internal)",
            rawCoarseRange = "25 - 30 Klik",
            rawMediumRange = "13 - 24 Klik",
            rawFineRange = "2 - 12 Klik",
            isEspressoCapable = false,
            evidenceStatus = EvidenceStatus.MANUFACTURER_VERIFIED,
            caveats = listOf("Entry filter grinder; clicks below 10 are not recommended as burrs rub and lack fine espresso resolution.")
        ),
        GrinderModelRecord(
            id = "grinder-timemore-c3",
            brand = "Timemore",
            modelName = "Timemore C3",
            type = GrinderType.HAND,
            adjustmentUnit = AdjustmentUnit.CLICK,
            adjustmentMechanism = "Klik (Internal)",
            rawCoarseRange = "21 - 24 Klik",
            rawMediumRange = "11 - 20 Klik",
            rawFineRange = "2 - 10 Klik",
            isEspressoCapable = false,
            evidenceStatus = EvidenceStatus.MANUFACTURER_VERIFIED,
            variants = listOf(
                GrinderVariant("c3-s2c660", "S2C 660 Conical Burrs", "Stainless Steel 38mm S2C", 30.0)
            )
        ),
        GrinderModelRecord(
            id = "grinder-timemore-c3-esp",
            brand = "Timemore",
            modelName = "Timemore C3 ESP",
            type = GrinderType.HAND,
            adjustmentUnit = AdjustmentUnit.ROTATION,
            adjustmentMechanism = "Putaran (Internal)",
            rawCoarseRange = "2.4 - 3",
            rawMediumRange = "1.2 - 2",
            rawFineRange = "0.5 - 1",
            normalizedCoarse = "2.4 - 3.0 Putaran",
            normalizedMedium = "1.2 - 2.0 Putaran",
            normalizedFine = "0.5 - 1.0 Putaran",
            isEspressoCapable = true,
            evidenceStatus = EvidenceStatus.MANUFACTURER_VERIFIED
        ),
        GrinderModelRecord(
            id = "grinder-timemore-c3-esp-pro",
            brand = "Timemore",
            modelName = "Timemore C3 ESP Pro",
            type = GrinderType.HAND,
            adjustmentUnit = AdjustmentUnit.ROTATION,
            adjustmentMechanism = "Putaran",
            rawCoarseRange = "2.4 - 3 Putaran",
            rawMediumRange = "1.4 - 2 Putaran",
            rawFineRange = "0.6 - 1.2 Putaran",
            isEspressoCapable = true,
            evidenceStatus = EvidenceStatus.USER_SUPPLIED_UNVERIFIED
        ),
        GrinderModelRecord(
            id = "grinder-timemore-c5",
            brand = "Timemore",
            modelName = "Timemore C5",
            type = GrinderType.HAND,
            adjustmentUnit = AdjustmentUnit.CLICK,
            adjustmentMechanism = "Klik (Internal)",
            rawCoarseRange = "15 - 16 Klik",
            rawMediumRange = "9 - 14 Klik",
            rawFineRange = "2 - 8 Klik",
            isEspressoCapable = false,
            evidenceStatus = EvidenceStatus.USER_SUPPLIED_UNVERIFIED
        ),
        GrinderModelRecord(
            id = "grinder-timemore-c5-esp-pro",
            brand = "Timemore",
            modelName = "Timemore C5 ESP PRO",
            type = GrinderType.HAND,
            adjustmentUnit = AdjustmentUnit.CLICK,
            adjustmentMechanism = "Klik (Internal)",
            rawCoarseRange = "110 - 120 Klik",
            rawMediumRange = "75 - 100 Klik",
            rawFineRange = "25 - 60 Klik",
            isEspressoCapable = true,
            evidenceStatus = EvidenceStatus.USER_SUPPLIED_UNVERIFIED
        ),
        GrinderModelRecord(
            id = "grinder-timemore-c5-pro",
            brand = "Timemore",
            modelName = "Timemore C5 Pro",
            type = GrinderType.HAND,
            adjustmentUnit = AdjustmentUnit.CLICK,
            adjustmentMechanism = "Klik (Internal)",
            rawCoarseRange = "23 - 30 Klik",
            rawMediumRange = "14 - 22 Klik",
            rawFineRange = "5 - 12 Klik",
            isEspressoCapable = false,
            evidenceStatus = EvidenceStatus.USER_SUPPLIED_UNVERIFIED
        ),
        GrinderModelRecord(
            id = "grinder-timemore-chestnut-x",
            brand = "Timemore",
            modelName = "Timemore Chestnut X",
            type = GrinderType.HAND,
            adjustmentUnit = AdjustmentUnit.CLICK,
            adjustmentMechanism = "Mayor (Internal)",
            rawCoarseRange = "20 - 24 Mayor",
            rawMediumRange = "14 - 18 Mayor",
            rawFineRange = "6 - 10 Mayor",
            isEspressoCapable = true,
            evidenceStatus = EvidenceStatus.MANUFACTURER_VERIFIED,
            caveats = listOf("Dual adjustment plate: primary major clicks and secondary micro-adjustment steps.")
        ),
        GrinderModelRecord(
            id = "grinder-timemore-nano",
            brand = "Timemore",
            modelName = "Timemore Nano / Plus",
            type = GrinderType.HAND,
            adjustmentUnit = AdjustmentUnit.CLICK,
            adjustmentMechanism = "Klik (Internal)",
            rawCoarseRange = "20 - 24 Klik",
            rawMediumRange = "14 - 18 Klik",
            rawFineRange = "10 - 12 Klik",
            isEspressoCapable = false,
            evidenceStatus = EvidenceStatus.USER_SUPPLIED_UNVERIFIED
        ),
        GrinderModelRecord(
            id = "grinder-timemore-s3",
            brand = "Timemore",
            modelName = "Timemore S3",
            type = GrinderType.HAND,
            adjustmentUnit = AdjustmentUnit.DIAL_NUMBER,
            adjustmentMechanism = "Angka (External)",
            rawCoarseRange = "Angka 7.5 - 9.0",
            rawMediumRange = "Angka 4.5 - 6.5",
            rawFineRange = "Angka 1.5 - 3.0",
            isEspressoCapable = true,
            evidenceStatus = EvidenceStatus.MANUFACTURER_VERIFIED
        ),
        GrinderModelRecord(
            id = "grinder-timemore-sculptor",
            brand = "Timemore",
            modelName = "Timemore Sculptor 064S / 078S",
            type = GrinderType.ELECTRIC,
            adjustmentUnit = AdjustmentUnit.STEPLESS,
            adjustmentMechanism = "Angka (Stepless)",
            rawCoarseRange = "Angka 6.5 - 8.0",
            rawMediumRange = "Angka 4.0 - 6.0",
            rawFineRange = "Angka 1.0 - 2.5",
            isEspressoCapable = true,
            evidenceStatus = EvidenceStatus.MANUFACTURER_VERIFIED,
            caveats = listOf("Electric flat burr grinder; rotary dial with RPM control.")
        ),
        GrinderModelRecord(
            id = "grinder-timemore-slim-plus",
            brand = "Timemore",
            modelName = "Timemore Slim Plus",
            type = GrinderType.HAND,
            adjustmentUnit = AdjustmentUnit.CLICK,
            adjustmentMechanism = "Klik (Internal)",
            rawCoarseRange = "22 - 26 Klik",
            rawMediumRange = "15 - 20 Klik",
            rawFineRange = "10 - 14 Klik",
            isEspressoCapable = false,
            evidenceStatus = EvidenceStatus.USER_SUPPLIED_UNVERIFIED
        ),
        GrinderModelRecord(
            id = "grinder-timemore-millab-m01",
            brand = "Timemore",
            modelName = "Timemore x Millab M01",
            type = GrinderType.HAND,
            adjustmentUnit = AdjustmentUnit.CLICK,
            adjustmentMechanism = "Klik (External)",
            rawCoarseRange = "9 - 10 Klik",
            rawMediumRange = "5 - 8 Klik",
            rawFineRange = "1 - 4 Klik",
            isEspressoCapable = true,
            evidenceStatus = EvidenceStatus.USER_SUPPLIED_UNVERIFIED
        ),
        GrinderModelRecord(
            id = "grinder-varia-hand",
            brand = "Varia",
            modelName = "Varia Hand Grinder",
            type = GrinderType.HAND,
            adjustmentUnit = AdjustmentUnit.CLICK,
            adjustmentMechanism = "Klik (External)",
            rawCoarseRange = "90 - 110 Klik",
            rawMediumRange = "60 - 85 Klik",
            rawFineRange = "20 - 40 Klik",
            isEspressoCapable = true,
            evidenceStatus = EvidenceStatus.USER_SUPPLIED_UNVERIFIED
        ),
        GrinderModelRecord(
            id = "grinder-vesper-vs3-fold",
            brand = "Vesper",
            modelName = "Vesper VS-3 FOLD",
            type = GrinderType.HAND,
            adjustmentUnit = AdjustmentUnit.CLICK,
            adjustmentMechanism = "Klik (External)",
            rawCoarseRange = "80 - 92 Klik",
            rawMediumRange = "54 - 72 Klik",
            rawFineRange = "15 - 40 Kilk", // Preserves source raw typo "Kilk"
            normalizedFine = "15 - 40 Klik",
            isEspressoCapable = true,
            evidenceStatus = EvidenceStatus.USER_SUPPLIED_UNVERIFIED
        ),
        GrinderModelRecord(
            id = "grinder-wacaco-exagrind",
            brand = "Wacaco",
            modelName = "Wacaco Exagrind",
            type = GrinderType.HAND,
            adjustmentUnit = AdjustmentUnit.ROTATION,
            adjustmentMechanism = "Putaran/Klik",
            rawCoarseRange = "1.5 - 2.0 Putaran",
            rawMediumRange = "1.0 - 1.3 Putaran",
            rawFineRange = "0 - 20 Klik",
            isEspressoCapable = true,
            evidenceStatus = EvidenceStatus.USER_SUPPLIED_UNVERIFIED
        )
    )

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
            m.contains("coarse") || m.contains("cold brew") || m.contains("french press") || m.contains("tubruk") -> grinder.normalizedCoarse
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

    fun dialInRecommendation(
        brewTimeSec: Int,
        targetTimeSec: Int = 180,
        sensoryFeedback: String = ""
    ): DialInAdvice {
        val feedback = sensoryFeedback.lowercase(Locale.ROOT)
        val timeDelta = brewTimeSec - targetTimeSec

        return when {
            feedback.contains("bitter") || feedback.contains("astringent") || feedback.contains("harsh") || timeDelta > 45 -> {
                DialInAdvice(
                    proposedAction = "Adjust grind 1-2 clicks / 0.2 rotations coarser",
                    scientificReason = "Drawdown duration or bitter/astringent extraction indicates over-extraction or bed clogging. A coarser grind reduces surface area and speeds percolation."
                )
            }
            feedback.contains("sour") || feedback.contains("watery") || feedback.contains("weak") || timeDelta < -40 -> {
                DialInAdvice(
                    proposedAction = "Adjust grind 1-2 clicks / 0.2 rotations finer",
                    scientificReason = "Fast drawdown or sour, hollow flavor indicates under-extraction. A finer grind increases contact surface area and retards fluid flow."
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
