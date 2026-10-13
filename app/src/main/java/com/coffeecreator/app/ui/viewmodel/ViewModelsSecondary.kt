package com.coffeecreator.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coffeecreator.app.core.ai.HybridRagEngine
import com.coffeecreator.app.core.data.DataRepository
import com.coffeecreator.app.core.model.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ==========================================
// 6. KNOWLEDGE VIEWMODEL
// ==========================================
data class KnowledgeUiState(
    val documents: List<KnowledgeDocument> = emptyList(),
    val chunks: List<DocumentChunk> = emptyList(),
    val searchQuery: String = "",
    val searchResultText: String? = null,
    val citations: List<String> = emptyList(),
    val showImportDialog: Boolean = false
)

sealed interface KnowledgeIntent {
    data class UpdateQuery(val query: String) : KnowledgeIntent
    data object ExecuteSearch : KnowledgeIntent
    data class IngestDocument(val title: String, val content: String) : KnowledgeIntent
    data class ToggleImportDialog(val show: Boolean) : KnowledgeIntent
}

class KnowledgeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(KnowledgeUiState())
    val uiState: StateFlow<KnowledgeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            DataRepository.documents.collect { docs ->
                _uiState.update { it.copy(documents = docs) }
            }
        }
        viewModelScope.launch {
            DataRepository.chunks.collect { cList ->
                _uiState.update { it.copy(chunks = cList) }
            }
        }
    }

    fun dispatch(intent: KnowledgeIntent) {
        when (intent) {
            is KnowledgeIntent.UpdateQuery -> _uiState.update { it.copy(searchQuery = intent.query) }
            is KnowledgeIntent.ExecuteSearch -> {
                val q = _uiState.value.searchQuery
                if (q.isNotBlank()) {
                    val res = HybridRagEngine.search(q)
                    _uiState.update { it.copy(searchResultText = res.text, citations = res.citations) }
                }
            }
            is KnowledgeIntent.ToggleImportDialog -> _uiState.update { it.copy(showImportDialog = intent.show) }
            is KnowledgeIntent.IngestDocument -> {
                HybridRagEngine.ingestDocument(intent.title, intent.content)
                _uiState.update { it.copy(showImportDialog = false) }
            }
        }
    }
}

// ==========================================
// 7. MODEL MANAGER VIEWMODEL
// ==========================================
data class ModelManagerUiState(
    val models: List<ModelAsset> = emptyList(),
    val safDirectoryUri: String? = null
)

sealed interface ModelManagerIntent {
    data class InstallModel(val modelId: String) : ModelManagerIntent
    data class PauseModel(val modelId: String) : ModelManagerIntent
    data class UninstallModel(val modelId: String) : ModelManagerIntent
    data class SelectSafDirectory(val uri: String) : ModelManagerIntent
}

class ModelManagerViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ModelManagerUiState())
    val uiState: StateFlow<ModelManagerUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            DataRepository.models.collect { mList ->
                _uiState.update { it.copy(models = mList) }
            }
        }
        viewModelScope.launch {
            DataRepository.safDirectoryUri.collect { uri ->
                _uiState.update { it.copy(safDirectoryUri = uri) }
            }
        }
    }

    fun dispatch(intent: ModelManagerIntent) {
        when (intent) {
            is ModelManagerIntent.SelectSafDirectory -> DataRepository.setSafDirectoryUri(intent.uri)
            is ModelManagerIntent.PauseModel -> {
                val model = _uiState.value.models.find { it.id == intent.modelId }
                DataRepository.updateModelStatus(intent.modelId, ModelStatus.PAUSED, model?.progressPercent ?: 0)
            }
            is ModelManagerIntent.UninstallModel -> {
                DataRepository.updateModelStatus(intent.modelId, ModelStatus.CATALOGUED, 0)
            }
            is ModelManagerIntent.InstallModel -> {
                DataRepository.updateModelStatus(intent.modelId, ModelStatus.DOWNLOADING, 10)
                viewModelScope.launch {
                    for (p in 20..100 step 20) {
                        delay(150)
                        DataRepository.updateModelStatus(
                            intent.modelId,
                            if (p == 100) ModelStatus.INSTALLED else ModelStatus.DOWNLOADING,
                            p
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// 8. BREW JOURNAL VIEWMODEL
// ==========================================
data class BrewJournalUiState(
    val recipes: List<Recipe> = emptyList(),
    val observations: List<SensoryObservation> = emptyList()
)

sealed interface BrewJournalIntent {
    data class CloneRecipe(val recipe: Recipe) : BrewJournalIntent
    data class DeleteRecipe(val recipeId: String) : BrewJournalIntent
}

class BrewJournalViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(BrewJournalUiState())
    val uiState: StateFlow<BrewJournalUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            DataRepository.recipes.collect { rList ->
                _uiState.update { it.copy(recipes = rList) }
            }
        }
        viewModelScope.launch {
            DataRepository.sensoryObservations.collect { oList ->
                _uiState.update { it.copy(observations = oList) }
            }
        }
    }

    fun dispatch(intent: BrewJournalIntent) {
        when (intent) {
            is BrewJournalIntent.CloneRecipe -> DataRepository.duplicateRecipe(intent.recipe)
            is BrewJournalIntent.DeleteRecipe -> DataRepository.deleteRecipe(intent.recipeId)
        }
    }
}

// ==========================================
// 9. SETTINGS VIEWMODEL
// ==========================================
data class SettingsUiState(
    val isDarkTheme: Boolean = false,
    val exportJson: String? = null,
    val resetConfirmMsg: String? = null
)

sealed interface SettingsIntent {
    data class ToggleDarkTheme(val enabled: Boolean) : SettingsIntent
    data object ExportBackup : SettingsIntent
    data object ResetCatalog : SettingsIntent
}

class SettingsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun dispatch(intent: SettingsIntent) {
        when (intent) {
            is SettingsIntent.ToggleDarkTheme -> _uiState.update { it.copy(isDarkTheme = intent.enabled) }
            is SettingsIntent.ExportBackup -> {
                val json = DataRepository.exportBackupJson()
                _uiState.update { it.copy(exportJson = json) }
            }
            is SettingsIntent.ResetCatalog -> {
                DataRepository.resetToDefaults()
                _uiState.update { it.copy(resetConfirmMsg = "Database reset to curated seed defaults.") }
            }
        }
    }
}
