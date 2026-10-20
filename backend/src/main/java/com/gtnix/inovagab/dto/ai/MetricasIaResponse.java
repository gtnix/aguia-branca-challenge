package com.gtnix.inovagab.dto.ai;

import java.util.List;

/**
 * Indicadores de governança da triagem por IA para o Líder.
 *
 * @param taxaConcordancia      % de decisões em que o gestor concordou com a IA; nulo sem opiniões registradas.
 * @param percentualFallback    % de execuções de score sem resposta válida do Gemini; nulo sem execuções.
 * @param tempoMedioTriagemHoras média entre criação e avaliação das ideias avaliadas; nulo sem avaliações.
 * @param aprovacaoPorFaixa     taxa de aprovação humana por faixa da IA — se não varia, a IA não está ajudando.
 */
public record MetricasIaResponse(
        long ideiasAnalisadas,
        long decisoesComOpiniao,
        long concordancias,
        Double taxaConcordancia,
        long execucoes,
        Double percentualFallback,
        Double tempoMedioTriagemHoras,
        List<GrupoScore> scorePorArea,
        List<GrupoScore> scorePorDivisao,
        List<FaixaAprovacao> aprovacaoPorFaixa
) {
    public record GrupoScore(String grupo, long quantidade, double mediaScore) {
    }

    public record FaixaAprovacao(String faixa, long avaliadas, long aprovadas, Double taxaAprovacao) {
    }
}
