package com.example.campusconnect.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFF48FB1),              // Light Pink
    onPrimary = Color(0xFF4A0A24),            // Dark Pink/Maroon
    primaryContainer = Color(0xFF6D1B3A),     // Deep Rose
    onPrimaryContainer = Color(0xFFFFD9E3),   // Very Light Pink
    secondary = Color(0xFFE8B4CB),            // Soft Pink
    onSecondary = Color(0xFF46263A),          // Dark Mauve
    secondaryContainer = Color(0xFF5E3C51),   // Muted Rose
    onSecondaryContainer = Color(0xFFFFD9E8), // Pale Pink
    tertiary = Color(0xFFEFB8C8),             // Blush Pink
    onTertiary = Color(0xFF4A2532),           // Dark Rose
    tertiaryContainer = Color(0xFF633B48),    // Dusty Rose
    onTertiaryContainer = Color(0xFFFFD9E4),  // Light Blush
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF1F1A1C),           // Dark with pink tint
    onBackground = Color(0xFFF0DEE3),         // Light Pink-ish
    surface = Color(0xFF1F1A1C),              // Dark with pink tint
    onSurface = Color(0xFFF0DEE3),            // Light Pink-ish
    surfaceVariant = Color(0xFF524346),       // Dark Mauve
    onSurfaceVariant = Color(0xFFD6C2C6),     // Dusty Pink
    outline = Color(0xFF9E8C90),              // Muted Pink-Grey
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFD81B60),              // Vibrant Pink
    onPrimary = Color(0xFFFFFFFF),            // White
    primaryContainer = Color(0xFFFFD9E3),     // Very Light Pink
    onPrimaryContainer = Color(0xFF3E0021),   // Dark Magenta
    secondary = Color(0xFF984061),            // Rose
    onSecondary = Color(0xFFFFFFFF),          // White
    secondaryContainer = Color(0xFFFFD9E5),   // Pale Pink
    onSecondaryContainer = Color(0xFF3E001E), // Deep Burgundy
    tertiary = Color(0xFFBD4B7A),             // Medium Pink
    onTertiary = Color(0xFFFFFFFF),           // White
    tertiaryContainer = Color(0xFFFFD8E6),    // Soft Pink
    onTertiaryContainer = Color(0xFF3F0025),  // Dark Wine
    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    background = Color(0xFFFFF8F9),           // Very Light Pink-White
    onBackground = Color(0xFF201A1B),         // Almost Black
    surface = Color(0xFFFFF8F9),              // Very Light Pink-White
    onSurface = Color(0xFF201A1B),            // Almost Black
    surfaceVariant = Color(0xFFF3DEE1),       // Light Blush
    onSurfaceVariant = Color(0xFF514346),     // Dark Mauve
    outline = Color(0xFF847376),              // Muted Pink-Grey
)

@Composable
fun CampusConnectTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}