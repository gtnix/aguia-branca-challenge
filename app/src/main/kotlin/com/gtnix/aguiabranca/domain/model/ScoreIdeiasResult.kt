package com.gtnix.aguiabranca.domain.model

data class RankingIdeia(
    val ideiaId: String,
    val score: Int?,
    val justificativa: String,
    val status: StatusAnaliseIa = StatusAnaliseIa.IA
)

data class ScoreIdeiasResult(
    val ranking: List<RankingIdeia>,
    val modelo: String,
    val fallback: Boolean
) {
    val semDados: Boolean
        get() = ranking.any { it.status == StatusAnaliseIa.DADOS_INSUFICIENTES }
}
