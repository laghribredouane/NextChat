package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.model.AppLanguage
import com.example.ui.screens.*
import com.example.ui.theme.NexChatTheme
import com.example.ui.theme.NexTealPrimary
import com.example.viewmodel.NexChatViewModel
import com.example.viewmodel.Screen

class MainActivity : ComponentActivity() {

    private val viewModel: NexChatViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val currentLanguage by viewModel.repository.currentLanguage.collectAsState()
            val layoutDirection = if (currentLanguage == AppLanguage.ARABIC) {
                LayoutDirection.Rtl
            } else {
                LayoutDirection.Ltr
            }

            CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                NexChatTheme(darkTheme = true) {
                    val currentScreen by viewModel.currentScreen.collectAsState()

                    // Handle Android system back gesture/button
                    BackHandler(enabled = currentScreen !is Screen.ChatsList) {
                        viewModel.navigateBack()
                    }

                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        bottomBar = {
                            // Show standard M3 Bottom Navigation only on primary tabs
                            if (currentScreen is Screen.ChatsList ||
                                currentScreen is Screen.Communities ||
                                currentScreen is Screen.Status ||
                                currentScreen is Screen.Calls ||
                                currentScreen is Screen.Settings
                            ) {
                                NavigationBar(
                                    modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars),
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    tonalElevation = 8.dp
                                ) {
                                    NavigationBarItem(
                                        selected = currentScreen is Screen.ChatsList,
                                        onClick = { viewModel.navigateTo(Screen.ChatsList) },
                                        icon = {
                                            Icon(
                                                if (currentScreen is Screen.ChatsList) Icons.Filled.Chat else Icons.Outlined.Chat,
                                                contentDescription = "Chats"
                                            )
                                        },
                                        label = { Text("Chats") },
                                        modifier = Modifier.testTag("nav_chats"),
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = Color.White,
                                            selectedTextColor = NexTealPrimary,
                                            indicatorColor = NexTealPrimary
                                        )
                                    )

                                    NavigationBarItem(
                                        selected = currentScreen is Screen.Communities,
                                        onClick = { viewModel.navigateTo(Screen.Communities) },
                                        icon = {
                                            Icon(
                                                if (currentScreen is Screen.Communities) Icons.Filled.Groups else Icons.Outlined.Groups,
                                                contentDescription = "Communities"
                                            )
                                        },
                                        label = { Text("Communities") },
                                        modifier = Modifier.testTag("nav_communities"),
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = Color.White,
                                            selectedTextColor = NexTealPrimary,
                                            indicatorColor = NexTealPrimary
                                        )
                                    )

                                    NavigationBarItem(
                                        selected = currentScreen is Screen.Status,
                                        onClick = { viewModel.navigateTo(Screen.Status) },
                                        icon = {
                                            Icon(
                                                if (currentScreen is Screen.Status) Icons.Filled.DonutLarge else Icons.Outlined.DonutLarge,
                                                contentDescription = "Status"
                                            )
                                        },
                                        label = { Text("Status") },
                                        modifier = Modifier.testTag("nav_status"),
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = Color.White,
                                            selectedTextColor = NexTealPrimary,
                                            indicatorColor = NexTealPrimary
                                        )
                                    )

                                    NavigationBarItem(
                                        selected = currentScreen is Screen.Calls,
                                        onClick = { viewModel.navigateTo(Screen.Calls) },
                                        icon = {
                                            Icon(
                                                if (currentScreen is Screen.Calls) Icons.Filled.Call else Icons.Outlined.Call,
                                                contentDescription = "Calls"
                                            )
                                        },
                                        label = { Text("Calls") },
                                        modifier = Modifier.testTag("nav_calls"),
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = Color.White,
                                            selectedTextColor = NexTealPrimary,
                                            indicatorColor = NexTealPrimary
                                        )
                                    )

                                    NavigationBarItem(
                                        selected = currentScreen is Screen.Settings,
                                        onClick = { viewModel.navigateTo(Screen.Settings) },
                                        icon = {
                                            Icon(
                                                if (currentScreen is Screen.Settings) Icons.Filled.Settings else Icons.Outlined.Settings,
                                                contentDescription = "Settings"
                                            )
                                        },
                                        label = { Text("Settings") },
                                        modifier = Modifier.testTag("nav_settings"),
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = Color.White,
                                            selectedTextColor = NexTealPrimary,
                                            indicatorColor = NexTealPrimary
                                        )
                                    )
                                }
                            }
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            when (val screen = currentScreen) {
                                is Screen.ChatsList -> ChatsScreen(viewModel)
                                is Screen.ChatDetail -> ChatDetailScreen(screen.conversationId, viewModel)
                                is Screen.Communities -> CommunitiesScreen(viewModel)
                                is Screen.Status -> StatusScreen(viewModel)
                                is Screen.Calls -> CallsScreen(viewModel)
                                is Screen.ActiveCall -> ActiveCallScreen(viewModel)
                                is Screen.Settings -> SettingsScreen(viewModel)
                                is Screen.AdminPanel -> AdminPanelScreen(viewModel)
                                is Screen.SecurityInfo -> SecurityInfoScreen(viewModel)
                            }
                        }
                    }
                }
            }
        }
    }
}
