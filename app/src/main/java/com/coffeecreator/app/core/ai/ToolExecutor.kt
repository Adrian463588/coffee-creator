package com.coffeecreator.app.core.ai

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
import com.coffeecreator.app.core.ai.personalization.BeanInventoryCandidate
import com.coffeecreator.app.core.ai.personalization.FeedbackLoopEngine
import com.coffeecreator.app.core.ai.personalization.IngredientSafetyValidator
import com.coffeecreator.app.core.ai.personalization.PalateProfile
import com.coffeecreator.app.core.ai.personalization.PalateVector
import com.coffeecreator.app.core.ai.personalization.RecommendationEngine
import com.coffeecreator.app.core.ai.personalization.SensoryFeedbackDiagnosis
import com.coffeecreator.app.core.ai.personalization.WeatherAltitudeCalculator
import java.time.LocalDate
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
            "Single-variable-at-a-time (OVAT) recipe correction from sensory diagnosis",
            mapOf(
                "actualDurationSeconds" to "number",
                "targetDurationSeconds" to "number",
                "diagnosis" to "string"
            )
        ),
        ToolDefinition(
            "recommend_inventory_bean",
            "Recommend available bean from inventory matching method and palate",
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
        fun number(name: String) = (arguments.getValue(name) as Number).toDouble()
        fun string(name: String) = arguments.getValue(name) as String
        val result: Any = when (toolName) {
            "calculate_brew_ratio" -> CoffeeScienceEngine.calculateBrewRatio(number("doseGrams"), number("waterGrams")).getOrThrow()
            "calculate_extraction_yield" -> CoffeeScienceEngine.calculateExtractionYield(
                number("doseGrams"),
                number("beverageMassGrams"),
                number("measuredTdsPercent")
            ).getOrThrow()
            "calculate_dilution" -> CoffeeScienceEngine.calculateDilution(
                number("initialMassGrams"),
                number("initialTdsPercent"),
                number("addedWaterGrams")
            ).getOrThrow()
            "calculate_concentration_ppm" -> CoffeeScienceEngine.calculateConcentration(
                number("soluteMassGrams"),
                number("solutionMassGrams")
            ).getOrThrow()
            "convert_units" -> CoffeeScienceEngine.calculateVolumeToMass(number("volumeMl"), number("densityGramsPerMl")).getOrThrow()
            "lookup_ingredient" -> ingredients().filter { it.name.contains(string("query"), true) }
            "lookup_coffee_origin" -> beans().filter {
                listOf(it.name, it.country, it.region, it.process).any { value -> value.contains(string("query"), true) }
            }
            "search_documents" -> search(string("query"))
            "estimate_sensory_profile" -> {
                val recipe = recipes().find { it.id == string("recipeId") } ?: error("Recipe not found.")
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
                SensoryRulesEngine.estimateProfile(bean, recipe, ey)
            }
            "recommend_recipe", "save_recipe" -> Recipe(
                id = UUID.randomUUID().toString(),
                name = string("name"),
                doseGrams = number("doseGrams"),
                waterGrams = number("waterGrams"),
                brewMethod = string("brewMethod")
            ).also { DomainValidation.validateRecipe(it, ingredients()) }
            "calculate_altitude_boiling_point" -> {
                val elevation = number("elevationMeters")
                val tBoil = WeatherAltitudeCalculator.calculateBoilingPointCelsius(elevation)
                val maxBrewTemp = WeatherAltitudeCalculator.clampWaterTemperature(95.0, elevation)
                mapOf(
                    "elevationMeters" to elevation,
                    "boilingPointCelsius" to tBoil,
                    "maxSafeBrewTempCelsius" to maxBrewTemp,
                    "status" to "At ${elevation}m, water boils at ${tBoil}°C"
                )
            }
            "audit_ingredient_safety" -> {
                val ingName = string("ingredientName")
                val pct = number("massFractionPercent")
                val matched = ingredients().find { it.name.contains(ingName, ignoreCase = true) }
                val foodGrade = matched?.foodGrade ?: true
                val allergens = matched?.allergens.orEmpty()
                IngredientSafetyValidator.auditIngredient(ingName, foodGrade, allergens, pct, matched?.supplierMaxPercent)
            }
            "propose_feedback_adjustment" -> {
                val actualDur = number("actualDurationSeconds").toInt()
                val targetDur = number("targetDurationSeconds").toInt()
                val diagStr = string("diagnosis")
                val diag = try {
                    SensoryFeedbackDiagnosis.valueOf(diagStr.uppercase())
                } catch (_: Exception) {
                    SensoryFeedbackDiagnosis.SOUR_UNDER_EXTRACTED
                }
                val sampleRecipe = mapOf(
                    "grindClicks" to 18,
                    "waterTempCelsius" to 93.0,
                    "doseGrams" to 15.0,
                    "waterGrams" to 250.0
                )
                FeedbackLoopEngine.proposeAdjustment(sampleRecipe, actualDur, targetDur, diag)
            }
            "recommend_inventory_bean" -> {
                val method = string("brewMethod")
                val candidates = beans().map { b ->
                    BeanInventoryCandidate(
                        beanId = b.id,
                        beanName = b.name,
                        sensoryProfile = PalateVector(0.3, 0.8, 0.7, 0.2, 0.5),
                        roastDate = LocalDate.now().minusDays(14),
                        availableMassGrams = 250.0
                    )
                }
                RecommendationEngine.rankInventory(
                    palate = PalateProfile(),
                    candidates = candidates,
                    availableGearMethods = setOf(method),
                    brewMethod = method
                )
            }
            else -> error("Tool not implemented.")
        }
        val confirmation = tool.isWriteOperation
        ToolExecutionResult(toolName, true, gson.toJson(result), confirmation, result)
    } catch (cancelled: CancellationException) {
        throw cancelled
    } catch (boundaryFailure: Exception) {
        ToolExecutionResult(toolName, false, boundaryFailure.message ?: "Tool validation failed.")
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
}
