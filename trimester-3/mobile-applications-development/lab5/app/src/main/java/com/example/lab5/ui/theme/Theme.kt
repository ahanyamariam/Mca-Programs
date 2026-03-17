package com.example.lab5.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import android.app.Activity

private val PinkDarkColorScheme = darkColorScheme(
    primary = PinkPrimaryLight,
    onPrimary = Color.Black,
    primaryContainer = PinkPrimaryDark,
    onPrimaryContainer = PinkShimmer,
    secondary = PinkAccent,
    onSecondary = Color.Black,
    secondaryContainer = PinkPrimaryDark,
    onSecondaryContainer = PinkShimmer,
    tertiary = PinkSecondary,
    background = PinkBackgroundDark,
    onBackground = OnPinkSurfaceDark,
    surface = PinkSurfaceDark,
    onSurface = OnPinkSurfaceDark,
    surfaceVariant = PinkCardDark,
    onSurfaceVariant = OnPinkSurfaceDark,
)

private val PinkLightColorScheme = lightColorScheme(
    primary = PinkPrimary,
    onPrimary = OnPinkPrimary,
    primaryContainer = PinkShimmer,
    onPrimaryContainer = PinkPrimaryDark,
    secondary = PinkSecondary,
    onSecondary = OnPinkPrimary,
    secondaryContainer = PinkPrimaryLight,
    onSecondaryContainer = PinkPrimaryDark,
    tertiary = PinkAccent,
    background = PinkBackgroundLight,
    onBackground = OnPinkSurface,
    surface = PinkSurfaceLight,
    onSurface = OnPinkSurface,
    surfaceVariant = PinkCardLight,
    onSurfaceVariant = OnPinkSurface,
)

@Composable
fun Lab5Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) PinkDarkColorScheme else PinkLightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}