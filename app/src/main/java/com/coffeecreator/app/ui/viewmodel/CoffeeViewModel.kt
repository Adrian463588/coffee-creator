@file:OptIn(kotlinx.coroutines.FlowPreview::class)

package com.coffeecreator.app.ui.viewmodel

import android.content.Context
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
import com.coffeecreator.app.core.ai.personalization.AcidityTolerance
import com.coffeecreator.app.core.ai.personalization.BaristaPersonaPreset
import com.coffeecreator.app.core.ai.personalization.BeanInventoryCandidate
import com.coffeecreator.app.core.ai.personalization.BitternessSensitivity
import com.coffeecreator.app.core.ai.personalization.BodyPreference
import com.coffeecreator.app.core.ai.personalization.ElevationUnit
import com.coffeecreator.app.core.ai.personalization.FeedbackLoopEngine
import com.coffeecreator.app.core.ai.personalization.IngredientSafetyValidator
import com.coffeecreator.app.core.ai.personalization.OcrBeanParser
import com.coffeecreator.app.core.ai.personalization.OvatAdjustmentProposal
import com.coffeecreator.app.core.ai.personalization.PalateBayesianEngine
import com.coffeecreator.app.core.ai.personalization.PalateProfile
import com.coffeecreator.app.core.ai.personalization.PalateQuizAnswers
import com.coffeecreator.app.core.ai.personalization.PalateQuizMapper
import com.coffeecreator.app.core.ai.personalization.PalateVector
import com.coffeecreator.app.core.ai.personalization.ParsedBeanDraft
import com.coffeecreator.app.core.ai.personalization.PersonaSanitizer
import com.coffeecreator.app.core.ai.personalization.PersonalSensoryEvidence
import com.coffeecreator.app.core.ai.personalization.RecommendationEngine
import com.coffeecreator.app.core.ai.personalization.RecommendationItem
import com.coffeecreator.app.core.ai.personalization.RoastPreference
import com.coffeecreator.app.core.ai.personalization.SafetyAuditResult
import com.coffeecreator.app.core.ai.personalization.SensoryFeedbackDiagnosis
import com.coffeecreator.app.core.ai.personalization.SweetnessPriority
import com.coffeecreator.app.core.ai.personalization.WeatherAltitudeCalculator
import com.coffeecreator.app.core.ai.personalization.WeatherParameters
import com.coffeecreator.app.core.ble.BleScaleDevice
import com.coffeecreator.app.core.ble.BleScaleDriver
import com.coffeecreator.app.core.ble.ScaleConnectionState
import com.coffeecreator.app.core.ble.ScaleProtocolType
import com.coffeecreator.app.core.ble.ScaleWeightSample
import com.coffeecreator.app.core.ble.SimulatedBleScaleDriver
import com.coffeecreator.app.core.brew.AudioCueType
import com.coffeecreator.app.core.brew.BrewExecutionStatus
import com.coffeecreator.app.core.brew.BrewFilterEngine
import com.coffeecreator.app.core.brew.BrewNotificationPayload
import com.coffeecreator.app.core.brew.BrewSessionSnapshot
import com.coffeecreator.app.core.brew.BrewSessionState
import com.coffeecreator.app.core.brew.BrewStepProgress
import com.coffeecreator.app.core.brew.HapticCueType
import com.coffeecreator.app.core.brew.ProximityState
import com.coffeecreator.app.core.data.BackupCodec
import com.coffeecreator.app.core.data.CuratedCoffeeCatalog
import com.coffeecreator.app.core.data.RecipeExportFormatter
import com.coffeecreator.app.core.engine.CoffeeScienceEngine
import com.coffeecreator.app.core.engine.DomainValidation
import com.coffeecreator.app.core.engine.JsonInputPolicy
import com.coffeecreator.app.core.engine.SensoryRulesEngine
import com.coffeecreator.app.core.filesystem.BuiltInAssets
import com.coffeecreator.app.core.filesystem.DocumentImporter.Companion.readBytesBounded
import com.coffeecreator.app.core.model.AssistantPersona
import com.coffeecreator.app.core.model.BrewStep
import com.coffeecreator.app.core.model.CalculationResult
import com.coffeecreator.app.core.model.ChatMessage
import com.coffeecreator.app.core.model.CoffeeBean
import com.coffeecreator.app.core.model.DocumentChunk
import com.coffeecreator.app.core.model.RefractometerProfile
import com.coffeecreator.app.core.model.Ingredient
import com.coffeecreator.app.core.model.InputLimits
import com.coffeecreator.app.core.model.KnowledgeDocument
import com.coffeecreator.app.core.model.ModelAsset
import com.coffeecreator.app.core.model.Recipe
import com.coffeecreator.app.core.model.RecipeLine
import com.coffeecreator.app.core.model.SensoryEstimate
import com.coffeecreator.app.core.model.SensoryObservation
import com.coffeecreator.app.core.model.StoredSensoryEstimate
import com.coffeecreator.app.core.journal.AdjustmentDirection
import com.coffeecreator.app.core.journal.BrewAnalyticsEngine
import com.coffeecreator.app.core.journal.BrewFilterCriteria
import com.coffeecreator.app.core.journal.BrewJournalEntry
import com.coffeecreator.app.core.journal.BrewSessionComparison
import com.coffeecreator.app.core.journal.BrewStreakResult
import com.coffeecreator.app.core.journal.CafeCupEntry
import com.coffeecreator.app.core.journal.CaffeineEstimate
import com.coffeecreator.app.core.journal.EspressoJournalDetails
import com.coffeecreator.app.core.journal.ImportConflictItem
import com.coffeecreator.app.core.journal.ImportConflictPreview
import com.coffeecreator.app.core.journal.ImportRecordStatus
import com.coffeecreator.app.core.journal.MonthlyBrewStatistics
import com.coffeecreator.app.core.journal.OvatRecommendation
import com.coffeecreator.app.core.journal.OvatTargetVariable
import com.coffeecreator.app.core.sensory.AttributeConsensus
import com.coffeecreator.app.core.sensory.AuditEntry
import com.coffeecreator.app.core.sensory.CalibrationReferenceExercise
import com.coffeecreator.app.core.sensory.ConsensusAnalytics
import com.coffeecreator.app.core.sensory.CuppingAnalyticsEngine
import com.coffeecreator.app.core.sensory.CuppingSample
import com.coffeecreator.app.core.sensory.CuppingSession
import com.coffeecreator.app.core.sensory.CuppingTemplate
import com.coffeecreator.app.core.sensory.CuppingTemplateType
import com.coffeecreator.app.core.sensory.DefectRecord
import com.coffeecreator.app.core.sensory.DegassingPhase
import com.coffeecreator.app.core.sensory.EpistemologicalOrigin
import com.coffeecreator.app.core.sensory.EvaluationSheet
import com.coffeecreator.app.core.sensory.Evaluator
import com.coffeecreator.app.core.sensory.EvaluatorRole
import com.coffeecreator.app.core.sensory.FlavorDescriptor
import com.coffeecreator.app.core.sensory.GreenCoffeeDefectEntry
import com.coffeecreator.app.core.sensory.GreenDefectType
import com.coffeecreator.app.core.sensory.MaskingMode
import com.coffeecreator.app.core.sensory.PhysicalQcRecord
import com.coffeecreator.app.core.sensory.QcReportReceipt
import com.coffeecreator.app.core.sensory.RestCurvePrediction
import com.coffeecreator.app.core.sensory.RoastRestObservation
import com.coffeecreator.app.core.sensory.SensoryTaxonomyRegistry
import com.coffeecreator.app.core.sensory.SessionStatus
import com.coffeecreator.app.core.sensory.StandardCuppingTemplates
import com.coffeecreator.app.core.reliability.AppLanguage
import com.coffeecreator.app.core.reliability.DiagnosticLogBuffer
import com.coffeecreator.app.core.reliability.DiagnosticLogEntry
import com.coffeecreator.app.core.reliability.LogLevel
import com.coffeecreator.app.core.reliability.MassUnit
import com.coffeecreator.app.core.reliability.ModuleStatus
import com.coffeecreator.app.core.reliability.OfflineHelpCatalog
import com.coffeecreator.app.core.reliability.OfflineHelpTopic
import com.coffeecreator.app.core.reliability.SystemDegradationState
import com.coffeecreator.app.core.reliability.TemperatureUnit
import com.coffeecreator.app.core.reliability.UnitConverter
import com.coffeecreator.app.core.reliability.UserUnitPreferences
import com.coffeecreator.app.core.reliability.VolumeUnit
import com.coffeecreator.app.core.catalog.BagInventoryItem
import com.coffeecreator.app.core.catalog.BagOcrParsedDraft
import com.coffeecreator.app.core.catalog.BagStatus
import com.coffeecreator.app.core.catalog.BlendComponentSpec
import com.coffeecreator.app.core.catalog.BurrMaterial
import com.coffeecreator.app.core.catalog.BurrProfile
import com.coffeecreator.app.core.catalog.BurrType
import com.coffeecreator.app.core.catalog.CalibrationUnit
import com.coffeecreator.app.core.catalog.CatalogExportPayload
import com.coffeecreator.app.core.catalog.CatalogImportExportEngine
import com.coffeecreator.app.core.catalog.ConflictResolutionStrategy
import com.coffeecreator.app.core.catalog.DetailedBeanRecord
import com.coffeecreator.app.core.catalog.EquipmentCategory
import com.coffeecreator.app.core.catalog.EquipmentItem
import com.coffeecreator.app.core.catalog.FoodGradeIngredientItem
import com.coffeecreator.app.core.catalog.FreezeEvent
import com.coffeecreator.app.core.catalog.FreshnessAnalysis
import com.coffeecreator.app.core.catalog.FreshnessState
import com.coffeecreator.app.core.catalog.GreenCoffeeLot
import com.coffeecreator.app.core.catalog.GrinderRecord
import com.coffeecreator.app.core.catalog.ImportDryRunSummary
import com.coffeecreator.app.core.catalog.ImportExecutionReceipt
import com.coffeecreator.app.core.catalog.InventoryDeductionEngine
import com.coffeecreator.app.core.catalog.InventoryDeductionResult
import com.coffeecreator.app.core.catalog.OfflineBagOcrParser
import com.coffeecreator.app.core.catalog.ProcessMethod
import com.coffeecreator.app.core.catalog.RoastBatchRecord
import com.coffeecreator.app.core.catalog.RoastDataPoint
import com.coffeecreator.app.core.catalog.RoastLevel
import com.coffeecreator.app.core.catalog.RoastRestCalculator
import com.coffeecreator.app.core.catalog.SampleLotStatus
import com.coffeecreator.app.core.catalog.WaterChemistryCalculator
import com.coffeecreator.app.core.catalog.WaterMineralProfile
import com.coffeecreator.app.core.catalog.WaterRecipePreset
import java.math.BigDecimal
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
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
    val parsedBeanDraft: ParsedBeanDraft? = null,
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
    val catalogImportPreview: ImportDryRunSummary? = null
)

