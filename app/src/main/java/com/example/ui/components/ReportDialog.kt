package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun ReportDialog(
    targetTitle: String,
    onDismiss: () -> Unit,
    onSubmitReport: (reason: String, details: String) -> Unit
) {
    val reasons = listOf(
        "Acoso o intimidación",
        "Amenazas o violencia",
        "Spam o enlaces sospechosos",
        "Fraude o suplantación de identidad",
        "Contenido ilegal o inapropiado",
        "Malware, phishing o estafa"
    )

    var selectedReason by remember { mutableStateOf(reasons.first()) }
    var details by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = NexaSurface,
        title = {
            Text(
                text = "Denunciar a ClipBook Moderación",
                color = NexaTextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp
            )
        },
        text = {
            Column {
                Text(
                    text = "Elemento: $targetTitle",
                    color = NexaCyan,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Selecciona el motivo de la infracción:",
                    color = NexaTextSecondary,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                reasons.forEach { reason ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (selectedReason == reason) NexaSurfaceElevated else NexaSurface)
                            .clickable { selectedReason = reason }
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedReason == reason,
                            onClick = { selectedReason = reason },
                            colors = RadioButtonDefaults.colors(selectedColor = NexaCyan)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = reason,
                            color = NexaTextPrimary,
                            fontSize = 13.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = details,
                    onValueChange = { details = it },
                    placeholder = { Text("Detalles adicionales (opcional)", color = NexaTextMuted, fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
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
        },
        confirmButton = {
            Button(
                onClick = { onSubmitReport(selectedReason, details) },
                colors = ButtonDefaults.buttonColors(containerColor = NexaError)
            ) {
                Text("Enviar Denuncia", color = androidx.compose.ui.graphics.Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = NexaTextSecondary)
            }
        }
    )
}
