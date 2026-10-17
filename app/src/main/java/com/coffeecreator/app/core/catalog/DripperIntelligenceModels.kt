package com.coffeecreator.app.core.catalog

import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Locale

// ============================================================================
// Sprint 03: Dripper & Manual Brewing Intelligence Domain Models (BR-01 .. BR-18)
// Pure Kotlin/JVM, zero Android UI dependencies.
// ============================================================================

enum class BrewerGeometry {
    CONE_60,
    CONE_80,
    FLAT_BOTTOM,
    TRAPEZOID,
    IMMERSION_HYBRID,
    NO_BYPASS
}

enum class FlowClass {
    FAST,
    MODERATE,
    RESTRICTED,
    VALVE_CONTROLLED,
    UNKNOWN // Invariant: Qualitative only; never fabricate numeric mL/s
}

enum class FlowMode {
    PERCOLATION,
    IMMERSION,
    HYBRID
}

enum class ValveState {
    OPEN,
    CLOSED,
    NOT_APPLICABLE
}

data class FilterType(
    val id: String,
    val name: String,
    val shape: String, // "Cone", "Flat Wave", "Trapezoid", "Disc", "Bonded Thick"
    val size: String,  // "01", "02", "155", "185", "6-Cup"
    val material: String = "Paper"
)

data class DripperModelRecord(
    val id: String,
    val name: String,
    val geometry: BrewerGeometry,
    val flowClass: FlowClass,
    val flowMode: FlowMode,
    val flowBehaviorDescription: String,
    val grindBaselineRelativeToV60: String,
    val startingGrindText: String,
    val evidenceStatus: EvidenceStatus,
    val sourceUrl: String? = null,
    val isValveEquipped: Boolean = false,
    val supportedBottomVariants: List<String> = emptyList(),
    val compatibleFilterIds: List<String> = emptyList(),
    val notes: String = ""
)

data class FilterCompatibilityResult(
    val isCompatible: Boolean,
    val statusText: String,
    val recommendationNote: String = ""
)

data class DripperFlowAdvice(
    val flowClass: FlowClass,
    val flowMode: FlowMode,
    val qualitativeDrainSpeed: String,
    val valveNotice: String? = null,
    val relativeGrindAdjustmentNote: String
)

data class ManualStagedPour(
    val stageIndex: Int,
    val name: String, // "Bloom", "First Pour", "Main Pour", "Final Pulse"
    val pourMassGrams: BigDecimal,
    val cumulativeMassGrams: BigDecimal,
    val durationSeconds: Int,
    val valveState: ValveState = ValveState.NOT_APPLICABLE,
    val notes: String = ""
)

data class ManualBrewRecipePlan(
    val id: String,
    val name: String,
    val coffeeDoseGrams: BigDecimal,
    val targetWaterGrams: BigDecimal,
    val calculatedRatioDenominator: BigDecimal,
    val temperatureCelsius: BigDecimal = BigDecimal("93.0"),
    val grinderModelId: String? = null,
    val grinderSetting: GrinderSetting? = null,
    val dripperId: String? = null,
    val filterTypeId: String? = null,
    val bottomVariant: String? = null,
    val stagedPours: List<ManualStagedPour> = emptyList(),
    val waterMineralProfileId: String? = null
) {
    init {
        require(coffeeDoseGrams > BigDecimal.ZERO) { "Coffee dose must be greater than zero." }
        require(targetWaterGrams > BigDecimal.ZERO) { "Target water mass must be greater than zero." }
    }
}

data class BrewDiagnosticRecord(
    val id: String,
    val brewJournalId: String,
    val totalDrawdownSeconds: Int,
    val cloggingObserved: String = "NONE", // "NONE", "MINOR", "SEVERE_STALL"
    val bypassObserved: Boolean = false,
    val channelingObserved: Boolean = false,
    val bedAppearance: String = "FLAT", // "FLAT", "MUDDY", "SLOPED", "CRATERED"
    val notes: String = ""
)

object DripperIntelligenceCatalog {

