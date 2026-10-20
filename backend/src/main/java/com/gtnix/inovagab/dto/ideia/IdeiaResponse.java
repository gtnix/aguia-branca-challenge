package com.gtnix.inovagab.dto.ideia;

import com.gtnix.inovagab.domain.StatusIdeia;

import java.time.Instant;

/**
 * Campos de IA ({@code scoreIa}, {@code justificativaIa}, {@code scoreIaFallback}, {@code analiseIa})
 * só são preenchidos para GESTOR e LIDER.
 */
public record IdeiaResponse(
        String id,
        String autorUserId,
        String autorNome,
        String titulo,
        String descricao,
        String tipo,
        String area,
        String orientacaoId,
        StatusIdeia status,
        Integer prioridade,
        Integer impacto,
        Integer esforco,
        Integer scoreIa,
        String justificativaIa,
        Boolean scoreIaFallback,
        String feedbackReprovacao,
        String avaliadorUserId,
        Boolean feedbackUtil,
        String duplicadaDe,
        int apoios,
        Instant dataCriacao,
        Instant dataAtualizacao,
        String problema,
        String publicoAfetado,
        String frequencia,
        String beneficio,
        AnaliseIaResponse analiseIa
) {
}
