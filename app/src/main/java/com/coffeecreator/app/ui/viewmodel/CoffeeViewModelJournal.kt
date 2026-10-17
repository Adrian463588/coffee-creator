package com.coffeecreator.app.ui.viewmodel

import android.content.Context
import com.coffeecreator.app.core.ai.personalization.PalateBayesianEngine
import com.coffeecreator.app.core.ai.personalization.PersonalSensoryEvidence
import com.coffeecreator.app.core.data.BackupCodec
import com.coffeecreator.app.core.data.RecipeExportFormatter
import com.coffeecreator.app.core.journal.BrewAnalyticsEngine
import com.coffeecreator.app.core.journal.BrewFilterCriteria
import com.coffeecreator.app.core.journal.BrewJournalEntry
import com.coffeecreator.app.core.journal.CafeCupEntry
import com.coffeecreator.app.core.journal.ImportConflictItem
import com.coffeecreator.app.core.journal.ImportConflictPreview
import com.coffeecreator.app.core.journal.ImportRecordStatus
import com.coffeecreator.app.core.model.Recipe
import com.coffeecreator.app.core.recipes.champion.ChampionRecipe
import com.coffeecreator.app.core.reliability.LogLevel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.UUID
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

private const val QUALIFIED_JOURNAL_RATING = 7.5
private const val MILLIS_PER_DAY = 86_400_000.0

/** Uses only a complete, qualified human record; missing dimensions stay missing. */
internal fun BrewJournalEntry.toPersonalSensoryEvidence(): PersonalSensoryEvidence? {
    val qualifiedRating = rating?.takeIf { it in QUALIFIED_JOURNAL_RATING..10.0 } ?: return null
    val dimensions = sensoryScores.entries.associate { it.key.trim().lowercase() to it.value }
    val scores = listOf("roast", "acidity", "sweetness", "bitterness", "body").map { key ->
        dimensions[key]?.takeIf { it in 1..10 } ?: return null
    }
    return PersonalSensoryEvidence(
        roast = scores[0] / 10.0,
        acidity = scores[1] / 10.0,
        sweetness = scores[2] / 10.0,
        bitterness = scores[3] / 10.0,
        body = scores[4] / 10.0,
        rating = qualifiedRating,
        daysAgo = ((System.currentTimeMillis() - createdAt).coerceAtLeast(0L) / MILLIS_PER_DAY)
    )
}

fun CoffeeViewModel.saveJournalEntry(entry: BrewJournalEntry) {
    mutableState.update { current ->
        val list = listOf(entry) + current.journalEntries.filter { e -> e.id != entry.id }
        val evidence = entry.toPersonalSensoryEvidence()
        val updatedPalate = evidence?.let { PalateBayesianEngine.updateProfile(current.palateProfile, it) }
            ?: current.palateProfile
        diagnosticBuffer.log(LogLevel.INFO, "Journal", "Saved journal entry for ${entry.beanName} (rating: ${entry.rating})")
        current.copy(journalEntries = list, palateProfile = updatedPalate, notice = "Saved journal entry for ${entry.beanName}.")
    }
    refreshStreakAndStats()
}

fun CoffeeViewModel.deleteJournalEntry(id: String) {
    mutableState.update {
        val list = it.journalEntries.filter { e -> e.id != id }
        it.copy(journalEntries = list, notice = "Deleted journal entry.")
    }
    refreshStreakAndStats()
}

fun CoffeeViewModel.updateJournalEntryTasteNotes(id: String, notes: List<String>) {
    mutableState.update { current ->
        val updated = current.journalEntries.map { entry ->
            if (entry.id == id) entry.copy(tasteNotes = notes, updatedAt = System.currentTimeMillis()) else entry
        }
        current.copy(journalEntries = updated, notice = "Updated tasting notes from Flavor Wheel.")
    }
}

fun CoffeeViewModel.updateJournalFilter(criteria: BrewFilterCriteria) {
    mutableState.update { it.copy(journalFilterCriteria = criteria) }
}

