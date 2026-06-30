package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = MinimalForest,
    secondary = MinimalSage,
    tertiary = MinimalBgSecondary,
    background = MinimalBgLight,
    surface = PureWhite,
    onPrimary = Color.White,
    onSecondary = MinimalTextDark,
    onTertiary = MinimalTextDark,
    onBackground = MinimalTextDark,
    onSurface = MinimalTextDark,
    surfaceVariant = MinimalBgSecondary,
    onSurfaceVariant = MinimalTextDark,
    outline = MinimalBorder
)

private val DarkColorScheme = darkColorScheme(
    primary = MinimalSage,
    secondary = MinimalForest,
    tertiary = MinimalDarkSurface,
    background = MinimalDarkBg,
    surface = MinimalDarkSurface,
    onPrimary = MinimalTextDark,
    onSecondary = Color.White,
    onTertiary = MinimalDarkText,
    onBackground = MinimalDarkText,
    onSurface = MinimalDarkText,
    surfaceVariant = MinimalDarkSurface,
    onSurfaceVariant = MinimalDarkText,
    outline = MinimalDarkBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
