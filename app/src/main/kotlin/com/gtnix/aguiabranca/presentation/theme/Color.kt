package com.gtnix.aguiabranca.presentation.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.gtnix.aguiabranca.domain.model.ConquistaTier
import com.gtnix.aguiabranca.domain.model.NivelUsuario

// =============================================================================
// LIGHT SCHEME - Premium Blue Theme
// =============================================================================

val LightPrimary = Color(0xFF0A2540)
val LightOnPrimary = Color(0xFFFFFFFF)
val LightPrimaryContainer = Color(0xFFD6F0FF)
val LightOnPrimaryContainer = Color(0xFF001F33)

val LightSecondary = Color(0xFFFF5C00)
val LightOnSecondary = Color(0xFFFFFFFF)
val LightSecondaryContainer = Color(0xFFFFE0CC)
val LightOnSecondaryContainer = Color(0xFF331200)

val LightTertiary = Color(0xFF00875A)
val LightOnTertiary = Color(0xFFFFFFFF)
val LightTertiaryContainer = Color(0xFFC8F5E0)
val LightOnTertiaryContainer = Color(0xFF002114)

val LightBackground = Color(0xFFF8F9FA)
val LightOnBackground = Color(0xFF1C1C1E)
val LightSurface = Color(0xFFFFFFFF)
val LightOnSurface = Color(0xFF1C1C1E)
val LightSurfaceVariant = Color(0xFFF0F1F3)
val LightOnSurfaceVariant = Color(0xFF44474E)

val LightOutline = Color(0xFF74777F)
val LightOutlineVariant = Color(0xFFC4C6D0)

val LightError = Color(0xFFBA1A1A)
val LightOnError = Color(0xFFFFFFFF)
val LightErrorContainer = Color(0xFFFFDAD6)
val LightOnErrorContainer = Color(0xFF410002)

// =============================================================================
// DARK SCHEME - Electric Mint Theme
// =============================================================================

val DarkPrimary = Color(0xFF00D4B2)
val DarkOnPrimary = Color(0xFF003B30)
val DarkPrimaryContainer = Color(0xFF005244)
val DarkOnPrimaryContainer = Color(0xFF6EFFD8)

val DarkSecondary = Color(0xFFFF7A00)
val DarkOnSecondary = Color(0xFF3D1D00)
val DarkSecondaryContainer = Color(0xFF5C2D00)
val DarkOnSecondaryContainer = Color(0xFFFFDCC2)

val DarkTertiary = Color(0xFF36B37E)
val DarkOnTertiary = Color(0xFF003921)
val DarkTertiaryContainer = Color(0xFF005232)
val DarkOnTertiaryContainer = Color(0xFFA3F5C8)

val DarkBackground = Color(0xFF0E0E10)
val DarkOnBackground = Color(0xFFE5E5EA)
val DarkSurface = Color(0xFF161618)
val DarkOnSurface = Color(0xFFE5E5EA)
val DarkSurfaceVariant = Color(0xFF1C1C1E)
val DarkOnSurfaceVariant = Color(0xFFC4C6D0)

val DarkOutline = Color(0xFF8E9099)
val DarkOutlineVariant = Color(0xFF44474E)

val DarkError = Color(0xFFFFB4AB)
val DarkOnError = Color(0xFF690005)
val DarkErrorContainer = Color(0xFF93000A)
val DarkOnErrorContainer = Color(0xFFFFDAD6)

// =============================================================================
// CUSTOM COLORS - Glassmorphism & Semantic
// =============================================================================

val GlassBorder = Color(0x33FFFFFF)
val GlassFill = Color(0x66161618)

val SuccessGreen = Color(0xFF36B37E)
val WarningAmber = Color(0xFFFFAB00)
val ErrorRed = Color(0xFFFF3B30)

val AISpark = Color(0xFF00D4B2)

// =============================================================================
// STATUS COLORS - Semantic tokens for status indicators
// =============================================================================

