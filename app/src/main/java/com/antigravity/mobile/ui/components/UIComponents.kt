package com.antigravity.mobile.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.antigravity.mobile.domain.model.FileNode
import com.antigravity.mobile.domain.model.PendingAction
import com.antigravity.mobile.ui.theme.DarkBorder
import com.antigravity.mobile.ui.theme.DarkSurface
import com.antigravity.mobile.ui.theme.DarkSurfaceElevated
import com.antigravity.mobile.ui.theme.DeepObsidian
import com.antigravity.mobile.ui.theme.IndigoGlow
import com.antigravity.mobile.ui.theme.RoseError
import com.antigravity.mobile.ui.theme.TextMuted
import com.antigravity.mobile.ui.theme.TextPrimary
import com.antigravity.mobile.ui.theme.TextSecondary

@Composable
fun AppTopBar(
    projectName: String,
    onToggleDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(DarkSurface)
            .border(1.dp, DarkBorder)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onToggleDrawer) {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "Toggle Project Drawer",
                tint = TextPrimary
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = projectName,
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun FileTreeDrawerContent(
    fileTree: FileNode?,
    onFileSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .width(280.dp)
            .background(DarkSurface)
            .border(1.dp, DarkBorder)
            .padding(16.dp)
    ) {
        Text(
            text = "PROJECT FILES",
            style = MaterialTheme.typography.labelSmall,
            color = IndigoGlow,
            fontSize = 11.sp
        )
        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            if (fileTree != null) {
                renderFileTree(fileTree, 0, onFileSelected)
            }
        }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.renderFileTree(
    node: FileNode,
    depth: Int,
    onFileSelected: (String) -> Unit
) {
    item(key = node.path) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    if (!node.isDirectory) {
                        onFileSelected(node.name)
                    }
                }
                .padding(vertical = 4.dp, horizontal = (depth * 12).dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (node.isDirectory) Icons.Default.Folder else Icons.Default.Description,
                contentDescription = null,
                tint = if (node.isDirectory) IndigoGlow else TextSecondary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = node.name,
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimary
            )
        }
    }

    node.children.forEach { child ->
        renderFileTree(child, depth + 1, onFileSelected)
    }
}

@Composable
fun PermissionDialog(
    action: PendingAction,
    onApprove: (String) -> Unit,
    onReject: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = { onReject(action.actionId) },
        title = {
            Text(text = action.title, style = MaterialTheme.typography.headlineMedium)
        },
        text = {
            Column {
                Text(text = action.description, style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Permission Tier: ${action.permissionTier}",
                    style = MaterialTheme.typography.labelSmall,
                    color = RoseError
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onApprove(action.actionId) },
                colors = ButtonDefaults.buttonColors(containerColor = IndigoGlow)
            ) {
                Text("Allow")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = { onReject(action.actionId) }) {
                Text("Deny")
            }
        },
        containerColor = DarkSurfaceElevated,
        titleContentColor = TextPrimary,
        textContentColor = TextSecondary
    )
}
