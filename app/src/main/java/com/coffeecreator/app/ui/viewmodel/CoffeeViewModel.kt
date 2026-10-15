@file:OptIn(kotlinx.coroutines.FlowPreview::class)

package com.coffeecreator.app.ui.viewmodel

import android.content.Intent
import android.net.Uri
import android.os.SystemClock
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coffeecreator.app.AppGraph
import com.coffeecreator.app.core.ai.NativeChatTurn
import com.coffeecreator.app.core.ai.NativeGenerationOptions
import com.coffeecreator.app.core.ai.NativeLlmStatus
import com.coffeecreator.app.core.ai.RagSearchResult
import com.coffeecreator.app.core.data.BackupCodec
import com.coffeecreator.app.core.engine.CoffeeScienceEngine
import com.coffeecreator.app.core.engine.DomainValidation
import com.coffeecreator.app.core.engine.JsonInputPolicy
import com.coffeecreator.app.core.engine.SensoryRulesEngine
import com.coffeecreator.app.core.filesystem.BuiltInAssets
import com.coffeecreator.app.core.filesystem.DocumentImporter.Companion.readBytesBounded
import com.coffeecreator.app.core.model.AssistantPersona
import com.coffeecreator.app.core.model.CalculationResult
import com.coffeecreator.app.core.model.ChatMessage
import com.coffeecreator.app.core.model.CoffeeBean
import com.coffeecreator.app.core.model.DocumentChunk
import com.coffeecreator.app.core.model.Ingredient
import com.coffeecreator.app.core.model.InputLimits
import com.coffeecreator.app.core.model.KnowledgeDocument
import com.coffeecreator.app.core.model.ModelAsset
import com.coffeecreator.app.core.model.Recipe
import com.coffeecreator.app.core.model.RecipeLine
import com.coffeecreator.app.core.model.SensoryEstimate
import com.coffeecreator.app.core.model.SensoryObservation
import com.coffeecreator.app.core.model.StoredSensoryEstimate
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class CoffeeUiState(
    val ready: Boolean = false,
    val busy: Boolean = false,
    val progress: String = "",
    val notice: String? = null,
    val error: String? = null,
    val beans: List<CoffeeBean> = emptyList(),
    val ingredients: List<Ingredient> = emptyList(),
    val recipes: List<Recipe> = emptyList(),
    val observations: List<SensoryObservation> = emptyList(),
    val estimates: List<StoredSensoryEstimate> = emptyList(),
    val documents: List<KnowledgeDocument> = emptyList(),
    val chunks: List<DocumentChunk> = emptyList(),
    val models: List<ModelAsset> = emptyList(),
    val personas: List<AssistantPersona> = emptyList(),
    val selectedPersona: AssistantPersona? = null,
    val messages: List<ChatMessage> = emptyList(),
    val directory: String? = null,
    val draft: RecipeDraft = RecipeDraft(),
    val canUndoDraft: Boolean = false,
    val calculation: String = "",
    val manualToolResult: String? = null,
    val selectedRecipeId: String? = null,
    val estimate: SensoryEstimate? = null,
    val searchResult: RagSearchResult? = null,
    val inspectedChunk: DocumentChunk? = null,
    val sourceOpenError: String? = null,
    val generating: Boolean = false,
    val loadedModel: String? = null,
    val pendingRecipe: Recipe? = null,
    val pendingToolJson: String? = null,
    val theme: String = "system",
    val maxTokens: Int = 128,
    val temperature: Float = 0.4f,
    val onboarded: Boolean = false,
    val timerSeconds: Int = 0,
    val timerRunning: Boolean = false,
    val deletion: Pair<String, String>? = null
)

@HiltViewModel
class CoffeeViewModel @Inject constructor(val graph: AppGraph, private val saved: SavedStateHandle) : ViewModel() {
    private val gson = Gson()
    private val _state = MutableStateFlow(CoffeeUiState())
    val state: StateFlow<CoffeeUiState> = _state.asStateFlow()
    private val draftWrites = MutableStateFlow<RecipeDraft?>(null)
    private var generation: Job? = null
    private var timer: Job? = null
    private var timerOrigin = 0L
    private var undoDraft: RecipeDraft? = null
    private var draftAutosave: Job? = null

