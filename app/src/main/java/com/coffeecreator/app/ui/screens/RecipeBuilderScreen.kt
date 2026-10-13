package com.coffeecreator.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.coffeecreator.app.core.data.DataRepository
import com.coffeecreator.app.core.engine.CoffeeScienceEngine
import com.coffeecreator.app.core.model.Recipe

@Composable
fun RecipeBuilderScreen(
    onNavigateToSensory: () -> Unit
) {
    val beans by DataRepository.beans.collectAsState()

    var recipeName by remember { mutableStateOf("Custom V60 Formulation") }
    var selectedBeanId by remember { mutableStateOf(beans.firstOrNull()?.id ?: "") }
    var brewMethod by remember { mutableStateOf("Pour Over (V60)") }

    var doseInput by remember { mutableStateOf("18.0") }
    var waterInput by remember { mutableStateOf("300.0") }
    var beverageInput by remember { mutableStateOf("270.0") }
    var tdsInput by remember { mutableStateOf("1.30") }

    var saveMessage by remember { mutableStateOf<String?>(null) }

    // Pure deterministic calculations
    val dose = doseInput.toDoubleOrNull() ?: 0.0
    val water = waterInput.toDoubleOrNull() ?: 0.0
    val beverage = beverageInput.toDoubleOrNull() ?: 0.0
    val tds = tdsInput.toDoubleOrNull() ?: 0.0

    val ratioResult = remember(dose, water) {
        CoffeeScienceEngine.calculateBrewRatio(dose, water)
    }

    val eyResult = remember(dose, beverage, tds) {
        CoffeeScienceEngine.calculateExtractionYield(dose, beverage, tds)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Manual Formulation & Science",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Deterministic, unit-aware recipe formulation. TDS is measured, EY is calculated.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item {
            // Recipe Name & Method
            OutlinedTextField(
                value = recipeName,
                onValueChange = { recipeName = it },
                label = { Text("Recipe Title") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
        }

        item {
            // Brew Method Selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Pour Over (V60)", "Aeropress", "Espresso", "French Press").forEach { method ->
                    FilterChip(
                        selected = brewMethod == method,
                        onClick = { brewMethod = method },
                        label = { Text(method) }
                    )
                }
            }
        }

        item {
            // Numeric Input Matrix (Dose, Water, Beverage, TDS)
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Brew Measurements & Inputs",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = doseInput,
                            onValueChange = { doseInput = it },
                            label = { Text("Dose (g)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = waterInput,
                            onValueChange = { waterInput = it },
                            label = { Text("Water (g)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = beverageInput,
                            onValueChange = { beverageInput = it },
                            label = { Text("Cup Mass (g)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = tdsInput,
                            onValueChange = { tdsInput = it },
                            label = { Text("Measured TDS (%)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }
        }

        item {
            // Instant Deterministic Results Card
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Deterministic Science Results",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    if (ratioResult.isSuccess) {
                        val ratio = ratioResult.getOrThrow()
                        Text(
                            text = "• Brew Ratio: ${ratio.ratioString} (${ratio.doseGrams}g dose : ${ratio.waterGrams}g water)",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    } else {
                        Text(
                            text = "• Brew Ratio: ${ratioResult.exceptionOrNull()?.message}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    if (eyResult.isSuccess) {
                        val ey = eyResult.getOrThrow()
                        Text(
                            text = "• Extraction Yield: ${ey.extractionYieldPercent}%",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Method: ${ey.methodLabel}\nAssumptions: ${ey.assumptions}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    } else {
                        Text(
                            text = "• Extraction Yield: ${eyResult.exceptionOrNull()?.message}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }

        item {
            // Action Buttons (Save, Duplicate, Sensory)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                        val recipe = Recipe(
                            id = "",
                            name = recipeName,
                            beanId = selectedBeanId,
                            brewMethod = brewMethod,
                            doseGrams = dose,
                            waterGrams = water,
                            beverageMassGrams = beverage,
                            measuredTdsPercent = if (tds > 0.0) tds else null,
                            targetRatio = ratioResult.getOrNull()?.ratioString ?: "1:16.67"
                        )
                        val saved = DataRepository.saveRecipe(recipe)
                        saveMessage = "Saved '${saved.name}' (v${saved.version}) successfully!"
                    },
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 52.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Save Recipe")
                }

                FilledTonalButton(
                    onClick = onNavigateToSensory,
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 52.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Tasting Notes")
                }
            }
        }

        saveMessage?.let { msg ->
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = msg,
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
