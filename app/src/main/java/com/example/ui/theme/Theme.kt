package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = NexTealPrimary,
    onPrimary = Color(0xFF00382B),
    primaryContainer = Color(0xFF00513F),
    onPrimaryContainer = Color(0xFF73F8D4),
    secondary = NexCyanAccent,
    onSecondary = Color(0xFF00363D),
    secondaryContainer = Color(0xFF004F58),
    onSecondaryContainer = Color(0xFF97F0FF),
    tertiary = NexTealSecondary,
    onTertiary = Color(0xFF00391A),
    background = NexDarkBackground,
    onBackground = NexTextPrimaryDark,
    surface = NexDarkSurface,
    onSurface = NexTextPrimaryDark,
    surfaceVariant = NexDarkSurfaceVariant,
    onSurfaceVariant = NexTextSecondaryDark,
    outline = NexDivider,
    error = NexCallDecline,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = NexLightHeader,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF73F8D4),
    onPrimaryContainer = Color(0xFF002018),
    secondary = NexTealSecondary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD9FDD3),
    onSecondaryContainer = Color(0xFF00210B),
    tertiary = NexCyanAccent,
    onTertiary = Color.Black,
    background = NexLightBackground,
    onBackground = NexTextPrimaryLight,
    surface = NexLightSurface,
    onSurface = NexTextPrimaryLight,
    surfaceVariant = NexLightSurfaceVariant,
    onSurfaceVariant = NexTextSecondaryLight,
    outline = Color(0xFFE2E8F0),
    error = NexCallDecline,
    onError = Color.White
)

@Composable
fun NexChatTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep branded NEXCHAT aesthetic
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
