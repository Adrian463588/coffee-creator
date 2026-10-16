package com.coffeecreator.app.core.filesystem

import android.content.ContentResolver
import android.content.ContentValues
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import java.util.UUID

object CatalogPhotoCapture {
    @RequiresApi(Build.VERSION_CODES.Q)
    fun createPendingImage(resolver: ContentResolver): Uri {
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "coffee-creator-${UUID.randomUUID()}.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/Coffee Creator")
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }
        return requireNotNull(resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)) {
            "Cannot create a camera output. Choose an existing photo instead."
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun publish(resolver: ContentResolver, uri: Uri) {
        val count = resolver.update(
            uri,
            ContentValues().apply { put(MediaStore.Images.Media.IS_PENDING, 0) },
            null,
            null
        )
        check(count == 1) { "Camera photo could not be saved. Choose another image." }
    }

    fun discard(resolver: ContentResolver, uri: Uri) {
        resolver.delete(uri, null, null)
    }
}