    @Volatile private var replacingDraft = false

    init {
        observeRepository()
        task("Opening local records") {
            graph.repository.initialize()
            BuiltInAssets.catalog.forEach { asset ->
                if (graph.repository.models.value.none { it.id == asset.id }) graph.repository.upsertModel(asset)
            }
            graph.repository.models.value.filter {
                it.status in
                    setOf(com.coffeecreator.app.core.model.ModelStatus.VERIFYING, com.coffeecreator.app.core.model.ModelStatus.PUBLISHING)
            }
                .forEach { model ->
                    graph.repository.upsertModel(
                        model.copy(
                            status = com.coffeecreator.app.core.model.ModelStatus.FAILED,
                            error = "Interrupted verification/publication; retry import or resume the transfer."
                        )
                    )
                }
            val draftJson = saved.get<String>("draft") ?: graph.repository.settings.value["draft"]
            if (!draftJson.isNullOrBlank()) {
                runCatching {
                    JsonInputPolicy.validateDepth(draftJson)
                    gson.fromJson(draftJson, RecipeDraft::class.java).also { it.validate() }
                }.getOrNull()?.let { draft ->
                    _state.update { it.copy(draft = draft) }
                }
            }
            undoDraft = saved.get<String>("undoDraft")?.let { json ->
                runCatching {
                    JsonInputPolicy.validateDepth(json)
                    gson.fromJson(json, RecipeDraft::class.java).also { it.validate() }
                }.getOrNull()
            }
            _state.update { it.copy(ready = true, canUndoDraft = undoDraft != null) }
            calculate()
        }
        viewModelScope.launch {
            graph.preferences.data.catch { failure ->
                _state.update { it.copy(error = "Preferences unavailable: ${failure.message}. Recipe records are retained.") }
            }.collect { prefs ->
                _state.update {
                    it.copy(
                        theme = prefs.theme.ifBlank { "system" },
                        maxTokens = prefs.maxTokens.takeIf { count -> count in 16..256 } ?: 128,
                        temperature = prefs.temperature.takeIf { value -> value in 0.1f..1.2f } ?: 0.4f,
                        onboarded = prefs.onboarded
                    )
                }
            }
        }
        observeDraftWrites()
        viewModelScope.launch {
            graph.native.status.collect { status ->
                if (status is NativeLlmStatus.Unloaded || status is NativeLlmStatus.Failed) {
                    _state.update { it.copy(loadedModel = null) }
                }
            }
        }
    }

    private fun observeDraftWrites() {
        draftAutosave = viewModelScope.launch {
            draftWrites.debounce(DRAFT_DEBOUNCE_MS).collect { draft ->
                if (draft != null && graph.repository.ready.value) {
                    try {
                        graph.repository.saveSetting("draft", gson.toJson(draft))
                    } catch (cancelled: CancellationException) {
                        throw cancelled
                    } catch (boundaryFailure: Exception) {
                        _state.update { it.copy(error = "Draft not autosaved: ${boundaryFailure.message}. Saved revisions are retained.") }
                    }
                }
            }
        }
    }

    private suspend fun <T> withDraftWritesPaused(block: suspend () -> T): T {
        // Serialize closing the gate with lifecycle flush check-through-enqueue.
        withContext(Dispatchers.Main.immediate) { replacingDraft = true }
        try {
            draftAutosave?.cancelAndJoin()
            graph.awaitBackgroundWrites()
            return block()
        } finally {
            withContext(NonCancellable + Dispatchers.Main.immediate) {
                replacingDraft = false
                observeDraftWrites()
            }
        }
    }

