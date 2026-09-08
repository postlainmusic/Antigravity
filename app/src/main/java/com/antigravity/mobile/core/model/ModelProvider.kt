package com.antigravity.mobile.core.model

import com.antigravity.mobile.domain.model.ChatMessage
import com.antigravity.mobile.domain.model.ToolDefinition
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable

@Serializable
data class ModelCapabilities(
    val coding: Boolean = true,
    val reasoning: Boolean = true,
    val longContext: Boolean = false,
    val vision: Boolean = false,
    val toolUse: Boolean = true,
    val structuredOutput: Boolean = true,
    val lowLatency: Boolean = false,
    val offline: Boolean = false,
    val privacySensitive: Boolean = false,
    val lowCost: Boolean = false
)

@Serializable
data class ModelDescriptor(
    val id: String,
    val providerId: String,
    val displayName: String,
    val version: String,
    val contextWindowTokens: Int,
    val capabilities: ModelCapabilities,
    val costPer1kInputTokensUsd: Double = 0.0,
    val costPer1kOutputTokensUsd: Double = 0.0,
    val isDefault: Boolean = false
)

@Serializable
data class ModelPolicy(
    val preferredProviderId: String? = null,
    val requireOffline: Boolean = false,
    val requireToolUse: Boolean = true,
    val requireLowLatency: Boolean = false,
    val maxCostThresholdUsd: Double = 1.0,
    val fallbackAllowed: Boolean = true
)

@Serializable
data class TokenUsage(
    val promptTokens: Int = 0,
    val completionTokens: Int = 0,
    val totalTokens: Int = 0
)

@Serializable
data class CompletionRequest(
    val modelId: String,
    val messages: List<ChatMessage>,
    val systemPrompt: String = "",
    val temperature: Float = 0.2f,
    val maxTokens: Int = 4096,
    val requiredCapabilities: ModelCapabilities = ModelCapabilities()
)

sealed interface StreamChunk {
    data class Content(val text: String) : StreamChunk
    data class ToolCall(val callId: String, val toolName: String, val argumentsJson: String) : StreamChunk
    data class Completed(val finishReason: String, val usage: TokenUsage) : StreamChunk
    data class Error(val error: Throwable) : StreamChunk
}

interface ModelProvider {
    val id: String
    val displayName: String
    val isAvailable: Boolean
    val descriptors: List<ModelDescriptor>

    fun streamCompletion(
        request: CompletionRequest,
        tools: List<ToolDefinition> = emptyList()
    ): Flow<StreamChunk>
}
