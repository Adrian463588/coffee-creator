@file:OptIn(kotlinx.coroutines.FlowPreview::class)

package com.coffeecreator.app.ui.viewmodel

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coffeecreator.app.AppGraph
import com.coffeecreator.app.core.ai.NativeLlmStatus
import com.coffeecreator.app.core.ai.RagSearchResult
import com.coffeecreator.app.core.ai.personalization.OvatAdjustmentProposal
import com.coffeecreator.app.core.ai.personalization.PalateProfile
import com.coffeecreator.app.core.ai.personalization.RecommendationItem
import com.coffeecreator.app.core.ai.personalization.SafetyAuditResult
import com.coffeecreator.app.core.ai.personalization.WeatherParameters
import com.coffeecreator.app.core.ble.BleScaleDriver
import com.coffeecreator.app.core.brew.AudioCueType
import com.coffeecreator.app.core.brew.BrewSessionState
import com.coffeecreator.app.core.brew.HapticCueType
import com.coffeecreator.app.core.catalog.BagOcrParsedDraft
import com.coffeecreator.app.core.catalog.EquipmentOcrParsedDraft
import com.coffeecreator.app.core.catalog.DetailedBeanRecord
import com.coffeecreator.app.core.catalog.EquipmentItem
import com.coffeecreator.app.core.catalog.FoodGradeIngredientItem
import com.coffeecreator.app.core.catalog.GreenCoffeeLot
import com.coffeecreator.app.core.catalog.GrinderRecord
import com.coffeecreator.app.core.catalog.ImportDryRunSummary
import com.coffeecreator.app.core.catalog.RoastBatchRecord
import com.coffeecreator.app.core.catalog.WaterMineralProfile
import com.coffeecreator.app.core.data.BackupCodec
import com.coffeecreator.app.core.engine.DomainValidation
import com.coffeecreator.app.core.engine.JsonInputPolicy
import com.coffeecreator.app.core.filesystem.BuiltInAssets
import com.coffeecreator.app.core.filesystem.DocumentImporter.Companion.readBytesBounded
import com.coffeecreator.app.core.journal.BrewFilterCriteria
import com.coffeecreator.app.core.journal.BrewJournalEntry
import com.coffeecreator.app.core.journal.BrewSessionComparison
import com.coffeecreator.app.core.journal.BrewStreakResult
import com.coffeecreator.app.core.journal.CafeCupEntry
import com.coffeecreator.app.core.journal.CaffeineEstimate
import com.coffeecreator.app.core.journal.ImportConflictPreview
import com.coffeecreator.app.core.journal.MonthlyBrewStatistics
import com.coffeecreator.app.core.journal.OvatRecommendation
import com.coffeecreator.app.core.model.AssistantPersona
import com.coffeecreator.app.core.model.ChatMessage
import com.coffeecreator.app.core.model.CoffeeBean
import com.coffeecreator.app.core.model.DocumentChunk
import com.coffeecreator.app.core.model.Ingredient
import com.coffeecreator.app.core.model.InputLimits
import com.coffeecreator.app.core.model.KnowledgeDocument
import com.coffeecreator.app.core.model.ModelAsset
import com.coffeecreator.app.core.model.Recipe
import com.coffeecreator.app.core.model.SensoryEstimate
import com.coffeecreator.app.core.model.SensoryObservation
import com.coffeecreator.app.core.model.StoredSensoryEstimate
import com.coffeecreator.app.core.reliability.DiagnosticLogBuffer
import com.coffeecreator.app.core.reliability.DiagnosticLogEntry
import com.coffeecreator.app.core.reliability.LogLevel
import com.coffeecreator.app.core.reliability.SystemDegradationState
import com.coffeecreator.app.core.reliability.UserUnitPreferences
import com.coffeecreator.app.core.sensory.CuppingSession
import com.coffeecreator.app.core.sensory.PhysicalQcRecord
import com.coffeecreator.app.core.sensory.QcReportReceipt
import com.coffeecreator.app.core.sensory.RoastRestObservation
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.map
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
    val deletion: Pair<String, String>? = null,
    val brewSession: BrewSessionState? = null,
    val isScaleScanning: Boolean = false,
    val activeHapticCue: HapticCueType? = null,
    val activeAudioCue: AudioCueType? = null,
    val brewCurveRmse: Double? = null,
    val journalEntries: List<BrewJournalEntry> = emptyList(),
    val cafeCupEntries: List<CafeCupEntry> = emptyList(),
    val journalFilterCriteria: BrewFilterCriteria = BrewFilterCriteria(),
    val selectedComparisonA: BrewJournalEntry? = null,
    val selectedComparisonB: BrewJournalEntry? = null,
    val activeComparison: BrewSessionComparison? = null,
    val activeOvatRecommendation: OvatRecommendation? = null,
    val activeCaffeineEstimate: CaffeineEstimate? = null,
    val brewStreak: BrewStreakResult? = null,
    val monthlyStats: MonthlyBrewStatistics? = null,
    val importConflictPreview: ImportConflictPreview? = null,
    val cuppingSessions: List<CuppingSession> = emptyList(),
    val activeCuppingSessionId: String? = null,
    val selectedCuppingSampleId: String? = null,
    val physicalQcRecords: Map<String, PhysicalQcRecord> = emptyMap(),
    val roastRestObservations: List<RoastRestObservation> = emptyList(),
    val generatedQcReceipt: QcReportReceipt? = null,
    val palateProfile: PalateProfile = PalateProfile(),
    val weatherParams: WeatherParameters = WeatherParameters(),
    val ovatAdjustmentProposal: OvatAdjustmentProposal? = null,
    val inventoryRecommendations: List<RecommendationItem> = emptyList(),
    val safetyAuditResult: SafetyAuditResult? = null,
    val unitPreferences: UserUnitPreferences = UserUnitPreferences(),
    val degradationState: SystemDegradationState = SystemDegradationState(),
    val selectedHelpTopicId: String? = null,
    val diagnosticLogSnapshot: List<DiagnosticLogEntry> = emptyList(),
    val detailedBeans: List<DetailedBeanRecord> = emptyList(),
    val grinders: List<GrinderRecord> = emptyList(),
    val equipmentCatalog: List<EquipmentItem> = emptyList(),
    val waterProfiles: List<WaterMineralProfile> = emptyList(),
    val foodGradeIngredients: List<FoodGradeIngredientItem> = emptyList(),
    val greenCoffeeLots: List<GreenCoffeeLot> = emptyList(),
    val roastBatches: List<RoastBatchRecord> = emptyList(),
    val selectedBagId: String? = null,
    val pendingOcrDraft: BagOcrParsedDraft? = null,
    val pendingEquipmentOcrDraft: EquipmentOcrParsedDraft? = null,
    val photoOcrError: String? = null,
    val photoOcrNotice: String? = null,
    val catalogImportPreview: ImportDryRunSummary? = null
)

