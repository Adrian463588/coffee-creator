package com.coffeecreator.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.coffeecreator.app.ui.viewmodel.KnowledgeIntent
import com.coffeecreator.app.ui.viewmodel.KnowledgeViewModel

@Composable
fun KnowledgeLibraryScreen(
    viewModel: KnowledgeViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.dispatch(KnowledgeIntent.ToggleImportDialog(true)) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.FileUpload, contentDescription = "Import Document")
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
                    text = "Knowledge Documents & RAG",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Local offline document store. Chunked and indexed with citations and strict abstention.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = state.searchQuery,
                        onValueChange = { viewModel.dispatch(KnowledgeIntent.UpdateQuery(it)) },
                        placeholder = { Text("Query local manuals...") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Button(
                        onClick = { viewModel.dispatch(KnowledgeIntent.ExecuteSearch) },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.heightIn(min = 52.dp)
                    ) {
                        Icon(Icons.Default.Search, contentDescription = null)
                    }
                }
            }

            state.searchResultText?.let { text ->
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Search Evidence Result:",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = text,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Imported Coffee Reference Documents (${state.documents.size})",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            items(state.documents) { doc ->
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
                                text = doc.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Badge(containerColor = MaterialTheme.colorScheme.secondaryContainer) {
                                Text(doc.fileType)
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "URI: ${doc.sourceUri}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Pages: ${doc.pageCount} | Indexed Chunks: ${doc.chunkCount} | SHA-256: ${doc.contentHash.take(12)}...",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }

        if (state.showImportDialog) {
            var docTitle by remember { mutableStateOf("") }
            var docContent by remember { mutableStateOf("") }

            AlertDialog(
                onDismissRequest = { viewModel.dispatch(KnowledgeIntent.ToggleImportDialog(false)) },
                title = { Text("Import SAF Document into Local RAG") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = docTitle,
                            onValueChange = { docTitle = it },
                            label = { Text("Document Title") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = docContent,
                            onValueChange = { docContent = it },
                            label = { Text("Document Content / Text Extract") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (docTitle.isNotBlank() && docContent.isNotBlank()) {
                                viewModel.dispatch(KnowledgeIntent.IngestDocument(docTitle, docContent))
                            }
                        }
                    ) {
                        Text("Ingest & Chunk")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.dispatch(KnowledgeIntent.ToggleImportDialog(false)) }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
