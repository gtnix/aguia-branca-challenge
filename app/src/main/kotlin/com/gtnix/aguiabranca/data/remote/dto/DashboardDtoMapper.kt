package com.gtnix.aguiabranca.data.remote.dto

import com.gtnix.aguiabranca.domain.model.DashboardByProject
import com.gtnix.aguiabranca.domain.model.DashboardByStrategy
import com.gtnix.aguiabranca.domain.model.FunnelStepMetric
import com.gtnix.aguiabranca.domain.model.LeaderDashboardSnapshot
import com.gtnix.aguiabranca.domain.usecase.dashboard.AreaDesempenhoMetric

fun DashboardSummaryDto.toDomain(): LeaderDashboardSnapshot {
    return LeaderDashboardSnapshot(
        roiMedio = roiMedio,
        lucroTotal = lucroTotal,
        investimentoTotal = investimentoTotal,
        retornoTotal = retornoTotal,
        projetosNoPrazo = projetosNoPrazo.toInt(),
        projetosAtrasados = projetosAtrasados.toInt(),
        ganhoProdutividadePercentual = ganhoProdutividadePercentual,
        totalIdeias = totalIdeias.toInt(),
        ideiasAprovadas = ideiasAprovadas.toInt(),
        ideiasEmProjeto = ideiasEmProjeto.toInt(),
        projetosAtivos = projetosAtivos.toInt(),
        tempoMedioAprovacaoDias = tempoMedioAprovacaoDias.toInt(),
        funil = funil.map { it.toDomain() },
        desempenhoPorArea = desempenhoPorArea.map { it.toDomain() }
    )
}

fun FunnelStepDto.toDomain(): FunnelStepMetric {
    return FunnelStepMetric(
        etapa = etapa.orEmpty(),
        label = label?.takeIf { it.isNotBlank() } ?: etapa.orEmpty(),
        quantidade = quantidade.toInt()
    )
}

fun AreaDesempenhoDto.toDomain(): AreaDesempenhoMetric {
    return AreaDesempenhoMetric(
        label = label?.takeIf { it.isNotBlank() } ?: area.orEmpty(),
        value = percentual.toFloat(),
        quantidade = quantidade.toInt()
    )
}

fun DashboardByStrategyDto.toDomain(): DashboardByStrategy {
    return DashboardByStrategy(
        orientacaoId = orientacaoId,
        titulo = titulo.orEmpty(),
        ativo = ativo,
        investimentoTotal = investimentoTotal,
        retornoTotal = retornoTotal,
        lucroTotal = lucroTotal,
        roiMedio = roiMedio,
        projetosNoPrazo = projetosNoPrazo.toInt(),
        projetosAtrasados = projetosAtrasados.toInt(),
        projetos = projetos.map { it.toDomain() }
    )
}

fun DashboardByProjectDto.toDomain(): DashboardByProject {
    return DashboardByProject(
        projetoId = projetoId,
        titulo = titulo.orEmpty(),
        orientacaoId = orientacaoId,
        status = status.orEmpty(),
        investimento = investimento,
        retorno = retorno,
        lucroAnualizado = lucroAnualizado,
        roi = roi,
        prazo = prazo,
        noPrazo = noPrazo,
        progressoPercentual = progressoPercentual ?: 0
    )
}
