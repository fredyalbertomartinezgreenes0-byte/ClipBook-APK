package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NexaBlue
import com.example.ui.theme.NexaCyan
import com.example.ui.theme.NexaPurple

@Composable
fun VerifiedBadgeIcon(
    modifier: Modifier = Modifier,
    size: Int = 16
) {
    Box(
        modifier = modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(
                Brush.linearGradient(
                    colors = listOf(NexaCyan, NexaBlue, NexaPurple)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = "Cuenta verificada",
            tint = Color.White,
            modifier = Modifier.size((size * 0.7f).dp)
        )
    }
}

/**
 * Verified Badge Pill as specified by NEXA rules:
 * Strictly displays only "✓ Cuenta verificada" without revealing any internal administrative dates or status.
 */
@Composable
fun VerifiedBadgePill(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(
                        Color(0xFF0F2547),
                        Color(0xFF1E1B4B)
                    )
                )
            )
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        VerifiedBadgeIcon(size = 14)
        Text(
            text = "✓ Cuenta verificada",
            color = NexaCyan,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
