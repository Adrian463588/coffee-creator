package com.coffeecreator.app.core.data

import com.coffeecreator.app.core.engine.DomainValidation
import com.coffeecreator.app.core.model.AssistantPersona
import com.coffeecreator.app.core.model.ChatMessage
import com.coffeecreator.app.core.model.CoffeeBean
import com.coffeecreator.app.core.model.DocumentChunk
import com.coffeecreator.app.core.model.Ingredient
import com.coffeecreator.app.core.model.InputLimits
import com.coffeecreator.app.core.model.KnowledgeDocument
import com.coffeecreator.app.core.model.Recipe
import com.coffeecreator.app.core.model.SensoryObservation
import com.coffeecreator.app.core.model.StoredSensoryEstimate
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.Strictness
import java.lang.reflect.Modifier
import java.lang.reflect.ParameterizedType
import java.security.MessageDigest

internal object RecordJson {
    val gson = GsonBuilder().serializeNulls().disableHtmlEscaping().setStrictness(Strictness.STRICT).create()
}

data class BackupPayload(
    val beans: List<CoffeeBean> = emptyList(),
    val ingredients: List<Ingredient> = emptyList(),
    val recipes: List<Recipe> = emptyList(),
    val observations: List<SensoryObservation> = emptyList(),
    val documents: List<KnowledgeDocument> = emptyList(),
    val chunks: List<DocumentChunk> = emptyList(),
    val chatMessages: List<ChatMessage> = emptyList(),
    val personas: List<AssistantPersona> = emptyList(),
    val settings: Map<String, String> = emptyMap(),
    val estimates: List<StoredSensoryEstimate> = emptyList()
)

data class BackupManifest(
    val applicationId: String = "com.coffeecreator.app",
    val schemaVersion: Int = 3,
    val exportedAt: Long,
    val payloadSha256: String,
    val recordCounts: Map<String, Int>,
    val ownershipNotice: String = "Plaintext export. Models, installation status, checkpoints and grants are separate. " +
        "Source metadata does not grant file access."
)

data class ValidatedBackup(val manifest: BackupManifest, val payload: BackupPayload)

/** Complete, bounded plaintext backup format. SHA-256 detects corruption, not source authenticity. */
object BackupCodec {
    private const val MAX_PERSONA_DESCRIPTION = 2048
    private const val MAX_PERSONA_PROMPT = 16_384
    private const val MAX_MESSAGE_CHARS = 128_000
    const val MAX_BACKUP_CHARS = 16_000_000
    const val MAX_RECORDS = 20_000
    const val MAX_CHUNK_CHARS = 32_000
    const val MAX_SETTING_CHARS = 32_000
    private val payloadKeys =
        setOf("beans", "ingredients", "recipes", "observations", "documents", "chunks", "chatMessages", "personas", "settings", "estimates")
    private val nullableTextFields =
        setOf(
            "beanId", "roastDate", "supplierLimitsNotes", "toolCallName", "toolArgumentsJson", "toolResultJson",
            "grinderModel", "grinderSetting", "basketPuckPrep", "valveStatus"
        )

    fun encode(payload: BackupPayload, exportedAt: Long = System.currentTimeMillis()): String {
        validatePayload(payload)
        require(exportedAt >= 0L) { "Invalid export timestamp." }
        val payloadTree = RecordJson.gson.toJsonTree(payload).asJsonObject
        val manifest = BackupManifest(
            exportedAt = exportedAt,
            payloadSha256 = sha256(RecordJson.gson.toJson(payloadTree)),
            recordCounts = counts(payload)
        )
        val envelope = JsonObject().apply {
            add("manifest", RecordJson.gson.toJsonTree(manifest))
            add("payload", payloadTree)
        }
        return RecordJson.gson.toJson(envelope).also {
            require(it.length <= MAX_BACKUP_CHARS) { "Backup exceeds the supported size limit." }
        }
    }

