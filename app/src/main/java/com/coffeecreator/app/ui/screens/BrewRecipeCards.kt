@file:OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)

package com.coffeecreator.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.coffeecreator.app.core.recipes.champion.ChampionRecipe
import com.coffeecreator.app.core.recipes.champion.RecipeCategory
import com.coffeecreator.app.core.recipes.champion.WorldChampionRecipeCatalog
import com.coffeecreator.app.ui.components.TactileButton
import com.coffeecreator.app.ui.theme.CoffeeCreatorTheme
import java.util.Locale

private const val LARGE_BREW_FONT_SCALE = 1.5f
private const val SECONDS_PER_MINUTE = 60

/** Responsive recipe facts keep source ratios, including bypass instructions, intact. */
@Composable
internal fun ChampionRecipeCard(
    recipe: ChampionRecipe,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onLogToJournal: () -> Unit,
    onBrewOnTimer: () -> Unit,
    onSaveTemplate: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.small)
                    .clickable(onClickLabel = "Brew ${recipe.name}", role = Role.Button, onClick = onBrewOnTimer)
                    .padding(vertical = 4.dp)
                    .testTag("champion:open:${recipe.id}"),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    RecipeBadge(
                        when (recipe.category) {
                            RecipeCategory.COMPETITION -> "Competition"
                            RecipeCategory.SIGNATURE -> "Signature"
                            RecipeCategory.STANDARD -> "Traditional"
                        }
                    )
                    RecipeBadge(recipe.verificationStatus.label)
                }
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        recipe.name,
                        Modifier.weight(1f).semantics { heading() },
                        style = MaterialTheme.typography.titleMedium
                    )
                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                }
                Text(
                    "By ${recipe.baristaOrCreator}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    "${recipe.method} · ${recipe.equipment}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                recipe.podium?.let { podium ->
                    Text(
                        "${podium.competitionName} ${podium.year} · #${podium.rank}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            ChampionRecipeFacts(recipe)
            Text(
                "Grind: ${recipe.grindSize}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (recipe.notes.isNotBlank()) {
                Text(recipe.notes, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (recipe.tags.isNotEmpty()) {
                Text(
                    recipe.tags.joinToString(" · ") { "#$it" },
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            TactileButton(
                onClick = onBrewOnTimer,
                modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp).testTag("champion:brew:${recipe.id}")
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                Text("Brew now", style = MaterialTheme.typography.labelLarge)
            }
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                TextButton(
                    onClick = onLogToJournal,
                    modifier = Modifier.heightIn(min = 48.dp).testTag("champion:log:${recipe.id}")
                ) { Text("Log brew") }
                TextButton(
                    onClick = onSaveTemplate,
                    modifier = Modifier.heightIn(min = 48.dp).testTag("champion:save:${recipe.id}")
                ) { Text("Save template") }
            }
            if (recipe.steps.isNotEmpty()) {
                HorizontalDivider()
                TextButton(
                    onClick = onToggleExpand,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp)
                        .testTag("champion:steps:${recipe.id}")
                        .semantics { stateDescription = if (isExpanded) "Expanded" else "Collapsed" }
                ) {
                    Text(
                        if (isExpanded) "Hide ${recipe.steps.size} brew steps" else "View ${recipe.steps.size} brew steps",
                        modifier = Modifier.weight(1f)
                    )
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                }
                AnimatedVisibility(visible = isExpanded) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        recipe.steps.forEach { step ->
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    "${formatBrewTime(step.startOffsetSeconds)} · ${step.action}",
                                    style = MaterialTheme.typography.titleSmall
                                )
                                Text(
                                    "+${step.waterGrams} g · target ${step.cumulativeWaterGrams} g · ${step.durationSeconds} s",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                if (step.description.isNotBlank()) {
                                    Text(
                                        step.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RecipeBadge(text: String) {
    Surface(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.surfaceContainerHigh) {
        Text(
            text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
internal fun ChampionRecipeFacts(recipe: ChampionRecipe) {
    Column(Modifier.fillMaxWidth().testTag("champion:facts:${recipe.id}"), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        BoxWithConstraints(Modifier.fillMaxWidth()) {
            val stackFacts = maxWidth < 280.dp || LocalDensity.current.fontScale >= LARGE_BREW_FONT_SCALE
            val facts = listOf(
                "Coffee" to "${recipe.doseGrams} g",
                "Brew water" to "${recipe.waterGrams} g",
                "Temperature" to "${recipe.waterTemperatureCelsius} °C",
                "Brew time" to formatBrewTime(recipe.totalTimeSeconds)
            )
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (stackFacts) {
                    facts.forEach { (label, value) -> RecipeFact(label, value, Modifier.fillMaxWidth()) }
                } else {
                    facts.chunked(2).forEach { pair ->
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            pair.forEach { (label, value) -> RecipeFact(label, value, Modifier.weight(1f)) }
                        }
                    }
                }
            }
        }
        RecipeFact("Ratio / bypass", recipe.ratio, Modifier.fillMaxWidth().testTag("champion:ratio:${recipe.id}"))
    }
}

@Composable
private fun RecipeFact(label: String, value: String, modifier: Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyLarge.copy(fontFeatureSettings = "tnum"))
    }
}

internal fun formatBrewTime(seconds: Int): String =
    String.format(Locale.US, "%02d:%02d", seconds / SECONDS_PER_MINUTE, seconds % SECONDS_PER_MINUTE)

@Preview(name = "Champion recipe · narrow", widthDp = 320, showBackground = true)
@Preview(name = "Champion recipe · large text", widthDp = 320, fontScale = 2f, showBackground = true)
@Composable
private fun ChampionRecipePreview() {
    CoffeeCreatorTheme(dynamicColor = false) {
        ChampionRecipeCard(
            recipe = WorldChampionRecipeCatalog.entries.first { it.id == "comp-george-stanica-aeropress" },
            isExpanded = false,
            onToggleExpand = {},
            onLogToJournal = {},
            onBrewOnTimer = {},
            onSaveTemplate = {}
        )
    }
}
