package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.entity.StoryEntity
import com.example.data.local.entity.UserEntity
import com.example.data.repository.NexaRepository
import com.example.ui.components.VerifiedBadgeIcon
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun StoryViewerScreen(
    story: StoryEntity,
    currentUser: UserEntity?,
    repository: NexaRepository,
    onClose: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var replyText by remember { mutableStateOf("") }
    val progress = remember { Animatable(0f) }

    LaunchedEffect(story.id) {
        repository.incrementStoryView(story.id)
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 6000, easing = LinearEasing)
        )
        onClose()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("story_viewer_screen")
    ) {
        // Background media / gradient
        if (!story.mediaUrl.isNullOrBlank()) {
            AsyncImage(
                model = story.mediaUrl,
                contentDescription = "Historia",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        when (story.gradientIndex % 3) {
                            1 -> NexaGradientCard
                            2 -> NexaGradientButton
                            else -> NexaGradientPrimary
                        }
                    )
            )
        }

        // Top progress indicator
        LinearProgressIndicator(
            progress = { progress.value },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp)),
            color = NexaCyan,
            trackColor = Color.White.copy(alpha = 0.3f),
        )

        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 18.dp, start = 14.dp, end = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = story.authorAvatar.ifBlank { "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=400&q=80" },
                contentDescription = story.authorName,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = story.authorName,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    if (story.isAuthorVerified) {
                        Spacer(modifier = Modifier.width(4.dp))
                        VerifiedBadgeIcon(size = 14)
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Visibility, contentDescription = null, tint = NexaCyan, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${story.viewsCount} vistas",
                        color = NexaTextSecondary,
                        fontSize = 11.sp
                    )
                }
            }

            if (story.userId == currentUser?.id) {
                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            repository.deleteStory(story.id)
                            onClose()
                        }
                    }
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = NexaError)
                }
            }

            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color.White)
            }
        }

        // Caption text
        if (story.textCaption.isNotBlank()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center)
                    .padding(horizontal = 24.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.Black.copy(alpha = 0.5f))
                    .padding(16.dp)
            ) {
                Text(
                    text = story.textCaption,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Bottom Reply Input
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = replyText,
                onValueChange = { replyText = it },
                placeholder = { Text("Responder a la historia...", color = Color.White.copy(alpha = 0.6f), fontSize = 13.sp) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.Black.copy(alpha = 0.6f),
                    unfocusedContainerColor = Color.Black.copy(alpha = 0.6f),
                    focusedBorderColor = NexaCyan,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.4f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    if (replyText.isNotBlank()) {
                        coroutineScope.launch {
                            repository.sendMessage(
                                senderId = currentUser?.id ?: 1L,
                                receiverId = story.userId,
                                text = "Respondió a tu historia: $replyText"
                            )
                            replyText = ""
                            onClose()
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

@Composable
fun CreateStoryDialog(
    currentUser: UserEntity?,
    repository: NexaRepository,
    onDismiss: () -> Unit,
    onStoryCreated: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var caption by remember { mutableStateOf("") }
    var mediaUrl by remember { mutableStateOf("") }
    var gradientIdx by remember { mutableStateOf(0) }
    var isSubmitting by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = NexaSurface,
        title = {
            Text("Crear Nueva Historia", color = NexaTextPrimary, fontWeight = FontWeight.Bold)
        },
        text = {
            Column {
                Text(
                    "Las historias desaparecen automáticamente tras 24 horas.",
                    color = NexaTextMuted,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = caption,
                    onValueChange = { caption = it },
                    label = { Text("Texto de la historia", color = NexaTextSecondary) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = NexaSurfaceElevated,
                        unfocusedContainerColor = NexaSurfaceElevated,
                        focusedBorderColor = NexaCyan,
                        unfocusedBorderColor = NexaBorder
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = mediaUrl,
                    onValueChange = { mediaUrl = it },
                    label = { Text("URL de la foto (opcional)", color = NexaTextSecondary) },
                    placeholder = { Text("https://...", color = NexaTextMuted) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = NexaSurfaceElevated,
                        unfocusedContainerColor = NexaSurfaceElevated,
                        focusedBorderColor = NexaCyan,
                        unfocusedBorderColor = NexaBorder
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text("Estilo de fondo:", color = NexaTextSecondary, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Cian/Azul", "Oscuro", "Gradiente").forEachIndexed { index, name ->
                        FilterChip(
                            selected = gradientIdx == index,
                            onClick = { gradientIdx = index },
                            label = { Text(name, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = NexaCyan,
                                selectedLabelColor = Color.Black
                            )
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (caption.isNotBlank() || mediaUrl.isNotBlank()) {
                        coroutineScope.launch {
                            isSubmitting = true
                            repository.createStory(
                                userId = currentUser?.id ?: 1L,
                                mediaUrl = mediaUrl.ifBlank { null },
                                caption = caption,
                                gradientIndex = gradientIdx
                            )
                            isSubmitting = false
                            onStoryCreated()
                        }
                    }
                },
                enabled = !isSubmitting,
                colors = ButtonDefaults.buttonColors(containerColor = NexaCyan)
            ) {
                Text("Publicar Historia", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = NexaTextSecondary)
            }
        }
    )
}
