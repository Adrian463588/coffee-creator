package com.coffeecreator.app.core.model

import java.math.BigDecimal

const val COFFEE_SCIENCE_VERSION = "coffee-science/2.0.0"

/** Canonical quantities keep exact decimal input and prevent accidental unit mixing. */
@JvmInline
value class MassGrams(val value: BigDecimal) {
    init {
        require(value.signum() >= 0 && value.toDouble().isFinite()) { "Mass must be finite and nonnegative (g)." }
    }
}

@JvmInline
value class MassMilligrams(val value: BigDecimal) {
    init {
        require(value.signum() >= 0 && value.toDouble().isFinite()) { "Mass must be finite and nonnegative (mg)." }
    }

    fun toGrams(): MassGrams = MassGrams(value.movePointLeft(3))
}

@JvmInline
value class VolumeMillilitres(val value: BigDecimal) {
    init {
        require(value.signum() >= 0 && value.toDouble().isFinite()) { "Volume must be finite and nonnegative (mL)." }
    }
}

@JvmInline
value class DensityGramsPerMl(val value: BigDecimal) {
    init {
        require(value.signum() > 0 && value.toDouble().isFinite()) { "Density must be finite and positive (g/mL)." }
    }
}

@JvmInline
value class MassFractionPercent(val value: BigDecimal) {
    init {
        require(value >= BigDecimal.ZERO && value <= BigDecimal("100")) { "Mass fraction must be between 0 and 100 (% w/w)." }
    }
}

@JvmInline
value class TemperatureCelsius(val value: BigDecimal) {
    init {
        require(value >= BigDecimal("-273.15") && value.toDouble().isFinite()) { "Temperature must be finite and at least absolute zero (°C)." }
    }
}

data class Ingredient(
    val id: String,
    val name: String,
    val densityGramsPerMl: Double? = null,
    val allergens: List<String> = emptyList(),
    val foodGrade: Boolean? = null,
    val supplierMaxPercent: Double? = null,
    val provenance: String = "Unknown"
)

/** quantity is the supplied food-grade ingredient/solution, never inferred active-compound mass. */
data class RecipeLine(
    val ingredientId: String,
    val quantity: Double,
    val unit: String = "g",
    val densityGramsPerMl: Double? = null
)

/**
 * Blend component for multi-bean lots.
 */
data class BlendComponent(
    val beanId: String,
    val beanName: String,
    val percentage: Double // 0.0 to 100.0
)

/**
 * Curated and user-created coffee beans with origin, processing, and provenance metadata.
 */
data class CoffeeBean(
    val id: String,
    val name: String,
    val country: String = "Unknown",
    val region: String = "Unknown",
    val producer: String = "Unknown",
    val variety: String = "Unknown",
    val process: String = "Unknown",
    val altitudeMeters: Int? = null,
    val roastLevel: String = "Unknown",
    val roastDate: String? = null,
    val degasDays: Int? = null,
    val blendComponents: List<BlendComponent> = emptyList(),
    val densityGramsPerMl: Double? = null,
    val allergens: String = "Unknown",
    val foodGrade: Boolean? = null,
    val supplierLimitsNotes: String? = null,
    val isCuratedSeed: Boolean = false,
    val provenance: String = "Unknown"
)

/**
 * Multi-step brew guide for timer.
 */
data class BrewStep(
    val stepNumber: Int,
    val description: String,
    val targetWaterGrams: Double,
    val durationSeconds: Int
)

/**
 * Full recipe formulation.
 */
data class Recipe(
    val id: String,
    val name: String,
    val beanId: String? = null,
    val brewMethod: String = "Pour Over (V60)", // V60, Aeropress, Espresso, French Press, Chemex, Immersion
    val equipment: String = "",
    val doseGrams: Double = 18.0,
    val waterGrams: Double = 300.0,
    val beverageMassGrams: Double? = null,
    val bypassGrams: Double = 0.0,
    val agitation: String = "",
    val measuredTdsPercent: Double? = null,
    val targetRatio: String = "",
    val temperatureCelsius: Double = 93.0,
    val pressureBars: Double? = null,
    val brewTimeSeconds: Int = 180,
    val grindSize: String = "",
    val brewSteps: List<BrewStep> = emptyList(),
    val notes: String = "",
    val version: Int = 1,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val lines: List<RecipeLine> = emptyList(),
    val revisionGroupId: String = id,
    val acknowledgedAllergens: List<String> = emptyList()
)

data class CalculationMetadata(
    val methodVersion: String = COFFEE_SCIENCE_VERSION,
    val exactInputs: Map<String, String> = emptyMap(),
    val inputUnits: Map<String, String> = emptyMap(),
    val inputProvenance: Map<String, String> = emptyMap(),
    val uncertainty: String = "Input uncertainty was not supplied; no laboratory accuracy is claimed."
)

/**
 * Pure calculation results with explicit provenance, units, and assumptions.
 */
sealed class CalculationResult {
    abstract val metadata: CalculationMetadata
    val methodVersion: String get() = metadata.methodVersion
    val exactInputs: Map<String, String> get() = metadata.exactInputs
    val inputUnits: Map<String, String> get() = metadata.inputUnits
    val inputProvenance: Map<String, String> get() = metadata.inputProvenance
    val uncertainty: String get() = metadata.uncertainty

    data class ExtractionYield(
        val extractionYieldPercent: Double,
        val beverageMassGrams: Double,
        val measuredTdsPercent: Double,
        val doseGrams: Double,
        val methodLabel: String = "Direct Mass Balance",
        val assumptions: String = "Extraction Yield = (Beverage Mass * TDS%) / Dose Mass",
        val provenance: String = "Calculated from caller-provided measurements, not independently verified.",
        override val metadata: CalculationMetadata = CalculationMetadata()
    ) : CalculationResult()

