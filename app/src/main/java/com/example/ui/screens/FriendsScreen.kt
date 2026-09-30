package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
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
import com.example.ui.components.VerifiedBadgeIcon
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun FriendsScreen(
    repository: NexaRepository,
    currentUser: UserEntity?,
    onOpenProfile: (Long) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val allUsers by repository.getAllUsers().collectAsState(initial = emptyList())
    val pendingRequests by repository.getPendingRequests(currentUser?.id ?: 1L).collectAsState(initial = emptyList())
    val friendships by repository.getFriendships(currentUser?.id ?: 1L).collectAsState(initial = emptyList())

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Sugerencias", "Solicitudes (${pendingRequests.size})", "Mis Amigos")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NexaBackground)
            .testTag("friends_screen")
    ) {
        // Tab row
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = NexaSurface,
            contentColor = NexaCyan
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title, fontSize = 13.sp, fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal) }
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp)
        ) {
            when (selectedTab) {
                0 -> {
                    // SUGERENCIAS: "Personas que quizá conozcas"
                    val suggestions = allUsers.filter { it.id != currentUser?.id }
                    if (suggestions.isEmpty()) {
                        item {
                            EmptyStateNotice("No hay sugerencias en este momento.")
                        }
                    } else {
                        items(suggestions, key = { it.id }) { user ->
                            val isFriend = friendships.any {
                                (it.senderId == user.id || it.receiverId == user.id) && it.status == "ACCEPTED"
                            }
                            val isPending = friendships.any {
                                (it.senderId == currentUser?.id && it.receiverId == user.id) && it.status == "PENDING"
                            }

                            UserCardRow(
                                user = user,
                                actionText = when {
                                    isFriend -> "Amigos ✓"
                                    isPending -> "Pendiente"
                                    else -> "Agregar"
                                },
                                isActionDisabled = isFriend || isPending,
                                onActionClick = {
                                    coroutineScope.launch {
                                        repository.sendFriendRequest(currentUser?.id ?: 1L, user.id)
                                    }
                                },
                                onProfileClick = { onOpenProfile(user.id) }
                            )
                        }
                    }
                }
                1 -> {
                    // SOLICITUDES DE AMISTAD
                    if (pendingRequests.isEmpty()) {
                        item {
                            EmptyStateNotice("No tienes solicitudes de amistad pendientes.")
                        }
                    } else {
                        items(pendingRequests, key = { "${it.senderId}_${it.receiverId}" }) { req ->
                            val sender = allUsers.find { it.id == req.senderId }
                            if (sender != null) {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = NexaSurface)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        AsyncImage(
                                            model = sender.avatarUrl,
                                            contentDescription = sender.name,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .size(50.dp)
                                                .clip(CircleShape)
                                                .clickable { onOpenProfile(sender.id) }
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(text = sender.name, color = NexaTextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                                if (sender.role == "CREATOR") {
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    VerifiedBadgeIcon(size = 14)
                                                }
                                            }
                                            Text(text = "@${sender.username}", color = NexaTextSecondary, fontSize = 12.sp)

                                            Spacer(modifier = Modifier.height(8.dp))

                                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                Button(
                                                    onClick = {
                                                        coroutineScope.launch {
                                                            repository.acceptFriendRequest(req.senderId, req.receiverId)
                                                        }
                                                    },
                                                    colors = ButtonDefaults.buttonColors(containerColor = NexaCyan),
                                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                                                    shape = RoundedCornerShape(12.dp)
                                                ) {
                                                    Text("Confirmar", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                                }

                                                OutlinedButton(
                                                    onClick = {
                                                        coroutineScope.launch {
                                                            repository.rejectFriendRequest(req.senderId, req.receiverId)
                                                        }
                                                    },
                                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = NexaTextSecondary),
                                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                                                    shape = RoundedCornerShape(12.dp)
                                                ) {
                                                    Text("Eliminar", fontSize = 12.sp)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                2 -> {
                    // MIS AMIGOS
                    val friendIds = friendships.filter { it.status == "ACCEPTED" }.map {
                        if (it.senderId == currentUser?.id) it.receiverId else it.senderId
                    }
                    val myFriends = allUsers.filter { friendIds.contains(it.id) }

                    if (myFriends.isEmpty()) {
                        item {
                            EmptyStateNotice("Aún no tienes amigos agregados. ¡Explora las sugerencias!")
                        }
                    } else {
                        items(myFriends, key = { it.id }) { friend ->
                            UserCardRow(
                                user = friend,
                                actionText = "Ver perfil",
                                isActionDisabled = false,
                                onActionClick = { onOpenProfile(friend.id) },
                                onProfileClick = { onOpenProfile(friend.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UserCardRow(
    user: UserEntity,
    actionText: String,
    isActionDisabled: Boolean,
    onActionClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NexaSurface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = user.avatarUrl.ifBlank { "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=400&q=80" },
                contentDescription = user.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onProfileClick)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable(onClick = onProfileClick)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = user.name,
                        color = NexaTextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    if (user.role == "CREATOR") {
                        Spacer(modifier = Modifier.width(4.dp))
                        VerifiedBadgeIcon(size = 14)
                    }
                }
                Text(
                    text = "@${user.username}",
                    color = NexaCyan,
                    fontSize = 12.sp
                )
                if (user.bio.isNotBlank()) {
                    Text(
                        text = user.bio,
                        color = NexaTextSecondary,
                        fontSize = 11.sp,
                        maxLines = 1
                    )
                }
            }

            Button(
                onClick = onActionClick,
                enabled = !isActionDisabled,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (actionText.contains("Amigos") || actionText.contains("Pendiente")) NexaSurfaceElevated else NexaCyan,
                    disabledContainerColor = NexaSurfaceHighlight
                ),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = actionText,
                    color = if (actionText.contains("Amigos") || actionText.contains("Pendiente")) NexaTextSecondary else Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun EmptyStateNotice(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, color = NexaTextMuted, fontSize = 14.sp)
    }
}
