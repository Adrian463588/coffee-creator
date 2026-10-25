package com.coffeecreator.app.core.filesystem

import android.content.ContentResolver
import android.net.Uri
import java.io.IOException
import java.io.OutputStream
import java.security.MessageDigest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Writes user-selected text exports through a SAF URI without deriving filesystem paths. */
class SafTextDocumentWriter internal constructor(
    private val openOutputStream: (Uri, String) -> OutputStream?
) {
    constructor(resolver: ContentResolver) : this({ uri, mode -> resolver.openOutputStream(uri, mode) })

    suspend fun write(uri: Uri, content: String): String = withContext(Dispatchers.IO) {
        val bytes = content.toByteArray(Charsets.UTF_8)
        val output = openOutputStream(uri, "wt")
            ?: throw IOException("The selected document provider did not open an output stream.")
        output.use {
            it.write(bytes)
            it.flush()
        }
        MessageDigest.getInstance("SHA-256")
            .digest(bytes)
            .joinToString("") { byte -> "%02x".format(byte) }
    }
}
