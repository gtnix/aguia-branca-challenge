package com.gtnix.inovagab.dto.ai;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

/** Cada ideia gera uma chamada independente ao Gemini, por isso o lote é limitado. */
public record ScoreIdeasRequest(
        @NotBlank String orientacaoId,
        @NotEmpty @Size(max = ScoreIdeasRequest.MAX_IDEIAS) List<String> ideiaIds
) {
    public static final int MAX_IDEIAS = 20;
}
