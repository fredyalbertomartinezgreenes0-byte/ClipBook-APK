package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.UserEntity
import com.example.data.repository.NexaRepository
import com.example.ui.components.VerifiedBadgeIcon
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerifyPurchaseScreen(
    repository: NexaRepository,
    currentUser: UserEntity?,
    onBack: () -> Unit,
    onSuccess: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var cardHolder by remember { mutableStateOf(currentUser?.name ?: "") }
    var cardNumber by remember { mutableStateOf("4532 8901 2345 7890") }
    var expiry by remember { mutableStateOf("12/28") }
    var cvv by remember { mutableStateOf("123") }

    var isProcessing by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isPurchasedSuccess by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Solicitar Verificación", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NexaSurface)
            )
        },
        containerColor = NexaBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Badge
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.linearGradient(listOf(NexaCyan, NexaBlue, NexaPurple))
                    ),
                contentAlignment = Alignment.Center
            ) {
                VerifiedBadgeIcon(size = 40)
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Insignia Oficial de Verificación",
                color = NexaTextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Autentica tu presencia, destaca en los comentarios y protege tu identidad frente a suplantaciones en ClipBook.",
                color = NexaTextSecondary,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Pricing & Tokenized Security Guarantee
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = NexaSurfaceElevated),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.horizontalGradient(listOf(NexaCyan.copy(alpha = 0.4f), NexaPurple.copy(alpha = 0.4f)))
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Suscripción Verificada", color = NexaTextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text("$9.99 / mes", color = NexaCyan, fontWeight = FontWeight.Black, fontSize = 16.sp)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = NexaCyan, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Pagos 100% tokenizados. No se almacena CVV ni datos sensibles.",
                            color = NexaTextMuted,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (isPurchasedSuccess) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = NexaSurface)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = NexaCyan, modifier = Modifier.size(52.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("¡Verificación Aprobada!", color = NexaTextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            "El pago tokenizado ha sido confirmado por la pasarela de pagos. Tu cuenta cuenta ahora con la insignia oficial.",
                            color = NexaTextSecondary,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onSuccess,
                            colors = ButtonDefaults.buttonColors(containerColor = NexaCyan),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Volver a mi Perfil", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                // Payment Form Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = NexaSurface)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text("Titular de la tarjeta", color = NexaTextSecondary, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = cardHolder,
                            onValueChange = { cardHolder = it },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text("Número de tarjeta (demostración tokenizada)", color = NexaTextSecondary, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = cardNumber,
                            onValueChange = { cardNumber = it },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Vencimiento", color = NexaTextSecondary, fontSize = 12.sp)
                                OutlinedTextField(
                                    value = expiry,
                                    onValueChange = { expiry = it },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text("CVV (no retenido)", color = NexaTextSecondary, fontSize = 12.sp)
                                OutlinedTextField(
                                    value = cvv,
                                    onValueChange = { cvv = it },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }

                        errorMessage?.let {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(text = it, color = NexaError, fontSize = 13.sp)
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = {
                                val last4 = cardNumber.replace(" ", "").takeLast(4)
                                coroutineScope.launch {
                                    isProcessing = true
                                    errorMessage = null
                                    val result = repository.purchaseVerification(
                                        userId = currentUser?.id ?: 1L,
                                        cardHolder = cardHolder,
                                        last4Digits = if (last4.length == 4) last4 else "4242"
                                    )
                                    isProcessing = false
                                    if (result.isSuccess) {
                                        isPurchasedSuccess = true
                                    } else {
                                        errorMessage = result.exceptionOrNull()?.message ?: "Error en el procesamiento del pago"
                                    }
                                }
                            },
                            enabled = !isProcessing,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("submit_payment_button"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NexaCyan)
                        ) {
                            if (isProcessing) {
                                CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(22.dp))
                            } else {
                                Text(
                                    text = "Confirmar Pago Seguro ($9.99)",
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
