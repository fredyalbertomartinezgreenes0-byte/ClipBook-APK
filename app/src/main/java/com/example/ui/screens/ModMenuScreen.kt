package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.UserEntity
import com.example.data.repository.NexaRepository
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModMenuScreen(
    repository: NexaRepository,
    currentUser: UserEntity?,
    onBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var customCoinsInput by remember { mutableStateOf("") }
    var broadcastMessage by remember { mutableStateOf("") }
    var notificationFeedback by remember { mutableStateOf<String?>(null) }
    var isOperating by remember { mutableStateOf(false) }

    val allUsers by repository.getAllUsers().collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("⚡ MOD MENÚ CREADOR", fontWeight = FontWeight.Black, fontSize = 18.sp, color = NexaCyan)
                    }
                },
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
            // Header Hero Banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Brush.horizontalGradient(listOf(NexaCyan, NexaPurpleLight, NexaGold)), RoundedCornerShape(18.dp)),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = NexaSurfaceElevated)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Panel de Superusuario ClipBook",
                                color = NexaGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "👑 ${currentUser?.name ?: "Creador"}",
                                color = NexaTextPrimary,
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = NexaGold.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "MOD ON ⚡",
                                color = NexaGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Saldo actual: 🪙 ${currentUser?.coins ?: 0} ClipCoins",
                        color = NexaCyan,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp
                    )
                }
            }

            // Notification Feedback
            notificationFeedback?.let {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = NexaCyan.copy(alpha = 0.2f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = it,
                        color = NexaCyan,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // SECTION 1: FREE COINS GENERATOR
            Text("🪙 RECARGAR MONEDAS GRATIS (SIN LÍMITE)", color = NexaTextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Spacer(modifier = Modifier.height(10.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = NexaSurface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Selecciona un monto rápido para inyectar monedas a tu saldo instantáneamente:",
                        color = NexaTextSecondary,
                        fontSize = 13.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                if (currentUser == null) return@Button
                                coroutineScope.launch {
                                    repository.addCoins(currentUser.id, 5000L)
                                    notificationFeedback = "¡+5,000 🪙 ClipCoins inyectadas con éxito!"
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NexaSurfaceElevated),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("+5,000 🪙", color = NexaCyan, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        Button(
                            onClick = {
                                if (currentUser == null) return@Button
                                coroutineScope.launch {
                                    repository.addCoins(currentUser.id, 25000L)
                                    notificationFeedback = "¡+25,000 🪙 ClipCoins inyectadas con éxito!"
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NexaSurfaceElevated),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("+25,000 🪙", color = NexaCyan, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        Button(
                            onClick = {
                                if (currentUser == null) return@Button
                                coroutineScope.launch {
                                    repository.addCoins(currentUser.id, 100000L)
                                    notificationFeedback = "¡+100,000 🪙 ClipCoins inyectadas con éxito!"
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NexaSurfaceElevated),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("+100,000 🪙", color = NexaGold, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Maximum Infinite Coins Button
                    Button(
                        onClick = {
                            if (currentUser == null) return@Button
                            coroutineScope.launch {
                                repository.setCoins(currentUser.id, 999999999L)
                                notificationFeedback = "⚡ ¡MODO MONEDAS INFINITAS ACTIVADO: 999,999,999 🪙!"
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NexaGold),
                        modifier = Modifier.fillMaxWidth().testTag("infinite_coins_button"),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Default.AllInclusive, contentDescription = null, tint = Color.Black)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Activar Monedas Infinitas (999,999,999 🪙)", color = Color.Black, fontWeight = FontWeight.Black)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Custom input
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = customCoinsInput,
                            onValueChange = { customCoinsInput = it.filter { char -> char.isDigit() } },
                            placeholder = { Text("Monto personalizado...") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                val amount = customCoinsInput.toLongOrNull() ?: 0L
                                if (amount > 0 && currentUser != null) {
                                    coroutineScope.launch {
                                        repository.addCoins(currentUser.id, amount)
                                        notificationFeedback = "¡+$amount 🪙 ClipCoins añadidas!"
                                        customCoinsInput = ""
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NexaCyan),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Recargar", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // SECTION 2: PLATFORM BROADCAST (DIFUSIÓN GLOBAL)
            Text("📢 MENSAJE GLOBAL DEL CREADOR", color = NexaTextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Spacer(modifier = Modifier.height(10.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = NexaSurface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Publica un anuncio oficial con insignia de Creador que verán todos los usuarios:",
                        color = NexaTextSecondary,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = broadcastMessage,
                        onValueChange = { broadcastMessage = it },
                        placeholder = { Text("Escribe el comunicado oficial de ClipBook...") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3,
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            if (broadcastMessage.isNotBlank() && currentUser != null) {
                                coroutineScope.launch {
                                    repository.createPost(
                                        userId = currentUser.id,
                                        content = "📢 COMUNICADO OFICIAL DEL CREADOR:\n$broadcastMessage"
                                    )
                                    notificationFeedback = "¡Comunicado publicado en el Feed para todos los usuarios! 🚀"
                                    broadcastMessage = ""
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NexaPurpleLight),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Campaign, contentDescription = null, tint = Color.Black)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Emitir Comunicado Global", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // SECTION 3: QUICK USER BALANCES & GIFTS
            Text("👥 GESTIÓN DE MONEDAS DE OTROS USUARIOS", color = NexaTextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Spacer(modifier = Modifier.height(10.dp))

            allUsers.filter { it.id != currentUser?.id }.forEach { user ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = NexaSurfaceElevated)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(user.name, color = NexaTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("@${user.username} • Saldo: 🪙 ${user.coins}", color = NexaCyan, fontSize = 12.sp)
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                FilledTonalButton(
                                    onClick = {
                                        coroutineScope.launch {
                                            repository.addCoins(user.id, 1000L)
                                            notificationFeedback = "Regalaste +1,000 monedas a ${user.name}."
                                        }
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp)
                                ) {
                                    Text("+1K 🪙", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                FilledTonalButton(
                                    onClick = {
                                        coroutineScope.launch {
                                            repository.addCoins(user.id, 5000L)
                                            notificationFeedback = "Regalaste +5,000 monedas a ${user.name}."
                                        }
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp)
                                ) {
                                    Text("+5K 🪙", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = {
                                        coroutineScope.launch {
                                            repository.addCoins(user.id, 25000L)
                                            notificationFeedback = "Regalaste +25,000 monedas a ${user.name}."
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = NexaGold),
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp)
                                ) {
                                    Text("+25K 🪙", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
