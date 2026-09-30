package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.entity.ConversationEntity
import com.example.data.local.entity.MessageEntity
import com.example.data.local.entity.UserEntity
import com.example.data.repository.NexaRepository
import com.example.ui.components.ReportDialog
import com.example.ui.components.VerifiedBadgeIcon
import com.example.ui.components.formatTime
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagesScreen(
    repository: NexaRepository,
    currentUser: UserEntity?,
    onOpenConversation: (otherUserId: Long) -> Unit,
    onBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val allUsers by repository.getAllUsers().collectAsState(initial = emptyList())
    val conversations by repository.getUserConversations(currentUser?.id ?: 1L).collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mensajes Privados", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NexaSurface)
            )
        },
        containerColor = NexaBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Quick start chat with contacts row
            Text(
                text = "Contactos",
                color = NexaTextSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                allUsers.filter { it.id != currentUser?.id }.take(5).forEach { contact ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { onOpenConversation(contact.id) }
                    ) {
                        AsyncImage(
                            model = contact.avatarUrl.ifBlank { "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=400&q=80" },
                            contentDescription = contact.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = contact.name.split(" ").firstOrNull() ?: "",
                            color = NexaTextPrimary,
                            fontSize = 11.sp,
                            maxLines = 1
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Conversaciones Recientes",
                color = NexaTextSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            if (conversations.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No tienes conversaciones activas aún.\nSelecciona un contacto arriba para chatear.",
                        color = NexaTextMuted,
                        fontSize = 13.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    items(conversations, key = { it.id }) { conv ->
                        val otherUserId = if (conv.user1Id == currentUser?.id) conv.user2Id else conv.user1Id
                        val otherUser = allUsers.find { it.id == otherUserId }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { onOpenConversation(otherUserId) },
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = NexaSurface)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = otherUser?.avatarUrl?.ifBlank { "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=400&q=80" },
                                    contentDescription = otherUser?.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(CircleShape)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = otherUser?.name ?: "Usuario",
                                            color = NexaTextPrimary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                        if (otherUser?.role == "CREATOR") {
                                            Spacer(modifier = Modifier.width(4.dp))
                                            VerifiedBadgeIcon(size = 13)
                                        }
                                    }
                                    Text(
                                        text = conv.lastMessage,
                                        color = NexaTextSecondary,
                                        fontSize = 12.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Text(
                                    text = formatTime(conv.lastMessageTime),
                                    color = NexaTextMuted,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailScreen(
    repository: NexaRepository,
    currentUser: UserEntity?,
    otherUserId: Long,
    onBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val allUsers by repository.getAllUsers().collectAsState(initial = emptyList())
    val otherUser = allUsers.find { it.id == otherUserId }

    var messageInput by remember { mutableStateOf("") }
    var showReportDialog by remember { mutableStateOf(false) }

    val conversations by repository.getUserConversations(currentUser?.id ?: 1L).collectAsState(initial = emptyList())
    val currentConv = conversations.find {
        (it.user1Id == currentUser?.id && it.user2Id == otherUserId) ||
        (it.user2Id == currentUser?.id && it.user1Id == otherUserId)
    }

    val messages by if (currentConv != null) {
        repository.getConversationMessages(currentConv.id).collectAsState(initial = emptyList())
    } else {
        remember { mutableStateOf(emptyList<MessageEntity>()) }
    }

    // Auto mark as read
    LaunchedEffect(currentConv?.id) {
        currentConv?.id?.let {
            repository.markMessagesAsRead(it, currentUser?.id ?: 1L)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(
                            model = otherUser?.avatarUrl?.ifBlank { "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=400&q=80" },
                            contentDescription = otherUser?.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = otherUser?.name ?: "Chat",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                if (otherUser?.role == "CREATOR") {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    VerifiedBadgeIcon(size = 13)
                                }
                            }
                            Text(
                                text = "En línea • Cifrado de extremo a extremo",
                                color = NexaCyan,
                                fontSize = 11.sp
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                actions = {
                    IconButton(onClick = { showReportDialog = true }) {
                        Icon(Icons.Default.Flag, contentDescription = "Denunciar", tint = NexaWarning)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NexaSurface)
            )
        },
        containerColor = NexaBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Message List
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 14.dp),
                reverseLayout = true
            ) {
                items(messages.reversed(), key = { it.id }) { msg ->
                    val isMine = msg.senderId == currentUser?.id
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = if (isMine) Arrangement.End else Arrangement.Start
                    ) {
                        Box(
                            modifier = Modifier
                                .widthIn(max = 280.dp)
                                .clip(
                                    RoundedCornerShape(
                                        topStart = 16.dp,
                                        topEnd = 16.dp,
                                        bottomStart = if (isMine) 16.dp else 2.dp,
                                        bottomEnd = if (isMine) 2.dp else 16.dp
                                    )
                                )
                                .background(if (isMine) NexaBlue else NexaSurfaceElevated)
                                .padding(12.dp)
                        ) {
                            Column {
                                Text(
                                    text = msg.text,
                                    color = Color.White,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                Row(
                                    modifier = Modifier.align(Alignment.End),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = formatTime(msg.timestamp),
                                        color = Color.White.copy(alpha = 0.6f),
                                        fontSize = 10.sp
                                    )
                                    if (isMine) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            imageVector = Icons.Default.DoneAll,
                                            contentDescription = if (msg.isRead) "Leído" else "Enviado",
                                            tint = if (msg.isRead) NexaCyan else Color.White.copy(alpha = 0.6f),
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Chat input row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(NexaSurface)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = messageInput,
                    onValueChange = { messageInput = it },
                    placeholder = { Text("Escribe un mensaje seguro...", color = NexaTextMuted, fontSize = 13.sp) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = NexaSurfaceElevated,
                        unfocusedContainerColor = NexaSurfaceElevated,
                        focusedBorderColor = NexaCyan,
                        unfocusedBorderColor = NexaBorder
                    )
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        if (messageInput.isNotBlank()) {
                            val textToSend = messageInput
                            messageInput = ""
                            coroutineScope.launch {
                                repository.sendMessage(
                                    senderId = currentUser?.id ?: 1L,
                                    receiverId = otherUserId,
                                    text = textToSend
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(NexaCyan)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Enviar",
                        tint = Color.Black
                    )
                }
            }
        }
    }

    if (showReportDialog) {
        ReportDialog(
            targetTitle = "Chat con @${otherUser?.username ?: "usuario"}",
            onDismiss = { showReportDialog = false },
            onSubmitReport = { reason, _ ->
                coroutineScope.launch {
                    repository.submitReport(
                        reporterId = currentUser?.id ?: 1L,
                        targetType = "MESSAGE_CHAT",
                        targetId = otherUserId,
                        targetPreview = "Conversación con usuario $otherUserId",
                        reason = reason
                    )
                }
                showReportDialog = false
            }
        )
    }
}
