package com.antigravity.mobile.core.terminal

import com.antigravity.mobile.core.security.SandboxManager
import com.antigravity.mobile.domain.model.TerminalOutputLine
import com.antigravity.mobile.domain.model.TerminalSession
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID

interface TerminalBackend {
    val id: String
    val name: String
    fun execute(workingDir: File, commandLine: String): Flow<String>
}

class LocalTerminalBackend : TerminalBackend {
    override val id: String = "local_process"
    override val name: String = "Local Android Subshell"

    override fun execute(workingDir: File, commandLine: String): Flow<String> = flow {
        val process = ProcessBuilder(commandLine.split("\\s+".toRegex()))
            .directory(workingDir)
            .redirectErrorStream(true)
            .start()

        val reader = process.inputStream.bufferedReader()
        var line = reader.readLine()
        while (line != null) {
            emit("$line\n")
            line = reader.readLine()
        }
        process.waitFor()
    }
}

class SandboxedTerminalBackend(private val sandboxManager: SandboxManager) : TerminalBackend {
    override val id: String = "sandboxed"
    override val name: String = "Sandboxed Runner"

    override fun execute(workingDir: File, commandLine: String): Flow<String> = flow {
        if (sandboxManager.isCommandDangerous(commandLine)) {
            emit("Error: Command '$commandLine' violates security policy.\n")
            return@flow
        }
        emit("Simulating sandboxed command execution: $commandLine\n")
        emit("Command completed with exit code 0.\n")
    }
}

class RemoteSshTerminalBackend(
    val host: String = "127.0.0.1",
    val port: Int = 2222
) : TerminalBackend {
    override val id: String = "remote_ssh"
    override val name: String = "Remote Dev Machine (SSH)"

    override fun execute(workingDir: File, commandLine: String): Flow<String> = flow {
        emit("Connected to remote workstation $host:$port\n")
        emit("Remote execution output for: $commandLine\n")
    }
}

class TerminalManager(
    private val sandboxManager: SandboxManager,
    private var backend: TerminalBackend = LocalTerminalBackend()
) {
    private val _sessions = MutableStateFlow<List<TerminalSession>>(emptyList())
    val sessions = _sessions.asStateFlow()

    private val _activeSessionId = MutableStateFlow<String?>(null)
    val activeSessionId = _activeSessionId.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        createSession("Main Terminal")
    }

    fun setBackend(newBackend: TerminalBackend) {
        this.backend = newBackend
    }

    fun createSession(title: String): TerminalSession {
        val root = sandboxManager.resolveSafePath(".")
        val session = TerminalSession(
            id = UUID.randomUUID().toString(),
            title = title,
            workingDir = root.absolutePath,
            outputHistory = listOf(
                TerminalOutputLine("Antigravity Mobile Terminal (${backend.name}) ready.\nType commands or use quick shortcuts.\n")
            )
        )
        _sessions.value = _sessions.value + session
        _activeSessionId.value = session.id
        return session
    }

    fun executeCommand(sessionId: String, commandLine: String) {
        val session = _sessions.value.find { it.id == sessionId } ?: return

        appendOutput(sessionId, "$ $commandLine\n", isError = false)

        if (sandboxManager.isCommandDangerous(commandLine)) {
            appendOutput(sessionId, "Error: Dangerous command blocked by sandbox security.\n", isError = true)
            return
        }

        scope.launch {
            try {
                val workingDir = File(session.workingDir)
                backend.execute(workingDir, commandLine).collect { text ->
                    appendOutput(sessionId, text, isError = false)
                }
            } catch (e: Exception) {
                appendOutput(sessionId, "Execution error: ${e.message}\n", isError = true)
            }
        }
    }

    private fun appendOutput(sessionId: String, text: String, isError: Boolean) {
        _sessions.value = _sessions.value.map { sess ->
            if (sess.id == sessionId) {
                sess.copy(outputHistory = sess.outputHistory + TerminalOutputLine(text, isError))
            } else sess
        }
    }
}
