package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.entity.StoryEntity
import com.example.data.local.entity.UserEntity
import com.example.ui.theme.*

@Composable
fun StoryBar(
    currentUser: UserEntity?,
    stories: List<StoryEntity>,
    onAddStoryClick: () -> Unit,
    onStoryClick: (StoryEntity) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .testTag("stories_row"),
        contentPadding = PaddingValues(horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // "Add Story" Card
        item {
            AddStoryCard(currentUser = currentUser, onClick = onAddStoryClick)
        }

        // Active stories
        items(stories, key = { it.id }) { story ->
            StoryItemCard(story = story, onClick = { onStoryClick(story) })
        }
    }
}

@Composable
fun AddStoryCard(
    currentUser: UserEntity?,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(100.dp)
            .height(150.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(NexaSurfaceElevated)
            .border(1.dp, NexaBorder, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .testTag("add_story_card")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.65f)
            ) {
                AsyncImage(
                    model = currentUser?.avatarUrl?.ifBlank { "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=400&q=80" },
                    contentDescription = "Tu historia",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.35f)
                    .background(NexaSurface),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Crear historia",
                    color = NexaTextPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }
        }

        // Add Button Plus Circle overlapping
        Box(
            modifier = Modifier
                .size(28.dp)
                .align(Alignment.Center)
                .offset(y = 12.dp)
                .clip(CircleShape)
                .background(NexaGradientPrimary),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Agregar historia",
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun StoryItemCard(
    story: StoryEntity,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(100.dp)
            .height(150.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(2.dp, NexaGradientStory, RoundedCornerShape(16.dp))
            .background(
                when (story.gradientIndex % 3) {
                    1 -> NexaGradientCard
                    2 -> NexaGradientButton
                    else -> NexaGradientPrimary
                }
            )
            .clickable(onClick = onClick)
            .testTag("story_card_${story.id}")
    ) {
        if (!story.mediaUrl.isNullOrBlank()) {
            AsyncImage(
                model = story.mediaUrl,
                contentDescription = "Historia de ${story.authorName}",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Gradient overlay for text contrast
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.75f))
                    )
                )
        )

        // Avatar ring at top-left
        Box(
            modifier = Modifier
                .padding(8.dp)
                .size(32.dp)
                .clip(CircleShape)
                .border(2.dp, NexaCyan, CircleShape)
                .align(Alignment.TopStart)
        ) {
            AsyncImage(
                model = story.authorAvatar.ifBlank { "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=400&q=80" },
                contentDescription = story.authorName,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Text / Author name at bottom
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(8.dp)
        ) {
            if (story.textCaption.isNotBlank()) {
                Text(
                    text = story.textCaption,
                    color = Color.White,
                    fontSize = 11.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Text(
                text = story.authorName,
                color = NexaCyanLight,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
