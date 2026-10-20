package com.gtnix.inovagab.service;

import com.gtnix.inovagab.document.CriterioIa;
import com.gtnix.inovagab.document.IdeiaDocument;
import com.gtnix.inovagab.domain.OrigemEstimativa;
import com.gtnix.inovagab.domain.StatusAnaliseIa;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Rubrica fixa da triagem de ideias. A IA dá notas de 1 a 5 por critério; o score de 0 a 100
 * é sempre calculado aqui, com pesos iguais para todas as orientações.
 */
final class RubricaIdeia {

    static final String ALINHAMENTO = "ALINHAMENTO";
    static final String IMPACTO = "IMPACTO";
    static final String VIABILIDADE = "VIABILIDADE";
    static final String CLAREZA = "CLAREZA";
    static final String ORIGINALIDADE = "ORIGINALIDADE";

    static final Map<String, Integer> PESOS;

    static {
        Map<String, Integer> pesos = new LinkedHashMap<>();
        pesos.put(ALINHAMENTO, 30);
        pesos.put(IMPACTO, 30);
        pesos.put(VIABILIDADE, 20);
        pesos.put(CLAREZA, 10);
        pesos.put(ORIGINALIDADE, 10);
        PESOS = Collections.unmodifiableMap(pesos);
    }

    static final int NOTA_NEUTRA = 3;

    static final String FAIXA_ALTA = "ALTA";
    static final String FAIXA_MEDIA = "MEDIA";
    static final String FAIXA_BAIXA = "BAIXA";

    static final String JUSTIFICATIVA_ESTIMATIVA =
            "Estimativa automática (IA indisponível): calculada só com impacto e esforço; demais critérios ficaram neutros.";
    static final String JUSTIFICATIVA_DADOS_INSUFICIENTES =
            "Sem impacto e esforço definidos e IA indisponível: não há base para recomendar. Defina a estimativa ou tente novamente.";

    private RubricaIdeia() {
    }

    /** Score de 0 a 100: cada critério contribui com {@code peso * (nota - 1) / 4}. */
    static int score(Map<String, Integer> notas) {
        double total = 0;
        for (Map.Entry<String, Integer> peso : PESOS.entrySet()) {
            int nota = clampNota(notas.getOrDefault(peso.getKey(), NOTA_NEUTRA));
            total += peso.getValue() * (nota - 1) / 4.0;
        }
        return (int) Math.round(total);
    }

    static int viabilidadeDoEsforco(int esforco) {
        return 6 - clampNota(esforco);
    }

    static int clampNota(int nota) {
        return Math.max(1, Math.min(5, nota));
    }

    static String faixa(Integer score) {
        if (score == null) {
            return null;
        }
        if (score >= 70) {
            return FAIXA_ALTA;
        }
        return score >= 40 ? FAIXA_MEDIA : FAIXA_BAIXA;
    }

    /**
     * Recalcula score, status e origem da estimativa. Impacto e esforço oficiais (do gestor)
     * prevalecem sobre a sugestão da IA.
     */
    static void recalcular(IdeiaDocument doc) {
        boolean oficial = doc.getImpacto() != null && doc.getEsforco() != null;
        Integer impacto = doc.getImpacto() != null ? doc.getImpacto() : doc.getImpactoSugeridoIa();
        Integer esforco = doc.getEsforco() != null ? doc.getEsforco() : doc.getEsforcoSugeridoIa();
        boolean temAnaliseIa = doc.getCriteriosIa() != null && !doc.getCriteriosIa().isEmpty();

        if (impacto == null || esforco == null) {
            doc.setScoreIa(null);
            doc.setScoreIaStatus(StatusAnaliseIa.DADOS_INSUFICIENTES);
            doc.setScoreIaFallback(true);
            doc.setEstimativaOrigem(null);
            doc.setJustificativaIa(JUSTIFICATIVA_DADOS_INSUFICIENTES);
            return;
        }

        Map<String, Integer> notas = new HashMap<>();
        if (temAnaliseIa) {
            for (CriterioIa criterio : doc.getCriteriosIa()) {
                notas.put(criterio.getCriterio(), criterio.getNota());
            }
        }
        notas.put(IMPACTO, impacto);
        notas.put(VIABILIDADE, viabilidadeDoEsforco(esforco));

        doc.setScoreIa(score(notas));
        doc.setEstimativaOrigem(oficial ? OrigemEstimativa.GESTOR : OrigemEstimativa.IA);
        if (temAnaliseIa) {
            doc.setScoreIaStatus(StatusAnaliseIa.IA);
            doc.setScoreIaFallback(false);
        } else {
            doc.setScoreIaStatus(StatusAnaliseIa.ESTIMATIVA);
            doc.setScoreIaFallback(true);
            doc.setJustificativaIa(JUSTIFICATIVA_ESTIMATIVA);
        }
    }

    static void limpar(IdeiaDocument doc) {
        doc.setScoreIa(null);
        doc.setJustificativaIa(null);
        doc.setScoreIaModelo(null);
        doc.setScoreIaFallback(null);
        doc.setScoreIaStatus(null);
        doc.setCriteriosIa(null);
        doc.setRiscosIa(null);
        doc.setPerguntasIa(null);
        doc.setImpactoSugeridoIa(null);
        doc.setEsforcoSugeridoIa(null);
        doc.setScoreIaEm(null);
        doc.setEstimativaOrigem(doc.getImpacto() != null && doc.getEsforco() != null ? OrigemEstimativa.GESTOR : null);
    }
}
