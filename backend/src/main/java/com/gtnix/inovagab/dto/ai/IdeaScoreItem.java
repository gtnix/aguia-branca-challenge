package com.gtnix.inovagab.dto.ai;

import com.gtnix.inovagab.domain.StatusAnaliseIa;

/**
 * @param score nulo quando {@code status} é {@code DADOS_INSUFICIENTES}.
 */
public record IdeaScoreItem(
        String ideiaId,
        Integer score,
        String justificativa,
        StatusAnaliseIa status,
        String faixa
) {
}