    private fun observeRepository() {
        val repository = graph.repository
        viewModelScope.launch { repository.errorState.collect { error -> if (error != null) _state.update { it.copy(error = error) } } }
        viewModelScope.launch { repository.beans.collect { beans -> _state.update { it.copy(beans = beans) } } }
        viewModelScope.launch { repository.ingredients.collect { ingredients -> _state.update { it.copy(ingredients = ingredients) } } }
        viewModelScope.launch { repository.recipes.collect { recipes -> _state.update { it.copy(recipes = recipes) } } }
        viewModelScope.launch {
            repository.sensoryObservations.collect { observations -> _state.update { it.copy(observations = observations) } }
        }
        viewModelScope.launch {
            repository.sensoryEstimates.collect { estimates -> _state.update { it.copy(estimates = estimates) } }
        }
        viewModelScope.launch { repository.documents.collect { documents -> _state.update { it.copy(documents = documents) } } }
        viewModelScope.launch { repository.chunks.collect { chunks -> _state.update { it.copy(chunks = chunks) } } }
        viewModelScope.launch { repository.models.collect { models -> _state.update { it.copy(models = models) } } }
        viewModelScope.launch { repository.personas.collect { personas -> _state.update { it.copy(personas = personas) } } }
        viewModelScope.launch { repository.selectedPersona.collect { persona -> _state.update { it.copy(selectedPersona = persona) } } }
        viewModelScope.launch {
            repository.chatMessages.collect { messages -> if (!_state.value.generating) _state.update { it.copy(messages = messages) } }
        }
        viewModelScope.launch { repository.safDirectoryUri.collect { uri -> _state.update { it.copy(directory = uri) } } }
    }

    fun updateField(key: String, value: String) {
        require(key in RecipeDraft.defaultFields())
        if (value.length > InputLimits.METADATA_CHARS) return
        val draft = _state.value.draft.copy(fields = _state.value.draft.fields + (key to value))
        updateDraft(draft)
        calculate()
    }
    private fun updateDraft(draft: RecipeDraft, recordUndo: Boolean = false) {
        if (replacingDraft) return
        publishDraft(draft, recordUndo)
    }
    private fun publishDraft(draft: RecipeDraft, recordUndo: Boolean = false) {
        if (recordUndo && draft == _state.value.draft) return
        undoDraft = _state.value.draft.takeIf { recordUndo && it != draft }
        if (undoDraft != null) saved["undoDraft"] = gson.toJson(undoDraft) else saved.remove<String>("undoDraft")
        _state.update { it.copy(draft = draft, canUndoDraft = undoDraft != null, notice = null, error = null) }
        val json = gson.toJson(draft)
        saved["draft"] = json
        draftWrites.value = draft
    }
    fun newRecipe() {
        updateDraft(RecipeDraft(), recordUndo = true)
        calculate()
    }
    fun undoDraftChange() {
        val previous = undoDraft ?: return
        updateDraft(previous)
        calculate()
    }
    fun editRecipe(recipe: Recipe) {
        updateDraft(RecipeDraft.fromRecipe(recipe), recordUndo = true)
        calculate()
    }
    fun addLine(ingredientId: String, quantity: String, unit: String, density: String) = task("Validating ingredient line") {
        val line = RecipeLine(ingredientId, decimal(quantity), unit, optionalDecimal(density))
        require(line.quantity > 0 && line.unit in listOf("g", "mg", "mL")) { "Positive quantity and g/mg/mL unit required." }
        if (unit == "mL") {
            val value = line.densityGramsPerMl ?: _state.value.ingredients.find { it.id == ingredientId }?.densityGramsPerMl
            CoffeeScienceEngine.convertVolumeToMass(line.quantity, value).getOrThrow()
        }
        updateDraft(
            _state.value.draft.copy(
                lines = _state.value.draft.lines + line,
                fields =
                _state.value.draft.fields + ("allergens" to "")
            )
        )
    }
    fun removeLine(index: Int) {
        if (index !in _state.value.draft.lines.indices) return
        updateDraft(
            _state.value.draft.copy(
                lines = _state.value.draft.lines.filterIndexed { i, _ -> i != index },
                fields =
                _state.value.draft.fields + ("allergens" to "")
            ),
            recordUndo = true
        )
    }