    const val VIDEO_REFERENCE_STATUS = "PENDING_VIDEO_REVIEW"

    val FILTERS: List<FilterType> = listOf(
        FilterType("filter-v60-01", "Hario V60 01 Cone Filter", "Cone", "01"),
        FilterType("filter-v60-02", "Hario V60 02 Cone Filter", "Cone", "02"),
        FilterType("filter-cafec-abaca-01", "Cafec Abaca 01 Cone Filter", "Cone", "01"),
        FilterType("filter-cafec-abaca-02", "Cafec Abaca 02 Cone Filter", "Cone", "02"),
        FilterType("filter-kalita-155", "Kalita Wave 155 Filter", "Flat Wave", "155"),
        FilterType("filter-kalita-185", "Kalita Wave 185 Filter", "Flat Wave", "185"),
        FilterType("filter-chemex-bonded", "Chemex Bonded Square/Circle Filter", "Bonded Thick", "6-Cup"),
        FilterType("filter-melitta-1x4", "Melitta 1x4 Trapezoid Filter", "Trapezoid", "1x4"),
        FilterType("filter-pulsar-disc", "NextLevel Pulsar Disc Filter", "Disc", "Standard"),
        FilterType("filter-orea-wave-185", "Orea / Kalita 185 Wave Filter", "Flat Wave", "185")
    )

