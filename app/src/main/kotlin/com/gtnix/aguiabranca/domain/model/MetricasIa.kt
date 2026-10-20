package com.gtnix.aguiabranca.domain.model

/** Governança da IA para o líder: mostra se a recomendação ajuda ou distorce a triagem. */
data class MetricasIa(
    val ideiasAnalisadas: Int,
    val decisoesComOpiniao: Int,
    val concordancias: Int,
    val taxaConcordancia: Double?,
    val execucoes: Int,
    val percentualFallback: Double?,
    val tempoMedioTriagemHoras: Double?,
    val scorePorArea: List<GrupoScoreIa>,
    val scorePorDivisao: List<GrupoScoreIa>,
    val aprovacaoPorFaixa: List<AprovacaoPorFaixa>
)

data class GrupoScoreIa(
    val grupo: String,
    val quantidade: Int,
    val mediaScore: Double
)

data class AprovacaoPorFaixa(
    val faixa: String,
    val avaliadas: Int,
    val aprovadas: Int,
    val taxaAprovacao: Double?
)
