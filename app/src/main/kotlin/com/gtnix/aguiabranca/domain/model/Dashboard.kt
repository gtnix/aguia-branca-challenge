package com.gtnix.aguiabranca.domain.model

import com.gtnix.aguiabranca.domain.usecase.dashboard.AreaDesempenhoMetric

data class FunnelStepMetric(
    val etapa: String,
    val label: String,
    val quantidade: Int
)

/**
 * Insight estruturado gerado pelo Gemini (ou fallback local) para o dashboard do Líder.
 */
data class DashboardInsight(
    val titulo: String,
    val resumo: String,
    val evidencias: List<String>,
    val acaoRecomendada: String,
    val modelo: String,
    val isFallback: Boolean,
    val geradoEm: String?
) {
    companion object {
        val vazio = DashboardInsight(
            titulo = "",
            resumo = "",
            evidencias = emptyList(),
            acaoRecomendada = "",
            modelo = "",
            isFallback = true,
            geradoEm = null
        )
    }
}

data class LeaderDashboardSnapshot(
    val roiMedio: Double,
    val lucroTotal: Double,
    val investimentoTotal: Double,
    val retornoTotal: Double,
    val projetosNoPrazo: Int,
    val projetosAtrasados: Int,
    val ganhoProdutividadePercentual: Double,
    val totalIdeias: Int,
    val ideiasAprovadas: Int,
    val ideiasEmProjeto: Int,
    val projetosAtivos: Int,
    val tempoMedioAprovacaoDias: Int,
    val funil: List<FunnelStepMetric>,
    val desempenhoPorArea: List<AreaDesempenhoMetric>
)

data class DashboardByStrategy(
    val orientacaoId: String,
    val titulo: String,
    val ativo: Boolean,
    val investimentoTotal: Double,
    val retornoTotal: Double,
    val lucroTotal: Double,
    val roiMedio: Double,
    val projetosNoPrazo: Int,
    val projetosAtrasados: Int,
    val projetos: List<Projeto>
)

data class DashboardByProject(
    val projetoId: String,
    val titulo: String,
    val orientacaoId: String?,
    val status: String,
    val investimento: Double,
    val retorno: Double,
    val lucroAnualizado: Double,
    val roi: Double,
    val prazo: String?,
    val noPrazo: Boolean,
    val progressoPercentual: Int
)
