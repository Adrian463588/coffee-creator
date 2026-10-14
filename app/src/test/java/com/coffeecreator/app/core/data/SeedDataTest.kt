package com.coffeecreator.app.core.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SeedDataTest {
    @Test
    fun givenFreshInstall_whenSeeded_thenNoMeasurementsOrSupplierClaimsAreInvented() {
        assertTrue(SeedData.sampleRecipes.isEmpty())
        assertTrue(SeedData.curatedDocuments.isEmpty())
        assertTrue(SeedData.curatedChunks.isEmpty())
        assertTrue(SeedData.curatedModels.isEmpty())
        SeedData.curatedBeans.forEach {
            assertEquals("Unknown", it.producer)
            assertEquals("Unknown", it.process)
            assertEquals("Unknown", it.roastLevel)
            assertNull(it.densityGramsPerMl)
            assertNull(it.foodGrade)
            assertTrue(it.provenance.contains("https://varieties.worldcoffeeresearch.org/arabica"))
        }
    }
}
