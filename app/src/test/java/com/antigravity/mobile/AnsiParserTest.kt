package com.antigravity.mobile

import com.antigravity.mobile.core.terminal.AnsiParser
import org.junit.Assert.assertEquals
import org.junit.Test

class AnsiParserTest {

    @Test
    fun `parseAnsiToAnnotatedString strips escape codes and extracts plain text`() {
        val ansiText = "\u001B[32mSuccess\u001B[0m: Test passed!"
        val annotated = AnsiParser.parseAnsiToAnnotatedString(ansiText)

        assertEquals("Success: Test passed!", annotated.text)
    }
}
