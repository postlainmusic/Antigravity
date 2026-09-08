package com.antigravity.mobile.ui.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.antigravity.mobile.ui.theme.CodeMonoTypography
import com.antigravity.mobile.ui.theme.DarkBorder
import com.antigravity.mobile.ui.theme.DarkSurface
import com.antigravity.mobile.ui.theme.DarkSurfaceElevated
import com.antigravity.mobile.ui.theme.IndigoGlow
import com.antigravity.mobile.ui.theme.TextPrimary

@Composable
fun KeyboardAccessoryBar(
    onInsertSymbol: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val symbols = listOf(
        "{", "}", "(", ")", "[", "]",
        ";", ":", "<", ">", "=", "+",
        "-", "*", "/", "\\", "$", "->",
        "\"", "'", "#", "@", "!", "?"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(44.dp)
            .background(DarkSurface)
            .padding(horizontal = 4.dp, vertical = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            symbols.forEach { symbol ->
                TextButton(
                    onClick = { onInsertSymbol(symbol) },
                    modifier = Modifier
                        .height(36.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(DarkSurfaceElevated),
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = TextPrimary
                    )
                ) {
                    Text(
                        text = symbol,
                        style = CodeMonoTypography.copy(fontSize = 14.sp, color = IndigoGlow)
                    )
                }
            }
        }
    }
}
