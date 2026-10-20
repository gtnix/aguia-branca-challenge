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
import androidx.compose.material.icons.filled.HealthAndSafety
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
import com.gtnix.aguiabranca.presentation.theme.AsaAzul
import com.gtnix.aguiabranca.presentation.theme.AzulAcao
import com.gtnix.aguiabranca.presentation.theme.CianoMarca
import com.gtnix.aguiabranca.presentation.theme.LaranjaMarca
import com.gtnix.aguiabranca.presentation.theme.MarinhoInstitucional
import com.gtnix.aguiabranca.presentation.theme.RoxoMarca
import com.gtnix.aguiabranca.presentation.theme.VerdeMarca
import com.gtnix.aguiabranca.presentation.theme.AmareloMarca
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.domain.model.Conquista
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
    TipoConquista.GUARDIAO_SEGURANCA -> R.string.conquista_guardiao_seguranca_titulo
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

@Composable
fun Conquista.tituloExibicao(): String = titulo.ifBlank { tipo.titulo() }

fun TipoConquista.icon(): ImageVector = when (this) {
    TipoConquista.BEM_VINDO -> Icons.Default.Celebration
    TipoConquista.PRIMEIRA_IDEIA -> Icons.Default.Lightbulb
    TipoConquista.INOVADOR -> Icons.Default.Stars
    TipoConquista.VISIONARIO_IDEIAS -> Icons.Default.Visibility
    TipoConquista.PRIMEIRA_APROVACAO -> Icons.Default.CheckCircle
    TipoConquista.INFLUENCIADOR -> Icons.Default.EmojiEvents
    TipoConquista.EM_PROJETO -> Icons.Default.RocketLaunch
    TipoConquista.TRANSFORMADOR -> Icons.Default.AutoAwesome
    TipoConquista.GUARDIAO_SEGURANCA -> Icons.Default.HealthAndSafety
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
    TipoConquista.BEM_VINDO -> CianoMarca
    TipoConquista.PRIMEIRA_IDEIA -> AmareloMarca
    TipoConquista.INOVADOR -> LaranjaMarca
    TipoConquista.VISIONARIO_IDEIAS -> RoxoMarca
    TipoConquista.PRIMEIRA_APROVACAO -> VerdeMarca
    TipoConquista.INFLUENCIADOR -> AmareloMarca
    TipoConquista.EM_PROJETO -> AsaAzul
    TipoConquista.TRANSFORMADOR -> AzulAcao
    TipoConquista.GUARDIAO_SEGURANCA -> VerdeMarca
    TipoConquista.LIDER_INOVACAO -> LaranjaMarca
    TipoConquista.CURADOR -> AsaAzul
    TipoConquista.MENTOR -> CianoMarca
    TipoConquista.EXECUTOR -> MarinhoInstitucional
    TipoConquista.LIDER_RESULTADOS -> VerdeMarca
    TipoConquista.ESTRATEGISTA -> RoxoMarca
    TipoConquista.VISIONARIO_ESTRATEGICO -> RoxoMarca
    TipoConquista.TRANSFORMADOR_ESTRATEGICO -> AzulAcao
}
