package com.coffeecreator.app.core.data

import com.coffeecreator.app.core.model.*
import kotlinx.coroutines.flow.StateFlow

interface CoffeeCatalogRepository {
    val beans: StateFlow<List<CoffeeBean>>
    fun addBean(bean: CoffeeBean)
    fun updateBean(bean: CoffeeBean)
    fun deleteBean(id: String)
    fun findBeanById(id: String?): CoffeeBean?
}

interface RecipeRepository {
    val recipes: StateFlow<List<Recipe>>
    fun saveRecipe(recipe: Recipe): Recipe
    fun duplicateRecipe(recipe: Recipe): Recipe
    fun deleteRecipe(id: String)
    fun findRecipeById(id: String?): Recipe?
}

interface SensoryRepository {
    val sensoryObservations: StateFlow<List<SensoryObservation>>
    fun saveSensoryObservation(observation: SensoryObservation)
    fun getObservationsForRecipe(recipeId: String): List<SensoryObservation>
}

interface KnowledgeRepository {
    val documents: StateFlow<List<KnowledgeDocument>>
    val chunks: StateFlow<List<DocumentChunk>>
    fun addDocumentWithChunks(doc: KnowledgeDocument, newChunks: List<DocumentChunk>)
    fun removeDocument(documentId: String)
}

interface ModelAssetRepository {
    val models: StateFlow<List<ModelAsset>>
    val safDirectoryUri: StateFlow<String?>
    fun updateModelStatus(modelId: String, status: ModelStatus, progress: Int = 0)
    fun setSafDirectoryUri(uri: String?)
}
