package com.coffeecreator.app.core.filesystem

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.provider.OpenableColumns
import com.coffeecreator.app.core.data.DataRepository
import com.coffeecreator.app.core.model.DocumentChunk
import com.coffeecreator.app.core.model.KnowledgeDocument
import com.googlecode.tesseract.android.TessBaseAPI
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper
import java.io.File
import java.io.Writer
import java.nio.ByteBuffer
import java.nio.charset.CodingErrorAction
import java.security.MessageDigest
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

class DocumentImporter(private val context: Context, private val repository: DataRepository) {
    private val mutex = Mutex()

    suspend fun import(uri: Uri, onProgress: (String) -> Unit = {}): KnowledgeDocument = mutex.withLock {
        withContext(Dispatchers.IO) {
            require(uri.scheme == "content") { "Select a local document through the file picker." }
            try {
                context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } catch (_: SecurityException) {
                // Some providers grant only transient access; indexed chunks remain locally inspectable.
            }
            val title = displayName(uri)
            val mime = context.contentResolver.getType(uri)
            val pdf = mime == "application/pdf" || title.endsWith(".pdf", true)
            require(pdf || mime == "text/plain" || title.endsWith(".txt", true)) { "Supported imports: UTF-8 TXT or PDF." }
            onProgress("Reading and hashing $title")
            val bytes = context.contentResolver.openInputStream(uri)?.use { input ->
                input.readBytesBounded(MAX_FILE_BYTES)
            } ?: error("File grant revoked. Select the source again.")
            val hash = MessageDigest.getInstance("SHA-256").digest(bytes).joinToString("") { "%02x".format(it) }
            repository.documents.value.find { it.contentHash == hash }?.let { existing ->
                if (existing.sourceUri != uri.toString()) {
                    return@withContext repository.addDocumentWithChunks(
                        existing.copy(sourceUri = uri.toString()),
                        repository.chunks.value.filter { it.documentId == existing.id }
                    )
                }
                return@withContext existing
            }
            val pages = if (pdf) {
                extractPdf(bytes, onProgress)
            } else {
                require(!bytes.take(1024).contains(0)) { "Binary files cannot be imported as text." }
                val text = Charsets.UTF_8.newDecoder().onMalformedInput(CodingErrorAction.REPORT)
                    .onUnmappableCharacter(CodingErrorAction.REPORT).decode(ByteBuffer.wrap(bytes)).toString()
                listOf(text)
            }
            require(pages.sumOf(String::length) <= MAX_TEXT_CHARS) { "Extracted document text exceeds the indexing limit." }
            val id = UUID.randomUUID().toString()
            val chunks = pages.flatMapIndexed { page, content ->
                chunkText(content).mapIndexed { index, chunk ->
                    DocumentChunk(
                        id = "$id-${page + 1}-$index",
                        documentId = id,
                        documentTitle = title,
                        pageNumber = if (pdf) page + 1 else 0,
                        chunkIndex = index + 1,
                        content = chunk,
                        citationText = if (pdf) "[$title, page ${page + 1}, chunk ${index + 1}]" else "[$title, text chunk ${index + 1}]"
                    )
                }
            }.mapIndexed { index, chunk ->
                chunk.copy(
                    chunkIndex = index + 1,
                    citationText = if (pdf) {
                        "[$title, page ${chunk.pageNumber}, chunk ${index + 1}]"
                    } else {
                        "[$title, text chunk ${index + 1}]"
                    }
                )
            }
            require(chunks.isNotEmpty() && chunks.size <= MAX_CHUNKS) { "No readable text or document exceeds the chunk limit." }
            onProgress("Saving ${chunks.size} source chunks")
            repository.addDocumentWithChunks(
                KnowledgeDocument(
                    id = id,
                    title = title,
                    sourceUri = uri.toString(),
                    fileType = if (pdf) "PDF" else "TXT",
                    pageCount = if (pdf) pages.size else 0,
                    chunkCount = chunks.size,
                    contentHash = hash
                ),
                chunks
            )
        }
    }

    private fun displayName(uri: Uri): String = context.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
        ?.use { cursor -> if (cursor.moveToFirst()) cursor.getString(0).take(MAX_TITLE_CHARS) else "Imported document" }
        ?: "Imported document"

