package com.coffeecreator.app.core.data

import com.coffeecreator.app.core.engine.DomainValidation
import java.nio.file.Files
import java.nio.file.Paths
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class ReferenceCatalogTest {
    @Test
    fun givenBundledReferences_whenValidated_thenIdsAndFactSourcesAreCompleteAndUnique() {
        val entries = ReferenceCatalog.entries
        assertEquals(32, entries.size)
        assertEquals(entries.size, entries.map { it.id }.toSet().size)
        assertEquals(entries.size, entries.map { it.sourceUrl }.toSet().size)
        entries.forEach { entry ->
            ReferenceCatalog.validate(entry)
            val bean = entry.toBean()
            DomainValidation.validateBean(bean)
            assertTrue(bean.isCuratedSeed)
            assertEquals("Unknown", bean.country)
            assertEquals("Unknown", bean.region)
            assertEquals("Unknown", bean.producer)
            assertEquals("Unknown", bean.process)
            assertEquals("Unknown", bean.roastLevel)
            assertEquals("Unknown", bean.allergens)
            assertNull(bean.altitudeMeters)
            assertNull(bean.densityGramsPerMl)
            assertNull(bean.foodGrade)
            assertTrue(bean.provenance.contains(entry.sourceUrl))
            assertTrue(bean.provenance.contains(entry.sourceSha256))
            assertTrue(bean.provenance.contains("not a supplier lot"))
        }
    }

    @Test
    fun givenReviewedDataset_whenReceiptIsRead_thenEveryFieldMatchesBundledRuntimeRecords() {
        val root = if (Files.exists(Paths.get("src/main"))) Paths.get("") else Paths.get("app")
        val json = String(Files.readAllBytes(root.resolve("src/main/assets/reference/varieties-v3.json")), Charsets.UTF_8)
        val receipt = RecordJson.gson.fromJson(json, ReferenceCatalogReceipt::class.java)
        assertEquals(3, receipt.version)
        assertEquals("2026-10-15", receipt.retrievedAt)
        assertEquals(ReferenceCatalog.entries, receipt.entries)
    }

    @Test
    fun givenMalformedReference_whenValidated_thenMissingEvidenceAndInventedLinksAreRejected() {
        val first = ReferenceCatalog.entries.first()
        listOf(
            first.copy(sourceSha256 = "unknown"),
            first.copy(sourceUrl = "https://example.com/not-a-primary-source"),
            first.copy(name = ""),
            first.copy(retrievedAt = "later"),
            first.copy(geneticGroup = ""),
            first.copy(id = "../unsafe")
        ).forEach { malformed ->
            assertThrows(IllegalArgumentException::class.java) { ReferenceCatalog.validate(malformed) }
        }
    }

    @Test
    fun givenLegacySeeds_whenPlanningUpgrade_thenOnlyNewIdsAreOfferedAndNoLegacyDeletionIsResurrected() {
        val additions = SeedData.beansIntroducedAfter(2)
        assertEquals(32, additions.size)
        assertFalse(additions.any { it.id == "reference-arabica-ethiopia" || it.id == "reference-arabica-yemen" })
        assertTrue(SeedData.beansIntroducedAfter(3).isEmpty())
        assertEquals(34, SeedData.curatedBeans.size)
    }
}
