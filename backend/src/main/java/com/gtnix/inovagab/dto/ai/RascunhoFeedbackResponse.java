package com.gtnix.inovagab.dto.ai;

/** Texto sugerido; nunca é enviado automaticamente — o gestor edita e envia na avaliação. */
public record RascunhoFeedbackResponse(
        String rascunho,
        String modelo,
        boolean fallback
) {
}
