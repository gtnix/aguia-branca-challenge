package com.gtnix.inovagab.service;

import com.gtnix.inovagab.document.IdeiaDocument;
import com.gtnix.inovagab.document.OrientacaoDocument;
import com.gtnix.inovagab.document.ProjetoDocument;
import com.gtnix.inovagab.domain.StatusIdeia;
import com.gtnix.inovagab.dto.dashboard.DashboardByProjectResponse;
import com.gtnix.inovagab.dto.dashboard.DashboardByStrategyResponse;
import com.gtnix.inovagab.dto.dashboard.DashboardSummaryResponse;
import com.gtnix.inovagab.dto.projeto.ProjetoResponse;
import com.gtnix.inovagab.repository.IdeiaRepository;
import com.gtnix.inovagab.repository.OrientacaoRepository;
import com.gtnix.inovagab.repository.ProjetoRepository;
import com.gtnix.inovagab.web.ApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private ProjetoRepository projetoRepository;
    @Mock
    private IdeiaRepository ideiaRepository;
    @Mock
    private OrientacaoRepository orientacaoRepository;
    @Mock
    private OrientacaoService orientacaoService;
    @Mock
    private GamificacaoService gamificacaoService;

    private ProjetoService projetoService;
    private DashboardService service;

    @BeforeEach
    void setUp() {
        projetoService = new ProjetoService(projetoRepository, orientacaoService, ideiaRepository, gamificacaoService);
        service = new DashboardService(projetoRepository, ideiaRepository, orientacaoRepository, projetoService);
    }

    @Test
    void summaryCalculaKpisESeries() {
        when(projetoRepository.findByAtivoTrue()).thenReturn(List.of(
                projeto("p1", "OPERACOES", "ori-1", "EM_ANDAMENTO", 100_000, 10_000, LocalDate.now().plusDays(10), 40),
                projeto("p2", "LOGISTICA", "ori-1", "PLANEJADO", 50_000, 5_000, LocalDate.now().minusDays(2), 0)
        ));
        Instant criacao = Instant.parse("2026-01-01T00:00:00Z");
        Instant avaliacao = Instant.parse("2026-01-04T00:00:00Z");
        when(ideiaRepository.findByAtivoTrue()).thenReturn(List.of(
                ideia("i1", "OPERACOES", StatusIdeia.PENDENTE, criacao, null),
                ideia("i2", "OPERACOES", StatusIdeia.APROVADA, criacao, avaliacao),
                ideia("i3", "LOGISTICA", StatusIdeia.CONVERTIDA_PROJETO, criacao, avaliacao),
                ideia("i4", "RH", StatusIdeia.REPROVADA, criacao, avaliacao)
        ));

        DashboardSummaryResponse summary = service.summary();

        assertEquals(new BigDecimal("20.00"), summary.roiMedio());
        assertEquals(new BigDecimal("30000.00"), summary.lucroTotal());
        assertEquals(new BigDecimal("150000.00"), summary.investimentoTotal());
        assertEquals(new BigDecimal("15000.00"), summary.retornoTotal());
        assertEquals(1, summary.projetosNoPrazo());
        assertEquals(1, summary.projetosAtrasados());
        assertEquals(new BigDecimal("20.0"), summary.ganhoProdutividadePercentual());
        assertEquals(4, summary.totalIdeias());
        assertEquals(1, summary.ideiasAprovadas());
        assertEquals(1, summary.ideiasEmProjeto());
        assertEquals(2, summary.projetosAtivos());
        assertEquals(3, summary.tempoMedioAprovacaoDias());
        assertEquals(3, summary.funil().size());
        assertEquals(4, summary.funil().get(0).quantidade());
        assertEquals(1, summary.funil().get(1).quantidade());
        assertEquals(1, summary.funil().get(2).quantidade());
        assertEquals("OPERACOES", summary.desempenhoPorArea().get(0).area());
        assertEquals("Operações", summary.desempenhoPorArea().get(0).label());
        assertEquals(new BigDecimal("50.0"), summary.desempenhoPorArea().get(0).percentual());
    }

    @Test
    void summaryExcluiCanceladoDeRoiInvestimentoEProjetosAtivos() {
        when(projetoRepository.findByAtivoTrue()).thenReturn(List.of(
                projeto("p1", "OPERACOES", "ori-1", "EM_ANDAMENTO", 100_000, 10_000, LocalDate.now().plusDays(10), 40),
                projeto("p2", "LOGISTICA", "ori-1", "CANCELADO", 900_000, 1_000, LocalDate.now().minusDays(2), 90)
        ));
        when(ideiaRepository.findByAtivoTrue()).thenReturn(List.of());

        DashboardSummaryResponse summary = service.summary();

        assertEquals(new BigDecimal("20.00"), summary.roiMedio());
        assertEquals(new BigDecimal("100000.00"), summary.investimentoTotal());
        assertEquals(new BigDecimal("10000.00"), summary.retornoTotal());
        assertEquals(new BigDecimal("20000.00"), summary.lucroTotal());
        assertEquals(1, summary.projetosAtivos());
        assertEquals(1, summary.projetosNoPrazo());
        assertEquals(0, summary.projetosAtrasados());
        assertEquals(new BigDecimal("40.0"), summary.ganhoProdutividadePercentual());
    }

    @Test
    void byProjectInexistenteRetorna404() {
        when(projetoRepository.findByIdAndAtivoTrue("faltando")).thenReturn(Optional.empty());

        ApiException ex = assertThrows(ApiException.class, () -> service.byProject("faltando"));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
    }

    @Test
    void summaryVazioZeraKpis() {
        when(projetoRepository.findByAtivoTrue()).thenReturn(List.of());
        when(ideiaRepository.findByAtivoTrue()).thenReturn(List.of());

        DashboardSummaryResponse summary = service.summary();

        assertEquals(new BigDecimal("0.00"), summary.roiMedio());
        assertEquals(new BigDecimal("0.00"), summary.lucroTotal());
        assertEquals(0, summary.totalIdeias());
        assertEquals(0, summary.tempoMedioAprovacaoDias());
        assertTrue(summary.desempenhoPorArea().isEmpty());
        assertEquals(0, summary.funil().get(0).quantidade());
    }

    @Test
    void byStrategyAgregaProjetosDaOrientacao() {
        OrientacaoDocument orientacao = new OrientacaoDocument();
        orientacao.setId("ori-1");
        orientacao.setTitulo("Eficiência");
        when(orientacaoRepository.findById("ori-1")).thenReturn(Optional.of(orientacao));
        ProjetoDocument doc = projeto("p1", "OPERACOES", "ori-1", "PLANEJADO", 80_000, 9_000, LocalDate.now().plusMonths(1), 0);
        when(projetoRepository.findByOrientacaoIdAndAtivoTrue("ori-1")).thenReturn(List.of(doc));
        when(projetoRepository.findByIdAndAtivoTrue("p1")).thenReturn(Optional.of(doc));

        DashboardByStrategyResponse response = service.byStrategy("ori-1");

        assertEquals("ori-1", response.orientacaoId());
        assertEquals("Eficiência", response.titulo());
        assertEquals(new BigDecimal("35.00"), response.roiMedio());
        assertEquals(1, response.projetos().size());
        assertEquals("p1", response.projetos().get(0).id());
    }

    @Test
    void byStrategyInexistenteRetorna404() {
        when(orientacaoRepository.findById("faltando")).thenReturn(Optional.empty());

        ApiException ex = assertThrows(ApiException.class, () -> service.byStrategy("faltando"));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
    }

    @Test
    void byProjectRetornaRetornoERoi() {
        ProjetoDocument doc = projeto("p1", "OPERACOES", "ori-1", "EM_ANDAMENTO", 100_000, 10_000, LocalDate.now().plusDays(5), 40);
        when(projetoRepository.findByIdAndAtivoTrue("p1")).thenReturn(Optional.of(doc));

        DashboardByProjectResponse response = service.byProject("p1");

        assertEquals("p1", response.projetoId());
        assertEquals(new BigDecimal("20000.00"), response.lucroAnualizado());
        assertEquals(new BigDecimal("20.00"), response.roi());
        assertTrue(response.noPrazo());
    }

    @Test
    void labelAreaConhecida() {
        assertEquals("Operações", DashboardService.labelArea("OPERACOES"));
        assertEquals("Logística", DashboardService.labelArea("LOGISTICA"));
    }

    private static ProjetoDocument projeto(
            String id,
            String area,
            String orientacaoId,
            String status,
            double investimento,
            double retorno,
            LocalDate prazo,
            int progresso
    ) {
        ProjetoDocument doc = new ProjetoDocument();
        doc.setId(id);
        doc.setTitulo("Projeto " + id);
        doc.setArea(area);
        doc.setOrientacaoId(orientacaoId);
        doc.setStatus(status);
        doc.setInvestimento(BigDecimal.valueOf(investimento));
        doc.setRetorno(BigDecimal.valueOf(retorno));
        doc.setPrazo(prazo);
        doc.setProgressoPercentual(progresso);
        return doc;
    }

    private static IdeiaDocument ideia(
            String id,
            String area,
            StatusIdeia status,
            Instant criacao,
            Instant avaliacao
    ) {
        IdeiaDocument doc = new IdeiaDocument();
        doc.setId(id);
        doc.setArea(area);
        doc.setStatus(status);
        doc.setDataCriacao(criacao);
        doc.setDataAvaliacao(avaliacao);
        doc.setDataAtualizacao(avaliacao);
        return doc;
    }
}
