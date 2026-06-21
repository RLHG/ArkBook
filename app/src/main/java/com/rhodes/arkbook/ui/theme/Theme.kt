package com.rhodes.arkbook.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import com.rhodes.arkbook.data.ThemeMode

private val DarkColorScheme = darkColorScheme(
    primary = DarkArkColors.Primary,
    onPrimary = DarkArkColors.OnPrimary,
    primaryContainer = DarkArkColors.Primary.copy(alpha = 0.15f),
    secondary = DarkArkColors.SurfaceLight,
    onSecondary = DarkArkColors.TextPrimary,
    secondaryContainer = DarkArkColors.SurfaceLight,
    background = DarkArkColors.Background,
    onBackground = DarkArkColors.TextPrimary,
    surface = DarkArkColors.Surface,
    onSurface = DarkArkColors.TextPrimary,
    surfaceVariant = DarkArkColors.SurfaceLight,
    onSurfaceVariant = DarkArkColors.TextSecondary,
    error = DarkArkColors.Expense,
    onError = Color.White,
    outline = DarkArkColors.Border,
    outlineVariant = DarkArkColors.BorderLight
)

private val LightColorScheme = lightColorScheme(
    primary = LightArkColors.Primary,
    onPrimary = LightArkColors.OnPrimary,
    primaryContainer = LightArkColors.Primary.copy(alpha = 0.15f),
    secondary = LightArkColors.SurfaceLight,
    onSecondary = LightArkColors.TextPrimary,
    secondaryContainer = LightArkColors.SurfaceLight,
    background = LightArkColors.Background,
    onBackground = LightArkColors.TextPrimary,
    surface = LightArkColors.Surface,
    onSurface = LightArkColors.TextPrimary,
    surfaceVariant = LightArkColors.SurfaceLight,
    onSurfaceVariant = LightArkColors.TextSecondary,
    error = LightArkColors.Expense,
    onError = Color.White,
    outline = LightArkColors.Border,
    outlineVariant = LightArkColors.BorderLight
)

@Composable
fun ArkBookTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val arkColors = if (darkTheme) DarkArkColors else LightArkColors

    CompositionLocalProvider(
        LocalArkColors provides arkColors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = getArkTypography(),
            content = content
        )
    }
}
