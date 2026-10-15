package com.coffeecreator.app.core.model

/** Shared trust-boundary budgets, independent of Android and storage adapters. */
object InputLimits {
    const val MODEL_BYTES_MAX = 4L * 1024 * 1024 * 1024
    const val DECIMAL_CHARS = 64
    const val METADATA_CHARS = 4096
    const val COLLECTION_ITEMS = 1000
    const val ALLERGEN_ITEMS = 64
    const val TOOL_JSON_CHARS = 8192
    const val QUERY_CHARS = 4000
    const val PERSONA_CHARS = 2000
    const val NATIVE_TEXT_CHARS = 32768
    const val EMBEDDING_DIMENSIONS = 4096
    const val VECTOR_BATCH = 64
}
