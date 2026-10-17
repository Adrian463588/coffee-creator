package com.coffeecreator.app.core.recipes.champion

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class WorldChampionRecipeCatalogTest {

    @Test
    fun `Catalog contains exactly 80 recipes across competition, signature and traditional categories`() {
        val recipes = WorldChampionRecipeCatalog.entries
        assertEquals(80, recipes.size)

        val competition = recipes.filter { it.category == RecipeCategory.COMPETITION }
        val signature = recipes.filter { it.category == RecipeCategory.SIGNATURE }
        val standard = recipes.filter { it.category == RecipeCategory.STANDARD }

        assertEquals(21, competition.size)
        assertEquals(14, signature.size)
        assertEquals(45, standard.size)
    }

    @Test
    fun `Every champion recipe has valid physical extraction quantities and attributes`() {
        WorldChampionRecipeCatalog.entries.forEach { recipe ->
            assertTrue("Recipe ${recipe.id} must have non-blank name", recipe.name.isNotBlank())
            assertTrue("Recipe ${recipe.id} must have non-blank creator", recipe.baristaOrCreator.isNotBlank())
            assertTrue("Recipe ${recipe.id} must have non-blank method", recipe.method.isNotBlank())
            assertTrue("Recipe ${recipe.id} must have non-blank equipment", recipe.equipment.isNotBlank())
            assertTrue("Recipe ${recipe.id} dose must be positive", recipe.doseGrams in 5.0..150.0)
            assertTrue("Recipe ${recipe.id} water must be positive", recipe.waterGrams in 20.0..2000.0)
            assertTrue("Recipe ${recipe.id} temp must be positive", recipe.waterTemperatureCelsius in 0.0..100.0)
            assertTrue("Recipe ${recipe.id} time must be positive", recipe.totalTimeSeconds > 0)
            assertNotNull(recipe.verificationStatus)
        }
    }

    @Test
    fun `Converting champion recipe to BrewJournalEntry retains provenance, rating and exact units`() {
        val kasuya = WorldChampionRecipeCatalog.getById("comp-tetsu-kasuya-46")
        assertNotNull(kasuya)
        val journalEntry = kasuya!!.toBrewJournalEntry(beanName = "Colombia Huila Pink Bourbon")

        assertEquals("comp-tetsu-kasuya-46", journalEntry.recipeId)
        assertEquals("Colombia Huila Pink Bourbon", journalEntry.beanName)
        assertEquals("Pour Over (V60)", journalEntry.brewMethod)
        assertEquals(20.0, journalEntry.doseGrams, 0.001)
        assertEquals(300.0, journalEntry.waterGrams, 0.001)
        assertEquals(92.0, journalEntry.waterTemperatureCelsius ?: 0.0, 0.001)
        assertEquals(210, journalEntry.brewDurationSeconds)
        assertEquals(9.5, journalEntry.rating ?: 0.0, 0.001)
        assertTrue(journalEntry.generalNotes.contains("Tetsu Kasuya"))
        assertTrue(journalEntry.tags.contains("champion-catalog"))
        assertTrue(journalEntry.tags.contains("competition"))
    }

    @Test
    fun `Converting champion recipe to Recipe builds valid brew steps and target ratio`() {
        val martin = WorldChampionRecipeCatalog.getById("comp-martin-wolfl-orea-v4")
        assertNotNull(martin)
        val recipe = martin!!.toRecipe()

        assertEquals("Martin Wölfl Orea V4 Apex", recipe.name)
        assertEquals("Flat Bed (Orea V4)", recipe.brewMethod)
        assertEquals(17.0, recipe.doseGrams, 0.001)
        assertEquals(270.0, recipe.waterGrams, 0.001)
        assertEquals(4, recipe.brewSteps.size)
        assertEquals(60.0, recipe.brewSteps[0].targetWaterGrams, 0.001)
        assertEquals(100.0, recipe.brewSteps[3].targetWaterGrams, 0.001)
        assertEquals(270.0, recipe.brewSteps[3].cumulativeMassGrams ?: 0.0, 0.001)
    }

    @Test
    fun `Search and filtering works reliably across all dimensions`() {
        val aeropressRecipes = WorldChampionRecipeCatalog.filterByMethod("AeroPress")
        assertTrue(aeropressRecipes.isNotEmpty())
        assertTrue(aeropressRecipes.all { it.method.contains("AeroPress", ignoreCase = true) })

        val hoffmannSearch = WorldChampionRecipeCatalog.search("Hoffmann")
        assertEquals(4, hoffmannSearch.size)

        val tubrukSearch = WorldChampionRecipeCatalog.search("Tubruk")
        assertEquals(2, tubrukSearch.size)
        assertTrue(tubrukSearch.any { it.id == "std-kopi-tubruk" })
    }
}
