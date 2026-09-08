package com.antigravity.mobile.domain.usecase

import com.antigravity.mobile.core.agent.AgentOrchestrator
import com.antigravity.mobile.core.agent.DiffManager
import com.antigravity.mobile.core.security.SandboxManager
import com.antigravity.mobile.core.terminal.TerminalManager
import com.antigravity.mobile.domain.model.AgentEvent
import com.antigravity.mobile.domain.model.FileDiff
import com.antigravity.mobile.domain.model.FileNode
import kotlinx.coroutines.flow.Flow
import java.io.File

class ExecuteAgentTaskUseCase(
    private val orchestrator: AgentOrchestrator
) {
    operator fun invoke(userPrompt: String, activeFilePath: String? = null): Flow<AgentEvent> {
        return orchestrator.executeTask(userPrompt, activeFilePath)
    }
}

class ApplyFileDiffUseCase(
    private val sandboxManager: SandboxManager
) {
    operator fun invoke(fileDiff: FileDiff): Result<Unit> {
        return try {
            val safeFile = sandboxManager.resolveSafePath(fileDiff.filePath)
            safeFile.parentFile?.mkdirs()
            safeFile.writeText(fileDiff.newContent)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

class IndexWorkspaceUseCase(
    private val sandboxManager: SandboxManager
) {
    operator fun invoke(): FileNode {
        val root = sandboxManager.resolveSafePath(".")
        return buildTree(root)
    }

    private fun buildTree(file: File): FileNode {
        val children = if (file.isDirectory) {
            file.listFiles()
                ?.filter { !it.name.startsWith(".") && it.name != "node_modules" && it.name != "build" }
                ?.map { buildTree(it) }
                ?: emptyList()
        } else emptyList()

        return FileNode(
            name = file.name.ifEmpty { "Project Root" },
            path = file.path,
            isDirectory = file.isDirectory,
            sizeBytes = if (file.isFile) file.length() else 0,
            children = children
        )
    }
}

class RunTerminalCommandUseCase(
    private val terminalManager: TerminalManager
) {
    operator fun invoke(sessionId: String, command: String) {
        terminalManager.executeCommand(sessionId, command)
    }
}
