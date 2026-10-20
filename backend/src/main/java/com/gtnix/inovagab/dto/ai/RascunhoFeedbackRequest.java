package com.gtnix.inovagab.dto.ai;

import jakarta.validation.constraints.Size;

/** @param observacoes motivos rápidos do gestor que o rascunho deve explicar ao colaborador. */
public record RascunhoFeedbackRequest(
        @Size(max = 500) String observacoes
) {
}
