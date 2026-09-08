package com.antigravity.mobile.core.terminal

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import com.antigravity.mobile.ui.theme.EmeraldSuccess
import com.antigravity.mobile.ui.theme.RoseError
import com.antigravity.mobile.ui.theme.SyntaxString
import com.antigravity.mobile.ui.theme.TextPrimary
import com.antigravity.mobile.ui.theme.TextSecondary

object AnsiParser {

    private val ANSI_REGEX = Regex("""\u001B\[([0-9;]*)m""")

    /**
     * Parses ANSI escape sequences into a styled Compose AnnotatedString.
     */
    fun parseAnsiToAnnotatedString(rawText: String): AnnotatedString {
        return buildAnnotatedString {
            var currentIndex = 0
            var currentColor = TextPrimary
            var isBold = false

            val matches = ANSI_REGEX.findAll(rawText).toList()

            for (match in matches) {
                if (match.range.first > currentIndex) {
                    val textSegment = rawText.substring(currentIndex, match.range.first)
                    appendStyledText(textSegment, currentColor, isBold)
                }

                val codes = match.groupValues[1].split(";").mapNotNull { it.toIntOrNull() }
                for (code in codes) {
                    when (code) {
                        0 -> { currentColor = TextPrimary; isBold = false }
                        1 -> isBold = true
                        31 -> currentColor = RoseError
                        32 -> currentColor = EmeraldSuccess
                        33 -> currentColor = SyntaxString
                        34 -> currentColor = Color(0xFF60A5FA)
                        35 -> currentColor = Color(0xFFC084FC)
                        36 -> currentColor = Color(0xFF38BDF8)
                        37 -> currentColor = TextSecondary
                    }
                }
                currentIndex = match.range.last + 1
            }

            if (currentIndex < rawText.length) {
                appendStyledText(rawText.substring(currentIndex), currentColor, isBold)
            }
        }
    }

    private fun AnnotatedString.Builder.appendStyledText(text: String, color: Color, isBold: Boolean) {
        val start = length
        append(text)
        addStyle(
            style = SpanStyle(
                color = color,
                fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal
            ),
            start = start,
            end = length
        )
    }
}
