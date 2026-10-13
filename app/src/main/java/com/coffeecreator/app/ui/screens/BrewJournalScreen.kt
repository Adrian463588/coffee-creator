package com.coffeecreator.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.coffeecreator.app.core.data.DataRepository
import com.coffeecreator.app.core.model.Recipe

@Composable
fun BrewJournalScreen(
    onSelectRecipeForDial: (Recipe) -> Unit
) {
    val recipes by DataRepository.recipes.collectAsState()
    val observations by DataRepository.sensoryObservations.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Brew Journal & History",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Repeatable brew logs, version revisions, and linked cupping observations.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (recipes.isEmpty()) {
            item {
                Text(
                    text = "No brew logs found in the journal.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            items(recipes) { recipe ->
                val linkedObs = observations.filter { it.recipeId == recipe.id }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = recipe.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Badge(containerColor = MaterialTheme.colorScheme.primaryContainer) {
                                Text("Revision v${recipe.version}")
                            }
                        }

                        Text(
                            text = "Method: ${recipe.brewMethod} • Target Ratio: ${recipe.targetRatio} • Temp: ${recipe.temperatureCelsius}°C",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Text(
                            text = "Dose: ${recipe.doseGrams}g | Water: ${recipe.waterGrams}g | Cup: ${recipe.beverageMassGrams}g @ ${recipe.measuredTdsPercent ?: "Unmeasured"}% TDS",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )

                        if (recipe.notes.isNotBlank()) {
                            Text(
                                text = "Notes: ${recipe.notes}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        if (linkedObs.isNotEmpty()) {
                            val obs = linkedObs.last()
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text(
                                        text = "Sensory Log: Aroma ${obs.aroma}/10, Acidity ${obs.acidity}/10, Sweetness ${obs.sweetness}/10, Body ${obs.body}/10",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    if (obs.notes.isNotBlank()) {
                                        Text(text = "\"${obs.notes}\"", style = MaterialTheme.typography.labelSmall)
                                    }
                                }
                            }
                        }

                        // Actions
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { DataRepository.duplicateRecipe(recipe) },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Clone")
                            }

                            FilledTonalButton(
                                onClick = { onSelectRecipeForDial(recipe) },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Redial Brew")
                            }

                            IconButton(
                                onClick = { DataRepository.deleteRecipe(recipe.id) }
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete recipe", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
    }
}
