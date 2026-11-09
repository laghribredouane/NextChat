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
import com.example.model.AppLanguage
import com.example.model.ReportStatus
import com.example.security.CryptoEngine
import com.example.ui.components.NexAvatar
import com.example.ui.components.NexTopBar
import com.example.ui.theme.*
import com.example.viewmodel.NexChatViewModel
import com.example.viewmodel.Screen

// --- SETTINGS SCREEN ---
@Composable
fun SettingsScreen(viewModel: NexChatViewModel) {
    val currentUser by viewModel.repository.currentUser.collectAsState()
    val currentLang by viewModel.repository.currentLanguage.collectAsState()

    var showEditProfileDialog by remember { mutableStateOf(false) }
    var editName by remember { mutableStateOf(currentUser.displayName) }
    var editBio by remember { mutableStateOf(currentUser.bio) }
    var editPhone by remember { mutableStateOf(currentUser.phoneNumber) }
    var showLanguageDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            NexTopBar(
                title = "Settings",
                navigationIcon = Icons.Default.ArrowBack,
                onNavClick = { viewModel.navigateBack() }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Profile Card Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            editName = currentUser.displayName
                            editBio = currentUser.bio
                            editPhone = currentUser.phoneNumber
                            showEditProfileDialog = true
                        }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NexAvatar(name = currentUser.displayName, sizeDp = 64)
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = currentUser.displayName,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (currentUser.isVerified) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(Icons.Default.Verified, contentDescription = "Verified", tint = NexTealPrimary, modifier = Modifier.size(18.dp))
                            }
                        }
                        Text(
                            text = currentUser.bio,
                            style = MaterialTheme.typography.bodyMedium,
                            color = NexTextMuted,
                            maxLines = 1
                        )
                        Text(
                            text = currentUser.phoneNumber,
                            style = MaterialTheme.typography.bodySmall,
                            color = NexCyanAccent
                        )
                    }
                    Icon(Icons.Default.QrCode, contentDescription = "QR Code", tint = NexTealPrimary)
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            }

            // Settings Options List
            item {
                SettingsItem(
                    icon = Icons.Default.Shield,
                    title = "End-to-End Encryption & Security",
                    subtitle = "Verify safety numbers, session prekeys, 2FA",
                    tint = NexCyanAccent,
                    onClick = { viewModel.navigateTo(Screen.SecurityInfo) }
                )
                SettingsItem(
                    icon = Icons.Default.Lock,
                    title = "Privacy",
                    subtitle = "Last seen, profile photo, disappearing messages",
                    tint = NexTealPrimary,
                    onClick = { }
                )
                SettingsItem(
                    icon = Icons.Default.Language,
                    title = "App Language",
                    subtitle = "${currentLang.title} (${currentLang.code})",
                    tint = NexEmeraldAccent,
                    onClick = { showLanguageDialog = true }
                )
                SettingsItem(
                    icon = Icons.Default.AdminPanelSettings,
                    title = "Administration Dashboard",
                    subtitle = "Telemetry, active sessions, moderation & reports",
                    tint = NexAdminBadge,
                    onClick = { viewModel.navigateTo(Screen.AdminPanel) }
                )
                val supabaseStatus by viewModel.repository.supabaseStatus.collectAsState()
                SettingsItem(
                    icon = Icons.Default.CloudQueue,
                    title = "Cloud Backend & Supabase",
                    subtitle = "Connected to refaejedfgunfioxhdzu • Tap to test",
                    tint = NexCyanAccent,
                    onClick = { viewModel.repository.checkSupabaseHealth() }
                )
                SettingsItem(
                    icon = Icons.Default.Notifications,
                    title = "Notifications",
                    subtitle = "Message, group & call tones",
                    tint = Color(0xFFEAB308),
                    onClick = { }
                )
                SettingsItem(
                    icon = Icons.Default.Storage,
                    title = "Storage & Data",
                    subtitle = "Network usage, auto-download",
                    tint = Color(0xFF3B82F6),
                    onClick = { }
                )
            }
        }
    }

    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = { Text("Select Language") },
            text = {
                Column {
                    AppLanguage.entries.forEach { lang ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.repository.setLanguage(lang)
                                    showLanguageDialog = false
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (currentLang == lang),
                                onClick = {
                                    viewModel.repository.setLanguage(lang)
                                    showLanguageDialog = false
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "${lang.title} (${lang.code.uppercase()})", style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            },
            confirmButton = {}
        )
    }

    if (showEditProfileDialog) {
        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            title = { Text("Edit Profile") },
            text = {
                Column {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Display Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editBio,
                        onValueChange = { editBio = it },
                        label = { Text("About / Bio") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editPhone,
                        onValueChange = { editPhone = it },
                        label = { Text("Phone Number") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.repository.updateProfile(editName, editBio, editPhone)
                        showEditProfileDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NexTealPrimary)
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun SettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    tint: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = title, tint = tint, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = NexTextMuted)
        }
    }
}

// --- E2EE SECURITY & VERIFICATION SCREEN ---
@Composable
fun SecurityInfoScreen(viewModel: NexChatViewModel) {
    val fingerprint = remember { CryptoEngine.generateSecurityFingerprint("user_me_001", "user_sarah") }

    Scaffold(
        topBar = {
            NexTopBar(
                title = "Security & Encryption",
                navigationIcon = Icons.Default.ArrowBack,
                onNavClick = { viewModel.navigateBack() }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(NexTealPrimary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Shield, contentDescription = null, tint = NexTealPrimary, modifier = Modifier.size(48.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "AES-256-GCM End-to-End Encryption",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Every message, voice note, photo, document, and WebRTC audio/video call on NEXCHAT is encrypted before leaving your device. Only you and the recipient hold the session keys.",
                style = MaterialTheme.typography.bodyMedium,
                color = NexTextSecondaryDark,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Safety Number Fingerprint verification
            Surface(
                color = NexDarkSurfaceVariant,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Safety Number Fingerprint",
                        style = MaterialTheme.typography.titleMedium,
                        color = NexCyanAccent,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = fingerprint,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Compare this safety number with Sarah Connor's device to confirm that your end-to-end encryption is securely verified.",
                        style = MaterialTheme.typography.bodySmall,
                        color = NexTextMuted,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { viewModel.navigateBack() },
                colors = ButtonDefaults.buttonColors(containerColor = NexTealPrimary),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Mark as Verified")
            }
        }
    }
}

// --- ADMINISTRATION DASHBOARD SCREEN ---
@Composable
fun AdminPanelScreen(viewModel: NexChatViewModel) {
    val reports by viewModel.repository.reports.collectAsState()

    Scaffold(
        topBar = {
            NexTopBar(
                title = "Admin Telemetry & Moderation",
                navigationIcon = Icons.Default.ArrowBack,
                onNavClick = { viewModel.navigateBack() }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp)
        ) {
            // Metrics Overview Cards
            item {
                Text(
                    text = "System Health & Platform Metrics",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricCard(title = "Total Users", value = "124,580", change = "+18% this wk", modifier = Modifier.weight(1f))
                    MetricCard(title = "Active Calls", value = "1,492", change = "WebRTC Mesh", modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricCard(title = "Messages/day", value = "4.2M", change = "Encrypted E2EE", modifier = Modifier.weight(1f))
                    MetricCard(title = "RLS Audits", value = "100%", change = "Zero violations", modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Zero knowledge notice
                Surface(
                    color = NexDarkSurfaceVariant,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = NexTealPrimary)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Zero-Knowledge Principle: Administrators can view system metrics and user reports, but cannot inspect encrypted private messages.",
                            style = MaterialTheme.typography.bodySmall,
                            color = NexTextSecondaryDark,
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Abuse Reports & Moderation Queue",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            items(reports, key = { it.id }) { rep ->
                Surface(
                    color = NexDarkSurfaceVariant,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Reported: ${rep.reportedUserName}",
                                fontWeight = FontWeight.Bold,
                                color = NexCallDecline
                            )
                            Text(
                                text = rep.date,
                                style = MaterialTheme.typography.bodySmall,
                                color = NexTextMuted
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "By ${rep.reporterName}: \"${rep.reason}\"",
                            style = MaterialTheme.typography.bodySmall,
                            color = NexTextPrimaryDark
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            if (rep.status == ReportStatus.PENDING) {
                                Button(
                                    onClick = { viewModel.repository.suspendUser(rep.id) },
                                    colors = ButtonDefaults.buttonColors(containerColor = NexCallDecline),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text("Suspend & Dismiss", fontSize = 12.sp)
                                }
                            } else {
                                Text(
                                    text = "Resolved",
                                    color = NexTealPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    change: String,
    modifier: Modifier = Modifier
) {
    Surface(
        color = NexDarkSurfaceVariant,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(text = title, style = MaterialTheme.typography.bodySmall, color = NexTextMuted)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = change, style = MaterialTheme.typography.labelSmall, color = NexCyanAccent)
        }
    }
}
