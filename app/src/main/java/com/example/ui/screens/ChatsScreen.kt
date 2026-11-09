package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Conversation
import com.example.model.ConversationType
import com.example.ui.components.DeliveryReceiptIcon
import com.example.ui.components.NexAvatar
import com.example.ui.components.NexTopBar
import com.example.ui.theme.*
import com.example.viewmodel.NexChatViewModel
import com.example.viewmodel.Screen

@Composable
fun ChatsScreen(viewModel: NexChatViewModel) {
    val conversations by viewModel.repository.conversations.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    var isSearchActive by remember { mutableStateOf(false) }

    val filteredList = remember(conversations, searchQuery) {
        if (searchQuery.isBlank()) conversations
        else conversations.filter {
            it.title.contains(searchQuery, ignoreCase = true) ||
            (it.subtitle?.contains(searchQuery, ignoreCase = true) == true)
        }
    }

    Scaffold(
        topBar = {
            if (isSearchActive) {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                isSearchActive = false
                                viewModel.onSearchQueryChanged("")
                            },
                            modifier = Modifier.testTag("close_search_button")
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Close search")
                        }
                        TextField(
                            value = searchQuery,
                            onValueChange = { viewModel.onSearchQueryChanged(it) },
                            placeholder = { Text("Search chats and messages...") },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("search_text_field"),
                            singleLine = true
                        )
                    }
                }
            } else {
                NexTopBar(
                    title = "NEXCHAT",
                    actions = {
                        IconButton(
                            onClick = { isSearchActive = true },
                            modifier = Modifier.testTag("action_search")
                        ) {
                            Icon(Icons.Default.Search, contentDescription = "Search", tint = NexTealPrimary)
                        }
                        IconButton(
                            onClick = { viewModel.navigateTo(Screen.SecurityInfo) },
                            modifier = Modifier.testTag("action_security")
                        ) {
                            Icon(Icons.Default.Shield, contentDescription = "Security", tint = NexCyanAccent)
                        }
                        IconButton(
                            onClick = { viewModel.navigateTo(Screen.AdminPanel) },
                            modifier = Modifier.testTag("action_admin")
                        ) {
                            Icon(Icons.Default.AdminPanelSettings, contentDescription = "Admin", tint = NexAdminBadge)
                        }
                    }
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    // Quick new chat shortcut to Sarah
                    viewModel.navigateTo(Screen.ChatDetail("conv_1"))
                },
                containerColor = NexTealPrimary,
                contentColor = Color.White,
                modifier = Modifier
                    .navigationBarsPadding()
                    .testTag("fab_new_chat")
            ) {
                Icon(Icons.Default.Chat, contentDescription = "New chat")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            val supabaseStatus by viewModel.repository.supabaseStatus.collectAsState()

            // Supabase Live Backend & E2EE Verified Banner
            Surface(
                color = NexDarkBubbleOutgoing.copy(alpha = 0.35f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clickable { viewModel.navigateTo(Screen.SecurityInfo) }
            ) {
                Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (supabaseStatus.isConnected) NexStatusOnline else NexCallDecline)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Supabase Live: ${supabaseStatus.endpoint.substringAfter("https://").take(22)}...",
                            style = MaterialTheme.typography.labelSmall,
                            color = NexCyanAccent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Lock,
                            contentDescription = "Encrypted",
                            tint = NexTealSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "AES-256-GCM End-to-End Encrypted. Tap to verify keys.",
                            style = MaterialTheme.typography.bodySmall,
                            color = NexTealSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(filteredList, key = { it.id }) { conv ->
                    ConversationItemRow(
                        conversation = conv,
                        onClick = { viewModel.navigateTo(Screen.ChatDetail(conv.id)) },
                        onPin = { viewModel.repository.pinConversation(conv.id) },
                        onArchive = { viewModel.repository.archiveConversation(conv.id) }
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(start = 76.dp),
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                    )
                }
            }
        }
    }
}

@Composable
fun ConversationItemRow(
    conversation: Conversation,
    onClick: () -> Unit,
    onPin: () -> Unit,
    onArchive: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag("conversation_row_${conversation.id}"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        NexAvatar(
            name = conversation.title,
            status = conversation.otherUserStatus,
            sizeDp = 50
        )

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = conversation.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (conversation.type == ConversationType.GROUP) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            Icons.Default.Groups,
                            contentDescription = "Group",
                            tint = NexTextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Text(
                    text = conversation.lastMessage?.timestamp ?: "10:36 AM",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (conversation.unreadCount > 0) NexTealPrimary else NexTextMuted,
                    fontSize = 12.sp,
                    fontWeight = if (conversation.unreadCount > 0) FontWeight.Bold else FontWeight.Normal
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (conversation.lastMessage?.senderId == "user_me_001") {
                        DeliveryReceiptIcon(
                            status = conversation.lastMessage.deliveryStatus,
                            modifier = Modifier.padding(end = 4.dp)
                        )
                    }
                    Text(
                        text = conversation.subtitle ?: "No messages yet",
                        style = MaterialTheme.typography.bodyMedium,
                        color = NexTextSecondaryDark,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 13.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (conversation.isPinned) {
                        Icon(
                            Icons.Default.PushPin,
                            contentDescription = "Pinned",
                            tint = NexTextMuted,
                            modifier = Modifier
                                .size(16.dp)
                                .padding(end = 6.dp)
                        )
                    }

                    if (conversation.unreadCount > 0) {
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(NexTealPrimary)
                                .padding(horizontal = 7.dp, vertical = 2.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = conversation.unreadCount.toString(),
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
