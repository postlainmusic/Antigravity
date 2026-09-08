package com.antigravity.mobile.core.tools

import com.antigravity.mobile.core.security.SandboxManager
import com.antigravity.mobile.domain.model.ToolDefinition
import com.antigravity.mobile.domain.model.ToolResult
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive

class ToolRegistry(private val sandboxManager: SandboxManager) {

    private val tools = mutableMapOf<String, BaseTool>()

    init {
        register(ReadFileTool(sandboxManager))
        register(WriteFileTool(sandboxManager))
        register(ReplaceFileContentTool(sandboxManager))
        register(ListDirTool(sandboxManager))
        register(GrepSearchTool(sandboxManager))
        register(RunCommandTool(sandboxManager))
    }

    fun register(tool: BaseTool) {
        tools[tool.name] = tool
    }

    fun getAllDefinitions(): List<ToolDefinition> {
        return tools.values.map { it.toDefinition() }
    }

    suspend fun execute(callId: String, toolName: String, argsJson: String): ToolResult {
        val tool = tools[toolName] ?: return ToolResult(
            callId = callId,
            toolName = toolName,
            output = "Tool not found: $toolName",
            isError = true
        )

        val argsMap = parseJsonArgs(argsJson)
        return tool.execute(callId, argsMap)
    }

    private fun parseJsonArgs(jsonStr: String): Map<String, String> {
        return try {
            val jsonElement = Json.parseToJsonElement(jsonStr) as? JsonObject ?: return emptyMap()
            jsonElement.mapValues { it.value.jsonPrimitive.content }
        } catch (e: Exception) {
            emptyMap()
        }
    }
}
