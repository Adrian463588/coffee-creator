package com.coffeecreator.app.core.engine

/** Reject excessive JSON nesting before a third-party parser can allocate a deep graph. */
object JsonInputPolicy {
    fun validateDepth(json: String, maxDepth: Int = 20) {
        var depth = 0
        var quoted = false
        var escaped = false
        json.forEach { char ->
            if (quoted) {
                if (escaped) {
                    escaped = false
                } else if (char == '\\') {
                    escaped = true
                } else if (char == '"') {
                    quoted = false
                }
            } else {
                when (char) {
                    '"' -> quoted = true
                    '{', '[' -> {
                        depth++
                        require(depth <= maxDepth) { "JSON nesting exceeds the supported limit." }
                    }
                    '}', ']' -> {
                        depth--
                        require(depth >= 0) { "Invalid JSON structure." }
                    }
                }
            }
        }
        require(depth == 0 && !quoted) { "Incomplete JSON structure." }
    }
}
