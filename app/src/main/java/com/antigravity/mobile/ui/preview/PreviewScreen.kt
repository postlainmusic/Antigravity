package com.antigravity.mobile.ui.preview

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Tablet
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.antigravity.mobile.ui.theme.CodeMonoTypography
import com.antigravity.mobile.ui.theme.CyanNeon
import com.antigravity.mobile.ui.theme.DarkBorder
import com.antigravity.mobile.ui.theme.DarkSurface
import com.antigravity.mobile.ui.theme.DarkSurfaceElevated
import com.antigravity.mobile.ui.theme.DeepObsidian
import com.antigravity.mobile.ui.theme.IndigoGlow
import com.antigravity.mobile.ui.theme.TextMuted
import com.antigravity.mobile.ui.theme.TextPrimary
import com.antigravity.mobile.ui.theme.TextSecondary

enum class ViewportMode {
    MOBILE,
    TABLET,
    FULL
}

@Composable
fun PreviewScreen(
    htmlContent: String,
    consoleLogs: List<String>,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    var viewport by remember { mutableStateOf(ViewportMode.FULL) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DeepObsidian)
    ) {
        // Preview Action Bar
        PreviewTopBar(
            currentViewport = viewport,
            onViewportChange = { viewport = it },
            onRefresh = onRefresh
        )

        // Embedded Web Preview Viewport
        Box(
            modifier = Modifier
                .weight(0.7f)
                .fillMaxWidth()
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            val widthModifier = when (viewport) {
                ViewportMode.MOBILE -> Modifier.width(360.dp)
                ViewportMode.TABLET -> Modifier.width(600.dp)
                ViewportMode.FULL -> Modifier.fillMaxWidth()
            }

            Box(
                modifier = widthModifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
            ) {
                AndroidView(
                    factory = { context ->
                        WebView(context).apply {
                            settings.javaScriptEnabled = true
                            settings.domStorageEnabled = true
                            webViewClient = WebViewClient()
                            loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
                        }
                    },
                    update = { webView ->
                        webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // Live Console Log Inspector
        ConsoleInspectorPanel(
            logs = consoleLogs,
            modifier = Modifier
                .weight(0.3f)
                .fillMaxWidth()
        )
    }
}

@Composable
fun PreviewTopBar(
    currentViewport: ViewportMode,
    onViewportChange: (ViewportMode) -> Unit,
    onRefresh: () -> Unit,
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
        Text(
            text = "LIVE EMBEDDED PREVIEW",
            style = MaterialTheme.typography.labelSmall,
            color = CyanNeon
        )
        Spacer(modifier = Modifier.weight(1f))

        IconButton(onClick = { onViewportChange(ViewportMode.MOBILE) }) {
            Icon(
                imageVector = Icons.Default.PhoneAndroid,
                contentDescription = "Mobile Viewport",
                tint = if (currentViewport == ViewportMode.MOBILE) IndigoGlow else TextSecondary,
                modifier = Modifier.size(18.dp)
            )
        }
        IconButton(onClick = { onViewportChange(ViewportMode.TABLET) }) {
            Icon(
                imageVector = Icons.Default.Tablet,
                contentDescription = "Tablet Viewport",
                tint = if (currentViewport == ViewportMode.TABLET) IndigoGlow else TextSecondary,
                modifier = Modifier.size(18.dp)
            )
        }
        IconButton(onClick = { onViewportChange(ViewportMode.FULL) }) {
            Icon(
                imageVector = Icons.Default.Computer,
                contentDescription = "Full Viewport",
                tint = if (currentViewport == ViewportMode.FULL) IndigoGlow else TextSecondary,
                modifier = Modifier.size(18.dp)
            )
        }
        IconButton(onClick = onRefresh) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Reload",
                tint = TextPrimary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun ConsoleInspectorPanel(
    logs: List<String>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(DarkSurfaceElevated)
            .border(1.dp, DarkBorder)
            .padding(8.dp)
    ) {
        Text(
            text = "DEVELOPER CONSOLE",
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted,
            fontSize = 11.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            if (logs.isEmpty()) {
                item {
                    Text(
                        text = "No runtime errors or console messages.",
                        style = CodeMonoTypography.copy(fontSize = 12.sp, color = TextMuted)
                    )
                }
            } else {
                items(logs) { log ->
                    Text(
                        text = "> $log",
                        style = CodeMonoTypography.copy(fontSize = 12.sp, color = TextSecondary)
                    )
                }
            }
        }
    }
}
