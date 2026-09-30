package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.navigation.NexaScreen
import com.example.ui.theme.*

@Composable
fun NexaBottomBar(
    currentScreen: NexaScreen,
    unreadNotificationsCount: Int = 0,
    onNavigate: (NexaScreen) -> Unit
) {
    NavigationBar(
        modifier = Modifier
            .windowInsetsPadding(WindowInsets.navigationBars)
            .testTag("bottom_nav_bar"),
        containerColor = NexaSurface,
        tonalElevation = 8.dp
    ) {
        // Inicio (Feed)
        NavigationBarItem(
            selected = currentScreen == NexaScreen.FEED,
            onClick = { onNavigate(NexaScreen.FEED) },
            icon = {
                Icon(
                    imageVector = if (currentScreen == NexaScreen.FEED) Icons.Filled.Home else Icons.Outlined.Home,
                    contentDescription = "Inicio"
                )
            },
            label = { Text("Inicio", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = NexaCyan,
                selectedTextColor = NexaCyan,
                indicatorColor = NexaSurfaceElevated,
                unselectedIconColor = NexaTextSecondary,
                unselectedTextColor = NexaTextSecondary
            ),
            modifier = Modifier.testTag("nav_item_feed")
        )

        // Amigos (Friends)
        NavigationBarItem(
            selected = currentScreen == NexaScreen.FRIENDS,
            onClick = { onNavigate(NexaScreen.FRIENDS) },
            icon = {
                Icon(
                    imageVector = if (currentScreen == NexaScreen.FRIENDS) Icons.Filled.People else Icons.Outlined.People,
                    contentDescription = "Amigos"
                )
            },
            label = { Text("Amigos", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = NexaCyan,
                selectedTextColor = NexaCyan,
                indicatorColor = NexaSurfaceElevated,
                unselectedIconColor = NexaTextSecondary,
                unselectedTextColor = NexaTextSecondary
            ),
            modifier = Modifier.testTag("nav_item_friends")
        )

        // Crear (Create Post)
        NavigationBarItem(
            selected = currentScreen == NexaScreen.CREATE_POST,
            onClick = { onNavigate(NexaScreen.CREATE_POST) },
            icon = {
                Icon(
                    imageVector = Icons.Filled.AddCircle,
                    contentDescription = "Crear",
                    tint = NexaCyanLight
                )
            },
            label = { Text("Crear", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = NexaCyan,
                selectedTextColor = NexaCyan,
                indicatorColor = NexaSurfaceElevated,
                unselectedIconColor = NexaTextSecondary,
                unselectedTextColor = NexaTextSecondary
            ),
            modifier = Modifier.testTag("nav_item_create")
        )

        // Notificaciones (Notifications)
        NavigationBarItem(
            selected = currentScreen == NexaScreen.NOTIFICATIONS,
            onClick = { onNavigate(NexaScreen.NOTIFICATIONS) },
            icon = {
                BadgedBox(
                    badge = {
                        if (unreadNotificationsCount > 0) {
                            Badge(containerColor = NexaPurple) {
                                Text("$unreadNotificationsCount")
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (currentScreen == NexaScreen.NOTIFICATIONS) Icons.Filled.Notifications else Icons.Outlined.Notifications,
                        contentDescription = "Notificaciones"
                    )
                }
            },
            label = { Text("Avisos", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = NexaCyan,
                selectedTextColor = NexaCyan,
                indicatorColor = NexaSurfaceElevated,
                unselectedIconColor = NexaTextSecondary,
                unselectedTextColor = NexaTextSecondary
            ),
            modifier = Modifier.testTag("nav_item_notifications")
        )

        // Perfil (Profile)
        NavigationBarItem(
            selected = currentScreen == NexaScreen.PROFILE,
            onClick = { onNavigate(NexaScreen.PROFILE) },
            icon = {
                Icon(
                    imageVector = if (currentScreen == NexaScreen.PROFILE) Icons.Filled.AccountCircle else Icons.Outlined.AccountCircle,
                    contentDescription = "Perfil"
                )
            },
            label = { Text("Perfil", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = NexaCyan,
                selectedTextColor = NexaCyan,
                indicatorColor = NexaSurfaceElevated,
                unselectedIconColor = NexaTextSecondary,
                unselectedTextColor = NexaTextSecondary
            ),
            modifier = Modifier.testTag("nav_item_profile")
        )
    }
}
