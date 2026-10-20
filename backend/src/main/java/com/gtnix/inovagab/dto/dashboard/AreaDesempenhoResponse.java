package com.gtnix.inovagab.dto.dashboard;

import java.math.BigDecimal;

public record AreaDesempenhoResponse(
        String area,
        String label,
        long quantidade,
        BigDecimal percentual
) {
}