    val ALL_26_DRIPPERS: List<DripperModelRecord> = listOf(
        DripperModelRecord(
            id = "dripper-1zpresso-01",
            name = "1Zpresso Dripper 01",
            geometry = BrewerGeometry.CONE_60,
            flowClass = FlowClass.UNKNOWN,
            flowMode = FlowMode.PERCOLATION,
            flowBehaviorDescription = "Belum terverifikasi",
            grindBaselineRelativeToV60 = "Equal to V60",
            startingGrindText = "Medium; awal setara V60 (hipotesis)",
            evidenceStatus = EvidenceStatus.USER_SUPPLIED_UNVERIFIED,
            compatibleFilterIds = listOf("filter-v60-01")
        ),
        DripperModelRecord(
            id = "dripper-april",
            name = "April Brewer",
            geometry = BrewerGeometry.FLAT_BOTTOM,
            flowClass = FlowClass.MODERATE,
            flowMode = FlowMode.PERCOLATION,
            flowBehaviorDescription = "Terkontrol; tergantung filter dan pouring",
            grindBaselineRelativeToV60 = "Coarser than V60",
            startingGrindText = "Medium–coarse; dial-in dengan resep April",
            evidenceStatus = EvidenceStatus.THIRD_PARTY,
            sourceUrl = "https://www.aprilcoffeeroasters.com/pages/arsosala-infos",
            compatibleFilterIds = listOf("filter-kalita-155", "filter-kalita-185")
        ),
        DripperModelRecord(
            id = "dripper-cafec-flower",
            name = "Cafec Flower Dripper",
            geometry = BrewerGeometry.CONE_60,
            flowClass = FlowClass.FAST,
            flowMode = FlowMode.PERCOLATION,
            flowBehaviorDescription = "Relatif lancar/cepat; flower ribs menjaga celah udara",
            grindBaselineRelativeToV60 = "Equal to slightly finer than V60",
            startingGrindText = "Medium–fine sampai medium (awal)",
            evidenceStatus = EvidenceStatus.MANUFACTURER_VERIFIED,
            sourceUrl = "https://www.cafec-jp.com/products/dripper/",
            compatibleFilterIds = listOf("filter-cafec-abaca-01", "filter-cafec-abaca-02", "filter-v60-01", "filter-v60-02")
        ),
        DripperModelRecord(
            id = "dripper-chemex-6cup",
            name = "Chemex 6-Cup",
            geometry = BrewerGeometry.CONE_60,
            flowClass = FlowClass.RESTRICTED,
            flowMode = FlowMode.PERCOLATION,
            flowBehaviorDescription = "Cenderung lambat/terhambat filter bonded tebal",
            grindBaselineRelativeToV60 = "Coarser than V60",
            startingGrindText = "Medium–coarse (resmi Chemex)",
            evidenceStatus = EvidenceStatus.MANUFACTURER_VERIFIED,
            sourceUrl = "https://chemexcoffeemaker.com/pages/filter-series-support-page",
            compatibleFilterIds = listOf("filter-chemex-bonded"),
            notes = "Thick multi-layer bonded paper requires coarser grind to prevent prolonged stalling."
        ),
        DripperModelRecord(
            id = "dripper-clever",
            name = "Clever Dripper",
            geometry = BrewerGeometry.TRAPEZOID,
            flowClass = FlowClass.VALVE_CONTROLLED,
            flowMode = FlowMode.HYBRID,
            flowBehaviorDescription = "Tertahan saat steep; mengalir saat valve dibuka",
            grindBaselineRelativeToV60 = "Equal or slightly coarser than V60",
            startingGrindText = "Medium hingga medium–coarse (awal)",
            evidenceStatus = EvidenceStatus.THIRD_PARTY,
            sourceUrl = "https://cleverbrewing.coffee/products/clever-dripper-black",
            isValveEquipped = true,
            compatibleFilterIds = listOf("filter-melitta-1x4"),
            notes = "Valve closed during steep phase, opened when placed on decanter."
        ),
        DripperModelRecord(
            id = "dripper-crystal-eye-vector",
            name = "CRYSTAL EYE VECTOR",
            geometry = BrewerGeometry.CONE_60,
            flowClass = FlowClass.UNKNOWN,
            flowMode = FlowMode.PERCOLATION,
            flowBehaviorDescription = "Belum terverifikasi untuk varian VECTOR",
            grindBaselineRelativeToV60 = "Equal to V60",
            startingGrindText = "Medium; hipotesis V60, perlu validasi",
            evidenceStatus = EvidenceStatus.USER_SUPPLIED_UNVERIFIED,
            compatibleFilterIds = listOf("filter-v60-01")
        ),
        DripperModelRecord(
            id = "dripper-fellow-stagg-x",
            name = "Fellow Stagg X",
            geometry = BrewerGeometry.FLAT_BOTTOM,
            flowClass = FlowClass.MODERATE,
            flowMode = FlowMode.PERCOLATION,
            flowBehaviorDescription = "Terkontrol; lubang/pola dasar membantu drainase",
            grindBaselineRelativeToV60 = "Equal to V60",
            startingGrindText = "Medium sampai medium–fine (awal)",
            evidenceStatus = EvidenceStatus.MANUFACTURER_VERIFIED,
            sourceUrl = "https://fellowproducts.com/collections/coffee-makers-brewers/products/staggdripper",
            compatibleFilterIds = listOf("filter-kalita-185")
        ),
        DripperModelRecord(
            id = "dripper-hario-v60",
            name = "Hario V60",
            geometry = BrewerGeometry.CONE_60,
            flowClass = FlowClass.FAST,
            flowMode = FlowMode.PERCOLATION,
            flowBehaviorDescription = "Bebas/relatif cepat; sangat sensitif teknik tuang",
            grindBaselineRelativeToV60 = "Baseline standard",
            startingGrindText = "Medium–fine sampai medium (baseline)",
            evidenceStatus = EvidenceStatus.MANUFACTURER_VERIFIED,
            sourceUrl = "https://www.hario.co.uk/pages/brew-guides-v60-expert",
            compatibleFilterIds = listOf("filter-v60-01", "filter-v60-02")
        ),
        DripperModelRecord(
            id = "dripper-hario-v60-mugen",
            name = "Hario V60 Mugen",
            geometry = BrewerGeometry.CONE_60,
            flowClass = FlowClass.RESTRICTED,
            flowMode = FlowMode.PERCOLATION,
            flowBehaviorDescription = "Lebih lambat/terbatas daripada V60; flat internal ribs promote single-pour bypass control",
            grindBaselineRelativeToV60 = "Coarser than V60",
            startingGrindText = "Sedikit lebih kasar daripada V60 (resmi Hario)",
            evidenceStatus = EvidenceStatus.MANUFACTURER_VERIFIED,
            sourceUrl = "https://www.hario-usa.com/products/v60-one-pour-dripper-mugen",
            compatibleFilterIds = listOf("filter-v60-02"),
            notes = "Manufacturer recommends slightly coarser grind than standard V60 for one-pour recipe."
        ),
        DripperModelRecord(
            id = "dripper-hario-switch",
            name = "Immersion Dripper Switch",
            geometry = BrewerGeometry.IMMERSION_HYBRID,
            flowClass = FlowClass.VALVE_CONTROLLED,
            flowMode = FlowMode.HYBRID,
            flowBehaviorDescription = "Valve tutup: 0 flow (immersion); valve buka: perkolasi V60",
            grindBaselineRelativeToV60 = "Slightly coarser than V60",
            startingGrindText = "Medium; sedikit lebih kasar dari V60 untuk steep 2 menit",
            evidenceStatus = EvidenceStatus.MANUFACTURER_VERIFIED,
            sourceUrl = "https://www.hario.co.uk/blogs/news/how-to-brew-with-the-hario-v60-switch",
            isValveEquipped = true,
            compatibleFilterIds = listOf("filter-v60-02"),
            notes = "Hybrid immersion/percolation. Switch down = open; Switch up = closed."
        ),
        DripperModelRecord(
            id = "dripper-kalita-wave-155",
            name = "Kalita Wave 155",
            geometry = BrewerGeometry.FLAT_BOTTOM,
            flowClass = FlowClass.MODERATE,
            flowMode = FlowMode.PERCOLATION,
            flowBehaviorDescription = "Terkontrol lewat 3 lubang dan filter wave",
            grindBaselineRelativeToV60 = "Equal to V60",
            startingGrindText = "Medium (awal)",
            evidenceStatus = EvidenceStatus.MANUFACTURER_VERIFIED,
            sourceUrl = "https://kalitaofficial.com/product/kalita-wave-155-stainless-steel-coffee-dripper/",
            compatibleFilterIds = listOf("filter-kalita-155")
        ),
        DripperModelRecord(
            id = "dripper-kono-meimon",
            name = "Kono Meimon",
            geometry = BrewerGeometry.CONE_60,
            flowClass = FlowClass.MODERATE,
            flowMode = FlowMode.PERCOLATION,
            flowBehaviorDescription = "Terkontrol; bergantung rib/filter dan tuangan",
            grindBaselineRelativeToV60 = "Equal to V60",
            startingGrindText = "Medium (awal; uji dibanding V60)",
            evidenceStatus = EvidenceStatus.USER_SUPPLIED_UNVERIFIED,
            compatibleFilterIds = listOf("filter-v60-01", "filter-v60-02")
        ),
        DripperModelRecord(
            id = "dripper-melitta-1x4",
            name = "Melitta 1x4",
            geometry = BrewerGeometry.TRAPEZOID,
            flowClass = FlowClass.RESTRICTED,
            flowMode = FlowMode.PERCOLATION,
            flowBehaviorDescription = "Terkontrol/relatif lambat; outlet bervariasi",
            grindBaselineRelativeToV60 = "Equal to V60",
            startingGrindText = "Medium (awal)",
            evidenceStatus = EvidenceStatus.USER_SUPPLIED_UNVERIFIED,
            compatibleFilterIds = listOf("filter-melitta-1x4")
        ),
        DripperModelRecord(
            id = "dripper-nextlevel-pulsar",
            name = "NextLevel Pulsar",
            geometry = BrewerGeometry.NO_BYPASS,
            flowClass = FlowClass.VALVE_CONTROLLED,
            flowMode = FlowMode.HYBRID,
            flowBehaviorDescription = "Valve-adjustable; dapat berhenti total",
            grindBaselineRelativeToV60 = "Medium to medium-coarse",
            startingGrindText = "Medium hingga medium–coarse (awal)",
            evidenceStatus = EvidenceStatus.THIRD_PARTY,
            isValveEquipped = true,
            compatibleFilterIds = listOf("filter-pulsar-disc"),
            notes = "No-bypass column brewer with rotary flow control valve."
        ),
        DripperModelRecord(
            id = "dripper-orea-01",
            name = "OREA 01",
            geometry = BrewerGeometry.FLAT_BOTTOM,
            flowClass = FlowClass.UNKNOWN,
            flowMode = FlowMode.PERCOLATION,
            flowBehaviorDescription = "Bergantung varian dasar/filter; belum diverifikasi",
            grindBaselineRelativeToV60 = "Equal to V60",
            startingGrindText = "Medium (awal)",
            evidenceStatus = EvidenceStatus.USER_SUPPLIED_UNVERIFIED,
            compatibleFilterIds = listOf("filter-kalita-155")
        ),
        DripperModelRecord(
            id = "dripper-orea-v3",
            name = "Orea V3",
            geometry = BrewerGeometry.FLAT_BOTTOM,
            flowClass = FlowClass.FAST,
            flowMode = FlowMode.PERCOLATION,
            flowBehaviorDescription = "Relatif cepat; bergantung filter/negotiator",
            grindBaselineRelativeToV60 = "Medium to medium-coarse",
            startingGrindText = "Medium sampai medium–coarse (awal)",
            evidenceStatus = EvidenceStatus.THIRD_PARTY,
            compatibleFilterIds = listOf("filter-kalita-185", "filter-orea-wave-185")
        ),
        DripperModelRecord(
            id = "dripper-orea-v4",
            name = "Orea V4",
            geometry = BrewerGeometry.FLAT_BOTTOM,
            flowClass = FlowClass.MODERATE,
            flowMode = FlowMode.PERCOLATION,
            flowBehaviorDescription = "FAST/CLASSIC/OPEN/APEX menghasilkan flow berbeda",
            grindBaselineRelativeToV60 = "Medium–coarse baseline; CLASSIC lebih kasar, APEX lebih halus vs FAST",
            startingGrindText = "Medium–coarse baseline; CLASSIC lebih kasar, APEX lebih halus vs FAST pada resep tertentu",
            evidenceStatus = EvidenceStatus.MANUFACTURER_VERIFIED,
            sourceUrl = "https://www.orea.uk/guides-v4",
            supportedBottomVariants = listOf("FAST", "CLASSIC", "OPEN", "APEX"),
            compatibleFilterIds = listOf("filter-kalita-185", "filter-orea-wave-185")
        ),
        DripperModelRecord(
            id = "dripper-orea-v4-wide",
            name = "Orea V4 Wide",
            geometry = BrewerGeometry.FLAT_BOTTOM,
            flowClass = FlowClass.MODERATE,
            flowMode = FlowMode.PERCOLATION,
            flowBehaviorDescription = "Bergantung base dan filter; geometri wide mengubah drain",
            grindBaselineRelativeToV60 = "Coarser than V4 Narrow",
            startingGrindText = "Sedikit lebih kasar daripada V4 Narrow untuk resep sepadan (resmi OREA)",
            evidenceStatus = EvidenceStatus.MANUFACTURER_VERIFIED,
            sourceUrl = "https://www.orea.uk/guides-v4",
            supportedBottomVariants = listOf("FAST", "CLASSIC", "OPEN", "APEX"),
            compatibleFilterIds = listOf("filter-kalita-185", "filter-orea-wave-185")
        ),
        DripperModelRecord(
            id = "dripper-origami-s",
            name = "Origami S",
            geometry = BrewerGeometry.CONE_60,
            flowClass = FlowClass.FAST,
            flowMode = FlowMode.PERCOLATION,
            flowBehaviorDescription = "Berubah signifikan menurut filter cone vs wave; 20 ribs accelerate drainage",
            grindBaselineRelativeToV60 = "Equal to V60 (cone) or finer (wave)",
            startingGrindText = "Medium; kalibrasi ulang per filter",
            evidenceStatus = EvidenceStatus.THIRD_PARTY,
            compatibleFilterIds = listOf("filter-v60-01", "filter-kalita-155")
        ),
        DripperModelRecord(
            id = "dripper-timemore-b75",
            name = "Timemore B75",
            geometry = BrewerGeometry.FLAT_BOTTOM,
            flowClass = FlowClass.FAST,
            flowMode = FlowMode.PERCOLATION,
            flowBehaviorDescription = "Cepat; 20 channel flow, 75° steep flat geometry",
            grindBaselineRelativeToV60 = "Equal to slightly finer than V60",
            startingGrindText = "Medium; fine-tune menurut drawdown",
            evidenceStatus = EvidenceStatus.MANUFACTURER_VERIFIED,
            sourceUrl = "https://www.timemore.com/collections/brew-serve/products/timemore-crystal-eye-b75-dripper-pctg",
            compatibleFilterIds = listOf("filter-kalita-155")
        ),
        DripperModelRecord(
            id = "dripper-timemore-crystal-eye",
            name = "Timemore Crystal Eye",
            geometry = BrewerGeometry.CONE_60,
            flowClass = FlowClass.UNKNOWN,
            flowMode = FlowMode.PERCOLATION,
            flowBehaviorDescription = "Ribbed cone; laju spesifik varian belum tervalidasi",
            grindBaselineRelativeToV60 = "Equal to V60",
            startingGrindText = "Medium (awal; hipotesis)",
            evidenceStatus = EvidenceStatus.USER_SUPPLIED_UNVERIFIED,
            compatibleFilterIds = listOf("filter-v60-01", "filter-v60-02")
        ),
        DripperModelRecord(
            id = "dripper-timemore-crystal-eye-01",
            name = "Timemore Crystal Eye Dripper 01",
            geometry = BrewerGeometry.CONE_60,
            flowClass = FlowClass.UNKNOWN,
            flowMode = FlowMode.PERCOLATION,
            flowBehaviorDescription = "Belum terverifikasi terpisah dari Crystal Eye",
            grindBaselineRelativeToV60 = "Equal to V60",
            startingGrindText = "Medium (awal; hipotesis)",
            evidenceStatus = EvidenceStatus.USER_SUPPLIED_UNVERIFIED,
            compatibleFilterIds = listOf("filter-v60-01")
        ),
        DripperModelRecord(
            id = "dripper-timemore-crystal-eye-glass",
            name = "Timemore Crystal Eye Glass Dripper",
            geometry = BrewerGeometry.CONE_60,
            flowClass = FlowClass.UNKNOWN,
            flowMode = FlowMode.PERCOLATION,
            flowBehaviorDescription = "Belum terverifikasi khusus material/varian",
            grindBaselineRelativeToV60 = "Equal to V60",
            startingGrindText = "Medium (awal; hipotesis)",
            evidenceStatus = EvidenceStatus.USER_SUPPLIED_UNVERIFIED,
            compatibleFilterIds = listOf("filter-v60-01", "filter-v60-02")
        ),
        DripperModelRecord(
            id = "dripper-timemore-crystal-eye-turbo",
            name = "Timemore Crystal Eye Turbo Dripper",
            geometry = BrewerGeometry.CONE_60,
            flowClass = FlowClass.FAST,
            flowMode = FlowMode.PERCOLATION,
            flowBehaviorDescription = "Didesain beraliran cepat (perlu verifikasi spesifikasi)",
            grindBaselineRelativeToV60 = "Equal to slightly finer than V60",
            startingGrindText = "Medium–fine sampai medium (hipotesis)",
            evidenceStatus = EvidenceStatus.USER_SUPPLIED_UNVERIFIED,
            compatibleFilterIds = listOf("filter-v60-01")
        ),
        DripperModelRecord(
            id = "dripper-ufo-v3-01",
            name = "UFO V3 01",
            geometry = BrewerGeometry.CONE_80,
            flowClass = FlowClass.UNKNOWN,
            flowMode = FlowMode.PERCOLATION,
            flowBehaviorDescription = "Belum terverifikasi",
            grindBaselineRelativeToV60 = "Medium (hipotesis; tidak ada konversi pasti V60)",
            startingGrindText = "Medium (hipotesis; tidak ada konversi pasti V60)",
            evidenceStatus = EvidenceStatus.USER_SUPPLIED_UNVERIFIED,
            compatibleFilterIds = listOf("filter-v60-01")
        ),
        DripperModelRecord(
            id = "dripper-v60-neo-01-switch",
            name = "V60 Dripper Neo 01 + Switch",
            geometry = BrewerGeometry.IMMERSION_HYBRID,
            flowClass = FlowClass.VALVE_CONTROLLED,
            flowMode = FlowMode.HYBRID,
            flowBehaviorDescription = "Switch tutup: 0; buka: tergantung Neo dan filter",
            grindBaselineRelativeToV60 = "Equal to V60",
            startingGrindText = "Medium; sesuaikan mode immersion/percolation",
            evidenceStatus = EvidenceStatus.USER_SUPPLIED_UNVERIFIED,
            isValveEquipped = true,
            compatibleFilterIds = listOf("filter-v60-01")
        )
    )

