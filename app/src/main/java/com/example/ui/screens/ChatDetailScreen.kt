package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.components.DeliveryReceiptIcon
import com.example.ui.components.NexAvatar
import com.example.ui.components.VoiceWaveformVisualizer
import com.example.ui.theme.*
import com.example.viewmodel.NexChatViewModel

@Composable
fun ChatDetailScreen(
    conversationId: String,
    viewModel: NexChatViewModel
) {
    val conversations by viewModel.repository.conversations.collectAsState()
    val conv = conversations.find { it.id == conversationId } ?: return
    val messagesMap by viewModel.repository.messagesMap.collectAsState()
    val messages = messagesMap[conversationId] ?: emptyList()

    var inputText by remember { mutableStateOf("") }
    var replyingTo by remember { mutableStateOf<Message?>(null) }
    var activeVoicePlayingId by remember { mutableStateOf<String?>(null) }
    var showAttachmentMenu by remember { mutableStateOf(false) }

    val isRecordingVoice by viewModel.isRecordingVoice.collectAsState()
    val recordingDurationSec by viewModel.recordingDurationSec.collectAsState()

    Scaffold(
        topBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 3.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 4.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { viewModel.navigateBack() },
                        modifier = Modifier.testTag("chat_back_button")
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }

                    NexAvatar(
                        name = conv.title,
                        status = conv.otherUserStatus,
                        sizeDp = 40
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = conv.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1
                        )
                        Text(
                            text = if (conv.otherUserStatus == UserStatus.ONLINE) "Online • E2EE" else "End-to-End Encrypted",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (conv.otherUserStatus == UserStatus.ONLINE) NexStatusOnline else NexTextSecondaryDark,
                            fontSize = 11.sp
                        )
                    }

                    // Call buttons (Audio and Video WebRTC)
                    IconButton(
                        onClick = { viewModel.startCall(conv.title, CallType.AUDIO) },
                        modifier = Modifier.testTag("chat_audio_call_btn")
                    ) {
                        Icon(Icons.Default.Call, contentDescription = "Audio Call", tint = NexTealPrimary)
                    }

                    IconButton(
                        onClick = { viewModel.startCall(conv.title, CallType.VIDEO) },
                        modifier = Modifier.testTag("chat_video_call_btn")
                    ) {
                        Icon(Icons.Default.Videocam, contentDescription = "Video Call", tint = NexCyanAccent)
                    }
                }
            }
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                // Reply banner if active
                AnimatedVisibility(visible = replyingTo != null) {
                    replyingTo?.let { rep ->
                        Surface(
                            color = NexDarkSurfaceVariant,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .width(3.dp)
                                        .height(32.dp)
                                        .background(NexTealPrimary)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = rep.senderName,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = NexTealPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = rep.content,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = NexTextSecondaryDark,
                                        maxLines = 1
                                    )
                                }
                                IconButton(onClick = { replyingTo = null }) {
                                    Icon(Icons.Default.Close, contentDescription = "Cancel reply", tint = NexTextMuted)
                                }
                            }
                        }
                    }
                }

                // Attachment Drawer
                AnimatedVisibility(visible = showAttachmentMenu) {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            AttachmentTypeItem(Icons.Default.Description, "Document", Color(0xFF5B51D8)) {
                                showAttachmentMenu = false
                                viewModel.repository.sendMessage(conversationId, "📎 Project_Architecture_v2.pdf", MessageType.DOCUMENT)
                            }
                            AttachmentTypeItem(Icons.Default.CameraAlt, "Camera", Color(0xFFD32F2F)) {
                                showAttachmentMenu = false
                                viewModel.repository.sendMessage(conversationId, "📷 Photo captured", MessageType.IMAGE)
                            }
                            AttachmentTypeItem(Icons.Default.Image, "Gallery", Color(0xFFAC44CF)) {
                                showAttachmentMenu = false
                                viewModel.repository.sendMessage(conversationId, "🖼️ Image from gallery", MessageType.IMAGE)
                            }
                            AttachmentTypeItem(Icons.Default.Headphones, "Audio", Color(0xFFE05638)) {
                                showAttachmentMenu = false
                                viewModel.repository.sendMessage(conversationId, "🎵 Audio sample", MessageType.AUDIO)
                            }
                        }
                    }
                }

                // Input Bar / Voice Recorder
                if (isRecordingVoice) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Mic,
                            contentDescription = "Recording",
                            tint = NexCallDecline,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Recording: ${recordingDurationSec}s",
                            color = NexCallDecline,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        TextButton(onClick = { viewModel.cancelVoiceRecording() }) {
                            Text("Cancel", color = NexTextMuted)
                        }
                        IconButton(
                            onClick = { viewModel.finishVoiceRecording(conversationId) },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(NexTealPrimary)
                                .testTag("btn_finish_voice_recording")
                        ) {
                            Icon(Icons.Default.Send, contentDescription = "Send Voice Note", tint = Color.White)
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { showAttachmentMenu = !showAttachmentMenu },
                            modifier = Modifier.testTag("btn_chat_attach")
                        ) {
                            Icon(Icons.Default.AttachFile, contentDescription = "Attach", tint = NexTealPrimary)
                        }

                        TextField(
                            value = inputText,
                            onValueChange = { inputText = it },
                            placeholder = { Text("Message", color = NexTextMuted) },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("chat_input_text_field"),
                            maxLines = 4
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        if (inputText.isNotBlank()) {
                            IconButton(
                                onClick = {
                                    viewModel.sendMessage(conversationId, inputText, replyingTo)
                                    inputText = ""
                                    replyingTo = null
                                },
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(NexTealPrimary)
                                    .testTag("chat_send_button")
                            ) {
                                Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White)
                            }
                        } else {
                            IconButton(
                                onClick = { viewModel.startVoiceRecording() },
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(NexTealPrimary)
                                    .testTag("chat_mic_button")
                            ) {
                                Icon(Icons.Default.Mic, contentDescription = "Voice note", tint = Color.White)
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
        ) {
            // E2EE System Header Notice
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        color = NexDarkSurfaceVariant.copy(alpha = 0.8f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "🔒 Messages and calls are end-to-end encrypted. No one outside of this chat, not even NEXCHAT, can read or listen to them.",
                            style = MaterialTheme.typography.bodySmall,
                            color = NexTextMuted,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            items(messages, key = { it.id }) { msg ->
                MessageBubble(
                    message = msg,
                    isMe = (msg.senderId == "user_me_001"),
                    isPlayingVoice = (activeVoicePlayingId == msg.id),
                    onToggleVoice = {
                        activeVoicePlayingId = if (activeVoicePlayingId == msg.id) null else msg.id
                    },
                    onReply = { replyingTo = msg },
                    onReact = { emoji ->
                        viewModel.repository.toggleReaction(conversationId, msg.id, emoji)
                    },
                    onDelete = {
                        viewModel.repository.deleteMessage(conversationId, msg.id)
                    }
                )
                Spacer(modifier = Modifier.height(6.dp))
            }
        }
    }
}

