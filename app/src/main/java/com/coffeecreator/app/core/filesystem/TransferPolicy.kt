package com.coffeecreator.app.core.filesystem

import java.net.HttpURLConnection

object TransferPolicy {
    fun safeIdentifier(identifier: String): String {
        require(identifier.matches(Regex("[A-Za-z0-9][A-Za-z0-9_-]{0,79}"))) { "Invalid asset identifier." }
        return identifier
    }

    fun validateHash(hash: String) {
        require(hash.matches(Regex("[a-fA-F0-9]{64}"))) { "Expected SHA-256 must contain 64 hexadecimal digits." }
    }

    fun resumeOffset(
        partialSize: Long,
        expectedSize: Long,
        savedEtag: String?,
        code: Int,
        contentRange: String?,
        responseEtag: String?
    ): Long {
        require(partialSize in 0..expectedSize && expectedSize > 0) { "Invalid partial byte count." }
        if (code == HttpURLConnection.HTTP_OK) return 0
        require(code == HttpURLConnection.HTTP_PARTIAL) { "HTTP $code cannot publish an asset." }
        require(savedEtag != null && savedEtag.startsWith('"') && savedEtag == responseEtag) {
            "Remote identity changed or lacks a strong ETag. Restart required."
        }
        val match = Regex("bytes (\\d+)-(\\d+)/(\\d+)").matchEntire(contentRange.orEmpty())
            ?: error("Invalid Content-Range; restart required.")
        val (start, end, total) = match.destructured
        require(start.toLong() == partialSize && total.toLong() == expectedSize && end.toLong() == expectedSize - 1) {
            "Content-Range does not match verified local partial bytes."
        }
        return partialSize
    }
}
