package com.coffeecreator.app.core.ai

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ToolExecutorTest {
    private val executor = ToolExecutor({ emptyList() }, { emptyList() }, { emptyList() }, {
        RagSearchResult("No evidence", emptyList(), didAbstain = true)
    })

    @Test fun givenExplicitMeasurements_whenTypedToolCalled_thenGoldenMathPreserved() = runTest {
        val result = executor.execute(
            "calculate_extraction_yield",
            mapOf(
                "doseGrams" to 18.0,
                "beverageMassGrams" to 270.0,
                "measuredTdsPercent" to 1.3
            )
        )
        assertTrue(result.isSuccess)
        assertTrue(result.output.contains("19.5"))
    }

    @Test fun givenMissingOrWrongOrExtraArguments_whenCalled_thenRejectInsteadOfInventingDefaults() = runTest {
        assertFalse(executor.execute("calculate_extraction_yield", emptyMap()).isSuccess)
        assertFalse(executor.execute("calculate_brew_ratio", mapOf("doseGrams" to "18", "waterGrams" to 300)).isSuccess)
        assertFalse(executor.execute("calculate_brew_ratio", mapOf("doseGrams" to 18, "waterGrams" to 300, "shell" to "id")).isSuccess)
        assertFalse(executor.execute("calculate_brew_ratio", mapOf("doseGrams" to Double.NaN, "waterGrams" to 300)).isSuccess)
    }

    @Test fun givenUnknownToolOrMalformedJson_whenRequested_thenRejectWithoutExecution() = runTest {
        assertFalse(executor.execute("shell", mapOf("command" to "id")).isSuccess)
        assertFalse(executor.executeJson("{ broken json }").isSuccess)
        assertFalse(executor.executeJson("{\"tool\":\"calculate_brew_ratio\",\"arguments\":{}}").isSuccess)
    }

    @Test fun givenUnknownIngredient_whenSafetyAudited_thenFailClosedWithoutLocalEvidence() = runTest {
        val result = executor.execute(
            "audit_ingredient_safety",
            mapOf("ingredientName" to "unlisted aroma", "massFractionPercent" to 0.1)
        )

        assertFalse(result.isSuccess)
    }

    @Test fun givenFeedbackRequestWithoutRecipeFacts_whenExecuted_thenRejectInsteadOfUsingSampleRecipe() = runTest {
        val result = executor.execute(
            "propose_feedback_adjustment",
            mapOf(
                "actualDurationSeconds" to 200,
                "targetDurationSeconds" to 180,
                "diagnosis" to "BITTER_OVER_EXTRACTED"
            )
        )

        assertFalse(result.isSuccess)
    }

    @Test fun givenModelClaimsBalancedWithoutSensoryEvidence_whenFeedbackToolCalled_thenReject() = runTest {
        val result = executor.execute(
            "propose_feedback_adjustment",
            mapOf(
                "actualDurationSeconds" to 180,
                "targetDurationSeconds" to 180,
                "diagnosis" to "BALANCED",
                "grindClicks" to 18,
                "waterTempCelsius" to 93.0,
                "doseGrams" to 15.0,
                "waterGrams" to 250.0,
                "boilingPointCelsius" to 100.0
            )
        )

        assertFalse(result.isSuccess)
    }

    @Test fun givenUnrepresentableFeedbackInteger_whenCalled_thenRejectWithoutWraparound() = runTest {
        val result = executor.execute(
            "propose_feedback_adjustment",
            mapOf(
                "actualDurationSeconds" to 180,
                "targetDurationSeconds" to 155,
                "diagnosis" to "BITTER_OVER_EXTRACTED",
                "grindClicks" to Int.MAX_VALUE,
                "waterTempCelsius" to 93.0,
                "doseGrams" to 15.0,
                "waterGrams" to 250.0,
                "boilingPointCelsius" to 100.0
            )
        )

        assertFalse(result.isSuccess)
    }

    @Test fun givenFeedbackDurationOutsideIntegerRange_whenCalled_thenRejectBeforeConversion() = runTest {
        val result = executor.execute(
            "propose_feedback_adjustment",
            mapOf(
                "actualDurationSeconds" to Int.MAX_VALUE.toDouble() + 1.0,
                "targetDurationSeconds" to 155,
                "diagnosis" to "BITTER_OVER_EXTRACTED",
                "grindClicks" to 18,
                "waterTempCelsius" to 93.0,
                "doseGrams" to 15.0,
                "waterGrams" to 250.0,
                "boilingPointCelsius" to 100.0
            )
        )

        assertFalse(result.isSuccess)
    }

    @Test fun givenNoInventoryRecords_whenRecommendationRequested_thenAbstain() = runTest {
        val result = executor.execute("recommend_inventory_bean", mapOf("brewMethod" to "Pour Over"))

        assertFalse(result.isSuccess)
    }

    @Test fun givenSaveTool_whenValidated_thenReturnDraftAndRequireConfirmation() = runTest {
        val result = executor.execute(
            "save_recipe",
            mapOf(
                "name" to "My recipe",
                "doseGrams" to 18.0,
                "waterGrams" to 300.0,
                "brewMethod" to "V60"
            )
        )
        assertTrue(result.isSuccess)
        assertTrue(result.requiresUserConfirmation)
        val recipe = result.structuredData as com.coffeecreator.app.core.model.Recipe
        assertEquals(null, recipe.measuredTdsPercent)
        assertEquals(null, recipe.beverageMassGrams)
    }

    @Test fun givenRegistry_whenInspected_thenContainsAllPrdToolsAndNoNetworkTools() {
        val names = executor.registry.map { it.name }.toSet()
        assertTrue(
            names.containsAll(
                setOf(
                    "calculate_brew_ratio", "calculate_extraction_yield", "calculate_dilution",
                    "convert_units", "lookup_ingredient", "lookup_coffee_origin", "search_documents", "estimate_sensory_profile",
                    "recommend_recipe", "save_recipe"
                )
            )
        )
        assertFalse(names.any { it.contains("network") || it.contains("shell") })
    }
}
