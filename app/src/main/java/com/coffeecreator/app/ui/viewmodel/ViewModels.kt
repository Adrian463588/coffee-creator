package com.coffeecreator.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coffeecreator.app.core.ai.HybridRagEngine
import com.coffeecreator.app.core.ai.LocalLlmCoordinator
import com.coffeecreator.app.core.ai.ToolExecutor
import com.coffeecreator.app.core.data.DataRepository
import com.coffeecreator.app.core.engine.CoffeeScienceEngine
import com.coffeecreator.app.core.engine.SensoryRulesEngine
import com.coffeecreator.app.core.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

// ==========================================
// 1. HOME VIEWMODEL
// ==========================================
data class HomeUiState(
    val recipes: List<Recipe> = emptyList(),
    val beanCount: Int = 0,
    val isWorkstationReady: Boolean = true
)

sealed interface HomeIntent {
    data object Refresh : HomeIntent
}

class HomeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            DataRepository.recipes.collect { list ->
                _uiState.update { it.copy(recipes = list) }
            }
        }
        viewModelScope.launch {
            DataRepository.beans.collect { beans ->
                _uiState.update { it.copy(beanCount = beans.size) }
            }
        }
    }

    fun dispatch(intent: HomeIntent) {
        when (intent) {
            HomeIntent.Refresh -> {
                _uiState.update {
                    it.copy(
                        recipes = DataRepository.recipes.value,
                        beanCount = DataRepository.beans.value.size
                    )
                }
            }
        }
    }
}

// ==========================================
// 2. RECIPE BUILDER VIEWMODEL
// ==========================================
data class RecipeBuilderUiState(
    val recipeTitle: String = "Custom V60 Formulation",
    val selectedBeanId: String = "",
    val brewMethod: String = "Pour Over (V60)",
    val doseInput: String = "18.0",
    val waterInput: String = "300.0",
    val beverageInput: String = "270.0",
    val bypassInput: String = "0.0",
    val agitationInput: String = "Gentle spiral pour",
    val tdsInput: String = "1.30",
    val brewSteps: List<BrewStep> = listOf(
        BrewStep(1, "Bloom with 50g water (swirl)", 50.0, 45),
        BrewStep(2, "Pour to 150g in gentle circles", 150.0, 45),
        BrewStep(3, "Pour to 300g center draw-down", 300.0, 90)
    ),
    val timerRunning: Boolean = false,
    val timerSeconds: Int = 0,
    val ratioResult: Result<CalculationResult.BrewRatio>? = null,
    val eyResult: Result<CalculationResult.ExtractionYield>? = null,
    val saveMessage: String? = null
)

sealed interface RecipeBuilderIntent {
    data class UpdateTitle(val title: String) : RecipeBuilderIntent
    data class SelectBean(val beanId: String) : RecipeBuilderIntent
    data class UpdateMethod(val method: String) : RecipeBuilderIntent
    data class UpdateDose(val dose: String) : RecipeBuilderIntent
    data class UpdateWater(val water: String) : RecipeBuilderIntent
    data class UpdateBeverage(val beverage: String) : RecipeBuilderIntent
    data class UpdateBypass(val bypass: String) : RecipeBuilderIntent
    data class UpdateAgitation(val agitation: String) : RecipeBuilderIntent
    data class UpdateTds(val tds: String) : RecipeBuilderIntent
    data object ToggleTimer : RecipeBuilderIntent
    data object ResetTimer : RecipeBuilderIntent
    data object SaveRecipe : RecipeBuilderIntent
}

class RecipeBuilderViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(RecipeBuilderUiState())
    val uiState: StateFlow<RecipeBuilderUiState> = _uiState.asStateFlow()
    private var timerJob: kotlinx.coroutines.Job? = null

    init {
        recalculate()
    }

    fun dispatch(intent: RecipeBuilderIntent) {
        when (intent) {
            is RecipeBuilderIntent.UpdateTitle -> {
                _uiState.update { it.copy(recipeTitle = intent.title, saveMessage = null) }
            }
            is RecipeBuilderIntent.SelectBean -> {
                _uiState.update { it.copy(selectedBeanId = intent.beanId, saveMessage = null) }
            }
            is RecipeBuilderIntent.UpdateMethod -> {
                _uiState.update { it.copy(brewMethod = intent.method, saveMessage = null) }
            }
            is RecipeBuilderIntent.UpdateDose -> {
                _uiState.update { it.copy(doseInput = intent.dose, saveMessage = null) }
                recalculate()
            }
            is RecipeBuilderIntent.UpdateWater -> {
                _uiState.update { it.copy(waterInput = intent.water, saveMessage = null) }
                recalculate()
            }
            is RecipeBuilderIntent.UpdateBeverage -> {
                _uiState.update { it.copy(beverageInput = intent.beverage, saveMessage = null) }
                recalculate()
            }
            is RecipeBuilderIntent.UpdateBypass -> {
                _uiState.update { it.copy(bypassInput = intent.bypass, saveMessage = null) }
            }
            is RecipeBuilderIntent.UpdateAgitation -> {
                _uiState.update { it.copy(agitationInput = intent.agitation, saveMessage = null) }
            }
            is RecipeBuilderIntent.UpdateTds -> {
                _uiState.update { it.copy(tdsInput = intent.tds, saveMessage = null) }
                recalculate()
            }
            is RecipeBuilderIntent.ToggleTimer -> {
                val running = _uiState.value.timerRunning
                if (running) {
                    timerJob?.cancel()
                    _uiState.update { it.copy(timerRunning = false) }
                } else {
                    _uiState.update { it.copy(timerRunning = true) }
                    timerJob = viewModelScope.launch {
                        while (true) {
                            kotlinx.coroutines.delay(1000)
                            _uiState.update { it.copy(timerSeconds = it.timerSeconds + 1) }
                        }
                    }
                }
            }
            is RecipeBuilderIntent.ResetTimer -> {
                timerJob?.cancel()
                _uiState.update { it.copy(timerRunning = false, timerSeconds = 0) }
            }
            is RecipeBuilderIntent.SaveRecipe -> {
                saveCurrentRecipe()
            }
        }
    }

    private fun recalculate() {
        val s = _uiState.value
        val dose = s.doseInput.toDoubleOrNull() ?: 0.0
        val water = s.waterInput.toDoubleOrNull() ?: 0.0
        val beverage = s.beverageInput.toDoubleOrNull() ?: 0.0
        val tds = s.tdsInput.toDoubleOrNull() ?: 0.0

        val ratio = CoffeeScienceEngine.calculateBrewRatio(dose, water)
        val ey = CoffeeScienceEngine.calculateExtractionYield(dose, beverage, tds)

        _uiState.update { it.copy(ratioResult = ratio, eyResult = ey) }
    }

    private fun saveCurrentRecipe() {
        val s = _uiState.value
        val dose = s.doseInput.toDoubleOrNull() ?: 0.0
        val water = s.waterInput.toDoubleOrNull() ?: 0.0
        val beverage = s.beverageInput.toDoubleOrNull() ?: 0.0
        val bypass = s.bypassInput.toDoubleOrNull() ?: 0.0
        val tds = s.tdsInput.toDoubleOrNull()

        val recipe = Recipe(
            id = "",
            name = s.recipeTitle,
            beanId = s.selectedBeanId.ifBlank { null },
            brewMethod = s.brewMethod,
            doseGrams = dose,
            waterGrams = water,
            beverageMassGrams = beverage,
            bypassGrams = bypass,
            agitation = s.agitationInput,
            brewSteps = s.brewSteps,
            brewTimeSeconds = if (s.timerSeconds > 0) s.timerSeconds else 180,
            measuredTdsPercent = if (tds != null && tds > 0.0) tds else null,
            targetRatio = s.ratioResult?.getOrNull()?.ratioString ?: "1:16.67"
        )
        val saved = DataRepository.saveRecipe(recipe)
        _uiState.update { it.copy(saveMessage = "Saved '${saved.name}' (v${saved.version}) successfully!") }
    }
}

// ==========================================
// 3. COFFEE LIBRARY VIEWMODEL
// ==========================================
data class CoffeeLibraryUiState(
    val beans: List<CoffeeBean> = emptyList(),
    val searchQuery: String = "",
    val selectedProcessFilter: String? = null,
    val showAddDialog: Boolean = false
)

sealed interface CoffeeLibraryIntent {
    data class Search(val query: String) : CoffeeLibraryIntent
    data class FilterProcess(val process: String?) : CoffeeLibraryIntent
    data class AddBean(
        val name: String,
        val country: String,
        val process: String,
        val roast: String,
        val roastDate: String? = null,
        val degasDays: Int? = null,
        val blendComponents: List<BlendComponent> = emptyList()
    ) : CoffeeLibraryIntent
    data class ToggleAddDialog(val show: Boolean) : CoffeeLibraryIntent
}

class CoffeeLibraryViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(CoffeeLibraryUiState())
    val uiState: StateFlow<CoffeeLibraryUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            DataRepository.beans.collect { list ->
                _uiState.update { it.copy(beans = list) }
            }
        }
    }

    fun dispatch(intent: CoffeeLibraryIntent) {
        when (intent) {
            is CoffeeLibraryIntent.Search -> _uiState.update { it.copy(searchQuery = intent.query) }
            is CoffeeLibraryIntent.FilterProcess -> _uiState.update { it.copy(selectedProcessFilter = intent.process) }
            is CoffeeLibraryIntent.ToggleAddDialog -> _uiState.update { it.copy(showAddDialog = intent.show) }
            is CoffeeLibraryIntent.AddBean -> {
                val newBean = CoffeeBean(
                    id = UUID.randomUUID().toString(),
                    name = intent.name,
                    country = intent.country,
                    region = "Custom Region",
                    process = intent.process,
                    roastLevel = intent.roast,
                    roastDate = intent.roastDate,
                    degasDays = intent.degasDays,
                    blendComponents = intent.blendComponents,
                    isCuratedSeed = false
                )
                DataRepository.addBean(newBean)
                _uiState.update { it.copy(showAddDialog = false) }
            }
        }
    }
}

// ==========================================
// 4. SENSORY VIEWMODEL
// ==========================================
data class SensoryUiState(
    val recipes: List<Recipe> = emptyList(),
    val selectedRecipeId: String = "",
    val aroma: Float = 8f,
    val acidity: Float = 7f,
    val sweetness: Float = 8f,
    val bitterness: Float = 3f,
    val body: Float = 6f,
    val clarity: Float = 8f,
    val finish: Float = 7f,
    val balance: Float = 8f,
    val notes: String = "Clean floral fragrance, crisp malic acidity, cane sugar sweetness.",
    val estimate: SensoryEstimate? = null,
    val saveSuccessMsg: String? = null
)

sealed interface SensoryIntent {
    data class SelectRecipe(val recipeId: String) : SensoryIntent
    data class UpdateAroma(val value: Float) : SensoryIntent
    data class UpdateAcidity(val value: Float) : SensoryIntent
    data class UpdateSweetness(val value: Float) : SensoryIntent
    data class UpdateBitterness(val value: Float) : SensoryIntent
    data class UpdateBody(val value: Float) : SensoryIntent
    data class UpdateClarity(val value: Float) : SensoryIntent
    data class UpdateFinish(val value: Float) : SensoryIntent
    data class UpdateBalance(val value: Float) : SensoryIntent
    data class UpdateNotes(val notes: String) : SensoryIntent
    data object SaveObservation : SensoryIntent
}

class SensoryViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(SensoryUiState())
    val uiState: StateFlow<SensoryUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            DataRepository.recipes.collect { recs ->
                _uiState.update { state ->
                    val chosenId = if (state.selectedRecipeId.isBlank()) recs.firstOrNull()?.id ?: "" else state.selectedRecipeId
                    state.copy(recipes = recs, selectedRecipeId = chosenId)
                }
                updateEstimate()
            }
        }
    }

    fun dispatch(intent: SensoryIntent) {
        when (intent) {
            is SensoryIntent.SelectRecipe -> {
                _uiState.update { it.copy(selectedRecipeId = intent.recipeId, saveSuccessMsg = null) }
                updateEstimate()
            }
            is SensoryIntent.UpdateAroma -> _uiState.update { it.copy(aroma = intent.value) }
            is SensoryIntent.UpdateAcidity -> _uiState.update { it.copy(acidity = intent.value) }
            is SensoryIntent.UpdateSweetness -> _uiState.update { it.copy(sweetness = intent.value) }
            is SensoryIntent.UpdateBitterness -> _uiState.update { it.copy(bitterness = intent.value) }
            is SensoryIntent.UpdateBody -> _uiState.update { it.copy(body = intent.value) }
            is SensoryIntent.UpdateClarity -> _uiState.update { it.copy(clarity = intent.value) }
            is SensoryIntent.UpdateFinish -> _uiState.update { it.copy(finish = intent.value) }
            is SensoryIntent.UpdateBalance -> _uiState.update { it.copy(balance = intent.value) }
            is SensoryIntent.UpdateNotes -> _uiState.update { it.copy(notes = intent.notes) }
            is SensoryIntent.SaveObservation -> {
                val s = _uiState.value
                val rec = s.recipes.find { it.id == s.selectedRecipeId }
                if (rec != null) {
                    val obs = SensoryObservation(
                        id = UUID.randomUUID().toString(),
                        recipeId = rec.id,
                        aroma = s.aroma.toInt(),
                        acidity = s.acidity.toInt(),
                        sweetness = s.sweetness.toInt(),
                        bitterness = s.bitterness.toInt(),
                        body = s.body.toInt(),
                        clarity = s.clarity.toInt(),
                        finish = s.finish.toInt(),
                        balance = s.balance.toInt(),
                        notes = s.notes
                    )
                    DataRepository.saveSensoryObservation(obs)
                    _uiState.update { it.copy(saveSuccessMsg = "Saved cupping observation for '${rec.name}'.") }
                }
            }
        }
    }

    private fun updateEstimate() {
        val s = _uiState.value
        val rec = s.recipes.find { it.id == s.selectedRecipeId }
        val bean = DataRepository.findBeanById(rec?.beanId)
        if (rec != null) {
            val ey = (rec.beverageMassGrams * (rec.measuredTdsPercent ?: 1.30)) / rec.doseGrams
            val est = SensoryRulesEngine.estimateProfile(bean, rec, ey)
            _uiState.update { it.copy(estimate = est) }
        }
    }
}