    fun decode(json: String): ValidatedBackup {
        require(json.length in 1..MAX_BACKUP_CHARS) { "Backup is empty or exceeds the supported size limit." }
        validateDepth(json)
        try {
            val tree = RecordJson.gson.fromJson(json, JsonElement::class.java)
            require(tree.isJsonObject) { "Backup must be a JSON object." }
            val envelope = tree.asJsonObject
            require(envelope.keySet() == setOf("manifest", "payload")) { "Unexpected backup envelope fields." }
            require(envelope["manifest"].isJsonObject && envelope["payload"].isJsonObject) { "Missing backup manifest or payload." }
            val manifestTree = envelope["manifest"].asJsonObject
            requireRecordShape(manifestTree, BackupManifest::class.java)
            val manifest = RecordJson.gson.fromJson(manifestTree, BackupManifest::class.java)
            require(manifest.applicationId == "com.coffeecreator.app" && manifest.schemaVersion in 2..3) {
                "Unsupported backup application or schema version. Existing data is unchanged."
            }
            require(manifest.exportedAt >= 0L && manifest.payloadSha256.matches(Regex("[0-9a-f]{64}"))) {
                "Invalid backup manifest."
            }
            val payloadTree = envelope["payload"].asJsonObject
            val expectedKeys = if (manifest.schemaVersion == 2) payloadKeys - "estimates" else payloadKeys
            require(payloadTree.keySet() == expectedKeys) { "Backup payload is incomplete or has unknown fields." }
            val checksum = sha256(RecordJson.gson.toJson(payloadTree))
            require(MessageDigest.isEqual(checksum.toByteArray(Charsets.US_ASCII), manifest.payloadSha256.toByteArray(Charsets.US_ASCII))) {
                "Backup checksum mismatch. Existing data is unchanged."
            }
            requireCollectionShape(payloadTree, "beans", CoffeeBean::class.java)
            requireCollectionShape(payloadTree, "ingredients", Ingredient::class.java)
            requireCollectionShape(payloadTree, "recipes", Recipe::class.java)
            requireCollectionShape(payloadTree, "observations", SensoryObservation::class.java)
            if (manifest.schemaVersion == 3) requireCollectionShape(payloadTree, "estimates", StoredSensoryEstimate::class.java)
            requireCollectionShape(payloadTree, "documents", KnowledgeDocument::class.java)
            requireCollectionShape(payloadTree, "chunks", DocumentChunk::class.java)
            requireCollectionShape(payloadTree, "chatMessages", ChatMessage::class.java)
            requireCollectionShape(payloadTree, "personas", AssistantPersona::class.java)
            require(payloadTree["settings"].isJsonObject) { "Backup settings must be an object." }
            require(payloadTree["settings"].asJsonObject.entrySet().all { it.value.isJsonPrimitive && it.value.asJsonPrimitive.isString }) {
                "Backup setting values must be strings."
            }
            if (manifest.schemaVersion == 2) payloadTree.add("estimates", com.google.gson.JsonArray())
            val payload = RecordJson.gson.fromJson(payloadTree, BackupPayload::class.java)
            validatePayload(payload)
            val expectedCounts = if (manifest.schemaVersion == 2) counts(payload) - "estimates" else counts(payload)
            require(manifest.recordCounts == expectedCounts) { "Backup record counts do not match its payload." }
            return ValidatedBackup(manifest, payload)
        } catch (error: IllegalArgumentException) {
            throw error
        } catch (boundaryFailure: Exception) {
            throw IllegalArgumentException("Malformed backup. Existing data is unchanged.", boundaryFailure)
        }
    }

