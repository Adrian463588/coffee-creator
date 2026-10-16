package com.coffeecreator.app.ui.viewmodel

import android.content.Context
import com.coffeecreator.app.core.data.RecipeExportFormatter
import com.coffeecreator.app.core.engine.CoffeeScienceEngine
import com.coffeecreator.app.core.engine.DomainValidation
import com.coffeecreator.app.core.engine.SensoryRulesEngine
import com.coffeecreator.app.core.model.BrewStep
import com.coffeecreator.app.core.model.CalculationResult
import com.coffeecreator.app.core.model.CoffeeBean
import com.coffeecreator.app.core.model.Ingredient
import com.coffeecreator.app.core.model.InputLimits
import com.coffeecreator.app.core.model.Recipe
import com.coffeecreator.app.core.model.RecipeLine
import com.coffeecreator.app.core.model.RefractometerProfile
import com.coffeecreator.app.core.model.SensoryObservation
import com.coffeecreator.app.core.model.StoredSensoryEstimate
import com.coffeecreator.app.core.reliability.LogLevel
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext

fun CoffeeViewModel.updateField(key: String, value: String) {
    require(key in RecipeDraft.defaultFields())
    if (value.length > InputLimits.METADATA_CHARS) return
    val draft = mutableState.value.draft.copy(fields = mutableState.value.draft.fields + (key to value))
    updateDraft(draft)
    calculate()
}
private fun CoffeeViewModel.updateDraft(draft: RecipeDraft, recordUndo: Boolean = false) {
    if (replacingDraft) return
    publishDraft(draft, recordUndo)
}
internal fun CoffeeViewModel.publishDraft(draft: RecipeDraft, recordUndo: Boolean = false) {
    if (recordUndo && draft == mutableState.value.draft) return
    undoDraft = mutableState.value.draft.takeIf { recordUndo && it != draft }
    if (undoDraft != null) saved["undoDraft"] = gson.toJson(undoDraft) else saved.remove<String>("undoDraft")
    mutableState.update { it.copy(draft = draft, canUndoDraft = undoDraft != null, notice = null, error = null) }
    val json = gson.toJson(draft)
    saved["draft"] = json
    draftWrites.value = draft
}
fun CoffeeViewModel.newRecipe() {
    updateDraft(RecipeDraft(), recordUndo = true)
    calculate()
}
fun CoffeeViewModel.undoDraftChange() {
    val previous = undoDraft ?: return
    updateDraft(previous)
    calculate()
}
fun CoffeeViewModel.editRecipe(recipe: Recipe) {
    updateDraft(RecipeDraft.fromRecipe(recipe), recordUndo = true)
    calculate()
}
fun CoffeeViewModel.addLine(ingredientId: String, quantity: String, unit: String, density: String) = task("Validating ingredient line") {
    val line = RecipeLine(ingredientId, decimal(quantity), unit, optionalDecimal(density))
    require(line.quantity > 0 && line.unit in listOf("g", "mg", "mL")) { "Positive quantity and g/mg/mL unit required." }
    if (unit == "mL") {
        val value = line.densityGramsPerMl ?: mutableState.value.ingredients.find { it.id == ingredientId }?.densityGramsPerMl
        CoffeeScienceEngine.convertVolumeToMass(line.quantity, value).getOrThrow()
    }
    updateDraft(
        mutableState.value.draft.copy(
            lines = mutableState.value.draft.lines + line,
            fields =
            mutableState.value.draft.fields + ("allergens" to "")
        )
    )
}
fun CoffeeViewModel.removeLine(index: Int) {
    if (index !in mutableState.value.draft.lines.indices) return
    updateDraft(
        mutableState.value.draft.copy(
            lines = mutableState.value.draft.lines.filterIndexed { i, _ -> i != index },
            fields =
            mutableState.value.draft.fields + ("allergens" to "")
        ),
        recordUndo = true
    )
}

fun CoffeeViewModel.scaleDraft(factor: Double) {
    if (!factor.isFinite() || factor <= 0.0) return
    val currentDose = mutableState.value.draft.fields["dose"]?.toDoubleOrNull()?.takeIf { it.isFinite() && it > 0.0 }
    val currentWater = mutableState.value.draft.fields["water"]?.toDoubleOrNull()?.takeIf { it.isFinite() && it > 0.0 }
    if (currentDose == null || currentWater == null) {
        mutableState.update { it.copy(notice = "Enter a positive dose and water amount before scaling the recipe.") }
        return
    }
    val targetDose = "%.1f".format(java.util.Locale.US, currentDose * factor)
    val targetWater = "%.1f".format(java.util.Locale.US, currentWater * factor)
    val scaledSteps = if (mutableState.value.draft.steps.isNotEmpty()) {
        CoffeeScienceEngine.scaleRecipeSteps(
            mutableState.value.draft.steps,
            currentWater,
            targetWater.toDouble()
        ).getOrDefault(mutableState.value.draft.steps)
    } else {
        emptyList()
    }
    val updatedFields = mutableState.value.draft.fields + mapOf(
        "dose" to targetDose,
        "water" to targetWater
    )
    updateDraft(mutableState.value.draft.copy(fields = updatedFields, steps = scaledSteps), recordUndo = true)
    calculate()
}

