package com.gtnix.inovagab.dto.dashboard;

import com.gtnix.inovagab.dto.projeto.ProjetoResponse;

import java.math.BigDecimal;
import java.util.List;

public record DashboardByStrategyResponse(
        String orientacaoId,
        String titulo,
        boolean ativo,
        BigDecimal investimentoTotal,
        BigDecimal retornoTotal,
        BigDecimal lucroTotal,
        BigDecimal roiMedio,
        long projetosNoPrazo,
        long projetosAtrasados,
        List<ProjetoResponse> projetos
) {
}
