package com.gtnix.inovagab.dto.ideia;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Impacto e esforço oficiais definidos pelo gestor; prevalecem sobre a sugestão da IA. */
public record EstimativaRequest(
        @NotNull @Min(1) @Max(5) Integer impacto,
        @NotNull @Min(1) @Max(5) Integer esforco,
        @Size(max = 300) String motivoAjuste
) {
}
