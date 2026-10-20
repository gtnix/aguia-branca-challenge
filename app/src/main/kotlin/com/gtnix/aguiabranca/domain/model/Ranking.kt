package com.gtnix.aguiabranca.domain.model

data class ResultadoRanking(
    val topRanking: List<RankingEntrada>,
    val posicaoUsuarioLogado: Int?,
    val entradaUsuarioLogado: RankingEntrada?,
    val perfilFiltro: PerfilUsuario,
    val totalParticipantes: Int = 0,
    val regraPremio: String = "",
    val temporada: TemporadaInfo? = null
)

data class RankingEntrada(
    val usuarioId: String,
    val nome: String,
    val pontos: Int,
    val nivel: NivelUsuario,
    val inicialNome: Char,
    val perfil: PerfilUsuario,
    val cargo: String,
    val divisao: DivisaoNegocio,
    val equipe: String? = null,
    val zonaPremio: Boolean = false
)

enum class DivisaoFilter {
    MINHA_DIVISAO,
    GRUPO_COMPLETO
}

data class IndicacaoInovaGab(
    val divisao: DivisaoNegocio,
    val posicaoNaDivisao: Int,
    val usuarioId: String,
    val nome: String,
    val equipe: String?,
    val xpTemporada: Int
)

data class IndicacoesInovaGab(
    val temporada: TemporadaInfo?,
    val criterio: String,
    val indicados: List<IndicacaoInovaGab>
)
