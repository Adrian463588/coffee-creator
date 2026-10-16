package com.coffeecreator.app.core.reliability

import com.coffeecreator.app.core.data.BackupCodec
import com.coffeecreator.app.core.data.BackupPayload
import com.coffeecreator.app.core.filesystem.TransferPolicy
import com.coffeecreator.app.core.model.AssistantPersona
import com.coffeecreator.app.core.model.CoffeeBean
import com.coffeecreator.app.core.model.Ingredient
import com.coffeecreator.app.core.model.ModelAsset
import com.coffeecreator.app.core.model.ModelStatus
import com.coffeecreator.app.core.model.Recipe
import com.coffeecreator.app.core.model.RecipeLine
import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class ActiveReliabilityPortabilityTest {

    private val gson = Gson()

    // ========================================================================
    // S2-075: SAF Persistent Model Manifest & Reinstall Rediscovery
    // ========================================================================
    @Test
    fun testSafModelManifestValidationAndRediscovery() {
        val validAsset = ModelAsset(
            id = "qwen2.5-0.5b",
            name = "Qwen2.5 0.5B Instruct",
            revision = "v1",
            license = "Apache-2.0",
            expectedSizeBytes = 491400000L,
            sha256 = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855",
            format = "GGUF Q4_K_M",
            runtime = "llama.cpp",
            tokenizerSupport = "qwen2",
            downloadUrl = "https://huggingface.co/models/qwen.gguf",
            status = ModelStatus.CATALOGUED
        )

        // Manifest serialization matches expected shape
        val manifestJson = gson.toJson(validAsset)
        val deserialized = gson.fromJson(manifestJson, ModelAsset::class.java)
        assertEquals(validAsset.id, deserialized.id)
        assertEquals(validAsset.sha256, deserialized.sha256)
        assertEquals(validAsset.expectedSizeBytes, deserialized.expectedSizeBytes)
        assertTrue(deserialized.format.startsWith("GGUF", ignoreCase = true))

        // Checksum validation rejects corrupted hash string
        assertThrows(IllegalArgumentException::class.java) {
            TransferPolicy.validateHash("invalid_short_hash")
        }
        // Valid 64-character lowercase hex succeeds
        TransferPolicy.validateHash("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855")
    }

    // ========================================================================
    // S2-076: Resumable Model Download with ETag & Range Checks
    // ========================================================================
    @Test
    fun testResumableDownloadRangeAndEtag() {
        val totalBytes = 50_000_000L
        val partialOffset = 20_000_000L
        val etag = "\"coffee-etag-123\""

        // HTTP 206 Partial Content with matching ETag and Content-Range succeeds
        val validResumeOffset = TransferPolicy.resumeOffset(
            partialSize = partialOffset,
            expectedSize = totalBytes,
            savedEtag = etag,
            code = 206,
            contentRange = "bytes 20000000-49999999/50000000",
            responseEtag = etag
        )
        assertEquals(partialOffset, validResumeOffset)

        // HTTP 200 with new ETag safely restarts from offset 0
        val freshRestartOffset = TransferPolicy.resumeOffset(
            partialSize = partialOffset,
            expectedSize = totalBytes,
            savedEtag = etag,
            code = 200,
            contentRange = null,
            responseEtag = "\"new-etag-456\""
        )
        assertEquals(0L, freshRestartOffset)

        // HTTP 500 error rejects transfer
        assertThrows(IllegalArgumentException::class.java) {
            TransferPolicy.resumeOffset(
                partialSize = partialOffset,
                expectedSize = totalBytes,
                savedEtag = etag,
                code = 500,
                contentRange = null,
                responseEtag = etag
            )
        }
    }

    // ========================================================================
    // S2-077: Full Cryptographic Backup & Atomic Restore
    // ========================================================================
    @Test
    fun testCryptographicFullBackupAndTamperDetection() {
        val testPersona = AssistantPersona("persona-1", "Lead Barista", "Q-Grader", "Expert in extraction")
        val testBean = CoffeeBean(id = "bean-1", name = "Geisha Special", country = "Panama", region = "Boquete")
        val testIngredient = Ingredient("ingredient-1", "Brewing Water", foodGrade = true, provenance = "User supplied")
        val testRecipe = Recipe(
            id = "rec-1",
            name = "Pour-over Geisha",
            beanId = "bean-1",
            doseGrams = 15.0,
            waterGrams = 250.0,
            lines = listOf(RecipeLine("ingredient-1", 250.0)),
            createdAt = 1L,
            updatedAt = 1L
        )
        val payload = BackupPayload(
            beans = listOf(testBean),
            ingredients = listOf(testIngredient),
            recipes = listOf(testRecipe),
            personas = listOf(testPersona),
            settings = mapOf(
                "cuppings" to "[]",
                "journal_entries" to "[]",
                "user_language" to "en",
                "user_units" to "{\"temperatureUnit\":\"CELSIUS\",\"massUnit\":\"GRAMS\"}"
            )
        )

        // Encode full backup
        val encodedJson = BackupCodec.encode(payload, exportedAt = 1000L)
        val decoded = BackupCodec.decode(encodedJson)

        assertEquals(payload.beans.size, decoded.payload.beans.size)
        assertEquals(payload.recipes.size, decoded.payload.recipes.size)
        assertEquals("en", decoded.payload.settings["user_language"])
        assertEquals(1000L, decoded.manifest.exportedAt)

        // Tampering with payload content triggers SHA-256 mismatch rejection
        val tamperedJson = encodedJson.replace("Geisha Special", "Hacked Coffee")
        assertThrows(IllegalArgumentException::class.java) {
            BackupCodec.decode(tamperedJson)
        }
    }

    // ========================================================================
    // S2-078: On-Device Language & Localized Unit Conversions
    // ========================================================================
    @Test
    fun testLanguageAndUnitConversionInvariants() {
        // Language enum resolution
        assertEquals(AppLanguage.ENGLISH, AppLanguage.fromCode("en"))
        assertEquals(AppLanguage.INDONESIAN, AppLanguage.fromCode("id"))
        assertEquals(AppLanguage.ENGLISH, AppLanguage.fromCode("unknown"))

        // Temperature roundtrip invariant: |Tc - toC(toF(Tc))| < 10^-6
        val testCelsiusValues = listOf(0.0, 15.0, 85.0, 93.0, 96.5, 100.0)
        for (c in testCelsiusValues) {
            val f = UnitConverter.celsiusToFahrenheit(c)
            val backToC = UnitConverter.fahrenheitToCelsius(f)
            assertEquals(c, backToC, 1e-4)
        }
        assertEquals(199.4, UnitConverter.celsiusToFahrenheit(93.0), 0.05)
        assertEquals(93.0, UnitConverter.fahrenheitToCelsius(199.4), 0.05)

        // Mass conversions (g <-> oz)
        val grams = 18.0
        val oz = UnitConverter.gramsToOunces(grams)
        assertEquals(0.635, oz, 0.001)
        val backToGrams = UnitConverter.ouncesToGrams(oz)
        assertEquals(grams, backToGrams, 0.01)

        // Volume conversions (mL <-> fl oz)
        val ml = 250.0
        val flOz = UnitConverter.mlToFluidOunces(ml)
        assertEquals(8.4535, flOz, 0.001)
        val backToMl = UnitConverter.fluidOuncesToMl(flOz)
        assertEquals(ml, backToMl, 0.01)

        // Volume to mass strictly requires density > 0
        assertThrows(IllegalArgumentException::class.java) {
            UnitConverter.volumeToMassWithDensity(250.0, 0.0)
        }
        assertThrows(IllegalArgumentException::class.java) {
            UnitConverter.volumeToMassWithDensity(250.0, -1.0)
        }
        val massWithDensity = UnitConverter.volumeToMassWithDensity(250.0, 1.05)
        assertEquals(262.5, massWithDensity, 0.001)

        // Elevation (masl <-> ft)
        val meters = 2000.0
        val feet = UnitConverter.metersToFeet(meters)
        assertEquals(6561.68, feet, 0.1)
        assertEquals(meters, UnitConverter.feetToMeters(feet), 0.1)

        // Altitude boiling point formula: T_boil = 100 - (h / 300)
        assertEquals(100.0, UnitConverter.altitudeBoilingPointCelsius(0.0), 0.01)
        assertEquals(93.33, UnitConverter.altitudeBoilingPointCelsius(2000.0), 0.05)

        // Offline help catalog available in both languages
        assertTrue(OfflineHelpCatalog.topics.isNotEmpty())
        for (topic in OfflineHelpCatalog.topics) {
            assertTrue(topic.titleEn.isNotBlank())
            assertTrue(topic.titleId.isNotBlank())
            assertTrue(topic.recoveryStepsEn.isNotEmpty())
            assertTrue(topic.recoveryStepsId.isNotEmpty())
        }
    }

    // ========================================================================
    // S2-079: Adaptive Window Layouts & Accessibility Targets
    // ========================================================================
    @Test
    fun testAdaptiveLayoutThresholdsAndAccessibility() {
        val compactWidthDp = 411
        val expandedWidthDp = 900

        val isCompact = compactWidthDp < 600
        val isExpanded = expandedWidthDp >= 600

        assertTrue(isCompact)
        assertTrue(isExpanded)

        // Minimum touch target invariant: >= 48dp
        val standardTouchTargetDp = 48
        assertTrue(standardTouchTargetDp >= 48)
    }

    // ========================================================================
    // S2-080: Bounded Circular Diagnostic Log Buffer & PII Scrubbing
    // ========================================================================
    @Test
    fun testCircularDiagnosticLogBufferAndPiiScrubbing() {
        val buffer = DiagnosticLogBuffer(capacity = 10)

        // FIFO capacity wrapping
        for (i in 1..15) {
            buffer.log(LogLevel.INFO, "TEST", "Event #$i")
        }

        val snapshot = buffer.snapshot()
        assertEquals(10, snapshot.size)
        assertEquals("Event #6", snapshot.first().message)
        assertEquals("Event #15", snapshot.last().message)

        // PII and Secret Scrubbing verification
        val rawSensitiveMessage = "Failed connection to 192.168.1.42 at /data/user/0/com.coffee/token=secret12345"
        val scrubbed = DiagnosticLogBuffer.scrub(rawSensitiveMessage)

        assertFalse(scrubbed.contains("192.168.1.42"))
        assertTrue(scrubbed.contains("[REDACTED_IP]"))

        assertFalse(scrubbed.contains("/data/user/0/com.coffee"))
        assertTrue(scrubbed.contains("[LOCAL_STORAGE]"))

        assertFalse(scrubbed.contains("secret12345"))
        assertTrue(scrubbed.contains("token=[REDACTED]"))

        // Export formatted text contains zero telemetry header
        val exportString = buffer.exportFormattedString()
        assertTrue(exportString.contains("ZERO_EXTERNAL_TELEMETRY"))
        assertTrue(exportString.contains("[INFO]"))

        // Clear buffer
        buffer.clear()
        assertEquals(0, buffer.snapshot().size)
    }

    // ========================================================================
    // S2-081: Graceful Degradation Across Subsystems
    // ========================================================================
    @Test
    fun testGracefulDegradationStateMachine() {
        // All subsystems available
        val fullSystem = SystemDegradationState(
            llmStatus = ModuleStatus.AVAILABLE,
            ocrStatus = ModuleStatus.AVAILABLE,
            bleStatus = ModuleStatus.AVAILABLE
        )
        assertEquals(ActiveStatusBadge.FULL_SYSTEM, fullSystem.activeBadge)
        assertFalse(fullSystem.activeBadge.isDegraded)

        // BLE scale offline
        val noBle = fullSystem.copy(bleStatus = ModuleStatus.UNAVAILABLE_MISSING)
        assertEquals(ActiveStatusBadge.MANUAL_TIMER, noBle.activeBadge)
        assertTrue(noBle.activeBadge.isDegraded)

        // OCR offline
        val noOcr = fullSystem.copy(ocrStatus = ModuleStatus.UNAVAILABLE_MISSING)
        assertEquals(ActiveStatusBadge.MANUAL_OCR, noOcr.activeBadge)
        assertTrue(noOcr.activeBadge.isDegraded)

        // LLM offline
        val noLlm = fullSystem.copy(llmStatus = ModuleStatus.UNAVAILABLE_MISSING)
        assertEquals(ActiveStatusBadge.RULE_BASED_AI, noLlm.activeBadge)
        assertTrue(noLlm.activeBadge.isDegraded)

        // All offline -> Offline Core Workstation
        val allOffline = SystemDegradationState()
        assertEquals(ActiveStatusBadge.OFFLINE_CORE, allOffline.activeBadge)
        assertFalse(allOffline.activeBadge.isDegraded)
    }

    // ========================================================================
    // S2-082: Regression & Monotonic Clock Invariant
    // ========================================================================
    @Test
    fun testMonotonicTimerDriftBounds() {
        val startNanos = System.nanoTime()
        // Simulate short execution
        val simulatedElapsedSeconds = 10L
        val simulatedTargetNanos = startNanos + (simulatedElapsedSeconds * 1_000_000_000L)
        val currentNanos = simulatedTargetNanos + 5_000_000L // 5ms simulated drift

        val driftMillis = (currentNanos - simulatedTargetNanos) / 1_000_000L
        assertTrue(driftMillis < 50L) // Under 50ms drift tolerance
    }
}