    private suspend fun extractPdf(bytes: ByteArray, onProgress: (String) -> Unit): List<String> {
        require(bytes.take(5).toByteArray().toString(Charsets.US_ASCII) == "%PDF-") { "Invalid PDF signature." }
        PDFBoxResourceLoader.init(context)
        val pages = PDDocument.load(bytes).use { document ->
            require(!document.isEncrypted) { "Encrypted PDFs are unsupported. Export a readable copy." }
            require(document.numberOfPages in 1..MAX_PAGES) { "PDF must have 1 to $MAX_PAGES pages." }
            val stripper = PDFTextStripper()
            var extractedChars = 0
            (1..document.numberOfPages).map { page ->
                currentCoroutineContext().ensureActive()
                onProgress("Extracting page $page / ${document.numberOfPages}")
                stripper.startPage = page
                stripper.endPage = page
                val writer = BoundedTextWriter(MAX_TEXT_CHARS - extractedChars)
                stripper.writeText(document, writer)
                writer.toString().also {
                    extractedChars += it.length
                    require(extractedChars <= MAX_TEXT_CHARS) { "Extracted PDF text exceeds the indexing limit." }
                }
            }.toMutableList()
        }
        if (pages.any(String::isBlank)) applyOcr(bytes, pages, onProgress)
        return pages
    }

    private class BoundedTextWriter(private val limit: Int) : Writer() {
        private val output = StringBuilder()
        override fun write(chars: CharArray, offset: Int, length: Int) {
            require(output.length + length <= limit) { "Extracted PDF text exceeds the indexing limit." }
            output.append(chars, offset, length)
        }
        override fun flush() = Unit
        override fun close() = Unit
        override fun toString(): String = output.toString()
    }

    private suspend fun applyOcr(bytes: ByteArray, pages: MutableList<String>, progress: (String) -> Unit) {
        val file = File.createTempFile("ocr-", ".pdf", context.cacheDir)
        try {
            file.writeBytes(bytes)
            ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY).use { descriptor ->
                PdfRenderer(descriptor).use { renderer ->
                    recognizeEmptyPages(renderer, pages, progress)
                }
            }
        } finally {
            file.delete()
        }
    }

    private suspend fun recognizeEmptyPages(renderer: PdfRenderer, pages: MutableList<String>, progress: (String) -> Unit) {
        var extractedChars = pages.sumOf(String::length)
        pages.indices.filter { pages[it].isBlank() }.forEach { index ->
            currentCoroutineContext().ensureActive()
            progress("Offline OCR page ${index + 1} / ${pages.size}")
            val text = renderOcr(renderer, index)
            extractedChars += text.length
            require(extractedChars <= MAX_TEXT_CHARS) { "OCR text exceeds the indexing limit." }
            pages[index] = text
        }
    }

    private fun renderOcr(renderer: PdfRenderer, index: Int): String = renderer.openPage(index).use { page ->
        val scale = OCR_EDGE.toFloat() / maxOf(page.width, page.height)
        val bitmap = Bitmap.createBitmap(
            (page.width * scale).toInt().coerceAtLeast(1),
            (page.height * scale).toInt().coerceAtLeast(1),
            Bitmap.Config.ARGB_8888
        )
        try {
            bitmap.eraseColor(Color.WHITE)
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            recognize(bitmap)
        } finally {
            bitmap.recycle()
        }
    }

    private fun recognize(bitmap: Bitmap): String {
        val root = File(context.filesDir, "tesseract")
        val weights = File(root, "tessdata/eng.traineddata")
        if (!weights.exists()) {
            weights.parentFile?.mkdirs()
            context.assets.open("ocr/eng.traineddata").use { input -> weights.outputStream().use { input.copyTo(it) } }
        }
        val engine = TessBaseAPI()
        return try {
            check(engine.init(root.absolutePath, "eng")) { "Bundled English OCR initialization failed." }
            engine.setImage(bitmap)
            engine.utF8Text.orEmpty()
        } finally {
            engine.recycle()
        }
    }

    companion object {
        private const val MAX_TITLE_CHARS = 200
        private const val MAX_FILE_BYTES = 20 * 1024 * 1024
        private const val MAX_TEXT_CHARS = 2 * 1024 * 1024
        private const val MAX_PAGES = 100
        private const val MAX_CHUNKS = 4000
        private const val OCR_EDGE = 1600
        private const val CHUNK_CHARS = 900
        fun chunkText(content: String): List<String> {
            val normalized = content.replace("\r\n", "\n").replace('\u0000', ' ').trim()
            return normalized.split(Regex("\n\\s*\n")).filter(String::isNotBlank).flatMap { paragraph ->
                paragraph.chunked(CHUNK_CHARS).map(String::trim).filter(String::isNotBlank)
            }
        }
        fun java.io.InputStream.readBytesBounded(limit: Int): ByteArray {
            val output = java.io.ByteArrayOutputStream()
            val buffer = ByteArray(8192)
            while (true) {
                val count = read(buffer)
                if (count < 0) break
                require(output.size() + count <= limit) { "File exceeds the supported size limit." }
                output.write(buffer, 0, count)
            }
            return output.toByteArray()
        }
    }
}
