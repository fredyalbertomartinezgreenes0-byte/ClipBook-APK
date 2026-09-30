package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.entity.UserEntity
import com.example.data.repository.NexaRepository
import com.example.ui.components.VerifiedBadgeIcon
import com.example.ui.navigation.NexaScreen
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletScreen(
    repository: NexaRepository,
    currentUser: UserEntity?,
    onNavigate: (NexaScreen) -> Unit,
    onBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val allUsers by repository.getAllUsers().collectAsState(initial = emptyList())

    var showSendModal by remember { mutableStateOf(false) }
    var selectedRecipient by remember { mutableStateOf<UserEntity?>(null) }
    var coinsAmountInput by remember { mutableStateOf("") }
    var noteInput by remember { mutableStateOf("") }

    var statusMessage by remember { mutableStateOf<String?>(null) }
    var isError by remember { mutableStateOf(false) }
    var isSending by remember { mutableStateOf(false) }

    val otherUsers = remember(allUsers, currentUser) {
        allUsers.filter { it.id != currentUser?.id }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Billetera ClipCoins", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
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
                .padding(16.dp)
        ) {
            // Status Feedback
            statusMessage?.let {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp),
                    colors = CardDefaults.cardColors(containerColor = if (isError) NexaError.copy(alpha = 0.2f) else NexaSuccess.copy(alpha = 0.2f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = it,
                        color = if (isError) NexaError else NexaSuccess,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            // Main Balance Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = NexaSurface)
            ) {
                Column(
                    modifier = Modifier.padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Saldo Total Disponible", color = NexaTextSecondary, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "🪙 ${currentUser?.coins ?: 1000}",
                            color = NexaGold,
                            fontWeight = FontWeight.Black,
                            fontSize = 32.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("ClipCoins", color = NexaCyan, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // ACTION 1: SEND COINS BUTTON (PRIMARY)
                    Button(
                        onClick = {
                            showSendModal = true
                            if (selectedRecipient == null && otherUsers.isNotEmpty()) {
                                selectedRecipient = otherUsers.first()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NexaGold),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth().testTag("send_coins_to_user_button")
                    ) {
                        Icon(Icons.Default.Send, contentDescription = null, tint = Color.Black)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("💸 Enviar Monedas a un Usuario", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 15.sp)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (currentUser?.role == "CREATOR") {
                        Button(
                            onClick = { onNavigate(NexaScreen.MOD_MENU) },
                            colors = ButtonDefaults.buttonColors(containerColor = NexaCyan),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth().testTag("open_mod_menu_button")
                        ) {
                            Icon(Icons.Default.Bolt, contentDescription = null, tint = Color.Black)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Abrir Mod Menú (Recargas Gratis)", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Button(
                            onClick = { onNavigate(NexaScreen.GIFT_CODE) },
                            colors = ButtonDefaults.buttonColors(containerColor = NexaCyan),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.CardGiftcard, contentDescription = null, tint = Color.Black)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Canjear Código Promocional", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // SECTION: DIRECT TRANSFER BOX (INLINE ACCESSIBILITY)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = NexaSurfaceElevated)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = NexaGold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Transferencia Rápida a Usuarios", color = NexaTextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text("1. Selecciona al usuario:", color = NexaTextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(otherUsers) { user ->
                            val isSelected = selectedRecipient?.id == user.id
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = if (isSelected) NexaGold.copy(alpha = 0.25f) else NexaSurface,
                                border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, NexaGold) else null,
                                modifier = Modifier.clickable { selectedRecipient = user }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    AsyncImage(
                                        model = user.avatarUrl,
                                        contentDescription = user.name,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.size(28.dp).clip(CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = user.name,
                                            color = if (isSelected) NexaGold else NexaTextPrimary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                        Text("@${user.username}", color = NexaTextSecondary, fontSize = 10.sp)
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "2. Monto a enviar a ${selectedRecipient?.name ?: "usuario seleccionado"}:",
                        color = NexaTextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Preset Chips
                    val presets = listOf(50L, 100L, 250L, 500L, 1000L, 5000L)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        presets.take(4).forEach { amt ->
                            AssistChip(
                                onClick = { coinsAmountInput = amt.toString() },
                                label = { Text("$amt 🪙", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = coinsAmountInput,
                        onValueChange = { coinsAmountInput = it.filter { ch -> ch.isDigit() } },
                        placeholder = { Text("Monto en ClipCoins (ej: 500)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = noteInput,
                        onValueChange = { noteInput = it },
                        placeholder = { Text("Mensaje o motivo (opcional)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            val recipient = selectedRecipient
                            val amount = coinsAmountInput.toLongOrNull() ?: 0L
                            if (currentUser == null) return@Button
                            if (recipient == null) {
                                isError = true
                                statusMessage = "Por favor selecciona un destinatario."
                                return@Button
                            }
                            if (amount <= 0) {
                                isError = true
                                statusMessage = "Por favor ingresa un monto mayor a 0."
                                return@Button
                            }

                            coroutineScope.launch {
                                isSending = true
                                statusMessage = null
                                val res = repository.transferCoins(
                                    fromUserId = currentUser.id,
                                    toUserId = recipient.id,
                                    amount = amount,
                                    note = noteInput
                                )
                                isSending = false
                                if (res.isSuccess) {
                                    isError = false
                                    statusMessage = "¡Éxito! Enviaste $amount 🪙 ClipCoins a ${recipient.name} (@${recipient.username}). 🚀"
                                    coinsAmountInput = ""
                                    noteInput = ""
                                } else {
                                    isError = true
                                    statusMessage = res.exceptionOrNull()?.message ?: "Error al transferir monedas"
                                }
                            }
                        },
                        enabled = !isSending,
                        colors = ButtonDefaults.buttonColors(containerColor = NexaGold),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth().testTag("confirm_transfer_button")
                    ) {
                        if (isSending) {
                            CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(16.dp))
                        } else {
                            Icon(Icons.Default.Send, contentDescription = null, tint = Color.Black)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Enviar ${coinsAmountInput.ifBlank { "0" }} 🪙 a ${selectedRecipient?.username?.let { "@$it" } ?: "Usuario"}",
                                color = Color.Black,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // What can you do with ClipCoins?
            Text("¿Para qué sirven las ClipCoins?", color = NexaTextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Spacer(modifier = Modifier.height(10.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = NexaSurfaceElevated)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🎁", fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Enviar Regalos y Transferencias", color = NexaTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Transfiere monedas directamente o envía café, estrellas o coronas a creadores.", color = NexaTextSecondary, fontSize = 12.sp)
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("⭐", fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Destacar Publicaciones", color = NexaTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Haz que tus publicaciones alcancen a más personas en la comunidad de ClipBook.", color = NexaTextSecondary, fontSize = 12.sp)
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("👑", fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Recompensas Exclusivas", color = NexaTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Desbloquea insignias personalizadas y funciones comunitarias.", color = NexaTextSecondary, fontSize = 12.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
