package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val NexaColorScheme = darkColorScheme(
    primary = NexaCyan,
    onPrimary = Color.Black,
    primaryContainer = NexaBlue,
    onPrimaryContainer = Color.White,
    secondary = NexaPurple,
    onSecondary = Color.White,
    secondaryContainer = NexaSurfaceElevated,
    onSecondaryContainer = NexaPurpleLight,
    tertiary = NexaBlueLight,
    onTertiary = Color.Black,
    background = NexaBackground,
    onBackground = NexaTextPrimary,
    surface = NexaSurface,
    onSurface = NexaTextPrimary,
    surfaceVariant = NexaSurfaceElevated,
    onSurfaceVariant = NexaTextSecondary,
    outline = NexaBorder,
    outlineVariant = NexaBorderSubtle,
    error = NexaError,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Preserve NEXA brand dark aesthetic
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = NexaColorScheme,
        typography = Typography,
        content = content
    )
}