// ==========================================
// 5. AI CHAT VIEWMODEL
// ==========================================
data class AiChatUiState(
    val personas: List<AssistantPersona> = emptyList(),
    val selectedPersona: AssistantPersona = DataRepository.selectedPersona.value,
    val messages: List<ChatMessage> = listOf(
        ChatMessage(
            id = "msg-0",
            role = "assistant",
            content = "Hello! I am your on-device coffee assistant. I operate 100% offline, use deterministic Kotlin math tools, and cite verified local brewing documents."
        )
    ),
    val inputText: String = "",
    val isGenerating: Boolean = false,
    val pendingRecipeWrite: Map<String, Any?>? = null
)

sealed interface AiChatIntent {
    data class SelectPersona(val persona: AssistantPersona) : AiChatIntent
    data class UpdateInput(val text: String) : AiChatIntent
    data object SendMessage : AiChatIntent
    data object ConfirmWriteRecipe : AiChatIntent
    data object CancelWriteRecipe : AiChatIntent
}

class AiChatViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(AiChatUiState())
    val uiState: StateFlow<AiChatUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            DataRepository.personas.collect { pList ->
                _uiState.update { it.copy(personas = pList) }
            }
        }
        viewModelScope.launch {
            DataRepository.selectedPersona.collect { sel ->
                _uiState.update { it.copy(selectedPersona = sel) }
            }
        }
    }

    fun dispatch(intent: AiChatIntent) {
        when (intent) {
            is AiChatIntent.SelectPersona -> {
                DataRepository.selectPersona(intent.persona)
            }
            is AiChatIntent.UpdateInput -> {
                _uiState.update { it.copy(inputText = intent.text) }
            }
            is AiChatIntent.SendMessage -> {
                val prompt = _uiState.value.inputText
                if (prompt.isBlank() || _uiState.value.isGenerating) return

                val userMsg = ChatMessage(id = UUID.randomUUID().toString(), role = "user", content = prompt)
                val botMsgId = UUID.randomUUID().toString()
                val initialBot = ChatMessage(id = botMsgId, role = "assistant", content = "")

                _uiState.update {
                    it.copy(
                        inputText = "",
                        messages = it.messages + userMsg + initialBot,
                        isGenerating = true
                    )
                }

                viewModelScope.launch {
                    val sb = StringBuilder()
                    LocalLlmCoordinator.streamChatResponse(prompt, _uiState.value.selectedPersona).collect { token ->
                        sb.append(token)
                        _uiState.update { s ->
                            s.copy(
                                messages = s.messages.map { m ->
                                    if (m.id == botMsgId) m.copy(content = sb.toString()) else m
                                }
                            )
                        }
                    }
                    _uiState.update { it.copy(isGenerating = false) }
                }
            }
            is AiChatIntent.ConfirmWriteRecipe -> {
                val pending = _uiState.value.pendingRecipeWrite
                if (pending != null) {
                    val name = pending["name"] as? String ?: "AI Proposed Recipe"
                    val dose = (pending["dose"] as? Number)?.toDouble() ?: 18.0
                    val water = (pending["water"] as? Number)?.toDouble() ?: 300.0
                    val method = pending["method"] as? String ?: "V60"

                    val recipe = Recipe(
                        id = UUID.randomUUID().toString(),
                        name = name,
                        doseGrams = dose,
                        waterGrams = water,
                        brewMethod = method,
                        beverageMassGrams = water * 0.9,
                        measuredTdsPercent = 1.30
                    )
                    DataRepository.saveRecipe(recipe)
                    val confirmMsg = ChatMessage(
                        id = UUID.randomUUID().toString(),
                        role = "assistant",
                        content = "Confirmed and saved recipe '$name' to your local database!"
                    )
                    _uiState.update { it.copy(messages = it.messages + confirmMsg, pendingRecipeWrite = null) }
                }
            }
            is AiChatIntent.CancelWriteRecipe -> {
                _uiState.update { it.copy(pendingRecipeWrite = null) }
            }
        }
    }
}