fun CoffeeViewModel.addDraftStep() {
    val currentSteps = mutableState.value.draft.steps
    val nextNumber = currentSteps.size + 1
    val lastCum = currentSteps.lastOrNull()?.cumulativeMassGrams ?: 0.0
    val defaultWater = 50.0
    val newStep = BrewStep(
        stepNumber = nextNumber,
        description = "Pour to ${"%.1f".format(java.util.Locale.US, lastCum + defaultWater)}g",
        targetWaterGrams = defaultWater,
        durationSeconds = 30,
        action = if (nextNumber == 1) "Bloom" else "Pour",
        startOffsetSeconds = currentSteps.sumOf { it.durationSeconds },
        cumulativeMassGrams = "%.1f".format(java.util.Locale.US, lastCum + defaultWater).toDouble()
    )
    updateDraft(mutableState.value.draft.copy(steps = currentSteps + newStep), recordUndo = true)
}

fun CoffeeViewModel.updateDraftStep(index: Int, updated: BrewStep) {
    val steps = mutableState.value.draft.steps.toMutableList()
    if (index !in steps.indices) return
    steps[index] = updated
    var cum = 0.0
    val recomputed = steps.mapIndexed { idx, s ->
        cum += s.targetWaterGrams
        s.copy(stepNumber = idx + 1, cumulativeMassGrams = "%.1f".format(java.util.Locale.US, cum).toDouble())
    }
    updateDraft(mutableState.value.draft.copy(steps = recomputed), recordUndo = false)
}

fun CoffeeViewModel.removeDraftStep(index: Int) {
    val steps = mutableState.value.draft.steps.toMutableList()
    if (index !in steps.indices) return
    steps.removeAt(index)
    var cum = 0.0
    val recomputed = steps.mapIndexed { idx, s ->
        cum += s.targetWaterGrams
        s.copy(stepNumber = idx + 1, cumulativeMassGrams = "%.1f".format(java.util.Locale.US, cum).toDouble())
    }
    updateDraft(mutableState.value.draft.copy(steps = recomputed), recordUndo = true)
}

fun CoffeeViewModel.convertDraftBrix() = task("Converting Brix to TDS") {
    val brixStr = mutableState.value.draft.fields["brix"].orEmpty()
    require(brixStr.isNotBlank()) { "Enter a Brix reading." }
    val brix = DomainValidation.parseDecimal(brixStr).toDouble()
    val profileName = mutableState.value.draft.fields["brixProfile"].orEmpty()
    val profile = RefractometerProfile.find(profileName)
    val conversion = CoffeeScienceEngine.calculateBrixToTds(brix, profile).getOrThrow()
    val tdsFormatted = "%.2f".format(java.util.Locale.US, conversion.tdsPercent)
    val updatedFields = mutableState.value.draft.fields + ("tds" to tdsFormatted)
    updateDraft(mutableState.value.draft.copy(fields = updatedFields))
    calculate()
    mutableState.update {
        it.copy(
            notice = "Converted ${conversion.brixPercent}% Brix → $tdsFormatted% TDS (${conversion.instrumentProfile})."
        )
    }
}

fun CoffeeViewModel.toggleArchiveRecipe(recipe: Recipe) = task("Toggling archive status") {
    val updated = graph.repository.archiveRecipe(recipe.id, !recipe.isArchived)
    mutableState.update {
        it.copy(notice = "Recipe '${updated.name}' ${if (updated.isArchived) "archived" else "restored to active"}.")
    }
}

fun CoffeeViewModel.shareRecipe(context: Context, recipe: Recipe, formatJson: Boolean = false) {
    val bean = mutableState.value.beans.find { it.id == recipe.beanId }
    RecipeExportFormatter.shareRecipe(context, recipe, bean, formatJson)
}

