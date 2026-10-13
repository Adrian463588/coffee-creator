package com.coffeecreator.app.core.ai

import com.coffeecreator.app.core.data.DataRepository
import com.coffeecreator.app.core.engine.CoffeeScienceEngine
import com.coffeecreator.app.core.engine.SensoryRulesEngine
import com.coffeecreator.app.core.model.CalculationResult
import com.coffeecreator.app.core.model.CoffeeBean
import com.coffeecreator.app.core.model.Recipe

data class ToolDefinition(
    val name: String,
    val description: String,
    val parametersDescription: String,
    val isWriteOperation: Boolean = false
)

data class ToolExecutionResult(
    val toolName: String,
    val isSuccess: Boolean,
    val output: String,
    val requiresUserConfirmation: Boolean = false,
    val structuredData: Any? = null
)

object ToolExecutor {

    val registry: List<ToolDefinition> = listOf(
        ToolDefinition(
            name = "calculate_brew_ratio",
            description = "Calculate deterministic brew water-to-coffee ratio.",
            parametersDescription = "doseGrams: Double, waterGrams: Double"
        ),
        ToolDefinition(
            name = "calculate_extraction_yield",
            description = "Calculate deterministic Extraction Yield (%) from measured beverage mass, TDS%, and dose.",
            parametersDescription = "doseGrams: Double, beverageMassGrams: Double, measuredTdsPercent: Double"
        ),
        ToolDefinition(
            name = "calculate_dilution",
            description = "Calculate bypass water addition or concentrate dilution.",
            parametersDescription = "initialMassGrams: Double, initialTdsPercent: Double, addedWaterGrams: Double"
        ),
        ToolDefinition(
            name = "convert_units",
            description = "Convert liquid volume (mL) to mass (g) with required density.",
            parametersDescription = "volumeMl: Double, densityGramsPerMl: Double?"
        ),
        ToolDefinition(
            name = "lookup_ingredient",
            description = "Lookup ingredient density, allergen status, and food-grade limits.",
            parametersDescription = "query: String"
        ),
        ToolDefinition(
            name = "lookup_coffee_origin",
            description = "Lookup coffee bean origins, altitude, and processing methods from catalog.",
            parametersDescription = "countryOrRegion: String"
        ),
        ToolDefinition(
            name = "search_documents",
            description = "Search local imported coffee brewing standards and documents.",
            parametersDescription = "query: String"
        ),
        ToolDefinition(
            name = "estimate_sensory_profile",
            description = "Generate heuristic sensory direction (clearly labeled uncalibrated).",
            parametersDescription = "roastLevel: String, process: String, extractionYieldPercent: Double?"
        ),
        ToolDefinition(
            name = "recommend_recipe",
            description = "Propose a baseline brew formulation for a specific bean or flavor profile.",
            parametersDescription = "targetFlavor: String, brewMethod: String"
        ),
        ToolDefinition(
            name = "save_recipe",
            description = "Save proposed recipe to the local database (Requires explicit confirmation).",
            parametersDescription = "name: String, doseGrams: Double, waterGrams: Double, brewMethod: String",
            isWriteOperation = true
        )
    )

