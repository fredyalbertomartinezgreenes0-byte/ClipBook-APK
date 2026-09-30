package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.UserEntity
import com.example.data.repository.NexaRepository
import com.example.ui.components.formatTime
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen(
    repository: NexaRepository,
    currentUser: UserEntity?,
    onBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Códigos", "Denuncias", "Usuarios", "Auditoría")

    val giftCodes by repository.getAllGiftCodes().collectAsState(initial = emptyList())
    val redemptions by repository.getAllRedemptions().collectAsState(initial = emptyList())
    val reports by repository.getAllReports().collectAsState(initial = emptyList())
    val allUsers by repository.getAllUsers().collectAsState(initial = emptyList())
    val securityLogs by repository.getAllSecurityLogs().collectAsState(initial = emptyList())

    var newlyGeneratedCode by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panel de Administración ClipBook", fontWeight = FontWeight.Bold, fontSize = 17.sp) },
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
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = NexaSurfaceElevated,
                contentColor = NexaCyan
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title, fontSize = 12.sp, fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal) }
                    )
                }
            }

            when (selectedTab) {
                0 -> {
                    // CÓDIGOS DE REGALO ADMIN
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp),
                        contentPadding = PaddingValues(bottom = 60.dp)
                    ) {
                        item {
                            Button(
                                onClick = {
                                    coroutineScope.launch {
                                        val code = repository.createGiftCode(currentUser?.id ?: 1L)
                                        newlyGeneratedCode = code
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = NexaCyan),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("admin_generate_code_button")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Generar Código Criptográfico (NEXA-XXXX...)", color = Color.Black, fontWeight = FontWeight.Bold)
                            }

                            newlyGeneratedCode?.let { code ->
                                Spacer(modifier = Modifier.height(10.dp))
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = NexaSurfaceElevated)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = NexaCyan)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text("Código generado exitosamente:", color = NexaTextSecondary, fontSize = 11.sp)
                                            Text(code, color = NexaCyanLight, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Inventario de Códigos Oficiales (${giftCodes.size})", color = NexaTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        items(giftCodes, key = { it.id }) { codeEntity ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = NexaSurface)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = codeEntity.code,
                                            color = NexaCyanLight,
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = if (codeEntity.isUsed) "Estado: USADO" else "Estado: DISPONIBLE",
                                                color = if (codeEntity.isUsed) NexaTextMuted else NexaSuccess,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                            if (codeEntity.isBlocked) {
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text("(BLOQUEADO)", color = NexaError, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }

                                    Row {
                                        TextButton(
                                            onClick = {
                                                coroutineScope.launch {
                                                    repository.toggleGiftCodeBlocked(codeEntity.id, !codeEntity.isBlocked)
                                                }
                                            }
                                        ) {
                                            Text(if (codeEntity.isBlocked) "Desbloquear" else "Bloquear", color = if (codeEntity.isBlocked) NexaSuccess else NexaWarning, fontSize = 12.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                1 -> {
                    // MODERACIÓN / DENUNCIAS
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp),
                        contentPadding = PaddingValues(bottom = 60.dp)
                    ) {
                        if (reports.isEmpty()) {
                            item {
                                Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                                    Text("No hay denuncias pendientes en cola.", color = NexaTextMuted)
                                }
                            }
                        } else {
                            items(reports, key = { it.id }) { report ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 5.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = NexaSurface)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "${report.targetType} #${report.targetId}",
                                                color = NexaWarning,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp
                                            )
                                            Text(
                                                text = report.status,
                                                color = if (report.status == "PENDING") NexaWarning else NexaSuccess,
                                                fontSize = 11.sp
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text("Motivo: ${report.reason}", color = NexaTextPrimary, fontSize = 13.sp)
                                        if (report.targetPreview.isNotBlank()) {
                                            Text("Vista previa: \"${report.targetPreview}\"", color = NexaTextMuted, fontSize = 11.sp)
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))

                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Button(
                                                onClick = {
                                                    coroutineScope.launch { repository.updateReportStatus(report.id, "SANCTIONED") }
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = NexaError),
                                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Text("Sancionar", fontSize = 11.sp)
                                            }

                                            OutlinedButton(
                                                onClick = {
                                                    coroutineScope.launch { repository.updateReportStatus(report.id, "DISMISSED") }
                                                },
                                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Text("Desestimar", fontSize = 11.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                2 -> {
                    // GESTIÓN DE USUARIOS
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp),
                        contentPadding = PaddingValues(bottom = 60.dp)
                    ) {
                        items(allUsers, key = { it.id }) { usr ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = NexaSurface)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(text = usr.name, color = NexaTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text(text = "@${usr.username} • Rol: ${usr.role}", color = NexaCyan, fontSize = 12.sp)
                                        if (usr.isSuspended) {
                                            Text(text = "SUSPENDIDO", color = NexaError, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    if (usr.id != 1L) {
                                        Row {
                                            TextButton(
                                                onClick = {
                                                    coroutineScope.launch {
                                                        repository.suspendUser(usr.id, !usr.isSuspended)
                                                    }
                                                }
                                            ) {
                                                Text(
                                                    text = if (usr.isSuspended) "Reactivar" else "Suspender",
                                                    color = if (usr.isSuspended) NexaSuccess else NexaError,
                                                    fontSize = 12.sp
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                3 -> {
                    // LOGS DE AUDITORÍA Y SEGURIDAD
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp),
                        contentPadding = PaddingValues(bottom = 60.dp)
                    ) {
                        items(securityLogs, key = { it.id }) { log ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = NexaSurface)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = log.eventType, color = NexaCyanLight, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        Text(text = log.details, color = NexaTextSecondary, fontSize = 11.sp)
                                    }
                                    Text(text = formatTime(log.timestamp), color = NexaTextMuted, fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
