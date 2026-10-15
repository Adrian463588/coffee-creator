package com.coffeecreator.app.core.data

import com.coffeecreator.app.core.model.CoffeeBean
import com.coffeecreator.app.core.model.DocumentChunk
import com.coffeecreator.app.core.model.Ingredient
import com.coffeecreator.app.core.model.KnowledgeDocument
import com.coffeecreator.app.core.model.ModelAsset
import com.coffeecreator.app.core.model.Recipe
import com.coffeecreator.app.core.model.SensoryObservation
import com.coffeecreator.app.core.model.StoredSensoryEstimate
import kotlinx.coroutines.flow.StateFlow

/** Domain-facing contracts contain no Android, Room, HTTP or UI types. */
interface CoffeeCatalogRepository {
    val beans: StateFlow<List<CoffeeBean>>
    val ingredients: StateFlow<List<Ingredient>>
    suspend fun addBean(bean: CoffeeBean)
    suspend fun updateBean(bean: CoffeeBean)
    suspend fun deleteBean(id: String)
    suspend fun addIngredient(ingredient: Ingredient)
    suspend fun updateIngredient(ingredient: Ingredient)
    fun findBeanById(id: String?): CoffeeBean?
}

interface RecipeRepository {
    val recipes: StateFlow<List<Recipe>>
    suspend fun saveRecipe(recipe: Recipe): Recipe
    suspend fun duplicateRecipe(recipe: Recipe): Recipe
    suspend fun archiveRecipe(id: String, isArchived: Boolean): Recipe
    suspend fun deleteRecipe(id: String)
    fun findRecipeById(id: String?): Recipe?
}

interface SensoryRepository {
    val sensoryObservations: StateFlow<List<SensoryObservation>>
    suspend fun saveSensoryObservation(observation: SensoryObservation)
    fun getObservationsForRecipe(recipeId: String): List<SensoryObservation>
    val sensoryEstimates: StateFlow<List<StoredSensoryEstimate>>
    suspend fun saveSensoryEstimate(estimate: StoredSensoryEstimate)
}

interface KnowledgeRepository {
    val documents: StateFlow<List<KnowledgeDocument>>
    val chunks: StateFlow<List<DocumentChunk>>
    suspend fun addDocumentWithChunks(doc: KnowledgeDocument, newChunks: List<DocumentChunk>): KnowledgeDocument
    suspend fun removeDocument(documentId: String)
    suspend fun lexicalSearch(query: String, limit: Int = 5): List<DocumentChunk>
}

interface ModelAssetRepository {
    val models: StateFlow<List<ModelAsset>>
    val safDirectoryUri: StateFlow<String?>
    suspend fun upsertModel(model: ModelAsset)
    suspend fun setSafDirectoryUri(uri: String?)
}