    fun validatePayload(payload: BackupPayload) {
        require(counts(payload).values.sum() <= MAX_RECORDS) { "Backup has too many records." }
        uniqueIds(payload.beans.map { it.id }, "beans")
        uniqueIds(payload.ingredients.map { it.id }, "ingredients")
        uniqueIds(payload.recipes.map { it.id }, "recipes")
        uniqueIds(payload.observations.map { it.id }, "observations")
        uniqueIds(payload.estimates.map { it.id }, "estimates")
        uniqueIds(payload.documents.map { it.id }, "documents")
        uniqueIds(payload.chunks.map { it.id }, "chunks")
        uniqueIds(payload.chatMessages.map { it.id }, "chat messages")
        uniqueIds(payload.personas.map { it.id }, "personas")
        payload.beans.forEach { DomainValidation.validateBean(it) }
        payload.ingredients.forEach { DomainValidation.validateIngredient(it) }
        val beanIds = payload.beans.map { it.id }.toSet()
        require(payload.beans.all { bean -> bean.blendComponents.all { it.beanId in beanIds && it.beanId != bean.id } }) {
            "Blend component refers to a missing bean or itself."
        }
        validateBlendGraph(payload.beans)
        payload.recipes.forEach {
            DomainValidation.validateRecipe(it, payload.ingredients)
            require(it.beanId == null || it.beanId in beanIds) { "Recipe refers to a missing bean." }
            validateId(it.revisionGroupId)
            require(it.version > 0 && it.createdAt >= 0L && it.updatedAt >= it.createdAt) { "Invalid recipe revision metadata." }
        }
        require(
            payload.recipes.groupBy { it.revisionGroupId }.values.all { revisions ->
                revisions.map { it.version }.toSet().size ==
                    revisions.size
            }
        ) {
            "Recipe group contains duplicate revision numbers."
        }
        val recipeIds = payload.recipes.map { it.id }.toSet()
        payload.observations.forEach {
            DomainValidation.validateObservation(it)
            require(it.recipeId in recipeIds && it.timestamp >= 0L) { "Observation refers to a missing recipe or invalid timestamp." }
        }
        payload.estimates.forEach { validateEstimate(it, recipeIds) }
        uniqueIds(payload.documents.map { it.contentHash }, "document content hashes")
        val documentsById = payload.documents.associateBy { it.id }
        payload.documents.forEach { validateDocument(it, payload.chunks.filter { chunk -> chunk.documentId == it.id }) }
        require(payload.chunks.all { it.documentId in documentsById }) { "Chunk refers to a missing document." }
        payload.chatMessages.forEach { validateChatMessage(it) }
        require(payload.personas.isNotEmpty()) { "Backup must preserve at least one editable persona." }
        payload.personas.forEach { validatePersona(it) }
        require(payload.settings.size <= 256) { "Too many settings." }
        payload.settings.forEach { (key, value) -> validateSetting(key, value) }
        require(
            payload.settings.keys.none {
                isNonBackupSetting(it)
            }
        ) { "Asset ownership, grants, checkpoints and runtime caches cannot be restored from a records backup." }
        payload.settings["persona.selected"]?.let { selected ->
            require(payload.personas.any { it.id == selected }) { "Selected persona is missing." }
        }
    }

    internal fun validateId(id: String) {
        require(id.matches(Regex("[A-Za-z0-9][A-Za-z0-9._:-]{0,127}"))) { "Invalid record identifier." }
    }

    internal fun validateDocument(document: KnowledgeDocument, chunks: List<DocumentChunk>) {
        validateId(document.id)
        require(document.title.isNotBlank() && document.title.length <= 256) { "Invalid document title." }
        require(document.sourceUri.startsWith("content://") && document.sourceUri.length <= InputLimits.METADATA_CHARS) {
            "Document source must be a SAF content URI."
        }
        require(document.fileType.uppercase() in setOf("TXT", "TEXT", "MD", "PDF", "PNG", "JPG", "JPEG")) { "Unsupported document type." }
        require(document.contentHash.matches(Regex("[0-9a-f]{64}")) && document.importedAt >= 0L) { "Invalid document integrity metadata." }
        require(document.pageCount in 0..10_000 && document.chunkCount == chunks.size && chunks.isNotEmpty()) {
            "Document chunk/page counts are invalid."
        }
        uniqueIds(chunks.map { it.id }, "document chunks")
        require(chunks.map { it.chunkIndex }.toSet().size == chunks.size) { "Document chunk indices must be unique." }
        chunks.forEach {
            require(it.documentId == document.id && it.documentTitle == document.title) {
                "Chunk source metadata does not match its document."
            }
            require(it.content.isNotBlank() && it.content.length <= MAX_CHUNK_CHARS && it.chunkIndex in 0..MAX_RECORDS) {
                "Invalid document chunk."
            }
            require(it.pageNumber in 0..document.pageCount) { "Chunk page is outside its document." }
            require(document.pageCount > 0 || it.pageNumber == 0) { "Text chunks cannot invent page numbers." }
            require(it.citationText.length <= 1024) { "Citation exceeds the supported size limit." }
        }
    }

    internal fun validatePersona(persona: AssistantPersona) {
        validateId(persona.id)
        require(persona.title.isNotBlank() && persona.title.length <= 128 && persona.description.length <= MAX_PERSONA_DESCRIPTION) {
            "Invalid persona metadata."
        }
        require(persona.systemPrompt.isNotBlank() && persona.systemPrompt.length <= MAX_PERSONA_PROMPT) {
            "Persona instructions are empty or too long."
        }
    }