    fun searchDrippers(query: String): List<DripperModelRecord> {
        val q = query.trim().lowercase(Locale.ROOT)
        if (q.isEmpty()) return ALL_26_DRIPPERS
        return ALL_26_DRIPPERS.filter {
            it.name.lowercase(Locale.ROOT).contains(q) ||
                it.flowBehaviorDescription.lowercase(Locale.ROOT).contains(q) ||
                it.geometry.name.lowercase(Locale.ROOT).contains(q)
        }
    }

    fun getDripperById(id: String): DripperModelRecord? {
        return ALL_26_DRIPPERS.firstOrNull { it.id == id }
    }

    fun checkFilterCompatibility(dripperId: String, filterId: String): FilterCompatibilityResult {
        val dripper = getDripperById(dripperId)
            ?: return FilterCompatibilityResult(false, "Unknown Dripper")
        val filter = FILTERS.firstOrNull { it.id == filterId }
            ?: return FilterCompatibilityResult(false, "Unknown Filter")

        val isDirectCompatible = dripper.compatibleFilterIds.contains(filter.id)
        if (isDirectCompatible) {
            return FilterCompatibilityResult(
                isCompatible = true,
                statusText = "VERIFIED COMPATIBLE",
                recommendationNote = "${filter.name} matches ${dripper.name} geometry (${dripper.geometry})."
            )
        }

        // Check geometry fallback
        val isGeometryMatch = when (dripper.geometry) {
            BrewerGeometry.CONE_60, BrewerGeometry.CONE_80 -> filter.shape.contains("Cone", ignoreCase = true)
            BrewerGeometry.FLAT_BOTTOM -> filter.shape.contains("Flat", ignoreCase = true) || filter.shape.contains("Wave", ignoreCase = true)
            BrewerGeometry.TRAPEZOID -> filter.shape.contains("Trapezoid", ignoreCase = true)
            BrewerGeometry.IMMERSION_HYBRID -> filter.shape.contains("Cone", ignoreCase = true)
            BrewerGeometry.NO_BYPASS -> filter.shape.contains("Disc", ignoreCase = true)
        }

        return if (isGeometryMatch) {
            FilterCompatibilityResult(
                isCompatible = true,
                statusText = "GEOMETRY COMPATIBLE",
                recommendationNote = "Filter shape fits dripper chamber."
            )
        } else {
            FilterCompatibilityResult(
                isCompatible = false,
                statusText = "GEOMETRY MISMATCH",
                recommendationNote = "Filter shape (${filter.shape}) does not match dripper bed (${dripper.geometry}). May require adaptor or fold."
            )
        }
    }

