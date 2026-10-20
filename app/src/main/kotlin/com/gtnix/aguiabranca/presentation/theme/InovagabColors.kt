package com.gtnix.aguiabranca.presentation.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class InovagabColors(
    val success: Color,
    val warning: Color,
    val medalGold: Color,
    val glassFill: Color,
    val glassBorder: Color,
    val cardBorder: Color,
    val surfaceElevated: Color,
    val asa: Color,
    val action: Color,
    val cyan: Color,
    val purple: Color,
    val textSecondary: Color,
    val navFill: Color,
    val navBorder: Color
)

val LightInovagabColors = InovagabColors(
    success = VerdeMarca,
    warning = LaranjaMarca,
    medalGold = AmareloMarca,
    glassFill = GlassFillLight,
    glassBorder = GlassBorderLight,
    cardBorder = CardBorderLight,
    surfaceElevated = SurfaceElevatedLight,
    asa = AsaAzul,
    action = AzulAcao,
    cyan = CianoMarca,
    purple = RoxoMarca,
    textSecondary = LightOnSurfaceVariant,
    navFill = Color(0xEBFFFFFF),
    navBorder = Color(0x14002361)
)

val DarkInovagabColors = InovagabColors(
    success = VerdeMarca,
    warning = LaranjaMarca,
    medalGold = AmareloMarca,
    glassFill = GlassFill,
    glassBorder = CardBorderDark,
    cardBorder = CardBorderDark,
    surfaceElevated = SurfaceElevatedDark,
    asa = AsaAzul,
    action = AzulAcao,
    cyan = CianoMarca,
    purple = RoxoMarca,
    textSecondary = NoiteTextoSecundario,
    navFill = NoiteElevado,
    navBorder = Color(0x1FFFFFFF)
)

val LocalInovagabColors = staticCompositionLocalOf { LightInovagabColors }
val LocalDarkTheme = staticCompositionLocalOf { false }
