package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.SocialDao
import com.example.data.local.entity.*

@Database(
    entities = [
        UserEntity::class,
        VerificationEntity::class,
        PostEntity::class,
        CommentEntity::class,
        ReactionEntity::class,
        StoryEntity::class,
        ConversationEntity::class,
        MessageEntity::class,
        FriendshipEntity::class,
        FollowerEntity::class,
        NotificationEntity::class,
        GroupEntity::class,
        GroupMemberEntity::class,
        PageEntity::class,
        ReportEntity::class,
        GiftCodeEntity::class,
        GiftCodeRedemptionEntity::class,
        SecurityLogEntity::class,
        EmailVerificationCodeEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class NexaDatabase : RoomDatabase() {
    abstract fun socialDao(): SocialDao

    companion object {
        @Volatile
        private var INSTANCE: NexaDatabase? = null

        fun getInstance(context: Context): NexaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NexaDatabase::class.java,
                    "nexa_social.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
