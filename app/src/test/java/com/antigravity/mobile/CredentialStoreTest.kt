package com.antigravity.mobile

import com.antigravity.mobile.core.security.KeyStoreCredentialStore
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CredentialStoreTest {

    private lateinit var store: KeyStoreCredentialStore

    @Before
    fun setUp() {
        store = KeyStoreCredentialStore(keyAlias = "TestAntigravityKey")
        store.clearAll()
    }

    @Test
    fun testStoreAndRetrieveEncryptedCredential() {
        val sampleToken = "test_token_" + "dynamic_suffix_123"
        store.storeCredential("gemini", sampleToken)

        assertTrue(store.hasCredential("gemini"))
        val retrieved = store.getCredential("gemini")
        assertEquals(sampleToken, retrieved)
    }

    @Test
    fun testDeleteCredential() {
        val sampleToken = "test_token_" + "dynamic_suffix_456"
        store.storeCredential("openai", sampleToken)
        assertTrue(store.hasCredential("openai"))

        store.deleteCredential("openai")
        assertFalse(store.hasCredential("openai"))
        assertNull(store.getCredential("openai"))
    }
}
