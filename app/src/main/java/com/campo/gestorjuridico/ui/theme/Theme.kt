package com.example.gestorjuridico.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Colores base extraídos del diseño
val BgDark = Color(0xFF0A0D14)
val CardDark = Color(0xFF131824)
val CardInnerDark = Color(0xFF0E131D)
val BorderSubtle = Color(0xFF222B3D)

val PrimaryBlue = Color(0xFF5A8DEE)
val LightBlueButton = Color(0xFFB4CCF8)
val DarkTextOnLightBlue = Color(0xFF0D1B3E)

val TextPrimary = Color(0xFFF1F5F9)
val TextSecondary = Color(0xFF8C9BAE)

// Colores de estado y categorías
val GreenStatusBg = Color(0xFF102A25)
val GreenStatusBorder = Color(0xFF1A4D3E)
val GreenStatusText = Color(0xFF34D399)

val RedAlertBg = Color(0xFF2D1418)
val RedAlertBorder = Color(0xFF5C232A)
val RedAlertText = Color(0xFFF87171)

val BlueBadgeBg = Color(0xFF132238)
val BlueBadgeBorder = Color(0xFF23416B)
val BlueBadgeText = Color(0xFF60A5FA)

// Colores para la tarjeta de IA Jurídica
val AiCardBg = Color(0xFF181124)
val AiCardBorder = Color(0xFF422766)
val AiBadgeBg = Color(0xFF2C1647)
val AiPurple = Color(0xFF9333EA)
val AiBlue = Color(0xFF60A5FA)

val AiGradientBrush = Brush.horizontalGradient(
    colors = listOf(Color(0xFF8B5CF6), Color(0xFF60A5FA))
)

val AlertBannerBrush = Brush.horizontalGradient(
    colors = listOf(Color(0xFF112442), Color(0xFF121A2B))
)

private val DarkColorPalette = darkColorScheme(
    primary = PrimaryBlue,
    background = BgDark,
    surface = CardDark,
    onPrimary = Color.White,
    onBackground = TextPrimary,
    onSurface = TextPrimary
)

@Composable
fun GestorJuridicoTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorPalette,
        content = content
    )
}