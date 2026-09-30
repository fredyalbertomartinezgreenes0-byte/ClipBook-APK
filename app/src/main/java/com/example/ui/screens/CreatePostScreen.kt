package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.ui.components.VerifiedBadgeIcon
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(
    repository: NexaRepository,
    currentUser: UserEntity?,
    onBack: () -> Unit,
    onPostCreated: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var postContent by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var videoUrl by remember { mutableStateOf("") }
    var linkUrl by remember { mutableStateOf("") }

    // Poll feature
    var isPollEnabled by remember { mutableStateOf(false) }
    var pollQuestion by remember { mutableStateOf("") }
    var pollOptions by remember { mutableStateOf(listOf("", "")) }

    var isMediaPickerExpanded by remember { mutableStateOf(false) }
    var isVideoPickerExpanded by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isPublishing by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear Publicación", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                actions = {
                    Button(
                        onClick = {
                            if (postContent.isBlank() && imageUrl.isBlank() && !isPollEnabled) {
                                errorMessage = "Por favor escribe algo o añade una foto/encuesta."
                                return@Button
                            }
                            coroutineScope.launch {
                                isPublishing = true
                                errorMessage = null
                                val validOptions = if (isPollEnabled) pollOptions.filter { it.isNotBlank() } else null
                                val result = repository.createPost(
                                    userId = currentUser?.id ?: 1L,
                                    content = postContent,
                                    imageUrl = imageUrl.ifBlank { null },
                                    videoUrl = videoUrl.ifBlank { null },
                                    linkUrl = linkUrl.ifBlank { null },
                                    pollQuestion = if (isPollEnabled) pollQuestion.ifBlank { null } else null,
                                    pollOptions = if (isPollEnabled && validOptions?.size ?: 0 >= 2) validOptions else null
                                )
                                isPublishing = false
                                if (result.isSuccess) {
                                    onPostCreated()
                                } else {
                                    errorMessage = result.exceptionOrNull()?.message ?: "Error al publicar"
                                }
                            }
                        },
                        enabled = !isPublishing,
                        colors = ButtonDefaults.buttonColors(containerColor = NexaCyan),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.testTag("publish_post_button")
                    ) {
                        if (isPublishing) {
                            CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(18.dp))
                        } else {
                            Text("Publicar", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // User row
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = currentUser?.avatarUrl?.ifBlank { "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=400&q=80" },
                    contentDescription = currentUser?.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = currentUser?.name ?: "Usuario",
                            color = NexaTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        if (currentUser?.role == "CREATOR") {
                            Spacer(modifier = Modifier.width(4.dp))
                            VerifiedBadgeIcon(size = 14)
                        }
                    }
                    Text(
                        text = "Público 🌐",
                        color = NexaCyan,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Main text input
            OutlinedTextField(
                value = postContent,
                onValueChange = { postContent = it },
                placeholder = {
                    Text(
                        "¿De qué te gustaría hablar hoy en ClipBook?",
                        color = NexaTextMuted,
                        fontSize = 15.sp
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 120.dp)
                    .testTag("post_content_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = NexaTextPrimary,
                    unfocusedTextColor = NexaTextPrimary
                )
            )

            // Image Preview if set
            if (imageUrl.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(modifier = Modifier.fillMaxWidth()) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = "Vista previa de imagen",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                    Surface(
                        color = Color.Black.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.align(Alignment.BottomStart).padding(8.dp)
                    ) {
                        Text("📷 FOTO", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                    }
                    IconButton(
                        onClick = { imageUrl = "" },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(6.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.6f))
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Quitar", tint = Color.White)
                    }
                }
            }

            // Video Preview if set
            if (videoUrl.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF0F172A)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Surface(
                            shape = CircleShape,
                            color = NexaCyan.copy(alpha = 0.9f),
                            modifier = Modifier.size(54.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.PlayArrow, contentDescription = "Reproducir", tint = Color.Black, modifier = Modifier.size(32.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Clip de Video Adjunto", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(videoUrl.takeLast(30), color = NexaTextMuted, fontSize = 11.sp)
                    }

                    Surface(
                        color = Color.Black.copy(alpha = 0.7f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.align(Alignment.BottomStart).padding(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
                            Icon(Icons.Default.Videocam, contentDescription = null, tint = NexaCyan, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("VIDEO CLIP • 00:45", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    IconButton(
                        onClick = { videoUrl = "" },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(6.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.6f))
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Quitar", tint = Color.White)
                    }
                }
            }

            // Interactive Poll Builder
            AnimatedVisibility(visible = isPollEnabled) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = NexaSurfaceElevated)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Crear Encuesta", color = NexaCyan, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            IconButton(onClick = { isPollEnabled = false }) {
                                Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = NexaTextSecondary)
                            }
                        }

                        OutlinedTextField(
                            value = pollQuestion,
                            onValueChange = { pollQuestion = it },
                            placeholder = { Text("Haz una pregunta...", color = NexaTextMuted, fontSize = 13.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = NexaSurface,
                                unfocusedContainerColor = NexaSurface,
                                focusedBorderColor = NexaCyan,
                                unfocusedBorderColor = NexaBorder
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        pollOptions.forEachIndexed { index, optionText ->
                            OutlinedTextField(
                                value = optionText,
                                onValueChange = { newText ->
                                    val updated = pollOptions.toMutableList()
                                    updated[index] = newText
                                    pollOptions = updated
                                },
                                placeholder = { Text("Opción ${index + 1}", color = NexaTextMuted, fontSize = 13.sp) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = NexaSurface,
                                    unfocusedContainerColor = NexaSurface,
                                    focusedBorderColor = NexaCyan,
                                    unfocusedBorderColor = NexaBorder
                                )
                            )
                        }

                        if (pollOptions.size < 4) {
                            TextButton(
                                onClick = { pollOptions = pollOptions + "" }
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, tint = NexaCyan)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Añadir opción", color = NexaCyan, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }

            // Error display
            errorMessage?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = it, color = NexaError, fontSize = 13.sp)
            }

            Spacer(modifier = Modifier.weight(1f))

            // Attachment bar
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = NexaSurfaceElevated)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = {
                            isMediaPickerExpanded = !isMediaPickerExpanded
                            isVideoPickerExpanded = false
                        }) {
                            Icon(Icons.Default.Image, contentDescription = "Foto", tint = NexaCyan)
                        }
                        IconButton(onClick = {
                            isVideoPickerExpanded = !isVideoPickerExpanded
                            isMediaPickerExpanded = false
                        }) {
                            Icon(Icons.Default.Videocam, contentDescription = "Video", tint = NexaGold)
                        }
                        IconButton(onClick = {
                            isPollEnabled = !isPollEnabled
                        }) {
                            Icon(Icons.Default.Poll, contentDescription = "Encuesta", tint = NexaPurpleLight)
                        }
                        IconButton(onClick = {
                            imageUrl = "https://images.unsplash.com/photo-1579546929518-9e396f3cc809?w=1000&q=80"
                        }) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = "Fondo Creativo", tint = NexaBlueLight)
                        }
                    }

                    // Photo input panel
                    if (isMediaPickerExpanded) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Subir Foto o Imagen:", color = NexaCyan, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = imageUrl,
                            onValueChange = { imageUrl = it },
                            placeholder = { Text("URL de la imagen (ej: https://...)", color = NexaTextMuted, fontSize = 12.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = NexaSurface,
                                unfocusedContainerColor = NexaSurface,
                                focusedBorderColor = NexaCyan,
                                unfocusedBorderColor = NexaBorder
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            AssistChip(
                                onClick = { imageUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=1000&q=80" },
                                label = { Text("Arte Digital", fontSize = 11.sp) }
                            )
                            AssistChip(
                                onClick = { imageUrl = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=1000&q=80" },
                                label = { Text("Playa 4K", fontSize = 11.sp) }
                            )
                            AssistChip(
                                onClick = { imageUrl = "https://images.unsplash.com/photo-1518770660439-4636190af475?w=1000&q=80" },
                                label = { Text("Tecnología", fontSize = 11.sp) }
                            )
                        }
                    }

                    // Video input panel
                    if (isVideoPickerExpanded) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Subir Video o Clip:", color = NexaGold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = videoUrl,
                            onValueChange = { videoUrl = it },
                            placeholder = { Text("URL del video (ej: https://.../video.mp4)", color = NexaTextMuted, fontSize = 12.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = NexaSurface,
                                unfocusedContainerColor = NexaSurface,
                                focusedBorderColor = NexaGold,
                                unfocusedBorderColor = NexaBorder
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Clips virales de prueba:", color = NexaTextMuted, fontSize = 11.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            AssistChip(
                                onClick = { videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4" },
                                label = { Text("🐰 Animación HD", fontSize = 11.sp) }
                            )
                            AssistChip(
                                onClick = { videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4" },
                                label = { Text("🔥 Clip Acción", fontSize = 11.sp) }
                            )
                            AssistChip(
                                onClick = { videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4" },
                                label = { Text("⚡ Clip Sci-Fi", fontSize = 11.sp) }
                            )
                        }
                    }
                }
            }
        }
    }
}