    fun calculate() {
        val fields = _state.value.draft.fields
        val output = runCatching {
            val ratio = CoffeeScienceEngine.calculateBrewRatio(
                decimal(fields.getValue("dose")),
                decimal(fields.getValue("water"))
            ).getOrThrow()
            val ey = if (_state.value.draft.lines.isEmpty() &&
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
                } else if (_state.value.draft.lines.isNotEmpty()) {
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
        _state.update { it.copy(calculation = output.getOrElse { error -> error.message ?: "Invalid quantity." }) }
    }

    fun runManualCalculation(tool: String, inputs: Map<String, String>) = task("Calculating with explicit units") {
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
        _state.update { it.copy(manualToolResult = text) }
    }

    private fun buildRecipe() = _state.value.draft.build(_state.value.ingredients)
    fun saveRecipe() = task("Saving immutable recipe revision") {
        val recipe = graph.repository.saveRecipe(buildRecipe())
        updateDraft(_state.value.draft.copy(id = recipe.id, group = recipe.revisionGroupId))
        graph.repository.saveSetting("draft", gson.toJson(_state.value.draft))
        _state.update { it.copy(notice = "Saved ${recipe.name}, revision ${recipe.version}. Earlier revisions are retained.") }
    }
    fun copyRecipe(recipe: Recipe) = task("Copying recipe") {
        val copy = graph.repository.duplicateRecipe(recipe)
        _state.update { it.copy(notice = "Copied ${copy.name}; new independent history.") }
    }
    fun requestDeletion(kind: String, id: String) {
        _state.update { it.copy(deletion = kind to id) }
    }
    fun cancelDeletion() {
        _state.update { it.copy(deletion = null) }
    }
    fun confirmDeletion() = task("Deleting selected local record") {
        val deletion = _state.value.deletion ?: return@task
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
                _state.update { it.copy(loadedModel = null, selectedRecipeId = null, estimate = null, searchResult = null) }
            }
            else -> error("Unsupported deletion.")
        }
        _state.update { it.copy(deletion = null, notice = "Record deleted after confirmation.") }
    }

    fun saveBean(id: String?, values: Map<String, String>) = task("Saving coffee catalog entry") {
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
        _state.update { it.copy(notice = "Coffee entry saved locally.") }
    }
    fun saveIngredient(id: String?, values: Map<String, String>) = task("Saving ingredient constraints") {
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
        _state.update { it.copy(notice = "Ingredient saved. Unknown food-grade entries cannot be added to beverages.") }
    }

    fun selectSensoryRecipe(recipe: Recipe) = task("Preparing an uncalibrated estimate") {
        val bean = _state.value.beans.find { it.id == recipe.beanId }
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
        _state.update { it.copy(selectedRecipeId = recipe.id, estimate = profile) }
    }
    fun recordSensory(scores: List<Int>, descriptors: String, notes: String, assessor: String, panel: String) =
        task("Recording human observation") {
            val recipeId = _state.value.selectedRecipeId ?: error("Select a saved recipe revision.")
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
            _state.update { it.copy(notice = "Observed profile recorded for this revision.") }
        }

