package com.antigravity.mobile.ui.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.antigravity.mobile.ui.theme.CodeMonoTypography
import com.antigravity.mobile.ui.theme.DarkBorder
import com.antigravity.mobile.ui.theme.DarkSurface
import com.antigravity.mobile.ui.theme.DarkSurfaceElevated
import com.antigravity.mobile.ui.theme.DeepObsidian
import com.antigravity.mobile.ui.theme.EmeraldSuccess
import com.antigravity.mobile.ui.theme.IndigoGlow
import com.antigravity.mobile.ui.theme.SyntaxComment
import com.antigravity.mobile.ui.theme.SyntaxKeyword
import com.antigravity.mobile.ui.theme.SyntaxNumber
import com.antigravity.mobile.ui.theme.SyntaxString
import com.antigravity.mobile.ui.theme.SyntaxType
import com.antigravity.mobile.ui.theme.TextMuted
import com.antigravity.mobile.ui.theme.TextPrimary
import com.antigravity.mobile.ui.theme.TextSecondary

@Composable
fun CodeEditorScreen(
    filePath: String?,
    initialContent: String,
    onSaveFile: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var content by remember(initialContent) { mutableStateOf(initialContent) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DeepObsidian)
            .imePadding()
    ) {
        // File Header & Actions
        EditorHeader(
            filePath = filePath ?: "Untitled",
            onSave = {
                if (filePath != null) {
                    onSaveFile(filePath, content)
                }
            }
        )

        // Virtualized Line-by-Line Code Editor
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(DeepObsidian)
                .padding(8.dp)
        ) {
            val lines = content.lines()

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .horizontalScroll(rememberScrollState())
            ) {
                // Line Numbers Column
                Column(
                    modifier = Modifier
                        .padding(end = 12.dp)
                        .width(36.dp)
                ) {
                    lines.forEachIndexed { index, _ ->
                        Text(
                            text = "${index + 1}",
                            style = CodeMonoTypography.copy(
                                color = TextMuted,
                                fontSize = 12.sp,
                                lineHeight = 20.sp
                            ),
                            modifier = Modifier.height(20.dp)
                        )
                    }
                }

                // Interactive Text Field
                BasicTextField(
                    value = content,
                    onValueChange = { content = it },
                    textStyle = CodeMonoTypography.copy(
                        color = TextPrimary,
                        fontSize = 12.sp,
                        lineHeight = 20.sp
                    ),
                    cursorBrush = SolidColor(IndigoGlow),
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // Virtual Keyboard Accessory Bar
        KeyboardAccessoryBar(
            onInsertSymbol = { symbol ->
                content += symbol
            }
        )
    }
}

@Composable
fun EditorHeader(
    filePath: String,
    onSave: () -> Unit,
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
            imageVector = Icons.Default.Code,
            contentDescription = null,
            tint = IndigoGlow,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = filePath,
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = onSave) {
            Icon(
                imageVector = Icons.Default.Save,
                contentDescription = "Save File",
                tint = EmeraldSuccess,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
