package com.example.model

enum class UserStatus {
    ONLINE, OFFLINE, AWAY, BUSY
}

enum class ConversationType {
    DIRECT, GROUP, COMMUNITY, CHANNEL
}

enum class MessageType {
    TEXT, IMAGE, VIDEO, AUDIO, VOICE_NOTE, DOCUMENT, SYSTEM, CALL_EVENT
}

enum class DeliveryStatus {
    SENT, DELIVERED, READ
}

enum class CallType {
    AUDIO, VIDEO
}

enum class CallStatus {
    INITIATING, RINGING, ONGOING, ENDED, MISSED, DECLINED
}

enum class MemberRole {
    OWNER, ADMIN, MODERATOR, MEMBER
}

enum class ReportStatus {
    PENDING, INVESTIGATING, RESOLVED, DISMISSED
}

enum class AppLanguage(val code: String, val title: String, val isRtl: Boolean) {
    ENGLISH("en", "English", false),
    ARABIC("ar", "العربية", true),
    FRENCH("fr", "Français", false)
}

data class UserProfile(
    val id: String,
    val username: String,
    val displayName: String,
    val phoneNumber: String,
    val email: String,
    val bio: String = "Hey there! I am using NEXCHAT.",
    val avatarUrl: String? = null,
    val status: UserStatus = UserStatus.OFFLINE,
    val lastSeenText: String = "Just now",
    val isAdmin: Boolean = false,
    val isSuspended: Boolean = false,
    val isVerified: Boolean = false,
    val privacyLastSeen: String = "everyone",
    val privacyAvatar: String = "everyone",
    val privacyStatus: String = "everyone",
    val privacyReadReceipts: Boolean = true
)

data class Attachment(
    val id: String,
    val fileName: String,
    val fileType: String,
    val fileSizeBytes: Long,
    val durationSeconds: Int? = null,
    val url: String,
    val isEncrypted: Boolean = true
)

data class Message(
    val id: String,
    val conversationId: String,
    val senderId: String,
    val senderName: String,
    val type: MessageType = MessageType.TEXT,
    val content: String,
    val timestamp: String,
    val deliveryStatus: DeliveryStatus = DeliveryStatus.SENT,
    val isEncrypted: Boolean = true,
    val replyToMessageId: String? = null,
    val replyToSnippet: String? = null,
    val isForwarded: Boolean = false,
    val isEdited: Boolean = false,
    val isDeleted: Boolean = false,
    val attachment: Attachment? = null,
    val reactions: Map<String, Int> = emptyMap(), // emoji -> count
    val userReaction: String? = null,
    val disappearingSeconds: Int = 0
)

data class Conversation(
    val id: String,
    val type: ConversationType,
    val title: String,
    val subtitle: String? = null,
    val avatarUrl: String? = null,
    val lastMessage: Message? = null,
    val unreadCount: Int = 0,
    val isPinned: Boolean = false,
    val isMuted: Boolean = false,
    val isArchived: Boolean = false,
    val otherUserId: String? = null,
    val otherUserStatus: UserStatus = UserStatus.OFFLINE,
    val membersCount: Int = 2,
    val disappearingMessagesSeconds: Int = 0,
    val isEncrypted: Boolean = true
)

data class CallRecord(
    val id: String,
    val contactName: String,
    val contactAvatar: String?,
    val callType: CallType,
    val isIncoming: Boolean,
    val status: CallStatus,
    val timestamp: String,
    val durationFormatted: String = ""
)

data class StatusStory(
    val id: String,
    val userId: String,
    val userName: String,
    val userAvatar: String?,
    val caption: String,
    val timestampText: String,
    val isSeen: Boolean = false,
    val backgroundColorHex: String = "#0C1317",
    val viewsCount: Int = 0
)

data class Community(
    val id: String,
    val name: String,
    val description: String,
    val avatarUrl: String?,
    val memberCount: Int,
    val channels: List<Conversation> = emptyList()
)

data class UserReport(
    val id: String,
    val reporterName: String,
    val reportedUserName: String,
    val reason: String,
    val status: ReportStatus,
    val date: String
)
