package com.coffeecreator.app.core.data

import android.content.Context
import androidx.room.withTransaction
import com.coffeecreator.app.core.engine.DomainValidation
import com.coffeecreator.app.core.model.AssistantPersona
import com.coffeecreator.app.core.model.ChatMessage
import com.coffeecreator.app.core.model.CoffeeBean
import com.coffeecreator.app.core.model.DocumentChunk
import com.coffeecreator.app.core.model.Ingredient
import com.coffeecreator.app.core.model.InputLimits
import com.coffeecreator.app.core.model.KnowledgeDocument
import com.coffeecreator.app.core.model.ModelAsset
import com.coffeecreator.app.core.model.ModelStatus
import com.coffeecreator.app.core.model.Recipe
import com.coffeecreator.app.core.model.SensoryObservation
import com.coffeecreator.app.core.model.StoredSensoryEstimate
import com.coffeecreator.app.core.sensory.cva.CuppingSession
import com.coffeecreator.app.core.sensory.cva.SessionState
import com.google.gson.reflect.TypeToken
import java.util.UUID
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

/** One application-owned instance. All durable reads/writes run off the UI thread. */
class DataRepository(context: Context, private val database: CoffeeDatabase = CoffeeDatabase.open(context)) :
    CoffeeCatalogRepository,
    RecipeRepository,
    SensoryRepository,
    KnowledgeRepository,
    ModelAssetRepository,
    CvaSessionRepository {
    private companion object {
        const val SQL_BIND_BATCH = 500
    }
    private val mutex = Mutex()
    private val dao = database.dao()
    private val _ready = MutableStateFlow(false)
    val ready: StateFlow<Boolean> = _ready.asStateFlow()
    private val _errorState = MutableStateFlow<String?>(null)
    val errorState: StateFlow<String?> = _errorState.asStateFlow()
    fun reportFailure(failure: Throwable) {
        _errorState.value = failure.message ?: "Local storage operation failed; records are retained."
    }

    private val _beans = MutableStateFlow<List<CoffeeBean>>(emptyList())
    override val beans: StateFlow<List<CoffeeBean>> = _beans.asStateFlow()
    private val _ingredients = MutableStateFlow<List<Ingredient>>(emptyList())
    override val ingredients: StateFlow<List<Ingredient>> = _ingredients.asStateFlow()
    private val _recipes = MutableStateFlow<List<Recipe>>(emptyList())
    override val recipes: StateFlow<List<Recipe>> = _recipes.asStateFlow()
    private val _sensoryObservations = MutableStateFlow<List<SensoryObservation>>(emptyList())
    override val sensoryObservations: StateFlow<List<SensoryObservation>> = _sensoryObservations.asStateFlow()
    private val _sensoryEstimates = MutableStateFlow<List<StoredSensoryEstimate>>(emptyList())
    override val sensoryEstimates: StateFlow<List<StoredSensoryEstimate>> = _sensoryEstimates.asStateFlow()
    private val _cvaSessions = MutableStateFlow<List<CuppingSession>>(emptyList())
    override val cvaSessions: StateFlow<List<CuppingSession>> = _cvaSessions.asStateFlow()
    private val _documents = MutableStateFlow<List<KnowledgeDocument>>(emptyList())
    override val documents: StateFlow<List<KnowledgeDocument>> = _documents.asStateFlow()
    private val _chunks = MutableStateFlow<List<DocumentChunk>>(emptyList())
    override val chunks: StateFlow<List<DocumentChunk>> = _chunks.asStateFlow()
    private val _models = MutableStateFlow<List<ModelAsset>>(emptyList())
    override val models: StateFlow<List<ModelAsset>> = _models.asStateFlow()
    private val _personas = MutableStateFlow<List<AssistantPersona>>(emptyList())
    val personas: StateFlow<List<AssistantPersona>> = _personas.asStateFlow()
    private val _selectedPersona = MutableStateFlow(SeedData.personas.first())
    val selectedPersona: StateFlow<AssistantPersona> = _selectedPersona.asStateFlow()
    private val _safDirectoryUri = MutableStateFlow<String?>(null)
    override val safDirectoryUri: StateFlow<String?> = _safDirectoryUri.asStateFlow()
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()
    private val _settings = MutableStateFlow<Map<String, String>>(emptyMap())
    val settings: StateFlow<Map<String, String>> = _settings.asStateFlow()

    suspend fun initialize() = withContext(Dispatchers.IO) {
        mutex.withLock {
            if (_ready.value) return@withLock
            guarded {
                val snapshot = database.withTransaction {
                    if (dao.record("meta", "initialized") == null) {
                        val seed = SeedData.curatedBeans.map { record("bean", it.id, it) } +
                            SeedData.personas.map { record("persona", it.id, it) }
                        seed.forEach {
                            require(dao.record(it.kind, it.id) == null) {
                                "Existing records require explicit recovery; defaults will not overwrite them."
                            }
                        }
                        dao.putRecords(
                            seed + record("meta", "initialized", SeedData.VERSION) +
                                record("meta", "reference-catalog-version", SeedData.VERSION)
                        )
                    } else {
                        val previousVersion = dao.record("meta", "reference-catalog-version")?.let { decode<Int>(it.json) } ?: 2
                        require(previousVersion in 1..SeedData.VERSION) { "Unsupported reference catalog version; records retained." }
                        if (previousVersion < SeedData.VERSION) {
                            val additions = SeedData.beansIntroducedAfter(previousVersion)
                                .filter { dao.record("bean", it.id) == null }
                                .map { record("bean", it.id, it) }
                            dao.putRecords(additions + record("meta", "reference-catalog-version", SeedData.VERSION))
                        }
                    }
                    readSnapshot()
                }
                publishSnapshot(snapshot)
                _ready.value = true
            }
        }
    }

    suspend fun waitReady() = initialize()

    override suspend fun addBean(bean: CoffeeBean) = mutate {
        BackupCodec.validateId(bean.id)
        DomainValidation.validateBean(bean)
        require(dao.record("bean", bean.id) == null) { "Bean identifier already exists." }
        require(bean.blendComponents.all { findBeanById(it.beanId) != null }) { "Blend component refers to a missing bean." }
        dao.putRecords(listOf(record("bean", bean.id, bean)))
    }

    override suspend fun updateBean(bean: CoffeeBean) = mutate {
        BackupCodec.validateId(bean.id)
        DomainValidation.validateBean(bean)
        require(dao.record("bean", bean.id) != null) { "Bean not found." }
        require(
            bean.blendComponents.all {
                it.beanId != bean.id && findBeanById(it.beanId) != null
            }
        ) { "Blend components must refer to other existing beans." }
        dao.putRecords(listOf(record("bean", bean.id, bean)))
    }

    override suspend fun deleteBean(id: String) = mutate {
        require(_recipes.value.none { it.beanId == id } && _beans.value.none { bean -> bean.blendComponents.any { it.beanId == id } }) {
            "Bean is referenced by a recipe or blend. Preserve it to retain history."
        }
        dao.deleteRecord("bean", id)
    }

    override fun findBeanById(id: String?): CoffeeBean? = _beans.value.find { it.id == id }

    override suspend fun addIngredient(ingredient: Ingredient) = mutate {
        BackupCodec.validateId(ingredient.id)
        DomainValidation.validateIngredient(ingredient)
        require(dao.record("ingredient", ingredient.id) == null) { "Ingredient identifier already exists." }
        dao.putRecords(listOf(record("ingredient", ingredient.id, ingredient)))
    }

    override suspend fun updateIngredient(ingredient: Ingredient) = mutate {
        BackupCodec.validateId(ingredient.id)
        DomainValidation.validateIngredient(ingredient)
        require(dao.record("ingredient", ingredient.id) != null) { "Ingredient not found." }
        val changedCatalog = _ingredients.value.map { if (it.id == ingredient.id) ingredient else it }
        _recipes.value.filter { recipe -> recipe.lines.any { it.ingredientId == ingredient.id } }
            .forEach { DomainValidation.validateRecipe(it, changedCatalog) }
        dao.putRecords(listOf(record("ingredient", ingredient.id, ingredient)))
    }

    override suspend fun saveRecipe(recipe: Recipe): Recipe = mutate {
        val existing = findRecipeById(recipe.id)
        val id = if (existing != null || recipe.id.isBlank()) UUID.randomUUID().toString() else recipe.id
        val group = existing?.revisionGroupId ?: recipe.revisionGroupId.ifBlank { id }
        val groupRevisions = _recipes.value.filter { it.revisionGroupId == group }
        val highest = groupRevisions.maxOfOrNull { it.version } ?: 0
        require(highest < Int.MAX_VALUE) { "Recipe revision limit reached." }
        val now = System.currentTimeMillis()
        val saved = recipe.copy(
            id = id,
            revisionGroupId = group,
            version = highest + 1,
            createdAt = now,
            updatedAt = now
        )
        validateRecipe(saved)
        require(dao.record("recipe", saved.id) == null) { "Recipe revisions cannot overwrite existing records." }
        dao.putRecords(listOf(record("recipe", saved.id, saved)))
        saved
    }

    override suspend fun duplicateRecipe(recipe: Recipe): Recipe {
        val id = UUID.randomUUID().toString()
        return saveRecipe(recipe.copy(id = id, revisionGroupId = id, name = "${recipe.name} (Copy)", version = 1))
    }

    override suspend fun archiveRecipe(id: String, isArchived: Boolean): Recipe = mutate {
        val existing = requireNotNull(findRecipeById(id)) { "Recipe not found." }
        val updated = existing.copy(isArchived = isArchived, updatedAt = System.currentTimeMillis())
        dao.putRecords(listOf(record("recipe", updated.id, updated)))
        updated
    }

    override suspend fun deleteRecipe(id: String) = mutate {
        _sensoryObservations.value.filter { it.recipeId == id }.forEach { dao.deleteRecord("observation", it.id) }
        _sensoryEstimates.value.filter { it.recipeId == id }.forEach { dao.deleteRecord("estimate", it.id) }
        dao.deleteRecord("recipe", id)
    }

    override fun findRecipeById(id: String?): Recipe? = _recipes.value.find { it.id == id }

    override suspend fun createCvaSession(session: CuppingSession) = mutate {
        BackupCodec.validateCvaSession(session)
        require(session.state == SessionState.DRAFT) { "New CVA sessions must start as drafts." }
        require(dao.record("cva_session", session.id) == null) { "CVA session identifier already exists." }
        dao.putRecords(listOf(record("cva_session", session.id, session)))
    }

    override suspend fun updateCvaSession(session: CuppingSession) = mutate {
        BackupCodec.validateCvaSession(session)
        val existingRecord = dao.record("cva_session", session.id)
        require(existingRecord != null) { "CVA session not found." }
        val existing = decode<CuppingSession>(existingRecord.json)
        BackupCodec.validateCvaSession(existing)
        require(
            existing.protocolSpec == session.protocolSpec && existing.assessorId == session.assessorId &&
                existing.assessorPseudonym == session.assessorPseudonym && existing.createdAtEpochMs == session.createdAtEpochMs &&
                preservesCvaSampleIdentity(existing, session)
        ) { "CVA protocol, assessor, creation time and blind sample identities are immutable." }
        when (existing.state) {
            SessionState.LOCKED -> {
                val amendment = session.amendmentHistory.lastOrNull()
                require(
                    session.state == SessionState.AMENDED &&
                        session.amendmentHistory.size == existing.amendmentHistory.size + 1 &&
                        session.amendmentHistory.dropLast(1) == existing.amendmentHistory && amendment != null &&
                        amendment.previousSnapshotSha256 == existing.currentSha256Checksum &&
                        amendment.previousSnapshotJson == existing.toSnapshotJson()
                ) {
                    "A locked CVA session can only be changed through a verified amendment."
                }
            }
            SessionState.AMENDED -> throw IllegalArgumentException("Amended CVA revisions are immutable.")
            else -> {
                require(session.state in allowedCvaNextStates(existing.state)) {
                    "Invalid CVA session state transition from ${existing.state} to ${session.state}."
                }
            }
        }
        dao.putRecords(listOf(record("cva_session", session.id, session)))
    }

    override suspend fun findCvaSession(id: String): CuppingSession? {
        BackupCodec.validateId(id)
        initialize()
        return withContext(Dispatchers.IO) {
            mutex.withLock {
                dao.record("cva_session", id)?.let { decode<CuppingSession>(it.json) }
            }
        }
    }

    private fun preservesCvaSampleIdentity(existing: CuppingSession, updated: CuppingSession): Boolean =
        updated.samples.size >= existing.samples.size && existing.samples.indices.all { index ->
            val before = existing.samples[index].blindSample
            val after = updated.samples[index].blindSample
            before.sampleId == after.sampleId && before.blindCode == after.blindCode && before.actualLotId == after.actualLotId
        }

    private fun allowedCvaNextStates(state: SessionState): Set<SessionState> = when (state) {
        SessionState.DRAFT -> setOf(
            SessionState.DRAFT,
            SessionState.PREPARED,
            SessionState.IN_PROGRESS,
            SessionState.REVIEW,
            SessionState.LOCKED
        )
        SessionState.PREPARED -> setOf(
            SessionState.PREPARED,
            SessionState.IN_PROGRESS,
            SessionState.REVIEW,
            SessionState.LOCKED
        )
        SessionState.IN_PROGRESS -> setOf(SessionState.IN_PROGRESS, SessionState.REVIEW, SessionState.LOCKED)
        SessionState.REVIEW -> setOf(SessionState.IN_PROGRESS, SessionState.REVIEW, SessionState.LOCKED)
        SessionState.LOCKED, SessionState.AMENDED -> emptySet()
    }

    override suspend fun saveSensoryEstimate(estimate: StoredSensoryEstimate) = mutate {
        BackupCodec.validateEstimate(estimate, _recipes.value.map { it.id }.toSet())
        dao.putRecords(listOf(record("estimate", estimate.id, estimate)))
    }

    override suspend fun saveSensoryObservation(observation: SensoryObservation) = mutate {
        BackupCodec.validateId(observation.id)
        DomainValidation.validateObservation(observation)
        require(findRecipeById(observation.recipeId) != null) { "Observation requires an existing recipe revision." }
        require(observation.timestamp >= 0L && dao.record("observation", observation.id) == null) {
            "Observation identifier or timestamp is invalid."
        }
        dao.putRecords(listOf(record("observation", observation.id, observation)))
    }

    override fun getObservationsForRecipe(recipeId: String): List<SensoryObservation> =
        _sensoryObservations.value.filter { it.recipeId == recipeId }

    override suspend fun addDocumentWithChunks(doc: KnowledgeDocument, newChunks: List<DocumentChunk>): KnowledgeDocument = mutate {
        BackupCodec.validateDocument(doc, newChunks)
        val duplicate = _documents.value.find { it.contentHash == doc.contentHash && it.id != doc.id }
        if (duplicate != null) return@mutate duplicate
        require(newChunks.all { chunk -> _chunks.value.none { it.id == chunk.id && it.documentId != doc.id } }) {
            "Chunk identifier belongs to another document."
        }
        removeDocumentVectors(doc.id)
        dao.removeDocumentIndex(doc.id)
        dao.removeDocumentChunks(doc.id)
        dao.putRecords(listOf(record("document", doc.id, doc)))
        putChunks(newChunks)
        doc
    }

    override suspend fun removeDocument(documentId: String) = mutate {
        removeDocumentVectors(documentId)
        dao.removeDocumentIndex(documentId)
        dao.removeDocumentChunks(documentId)
        dao.deleteRecord("document", documentId)
    }

    override suspend fun lexicalSearch(query: String, limit: Int): List<DocumentChunk> {
        require(limit in 1..1000) { "Search result limit is invalid." }
        initialize()
        val tokens = Regex("[\\p{L}\\p{N}]+").findAll(query.take(4096))
            .map { it.value.take(64) }.distinct().take(24).toList()
        if (tokens.isEmpty()) return emptyList()
        // Only literal tokens reach MATCH; user-supplied FTS operators/quotes cannot alter its grammar.
        val matchQuery = tokens.joinToString(" OR ") { "\"$it*\"" }
        return withContext(Dispatchers.IO) {
            guarded { dao.search(matchQuery, limit).map { decode<DocumentChunk>(it.json) } }
        }
    }

    override suspend fun upsertModel(model: ModelAsset) = mutate {
        validateModel(model)
        dao.putRecords(listOf(record("model", model.id, model)))
    }

    suspend fun removeModel(modelId: String) = mutate { dao.deleteRecord("model", modelId) }

    override suspend fun setSafDirectoryUri(uri: String?) = mutate {
        require(uri == null || uri.startsWith("content://") && uri.length <= InputLimits.METADATA_CHARS) {
            "SAF location must be a granted content URI."
        }
        if (uri == null) {
            dao.deleteRecord("setting", "storage.safTree")
        } else {
            dao.putRecords(listOf(record("setting", "storage.safTree", uri)))
        }
    }

    suspend fun selectPersona(persona: AssistantPersona) = mutate {
        require(_personas.value.any { it.id == persona.id }) { "Persona is not in the local catalog." }
        dao.putRecords(listOf(record("setting", "persona.selected", persona.id)))
    }

    suspend fun updatePersona(persona: AssistantPersona) = mutate {
        BackupCodec.validatePersona(persona)
        dao.putRecords(listOf(record("persona", persona.id, persona)))
    }

    suspend fun saveChatMessages(messages: List<ChatMessage>) = mutate {
        require(
            messages.size <= BackupCodec.MAX_RECORDS &&
                messages.map {
                    it.id
                }.toSet().size == messages.size
        ) { "Invalid chat history." }
        messages.forEach { BackupCodec.validateChatMessage(it) }
        dao.putRecords(listOf(record("chat", "history", messages)))
    }

    suspend fun saveSetting(key: String, value: String) = saveSettings(mapOf(key to value))

    /** One index batch has one transaction and one snapshot validation/publication. */
    suspend fun saveSettings(values: Map<String, String>) = mutate {
        values.forEach { (key, value) ->
            BackupCodec.validateSetting(key, value)
            require(key != "storage.safTree" && key != "persona.selected") {
                "Use the dedicated ownership/persona action for this setting."
            }
        }
        dao.putRecords(values.map { (key, value) -> record("setting", key, value) })
    }

    suspend fun removeSettings(prefix: String) = mutate {
        BackupCodec.validateId(prefix)
        require(!"storage.safTree".startsWith(prefix) && !"persona.selected".startsWith(prefix)) {
            "Use the dedicated ownership/persona action for this setting."
        }
        dao.deleteSettingsWithPrefix(prefix)
    }

    suspend fun exportBackupJson(): String {
        initialize()
        return withContext(Dispatchers.IO) {
            mutex.withLock { guarded { BackupCodec.encode(backupPayload()) } }
        }
    }

    suspend fun restoreBackupJson(json: String): BackupManifest {
        // Validate completely before opening any write transaction.
        val backup = withContext(Dispatchers.IO) { guarded { BackupCodec.decode(json) } }
        return mutate {
            val payload = backup.payload
            val retainedOwnershipSettings = _settings.value.filterKeys { BackupCodec.isOwnershipSetting(it) }
            dao.deleteKinds(
                listOf(
                    "bean",
                    "ingredient",
                    "recipe",
                    "observation",
                    "estimate",
                    "document",
                    "persona",
                    "chat",
                    "setting"
                ) + if (backup.manifest.schemaVersion >= 4) listOf("cva_session") else emptyList()
            )
            dao.clearIndex()
            dao.clearChunks()
            val records = payload.beans.map { record("bean", it.id, it) } +
                payload.ingredients.map { record("ingredient", it.id, it) } +
                payload.recipes.map { record("recipe", it.id, it) } +
                payload.observations.map { record("observation", it.id, it) } +
                payload.estimates.map { record("estimate", it.id, it) } +
                payload.documents.map { record("document", it.id, it) } +
                payload.personas.map { record("persona", it.id, it) } +
                (if (backup.manifest.schemaVersion >= 4) payload.cvaSessions.map { record("cva_session", it.id, it) } else emptyList()) +
                (payload.settings + retainedOwnershipSettings).map { (key, value) -> record("setting", key, value) } +
                record("chat", "history", payload.chatMessages)
            dao.putRecords(records)
            putChunks(payload.chunks)
            backup.manifest
        }
    }

    /** Explicit local deletion keeps independently user-owned SAF assets and their grant metadata. */
    suspend fun deleteApplicationRecords() = mutate {
        val ownership = _settings.value.filterKeys { BackupCodec.isOwnershipSetting(it) }
        dao.deleteKinds(
            listOf(
                "bean",
                "ingredient",
                "recipe",
                "observation",
                "estimate",
                "document",
                "persona",
                "chat",
                "setting",
                "cva_session"
            )
        )
        dao.clearIndex()
        dao.clearChunks()
        dao.putRecords(
            SeedData.personas.map { record("persona", it.id, it) } + ownership.map { (key, value) -> record("setting", key, value) }
        )
    }

    fun close() = database.close()

    private suspend fun <T> mutate(block: suspend () -> T): T {
        initialize()
        return withContext(Dispatchers.IO) {
            mutex.withLock {
                guarded {
                    val (result, snapshot) = database.withTransaction {
                        val result = block()
                        result to readSnapshot()
                    }
                    // Once committed, cache publication must finish even if the requesting screen is cancelled.
                    withContext(NonCancellable) { publishSnapshot(snapshot) }
                    result
                }
            }
        }
    }

    private data class RepositorySnapshot(val payload: BackupPayload, val models: List<ModelAsset>, val settings: Map<String, String>)

    private suspend fun readSnapshot(): RepositorySnapshot {
        val records = dao.allRecords().groupBy { it.kind }
        fun <T> items(kind: String, type: Class<T>): List<T> =
            records[kind].orEmpty().map { row -> RecordJson.gson.fromJson(row.json, type) }
        val beans = items("bean", CoffeeBean::class.java)
        val ingredients = items("ingredient", Ingredient::class.java)
        val recipes = items("recipe", Recipe::class.java).sortedByDescending { it.updatedAt }
        val observations = items("observation", SensoryObservation::class.java).sortedBy { it.timestamp }
        val documents = items("document", KnowledgeDocument::class.java).sortedByDescending { it.importedAt }
        val chunks = dao.allChunks().map { decode<DocumentChunk>(it.json) }.sortedWith(compareBy({ it.documentId }, { it.chunkIndex }))
        val personas = items("persona", AssistantPersona::class.java)
        val settings = records["setting"].orEmpty().associate { it.id to decode<String>(it.json) }
        val cvaSessions = items("cva_session", CuppingSession::class.java).sortedBy { it.createdAtEpochMs }
        val historyJson = records["chat"].orEmpty().find { it.id == "history" }?.json
        val history = if (historyJson == null) emptyList() else decode<List<ChatMessage>>(historyJson)
        val payload =
            BackupPayload(
                beans, ingredients, recipes, observations, documents, chunks, history, personas,
                settings.filterKeys {
                    !BackupCodec.isNonBackupSetting(it)
                },
                items("estimate", StoredSensoryEstimate::class.java).sortedByDescending { it.timestamp },
                cvaSessions
            )
        BackupCodec.validatePayload(payload)
        val models = items("model", ModelAsset::class.java)
        models.forEach { validateModel(it) }
        return RepositorySnapshot(payload, models, settings)
    }

    private fun publishSnapshot(snapshot: RepositorySnapshot) {
        val payload = snapshot.payload
        _beans.value = payload.beans
        _ingredients.value = payload.ingredients
        _recipes.value = payload.recipes
        _sensoryObservations.value = payload.observations
        _sensoryEstimates.value = payload.estimates
        _cvaSessions.value = payload.cvaSessions
        _documents.value = payload.documents
        _chunks.value = payload.chunks
        _personas.value = payload.personas
        _models.value = snapshot.models
        _settings.value = snapshot.settings
        _chatMessages.value = payload.chatMessages
        _safDirectoryUri.value = snapshot.settings["storage.safTree"]
        _selectedPersona.value = payload.personas.find { it.id == snapshot.settings["persona.selected"] } ?: payload.personas.first()
    }

    private fun backupPayload(): BackupPayload = BackupPayload(
        beans = _beans.value,
        ingredients = _ingredients.value,
        recipes = _recipes.value,
        observations = _sensoryObservations.value,
        documents = _documents.value,
        chunks = _chunks.value,
        chatMessages = _chatMessages.value,
        personas = _personas.value,
        settings = _settings.value.filterKeys { !BackupCodec.isNonBackupSetting(it) },
        estimates = _sensoryEstimates.value,
        cvaSessions = _cvaSessions.value
    )

    private fun validateRecipe(recipe: Recipe) {
        BackupCodec.validateId(recipe.id)
        BackupCodec.validateId(recipe.revisionGroupId)
        DomainValidation.validateRecipe(recipe, _ingredients.value)
        require(recipe.beanId == null || findBeanById(recipe.beanId) != null) { "Recipe bean is missing." }
    }

    private fun validateModel(model: ModelAsset) {
        BackupCodec.validateId(model.id)
        require(model.name.isNotBlank() && model.name.length <= 256 && model.revision.isNotBlank()) { "Invalid model metadata." }
        require(model.expectedSizeBytes > 0 && model.sha256.matches(Regex("[0-9a-fA-F]{64}"))) { "Model size or checksum is invalid." }
        require(model.downloadUrl.isEmpty() || model.downloadUrl.startsWith("https://")) { "Model download must use HTTPS." }
        require(model.downloadedBytes in 0..model.expectedSizeBytes && model.progressPercent in 0..100) { "Model progress is invalid." }
        if (model.status in setOf(ModelStatus.INSTALLED, ModelStatus.READY, ModelStatus.LOADING)) {
            require(model.localUri?.startsWith("content://") == true && model.downloadedBytes == model.expectedSizeBytes) {
                "Installed models require verified bytes and a real SAF source URI."
            }
        }
    }

    private suspend fun putChunks(chunks: List<DocumentChunk>) {
        if (chunks.isEmpty()) return
        dao.putChunks(chunks.map { StoredChunk(it.id, it.documentId, it.content, RecordJson.gson.toJson(it)) })
        dao.indexChunks(chunks.map { ChunkFts(it.id, it.content) })
    }

    private suspend fun removeDocumentVectors(documentId: String) {
        val chunkIds = _chunks.value.filter { it.documentId == documentId }.map { it.id }.toSet()
        val keys = _settings.value.keys.filter { key ->
            BackupCodec.isRuntimeCacheSetting(key) && key.substringAfter(':').substringAfter(':', "") in chunkIds
        }
        // Keep each query below SQLite's bound-parameter limit on the oldest supported Android tier.
        keys.chunked(SQL_BIND_BATCH).forEach { dao.deleteSettings(it) }
    }

    private fun record(kind: String, id: String, value: Any): StoredRecord = StoredRecord(kind, id, RecordJson.gson.toJson(value))
    private inline fun <reified T> decode(json: String): T = RecordJson.gson.fromJson(json, object : TypeToken<T>() {}.type)

    private suspend fun <T> guarded(block: suspend () -> T): T = try {
        val result = block()
        _errorState.value = null
        result
    } catch (cancelled: CancellationException) {
        throw cancelled
    } catch (boundaryFailure: Exception) {
        _errorState.value = boundaryFailure.message ?: "Local storage operation failed. Existing data was preserved."
        throw boundaryFailure
    }
}
