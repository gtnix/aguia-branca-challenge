package com.gtnix.inovagab.dto.ideia;

import com.gtnix.inovagab.domain.StatusIdeia;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * @param concordaIa opinião opcional do gestor sobre a recomendação da IA; alimenta as métricas de calibração.
 */
public record AvaliacaoRequest(
        @NotNull StatusIdeia status,
        @Size(max = 2000) String feedback,
        Boolean concordaIa,
        @Size(max = 300) String motivoDiscordanciaIa
) {
    public AvaliacaoRequest(StatusIdeia status, String feedback) {
        this(status, feedback, null, null);
    }
}
