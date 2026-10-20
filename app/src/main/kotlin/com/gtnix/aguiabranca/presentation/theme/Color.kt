package com.gtnix.aguiabranca.presentation.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.gtnix.aguiabranca.domain.model.ConquistaTier
import com.gtnix.aguiabranca.domain.model.NivelUsuario

// =============================================================================
// Holding — gab.com.br (dossiê IDENTIDADE_AGUIA_BRANCA)
// Núcleo: marinho #002361, asa #005FC5
// Interface: superfície #003071, intermediário #013171, ação #008FFF,
//            página #F1F4F7, container #E8F0FA, texto #101828
// Acentos (gráfico, status, gamificação): #D7D624 #00A544 #E87C1A #00B7D7 #7A2E85
// =============================================================================

val MarinhoInstitucional = Color(0xFF002361)
val AsaAzul = Color(0xFF005FC5)
val MarinhoSuperficie = Color(0xFF003071)
val MarinhoIntermediario = Color(0xFF013171)
val AzulAcao = Color(0xFF008FFF)
val FundoPagina = Color(0xFFF1F4F7)
val FundoContainer = Color(0xFFE8F0FA)
val TextoInstitucional = Color(0xFF101828)

val AmareloMarca = Color(0xFFD7D624)
val VerdeMarca = Color(0xFF00A544)
val LaranjaMarca = Color(0xFFE87C1A)
val CianoMarca = Color(0xFF00B7D7)
val RoxoMarca = Color(0xFF7A2E85)

// Noite fechada: matiz do marinho #002361 empurrada ao preto (profundidade, não azul aberto).
// Fundo #000814 → card #0A1628 → card alto #102033 → elevado/nav #15283F.
// Primary do escuro = asa #005FC5; #008FFF só em token de ação (CTA pontual / IA), não no chrome.
val NoiteFundo = Color(0xFF000814)
val NoiteCard = Color(0xFF0A1628)
val NoiteCardAlto = Color(0xFF102033)
val NoiteElevado = Color(0xFF15283F)
val NoiteTexto = FundoPagina
val NoiteTextoSecundario = Color(0xFF8B9CB5)
// Branco a 33%: composto sobre NoiteCard ≥ 3:1 (borda de card, barra e botão secundário).
val NoiteOutline = Color(0x55FFFFFF)
// Branco a 20%: divisor visível, abaixo do outline.
val NoiteDivisor = Color(0x33FFFFFF)

// =============================================================================
// LIGHT SCHEME
// =============================================================================

val LightPrimary = MarinhoInstitucional
val LightOnPrimary = Color.White
val LightPrimaryContainer = FundoContainer
val LightOnPrimaryContainer = MarinhoInstitucional

val LightSecondary = AsaAzul
val LightOnSecondary = Color.White
val LightSecondaryContainer = Color(0xFFD6E6F8)
val LightOnSecondaryContainer = MarinhoInstitucional

val LightTertiary = LaranjaMarca
val LightOnTertiary = TextoInstitucional
val LightTertiaryContainer = Color(0xFFF8E6D4)
val LightOnTertiaryContainer = TextoInstitucional

val LightBackground = FundoPagina
val LightOnBackground = TextoInstitucional
val LightSurface = Color.White
val LightOnSurface = TextoInstitucional
val LightSurfaceVariant = FundoContainer
val LightOnSurfaceVariant = Color(0xFF3D4F6A)

val LightOutline = Color(0xFF6B7C93)
val LightOutlineVariant = Color(0xFFD0DCE8)

val LightError = Color(0xFFBA1A1A)
val LightOnError = Color.White
val LightErrorContainer = Color(0xFFFFDAD6)
val LightOnErrorContainer = Color(0xFF410002)

val LightInverseSurface = MarinhoInstitucional
val LightInverseOnSurface = FundoPagina
val LightInversePrimary = AzulAcao
val LightScrim = NoiteFundo
val LightSurfaceTint = AsaAzul

val LightSurfaceDim = Color(0xFFE4EAF1)
val LightSurfaceBright = Color.White
val LightSurfaceContainerLowest = Color.White
val LightSurfaceContainerLow = Color(0xFFF7F9FC)
val LightSurfaceContainer = FundoPagina
val LightSurfaceContainerHigh = FundoContainer
val LightSurfaceContainerHighest = Color(0xFFD6E4F2)

// =============================================================================
// DARK SCHEME — marinho fechado
// primary asa #005FC5 + onPrimary branco (~6,1:1). Ação #008FFF não é primary.
// tertiary #E87C1A com onTertiary #101828 (branco no laranja fica ~2,9:1).
// =============================================================================

val DarkPrimary = AsaAzul
val DarkOnPrimary = Color.White
val DarkPrimaryContainer = NoiteElevado
val DarkOnPrimaryContainer = NoiteTexto

val DarkSecondary = AsaAzul
val DarkOnSecondary = Color.White
val DarkSecondaryContainer = NoiteCardAlto
val DarkOnSecondaryContainer = NoiteTexto

val DarkTertiary = LaranjaMarca
val DarkOnTertiary = TextoInstitucional
val DarkTertiaryContainer = Color(0xFF4A2808)
val DarkOnTertiaryContainer = FundoPagina

val DarkBackground = NoiteFundo
val DarkOnBackground = NoiteTexto
val DarkSurface = NoiteCard
val DarkOnSurface = NoiteTexto
val DarkSurfaceVariant = NoiteCardAlto
val DarkOnSurfaceVariant = NoiteTextoSecundario

