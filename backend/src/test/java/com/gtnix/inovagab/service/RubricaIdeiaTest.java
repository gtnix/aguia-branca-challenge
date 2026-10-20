package com.gtnix.inovagab.service;

import com.gtnix.inovagab.document.CriterioIa;
import com.gtnix.inovagab.document.IdeiaDocument;
import com.gtnix.inovagab.domain.OrigemEstimativa;
import com.gtnix.inovagab.domain.StatusAnaliseIa;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RubricaIdeiaTest {

    @Test
    void pesosSomam100() {
        assertEquals(100, RubricaIdeia.PESOS.values().stream().mapToInt(Integer::intValue).sum());
    }

    @Test
    void scoreVaiDeZeroACem() {
        Map<String, Integer> minimo = Map.of(
                RubricaIdeia.ALINHAMENTO, 1, RubricaIdeia.IMPACTO, 1, RubricaIdeia.VIABILIDADE, 1,
                RubricaIdeia.CLAREZA, 1, RubricaIdeia.ORIGINALIDADE, 1);
        Map<String, Integer> maximo = Map.of(
                RubricaIdeia.ALINHAMENTO, 5, RubricaIdeia.IMPACTO, 5, RubricaIdeia.VIABILIDADE, 5,
                RubricaIdeia.CLAREZA, 5, RubricaIdeia.ORIGINALIDADE, 5);

        assertEquals(0, RubricaIdeia.score(minimo));
        assertEquals(100, RubricaIdeia.score(maximo));
        assertEquals(50, RubricaIdeia.score(Map.of()));
    }

    @Test
    void notasForaDaFaixaSaoLimitadas() {
        assertEquals(100, RubricaIdeia.score(Map.of(
                RubricaIdeia.ALINHAMENTO, 9, RubricaIdeia.IMPACTO, 9, RubricaIdeia.VIABILIDADE, 9,
                RubricaIdeia.CLAREZA, 9, RubricaIdeia.ORIGINALIDADE, 9)));
    }

    @Test
    void faixas() {
        assertEquals(RubricaIdeia.FAIXA_ALTA, RubricaIdeia.faixa(70));
        assertEquals(RubricaIdeia.FAIXA_MEDIA, RubricaIdeia.faixa(69));
        assertEquals(RubricaIdeia.FAIXA_MEDIA, RubricaIdeia.faixa(40));
        assertEquals(RubricaIdeia.FAIXA_BAIXA, RubricaIdeia.faixa(39));
        assertNull(RubricaIdeia.faixa(null));
    }

    @Test
    void recalcularSemDadosNaoGeraScore() {
        IdeiaDocument doc = new IdeiaDocument();

        RubricaIdeia.recalcular(doc);

        assertNull(doc.getScoreIa());
        assertEquals(StatusAnaliseIa.DADOS_INSUFICIENTES, doc.getScoreIaStatus());
        assertTrue(doc.getScoreIaFallback());
    }

    @Test
    void recalcularUsaValorOficialDoGestorAntesDaSugestao() {
        IdeiaDocument doc = new IdeiaDocument();
        doc.setCriteriosIa(List.of(new CriterioIa(RubricaIdeia.ALINHAMENTO, 3, "x")));
        doc.setImpactoSugeridoIa(5);
        doc.setEsforcoSugeridoIa(1);

        RubricaIdeia.recalcular(doc);
        int comSugestao = doc.getScoreIa();
        assertEquals(OrigemEstimativa.IA, doc.getEstimativaOrigem());
        assertEquals(StatusAnaliseIa.IA, doc.getScoreIaStatus());
        assertFalse(doc.getScoreIaFallback());

        doc.setImpacto(1);
        doc.setEsforco(5);
        RubricaIdeia.recalcular(doc);

        assertTrue(doc.getScoreIa() < comSugestao);
        assertEquals(OrigemEstimativa.GESTOR, doc.getEstimativaOrigem());
    }

    @Test
    void limparPreservaEstimativaOficial() {
        IdeiaDocument doc = new IdeiaDocument();
        doc.setImpacto(4);
        doc.setEsforco(2);
        doc.setScoreIa(80);
        doc.setScoreIaStatus(StatusAnaliseIa.IA);
        doc.setImpactoSugeridoIa(3);

        RubricaIdeia.limpar(doc);

        assertNull(doc.getScoreIa());
        assertNull(doc.getScoreIaStatus());
        assertNull(doc.getImpactoSugeridoIa());
        assertEquals(4, doc.getImpacto());
        assertEquals(OrigemEstimativa.GESTOR, doc.getEstimativaOrigem());
    }
}
