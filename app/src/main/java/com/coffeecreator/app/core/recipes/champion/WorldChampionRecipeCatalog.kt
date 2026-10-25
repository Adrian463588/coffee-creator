package com.coffeecreator.app.core.recipes.champion

/**
 * Catalog of 80 competition, signature, and traditional coffee recipes.
 * All extraction parameters are typed and verified for precision brewing.
 */
object WorldChampionRecipeCatalog {

    val entries: List<ChampionRecipe> = championRecipeSeedEntries

    fun getById(id: String): ChampionRecipe? = entries.find { it.id == id }

    fun filterByCategory(category: RecipeCategory): List<ChampionRecipe> = entries.filter { it.category == category }

    fun filterByMethod(methodQuery: String): List<ChampionRecipe> =
        if (methodQuery.isBlank() || methodQuery.equals("All", ignoreCase = true)) {
            entries
        } else {
            entries.filter { it.method.contains(methodQuery, ignoreCase = true) }
        }

    fun search(query: String): List<ChampionRecipe> {
        val q = query.trim().lowercase()
        if (q.isBlank()) return entries
        return entries.filter {
            it.name.lowercase().contains(q) ||
                it.baristaOrCreator.lowercase().contains(q) ||
                it.method.lowercase().contains(q) ||
                it.equipment.lowercase().contains(q) ||
                it.tags.any { tag -> tag.lowercase().contains(q) }
        }
    }
}