val DarkOutline = NoiteOutline
val DarkOutlineVariant = NoiteDivisor

val DarkError = Color(0xFFFFB4AB)
val DarkOnError = Color(0xFF690005)
val DarkErrorContainer = Color(0xFF93000A)
val DarkOnErrorContainer = Color(0xFFFFDAD6)

val DarkInverseSurface = FundoPagina
val DarkInverseOnSurface = TextoInstitucional
val DarkInversePrimary = MarinhoInstitucional
val DarkScrim = NoiteFundo
val DarkSurfaceTint = AsaAzul

val DarkSurfaceDim = Color(0xFF00050A)
val DarkSurfaceBright = NoiteElevado
val DarkSurfaceContainerLowest = NoiteFundo
val DarkSurfaceContainerLow = Color(0xFF060E1A)
val DarkSurfaceContainer = NoiteCard
val DarkSurfaceContainerHigh = NoiteCardAlto
val DarkSurfaceContainerHighest = NoiteElevado

// =============================================================================
// GLASS E SUPERFÍCIES
// =============================================================================

val GlassFill = Color(0xFF102033)

val SurfaceElevatedLight = Color.White
val SurfaceElevatedDark = NoiteElevado

val CardBorderLight = LightOutlineVariant
val CardBorderDark = Color(0x14FFFFFF)

val GlassFillLight = Color(0xF2FFFFFF)
val GlassBorderLight = Color(0x33005FC5)
val SurfaceTintLight = FundoContainer

// =============================================================================
// SEMÂNTICA — acentos do dossiê, não chrome
// =============================================================================

val SuccessGreen = VerdeMarca
val WarningAmber = LaranjaMarca
val ErrorRed = LightError
val AISpark = AzulAcao

val NeutralGray = Color(0xFF6B7C93)
val InfoBlue = AsaAzul
val CompletedGreen = VerdeMarca

val AccentCyan = CianoMarca
val AccentPurple = RoxoMarca

// =============================================================================
// GAMIFICAÇÃO
// =============================================================================

val LevelInicianteStart = Color(0xFF5BA3E8)
val LevelInicianteEnd = AzulAcao
val LevelIniciante = LevelInicianteStart
val LevelEmAscensaoStart = AsaAzul
val LevelEmAscensaoEnd = AzulAcao
val LevelEmAscensao = LevelEmAscensaoStart
val LevelEngajado = LaranjaMarca
val LevelEngajadoEnd = Color(0xFFFFB74D)
val LevelVisionario = WarningAmber
val LevelVisionarioEnd = AmareloMarca
val LevelTransformadorStart = AmareloMarca
val LevelTransformadorEnd = LaranjaMarca
val LevelTransformador = LevelTransformadorStart

fun levelGradientColors(nivel: NivelUsuario): List<Color> = when (nivel) {
    NivelUsuario.INICIANTE -> listOf(LevelInicianteStart, LevelInicianteEnd)
    NivelUsuario.EM_ASCENSAO -> listOf(LevelEmAscensaoStart, LevelEmAscensaoEnd)
    NivelUsuario.ENGAJADO -> listOf(LevelEngajado, LevelEngajadoEnd)
    NivelUsuario.VISIONARIO -> listOf(LevelVisionario, LevelVisionarioEnd)
    NivelUsuario.TRANSFORMADOR -> listOf(LevelTransformadorStart, LevelTransformadorEnd)
}

fun levelBrush(nivel: NivelUsuario): Brush = Brush.linearGradient(levelGradientColors(nivel))

val MedalGold = AmareloMarca
val MedalSilver = Color(0xFFC0C0C0)
val MedalBronze = Color(0xFFCD7F32)
val MedalGoldGlow = Color(0x33D7D624)
val MedalSilverGlow = Color(0x33C0C0C0)
val MedalBronzeGlow = Color(0x33CD7F32)

val TierBronzeStart = Color(0xFFCD7F32)
val TierBronzeEnd = Color(0xFF8B5A2B)
val TierPrataStart = Color(0xFFC0C0C0)
val TierPrataEnd = Color(0xFF808080)
val TierOuroStart = AmareloMarca
val TierOuroEnd = LaranjaMarca
val TierPlatinaStart = Color(0xFFE5E4E2)
val TierPlatinaEnd = AzulAcao

fun tierGradientColors(tier: ConquistaTier): List<Color> = when (tier) {
    ConquistaTier.BRONZE -> listOf(TierBronzeStart, TierBronzeEnd)
    ConquistaTier.PRATA -> listOf(TierPrataStart, TierPrataEnd)
    ConquistaTier.OURO -> listOf(TierOuroStart, TierOuroEnd)
    ConquistaTier.PLATINA -> listOf(TierPlatinaStart, TierPlatinaEnd)
}

fun tierBrush(tier: ConquistaTier): Brush = Brush.linearGradient(tierGradientColors(tier))

/** Cor única do tier para traço e ícone; no escuro sobe o tom para manter contraste. */
fun tierAccent(tier: ConquistaTier, isDark: Boolean): Color = when (tier) {
    ConquistaTier.BRONZE -> Color(0xFFCD7F32)
    ConquistaTier.PRATA -> if (isDark) Color(0xFFC9D1DB) else Color(0xFF7D8794)
    ConquistaTier.OURO -> if (isDark) Color(0xFFE3C55A) else Color(0xFFC9A227)
    ConquistaTier.PLATINA -> if (isDark) Color(0xFF8FB4FF) else Color(0xFF5B8DEF)
}
