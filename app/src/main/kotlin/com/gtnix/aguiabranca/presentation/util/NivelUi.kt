package com.gtnix.aguiabranca.presentation.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.outlined.Star
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.gtnix.aguiabranca.domain.model.NivelUsuario
import com.gtnix.aguiabranca.presentation.theme.LevelEmAscensao
import com.gtnix.aguiabranca.presentation.theme.LevelEngajado
import com.gtnix.aguiabranca.presentation.theme.LevelIniciante
import com.gtnix.aguiabranca.presentation.theme.LevelTransformador
import com.gtnix.aguiabranca.presentation.theme.LevelVisionario
import com.gtnix.aguiabranca.presentation.theme.levelGradientColors

fun NivelUsuario.nivelIcon(): ImageVector = when (icone) {
    "star_outline" -> Icons.Outlined.Star
    "trending_up" -> Icons.AutoMirrored.Filled.TrendingUp
    "auto_awesome" -> Icons.Filled.AutoAwesome
    "bolt" -> Icons.Filled.Bolt
    "emoji_events" -> Icons.Filled.EmojiEvents
    else -> Icons.Outlined.Star
}

fun NivelUsuario.nivelColor(): Color = levelGradientColors(this).first()

fun nivelIconAndColor(nivel: NivelUsuario): Pair<ImageVector, Color> =
    nivel.nivelIcon() to when (nivel) {
        NivelUsuario.INICIANTE -> LevelIniciante
        NivelUsuario.EM_ASCENSAO -> LevelEmAscensao
        NivelUsuario.ENGAJADO -> LevelEngajado
        NivelUsuario.VISIONARIO -> LevelVisionario
        NivelUsuario.TRANSFORMADOR -> LevelTransformador
    }
