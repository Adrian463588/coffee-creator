package com.coffeecreator.app.core.filesystem

import android.os.Build
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assume.assumeTrue
import org.junit.Test

class CatalogPhotoCaptureDeviceTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    @RequiresApi(Build.VERSION_CODES.Q)
    fun givenCanceledCapture_whenPendingOutputIsDiscarded_thenMediaStoreRowIsRemoved() {
        assumeTrue(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        val resolver = context.contentResolver
        val uri = CatalogPhotoCapture.createPendingImage(resolver)

        try {
            assertTrue(hasMediaStoreRow(uri))

            CatalogPhotoCapture.discard(resolver, uri)

            assertFalse(hasMediaStoreRow(uri))
        } finally {
            runCatching { CatalogPhotoCapture.discard(resolver, uri) }
        }
    }

    private fun hasMediaStoreRow(uri: android.net.Uri): Boolean =
        context.contentResolver.query(uri, arrayOf(MediaStore.Images.Media._ID), null, null, null)
            ?.use { it.moveToFirst() }
            ?: false
}
