package com.coffeecreator.app.ui.screens

import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.coffeecreator.app.ui.viewmodel.SensoryIntent
import com.coffeecreator.app.ui.viewmodel.SensoryViewModel
import kotlin.math.cos
import kotlin.math.sin

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
                    SensorySlider(label = "Bitterness", value = state.bitterness, onValueChange = { viewModel.dispatch(SensoryIntent.UpdateBitterness(it)) })
                    SensorySlider(label = "Body / Mouthfeel", value = state.body, onValueChange = { viewModel.dispatch(SensoryIntent.UpdateBody(it)) })
                    SensorySlider(label = "Clarity / Translucency", value = state.clarity, onValueChange = { viewModel.dispatch(SensoryIntent.UpdateClarity(it)) })
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

        // Radar chart visualization
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Sensory Radar Profile",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    RadarFlavorChart(
                        observed = listOf(state.aroma, state.acidity, state.sweetness, state.bitterness, state.body, state.clarity, state.finish, state.balance),
                        estimated = state.estimate?.let {
                            listOf(it.aroma.toFloat(), it.acidity.toFloat(), it.sweetness.toFloat(), it.bitterness.toFloat(), it.body.toFloat(), it.clarity.toFloat(), it.finish.toFloat(), it.balance.toFloat())
                        },
                        labels = listOf("Aroma", "Acidity", "Sweet", "Bitter", "Body", "Clarity", "Finish", "Balance")
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
                            text = "Model Heuristic Comparison:\n• Aroma: ${est.aroma}/10 vs Observed: ${state.aroma.toInt()}/10\n• Acidity: ${est.acidity}/10 vs Observed: ${state.acidity.toInt()}/10\n• Sweetness: ${est.sweetness}/10 vs Observed: ${state.sweetness.toInt()}/10\n• Bitterness: ${est.bitterness}/10 vs Observed: ${state.bitterness.toInt()}/10\n• Body: ${est.body}/10 vs Observed: ${state.body.toInt()}/10\n• Clarity: ${est.clarity}/10 vs Observed: ${state.clarity.toInt()}/10",
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

@Composable
fun RadarFlavorChart(
    observed: List<Float>,
    estimated: List<Float>?,
    labels: List<String>,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.tertiary
    val outlineColor = MaterialTheme.colorScheme.outlineVariant

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(12.dp)
    ) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = minOf(size.width, size.height) / 2.2f
        val count = labels.size
        val angleStep = (2 * Math.PI / count).toFloat()

        // Draw web rings
        for (step in 1..4) {
            val ringRadius = radius * (step / 4f)
            val ringPath = Path()
            for (i in 0 until count) {
                val angle = i * angleStep - (Math.PI / 2).toFloat()
                val x = center.x + ringRadius * cos(angle)
                val y = center.y + ringRadius * sin(angle)
                if (i == 0) ringPath.moveTo(x, y) else ringPath.lineTo(x, y)
            }
            ringPath.close()
            drawPath(ringPath, color = outlineColor, style = Stroke(width = 1.dp.toPx()))
        }

        // Draw axis spokes
        for (i in 0 until count) {
            val angle = i * angleStep - (Math.PI / 2).toFloat()
            val end = Offset(center.x + radius * cos(angle), center.y + radius * sin(angle))
            drawLine(color = outlineColor, start = center, end = end, strokeWidth = 1.dp.toPx())
        }

        // Draw estimated polygon if available
        if (estimated != null && estimated.size == count) {
            val estPath = Path()
            for (i in 0 until count) {
                val fraction = (estimated[i] / 10f).coerceIn(0f, 1f)
                val angle = i * angleStep - (Math.PI / 2).toFloat()
                val x = center.x + radius * fraction * cos(angle)
                val y = center.y + radius * fraction * sin(angle)
                if (i == 0) estPath.moveTo(x, y) else estPath.lineTo(x, y)
            }
            estPath.close()
            drawPath(estPath, color = secondaryColor.copy(alpha = 0.2f), style = Fill)
            drawPath(estPath, color = secondaryColor, style = Stroke(width = 2.dp.toPx()))
        }

        // Draw observed polygon
        if (observed.size == count) {
            val obsPath = Path()
            for (i in 0 until count) {
                val fraction = (observed[i] / 10f).coerceIn(0f, 1f)
                val angle = i * angleStep - (Math.PI / 2).toFloat()
                val x = center.x + radius * fraction * cos(angle)
                val y = center.y + radius * fraction * sin(angle)
                if (i == 0) obsPath.moveTo(x, y) else obsPath.lineTo(x, y)
            }
            obsPath.close()
            drawPath(obsPath, color = primaryColor.copy(alpha = 0.35f), style = Fill)
            drawPath(obsPath, color = primaryColor, style = Stroke(width = 2.5.dp.toPx()))
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(color = primaryColor, shape = RoundedCornerShape(4.dp), modifier = Modifier.size(10.dp)) {}
        Spacer(modifier = Modifier.width(4.dp))
        Text("Observed", style = MaterialTheme.typography.labelSmall)
        Spacer(modifier = Modifier.width(16.dp))
        Surface(color = secondaryColor, shape = RoundedCornerShape(4.dp), modifier = Modifier.size(10.dp)) {}
        Spacer(modifier = Modifier.width(4.dp))
        Text("Heuristic Direction", style = MaterialTheme.typography.labelSmall)
    }
}
