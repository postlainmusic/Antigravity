package com.antigravity.mobile

import com.antigravity.mobile.core.model.CompletionRequest
import com.antigravity.mobile.core.model.GeminiModelProvider
import com.antigravity.mobile.core.model.LocalModelProvider
import com.antigravity.mobile.core.model.ModelCapabilities
import com.antigravity.mobile.core.model.ModelPolicy
import com.antigravity.mobile.core.model.ModelRegistry
import com.antigravity.mobile.core.model.ModelRouter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ModelRouterTest {

    private lateinit var registry: ModelRegistry
    private lateinit var router: ModelRouter

    @Before
    fun setUp() {
        registry = ModelRegistry()
        registry.register(GeminiModelProvider { "mock-key-for-test" })
        registry.register(LocalModelProvider())
        router = ModelRouter(registry)
    }

    @Test
    fun testSelectDefaultOptimalModel() {
        val request = CompletionRequest(
            modelId = "auto",
            messages = emptyList(),
            requiredCapabilities = ModelCapabilities(coding = true, reasoning = true)
        )

        val (provider, descriptor) = router.selectOptimalModel(request)
        assertNotNull(provider)
        assertNotNull(descriptor)
        assertEquals("gemini-1.5-pro", descriptor.id)
    }

    @Test
    fun testSelectOfflineModelWhenPolicyRequiresOffline() {
        router.updatePolicy(ModelPolicy(requireOffline = true))

        val request = CompletionRequest(
            modelId = "auto",
            messages = emptyList()
        )

        val (provider, descriptor) = router.selectOptimalModel(request)
        assertEquals("local", provider.id)
        assertTrue(descriptor.capabilities.offline)
        assertEquals("gemma-2b-it", descriptor.id)
    }
}
