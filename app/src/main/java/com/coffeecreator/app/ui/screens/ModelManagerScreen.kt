package com.coffeecreator.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.coffeecreator.app.core.model.ModelStatus
import com.coffeecreator.app.ui.viewmodel.ModelManagerIntent
import com.coffeecreator.app.ui.viewmodel.ModelManagerViewModel

@Composable
fun ModelManagerScreen(
    viewModel: ModelManagerViewModel = viewModel()
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
                text = "Model & Asset Manager (SAF)",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "User-owned weights via Android Storage Access Framework (SAF). Resumable downloads, checksum validation, and reinstall recovery.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Folder, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Target SAF Directory",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = state.safDirectoryUri ?: "No directory selected. Tap to grant SAF folder permission.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = {
                            viewModel.dispatch(ModelManagerIntent.SelectSafDirectory("content://com.android.externalstorage.documents/tree/primary%3ACoffeeCreatorModels"))
                        },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Reselect / Regrant SAF Directory")
                    }
                }
            }
        }

        item {
            Text(
                text = "Quantized Models Catalog (${state.models.size})",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        items(state.models) { model ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = model.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Badge(
                            containerColor = when (model.status) {
                                ModelStatus.INSTALLED -> MaterialTheme.colorScheme.primaryContainer
                                ModelStatus.DOWNLOADING -> MaterialTheme.colorScheme.tertiaryContainer
                                else -> MaterialTheme.colorScheme.secondaryContainer
                            }
                        ) {
                            Text(model.status.name)
                        }
                    }

                    Text(
                        text = "Format: ${model.format} | Expected Size: ${model.expectedSizeBytes / (1024 * 1024)} MB | License: ${model.license}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = "SHA-256: ${model.sha256.take(16)}...",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (model.status == ModelStatus.DOWNLOADING) {
                        LinearProgressIndicator(
                            progress = { model.progressPercent / 100f },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            text = "Downloading & verifying chunks: ${model.progressPercent}%",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        when (model.status) {
                            ModelStatus.CATALOGUED, ModelStatus.PAUSED -> {
                                Button(
                                    onClick = { viewModel.dispatch(ModelManagerIntent.InstallModel(model.id)) },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Download, contentDescription = null)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(if (model.status == ModelStatus.PAUSED) "Resume" else "Install to SAF")
                                }
                            }
                            ModelStatus.DOWNLOADING -> {
                                OutlinedButton(
                                    onClick = { viewModel.dispatch(ModelManagerIntent.PauseModel(model.id)) },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Pause, contentDescription = null)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Pause")
                                }
                            }
                            ModelStatus.INSTALLED -> {
                                FilledTonalButton(
                                    onClick = { viewModel.dispatch(ModelManagerIntent.UninstallModel(model.id)) },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Uninstall Asset")
                                }
                            }
                            else -> {}
                        }
                    }
                }
            }
        }
    }
}
