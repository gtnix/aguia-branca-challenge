package com.gtnix.inovagab.service;

import com.gtnix.inovagab.document.AiScoreRunDocument;
import com.gtnix.inovagab.document.IdeiaDocument;
import com.gtnix.inovagab.document.OrientacaoDocument;
import com.gtnix.inovagab.domain.StatusIdeia;
import com.gtnix.inovagab.dto.ai.MetricasIaResponse;
import com.gtnix.inovagab.repository.AiScoreRunRepository;
import com.gtnix.inovagab.repository.IdeiaRepository;
import com.gtnix.inovagab.repository.OrientacaoRepository;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MetricasIaServiceTest {

    private final IdeiaRepository ideiaRepository = mock(IdeiaRepository.class);
    private final OrientacaoRepository orientacaoRepository = mock(OrientacaoRepository.class);
    private final AiScoreRunRepository runRepository = mock(AiScoreRunRepository.class);
    private final MetricasIaService service = new MetricasIaService(ideiaRepository, orientacaoRepository, runRepository);

    @Test
    void calculaConcordanciaFallbackViesETriagem() {
        OrientacaoDocument passageiros = new OrientacaoDocument();
        passageiros.setId("ori-1");
        passageiros.setDivisao("PASSAGEIROS");
        when(orientacaoRepository.findAll()).thenReturn(List.of(passageiros));
        when(ideiaRepository.findByAtivoTrue()).thenReturn(List.of(
                ideia("OPERACOES", 80, StatusIdeia.APROVADA, true, 10),
                ideia("OPERACOES", 60, StatusIdeia.REPROVADA, false, 30),
                ideia("TI", 20, StatusIdeia.REPROVADA, true, null),
                ideia("TI", null, StatusIdeia.PENDENTE, null, null)
        ));
        when(runRepository.findByTipo(AiScoreRunDocument.TIPO_SCORE)).thenReturn(List.of(run(false), run(false), run(false), run(true)));

        MetricasIaResponse m = service.metricas(null);

        assertEquals(3, m.ideiasAnalisadas());
        assertEquals(3, m.decisoesComOpiniao());
        assertEquals(2, m.concordancias());
        assertEquals(66.7, m.taxaConcordancia());
        assertEquals(25.0, m.percentualFallback());
        assertEquals(20.0, m.tempoMedioTriagemHoras());
        assertEquals(2, m.scorePorArea().size());
        assertEquals("OPERACOES", m.scorePorArea().get(0).grupo());
        assertEquals(70.0, m.scorePorArea().get(0).mediaScore());
        assertEquals("PASSAGEIROS", m.scorePorDivisao().get(0).grupo());
        assertEquals(RubricaIdeia.FAIXA_ALTA, m.aprovacaoPorFaixa().get(0).faixa());
        assertEquals(100.0, m.aprovacaoPorFaixa().get(0).taxaAprovacao());
        assertEquals(0.0, m.aprovacaoPorFaixa().get(1).taxaAprovacao());
    }

    @Test
    void semDadosRetornaIndicadoresNulos() {
        when(orientacaoRepository.findAll()).thenReturn(List.of());
        when(ideiaRepository.findByAtivoTrueAndOrientacaoId("ori-1")).thenReturn(List.of());
        when(runRepository.findByTipoAndOrientacaoId(AiScoreRunDocument.TIPO_SCORE, "ori-1")).thenReturn(List.of());

        MetricasIaResponse m = service.metricas("ori-1");

        assertNull(m.taxaConcordancia());
        assertNull(m.percentualFallback());
        assertNull(m.tempoMedioTriagemHoras());
        assertNull(m.aprovacaoPorFaixa().get(0).taxaAprovacao());
    }

    private static IdeiaDocument ideia(String area, Integer score, StatusIdeia status, Boolean concorda, Integer horasAteAvaliar) {
        IdeiaDocument doc = new IdeiaDocument();
        doc.setArea(area);
        doc.setOrientacaoId("ori-1");
        doc.setScoreIa(score);
        doc.setStatus(status);
        doc.setConcordaIa(concorda);
        Instant criacao = Instant.parse("2026-01-01T00:00:00Z");
        doc.setDataCriacao(criacao);
        if (status != StatusIdeia.PENDENTE) {
            doc.setDataAvaliacao(criacao.plusSeconds(3600L * (horasAteAvaliar == null ? 20 : horasAteAvaliar)));
        }
        return doc;
    }

    private static AiScoreRunDocument run(boolean fallback) {
        AiScoreRunDocument doc = new AiScoreRunDocument();
        doc.setTipo(AiScoreRunDocument.TIPO_SCORE);
        doc.setFallback(fallback);
        return doc;
    }
}
