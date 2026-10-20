package com.gtnix.inovagab.service;

import com.gtnix.inovagab.document.IdeiaDocument;
import com.gtnix.inovagab.document.OrientacaoDocument;
import com.gtnix.inovagab.document.ProjetoDocument;
import com.gtnix.inovagab.domain.StatusIdeia;
import com.gtnix.inovagab.domain.StatusProjeto;
import com.gtnix.inovagab.dto.dashboard.AreaDesempenhoResponse;
import com.gtnix.inovagab.dto.dashboard.DashboardByProjectResponse;
import com.gtnix.inovagab.dto.dashboard.DashboardByStrategyResponse;
import com.gtnix.inovagab.dto.dashboard.DashboardSummaryResponse;
import com.gtnix.inovagab.dto.dashboard.FunnelStepResponse;
import com.gtnix.inovagab.dto.projeto.ProjetoResponse;
import com.gtnix.inovagab.repository.IdeiaRepository;
import com.gtnix.inovagab.repository.OrientacaoRepository;
import com.gtnix.inovagab.repository.ProjetoRepository;
import com.gtnix.inovagab.web.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

@Service
public class DashboardService {

    private static final int TOP_AREAS = 4;
    private static final BigDecimal ZERO = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

    private final ProjetoRepository projetoRepository;
    private final IdeiaRepository ideiaRepository;
    private final OrientacaoRepository orientacaoRepository;
    private final ProjetoService projetoService;

    public DashboardService(
            ProjetoRepository projetoRepository,
            IdeiaRepository ideiaRepository,
            OrientacaoRepository orientacaoRepository,
            ProjetoService projetoService
    ) {
        this.projetoRepository = projetoRepository;
        this.ideiaRepository = ideiaRepository;
        this.orientacaoRepository = orientacaoRepository;
        this.projetoService = projetoService;
    }

    /**
     * Retorna o resumo geral do portfólio (todos os projetos e ideias ativos).
     */
    public DashboardSummaryResponse summary() {
        return summary(null);
    }

    /**
     * Retorna o resumo filtrado por orientação estratégica.
     *
     * @param orientacaoId ID da orientação; {@code null} retorna o portfólio completo.
     */
    public DashboardSummaryResponse summary(String orientacaoId) {
        List<ProjetoDocument> projetos;
        List<IdeiaDocument> ideias;

        if (orientacaoId != null && !orientacaoId.isBlank()) {
            projetos = carteira(projetoRepository.findByOrientacaoIdAndAtivoTrue(orientacaoId));
            ideias = ideiaRepository.findByAtivoTrueAndOrientacaoId(orientacaoId);
        } else {
            projetos = carteira(projetoRepository.findByAtivoTrue());
            ideias = ideiaRepository.findByAtivoTrue();
        }

        Financeiro financeiro = agregarFinanceiro(projetos);

        long totalIdeias = ideias.size();
        long ideiasAprovadas = ideias.stream()
                .filter(i -> i.getStatus() == StatusIdeia.APROVADA)
                .count();
        long ideiasEmProjeto = ideias.stream()
                .filter(i -> i.getStatus() == StatusIdeia.CONVERTIDA_PROJETO)
                .count();

        return new DashboardSummaryResponse(
                financeiro.roi(),
                financeiro.lucro(),
                financeiro.investimento(),
                financeiro.retorno(),
                financeiro.noPrazo(),
                financeiro.atrasados(),
                ganhoProdutividade(projetos),
                totalIdeias,
                ideiasAprovadas,
                ideiasEmProjeto,
                projetos.size(),
                tempoMedioAprovacaoDias(ideias),
                funnelFrom(totalIdeias, ideiasAprovadas, ideiasEmProjeto),
                byAreaFrom(ideias)
        );
    }

