package com.example

import com.example.model.CallType
import com.example.model.MessageType
import com.example.repository.NexChatRepository
import com.example.security.CryptoEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class NexChatUnitTest {

    @Test
    fun test_aes_gcm_256_encryption_and_decryption_roundtrip() {
        val originalMessage = "Top Secret Double Ratchet Handshake 2026"
        val encrypted = CryptoEngine.encrypt(originalMessage)
        assertNotNull(encrypted.ciphertext)
        assertNotNull(encrypted.nonce)

        val decrypted = CryptoEngine.decrypt(encrypted.ciphertext, encrypted.nonce)
        assertEquals(originalMessage, decrypted)
    }

    @Test
    fun test_repository_sends_encrypted_message_and_updates_conversation() {
        val repo = NexChatRepository()
        val initialCount = repo.messagesMap.value["conv_1"]?.size ?: 0

        repo.sendMessage(
            conversationId = "conv_1",
            content = "Testing real-time message stream",
            type = MessageType.TEXT
        )

        val updatedCount = repo.messagesMap.value["conv_1"]?.size ?: 0
        assertEquals(initialCount + 1, updatedCount)
    }

    @Test
    fun test_call_lifecycle_in_repository() {
        val repo = NexChatRepository()
        repo.startCall("Sarah Connor", CallType.VIDEO)
        assertNotNull(repo.activeCall.value)
        assertEquals("Sarah Connor", repo.activeCall.value?.contactName)

        repo.endCall()
        assertEquals(null, repo.activeCall.value)
        assertTrue(repo.callRecords.value.isNotEmpty())
    }

    @Test
    fun test_security_fingerprint_generation() {
        val fingerprint = CryptoEngine.generateSecurityFingerprint("user_me_001", "user_sarah")
        assertNotNull(fingerprint)
        assertTrue(fingerprint.length >= 12)
    }
}
