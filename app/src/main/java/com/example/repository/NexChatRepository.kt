package com.example.repository

import com.example.model.*
import com.example.security.CryptoEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class NexChatRepository {

    // Current Authenticated User
    private val _currentUser = MutableStateFlow(
        UserProfile(
            id = "user_me_001",
            username = "alex_vortex",
            displayName = "Alex Vance",
            phoneNumber = "+1 555 019 2834",
            email = "alex.vance@nexchat.io",
            bio = "Building the decentralized future on NEXCHAT. ⚡",
            status = UserStatus.ONLINE,
            isAdmin = true,
            isVerified = true
        )
    )
    val currentUser: StateFlow<UserProfile> = _currentUser.asStateFlow()

    // Conversations List
    private val _conversations = MutableStateFlow<List<Conversation>>(emptyList())
    val conversations: StateFlow<List<Conversation>> = _conversations.asStateFlow()

    // Messages per Conversation ID
    private val _messagesMap = MutableStateFlow<Map<String, List<Message>>>(emptyMap())
    val messagesMap: StateFlow<Map<String, List<Message>>> = _messagesMap.asStateFlow()

    // Status Stories
    private val _statusStories = MutableStateFlow<List<StatusStory>>(emptyList())
    val statusStories: StateFlow<List<StatusStory>> = _statusStories.asStateFlow()

    // Call Records
    private val _callRecords = MutableStateFlow<List<CallRecord>>(emptyList())
    val callRecords: StateFlow<List<CallRecord>> = _callRecords.asStateFlow()

    // Communities
    private val _communities = MutableStateFlow<List<Community>>(emptyList())
    val communities: StateFlow<List<Community>> = _communities.asStateFlow()

    // Admin Reports
    private val _reports = MutableStateFlow<List<UserReport>>(emptyList())
    val reports: StateFlow<List<UserReport>> = _reports.asStateFlow()

    // Active Call State
    private val _activeCall = MutableStateFlow<CallRecord?>(null)
    val activeCall: StateFlow<CallRecord?> = _activeCall.asStateFlow()

    // Active App Language
    private val _currentLanguage = MutableStateFlow(AppLanguage.ENGLISH)
    val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

    // Supabase Live Backend Connection Status
    private val _supabaseStatus = MutableStateFlow(
        com.example.network.SupabaseClient.SupabaseConnectionStatus(
            isConnected = true,
            endpoint = com.example.network.SupabaseClient.supabaseUrl,
            message = "Supabase Connected • refaejedfgunfioxhdzu"
        )
    )
    val supabaseStatus: StateFlow<com.example.network.SupabaseClient.SupabaseConnectionStatus> = _supabaseStatus.asStateFlow()

    fun checkSupabaseHealth() {
        kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
            val status = com.example.network.SupabaseClient.testConnection()
            _supabaseStatus.value = status
        }
    }

    init {
        seedInitialData()
    }

    private fun seedInitialData() {
        // Initial conversations
        val conv1 = Conversation(
            id = "conv_1",
            type = ConversationType.DIRECT,
            title = "Sarah Connor",
            subtitle = "Online",
            unreadCount = 2,
            isPinned = true,
            otherUserId = "user_sarah",
            otherUserStatus = UserStatus.ONLINE,
            isEncrypted = true
        )

        val conv2 = Conversation(
            id = "conv_2",
            type = ConversationType.GROUP,
            title = "NEXCHAT Core Architecture",
            subtitle = "Liam: E2EE handshake confirmed.",
            unreadCount = 0,
            isPinned = true,
            membersCount = 18,
            isEncrypted = true
        )

        val conv3 = Conversation(
            id = "conv_3",
            type = ConversationType.DIRECT,
            title = "Dr. Elena Rostova",
            subtitle = "Last seen today at 11:42",
            unreadCount = 0,
            otherUserId = "user_elena",
            otherUserStatus = UserStatus.AWAY,
            isEncrypted = true
        )

        val conv4 = Conversation(
            id = "conv_4",
            type = ConversationType.COMMUNITY,
            title = "Global WebRTC Developers",
            subtitle = "Announcement: Mesh signaling latency reduced by 40%",
            unreadCount = 5,
            membersCount = 1420,
            isEncrypted = false
        )

        _conversations.value = listOf(conv1, conv2, conv3, conv4)

        // Initial messages for conv_1
        val m1 = Message(
            id = "m1",
            conversationId = "conv_1",
            senderId = "user_sarah",
            senderName = "Sarah Connor",
            content = "Hey Alex! Did you review the double ratchet session keys?",
            timestamp = "10:30 AM",
            deliveryStatus = DeliveryStatus.READ,
            isEncrypted = true
        )
        val m2 = Message(
            id = "m2",
            conversationId = "conv_1",
            senderId = "user_me_001",
            senderName = "Alex Vance",
            content = "Yes, AES-256-GCM verification passed with flying colors. The nonce rotation is rock solid.",
            timestamp = "10:32 AM",
            deliveryStatus = DeliveryStatus.READ,
            isEncrypted = true,
            reactions = mapOf("🔒" to 1, "🚀" to 1)
        )
        val m3 = Message(
            id = "m3",
            conversationId = "conv_1",
            senderId = "user_sarah",
            senderName = "Sarah Connor",
            type = MessageType.VOICE_NOTE,
            content = "Voice note (0:18)",
            timestamp = "10:35 AM",
            deliveryStatus = DeliveryStatus.READ,
            attachment = Attachment(
                id = "att_vn1",
                fileName = "voice_note_1035.opus",
                fileType = "audio/ogg",
                fileSizeBytes = 48200,
                durationSeconds = 18,
                url = "voice_note",
                isEncrypted = true
            )
        )
        val m4 = Message(
            id = "m4",
            conversationId = "conv_1",
            senderId = "user_sarah",
            senderName = "Sarah Connor",
            content = "Let's test the WebRTC HD video call when you're available!",
            timestamp = "10:36 AM",
            deliveryStatus = DeliveryStatus.DELIVERED,
            isEncrypted = true
        )

        _messagesMap.value = mapOf(
            "conv_1" to listOf(m1, m2, m3, m4),
            "conv_2" to listOf(
                Message(
                    id = "m_g1",
                    conversationId = "conv_2",
                    senderId = "user_liam",
                    senderName = "Liam Dev",
                    content = "Deploying v2.4 of the Supabase Realtime channel cluster.",
                    timestamp = "09:15 AM",
                    deliveryStatus = DeliveryStatus.READ
                ),
                Message(
                    id = "m_g2",
                    conversationId = "conv_2",
                    senderId = "user_me_001",
                    senderName = "Alex Vance",
                    content = "All RLS policies verified. Row-level security active.",
                    timestamp = "09:20 AM",
                    deliveryStatus = DeliveryStatus.READ
                )
            )
        )

        // Status stories
        _statusStories.value = listOf(
            StatusStory(
                id = "st_sarah",
                userId = "user_sarah",
                userName = "Sarah Connor",
                userAvatar = null,
                caption = "Working on the E2EE encryption verification protocols 🛡️",
                timestampText = "35 minutes ago",
                backgroundColorHex = "#005C4B",
                viewsCount = 14
            ),
            StatusStory(
                id = "st_elena",
                userId = "user_elena",
                userName = "Dr. Elena Rostova",
                userAvatar = null,
                caption = "Audio spectrum analysis for WebRTC active noise cancellation 🎙️",
                timestampText = "2 hours ago",
                backgroundColorHex = "#1F2C34",
                viewsCount = 28
            ),
            StatusStory(
                id = "st_core",
                userId = "user_core",
                userName = "NEXCHAT Team",
                userAvatar = null,
                caption = "Welcome to NEXCHAT! 100% private, zero-compromise communication.",
                timestampText = "5 hours ago",
                backgroundColorHex = "#0C1317",
                viewsCount = 189
            )
        )

        // Call records
        _callRecords.value = listOf(
            CallRecord(
                id = "c1",
                contactName = "Sarah Connor",
                contactAvatar = null,
                callType = CallType.VIDEO,
                isIncoming = false,
                status = CallStatus.ENDED,
                timestamp = "Today, 10:15 AM",
                durationFormatted = "14:22"
            ),
            CallRecord(
                id = "c2",
                contactName = "Dr. Elena Rostova",
                contactAvatar = null,
                callType = CallType.AUDIO,
                isIncoming = true,
                status = CallStatus.MISSED,
                timestamp = "Yesterday, 6:40 PM",
                durationFormatted = "Missed"
            ),
            CallRecord(
                id = "c3",
                contactName = "NEXCHAT Core Arch",
                contactAvatar = null,
                callType = CallType.AUDIO,
                isIncoming = true,
                status = CallStatus.ENDED,
                timestamp = "Nov 7, 3:12 PM",
                durationFormatted = "45:10"
            )
        )

        // Communities
        _communities.value = listOf(
            Community(
                id = "comm_1",
                name = "Cybersecurity & Cryptography",
                description = "Discussions on post-quantum cryptography, Signal protocol, and zero-knowledge proofs.",
                avatarUrl = null,
                memberCount = 2840,
                channels = listOf(
                    Conversation(
                        id = "chan_1",
                        type = ConversationType.CHANNEL,
                        title = "Announcements",
                        subtitle = "Admin updates",
                        unreadCount = 1
                    ),
                    Conversation(
                        id = "chan_2",
                        type = ConversationType.CHANNEL,
                        title = "E2EE-Audits",
                        subtitle = "Key exchange discussions",
                        unreadCount = 0
                    )
                )
            ),
            Community(
                id = "comm_2",
                name = "Open Source Android Devs",
                description = "Jetpack Compose, Kotlin Multiplatform, and WebRTC streaming enthusiasts.",
                avatarUrl = null,
                memberCount = 5120
            )
        )

        // Admin Reports
        _reports.value = listOf(
            UserReport(
                id = "rep_1",
                reporterName = "Dr. Elena Rostova",
                reportedUserName = "SpamBot_99",
                reason = "Unsolicited promotional messages in public community channel.",
                status = ReportStatus.PENDING,
                date = "Nov 9, 2026"
            ),
            UserReport(
                id = "rep_2",
                reporterName = "Alex Vance",
                reportedUserName = "Impersonator_Account",
                reason = "Impersonation of platform moderators.",
                status = ReportStatus.RESOLVED,
                date = "Nov 8, 2026"
            )
        )
    }

    // Message Actions
    fun sendMessage(
        conversationId: String,
        content: String,
        type: MessageType = MessageType.TEXT,
        replyTo: Message? = null,
        attachment: Attachment? = null
    ) {
        val user = _currentUser.value
        val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
        val timestamp = timeFormat.format(Date())

        // Run through real AES-GCM encryption verification
        val encrypted = CryptoEngine.encrypt(content)
        val decryptedVerified = CryptoEngine.decrypt(encrypted.ciphertext, encrypted.nonce)

        val newMsg = Message(
            id = "msg_" + UUID.randomUUID().toString().take(8),
            conversationId = conversationId,
            senderId = user.id,
            senderName = user.displayName,
            type = type,
            content = decryptedVerified, // rendered decrypted for verified local participant
            timestamp = timestamp,
            deliveryStatus = DeliveryStatus.SENT,
            isEncrypted = true,
            replyToMessageId = replyTo?.id,
            replyToSnippet = replyTo?.content?.take(40),
            attachment = attachment
        )

        val currentList = _messagesMap.value[conversationId] ?: emptyList()
        val updatedList = currentList + newMsg
        _messagesMap.value = _messagesMap.value + (conversationId to updatedList)

        // Update conversation subtitle / timestamp
        val convs = _conversations.value.map { conv ->
            if (conv.id == conversationId) {
                conv.copy(
                    subtitle = if (type == MessageType.VOICE_NOTE) "🎤 Voice note" else content,
                    lastMessage = newMsg
                )
            } else conv
        }
        _conversations.value = convs

        // Simulate delivery & read acknowledgement in background
        simulatePeerDeliveryAndReply(conversationId, newMsg)
    }

    private fun simulatePeerDeliveryAndReply(conversationId: String, sentMsg: Message) {
        // Update delivery status to DELIVERED then READ
        val currentList = _messagesMap.value[conversationId] ?: return
        val markedDelivered = currentList.map {
            if (it.id == sentMsg.id) it.copy(deliveryStatus = DeliveryStatus.READ) else it
        }
        _messagesMap.value = _messagesMap.value + (conversationId to markedDelivered)
    }

    fun toggleReaction(conversationId: String, messageId: String, emoji: String) {
        val list = _messagesMap.value[conversationId] ?: return
        val updated = list.map { msg ->
            if (msg.id == messageId) {
                val currentReactions = msg.reactions.toMutableMap()
                val currentSelected = msg.userReaction
                if (currentSelected == emoji) {
                    // remove
                    val count = (currentReactions[emoji] ?: 1) - 1
                    if (count <= 0) currentReactions.remove(emoji) else currentReactions[emoji] = count
                    msg.copy(reactions = currentReactions, userReaction = null)
                } else {
                    // replace or add
                    if (currentSelected != null) {
                        val prevCount = (currentReactions[currentSelected] ?: 1) - 1
                        if (prevCount <= 0) currentReactions.remove(currentSelected) else currentReactions[currentSelected] = prevCount
                    }
                    currentReactions[emoji] = (currentReactions[emoji] ?: 0) + 1
                    msg.copy(reactions = currentReactions, userReaction = emoji)
                }
            } else msg
        }
        _messagesMap.value = _messagesMap.value + (conversationId to updated)
    }

    fun deleteMessage(conversationId: String, messageId: String) {
        val list = _messagesMap.value[conversationId] ?: return
        val updated = list.map {
            if (it.id == messageId) it.copy(isDeleted = true, content = "This message was deleted") else it
        }
        _messagesMap.value = _messagesMap.value + (conversationId to updated)
    }

    fun pinConversation(conversationId: String) {
        _conversations.value = _conversations.value.map {
            if (it.id == conversationId) it.copy(isPinned = !it.isPinned) else it
        }
    }

    fun archiveConversation(conversationId: String) {
        _conversations.value = _conversations.value.map {
            if (it.id == conversationId) it.copy(isArchived = !it.isArchived) else it
        }
    }

    // Call Actions
    fun startCall(contactName: String, type: CallType) {
        val record = CallRecord(
            id = "call_" + UUID.randomUUID().toString().take(6),
            contactName = contactName,
            contactAvatar = null,
            callType = type,
            isIncoming = false,
            status = CallStatus.ONGOING,
            timestamp = "Just now",
            durationFormatted = "00:01"
        )
        _activeCall.value = record
    }

    fun endCall() {
        val current = _activeCall.value
        if (current != null) {
            val finished = current.copy(status = CallStatus.ENDED, durationFormatted = "01:24")
            _callRecords.value = listOf(finished) + _callRecords.value
            _activeCall.value = null
        }
    }

    // Status Story creation
    fun publishStatus(caption: String, bgHex: String) {
        val user = _currentUser.value
        val newStory = StatusStory(
            id = "status_" + UUID.randomUUID().toString().take(6),
            userId = user.id,
            userName = "My Status",
            userAvatar = null,
            caption = caption,
            timestampText = "Just now",
            backgroundColorHex = bgHex,
            viewsCount = 0
        )
        _statusStories.value = listOf(newStory) + _statusStories.value
    }

    // Language switcher
    fun setLanguage(lang: AppLanguage) {
        _currentLanguage.value = lang
    }

    // Profile updates
    fun updateProfile(name: String, bio: String, phone: String) {
        _currentUser.value = _currentUser.value.copy(
            displayName = name,
            bio = bio,
            phoneNumber = phone
        )
    }

    // Admin Actions
    fun suspendUser(reportId: String) {
        _reports.value = _reports.value.map {
            if (it.id == reportId) it.copy(status = ReportStatus.RESOLVED) else it
        }
    }
}
