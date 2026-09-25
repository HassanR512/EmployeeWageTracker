package com.wagetracker.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = Color.White,
    secondary = AccentTeal,
    onSecondary = Color.White,
    background = SurfaceLight,
    surface = CardWhite,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    error = ErrorRed,
    outline = DividerColor
)

private val DarkColors = darkColorScheme(
    primary = AccentTeal,
    secondary = PrimaryBlue,
    background = Color(0xFF12151C),
    surface = Color(0xFF1B1F29),
    onBackground = Color(0xFFE7E9EE),
    onSurface = Color(0xFFE7E9EE),
    error = ErrorRed
)

@Composable
fun WageTrackerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
