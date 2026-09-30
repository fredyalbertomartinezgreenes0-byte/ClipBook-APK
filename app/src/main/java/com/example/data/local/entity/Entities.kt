package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [Index(value = ["email"], unique = true), Index(value = ["username"], unique = true)]
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val username: String,
    val email: String,
    val passwordHash: String,
    val passwordSalt: String,
    val birthDate: String = "",
    val avatarUrl: String = "",
    val coverUrl: String = "",
    val bio: String = "",
    val isEmailVerified: Boolean = false,
    val role: String = "USER", // "CREATOR", "ADMIN", "USER"
    val isSuspended: Boolean = false,
    val coins: Long = 1000L,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "verification",
    indices = [Index(value = ["userId"])]
)
data class VerificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val verificationType: String, // "CREATOR", "PURCHASED", "GIFT_CODE"
    val internalStatus: String = "ACTIVE", // "ACTIVE", "EXPIRED", "REVOKED"
    val internalStartDate: Long = System.currentTimeMillis(),
    val internalExpiresAt: Long? = null // null if permanent (e.g. CREATOR), or millis for 24h
)

@Entity(
    tableName = "posts",
    indices = [Index(value = ["userId"]), Index(value = ["groupId"]), Index(value = ["pageId"])]
)
data class PostEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val authorName: String,
    val authorUsername: String,
    val authorAvatar: String = "",
    val isAuthorVerified: Boolean = false,
    val content: String,
    val imageUrl: String? = null,
    val videoUrl: String? = null,
    val linkUrl: String? = null,
    val pollQuestion: String? = null,
    val pollOptions: String? = null, // JSON: e.g. [{"id":0,"text":"Opción 1","votes":2}]
    val groupId: Long? = null,
    val pageId: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val isReported: Boolean = false,
    val isHidden: Boolean = false,
    val shareCount: Int = 0
)

@Entity(
    tableName = "comments",
    indices = [Index(value = ["postId"]), Index(value = ["userId"])]
)
data class CommentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val postId: Long,
    val userId: Long,
    val authorName: String,
    val authorUsername: String,
    val authorAvatar: String = "",
    val isAuthorVerified: Boolean = false,
    val parentCommentId: Long? = null,
    val content: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "reactions",
    indices = [Index(value = ["targetType", "targetId", "userId"], unique = true)]
)
data class ReactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val targetType: String, // "POST", "COMMENT"
    val targetId: Long,
    val userId: Long,
    val reactionType: String, // "LIKE", "LOVE", "LAUGH", "WOW", "SAD", "FIRE"
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "stories",
    indices = [Index(value = ["userId"])]
)
data class StoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val authorName: String,
    val authorAvatar: String = "",
    val isAuthorVerified: Boolean = false,
    val mediaUrl: String? = null,
    val textCaption: String = "",
    val gradientIndex: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val expiresAt: Long = System.currentTimeMillis() + 24 * 60 * 60 * 1000,
    val viewsCount: Int = 0
)

@Entity(
    tableName = "conversations",
    indices = [Index(value = ["user1Id", "user2Id"])]
)
data class ConversationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val user1Id: Long,
    val user2Id: Long,
    val lastMessage: String = "",
    val lastMessageTime: Long = System.currentTimeMillis(),
    val isBlocked: Boolean = false,
    val blockedByUserId: Long? = null
)

@Entity(
    tableName = "messages",
    indices = [Index(value = ["conversationId"]), Index(value = ["senderId"])]
)
data class MessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val conversationId: Long,
    val senderId: Long,
    val receiverId: Long,
    val text: String,
    val mediaUrl: String? = null,
    val isSent: Boolean = true,
    val isRead: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "friendships",
    indices = [Index(value = ["senderId", "receiverId"], unique = true)]
)
data class FriendshipEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val senderId: Long,
    val receiverId: Long,
    val status: String = "PENDING", // "PENDING", "ACCEPTED", "REJECTED"
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "followers",
    indices = [Index(value = ["followerId", "followingId", "targetType"], unique = true)]
)
data class FollowerEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val followerId: Long,
    val followingId: Long,
    val targetType: String = "USER", // "USER", "PAGE"
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "notifications",
    indices = [Index(value = ["userId"])]
)
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val senderId: Long? = null,
    val senderName: String = "",
    val senderAvatar: String = "",
    val title: String,
    val message: String,
    val type: String, // "FRIEND_REQ", "FOLLOW", "REACTION", "COMMENT", "MESSAGE", "VERIFICATION", "SECURITY"
    val isRead: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "groups")
data class GroupEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String,
    val isPrivate: Boolean = false,
    val creatorId: Long,
    val coverUrl: String = "",
    val rules: String = "1. Respeto mutuo.\n2. No spam.\n3. Contenido verificado.",
    val memberCount: Int = 1
)

@Entity(
    tableName = "group_members",
    indices = [Index(value = ["groupId", "userId"], unique = true)]
)
data class GroupMemberEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val groupId: Long,
    val userId: Long,
    val role: String = "MEMBER", // "ADMIN", "MODERATOR", "MEMBER"
    val status: String = "ACTIVE", // "ACTIVE", "PENDING"
    val joinedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "pages")
data class PageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val category: String, // "Creador", "Empresa", "Comunidad", "Proyecto", "Marca"
    val description: String,
    val avatarUrl: String = "",
    val coverUrl: String = "",
    val contactInfo: String = "",
    val ownerId: Long,
    val followersCount: Int = 0
)

@Entity(tableName = "reports")
data class ReportEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val reporterUserId: Long,
    val targetType: String, // "POST", "COMMENT", "USER", "MESSAGE", "GROUP"
    val targetId: Long,
    val targetPreview: String = "",
    val reason: String, // "Acoso", "Amenazas", "Spam", "Fraude", "Suplantación", "Contenido ilegal", "Malware/Phishing"
    val description: String = "",
    val status: String = "PENDING", // "PENDING", "RESOLVED", "DISMISSED"
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "gift_codes",
    indices = [Index(value = ["code"], unique = true)]
)
data class GiftCodeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val code: String, // Format: NEXA-XXXX-XXXX-XXXX
    val rewardType: String = "VERIFICATION_24H",
    val isUsed: Boolean = false,
    val isBlocked: Boolean = false,
    val createdByAdminId: Long = 1,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "gift_code_redemptions",
    indices = [Index(value = ["code"]), Index(value = ["userId"])]
)
data class GiftCodeRedemptionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val giftCodeId: Long,
    val code: String,
    val userId: Long,
    val redeemedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "security_logs")
data class SecurityLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long? = null,
    val eventType: String, // "LOGIN_SUCCESS", "LOGIN_FAILED", "RATE_LIMIT", "CODE_REDEEM", "REPORT_FILED", "USER_SUSPENDED"
    val details: String,
    val ipAddress: String = "127.0.0.1",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "email_verifications",
    indices = [Index(value = ["email"])]
)
data class EmailVerificationCodeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val email: String,
    val codeHash: String,
    val expiresAt: Long,
    val attemptsCount: Int = 0,
    val lastSentAt: Long = System.currentTimeMillis(),
    val isUsed: Boolean = false
)
