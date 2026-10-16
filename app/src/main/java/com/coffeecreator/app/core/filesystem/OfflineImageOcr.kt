package com.coffeecreator.app.core.filesystem

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.ByteArrayOutputStream
import java.io.InputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext

class OfflineImageOcr(
    private val context: Context,
    private val tesseract: BundledTesseractOcr = BundledTesseractOcr(context),
    private val recognizeBitmap: suspend (Bitmap) -> String = tesseract::recognize
) {
    fun retainReadGrant(uri: Uri): Boolean = try {
        context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
        true
    } catch (_: SecurityException) {
        context.contentResolver.persistedUriPermissions.any { it.uri == uri && it.isReadPermission }
    } catch (_: IllegalArgumentException) {
        context.contentResolver.persistedUriPermissions.any { it.uri == uri && it.isReadPermission }
    }

    fun isReadable(uri: Uri): Boolean = uri.scheme == "content" && try {
        context.contentResolver.openFileDescriptor(uri, "r")?.use { true } ?: false
    } catch (_: Exception) {
        false
    }

    suspend fun recognize(uri: Uri): String = withContext(Dispatchers.IO) {
        require(uri.scheme == "content") { "Choose a local image through the photo picker." }
        currentCoroutineContext().ensureActive()
        val resolver = context.contentResolver
        val mimeType = resolver.getType(uri)
        val bytes = resolver.openInputStream(uri)?.use { input -> readImageBytes(input) }
            ?: error("Photo access is unavailable. Select the image again.")
        currentCoroutineContext().ensureActive()

        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size, bounds)
        BagPhotoInputPolicy.validate(mimeType, bytes.size, bounds.outWidth, bounds.outHeight)

        val options = BitmapFactory.Options().apply {
            inSampleSize = BagPhotoInputPolicy.sampleSize(bounds.outWidth, bounds.outHeight)
            inPreferredConfig = Bitmap.Config.ARGB_8888
            inScaled = false
        }
        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
            ?: error("Photo could not be decoded. Select another image.")
        try {
            currentCoroutineContext().ensureActive()
            recognizeBitmap(bitmap).also { currentCoroutineContext().ensureActive() }
        } finally {
            bitmap.recycle()
        }
    }

    private suspend fun readImageBytes(input: InputStream): ByteArray {
        val output = ByteArrayOutputStream()
        val buffer = ByteArray(8192)
        while (true) {
            currentCoroutineContext().ensureActive()
            val count = input.read(buffer)
            if (count < 0) return output.toByteArray()
            require(output.size() + count <= MAX_BYTES) { "Image exceeds the 15 MB processing limit." }
            output.write(buffer, 0, count)
        }
    }

    companion object {
        const val MAX_BYTES = BagPhotoInputPolicy.MAX_BYTES
    }
}
