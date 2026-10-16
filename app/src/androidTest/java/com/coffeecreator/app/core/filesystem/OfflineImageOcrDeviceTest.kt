package com.coffeecreator.app.core.filesystem

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.core.content.FileProvider
import androidx.test.platform.app.InstrumentationRegistry
import java.io.File
import java.util.UUID
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test

class OfflineImageOcrDeviceTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun givenReadableLabelContentUri_whenRecognized_thenBundledOfflineOcrReturnsText() = runBlocking {
        withOwnedSource { directory ->
            val image = Bitmap.createBitmap(1400, 500, Bitmap.Config.ARGB_8888)
            val source = File(directory, "coffee-label.jpg")
            try {
                Canvas(image).apply {
                    drawColor(Color.WHITE)
                    drawText(
                        "ETHIOPIA WASHED 250G",
                        36f,
                        260f,
                        Paint().apply { color = Color.BLACK; textSize = 82f; isAntiAlias = true }
                    )
                }
                source.outputStream().use { output ->
                    check(image.compress(Bitmap.CompressFormat.JPEG, 95, output))
                }

                val text = OfflineImageOcr(context).recognize(
                    FileProvider.getUriForFile(context, "com.coffeecreator.app.qa.files", source)
                )

                assertTrue(text.isNotBlank())
                assertTrue(text.contains("ETHIOPIA", ignoreCase = true))
            } finally {
                image.recycle()
            }
        }
    }

    @Test
    fun givenRevokedOrUnsupportedContentUri_whenRecognized_thenRecoverableFailure() = runBlocking {
        withOwnedSource { directory ->
            val source = File(directory, "label.txt").apply { writeText("not an image") }
            val uri = FileProvider.getUriForFile(context, "com.coffeecreator.app.qa.files", source)
            assertTrue(runCatching { OfflineImageOcr(context).recognize(uri) }.isFailure)

            source.delete()
            assertTrue(runCatching { OfflineImageOcr(context).recognize(uri) }.isFailure)
        }
    }

    @Test
    fun givenOversizedImageStream_whenRecognized_thenRejectedBeforeBitmapDecode() = runBlocking {
        withOwnedSource { directory ->
            val source = File(directory, "oversized.jpg").apply {
                outputStream().use { stream -> stream.write(ByteArray(OfflineImageOcr.MAX_BYTES + 1)) }
            }
            val uri = FileProvider.getUriForFile(context, "com.coffeecreator.app.qa.files", source)

            assertTrue(runCatching { OfflineImageOcr(context).recognize(uri) }.isFailure)
        }
    }

    @Test
    fun givenRecognitionCanceled_whenOcrSuspends_thenDecodedBitmapIsRecycled() = runBlocking {
        withOwnedSource { directory ->
            val image = Bitmap.createBitmap(400, 200, Bitmap.Config.ARGB_8888)
            val source = File(directory, "cancel-label.jpg")
            try {
                Canvas(image).apply {
                    drawColor(Color.WHITE)
                    drawText("COFFEE LABEL", 20f, 100f, Paint().apply { color = Color.BLACK; textSize = 32f })
                }
                source.outputStream().use { output -> check(image.compress(Bitmap.CompressFormat.JPEG, 95, output)) }
            } finally {
                image.recycle()
            }
            val uri = FileProvider.getUriForFile(context, "com.coffeecreator.app.qa.files", source)
            val recognitionStarted = CompletableDeferred<Bitmap>()
            val ocr = OfflineImageOcr(context, recognizeBitmap = { bitmap ->
                recognitionStarted.complete(bitmap)
                awaitCancellation()
            })

            val job = launch { ocr.recognize(uri) }
            val decodedBitmap = recognitionStarted.await()
            job.cancelAndJoin()

            assertTrue(job.isCancelled)
            assertTrue("Decoded bitmap must be released after cancellation.", decodedBitmap.isRecycled)
        }
    }

    private suspend fun withOwnedSource(block: suspend (File) -> Unit) {
        val root = File(context.filesDir, "qa-sources").canonicalFile
        val directory = File(root, "bag-ocr-${UUID.randomUUID()}").apply { mkdirs() }.canonicalFile
        check(directory.path.startsWith(root.path + File.separator))
        try {
            block(directory)
        } finally {
            check(directory.path.startsWith(root.path + File.separator))
            directory.deleteRecursively()
        }
    }
}
