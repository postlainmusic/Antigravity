package com.antigravity.mobile.core.model

import com.antigravity.mobile.domain.model.ToolDefinition
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GeminiModelProvider(
    private val apiKeyProvider: () -> String = { "" }
) : ModelProvider {
    override val id: String = "gemini"
    override val displayName: String = "Google Gemini"
    override val isAvailable: Boolean get() = apiKeyProvider().isNotBlank()

    override val descriptors: List<ModelDescriptor> = listOf(
        ModelDescriptor(
            id = "gemini-1.5-pro",
            providerId = id,
            displayName = "Gemini 1.5 Pro",
            version = "1.5.0",
            contextWindowTokens = 2097152,
            capabilities = ModelCapabilities(
                coding = true,
                reasoning = true,
                longContext = true,
                vision = true,
                toolUse = true,
                structuredOutput = true,
                lowLatency = false,
                offline = false
            ),
            isDefault = true
        ),
        ModelDescriptor(
            id = "gemini-1.5-flash",
            providerId = id,
            displayName = "Gemini 1.5 Flash",
            version = "1.5.0",
            contextWindowTokens = 1048576,
            capabilities = ModelCapabilities(
                coding = true,
                reasoning = true,
                longContext = true,
                vision = true,
                toolUse = true,
                structuredOutput = true,
                lowLatency = true,
                offline = false,
                lowCost = true
            )
        )
    )

    override fun streamCompletion(
        request: CompletionRequest,
        tools: List<ToolDefinition>
    ): Flow<StreamChunk> = flow {
        val prompt = request.messages.lastOrNull()?.content ?: ""
        emit(StreamChunk.Content("Analyzing request with ${request.modelId}...\n\n"))
        delay(120)

        if (prompt.contains("list", ignoreCase = true) || prompt.contains("explore", ignoreCase = true)) {
            emit(StreamChunk.ToolCall("call_1", "list_dir", """{"directoryPath": "."}"""))
        } else if (prompt.contains("create", ignoreCase = true) || prompt.contains("build", ignoreCase = true)) {
            emit(StreamChunk.Content("Creating implementation plan...\n"))
            delay(100)
            emit(StreamChunk.ToolCall("call_2", "write_file", """{"targetFile": "index.html", "codeContent": "<!DOCTYPE html>\n<html>\n<head><title>App</title></head>\n<body><h1>Hello World</h1></body>\n</html>"}"""))
        } else {
            emit(StreamChunk.Content("Executing plan for: $prompt\n"))
        }

        emit(StreamChunk.Completed("stop", TokenUsage(promptTokens = 250, completionTokens = 120, totalTokens = 370)))
    }
}

class ClaudeModelProvider(
    private val apiKeyProvider: () -> String = { "" }
) : ModelProvider {
    override val id: String = "claude"
    override val displayName: String = "Anthropic Claude"
    override val isAvailable: Boolean get() = apiKeyProvider().isNotBlank()

    override val descriptors: List<ModelDescriptor> = listOf(
        ModelDescriptor(
            id = "claude-3-5-sonnet",
            providerId = id,
            displayName = "Claude 3.5 Sonnet",
            version = "3.5.0",
            contextWindowTokens = 200000,
            capabilities = ModelCapabilities(
                coding = true,
                reasoning = true,
                longContext = true,
                vision = true,
                toolUse = true,
                structuredOutput = true,
                lowLatency = false,
                offline = false
            )
        )
    )

    override fun streamCompletion(request: CompletionRequest, tools: List<ToolDefinition>): Flow<StreamChunk> = flow {
        emit(StreamChunk.Content("Processing with Claude 3.5 Sonnet...\n"))
        delay(150)
        emit(StreamChunk.Completed("end_turn", TokenUsage(promptTokens = 300, completionTokens = 90, totalTokens = 390)))
    }
}

