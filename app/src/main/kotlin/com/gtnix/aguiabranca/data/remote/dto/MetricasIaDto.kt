package com.gtnix.aguiabranca.data.remote.dto

import com.gtnix.aguiabranca.domain.model.AprovacaoPorFaixa
import com.gtnix.aguiabranca.domain.model.GrupoScoreIa
import com.gtnix.aguiabranca.domain.model.MetricasIa

data class MetricasIaDto(
    val ideiasAnalisadas: Int = 0,
    val decisoesComOpiniao: Int = 0,
    val concordancias: Int = 0,
    val taxaConcordancia: Double? = null,
    val execucoes: Int = 0,
    val percentualFallback: Double? = null,
    val tempoMedioTriagemHoras: Double? = null,
    val scorePorArea: List<GrupoScoreDto> = emptyList(),
    val scorePorDivisao: List<GrupoScoreDto> = emptyList(),
    val aprovacaoPorFaixa: List<FaixaAprovacaoDto> = emptyList()
)

data class GrupoScoreDto(
    val grupo: String,
    val quantidade: Int = 0,
    val mediaScore: Double = 0.0
)

data class FaixaAprovacaoDto(
    val faixa: String,
    val avaliadas: Int = 0,
    val aprovadas: Int = 0,
    val taxaAprovacao: Double? = null
)

fun MetricasIaDto.toDomain(): MetricasIa = MetricasIa(
    ideiasAnalisadas = ideiasAnalisadas,
    decisoesComOpiniao = decisoesComOpiniao,
    concordancias = concordancias,
    taxaConcordancia = taxaConcordancia,
    execucoes = execucoes,
    percentualFallback = percentualFallback,
    tempoMedioTriagemHoras = tempoMedioTriagemHoras,
    scorePorArea = scorePorArea.map { GrupoScoreIa(it.grupo, it.quantidade, it.mediaScore) },
    scorePorDivisao = scorePorDivisao.map { GrupoScoreIa(it.grupo, it.quantidade, it.mediaScore) },
    aprovacaoPorFaixa = aprovacaoPorFaixa.map {
        AprovacaoPorFaixa(it.faixa, it.avaliadas, it.aprovadas, it.taxaAprovacao)
    }
)