private data class RecommendationInputs(
    val palate: PalateProfile,
    val journalEntries: List<BrewJournalEntry>,
    val inventory: List<DetailedBeanRecord>,
    val equipment: List<EquipmentItem>
)

private const val DRAFT_DEBOUNCE_MS = 400L
internal const val MILLIS_PER_SECOND = 1000L
internal const val TIMER_REFRESH_MS = 250L

internal fun ModelAsset.isSupportedByNativeRuntime(): Boolean =
    format.startsWith("GGUF", ignoreCase = true) && runtime == "llama.cpp" && tokenizerSupport.isNotBlank()

@HiltViewModel
class CoffeeViewModel @Inject constructor(val graph: AppGraph, internal val saved: SavedStateHandle) : ViewModel() {
    internal val gson = Gson()
    val diagnosticBuffer = DiagnosticLogBuffer(500)
    internal val _state = MutableStateFlow(CoffeeUiState())
    val state: StateFlow<CoffeeUiState> = _state.asStateFlow()
    internal val draftWrites = MutableStateFlow<RecipeDraft?>(null)
    internal var generation: Job? = null
    internal var photoOcrJob: Job? = null
    internal var timer: Job? = null
    internal var timerOrigin = 0L
    internal var undoDraft: RecipeDraft? = null
    private var draftAutosave: Job? = null
    internal var scaleDriver: BleScaleDriver? = null
    internal var scaleJob: Job? = null
    internal var lowFlowStartMonoMs = 0L
    internal var prevStepIndex = 0
    private var persistedCoffeeSnapshot: CoffeeDurableSnapshot? = null

