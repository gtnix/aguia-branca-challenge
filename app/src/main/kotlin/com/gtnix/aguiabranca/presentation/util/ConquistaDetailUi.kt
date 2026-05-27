package com.gtnix.aguiabranca.presentation.util

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.domain.model.ConquistaTier
import com.gtnix.aguiabranca.domain.model.Estatisticas
import com.gtnix.aguiabranca.domain.model.TipoConquista
import java.time.LocalDate
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

fun conquistaPlaceholderDate(conquistaId: String): String {
    val daysAgo = (conquistaId.hashCode() and Int.MAX_VALUE) % 180 + 1
    val date = LocalDate.now().minusDays(daysAgo.toLong())
    val formatter = DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy", Locale("pt", "BR"))
    return formatter.format(date)
}

fun TipoConquista.unlockProgress(estatisticas: Estatisticas?): Float {
    val stats = estatisticas ?: Estatisticas()
    return when (this) {
        TipoConquista.BEM_VINDO -> 1f
        TipoConquista.PRIMEIRA_IDEIA -> (stats.totalIdeias.coerceAtMost(1) / 1f)
        TipoConquista.INOVADOR -> (stats.totalIdeias.coerceAtMost(5) / 5f)
        TipoConquista.VISIONARIO_IDEIAS -> (stats.totalIdeias.coerceAtMost(10) / 10f)
        TipoConquista.PRIMEIRA_APROVACAO -> (stats.ideiasAprovadas.coerceAtMost(1) / 1f)
        TipoConquista.INFLUENCIADOR -> (stats.ideiasAprovadas.coerceAtMost(5) / 5f)
        TipoConquista.EM_PROJETO -> (stats.projetosParticipando.coerceAtMost(1) / 1f)
        TipoConquista.TRANSFORMADOR -> (stats.projetosParticipando.coerceAtMost(3) / 3f)
        TipoConquista.LIDER_INOVACAO -> {
            val ideiasProgress = stats.totalIdeias.coerceAtMost(15) / 15f
            val aprovadasProgress = stats.ideiasAprovadas.coerceAtMost(10) / 10f
            minOf(ideiasProgress, aprovadasProgress)
        }
        TipoConquista.CURADOR -> (stats.totalIdeias.coerceAtMost(1) / 1f)
        TipoConquista.MENTOR -> (stats.totalIdeias.coerceAtMost(10) / 10f)
        TipoConquista.EXECUTOR -> (stats.projetosParticipando.coerceAtMost(3) / 3f)
        TipoConquista.LIDER_RESULTADOS -> (stats.ideiasAprovadas.coerceAtMost(5) / 5f)
        TipoConquista.ESTRATEGISTA -> (stats.totalIdeias.coerceAtMost(1) / 1f)
        TipoConquista.VISIONARIO_ESTRATEGICO -> (stats.totalIdeias.coerceAtMost(10) / 10f)
        TipoConquista.TRANSFORMADOR_ESTRATEGICO -> (stats.projetosParticipando.coerceAtMost(5) / 5f)
    }.coerceIn(0f, 1f)
}

fun TipoConquista.simulatedUnlockProgress(conquistaId: String, estatisticas: Estatisticas?): Float {
    val base = unlockProgress(estatisticas)
    if (base >= 1f) return 1f
    val jitter = ((conquistaId.hashCode() ushr 4) and 0xFF) / 255f * 0.15f
    return (base + jitter).coerceIn(0.05f, 0.95f)
}
