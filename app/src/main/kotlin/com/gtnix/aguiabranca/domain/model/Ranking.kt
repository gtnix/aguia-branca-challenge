package com.gtnix.aguiabranca.domain.model

data class ResultadoRanking(
    val topRanking: List<RankingEntrada>,
    val posicaoUsuarioLogado: Int?,
    val entradaUsuarioLogado: RankingEntrada?,
    val perfilFiltro: PerfilUsuario,
    val totalParticipantes: Int = 0
)

data class RankingEntrada(
    val usuarioId: String,
    val nome: String,
    val pontos: Int,
    val nivel: NivelUsuario,
    val inicialNome: Char,
    val perfil: PerfilUsuario,
    val cargo: String,
    val divisao: DivisaoNegocio
)

enum class DivisaoFilter {
    MINHA_DIVISAO,
    GRUPO_COMPLETO
}
