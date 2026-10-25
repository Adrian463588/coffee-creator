package com.coffeecreator.app.core.filesystem

import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.security.MessageDigest
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [33])
class SafTextDocumentWriterTest {
    @Test
    fun givenSelectedSafUri_whenWriteCompletes_thenUtf8BytesAndDigestMatch() = runTest {
        val output = ByteArrayOutputStream()
        var requestedMode: String? = null
        val writer = SafTextDocumentWriter { _, mode ->
            requestedMode = mode
            output
        }
        val payload = "Coffee Creator — kopi"
        val expectedBytes = payload.toByteArray(Charsets.UTF_8)

        val digest = writer.write(Uri.parse("content://test/coffee-export"), payload)
        val expectedDigest = MessageDigest.getInstance("SHA-256")
            .digest(expectedBytes)
            .joinToString("") { byte -> "%02x".format(byte) }

        assertEquals("wt", requestedMode)
        assertArrayEquals(expectedBytes, output.toByteArray())
        assertEquals(expectedDigest, digest)
    }

    @Test
    fun givenProviderCannotOpenDocument_whenWriteRuns_thenReportsIoFailure() = runTest {
        val writer = SafTextDocumentWriter { _, _ -> null }

        val failure = runCatching {
            writer.write(Uri.parse("content://test/unavailable"), "coffee")
        }.exceptionOrNull()

        assertTrue(failure is IOException)
    }
}
