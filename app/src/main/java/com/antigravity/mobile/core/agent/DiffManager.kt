package com.antigravity.mobile.core.agent

import com.antigravity.mobile.domain.model.DiffChunk
import com.antigravity.mobile.domain.model.DiffHunk
import com.antigravity.mobile.domain.model.DiffType
import com.antigravity.mobile.domain.model.FileDiff

class DiffManager {

    /**
     * Computes line-by-line diff chunks between old and new text content.
     */
    fun computeDiff(filePath: String, oldText: String, newText: String): FileDiff {
        val oldLines = if (oldText.isEmpty()) emptyList() else oldText.lines()
        val newLines = if (newText.isEmpty()) emptyList() else newText.lines()

        val chunks = mutableListOf<DiffChunk>()
        var adds = 0
        var dels = 0

        var i = 0
        var j = 0

        while (i < oldLines.size || j < newLines.size) {
            if (i < oldLines.size && j < newLines.size && oldLines[i] == newLines[j]) {
                chunks.add(DiffChunk(DiffType.CONTEXT, oldLines[i], i + 1, j + 1))
                i++
                j++
            } else if (j < newLines.size && (i >= oldLines.size || !oldLines.contains(newLines[j]))) {
                chunks.add(DiffChunk(DiffType.ADDITION, newLines[j], null, j + 1))
                adds++
                j++
            } else if (i < oldLines.size) {
                chunks.add(DiffChunk(DiffType.DELETION, oldLines[i], i + 1, null))
                dels++
                i++
            }
        }

        val hunk = DiffHunk(
            oldStart = 1,
            oldCount = oldLines.size,
            newStart = 1,
            newCount = newLines.size,
            chunks = chunks
        )

        return FileDiff(
            filePath = filePath,
            oldContent = oldText,
            newContent = newText,
            hunks = listOf(hunk),
            additionsCount = adds,
            deletionsCount = dels
        )
    }
}
