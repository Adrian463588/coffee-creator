package com.coffeecreator.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.coffeecreator.app.core.data.DataRepository
import com.coffeecreator.app.core.model.CoffeeBean
import java.util.UUID

@Composable
fun CoffeeLibraryScreen() {
    val beans by DataRepository.beans.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedProcessFilter by remember { mutableStateOf<String?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }

    val filteredBeans = remember(beans, searchQuery, selectedProcessFilter) {
        beans.filter { bean ->
            val matchesQuery = searchQuery.isBlank() ||
                bean.name.contains(searchQuery, ignoreCase = true) ||
                bean.country.contains(searchQuery, ignoreCase = true) ||
                bean.variety.contains(searchQuery, ignoreCase = true)
            val matchesProcess = selectedProcessFilter == null ||
                bean.process.equals(selectedProcessFilter, ignoreCase = true)
            matchesQuery && matchesProcess
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add custom bean")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Coffee Catalog & Provenance",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Curated and user-logged beans. Missing data is marked unknown (no hallucination).",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            item {
                // Search bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    placeholder = { Text("Search by origin, variety, producer...") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            item {
                // Process Filters
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("All", "Washed", "Natural", "Honey", "Wet Hulled").forEach { filter ->
                        val isSelected = (filter == "All" && selectedProcessFilter == null) ||
                            (selectedProcessFilter == filter)
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                selectedProcessFilter = if (filter == "All") null else filter
                            },
                            label = { Text(filter) }
                        )
                    }
                }
            }

            if (filteredBeans.isEmpty()) {
                item {
                    Text(
                        text = "No matching beans found in catalog.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                items(filteredBeans) { bean ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = bean.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                if (bean.isCuratedSeed) {
                                    Badge(containerColor = MaterialTheme.colorScheme.primaryContainer) {
                                        Text("Curated")
                                    }
                                } else {
                                    Badge(containerColor = MaterialTheme.colorScheme.secondaryContainer) {
                                        Text("Custom")
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Origin: ${bean.country} (${bean.region}) • Producer: ${bean.producer}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Process: ${bean.process} | Roast: ${bean.roastLevel} | Variety: ${bean.variety}",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Altitude: ${bean.altitudeMeters?.let { "${it}m" } ?: "Unknown"} | Density: ${bean.densityGramsPerMl?.let { "${it} g/mL" } ?: "Unknown"} | Allergens: ${bean.allergens}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Add custom bean modal
        if (showAddDialog) {
            var newName by remember { mutableStateOf("") }
            var newCountry by remember { mutableStateOf("") }
            var newProcess by remember { mutableStateOf("Washed") }
            var newRoast by remember { mutableStateOf("Medium") }

            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text("Add Custom Coffee Lot") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = newName,
                            onValueChange = { newName = it },
                            label = { Text("Bean Name") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = newCountry,
                            onValueChange = { newCountry = it },
                            label = { Text("Country / Origin") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = newProcess,
                            onValueChange = { newProcess = it },
                            label = { Text("Processing Method") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = newRoast,
                            onValueChange = { newRoast = it },
                            label = { Text("Roast Level") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (newName.isNotBlank() && newCountry.isNotBlank()) {
                                DataRepository.addBean(
                                    CoffeeBean(
                                        id = UUID.randomUUID().toString(),
                                        name = newName,
                                        country = newCountry,
                                        region = "Custom Region",
                                        process = newProcess,
                                        roastLevel = newRoast,
                                        isCuratedSeed = false
                                    )
                                )
                                showAddDialog = false
                            }
                        }
                    ) {
                        Text("Add Bean")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
