package com.example.lab9.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Brand palette – deep navy / indigo + gold accent
val NavyDeep = Color(0xFF0A0E27)
val NavyCard = Color(0xFF141833)
val NavySurface = Color(0xFF1A1F3C)
val IndigoAccent = Color(0xFF5C6BC0)
val IndigoBright = Color(0xFF7986CB)
val GoldAccent = Color(0xFFFFBF00)
val TealAccent = Color(0xFF26C6DA)
val ErrorRed = Color(0xFFEF5350)
val SuccessGreen = Color(0xFF66BB6A)
val TextPrimary = Color(0xFFF0F4FF)
val TextSecondary = Color(0xFF9BA4C4)

private val DarkColorScheme = darkColorScheme(
    primary = IndigoAccent,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF1A2065),
    onPrimaryContainer = IndigoBright,
    secondary = GoldAccent,
    onSecondary = NavyDeep,
    secondaryContainer = Color(0xFF3E2C00),
    onSecondaryContainer = Color(0xFFFFE57F),
    tertiary = TealAccent,
    onTertiary = NavyDeep,
    background = NavyDeep,
    onBackground = TextPrimary,
    surface = NavyCard,
    onSurface = TextPrimary,
    surfaceVariant = NavySurface,
    onSurfaceVariant = TextSecondary,
    error = ErrorRed,
    onError = Color.White,
    outline = Color(0xFF2A3160)
)

@Composable
fun PortalTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography(),
        content = content
    )
}