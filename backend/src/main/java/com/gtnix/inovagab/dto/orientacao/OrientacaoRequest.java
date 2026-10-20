package com.gtnix.inovagab.dto.orientacao;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * Campos {@code campanha*} são opcionais: preenchidos, transformam a orientação numa campanha
 * patrocinada com prazo e bônus de Asas para os primeiros colocados.
 */
public record OrientacaoRequest(
        @NotBlank @Size(max = 200) String titulo,
        @Size(max = 4000) String descricao,
        @NotBlank @Size(max = 80) String categoria,
        @NotBlank @Size(max = 120) String campanha,
        LocalDate campanhaFim,
        @Min(0) @Max(1000) Integer campanhaBonusAsas,
        @Min(1) @Max(20) Integer campanhaTopN,
        @Pattern(regexp = "PASSAGEIROS|LOGISTICA|COMERCIO", message = "Divisão deve ser PASSAGEIROS, LOGISTICA ou COMERCIO")
        String divisao
) {
    public OrientacaoRequest(String titulo, String descricao, String categoria, String campanha) {
        this(titulo, descricao, categoria, campanha, null, null, null, null);
    }
}
