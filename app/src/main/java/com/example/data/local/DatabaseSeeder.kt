package com.example.data.local

import com.example.data.local.dao.SocialDao
import com.example.data.local.entity.*
import com.example.data.security.SecurityHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object DatabaseSeeder {

    suspend fun seedIfEmpty(dao: SocialDao) = withContext(Dispatchers.IO) {
        val existingUsers = dao.getUserByIdDirect(1)
        if (existingUsers != null) {
            // Already seeded
            return@withContext
        }

        val now = System.currentTimeMillis()

        // 1. Official Creator Account (Fredy Alberto Martínez Greenes)
        val creatorSalt = SecurityHelper.generateSalt()
        val creatorHash = SecurityHelper.hashPassword("Creator2026!", creatorSalt)
        val creatorId = dao.insertUser(
            UserEntity(
                id = 1,
                name = "Fredy Alberto Martínez Greenes",
                username = "fredy",
                email = "fredyalbertomartinezgreenes0@gmail.com",
                passwordHash = creatorHash,
                passwordSalt = creatorSalt,
                birthDate = "1998-01-01",
                avatarUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=400&q=80",
                coverUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=1200&q=80",
                bio = "Creador y Fundador Oficial de ClipBook. 👑 Conectando mentes creativas con tecnología de vanguardia.",
                isEmailVerified = true,
                role = "CREATOR",
                isSuspended = false,
                coins = 999999L,
                createdAt = now - 100_000_000
            )
        )

        // Permanent free verification for Creator
        dao.insertVerification(
            VerificationEntity(
                userId = creatorId,
                verificationType = "CREATOR",
                internalStatus = "ACTIVE",
                internalStartDate = now - 100_000_000,
                internalExpiresAt = null // Permanent
            )
        )

        // 2. Sample Users
        val u2Salt = SecurityHelper.generateSalt()
        val u2Id = dao.insertUser(
            UserEntity(
                id = 2,
                name = "Elena Morales",
                username = "elena_m",
                email = "elena@clipbook.app",
                passwordHash = SecurityHelper.hashPassword("Password123!", u2Salt),
                passwordSalt = u2Salt,
                birthDate = "1998-05-14",
                avatarUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=400&q=80",
                coverUrl = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=1200&q=80",
                bio = "Fotógrafa y diseñadora UI/UX apasionada por el arte digital y la innovación.",
                isEmailVerified = true,
                role = "USER",
                createdAt = now - 80_000_000
            )
        )

        val u3Salt = SecurityHelper.generateSalt()
        val u3Id = dao.insertUser(
            UserEntity(
                id = 3,
                name = "Carlos Mendoza",
                username = "carlos_dev",
                email = "carlos@clipbook.app",
                passwordHash = SecurityHelper.hashPassword("Password123!", u3Salt),
                passwordSalt = u3Salt,
                birthDate = "1995-11-22",
                avatarUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=400&q=80",
                coverUrl = "https://images.unsplash.com/photo-1526374965328-7f61d4dc18c5?w=1200&q=80",
                bio = "Ingeniero de software. Explorando IA, Web3 y sistemas distribuidos.",
                isEmailVerified = true,
                role = "USER",
                createdAt = now - 70_000_000
            )
        )

        val u4Salt = SecurityHelper.generateSalt()
        val u4Id = dao.insertUser(
            UserEntity(
                id = 4,
                name = "Sofia Romero",
                username = "sofia_art",
                email = "sofia@clipbook.app",
                passwordHash = SecurityHelper.hashPassword("Password123!", u4Salt),
                passwordSalt = u4Salt,
                birthDate = "2000-03-08",
                avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=400&q=80",
                coverUrl = "https://images.unsplash.com/photo-1518770660439-4636190af475?w=1200&q=80",
                bio = "Ilustradora 3D y creadora de mundos virtuales.",
                isEmailVerified = true,
                role = "USER",
                createdAt = now - 60_000_000
            )
        )

        // Prepopulate friendships
        dao.insertFriendship(FriendshipEntity(senderId = creatorId, receiverId = u2Id, status = "ACCEPTED", createdAt = now - 50_000_000))
        dao.insertFriendship(FriendshipEntity(senderId = u3Id, receiverId = creatorId, status = "ACCEPTED", createdAt = now - 40_000_000))
        dao.insertFriendship(FriendshipEntity(senderId = u4Id, receiverId = creatorId, status = "PENDING", createdAt = now - 10_000_000))

        // Prepopulate posts
        val p1 = dao.insertPost(
            PostEntity(
                userId = creatorId,
                authorName = "Fredy Alberto Martínez Greenes",
                authorUsername = "fredy",
                authorAvatar = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=400&q=80",
                isAuthorVerified = true,
                content = "¡Bienvenidos a ClipBook! 🚀 Una experiencia social pensada para ser moderna, fluida y con privacidad de alto nivel. Explora historias, comparte tus momentos y disfruta de una comunidad auténtica.",
                imageUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=1000&q=80",
                createdAt = now - 3600_000 * 3
            )
        )

        val pollJson = """[{"id":0,"text":"Historias y Reacciones rápidas","votes":18},{"id":1,"text":"Insignia y Códigos de Regalo","votes":24},{"id":2,"text":"Grupos y Comunidades","votes":12},{"id":3,"text":"Mensajería Privada Segura","votes":31}]"""
        val p2 = dao.insertPost(
            PostEntity(
                userId = creatorId,
                authorName = "Fredy Alberto Martínez Greenes",
                authorUsername = "fredy",
                authorAvatar = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=400&q=80",
                isAuthorVerified = true,
                content = "¿Cuál de estas características te entusiasma más de nuestra nueva red social? Vota en la encuesta oficial 👇",
                pollQuestion = "¿Qué característica prefieres?",
                pollOptions = pollJson,
                createdAt = now - 3600_000 * 2
            )
        )

        val p3 = dao.insertPost(
            PostEntity(
                userId = u2Id,
                authorName = "Elena Morales",
                authorUsername = "elena_m",
                authorAvatar = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=400&q=80",
                isAuthorVerified = false,
                content = "Atardecer capturado hoy en la costa. Los tonos violetas y azules me recuerdan a la estética de NEXA 🌊✨",
                imageUrl = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=1000&q=80",
                createdAt = now - 3600_000
            )
        )

        // Seed Reactions
        dao.insertReaction(ReactionEntity(targetType = "POST", targetId = p1, userId = u2Id, reactionType = "LOVE"))
        dao.insertReaction(ReactionEntity(targetType = "POST", targetId = p1, userId = u3Id, reactionType = "FIRE"))
        dao.insertReaction(ReactionEntity(targetType = "POST", targetId = p2, userId = u2Id, reactionType = "LIKE"))

        // Seed Comments
        dao.insertComment(
            CommentEntity(
                postId = p1,
                userId = u2Id,
                authorName = "Elena Morales",
                authorUsername = "elena_m",
                authorAvatar = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=400&q=80",
                content = "¡Excelente lanzamiento! La interfaz oscura y los gradientes se sienten increíbles.",
                createdAt = now - 3600_000 * 2
            )
        )
        dao.insertComment(
            CommentEntity(
                postId = p1,
                userId = creatorId,
                authorName = "Fredy Alberto Martínez Greenes",
                authorUsername = "fredy",
                authorAvatar = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=400&q=80",
                isAuthorVerified = true,
                parentCommentId = 1,
                content = "¡Gracias Elena! Diseñado con pasión para toda la comunidad de ClipBook.",
                createdAt = now - 3600_000
            )
        )

        // Seed Stories
        dao.insertStory(
            StoryEntity(
                userId = creatorId,
                authorName = "Fredy Alberto Martínez Greenes",
                authorAvatar = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=400&q=80",
                isAuthorVerified = true,
                textCaption = "¡Bienvenidos a todos a ClipBook! Disfruten de la experiencia social.",
                gradientIndex = 0,
                viewsCount = 42
            )
        )
        dao.insertStory(
            StoryEntity(
                userId = u2Id,
                authorName = "Elena Morales",
                authorAvatar = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=400&q=80",
                mediaUrl = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=800&q=80",
                textCaption = "Paz mental hoy en la playa 🌴",
                gradientIndex = 1,
                viewsCount = 18
            )
        )

        // Seed Groups
        val g1 = dao.insertGroup(
            GroupEntity(
                name = "Innovadores Tecnológicos NEXA",
                description = "Comunidad abierta para discutir avances de software, IA y diseño.",
                isPrivate = false,
                creatorId = creatorId,
                coverUrl = "https://images.unsplash.com/photo-1526374965328-7f61d4dc18c5?w=800&q=80",
                memberCount = 124
            )
        )
        dao.insertGroupMember(GroupMemberEntity(groupId = g1, userId = creatorId, role = "ADMIN"))
        dao.insertGroupMember(GroupMemberEntity(groupId = g1, userId = u2Id, role = "MEMBER"))

        val g2 = dao.insertGroup(
            GroupEntity(
                name = "Club de Creadores Digitales",
                description = "Grupo exclusivo para compartir portfolios, feedback y colaboraciones.",
                isPrivate = true,
                creatorId = u2Id,
                coverUrl = "https://images.unsplash.com/photo-1518770660439-4636190af475?w=800&q=80",
                memberCount = 58
            )
        )
        dao.insertGroupMember(GroupMemberEntity(groupId = g2, userId = u2Id, role = "ADMIN"))

        // Seed Pages
        val pPage1 = dao.insertPage(
            PageEntity(
                name = "NEXA Studios",
                category = "Empresa",
                description = "Laboratorio de innovación y experiencias digitales interactivas.",
                avatarUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=400&q=80",
                coverUrl = "https://images.unsplash.com/photo-1579546929518-9e396f3cc809?w=1200&q=80",
                contactInfo = "contact@nexa.social",
                ownerId = creatorId,
                followersCount = 1250
            )
        )

        // Seed Gift Codes
        // Pre-generate ready-to-test valid gift codes:
        dao.insertGiftCode(
            GiftCodeEntity(
                code = "NEXA-2026-GOLD-VIP1",
                rewardType = "VERIFICATION_24H",
                isUsed = false,
                isBlocked = false,
                createdByAdminId = creatorId,
                createdAt = now
            )
        )
        dao.insertGiftCode(
            GiftCodeEntity(
                code = "NEXA-7777-BETA-PASS",
                rewardType = "VERIFICATION_24H",
                isUsed = false,
                isBlocked = false,
                createdByAdminId = creatorId,
                createdAt = now
            )
        )
        dao.insertGiftCode(
            GiftCodeEntity(
                code = "NEXA-8888-FAST-FREE",
                rewardType = "VERIFICATION_24H",
                isUsed = false,
                isBlocked = false,
                createdByAdminId = creatorId,
                createdAt = now
            )
        )
        // Already used code for testing error handling
        val usedCode = dao.insertGiftCode(
            GiftCodeEntity(
                code = "NEXA-USED-CODE-DEMO",
                rewardType = "VERIFICATION_24H",
                isUsed = true,
                isBlocked = false,
                createdByAdminId = creatorId,
                createdAt = now - 50_000
            )
        )
        dao.insertRedemption(
            GiftCodeRedemptionEntity(
                giftCodeId = usedCode,
                code = "NEXA-USED-CODE-DEMO",
                userId = u3Id,
                redeemedAt = now - 40_000
            )
        )

        // Seed Notifications
        dao.insertNotification(
            NotificationEntity(
                userId = creatorId,
                senderId = u2Id,
                senderName = "Elena Morales",
                senderAvatar = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=400&q=80",
                title = "Nueva Reacción",
                message = "Elena Morales reaccionó con ❤️ a tu publicación.",
                type = "REACTION",
                createdAt = now - 1800_000
            )
        )
        dao.insertNotification(
            NotificationEntity(
                userId = creatorId,
                senderId = u4Id,
                senderName = "Sofia Romero",
                senderAvatar = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=400&q=80",
                title = "Solicitud de Amistad",
                message = "Sofia Romero te ha enviado una solicitud de amistad.",
                type = "FRIEND_REQ",
                createdAt = now - 900_000
            )
        )

        // Initial Security Log
        dao.insertSecurityLog(
            SecurityLogEntity(
                userId = creatorId,
                eventType = "SYSTEM_INITIALIZATION",
                details = "Plataforma NEXA SOCIAL inicializada con éxito.",
                ipAddress = "127.0.0.1",
                timestamp = now
            )
        )
    }
}
