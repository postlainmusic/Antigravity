package com.antigravity.mobile.ui.git

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Difference
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.antigravity.mobile.domain.model.DiffChunk
import com.antigravity.mobile.domain.model.DiffType
import com.antigravity.mobile.domain.model.FileDiff
import com.antigravity.mobile.ui.theme.CodeMonoTypography
import com.antigravity.mobile.ui.theme.DarkBorder
import com.antigravity.mobile.ui.theme.DarkSurface
import com.antigravity.mobile.ui.theme.DarkSurfaceElevated
import com.antigravity.mobile.ui.theme.DeepObsidian
import com.antigravity.mobile.ui.theme.DiffAddGreen
import com.antigravity.mobile.ui.theme.DiffAddGreenBg
import com.antigravity.mobile.ui.theme.DiffDeleteRed
import com.antigravity.mobile.ui.theme.DiffDeleteRedBg
import com.antigravity.mobile.ui.theme.EmeraldSuccess
import com.antigravity.mobile.ui.theme.IndigoGlow
import com.antigravity.mobile.ui.theme.RoseError
import com.antigravity.mobile.ui.theme.TextMuted
import com.antigravity.mobile.ui.theme.TextPrimary
import com.antigravity.mobile.ui.theme.TextSecondary

@Composable
fun GitDiffScreen(
    diff: FileDiff?,
    onApplyDiff: (FileDiff) -> Unit,
    onDiscardDiff: (FileDiff) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DeepObsidian)
    ) {
        // Diff Header
        GitDiffHeader(
            filePath = diff?.filePath ?: "No Pending Diff",
            additions = diff?.additionsCount ?: 0,
            deletions = diff?.deletionsCount ?: 0
        )

        if (diff == null) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Working tree clean. No pending diffs.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextMuted
                )
            }
        } else {
            // Diff Line Stream
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                diff.hunks.forEach { hunk ->
                    items(hunk.chunks) { chunk ->
                        DiffChunkRow(chunk = chunk)
                    }
                }
            }

            // Bottom Action Bar
            DiffActionBar(
                onApply = { onApplyDiff(diff) },
                onDiscard = { onDiscardDiff(diff) }
            )
        }
    }
}

@Composable
fun GitDiffHeader(
    filePath: String,
    additions: Int,
    deletions: Int,
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
            imageVector = Icons.Default.Difference,
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
        if (additions > 0 || deletions > 0) {
            Text(
                text = "+$additions",
                style = CodeMonoTypography.copy(fontSize = 12.sp, color = DiffAddGreen)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "-$deletions",
                style = CodeMonoTypography.copy(fontSize = 12.sp, color = DiffDeleteRed)
            )
        }
    }
}

@Composable
fun DiffChunkRow(
    chunk: DiffChunk,
    modifier: Modifier = Modifier
) {
    val bgColor = when (chunk.type) {
        DiffType.ADDITION -> DiffAddGreenBg.copy(alpha = 0.3f)
        DiffType.DELETION -> DiffDeleteRedBg.copy(alpha = 0.3f)
        DiffType.CONTEXT -> DeepObsidian
    }

    val textColor = when (chunk.type) {
        DiffType.ADDITION -> DiffAddGreen
        DiffType.DELETION -> DiffDeleteRed
        DiffType.CONTEXT -> TextSecondary
    }

    val prefix = when (chunk.type) {
        DiffType.ADDITION -> "+ "
        DiffType.DELETION -> "- "
        DiffType.CONTEXT -> "  "
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(bgColor)
            .padding(horizontal = 4.dp, vertical = 2.dp)
    ) {
        Text(
            text = "${chunk.oldLineNum?.toString() ?: " "} ${chunk.newLineNum?.toString() ?: " "}",
            style = CodeMonoTypography.copy(fontSize = 11.sp, color = TextMuted),
            modifier = Modifier.width(48.dp)
        )
        Text(
            text = "$prefix${chunk.content}",
            style = CodeMonoTypography.copy(fontSize = 12.sp, color = textColor),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun DiffActionBar(
    onApply: () -> Unit,
    onDiscard: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(DarkSurface)
            .border(1.dp, DarkBorder)
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedButton(
            onClick = onDiscard,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = RoseError)
        ) {
            Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Discard")
        }

        Button(
            onClick = onApply,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess)
        ) {
            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Apply Diff")
        }
    }
}
