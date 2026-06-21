package com.rhodes.arkbook.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class ArkColorPalette(
    val Background: Color,
    val Surface: Color,
    val SurfaceLight: Color,
    val SurfaceLighter: Color,
    val Border: Color,
    val BorderLight: Color,
    val Primary: Color,
    val PrimaryDark: Color,
    val OnPrimary: Color,
    val Expense: Color,
    val Income: Color,
    val Accent: Color,
    val AccentOrange: Color,
    val TextPrimary: Color,
    val TextSecondary: Color,
    val TextTertiary: Color,
    val TextHint: Color,
    val Divider: Color,
    val BackgroundGradient: List<Color>,
    val isLight: Boolean
)

val DarkArkColors = ArkColorPalette(
    Background = Color(0xFF0C0C0C),
    Surface = Color(0xFF161616),
    SurfaceLight = Color(0xFF1A1A1A),
    SurfaceLighter = Color(0xFF222222),
    Border = Color(0xFF2A2A2A),
    BorderLight = Color(0xFF333333),
    Primary = Color(0xFFFFB800),
    PrimaryDark = Color(0xFFFF8C00),
    OnPrimary = Color(0xFF0C0C0C),
    Expense = Color(0xFFFF4757),
    Income = Color(0xFF2ED573),
    Accent = Color(0xFF00D4AA),
    AccentOrange = Color(0xFFFF6B35),
    TextPrimary = Color(0xFFFFFFFF),
    TextSecondary = Color(0xFF8A8A8A),
    TextTertiary = Color(0xFF555555),
    TextHint = Color(0xFF444444),
    Divider = Color(0xFF1A1A1A),
    BackgroundGradient = listOf(Color(0xFF0C0C0C), Color(0xFF161616)),
    isLight = false
)

val LightArkColors = ArkColorPalette(
    Background = Color(0xFFF8F9FA),
    Surface = Color(0xFFFFFFFF),
    SurfaceLight = Color(0xFFF1F3F4),
    SurfaceLighter = Color(0xFFE8EAED),
    Border = Color(0xFFDADCE0),
    BorderLight = Color(0xFFE8EAED),
    Primary = Color(0xFFFFB800),
    PrimaryDark = Color(0xFFFF8C00),
    OnPrimary = Color(0xFFFFFFFF),
    Expense = Color(0xFFFF4757),
    Income = Color(0xFF2ED573),
    Accent = Color(0xFF00D4AA),
    AccentOrange = Color(0xFFFF6B35),
    TextPrimary = Color(0xFF202124),
    TextSecondary = Color(0xFF5F6368),
    TextTertiary = Color(0xFF80868B),
    TextHint = Color(0xFF9AA0A6),
    Divider = Color(0xFFE8EAED),
    BackgroundGradient = listOf(Color(0xFFF8F9FA), Color(0xFFE8EAED)),
    isLight = true
)

val LocalArkColors = staticCompositionLocalOf { DarkArkColors }

object ArkColors {
    val Background: Color @Composable get() = LocalArkColors.current.Background
    val Surface: Color @Composable get() = LocalArkColors.current.Surface
    val SurfaceLight: Color @Composable get() = LocalArkColors.current.SurfaceLight
    val SurfaceLighter: Color @Composable get() = LocalArkColors.current.SurfaceLighter
    val Border: Color @Composable get() = LocalArkColors.current.Border
    val BorderLight: Color @Composable get() = LocalArkColors.current.BorderLight
    val Primary: Color @Composable get() = LocalArkColors.current.Primary
    val PrimaryDark: Color @Composable get() = LocalArkColors.current.PrimaryDark
    val OnPrimary: Color @Composable get() = LocalArkColors.current.OnPrimary
    val Expense: Color @Composable get() = LocalArkColors.current.Expense
    val Income: Color @Composable get() = LocalArkColors.current.Income
    val Accent: Color @Composable get() = LocalArkColors.current.Accent
    val AccentOrange: Color @Composable get() = LocalArkColors.current.AccentOrange
    val TextPrimary: Color @Composable get() = LocalArkColors.current.TextPrimary
    val TextSecondary: Color @Composable get() = LocalArkColors.current.TextSecondary
    val TextTertiary: Color @Composable get() = LocalArkColors.current.TextTertiary
    val TextHint: Color @Composable get() = LocalArkColors.current.TextHint
    val Divider: Color @Composable get() = LocalArkColors.current.Divider
    val BackgroundGradient: List<Color> @Composable get() = LocalArkColors.current.BackgroundGradient
}