fun CoffeeViewModel.selectComparisonSlot(entry: BrewJournalEntry?, slot: String) {
    mutableState.update { current ->
        val newA = if (slot == "A") entry else current.selectedComparisonA
        val newB = if (slot == "B") entry else current.selectedComparisonB
        val comparison = if (newA != null && newB != null) {
            BrewAnalyticsEngine.compareSessions(newA, newB)
        } else {
            null
        }
        current.copy(selectedComparisonA = newA, selectedComparisonB = newB, activeComparison = comparison)
    }
}

fun CoffeeViewModel.clearComparison() {
    mutableState.update { it.copy(selectedComparisonA = null, selectedComparisonB = null, activeComparison = null) }
}

fun CoffeeViewModel.evaluateOvatForEntry(entry: BrewJournalEntry, tasteFlags: List<String> = emptyList()) {
    val rec = BrewAnalyticsEngine.evaluateOvat(entry, tasteFlags)
    mutableState.update {
        it.copy(
            activeOvatRecommendation = rec,
            notice = if (rec == null) {
                "OVAT needs measured extraction data or recorded sensory notes. Timer-only entries are not enough."
            } else {
                "Generated an OVAT suggestion from recorded brew evidence."
            }
        )
    }
}

fun CoffeeViewModel.estimateCaffeineForDose(doseGrams: Double, arabicaFraction: Double = 1.0) {
    val estimate = BrewAnalyticsEngine.estimateCaffeine(doseGrams, arabicaFraction)
    mutableState.update { it.copy(activeCaffeineEstimate = estimate) }
}

fun CoffeeViewModel.repeatLastBrew() {
    val last = mutableState.value.journalEntries.maxByOrNull { it.createdAt } ?: return
    val matchingRecipe = mutableState.value.recipes.find { it.id == last.recipeId }
    if (matchingRecipe != null) {
        editRecipe(matchingRecipe)
        initBrewSession(matchingRecipe)
    } else {
        val newRecipe = Recipe(
            id = UUID.randomUUID().toString(),
            name = "${last.brewMethod} Repeat (${last.beanName})",
            beanId = last.beanId,
            brewMethod = last.brewMethod,
            doseGrams = last.doseGrams,
            waterGrams = last.waterGrams,
            temperatureCelsius = last.waterTemperatureCelsius,
            brewTimeSeconds = last.brewDurationSeconds,
            grindSize = last.grindSetting,
            grinderModel = last.grinderModel
        )
        initBrewSession(newRecipe)
    }
    mutableState.update { it.copy(notice = "Repeated previous brew: ${last.brewMethod} (${last.doseGrams}g). Armed on timer.") }
}

fun CoffeeViewModel.saveEntryAsTemplate(entry: BrewJournalEntry, templateName: String) = task("Saving brew template") {
    val recipe = Recipe(
        id = UUID.randomUUID().toString(),
        name = templateName.ifBlank { "${entry.brewMethod} Template" },
        beanId = entry.beanId,
        brewMethod = entry.brewMethod,
        equipment = entry.equipment,
        doseGrams = entry.doseGrams,
        waterGrams = entry.waterGrams,
        beverageMassGrams = entry.beverageMassGrams,
        measuredTdsPercent = entry.measuredTdsPercent,
        temperatureCelsius = entry.waterTemperatureCelsius,
        brewTimeSeconds = entry.brewDurationSeconds,
        grindSize = entry.grindSetting,
        grinderModel = entry.grinderModel,
        notes = entry.generalNotes
    )
    graph.repository.saveRecipe(recipe)
    mutableState.update { it.copy(notice = "Saved template '${recipe.name}' to Recipe Library.") }
}

fun CoffeeViewModel.saveCafeCup(entry: CafeCupEntry) {
    mutableState.update {
        val list = listOf(entry) + it.cafeCupEntries.filter { c -> c.id != entry.id }
        it.copy(cafeCupEntries = list, notice = "Saved cafe cup: ${entry.drinkName} at ${entry.cafeName}.")
    }
}

fun CoffeeViewModel.deleteCafeCup(id: String) {
    mutableState.update {
        val list = it.cafeCupEntries.filter { c -> c.id != id }
        it.copy(cafeCupEntries = list, notice = "Deleted cafe cup.")
    }
}

