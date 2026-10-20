package com.gtnix.aguiabranca.presentation.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.gtnix.aguiabranca.presentation.theme.LocalDarkTheme
import com.gtnix.aguiabranca.domain.model.CategoriaOrientacao
import com.gtnix.aguiabranca.domain.model.DivisaoNegocio
import com.gtnix.aguiabranca.presentation.theme.AsaAzul
import com.gtnix.aguiabranca.presentation.theme.CianoMarca
import com.gtnix.aguiabranca.presentation.theme.LaranjaMarca
import com.gtnix.aguiabranca.presentation.theme.MarinhoInstitucional
import com.gtnix.aguiabranca.presentation.theme.RoxoMarca
import com.gtnix.aguiabranca.presentation.theme.VerdeMarca

const val ORIENTACAO_DIVISAO_GRUPO_LABEL = "Todo o grupo"

fun orientacaoDivisaoLabel(divisao: DivisaoNegocio?): String =
    divisao?.label ?: ORIENTACAO_DIVISAO_GRUPO_LABEL

fun orientacaoCategoriaLabel(categoria: CategoriaOrientacao): String = when (categoria) {
    CategoriaOrientacao.REDUCAO_CUSTOS -> "Redução de Custos"
    CategoriaOrientacao.QUALIDADE_SERVICO -> "Qualidade de Serviço"
    CategoriaOrientacao.INOVACAO_TECNOLOGICA -> "Inovação Tecnológica"
    CategoriaOrientacao.SUSTENTABILIDADE -> "Sustentabilidade"
    CategoriaOrientacao.SEGURANCA -> "Segurança"
    CategoriaOrientacao.EXPERIENCIA_CLIENTE -> "Experiência do Cliente"
    CategoriaOrientacao.EFICIENCIA_OPERACIONAL -> "Eficiência Operacional"
}

fun orientacaoCategoriaShortLabel(categoria: CategoriaOrientacao): String = when (categoria) {
    CategoriaOrientacao.REDUCAO_CUSTOS -> "Custos"
    CategoriaOrientacao.QUALIDADE_SERVICO -> "Qualidade"
    CategoriaOrientacao.INOVACAO_TECNOLOGICA -> "Inovação"
    CategoriaOrientacao.SUSTENTABILIDADE -> "Sustentabilidade"
    CategoriaOrientacao.SEGURANCA -> "Segurança"
    CategoriaOrientacao.EXPERIENCIA_CLIENTE -> "Cliente"
    CategoriaOrientacao.EFICIENCIA_OPERACIONAL -> "Eficiência"
}

/** O marinho some sobre o fundo escuro, então no tema escuro a categoria usa o azul da marca. */
@Composable
fun orientacaoCategoriaColor(categoria: CategoriaOrientacao): Color = when (categoria) {
    CategoriaOrientacao.REDUCAO_CUSTOS -> AsaAzul
    CategoriaOrientacao.QUALIDADE_SERVICO -> VerdeMarca
    CategoriaOrientacao.INOVACAO_TECNOLOGICA -> RoxoMarca
    CategoriaOrientacao.SUSTENTABILIDADE -> VerdeMarca
    CategoriaOrientacao.SEGURANCA -> LaranjaMarca
    CategoriaOrientacao.EXPERIENCIA_CLIENTE -> CianoMarca
    CategoriaOrientacao.EFICIENCIA_OPERACIONAL ->
        if (LocalDarkTheme.current) AsaAzul else MarinhoInstitucional
}

fun orientacaoCategoriaIcon(categoria: CategoriaOrientacao): ImageVector = when (categoria) {
    CategoriaOrientacao.REDUCAO_CUSTOS -> Icons.AutoMirrored.Filled.TrendingDown
    CategoriaOrientacao.QUALIDADE_SERVICO -> Icons.Default.Star
    CategoriaOrientacao.INOVACAO_TECNOLOGICA -> Icons.Default.Lightbulb
    CategoriaOrientacao.SUSTENTABILIDADE -> Icons.Default.Eco
    CategoriaOrientacao.SEGURANCA -> Icons.Default.Security
    CategoriaOrientacao.EXPERIENCIA_CLIENTE -> Icons.Default.SupportAgent
    CategoriaOrientacao.EFICIENCIA_OPERACIONAL -> Icons.Default.Speed
}

fun orientacaoCategoriaIconForList(categoria: CategoriaOrientacao): ImageVector = when (categoria) {
    CategoriaOrientacao.REDUCAO_CUSTOS -> Icons.Default.Savings
    CategoriaOrientacao.QUALIDADE_SERVICO -> Icons.Default.Star
    CategoriaOrientacao.INOVACAO_TECNOLOGICA -> Icons.Default.Bolt
    CategoriaOrientacao.SUSTENTABILIDADE -> Icons.Default.Eco
    CategoriaOrientacao.SEGURANCA -> Icons.Default.Shield
    CategoriaOrientacao.EXPERIENCIA_CLIENTE -> Icons.Default.People
    CategoriaOrientacao.EFICIENCIA_OPERACIONAL -> Icons.Default.Speed
}