    fun getDripperFlowAdvice(
        dripperId: String,
        valveState: ValveState = ValveState.NOT_APPLICABLE,
        bottomVariant: String? = null
    ): DripperFlowAdvice {
        val dripper = getDripperById(dripperId)
            ?: return DripperFlowAdvice(
                flowClass = FlowClass.UNKNOWN,
                flowMode = FlowMode.PERCOLATION,
                qualitativeDrainSpeed = "Unknown",
                relativeGrindAdjustmentNote = "No data"
            )

        val valveNote = if (dripper.isValveEquipped) {
            when (valveState) {
                ValveState.CLOSED -> "Valve CLOSED: Fluid held in chamber (0 flow immersion stage)."
                ValveState.OPEN -> "Valve OPEN: Fluid drains freely through percolation bed."
                ValveState.NOT_APPLICABLE -> "Valve state not set (defaults to OPEN)."
            }
        } else {
            null
        }

        val relativeGrindNote = when (dripper.id) {
            "dripper-hario-v60-mugen" -> "Mugen single-pour: Grind slightly coarser than V60 baseline."
            "dripper-chemex-6cup" -> "Chemex thick bonded paper: Grind medium-coarse to avoid clogging."
            "dripper-orea-v4-wide" -> "Orea V4 Wide: Grind slightly coarser than standard V4 narrow for comparable extraction."
            "dripper-orea-v4" -> {
                when (bottomVariant?.uppercase(Locale.ROOT)) {
                    "CLASSIC" -> "CLASSIC bottom: Medium-coarse grind recommended."
                    "APEX" -> "APEX bottom: Finer grind compared to FAST base."
                    "FAST" -> "FAST bottom: Nominal pour over grind."
                    else -> dripper.grindBaselineRelativeToV60
                }
            }
            else -> dripper.grindBaselineRelativeToV60
        }

        val drainSpeed = when (dripper.flowClass) {
            FlowClass.FAST -> "Relatively fast drainage; sensitive to pouring agitation."
            FlowClass.MODERATE -> "Moderate, controlled drainage rate."
            FlowClass.RESTRICTED -> "Slow/restricted drainage; watch out for bed stalling."
            FlowClass.VALVE_CONTROLLED -> if (valveState == ValveState.CLOSED) "Held (Immersion)" else "Free percolation"
            FlowClass.UNKNOWN -> "Qualitative flow rate unverified (test empirically)."
        }

        return DripperFlowAdvice(
            flowClass = dripper.flowClass,
            flowMode = dripper.flowMode,
            qualitativeDrainSpeed = drainSpeed,
            valveNotice = valveNote,
            relativeGrindAdjustmentNote = relativeGrindNote
        )
    }

