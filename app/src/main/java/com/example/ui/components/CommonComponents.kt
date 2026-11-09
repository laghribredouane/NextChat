package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DeliveryStatus
import com.example.model.UserStatus
import com.example.ui.theme.*

@Composable
fun NexAvatar(
    name: String,
    status: UserStatus = UserStatus.OFFLINE,
    sizeDp: Int = 48,
    showStatusDot: Boolean = true,
    modifier: Modifier = Modifier
) {
    // Generate harmonious gradient based on name hash
    val hash = name.hashCode()
    val gradients = listOf(
        listOf(Color(0xFF00A884), Color(0xFF005C4B)),
        listOf(Color(0xFF00E5FF), Color(0xFF00513F)),
        listOf(Color(0xFF6366F1), Color(0xFF4338CA)),
        listOf(Color(0xFFEC4899), Color(0xFFBE185D)),
        listOf(Color(0xFFF59E0B), Color(0xFFB45309)),
        listOf(Color(0xFF10B981), Color(0xFF047857))
    )
    val colorPair = gradients[kotlin.math.abs(hash) % gradients.size]

    val initials = name.split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .map { it.first().uppercase() }
        .joinToString("")
        .ifEmpty { "N" }

    Box(
        modifier = modifier.size(sizeDp.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .background(Brush.linearGradient(colorPair)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initials,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = (sizeDp * 0.4).sp
            )
        }

        if (showStatusDot && status == UserStatus.ONLINE) {
            Box(
                modifier = Modifier
                    .size((sizeDp * 0.3).dp.coerceAtLeast(12.dp))
                    .clip(CircleShape)
                    .background(Color(0xFF0C1317))
                    .padding(2.dp)
                    .clip(CircleShape)
                    .background(NexStatusOnline)
                    .align(Alignment.BottomEnd)
            )
        }
    }
}

@Composable
fun DeliveryReceiptIcon(status: DeliveryStatus, modifier: Modifier = Modifier) {
    when (status) {
        DeliveryStatus.SENT -> {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Sent",
                tint = NexTextMuted,
                modifier = modifier.size(14.dp)
            )
        }
        DeliveryStatus.DELIVERED -> {
            Icon(
                imageVector = Icons.Default.DoneAll,
                contentDescription = "Delivered",
                tint = NexTextMuted,
                modifier = modifier.size(16.dp)
            )
        }
        DeliveryStatus.READ -> {
            Icon(
                imageVector = Icons.Default.DoneAll,
                contentDescription = "Read",
                tint = NexReceiptRead,
                modifier = modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun VoiceWaveformVisualizer(
    isPlaying: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "wave")
    val heights = (0..14).map { i ->
        val anim by infiniteTransition.animateFloat(
            initialValue = 6f,
            targetValue = if (isPlaying) (8 + (i * 7) % 24).toFloat() else 8f,
            animationSpec = infiniteRepeatable(
                animation = tween(400 + (i * 60), easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "bar_$i"
        )
        anim
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        heights.forEach { h ->
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(h.dp)
                    .clip(RoundedCornerShape(1.5.dp))
                    .background(NexTealPrimary)
            )
        }
    }
}

@Composable
fun NexTopBar(
    title: String,
    subtitle: String? = null,
    navigationIcon: ImageVector? = null,
    onNavClick: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (navigationIcon != null && onNavClick != null) {
                IconButton(
                    onClick = onNavClick,
                    modifier = Modifier.testTag("top_bar_nav_back")
                ) {
                    Icon(
                        imageVector = navigationIcon,
                        contentDescription = "Navigate back",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            } else {
                Spacer(modifier = Modifier.width(8.dp))
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = NexTealPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                actions()
            }
        }
    }
}
