package com.gtnix.inovagab.dto.projeto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ProjetoRequest(
        @NotBlank @Size(max = 200) String titulo,
        @Size(max = 500) String objetivo,
        @Size(max = 4000) String descricao,
        @Size(max = 40) String area,
        @NotBlank String orientacaoId,
        String ideiaId,
        @Size(max = 80) String etapa,
        @Size(max = 40) String status,
        @DecimalMin(value = "0.0", inclusive = true) BigDecimal investimento,
        @DecimalMin(value = "0.0", inclusive = true) BigDecimal retorno,
        LocalDate prazo,
        @Min(0) @Max(100) Integer progressoPercentual,
        @Size(max = 4000) String resultadosObtidos
) {
}
