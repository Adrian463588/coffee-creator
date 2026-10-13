package com.coffeecreator.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.coffeecreator.app.core.data.DataRepository
import com.coffeecreator.app.core.engine.SensoryRulesEngine
import com.coffeecreator.app.core.model.SensoryObservation
import java.util.UUID

@Composable
fun SensoryScreen() {
    val recipes by DataRepository.recipes.collectAsState()
    val beans by DataRepository.beans.collectAsState()

    var selectedRecipeId by remember { mutableStateOf(recipes.firstOrNull()?.id ?: "") }
    val currentRecipe = recipes.find { it.id == selectedRecipeId } ?: recipes.firstOrNull()
    val currentBean = beans.find { it.id == currentRecipe?.beanId }

    // Observed attributes (1-10)
    var aroma by remember { mutableFloatStateOf(8f) }
    var acidity by remember { mutableFloatStateOf(7f) }
    var sweetness by remember { mutableFloatStateOf(8f) }
    var body by remember { mutableFloatStateOf(6f) }
    var finish by remember { mutableFloatStateOf(7f) }
    var balance by remember { mutableFloatStateOf(8f) }
    var notesText by remember { mutableStateOf("Clean floral fragrance, crisp malic acidity, cane sugar sweetness.") }
    var saveSuccessMsg by remember { mutableStateOf<String?>(null) }

    // Estimated direction from rule engine
    val estimate = remember(currentBean, currentRecipe) {
        if (currentRecipe != null) {
            val ey = (currentRecipe.beverageMassGrams * (currentRecipe.measuredTdsPercent ?: 1.30)) / currentRecipe.doseGrams
            SensoryRulesEngine.estimateProfile(currentBean, currentRecipe, ey)
        } else {
            null
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Sensory Tracking & Evaluation",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Record empirical human cupping observations and inspect rule-based directional estimates.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item {
            // Recipe selection
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                recipes.take(3).forEach { rec ->
                    FilterChip(
                        selected = selectedRecipeId == rec.id,
                        onClick = { selectedRecipeId = rec.id },
                        label = { Text(rec.name) }
                    )
                }
            }
        }

        // Observed attribute sliders
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Observed Tasting Attributes (Assessor: Head Barista)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    SensorySlider(label = "Aroma / Fragrance", value = aroma, onValueChange = { aroma = it })
                    SensorySlider(label = "Acidity / Brightness", value = acidity, onValueChange = { acidity = it })
                    SensorySlider(label = "Sweetness", value = sweetness, onValueChange = { sweetness = it })
                    SensorySlider(label = "Body / Mouthfeel", value = body, onValueChange = { body = it })
                    SensorySlider(label = "Finish / Aftertaste", value = finish, onValueChange = { finish = it })
                    SensorySlider(label = "Overall Balance", value = balance, onValueChange = { balance = it })

                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = notesText,
                        onValueChange = { notesText = it },
                        label = { Text("Cupping Notes & Descriptors") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }
        }

        // Estimated Direction Overlay
        estimate?.let { est ->
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Estimated Direction (Heuristic Rule-Engine)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = "Model Heuristic Comparison:\n• Aroma: ${est.aroma}/10 vs Observed: ${aroma.toInt()}/10\n• Acidity: ${est.acidity}/10 vs Observed: ${acidity.toInt()}/10\n• Sweetness: ${est.sweetness}/10 vs Observed: ${sweetness.toInt()}/10\n• Body: ${est.body}/10 vs Observed: ${body.toInt()}/10",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Text(
                            text = "Predicted Flavor Direction: " + est.suggestedDescriptors.joinToString(", "),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Text(
                            text = est.uncertaintyDisclaimer,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        item {
            Button(
                onClick = {
                    if (currentRecipe != null) {
                        DataRepository.saveSensoryObservation(
                            SensoryObservation(
                                id = UUID.randomUUID().toString(),
                                recipeId = currentRecipe.id,
                                aroma = aroma.toInt(),
                                acidity = acidity.toInt(),
                                sweetness = sweetness.toInt(),
                                body = body.toInt(),
                                finish = finish.toInt(),
                                balance = balance.toInt(),
                                notes = notesText
                            )
                        )
                        saveSuccessMsg = "Saved sensory cupping observation for '${currentRecipe.name}'."
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Record Sensory Observation")
            }
        }

        saveSuccessMsg?.let { msg ->
            item {
                Text(
                    text = msg,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun SensorySlider(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, style = MaterialTheme.typography.bodyMedium)
            Text(text = "${value.toInt()}/10", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = 1f..10f,
            steps = 8
        )
    }
}
