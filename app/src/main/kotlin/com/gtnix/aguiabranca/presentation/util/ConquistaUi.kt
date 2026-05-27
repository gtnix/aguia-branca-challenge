package com.gtnix.aguiabranca.presentation.util

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.domain.model.TipoConquista

@StringRes
fun TipoConquista.tituloResId(): Int = when (this) {
    TipoConquista.BEM_VINDO -> R.string.achievement_bem_vindo_titulo
    TipoConquista.PRIMEIRA_IDEIA -> R.string.conquista_primeira_ideia_titulo
    TipoConquista.INOVADOR -> R.string.conquista_inovador_titulo
    TipoConquista.VISIONARIO_IDEIAS -> R.string.conquista_visionario_ideias_titulo
    TipoConquista.PRIMEIRA_APROVACAO -> R.string.conquista_primeira_aprovacao_titulo
    TipoConquista.INFLUENCIADOR -> R.string.conquista_influenciador_titulo
    TipoConquista.EM_PROJETO -> R.string.conquista_em_projeto_titulo
    TipoConquista.TRANSFORMADOR -> R.string.conquista_transformador_titulo
    TipoConquista.LIDER_INOVACAO -> R.string.conquista_lider_inovacao_titulo
    TipoConquista.CURADOR -> R.string.conquista_curador_titulo
    TipoConquista.MENTOR -> R.string.conquista_mentor_titulo
    TipoConquista.EXECUTOR -> R.string.conquista_executor_titulo
    TipoConquista.LIDER_RESULTADOS -> R.string.conquista_lider_resultados_titulo
    TipoConquista.ESTRATEGISTA -> R.string.conquista_estrategista_titulo
    TipoConquista.VISIONARIO_ESTRATEGICO -> R.string.conquista_visionario_estrategico_titulo
    TipoConquista.TRANSFORMADOR_ESTRATEGICO -> R.string.conquista_transformador_estrategico_titulo
}

@Composable
fun TipoConquista.titulo(): String = stringResource(tituloResId())

fun TipoConquista.icon(): ImageVector = when (this) {
    TipoConquista.BEM_VINDO -> Icons.Default.Celebration
    TipoConquista.PRIMEIRA_IDEIA -> Icons.Default.Lightbulb
    TipoConquista.INOVADOR -> Icons.Default.Stars
    TipoConquista.VISIONARIO_IDEIAS -> Icons.Default.Visibility
    TipoConquista.PRIMEIRA_APROVACAO -> Icons.Default.CheckCircle
    TipoConquista.INFLUENCIADOR -> Icons.Default.EmojiEvents
    TipoConquista.EM_PROJETO -> Icons.Default.RocketLaunch
    TipoConquista.TRANSFORMADOR -> Icons.Default.AutoAwesome
    TipoConquista.LIDER_INOVACAO -> Icons.Default.MilitaryTech
    TipoConquista.CURADOR -> Icons.Default.RateReview
    TipoConquista.MENTOR -> Icons.Default.School
    TipoConquista.EXECUTOR -> Icons.Default.Build
    TipoConquista.LIDER_RESULTADOS -> Icons.AutoMirrored.Filled.TrendingUp
    TipoConquista.ESTRATEGISTA -> Icons.Default.Flag
    TipoConquista.VISIONARIO_ESTRATEGICO -> Icons.Default.Insights
    TipoConquista.TRANSFORMADOR_ESTRATEGICO -> Icons.Default.WorkspacePremium
}

fun TipoConquista.color(): Color = when (this) {
    TipoConquista.BEM_VINDO -> Color(0xFF00BCD4)
    TipoConquista.PRIMEIRA_IDEIA -> Color(0xFFFFC107)
    TipoConquista.INOVADOR -> Color(0xFFFF9800)
    TipoConquista.VISIONARIO_IDEIAS -> Color(0xFF9C27B0)
    TipoConquista.PRIMEIRA_APROVACAO -> Color(0xFF4CAF50)
    TipoConquista.INFLUENCIADOR -> Color(0xFFFFD700)
    TipoConquista.EM_PROJETO -> Color(0xFF2196F3)
    TipoConquista.TRANSFORMADOR -> Color(0xFFE91E63)
    TipoConquista.LIDER_INOVACAO -> Color(0xFFFF5722)
    TipoConquista.CURADOR -> Color(0xFF3F51B5)
    TipoConquista.MENTOR -> Color(0xFF009688)
    TipoConquista.EXECUTOR -> Color(0xFF795548)
    TipoConquista.LIDER_RESULTADOS -> Color(0xFF4CAF50)
    TipoConquista.ESTRATEGISTA -> Color(0xFF673AB7)
    TipoConquista.VISIONARIO_ESTRATEGICO -> Color(0xFF9C27B0)
    TipoConquista.TRANSFORMADOR_ESTRATEGICO -> Color(0xFFE91E63)
}
