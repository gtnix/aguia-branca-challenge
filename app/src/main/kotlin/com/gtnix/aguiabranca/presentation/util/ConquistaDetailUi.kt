package com.gtnix.aguiabranca.presentation.util

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.domain.model.ConquistaTier
import com.gtnix.aguiabranca.domain.model.Conquista
import com.gtnix.aguiabranca.domain.model.TipoConquista
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@StringRes
fun TipoConquista.descricaoResId(): Int = when (this) {
    TipoConquista.BEM_VINDO -> R.string.achievement_bem_vindo_desc
    TipoConquista.PRIMEIRA_IDEIA -> R.string.conquista_primeira_ideia_desc
    TipoConquista.INOVADOR -> R.string.conquista_inovador_desc
    TipoConquista.VISIONARIO_IDEIAS -> R.string.conquista_visionario_ideias_desc
    TipoConquista.PRIMEIRA_APROVACAO -> R.string.conquista_primeira_aprovacao_desc
    TipoConquista.INFLUENCIADOR -> R.string.conquista_influenciador_desc
    TipoConquista.EM_PROJETO -> R.string.conquista_em_projeto_desc
    TipoConquista.TRANSFORMADOR -> R.string.conquista_transformador_desc
    TipoConquista.GUARDIAO_SEGURANCA -> R.string.conquista_guardiao_seguranca_desc
    TipoConquista.LIDER_INOVACAO -> R.string.conquista_lider_inovacao_desc
    TipoConquista.CURADOR -> R.string.conquista_curador_desc
    TipoConquista.MENTOR -> R.string.conquista_mentor_desc
    TipoConquista.EXECUTOR -> R.string.conquista_executor_desc
    TipoConquista.LIDER_RESULTADOS -> R.string.conquista_lider_resultados_desc
    TipoConquista.ESTRATEGISTA -> R.string.conquista_estrategista_desc
    TipoConquista.VISIONARIO_ESTRATEGICO -> R.string.conquista_visionario_estrategico_desc
    TipoConquista.TRANSFORMADOR_ESTRATEGICO -> R.string.conquista_transformador_estrategico_desc
}

@Composable
fun TipoConquista.descricao(): String = stringResource(descricaoResId())

@Composable
fun Conquista.descricaoExibicao(): String = descricao.ifBlank { tipo.descricao() }

@Composable
fun ConquistaTier.label(): String = stringResource(
    when (this) {
        ConquistaTier.BRONZE -> R.string.conquista_tier_bronze
        ConquistaTier.PRATA -> R.string.conquista_tier_prata
        ConquistaTier.OURO -> R.string.conquista_tier_ouro
        ConquistaTier.PLATINA -> R.string.conquista_tier_platina
    }
)

private enum class ConquistaQuoteCategory {
    BOAS_VINDAS,
    CRIACAO_IDEIAS,
    IMPACTO,
    EXECUCAO,
    CURADORIA,
    ESTRATEGIA
}

private fun TipoConquista.quoteCategory(): ConquistaQuoteCategory = when (this) {
    TipoConquista.BEM_VINDO -> ConquistaQuoteCategory.BOAS_VINDAS
    TipoConquista.PRIMEIRA_IDEIA,
    TipoConquista.INOVADOR,
    TipoConquista.VISIONARIO_IDEIAS -> ConquistaQuoteCategory.CRIACAO_IDEIAS
    TipoConquista.PRIMEIRA_APROVACAO,
    TipoConquista.INFLUENCIADOR -> ConquistaQuoteCategory.IMPACTO
    TipoConquista.EM_PROJETO,
    TipoConquista.TRANSFORMADOR,
    TipoConquista.GUARDIAO_SEGURANCA,
    TipoConquista.LIDER_INOVACAO,
    TipoConquista.EXECUTOR,
    TipoConquista.LIDER_RESULTADOS -> ConquistaQuoteCategory.EXECUCAO
    TipoConquista.CURADOR,
    TipoConquista.MENTOR -> ConquistaQuoteCategory.CURADORIA
    TipoConquista.ESTRATEGISTA,
    TipoConquista.VISIONARIO_ESTRATEGICO,
    TipoConquista.TRANSFORMADOR_ESTRATEGICO -> ConquistaQuoteCategory.ESTRATEGIA
}

private val quotesByCategory: Map<ConquistaQuoteCategory, List<String>> = mapOf(
    ConquistaQuoteCategory.BOAS_VINDAS to listOf(
        "Toda jornada de inovação começa com um primeiro passo.",
        "Bem-vindo ao time que transforma a Águia Branca.",
        "Sua presença já faz diferença nesta comunidade.",
        "O futuro é construído por quem ousa participar."
    ),
    ConquistaQuoteCategory.CRIACAO_IDEIAS to listOf(
        "Ideias são sementes — cuide delas e elas florescerão.",
        "Cada ideia enviada é um passo em direção à transformação.",
        "Inovar é transformar o impossível em possível.",
        "Grandes mudanças começam com pequenas ideias.",
        "Sua criatividade move a empresa para frente."
    ),
    ConquistaQuoteCategory.IMPACTO to listOf(
        "Uma ideia aprovada pode impactar milhares de pessoas.",
        "Reconhecimento é combustível para a inovação.",
        "Quando sua ideia é aprovada, todos ganham.",
        "Impacto real nasce de ideias validadas."
    ),
    ConquistaQuoteCategory.EXECUCAO to listOf(
        "Ideias sem execução são apenas sonhos.",
        "Transformar ideias em projetos é onde a magia acontece.",
        "Resultados concretos nascem da ação persistente.",
        "Quem executa, transforma.",
        "Da ideia ao projeto: essa é a jornada dos campeões."
    ),
    ConquistaQuoteCategory.CURADORIA to listOf(
        "Avaliar ideias é cultivar o futuro da empresa.",
        "Um bom gestor transforma potencial em realidade.",
        "Mentoria é o multiplicador da inovação.",
        "Qualidade na curadoria gera qualidade nos resultados."
    ),
    ConquistaQuoteCategory.ESTRATEGIA to listOf(
        "Visão estratégica ilumina o caminho da inovação.",
        "Líderes transformam direção em resultados.",
        "Estratégia sem execução é ilusão; execução sem estratégia é caos.",
        "Quem define a direção, guia a transformação.",
        "Grandes líderes inspiram grandes resultados."
    )
)

@Composable
fun TipoConquista.motivationalQuote(conquistaId: String): String {
    val category = quoteCategory()
    val quotes = quotesByCategory[category].orEmpty()
    if (quotes.isEmpty()) return ""
    val index = remember(conquistaId, category) {
        (conquistaId.hashCode() and Int.MAX_VALUE) % quotes.size
    }
    return quotes[index]
}

private val dataConquistaFormatter = DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy", Locale("pt", "BR"))

fun Conquista.dataDesbloqueioFormatada(): String? = desbloqueadaEm?.let { epochMillis ->
    dataConquistaFormatter.format(
        Instant.ofEpochMilli(epochMillis).atZone(ZoneId.systemDefault()).toLocalDate()
    )
}
