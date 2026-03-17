package com.example.dailyplanner.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightPinkScheme = lightColorScheme(
    primary = PinkPrimary,
    onPrimary = WarmWhite,
    primaryContainer = PinkLight,
    onPrimaryContainer = DeepWine,
    secondary = RoseGold,
    onSecondary = WarmWhite,
    secondaryContainer = PinkSoft,
    onSecondaryContainer = DeepWine,
    tertiary = PinkMedium,
    onTertiary = WarmWhite,
    tertiaryContainer = PinkLight,
    onTertiaryContainer = DeepWine,
    background = WarmWhite,
    onBackground = DeepWine,
    surface = PinkSurface,
    onSurface = DeepWine,
    surfaceVariant = PinkSoft,
    onSurfaceVariant = SoftGray,
    outline = MediumGray,
    outlineVariant = LightGray,
    error = ErrorRed,
    onError = WarmWhite,
)

private val DarkPinkScheme = darkColorScheme(
    primary = PinkLight,
    onPrimary = PinkDark,
    primaryContainer = PinkPrimary,
    onPrimaryContainer = PinkSoft,
    secondary = RoseGold,
    onSecondary = DeepWine,
    secondaryContainer = PinkDark,
    onSecondaryContainer = PinkSoft,
    tertiary = PinkMedium,
    onTertiary = DeepWine,
    tertiaryContainer = PinkDark,
    onTertiaryContainer = PinkSoft,
    background = DeepWine,
    onBackground = PinkSoft,
    surface = DeepWine,
    onSurface = PinkSoft,
    surfaceVariant = PinkDark,
    onSurfaceVariant = PinkLight,
    outline = MediumGray,
    outlineVariant = SoftGray,
    error = ErrorRed,
    onError = WarmWhite,
)

@Composable
fun DailyplannerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkPinkScheme else LightPinkScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}