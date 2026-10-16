package com.coffeecreator.app.core.filesystem

import android.content.Context
import android.graphics.Bitmap
import com.googlecode.tesseract.android.TessBaseAPI
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

class BundledTesseractOcr(private val context: Context) {
    private val mutex = Mutex()

    suspend fun recognize(bitmap: Bitmap): String = mutex.withLock {
        withContext(Dispatchers.IO) {
            currentCoroutineContext().ensureActive()
            val root = File(context.filesDir, "tesseract")
            val weights = File(root, "tessdata/eng.traineddata")
            if (!weights.exists()) {
                check(weights.parentFile?.mkdirs() == true || weights.parentFile?.isDirectory == true) {
                    "Cannot prepare bundled English OCR data."
                }
                context.assets.open("ocr/eng.traineddata").use { input ->
                    weights.outputStream().use { output -> input.copyTo(output) }
                }
            }

            val engine = TessBaseAPI()
            try {
                check(engine.init(root.absolutePath, "eng")) { "Bundled English OCR initialization failed." }
                currentCoroutineContext().ensureActive()
                engine.setImage(bitmap)
                engine.utF8Text.orEmpty().also {
                    currentCoroutineContext().ensureActive()
                    require(it.length <= MAX_TEXT_CHARS) { "Recognized text exceeds the review limit." }
                }
            } finally {
                engine.recycle()
            }
        }
    }

    private companion object {
        const val MAX_TEXT_CHARS = 100_000
    }
}
