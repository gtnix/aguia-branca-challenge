package com.gtnix.inovagab.dto.ideia;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Submissão do colaborador. Impacto e esforço não fazem parte do cadastro: são definidos pelo gestor
 * em {@code PATCH /ideias/{id}/estimativa}.
 */
public record IdeiaRequest(
        @NotBlank @Size(max = 200) String titulo,
        @NotBlank @Size(max = 4000) String descricao,
        @Size(max = 40) String tipo,
        @Size(max = 40) String area,
        @NotBlank String orientacaoId,
        @Size(max = 500) String problema,
        @Size(max = 300) String publicoAfetado,
        @Pattern(regexp = "DIARIA|SEMANAL|MENSAL|EVENTUAL") String frequencia,
        @Size(max = 500) String beneficio
) {
}
