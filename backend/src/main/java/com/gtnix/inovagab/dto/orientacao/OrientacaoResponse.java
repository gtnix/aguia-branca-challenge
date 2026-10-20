package com.gtnix.inovagab.dto.orientacao;

import java.time.Instant;
import java.time.LocalDate;

public record OrientacaoResponse(
        String id,
        String titulo,
        String descricao,
        String categoria,
        String campanha,
        Instant dataCriacao,
        Instant dataAtualizacao,
        boolean ativo,
        String criadoPorUserId,
        LocalDate campanhaFim,
        Integer campanhaBonusAsas,
        Integer campanhaTopN,
        Instant campanhaEncerradaEm,
        boolean campanhaAtiva,
        String divisao
) {
}
