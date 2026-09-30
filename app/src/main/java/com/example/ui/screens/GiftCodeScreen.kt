package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.UserEntity
import com.example.data.repository.NexaRepository
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GiftCodeScreen(
    repository: NexaRepository,
    currentUser: UserEntity?,
    onBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var codeInput by remember { mutableStateOf("") }
    var resultMessage by remember { mutableStateOf<String?>(null) }
    var isSuccessResult by remember { mutableStateOf(false) }
    var isSubmitting by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Canjear Código", fontWeight = FontWeight.Bold) },
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
            Spacer(modifier = Modifier.height(10.dp))

            // Gift Icon Banner
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(
                        Brush.linearGradient(listOf(NexaCyan, NexaBlue, NexaPurple))
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CardGiftcard,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(42.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Códigos de Regalo Oficiales",
                color = NexaTextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Introduce tu código promocional de ClipBook para recibir beneficios oficiales en tu cuenta.",
                color = NexaTextSecondary,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Form Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = NexaSurface),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.verticalGradient(listOf(NexaBorder, NexaBorderSubtle))
                )
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Código de Regalo",
                        color = NexaTextSecondary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = codeInput,
                        onValueChange = {
                            codeInput = it.uppercase()
                            resultMessage = null
                        },
                        placeholder = {
                            Text("NEXA-XXXX-XXXX-XXXX", color = NexaTextMuted, fontFamily = FontFamily.Monospace)
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("gift_code_input"),
                        shape = RoundedCornerShape(12.dp),
                        textStyle = androidx.compose.ui.text.TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            letterSpacing = 1.sp
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = NexaSurfaceElevated,
                            unfocusedContainerColor = NexaSurfaceElevated,
                            focusedBorderColor = NexaCyan,
                            unfocusedBorderColor = NexaBorder,
                            focusedTextColor = NexaCyanLight,
                            unfocusedTextColor = NexaTextPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Redeem Button
                    Button(
                        onClick = {
                            if (codeInput.isBlank()) {
                                resultMessage = "Código no válido."
                                isSuccessResult = false
                                return@Button
                            }
                            coroutineScope.launch {
                                isSubmitting = true
                                val result = repository.redeemGiftCode(
                                    rawCode = codeInput,
                                    userId = currentUser?.id ?: 1L
                                )
                                isSubmitting = false
                                when (result) {
                                    is NexaRepository.GiftCodeResult.Success -> {
                                        resultMessage = result.message
                                        isSuccessResult = true
                                        codeInput = ""
                                    }
                                    is NexaRepository.GiftCodeResult.Error -> {
                                        resultMessage = result.message
                                        isSuccessResult = false
                                    }
                                }
                            }
                        },
                        enabled = !isSubmitting,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("redeem_gift_code_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NexaCyan)
                    ) {
                        if (isSubmitting) {
                            CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(20.dp))
                        } else {
                            Text(
                                text = "Canjear Código",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }

                    // Result Notice
                    AnimatedVisibility(visible = resultMessage != null) {
                        resultMessage?.let { msg ->
                            Spacer(modifier = Modifier.height(16.dp))
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSuccessResult) NexaSuccess.copy(alpha = 0.15f) else NexaError.copy(alpha = 0.15f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (isSuccessResult) Icons.Default.CheckCircle else Icons.Default.ErrorOutline,
                                        contentDescription = null,
                                        tint = if (isSuccessResult) NexaSuccess else NexaError,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = msg,
                                        color = if (isSuccessResult) NexaSuccess else NexaError,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Test Helper Card for Reviewer/User
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = NexaSurfaceElevated)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = NexaCyan, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Códigos de prueba disponibles:", color = NexaCyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Puedes probar con estos códigos generados por el sistema:",
                        color = NexaTextMuted,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    listOf(
                        "NEXA-2026-GOLD-VIP1",
                        "NEXA-7777-BETA-PASS",
                        "NEXA-8888-FAST-FREE",
                        "NEXA-USED-CODE-DEMO"
                    ).forEach { sampleCode ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(NexaSurface)
                                .clickable {
                                    codeInput = sampleCode
                                }
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = sampleCode,
                                color = NexaCyanLight,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = if (sampleCode.contains("USED")) "(Ya usado)" else "Tocar para copiar",
                                color = NexaTextMuted,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
