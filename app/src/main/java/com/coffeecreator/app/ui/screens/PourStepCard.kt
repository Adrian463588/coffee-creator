package com.coffeecreator.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.coffeecreator.app.core.model.BrewStep

@Composable
internal fun PourStepCard(
    step: BrewStep,
    index: Int,
    expanded: Boolean,
    onToggle: () -> Unit,
    onUpdate: (BrewStep) -> Unit,
    onRemove: () -> Unit,
    onValidityChanged: (Boolean) -> Unit
) {
    var waterInput by rememberSaveable(step.stepNumber) { mutableStateOf(step.targetWaterGrams.toString()) }
    var secondsInput by rememberSaveable(step.stepNumber) { mutableStateOf(step.durationSeconds.toString()) }
    val water = waterInput.replace(',', '.').toDoubleOrNull()?.takeIf { it.isFinite() && it >= 0.0 }
    val seconds = secondsInput.toIntOrNull()?.takeIf { it > 0 }
    LaunchedEffect(water != null, seconds != null) { onValidityChanged(water != null && seconds != null) }
    Card(
        modifier = Modifier.fillMaxWidth().testTag("step:card:$index"),
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            TextButton(
                onClick = onToggle,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp)
                    .testTag("step:edit:$index")
                    .semantics { stateDescription = if (expanded) "Editing step" else "Step summary" }
            ) {
                Text("${step.stepNumber}. ${step.action}", Modifier.weight(1f), style = MaterialTheme.typography.titleMedium)
                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
            }
            Text(
                "+${step.targetWaterGrams} g · ${step.durationSeconds} s · target ${step.cumulativeMassGrams} g",
                style = MaterialTheme.typography.bodyMedium
            )
            if (step.temperatureCelsius != null || !step.valveStatus.isNullOrBlank()) {
                Text(
                    listOfNotNull(
                        step.temperatureCelsius?.let { "$it °C" },
                        step.valveStatus?.takeIf { it.isNotBlank() }?.let { "Valve: $it" }
                    )
                        .joinToString(" · "),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (step.description.isNotBlank()) {
                Text(step.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (water == null || seconds == null) {
                Text(
                    "Complete the highlighted step inputs.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
            AnimatedVisibility(visible = expanded) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Select(
                        "Action",
                        step.action,
                        listOf("Bloom", "Pour", "Wait", "Stir", "Press", "Drawdown", "Valve").map { it to it },
                        { onUpdate(step.copy(action = it)) },
                        "step:action:$index"
                    )
                    OutlinedTextField(
                        value = waterInput,
                        onValueChange = { input ->
                            waterInput = input
                            input.replace(',', '.').toDoubleOrNull()?.takeIf { it.isFinite() && it >= 0.0 }?.let {
                                onUpdate(step.copy(targetWaterGrams = it))
                            }
                        },
                        label = { Text("Pour water (g)") },
                        supportingText = { Text(if (water == null) "Enter zero or a positive mass in grams." else "Added in this step.") },
                        isError = water == null,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth().testTag("step:water:$index")
                    )
                    OutlinedTextField(
                        value = secondsInput,
                        onValueChange = { input ->
                            secondsInput = input
                            input.toIntOrNull()?.takeIf { it > 0 }?.let { onUpdate(step.copy(durationSeconds = it)) }
                        },
                        label = { Text("Duration (seconds)") },
                        supportingText = {
                            Text(if (seconds == null) "Enter a whole number above zero." else "Time before the next step.")
                        },
                        isError = seconds == null,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth().testTag("step:duration:$index")
                    )
                    Select(
                        "Valve",
                        step.valveStatus.orEmpty(),
                        listOf("" to "Not applicable", "Open" to "Open", "Closed" to "Closed", "Half" to "Half"),
                        { onUpdate(step.copy(valveStatus = it.ifBlank { null })) },
                        "step:valve:$index"
                    )
                    OutlinedTextField(
                        value = step.description,
                        onValueChange = { onUpdate(step.copy(description = it)) },
                        label = { Text("Instructions") },
                        modifier = Modifier.fillMaxWidth().testTag("step:desc:$index")
                    )
                    TextButton(
                        onClick = onRemove,
                        modifier = Modifier.heightIn(min = 48.dp).testTag("step:remove:$index")
                    ) { Text("Remove step", color = MaterialTheme.colorScheme.error) }
                }
            }
        }
    }
}
