package com.antigravity.mobile.core.tools

import com.antigravity.mobile.core.security.SandboxManager
import com.antigravity.mobile.domain.model.PermissionTier
import com.antigravity.mobile.domain.model.ToolDefinition
import com.antigravity.mobile.domain.model.ToolParameter
import com.antigravity.mobile.domain.model.ToolResult
import java.io.File

abstract class BaseTool(
    val name: String,
    val description: String,
    val permissionTier: PermissionTier = PermissionTier.SAFE
) {
    abstract val parameters: List<ToolParameter>

    fun toDefinition(): ToolDefinition = ToolDefinition(
        name = name,
        description = description,
        parameters = parameters,
        permissionTier = permissionTier
    )

    abstract suspend fun execute(callId: String, arguments: Map<String, String>): ToolResult
}

class ReadFileTool(private val sandboxManager: SandboxManager) : BaseTool(
    name = "read_file",
    description = "Read the contents of a file from the workspace",
    permissionTier = PermissionTier.SAFE
) {
    override val parameters = listOf(
        ToolParameter("filePath", "string", "Relative path to file in workspace")
    )

    override suspend fun execute(callId: String, arguments: Map<String, String>): ToolResult {
        val path = arguments["filePath"] ?: return ToolResult(callId, name, "Missing filePath argument", isError = true)
        return try {
            val safeFile = sandboxManager.resolveSafePath(path)
            if (!safeFile.exists()) {
                ToolResult(callId, name, "File not found: $path", isError = true)
            } else {
                ToolResult(callId, name, safeFile.readText())
            }
        } catch (e: Exception) {
            ToolResult(callId, name, "Error reading file: ${e.message}", isError = true)
        }
    }
}

class WriteFileTool(private val sandboxManager: SandboxManager) : BaseTool(
    name = "write_file",
    description = "Create or overwrite a file in the workspace",
    permissionTier = PermissionTier.MODERATE
) {
    override val parameters = listOf(
        ToolParameter("targetFile", "string", "Relative path to file"),
        ToolParameter("codeContent", "string", "Code content to write")
    )

    override suspend fun execute(callId: String, arguments: Map<String, String>): ToolResult {
        val path = arguments["targetFile"] ?: return ToolResult(callId, name, "Missing targetFile", isError = true)
        val content = arguments["codeContent"] ?: ""
        return try {
            val safeFile = sandboxManager.resolveSafePath(path)
            safeFile.parentFile?.mkdirs()
            safeFile.writeText(content)
            ToolResult(callId, name, "Successfully wrote ${content.length} characters to $path")
        } catch (e: Exception) {
            ToolResult(callId, name, "Error writing file: ${e.message}", isError = true)
        }
    }
}

class ReplaceFileContentTool(private val sandboxManager: SandboxManager) : BaseTool(
    name = "replace_file_content",
    description = "Replace a specific substring in a file with new content",
    permissionTier = PermissionTier.MODERATE
) {
    override val parameters = listOf(
        ToolParameter("targetFile", "string", "Relative path to file"),
        ToolParameter("targetContent", "string", "Exact text to replace"),
        ToolParameter("replacementContent", "string", "New replacement text")
    )

    override suspend fun execute(callId: String, arguments: Map<String, String>): ToolResult {
        val path = arguments["targetFile"] ?: return ToolResult(callId, name, "Missing targetFile", isError = true)
        val target = arguments["targetContent"] ?: return ToolResult(callId, name, "Missing targetContent", isError = true)
        val replacement = arguments["replacementContent"] ?: ""
        return try {
            val safeFile = sandboxManager.resolveSafePath(path)
            if (!safeFile.exists()) return ToolResult(callId, name, "File not found: $path", isError = true)
            val current = safeFile.readText()
            if (!current.contains(target)) {
                return ToolResult(callId, name, "targetContent not found in $path", isError = true)
            }
            val updated = current.replace(target, replacement)
            safeFile.writeText(updated)
            ToolResult(callId, name, "Successfully replaced content in $path")
        } catch (e: Exception) {
            ToolResult(callId, name, "Error replacing content: ${e.message}", isError = true)
        }
    }
}

class ListDirTool(private val sandboxManager: SandboxManager) : BaseTool(
    name = "list_dir",
    description = "List files and subdirectories in a directory",
    permissionTier = PermissionTier.SAFE
) {
    override val parameters = listOf(
        ToolParameter("directoryPath", "string", "Relative directory path", required = false)
    )

    override suspend fun execute(callId: String, arguments: Map<String, String>): ToolResult {
        val relPath = arguments["directoryPath"] ?: "."
        return try {
            val dir = sandboxManager.resolveSafePath(relPath)
            if (!dir.exists() || !dir.isDirectory) {
                return ToolResult(callId, name, "Not a valid directory: $relPath", isError = true)
            }
            val files = dir.listFiles()?.map {
                "${if (it.isDirectory) "[DIR]" else "[FILE]"} ${it.name} (${it.length()} bytes)"
            }?.joinToString("\n") ?: "Empty directory"
            ToolResult(callId, name, files)
        } catch (e: Exception) {
            ToolResult(callId, name, "Error listing directory: ${e.message}", isError = true)
        }
    }
}

class GrepSearchTool(private val sandboxManager: SandboxManager) : BaseTool(
    name = "grep_search",
    description = "Search for a pattern across workspace files",
    permissionTier = PermissionTier.SAFE
) {
    override val parameters = listOf(
        ToolParameter("query", "string", "Search query or regex")
    )

    override suspend fun execute(callId: String, arguments: Map<String, String>): ToolResult {
        val query = arguments["query"] ?: return ToolResult(callId, name, "Missing query", isError = true)
        val matches = mutableListOf<String>()
        val root = sandboxManager.resolveSafePath(".")

        fun searchDir(dir: File) {
            dir.listFiles()?.forEach { file ->
                if (file.isDirectory && !file.name.startsWith(".")) {
                    searchDir(file)
                } else if (file.isFile && file.length() < 1024 * 1024) {
                    file.readLines().forEachIndexed { idx, line ->
                        if (line.contains(query, ignoreCase = true)) {
                            matches.add("${file.relativeTo(root).path}:${idx + 1}: ${line.trim()}")
                        }
                    }
                }
            }
        }

        searchDir(root)
        val result = if (matches.isEmpty()) "No matches found." else matches.take(50).joinToString("\n")
        return ToolResult(callId, name, result)
    }
}

class RunCommandTool(private val sandboxManager: SandboxManager) : BaseTool(
    name = "run_command",
    description = "Execute a shell command inside workspace",
    permissionTier = PermissionTier.MODERATE
) {
    override val parameters = listOf(
        ToolParameter("commandLine", "string", "Command to execute")
    )

    override suspend fun execute(callId: String, arguments: Map<String, String>): ToolResult {
        val cmd = arguments["commandLine"] ?: return ToolResult(callId, name, "Missing commandLine", isError = true)
        if (sandboxManager.isCommandDangerous(cmd)) {
            return ToolResult(callId, name, "Command blocked by security sandbox: $cmd", isError = true)
        }
        return try {
            val root = sandboxManager.resolveSafePath(".")
            val process = ProcessBuilder(cmd.split(" "))
                .directory(root)
                .redirectErrorStream(true)
                .start()
            val output = process.inputStream.bufferedReader().readText()
            process.waitFor()
            ToolResult(callId, name, output.ifEmpty { "Command executed with exit code ${process.exitValue()}" })
        } catch (e: Exception) {
            ToolResult(callId, name, "Execution failed: ${e.message}", isError = true)
        }
    }
}
