package com.gtnix.inovagab.dto.projeto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record ProgressoRequest(
        @Size(max = 80) String etapa,
        @Size(max = 40) String status,
        @Min(0) @Max(100) Integer progressoPercentual,
        @Size(max = 4000) String resultadosObtidos
) {
}
