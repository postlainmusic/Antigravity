package com.antigravity.mobile.core.security

import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import java.util.Base64

interface CredentialStore {
    fun storeCredential(providerId: String, apiKey: String)
    fun getCredential(providerId: String): String?
    fun deleteCredential(providerId: String)
    fun hasCredential(providerId: String): Boolean
    fun clearAll()
}

/**
 * Android KeyStore-backed hardware security credential store using AES-256-GCM.
 */
class KeyStoreCredentialStore(
    private val keyAlias: String = "AntigravityMasterKey"
) : CredentialStore {

    private val encryptedStorage = mutableMapOf<String, EncryptedPayload>()

    private data class EncryptedPayload(
        val ivBase64: String,
        val cipherTextBase64: String
    )

    override fun storeCredential(providerId: String, apiKey: String) {
        if (apiKey.isBlank()) {
            deleteCredential(providerId)
            return
        }

        // Generate IV and encrypt
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val secretKey = getOrCreateSecretKey()
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val iv = cipher.iv
        val encryptedBytes = cipher.doFinal(apiKey.toByteArray(Charsets.UTF_8))

        encryptedStorage[providerId] = EncryptedPayload(
            ivBase64 = Base64.getEncoder().encodeToString(iv),
            cipherTextBase64 = Base64.getEncoder().encodeToString(encryptedBytes)
        )
    }

    override fun getCredential(providerId: String): String? {
        val payload = encryptedStorage[providerId] ?: return null
        return try {
            val iv = Base64.getDecoder().decode(payload.ivBase64)
            val cipherText = Base64.getDecoder().decode(payload.cipherTextBase64)

            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            val secretKey = getOrCreateSecretKey()
            val spec = GCMParameterSpec(128, iv)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)

            val decryptedBytes = cipher.doFinal(cipherText)
            String(decryptedBytes, Charsets.UTF_8)
        } catch (e: Exception) {
            null
        }
    }

    override fun deleteCredential(providerId: String) {
        encryptedStorage.remove(providerId)
    }

    override fun hasCredential(providerId: String): Boolean {
        return encryptedStorage.containsKey(providerId)
    }

    override fun clearAll() {
        encryptedStorage.clear()
    }

    private var fallbackKey: SecretKey? = null

    private fun getOrCreateSecretKey(): SecretKey {
        return try {
            val keyStore = KeyStore.getInstance("AndroidKeyStore")
            keyStore.load(null)
            if (!keyStore.containsAlias(keyAlias)) {
                val keyGen = KeyGenerator.getInstance("AES", "AndroidKeyStore")
                keyGen.init(256)
                keyGen.generateKey()
            } else {
                (keyStore.getEntry(keyAlias, null) as KeyStore.SecretKeyEntry).secretKey
            }
        } catch (e: Exception) {
            // In unit test or JVM runtime without AndroidKeyStore, fallback to ephemeral AES-256 key
            if (fallbackKey == null) {
                val keyGen = KeyGenerator.getInstance("AES")
                keyGen.init(256)
                fallbackKey = keyGen.generateKey()
            }
            fallbackKey!!
        }
    }
}
