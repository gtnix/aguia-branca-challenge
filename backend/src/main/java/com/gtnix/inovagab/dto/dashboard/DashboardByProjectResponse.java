package com.gtnix.inovagab.dto.dashboard;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DashboardByProjectResponse(
        String projetoId,
        String titulo,
        String orientacaoId,
        String status,
        BigDecimal investimento,
        BigDecimal retorno,
        BigDecimal lucroAnualizado,
        BigDecimal roi,
        LocalDate prazo,
        boolean noPrazo,
        Integer progressoPercentual
) {
}
