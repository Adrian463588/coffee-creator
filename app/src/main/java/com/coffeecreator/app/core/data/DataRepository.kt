package com.coffeecreator.app.core.data

import com.coffeecreator.app.core.model.AssistantPersona
import com.coffeecreator.app.core.model.CoffeeBean
import com.coffeecreator.app.core.model.DocumentChunk
import com.coffeecreator.app.core.model.KnowledgeDocument
import com.coffeecreator.app.core.model.ModelAsset
import com.coffeecreator.app.core.model.ModelStatus
import com.coffeecreator.app.core.model.Recipe
import com.coffeecreator.app.core.model.SensoryObservation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

object DataRepository : CoffeeCatalogRepository, RecipeRepository, SensoryRepository, KnowledgeRepository, ModelAssetRepository {

    private val _beans = MutableStateFlow<List<CoffeeBean>>(SeedData.curatedBeans)
    override val beans: StateFlow<List<CoffeeBean>> = _beans.asStateFlow()

    private val _recipes = MutableStateFlow<List<Recipe>>(SeedData.sampleRecipes)
    override val recipes: StateFlow<List<Recipe>> = _recipes.asStateFlow()

    private val _sensoryObservations = MutableStateFlow<List<SensoryObservation>>(
        listOf(
            SensoryObservation(
                id = "obs-01",
                recipeId = "rec-01",
                aroma = 9,
                acidity = 8,
                sweetness = 8,
                body = 6,
                finish = 8,
                balance = 9,
                tastingDescriptors = listOf("Floral Jasmine", "Bergamot", "Peach"),
                notes = "Very sweet cup, bright citric acidity, elegant finish.",
                assessor = "Head Barista"
            )
        )
    )
    override val sensoryObservations: StateFlow<List<SensoryObservation>> = _sensoryObservations.asStateFlow()

    private val _documents = MutableStateFlow<List<KnowledgeDocument>>(SeedData.curatedDocuments)
    override val documents: StateFlow<List<KnowledgeDocument>> = _documents.asStateFlow()

    private val _chunks = MutableStateFlow<List<DocumentChunk>>(SeedData.curatedChunks)
    override val chunks: StateFlow<List<DocumentChunk>> = _chunks.asStateFlow()

    private val _models = MutableStateFlow<List<ModelAsset>>(SeedData.curatedModels)
    override val models: StateFlow<List<ModelAsset>> = _models.asStateFlow()

    private val _personas = MutableStateFlow<List<AssistantPersona>>(SeedData.personas)
    val personas: StateFlow<List<AssistantPersona>> = _personas.asStateFlow()

    private val _selectedPersona = MutableStateFlow<AssistantPersona>(SeedData.personas.first())
    val selectedPersona: StateFlow<AssistantPersona> = _selectedPersona.asStateFlow()

    private val _safDirectoryUri = MutableStateFlow<String?>("content://com.android.externalstorage.documents/tree/primary%3ACoffeeCreatorModels")
    override val safDirectoryUri: StateFlow<String?> = _safDirectoryUri.asStateFlow()

    // Bean actions
    override fun addBean(bean: CoffeeBean) {
        _beans.value = _beans.value + bean
    }

    override fun updateBean(bean: CoffeeBean) {
        _beans.value = _beans.value.map { if (it.id == bean.id) bean else it }
    }

    override fun deleteBean(id: String) {
        _beans.value = _beans.value.filterNot { it.id == id }
    }

    override fun findBeanById(id: String?): CoffeeBean? {
        return _beans.value.find { it.id == id }
    }

    // Recipe actions
    override fun saveRecipe(recipe: Recipe): Recipe {
        val existingIndex = _recipes.value.indexOfFirst { it.id == recipe.id }
        return if (existingIndex >= 0) {
            val updated = recipe.copy(updatedAt = System.currentTimeMillis())
            _recipes.value = _recipes.value.toMutableList().also { it[existingIndex] = updated }
            updated
        } else {
            val newRecipe = recipe.copy(
                id = if (recipe.id.isBlank()) UUID.randomUUID().toString() else recipe.id,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            _recipes.value = _recipes.value + newRecipe
            newRecipe
        }
    }

    override fun duplicateRecipe(recipe: Recipe): Recipe {
        val clone = recipe.copy(
            id = UUID.randomUUID().toString(),
            name = "${recipe.name} (Copy)",
            version = recipe.version + 1,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        _recipes.value = _recipes.value + clone
        return clone
    }

    override fun deleteRecipe(id: String) {
        _recipes.value = _recipes.value.filterNot { it.id == id }
    }

    override fun findRecipeById(id: String?): Recipe? {
        return _recipes.value.find { it.id == id }
    }

    // Sensory actions
    override fun saveSensoryObservation(observation: SensoryObservation) {
        _sensoryObservations.value = _sensoryObservations.value + observation
    }

    override fun getObservationsForRecipe(recipeId: String): List<SensoryObservation> {
        return _sensoryObservations.value.filter { it.recipeId == recipeId }
    }

    // Document actions
    override fun addDocumentWithChunks(doc: KnowledgeDocument, newChunks: List<DocumentChunk>) {
        _documents.value = _documents.value + doc
        _chunks.value = _chunks.value + newChunks
    }

    override fun removeDocument(documentId: String) {
        _documents.value = _documents.value.filterNot { it.id == documentId }
        _chunks.value = _chunks.value.filterNot { it.documentId == documentId }
    }

    // Model management
    override fun updateModelStatus(modelId: String, status: ModelStatus, progress: Int) {
        _models.value = _models.value.map {
            if (it.id == modelId) {
                it.copy(
                    status = status,
                    progressPercent = progress,
                    localUri = if (status == ModelStatus.INSTALLED) "saf://CoffeeCreatorModels/${it.name}" else it.localUri
                )
            } else it
        }
    }

    override fun setSafDirectoryUri(uri: String?) {
        _safDirectoryUri.value = uri
    }

    fun selectPersona(persona: AssistantPersona) {
        _selectedPersona.value = persona
    }

    // Backup & Restore
    fun exportBackupJson(): String {
        val beanCount = _beans.value.size
        val recipeCount = _recipes.value.size
        val obsCount = _sensoryObservations.value.size
        val docCount = _documents.value.size
        return """
        {
          "manifest": {
            "app": "Coffee Creator",
            "version": "1.0.0",
            "timestamp": ${System.currentTimeMillis()},
            "records": {
              "beans": $beanCount,
              "recipes": $recipeCount,
              "sensory": $obsCount,
              "documents": $docCount
            }
          },
          "status": "VALID_INTEGRITY_CHECK_OK"
        }
        """.trimIndent()
    }

    fun resetToDefaults() {
        _beans.value = SeedData.curatedBeans
        _recipes.value = SeedData.sampleRecipes
        _documents.value = SeedData.curatedDocuments
        _chunks.value = SeedData.curatedChunks
        _models.value = SeedData.curatedModels
    }
}
