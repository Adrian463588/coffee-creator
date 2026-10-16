package com.coffeecreator.app.ui.viewmodel

import androidx.lifecycle.viewModelScope
import com.coffeecreator.app.core.ai.NativeChatTurn
import com.coffeecreator.app.core.ai.NativeGenerationOptions
import com.coffeecreator.app.core.model.ChatMessage
import com.coffeecreator.app.core.model.DocumentChunk
import com.coffeecreator.app.core.model.InputLimits
import com.coffeecreator.app.core.model.Recipe
import java.util.UUID
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun CoffeeViewModel.sendChat(prompt: String) {
    if (prompt.isBlank() || mutableState.value.generating || mutableState.value.busy || replacingDraft) return
    if (prompt.length > InputLimits.QUERY_CHARS || '\u0000' in prompt) {
        mutableState.update { it.copy(error = "Message exceeds the safe input budget; shorten it before sending.") }
        return
    }
    generation = viewModelScope.launch {
        mutableState.update { it.copy(generating = true, error = null) }
        val user = ChatMessage(UUID.randomUUID().toString(), "user", prompt)
        val replyId = UUID.randomUUID().toString()
        mutableState.update { it.copy(messages = it.messages + user + ChatMessage(replyId, "assistant", "")) }
        val response = StringBuilder()
        try {
            if (prompt.trim().startsWith('{')) {
                val result = withContext(Dispatchers.Default) { graph.tools.executeJson(prompt) }
                response.append("Exact Kotlin tool result (${result.toolName}):\n${result.output}")
                mutableState.update { state ->
                    state.copy(
                        messages = state.messages.map { message ->
                            if (message.id ==
                                replyId
                            ) {
                                message.copy(role = "tool", toolCallName = result.toolName, toolResultJson = result.output)
                            } else {
                                message
                            }
                        }
                    )
                }
                if (result.requiresUserConfirmation || result.toolName == "recommend_recipe") {
                    mutableState.update { it.copy(pendingRecipe = result.structuredData as? Recipe) }
                }
                replaceReply(replyId, response.toString())
            } else {
                require(graph.native.initialized) {
                    "No local model loaded. Use Models to install/load a verified GGUF, or run a typed tool with explicit arguments."
                }
                val persona = mutableState.value.selectedPersona ?: error("Select a persona.")
                if (graph.repository.chunks.value.isNotEmpty()) graph.rag.index(graph.repository.chunks.value)
                val evidence = if (graph.repository.chunks.value.isNotEmpty()) {
                    graph.rag.search(prompt).let {
                        it.copy(matchedChunks = it.matchedChunks.take(2), citations = it.citations.take(2))
                    }
                } else {
                    null
                }
                if (evidence != null) mutableState.update { it.copy(searchResult = evidence) }
                val history = mutableState.value.messages.dropLast(2).filter {
                    it.content.isNotBlank() &&
                        it.role in setOf("user", "assistant")
                }.takeLast(2)
                    .map { NativeChatTurn(it.role, it.content.take(300)) }
                var deliveredSources = emptyList<DocumentChunk>()
                graph.coordinator.streamChatResponse(
                    prompt,
                    persona,
                    history,
                    evidence,
                    NativeGenerationOptions(maxTokens = mutableState.value.maxTokens, temperature = mutableState.value.temperature),
                    onSources = { deliveredSources = it },
                    onTool = ::addToolCard
                ).collect { token ->
                    response.append(token)
                    val actualCitations = deliveredSources.map { it.citationText }.filter { response.contains(it) }
                    replaceReply(replyId, response.toString(), actualCitations, deliveredSources.map { it.id })
                }
            }
        } catch (cancelled: CancellationException) {
            graph.native.cancel()
            replaceReply(replyId, response.toString() + "\nGeneration cancelled.")
            throw cancelled
        } catch (boundaryFailure: Exception) {
            replaceReply(replyId, response.toString() + "\n${boundaryFailure.message}")
            mutableState.update { it.copy(error = boundaryFailure.message) }
        } finally {
            mutableState.update { it.copy(generating = false) }
            withContext(kotlinx.coroutines.NonCancellable + Dispatchers.IO) {
                try {
                    graph.repository.saveChatMessages(mutableState.value.messages.takeLast(100))
                } catch (boundaryFailure: Exception) {
                    mutableState.update { it.copy(error = "Chat history was not saved: ${boundaryFailure.message}") }
                }
            }
        }
    }
}
private fun CoffeeViewModel.addToolCard(result: com.coffeecreator.app.core.ai.ToolExecutionResult) {
    val card = ChatMessage(
        UUID.randomUUID().toString(),
        "tool",
        result.output,
        toolCallName = result.toolName,
        toolResultJson = result.output
    )
    mutableState.update { it.copy(messages = it.messages + card, pendingToolJson = result.inputConfirmationJson ?: it.pendingToolJson) }
    if (result.requiresUserConfirmation || result.toolName == "recommend_recipe") {
        mutableState.update { it.copy(pendingRecipe = result.structuredData as? Recipe) }
    }
}
private fun CoffeeViewModel.replaceReply(
    id: String,
    content: String,
    citations: List<String> = emptyList(),
    sourceIds: List<String> = emptyList()
) {
    mutableState.update {
        it.copy(
            messages = it.messages.map { message ->
                if (message.id ==
                    id
                ) {
                    message.copy(content = content, citations = citations, sourceChunkIds = sourceIds)
                } else {
                    message
                }
            }
        )
    }
}
fun CoffeeViewModel.cancelGeneration() {
    graph.native.cancel()
    generation?.cancel()
}
fun CoffeeViewModel.confirmAiRecipe() = task("Saving user-confirmed AI draft") {
    val draft = mutableState.value.pendingRecipe ?: error("No recipe draft awaiting approval.")
    graph.repository.saveRecipe(draft)
    mutableState.update { it.copy(pendingRecipe = null, notice = "Saved confirmed draft. No measurements were invented.") }
}
fun CoffeeViewModel.dismissAiRecipe() {
    mutableState.update { it.copy(pendingRecipe = null) }
}
fun CoffeeViewModel.confirmToolInputs() {
    if (mutableState.value.busy || replacingDraft) return
    val json = mutableState.value.pendingToolJson ?: return
    mutableState.update { it.copy(pendingToolJson = null) }
    sendChat(json)
}
fun CoffeeViewModel.dismissToolInputs() {
    mutableState.update { it.copy(pendingToolJson = null) }
}
