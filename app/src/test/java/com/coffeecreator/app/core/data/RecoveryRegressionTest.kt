package com.coffeecreator.app.core.data

import com.coffeecreator.app.core.engine.SensoryRulesEngine
import com.coffeecreator.app.core.model.Recipe
import com.coffeecreator.app.core.model.StoredSensoryEstimate
import com.google.gson.JsonObject
import org.junit.Assert.assertEquals
import org.junit.Test

class RecoveryRegressionTest {
    @Test fun givenManualRecipeWithoutBean_whenBackedUp_thenNullBeanRemainsAbsent() {
        val recipe = Recipe(id = "manual", name = "QA manual")
        val data = BackupPayload(recipes = listOf(recipe), personas = SeedData.personas)
        assertEquals(data, BackupCodec.decode(BackupCodec.encode(data)).payload)
    }

    @Test fun givenEstimatedProfile_whenRestored_thenItCannotBecomeAnObservedMeasurement() {
        val recipe = Recipe(id = "estimate-recipe", name = "QA estimate")
        val estimate = StoredSensoryEstimate("estimate", recipe.id, SensoryRulesEngine.estimateProfile(null, recipe, null))
        val data = BackupPayload(recipes = listOf(recipe), estimates = listOf(estimate), personas = SeedData.personas)
        val restored = BackupCodec.decode(BackupCodec.encode(data)).payload
        assertEquals(listOf(estimate), restored.estimates)
        assertEquals(emptyList<Any>(), restored.observations)
    }

    @Test fun givenVersionTwoBackup_whenRestored_thenEstimatesMigrateToAnEmptySeparateCollection() {
        val data = BackupPayload(personas = SeedData.personas)
        val tree = RecordJson.gson.fromJson(BackupCodec.encode(data), JsonObject::class.java)
        val payload = tree.getAsJsonObject("payload")
        payload.remove("estimates")
        payload.remove("cvaSessions")
        val manifest = tree.getAsJsonObject("manifest")
        manifest.addProperty("schemaVersion", 2)
        manifest.getAsJsonObject("recordCounts").remove("estimates")
        manifest.getAsJsonObject("recordCounts").remove("cvaSessions")
        manifest.addProperty("payloadSha256", BackupCodec.sha256(RecordJson.gson.toJson(payload)))
        assertEquals(data, BackupCodec.decode(RecordJson.gson.toJson(tree)).payload)
    }
}
