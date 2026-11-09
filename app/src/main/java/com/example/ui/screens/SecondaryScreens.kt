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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CallRecord
import com.example.model.CallStatus
import com.example.model.CallType
import com.example.model.StatusStory
import com.example.ui.components.NexAvatar
import com.example.ui.components.NexTopBar
import com.example.ui.theme.*
import com.example.viewmodel.NexChatViewModel
import com.example.viewmodel.Screen

// --- STATUS & STORIES SCREEN ---
@Composable
fun StatusScreen(viewModel: NexChatViewModel) {
    val stories by viewModel.repository.statusStories.collectAsState()
    var showNewStatusDialog by remember { mutableStateOf(false) }
    var statusText by remember { mutableStateOf("") }
    var selectedColorHex by remember { mutableStateOf("#005C4B") }
    var viewingStory by remember { mutableStateOf<StatusStory?>(null) }

    Scaffold(
        topBar = {
            NexTopBar(
                title = "Status",
                actions = {
                    IconButton(onClick = { viewModel.navigateTo(Screen.Settings) }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showNewStatusDialog = true },
                containerColor = NexTealPrimary,
                contentColor = Color.White,
                modifier = Modifier
                    .navigationBarsPadding()
                    .testTag("fab_new_status")
            ) {
                Icon(Icons.Default.CameraAlt, contentDescription = "Add status")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // My status row
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showNewStatusDialog = true }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.size(54.dp)) {
                        NexAvatar(name = "My Status", sizeDp = 54)
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(NexTealPrimary)
                                .align(Alignment.BottomEnd),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = "My Status",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Tap to add 24h status update",
                            style = MaterialTheme.typography.bodySmall,
                            color = NexTextMuted
                        )
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                Text(
                    text = "Recent updates",
                    style = MaterialTheme.typography.labelMedium,
                    color = NexTealPrimary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            items(stories, key = { it.id }) { story ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewingStory = story }
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(NexTealPrimary)
                            .padding(2.5.dp)
                    ) {
                        NexAvatar(name = story.userName, sizeDp = 47)
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = story.userName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = story.timestampText,
                            style = MaterialTheme.typography.bodySmall,
                            color = NexTextMuted
                        )
                    }
                }
            }
        }
    }

    // Story Viewer Modal
    if (viewingStory != null) {
        val st = viewingStory!!
        AlertDialog(
            onDismissRequest = { viewingStory = null },
            confirmButton = {
                TextButton(onClick = { viewingStory = null }) {
                    Text("Close", color = Color.White)
                }
            },
            title = {
                Text(st.userName, color = Color.White, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(android.graphics.Color.parseColor(st.backgroundColorHex)))
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = st.caption,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "🔒 24h disappearing status • ${st.viewsCount} views",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 11.sp
                    )
                }
            },
            containerColor = Color(0xFF111B21)
        )
    }

    // Publish New Status Dialog
    if (showNewStatusDialog) {
        AlertDialog(
            onDismissRequest = { showNewStatusDialog = false },
            title = { Text("Publish Status") },
            text = {
                Column {
                    OutlinedTextField(
                        value = statusText,
                        onValueChange = { statusText = it },
                        placeholder = { Text("What's on your mind? (Expires in 24h)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Background Color", style = MaterialTheme.typography.labelSmall)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        listOf("#005C4B", "#1F2C34", "#4338CA", "#BE185D", "#047857").forEach { hex ->
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(android.graphics.Color.parseColor(hex)))
                                    .clickable { selectedColorHex = hex }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (statusText.isNotBlank()) {
                            viewModel.repository.publishStatus(statusText, selectedColorHex)
                            statusText = ""
                            showNewStatusDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NexTealPrimary)
                ) {
                    Text("Publish")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNewStatusDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

// --- CALLS HISTORY SCREEN ---
@Composable
fun CallsScreen(viewModel: NexChatViewModel) {
    val callRecords by viewModel.repository.callRecords.collectAsState()

    Scaffold(
        topBar = {
            NexTopBar(
                title = "Calls",
                actions = {
                    IconButton(onClick = { /* Start group call */ }) {
                        Icon(Icons.Default.Call, contentDescription = "New Call", tint = NexTealPrimary)
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            items(callRecords, key = { it.id }) { call ->
                CallHistoryRow(
                    record = call,
                    onCallBack = {
                        viewModel.startCall(call.contactName, call.callType)
                    }
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

@Composable
fun CallHistoryRow(
    record: CallRecord,
    onCallBack: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onCallBack)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        NexAvatar(name = record.contactName, sizeDp = 48)
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = record.contactName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = if (record.status == CallStatus.MISSED) NexCallDecline else MaterialTheme.colorScheme.onSurface
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (record.isIncoming) Icons.Default.CallReceived else Icons.Default.CallMade,
                    contentDescription = null,
                    tint = if (record.status == CallStatus.MISSED) NexCallDecline else NexStatusOnline,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${record.timestamp} • ${record.durationFormatted}",
                    style = MaterialTheme.typography.bodySmall,
                    color = NexTextMuted
                )
            }
        }
        IconButton(onClick = onCallBack) {
            Icon(
                imageVector = if (record.callType == CallType.VIDEO) Icons.Default.Videocam else Icons.Default.Call,
                contentDescription = "Call back",
                tint = NexTealPrimary
            )
        }
    }
}

// --- COMMUNITIES SCREEN ---
@Composable
fun CommunitiesScreen(viewModel: NexChatViewModel) {
    val communities by viewModel.repository.communities.collectAsState()

    Scaffold(
        topBar = {
            NexTopBar(title = "Communities")
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // New community banner
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(NexDarkSurfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Groups, contentDescription = "Community", tint = NexTealPrimary)
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Text(
                        text = "New Community",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            }

            items(communities, key = { it.id }) { comm ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(NexTealPrimary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Public, contentDescription = null, tint = NexTealPrimary)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = comm.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${comm.memberCount} members",
                                style = MaterialTheme.typography.bodySmall,
                                color = NexTextMuted
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = comm.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = NexTextSecondaryDark
                    )

                    // Channels list
                    if (comm.channels.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        comm.channels.forEach { chan ->
                            Surface(
                                color = NexDarkSurfaceVariant,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable {
                                        viewModel.navigateTo(Screen.ChatDetail("conv_2"))
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Tag, contentDescription = null, tint = NexCyanAccent, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = chan.title,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            }
        }
    }
}
