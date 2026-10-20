package com.gtnix.inovagab.dto.ideia;

import com.gtnix.inovagab.domain.OrigemEstimativa;
import com.gtnix.inovagab.domain.StatusAnaliseIa;

import java.time.Instant;
import java.util.List;

/**
 * Detalhamento da recomendação para o gestor.
 *
 * @param faixa            ALTA, MEDIA ou BAIXA; nulo quando não há score.
 * @param estimativaOrigem indica se o impacto/esforço usados no score são do gestor ou sugeridos pela IA.
 */
public record AnaliseIaResponse(
        StatusAnaliseIa status,
        String faixa,
        List<CriterioIaResponse> criterios,
        List<String> riscos,
        List<String> perguntas,
        Integer impactoSugerido,
        Integer esforcoSugerido,
        OrigemEstimativa estimativaOrigem,
        String motivoAjusteEstimativa,
        String modelo,
        Instant avaliadoEm,
        Boolean concordaIa,
        String motivoDiscordanciaIa
) {
    public record CriterioIaResponse(String criterio, int nota, int peso, String evidencia) {
    }
}
