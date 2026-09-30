package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.entity.UserEntity
import com.example.data.local.entity.VerificationEntity
import com.example.data.repository.NexaRepository
import com.example.ui.components.PostCard
import com.example.ui.components.VerifiedBadgeIcon
import com.example.ui.components.VerifiedBadgePill
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    repository: NexaRepository,
    targetUserId: Long,
    currentUserId: Long,
    onRequestVerification: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val allUsers by repository.getAllUsers().collectAsState(initial = emptyList())
    val user = allUsers.find { it.id == targetUserId }

    val verification by repository.currentUserVerification.collectAsState(initial = null)
    val isVerified = if (targetUserId == currentUserId) {
        verification != null && verification?.internalStatus == "ACTIVE"
    } else {
        user?.role == "CREATOR"
    }

    val allPosts by repository.feedPosts.collectAsState(initial = emptyList())
    val userPosts = remember(allPosts, targetUserId) {
        allPosts.filter { it.userId == targetUserId }
    }

    val friendships by repository.getFriendships(targetUserId).collectAsState(initial = emptyList())
    val friendsCount = friendships.count { it.status == "ACCEPTED" }

    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showSendCoinsDialog by remember { mutableStateOf(false) }
    var transferCoinsAmount by remember { mutableStateOf("") }
    var transferCoinsNote by remember { mutableStateOf("") }
    var transferFeedback by remember { mutableStateOf<String?>(null) }
    var isSendingCoins by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(NexaBackground)
            .testTag("profile_screen"),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        item {
            Box(modifier = Modifier.fillMaxWidth()) {
                // Cover photo
                AsyncImage(
                    model = user?.coverUrl?.ifBlank { "https://images.unsplash.com/photo-1579546929518-9e396f3cc809?w=1200&q=80" },
                    contentDescription = "Portada",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(170.dp)
                )

                // Avatar
                Box(
                    modifier = Modifier
                        .padding(start = 16.dp, top = 115.dp)
                        .size(96.dp)
                        .clip(CircleShape)
                        .border(3.dp, NexaBackground, CircleShape)
                        .background(NexaSurfaceElevated)
                ) {
                    AsyncImage(
                        model = user?.avatarUrl?.ifBlank { "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=400&q=80" },
                        contentDescription = user?.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                // Name & Verified Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = user?.name ?: "Usuario",
                                color = NexaTextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                            if (isVerified) {
                                Spacer(modifier = Modifier.width(6.dp))
                                VerifiedBadgeIcon(size = 18)
                            }
                        }
                        Text(
                            text = "@${user?.username ?: "usuario"}",
                            color = NexaCyan,
                            fontSize = 13.sp
                        )
                    }

                    if (targetUserId == currentUserId) {
                        OutlinedButton(
                            onClick = { showEditProfileDialog = true },
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = NexaTextPrimary),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = Brush.horizontalGradient(listOf(NexaBorder, NexaBorderSubtle))
                            )
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Editar perfil", fontSize = 12.sp)
                        }
                    } else {
                        Button(
                            onClick = { showSendCoinsDialog = true },
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NexaGold),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text("🪙 Enviar Monedas", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Bio
                if (!user?.bio.isNullOrBlank()) {
                    Text(
                        text = user?.bio ?: "",
                        color = NexaTextPrimary,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // ClipCoins & Role Card
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = NexaSurfaceElevated,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🪙", fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("Billetera ClipCoins", color = NexaTextMuted, fontSize = 11.sp)
                                Text("${user?.coins ?: 1000} monedas", color = NexaGold, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        }
                        if (user?.role == "CREATOR") {
                            Surface(shape = RoundedCornerShape(8.dp), color = NexaGold.copy(alpha = 0.2f)) {
                                Text("👑 CREADOR OFICIAL", color = NexaGold, fontWeight = FontWeight.Black, fontSize = 11.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                            }
                        }
                    }
                }

                // Verification Badge Pill or Request Button
                // Strictly follows Requirement 14:
                // If verified: ONLY shows "✓ Cuenta verificada"
                // If not verified: shows "Solicitar verificación"
                // No dates, expirations or internal pending/active statuses!
                if (isVerified) {
                    VerifiedBadgePill()
                } else if (targetUserId == currentUserId) {
                    Button(
                        onClick = onRequestVerification,
                        colors = ButtonDefaults.buttonColors(containerColor = NexaBlue),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.VerifiedUser, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Solicitar verificación", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Stats row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(NexaSurface)
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    ProfileStat("Publicaciones", "${userPosts.size}")
                    ProfileStat("Amigos", "$friendsCount")
                    ProfileStat("Seguidores", "${friendsCount * 2 + 15}")
                }

                Spacer(modifier = Modifier.height(18.dp))
                Text(
                    text = "Publicaciones de ${user?.name ?: ""}",
                    color = NexaTextSecondary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }

        if (userPosts.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 30.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Aún no hay publicaciones aquí.", color = NexaTextMuted, fontSize = 13.sp)
                }
            }
        } else {
            items(userPosts, key = { it.id }) { post ->
                val reactions by repository.getReactions("POST", post.id).collectAsState(initial = emptyList())
                val comments by repository.getComments(post.id).collectAsState(initial = emptyList())

                PostCard(
                    post = post,
                    currentUserId = currentUserId,
                    reactions = reactions,
                    comments = comments,
                    onReactionClick = { rType ->
                        coroutineScope.launch { repository.toggleReaction("POST", post.id, currentUserId, rType) }
                    },
                    onVotePoll = { optId ->
                        coroutineScope.launch { repository.votePoll(post.id, optId) }
                    },
                    onAddComment = { pId, text, parentId ->
                        coroutineScope.launch { repository.addComment(pId, currentUserId, text, parentId) }
                    },
                    onDeletePost = {
                        coroutineScope.launch { repository.deletePost(post.id) }
                    },
                    onHidePost = {
                        coroutineScope.launch { repository.hidePost(post.id) }
                    },
                    onReportPost = {}
                )
            }
        }
    }

    if (showEditProfileDialog && user != null) {
        EditProfileDialog(
            user = user,
            onDismiss = { showEditProfileDialog = false },
            onSave = { newName, newBio, newAvatar, newCover ->
                coroutineScope.launch {
                    repository.updateProfile(user.id, newName, newBio, newAvatar, newCover)
                    showEditProfileDialog = false
                }
            }
        )
    }

    if (showSendCoinsDialog && user != null) {
        AlertDialog(
            onDismissRequest = {
                showSendCoinsDialog = false
                transferFeedback = null
            },
            containerColor = NexaSurface,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🪙 Enviar ClipCoins", color = NexaTextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            },
            text = {
                Column {
                    Text("Destinatario: ${user.name} (@${user.username})", color = NexaCyan, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    transferFeedback?.let {
                        Text(it, color = NexaGold, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.padding(bottom = 8.dp))
                    }

                    // Chips
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(50L, 100L, 500L, 1000L).forEach { amt ->
                            AssistChip(
                                onClick = { transferCoinsAmount = amt.toString() },
                                label = { Text("$amt 🪙", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = transferCoinsAmount,
                        onValueChange = { transferCoinsAmount = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Cantidad de monedas") },
                        placeholder = { Text("ej: 500") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = transferCoinsNote,
                        onValueChange = { transferCoinsNote = it },
                        label = { Text("Mensaje (opcional)") },
                        placeholder = { Text("¡Un regalo para ti!") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amt = transferCoinsAmount.toLongOrNull() ?: 0L
                        if (amt <= 0) {
                            transferFeedback = "Ingresa un monto válido mayor a 0."
                            return@Button
                        }
                        coroutineScope.launch {
                            isSendingCoins = true
                            val res = repository.transferCoins(
                                fromUserId = currentUserId ?: 1L,
                                toUserId = user.id,
                                amount = amt,
                                note = transferCoinsNote
                            )
                            isSendingCoins = false
                            if (res.isSuccess) {
                                transferFeedback = "¡Se enviaron $amt 🪙 a ${user.name}! 🚀"
                                transferCoinsAmount = ""
                                transferCoinsNote = ""
                            } else {
                                transferFeedback = res.exceptionOrNull()?.message ?: "Error al transferir"
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NexaGold),
                    enabled = !isSendingCoins
                ) {
                    if (isSendingCoins) {
                        CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(16.dp))
                    } else {
                        Text("Enviar Monedas", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showSendCoinsDialog = false
                    transferFeedback = null
                }) {
                    Text("Cerrar", color = NexaCyan)
                }
            }
        )
    }
}

@Composable
fun ProfileStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, color = NexaCyan, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Text(text = label, color = NexaTextSecondary, fontSize = 12.sp)
    }
}

@Composable
fun EditProfileDialog(
    user: UserEntity,
    onDismiss: () -> Unit,
    onSave: (name: String, bio: String, avatarUrl: String, coverUrl: String) -> Unit
) {
    var name by remember { mutableStateOf(user.name) }
    var bio by remember { mutableStateOf(user.bio) }
    var avatarUrl by remember { mutableStateOf(user.avatarUrl) }
    var coverUrl by remember { mutableStateOf(user.coverUrl) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = NexaSurface,
        title = { Text("Editar Perfil", color = NexaTextPrimary, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre", color = NexaTextSecondary) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = { Text("Biografía", color = NexaTextSecondary) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = avatarUrl,
                    onValueChange = { avatarUrl = it },
                    label = { Text("URL de Foto de Perfil", color = NexaTextSecondary) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = coverUrl,
                    onValueChange = { coverUrl = it },
                    label = { Text("URL de Portada", color = NexaTextSecondary) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(name, bio, avatarUrl, coverUrl) },
                colors = ButtonDefaults.buttonColors(containerColor = NexaCyan)
            ) {
                Text("Guardar Cambios", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = NexaTextSecondary)
            }
        }
    )
}
