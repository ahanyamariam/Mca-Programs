package com.example.lab7.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val PinkLightColorScheme = lightColorScheme(
    primary = PinkPrimary,
    onPrimary = PinkOnPrimary,
    primaryContainer = PinkPrimaryContainer,
    onPrimaryContainer = PinkOnPrimaryContainer,
    secondary = PinkSecondary,
    onSecondary = PinkOnSecondary,
    secondaryContainer = PinkSecondaryContainer,
    onSecondaryContainer = PinkOnSecondaryContainer,
    tertiary = PinkTertiary,
    onTertiary = PinkOnTertiary,
    tertiaryContainer = PinkTertiaryContainer,
    onTertiaryContainer = PinkOnTertiaryContainer,
    background = PinkBackground,
    onBackground = PinkOnBackground,
    surface = PinkSurface,
    onSurface = PinkOnSurface,
    surfaceVariant = PinkSurfaceVariant,
    onSurfaceVariant = PinkOnSurfaceVariant,
    outline = PinkOutline,
    outlineVariant = PinkOutlineVariant,
    error = PinkError,
    onError = PinkOnError,
    errorContainer = PinkErrorContainer,
    onErrorContainer = PinkOnErrorContainer,
)

private val PinkDarkColorScheme = darkColorScheme(
    primary = PinkPrimaryDark,
    onPrimary = PinkOnPrimaryDark,
    primaryContainer = PinkPrimaryContainerDark,
    onPrimaryContainer = PinkOnPrimaryContainerDark,
    secondary = PinkSecondaryDark,
    onSecondary = PinkOnSecondaryDark,
    secondaryContainer = PinkSecondaryContainerDark,
    onSecondaryContainer = PinkOnSecondaryContainerDark,
    tertiary = PinkTertiaryDark,
    onTertiary = PinkOnTertiaryDark,
    tertiaryContainer = PinkTertiaryContainerDark,
    onTertiaryContainer = PinkOnTertiaryContainerDark,
    background = PinkBackgroundDark,
    onBackground = PinkOnBackgroundDark,
    surface = PinkSurfaceDark,
    onSurface = PinkOnSurfaceDark,
    surfaceVariant = PinkSurfaceVariantDark,
    onSurfaceVariant = PinkOnSurfaceVariantDark,
    outline = PinkOutlineDark,
    outlineVariant = PinkOutlineVariantDark,
    error = PinkErrorDark,
    onError = PinkOnErrorDark,
    errorContainer = PinkErrorContainerDark,
    onErrorContainer = PinkOnErrorContainerDark,
)

@Composable
fun Lab7Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Disable dynamic color so our custom pink palette always applies
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) PinkDarkColorScheme else PinkLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}