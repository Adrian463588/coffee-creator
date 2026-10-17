package com.coffeecreator.app.core.recipes.champion

import com.coffeecreator.app.core.journal.BrewJournalEntry
import com.coffeecreator.app.core.model.BrewStep
import com.coffeecreator.app.core.model.Recipe
import java.util.UUID

enum class RecipeCategory(val label: String) {
    COMPETITION("Competition"),
    SIGNATURE("Signature"),
    STANDARD("Traditional / Standard")
}

enum class RecipeVerificationStatus(val label: String, val badgeColorHex: Long) {
    OFFICIAL("Official Champion / Direct Source", 0xFF2E7D32),
    DOCUMENTED_RECREATION("Documented Competition Recreation", 0xFF1565C0),
    SECONDARY_ADAPTATION("Standardized Adaptation", 0xFFE65100),
    NEEDS_VERIFICATION("Heritage / Needs Calibration", 0xFF6A1B9A)
}

data class CompetitionPodium(
    val competitionName: String,
    val year: Int,
    val rank: Int,
    val hostCity: String,
    val barista: String,
    val countryOrRegion: String
)

data class ChampionPourStep(
    val stepNumber: Int,
    val action: String = "Pour",
    val waterGrams: Double,
    val cumulativeWaterGrams: Double,
    val durationSeconds: Int,
    val startOffsetSeconds: Int = 0,
    val temperatureCelsius: Double? = null,
    val description: String = "",
    val valveStatus: String? = null
) {
    fun toBrewStep(): BrewStep = BrewStep(
        stepNumber = stepNumber,
        description = description.ifBlank { "$action ${waterGrams}g" },
        targetWaterGrams = waterGrams,
        durationSeconds = durationSeconds,
        action = action,
        startOffsetSeconds = startOffsetSeconds,
        cumulativeMassGrams = cumulativeWaterGrams,
        temperatureCelsius = temperatureCelsius,
        valveStatus = valveStatus
    )
}

data class ChampionRecipe(
    val id: String,
    val name: String,
    val category: RecipeCategory,
    val baristaOrCreator: String,
    val originOrAffiliation: String,
    val method: String,
    val equipment: String,
    val doseGrams: Double,
    val waterGrams: Double,
    val ratio: String,
    val grindSize: String,
    val waterTemperatureCelsius: Double,
    val totalTimeSeconds: Int,
    val verificationStatus: RecipeVerificationStatus,
    val podium: CompetitionPodium? = null,
    val steps: List<ChampionPourStep> = emptyList(),
    val notes: String = "",
    val recommendedBeansOrRoast: String = "",
    val tags: List<String> = emptyList()
) {
    fun toBrewJournalEntry(beanName: String? = null, beanId: String? = null, customNotes: String = ""): BrewJournalEntry {
        val podiumStr = podium?.let { " [${it.competitionName} ${it.year} Rank #${it.rank}]" } ?: ""
        return BrewJournalEntry(
            id = UUID.randomUUID().toString(),
            recipeId = this.id,
            beanId = beanId,
            beanName = beanName ?: "${this.name} (${this.baristaOrCreator})",
            brewMethod = this.method,
            equipment = this.equipment,
            doseGrams = this.doseGrams,
            waterGrams = this.waterGrams,
            waterTemperatureCelsius = this.waterTemperatureCelsius,
            brewDurationSeconds = this.totalTimeSeconds,
            grindSetting = this.grindSize,
            rating = if (this.category == RecipeCategory.COMPETITION) 9.5 else 9.0,
            tasteNotes = emptyList(),
            generalNotes = "[$category | ${verificationStatus.label}]$podiumStr Creator: $baristaOrCreator\n${this.notes}" +
                if (customNotes.isNotBlank()) "\nNotes: $customNotes" else "",
            tags = this.tags + listOf("champion-catalog", category.name.lowercase(), verificationStatus.name.lowercase()),
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
    }

    fun toRecipe(customId: String? = null): Recipe {
        val podiumStr = podium?.let { " [${it.competitionName} ${it.year} #${it.rank}]" } ?: ""
        return Recipe(
            id = customId ?: UUID.randomUUID().toString(),
            name = this.name,
            brewMethod = this.method,
            equipment = this.equipment,
            doseGrams = this.doseGrams,
            waterGrams = this.waterGrams,
            targetRatio = this.ratio,
            temperatureCelsius = this.waterTemperatureCelsius,
            brewTimeSeconds = this.totalTimeSeconds,
            grindSize = this.grindSize,
            brewSteps = this.steps.map { it.toBrewStep() },
            notes = "Creator: $baristaOrCreator$podiumStr | Verification: ${verificationStatus.label}\n${this.notes}"
        )
    }
}