fun CoffeeViewModel.calculate() {
    val fields = mutableState.value.draft.fields
    val output = runCatching {
        val ratio = CoffeeScienceEngine.calculateBrewRatio(
            decimal(fields.getValue("dose")),
            decimal(fields.getValue("water"))
        ).getOrThrow()
        val ey = if (mutableState.value.draft.lines.isEmpty() &&
            fields.getValue("beverage").isNotBlank() &&
            fields.getValue("tds").isNotBlank()
        ) {
            CoffeeScienceEngine.calculateExtractionYield(
                decimal(fields.getValue("dose")),
                decimal(fields.getValue("beverage")),
                decimal(fields.getValue("tds")),
                fields.getValue("method")
            ).getOrThrow()
        } else {
            null
        }
        buildString {
            append("Calculated ratio ${ratio.ratioString} by mass\n")
            if (ey !=
                null
            ) {
                append(
                    "Approximate extraction yield ${"%.2f".format(
                        java.util.Locale.US,
                        ey.extractionYieldPercent
                    )}%\n${ey.methodLabel}\n${ey.assumptions}\n${ey.provenance}"
                )
            } else if (mutableState.value.draft.lines.isNotEmpty()) {
                append("Extraction yield unavailable: additive solids affect cup TDS. Measure the base brew before additions.")
            } else {
                append(
                    "Extraction yield unavailable: enter measured beverage mass and measured TDS.\nTDS is never " +
                        "inferred from dose or water."
                )
            }
            if (fields.getValue("method") in
                setOf("French Press", "Immersion", "AeroPress")
            ) {
                append(
                    "\nImmersion: cup mass balance excludes retained liquid; this is approximate cup recovery, " +
                        "not total extracted solids."
                )
            }
        }
    }
    mutableState.update { it.copy(calculation = output.getOrElse { error -> error.message ?: "Invalid quantity." }) }
}

fun CoffeeViewModel.runManualCalculation(tool: String, inputs: Map<String, String>) = task("Calculating with explicit units") {
    require(tool in setOf("calculate_dilution", "calculate_concentration_ppm", "convert_units")) { "Unsupported manual calculation." }
    val result = graph.tools.execute(tool, inputs.mapValues { decimal(it.value) })
    require(result.isSuccess) { result.output }
    val text = when (val value = result.structuredData) {
        is CalculationResult.Dilution -> listOf(
            "Calculated final mass ${value.finalMassGrams} g",
            "Calculated final TDS ${value.finalTdsPercent}% w/w",
            value.assumptions,
            value.methodVersion,
            value.uncertainty
        ).joinToString("\n")
        is CalculationResult.Concentration -> listOf(
            "Calculated concentration ${value.ppm} ppm by mass",
            "${value.massFractionPercent}% w/w",
            value.assumptions,
            value.methodVersion,
            value.uncertainty
        ).joinToString("\n")
        is CalculationResult.UnitConversion -> listOf(
            "Calculated mass ${value.massGrams} g",
            "${value.volumeMl} mL × ${value.densityGramsPerMl} g/mL",
            value.assumptions,
            value.methodVersion,
            value.uncertainty
        ).joinToString("\n")
        else -> error("No calculation result.")
    }
    mutableState.update { it.copy(manualToolResult = text) }
}

internal fun CoffeeViewModel.buildRecipe() = mutableState.value.draft.build(mutableState.value.ingredients)
fun CoffeeViewModel.saveRecipe() = task("Saving immutable recipe revision") {
    val recipe = graph.repository.saveRecipe(buildRecipe())
    updateDraft(mutableState.value.draft.copy(id = recipe.id, group = recipe.revisionGroupId))
    graph.repository.saveSetting("draft", gson.toJson(mutableState.value.draft))
    diagnosticBuffer.log(LogLevel.INFO, "Recipe", "Saved recipe ${recipe.name}, revision ${recipe.version}")
    mutableState.update { it.copy(notice = "Saved ${recipe.name}, revision ${recipe.version}. Earlier revisions are retained.") }
}
fun CoffeeViewModel.copyRecipe(recipe: Recipe) = task("Copying recipe") {
    val copy = graph.repository.duplicateRecipe(recipe)
    mutableState.update { it.copy(notice = "Copied ${copy.name}; new independent history.") }
}
fun CoffeeViewModel.requestDeletion(kind: String, id: String) {
    mutableState.update { it.copy(deletion = kind to id) }
}
fun CoffeeViewModel.cancelDeletion() {
    mutableState.update { it.copy(deletion = null) }
}
fun CoffeeViewModel.confirmDeletion() = task("Deleting selected local record") {
    val deletion = mutableState.value.deletion ?: return@task
    when (deletion.first) {
        "recipe" -> graph.repository.deleteRecipe(deletion.second)
        "bean" -> graph.repository.deleteBean(deletion.second)
        "document" -> {
            graph.repository.removeDocument(deletion.second)
            graph.rag.clearVectors()
        }
        "all application records" -> {
            cancelGeneration()
            generation?.join()
            graph.native.unload()
            withDraftWritesPaused {
                withContext(NonCancellable) {
                    graph.repository.deleteApplicationRecords()
                    withContext(Dispatchers.Main.immediate) { publishDraft(RecipeDraft()) }
                }
            }
            graph.rag.clearVectors()
            mutableState.update { it.copy(loadedModel = null, selectedRecipeId = null, estimate = null, searchResult = null) }
        }
        else -> error("Unsupported deletion.")
    }
    mutableState.update { it.copy(deletion = null, notice = "Record deleted after confirmation.") }
}

