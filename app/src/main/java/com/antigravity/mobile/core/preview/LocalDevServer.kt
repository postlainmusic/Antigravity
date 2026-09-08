package com.antigravity.mobile.core.preview

import com.antigravity.mobile.core.security.SandboxManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

class LocalDevServer(private val sandboxManager: SandboxManager) {

    private val _serverUrl = MutableStateFlow<String?>("http://localhost:3000")
    val serverUrl = _serverUrl.asStateFlow()

    private val _consoleLogs = MutableStateFlow<List<String>>(emptyList())
    val consoleLogs = _consoleLogs.asStateFlow()

    fun getIndexHtmlContent(): String {
        return try {
            val file = sandboxManager.resolveSafePath("index.html")
            if (file.exists()) file.readText() else getFallbackHtml()
        } catch (e: Exception) {
            getFallbackHtml()
        }
    }

    fun addConsoleLog(log: String) {
        _consoleLogs.value = _consoleLogs.value + log
    }

    fun clearLogs() {
        _consoleLogs.value = emptyList()
    }

    private fun getFallbackHtml(): String = """
        <!DOCTYPE html>
        <html>
        <head>
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <style>
                body {
                    background: #090B10;
                    color: #F8FAFC;
                    font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
                    display: flex;
                    flex-direction: column;
                    align-items: center;
                    justify-content: center;
                    height: 100vh;
                    margin: 0;
                    padding: 20px;
                    text-align: center;
                }
                .card {
                    background: #11141D;
                    border: 1px solid #222938;
                    border-radius: 16px;
                    padding: 24px;
                    max-width: 400px;
                    box-shadow: 0 8px 32px rgba(0,0,0,0.4);
                }
                h1 { color: #6366F1; margin-top: 0; }
                p { color: #94A3B8; font-size: 14px; line-height: 1.5; }
                .badge {
                    display: inline-block;
                    background: #064E3B;
                    color: #10B981;
                    padding: 4px 12px;
                    border-radius: 999px;
                    font-size: 12px;
                    font-weight: 600;
                    margin-bottom: 12px;
                }
            </style>
        </head>
        <body>
            <div class="card">
                <div class="badge">LIVE PREVIEW READY</div>
                <h1>Antigravity Web App</h1>
                <p>Ask the AI Agent to build a web application, and watch it live update right here in real-time!</p>
            </div>
        </body>
        </html>
    """.trimIndent()
}
