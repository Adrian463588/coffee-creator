package com.coffeecreator.app.core.filesystem

import com.coffeecreator.app.core.model.InputLimits
import com.coffeecreator.app.core.model.ModelStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

private const val EXPECTED_GEMMA4_LITERT_SIZE_BYTES = 2_588_147_712L
private const val EXPECTED_GEMMA4_LITERT_SHA256 = "181938105e0eefd105961417e8da75903eacda102c4fce9ce90f50b97139a63c"
private const val EXPECTED_GEMMA4_LITERT_REVISION = "6e5c4f1e395deb959c494953478fa5cec4b8008f"

class BuiltInAssetsTest {
    @Test
    fun givenRequestedModels_whenReadingCatalog_thenUseVerifiedImmutableManifestsAndNoInstalledClaim() {
        assertEquals(4, BuiltInAssets.catalog.size)
        assertEquals(4, BuiltInAssets.catalog.map { it.id }.toSet().size)
        BuiltInAssets.catalog.forEach { asset ->
            TransferPolicy.safeIdentifier(asset.id)
            TransferPolicy.safeIdentifier(asset.revision)
            TransferPolicy.validateHash(asset.sha256)
            assertTrue(asset.revision.matches(Regex("[0-9a-f]{40}")))
            assertTrue(asset.downloadUrl.startsWith("https://huggingface.co/") && asset.downloadUrl.contains("/resolve/${asset.revision}/"))
            assertTrue(asset.expectedSizeBytes in 1..InputLimits.MODEL_BYTES_MAX)
            assertEquals(ModelStatus.CATALOGUED, asset.status)
            assertEquals(null, asset.localUri)
            assertEquals(0L, asset.downloadedBytes)
        }
        assertEquals(807694080L, BuiltInAssets.kafi.expectedSizeBytes)
        assertEquals("8b2023ef1c79a7e6c5b4de61db1d720160f12ab2b4db08dbc2ea111c5da0c8bb", BuiltInAssets.kafi.sha256)
        assertEquals(3106738272L, BuiltInAssets.gemma4.expectedSizeBytes)
        assertEquals("740185b21d22ceb83a11c3aa62ad5842ef32c70f6096d756bbee85a1e4ec34b8", BuiltInAssets.gemma4.sha256)
        assertEquals(EXPECTED_GEMMA4_LITERT_SIZE_BYTES, BuiltInAssets.gemma4LiteRt.expectedSizeBytes)
        assertEquals(
            EXPECTED_GEMMA4_LITERT_SHA256,
            BuiltInAssets.gemma4LiteRt.sha256
        )
        assertEquals(EXPECTED_GEMMA4_LITERT_REVISION, BuiltInAssets.gemma4LiteRt.revision)
        assertTrue(BuiltInAssets.gemma4LiteRt.downloadUrl.contains("/resolve/${BuiltInAssets.gemma4LiteRt.revision}/"))
        assertTrue(BuiltInAssets.gemma4LiteRt.downloadUrl.endsWith("/gemma-4-E2B-it.litertlm"))
        assertTrue(BuiltInAssets.gemma4LiteRt.license.contains("Apache-2.0"))
        assertTrue(BuiltInAssets.guidance(BuiltInAssets.kafi) != null)
        assertEquals(null, BuiltInAssets.guidance(BuiltInAssets.kafi.copy(sha256 = "0".repeat(64))))
    }

    @Test
    fun givenGemmaLiteRtBeforeAndAfterInstall_whenCheckingRuntime_thenStayDownloadOnly() {
        val catalogAsset = BuiltInAssets.gemma4LiteRt
        assertTrue(BuiltInAssets.isPinnedAsset(catalogAsset))
        assertEquals(ModelRuntimeCapability.DOWNLOAD_ONLY, BuiltInAssets.runtimeCapability(catalogAsset))
        assertFalse(BuiltInAssets.canLoadForInference(catalogAsset))
        assertEquals("litertlm", BuiltInAssets.safFileExtension(catalogAsset))

        val installedLiteRt = BuiltInAssets.gemma4LiteRt.copy(
            status = ModelStatus.INSTALLED,
            localUri = "content://provider/document/42",
            downloadedBytes = EXPECTED_GEMMA4_LITERT_SIZE_BYTES,
            progressPercent = 100
        )
        assertTrue(
            BuiltInAssets.isPinnedAsset(installedLiteRt)
        )
        assertEquals(
            ModelRuntimeCapability.DOWNLOAD_ONLY,
            BuiltInAssets.runtimeCapability(installedLiteRt)
        )
        assertFalse(BuiltInAssets.canLoadForInference(installedLiteRt))
    }

    @Test
    fun givenBuiltInGguf_whenCheckingRuntime_thenUseExistingLlamaCppCapability() {
        assertEquals(
            ModelRuntimeCapability.LLAMA_CPP_INFERENCE,
            BuiltInAssets.runtimeCapability(BuiltInAssets.gemma4)
        )
        assertEquals("gguf", BuiltInAssets.safFileExtension(BuiltInAssets.gemma4))
        assertTrue(BuiltInAssets.canLoadForInference(BuiltInAssets.gemma4))
    }

    @Test
    fun givenGemmaLiteRtManifest_whenPinnedTransferMetadataChanges_thenRejectCatalogTrust() {
        val asset = BuiltInAssets.gemma4LiteRt
        val tampered = listOf(
            asset.copy(revision = "0".repeat(40)),
            asset.copy(downloadUrl = asset.downloadUrl.replace("/resolve/", "/resolve/main/")),
            asset.copy(expectedSizeBytes = asset.expectedSizeBytes - 1L),
            asset.copy(sha256 = "0".repeat(64)),
            asset.copy(format = "GGUF Q4_K_M"),
            asset.copy(runtime = "llama.cpp"),
            asset.copy(license = "Unknown")
        )

        assertTrue(tampered.all { !BuiltInAssets.isPinnedAsset(it) })
        assertTrue(tampered.all { BuiltInAssets.runtimeCapability(it) == ModelRuntimeCapability.UNSUPPORTED })
    }

    @Test
    fun givenCustomGgufManifest_whenRuntimeIsLlamaCpp_thenRemainLoadable() {
        val customAsset = BuiltInAssets.kafi.copy(id = "user-imported-q4", name = "Imported GGUF")
        val unsupportedAsset = customAsset.copy(tokenizerSupport = "")

        assertEquals(
            ModelRuntimeCapability.LLAMA_CPP_INFERENCE,
            BuiltInAssets.runtimeCapability(customAsset)
        )
        assertTrue(BuiltInAssets.canLoadForInference(customAsset))
        assertEquals(ModelRuntimeCapability.UNSUPPORTED, BuiltInAssets.runtimeCapability(unsupportedAsset))
        assertFalse(BuiltInAssets.canLoadForInference(unsupportedAsset))
    }
}
