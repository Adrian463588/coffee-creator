package com.coffeecreator.app.core.filesystem

import com.coffeecreator.app.core.model.InputLimits
import com.coffeecreator.app.core.model.ModelStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BuiltInAssetsTest {
    @Test
    fun givenRequestedModels_whenReadingCatalog_thenUseVerifiedImmutableManifestsAndNoInstalledClaim() {
        assertEquals(3, BuiltInAssets.catalog.size)
        assertEquals(3, BuiltInAssets.catalog.map { it.id }.toSet().size)
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
        assertTrue(BuiltInAssets.guidance(BuiltInAssets.kafi) != null)
        assertEquals(null, BuiltInAssets.guidance(BuiltInAssets.kafi.copy(sha256 = "0".repeat(64))))
    }
}