val NeutralGray = Color(0xFF8E9099)
val InfoBlue = Color(0xFF3B82F6)
val CompletedGreen = Color(0xFF059669)

// =============================================================================
// GAMIFICATION COLORS - Level badges
// =============================================================================

val LevelInicianteStart = Color(0xFF7FCBF5)
val LevelInicianteEnd = Color(0xFF00D4B2)
val LevelIniciante = LevelInicianteStart
val LevelEmAscensaoStart = Color(0xFF00E5A0)
val LevelEmAscensaoEnd = Color(0xFF00D4B2)
val LevelEmAscensao = LevelEmAscensaoStart
val LevelEngajado = AISpark
val LevelEngajadoEnd = Color(0xFF6EFFD8)
val LevelVisionario = LightSecondary
val LevelVisionarioEnd = WarningAmber
val LevelTransformadorStart = Color(0xFFFFD700)
val LevelTransformadorEnd = Color(0xFFFFA500)
val LevelTransformador = LevelTransformadorStart

fun levelGradientColors(nivel: NivelUsuario): List<Color> = when (nivel) {
    NivelUsuario.INICIANTE -> listOf(LevelInicianteStart, LevelInicianteEnd)
    NivelUsuario.EM_ASCENSAO -> listOf(LevelEmAscensaoStart, LevelEmAscensaoEnd)
    NivelUsuario.ENGAJADO -> listOf(LevelEngajado, LevelEngajadoEnd)
    NivelUsuario.VISIONARIO -> listOf(LevelVisionario, LevelVisionarioEnd)
    NivelUsuario.TRANSFORMADOR -> listOf(LevelTransformadorStart, LevelTransformadorEnd)
}

fun levelBrush(nivel: NivelUsuario): Brush = Brush.linearGradient(levelGradientColors(nivel))

// =============================================================================
// RANKING / PODIUM - Medal accents
// =============================================================================

val MedalGold = Color(0xFFFFD700)
val MedalSilver = Color(0xFFC0C0C0)
val MedalBronze = Color(0xFFCD7F32)
val MedalGoldGlow = Color(0x33FFD700)
val MedalSilverGlow = Color(0x33C0C0C0)
val MedalBronzeGlow = Color(0x33CD7F32)

// =============================================================================
// ACHIEVEMENT TIERS - Metallic badge frames
// =============================================================================

val TierBronzeStart = Color(0xFFCD7F32)
val TierBronzeEnd = Color(0xFF8B5A2B)
val TierPrataStart = Color(0xFFC0C0C0)
val TierPrataEnd = Color(0xFF808080)
val TierOuroStart = Color(0xFFFFD700)
val TierOuroEnd = Color(0xFFFFA500)
val TierPlatinaStart = Color(0xFFE5E4E2)
val TierPlatinaEnd = Color(0xFF00D4B2)

fun tierGradientColors(tier: ConquistaTier): List<Color> = when (tier) {
    ConquistaTier.BRONZE -> listOf(TierBronzeStart, TierBronzeEnd)
    ConquistaTier.PRATA -> listOf(TierPrataStart, TierPrataEnd)
    ConquistaTier.OURO -> listOf(TierOuroStart, TierOuroEnd)
    ConquistaTier.PLATINA -> listOf(TierPlatinaStart, TierPlatinaEnd)
}

fun tierBrush(tier: ConquistaTier): Brush = Brush.linearGradient(tierGradientColors(tier))

// =============================================================================
// PREMIUM SURFACE TOKENS - Unified card system
// =============================================================================

val SurfaceElevatedLight = Color(0xFFFFFFFF)
val SurfaceElevatedDark = Color(0xFF1C1C1E)

val CardBorderLight = Color(0xFFE8ECF1)
val CardBorderDark = Color(0x0FFFFFFF)

val GlassFillLight = Color(0xFFF7F9FC)
val GlassBorderLight = Color(0xFFE8ECF1)
val SurfaceTintLight = Color(0xFFF8FAFC)
