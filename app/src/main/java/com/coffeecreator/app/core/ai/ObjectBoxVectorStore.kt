package com.coffeecreator.app.core.ai

import android.content.Context
import com.coffeecreator.app.core.ai.vectors.ObjectBoxBackend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ObjectBoxVectorStore(context: Context, name: String = "minilm-vectors") : VectorStore, AutoCloseable {
    private val backend by lazy { ObjectBoxBackend(context, name) }
    override suspend fun get(chunkId: String): FloatArray? = withContext(Dispatchers.IO) { backend.get(chunkId) }
    override suspend fun put(chunkId: String, vector: FloatArray) = putAll(mapOf(chunkId to vector))
    override suspend fun putAll(vectors: Map<String, FloatArray>) = withContext(Dispatchers.IO) { backend.putAll(vectors) }
    override suspend fun search(vector: FloatArray, limit: Int): Map<String, Double> = withContext(Dispatchers.IO) {
        backend.search(vector, limit)
    }
    override suspend fun retain(keys: Set<String>) = withContext(Dispatchers.IO) { backend.retain(keys) }
    override suspend fun clear() = withContext(Dispatchers.IO) { backend.clear() }
    override fun close() {
        backend.close()
    }
}
