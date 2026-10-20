package com.gtnix.inovagab.dto.gamificacao;

import com.gtnix.inovagab.domain.NivelGamificacao;
import com.gtnix.inovagab.domain.StatusResgate;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;

/** Contratos da loja de recompensas e do fluxo de resgate. */
public final class RecompensaDtos {

    private RecompensaDtos() {
    }

    public record RecompensaRequest(
            @NotBlank @Size(max = 120) String titulo,
            @Size(max = 500) String descricao,
            @NotBlank @Size(max = 40) String categoria,
            @Size(max = 80) String parceiro,
            @Min(1) @Max(10000) int custoAsas,
            @Min(0) @Max(100000) int estoque,
            NivelGamificacao nivelMinimo,
            Boolean ativo
    ) {
    }

    public record RecompensaResponse(
            String id,
            String titulo,
            String descricao,
            String categoria,
            String parceiro,
            int custoAsas,
            int estoque,
            String nivelMinimo,
            String nivelMinimoLabel,
            boolean ativo,
            boolean podeResgatar,
            String motivoBloqueio
    ) {
    }

    public record ResgateResponse(
            String id,
            String recompensaId,
            String recompensaTitulo,
            String userId,
            String userNome,
            int custoAsas,
            StatusResgate status,
            String observacao,
            String aprovadorUserId,
            Instant solicitadoEm,
            Instant decididoEm,
            Instant entregueEm
    ) {
    }

    public record DecisaoResgateRequest(@NotNull StatusResgate status, @Size(max = 500) String observacao) {
    }
}