    public List<FunnelStepResponse> funnel() {
        List<IdeiaDocument> ideias = ideiaRepository.findByAtivoTrue();
        long total = ideias.size();
        long aprovadas = ideias.stream()
                .filter(i -> i.getStatus() == StatusIdeia.APROVADA)
                .count();
        long emProjeto = ideias.stream()
                .filter(i -> i.getStatus() == StatusIdeia.CONVERTIDA_PROJETO)
                .count();
        return funnelFrom(total, aprovadas, emProjeto);
    }

    public List<AreaDesempenhoResponse> byArea() {
        return byAreaFrom(ideiaRepository.findByAtivoTrue());
    }

    public DashboardByStrategyResponse byStrategy(String orientacaoId) {
        OrientacaoDocument orientacao = orientacaoRepository.findById(orientacaoId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Orientação não encontrada"));
        List<ProjetoDocument> docs = carteira(projetoRepository.findByOrientacaoIdAndAtivoTrue(orientacaoId));
        Financeiro financeiro = agregarFinanceiro(docs);
        List<ProjetoResponse> projetos = docs.stream()
                .map(doc -> projetoService.obter(doc.getId()))
                .toList();
        return new DashboardByStrategyResponse(
                orientacao.getId(),
                orientacao.getTitulo(),
                orientacao.isAtivo(),
                financeiro.investimento(),
                financeiro.retorno(),
                financeiro.lucro(),
                financeiro.roi(),
                financeiro.noPrazo(),
                financeiro.atrasados(),
                projetos
        );
    }

    public DashboardByProjectResponse byProject(String projetoId) {
        ProjetoResponse projeto = projetoService.obter(projetoId);
        BigDecimal investimento = nvl(projeto.investimento());
        BigDecimal retorno = nvl(projeto.retorno());
        LocalDate hoje = LocalDate.now();
        boolean noPrazo = projeto.prazo() == null || !projeto.prazo().isBefore(hoje)
                || StatusProjeto.CONCLUIDO.name().equalsIgnoreCase(projeto.status());
        return new DashboardByProjectResponse(
                projeto.id(),
                projeto.titulo(),
                projeto.orientacaoId(),
                projeto.status(),
                investimento,
                retorno,
                lucroAnualizado(investimento, retorno),
                nvl(projeto.roi()),
                projeto.prazo(),
                noPrazo,
                projeto.progressoPercentual()
        );
    }

    private Financeiro agregarFinanceiro(List<ProjetoDocument> projetos) {
        BigDecimal investimento = BigDecimal.ZERO;
        BigDecimal retorno = BigDecimal.ZERO;
        long noPrazo = 0;
        long atrasados = 0;
        LocalDate hoje = LocalDate.now();

        for (ProjetoDocument p : projetos) {
            investimento = investimento.add(nvl(p.getInvestimento()));
            retorno = retorno.add(nvl(p.getRetorno()));
            if (p.getPrazo() == null) {
                continue;
            }
            if (!p.getPrazo().isBefore(hoje)) {
                noPrazo++;
            } else if (!StatusProjeto.CONCLUIDO.name().equalsIgnoreCase(String.valueOf(p.getStatus()))) {
                atrasados++;
            }
        }

        return new Financeiro(
                scale(investimento),
                scale(retorno),
                lucroAnualizado(investimento, retorno),
                projetoService.calcularRoi(investimento, retorno),
                noPrazo,
                atrasados
        );
    }

    private List<ProjetoDocument> carteira(List<ProjetoDocument> projetos) {
        return projetos.stream().filter(p -> !isCancelado(p)).toList();
    }

    private static boolean isCancelado(ProjetoDocument projeto) {
        return StatusProjeto.CANCELADO.name().equalsIgnoreCase(String.valueOf(projeto.getStatus()));
    }

    private BigDecimal ganhoProdutividade(List<ProjetoDocument> projetos) {
        List<Integer> progressos = projetos.stream()
                .map(ProjetoDocument::getProgressoPercentual)
                .filter(Objects::nonNull)
                .toList();
        if (progressos.isEmpty()) {
            return ZERO;
        }
        double media = progressos.stream().mapToInt(Integer::intValue).average().orElse(0);
        return BigDecimal.valueOf(media).setScale(1, RoundingMode.HALF_UP);
    }

    private long tempoMedioAprovacaoDias(List<IdeiaDocument> ideias) {
        List<Long> dias = new ArrayList<>();
        for (IdeiaDocument ideia : ideias) {
            if (!isAvaliada(ideia.getStatus()) || ideia.getDataCriacao() == null) {
                continue;
            }
            Instant avaliacao = ideia.getDataAvaliacao() != null
                    ? ideia.getDataAvaliacao()
                    : ideia.getDataAtualizacao();
            if (avaliacao == null || avaliacao.isBefore(ideia.getDataCriacao())) {
                continue;
            }
            dias.add(Duration.between(ideia.getDataCriacao(), avaliacao).toDays());
        }
        if (dias.isEmpty()) {
            return 0;
        }
        return Math.round(dias.stream().mapToLong(Long::longValue).average().orElse(0));
    }

    private List<FunnelStepResponse> funnelFrom(long total, long aprovadas, long emProjeto) {
        return List.of(
                new FunnelStepResponse("IDEIAS", "Ideias", total),
                new FunnelStepResponse("APROVADAS", "Aprovadas", aprovadas),
                new FunnelStepResponse("EM_PROJETO", "Em Projeto", emProjeto)
        );
    }

    private List<AreaDesempenhoResponse> byAreaFrom(List<IdeiaDocument> ideias) {
        if (ideias.isEmpty()) {
            return List.of();
        }
        Map<String, Long> porArea = new LinkedHashMap<>();
        for (IdeiaDocument ideia : ideias) {
            String area = ideia.getArea() == null || ideia.getArea().isBlank()
                    ? "OUTROS"
                    : ideia.getArea().trim().toUpperCase(Locale.ROOT);
            porArea.merge(area, 1L, Long::sum);
        }
        BigDecimal total = BigDecimal.valueOf(ideias.size());
        return porArea.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(TOP_AREAS)
                .map(entry -> new AreaDesempenhoResponse(
                        entry.getKey(),
                        labelArea(entry.getKey()),
                        entry.getValue(),
                        BigDecimal.valueOf(entry.getValue())
                                .multiply(BigDecimal.valueOf(100))
                                .divide(total, 1, RoundingMode.HALF_UP)
                ))
                .sorted(Comparator.comparing(AreaDesempenhoResponse::percentual).reversed())
                .toList();
    }

    private boolean isAvaliada(StatusIdeia status) {
        return status == StatusIdeia.APROVADA
                || status == StatusIdeia.REPROVADA
                || status == StatusIdeia.CONVERTIDA_PROJETO;
    }

    private static BigDecimal lucroAnualizado(BigDecimal investimento, BigDecimal retorno) {
        return scale(nvl(retorno).multiply(ProjetoService.DOZE).subtract(nvl(investimento)));
    }

    private static BigDecimal nvl(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private static BigDecimal scale(BigDecimal value) {
        return nvl(value).setScale(2, RoundingMode.HALF_UP);
    }

    static String labelArea(String area) {
        return switch (area) {
            case "OPERACOES" -> "Operações";
            case "LOGISTICA" -> "Logística";
            case "COMERCIAL" -> "Comercial";
            case "FINANCEIRO" -> "Financeiro";
            case "RH" -> "RH";
            case "TI" -> "TI";
            case "MARKETING" -> "Marketing";
            case "QUALIDADE" -> "Qualidade";
            default -> area;
        };
    }

    private record Financeiro(
            BigDecimal investimento,
            BigDecimal retorno,
            BigDecimal lucro,
            BigDecimal roi,
            long noPrazo,
            long atrasados
    ) {
    }
}
