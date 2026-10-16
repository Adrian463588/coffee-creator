package com.coffeecreator.app.core.ai

import com.coffeecreator.app.core.ai.personalization.FeedbackLoopEngine
import com.coffeecreator.app.core.ai.personalization.IngredientSafetyValidator
import com.coffeecreator.app.core.ai.personalization.SensoryFeedbackDiagnosis
import com.coffeecreator.app.core.ai.personalization.WeatherAltitudeCalculator
import com.coffeecreator.app.core.engine.CoffeeScienceEngine
import com.coffeecreator.app.core.engine.DomainValidation
import com.coffeecreator.app.core.engine.JsonInputPolicy
import com.coffeecreator.app.core.engine.SensoryRulesEngine
import com.coffeecreator.app.core.model.CoffeeBean
import com.coffeecreator.app.core.model.Ingredient
import com.coffeecreator.app.core.model.InputLimits
import com.coffeecreator.app.core.model.Recipe
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.Strictness
import java.util.UUID
import kotlinx.coroutines.CancellationException

data class ToolDefinition(
    val name: String,
    val description: String,
    val parameters: Map<String, String>,
    val isWriteOperation: Boolean = false
)
data class ToolExecutionResult(
    val toolName: String,
    val isSuccess: Boolean,
    val output: String,
    val requiresUserConfirmation: Boolean = false,
    val structuredData: Any? = null,
    val inputConfirmationJson: String? = null
)

