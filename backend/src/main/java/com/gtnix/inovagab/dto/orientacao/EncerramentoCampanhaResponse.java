package com.gtnix.inovagab.dto.orientacao;

import java.time.Instant;
import java.util.List;

public record EncerramentoCampanhaResponse(
        String orientacaoId,
        String campanha,
        Instant encerradaEm,
        List<Vencedor> vencedores
) {
    public record Vencedor(String userId, String nome, int xpCampanha, int bonusAsas) {
    }
}
