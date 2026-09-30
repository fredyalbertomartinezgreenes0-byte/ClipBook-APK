package com.example.data.security

import kotlinx.coroutines.delay
import java.util.UUID

object PaymentService {

    data class PaymentTokenRequest(
        val last4Digits: String,
        val cardHolder: String,
        val amount: Double = 9.99,
        val currency: String = "USD"
    )

    data class PaymentResult(
        val success: Boolean,
        val transactionToken: String,
        val errorMessage: String? = null
    )

    /**
     * Simulates tokenized gateway charge (e.g. Stripe / MercadoPago / Google Pay).
     * Never retains full PAN, expiration date, or CVV.
     */
    suspend fun processVerificationPayment(request: PaymentTokenRequest): PaymentResult {
        delay(1200) // Simulate gateway communication
        if (request.cardHolder.isBlank() || request.last4Digits.length != 4) {
            return PaymentResult(
                success = false,
                transactionToken = "",
                errorMessage = "Datos de pago inválidos o incompletos."
            )
        }
        val token = "tok_nexa_${UUID.randomUUID().toString().take(12)}"
        return PaymentResult(
            success = true,
            transactionToken = token
        )
    }
}
