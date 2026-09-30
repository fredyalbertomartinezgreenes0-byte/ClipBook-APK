package com.example.data.security

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.concurrent.ConcurrentHashMap
import java.util.regex.Pattern

object SecurityHelper {

    private val secureRandom = SecureRandom()
    private val attemptsMap = ConcurrentHashMap<String, MutableList<Long>>()

    /**
     * Generates a cryptographically random salt (16 bytes, hex encoded)
     */
    fun generateSalt(): String {
        val salt = ByteArray(16)
        secureRandom.nextBytes(salt)
        return salt.joinToString("") { "%02x".format(it) }
    }

    /**
     * Hashes password with SHA-256 and salt
     */
    fun hashPassword(password: String, salt: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val combined = "$password:$salt"
        val hash = digest.digest(combined.toByteArray(Charsets.UTF_8))
        return hash.joinToString("") { "%02x".format(it) }
    }

    /**
     * Checks whether the provided password matches the stored hash
     */
    fun verifyPassword(password: String, salt: String, expectedHash: String): Boolean {
        val computed = hashPassword(password, salt)
        return MessageDigest.isEqual(computed.toByteArray(), expectedHash.toByteArray())
    }

    /**
     * Generates a 6-digit random code for email verification
     */
    fun generateSixDigitCode(): String {
        val number = secureRandom.nextInt(900000) + 100000
        return number.toString()
    }

    /**
     * Hashes a 6-digit verification code before storing to prevent plaintext exposure
     */
    fun hashCode(code: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(code.toByteArray(Charsets.UTF_8))
        return hash.joinToString("") { "%02x".format(it) }
    }

    /**
     * Generates a unique, cryptographically secure Gift Code in format NEXA-XXXX-XXXX-XXXX
     */
    fun generateGiftCode(): String {
        val chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
        fun chunk(length: Int): String {
            return (1..length).map { chars[secureRandom.nextInt(chars.length)] }.joinToString("")
        }
        return "NEXA-${chunk(4)}-${chunk(4)}-${chunk(4)}"
    }

    /**
     * Validates gift code format: NEXA-XXXX-XXXX-XXXX
     */
    fun isValidGiftCodeFormat(code: String): Boolean {
        val pattern = Pattern.compile("^NEXA-[A-Z0-9]{4}-[A-Z0-9]{4}-[A-Z0-9]{4}$")
        return pattern.matcher(code.trim().uppercase()).matches()
    }

    /**
     * Sanitizes general user input, removing script tags, null bytes and dangerous control characters
     * while preserving normal user text, emojis, and punctuation.
     */
    fun sanitizeInput(input: String): String {
        return input
            .replace("\u0000", "")
            .replace("<script", "&lt;script", ignoreCase = true)
            .replace("</script>", "&lt;/script&gt;", ignoreCase = true)
            .replace("javascript:", "", ignoreCase = true)
            .trim()
    }

    /**
     * In-memory Rate Limiter:
     * Checks if an action key (e.g. "login:user@email.com" or "redeem:userId")
     * exceeds maxAttempts within windowMillis.
     * Returns true if allowed, false if rate limited.
     */
    fun checkRateLimit(actionKey: String, maxAttempts: Int = 5, windowMillis: Long = 60_000L): Boolean {
        val now = System.currentTimeMillis()
        val list = attemptsMap.computeIfAbsent(actionKey) { mutableListOf() }
        synchronized(list) {
            list.removeAll { now - it > windowMillis }
            if (list.size >= maxAttempts) {
                return false
            }
            list.add(now)
            return true
        }
    }

    /**
     * Detects abusive, scam, phishing or malicious links
     */
    fun checkContentModeration(text: String): ModerationResult {
        val lower = text.lowercase()
        val highRiskWords = listOf(
            "phishing", "free crypto", "free nitro", "hack instagram",
            "password stealer", "malware", "bit.ly/scam", "suicide", "hate speech"
        )
        for (bad in highRiskWords) {
            if (lower.contains(bad)) {
                return ModerationResult(isBlocked = true, reason = "Contenido identificado como fraudulento o peligroso: $bad")
            }
        }
        return ModerationResult(isBlocked = false)
    }

    data class ModerationResult(
        val isBlocked: Boolean,
        val reason: String? = null
    )
}
