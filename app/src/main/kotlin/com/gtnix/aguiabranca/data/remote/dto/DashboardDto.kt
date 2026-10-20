package com.gtnix.aguiabranca.data.remote.dto

data class FunnelStepDto(
    val etapa: String? = null,
    val label: String? = null,
    val quantidade: Long = 0
)

data class AreaDesempenhoDto(
    val area: String? = null,
    val label: String? = null,
    val quantidade: Long = 0,
    val percentual: Double = 0.0
)

data class DashboardSummaryDto(
    val roiMedio: Double = 0.0,
    val lucroTotal: Double = 0.0,
    val investimentoTotal: Double = 0.0,
    val retornoTotal: Double = 0.0,
    val projetosNoPrazo: Long = 0,
    val projetosAtrasados: Long = 0,
    val ganhoProdutividadePercentual: Double = 0.0,
    val totalIdeias: Long = 0,
    val ideiasAprovadas: Long = 0,
    val ideiasEmProjeto: Long = 0,
    val projetosAtivos: Long = 0,
    val tempoMedioAprovacaoDias: Long = 0,
    val funil: List<FunnelStepDto> = emptyList(),
    val desempenhoPorArea: List<AreaDesempenhoDto> = emptyList()
)

data class DashboardByStrategyDto(
    val orientacaoId: String,
    val titulo: String? = null,
    val ativo: Boolean = true,
    val investimentoTotal: Double = 0.0,
    val retornoTotal: Double = 0.0,
    val lucroTotal: Double = 0.0,
    val roiMedio: Double = 0.0,
    val projetosNoPrazo: Long = 0,
    val projetosAtrasados: Long = 0,
    val projetos: List<ProjetoDto> = emptyList()
)

data class DashboardByProjectDto(
    val projetoId: String,
    val titulo: String? = null,
    val orientacaoId: String? = null,
    val status: String? = null,
    val investimento: Double = 0.0,
    val retorno: Double = 0.0,
    val lucroAnualizado: Double = 0.0,
    val roi: Double = 0.0,
    val prazo: String? = null,
    val noPrazo: Boolean = true,
    val progressoPercentual: Int? = null
)