class OpenAiModelProvider(
    private val apiKeyProvider: () -> String = { "" }
) : ModelProvider {
    override val id: String = "openai"
    override val displayName: String = "OpenAI GPT"
    override val isAvailable: Boolean get() = apiKeyProvider().isNotBlank()

    override val descriptors: List<ModelDescriptor> = listOf(
        ModelDescriptor(
            id = "gpt-4o",
            providerId = id,
            displayName = "GPT-4o",
            version = "4.0.0",
            contextWindowTokens = 128000,
            capabilities = ModelCapabilities(
                coding = true,
                reasoning = true,
                longContext = true,
                vision = true,
                toolUse = true,
                structuredOutput = true,
                lowLatency = true,
                offline = false
            )
        )
    )

    override fun streamCompletion(request: CompletionRequest, tools: List<ToolDefinition>): Flow<StreamChunk> = flow {
        emit(StreamChunk.Content("Processing with GPT-4o...\n"))
        delay(150)
        emit(StreamChunk.Completed("stop", TokenUsage(promptTokens = 220, completionTokens = 85, totalTokens = 305)))
    }
}

class LocalModelProvider : ModelProvider {
    override val id: String = "local"
    override val displayName: String = "Local Gemma 2B (On-Device)"
    override val isAvailable: Boolean = true

    override val descriptors: List<ModelDescriptor> = listOf(
        ModelDescriptor(
            id = "gemma-2b-it",
            providerId = id,
            displayName = "Gemma 2B (Local)",
            version = "2.0.0",
            contextWindowTokens = 8192,
            capabilities = ModelCapabilities(
                coding = true,
                reasoning = false,
                longContext = false,
                vision = false,
                toolUse = true,
                structuredOutput = false,
                lowLatency = true,
                offline = true,
                privacySensitive = true,
                lowCost = true
            )
        )
    )

    override fun streamCompletion(request: CompletionRequest, tools: List<ToolDefinition>): Flow<StreamChunk> = flow {
        emit(StreamChunk.Content("Generating on-device offline completion...\n"))
        delay(100)
        emit(StreamChunk.Completed("stop", TokenUsage(promptTokens = 120, completionTokens = 40, totalTokens = 160)))
    }
}

class ModelRegistry {
    private val providers = mutableMapOf<String, ModelProvider>()

    fun register(provider: ModelProvider) {
        providers[provider.id] = provider
    }

    fun unregister(providerId: String) {
        providers.remove(providerId)
    }

    fun getProvider(providerId: String): ModelProvider? = providers[providerId]

    fun getAllProviders(): List<ModelProvider> = providers.values.toList()

    fun getAllDescriptors(): List<ModelDescriptor> = providers.values.flatMap { it.descriptors }

    fun findDescriptorsByCapability(matcher: (ModelCapabilities) -> Boolean): List<ModelDescriptor> {
        return getAllDescriptors().filter { matcher(it.capabilities) }
    }
}

class ModelRouter(
    private val registry: ModelRegistry,
    private var policy: ModelPolicy = ModelPolicy()
) {
    fun updatePolicy(newPolicy: ModelPolicy) {
        this.policy = newPolicy
    }

    fun selectOptimalModel(request: CompletionRequest): Pair<ModelProvider, ModelDescriptor> {
        val allDescriptors = registry.getAllDescriptors()

        // 1. Check if policy specifies offline
        val filtered = allDescriptors.filter { desc ->
            val provider = registry.getProvider(desc.providerId)
            val providerAvailable = provider?.isAvailable == true || desc.capabilities.offline
            
            if (!providerAvailable) return@filter false
            if (policy.requireOffline && !desc.capabilities.offline) return@filter false
            if (policy.requireToolUse && !desc.capabilities.toolUse) return@filter false
            if (policy.requireLowLatency && !desc.capabilities.lowLatency) return@filter false

            true
        }

        // 2. Try to match preferred provider
        val preferred = if (policy.preferredProviderId != null) {
            filtered.find { it.providerId == policy.preferredProviderId }
        } else null

        val selectedDescriptor = preferred
            ?: filtered.find { it.isDefault }
            ?: filtered.firstOrNull()
            ?: allDescriptors.firstOrNull { it.capabilities.offline }
            ?: throw IllegalStateException("No available AI model matches the current policy and capabilities.")

        val selectedProvider = registry.getProvider(selectedDescriptor.providerId)
            ?: throw IllegalStateException("Provider ${selectedDescriptor.providerId} not found in registry.")

        return Pair(selectedProvider, selectedDescriptor)
    }
}
