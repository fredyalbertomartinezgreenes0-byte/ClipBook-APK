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
import com.example.data.local.entity.PostEntity
import com.example.data.local.entity.StoryEntity
import com.example.data.local.entity.UserEntity
import com.example.data.repository.NexaRepository
import com.example.ui.components.PostCard
import com.example.ui.components.StoryBar
import com.example.ui.theme.*
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun FeedScreen(
    repository: NexaRepository,
    currentUser: UserEntity?,
    onOpenCreatePost: () -> Unit,
    onOpenCreateStory: () -> Unit,
    onOpenStory: (StoryEntity) -> Unit,
    onOpenProfile: (Long) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val posts by repository.feedPosts.collectAsState(initial = emptyList())
    val stories by repository.activeStories.collectAsState(initial = emptyList())

    var selectedFilter by remember { mutableStateOf("Todo") }
    val filters = listOf("Todo", "Amigos", "Popular")

    val filteredPosts = remember(posts, selectedFilter) {
        when (selectedFilter) {
            "Amigos" -> posts.filter { it.userId != 1L }
            "Popular" -> posts.sortedByDescending { it.shareCount }
            else -> posts
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(NexaBackground)
            .testTag("feed_list"),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // Quick composer teaser card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .clickable(onClick = onOpenCreatePost),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = NexaSurfaceElevated)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = currentUser?.avatarUrl?.ifBlank { "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=400&q=80" },
                        contentDescription = "Avatar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(20.dp))
                            .background(NexaSurface)
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = "¿Qué estás pensando, ${currentUser?.name?.split(" ")?.firstOrNull() ?: ""}?",
                            color = NexaTextMuted,
                            fontSize = 13.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = onOpenCreatePost) {
                        Icon(Icons.Default.AddPhotoAlternate, contentDescription = "Foto", tint = NexaCyan)
                    }
                    IconButton(onClick = onOpenCreatePost) {
                        Icon(Icons.Default.Videocam, contentDescription = "Video Clip", tint = NexaGold)
                    }
                    IconButton(onClick = onOpenCreatePost) {
                        Icon(Icons.Default.Poll, contentDescription = "Encuesta", tint = NexaPurpleLight)
                    }
                }
            }
        }

        // Stories carousel
        item {
            StoryBar(
                currentUser = currentUser,
                stories = stories,
                onAddStoryClick = onOpenCreateStory,
                onStoryClick = onOpenStory
            )
        }

        // Filter chips row
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filters.forEach { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter },
                        label = { Text(filter, fontSize = 13.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NexaCyan,
                            selectedLabelColor = Color.Black,
                            containerColor = NexaSurfaceElevated,
                            labelColor = NexaTextSecondary
                        )
                    )
                }
            }
        }

        // Posts
        if (filteredPosts.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No hay publicaciones disponibles en esta sección.", color = NexaTextMuted, fontSize = 14.sp)
                }
            }
        } else {
            items(filteredPosts, key = { it.id }) { post ->
                val reactions by repository.getReactions("POST", post.id).collectAsState(initial = emptyList())
                val comments by repository.getComments(post.id).collectAsState(initial = emptyList())

                PostCard(
                    post = post,
                    currentUserId = currentUser?.id ?: 1L,
                    reactions = reactions,
                    comments = comments,
                    onReactionClick = { rType ->
                        coroutineScope.launch {
                            repository.toggleReaction("POST", post.id, currentUser?.id ?: 1L, rType)
                        }
                    },
                    onVotePoll = { optionId ->
                        coroutineScope.launch {
                            repository.votePoll(post.id, optionId)
                        }
                    },
                    onAddComment = { postId, text, parentId ->
                        coroutineScope.launch {
                            repository.addComment(postId, currentUser?.id ?: 1L, text, parentId)
                        }
                    },
                    onDeletePost = {
                        coroutineScope.launch {
                            repository.deletePost(post.id)
                        }
                    },
                    onHidePost = {
                        coroutineScope.launch {
                            repository.hidePost(post.id)
                        }
                    },
                    onReportPost = { reason ->
                        coroutineScope.launch {
                            repository.submitReport(
                                reporterId = currentUser?.id ?: 1L,
                                targetType = "POST",
                                targetId = post.id,
                                targetPreview = post.content,
                                reason = reason
                            )
                        }
                    },
                    onSendGift = { giftName, giftEmoji, cost ->
                        coroutineScope.launch {
                            repository.sendGift(
                                fromUserId = currentUser?.id ?: 1L,
                                toUserId = post.userId,
                                postId = post.id,
                                giftName = giftName,
                                giftEmoji = giftEmoji,
                                coinCost = cost
                            )
                        }
                    },
                    onAuthorClick = {
                        onOpenProfile(post.userId)
                    }
                )
            }
        }
    }
}
