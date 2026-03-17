package com.example.cia3.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp

// ── Color scheme factory ──

fun getColorScheme(accentIndex: Int, isDark: Boolean): ColorScheme {
    return when (accentIndex) {
        1 -> if (isDark) darkBlueScheme() else lightBlueScheme()
        2 -> if (isDark) darkGreenScheme() else lightGreenScheme()
        3 -> if (isDark) darkRedScheme() else lightRedScheme()
        4 -> if (isDark) darkPurpleScheme() else lightPurpleScheme()
        else -> if (isDark) darkPinkScheme() else lightPinkScheme()
    }
}

// ── Font scale helper ──

fun getScaledTypography(fontSizeIndex: Int): Typography {
    val scale = when (fontSizeIndex) {
        0 -> 0.85f  // small
        2 -> 1.15f  // large
        else -> 1f  // medium
    }
    return Typography(
        displayLarge = Typography.displayLarge.copy(fontSize = (32 * scale).sp),
        headlineLarge = Typography.headlineLarge.copy(fontSize = (28 * scale).sp),
        headlineMedium = Typography.headlineMedium.copy(fontSize = (24 * scale).sp),
        titleLarge = Typography.titleLarge.copy(fontSize = (20 * scale).sp),
        titleMedium = Typography.titleMedium.copy(fontSize = (16 * scale).sp),
        bodyLarge = Typography.bodyLarge.copy(fontSize = (16 * scale).sp),
        bodyMedium = Typography.bodyMedium.copy(fontSize = (14 * scale).sp),
        bodySmall = Typography.bodySmall.copy(fontSize = (12 * scale).sp),
        labelLarge = Typography.labelLarge.copy(fontSize = (14 * scale).sp),
        labelSmall = Typography.labelSmall.copy(fontSize = (11 * scale).sp)
    )
}

@Composable
fun Cia3Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    accentColor: Int = 0,
    fontSizeIndex: Int = 1,
    content: @Composable () -> Unit
) {
    val colorScheme = getColorScheme(accentColor, darkTheme)
    val typography = getScaledTypography(fontSizeIndex)

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        content = content
    )
}

// ══════════════════════════════
//  PINK schemes
// ══════════════════════════════

private fun lightPinkScheme() = lightColorScheme(
    primary = PinkPrimary,
    onPrimary = PinkOnPrimary,
    primaryContainer = PinkPrimaryLight,
    onPrimaryContainer = PinkPrimaryDark,
    secondary = PinkSecondary,
    onSecondary = PinkOnSecondary,
    secondaryContainer = PinkSecondaryLight,
    onSecondaryContainer = PinkPrimaryDark,
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
    errorContainer = PinkErrorContainer
)

private fun darkPinkScheme() = darkColorScheme(
    primary = DarkPinkPrimary,
    onPrimary = DarkPinkOnPrimary,
    primaryContainer = DarkPinkPrimaryContainer,
    onPrimaryContainer = DarkPinkOnPrimaryContainer,
    secondary = DarkPinkSecondary,
    onSecondary = DarkPinkOnSecondary,
    background = DarkPinkBackground,
    onBackground = DarkPinkOnSurface,
    surface = DarkPinkSurface,
    onSurface = DarkPinkOnSurface,
    surfaceVariant = DarkPinkSurfaceVariant,
    onSurfaceVariant = DarkPinkOnSurfaceVariant,
    outline = DarkPinkOutline
)

// ══════════════════════════════
//  BLUE schemes
// ══════════════════════════════

private fun lightBlueScheme() = lightColorScheme(
    primary = BluePrimary,
    onPrimary = PinkOnPrimary,
    primaryContainer = BluePrimaryLight,
    onPrimaryContainer = BluePrimaryDark,
    secondary = BlueSecondary,
    onSecondary = PinkOnSecondary,
    secondaryContainer = BlueSecondaryLight,
    onSecondaryContainer = BluePrimaryDark,
    background = BlueBackground,
    onBackground = PinkOnBackground,
    surface = PinkSurface,
    onSurface = PinkOnSurface,
    surfaceVariant = BlueSurfaceVariant,
    onSurfaceVariant = BlueOnSurfaceVariant,
    outline = BlueOutline,
    error = PinkError,
    onError = PinkOnError,
    errorContainer = PinkErrorContainer
)

private fun darkBlueScheme() = darkColorScheme(
    primary = DarkBluePrimary,
    onPrimary = DarkBlueOnPrimary,
    primaryContainer = DarkBluePrimaryContainer,
    onPrimaryContainer = DarkBlueOnPrimaryContainer,
    secondary = DarkBlueSecondary,
    onSecondary = DarkBlueOnSecondary,
    background = DarkBlueBackground,
    onBackground = DarkBlueOnSurface,
    surface = DarkBlueSurface,
    onSurface = DarkBlueOnSurface,
    surfaceVariant = DarkBlueSurfaceVariant,
    onSurfaceVariant = DarkBlueOnSurfaceVariant,
    outline = DarkBlueOutline
)

