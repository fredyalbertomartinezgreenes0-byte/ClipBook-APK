package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.entity.UserEntity
import com.example.data.local.entity.VerificationEntity
import com.example.ui.navigation.NexaScreen
import com.example.ui.theme.*

@Composable
fun NexaDrawer(
    currentUser: UserEntity?,
    currentVerification: VerificationEntity?,
    onNavigate: (NexaScreen) -> Unit,
    onCloseDrawer: () -> Unit,
    onLogout: () -> Unit
) {
    ModalDrawerSheet(
        modifier = Modifier
            .width(310.dp)
            .fillMaxHeight(),
        drawerContainerColor = NexaSurface,
        drawerContentColor = NexaTextPrimary
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // User Header Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(NexaSurfaceElevated, NexaSurfaceHighlight)
                        )
                    )
                    .clickable {
                        onCloseDrawer()
                        onNavigate(NexaScreen.PROFILE)
                    }
                    .padding(14.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(
                            model = currentUser?.avatarUrl?.ifBlank { "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=400&q=80" },
                            contentDescription = currentUser?.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = currentUser?.name ?: "Usuario",
                                    color = NexaTextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                if (currentVerification != null && currentVerification.internalStatus == "ACTIVE") {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    VerifiedBadgeIcon(size = 14)
                                }
                            }
                            Text(
                                text = "@${currentUser?.username ?: "usuario"}",
                                color = NexaCyan,
                                fontSize = 13.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // ClipCoins Balance Pill
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = NexaGold.copy(alpha = 0.15f),
                        modifier = Modifier.clickable {
                            onCloseDrawer()
                            onNavigate(NexaScreen.WALLET)
                        }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text("🪙 ${currentUser?.coins ?: 1000} ClipCoins", color = NexaGold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Billetera ›", color = NexaCyan, fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Verification Pill or Request Button (Strict requirement: show ONLY "✓ Cuenta verificada" or "Solicitar verificación")
                    if (currentVerification != null && currentVerification.internalStatus == "ACTIVE") {
                        VerifiedBadgePill()
                    } else {
                        Button(
                            onClick = {
                                onCloseDrawer()
                                onNavigate(NexaScreen.VERIFY_PURCHASE)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NexaBlue),
                            shape = RoundedCornerShape(20.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.VerifiedUser, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Solicitar verificación", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Navigation Links
            DrawerMenuItem(
                icon = Icons.Default.ManageAccounts,
                title = "Administrar Cuenta",
                subtitle = "Perfil, seguridad y datos",
                iconTint = NexaCyan,
                onClick = {
                    onCloseDrawer()
                    onNavigate(NexaScreen.ACCOUNT_MANAGEMENT)
                }
            )

            DrawerMenuItem(
                icon = Icons.Default.Paid,
                title = "Billetera ClipCoins",
                subtitle = "Saldo y recompensas",
                iconTint = NexaGold,
                onClick = {
                    onCloseDrawer()
                    onNavigate(NexaScreen.WALLET)
                }
            )

            // Mod Menu for Creator
            if (currentUser?.role == "CREATOR") {
                DrawerMenuItem(
                    icon = Icons.Default.Bolt,
                    title = "Mod Menú Creador",
                    subtitle = "Recargas gratis & Trucos",
                    iconTint = NexaCyan,
                    badge = "⚡ MOD",
                    onClick = {
                        onCloseDrawer()
                        onNavigate(NexaScreen.MOD_MENU)
                    }
                )
            }

            // Navigation Links
            DrawerMenuItem(
                icon = Icons.Default.Chat,
                title = "Mensajes privados",
                badge = "Directo",
                onClick = {
                    onCloseDrawer()
                    onNavigate(NexaScreen.MESSAGES)
                }
            )

            DrawerMenuItem(
                icon = Icons.Default.Group,
                title = "Grupos",
                onClick = {
                    onCloseDrawer()
                    onNavigate(NexaScreen.GROUPS)
                }
            )

            DrawerMenuItem(
                icon = Icons.Default.Public,
                title = "Páginas y Creadores",
                onClick = {
                    onCloseDrawer()
                    onNavigate(NexaScreen.PAGES)
                }
            )

            DrawerMenuItem(
                icon = Icons.Default.CardGiftcard,
                title = "Canjear código",
                subtitle = "Insignia 24h",
                iconTint = NexaCyan,
                onClick = {
                    onCloseDrawer()
                    onNavigate(NexaScreen.GIFT_CODE)
                }
            )

            // Admin Panel for Creator / Admin
            if (currentUser?.role == "CREATOR" || currentUser?.role == "ADMIN") {
                DrawerMenuItem(
                    icon = Icons.Default.AdminPanelSettings,
                    title = "Panel de Administración",
                    iconTint = NexaPurpleLight,
                    badge = "Admin",
                    onClick = {
                        onCloseDrawer()
                        onNavigate(NexaScreen.ADMIN_PANEL)
                    }
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = NexaBorderSubtle
            )

            DrawerMenuItem(
                icon = Icons.Default.Settings,
                title = "Configuración y Privacidad",
                onClick = {
                    onCloseDrawer()
                    onNavigate(NexaScreen.SETTINGS)
                }
            )

            DrawerMenuItem(
                icon = Icons.Default.HelpOutline,
                title = "Centro de ayuda",
                onClick = {
                    onCloseDrawer()
                    onNavigate(NexaScreen.SETTINGS)
                }
            )

            Spacer(modifier = Modifier.weight(1f))

            // Logout / Switch User Button
            OutlinedButton(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("drawer_logout_button"),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = NexaError),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = Brush.horizontalGradient(listOf(NexaError.copy(alpha = 0.5f), NexaError))
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.ExitToApp, contentDescription = null, tint = NexaError)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cambiar de cuenta / Salir")
            }
        }
    }
}

@Composable
fun DrawerMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    badge: String? = null,
    iconTint: Color = NexaTextSecondary,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = title, tint = iconTint, modifier = Modifier.size(22.dp))
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = NexaTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            if (!subtitle.isNullOrBlank()) {
                Text(text = subtitle, color = NexaTextMuted, fontSize = 11.sp)
            }
        }
        if (!badge.isNullOrBlank()) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(NexaPurple.copy(alpha = 0.2f))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(text = badge, color = NexaPurpleLight, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