fun CoffeeViewModel.saveBean(id: String?, values: Map<String, String>) = task("Saving coffee catalog entry") {
    val bean = CoffeeBean(
        id = id ?: UUID.randomUUID().toString(), name = values.getValue("name").trim(),
        country = values["country"].orEmpty().ifBlank { "Unknown" }, region = values["region"].orEmpty().ifBlank { "Unknown" },
        producer = values["producer"].orEmpty().ifBlank { "Unknown" }, variety = values["variety"].orEmpty().ifBlank { "Unknown" },
        process = values["process"].orEmpty().ifBlank { "Unknown" }, roastLevel = values["roast"].orEmpty().ifBlank { "Unknown" },
        altitudeMeters = optionalInt(values["altitude"].orEmpty(), "Altitude"),
        roastDate = values["roastDate"]?.ifBlank { null }, degasDays = optionalInt(values["degasDays"].orEmpty(), "Degas days"),
        densityGramsPerMl = optionalDecimal(values["density"].orEmpty()),
        provenance = values["provenance"].orEmpty().ifBlank { "User entry; supplier facts not verified" }, foodGrade = null
    )
    DomainValidation.validateBean(bean)
    if (id == null) graph.repository.addBean(bean) else graph.repository.updateBean(bean)
    mutableState.update { it.copy(notice = "Coffee entry saved locally.") }
}
fun CoffeeViewModel.saveIngredient(id: String?, values: Map<String, String>) = task("Saving ingredient constraints") {
    val ingredient = Ingredient(
        id = id ?: UUID.randomUUID().toString(),
        name = values.getValue("name").trim(),
        densityGramsPerMl = optionalDecimal(values["density"].orEmpty()),
        allergens = values["allergens"].orEmpty().split(',').map(String::trim).filter(String::isNotBlank),
        foodGrade = when (values["foodGrade"]) {
            "Confirmed food grade" -> true
            "Not food grade" -> false
            else -> null
        },
        supplierMaxPercent = optionalDecimal(values["limit"].orEmpty()),
        provenance = values["provenance"].orEmpty().ifBlank { "Unknown" }
    )
    DomainValidation.validateIngredient(ingredient)
    if (id == null) graph.repository.addIngredient(ingredient) else graph.repository.updateIngredient(ingredient)
    mutableState.update { it.copy(notice = "Ingredient saved. Unknown food-grade entries cannot be added to beverages.") }
}

fun CoffeeViewModel.selectSensoryRecipe(recipe: Recipe) = task("Preparing an uncalibrated estimate") {
    val bean = mutableState.value.beans.find { it.id == recipe.beanId }
    val ey = if (recipe.lines.isEmpty() && recipe.beverageMassGrams != null && recipe.measuredTdsPercent != null) {
        CoffeeScienceEngine.calculateExtractionYield(
            recipe.doseGrams,
            recipe.beverageMassGrams,
            recipe.measuredTdsPercent
        ).getOrNull()?.extractionYieldPercent
    } else {
        null
    }
    val profile = SensoryRulesEngine.estimateProfile(bean, recipe, ey)
    val record = StoredSensoryEstimate(UUID.randomUUID().toString(), recipe.id, profile)
    graph.repository.saveSensoryEstimate(record)
    mutableState.update { it.copy(selectedRecipeId = recipe.id, estimate = profile) }
}
fun CoffeeViewModel.recordSensory(scores: List<Int>, descriptors: String, notes: String, assessor: String, panel: String) =
    task("Recording human observation") {
        val recipeId = mutableState.value.selectedRecipeId ?: error("Select a saved recipe revision.")
        require(scores.size == 8) { "Eight cupping scores required." }
        val parsedDescriptors = descriptors.split(',').map(String::trim).filter(String::isNotBlank).map { entry ->
            val parts = entry.split(':', limit = 2)
            val intensity = if (parts.size > 1) parts[1].trim().toIntOrNull() ?: error("Descriptor intensity must be 1–10.") else null
            parts[0].trim() to intensity
        }
        graph.repository.saveSensoryObservation(
            SensoryObservation(
                id = UUID.randomUUID().toString(), recipeId = recipeId, aroma = scores[0], acidity = scores[1], sweetness = scores[2],
                bitterness = scores[3], body = scores[4], clarity = scores[5], finish = scores[6], balance = scores[7],
                tastingDescriptors = parsedDescriptors.map { it.first },
                descriptorIntensities = parsedDescriptors.mapNotNull { (name, intensity) -> intensity?.let { name to it } }.toMap(),
                notes = notes, assessor = assessor, panel = panel
            )
        )
        mutableState.update { it.copy(notice = "Observed profile recorded for this revision.") }
    }