@Composable
fun MessageBubble(
    message: Message,
    isMe: Boolean,
    isPlayingVoice: Boolean,
    onToggleVoice: () -> Unit,
    onReply: () -> Unit,
    onReact: (String) -> Unit,
    onDelete: () -> Unit
) {
    var showActionMenu by remember { mutableStateOf(false) }

    val bubbleColor = if (isMe) NexDarkBubbleOutgoing else NexDarkBubbleIncoming
    val align = if (isMe) Alignment.End else Alignment.Start

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = { showActionMenu = true }
                )
            },
        horizontalAlignment = align
    ) {
        Surface(
            color = bubbleColor,
            shape = RoundedCornerShape(
                topStart = 14.dp,
                topEnd = 14.dp,
                bottomStart = if (isMe) 14.dp else 2.dp,
                bottomEnd = if (isMe) 2.dp else 14.dp
            ),
            modifier = Modifier.widthIn(min = 80.dp, max = 300.dp)
        ) {
            Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
                // Reply snippet if present
                if (message.replyToSnippet != null) {
                    Surface(
                        color = Color.Black.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 4.dp)
                    ) {
                        Text(
                            text = message.replyToSnippet,
                            style = MaterialTheme.typography.bodySmall,
                            color = NexCyanAccent,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(6.dp),
                            maxLines = 1
                        )
                    }
                }

                // Voice Note or Text content
                if (message.type == MessageType.VOICE_NOTE) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        IconButton(
                            onClick = onToggleVoice,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(NexTealPrimary)
                        ) {
                            Icon(
                                imageVector = if (isPlayingVoice) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = "Play/Pause voice note",
                                tint = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        VoiceWaveformVisualizer(
                            isPlaying = isPlayingVoice,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${message.attachment?.durationSeconds ?: 15}s",
                            color = NexTextMuted,
                            fontSize = 11.sp
                        )
                    }
                } else {
                    Text(
                        text = message.content,
                        style = MaterialTheme.typography.bodyMedium,
                        color = NexTextPrimaryDark,
                        fontSize = 15.sp
                    )
                }

                // Timestamp and receipt
                Row(
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(top = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = message.timestamp,
                        style = MaterialTheme.typography.labelSmall,
                        color = NexTextMuted,
                        fontSize = 10.sp
                    )
                    if (isMe) {
                        Spacer(modifier = Modifier.width(4.dp))
                        DeliveryReceiptIcon(status = message.deliveryStatus)
                    }
                }
            }
        }

        // Reactions display
        if (message.reactions.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .offset(y = (-6).dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(NexDarkSurfaceVariant)
                    .padding(horizontal = 6.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                message.reactions.forEach { (emoji, count) ->
                    Text(
                        text = "$emoji $count",
                        fontSize = 12.sp,
                        color = Color.White
                    )
                }
            }
        }

        // Long press menu (Emoji reactions, Reply, Delete)
        DropdownMenu(
            expanded = showActionMenu,
            onDismissRequest = { showActionMenu = false }
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("❤️", "👍", "🔥", "😂", "🔒", "👏").forEach { emoji ->
                    Text(
                        text = emoji,
                        fontSize = 20.sp,
                        modifier = Modifier
                            .clickable {
                                onReact(emoji)
                                showActionMenu = false
                            }
                            .padding(4.dp)
                    )
                }
            }
            HorizontalDivider()
            DropdownMenuItem(
                text = { Text("Reply") },
                leadingIcon = { Icon(Icons.Default.Reply, contentDescription = null) },
                onClick = {
                    onReply()
                    showActionMenu = false
                }
            )
            DropdownMenuItem(
                text = { Text("Delete") },
                leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null) },
                onClick = {
                    onDelete()
                    showActionMenu = false
                }
            )
        }
    }
}

@Composable
fun AttachmentTypeItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    tint: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(tint),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = label, tint = Color.White)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = NexTextPrimaryDark)
    }
}