@HiltViewModel
class CoffeeViewModel @Inject constructor(val graph: AppGraph, private val saved: SavedStateHandle) : ViewModel() {
    private val gson = Gson()
    val diagnosticBuffer = DiagnosticLogBuffer(500)
    private val _state = MutableStateFlow(CoffeeUiState())
    val state: StateFlow<CoffeeUiState> = _state.asStateFlow()
    private val draftWrites = MutableStateFlow<RecipeDraft?>(null)
    private var generation: Job? = null
    private var timer: Job? = null
    private var timerOrigin = 0L
    private var undoDraft: RecipeDraft? = null
    private var draftAutosave: Job? = null
    private var scaleDriver: BleScaleDriver? = null
    private var scaleJob: Job? = null
    private var lowFlowStartMonoMs = 0L
    private var prevStepIndex = 0

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
            seedInitialSensoryData()
            seedInitialCatalogData()
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
        viewModelScope.launch {
            repository.recipes.collect { recipes ->
                _state.update { current ->
                    val j = if (current.journalEntries.isEmpty()) CuratedCoffeeCatalog.curatedJournalEntries else current.journalEntries
                    val c = if (current.cafeCupEntries.isEmpty()) CuratedCoffeeCatalog.curatedCafeCups else current.cafeCupEntries
                    current.copy(recipes = recipes, journalEntries = j, cafeCupEntries = c)
                }
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

    fun scaleDraft(factor: Double) {
        val currentDose = _state.value.draft.fields["dose"]?.toDoubleOrNull() ?: 18.0
        val currentWater = _state.value.draft.fields["water"]?.toDoubleOrNull() ?: 300.0
        val targetDose = "%.1f".format(java.util.Locale.US, currentDose * factor)
        val targetWater = "%.1f".format(java.util.Locale.US, currentWater * factor)
        val scaledSteps = if (_state.value.draft.steps.isNotEmpty()) {
            CoffeeScienceEngine.scaleRecipeSteps(_state.value.draft.steps, currentWater, targetWater.toDouble()).getOrDefault(_state.value.draft.steps)
        } else {
            emptyList()
        }
        val updatedFields = _state.value.draft.fields + mapOf(
            "dose" to targetDose,
            "water" to targetWater
        )
        updateDraft(_state.value.draft.copy(fields = updatedFields, steps = scaledSteps), recordUndo = true)
        calculate()
    }

    fun addDraftStep() {
        val currentSteps = _state.value.draft.steps
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
        updateDraft(_state.value.draft.copy(steps = currentSteps + newStep), recordUndo = true)
    }

    fun updateDraftStep(index: Int, updated: BrewStep) {
        val steps = _state.value.draft.steps.toMutableList()
        if (index !in steps.indices) return
        steps[index] = updated
        var cum = 0.0
        val recomputed = steps.mapIndexed { idx, s ->
            cum += s.targetWaterGrams
            s.copy(stepNumber = idx + 1, cumulativeMassGrams = "%.1f".format(java.util.Locale.US, cum).toDouble())
        }
        updateDraft(_state.value.draft.copy(steps = recomputed), recordUndo = false)
    }

    fun removeDraftStep(index: Int) {
        val steps = _state.value.draft.steps.toMutableList()
        if (index !in steps.indices) return
        steps.removeAt(index)
        var cum = 0.0
        val recomputed = steps.mapIndexed { idx, s ->
            cum += s.targetWaterGrams
            s.copy(stepNumber = idx + 1, cumulativeMassGrams = "%.1f".format(java.util.Locale.US, cum).toDouble())
        }
        updateDraft(_state.value.draft.copy(steps = recomputed), recordUndo = true)
    }

    fun convertDraftBrix() = task("Converting Brix to TDS") {
        val brixStr = _state.value.draft.fields["brix"].orEmpty()
        require(brixStr.isNotBlank()) { "Enter a Brix reading." }
        val brix = DomainValidation.parseDecimal(brixStr).toDouble()
        val profileName = _state.value.draft.fields["brixProfile"].orEmpty()
        val profile = RefractometerProfile.find(profileName)
        val conversion = CoffeeScienceEngine.calculateBrixToTds(brix, profile).getOrThrow()
        val tdsFormatted = "%.2f".format(java.util.Locale.US, conversion.tdsPercent)
        val updatedFields = _state.value.draft.fields + ("tds" to tdsFormatted)
        updateDraft(_state.value.draft.copy(fields = updatedFields))
        calculate()
        _state.update {
            it.copy(
                notice = "Converted ${conversion.brixPercent}% Brix → $tdsFormatted% TDS (${conversion.instrumentProfile})."
            )
        }
    }

    fun toggleArchiveRecipe(recipe: Recipe) = task("Toggling archive status") {
        val updated = graph.repository.archiveRecipe(recipe.id, !recipe.isArchived)
        _state.update {
            it.copy(notice = "Recipe '${updated.name}' ${if (updated.isArchived) "archived" else "restored to active"}.")
        }
    }

    fun shareRecipe(context: Context, recipe: Recipe, formatJson: Boolean = false) {
        val bean = _state.value.beans.find { it.id == recipe.beanId }
        RecipeExportFormatter.shareRecipe(context, recipe, bean, formatJson)
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
        diagnosticBuffer.log(LogLevel.INFO, "Recipe", "Saved recipe ${recipe.name}, revision ${recipe.version}")
        _state.update { it.copy(notice = "Saved ${recipe.name}, revision ${recipe.version}. Earlier revisions are retained.") }
    }
    fun copyRecipe(recipe: Recipe) = task("Copying recipe") {
        val copy = graph.repository.duplicateRecipe(recipe)
        _state.update { it.copy(notice = "Copied ${copy.name}; new independent history.") }
    }
    fun importCuratedCatalog() = task("Loading curated specialty coffee catalog") {
        graph.repository.importCuratedCatalog()
        _state.update {
            it.copy(
                journalEntries = CuratedCoffeeCatalog.curatedJournalEntries,
                cafeCupEntries = CuratedCoffeeCatalog.curatedCafeCups,
                notice = "Imported 10 curated recipes, 8 single-origin beans, and sensory observation logs."
            )
        }
        refreshStreakAndStats()
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
            graph.repository.saveSetting("cuppings", gson.toJson(_state.value.cuppingSessions))
            graph.repository.saveSetting("journal_entries", gson.toJson(_state.value.journalEntries))
            graph.repository.saveSetting("palate_profile", gson.toJson(_state.value.palateProfile))
            graph.repository.saveSetting("weather_parameters", gson.toJson(_state.value.weatherParams))
            graph.repository.saveSetting("user_units", gson.toJson(_state.value.unitPreferences))
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
        val restoredCuppings = settings["cuppings"]?.let {
            runCatching { gson.fromJson(it, Array<CuppingSession>::class.java).toList() }.getOrNull()
        }
        val restoredJournals = settings["journal_entries"]?.let {
            runCatching { gson.fromJson(it, Array<BrewJournalEntry>::class.java).toList() }.getOrNull()
        }
        val restoredPalate = settings["palate_profile"]?.let {
            runCatching { gson.fromJson(it, PalateProfile::class.java) }.getOrNull()
        }
        val restoredWeather = settings["weather_parameters"]?.let {
            runCatching { gson.fromJson(it, WeatherParameters::class.java) }.getOrNull()
        }
        val restoredUnits = settings["user_units"]?.let {
            runCatching { gson.fromJson(it, UserUnitPreferences::class.java) }.getOrNull()
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
                            pendingToolJson = null,
                            cuppingSessions = restoredCuppings ?: it.cuppingSessions,
                            journalEntries = restoredJournals ?: it.journalEntries,
                            palateProfile = restoredPalate ?: it.palateProfile,
                            weatherParams = restoredWeather ?: it.weatherParams,
                            unitPreferences = restoredUnits ?: it.unitPreferences
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

    fun initBrewSession(targetRecipe: Recipe? = null) {
        val recipe = targetRecipe
            ?: runCatching { buildRecipe() }.getOrNull()
            ?: _state.value.recipes.firstOrNull()
            ?: CuratedCoffeeCatalog.recipes.first()

        val steps = recipe.brewSteps.ifEmpty {
            listOf(
                BrewStep(
                    stepNumber = 1,
                    description = "Continuous extraction",
                    targetWaterGrams = recipe.waterGrams,
                    durationSeconds = recipe.brewTimeSeconds,
                    action = "Pour",
                    startOffsetSeconds = 0,
                    cumulativeMassGrams = recipe.waterGrams
                )
            )
        }
        val activeRecipe = recipe.copy(brewSteps = steps)
        val firstStep = steps.first()
        val nextStep = if (steps.size > 1) steps[1] else null
        val progress = BrewStepProgress(
            stepIndex = 0,
            totalSteps = steps.size,
            currentStep = firstStep,
            nextStep = nextStep,
            stepElapsedSeconds = 0,
            stepRemainingSeconds = firstStep.durationSeconds,
            stepTargetWaterGrams = firstStep.targetWaterGrams,
            cumulativeTargetWaterGrams = firstStep.cumulativeMassGrams,
            pouredInStepGrams = 0.0,
            remainingToCumulativeTargetGrams = firstStep.cumulativeMassGrams,
            proximityState = ProximityState.NORMAL
        )
        val session = BrewSessionState(
            sessionId = UUID.randomUUID().toString(),
            recipe = activeRecipe,
            status = BrewExecutionStatus.READY_ARMED,
            totalElapsedSeconds = 0,
            currentWeightGrams = 0.0,
            currentFlowRateGps = 0.0,
            stepProgress = progress,
            liveSamples = emptyList(),
            autoStartArmed = true
        )
        prevStepIndex = 0
        lowFlowStartMonoMs = 0L
        _state.update {
            it.copy(
                brewSession = session,
                timerSeconds = 0,
                timerRunning = false,
                brewCurveRmse = null
            )
        }
    }

    fun startBrewSession() {
        if (_state.value.brewSession == null) {
            initBrewSession()
        }
        val session = _state.value.brewSession ?: return
        timerOrigin = SystemClock.elapsedRealtime() - _state.value.timerSeconds * MILLIS_PER_SECOND
        _state.update {
            it.copy(
                timerRunning = true,
                brewSession = session.copy(status = BrewExecutionStatus.RUNNING)
            )
        }
        timer?.cancel()
        timer = viewModelScope.launch {
            while (true) {
                delay(TIMER_REFRESH_MS)
                val now = SystemClock.elapsedRealtime()
                val elapsed = ((now - timerOrigin) / MILLIS_PER_SECOND).toInt()
                updateSessionTick(elapsed)
            }
        }
    }

    fun pauseBrewSession() {
        timer?.cancel()
        _state.update {
            it.copy(
                timerRunning = false,
                brewSession = it.brewSession?.copy(status = BrewExecutionStatus.PAUSED)
            )
        }
    }

    fun resumeBrewSession() {
        startBrewSession()
    }

    fun skipBrewStep() {
        val session = _state.value.brewSession ?: return
        val steps = session.recipe.brewSteps
        val currentIdx = session.stepProgress?.stepIndex ?: 0
        if (currentIdx + 1 < steps.size) {
            val nextIdx = currentIdx + 1
            var accumulatedTime = 0
            for (i in 0 until nextIdx) {
                accumulatedTime += steps[i].durationSeconds
            }
            timerOrigin = SystemClock.elapsedRealtime() - accumulatedTime * MILLIS_PER_SECOND
            updateSessionTick(accumulatedTime)
        } else {
            finishBrewSession()
        }
    }

    fun restartBrewStep() {
        val session = _state.value.brewSession ?: return
        val steps = session.recipe.brewSteps
        val currentIdx = session.stepProgress?.stepIndex ?: 0
        var stepStartTime = 0
        for (i in 0 until currentIdx) {
            stepStartTime += steps[i].durationSeconds
        }
        timerOrigin = SystemClock.elapsedRealtime() - stepStartTime * MILLIS_PER_SECOND
        updateSessionTick(stepStartTime)
    }

    fun resetBrewSession() {
        timer?.cancel()
        _state.update {
            it.copy(
                timerSeconds = 0,
                timerRunning = false,
                brewSession = it.brewSession?.copy(
                    status = BrewExecutionStatus.READY_ARMED,
                    totalElapsedSeconds = 0,
                    currentWeightGrams = 0.0,
                    currentFlowRateGps = 0.0,
                    liveSamples = emptyList()
                )
            )
        }
    }

    fun finishBrewSession() {
        timer?.cancel()
        val session = _state.value.brewSession ?: return
        val elapsed = _state.value.timerSeconds
        val actualWater = if (session.currentWeightGrams > 0.0) session.currentWeightGrams else session.recipe.waterGrams
        val observation = SensoryObservation(
            id = UUID.randomUUID().toString(),
            recipeId = session.recipe.id,
            aroma = 7, acidity = 7, sweetness = 7, bitterness = 3, body = 7, clarity = 8, finish = 7, balance = 7,
            notes = "Auto-logged from active brew session: ${session.recipe.name} (${elapsed}s elapsed, ${actualWater}g water poured).",
            assessor = "Barista",
            timestamp = System.currentTimeMillis()
        )
        viewModelScope.launch {
            runCatching {
                if (graph.repository.findRecipeById(session.recipe.id) == null) {
                    graph.repository.saveRecipe(session.recipe)
                }
                graph.repository.saveSensoryObservation(observation)
            }
        }

        val idealPoints = BrewFilterEngine.generateIdealCurvePoints(session.recipe.brewSteps)
        val actualPoints = session.liveSamples.map {
            Pair(((it.timestampMonoMs - session.liveSamples.first().timestampMonoMs) / 1000).toInt(), it.filteredWeightGrams)
        }
        val rmse = if (actualPoints.isNotEmpty()) BrewFilterEngine.calculateCurveRmse(actualPoints, idealPoints) else null

        val isEsp = session.recipe.brewMethod.contains("Espresso", true)
        val journalEntry = BrewJournalEntry(
            id = UUID.randomUUID().toString(),
            recipeId = session.recipe.id,
            beanId = session.recipe.beanId,
            beanName = _state.value.beans.find { it.id == session.recipe.beanId }?.name ?: "House Blend",
            brewMethod = session.recipe.brewMethod,
            equipment = session.recipe.equipment,
            doseGrams = session.recipe.doseGrams,
            waterGrams = actualWater,
            beverageMassGrams = if (isEsp) actualWater else (session.recipe.beverageMassGrams ?: (actualWater * 0.9)),
            grinderModel = session.recipe.grinderModel ?: "Baratza Sette 270",
            grindSetting = session.recipe.grinderSetting ?: session.recipe.grindSize,
            waterTemperatureCelsius = session.recipe.temperatureCelsius,
            brewDurationSeconds = elapsed,
            drawdownSeconds = if (!isEsp) 40 else null,
            measuredTdsPercent = session.recipe.measuredTdsPercent ?: if (isEsp) 9.5 else 1.35,
            rating = 8.5,
            tasteNotes = listOf("Clean", "Sweet", "Balanced"),
            generalNotes = "Auto-logged from active brew session: ${session.recipe.name} (${elapsed}s elapsed, ${actualWater}g water poured).",
            tags = listOf(session.recipe.brewMethod.substringBefore(" "), "ActiveTimer", if (isEsp) "DialIn" else "Filter"),
            createdAt = System.currentTimeMillis(),
            isEspresso = isEsp,
            espressoDetails = if (isEsp) {
                EspressoJournalDetails(
                    targetDoseGrams = session.recipe.doseGrams,
                    actualDoseGrams = session.recipe.doseGrams,
                    targetYieldGrams = session.recipe.beverageMassGrams ?: 36.0,
                    actualYieldGrams = actualWater,
                    targetDurationSeconds = session.recipe.brewTimeSeconds,
                    actualDurationSeconds = elapsed,
                    puckPrepMethods = listOf("WDT", "Tamp"),
                    firstDropSeconds = session.recipe.firstDropSeconds ?: 5
                )
            } else null,
            dialInChainId = if (isEsp) "dialin-${session.recipe.beanId ?: "default"}" else null
        )

        _state.update {
            val updatedJournal = listOf(journalEntry) + it.journalEntries.filter { j -> j.id != journalEntry.id }
            it.copy(
                timerRunning = false,
                brewCurveRmse = rmse,
                brewSession = session.copy(status = BrewExecutionStatus.COMPLETED),
                journalEntries = updatedJournal,
                notice = "Brew session completed and auto-logged to Journal!"
            )
        }
        refreshStreakAndStats()
    }

    private fun updateSessionTick(elapsedSeconds: Int) {
        val currentSession = _state.value.brewSession ?: return
        val steps = currentSession.recipe.brewSteps
        val totalDuration = steps.sumOf { it.durationSeconds }

        if (elapsedSeconds >= totalDuration && currentSession.status == BrewExecutionStatus.RUNNING) {
            finishBrewSession()
            return
        }

        var stepIdx = 0
        var accTime = 0
        for (i in steps.indices) {
            val step = steps[i]
            if (elapsedSeconds < accTime + step.durationSeconds || i == steps.lastIndex) {
                stepIdx = i
                break
            }
            accTime += step.durationSeconds
        }

        val step = steps[stepIdx]
        val stepElapsed = (elapsedSeconds - accTime).coerceAtLeast(0)
        val stepRemaining = (step.durationSeconds - stepElapsed).coerceAtLeast(0)
        val nextStep = if (stepIdx + 1 < steps.size) steps[stepIdx + 1] else null

        var haptic: HapticCueType? = null
        var audio: AudioCueType? = null

        if (stepIdx != prevStepIndex) {
            prevStepIndex = stepIdx
            haptic = HapticCueType.STEP_TRANSITION
            audio = AudioCueType.CHIME
        } else if (stepRemaining in 1..3) {
            haptic = HapticCueType.COUNTDOWN_WARNING
            audio = AudioCueType.TICK
        }

        val proximity = BrewFilterEngine.calculateProximity(currentSession.currentWeightGrams, step.cumulativeMassGrams)
        if (proximity == ProximityState.TARGET_MATCHED && currentSession.stepProgress?.proximityState != ProximityState.TARGET_MATCHED) {
            haptic = HapticCueType.TARGET_MATCHED
        }

        val progress = BrewStepProgress(
            stepIndex = stepIdx,
            totalSteps = steps.size,
            currentStep = step,
            nextStep = nextStep,
            stepElapsedSeconds = stepElapsed,
            stepRemainingSeconds = stepRemaining,
            stepTargetWaterGrams = step.targetWaterGrams,
            cumulativeTargetWaterGrams = step.cumulativeMassGrams,
            pouredInStepGrams = currentSession.currentWeightGrams,
            remainingToCumulativeTargetGrams = (step.cumulativeMassGrams - currentSession.currentWeightGrams).coerceAtLeast(0.0),
            proximityState = proximity
        )

        _state.update {
            it.copy(
                timerSeconds = elapsedSeconds,
                activeHapticCue = haptic ?: it.activeHapticCue,
                activeAudioCue = audio ?: it.activeAudioCue,
                brewSession = currentSession.copy(
                    totalElapsedSeconds = elapsedSeconds,
                    stepProgress = progress
                )
            )
        }
    }

    fun connectMockScale() {
        val simDriver = SimulatedBleScaleDriver()
        scaleDriver = simDriver
        _state.update { it.copy(isScaleScanning = true) }
        viewModelScope.launch {
            simDriver.connect()
            _state.update {
                it.copy(
                    isScaleScanning = false,
                    brewSession = (it.brewSession ?: createDefaultSession()).copy(
                        scaleDevice = simDriver.device,
                        scaleConnectionState = ScaleConnectionState.READY
                    ),
                    notice = "Connected to ${simDriver.device.name}"
                )
            }
            scaleJob?.cancel()
            scaleJob = launch {
                simDriver.weightStream.collect { sample ->
                    onScaleSampleReceived(sample)
                }
            }
            updateDegradationState(ble = ModuleStatus.AVAILABLE)
            diagnosticBuffer.log(LogLevel.INFO, "BLE", "Connected to ${simDriver.device.name}")
        }
    }

    fun disconnectScale() {
        viewModelScope.launch {
            scaleJob?.cancel()
            scaleDriver?.disconnect()
            scaleDriver = null
            updateDegradationState(ble = ModuleStatus.UNAVAILABLE_MISSING)
            diagnosticBuffer.log(LogLevel.INFO, "BLE", "Scale disconnected")
            _state.update {
                it.copy(
                    brewSession = it.brewSession?.copy(
                        scaleDevice = null,
                        scaleConnectionState = ScaleConnectionState.DISCONNECTED
                    )
                )
            }
        }
    }

    fun tareScale() {
        viewModelScope.launch {
            scaleDriver?.tare()
            _state.update {
                it.copy(
                    brewSession = it.brewSession?.copy(
                        currentWeightGrams = 0.0,
                        currentFlowRateGps = 0.0
                    )
                )
            }
        }
    }

    fun feedSimulatedWeight(grams: Double) {
        (scaleDriver as? SimulatedBleScaleDriver)?.feedWeight(grams)
    }

    private fun onScaleSampleReceived(sample: ScaleWeightSample) {
        val currentSession = _state.value.brewSession ?: return

        // Auto-start check
        if (currentSession.status == BrewExecutionStatus.READY_ARMED && currentSession.autoStartArmed) {
            if (sample.rawWeightGrams >= 0.5) {
                startBrewSession()
            }
        }

        val updatedSamples = (currentSession.liveSamples + sample).takeLast(2000)
        val currentStep = currentSession.stepProgress?.currentStep
        val targetWater = currentStep?.cumulativeMassGrams ?: currentSession.recipe.waterGrams
        val proximity = BrewFilterEngine.calculateProximity(sample.filteredWeightGrams, targetWater)

        // Espresso auto-stop check (< 0.20 g/s sustained for >= 1.5s after 5g yield)
        var autoStopped = false
        if (currentSession.status == BrewExecutionStatus.RUNNING &&
            currentSession.recipe.brewMethod.contains("Espresso", ignoreCase = true) &&
            sample.filteredWeightGrams >= 5.0 && sample.flowRateGramsPerSec < 0.20
        ) {
            if (lowFlowStartMonoMs == 0L) {
                lowFlowStartMonoMs = sample.timestampMonoMs
            } else if (sample.timestampMonoMs - lowFlowStartMonoMs >= 1500L) {
                autoStopped = true
                finishBrewSession()
            }
        } else {
            lowFlowStartMonoMs = 0L
        }

        if (!autoStopped) {
            _state.update {
                it.copy(
                    brewSession = currentSession.copy(
                        currentWeightGrams = sample.filteredWeightGrams,
                        currentFlowRateGps = sample.flowRateGramsPerSec,
                        liveSamples = updatedSamples,
                        stepProgress = currentSession.stepProgress?.copy(
                            pouredInStepGrams = sample.filteredWeightGrams,
                            remainingToCumulativeTargetGrams = (targetWater - sample.filteredWeightGrams).coerceAtLeast(0.0),
                            proximityState = proximity
                        )
                    )
                )
            }
        }
    }

    fun toggleKeepScreenOn() {
        _state.update {
            it.copy(brewSession = it.brewSession?.copy(keepScreenOn = !(it.brewSession?.keepScreenOn ?: true)))
        }
    }

    fun toggleSound() {
        _state.update {
            it.copy(brewSession = it.brewSession?.copy(audioEnabled = !(it.brewSession?.audioEnabled ?: true)))
        }
    }

    fun toggleHaptics() {
        _state.update {
            it.copy(brewSession = it.brewSession?.copy(hapticsEnabled = !(it.brewSession?.hapticsEnabled ?: true)))
        }
    }

    fun clearActiveCues() {
        _state.update { it.copy(activeHapticCue = null, activeAudioCue = null) }
    }

    fun exportBrewSessionSnapshot(): BrewSessionSnapshot? {
        val session = _state.value.brewSession ?: return null
        return BrewSessionSnapshot(
            sessionId = session.sessionId,
            recipeId = session.recipe.id,
            status = session.status,
            currentStepIndex = session.stepProgress?.stepIndex ?: 0,
            accumulatedElapsedSeconds = _state.value.timerSeconds,
            lastWeightGrams = session.currentWeightGrams,
            sampleCount = session.liveSamples.size
        )
    }

    fun restoreBrewSession(snapshot: BrewSessionSnapshot) {
        val recipe = _state.value.recipes.find { it.id == snapshot.recipeId }
            ?: runCatching { buildRecipe() }.getOrNull()
            ?: CuratedCoffeeCatalog.recipes.first()
        initBrewSession(recipe)
        _state.update {
            it.copy(
                timerSeconds = snapshot.accumulatedElapsedSeconds,
                brewSession = it.brewSession?.copy(
                    sessionId = snapshot.sessionId,
                    status = snapshot.status,
                    currentWeightGrams = snapshot.lastWeightGrams
                )
            )
        }
    }

    private fun createDefaultSession(): BrewSessionState {
        val recipe = CuratedCoffeeCatalog.recipes.first()
        return BrewSessionState(
            sessionId = UUID.randomUUID().toString(),
            recipe = recipe
        )
    }

    fun toggleTimer() {
        if (_state.value.brewSession == null) {
            initBrewSession()
        }
        if (_state.value.timerRunning) {
            pauseBrewSession()
        } else {
            startBrewSession()
        }
    }

    fun resetTimer() {
        resetBrewSession()
    }

    fun saveJournalEntry(entry: BrewJournalEntry) {
        _state.update { current ->
            val list = listOf(entry) + current.journalEntries.filter { e -> e.id != entry.id }
            val updatedPalate = if (entry.rating != null && entry.rating >= 7.5) {
                val norm = { v: Double -> (v / 10.0).coerceIn(0.0, 1.0) }
                val evidence = PersonalSensoryEvidence(
                    roast = 0.3,
                    acidity = norm(entry.sensoryScores["acidity"]?.toDouble() ?: 7.5),
                    sweetness = norm(entry.sensoryScores["sweetness"]?.toDouble() ?: 7.5),
                    bitterness = norm(entry.sensoryScores["bitterness"]?.toDouble() ?: 2.5),
                    body = norm(entry.sensoryScores["body"]?.toDouble() ?: 6.0),
                    rating = entry.rating,
                    daysAgo = 0.0
                )
                PalateBayesianEngine.updateProfile(current.palateProfile, evidence)
            } else current.palateProfile
            diagnosticBuffer.log(LogLevel.INFO, "Journal", "Saved journal entry for ${entry.beanName} (rating: ${entry.rating})")
            current.copy(journalEntries = list, palateProfile = updatedPalate, notice = "Saved journal entry for ${entry.beanName}.")
        }
        refreshStreakAndStats()
    }

    fun deleteJournalEntry(id: String) {
        _state.update {
            val list = it.journalEntries.filter { e -> e.id != id }
            it.copy(journalEntries = list, notice = "Deleted journal entry.")
        }
        refreshStreakAndStats()
    }

    fun updateJournalFilter(criteria: BrewFilterCriteria) {
        _state.update { it.copy(journalFilterCriteria = criteria) }
    }

    fun selectComparisonSlot(entry: BrewJournalEntry?, slot: String) {
        _state.update { current ->
            val newA = if (slot == "A") entry else current.selectedComparisonA
            val newB = if (slot == "B") entry else current.selectedComparisonB
            val comparison = if (newA != null && newB != null) {
                BrewAnalyticsEngine.compareSessions(newA, newB)
            } else null
            current.copy(selectedComparisonA = newA, selectedComparisonB = newB, activeComparison = comparison)
        }
    }

    fun clearComparison() {
        _state.update { it.copy(selectedComparisonA = null, selectedComparisonB = null, activeComparison = null) }
    }

    fun evaluateOvatForEntry(entry: BrewJournalEntry, tasteFlags: List<String> = emptyList()) {
        val rec = BrewAnalyticsEngine.evaluateOvat(entry, tasteFlags)
        _state.update { it.copy(activeOvatRecommendation = rec) }
    }

    fun estimateCaffeineForDose(doseGrams: Double, arabicaFraction: Double = 1.0) {
        val estimate = BrewAnalyticsEngine.estimateCaffeine(doseGrams, arabicaFraction)
        _state.update { it.copy(activeCaffeineEstimate = estimate) }
    }

    fun repeatLastBrew() {
        val last = _state.value.journalEntries.maxByOrNull { it.createdAt } ?: return
        val matchingRecipe = _state.value.recipes.find { it.id == last.recipeId }
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
        _state.update { it.copy(notice = "Repeated previous brew: ${last.brewMethod} (${last.doseGrams}g). Armed on timer.") }
    }

    fun saveEntryAsTemplate(entry: BrewJournalEntry, templateName: String) = task("Saving brew template") {
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
        _state.update { it.copy(notice = "Saved template '${recipe.name}' to Recipe Library.") }
    }

    fun saveCafeCup(entry: CafeCupEntry) {
        _state.update {
            val list = listOf(entry) + it.cafeCupEntries.filter { c -> c.id != entry.id }
            it.copy(cafeCupEntries = list, notice = "Saved cafe cup: ${entry.drinkName} at ${entry.cafeName}.")
        }
    }

    fun deleteCafeCup(id: String) {
        _state.update {
            val list = it.cafeCupEntries.filter { c -> c.id != id }
            it.copy(cafeCupEntries = list, notice = "Deleted cafe cup.")
        }
    }

    fun exportJournalCsv(context: Context) {
        val csv = BrewAnalyticsEngine.exportJournalToCsv(_state.value.journalEntries)
        RecipeExportFormatter.shareJournalCsv(context, csv)
    }

    fun importBeanconquerorJson(json: String) = task("Importing Beanconqueror backup") {
        val (beans, brews) = BrewAnalyticsEngine.parseBeanconquerorJson(json)
        require(beans.isNotEmpty() || brews.isNotEmpty()) { "No valid Beanconqueror beans or brews found in JSON." }
        beans.forEach { bean ->
            if (graph.repository.findBeanById(bean.id) == null) {
                graph.repository.addBean(bean)
            }
        }
        _state.update { current ->
            val updatedBrews = brews + current.journalEntries.filter { j -> brews.none { it.id == j.id } }
            current.copy(
                journalEntries = updatedBrews,
                notice = "Imported ${beans.size} beans and ${brews.size} brews from Beanconqueror."
            )
        }
        refreshStreakAndStats()
    }

    fun previewBackup(backupJson: String) {
        val preview = runCatching {
            val validated = BackupCodec.decode(backupJson)
            val currentBeans = _state.value.beans.associateBy { it.id }
            val items = validated.payload.beans.map { incoming ->
                val local = currentBeans[incoming.id]
                val status = when {
                    local == null -> ImportRecordStatus.NEW
                    local == incoming -> ImportRecordStatus.IDENTICAL
                    else -> ImportRecordStatus.MODIFIED_REMOTE_NEWER
                }
                ImportConflictItem(incoming.id, local, incoming, status, local?.let { it.hashCode().toString() }, incoming.hashCode().toString())
            }
            ImportConflictPreview(
                newRecordsCount = items.count { it.status == ImportRecordStatus.NEW },
                identicalCount = items.count { it.status == ImportRecordStatus.IDENTICAL },
                conflictCount = items.count { it.status != ImportRecordStatus.NEW && it.status != ImportRecordStatus.IDENTICAL },
                items = items
            )
        }.getOrNull()
        _state.update { it.copy(importConflictPreview = preview) }
    }

    fun clearConflictPreview() {
        _state.update { it.copy(importConflictPreview = null) }
    }

    fun refreshStreakAndStats() {
        val entries = _state.value.journalEntries
        val zone = ZoneId.systemDefault()
        val dates = entries.map { Instant.ofEpochMilli(it.createdAt).atZone(zone).toLocalDate() }
        val streak = BrewAnalyticsEngine.calculateStreak(dates)
        val now = LocalDate.now()
        val stats = BrewAnalyticsEngine.calculateMonthlyStats(entries, now.year, now.monthValue)
        _state.update { it.copy(brewStreak = streak, monthlyStats = stats) }
    }

    // ========================================================================
    // Sensory & Cupping Sessions (Epic S2-E05)
    // ========================================================================

    fun selectCuppingSession(sessionId: String?) {
        _state.update {
            it.copy(
                activeCuppingSessionId = sessionId,
                selectedCuppingSampleId = it.cuppingSessions.find { s -> s.id == sessionId }?.samples?.firstOrNull()?.sampleId
            )
        }
    }

    fun selectCuppingSample(sampleId: String?) {
        _state.update { it.copy(selectedCuppingSampleId = sampleId) }
    }

    fun createCuppingSession(
        title: String,
        location: String,
        templateType: CuppingTemplateType,
        maskingMode: MaskingMode,
        evaluatorNames: List<String>,
        sampleBeanNames: List<String>
    ) {
        val sessionId = UUID.randomUUID().toString()
        val template = StandardCuppingTemplates.getTemplate(templateType)
        val evaluators = evaluatorNames.mapIndexed { idx, name ->
            Evaluator(
                id = "eval_${idx + 1}_${UUID.randomUUID().toString().take(4)}",
                name = name.ifBlank { "Evaluator ${idx + 1}" },
                role = if (idx == 0) EvaluatorRole.LEAD_CUPPER else EvaluatorRole.Q_GRADER
            )
        }.ifEmpty {
            listOf(Evaluator("eval_lead", "Lead Evaluator", EvaluatorRole.LEAD_CUPPER))
        }

        val samples = sampleBeanNames.mapIndexed { idx, beanName ->
            val codeLetter = ('A' + idx).toString()
            CuppingSample(
                sampleId = "sample_${idx + 1}_${UUID.randomUUID().toString().take(4)}",
                blindCode = "Sample #$codeLetter${idx + 1}",
                actualBeanId = "bean_$idx",
                beanName = beanName.ifBlank { "Sample $codeLetter" },
                roaster = "Local Artisan Roaster",
                origin = "Specialty Origin",
                process = "Washed",
                roastDateEpoch = System.currentTimeMillis() - 86400000L * 10,
                tablePosition = idx + 1,
                isRevealed = maskingMode == MaskingMode.OPEN_LABEL
            )
        }.ifEmpty {
            listOf(
                CuppingSample(
                    sampleId = "sample_default",
                    blindCode = "Sample #A1",
                    actualBeanId = "bean_def",
                    beanName = "House Single Origin",
                    roaster = "Roastery",
                    origin = "Origin",
                    process = "Washed",
                    roastDateEpoch = System.currentTimeMillis() - 86400000L * 7,
                    tablePosition = 1,
                    isRevealed = maskingMode == MaskingMode.OPEN_LABEL
                )
            )
        }

        val session = CuppingSession(
            id = sessionId,
            title = title.ifBlank { "New Cupping Session" },
            location = location.ifBlank { "Cupping Lab" },
            template = template,
            maskingMode = maskingMode,
            status = SessionStatus.OPEN,
            evaluators = evaluators,
            samples = samples,
            sheets = emptyList(),
            auditTrail = listOf(
                AuditEntry(
                    timestamp = System.currentTimeMillis(),
                    editorId = evaluators.first().id,
                    action = "CREATE_SESSION",
                    previousStateSummary = "NONE",
                    newStateSummary = "OPEN",
                    reason = "Session initialized by ${evaluators.first().name}"
                )
            ),
            createdAt = System.currentTimeMillis()
        )

        _state.update {
            it.copy(
                cuppingSessions = listOf(session) + it.cuppingSessions,
                activeCuppingSessionId = sessionId,
                selectedCuppingSampleId = samples.first().sampleId,
                notice = "Created cupping session '${session.title}'."
            )
        }
    }

    fun revealCuppingSample(sessionId: String, sampleId: String) {
        _state.update { current ->
            val updated = current.cuppingSessions.map { s ->
                if (s.id == sessionId) {
                    val updatedSamples = s.samples.map { smp ->
                        if (smp.sampleId == sampleId) smp.copy(isRevealed = true) else smp
                    }
                    val audit = s.auditTrail + AuditEntry(
                        timestamp = System.currentTimeMillis(),
                        editorId = s.evaluators.firstOrNull()?.id ?: "lead",
                        action = "REVEAL_SAMPLE",
                        previousStateSummary = "Masked",
                        newStateSummary = "Revealed",
                        reason = "Controlled blind reveal for sample $sampleId"
                    )
                    s.copy(samples = updatedSamples, auditTrail = audit)
                } else s
            }
            current.copy(cuppingSessions = updated, notice = "Revealed true identity for sample.")
        }
    }

    fun submitCuppingSheet(
        sessionId: String,
        sampleId: String,
        evaluatorId: String,
        attributeScores: Map<String, BigDecimal>,
        descriptors: List<String>,
        taintCups: Int = 0,
        faultCups: Int = 0,
        defectDesc: String = "",
        notes: String = ""
    ) {
        val session = _state.value.cuppingSessions.find { it.id == sessionId } ?: return
        require(session.status == SessionStatus.OPEN || session.status == SessionStatus.REOPENED) {
            "Cannot submit evaluation to a ${session.status.name} session."
        }
        val defects = DefectRecord(taintCups, faultCups, defectDesc)
        val totalScore = CuppingAnalyticsEngine.calculateTotalCuppingScore(
            session.template.type,
            attributeScores,
            defects
        )
        val sheet = EvaluationSheet(
            id = UUID.randomUUID().toString(),
            sessionId = sessionId,
            sampleId = sampleId,
            evaluatorId = evaluatorId,
            origin = EpistemologicalOrigin.HUMAN_OBSERVATION,
            attributeScores = attributeScores,
            descriptors = descriptors,
            defects = defects,
            totalScore = totalScore,
            notes = notes,
            submittedAt = System.currentTimeMillis()
        )

        _state.update { current ->
            val updated = current.cuppingSessions.map { s ->
                if (s.id == sessionId) {
                    val newSheets = s.sheets.filter { it.sampleId != sampleId || it.evaluatorId != evaluatorId } + sheet
                    s.copy(sheets = newSheets)
                } else s
            }
            val updatedPalate = if (totalScore.toDouble() >= 80.0) {
                val acidity = attributeScores["acidity"]?.toDouble() ?: 7.5
                val sweetness = attributeScores["sweetness"]?.toDouble() ?: 7.5
                val bitterness = attributeScores["bitterness"]?.toDouble() ?: 7.5
                val body = attributeScores["body"]?.toDouble() ?: 7.5
                val norm = { v: Double -> ((v - 6.0) / 4.0).coerceIn(0.0, 1.0) }
                val evidence = PersonalSensoryEvidence(
                    roast = 0.3,
                    acidity = norm(acidity),
                    sweetness = norm(sweetness),
                    bitterness = norm(bitterness),
                    body = norm(body),
                    rating = (totalScore.toDouble() / 10.0).coerceIn(7.5, 10.0),
                    daysAgo = 0.0
                )
                PalateBayesianEngine.updateProfile(current.palateProfile, evidence)
            } else current.palateProfile
            current.copy(cuppingSessions = updated, palateProfile = updatedPalate, notice = "Submitted score: $totalScore.")
        }
    }

    fun closeCuppingSession(sessionId: String) {
        _state.update { current ->
            val updated = current.cuppingSessions.map { s ->
                if (s.id == sessionId) {
                    val audit = s.auditTrail + AuditEntry(
                        timestamp = System.currentTimeMillis(),
                        editorId = s.evaluators.firstOrNull()?.id ?: "lead",
                        action = "CLOSE_SESSION",
                        previousStateSummary = s.status.name,
                        newStateSummary = SessionStatus.CLOSED.name,
                        reason = "Formal evaluation closed; scores locked for consensus"
                    )
                    s.copy(status = SessionStatus.CLOSED, closedAt = System.currentTimeMillis(), auditTrail = audit)
                } else s
            }
            current.copy(cuppingSessions = updated, notice = "Cupping session closed and locked.")
        }
    }

    fun reopenCuppingSession(sessionId: String, editorId: String, reason: String) {
        _state.update { current ->
            val updated = current.cuppingSessions.map { s ->
                if (s.id == sessionId) {
                    val audit = s.auditTrail + AuditEntry(
                        timestamp = System.currentTimeMillis(),
                        editorId = editorId,
                        action = "REOPEN_SESSION",
                        previousStateSummary = s.status.name,
                        newStateSummary = SessionStatus.REOPENED.name,
                        reason = reason.ifBlank { "Administrative score correction requested" }
                    )
                    s.copy(status = SessionStatus.REOPENED, auditTrail = audit)
                } else s
            }
            current.copy(cuppingSessions = updated, notice = "Cupping session reopened with immutable audit trail.")
        }
    }

    fun savePhysicalQc(record: PhysicalQcRecord) {
        _state.update {
            val map = it.physicalQcRecords + (record.sampleId to record)
            it.copy(physicalQcRecords = map, notice = "Saved Physical QC record: ${record.calculatedGrade}.")
        }
    }

    fun recordRoastRestObservation(observation: RoastRestObservation) {
        _state.update {
            val list = listOf(observation) + it.roastRestObservations.filter { o -> o.id != observation.id }
            it.copy(roastRestObservations = list, notice = "Logged roast-rest observation: ${observation.observedPhase.label}.")
        }
    }

    fun exportSessionQcReport(sessionId: String) {
        val session = _state.value.cuppingSessions.find { it.id == sessionId } ?: return
        val receipt = CuppingAnalyticsEngine.generateOfflineReport(session, _state.value.physicalQcRecords)
        _state.update { it.copy(generatedQcReceipt = receipt, notice = "Generated QC Report (SHA-256: ${receipt.sha256Fingerprint.take(12)}...)") }
    }

    fun clearQcReceipt() {
        _state.update { it.copy(generatedQcReceipt = null) }
    }

    private fun seedInitialSensoryData() {
        if (_state.value.cuppingSessions.isNotEmpty()) return

        val lead = Evaluator("eval_marcus", "Marcus Vance (Lead Q-Grader)", EvaluatorRole.LEAD_CUPPER)
        val guest = Evaluator("eval_sarah", "Sarah Chen (Roast Master)", EvaluatorRole.Q_GRADER)
        val barista = Evaluator("eval_diego", "Diego Silva (Head Barista)", EvaluatorRole.BARISTA)

        val sample1 = CuppingSample(
            sampleId = "sample_geisha_01",
            blindCode = "Sample #A1",
            actualBeanId = "bean_panama_geisha",
            beanName = "Panama Boquete Geisha Washed",
            roaster = "Hacienda La Esmeralda",
            origin = "Panama (1,650m)",
            process = "Washed",
            roastDateEpoch = System.currentTimeMillis() - 86400000L * 14,
            tablePosition = 1,
            isRevealed = true
        )
        val sample2 = CuppingSample(
            sampleId = "sample_ethiopia_02",
            blindCode = "Sample #B2",
            actualBeanId = "bean_ethiopia_natural",
            beanName = "Ethiopia Yirgacheffe Banko Gotiti",
            roaster = "Onyx Coffee Lab",
            origin = "Ethiopia (2,050m)",
            process = "Natural Anaerobic",
            roastDateEpoch = System.currentTimeMillis() - 86400000L * 8,
            tablePosition = 2,
            isRevealed = false
        )

        val sheet1 = EvaluationSheet(
            id = "sheet_01",
            sessionId = "session_s2_seed",
            sampleId = "sample_geisha_01",
            evaluatorId = "eval_marcus",
            attributeScores = mapOf(
                "fragrance_aroma" to BigDecimal("9.25"),
                "flavor" to BigDecimal("9.25"),
                "aftertaste" to BigDecimal("9.00"),
                "acidity" to BigDecimal("9.25"),
                "body" to BigDecimal("8.75"),
                "balance" to BigDecimal("9.00"),
                "uniformity" to BigDecimal("10.00"),
                "clean_cup" to BigDecimal("10.00"),
                "sweetness" to BigDecimal("10.00"),
                "overall" to BigDecimal("9.25")
            ),
            descriptors = listOf("Jasmine", "Bergamot", "Peach"),
            totalScore = BigDecimal("93.75"),
            notes = "Ethereal floral aroma, sparkling bergamot citric acidity, long sweet peach finish.",
            submittedAt = System.currentTimeMillis() - 3600000L
        )

        val sheet2 = EvaluationSheet(
            id = "sheet_02",
            sessionId = "session_s2_seed",
            sampleId = "sample_geisha_01",
            evaluatorId = "eval_sarah",
            attributeScores = mapOf(
                "fragrance_aroma" to BigDecimal("9.00"),
                "flavor" to BigDecimal("9.00"),
                "aftertaste" to BigDecimal("8.75"),
                "acidity" to BigDecimal("9.25"),
                "body" to BigDecimal("8.75"),
                "balance" to BigDecimal("8.75"),
                "uniformity" to BigDecimal("10.00"),
                "clean_cup" to BigDecimal("10.00"),
                "sweetness" to BigDecimal("10.00"),
                "overall" to BigDecimal("9.00")
            ),
            descriptors = listOf("Jasmine", "Peach", "Honey"),
            totalScore = BigDecimal("92.50"),
            notes = "Super clean cup with delicate white flowers and sweet nectar.",
            submittedAt = System.currentTimeMillis() - 3000000L
        )

        val seedSession = CuppingSession(
            id = "session_s2_seed",
            title = "Specialty Origin Calibration & Blind QC",
            location = "Main Roastery Lab",
            template = StandardCuppingTemplates.SPECIALTY_STANDARD,
            maskingMode = MaskingMode.SINGLE_BLIND_EVALUATOR,
            status = SessionStatus.OPEN,
            evaluators = listOf(lead, guest, barista),
            samples = listOf(sample1, sample2),
            sheets = listOf(sheet1, sheet2),
            auditTrail = listOf(
                AuditEntry(
                    timestamp = System.currentTimeMillis() - 7200000L,
                    editorId = lead.id,
                    action = "CREATE_SESSION",
                    previousStateSummary = "NONE",
                    newStateSummary = "OPEN",
                    reason = "Specialty calibration session initialized"
                )
            ),
            createdAt = System.currentTimeMillis() - 7200000L
        )

        val physicalQc = PhysicalQcRecord(
            id = "phys_geisha_01",
            sampleId = "sample_geisha_01",
            sampleMassGrams = BigDecimal("350.0"),
            moisturePercentage = BigDecimal("10.8"),
            waterActivityAw = BigDecimal("0.58"),
            bulkDensityGPerL = BigDecimal("715.0"),
            totalPrimaryDefects = 0,
            totalSecondaryDefects = 2,
            calculatedGrade = "Specialty Grade 1 (Strict)",
            roastedAgtronWholeBean = BigDecimal("68.0"),
            roastedAgtronGround = BigDecimal("76.5"),
            roastDeltaAgtron = BigDecimal("8.5")
        )

        val obs1 = RoastRestObservation(
            id = "obs_01",
            bagId = "bag_geisha_lot1",
            roastDateEpoch = System.currentTimeMillis() - 86400000L * 14,
            observationDateEpoch = System.currentTimeMillis() - 86400000L * 11,
            daysPostRoast = 3,
            cuppingScore = BigDecimal("86.5"),
            observedAroma = BigDecimal("8.0"),
            observedAcidity = BigDecimal("8.5"),
            observedSweetness = BigDecimal("7.5"),
            perceivedCarbonicAcid = true,
            qualitativeNotes = "Carbonic sharpness masking subtle florals, rapid bubbling on crust.",
            observedPhase = DegassingPhase.TOO_FRESH_CO2_INTERFERENCE
        )

        val obs2 = RoastRestObservation(
            id = "obs_02",
            bagId = "bag_geisha_lot1",
            roastDateEpoch = System.currentTimeMillis() - 86400000L * 14,
            observationDateEpoch = System.currentTimeMillis(),
            daysPostRoast = 14,
            cuppingScore = BigDecimal("93.5"),
            observedAroma = BigDecimal("9.5"),
            observedAcidity = BigDecimal("9.2"),
            observedSweetness = BigDecimal("9.0"),
            perceivedCarbonicAcid = false,
            qualitativeNotes = "Peak sensory expression. Jasmine and bergamot fully open, silky lingering sweetness.",
            observedPhase = DegassingPhase.PEAK_FLAVOR_WINDOW
        )

        _state.update {
            it.copy(
                cuppingSessions = listOf(seedSession),
                activeCuppingSessionId = seedSession.id,
                selectedCuppingSampleId = sample1.sampleId,
                physicalQcRecords = mapOf(sample1.sampleId to physicalQc),
                roastRestObservations = listOf(obs2, obs1)
            )
        }
    }

    // ========================================================================
    // Epic S2-E06: Personalization, Local AI & RAG Integration
    // ========================================================================

    fun submitPalateQuiz(answers: PalateQuizAnswers) {
        val profile = PalateQuizMapper.createFromQuiz(answers)
        _state.update {
            it.copy(
                palateProfile = profile,
                notice = "Calibrated personal palate profile! Light/Acidity/Sweetness calibrated."
            )
        }
    }

    fun updatePalateSliders(
        roast: Double? = null,
        acidity: Double? = null,
        sweetness: Double? = null,
        bitterness: Double? = null,
        body: Double? = null
    ) {
        val updated = PalateQuizMapper.updateManualSliders(_state.value.palateProfile, roast, acidity, sweetness, bitterness, body)
        _state.update { it.copy(palateProfile = updated, notice = "Updated palate preference sliders.") }
    }

    fun recordSensoryEvidence(evidence: PersonalSensoryEvidence) {
        val updated = PalateBayesianEngine.updateProfile(_state.value.palateProfile, evidence)
        _state.update {
            it.copy(
                palateProfile = updated,
                notice = "Bayesian palate vector updated from qualified session (${evidence.rating}/10)."
            )
        }
    }

    fun parseBeanOcrText(lines: List<String>) {
        val draft = OcrBeanParser.parseLines(lines)
        _state.update {
            it.copy(
                parsedBeanDraft = draft,
                notice = "Parsed coffee bag OCR: ${draft.originCountry ?: "Origin"} / ${draft.variety ?: "Variety"}. Review below."
            )
        }
    }

    fun commitParsedBeanDraft(
        name: String,
        roaster: String,
        country: String,
        region: String,
        variety: String,
        process: String,
        roastLevel: String,
        elevation: String
    ) {
        val bean = CoffeeBean(
            id = UUID.randomUUID().toString(),
            name = name.ifBlank { "$country $variety" },
            producer = roaster,
            country = country,
            region = region,
            variety = variety,
            process = process,
            roastLevel = roastLevel,
            altitudeMeters = elevation.toIntOrNull()
        )
        task("Saving scanned bean") {
            graph.repository.addBean(bean)
            _state.update {
                it.copy(
                    parsedBeanDraft = null,
                    notice = "Saved scanned bean '${bean.name}' to inventory."
                )
            }
        }
    }

    fun dismissParsedBeanDraft() {
        _state.update { it.copy(parsedBeanDraft = null) }
    }

    fun updateWeatherParameters(elevationMeters: Double, ambientTemp: Double = 22.0, humidity: Double = 50.0) {
        val tBoil = WeatherAltitudeCalculator.calculateBoilingPointCelsius(elevationMeters, ElevationUnit.METERS)
        val params = WeatherParameters(
            elevationMeters = elevationMeters,
            ambientTempCelsius = ambientTemp,
            relativeHumidityPercent = humidity,
            calculatedBoilingPointCelsius = tBoil
        )
        _state.update {
            it.copy(
                weatherParams = params,
                notice = "Elevation set to ${elevationMeters.toInt()}m. Water boiling point is ${tBoil}°C."
            )
        }
    }

    fun generateInventoryRecommendations(brewMethod: String = "Pour Over (V60)") {
        val candidates = _state.value.beans.map { b ->
            val acidityScore = if (b.process.contains("wash", true)) 0.85 else 0.60
            val sweetnessScore = if (b.process.contains("nat", true)) 0.85 else 0.70
            BeanInventoryCandidate(
                beanId = b.id,
                beanName = b.name,
                sensoryProfile = PalateVector(0.30, acidityScore, sweetnessScore, 0.20, 0.50),
                roastDate = LocalDate.now().minusDays(14),
                availableMassGrams = 250.0
            )
        }
        val recommendations = RecommendationEngine.rankInventory(
            palate = _state.value.palateProfile,
            candidates = candidates,
            availableGearMethods = setOf("Pour Over (V60)", "Espresso", "AeroPress", "French Press"),
            brewMethod = brewMethod
        )
        _state.update {
            it.copy(
                inventoryRecommendations = recommendations,
                notice = "Generated ${recommendations.size} recommendations for $brewMethod."
            )
        }
    }

    fun proposeSingleVariableAdjustment(
        diagnosis: SensoryFeedbackDiagnosis,
        actualSec: Int,
        targetSec: Int
    ) {
        val currentRecipeMap = mapOf(
            "grindClicks" to 18,
            "waterTempCelsius" to (_state.value.draft.fields["temperature"]?.toDoubleOrNull() ?: 93.0),
            "doseGrams" to (_state.value.draft.fields["dose"]?.toDoubleOrNull() ?: 15.0),
            "waterGrams" to (_state.value.draft.fields["water"]?.toDoubleOrNull() ?: 250.0)
        )
        val proposal = FeedbackLoopEngine.proposeAdjustment(
            previousRecipe = currentRecipeMap,
            actualDurationSeconds = actualSec,
            targetDurationSeconds = targetSec,
            diagnosis = diagnosis,
            boilingPointCelsius = _state.value.weatherParams.calculatedBoilingPointCelsius
        )
        _state.update {
            it.copy(
                ovatAdjustmentProposal = proposal,
                notice = "Proposed adjustment: ${proposal.variableAdjusted} -> ${proposal.proposedValue}"
            )
        }
    }

    fun applyOvatAdjustmentToDraft() {
        val proposal = _state.value.ovatAdjustmentProposal ?: return
        val currentDraft = _state.value.draft
        val newFields = currentDraft.fields.toMutableMap()
        when (proposal.variableAdjusted) {
            "Grind Size" -> {
                val newClicks = proposal.modifiedRecipePayload["grindClicks"] as? Int
                newFields["grind"] = "$newClicks clicks (${proposal.proposedValue})"
            }
            "Water Temperature" -> {
                val newTemp = proposal.modifiedRecipePayload["waterTempCelsius"] as? Double
                newFields["temperature"] = (newTemp ?: 93.0).toString()
            }
            "Coffee Dose" -> {
                val newDose = proposal.modifiedRecipePayload["doseGrams"] as? Double
                newFields["dose"] = (newDose ?: 15.0).toString()
            }
            "Water Volume" -> {
                val newWater = proposal.modifiedRecipePayload["waterGrams"] as? Double
                newFields["water"] = (newWater ?: 250.0).toString()
            }
        }
        val updatedDraft = currentDraft.copy(fields = newFields)
        _state.update {
            it.copy(
                draft = updatedDraft,
                ovatAdjustmentProposal = null,
                notice = "Applied OVAT adjustment (${proposal.variableAdjusted}) to current recipe draft."
            )
        }
    }

    fun dismissOvatAdjustment() {
        _state.update { it.copy(ovatAdjustmentProposal = null) }
    }

    fun selectBaristaPersonaPreset(preset: BaristaPersonaPreset) {
        val persona = AssistantPersona(
            id = preset.id,
            title = preset.title,
            description = preset.baseSystemPrompt,
            systemPrompt = preset.baseSystemPrompt
        )
        _state.update {
            val list = if (it.personas.none { p -> p.id == preset.id }) it.personas + persona else it.personas
            it.copy(personas = list, selectedPersona = persona, notice = "Switched persona to '${preset.title}'.")
        }
    }

    fun updateCustomPersonaPrompt(customPrompt: String) {
        val sanitized = PersonaSanitizer.sanitizeCustomPrompt(customPrompt)
        val customPersona = AssistantPersona(
            id = "custom_persona",
            title = "Custom Persona",
            description = sanitized,
            systemPrompt = sanitized
        )
        _state.update {
            val list = it.personas.filter { p -> p.id != "custom_persona" } + customPersona
            it.copy(personas = list, selectedPersona = customPersona, notice = "Custom persona prompt sanitized and saved.")
        }
    }

    fun auditIngredientSafety(
        name: String,
        foodGrade: Boolean?,
        allergens: List<String>,
        massFractionPercent: Double,
        supplierMaxPercent: Double? = null
    ) {
        val audit = IngredientSafetyValidator.auditIngredient(name, foodGrade, allergens, massFractionPercent, supplierMaxPercent)
        _state.update {
            it.copy(
                safetyAuditResult = audit,
                notice = if (audit.permitsBeverageConsumption) "Ingredient audit passed." else "CRITICAL: Ingestion hazard detected!"
            )
        }
    }

    fun clearNotice() {
        _state.update { it.copy(notice = null, error = null) }
    }
    fun flushDraft() {
        if (_state.value.ready && !replacingDraft) graph.persistDraft(gson.toJson(_state.value.draft))
    }
    fun setLanguage(lang: AppLanguage) = task("Updating language") {
        graph.repository.saveSetting("user_language", lang.code)
        _state.update {
            val updated = it.unitPreferences.copy(language = lang)
            it.copy(unitPreferences = updated, notice = "Language updated to ${lang.displayName}.")
        }
        diagnosticBuffer.log(LogLevel.INFO, "Settings", "Language changed to ${lang.displayName}")
    }

    fun setUnitPreferences(prefs: UserUnitPreferences) = task("Saving unit preferences") {
        graph.repository.saveSetting("user_units", gson.toJson(prefs))
        _state.update { it.copy(unitPreferences = prefs, notice = "Unit preferences updated.") }
        diagnosticBuffer.log(LogLevel.INFO, "Settings", "Unit preferences updated: ${prefs.temperatureUnit.symbol}, ${prefs.massUnit.symbol}")
    }

    fun selectHelpTopic(topicId: String?) {
        _state.update { it.copy(selectedHelpTopicId = topicId) }
    }

    fun refreshDiagnostics() {
        _state.update { it.copy(diagnosticLogSnapshot = diagnosticBuffer.snapshot()) }
    }

    fun clearDiagnostics() {
        diagnosticBuffer.clear()
        _state.update { it.copy(diagnosticLogSnapshot = emptyList(), notice = "Diagnostic logs cleared.") }
    }

    fun exportDiagnosticLogs(context: Context) {
        val formatted = diagnosticBuffer.exportFormattedString()
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, formatted)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Export Diagnostic Logs")
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(shareIntent)
        diagnosticBuffer.log(LogLevel.INFO, "Diagnostics", "User exported diagnostic logs via Sharesheet")
    }

    fun updateDegradationState(
        llm: ModuleStatus? = null,
        ocr: ModuleStatus? = null,
        ble: ModuleStatus? = null
    ) {
        _state.update {
            val current = it.degradationState
            val newState = current.copy(
                llmStatus = llm ?: current.llmStatus,
                ocrStatus = ocr ?: current.ocrStatus,
                bleStatus = ble ?: current.bleStatus
            )
            it.copy(degradationState = newState)
        }
    }

    // ========================================================================
    // Epic S2-E01: Coffee Bean, Ingredient & Equipment Foundation
    // ========================================================================

    private fun seedInitialCatalogData() {
        if (_state.value.detailedBeans.isNotEmpty()) return

        val roast1 = LocalDate.now().minusDays(14)
        val roast2 = LocalDate.now().minusDays(10)
        val roast3 = LocalDate.now().minusDays(21)
        val roast4 = LocalDate.now().minusDays(18)

        val bean1 = DetailedBeanRecord(
            id = "detailed-bean-sey-worka",
            roaster = "Sey Coffee",
            name = "Worka Sakaro",
            originCountry = "Ethiopia",
            region = "Gedeb, Yirgacheffe",
            farm = "Halo Hartume",
            producer = "Mijane Woresa",
            varietal = "Kurume, Dega",
            process = ProcessMethod.WASHED,
            altitudeMeters = 2100,
            harvestDate = "2026-02-15",
            roastDate = roast1,
            roastLevel = RoastLevel.LIGHT,
            tastingNotes = listOf("Peach", "Jasmine", "Bergamot", "Meyer Lemon"),
            tags = listOf("Floral", "High-Altitude", "African", "Ethiopian"),
            isFavorite = true,
            bags = listOf(
                BagInventoryItem(
                    id = "bag-sey-01",
                    beanId = "detailed-bean-sey-worka",
                    packageMassGrams = BigDecimal("250.00"),
                    remainingMassGrams = BigDecimal("214.00"),
                    status = BagStatus.ACTIVE,
                    openedAtEpochMs = System.currentTimeMillis() - 86400000L * 7,
                    pricePaid = BigDecimal("24.00")
                )
            )
        )

        val bean2 = DetailedBeanRecord(
            id = "detailed-bean-tw-caballero",
            roaster = "Tim Wendelboe",
            name = "Caballero Geisha",
            originCountry = "Honduras",
            region = "Marcala",
            farm = "Finca El Puente",
            producer = "Marysabel Caballero",
            varietal = "Geisha",
            process = ProcessMethod.WASHED,
            altitudeMeters = 1600,
            harvestDate = "2026-03-01",
            roastDate = roast2,
            roastLevel = RoastLevel.LIGHT,
            tastingNotes = listOf("Floral", "White Tea", "Papaya", "Jasmine"),
            tags = listOf("Geisha", "Central American", "Light Roast"),
            isFavorite = true,
            bags = listOf(
                BagInventoryItem(
                    id = "bag-tw-01",
                    beanId = "detailed-bean-tw-caballero",
                    packageMassGrams = BigDecimal("250.00"),
                    remainingMassGrams = BigDecimal("185.00"),
                    status = BagStatus.ACTIVE,
                    openedAtEpochMs = System.currentTimeMillis() - 86400000L * 4,
                    pricePaid = BigDecimal("32.00")
                )
            )
        )

        val bean3 = DetailedBeanRecord(
            id = "detailed-bean-kawa-elparaiso",
            roaster = "Kawa Coffee",
            name = "El Paraiso Lychee",
            originCountry = "Colombia",
            region = "Piendamo, Cauca",
            farm = "El Paraiso",
            producer = "Diego Samuel Bermudez",
            varietal = "Castillo",
            process = ProcessMethod.ANAEROBIC_NATURAL,
            altitudeMeters = 1950,
            harvestDate = "2026-01-20",
            roastDate = roast4,
            roastLevel = RoastLevel.LIGHT,
            tastingNotes = listOf("Lychee", "Rose Water", "Raspberry Jam"),
            tags = listOf("Anaerobic", "Experimental", "Fruit Bomb"),
            isFavorite = false,
            bags = listOf(
                BagInventoryItem(
                    id = "bag-kawa-01",
                    beanId = "detailed-bean-kawa-elparaiso",
                    packageMassGrams = BigDecimal("200.00"),
                    remainingMassGrams = BigDecimal("200.00"),
                    status = BagStatus.FROZEN,
                    freezeHistory = listOf(
                        FreezeEvent(
                            frozenAtEpochMs = System.currentTimeMillis() - 86400000L * 12,
                            thawedAtEpochMs = null
                        )
                    ),
                    pricePaid = BigDecimal("28.00")
                )
            )
        )

        val grinder1 = GrinderRecord(
            id = "grinder-comandante-c40",
            brand = "Comandante",
            model = "C40 MK4 Nitro Blade",
            calibrationUnit = CalibrationUnit.CLICKS,
            burrProfile = BurrProfile(
                burrType = BurrType.CONICAL,
                diameterMm = BigDecimal("39.00"),
                material = BurrMaterial.STAINLESS_STEEL,
                burrModelName = "High-Nitrogen Nitro Blade"
            ),
            zeroPointOffset = BigDecimal.ZERO,
            clicksPerRotation = 12,
            typicalRpm = null,
            notes = "World benchmark manual hand grinder. Standard 1 click ~ 30 microns.",
            isFavorite = true
        )

        val grinder2 = GrinderRecord(
            id = "grinder-mahlkonig-ek43",
            brand = "Mahlkönig",
            model = "EK43S",
            calibrationUnit = CalibrationUnit.MICRONS,
            burrProfile = BurrProfile(
                burrType = BurrType.FLAT,
                diameterMm = BigDecimal("98.00"),
                material = BurrMaterial.TITANIUM_COATED,
                burrModelName = "Titus Pre-Breaker"
            ),
            zeroPointOffset = BigDecimal("20.00"),
            clicksPerRotation = null,
            typicalRpm = 1450,
            notes = "Laboratory commercial grinder aligned with dial indicator within 5 microns.",
            isFavorite = true
        )

        val grinder3 = GrinderRecord(
            id = "grinder-fellow-ode",
            brand = "Fellow",
            model = "Ode Gen 2",
            calibrationUnit = CalibrationUnit.STEPLESS_DEGREES,
            burrProfile = BurrProfile(
                burrType = BurrType.FLAT,
                diameterMm = BigDecimal("64.00"),
                material = BurrMaterial.STAINLESS_STEEL,
                burrModelName = "Gen 2 Brew Burrs"
            ),
            zeroPointOffset = BigDecimal.ZERO,
            clicksPerRotation = 31,
            typicalRpm = 1400,
            notes = "Domestic filter-focused single doser with anti-static ionizer.",
            isFavorite = false
        )

        val eq1 = EquipmentItem(
            id = "eq-hario-v60",
            category = EquipmentCategory.DRIPPER,
            brand = "Hario",
            model = "V60 Ceramic 02",
            parameters = mapOf("Material" to "Arita Porcelain", "Angle" to "60deg", "Ribs" to "Spiral"),
            isFavorite = true,
            tags = listOf("Conical", "Pour-Over")
        )

        val eq2 = EquipmentItem(
            id = "eq-kalita-185",
            category = EquipmentCategory.DRIPPER,
            brand = "Kalita",
            model = "Wave 185 Stainless Steel",
            parameters = mapOf("Material" to "Stainless Steel", "Bed" to "Flat bottom 3-hole"),
            isFavorite = false,
            tags = listOf("Flat-Bed", "Immersion-Drip")
        )

        val eq3 = EquipmentItem(
            id = "eq-fellow-stagg",
            category = EquipmentCategory.KETTLE,
            brand = "Fellow",
            model = "Stagg EKG Gooseneck",
            parameters = mapOf("Capacity" to "900mL", "Spout" to "Precision Gooseneck", "TempControl" to "PID 1°F"),
            isFavorite = true,
            tags = listOf("Gooseneck", "PID")
        )

        val eq4 = EquipmentItem(
            id = "eq-acaia-pearl",
            category = EquipmentCategory.SCALE,
            brand = "Acaia",
            model = "Pearl 2021",
            parameters = mapOf("Resolution" to "0.1g", "FlowIndicator" to "Visual LED Flow Rate", "Bluetooth" to "BLE 4.0"),
            isFavorite = true,
            tags = listOf("Smart Scale", "BLE")
        )

        val eq5 = EquipmentItem(
            id = "eq-vst-basket",
            category = EquipmentCategory.PORTAFILTER_BASKET,
            brand = "VST",
            model = "18g Precision Ridgeless Basket",
            parameters = mapOf("Diameter" to "58.5mm", "Holes" to "660 laser-measured", "Variance" to "< 2%"),
            isFavorite = true,
            tags = listOf("Espresso", "Precision")
        )

        val ing1 = FoodGradeIngredientItem(
            id = "food-ing-citric",
            name = "Citric Acid (Food Grade 10% w/w)",
            densityGramsPerMl = BigDecimal("1.04"),
            allergens = emptyList(),
            supplierMaxPercent = BigDecimal("2.50"),
            isFoodGradeVerified = true,
            casNumber = "77-92-9",
            provenance = "USP Food Chemicals Codex Standard Solution"
        )

        val ing2 = FoodGradeIngredientItem(
            id = "food-ing-malic",
            name = "Malic Acid (Food Grade 5% w/w)",
            densityGramsPerMl = BigDecimal("1.02"),
            allergens = emptyList(),
            supplierMaxPercent = BigDecimal("2.00"),
            isFoodGradeVerified = true,
            casNumber = "6915-15-7",
            provenance = "Food grade organic acid standard"
        )

        val greenLot1 = GreenCoffeeLot(
            id = "green-eth-004",
            lotNumber = "LOT-2026-ETH-004",
            originCountry = "Ethiopia",
            producer = "Mijane Woresa",
            harvestYear = "2026",
            moisturePercent = BigDecimal("10.80"),
            bulkDensityGramsPerLiter = BigDecimal("695.00"),
            waterActivityAw = BigDecimal("0.54"),
            category1DefectCount = 0,
            category2DefectCount = 2,
            screenDistributionPercent = mapOf(17 to BigDecimal("70.00"), 18 to BigDecimal("30.00")),
            sampleStatus = SampleLotStatus.PRODUCTION_LOT,
            notes = "High-density micro-lot. Screen 17/18 uniform."
        )

        val roastBatch1 = RoastBatchRecord(
            id = "roast-b101",
            batchNumber = "B-2026-101",
            greenLotId = "green-eth-004",
            chargeTemperatureCelsius = BigDecimal("200.00"),
            dropTemperatureCelsius = BigDecimal("211.50"),
            totalRoastDurationSeconds = 590,
            firstCrackTimeSeconds = 475,
            developmentTimeRatioPercent = RoastBatchRecord.calculateDtr(590, 475),
            curvePoints = listOf(
                RoastDataPoint(0, BigDecimal("200.00"), null),
                RoastDataPoint(60, BigDecimal("115.00"), null),
                RoastDataPoint(180, BigDecimal("145.00"), BigDecimal("15.00")),
                RoastDataPoint(300, BigDecimal("170.00"), BigDecimal("12.50")),
                RoastDataPoint(475, BigDecimal("202.00"), BigDecimal("11.00")),
                RoastDataPoint(590, BigDecimal("211.50"), BigDecimal("5.00"))
            ),
            blendComponents = listOf(
                BlendComponentSpec("detailed-bean-sey-worka", BigDecimal("100.00"))
            )
        )

        _state.update {
            it.copy(
                detailedBeans = listOf(bean1, bean2, bean3),
                grinders = listOf(grinder1, grinder2, grinder3),
                equipmentCatalog = listOf(eq1, eq2, eq3, eq4, eq5),
                waterProfiles = WaterChemistryCalculator.ALL_PRESETS,
                foodGradeIngredients = listOf(ing1, ing2),
                greenCoffeeLots = listOf(greenLot1),
                roastBatches = listOf(roastBatch1)
            )
        }
    }

    fun addDetailedBean(bean: DetailedBeanRecord) {
        _state.update { it.copy(detailedBeans = it.detailedBeans + bean) }
        diagnosticBuffer.log(LogLevel.INFO, "BEAN", "Added bean '${bean.roaster} - ${bean.name}'")
    }

    fun updateDetailedBean(bean: DetailedBeanRecord) {
        _state.update {
            it.copy(detailedBeans = it.detailedBeans.map { b -> if (b.id == bean.id) bean else b })
        }
    }

    fun deleteDetailedBean(id: String) {
        _state.update {
            it.copy(detailedBeans = it.detailedBeans.filterNot { b -> b.id == id })
        }
    }

    fun toggleBeanFavorite(id: String) {
        _state.update {
            it.copy(detailedBeans = it.detailedBeans.map { b -> if (b.id == id) b.copy(isFavorite = !b.isFavorite) else b })
        }
    }

    fun addBagToBean(beanId: String, packageMassGrams: BigDecimal, price: BigDecimal? = null) {
        val newBag = BagInventoryItem(
            id = UUID.randomUUID().toString(),
            beanId = beanId,
            packageMassGrams = packageMassGrams,
            remainingMassGrams = packageMassGrams,
            status = BagStatus.PURCHASED,
            pricePaid = price
        )
        _state.update {
            it.copy(detailedBeans = it.detailedBeans.map { b ->
                if (b.id == beanId) b.copy(bags = b.bags + newBag) else b
            })
        }
    }

    fun deductDoseFromBag(bagId: String, doseGrams: BigDecimal): Boolean {
        var success = false
        _state.update {
            it.copy(detailedBeans = it.detailedBeans.map { b ->
                val bag = b.bags.find { bag -> bag.id == bagId }
                if (bag != null) {
                    val result = InventoryDeductionEngine.deductDose(bag, doseGrams)
                    if (result is InventoryDeductionResult.Success) {
                        success = true
                        diagnosticBuffer.log(LogLevel.INFO, "INVENTORY", "Deducted ${doseGrams}g from bag $bagId on bean '${b.name}'")
                        b.copy(bags = b.bags.map { bg -> if (bg.id == bagId) result.updatedBag else bg })
                    } else b
                } else b
            })
        }
        return success
    }

    fun toggleFreezeBag(bagId: String) {
        _state.update {
            it.copy(detailedBeans = it.detailedBeans.map { b ->
                val bag = b.bags.find { bag -> bag.id == bagId }
                if (bag != null) {
                    val updated = if (bag.status == BagStatus.FROZEN) {
                        InventoryDeductionEngine.thawBag(bag)
                    } else {
                        InventoryDeductionEngine.freezeBag(bag)
                    }
                    b.copy(bags = b.bags.map { bg -> if (bg.id == bagId) updated else bg })
                } else b
            })
        }
    }

    fun addGrinder(grinder: GrinderRecord) {
        _state.update { it.copy(grinders = it.grinders + grinder) }
    }

    fun toggleGrinderFavorite(id: String) {
        _state.update {
            it.copy(grinders = it.grinders.map { g -> if (g.id == id) g.copy(isFavorite = !g.isFavorite) else g })
        }
    }

    fun deleteGrinder(id: String) {
        _state.update { it.copy(grinders = it.grinders.filterNot { g -> g.id == id }) }
    }

    fun addEquipmentItem(item: EquipmentItem) {
        _state.update { it.copy(equipmentCatalog = it.equipmentCatalog + item) }
    }

    fun toggleEquipmentFavorite(id: String) {
        _state.update {
            it.copy(equipmentCatalog = it.equipmentCatalog.map { e -> if (e.id == id) e.copy(isFavorite = !e.isFavorite) else e })
        }
    }

    fun deleteEquipmentItem(id: String) {
        _state.update { it.copy(equipmentCatalog = it.equipmentCatalog.filterNot { e -> e.id == id }) }
    }

    fun addWaterProfile(profile: WaterMineralProfile) {
        _state.update { it.copy(waterProfiles = it.waterProfiles + profile) }
    }

    fun deleteWaterProfile(id: String) {
        _state.update { it.copy(waterProfiles = it.waterProfiles.filterNot { w -> w.id == id }) }
    }

    fun addFoodGradeIngredient(item: FoodGradeIngredientItem) {
        _state.update { it.copy(foodGradeIngredients = it.foodGradeIngredients + item) }
    }

    fun deleteFoodGradeIngredient(id: String) {
        _state.update { it.copy(foodGradeIngredients = it.foodGradeIngredients.filterNot { i -> i.id == id }) }
    }

    fun addGreenLot(lot: GreenCoffeeLot) {
        _state.update { it.copy(greenCoffeeLots = it.greenCoffeeLots + lot) }
    }

    fun deleteGreenLot(id: String) {
        _state.update { it.copy(greenCoffeeLots = it.greenCoffeeLots.filterNot { l -> l.id == id }) }
    }

    fun addRoastBatch(batch: RoastBatchRecord) {
        _state.update { it.copy(roastBatches = it.roastBatches + batch) }
    }

    fun deleteRoastBatch(id: String) {
        _state.update { it.copy(roastBatches = it.roastBatches.filterNot { r -> r.id == id }) }
    }

    fun parseBagOcr(photoUri: String, text: String) {
        val draft = OfflineBagOcrParser.parseBagText(photoUri, text)
        _state.update { it.copy(pendingOcrDraft = draft) }
    }

    fun confirmOcrDraft(confirmedBean: DetailedBeanRecord) {
        addDetailedBean(confirmedBean)
        _state.update { it.copy(pendingOcrDraft = null) }
    }

    fun dismissOcrDraft() {
        _state.update { it.copy(pendingOcrDraft = null) }
    }

    fun previewCatalogImport(jsonContent: String): ImportDryRunSummary {
        val preview = CatalogImportExportEngine.previewImport(
            jsonContent,
            _state.value.detailedBeans,
            _state.value.equipmentCatalog
        )
        _state.update { it.copy(catalogImportPreview = preview) }
        return preview
    }

    fun executeCatalogImport(jsonContent: String, strategy: ConflictResolutionStrategy): ImportExecutionReceipt {
        val (receipt, updatedCollections) = CatalogImportExportEngine.executeImport(
            jsonContent,
            strategy,
            _state.value.detailedBeans,
            _state.value.equipmentCatalog
        )
        _state.update {
            it.copy(
                detailedBeans = updatedCollections.first,
                equipmentCatalog = updatedCollections.second,
                catalogImportPreview = null
            )
        }
        diagnosticBuffer.log(LogLevel.INFO, "IMPORT", "Catalog import completed: created=${receipt.createdCount}, updated=${receipt.updatedCount}, renamed=${receipt.renamedCount}, skipped=${receipt.skippedCount}")
        return receipt
    }

    fun exportCatalogJson(): String {
        val payload = CatalogExportPayload(
            beans = _state.value.detailedBeans,
            grinders = _state.value.grinders,
            equipment = _state.value.equipmentCatalog,
            waters = _state.value.waterProfiles,
            ingredients = _state.value.foodGradeIngredients
        )
        return CatalogImportExportEngine.exportToJson(payload)
    }

    fun exportBeansCsv(): String {
        return CatalogImportExportEngine.exportBeansToCsv(_state.value.detailedBeans)
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
        scaleJob?.cancel()
        super.onCleared()
    }
}
