package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.NexaRepository
import com.example.data.security.EmailService
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun AuthScreen(
    repository: NexaRepository,
    onAuthSuccess: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var isRegisterMode by remember { mutableStateOf(false) }
    var showEmailVerifyStep by remember { mutableStateOf(false) }
    var pendingEmail by remember { mutableStateOf("") }

    // Form states
    var nameInput by remember { mutableStateOf("") }
    var usernameInput by remember { mutableStateOf("") }
    var emailInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var birthDateInput by remember { mutableStateOf("2000-01-01") }
    var avatarUrlInput by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

    // 6-digit code state
    var verificationCodeInput by remember { mutableStateOf("") }
    var lastSentNotice by remember { mutableStateOf<EmailService.EmailDispatchNotice?>(null) }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    // Listen to secure test email dispatcher
    LaunchedEffect(Unit) {
        EmailService.simulatedInboxEvents.collect { notice ->
            lastSentNotice = notice
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NexaBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Logo Branding
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(NexaGradientPrimary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "C",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 38.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Clip",
                    color = NexaCyan,
                    fontWeight = FontWeight.Black,
                    fontSize = 28.sp,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Book",
                    color = NexaPurpleLight,
                    fontWeight = FontWeight.Black,
                    fontSize = 28.sp,
                    letterSpacing = 1.sp
                )
            }

            Text(
                text = "Tu red social moderna, segura y conectada",
                color = NexaTextSecondary,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Card Container
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = NexaSurface),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.verticalGradient(listOf(NexaBorder, NexaBorderSubtle))
                )
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    if (showEmailVerifyStep) {
                        // 6-DIGIT EMAIL VERIFICATION STEP (Requirement 4)
                        Text(
                            text = "Verificación de Correo",
                            color = NexaTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Hemos enviado un código aleatorio de 6 dígitos a:",
                            color = NexaTextSecondary,
                            fontSize = 13.sp
                        )
                        Text(
                            text = pendingEmail,
                            color = NexaCyan,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Simulated mailbox banner for development & testability
                        lastSentNotice?.let { notice ->
                            if (notice.email == pendingEmail) {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = NexaSurfaceHighlight),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.MailOutline, contentDescription = null, tint = NexaCyan)
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = "Simulador de Correo Seguro",
                                                color = NexaCyanLight,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp
                                            )
                                            Text(
                                                text = "Código de prueba: ${notice.testVerificationCode}",
                                                color = NexaTextPrimary,
                                                fontSize = 12.sp
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(14.dp))
                            }
                        }

                        OutlinedTextField(
                            value = verificationCodeInput,
                            onValueChange = { if (it.length <= 6) verificationCodeInput = it },
                            label = { Text("Código de 6 dígitos", color = NexaTextSecondary) },
                            placeholder = { Text("Ej: 123456", color = NexaTextMuted) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("email_code_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = NexaSurfaceElevated,
                                unfocusedContainerColor = NexaSurfaceElevated,
                                focusedBorderColor = NexaCyan,
                                unfocusedBorderColor = NexaBorder,
                                focusedTextColor = NexaTextPrimary,
                                unfocusedTextColor = NexaTextPrimary
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    isLoading = true
                                    errorMessage = null
                                    val result = repository.verifyEmailCode(pendingEmail, verificationCodeInput)
                                    isLoading = false
                                    if (result.isSuccess) {
                                        successMessage = "¡Correo confirmado con éxito!"
                                        onAuthSuccess()
                                    } else {
                                        errorMessage = result.exceptionOrNull()?.message ?: "Código incorrecto."
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("submit_code_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NexaCyan)
                        ) {
                            Text("Verificar y Continuar", color = Color.Black, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Resend Code with cooldown
                        TextButton(
                            onClick = {
                                coroutineScope.launch {
                                    errorMessage = null
                                    val res = repository.sendVerificationCode(pendingEmail)
                                    if (res.isSuccess) {
                                        successMessage = "Nuevo código enviado."
                                    } else {
                                        errorMessage = res.exceptionOrNull()?.message
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Reenviar código nuevo", color = NexaCyanLight, fontSize = 13.sp)
                        }

                        TextButton(
                            onClick = {
                                showEmailVerifyStep = false
                                onAuthSuccess() // Allow bypassing to feed if already registered
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Omitir por ahora y entrar", color = NexaTextSecondary, fontSize = 12.sp)
                        }

                    } else {
                        // Switch between Login and Register Mode Tabs
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(NexaSurfaceElevated)
                                .padding(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (!isRegisterMode) NexaCyan else Color.Transparent)
                                    .clickable { isRegisterMode = false }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Iniciar Sesión",
                                    color = if (!isRegisterMode) Color.Black else NexaTextSecondary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isRegisterMode) NexaCyan else Color.Transparent)
                                    .clickable { isRegisterMode = true }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Registrarse",
                                    color = if (isRegisterMode) Color.Black else NexaTextSecondary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        if (isRegisterMode) {
                            // Register Fields
                            OutlinedTextField(
                                value = nameInput,
                                onValueChange = { nameInput = it },
                                label = { Text("Nombre completo", color = NexaTextSecondary) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = NexaSurfaceElevated,
                                    unfocusedContainerColor = NexaSurfaceElevated,
                                    focusedBorderColor = NexaCyan,
                                    unfocusedBorderColor = NexaBorder,
                                    focusedTextColor = NexaTextPrimary,
                                    unfocusedTextColor = NexaTextPrimary
                                )
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = usernameInput,
                                onValueChange = { usernameInput = it },
                                label = { Text("Nombre de usuario (@usuario)", color = NexaTextSecondary) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = NexaSurfaceElevated,
                                    unfocusedContainerColor = NexaSurfaceElevated,
                                    focusedBorderColor = NexaCyan,
                                    unfocusedBorderColor = NexaBorder,
                                    focusedTextColor = NexaTextPrimary,
                                    unfocusedTextColor = NexaTextPrimary
                                )
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                        }

                        // Email Field
                        OutlinedTextField(
                            value = emailInput,
                            onValueChange = { emailInput = it },
                            label = { Text(if (isRegisterMode) "Gmail / Correo electrónico" else "Correo o @usuario", color = NexaTextSecondary) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("auth_email_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = NexaSurfaceElevated,
                                unfocusedContainerColor = NexaSurfaceElevated,
                                focusedBorderColor = NexaCyan,
                                unfocusedBorderColor = NexaBorder,
                                focusedTextColor = NexaTextPrimary,
                                unfocusedTextColor = NexaTextPrimary
                            )
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        // Password Field (hashed with salt in repository)
                        OutlinedTextField(
                            value = passwordInput,
                            onValueChange = { passwordInput = it },
                            label = { Text("Contraseña", color = NexaTextSecondary) },
                            singleLine = true,
                            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { showPassword = !showPassword }) {
                                    Icon(
                                        imageVector = if (showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = "Mostrar contraseña",
                                        tint = NexaTextSecondary
                                    )
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("auth_password_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = NexaSurfaceElevated,
                                unfocusedContainerColor = NexaSurfaceElevated,
                                focusedBorderColor = NexaCyan,
                                unfocusedBorderColor = NexaBorder,
                                focusedTextColor = NexaTextPrimary,
                                unfocusedTextColor = NexaTextPrimary
                            )
                        )

                        if (isRegisterMode) {
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(
                                value = birthDateInput,
                                onValueChange = { birthDateInput = it },
                                label = { Text("Fecha de nacimiento (AAAA-MM-DD)", color = NexaTextSecondary) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = NexaSurfaceElevated,
                                    unfocusedContainerColor = NexaSurfaceElevated,
                                    focusedBorderColor = NexaCyan,
                                    unfocusedBorderColor = NexaBorder,
                                    focusedTextColor = NexaTextPrimary,
                                    unfocusedTextColor = NexaTextPrimary
                                )
                            )

                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(
                                value = avatarUrlInput,
                                onValueChange = { avatarUrlInput = it },
                                label = { Text("Foto de perfil URL (opcional)", color = NexaTextSecondary) },
                                singleLine = true,
                                placeholder = { Text("https://...", color = NexaTextMuted) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = NexaSurfaceElevated,
                                    unfocusedContainerColor = NexaSurfaceElevated,
                                    focusedBorderColor = NexaCyan,
                                    unfocusedBorderColor = NexaBorder,
                                    focusedTextColor = NexaTextPrimary,
                                    unfocusedTextColor = NexaTextPrimary
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Error / Success feedback
                        errorMessage?.let {
                            Text(text = it, color = NexaError, fontSize = 13.sp, modifier = Modifier.padding(bottom = 8.dp))
                        }
                        successMessage?.let {
                            Text(text = it, color = NexaSuccess, fontSize = 13.sp, modifier = Modifier.padding(bottom = 8.dp))
                        }

                        // Submit Button
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    isLoading = true
                                    errorMessage = null
                                    successMessage = null

                                    if (isRegisterMode) {
                                        val regResult = repository.registerUser(
                                            name = nameInput,
                                            username = usernameInput,
                                            email = emailInput,
                                            passwordPlain = passwordInput,
                                            birthDate = birthDateInput,
                                            avatarUrl = avatarUrlInput
                                        )
                                        isLoading = false
                                        if (regResult.isSuccess) {
                                            pendingEmail = emailInput.trim().lowercase()
                                            showEmailVerifyStep = true
                                        } else {
                                            errorMessage = regResult.exceptionOrNull()?.message ?: "Error al registrarse"
                                        }
                                    } else {
                                        val loginResult = repository.loginUser(emailInput, passwordInput)
                                        isLoading = false
                                        if (loginResult.isSuccess) {
                                            onAuthSuccess()
                                        } else {
                                            errorMessage = loginResult.exceptionOrNull()?.message ?: "Error al iniciar sesión"
                                        }
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("auth_submit_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NexaCyan)
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(24.dp))
                            } else {
                                Text(
                                    text = if (isRegisterMode) "Crear mi Cuenta" else "Entrar a ClipBook",
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
