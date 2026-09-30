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
import androidx.compose.material.icons.filled.Close
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    repository: NexaRepository,
    currentUser: UserEntity?,
    onOpenProfile: (Long) -> Unit,
    onBack: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Personas", "Publicaciones", "Grupos y Páginas")

    val allUsers by repository.getAllUsers().collectAsState(initial = emptyList())
    val allPosts by repository.feedPosts.collectAsState(initial = emptyList())
    val allGroups by repository.getAllGroups().collectAsState(initial = emptyList())
    val allPages by repository.getAllPages().collectAsState(initial = emptyList())

    val filteredUsers = remember(searchQuery, allUsers) {
        if (searchQuery.isBlank()) allUsers else allUsers.filter {
            it.name.contains(searchQuery, ignoreCase = true) || it.username.contains(searchQuery, ignoreCase = true)
        }
    }

    val filteredPosts = remember(searchQuery, allPosts) {
        if (searchQuery.isBlank()) allPosts else allPosts.filter {
            it.content.contains(searchQuery, ignoreCase = true) || it.authorName.contains(searchQuery, ignoreCase = true)
        }
    }

    val filteredCommunities = remember(searchQuery, allGroups, allPages) {
        val q = searchQuery.trim()
        val g = if (q.isBlank()) allGroups else allGroups.filter { it.name.contains(q, ignoreCase = true) }
        val p = if (q.isBlank()) allPages else allPages.filter { it.name.contains(q, ignoreCase = true) }
        Pair(g, p)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Buscar en ClipBook...", color = NexaTextMuted, fontSize = 14.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = NexaCyan) },
                        trailingIcon = {
                            if (searchQuery.isNotBlank()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Close, contentDescription = "Limpiar", tint = NexaTextSecondary)
                                }
                            }
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("search_text_input"),
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = NexaSurfaceElevated,
                            unfocusedContainerColor = NexaSurfaceElevated,
                            focusedBorderColor = NexaCyan,
                            unfocusedBorderColor = NexaBorder
                        )
                    )
                },
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
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = NexaSurfaceElevated,
                contentColor = NexaCyan
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title, fontSize = 12.sp, fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal) }
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp),
                contentPadding = PaddingValues(vertical = 10.dp)
            ) {
                when (selectedTab) {
                    0 -> {
                        // PERSONAS
                        items(filteredUsers, key = { it.id }) { user ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable { onOpenProfile(user.id) },
                                shape = RoundedCornerShape(12.dp),
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
                                        modifier = Modifier.size(44.dp).clip(CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(user.name, color = NexaTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                            if (user.role == "CREATOR") {
                                                Spacer(modifier = Modifier.width(4.dp))
                                                VerifiedBadgeIcon(size = 13)
                                            }
                                        }
                                        Text("@${user.username}", color = NexaCyan, fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                    1 -> {
                        // PUBLICACIONES
                        items(filteredPosts, key = { it.id }) { post ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = NexaSurface)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(post.authorName, color = NexaCyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(post.content, color = NexaTextPrimary, fontSize = 13.sp)
                                }
                            }
                        }
                    }
                    2 -> {
                        // GRUPOS Y PÁGINAS
                        val (groups, pages) = filteredCommunities
                        items(groups, key = { "g_${it.id}" }) { group ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = NexaSurface)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(group.name, color = NexaTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text("Grupo • ${group.memberCount} miembros", color = NexaCyan, fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                        items(pages, key = { "p_${it.id}" }) { page ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = NexaSurface)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(page.name, color = NexaTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text("Página oficial • ${page.category}", color = NexaPurpleLight, fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