    data class BrewRatio(
        val ratioNumerator: Double = 1.0,
        val ratioDenominator: Double,
        val ratioString: String,
        val doseGrams: Double,
        val waterGrams: Double,
        val methodLabel: String = "Mass:Mass Direct Ratio",
        val provenance: String = "Calculated from caller-provided formulation masses.",
        val assumptions: String = "Mass ratio; water and coffee dose must use the same mass unit.",
        override val metadata: CalculationMetadata = CalculationMetadata()
    ) : CalculationResult()

    data class Dilution(
        val initialMassGrams: Double,
        val initialTdsPercent: Double,
        val addedWaterGrams: Double,
        val finalMassGrams: Double,
        val finalTdsPercent: Double,
        val methodLabel: String = "Mass-Fraction Weighted Dilution",
        val provenance: String = "Calculated from caller-provided mass and measured concentration.",
        val assumptions: String = "Added water contributes no dissolved solute; masses are conserved.",
        override val metadata: CalculationMetadata = CalculationMetadata()
    ) : CalculationResult()

    data class Concentration(
        val soluteMassGrams: Double,
        val solutionMassGrams: Double,
        val massFractionPercent: Double,
        val ppm: Double,
        val provenance: String = "Calculated from caller-provided solute and total solution masses.",
        val methodLabel: String = "Mass-Fraction Concentration",
        val assumptions: String = "Solution mass includes solute; ppm is by mass, not by volume.",
        override val metadata: CalculationMetadata = CalculationMetadata()
    ) : CalculationResult()

    data class UnitConversion(
        val volumeMl: Double,
        val densityGramsPerMl: Double,
        val massGrams: Double,
        val methodLabel: String = "Density-Based Volume to Mass",
        val assumptions: String = "Density must describe this material at the relevant temperature; no density is assumed.",
        val provenance: String = "Calculated from caller-provided volume and density.",
        override val metadata: CalculationMetadata = CalculationMetadata()
    ) : CalculationResult()
}

/**
 * Observed sensory assessment (human evaluation).
 */
data class SensoryObservation(
    val id: String,
    val recipeId: String,
    val aroma: Int, // 1 to 10
    val acidity: Int, // 1 to 10
    val sweetness: Int, // 1 to 10
    val bitterness: Int = 3, // 1 to 10
    val body: Int, // 1 to 10
    val clarity: Int = 8, // 1 to 10
    val finish: Int, // 1 to 10
    val balance: Int, // 1 to 10
    val tastingDescriptors: List<String> = emptyList(), // e.g. "Jasmine", "Bergamot", "Stone Fruit"
    val notes: String = "",
    val assessor: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val panel: String = "",
    val descriptorIntensities: Map<String, Int> = emptyMap()
)

/**
 * Estimated sensory direction (rules-based, labeled non-deterministic).
 */
data class SensoryEstimate(
    val aroma: Int,
    val acidity: Int,
    val sweetness: Int,
    val bitterness: Int = 3,
    val body: Int,
    val clarity: Int = 8,
    val finish: Int,
    val balance: Int,
    val suggestedDescriptors: List<String>,
    val method: String = "Heuristic Rule-Based Profile",
    val confidencePercent: Int? = null,
    val uncertaintyDisclaimer: String = "Uncalibrated directional heuristic; validate by observation. No measured intensity or laboratory accuracy is claimed.",
    val basis: String = "No directional basis provided."
)

/**
 * Knowledge document for local offline RAG.
 */
data class KnowledgeDocument(
    val id: String,
    val title: String,
    val sourceUri: String = "",
    val fileType: String = "TXT",
    val pageCount: Int = 1,
    val chunkCount: Int = 0,
    val contentHash: String = "",
    val importedAt: Long = System.currentTimeMillis()
)

/**
 * Chunked document with citations.
 */
data class DocumentChunk(
    val id: String,
    val documentId: String,
    val documentTitle: String,
    val pageNumber: Int,
    val chunkIndex: Int,
    val content: String,
    val citationText: String = "[$documentTitle, P.$pageNumber, Chunk $chunkIndex]"
)

/**
 * Chat message for AI conversation.
 */
data class ChatMessage(
    val id: String,
    val role: String, // "user", "assistant", "system", "tool"
    val content: String,
    val toolCallName: String? = null,
    val toolArgumentsJson: String? = null,
    val toolResultJson: String? = null,
    val citations: List<String> = emptyList(),
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Persona for local assistant.
 */
data class AssistantPersona(
    val id: String,
    val title: String,
    val description: String,
    val systemPrompt: String
)

/**
 * Model asset for SAF local GGUF / ONNX manager.
 */
data class ModelAsset(
    val id: String,
    val name: String,
    val revision: String,
    val format: String = "Unknown",
    val expectedSizeBytes: Long,
    val sha256: String,
    val downloadUrl: String,
    val license: String = "Unknown",
    val status: ModelStatus = ModelStatus.CATALOGUED,
    val localUri: String? = null,
    val downloadedBytes: Long = 0L,
    val progressPercent: Int = 0,
    val runtime: String = "llama.cpp",
    val tokenizerSupport: String = "embedded GGUF",
    val error: String? = null,
    val etag: String? = null
)

enum class ModelStatus {
    CATALOGUED,
    QUEUED,
    DOWNLOADING,
    PAUSED,
    VERIFYING,
    PUBLISHING,
    INSTALLED,
    LOADING,
    READY,
    FAILED,
    CANCELLED
}
