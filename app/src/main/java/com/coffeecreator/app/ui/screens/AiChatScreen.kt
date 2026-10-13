package com.coffeecreator.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.coffeecreator.app.ui.viewmodel.AiChatIntent
import com.coffeecreator.app.ui.viewmodel.AiChatViewModel

@Composable
fun AiChatScreen(
    viewModel: AiChatViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Persona: ${state.selectedPersona.title}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Badge(containerColor = MaterialTheme.colorScheme.primaryContainer) {
                        Text("100% Offline")
                    }
                }
                Text(
                    text = state.selectedPersona.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    state.personas.forEach { p ->
                        FilterChip(
                            selected = p.id == state.selectedPersona.id,
                            onClick = { viewModel.dispatch(AiChatIntent.SelectPersona(p)) },
                            label = { Text(p.title, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            AssistChip(
                onClick = { viewModel.dispatch(AiChatIntent.UpdateInput("Calculate extraction for 18g dose, 270g beverage, 1.30% TDS")) },
                label = { Text("Calculate EY", style = MaterialTheme.typography.labelSmall) }
            )
            AssistChip(
                onClick = { viewModel.dispatch(AiChatIntent.UpdateInput("What is the SCA brewing water temperature standard?")) },
                label = { Text("SCA Temp Standard", style = MaterialTheme.typography.labelSmall) }
            )
            AssistChip(
                onClick = { viewModel.dispatch(AiChatIntent.UpdateInput("Estimate flavor direction for Ethiopian Light Roast")) },
                label = { Text("Sensory Estimate", style = MaterialTheme.typography.labelSmall) }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(state.messages) { msg ->
                val isUser = msg.role == "user"
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
                ) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.widthIn(max = 320.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = if (isUser) "You" else state.selectedPersona.title,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = msg.content,
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        // Write Confirmation Dialog (FR-07 confirmed write action)
        state.pendingRecipeWrite?.let {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Tool Permission Gate: Confirm Save Recipe",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "AI proposed writing a recipe to your database. Confirm write?",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { viewModel.dispatch(AiChatIntent.CancelWriteRecipe) }) {
                            Text("Deny")
                        }
                        Button(onClick = { viewModel.dispatch(AiChatIntent.ConfirmWriteRecipe) }) {
                            Text("Confirm & Save")
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = state.inputText,
                onValueChange = { viewModel.dispatch(AiChatIntent.UpdateInput(it)) },
                placeholder = { Text("Ask question or invoke tool...") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(24.dp)
            )

            IconButton(
                onClick = { viewModel.dispatch(AiChatIntent.SendMessage) },
                enabled = !state.isGenerating && state.inputText.isNotBlank(),
                modifier = Modifier
                    .size(48.dp)
                    .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(24.dp))
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send message",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}