class ToolExecutor(
    private val beans: () -> List<CoffeeBean>,
    private val ingredients: () -> List<Ingredient>,
    private val recipes: () -> List<Recipe>,
    private val search: suspend (String) -> RagSearchResult
) {
    private val gson = GsonBuilder().setStrictness(Strictness.STRICT).create()
    val registry = listOf(
        ToolDefinition("calculate_brew_ratio", "Water:coffee ratio by mass", mapOf("doseGrams" to "number", "waterGrams" to "number")),
        ToolDefinition(
            "calculate_extraction_yield",
            "Approximate EY from explicit measurements",
            mapOf(
                "doseGrams" to "number",
                "beverageMassGrams" to "number",
                "measuredTdsPercent" to "number"
            )
        ),
        ToolDefinition(
            "calculate_dilution",
            "Mass-conserving dilution",
            mapOf(
                "initialMassGrams" to "number",
                "initialTdsPercent" to "number",
                "addedWaterGrams" to "number"
            )
        ),
        ToolDefinition(
            "calculate_concentration_ppm",
            "Concentration by solution mass",
            mapOf(
                "soluteMassGrams" to "number",
                "solutionMassGrams" to "number"
            )
        ),
        ToolDefinition(
            "convert_units",
            "Convert mL to g with explicit density",
            mapOf(
                "volumeMl" to "number",
                "densityGramsPerMl" to "number"
            )
        ),
        ToolDefinition("lookup_ingredient", "Local ingredient constraints and provenance", mapOf("query" to "string")),
        ToolDefinition("lookup_coffee_origin", "Local coffee catalog provenance", mapOf("query" to "string")),
        ToolDefinition("search_documents", "Evidence from imported local sources", mapOf("query" to "string")),
        ToolDefinition("estimate_sensory_profile", "Uncalibrated directional estimate", mapOf("recipeId" to "string")),
        ToolDefinition(
            "recommend_recipe",
            "Prepare a draft using explicit requested quantities; no sensory guarantee",
            mapOf(
                "name" to "string",
                "doseGrams" to "number",
                "waterGrams" to "number",
                "brewMethod" to "string"
            )
        ),
        ToolDefinition(
            "save_recipe",
            "Prepare a recipe for explicit user confirmation",
            mapOf(
                "name" to "string",
                "doseGrams" to "number",
                "waterGrams" to "number",
                "brewMethod" to "string"
            ),
            isWriteOperation = true
        ),
        ToolDefinition(
            "calculate_altitude_boiling_point",
            "Thermodynamic boiling point and maximum brew water temperature for given elevation",
            mapOf("elevationMeters" to "number")
        ),
        ToolDefinition(
            "audit_ingredient_safety",
            "Verify food-grade status, cosmetic chemical absence, and allergen presence",
            mapOf("ingredientName" to "string", "massFractionPercent" to "number")
        ),
        ToolDefinition(
            "propose_feedback_adjustment",
            "Single-variable-at-a-time (OVAT) correction from explicit recipe settings and diagnosis",
            mapOf(
                "actualDurationSeconds" to "number",
                "targetDurationSeconds" to "number",
                "diagnosis" to "string",
                "grindClicks" to "number",
                "waterTempCelsius" to "number",
                "doseGrams" to "number",
                "waterGrams" to "number",
                "boilingPointCelsius" to "number"
            )
        ),
        ToolDefinition(
            "recommend_inventory_bean",
            "Recommend only from verified available inventory and a recorded sensory profile",
            mapOf("brewMethod" to "string")
        )
    )

    suspend fun executeJson(json: String): ToolExecutionResult = try {
        require(json.length <= InputLimits.TOOL_JSON_CHARS) { "Tool request too large." }
        JsonInputPolicy.validateDepth(json)
        val root = gson.fromJson(json, JsonObject::class.java)
        require(root.keySet() == setOf("tool", "arguments")) { "Tool JSON requires exactly tool and arguments." }
        require(root["tool"].isJsonPrimitive && root["tool"].asJsonPrimitive.isString) { "Tool name must be a string." }
        val name = root["tool"].asString
        require(root["arguments"].isJsonObject) { "Arguments must be an object." }
        val args = root["arguments"].asJsonObject.entrySet().associate { (key, value) ->
            require(value.isJsonPrimitive) { "Only typed scalar arguments are allowed." }
            key to when {
                value.asJsonPrimitive.isNumber -> value.asDouble
                value.asJsonPrimitive.isString -> value.asString
                else -> error("Unsupported argument type.")
            }
        }
        execute(name, args)
    } catch (cancelled: CancellationException) {
        throw cancelled
    } catch (boundaryFailure: Exception) {
        ToolExecutionResult("invalid", false, boundaryFailure.message ?: "Malformed tool schema.")
    }

    suspend fun execute(toolName: String, arguments: Map<String, Any?>): ToolExecutionResult = try {
        val tool = registry.find { it.name == toolName } ?: error("Tool is not allowlisted: $toolName")
        validateArguments(tool, arguments)
        val result = dispatchTool(toolName, ToolArguments(arguments))
        val confirmation = tool.isWriteOperation
        ToolExecutionResult(toolName, true, gson.toJson(result), confirmation, result)
    } catch (cancelled: CancellationException) {
        throw cancelled
    } catch (boundaryFailure: Exception) {
        ToolExecutionResult(toolName, false, boundaryFailure.message ?: "Tool validation failed.")
    }

    private suspend fun dispatchTool(toolName: String, arguments: ToolArguments): Any = when (toolName) {
        in CALCULATION_TOOLS -> executeCalculation(toolName, arguments)
        in LOOKUP_TOOLS -> executeLookup(toolName, arguments)
        "estimate_sensory_profile" -> estimateSensoryProfile(arguments.string("recipeId"))
        "recommend_recipe", "save_recipe" -> createRecipe(arguments)
        "calculate_altitude_boiling_point" -> calculateAltitudeBoilingPoint(arguments.number("elevationMeters"))
        "audit_ingredient_safety" -> auditIngredientSafety(arguments)
        "propose_feedback_adjustment" -> proposeFeedbackAdjustment(arguments)
        "recommend_inventory_bean" -> error(
            "Verified bag inventory and a recorded sensory profile are not available to this tool."
        )
        else -> error("Tool not implemented.")
    }

    private fun executeCalculation(toolName: String, arguments: ToolArguments): Any = when (toolName) {
        "calculate_brew_ratio" -> CoffeeScienceEngine.calculateBrewRatio(
            arguments.number("doseGrams"),
            arguments.number("waterGrams")
        ).getOrThrow()
        "calculate_extraction_yield" -> CoffeeScienceEngine.calculateExtractionYield(
            arguments.number("doseGrams"),
            arguments.number("beverageMassGrams"),
            arguments.number("measuredTdsPercent")
        ).getOrThrow()
        "calculate_dilution" -> CoffeeScienceEngine.calculateDilution(
            arguments.number("initialMassGrams"),
            arguments.number("initialTdsPercent"),
            arguments.number("addedWaterGrams")
        ).getOrThrow()
        "calculate_concentration_ppm" -> CoffeeScienceEngine.calculateConcentration(
            arguments.number("soluteMassGrams"),
            arguments.number("solutionMassGrams")
        ).getOrThrow()
        "convert_units" -> CoffeeScienceEngine.calculateVolumeToMass(
            arguments.number("volumeMl"),
            arguments.number("densityGramsPerMl")
        ).getOrThrow()
        else -> error("Unsupported calculation tool: $toolName")
    }

    private suspend fun executeLookup(toolName: String, arguments: ToolArguments): Any {
        val query = arguments.string("query")
        return when (toolName) {
            "lookup_ingredient" -> ingredients().filter { it.name.contains(query, ignoreCase = true) }
            "lookup_coffee_origin" -> beans().filter { bean ->
                listOf(bean.name, bean.country, bean.region, bean.process)
                    .any { field -> field.contains(query, ignoreCase = true) }
            }
            "search_documents" -> search(query)
            else -> error("Unsupported lookup tool: $toolName")
        }
    }

    private fun estimateSensoryProfile(recipeId: String): Any {
        val recipe = recipes().find { it.id == recipeId } ?: error("Recipe not found.")
        val bean = beans().find { it.id == recipe.beanId }
        val ey = if (recipe.beverageMassGrams != null && recipe.measuredTdsPercent != null) {
            CoffeeScienceEngine.calculateExtractionYield(
                recipe.doseGrams,
                recipe.beverageMassGrams,
                recipe.measuredTdsPercent
            ).getOrNull()?.extractionYieldPercent
        } else {
            null
        }
        return SensoryRulesEngine.estimateProfile(bean, recipe, ey)
    }

    private fun createRecipe(arguments: ToolArguments): Recipe = Recipe(
        id = UUID.randomUUID().toString(),
        name = arguments.string("name"),
        doseGrams = arguments.number("doseGrams"),
        waterGrams = arguments.number("waterGrams"),
        brewMethod = arguments.string("brewMethod")
    ).also { DomainValidation.validateRecipe(it, ingredients()) }

    private fun calculateAltitudeBoilingPoint(elevationMeters: Double): Map<String, Any> {
        val boilingPoint = WeatherAltitudeCalculator.calculateBoilingPointCelsius(elevationMeters)
        val maxBrewTemperature = WeatherAltitudeCalculator.clampWaterTemperature(95.0, elevationMeters)
        return mapOf(
            "elevationMeters" to elevationMeters,
            "boilingPointCelsius" to boilingPoint,
            "maxSafeBrewTempCelsius" to maxBrewTemperature,
            "status" to "At ${elevationMeters}m, water boils at $boilingPoint°C"
        )
    }

    private fun auditIngredientSafety(arguments: ToolArguments): Any {
        val ingredientName = arguments.string("ingredientName")
        val massFractionPercent = arguments.number("massFractionPercent")
        val matched = ingredients().find { it.name.contains(ingredientName, ignoreCase = true) }
            ?: error("No local ingredient record verifies '$ingredientName'.")
        return IngredientSafetyValidator.auditIngredient(
            ingredientName,
            matched.foodGrade,
            matched.allergens,
            massFractionPercent,
            matched.supplierMaxPercent
        )
    }

    private fun proposeFeedbackAdjustment(arguments: ToolArguments): Any {
        val actualDuration = arguments.number("actualDurationSeconds")
        val targetDuration = arguments.number("targetDurationSeconds")
        require(actualDuration % 1.0 == 0.0 && targetDuration % 1.0 == 0.0) {
            "Brew durations must be whole seconds."
        }
        val grinderClicks = arguments.number("grindClicks")
        require(grinderClicks % 1.0 == 0.0) { "Grinder clicks must be a whole number." }
        val recipeInputs = mapOf(
            "grindClicks" to grinderClicks,
            "waterTempCelsius" to arguments.number("waterTempCelsius"),
            "doseGrams" to arguments.number("doseGrams"),
            "waterGrams" to arguments.number("waterGrams")
        )
        val diagnosis = SensoryFeedbackDiagnosis.valueOf(arguments.string("diagnosis").uppercase())
        require(diagnosis != SensoryFeedbackDiagnosis.BALANCED) {
            "Balanced feedback requires confirmed sensory and measured extraction evidence."
        }
        return FeedbackLoopEngine.proposeAdjustment(
            recipeInputs,
            actualDuration.toInt(),
            targetDuration.toInt(),
            diagnosis,
            arguments.number("boilingPointCelsius")
        )
    }

    private fun validateArguments(tool: ToolDefinition, arguments: Map<String, Any?>) {
        require(arguments.keys == tool.parameters.keys) { "Missing or unknown arguments for ${tool.name}." }
        tool.parameters.forEach { (name, type) ->
            val value = arguments[name]
            require(
                if (type ==
                    "number"
                ) {
                    value is Number && value.toDouble().isFinite()
                } else {
                    value is String &&
                        value.isNotBlank() &&
                        value.length <= InputLimits.QUERY_CHARS
                }
            ) {
                "Invalid $type argument: $name"
            }
        }
    }

    private data class ToolArguments(private val values: Map<String, Any?>) {
        fun number(name: String): Double = (values.getValue(name) as Number).toDouble()

        fun string(name: String): String = values.getValue(name) as String
    }

    private companion object {
        val CALCULATION_TOOLS = setOf(
            "calculate_brew_ratio",
            "calculate_extraction_yield",
            "calculate_dilution",
            "calculate_concentration_ppm",
            "convert_units"
        )
        val LOOKUP_TOOLS = setOf("lookup_ingredient", "lookup_coffee_origin", "search_documents")
    }
}
