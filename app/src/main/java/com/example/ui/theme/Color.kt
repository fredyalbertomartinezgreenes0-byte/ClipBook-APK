package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Canvas & Surfaces
val NexaBackground = Color(0xFF090D16)
val NexaSurface = Color(0xFF111827)
val NexaSurfaceElevated = Color(0xFF1A2234)
val NexaSurfaceHighlight = Color(0xFF232D42)
val NexaBorder = Color(0xFF28344E)
val NexaBorderSubtle = Color(0xFF1E283C)

// Brand Core Accents (Blue, Cyan, Purple)
val NexaBlue = Color(0xFF2563EB)
val NexaBlueLight = Color(0xFF60A5FA)
val NexaCyan = Color(0xFF06B6D4)
val NexaCyanLight = Color(0xFF67E8F9)
val NexaPurple = Color(0xFF8B5CF6)
val NexaPurpleLight = Color(0xFFC084FC)

// Text & Content
val NexaTextPrimary = Color(0xFFF3F4F6)
val NexaTextSecondary = Color(0xFF9CA3AF)
val NexaTextMuted = Color(0xFF64748B)

// Status & Verification
val NexaVerifiedBadge = Color(0xFF00D2FF)
val NexaSuccess = Color(0xFF10B981)
val NexaWarning = Color(0xFFF59E0B)
val NexaError = Color(0xFFEF4444)
val NexaGold = Color(0xFFFFD700)
val NexaGoldLight = Color(0xFFFFE57F)

// Gradients
val NexaGradientPrimary = Brush.horizontalGradient(
    colors = listOf(NexaCyan, NexaBlue, NexaPurple)
)

val NexaGradientStory = Brush.linearGradient(
    colors = listOf(NexaCyanLight, NexaBlue, NexaPurpleLight)
)

val NexaGradientCard = Brush.verticalGradient(
    colors = listOf(NexaSurfaceElevated, NexaSurface)
)

val NexaGradientButton = Brush.horizontalGradient(
    colors = listOf(Color(0xFF0284C7), Color(0xFF4F46E5), Color(0xFF7C3AED))
)
