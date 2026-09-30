package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.entity.UserEntity
import com.example.data.repository.NexaRepository
import com.example.ui.components.VerifiedBadgeIcon
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountManagementScreen(
    repository: NexaRepository,
    currentUser: UserEntity?,
    onBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    var nameInput by remember(currentUser) { mutableStateOf(currentUser?.name ?: "") }
    var usernameInput by remember(currentUser) { mutableStateOf(currentUser?.username ?: "") }
    var bioInput by remember(currentUser) { mutableStateOf(currentUser?.bio ?: "") }
    var avatarUrlInput by remember(currentUser) { mutableStateOf(currentUser?.avatarUrl ?: "") }
    var coverUrlInput by remember(currentUser) { mutableStateOf(currentUser?.coverUrl ?: "") }
    var birthDateInput by remember(currentUser) { mutableStateOf(currentUser?.birthDate ?: "1998-01-01") }

    // Password change
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmNewPassword by remember { mutableStateOf("") }
    var showPasswordFields by remember { mutableStateOf(false) }

    // Email change
    var newEmailInput by remember(currentUser) { mutableStateOf(currentUser?.email ?: "") }
    var showEmailChange by remember { mutableStateOf(false) }

    var statusMessage by remember { mutableStateOf<String?>(null) }
    var isError by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }

    val presetAvatars = listOf(
        "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=400&q=80",
        "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=400&q=80",
        "https://images.unsplash.com/photo-1570295999919-56ceb5ecca61?w=400&q=80",
        "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=400&q=80",
        "https://images.unsplash.com/photo-1580489944761-15a19d654956?w=400&q=80"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Administrar Cuenta", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                actions = {
                    Button(
                        onClick = {
                            if (currentUser == null) return@Button
                            coroutineScope.launch {
                                isSaving = true
                                statusMessage = null
                                val result = repository.updateProfile(
                                    userId = currentUser.id,
                                    name = nameInput,
                                    username = usernameInput,
                                    bio = bioInput,
                                    avatarUrl = avatarUrlInput,
                                    coverUrl = coverUrlInput,
                                    birthDate = birthDateInput
                                )
                                isSaving = false
                                if (result.isSuccess) {
                                    isError = false
                                    statusMessage = "¡Datos actualizados con éxito! ✨"
                                } else {
                                    isError = true
                                    statusMessage = result.exceptionOrNull()?.message ?: "Error al actualizar"
                                }
                            }
                        },
                        enabled = !isSaving,
                        colors = ButtonDefaults.buttonColors(containerColor = NexaCyan),
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier.padding(end = 8.dp).testTag("save_account_changes_button")
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(16.dp))
                        } else {
                            Text("Guardar", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
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
            // Feedback status banner
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

            // User Account Badge & Overview Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = NexaSurface)
            ) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(modifier = Modifier.size(86.dp)) {
                        AsyncImage(
                            model = avatarUrlInput.ifBlank { "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=400&q=80" },
                            contentDescription = "Avatar actual",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = nameInput.ifBlank { "Usuario" },
                            color = NexaTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        if (currentUser?.role == "CREATOR") {
                            Spacer(modifier = Modifier.width(6.dp))
                            VerifiedBadgeIcon(size = 18)
                        }
                    }

                    Text(
                        text = "@${usernameInput.ifBlank { "usuario" }} • ${currentUser?.email ?: ""}",
                        color = NexaTextSecondary,
                        fontSize = 13.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Role pill & Coins pill
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (currentUser?.role == "CREATOR") NexaGold.copy(alpha = 0.15f) else NexaCyan.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = if (currentUser?.role == "CREATOR") "👑 ROL: CREADOR OFICIAL" else "USUARIO REGULAR",
                                color = if (currentUser?.role == "CREATOR") NexaGold else NexaCyan,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = NexaPurpleLight.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "🪙 ${currentUser?.coins ?: 1000} ClipCoins",
                                color = NexaPurpleLight,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Presets for Avatar Selection
            Text("Elegir foto de perfil rápida:", color = NexaTextSecondary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                presetAvatars.forEach { url ->
                    AsyncImage(
                        model = url,
                        contentDescription = "Avatar preset",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .clickable { avatarUrlInput = url }
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Personal Information Fields
            Text("Información de Perfil", color = NexaCyan, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = nameInput,
                onValueChange = { nameInput = it },
                label = { Text("Nombre Completo") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = usernameInput,
                onValueChange = { usernameInput = it },
                label = { Text("Nombre de Usuario (@)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = bioInput,
                onValueChange = { bioInput = it },
                label = { Text("Biografía") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = avatarUrlInput,
                onValueChange = { avatarUrlInput = it },
                label = { Text("URL de Foto de Perfil") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = coverUrlInput,
                onValueChange = { coverUrlInput = it },
                label = { Text("URL de Portada") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = birthDateInput,
                onValueChange = { birthDateInput = it },
                label = { Text("Fecha de Nacimiento (AAAA-MM-DD)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Security: Password Management
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = NexaSurfaceElevated)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = NexaCyan)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Cambiar Contraseña", color = NexaTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        IconButton(onClick = { showPasswordFields = !showPasswordFields }) {
                            Icon(
                                if (showPasswordFields) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = null,
                                tint = NexaTextSecondary
                            )
                        }
                    }

                    if (showPasswordFields) {
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = currentPassword,
                            onValueChange = { currentPassword = it },
                            label = { Text("Contraseña Actual") },
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = newPassword,
                            onValueChange = { newPassword = it },
                            label = { Text("Nueva Contraseña (mín. 6)") },
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = confirmNewPassword,
                            onValueChange = { confirmNewPassword = it },
                            label = { Text("Confirmar Nueva Contraseña") },
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = {
                                if (currentUser == null) return@Button
                                if (newPassword != confirmNewPassword) {
                                    isError = true
                                    statusMessage = "Las contraseñas nuevas no coinciden."
                                    return@Button
                                }
                                coroutineScope.launch {
                                    val res = repository.changePassword(currentUser.id, currentPassword, newPassword)
                                    if (res.isSuccess) {
                                        isError = false
                                        statusMessage = "¡Contraseña actualizada exitosamente!"
                                        currentPassword = ""
                                        newPassword = ""
                                        confirmNewPassword = ""
                                        showPasswordFields = false
                                    } else {
                                        isError = true
                                        statusMessage = res.exceptionOrNull()?.message ?: "Error al cambiar contraseña"
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NexaCyan),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Actualizar Contraseña", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Security: Email Change
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = NexaSurfaceElevated)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Email, contentDescription = null, tint = NexaPurpleLight)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Cambiar Correo Electrónico", color = NexaTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        IconButton(onClick = { showEmailChange = !showEmailChange }) {
                            Icon(
                                if (showEmailChange) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = null,
                                tint = NexaTextSecondary
                            )
                        }
                    }

                    if (showEmailChange) {
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = newEmailInput,
                            onValueChange = { newEmailInput = it },
                            label = { Text("Nuevo Correo") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = {
                                if (currentUser == null) return@Button
                                coroutineScope.launch {
                                    val res = repository.changeEmail(currentUser.id, newEmailInput)
                                    if (res.isSuccess) {
                                        isError = false
                                        statusMessage = "¡Correo electrónico actualizado!"
                                        showEmailChange = false
                                    } else {
                                        isError = true
                                        statusMessage = res.exceptionOrNull()?.message ?: "Error al actualizar correo"
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NexaPurpleLight),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Guardar Correo", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
