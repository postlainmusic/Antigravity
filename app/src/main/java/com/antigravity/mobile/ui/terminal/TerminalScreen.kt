package com.antigravity.mobile.ui.terminal

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.antigravity.mobile.core.terminal.AnsiParser
import com.antigravity.mobile.domain.model.TerminalSession
import com.antigravity.mobile.ui.theme.CodeMonoTypography
import com.antigravity.mobile.ui.theme.DarkBorder
import com.antigravity.mobile.ui.theme.DarkSurface
import com.antigravity.mobile.ui.theme.DarkSurfaceElevated
import com.antigravity.mobile.ui.theme.DeepObsidian
import com.antigravity.mobile.ui.theme.EmeraldSuccess
import com.antigravity.mobile.ui.theme.IndigoGlow
import com.antigravity.mobile.ui.theme.TextMuted
import com.antigravity.mobile.ui.theme.TextPrimary
import com.antigravity.mobile.ui.theme.TextSecondary

@Composable
fun TerminalScreen(
    session: TerminalSession?,
    onExecuteCommand: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var commandInput by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DeepObsidian)
            .imePadding()
    ) {
        // Terminal Header
        TerminalHeader(title = session?.title ?: "Terminal Shell")

        // ANSI Terminal Log Output
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(12.dp),
            reverseLayout = false
        ) {
            session?.outputHistory?.let { lines ->
                items(lines) { line ->
                    val styled = remember(line.text) {
                        AnsiParser.parseAnsiToAnnotatedString(line.text)
                    }
                    Text(
                        text = styled,
                        style = CodeMonoTypography.copy(fontSize = 12.sp, lineHeight = 18.sp)
                    )
                }
            }
        }

        // Quick Command Chips
        QuickCommandChips(onSelectCommand = { cmd ->
            commandInput = cmd
        })

        // Command Input Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkSurface)
                .border(1.dp, DarkBorder)
                .padding(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "$ ",
                    style = CodeMonoTypography.copy(color = EmeraldSuccess, fontSize = 14.sp),
                    modifier = Modifier.padding(start = 8.dp)
                )
                BasicTextField(
                    value = commandInput,
                    onValueChange = { commandInput = it },
                    textStyle = CodeMonoTypography.copy(color = TextPrimary, fontSize = 13.sp),
                    cursorBrush = SolidColor(EmeraldSuccess),
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = {
                        if (commandInput.isNotBlank()) {
                            onExecuteCommand(commandInput)
                            commandInput = ""
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Execute Command",
                        tint = EmeraldSuccess,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun TerminalHeader(
    title: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(DarkSurface)
            .border(1.dp, DarkBorder)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Terminal,
            contentDescription = null,
            tint = EmeraldSuccess,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary
        )
    }
}

@Composable
fun QuickCommandChips(
    onSelectCommand: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val quickCmds = listOf("npm test", "git status", "ls -la", "node -v", "pwd", "clear")

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(DarkSurfaceElevated)
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        quickCmds.forEach { cmd ->
            TextButton(
                onClick = { onSelectCommand(cmd) },
                modifier = Modifier
                    .height(30.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(DarkSurface),
                colors = ButtonDefaults.textButtonColors(contentColor = TextSecondary)
            ) {
                Text(text = cmd, style = CodeMonoTypography.copy(fontSize = 11.sp, color = IndigoGlow))
            }
        }
    }
}
