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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.coffeecreator.app.ui.viewmodel.SensoryIntent
import com.coffeecreator.app.ui.viewmodel.SensoryViewModel

@Composable
fun SensoryScreen(
    viewModel: SensoryViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                state.recipes.take(3).forEach { rec ->
                    FilterChip(
                        selected = state.selectedRecipeId == rec.id,
                        onClick = { viewModel.dispatch(SensoryIntent.SelectRecipe(rec.id)) },
                        label = { Text(rec.name) }
                    )
                }
            }
        }

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

                    SensorySlider(label = "Aroma / Fragrance", value = state.aroma, onValueChange = { viewModel.dispatch(SensoryIntent.UpdateAroma(it)) })
                    SensorySlider(label = "Acidity / Brightness", value = state.acidity, onValueChange = { viewModel.dispatch(SensoryIntent.UpdateAcidity(it)) })
                    SensorySlider(label = "Sweetness", value = state.sweetness, onValueChange = { viewModel.dispatch(SensoryIntent.UpdateSweetness(it)) })
                    SensorySlider(label = "Body / Mouthfeel", value = state.body, onValueChange = { viewModel.dispatch(SensoryIntent.UpdateBody(it)) })
                    SensorySlider(label = "Finish / Aftertaste", value = state.finish, onValueChange = { viewModel.dispatch(SensoryIntent.UpdateFinish(it)) })
                    SensorySlider(label = "Overall Balance", value = state.balance, onValueChange = { viewModel.dispatch(SensoryIntent.UpdateBalance(it)) })

                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = state.notes,
                        onValueChange = { viewModel.dispatch(SensoryIntent.UpdateNotes(it)) },
                        label = { Text("Cupping Notes & Descriptors") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }
        }

        state.estimate?.let { est ->
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
                            text = "Model Heuristic Comparison:\n• Aroma: ${est.aroma}/10 vs Observed: ${state.aroma.toInt()}/10\n• Acidity: ${est.acidity}/10 vs Observed: ${state.acidity.toInt()}/10\n• Sweetness: ${est.sweetness}/10 vs Observed: ${state.sweetness.toInt()}/10\n• Body: ${est.body}/10 vs Observed: ${state.body.toInt()}/10",
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
                onClick = { viewModel.dispatch(SensoryIntent.SaveObservation) },
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

        state.saveSuccessMsg?.let { msg ->
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
