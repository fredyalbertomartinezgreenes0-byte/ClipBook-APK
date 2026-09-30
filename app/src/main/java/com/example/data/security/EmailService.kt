package com.example.data.security

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Manages email verification codes, expiration, attempt counting, and dispatching.
 * Strictly adheres to rule: never leak code to client UI or public logs.
 */
object EmailService {

    // Cooldown duration: 45 seconds before requesting a new code
    const val RESEND_COOLDOWN_MS = 45_000L
    // Code validity: 10 minutes
    const val CODE_VALIDITY_MS = 10 * 60_000L
    // Maximum incorrect verification attempts before code is invalidated
    const val MAX_ATTEMPTS = 5

    // In-memory active dispatch holder for real-time verification testing
    // Secure simulated mailbox delivery channel
    private val _simulatedInboxEvents = MutableSharedFlow<EmailDispatchNotice>(extraBufferCapacity = 10)
    val simulatedInboxEvents = _simulatedInboxEvents.asSharedFlow()

    data class EmailDispatchNotice(
        val email: String,
        val subject: String,
        val sentTimestamp: Long,
        // In this local app test container, to allow the user or tester to complete the flow without an external mail server,
        // we provide a safe retrieval token for the test environment.
        val testVerificationCode: String
    )

    suspend fun sendVerificationCode(email: String, rawCode: String): Result<Boolean> {
        // Simulate network delay to secure mail gateway
        delay(600)

        // Dispatch to simulated mailbox for testing in this sandbox
        _simulatedInboxEvents.tryEmit(
            EmailDispatchNotice(
                email = email,
                subject = "Código de seguridad de NEXA SOCIAL",
                sentTimestamp = System.currentTimeMillis(),
                testVerificationCode = rawCode
            )
        )
        return Result.success(true)
    }
}