// ══════════════════════════════
//  GREEN schemes
// ══════════════════════════════

private fun lightGreenScheme() = lightColorScheme(
    primary = GreenPrimary,
    onPrimary = PinkOnPrimary,
    primaryContainer = GreenPrimaryLight,
    onPrimaryContainer = GreenPrimaryDark,
    secondary = GreenSecondary,
    onSecondary = PinkOnSecondary,
    secondaryContainer = GreenSecondaryLight,
    onSecondaryContainer = GreenPrimaryDark,
    background = GreenBackground,
    onBackground = PinkOnBackground,
    surface = PinkSurface,
    onSurface = PinkOnSurface,
    surfaceVariant = GreenSurfaceVariant,
    onSurfaceVariant = GreenOnSurfaceVariant,
    outline = GreenOutline,
    error = PinkError,
    onError = PinkOnError,
    errorContainer = PinkErrorContainer
)

private fun darkGreenScheme() = darkColorScheme(
    primary = DarkGreenPrimary,
    onPrimary = DarkGreenOnPrimary,
    primaryContainer = DarkGreenPrimaryContainer,
    onPrimaryContainer = DarkGreenOnPrimaryContainer,
    secondary = DarkGreenSecondary,
    onSecondary = DarkGreenOnSecondary,
    background = DarkGreenBackground,
    onBackground = DarkGreenOnSurface,
    surface = DarkGreenSurface,
    onSurface = DarkGreenOnSurface,
    surfaceVariant = DarkGreenSurfaceVariant,
    onSurfaceVariant = DarkGreenOnSurfaceVariant,
    outline = DarkGreenOutline
)

// ══════════════════════════════
//  RED schemes
// ══════════════════════════════

private fun lightRedScheme() = lightColorScheme(
    primary = RedPrimary,
    onPrimary = PinkOnPrimary,
    primaryContainer = RedPrimaryLight,
    onPrimaryContainer = RedPrimaryDark,
    secondary = RedSecondary,
    onSecondary = PinkOnSecondary,
    secondaryContainer = RedSecondaryLight,
    onSecondaryContainer = RedPrimaryDark,
    background = RedBackground,
    onBackground = PinkOnBackground,
    surface = PinkSurface,
    onSurface = PinkOnSurface,
    surfaceVariant = RedSurfaceVariant,
    onSurfaceVariant = RedOnSurfaceVariant,
    outline = RedOutline,
    error = PinkError,
    onError = PinkOnError,
    errorContainer = PinkErrorContainer
)

private fun darkRedScheme() = darkColorScheme(
    primary = DarkRedPrimary,
    onPrimary = DarkRedOnPrimary,
    primaryContainer = DarkRedPrimaryContainer,
    onPrimaryContainer = DarkRedOnPrimaryContainer,
    secondary = DarkRedSecondary,
    onSecondary = DarkRedOnSecondary,
    background = DarkRedBackground,
    onBackground = DarkRedOnSurface,
    surface = DarkRedSurface,
    onSurface = DarkRedOnSurface,
    surfaceVariant = DarkRedSurfaceVariant,
    onSurfaceVariant = DarkRedOnSurfaceVariant,
    outline = DarkRedOutline
)

// ══════════════════════════════
//  PURPLE schemes
// ══════════════════════════════

private fun lightPurpleScheme() = lightColorScheme(
    primary = PurplePrimary,
    onPrimary = PinkOnPrimary,
    primaryContainer = PurplePrimaryLight,
    onPrimaryContainer = PurplePrimaryDark,
    secondary = PurpleSecondary,
    onSecondary = PinkOnSecondary,
    secondaryContainer = PurpleSecondaryLight,
    onSecondaryContainer = PurplePrimaryDark,
    background = PurpleBackground,
    onBackground = PinkOnBackground,
    surface = PinkSurface,
    onSurface = PinkOnSurface,
    surfaceVariant = PurpleSurfaceVariant,
    onSurfaceVariant = PurpleOnSurfaceVariant,
    outline = PurpleOutline,
    error = PinkError,
    onError = PinkOnError,
    errorContainer = PinkErrorContainer
)

private fun darkPurpleScheme() = darkColorScheme(
    primary = DarkPurplePrimary,
    onPrimary = DarkPurpleOnPrimary,
    primaryContainer = DarkPurplePrimaryContainer,
    onPrimaryContainer = DarkPurpleOnPrimaryContainer,
    secondary = DarkPurpleSecondary,
    onSecondary = DarkPurpleOnSecondary,
    background = DarkPurpleBackground,
    onBackground = DarkPurpleOnSurface,
    surface = DarkPurpleSurface,
    onSurface = DarkPurpleOnSurface,
    surfaceVariant = DarkPurpleSurfaceVariant,
    onSurfaceVariant = DarkPurpleOnSurfaceVariant,
    outline = DarkPurpleOutline
)