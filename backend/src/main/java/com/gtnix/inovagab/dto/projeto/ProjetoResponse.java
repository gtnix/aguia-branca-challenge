package com.gtnix.inovagab.dto.projeto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record ProjetoResponse(
        String id,
        String titulo,
        String objetivo,
        String descricao,
        String area,
        String orientacaoId,
        String ideiaId,
        String etapa,
        String status,
        BigDecimal investimento,
        BigDecimal retorno,
        BigDecimal roi,
        LocalDate prazo,
        Integer progressoPercentual,
        String resultadosObtidos,
        String responsavelUserId,
        Instant dataCriacao,
        Instant dataAtualizacao
) {
}