    fun execute(toolName: String, arguments: Map<String, Any?>): ToolExecutionResult {
        return try {
            when (toolName) {
                "calculate_extraction_yield" -> {
                    val dose = (arguments["doseGrams"] as? Number)?.toDouble() ?: 18.0
                    val beverage = (arguments["beverageMassGrams"] as? Number)?.toDouble() ?: 270.0
                    val tds = (arguments["measuredTdsPercent"] as? Number)?.toDouble() ?: 1.30

                    val result = CoffeeScienceEngine.calculateExtractionYield(dose, beverage, tds)
                    if (result.isSuccess) {
                        val ey = result.getOrThrow()
                        ToolExecutionResult(
                            toolName = toolName,
                            isSuccess = true,
                            output = "Extraction Yield: ${ey.extractionYieldPercent}%\nMethod: ${ey.methodLabel}\nAssumptions: ${ey.assumptions}\nProvenance: ${ey.provenance}",
                            structuredData = ey
                        )
                    } else {
                        ToolExecutionResult(
                            toolName = toolName,
                            isSuccess = false,
                            output = "Calculation failed: ${result.exceptionOrNull()?.message}"
                        )
                    }
                }

                "calculate_brew_ratio" -> {
                    val dose = (arguments["doseGrams"] as? Number)?.toDouble() ?: 15.0
                    val water = (arguments["waterGrams"] as? Number)?.toDouble() ?: 250.0
                    val result = CoffeeScienceEngine.calculateBrewRatio(dose, water)
                    if (result.isSuccess) {
                        val ratio = result.getOrThrow()
                        ToolExecutionResult(
                            toolName = toolName,
                            isSuccess = true,
                            output = "Brew Ratio: ${ratio.ratioString} (${ratio.waterGrams}g water / ${ratio.doseGrams}g dose)",
                            structuredData = ratio
                        )
                    } else {
                        ToolExecutionResult(
                            toolName = toolName,
                            isSuccess = false,
                            output = "Error: ${result.exceptionOrNull()?.message}"
                        )
                    }
                }

                "calculate_dilution" -> {
                    val initialMass = (arguments["initialMassGrams"] as? Number)?.toDouble() ?: 100.0
                    val initialTds = (arguments["initialTdsPercent"] as? Number)?.toDouble() ?: 2.00
                    val addedWater = (arguments["addedWaterGrams"] as? Number)?.toDouble() ?: 100.0
                    val result = CoffeeScienceEngine.calculateDilution(initialMass, initialTds, addedWater)
                    if (result.isSuccess) {
                        val dil = result.getOrThrow()
                        ToolExecutionResult(
                            toolName = toolName,
                            isSuccess = true,
                            output = "Final Mass: ${dil.finalMassGrams}g, Final TDS: ${dil.finalTdsPercent}%\nProvenance: ${dil.provenance}",
                            structuredData = dil
                        )
                    } else {
                        ToolExecutionResult(toolName = toolName, isSuccess = false, output = "Error: ${result.exceptionOrNull()?.message}")
                    }
                }

                "convert_units" -> {
                    val volume = (arguments["volumeMl"] as? Number)?.toDouble() ?: 250.0
                    val density = (arguments["densityGramsPerMl"] as? Number)?.toDouble()
                    val result = CoffeeScienceEngine.convertVolumeToMass(volume, density)
                    if (result.isSuccess) {
                        ToolExecutionResult(
                            toolName = toolName,
                            isSuccess = true,
                            output = "Converted Mass: ${result.getOrThrow()}g for $volume mL at density $density g/mL"
                        )
                    } else {
                        ToolExecutionResult(
                            toolName = toolName,
                            isSuccess = false,
                            output = "Conversion failed: ${result.exceptionOrNull()?.message}"
                        )
                    }
                }

                "lookup_ingredient" -> {
                    val query = (arguments["query"] as? String)?.lowercase() ?: ""
                    val matched = DataRepository.beans.value.filter {
                        it.name.lowercase().contains(query) || it.allergens.lowercase().contains(query)
                    }
                    val info = if (matched.isNotEmpty()) {
                        matched.joinToString("\n---\n") {
                            "Item: ${it.name}\nDensity: ${it.densityGramsPerMl ?: "Unknown"} g/mL\nAllergens: ${it.allergens}\nFood Grade: ${it.foodGrade}\nNotes: ${it.supplierLimitsNotes ?: "N/A"}"
                        }
                    } else {
                        "No specific ingredient matched for query '$query'. Allergen standard: safe food-grade only."
                    }
                    ToolExecutionResult(toolName = toolName, isSuccess = true, output = info)
                }

                "lookup_coffee_origin" -> {
                    val origin = (arguments["countryOrRegion"] as? String)?.lowercase() ?: ""
                    val matches = DataRepository.beans.value.filter {
                        it.country.lowercase().contains(origin) || it.region.lowercase().contains(origin)
                    }
                    val text = if (matches.isNotEmpty()) {
                        matches.joinToString("\n\n") {
                            "• ${it.name} (${it.country}, ${it.region})\n  Process: ${it.process} | Altitude: ${it.altitudeMeters ?: "Unknown"}m\n  Roast: ${it.roastLevel} | Variety: ${it.variety}"
                        }
                    } else {
                        "No origin records found for '$origin' in local catalog."
                    }
                    ToolExecutionResult(toolName = toolName, isSuccess = true, output = text)
                }

                "search_documents" -> {
                    val query = (arguments["query"] as? String) ?: ""
                    val ragResult = HybridRagEngine.search(query)
                    ToolExecutionResult(
                        toolName = toolName,
                        isSuccess = true,
                        output = ragResult.text,
                        structuredData = ragResult.citations
                    )
                }

                "estimate_sensory_profile" -> {
                    val roast = (arguments["roastLevel"] as? String) ?: "Light"
                    val process = (arguments["process"] as? String) ?: "Washed"
                    val ey = (arguments["extractionYieldPercent"] as? Number)?.toDouble() ?: 19.5
                    val dummyBean = CoffeeBean(
                        id = "temp",
                        name = "Sample",
                        country = "Ethiopia",
                        region = "Yirgacheffe",
                        roastLevel = roast,
                        process = process
                    )
                    val dummyRecipe = Recipe(
                        id = "temp-rec",
                        name = "Sample"
                    )
                    val estimate = SensoryRulesEngine.estimateProfile(dummyBean, dummyRecipe, ey)
                    ToolExecutionResult(
                        toolName = toolName,
                        isSuccess = true,
                        output = "Estimated Sensory Direction:\n• Aroma: ${estimate.aroma}/10\n• Acidity: ${estimate.acidity}/10\n• Sweetness: ${estimate.sweetness}/10\n• Body: ${estimate.body}/10\n• Finish: ${estimate.finish}/10\n• Balance: ${estimate.balance}/10\nDescriptors: ${estimate.suggestedDescriptors.joinToString(", ")}\n${estimate.uncertaintyDisclaimer}",
                        structuredData = estimate
                    )
                }

                "recommend_recipe" -> {
                    val method = (arguments["brewMethod"] as? String) ?: "Pour Over (V60)"
                    val flavor = (arguments["targetFlavor"] as? String) ?: "Bright & Floral"
                    val rec = "Recommended Recipe for $method ($flavor):\n• Dose: 18.0g\n• Brew Water: 300.0g (Ratio 1:16.67)\n• Temp: 93°C\n• Grind: Medium-Fine\n• Target Yield: 270g cup, 1.30% TDS (19.5% Extraction Yield)"
                    ToolExecutionResult(toolName = toolName, isSuccess = true, output = rec)
                }

                "save_recipe" -> {
                    // Confirmed write gate
                    val name = (arguments["name"] as? String) ?: "New Formulation"
                    val dose = (arguments["doseGrams"] as? Number)?.toDouble() ?: 18.0
                    val water = (arguments["waterGrams"] as? Number)?.toDouble() ?: 300.0
                    val method = (arguments["brewMethod"] as? String) ?: "V60"

                    ToolExecutionResult(
                        toolName = toolName,
                        isSuccess = true,
                        output = "Recipe draft prepared: '$name' ($dose g / $water g via $method). Confirm write operation to persist to local database?",
                        requiresUserConfirmation = true,
                        structuredData = mapOf("name" to name, "dose" to dose, "water" to water, "method" to method)
                    )
                }

                else -> ToolExecutionResult(
                    toolName = toolName,
                    isSuccess = false,
                    output = "Tool '$toolName' is not in the allowlist registry."
                )
            }
        } catch (e: Exception) {
            ToolExecutionResult(
                toolName = toolName,
                isSuccess = false,
                output = "Exception during tool execution: ${e.message}"
            )
        }
    }
}
