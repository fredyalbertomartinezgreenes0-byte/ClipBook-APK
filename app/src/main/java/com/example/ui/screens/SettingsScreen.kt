package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.UserEntity
import com.example.data.repository.NexaRepository
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    repository: NexaRepository,
    currentUser: UserEntity?,
    onBack: () -> Unit,
    onLogout: () -> Unit
) {
    var privacyPosts by remember { mutableStateOf("Todos") }
    var privacyMessages by remember { mutableStateOf("Amigos") }
    var twoFactorEnabled by remember { mutableStateOf(false) }
    var showSuccessToast by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configuración y Privacidad", fontWeight = FontWeight.Bold) },
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
            // Section: Cuenta
            SettingsSectionHeader("Cuenta")
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = NexaSurface)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    SettingsRowItem(Icons.Default.Email, "Correo electrónico", currentUser?.email ?: "correo@nexa.social")
                    SettingsRowItem(Icons.Default.Lock, "Contraseña", "••••••••")
                    SettingsRowItem(Icons.Default.Devices, "Sesiones activas", "Android Device (Actual)")
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Section: Privacidad
            SettingsSectionHeader("Privacidad")
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = NexaSurface)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("¿Quién puede ver mis publicaciones?", color = NexaTextPrimary, fontSize = 13.sp)
                        Text(privacyPosts, color = NexaCyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                    HorizontalDivider(color = NexaBorderSubtle)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("¿Quién puede enviarme mensajes?", color = NexaTextPrimary, fontSize = 13.sp)
                        Text(privacyMessages, color = NexaCyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Section: Seguridad y Antifraude
            SettingsSectionHeader("Seguridad y Antifraude")
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = NexaSurface)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Autenticación en dos pasos (2FA)", color = NexaTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            Text("Protege tu cuenta con verificación adicional", color = NexaTextMuted, fontSize = 11.sp)
                        }
                        Switch(
                            checked = twoFactorEnabled,
                            onCheckedChange = { twoFactorEnabled = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = NexaCyan)
                        )
                    }
                    HorizontalDivider(color = NexaBorderSubtle)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Estado de correo", color = NexaTextPrimary, fontSize = 13.sp)
                        Text(
                            text = if (currentUser?.isEmailVerified == true) "Confirmado ✓" else "Pendiente",
                            color = if (currentUser?.isEmailVerified == true) NexaSuccess else NexaWarning,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Section: Soporte y Acerca de
            SettingsSectionHeader("Información")
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = NexaSurface)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    SettingsRowItem(Icons.Default.HelpOutline, "Centro de ayuda", "Preguntas frecuentes y soporte")
                    SettingsRowItem(Icons.Default.Gavel, "Términos y Condiciones", "Políticas de uso de ClipBook")
                    SettingsRowItem(Icons.Default.Info, "Versión de la plataforma", "ClipBook v2.4.0 (Pro)")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Switch User / Logout Button
            Button(
                onClick = onLogout,
                colors = ButtonDefaults.buttonColors(containerColor = NexaError),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("settings_logout_button")
            ) {
                Icon(Icons.Default.ExitToApp, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cerrar Sesión / Cambiar Usuario", color = Color.White, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        color = NexaCyan,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp,
        modifier = Modifier.padding(bottom = 6.dp)
    )
}

@Composable
fun SettingsRowItem(icon: ImageVector, title: String, subtitle: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = NexaTextSecondary, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = NexaTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            Text(subtitle, color = NexaTextMuted, fontSize = 11.sp)
        }
    }
}
