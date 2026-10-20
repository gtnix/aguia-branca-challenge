package com.gtnix.inovagab.dto.ideia;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record PrioridadeRequest(
        @NotNull @Min(1) @Max(100) Integer prioridade
) {
}