fun CoffeeViewModel.exportJournalCsv(context: Context) {
    val csv = BrewAnalyticsEngine.exportJournalToCsv(mutableState.value.journalEntries)
    RecipeExportFormatter.shareJournalCsv(context, csv)
}

fun CoffeeViewModel.importBeanconquerorJson(json: String) = task("Importing Beanconqueror backup") {
    val (beans, brews) = BrewAnalyticsEngine.parseBeanconquerorJson(json)
    require(beans.isNotEmpty() || brews.isNotEmpty()) { "No valid Beanconqueror beans or brews found in JSON." }
    beans.forEach { bean ->
        if (graph.repository.findBeanById(bean.id) == null) {
            graph.repository.addBean(bean)
        }
    }
    mutableState.update { current ->
        val updatedBrews = brews + current.journalEntries.filter { j -> brews.none { it.id == j.id } }
        current.copy(
            journalEntries = updatedBrews,
            notice = "Imported ${beans.size} beans and ${brews.size} brews from Beanconqueror."
        )
    }
    refreshStreakAndStats()
}

fun CoffeeViewModel.previewBackup(backupJson: String) {
    val preview = runCatching {
        val validated = BackupCodec.decode(backupJson)
        val currentBeans = mutableState.value.beans.associateBy { it.id }
        val items = validated.payload.beans.map { incoming ->
            val local = currentBeans[incoming.id]
            val status = when {
                local == null -> ImportRecordStatus.NEW
                local == incoming -> ImportRecordStatus.IDENTICAL
                else -> ImportRecordStatus.MODIFIED_REMOTE_NEWER
            }
            ImportConflictItem(
                incoming.id,
                local,
                incoming,
                status,
                local?.let { it.hashCode().toString() },
                incoming.hashCode().toString()
            )
        }
        ImportConflictPreview(
            newRecordsCount = items.count { it.status == ImportRecordStatus.NEW },
            identicalCount = items.count { it.status == ImportRecordStatus.IDENTICAL },
            conflictCount = items.count { it.status != ImportRecordStatus.NEW && it.status != ImportRecordStatus.IDENTICAL },
            items = items
        )
    }.getOrNull()
    mutableState.update { it.copy(importConflictPreview = preview) }
}

fun CoffeeViewModel.clearConflictPreview() {
    mutableState.update { it.copy(importConflictPreview = null) }
}

fun CoffeeViewModel.refreshStreakAndStats() {
    val entries = mutableState.value.journalEntries
    val zone = ZoneId.systemDefault()
    val dates = entries.map { Instant.ofEpochMilli(it.createdAt).atZone(zone).toLocalDate() }
    val streak = BrewAnalyticsEngine.calculateStreak(dates)
    val now = LocalDate.now()
    val stats = BrewAnalyticsEngine.calculateMonthlyStats(entries, now.year, now.monthValue)
    mutableState.update { it.copy(brewStreak = streak, monthlyStats = stats) }
}

fun CoffeeViewModel.logChampionRecipeToJournal(recipe: ChampionRecipe, customNotes: String = "", beanName: String? = null) {
    val entry = recipe.toBrewJournalEntry(beanName = beanName, customNotes = customNotes)
    saveJournalEntry(entry)
    mutableState.update { it.copy(notice = "Logged '${recipe.name}' to Journal.") }
}

fun CoffeeViewModel.brewChampionRecipe(recipe: ChampionRecipe) {
    val r = recipe.toRecipe()
    initBrewSession(r)
    mutableState.update { it.copy(notice = "Armed '${recipe.name}' on brew timer.") }
}

fun CoffeeViewModel.saveChampionRecipeAsTemplate(recipe: ChampionRecipe) = task("Saving champion recipe template") {
    val r = recipe.toRecipe()
    graph.repository.saveRecipe(r)
    mutableState.update { it.copy(notice = "Saved template '${recipe.name}' to Recipe Library.") }
}

// ========================================================================
// Sensory & Cupping Sessions (Epic S2-E05)
// ========================================================================
