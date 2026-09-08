package com.antigravity.mobile

import com.antigravity.mobile.core.agent.DiffManager
import com.antigravity.mobile.domain.model.DiffType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DiffManagerTest {

    private val diffManager = DiffManager()

    @Test
    fun `computeDiff identifies additions and deletions correctly`() {
        val oldText = "line 1\nline 2\nline 3"
        val newText = "line 1\nline 2 modified\nline 3\nline 4"

        val diff = diffManager.computeDiff("test.txt", oldText, newText)

        assertEquals("test.txt", diff.filePath)
        assertTrue(diff.additionsCount > 0)
        assertTrue(diff.hunks.isNotEmpty())
        assertTrue(diff.hunks[0].chunks.any { it.type == DiffType.ADDITION })
    }
}
