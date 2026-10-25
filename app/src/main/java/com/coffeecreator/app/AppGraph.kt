package com.coffeecreator.app

import android.content.Context
import com.coffeecreator.app.core.ai.HybridRagEngine
import com.coffeecreator.app.core.ai.LocalLlmCoordinator
import com.coffeecreator.app.core.ai.NativeLlmEngine
import com.coffeecreator.app.core.ai.ObjectBoxVectorStore
import com.coffeecreator.app.core.ai.OnnxEmbeddingEngine
import com.coffeecreator.app.core.ai.ToolExecutor
import com.coffeecreator.app.core.brew.BrewTimerController
import com.coffeecreator.app.core.data.DataRepository
import com.coffeecreator.app.core.filesystem.AssetManager
import com.coffeecreator.app.core.filesystem.BundledTesseractOcr
import com.coffeecreator.app.core.filesystem.DocumentImporter
import com.coffeecreator.app.core.filesystem.OfflineImageOcr
import com.coffeecreator.app.core.filesystem.PreferenceStore
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

@Singleton
class AppGraph @Inject constructor(@ApplicationContext private val context: Context) {
    val repository = DataRepository(context)
    val native = NativeLlmEngine(context)
    val preferences = PreferenceStore(context)
    val brewTimer by lazy { BrewTimerController(context, preferences) }
    val assets by lazy { AssetManager(context, repository) }
    private val tesseract by lazy { BundledTesseractOcr(context) }
    val importer by lazy { DocumentImporter(context, repository, tesseract) }
    val bagPhotoOcr by lazy { OfflineImageOcr(context, tesseract) }
    private var embeddingEngine: OnnxEmbeddingEngine? = null
    private val embeddingMutex = Mutex()
    val rag = HybridRagEngine(repository, ::embed, ObjectBoxVectorStore(context))
    val tools = ToolExecutor({ repository.beans.value }, { repository.ingredients.value }, { repository.recipes.value }, rag::search)
    val coordinator = LocalLlmCoordinator(native, tools)
    fun persistDraft(json: String) = (context as CoffeeApplication).persistDraft(json)
    suspend fun awaitBackgroundWrites() = (context as CoffeeApplication).awaitBackgroundWrites()

    private suspend fun embed(text: String): FloatArray = embeddingMutex.withLock {
        withContext(Dispatchers.IO) {
            val engine = embeddingEngine ?: OnnxEmbeddingEngine(
                copyBundled("embedding/model_quantized.onnx", "afdb6f1a0e45b715d0bb9b11772f032c399babd23bfc31fed1c170afc848bdb1"),
                copyBundled("embedding/vocab.txt", "07eced375cec144d27c900241f3e339478dec958f92fddbc551f295c992038a3")
            ).also { embeddingEngine = it }
            engine.embed(text)
        }
    }

    private fun copyBundled(name: String, sha: String): File {
        val target = File(context.filesDir, name)
        if (!target.exists() || AssetManager.sha256(target) != sha) {
            target.parentFile?.mkdirs()
            val pending = File(target.parentFile, "${target.name}.part")
            context.assets.open(name).use { input -> pending.outputStream().use { input.copyTo(it) } }
            check(AssetManager.sha256(pending) == sha) { "Bundled embedding asset checksum mismatch." }
            check(pending.renameTo(target)) { "Cannot publish bundled embedding working file." }
        }
        return target
    }
}
