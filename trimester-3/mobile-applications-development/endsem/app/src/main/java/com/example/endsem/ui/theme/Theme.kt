package com.example.endsem.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = ElectricViolet,
    onPrimary = OnDarkPrimary,
    primaryContainer = VividPurple,
    onPrimaryContainer = SoftLavender,
    secondary = GoldenYellow,
    onSecondary = DeepPurple,
    secondaryContainer = MidnightBlue,
    onSecondaryContainer = GoldenYellow,
    tertiary = WarmOrange,
    onTertiary = OnDarkPrimary,
    background = DarkSurface,
    onBackground = OnDarkPrimary,
    surface = DarkSurfaceVariant,
    onSurface = OnDarkPrimary,
    surfaceVariant = Color(0xFF2A2A2A),
    onSurfaceVariant = OnDarkSecondary,
    error = ErrorRed,
    onError = OnDarkPrimary
)

private val LightColorScheme = lightColorScheme(
    primary = VividPurple,
    onPrimary = OnDarkPrimary,
    primaryContainer = SoftLavender,
    onPrimaryContainer = DeepPurple,
    secondary = WarmOrange,
    onSecondary = OnDarkPrimary,
    secondaryContainer = Color(0xFFFFE0D6),
    onSecondaryContainer = WarmOrange,
    tertiary = GoldenYellow,
    onTertiary = DeepPurple,
    background = LightSurface,
    onBackground = OnLightPrimary,
    surface = LightSurfaceVariant,
    onSurface = OnLightPrimary,
    surfaceVariant = Color(0xFFEEEEEE),
    onSurfaceVariant = OnLightSecondary,
    error = ErrorRed,
    onError = OnDarkPrimary
)

@Composable
fun EndsemTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Disabled to use our custom colors
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
