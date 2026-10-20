package com.gtnix.inovagab.dto.dashboard;

import java.math.BigDecimal;
import java.util.List;

public record DashboardSummaryResponse(
        BigDecimal roiMedio,
        BigDecimal lucroTotal,
        BigDecimal investimentoTotal,
        BigDecimal retornoTotal,
        long projetosNoPrazo,
        long projetosAtrasados,
        BigDecimal ganhoProdutividadePercentual,
        long totalIdeias,
        long ideiasAprovadas,
        long ideiasEmProjeto,
        long projetosAtivos,
        long tempoMedioAprovacaoDias,
        List<FunnelStepResponse> funil,
        List<AreaDesempenhoResponse> desempenhoPorArea
) {
}
