package com.coffeecreator.app.core.model

import java.math.BigDecimal
import java.math.RoundingMode

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
    val country: String,
    val region: String,
    val producer: String = "Unknown",
    val variety: String = "Arabica",
    val process: String = "Washed", // Washed, Natural, Honey, Wet Hulled, Anaerobic
    val altitudeMeters: Int? = null,
    val roastLevel: String = "Medium", // Light, Medium-Light, Medium, Medium-Dark, Dark
    val roastDate: String? = null,
    val degasDays: Int? = null,
    val blendComponents: List<BlendComponent> = emptyList(),
    val densityGramsPerMl: Double? = null,
    val allergens: String = "None",
    val foodGrade: Boolean = true,
    val supplierLimitsNotes: String? = null,
    val isCuratedSeed: Boolean = false
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
    val equipment: String = "Hario V60 02, Fellow Stagg EKG",
    val doseGrams: Double = 18.0,
    val waterGrams: Double = 300.0,
    val beverageMassGrams: Double = 270.0,
    val bypassGrams: Double = 0.0,
    val agitation: String = "Gentle spiral pour",
    val measuredTdsPercent: Double? = 1.30, // Measured with refractometer
    val targetRatio: String = "1:16.67",
    val temperatureCelsius: Double = 93.0,
    val pressureBars: Double? = null,
    val brewTimeSeconds: Int = 180,
    val grindSize: String = "Medium-Fine (Comandante 22 clicks)",
    val brewSteps: List<BrewStep> = emptyList(),
    val notes: String = "",
    val version: Int = 1,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

/**
 * Pure calculation results with explicit provenance, units, and assumptions.
 */
sealed class CalculationResult {
    data class ExtractionYield(
        val extractionYieldPercent: Double,
        val beverageMassGrams: Double,
        val measuredTdsPercent: Double,
        val doseGrams: Double,
        val methodLabel: String = "Direct Mass Balance",
        val assumptions: String = "Extraction Yield = (Beverage Mass * TDS%) / Dose Mass",
        val provenance: String = "Tool-calculated with pure Kotlin BigDecimal (Scale 2)"
    ) : CalculationResult()

    data class BrewRatio(
        val ratioNumerator: Double = 1.0,
        val ratioDenominator: Double,
        val ratioString: String,
        val doseGrams: Double,
        val waterGrams: Double,
        val methodLabel: String = "Mass:Mass Direct Ratio",
        val provenance: String = "Deterministic calculation"
    ) : CalculationResult()

    data class Dilution(
        val initialMassGrams: Double,
        val initialTdsPercent: Double,
        val addedWaterGrams: Double,
        val finalMassGrams: Double,
        val finalTdsPercent: Double,
        val methodLabel: String = "Mass-Fraction Weighted Dilution",
        val provenance: String = "Pure mass conservation formula"
    ) : CalculationResult()

    data class Concentration(
        val soluteMassGrams: Double,
        val solutionMassGrams: Double,
        val massFractionPercent: Double,
        val ppm: Double,
        val provenance: String = "Solute mass over total solution mass"
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
    val assessor: String = "Head Barista",
    val timestamp: Long = System.currentTimeMillis()
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
    val confidencePercent: Int = 75,
    val uncertaintyDisclaimer: String = "Estimate is a descriptive heuristic based on roast level, origin, and extraction yield. Not a laboratory-calibrated physical measurement."
)

/**
 * Knowledge document for local offline RAG.
 */
data class KnowledgeDocument(
    val id: String,
    val title: String,
    val sourceUri: String = "internal://assets/manual.txt",
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
    val format: String = "GGUF Q4_K_M",
    val expectedSizeBytes: Long,
    val sha256: String,
    val downloadUrl: String,
    val license: String = "Apache-2.0 / Open RAIL",
    val status: ModelStatus = ModelStatus.CATALOGUED,
    val localUri: String? = null,
    val downloadedBytes: Long = 0L,
    val progressPercent: Int = 0
)

enum class ModelStatus {
    CATALOGUED,
    DOWNLOADING,
    PAUSED,
    VERIFYING,
    INSTALLED,
    FAILED
}
