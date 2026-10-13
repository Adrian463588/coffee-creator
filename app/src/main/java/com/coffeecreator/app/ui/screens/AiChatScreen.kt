package com.coffeecreator.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.coffeecreator.app.core.ai.LocalLlmCoordinator
import com.coffeecreator.app.core.data.DataRepository
import com.coffeecreator.app.core.model.ChatMessage
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun AiChatScreen() {
    val personas by DataRepository.personas.collectAsState()
    val selectedPersona by DataRepository.selectedPersona.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    var messages by remember {
        mutableStateOf(
            listOf(
                ChatMessage(
                    id = "msg-0",
                    role = "assistant",
                    content = "Hello! I am your on-device coffee assistant (${selectedPersona.title}). I operate 100% offline, use deterministic Kotlin math tools, and cite verified local brewing documents."
                )
            )
        )
    }

    var inputText by remember { mutableStateOf("") }
    var isGenerating by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Persona Selector & Status
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
                        text = "Persona: ${selectedPersona.title}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Badge(containerColor = MaterialTheme.colorScheme.primaryContainer) {
                        Text("100% Offline")
                    }
                }
                Text(
                    text = selectedPersona.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    personas.forEach { p ->
                        FilterChip(
                            selected = p.id == selectedPersona.id,
                            onClick = { DataRepository.selectPersona(p) },
                            label = { Text(p.title, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Preset prompts
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            AssistChip(
                onClick = { inputText = "Calculate extraction for 18g dose, 270g beverage, 1.30% TDS" },
                label = { Text("Calculate EY", style = MaterialTheme.typography.labelSmall) }
            )
            AssistChip(
                onClick = { inputText = "What is the SCA brewing water temperature standard?" },
                label = { Text("SCA Temp Standard", style = MaterialTheme.typography.labelSmall) }
            )
            AssistChip(
                onClick = { inputText = "Estimate flavor direction for Ethiopian Light Roast" },
                label = { Text("Sensory Estimate", style = MaterialTheme.typography.labelSmall) }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Message List
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(messages) { msg ->
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
                                text = if (isUser) "You" else selectedPersona.title,
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

        Spacer(modifier = Modifier.height(8.dp))

        // Input row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = { Text("Ask question or invoke tool...") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(24.dp)
            )

            IconButton(
                onClick = {
                    if (inputText.isNotBlank() && !isGenerating) {
                        val prompt = inputText
                        inputText = ""
                        val userMsg = ChatMessage(id = UUID.randomUUID().toString(), role = "user", content = prompt)
                        val botMsgId = UUID.randomUUID().toString()
                        val initialBotMsg = ChatMessage(id = botMsgId, role = "assistant", content = "")

                        messages = messages + userMsg + initialBotMsg
                        isGenerating = true

                        coroutineScope.launch {
                            val sb = StringBuilder()
                            LocalLlmCoordinator.streamChatResponse(prompt, selectedPersona).collect { token ->
                                sb.append(token)
                                messages = messages.map { m ->
                                    if (m.id == botMsgId) m.copy(content = sb.toString()) else m
                                }
                            }
                            isGenerating = false
                        }
                    }
                },
                enabled = !isGenerating && inputText.isNotBlank(),
                modifier = Modifier
                    .size(48.dp)
                    .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(24.dp))
            ) {
                Icon(
                    Icons.Default.Send,
                    contentDescription = "Send message",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}
