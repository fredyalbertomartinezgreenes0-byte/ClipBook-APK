package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.entity.CommentEntity
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentsModalSheet(
    postId: Long,
    comments: List<CommentEntity>,
    onDismiss: () -> Unit,
    onAddComment: (postId: Long, text: String, parentId: Long?) -> Unit
) {
    var newCommentText by remember { mutableStateOf("") }
    var replyingToComment by remember { mutableStateOf<CommentEntity?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = NexaSurface,
        scrimColor = Color.Black.copy(alpha = 0.6f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .navigationBarsPadding()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Comentarios (${comments.size})",
                    color = NexaTextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = NexaTextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (comments.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Sé el primero en comentar.",
                        color = NexaTextMuted,
                        fontSize = 14.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 340.dp)
                ) {
                    // Filter top level comments
                    val topComments = comments.filter { it.parentCommentId == null }
                    items(topComments, key = { it.id }) { comment ->
                        CommentItem(
                            comment = comment,
                            onReplyClick = { replyingToComment = comment }
                        )

                        // Render replies
                        val replies = comments.filter { it.parentCommentId == comment.id }
                        replies.forEach { reply ->
                            CommentItem(
                                comment = reply,
                                isReply = true,
                                onReplyClick = { replyingToComment = comment }
                            )
                        }
                    }
                }
            }

            // Replying banner
            replyingToComment?.let { parent ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(NexaSurfaceElevated)
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Respondiendo a @${parent.authorUsername}",
                        color = NexaCyan,
                        fontSize = 12.sp
                    )
                    IconButton(
                        onClick = { replyingToComment = null },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = null, tint = NexaTextSecondary, modifier = Modifier.size(16.dp))
                    }
                }
            }

            // Input field row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = newCommentText,
                    onValueChange = { newCommentText = it },
                    placeholder = {
                        Text(
                            if (replyingToComment != null) "Escribe tu respuesta..." else "Escribe un comentario...",
                            color = NexaTextMuted,
                            fontSize = 14.sp
                        )
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = NexaSurfaceElevated,
                        unfocusedContainerColor = NexaSurfaceElevated,
                        focusedBorderColor = NexaCyan,
                        unfocusedBorderColor = NexaBorder,
                        focusedTextColor = NexaTextPrimary,
                        unfocusedTextColor = NexaTextPrimary
                    )
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        if (newCommentText.isNotBlank()) {
                            onAddComment(postId, newCommentText, replyingToComment?.id)
                            newCommentText = ""
                            replyingToComment = null
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
}

@Composable
fun CommentItem(
    comment: CommentEntity,
    isReply: Boolean = false,
    onReplyClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = if (isReply) 32.dp else 0.dp, top = 6.dp, bottom = 6.dp)
    ) {
        AsyncImage(
            model = comment.authorAvatar.ifBlank { "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=400&q=80" },
            contentDescription = comment.authorName,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(if (isReply) 28.dp else 34.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .background(NexaSurfaceElevated)
                .padding(10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = comment.authorName,
                    color = NexaTextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                if (comment.isAuthorVerified) {
                    Spacer(modifier = Modifier.width(4.dp))
                    VerifiedBadgeIcon(size = 12)
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = formatTime(comment.createdAt),
                    color = NexaTextMuted,
                    fontSize = 11.sp
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = comment.content,
                color = NexaTextPrimary,
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Responder",
                color = NexaCyan,
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp,
                modifier = Modifier.clickable(onClick = onReplyClick)
            )
        }
    }
}
