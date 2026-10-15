package com.coffeecreator.app.core.filesystem

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import androidx.test.platform.app.InstrumentationRegistry
import com.coffeecreator.app.core.data.CoffeeDatabase
import com.coffeecreator.app.core.data.DataRepository
import java.io.File
import java.util.UUID
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DocumentDeviceTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Test fun givenActualTextContentUri_whenImportedTwice_thenDeduplicatedAndNoPagesInvented() = runBlocking {
        ownedScope { directory, repository, importer ->
            val source = File(directory, "brewing.txt").apply {
                writeText(
                    "Coffee extraction uses measured dissolved-solids concentration.\n\nKeep observed notes separate from predictions."
                )
            }
            val uri = FileProvider.getUriForFile(context, "com.coffeecreator.app.qa.files", source)
            val first = importer.import(uri)
            val duplicate = importer.import(uri)
            assertEquals(first.id, duplicate.id)
            assertEquals(1, repository.documents.value.size)
            assertEquals(0, first.pageCount)
            assertTrue(repository.chunks.value.all { it.pageNumber == 0 && !it.citationText.contains("page") })
            assertTrue(repository.lexicalSearch("dissolved").isNotEmpty())
            source.delete()
            assertTrue(runCatching { importer.import(uri) }.isFailure)
            assertEquals(1, repository.documents.value.size)
        }
    }

    @Test fun givenTwoPageTextPdf_whenImported_thenRealPagesAndUniqueChunkIndicesPersist() = runBlocking {
        ownedScope { directory, repository, importer ->
            val source = File(directory, "two-pages.pdf")
            PdfDocument().useDocument { pdf ->
                repeat(2) { index ->
                    val page = pdf.startPage(PdfDocument.PageInfo.Builder(600, 800, index + 1).create())
                    val paint = Paint().apply {
                        textSize = 22f
                        color = Color.BLACK
                    }
                    page.canvas.drawText(
                        if (index ==
                            0
                        ) {
                            "Coffee extraction and dose measurement."
                        } else {
                            "Water filtration and local source citations."
                        },
                        30f,
                        80f,
                        paint
                    )
                    pdf.finishPage(page)
                }
                source.outputStream().use(pdf::writeTo)
            }
            val document = importer.import(FileProvider.getUriForFile(context, "com.coffeecreator.app.qa.files", source))
            assertEquals(2, document.pageCount)
            assertEquals(setOf(1, 2), repository.chunks.value.map { it.pageNumber }.toSet())
            assertEquals(repository.chunks.value.size, repository.chunks.value.map { it.chunkIndex }.toSet().size)
            assertTrue(repository.lexicalSearch("filtration").isNotEmpty())
        }
    }

    @Test fun givenScannedImagePdf_whenImported_thenBundledOfflineOcrExtractsReadableText() = runBlocking {
        ownedScope { directory, repository, importer ->
            val bitmap = Bitmap.createBitmap(1200, 400, Bitmap.Config.ARGB_8888)
            try {
                val canvas = Canvas(bitmap)
                canvas.drawColor(Color.WHITE)
                canvas.drawText(
                    "Coffee brewing temperature",
                    40f,
                    180f,
                    Paint().apply {
                        textSize = 64f
                        color = Color.BLACK
                        isAntiAlias =
                            true
                    }
                )
                val source = File(directory, "scanned.pdf")
                PdfDocument().useDocument { pdf ->
                    val page = pdf.startPage(PdfDocument.PageInfo.Builder(1200, 400, 1).create())
                    page.canvas.drawBitmap(bitmap, 0f, 0f, null)
                    pdf.finishPage(page)
                    source.outputStream().use(pdf::writeTo)
                }
                importer.import(FileProvider.getUriForFile(context, "com.coffeecreator.app.qa.files", source))
                assertTrue(repository.chunks.value.any { it.content.contains("coffee", ignoreCase = true) })
                assertTrue(repository.chunks.value.all { it.pageNumber == 1 })
            } finally {
                bitmap.recycle()
            }
        }
    }

    @Test fun givenMalformedPdfOrOversizedText_whenImported_thenExistingRecordsRemainIntact() = runBlocking {
        ownedScope { directory, repository, importer ->
            val source = File(directory, "malformed.pdf").apply { writeText("%PDF-invalid") }
            assertTrue(
                runCatching {
                    importer.import(FileProvider.getUriForFile(context, "com.coffeecreator.app.qa.files", source))
                }.isFailure
            )
            assertTrue(repository.documents.value.isEmpty())
            assertTrue(runCatching { ByteArray(20).inputStream().use { with(DocumentImporter) { it.readBytesBounded(10) } } }.isFailure)
        }
    }

    private suspend fun ownedScope(block: suspend (File, DataRepository, DocumentImporter) -> Unit) {
        val id = UUID.randomUUID().toString()
        val name = "document-$id.db"
        val directory = File(context.filesDir, "qa-sources/$id").apply { mkdirs() }
        val repository = DataRepository(context, CoffeeDatabase.open(context, name))
        try {
            repository.initialize()
            block(directory, repository, DocumentImporter(context, repository))
        } finally {
            repository.close()
            context.deleteDatabase(name)
            check(directory.canonicalPath.startsWith(File(context.filesDir, "qa-sources").canonicalPath + File.separator))
            directory.deleteRecursively()
        }
    }
    private fun <T> PdfDocument.useDocument(block: (PdfDocument) -> T): T = try {
        block(this)
    } finally {
        close()
    }
}