    internal fun validateChatMessage(message: ChatMessage) {
        validateId(message.id)
        require(message.role in setOf("user", "assistant", "system", "tool")) { "Invalid chat message role." }
        require(message.content.length <= MAX_MESSAGE_CHARS && message.timestamp >= 0L && message.citations.size <= 100) {
            "Invalid chat message."
        }
        require(message.citations.all { it.length <= 1024 }) { "Chat citation exceeds the supported size limit." }
        require(message.sourceChunkIds.size <= 10) { "Too many source references." }
        message.sourceChunkIds.forEach(::validateId)
        require(
            listOf(message.toolCallName, message.toolArgumentsJson, message.toolResultJson).all {
                it == null || it.length <= MAX_SETTING_CHARS
            }
        ) { "Chat tool metadata exceeds the supported size limit." }
    }

    internal fun validateEstimate(estimate: StoredSensoryEstimate, recipeIds: Set<String>) {
        validateId(estimate.id)
        require(estimate.recipeId in recipeIds && estimate.timestamp >= 0) { "Estimate requires an existing recipe revision." }
        val profile = estimate.profile
        require(
            listOf(
                profile.aroma,
                profile.acidity,
                profile.sweetness,
                profile.bitterness,
                profile.body,
                profile.clarity,
                profile.finish,
                profile.balance
            ).all { it in 1..10 }
        ) { "Invalid estimated ordinal profile." }
        require(profile.confidencePercent == null) { "Uncalibrated estimates cannot claim numeric confidence." }
        require(
            profile.basis.isNotBlank() && profile.basis.length <= InputLimits.METADATA_CHARS &&
                profile.uncertaintyDisclaimer.isNotBlank() && profile.uncertaintyDisclaimer.length <= InputLimits.METADATA_CHARS &&
                profile.method.isNotBlank() && profile.method.length <= 256
        ) { "Estimate requires bounded basis, method and uncertainty." }
        require(profile.suggestedDescriptors.size <= 100 && profile.suggestedDescriptors.all { it.isNotBlank() && it.length <= 128 }) {
            "Invalid estimated descriptors."
        }
    }

    internal fun validateSetting(key: String, value: String) {
        require(key.matches(Regex("[A-Za-z0-9][A-Za-z0-9._:-]{0,127}")) && value.length <= MAX_SETTING_CHARS) {
            "Invalid setting key or value."
        }
    }

    internal fun isOwnershipSetting(key: String): Boolean = key == "storage.safTree" ||
        key == "assetDiscoveryError" ||
        key.startsWith("checkpoint:") ||
        key.startsWith("checkpoint.") ||
        key.startsWith("asset.") ||
        key.startsWith("asset:") ||
        key.startsWith("assets.") ||
        key.startsWith("assets:") ||
        key.startsWith("download.") ||
        key.startsWith("download:") ||
        key.startsWith("downloads.") ||
        key.startsWith("downloads:")

    internal fun isRuntimeCacheSetting(key: String): Boolean = key.startsWith("vector:")

    internal fun isNonBackupSetting(key: String): Boolean = isOwnershipSetting(key) || isRuntimeCacheSetting(key)

    fun sha256(value: String): String = MessageDigest.getInstance("SHA-256")
        .digest(value.toByteArray(Charsets.UTF_8)).joinToString("") { "%02x".format(it) }

    private fun counts(payload: BackupPayload): Map<String, Int> = linkedMapOf(
        "beans" to payload.beans.size,
        "ingredients" to payload.ingredients.size,
        "recipes" to payload.recipes.size,
        "observations" to payload.observations.size,
        "documents" to payload.documents.size,
        "chunks" to payload.chunks.size,
        "chatMessages" to payload.chatMessages.size,
        "personas" to payload.personas.size,
        "settings" to payload.settings.size,
        "estimates" to payload.estimates.size
    )

    private fun uniqueIds(ids: List<String>, kind: String) {
        ids.forEach { validateId(it) }
        require(ids.toSet().size == ids.size) { "Duplicate $kind identifiers." }
    }

    private fun validateBlendGraph(beans: List<CoffeeBean>) {
        val remaining = beans.associate { it.id to it.blendComponents.size }.toMutableMap()
        val dependents = mutableMapOf<String, MutableList<String>>()
        beans.forEach { bean -> bean.blendComponents.forEach { dependents.getOrPut(it.beanId) { mutableListOf() }.add(bean.id) } }
        val ready = ArrayDeque(remaining.filterValues { it == 0 }.keys)
        var visited = 0
        while (ready.isNotEmpty()) {
            val id = ready.removeFirst()
            visited += 1
            dependents[id].orEmpty().forEach { dependent ->
                val count = remaining.getValue(dependent) - 1
                remaining[dependent] = count
                if (count == 0) ready.addLast(dependent)
            }
        }
        require(visited == beans.size) { "Blend components contain a cyclic reference." }
    }

