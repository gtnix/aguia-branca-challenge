package com.gtnix.inovagab.dto.ai;

import java.time.Instant;
import java.util.List;

/**
 * Resposta estruturada da narrativa IA para o dashboard executivo do Líder.
 *
 * <ul>
 *   <li>{@code titulo} – sentença executiva de até 10 palavras.</li>
 *   <li>{@code resumo} – até 2 frases explicando o estado do portfólio.</li>
 *   <li>{@code evidencias} – lista de até 3 dados numéricos reais extraídos do portfólio.</li>
 *   <li>{@code acaoRecomendada} – uma ação concreta e executável.</li>
 *   <li>{@code modelo} – modelo Gemini ou "heuristic-local" em fallback.</li>
 *   <li>{@code fallback} – true quando Gemini indisponível.</li>
 *   <li>{@code geradoEm} – timestamp de geração.</li>
 * </ul>
 */
public record DashboardNarrativeResponse(
        String titulo,
        String resumo,
        List<String> evidencias,
        String acaoRecomendada,
        String modelo,
        boolean fallback,
        Instant geradoEm
) {
}