    fun calculateStagedPours(
        doseGrams: BigDecimal,
        waterGrams: BigDecimal,
        pourCount: Int = 3
    ): List<ManualStagedPour> {
        require(doseGrams > BigDecimal.ZERO) { "Dose must be positive." }
        require(waterGrams > BigDecimal.ZERO) { "Water mass must be positive." }

        // Standard Bloom: 3x dose
        val bloomMass = doseGrams.multiply(BigDecimal("3.0")).min(waterGrams.multiply(BigDecimal("0.25"))).setScale(1, RoundingMode.HALF_UP)
        val remainingWater = waterGrams.subtract(bloomMass)
        val remainingPours = maxOf(1, pourCount - 1)
        val pulseMass = remainingWater.divide(BigDecimal(remainingPours), 1, RoundingMode.HALF_UP)

        val stages = mutableListOf<ManualStagedPour>()
        var cumMass = bloomMass
        stages.add(
            ManualStagedPour(
                stageIndex = 1,
                name = "Bloom",
                pourMassGrams = bloomMass,
                cumulativeMassGrams = cumMass,
                durationSeconds = 45,
                notes = "Saturate all dry grounds completely; allow degassing."
            )
        )

        for (i in 1..remainingPours) {
            val mass = if (i == remainingPours) {
                // Ensure exact sum matches waterGrams
                waterGrams.subtract(cumMass)
            } else {
                pulseMass
            }
            cumMass = cumMass.add(mass)
            stages.add(
                ManualStagedPour(
                    stageIndex = i + 1,
                    name = if (i == remainingPours) "Final Pour" else "Pour #$i",
                    pourMassGrams = mass,
                    cumulativeMassGrams = cumMass,
                    durationSeconds = 40,
                    notes = "Gentle circular pour towards outer edge."
                )
            )
        }

        return stages
    }
}
