package com.antigravity.mobile

import com.antigravity.mobile.core.security.SecretScrubber
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class SecretScrubberTest {

    @Test
    fun `scrub masks OpenAI and Google API keys`() {
        val mockOpenAiKey = "sk-" + "dummykey1234567890abcdef1234567890"
        val mockGoogleKey = "AIza" + "SyD123456789012345678901234567890"
        val raw = "Authorization: Bearer $mockOpenAiKey and $mockGoogleKey"
        val scrubbed = SecretScrubber.scrub(raw)

        assertFalse(scrubbed.contains(mockOpenAiKey))
        assertFalse(scrubbed.contains(mockGoogleKey))
    }

    @Test
    fun `scrub leaves normal text unchanged`() {
        val normal = "Building standard Jetpack Compose UI component"
        assertEquals(normal, SecretScrubber.scrub(normal))
    }
}