    fun importDocument(uri: Uri) = task("Importing local source") {
        val document = graph.importer.import(uri, ::progress)
        graph.rag.index(graph.repository.chunks.value.filter { it.documentId == document.id }) { index, total ->
            progress("Embedding source chunks $index / $total")
        }
        _state.update { it.copy(notice = "Indexed ${document.title} with real source provenance.") }
    }
    fun reindex(document: KnowledgeDocument) = task("Reindexing retained chunks") {
        graph.rag.index(graph.repository.chunks.value.filter { it.documentId == document.id })
        _state.update { it.copy(notice = "Source chunks indexed; repeated content is deduplicated.") }
    }
    fun searchDocuments(query: String) = task("Retrieving local evidence") {
        graph.rag.index(graph.repository.chunks.value)
        val result = graph.rag.search(query)
        _state.update { it.copy(searchResult = result) }
    }
    fun inspectChunk(chunk: DocumentChunk?) {
        _state.update { it.copy(inspectedChunk = chunk, sourceOpenError = null) }
    }
    fun openOriginal(context: android.content.Context) {
        val chunk = _state.value.inspectedChunk ?: return
        viewModelScope.launch {
            try {
                val source = _state.value.documents.find { it.id == chunk.documentId }
                require(source != null && source.sourceUri.startsWith("content://")) {
                    "Original grant unavailable. Read the retained excerpt here."
                }
                val uri = Uri.parse(source.sourceUri)
                withContext(Dispatchers.IO) {
                    context.contentResolver.openInputStream(uri)?.close() ?: error("Source access unavailable. Import the source again.")
                }
                context.startActivity(
                    Intent(Intent.ACTION_VIEW).setDataAndType(
                        uri,
                        if (source.fileType ==
                            "PDF"
                        ) {
                            "application/pdf"
                        } else {
                            "text/plain"
                        }
                    )
                        .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                )
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (boundaryFailure: Exception) {
                _state.update {
                    it.copy(
                        sourceOpenError = (boundaryFailure.message ?: "Original source unavailable.") +
                            " Retained excerpts remain readable."
                    )
                }
            }
        }
    }

    fun selectDirectory(uri: Uri) = task("Verifying chosen directory") {
        val count = graph.assets.selectDirectory(uri)
        _state.update { it.copy(notice = "Directory grant saved; $count verified model bundle(s) recovered.") }
    }
    fun discoverAssets() = task("Verifying published bundles") {
        val count = graph.assets.discover()
        _state.update { it.copy(notice = "Recovered $count verified model bundle(s). Exports restore app records separately.") }
    }
    fun registerManifest(json: String) = task("Validating model manifest") { graph.assets.registerManifest(json) }
    fun modelAction(asset: ModelAsset, action: String) = task("Model: $action") {
        when (action) {
            "download" -> graph.assets.requestDownload(asset)
            "pause" -> graph.assets.pause(asset)
            "cancel" -> graph.assets.cancel(asset)
            "forget" -> graph.assets.forget(asset)
            "load" -> {
                graph.native.load(graph.assets.stageForInference(asset))
                _state.update { it.copy(loadedModel = asset.name, notice = "Loaded actual local GGUF: ${asset.name}") }
            }
            "unload" -> {
                graph.native.unload()
                _state.update { it.copy(loadedModel = null, notice = "Native model unloaded.") }
            }
            else -> error("Unknown model action.")
        }
    }
    fun importModel(asset: ModelAsset, uri: Uri) = task("Verifying and publishing local GGUF") { graph.assets.importModel(asset, uri) }

    fun exportBackup(uri: Uri, resolver: android.content.ContentResolver) = task("Exporting complete local records") {
        val json = withDraftWritesPaused {
            graph.repository.saveSetting("draft", gson.toJson(_state.value.draft))
            graph.repository.exportBackupJson()
        }
        resolver.openOutputStream(uri, "wt")?.use { it.write(json.toByteArray(Charsets.UTF_8)) } ?: error("Cannot write export.")
        _state.update {
            it.copy(notice = "Complete integrity-checked backup exported. This file contains personal records; keep it private.")
        }
    }
    fun restoreBackup(uri: Uri, resolver: android.content.ContentResolver) = task("Validating and restoring backup") {
        val json = resolver.openInputStream(uri)?.use { it.readBytesBounded(20 * 1024 * 1024).toString(Charsets.UTF_8) }
            ?: error("Cannot read backup.")
        restoreRecords(json)
        graph.rag.clearVectors()
        _state.update { it.copy(notice = "Validated backup restored atomically. Reselect source/model directories to restore grants.") }
    }
    internal suspend fun restoreRecords(json: String) {
        // Validate UI-owned draft fields before the repository replaces any records.
        val restored = withContext(Dispatchers.IO) {
            val draftJson = BackupCodec.decode(json).payload.settings["draft"]
            if (draftJson.isNullOrBlank()) {
                RecipeDraft()
            } else {
                JsonInputPolicy.validateDepth(draftJson)
                gson.fromJson(draftJson, RecipeDraft::class.java).also { it.validate() }
            }
        }
        cancelGeneration()
        generation?.join()
        withDraftWritesPaused {
            // Finish synchronizing state after the bounded transaction commits,
            // even when the originating activity is finishing.
            withContext(NonCancellable) {
                graph.repository.restoreBackupJson(json)
                withContext(Dispatchers.Main.immediate) {
                    publishDraft(restored)
                    calculate()
                    _state.update {
                        it.copy(
                            selectedRecipeId = null,
                            estimate = null,
                            searchResult = null,
                            inspectedChunk = null,
                            pendingRecipe = null,
                            pendingToolJson = null
                        )
                    }
                }
            }
        }
    }
    fun setTheme(theme: String) = task("Saving theme preference") { graph.preferences.setTheme(theme) }
    fun setGeneration(tokens: Int, temperature: Float) = task("Saving bounded generation preferences") {
        graph.preferences.setGeneration(tokens, temperature)
    }
    fun finishOnboarding() = task("Saving offline introduction") { graph.preferences.finishOnboarding() }
    fun selectPersona(persona: AssistantPersona) = task("Saving persona selection") { graph.repository.selectPersona(persona) }
    fun updatePersona(persona: AssistantPersona, text: String) = task("Saving persona preference") {
        require(text.isNotBlank() && text.length <= InputLimits.PERSONA_CHARS) { "Persona must contain 1 to 2000 characters." }
        graph.repository.updatePersona(persona.copy(systemPrompt = text))
    }

    fun sendChat(prompt: String) {
        if (prompt.isBlank() || _state.value.generating || _state.value.busy || replacingDraft) return
        if (prompt.length > InputLimits.QUERY_CHARS || '\u0000' in prompt) {
            _state.update { it.copy(error = "Message exceeds the safe input budget; shorten it before sending.") }
            return
        }
        generation = viewModelScope.launch {
            _state.update { it.copy(generating = true, error = null) }
            val user = ChatMessage(UUID.randomUUID().toString(), "user", prompt)
            val replyId = UUID.randomUUID().toString()
            _state.update { it.copy(messages = it.messages + user + ChatMessage(replyId, "assistant", "")) }
            val response = StringBuilder()
            try {
                if (prompt.trim().startsWith('{')) {
                    val result = withContext(Dispatchers.Default) { graph.tools.executeJson(prompt) }
                    response.append("Exact Kotlin tool result (${result.toolName}):\n${result.output}")
                    _state.update { state ->
                        state.copy(
                            messages = state.messages.map { message ->
                                if (message.id ==
                                    replyId
                                ) {
                                    message.copy(role = "tool", toolCallName = result.toolName, toolResultJson = result.output)
                                } else {
                                    message
                                }
                            }
                        )
                    }
                    if (result.requiresUserConfirmation || result.toolName == "recommend_recipe") {
                        _state.update { it.copy(pendingRecipe = result.structuredData as? Recipe) }
                    }
                    replaceReply(replyId, response.toString())
                } else {
                    require(graph.native.initialized) {
                        "No local model loaded. Use Models to install/load a verified GGUF, or run a typed tool with explicit arguments."
                    }
                    val persona = _state.value.selectedPersona ?: error("Select a persona.")
                    if (graph.repository.chunks.value.isNotEmpty()) graph.rag.index(graph.repository.chunks.value)
                    val evidence = if (graph.repository.chunks.value.isNotEmpty()) {
                        graph.rag.search(prompt).let {
                            it.copy(matchedChunks = it.matchedChunks.take(2), citations = it.citations.take(2))
                        }
                    } else {
                        null
                    }
                    if (evidence != null) _state.update { it.copy(searchResult = evidence) }
                    val history = _state.value.messages.dropLast(2).filter {
                        it.content.isNotBlank() &&
                            it.role in setOf("user", "assistant")
                    }.takeLast(2)
                        .map { NativeChatTurn(it.role, it.content.take(300)) }
                    var deliveredSources = emptyList<DocumentChunk>()
                    graph.coordinator.streamChatResponse(
                        prompt,
                        persona,
                        history,
                        evidence,
                        NativeGenerationOptions(maxTokens = _state.value.maxTokens, temperature = _state.value.temperature),
                        onSources = { deliveredSources = it },
                        onTool = ::addToolCard
                    ).collect { token ->
                        response.append(token)
                        val actualCitations = deliveredSources.map { it.citationText }.filter { response.contains(it) }
                        replaceReply(replyId, response.toString(), actualCitations, deliveredSources.map { it.id })
                    }
                }
            } catch (cancelled: CancellationException) {
                graph.native.cancel()
                replaceReply(replyId, response.toString() + "\nGeneration cancelled.")
                throw cancelled
            } catch (boundaryFailure: Exception) {
                replaceReply(replyId, response.toString() + "\n${boundaryFailure.message}")
                _state.update { it.copy(error = boundaryFailure.message) }
            } finally {
                _state.update { it.copy(generating = false) }
                withContext(kotlinx.coroutines.NonCancellable + Dispatchers.IO) {
                    try {
                        graph.repository.saveChatMessages(_state.value.messages.takeLast(100))
                    } catch (boundaryFailure: Exception) {
                        _state.update { it.copy(error = "Chat history was not saved: ${boundaryFailure.message}") }
                    }
                }
            }
        }
    }
    private fun addToolCard(result: com.coffeecreator.app.core.ai.ToolExecutionResult) {
        val card = ChatMessage(
            UUID.randomUUID().toString(),
            "tool",
            result.output,
            toolCallName = result.toolName,
            toolResultJson = result.output
        )
        _state.update { it.copy(messages = it.messages + card, pendingToolJson = result.inputConfirmationJson ?: it.pendingToolJson) }
        if (result.requiresUserConfirmation || result.toolName == "recommend_recipe") {
            _state.update { it.copy(pendingRecipe = result.structuredData as? Recipe) }
        }
    }
    private fun replaceReply(id: String, content: String, citations: List<String> = emptyList(), sourceIds: List<String> = emptyList()) {
        _state.update {
            it.copy(
                messages = it.messages.map { message ->
                    if (message.id ==
                        id
                    ) {
                        message.copy(content = content, citations = citations, sourceChunkIds = sourceIds)
                    } else {
                        message
                    }
                }
            )
        }
    }
    fun cancelGeneration() {
        graph.native.cancel()
        generation?.cancel()
    }
    fun confirmAiRecipe() = task("Saving user-confirmed AI draft") {
        val draft = _state.value.pendingRecipe ?: error("No recipe draft awaiting approval.")
        graph.repository.saveRecipe(draft)
        _state.update { it.copy(pendingRecipe = null, notice = "Saved confirmed draft. No measurements were invented.") }
    }
    fun dismissAiRecipe() {
        _state.update { it.copy(pendingRecipe = null) }
    }
    fun confirmToolInputs() {
        if (_state.value.busy || replacingDraft) return
        val json = _state.value.pendingToolJson ?: return
        _state.update { it.copy(pendingToolJson = null) }
        sendChat(json)
    }
    fun dismissToolInputs() {
        _state.update { it.copy(pendingToolJson = null) }
    }

    fun toggleTimer() {
        if (_state.value.timerRunning) {
            timer?.cancel()
            _state.update { it.copy(timerRunning = false) }
        } else {
            timerOrigin = SystemClock.elapsedRealtime() - _state.value.timerSeconds * MILLIS_PER_SECOND
            _state.update { it.copy(timerRunning = true) }
            timer = viewModelScope.launch {
                while (true) {
                    _state.update { it.copy(timerSeconds = ((SystemClock.elapsedRealtime() - timerOrigin) / MILLIS_PER_SECOND).toInt()) }
                    delay(TIMER_REFRESH_MS)
                }
            }
        }
    }
    fun resetTimer() {
        timer?.cancel()
        _state.update { it.copy(timerSeconds = 0, timerRunning = false) }
    }
    fun clearNotice() {
        _state.update { it.copy(notice = null, error = null) }
    }
    fun flushDraft() {
        if (_state.value.ready && !replacingDraft) graph.persistDraft(gson.toJson(_state.value.draft))
    }
    private fun progress(message: String) {
        _state.update { it.copy(progress = message) }
    }
    private fun decimal(input: String) = DomainValidation.parseDecimal(input).toDouble()
    private fun optionalDecimal(input: String) = input.takeIf(String::isNotBlank)?.let(::decimal)
    private fun optionalInt(input: String, label: String): Int? = input.takeIf(String::isNotBlank)?.let {
        it.toIntOrNull() ?: error("$label must be a whole number.")
    }

    private fun task(description: String, block: suspend () -> Unit) {
        if (_state.value.busy) return
        _state.update { it.copy(busy = true, progress = description, error = null, notice = null) }
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) { block() }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (boundaryFailure: Exception) {
                _state.update { it.copy(error = boundaryFailure.message ?: "Operation failed. Your existing records were retained.") }
            } finally {
                _state.update { it.copy(busy = false, progress = "") }
            }
        }
    }

    private companion object {
        const val DRAFT_DEBOUNCE_MS = 400L
        const val MILLIS_PER_SECOND = 1000L
        const val TIMER_REFRESH_MS = 250L
    }

    override fun onCleared() {
        graph.native.cancel()
        timer?.cancel()
        super.onCleared()
    }
}
