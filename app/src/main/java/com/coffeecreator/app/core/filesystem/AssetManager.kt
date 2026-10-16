package com.coffeecreator.app.core.filesystem

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.coffeecreator.app.core.data.DataRepository
import com.coffeecreator.app.core.engine.JsonInputPolicy
import com.coffeecreator.app.core.filesystem.DocumentImporter.Companion.readBytesBounded
import com.coffeecreator.app.core.model.InputLimits
import com.coffeecreator.app.core.model.ModelAsset
import com.coffeecreator.app.core.model.ModelStatus
import com.google.gson.Gson
import java.io.File
import java.io.FileOutputStream
import java.security.MessageDigest
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Request

data class DownloadCheckpoint(val etag: String?, val bytes: Long, val partialSha256: String, val revision: String)

class AssetManager(
    private val context: Context,
    private val repository: DataRepository,
    private val client: OkHttpClient = OkHttpClient.Builder().connectTimeout(
        NETWORK_TIMEOUT_SECONDS,
        TimeUnit.SECONDS
    ).readTimeout(NETWORK_TIMEOUT_SECONDS, TimeUnit.SECONDS).build()
) {
    private val gson = Gson()
    private val assetLocks = ConcurrentHashMap<String, Mutex>()
    private val activeCalls = ConcurrentHashMap<String, Call>()
    private val staging = File(context.filesDir, "asset-staging").apply { mkdirs() }
    private val storage = context.getSystemService(android.os.storage.StorageManager::class.java)
    private fun availableStagingBytes() = storage.getAllocatableBytes(storage.getUuidForPath(staging))

    suspend fun selectDirectory(uri: Uri): Int = withContext(Dispatchers.IO) {
        require(uri.scheme == "content") { "Choose a document-provider directory." }
        context.contentResolver.takePersistableUriPermission(
            uri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        )
        val tree = DocumentFile.fromTreeUri(context, uri) ?: error("Directory unavailable; select it again.")
        require(tree.canRead() && tree.canWrite()) { "Directory must allow reading and writing." }
        repository.setSafDirectoryUri(uri.toString())
        discover()
    }

    fun validateManifest(asset: ModelAsset) {
        TransferPolicy.safeIdentifier(asset.id)
        TransferPolicy.safeIdentifier(asset.revision)
        TransferPolicy.validateHash(asset.sha256)
        require(asset.expectedSizeBytes in 1..MAX_MODEL_BYTES) { "Model size must be between 1 byte and 4 GiB." }
        when (BuiltInAssets.runtimeCapability(asset)) {
            ModelRuntimeCapability.LLAMA_CPP_INFERENCE -> {
                require(asset.runtime == "llama.cpp" && asset.tokenizerSupport.isNotBlank()) {
                    "Unsupported runtime/tokenizer."
                }
            }
            ModelRuntimeCapability.DOWNLOAD_ONLY -> require(BuiltInAssets.isPinnedAsset(asset)) {
                "Download-only assets must match a verified catalog manifest."
            }
            ModelRuntimeCapability.UNSUPPORTED -> error("Unsupported runtime or model format.")
        }
        require(asset.name.isNotBlank() && asset.license.isNotBlank()) { "Name and license are required." }
        val parsed = Uri.parse(asset.downloadUrl)
        require(asset.downloadUrl.isEmpty() || parsed.scheme == "https" && !parsed.host.isNullOrBlank()) {
            "Asset transfers require HTTPS."
        }
    }

    suspend fun registerManifest(json: String): ModelAsset {
        require(json.length <= MAX_MANIFEST_CHARS) { "Manifest too large." }
        JsonInputPolicy.validateDepth(json)
        val asset = gson.fromJson(json, ModelAsset::class.java) ?: error("Invalid asset manifest.")
        validateManifest(asset)
        val clean = asset.copy(status = ModelStatus.CATALOGUED, localUri = null, downloadedBytes = 0, progressPercent = 0, error = null)
        repository.upsertModel(clean)
        return clean
    }

    suspend fun requestDownload(asset: ModelAsset) {
        validateManifest(asset)
        require(asset.downloadUrl.isNotBlank()) { "No download URL. Import a matching local model instead." }
        directory()
        repository.upsertModel(asset.copy(status = ModelStatus.QUEUED, error = null))
        val request = OneTimeWorkRequestBuilder<AssetDownloadWorker>()
            .setInputData(workDataOf("assetId" to asset.id))
            .addTag("coffee-asset-${asset.id}")
            .build()
        WorkManager.getInstance(context).enqueueUniqueWork("coffee-asset-${asset.id}", ExistingWorkPolicy.KEEP, request)
    }

    suspend fun pause(asset: ModelAsset) {
        activeCalls[asset.id]?.cancel()
        WorkManager.getInstance(context).cancelUniqueWork("coffee-asset-${asset.id}").result.get()
        assetLock(asset.id).withLock {
            val current = repository.models.value.find { it.id == asset.id } ?: asset
            repository.upsertModel(current.copy(status = ModelStatus.PAUSED))
        }
    }

    suspend fun cancel(asset: ModelAsset) = withContext(Dispatchers.IO) {
        activeCalls[asset.id]?.cancel()
        WorkManager.getInstance(context).cancelUniqueWork("coffee-asset-${asset.id}").result.get()
        assetLock(asset.id).withLock {
            partial(asset).delete()
            repository.saveSetting("checkpoint:${asset.id}", "")
            repository.upsertModel(asset.copy(status = ModelStatus.CANCELLED, downloadedBytes = 0, progressPercent = 0, error = null))
        }
    }

    suspend fun forget(asset: ModelAsset) {
        // User-owned published weights remain in their SAF directory.
        repository.upsertModel(asset.copy(status = ModelStatus.CATALOGUED, localUri = null, error = null))
    }

    suspend fun importModel(asset: ModelAsset, uri: Uri) = assetLock(asset.id).withLock {
        try {
            importLocked(asset, uri)
        } catch (cancelled: CancellationException) {
            withContext(NonCancellable + Dispatchers.IO) {
                repository.upsertModel(
                    asset.copy(status = ModelStatus.FAILED, error = "Import interrupted. Select the matching file again.")
                )
            }
            throw cancelled
        } catch (boundaryFailure: Exception) {
            repository.upsertModel(
                asset.copy(
                    status = ModelStatus.FAILED,
                    error =
                    boundaryFailure.message ?: "Model import failed; retry is available."
                )
            )
            throw boundaryFailure
        }
    }

    private suspend fun importLocked(asset: ModelAsset, uri: Uri) = withContext(Dispatchers.IO) {
        validateManifest(asset)
        directory()
        val target = partial(asset)
        require(availableStagingBytes() > asset.expectedSizeBytes + STORAGE_RESERVE) { "Insufficient staging storage." }
        repository.upsertModel(asset.copy(status = ModelStatus.VERIFYING, error = null))
        context.contentResolver.openInputStream(uri)?.use { input ->
            target.outputStream().use { output ->
                val buffer = ByteArray(BUFFER_BYTES)
                var bytes = 0L
                while (true) {
                    currentCoroutineContext().ensureActive()
                    val count = input.read(buffer)
                    if (count < 0) break
                    bytes += count
                    require(bytes <= asset.expectedSizeBytes) { "File exceeds manifest size." }
                    output.write(buffer, 0, count)
                }
            }
        } ?: error("Model access revoked. Select the file again.")
        publish(asset, target)
    }

    suspend fun transfer(asset: ModelAsset) = assetLock(asset.id).withLock { transferLocked(asset) }

    private suspend fun transferLocked(asset: ModelAsset) = withContext(Dispatchers.IO) {
        validateManifest(asset)
        val file = partial(asset)
        if (file.exists() && file.length() == asset.expectedSizeBytes && sha256(file) == asset.sha256.lowercase()) {
            publish(asset, file)
            return@withContext
        }
        val checkpoint = recoverCheckpoint(asset, file)
        val offset = file.length()
        require(availableStagingBytes() > asset.expectedSizeBytes - offset + STORAGE_RESERVE) { "Insufficient staging storage." }
        val request = Request.Builder().url(asset.downloadUrl).apply {
            if (offset > 0) {
                header("Range", "bytes=$offset-")
                header("If-Range", requireNotNull(checkpoint?.etag))
            }
            header("Accept-Encoding", "identity")
        }.build()
        var etag: String? = checkpoint?.etag
        var unsafeResume = false
        try {
            repository.upsertModel(asset.copy(status = ModelStatus.DOWNLOADING, error = null))
            val call = client.newCall(request)
            activeCalls[asset.id] = call
            try {
                withCallCancellation(call) {
                    call.execute().use { response ->
                        require(response.isSuccessful) { "Asset server returned HTTP ${response.code}." }
                        val responseEtag = response.header("ETag")
                        val verifiedOffset = if (offset == 0L) {
                            require(response.code == 200) { "Unexpected partial response for a fresh transfer." }
                            0L
                        } else {
                            try {
                                TransferPolicy.resumeOffset(
                                    offset,
                                    asset.expectedSizeBytes,
                                    checkpoint?.etag,
                                    response.code,
                                    response.header("Content-Range"),
                                    responseEtag
                                )
                            } catch (failure: IllegalArgumentException) {
                                unsafeResume = true
                                throw failure
                            } catch (failure: IllegalStateException) {
                                unsafeResume = true
                                throw failure
                            }
                        }
                        val body = response.body ?: error("Empty HTTP body.")
                        if (body.contentLength() >= 0) {
                            require(body.contentLength() == asset.expectedSizeBytes - verifiedOffset) {
                                "Remote size differs from manifest."
                            }
                        }
                        etag = responseEtag
                        receiveBody(asset, file, verifiedOffset, body, etag)
                    }
                }
            } finally {
                activeCalls.remove(asset.id, call)
            }
            publish(asset, file)
        } catch (cancelled: CancellationException) {
            kotlinx.coroutines.withContext(kotlinx.coroutines.NonCancellable) { saveCheckpoint(asset, file, etag) }
            throw cancelled
        } catch (boundaryFailure: Exception) {
            if (unsafeResume) {
                file.delete()
                repository.saveSetting("checkpoint:${asset.id}", "")
            } else {
                saveCheckpoint(asset, file, etag)
            }
            repository.upsertModel(
                asset.copy(
                    status = ModelStatus.FAILED,
                    downloadedBytes = file.length().coerceIn(0, asset.expectedSizeBytes),
                    error =
                    boundaryFailure.message ?: "Asset transfer failed."
                )
            )
            throw boundaryFailure
        }
    }

    private suspend fun receiveBody(asset: ModelAsset, file: File, offset: Long, body: okhttp3.ResponseBody, etag: String?) {
        var bytes = offset
        var nextProgress = bytes + PROGRESS_BYTES
        FileOutputStream(file, offset > 0).use { output ->
            val input = body.byteStream()
            val buffer = ByteArray(BUFFER_BYTES)
            while (true) {
                currentCoroutineContext().ensureActive()
                val count = input.read(buffer)
                if (count < 0) break
                bytes += count
                require(bytes <= asset.expectedSizeBytes) { "Server sent excess model bytes." }
                output.write(buffer, 0, count)
                if (bytes >= nextProgress) {
                    output.flush()
                    saveCheckpoint(asset, file, etag)
                    repository.upsertModel(
                        asset.copy(
                            status = ModelStatus.DOWNLOADING,
                            downloadedBytes = bytes,
                            progressPercent = ((bytes * 100) / asset.expectedSizeBytes).toInt()
                        )
                    )
                    nextProgress = bytes + PROGRESS_BYTES
                }
            }
        }
    }

    private fun recoverCheckpoint(asset: ModelAsset, file: File): DownloadCheckpoint? {
        if (!file.exists()) return null
        val checkpoint = repository.settings.value["checkpoint:${asset.id}"]?.takeIf(String::isNotBlank)?.let { raw ->
            runCatching {
                JsonInputPolicy.validateDepth(raw)
                gson.fromJson(raw, DownloadCheckpoint::class.java)
            }.getOrNull()
        }
        val valid = checkpoint != null &&
            checkpoint.revision == asset.revision &&
            checkpoint.etag?.startsWith('"') == true &&
            file.length() == checkpoint.bytes &&
            file.length() < asset.expectedSizeBytes &&
            sha256(file) == checkpoint.partialSha256
        if (!valid) {
            file.delete()
            return null
        }
        return checkpoint
    }

    private suspend fun saveCheckpoint(asset: ModelAsset, file: File, etag: String?) {
        if (file.exists()) {
            repository.saveSetting(
                "checkpoint:${asset.id}",
                gson.toJson(
                    DownloadCheckpoint(etag, file.length(), sha256(file), asset.revision)
                )
            )
        }
    }

    private suspend fun publish(asset: ModelAsset, file: File) {
        repository.upsertModel(asset.copy(status = ModelStatus.VERIFYING, error = null))
        TransferPolicy.validateCompletedTransfer(
            asset.expectedSizeBytes,
            file.length(),
            asset.sha256,
            sha256(file)
        )
        val tree = directory()
        repository.upsertModel(asset.copy(status = ModelStatus.PUBLISHING))
        val extension = BuiltInAssets.safFileExtension(asset)
        val ownedFile = tree.createFile(
            "application/octet-stream",
            "verified-${asset.id}-${UUID.randomUUID()}.$extension"
        )
            ?: error("Provider cannot create model; choose a writable directory.")
        var manifest: DocumentFile? = null
        try {
            context.contentResolver.openOutputStream(ownedFile.uri, "wt")?.use { output ->
                file.inputStream().use { it.copyTo(output, BUFFER_BYTES) }
            } ?: error("Provider cannot write model.")
            verifyPublished(asset, ownedFile.uri)
            val installed = asset.copy(
                status = ModelStatus.INSTALLED,
                localUri = ownedFile.uri.toString(),
                downloadedBytes = asset.expectedSizeBytes,
                progressPercent = 100,
                error = null
            )
            manifest = tree.createFile("application/json", "${asset.id}-${UUID.randomUUID()}.coffee-asset.json")
                ?: error("Provider cannot create asset manifest.")
            context.contentResolver.openOutputStream(manifest.uri, "wt")?.use {
                it.write(gson.toJson(installed).toByteArray(Charsets.UTF_8))
            } ?: error("Provider cannot publish asset manifest.")
            repository.upsertModel(installed)
            file.delete()
            repository.saveSetting("checkpoint:${asset.id}", "")
        } catch (boundaryFailure: Exception) {
            manifest?.delete()
            ownedFile.delete()
            throw boundaryFailure
        }
    }

    suspend fun discover(): Int = withContext(Dispatchers.IO) {
        var count = 0
        val tree = directory()
        val ownedUris = tree.listFiles().map { it.uri.toString() }.toSet()
        tree.listFiles().filter { it.name.orEmpty().endsWith(".coffee-asset.json") }.forEach { manifest ->
            try {
                require(manifest.length() <= MAX_MANIFEST_CHARS) { "Oversized manifest." }
                val json =
                    context.contentResolver.openInputStream(manifest.uri)?.use {
                        it.readBytesBounded(MAX_MANIFEST_CHARS).toString(Charsets.UTF_8)
                    }
                        ?: error("Manifest grant revoked.")
                JsonInputPolicy.validateDepth(json)
                val asset = gson.fromJson(json, ModelAsset::class.java)
                validateManifest(asset)
                val uri = requireNotNull(asset.localUri) { "No published asset URI." }
                require(uri in ownedUris) { "Manifest points outside the selected directory." }
                verifyPublished(asset, Uri.parse(uri))
                repository.upsertModel(asset.copy(status = ModelStatus.INSTALLED, progressPercent = 100, error = null))
                count++
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (boundaryFailure: Exception) {
                repository.saveSetting("assetDiscoveryError", boundaryFailure.message ?: "Invalid asset manifest.")
            }
        }
        count
    }

    suspend fun stageForInference(asset: ModelAsset): File = withContext(Dispatchers.IO) {
        validateManifest(asset)
        require(BuiltInAssets.canLoadForInference(asset)) {
            "This asset is download-only; its inference runtime is not integrated."
        }
        require(asset.status == ModelStatus.INSTALLED && asset.localUri != null) { "Install a verified model first." }
        val target = File(staging, "${asset.id}-${asset.revision}-working.gguf")
        if (!target.exists() || target.length() != asset.expectedSizeBytes || sha256(target) != asset.sha256.lowercase()) {
            require(availableStagingBytes() > asset.expectedSizeBytes + STORAGE_RESERVE) { "Insufficient working-copy storage." }
            context.contentResolver.openInputStream(Uri.parse(asset.localUri))?.use { input ->
                target.outputStream().use { output ->
                    val buffer = ByteArray(BUFFER_BYTES)
                    var bytes = 0L
                    while (true) {
                        currentCoroutineContext().ensureActive()
                        val count = input.read(buffer)
                        if (count < 0) break
                        bytes += count
                        require(bytes <= asset.expectedSizeBytes) { "Published model exceeds manifest size." }
                        output.write(buffer, 0, count)
                    }
                }
            } ?: error("Model grant revoked. Reselect the original directory.")
            require(target.length() == asset.expectedSizeBytes && sha256(target) == asset.sha256.lowercase()) {
                target.delete()
                "Published model failed integrity validation."
            }
        }
        target
    }

    private fun directory(): DocumentFile {
        val uri = repository.safDirectoryUri.value ?: error("Choose a user-owned model directory first.")
        return DocumentFile.fromTreeUri(context, Uri.parse(uri))?.takeIf { it.canRead() && it.canWrite() }
            ?: error("Directory access revoked. Select the directory again.")
    }

    private suspend fun verifyPublished(asset: ModelAsset, uri: Uri) {
        val digest = MessageDigest.getInstance("SHA-256")
        var bytes = 0L
        context.contentResolver.openInputStream(uri)?.use { input ->
            val buffer = ByteArray(BUFFER_BYTES)
            while (true) {
                currentCoroutineContext().ensureActive()
                val count = input.read(buffer)
                if (count < 0) break
                bytes += count
                require(bytes <= asset.expectedSizeBytes) { "Published asset exceeds manifest size." }
                digest.update(buffer, 0, count)
            }
        } ?: error("Published asset access revoked.")
        TransferPolicy.validateCompletedTransfer(
            asset.expectedSizeBytes,
            bytes,
            asset.sha256,
            digest.digest().hex()
        )
    }

    private fun partial(asset: ModelAsset) = File(staging, "${TransferPolicy.safeIdentifier(asset.id)}.part")
    private fun assetLock(id: String) = assetLocks.computeIfAbsent(TransferPolicy.safeIdentifier(id)) { Mutex() }

    private suspend fun <T> withCallCancellation(call: Call, block: suspend () -> T): T = coroutineScope {
        val finished = AtomicBoolean(false)
        val watcher = launch(Dispatchers.IO, start = CoroutineStart.UNDISPATCHED) {
            try {
                awaitCancellation()
            } finally {
                if (!finished.get()) call.cancel()
            }
        }
        try {
            block()
        } finally {
            finished.set(true)
            withContext(NonCancellable) { watcher.cancelAndJoin() }
        }
    }

    companion object {
        private const val NETWORK_TIMEOUT_SECONDS = 30L
        const val MAX_MODEL_BYTES = InputLimits.MODEL_BYTES_MAX
        private const val STORAGE_RESERVE = 64L * 1024 * 1024
        private const val BUFFER_BYTES = 64 * 1024
        private const val PROGRESS_BYTES = 16L * 1024 * 1024
        private const val MAX_MANIFEST_CHARS = 32 * 1024
        fun sha256(file: File): String = file.inputStream().use { input ->
            val digest = MessageDigest.getInstance("SHA-256")
            val buffer = ByteArray(BUFFER_BYTES)
            while (true) {
                val count = input.read(buffer)
                if (count < 0) break
                digest.update(buffer, 0, count)
            }
            digest.digest().hex()
        }
        private fun ByteArray.hex() = joinToString("") { "%02x".format(it) }
    }
}
