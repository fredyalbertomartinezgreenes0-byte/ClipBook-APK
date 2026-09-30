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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.entity.UserEntity
import com.example.data.repository.NexaRepository
import com.example.ui.components.formatTime
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun NotificationsScreen(
    repository: NexaRepository,
    currentUser: UserEntity?
) {
    val coroutineScope = rememberCoroutineScope()
    val notifications by repository.getNotifications(currentUser?.id ?: 1L).collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NexaBackground)
            .testTag("notifications_screen")
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Notificaciones",
                color = NexaTextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )

            if (notifications.any { !it.isRead }) {
                TextButton(
                    onClick = {
                        coroutineScope.launch {
                            repository.markAllNotificationsRead(currentUser?.id ?: 1L)
                        }
                    }
                ) {
                    Text("Marcar todas como leídas", color = NexaCyan, fontSize = 12.sp)
                }
            }
        }

        if (notifications.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text("No tienes notificaciones pendientes.", color = NexaTextMuted, fontSize = 14.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 12.dp, end = 12.dp, top = 0.dp, bottom = 80.dp)
            ) {
                items(notifications, key = { it.id }) { notif ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable {
                                coroutineScope.launch {
                                    repository.markNotificationRead(notif.id)
                                }
                            },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (!notif.isRead) NexaSurfaceElevated else NexaSurface
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Icon indicator by type
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when (notif.type) {
                                            "VERIFICATION" -> NexaCyan.copy(alpha = 0.2f)
                                            "REACTION" -> NexaError.copy(alpha = 0.2f)
                                            "FRIEND_REQ" -> NexaPurple.copy(alpha = 0.2f)
                                            else -> NexaBlue.copy(alpha = 0.2f)
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (notif.senderAvatar.isNotBlank()) {
                                    AsyncImage(
                                        model = notif.senderAvatar,
                                        contentDescription = notif.senderName,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize().clip(CircleShape)
                                    )
                                } else {
                                    Icon(
                                        imageVector = when (notif.type) {
                                            "VERIFICATION" -> Icons.Default.Verified
                                            "REACTION" -> Icons.Default.Favorite
                                            "FRIEND_REQ" -> Icons.Default.PersonAdd
                                            else -> Icons.Default.Notifications
                                        },
                                        contentDescription = null,
                                        tint = when (notif.type) {
                                            "VERIFICATION" -> NexaCyan
                                            "REACTION" -> NexaError
                                            "FRIEND_REQ" -> NexaPurpleLight
                                            else -> NexaBlueLight
                                        },
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = notif.title,
                                    color = NexaTextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = notif.message,
                                    color = NexaTextSecondary,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = formatTime(notif.createdAt),
                                    color = NexaTextMuted,
                                    fontSize = 10.sp
                                )
                            }

                            if (!notif.isRead) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(NexaCyan)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