    @Volatile internal var replacingDraft = false

    init {
        observeRepository()
        observeRecommendationInputs()
        task("Opening local records") {
            graph.repository.initialize()
            val storedRecords = CoffeeDurableSnapshotCodec.read(graph.repository.settings.value) { key, failure ->
                diagnosticBuffer.log(
                    LogLevel.WARN,
                    "Personal records",
                    "Saved '$key' could not be read; the backup copy is retained.",
                    failure
                )
            }
            _state.update {
                it.copy(
                    journalEntries = storedRecords.journalEntries,
                    cafeCupEntries = storedRecords.cafeCupEntries,
                    cuppingSessions = storedRecords.cuppingSessions,
                    palateProfile = storedRecords.palateProfile,
                    weatherParams = storedRecords.weatherParameters,
                    unitPreferences = storedRecords.unitPreferences,
                    physicalQcRecords = storedRecords.physicalQcRecords,
                    roastRestObservations = storedRecords.roastRestObservations,
                    detailedBeans = storedRecords.detailedBeans,
                    grinders = storedRecords.grinders,
                    equipmentCatalog = storedRecords.equipmentCatalog,
                    waterProfiles = storedRecords.waterProfiles,
                    foodGradeIngredients = storedRecords.foodGradeIngredients,
                    greenCoffeeLots = storedRecords.greenCoffeeLots,
                    roastBatches = storedRecords.roastBatches
                )
            }
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
            observeDurableRecords()
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

    internal suspend fun <T> withDraftWritesPaused(block: suspend () -> T): T {
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
        viewModelScope.launch {
            repository.recipes.collect { recipes ->
                _state.update { current -> current.copy(recipes = recipes) }
                refreshStreakAndStats()
            }
        }
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

    private fun observeDurableRecords() {
        viewModelScope.launch {
            state.map(::durableSnapshot).collect { current ->
                if (!_state.value.ready || !graph.repository.ready.value) return@collect
                val previous = persistedCoffeeSnapshot
                if (previous == null) {
                    persistedCoffeeSnapshot = current
                    return@collect
                }
                if (previous == current) return@collect

                val changes = current.changedSettings(previous, CoffeeDurableSnapshotCodec.serializer)
                if (changes.isEmpty()) return@collect
                try {
                    graph.repository.saveSettings(changes)
                    persistedCoffeeSnapshot = current
                } catch (cancelled: CancellationException) {
                    throw cancelled
                } catch (boundaryFailure: Exception) {
                    _state.update {
                        it.copy(error = "Personal records were not saved: ${boundaryFailure.message ?: "local storage unavailable"}.")
                    }
                }
            }
        }
    }

    private fun observeRecommendationInputs() {
        viewModelScope.launch {
            state.map { current ->
                RecommendationInputs(
                    palate = current.palateProfile,
                    journalEntries = current.journalEntries,
                    inventory = current.detailedBeans,
                    equipment = current.equipmentCatalog
                )
            }.distinctUntilChanged().drop(1).collect {
                _state.update { current ->
                    if (current.inventoryRecommendations.isEmpty()) {
                        current
                    } else {
                        current.copy(
                            inventoryRecommendations = emptyList(),
                            notice = current.notice?.takeIf { !it.startsWith("Ranked ") }
                                ?: "Recommendation inputs changed. Request a fresh ranking."
                        )
                    }
                }
            }
        }
    }

    private fun durableSnapshot(state: CoffeeUiState) = CoffeeDurableSnapshot(
        journalEntries = state.journalEntries,
        cafeCupEntries = state.cafeCupEntries,
        cuppingSessions = state.cuppingSessions,
        palateProfile = state.palateProfile,
        weatherParameters = state.weatherParams,
        unitPreferences = state.unitPreferences,
        physicalQcRecords = state.physicalQcRecords,
        roastRestObservations = state.roastRestObservations,
        detailedBeans = state.detailedBeans,
        grinders = state.grinders,
        equipmentCatalog = state.equipmentCatalog,
        waterProfiles = state.waterProfiles,
        foodGradeIngredients = state.foodGradeIngredients,
        greenCoffeeLots = state.greenCoffeeLots,
        roastBatches = state.roastBatches
    )

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
                require(asset.isSupportedByNativeRuntime()) {
                    "This asset is download-only; the installed app has no compatible inference runtime."
                }
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
            val snapshot = durableSnapshot(_state.value)
            graph.repository.saveSettings(
                snapshot.allSettings(CoffeeDurableSnapshotCodec.serializer) + ("draft" to gson.toJson(_state.value.draft))
            )
            persistedCoffeeSnapshot = snapshot
            graph.repository.exportBackupJson()
        }
        resolver.openOutputStream(uri, "wt")?.use { it.write(json.toByteArray(Charsets.UTF_8)) } ?: error("Cannot write export.")
        diagnosticBuffer.log(LogLevel.INFO, "Backup", "Full backup exported to SAF URI: $uri")
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
        // Validate UI-owned draft fields and extended settings before replacing records.
        val decoded = withContext(Dispatchers.IO) { BackupCodec.decode(json) }
        val settings = decoded.payload.settings
        val restored = withContext(Dispatchers.IO) {
            val draftJson = settings["draft"]
            if (draftJson.isNullOrBlank()) {
                RecipeDraft()
            } else {
                JsonInputPolicy.validateDepth(draftJson)
                gson.fromJson(draftJson, RecipeDraft::class.java).also { it.validate() }
            }
        }
        val restoredRecords = withContext(Dispatchers.IO) { CoffeeDurableSnapshotCodec.decode(settings) }
        cancelGeneration()
        generation?.join()
        withDraftWritesPaused {
            // Finish synchronizing state after the bounded transaction commits,
            // even when the originating activity is finishing.
            withContext(NonCancellable) {
                graph.repository.restoreBackupJson(json)
                withContext(Dispatchers.Main.immediate) {
                    persistedCoffeeSnapshot = restoredRecords
                    publishDraft(restored)
                    calculate()
                    _state.update {
                        it.copy(
                            selectedRecipeId = null,
                            estimate = null,
                            searchResult = null,
                            inspectedChunk = null,
                            pendingRecipe = null,
                            pendingToolJson = null,
                            cuppingSessions = restoredRecords.cuppingSessions,
                            journalEntries = restoredRecords.journalEntries,
                            cafeCupEntries = restoredRecords.cafeCupEntries,
                            palateProfile = restoredRecords.palateProfile,
                            weatherParams = restoredRecords.weatherParameters,
                            unitPreferences = restoredRecords.unitPreferences,
                            physicalQcRecords = restoredRecords.physicalQcRecords,
                            roastRestObservations = restoredRecords.roastRestObservations,
                            detailedBeans = restoredRecords.detailedBeans,
                            grinders = restoredRecords.grinders,
                            equipmentCatalog = restoredRecords.equipmentCatalog,
                            waterProfiles = restoredRecords.waterProfiles,
                            foodGradeIngredients = restoredRecords.foodGradeIngredients,
                            greenCoffeeLots = restoredRecords.greenCoffeeLots,
                            roastBatches = restoredRecords.roastBatches,
                            inventoryRecommendations = emptyList()
                        )
                    }
                    diagnosticBuffer.log(LogLevel.INFO, "Backup", "Full backup restored atomically: ${decoded.manifest.recordCounts}")
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

    private fun progress(message: String) {
        _state.update { it.copy(progress = message) }
    }
    internal fun decimal(input: String) = DomainValidation.parseDecimal(input).toDouble()
    internal fun optionalDecimal(input: String) = input.takeIf(String::isNotBlank)?.let(::decimal)
    internal fun optionalInt(input: String, label: String): Int? = input.takeIf(String::isNotBlank)?.let {
        it.toIntOrNull() ?: error("$label must be a whole number.")
    }

    internal fun task(description: String, block: suspend () -> Unit) {
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

    override fun onCleared() {
        graph.native.cancel()
        timer?.cancel()
        scaleJob?.cancel()
        photoOcrJob?.cancel()
        super.onCleared()
    }
}
