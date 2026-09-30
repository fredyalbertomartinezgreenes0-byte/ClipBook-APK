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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Public
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
import com.example.data.local.entity.PageEntity
import com.example.data.local.entity.UserEntity
import com.example.data.repository.NexaRepository
import com.example.ui.components.VerifiedBadgeIcon
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PagesScreen(
    repository: NexaRepository,
    currentUser: UserEntity?,
    onBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val pages by repository.getAllPages().collectAsState(initial = emptyList())
    var showCreateDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Páginas y Marcas", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                actions = {
                    IconButton(onClick = { showCreateDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Crear Página", tint = NexaCyan)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NexaSurface)
            )
        },
        containerColor = NexaBackground
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 14.dp),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            items(pages, key = { it.id }) { page ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .testTag("page_card_${page.id}"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = NexaSurface)
                ) {
                    Column {
                        if (page.coverUrl.isNotBlank()) {
                            AsyncImage(
                                model = page.coverUrl,
                                contentDescription = page.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp)
                            )
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = page.avatarUrl.ifBlank { "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=400&q=80" },
                                contentDescription = page.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = page.name,
                                        color = NexaTextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    VerifiedBadgeIcon(size = 13)
                                }
                                Text(
                                    text = "${page.category} • ${page.followersCount} seguidores",
                                    color = NexaCyan,
                                    fontSize = 12.sp
                                )
                                if (page.description.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = page.description,
                                        color = NexaTextSecondary,
                                        fontSize = 12.sp,
                                        maxLines = 1
                                    )
                                }
                            }

                            Button(
                                onClick = {
                                    coroutineScope.launch {
                                        repository.toggleFollow(currentUser?.id ?: 1L, page.id, "PAGE")
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = NexaCyan),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Text("Seguir", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        CreatePageDialog(
            onDismiss = { showCreateDialog = false },
            onCreate = { name, cat, desc ->
                coroutineScope.launch {
                    repository.createPage(
                        name = name,
                        category = cat,
                        description = desc,
                        ownerId = currentUser?.id ?: 1L,
                        coverUrl = "https://images.unsplash.com/photo-1579546929518-9e396f3cc809?w=1200&q=80"
                    )
                    showCreateDialog = false
                }
            }
        )
    }
}

@Composable
fun CreatePageDialog(
    onDismiss: () -> Unit,
    onCreate: (name: String, category: String, description: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Creador") }
    var description by remember { mutableStateOf("") }
    val categories = listOf("Creador", "Empresa", "Comunidad", "Marca")

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = NexaSurface,
        title = { Text("Crear Página Oficial", color = NexaTextPrimary, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre de la página", color = NexaTextSecondary) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))

                Text("Categoría:", color = NexaTextSecondary, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    categories.forEach { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = NexaCyan,
                                selectedLabelColor = Color.Black
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción", color = NexaTextSecondary) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { if (name.isNotBlank()) onCreate(name, category, description) },
                colors = ButtonDefaults.buttonColors(containerColor = NexaCyan)
            ) {
                Text("Crear Página", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar", color = NexaTextSecondary) }
        }
    )
}
