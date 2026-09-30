package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SocialDao {

    // USERS
    @Query("SELECT * FROM users WHERE id = :id")
    fun getUserById(id: Long): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUserByIdDirect(id: Long): UserEntity?

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): UserEntity?

    @Query("SELECT * FROM users ORDER BY id DESC")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE name LIKE '%' || :query || '%' OR username LIKE '%' || :query || '%'")
    fun searchUsers(query: String): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity): Long

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("UPDATE users SET isSuspended = :suspended WHERE id = :userId")
    suspend fun suspendUser(userId: Long, suspended: Boolean)

    @Query("UPDATE users SET coins = coins + :amount WHERE id = :userId")
    suspend fun addCoins(userId: Long, amount: Long)

    @Query("UPDATE users SET coins = :coins WHERE id = :userId")
    suspend fun setCoins(userId: Long, coins: Long)

    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deleteUser(userId: Long)

    // VERIFICATION
    @Query("SELECT * FROM verification WHERE userId = :userId AND internalStatus = 'ACTIVE' LIMIT 1")
    fun getActiveVerification(userId: Long): Flow<VerificationEntity?>

    @Query("SELECT * FROM verification WHERE userId = :userId AND internalStatus = 'ACTIVE' LIMIT 1")
    suspend fun getActiveVerificationDirect(userId: Long): VerificationEntity?

    @Query("SELECT * FROM verification ORDER BY id DESC")
    fun getAllVerifications(): Flow<List<VerificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVerification(verification: VerificationEntity): Long

    @Update
    suspend fun updateVerification(verification: VerificationEntity)

    @Query("UPDATE verification SET internalStatus = 'EXPIRED' WHERE internalExpiresAt IS NOT NULL AND internalExpiresAt < :now")
    suspend fun expireOldVerifications(now: Long)

    @Query("UPDATE verification SET internalStatus = 'REVOKED' WHERE userId = :userId")
    suspend fun revokeVerification(userId: Long)

    // POSTS
    @Query("SELECT * FROM posts WHERE isHidden = 0 ORDER BY createdAt DESC")
    fun getAllFeedPosts(): Flow<List<PostEntity>>

    @Query("SELECT * FROM posts WHERE userId = :userId AND isHidden = 0 ORDER BY createdAt DESC")
    fun getUserPosts(userId: Long): Flow<List<PostEntity>>

    @Query("SELECT * FROM posts WHERE groupId = :groupId AND isHidden = 0 ORDER BY createdAt DESC")
    fun getGroupPosts(groupId: Long): Flow<List<PostEntity>>

    @Query("SELECT * FROM posts WHERE pageId = :pageId AND isHidden = 0 ORDER BY createdAt DESC")
    fun getPagePosts(pageId: Long): Flow<List<PostEntity>>

    @Query("SELECT * FROM posts WHERE content LIKE '%' || :query || '%' AND isHidden = 0 ORDER BY createdAt DESC")
    fun searchPosts(query: String): Flow<List<PostEntity>>

    @Query("SELECT * FROM posts WHERE id = :postId")
    fun getPostById(postId: Long): Flow<PostEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: PostEntity): Long

    @Update
    suspend fun updatePost(post: PostEntity)

    @Query("DELETE FROM posts WHERE id = :postId")
    suspend fun deletePost(postId: Long)

    @Query("UPDATE posts SET isHidden = :hidden WHERE id = :postId")
    suspend fun hidePost(postId: Long, hidden: Boolean)

    @Query("UPDATE posts SET isReported = 1 WHERE id = :postId")
    suspend fun reportPost(postId: Long)

    // COMMENTS
    @Query("SELECT * FROM comments WHERE postId = :postId ORDER BY createdAt ASC")
    fun getCommentsForPost(postId: Long): Flow<List<CommentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: CommentEntity): Long

    @Query("DELETE FROM comments WHERE id = :commentId")
    suspend fun deleteComment(commentId: Long)

    // REACTIONS
    @Query("SELECT * FROM reactions WHERE targetType = :targetType AND targetId = :targetId")
    fun getReactionsForTarget(targetType: String, targetId: Long): Flow<List<ReactionEntity>>

    @Query("SELECT * FROM reactions WHERE targetType = :targetType AND targetId = :targetId AND userId = :userId LIMIT 1")
    suspend fun getUserReaction(targetType: String, targetId: Long, userId: Long): ReactionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReaction(reaction: ReactionEntity): Long

    @Query("DELETE FROM reactions WHERE targetType = :targetType AND targetId = :targetId AND userId = :userId")
    suspend fun deleteReaction(targetType: String, targetId: Long, userId: Long)

    // STORIES
    @Query("SELECT * FROM stories WHERE expiresAt > :now ORDER BY createdAt DESC")
    fun getActiveStories(now: Long): Flow<List<StoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStory(story: StoryEntity): Long

    @Query("UPDATE stories SET viewsCount = viewsCount + 1 WHERE id = :storyId")
    suspend fun incrementStoryViews(storyId: Long)

    @Query("DELETE FROM stories WHERE id = :storyId")
    suspend fun deleteStory(storyId: Long)

    // CONVERSATIONS & MESSAGES
    @Query("SELECT * FROM conversations WHERE user1Id = :userId OR user2Id = :userId ORDER BY lastMessageTime DESC")
    fun getUserConversations(userId: Long): Flow<List<ConversationEntity>>

    @Query("SELECT * FROM conversations WHERE (user1Id = :u1 AND user2Id = :u2) OR (user1Id = :u2 AND user2Id = :u1) LIMIT 1")
    suspend fun getConversationBetween(u1: Long, u2: Long): ConversationEntity?

    @Query("SELECT * FROM conversations WHERE id = :id")
    fun getConversationById(id: Long): Flow<ConversationEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversation(conversation: ConversationEntity): Long

    @Update
    suspend fun updateConversation(conversation: ConversationEntity)

    @Query("DELETE FROM conversations WHERE id = :id")
    suspend fun deleteConversation(id: Long)

    @Query("SELECT * FROM messages WHERE conversationId = :conversationId ORDER BY timestamp ASC")
    fun getMessages(conversationId: Long): Flow<List<MessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity): Long

    @Query("UPDATE messages SET isRead = 1 WHERE conversationId = :conversationId AND receiverId = :userId")
    suspend fun markMessagesAsRead(conversationId: Long, userId: Long)

    // FRIENDSHIPS & FOLLOWERS
    @Query("SELECT * FROM friendships WHERE (senderId = :userId OR receiverId = :userId) AND status = 'ACCEPTED'")
    fun getFriendshipsForUser(userId: Long): Flow<List<FriendshipEntity>>

    @Query("SELECT * FROM friendships WHERE receiverId = :userId AND status = 'PENDING'")
    fun getPendingFriendRequests(userId: Long): Flow<List<FriendshipEntity>>

    @Query("SELECT * FROM friendships WHERE (senderId = :u1 AND receiverId = :u2) OR (senderId = :u2 AND receiverId = :u1) LIMIT 1")
    suspend fun getFriendship(u1: Long, u2: Long): FriendshipEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFriendship(friendship: FriendshipEntity): Long

    @Update
    suspend fun updateFriendship(friendship: FriendshipEntity)

    @Query("DELETE FROM friendships WHERE (senderId = :u1 AND receiverId = :u2) OR (senderId = :u2 AND receiverId = :u1)")
    suspend fun deleteFriendship(u1: Long, u2: Long)

    @Query("SELECT * FROM followers WHERE followingId = :followingId AND targetType = :targetType")
    fun getFollowers(followingId: Long, targetType: String): Flow<List<FollowerEntity>>

    @Query("SELECT * FROM followers WHERE followerId = :followerId AND targetType = :targetType")
    fun getFollowing(followerId: Long, targetType: String): Flow<List<FollowerEntity>>

    @Query("SELECT * FROM followers WHERE followerId = :followerId AND followingId = :followingId AND targetType = :targetType LIMIT 1")
    suspend fun isFollowing(followerId: Long, followingId: Long, targetType: String): FollowerEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFollower(follower: FollowerEntity): Long

    @Query("DELETE FROM followers WHERE followerId = :followerId AND followingId = :followingId AND targetType = :targetType")
    suspend fun deleteFollower(followerId: Long, followingId: Long, targetType: String)

    // NOTIFICATIONS
    @Query("SELECT * FROM notifications WHERE userId = :userId ORDER BY createdAt DESC")
    fun getUserNotifications(userId: Long): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity): Long

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markNotificationAsRead(id: Long)

    @Query("UPDATE notifications SET isRead = 1 WHERE userId = :userId")
    suspend fun markAllNotificationsAsRead(userId: Long)

    // GROUPS
    @Query("SELECT * FROM groups ORDER BY id DESC")
    fun getAllGroups(): Flow<List<GroupEntity>>

    @Query("SELECT * FROM groups WHERE id = :groupId")
    fun getGroupById(groupId: Long): Flow<GroupEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroup(group: GroupEntity): Long

    @Query("SELECT * FROM group_members WHERE groupId = :groupId")
    fun getGroupMembers(groupId: Long): Flow<List<GroupMemberEntity>>

    @Query("SELECT * FROM group_members WHERE groupId = :groupId AND userId = :userId LIMIT 1")
    suspend fun getGroupMember(groupId: Long, userId: Long): GroupMemberEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroupMember(member: GroupMemberEntity): Long

    @Query("DELETE FROM group_members WHERE groupId = :groupId AND userId = :userId")
    suspend fun removeGroupMember(groupId: Long, userId: Long)

    // PAGES
    @Query("SELECT * FROM pages ORDER BY id DESC")
    fun getAllPages(): Flow<List<PageEntity>>

    @Query("SELECT * FROM pages WHERE id = :pageId")
    fun getPageById(pageId: Long): Flow<PageEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPage(page: PageEntity): Long

    // REPORTS
    @Query("SELECT * FROM reports ORDER BY createdAt DESC")
    fun getAllReports(): Flow<List<ReportEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: ReportEntity): Long

    @Query("UPDATE reports SET status = :status WHERE id = :id")
    suspend fun updateReportStatus(id: Long, status: String)

    // GIFT CODES & REDEMPTIONS
    @Query("SELECT * FROM gift_codes WHERE code = :code LIMIT 1")
    suspend fun getGiftCode(code: String): GiftCodeEntity?

    @Query("SELECT * FROM gift_codes ORDER BY id DESC")
    fun getAllGiftCodes(): Flow<List<GiftCodeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGiftCode(giftCode: GiftCodeEntity): Long

    @Update
    suspend fun updateGiftCode(giftCode: GiftCodeEntity)

    @Query("UPDATE gift_codes SET isBlocked = :blocked WHERE id = :id")
    suspend fun setGiftCodeBlocked(id: Long, blocked: Boolean)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRedemption(redemption: GiftCodeRedemptionEntity): Long

    @Query("SELECT * FROM gift_code_redemptions ORDER BY redeemedAt DESC")
    fun getAllRedemptions(): Flow<List<GiftCodeRedemptionEntity>>

    // SECURITY LOGS
    @Query("SELECT * FROM security_logs ORDER BY timestamp DESC LIMIT 100")
    fun getAllSecurityLogs(): Flow<List<SecurityLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSecurityLog(log: SecurityLogEntity): Long

    // EMAIL VERIFICATIONS
    @Query("SELECT * FROM email_verifications WHERE email = :email ORDER BY id DESC LIMIT 1")
    suspend fun getLatestEmailVerification(email: String): EmailVerificationCodeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmailVerification(code: EmailVerificationCodeEntity): Long

    @Update
    suspend fun updateEmailVerification(code: EmailVerificationCodeEntity)
}
