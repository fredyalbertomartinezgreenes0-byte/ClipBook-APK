package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NexaTopBar(
    onOpenDrawer: () -> Unit,
    onOpenSearch: () -> Unit,
    onOpenMessages: () -> Unit,
    userCoins: Long = 1000L,
    onOpenWallet: () -> Unit = {}
) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Clip",
                    color = NexaCyan,
                    fontWeight = FontWeight.Black,
                    fontSize = 22.sp,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Book",
                    color = NexaPurpleLight,
                    fontWeight = FontWeight.Black,
                    fontSize = 22.sp,
                    letterSpacing = 1.sp
                )
            }
        },
        navigationIcon = {
            IconButton(
                onClick = onOpenDrawer,
                modifier = Modifier.testTag("top_menu_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menú",
                    tint = NexaTextPrimary
                )
            }
        },
        actions = {
            // Coins Balance Quick Access
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = NexaGold.copy(alpha = 0.15f),
                modifier = Modifier
                    .clickable { onOpenWallet() }
                    .padding(end = 4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                ) {
                    Text("🪙", fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = if (userCoins > 999999) "999K+" else "$userCoins",
                        color = NexaGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }

            IconButton(
                onClick = onOpenSearch,
                modifier = Modifier.testTag("top_search_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Buscar",
                    tint = NexaTextPrimary
                )
            }
            IconButton(
                onClick = onOpenMessages,
                modifier = Modifier.testTag("top_messages_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Chat,
                    contentDescription = "Mensajes",
                    tint = NexaCyan
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = NexaBackground,
            titleContentColor = NexaTextPrimary
        ),
        modifier = Modifier.background(NexaBackground)
    )
}