    private fun requireCollectionShape(payload: JsonObject, key: String, type: Class<*>) {
        require(payload[key].isJsonArray && payload[key].asJsonArray.size() <= MAX_RECORDS) { "Invalid $key collection." }
        payload[key].asJsonArray.forEach {
            require(it.isJsonObject) { "Invalid $key record." }
            requireRecordShape(it.asJsonObject, type)
        }
    }

    // Gson otherwise accepts missing Kotlin fields and coerces numbers into strings.
    private fun requireRecordShape(record: JsonObject, type: Class<*>) {
        val fields = type.declaredFields.filterNot { Modifier.isStatic(it.modifiers) || it.isSynthetic }
        require(record.keySet() == fields.map { it.name }.toSet()) { "Incomplete or unknown ${type.simpleName} fields." }
        fields.forEach { field ->
            val value = record[field.name]
            when {
                value.isJsonNull -> require(
                    !field.type.isPrimitive && (field.type != String::class.java || field.name in nullableTextFields)
                ) {
                    "Required field ${field.name} is null."
                }
                field.type == String::class.java -> require(value.isJsonPrimitive && value.asJsonPrimitive.isString) {
                    "Field ${field.name} must be text."
                }
                field.type == Boolean::class.javaPrimitiveType || field.type == Boolean::class.javaObjectType -> require(
                    value.isJsonPrimitive && value.asJsonPrimitive.isBoolean
                ) { "Field ${field.name} must be boolean." }
                field.type.isPrimitive || Number::class.java.isAssignableFrom(field.type) -> requireNumber(value, field.type)
                List::class.java.isAssignableFrom(field.type) -> {
                    require(value.isJsonArray && value.asJsonArray.size() <= MAX_RECORDS) { "Field ${field.name} must be a bounded array." }
                    val itemType = (field.genericType as ParameterizedType).actualTypeArguments.first() as Class<*>
                    value.asJsonArray.forEach { requireGenericValue(it, itemType) }
                }
                Map::class.java.isAssignableFrom(field.type) -> {
                    require(value.isJsonObject && value.asJsonObject.size() <= MAX_RECORDS) {
                        "Field ${field.name} must be a bounded object."
                    }
                    val itemType = (field.genericType as ParameterizedType).actualTypeArguments.last() as Class<*>
                    value.asJsonObject.entrySet().forEach { requireGenericValue(it.value, itemType) }
                }
                else -> {
                    require(value.isJsonObject) { "Field ${field.name} must be an object." }
                    requireRecordShape(value.asJsonObject, field.type)
                }
            }
        }
    }

    private fun requireGenericValue(value: JsonElement, type: Class<*>) {
        require(!value.isJsonNull) { "Collection entries cannot be null." }
        when {
            type == String::class.java -> require(value.isJsonPrimitive && value.asJsonPrimitive.isString) {
                "Collection entry must be text."
            }
            Number::class.java.isAssignableFrom(type) -> requireNumber(value, type)
            else -> {
                require(value.isJsonObject) { "Collection entry must be an object." }
                requireRecordShape(value.asJsonObject, type)
            }
        }
    }

    private fun requireNumber(value: JsonElement, type: Class<*>) {
        require(value.isJsonPrimitive && value.asJsonPrimitive.isNumber) { "Field must be numeric." }
        when (type) {
            Int::class.javaPrimitiveType, Int::class.javaObjectType -> value.asBigDecimal.intValueExact()
            Long::class.javaPrimitiveType, Long::class.javaObjectType -> value.asBigDecimal.longValueExact()
        }
    }

    private fun validateDepth(json: String) {
        var depth = 0
        var quoted = false
        var escaped = false
        json.forEach { character ->
            if (quoted) {
                if (escaped) {
                    escaped = false
                } else if (character == '\\') {
                    escaped = true
                } else if (character == '"') {
                    quoted = false
                }
            } else {
                when (character) {
                    '"' -> quoted = true
                    '{', '[' -> {
                        depth += 1
                        require(depth <= 20) { "Backup nesting exceeds the supported limit." }
                    }
                    '}', ']' -> depth -= 1
                }
            }
        }
    }
}
