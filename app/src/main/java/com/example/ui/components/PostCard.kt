package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.entity.CommentEntity
import com.example.data.local.entity.PostEntity
import com.example.data.local.entity.ReactionEntity
import com.example.ui.theme.*
import org.json.JSONArray

@Composable
fun PostCard(
    post: PostEntity,
    currentUserId: Long,
    reactions: List<ReactionEntity>,
    comments: List<CommentEntity>,
    onReactionClick: (String) -> Unit,
    onVotePoll: (Int) -> Unit,
    onAddComment: (postId: Long, text: String, parentId: Long?) -> Unit,
    onDeletePost: () -> Unit,
    onHidePost: () -> Unit,
    onReportPost: (reason: String) -> Unit,
    onSendGift: (giftName: String, giftEmoji: String, coinCost: Long) -> Unit = { _, _, _ -> },
    onAuthorClick: () -> Unit = {}
) {
    var showMenu by remember { mutableStateOf(false) }
    var showCommentsSheet by remember { mutableStateOf(false) }
    var showReactionPicker by remember { mutableStateOf(false) }
    var showReportDialog by remember { mutableStateOf(false) }
    var showGiftDialog by remember { mutableStateOf(false) }

    val userReaction = reactions.find { it.userId == currentUserId }
    val isMyPost = post.userId == currentUserId

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .testTag("post_card_${post.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = NexaSurface),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.verticalGradient(listOf(NexaBorder, NexaBorderSubtle)))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: Author, Verification Badge, Timestamp & 3-dot Menu
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(NexaSurfaceElevated)
                        .clickable { onAuthorClick() }
                ) {
                    if (post.authorAvatar.isNotBlank()) {
                        AsyncImage(
                            model = post.authorAvatar,
                            contentDescription = post.authorName,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = post.authorName,
                            tint = NexaCyan,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onAuthorClick() }
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = post.authorName,
                            color = NexaTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (post.isAuthorVerified) {
                            Spacer(modifier = Modifier.width(5.dp))
                            VerifiedBadgeIcon(size = 14)
                        }
                    }
                    Text(
                        text = "@${post.authorUsername} • ${formatTime(post.createdAt)}",
                        color = NexaTextSecondary,
                        fontSize = 12.sp
                    )
                }

                // Menu
                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Opciones",
                            tint = NexaTextSecondary
                        )
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                        modifier = Modifier.background(NexaSurfaceElevated)
                    ) {
                        if (isMyPost) {
                            DropdownMenuItem(
                                text = { Text("Eliminar publicación", color = NexaError) },
                                leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = NexaError) },
                                onClick = {
                                    showMenu = false
                                    onDeletePost()
                                }
                            )
                        }
                        DropdownMenuItem(
                            text = { Text("Ocultar", color = NexaTextPrimary) },
                            leadingIcon = { Icon(Icons.Default.VisibilityOff, contentDescription = null, tint = NexaTextSecondary) },
                            onClick = {
                                showMenu = false
                                onHidePost()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Denunciar publicación", color = NexaWarning) },
                            leadingIcon = { Icon(Icons.Default.Flag, contentDescription = null, tint = NexaWarning) },
                            onClick = {
                                showMenu = false
                                showReportDialog = true
                            }
                        )
                    }
                }
            }

            // Post Content Text
            if (post.content.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = post.content,
                    color = NexaTextPrimary,
                    fontSize = 15.sp,
                    lineHeight = 22.sp
                )
            }

            // Post Image
            if (!post.imageUrl.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                AsyncImage(
                    model = post.imageUrl,
                    contentDescription = "Imagen de la publicación",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .clip(RoundedCornerShape(14.dp))
                )
            }

            // Post Video Clip
            if (!post.videoUrl.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                var isPlaying by remember { mutableStateOf(false) }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF0F172A))
                        .clickable { isPlaying = !isPlaying },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Surface(
                            shape = CircleShape,
                            color = if (isPlaying) NexaSuccess.copy(alpha = 0.9f) else NexaCyan.copy(alpha = 0.9f),
                            modifier = Modifier.size(54.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = if (isPlaying) "Pausar" else "Reproducir",
                                    tint = Color.Black,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (isPlaying) "Reproduciendo video clip... ▶" else "Toca para reproducir clip",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }

                    Surface(
                        color = Color.Black.copy(alpha = 0.7f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.align(Alignment.TopStart).padding(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
                            Icon(Icons.Default.Videocam, contentDescription = null, tint = NexaGold, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("CLIP • HD", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (isPlaying) {
                        LinearProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomCenter)
                                .height(4.dp),
                            color = NexaCyan,
                            trackColor = Color.White.copy(alpha = 0.3f)
                        )
                    }
                }
            }

            // Interactive Poll (Encuesta)
            if (!post.pollQuestion.isNullOrBlank() && !post.pollOptions.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                PollView(
                    question = post.pollQuestion,
                    optionsJson = post.pollOptions,
                    onVote = onVotePoll
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Reaction bar / Counter row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (reactions.isNotEmpty()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = getReactionsSummaryEmojis(reactions),
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${reactions.size}",
                            color = NexaTextSecondary,
                            fontSize = 13.sp
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (comments.isNotEmpty()) {
                        Text(
                            text = "${comments.size} comentarios",
                            color = NexaTextSecondary,
                            fontSize = 12.sp,
                            modifier = Modifier.clickable { showCommentsSheet = true }
                        )
                    }
                    if (post.shareCount > 0) {
                        Text(
                            text = "${post.shareCount} compartidos",
                            color = NexaTextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = NexaBorderSubtle
            )

            // Reactions floating picker
            AnimatedVisibility(visible = showReactionPicker) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(NexaSurfaceHighlight)
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ReactionChoice("👍", "LIKE") {
                        onReactionClick("LIKE")
                        showReactionPicker = false
                    }
                    ReactionChoice("❤️", "LOVE") {
                        onReactionClick("LOVE")
                        showReactionPicker = false
                    }
                    ReactionChoice("😂", "LAUGH") {
                        onReactionClick("LAUGH")
                        showReactionPicker = false
                    }
                    ReactionChoice("😮", "WOW") {
                        onReactionClick("WOW")
                        showReactionPicker = false
                    }
                    ReactionChoice("😢", "SAD") {
                        onReactionClick("SAD")
                        showReactionPicker = false
                    }
                    ReactionChoice("🔥", "FIRE") {
                        onReactionClick("FIRE")
                        showReactionPicker = false
                    }
                }
            }

            // Action Buttons: Reaccionar, Comentar, Compartir
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Reaction Action Button
                val activeReactionEmoji = when (userReaction?.reactionType) {
                    "LOVE" -> "❤️ Me encanta"
                    "LAUGH" -> "😂 Me divierte"
                    "WOW" -> "😮 Me asombra"
                    "SAD" -> "😢 Me entristece"
                    "FIRE" -> "🔥 Fuego"
                    "LIKE" -> "👍 Me gusta"
                    else -> "👍 Reaccionar"
                }
                val activeColor = if (userReaction != null) NexaCyan else NexaTextSecondary

                TextButton(
                    onClick = { showReactionPicker = !showReactionPicker },
                    modifier = Modifier.testTag("reaction_button_${post.id}")
                ) {
                    Text(
                        text = activeReactionEmoji,
                        color = activeColor,
                        fontWeight = if (userReaction != null) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 13.sp
                    )
                }

                // Comment Button
                TextButton(
                    onClick = { showCommentsSheet = true },
                    modifier = Modifier.testTag("comment_button_${post.id}")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ChatBubbleOutline,
                        contentDescription = "Comentar",
                        tint = NexaTextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Comentar", color = NexaTextSecondary, fontSize = 13.sp)
                }

                // Share Button
                TextButton(
                    onClick = { /* Share simulated */ },
                    modifier = Modifier.testTag("share_button_${post.id}")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Share,
                        contentDescription = "Compartir",
                        tint = NexaTextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Compartir", color = NexaTextSecondary, fontSize = 13.sp)
                }

                // Gift Button (ClipCoins)
                TextButton(
                    onClick = { showGiftDialog = true },
                    modifier = Modifier.testTag("gift_button_${post.id}")
                ) {
                    Text("🎁 Regalar", color = NexaGold, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }

    // Gift Picker Dialog
    if (showGiftDialog) {
        AlertDialog(
            onDismissRequest = { showGiftDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🎁 Enviar Regalo ClipCoins", color = NexaTextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                Column {
                    Text("Elige un regalo para @${post.authorUsername}:", color = NexaTextSecondary, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(14.dp))

                    val gifts = listOf(
                        Triple("Café Virtual", "☕", 50L),
                        Triple("Estrella Clip", "⭐", 100L),
                        Triple("Cohete VIP", "🚀", 500L),
                        Triple("Corona Creador", "👑", 1000L)
                    )

                    gifts.forEach { (name, emoji, cost) ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    onSendGift(name, emoji, cost)
                                    showGiftDialog = false
                                },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = NexaSurfaceElevated)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(emoji, fontSize = 24.sp)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(name, color = NexaTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = NexaGold.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = "$cost 🪙",
                                        color = NexaGold,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showGiftDialog = false }) {
                    Text("Cerrar", color = NexaCyan)
                }
            },
            containerColor = NexaSurface
        )
    }

    // Comments Sheet
    if (showCommentsSheet) {
        CommentsModalSheet(
            postId = post.id,
            comments = comments,
            onDismiss = { showCommentsSheet = false },
            onAddComment = onAddComment
        )
    }

    // Report Dialog
    if (showReportDialog) {
        ReportDialog(
            targetTitle = "Publicación de @${post.authorUsername}",
            onDismiss = { showReportDialog = false },
            onSubmitReport = { reason, _ ->
                onReportPost(reason)
                showReportDialog = false
            }
        )
    }
}

@Composable
fun ReactionChoice(emoji: String, type: String, onClick: () -> Unit) {
    Text(
        text = emoji,
        fontSize = 24.sp,
        modifier = Modifier
            .clip(CircleShape)
            .clickable(onClick = onClick)
            .padding(6.dp)
    )
}

@Composable
fun PollView(
    question: String,
    optionsJson: String,
    onVote: (Int) -> Unit
) {
    var hasVoted by remember { mutableStateOf(false) }

    val options = remember(optionsJson) {
        try {
            val arr = JSONArray(optionsJson)
            (0 until arr.length()).map { i ->
                val obj = arr.getJSONObject(i)
                PollOption(
                    id = obj.getInt("id"),
                    text = obj.getString("text"),
                    votes = obj.getInt("votes")
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    val totalVotes = options.sumOf { it.votes }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(NexaSurfaceElevated)
            .border(1.dp, NexaBorder, RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Poll, contentDescription = null, tint = NexaCyan, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = question,
                color = NexaTextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }
        Spacer(modifier = Modifier.height(10.dp))

        options.forEach { opt ->
            val percentage = if (totalVotes > 0) (opt.votes * 100 / totalVotes) else 0
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(NexaSurface)
                    .clickable {
                        if (!hasVoted) {
                            hasVoted = true
                            onVote(opt.id)
                        }
                    }
                    .padding(10.dp)
            ) {
                // Progress background
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(if (totalVotes > 0) (percentage / 100f) else 0f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(NexaCyan.copy(alpha = 0.2f))
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = opt.text,
                        color = NexaTextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "$percentage% (${opt.votes})",
                        color = NexaTextSecondary,
                        fontSize = 12.sp
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "$totalVotes votos en total",
            color = NexaTextMuted,
            fontSize = 11.sp
        )
    }
}

data class PollOption(val id: Int, val text: String, val votes: Int)

fun getReactionsSummaryEmojis(reactions: List<ReactionEntity>): String {
    val uniqueTypes = reactions.map { it.reactionType }.distinct().take(3)
    return uniqueTypes.joinToString(" ") {
        when (it) {
            "LOVE" -> "❤️"
            "LAUGH" -> "😂"
            "WOW" -> "😮"
            "SAD" -> "😢"
            "FIRE" -> "🔥"
            else -> "👍"
        }
    }
}

fun formatTime(timestamp: Long): String {
    val diff = (System.currentTimeMillis() - timestamp) / 1000
    return when {
        diff < 60 -> "ahora"
        diff < 3600 -> "${diff / 60}m"
        diff < 86400 -> "${diff / 3600}h"
        else -> "${diff / 86400}d"
    }
}
