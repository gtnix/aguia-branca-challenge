package com.gtnix.inovagab.service;

import com.gtnix.inovagab.document.AiScoreRunDocument;
import com.gtnix.inovagab.document.IdeiaDocument;
import com.gtnix.inovagab.document.OrientacaoDocument;
import com.gtnix.inovagab.domain.StatusIdeia;
import com.gtnix.inovagab.dto.ai.MetricasIaResponse;
import com.gtnix.inovagab.dto.ai.MetricasIaResponse.FaixaAprovacao;
import com.gtnix.inovagab.dto.ai.MetricasIaResponse.GrupoScore;
import com.gtnix.inovagab.repository.AiScoreRunRepository;
import com.gtnix.inovagab.repository.IdeiaRepository;
import com.gtnix.inovagab.repository.OrientacaoRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/** Mede se a IA ajuda o gestor (concordância, calibração por faixa) e se distorce algum grupo (viés por área e divisão). */
@Service
public class MetricasIaService {

    static final String SEM_AREA = "SEM_AREA";
    static final String TODO_GRUPO = "GRUPO";

    private final IdeiaRepository ideiaRepository;
    private final OrientacaoRepository orientacaoRepository;
    private final AiScoreRunRepository aiScoreRunRepository;

    public MetricasIaService(
            IdeiaRepository ideiaRepository,
            OrientacaoRepository orientacaoRepository,
            AiScoreRunRepository aiScoreRunRepository
    ) {
        this.ideiaRepository = ideiaRepository;
        this.orientacaoRepository = orientacaoRepository;
        this.aiScoreRunRepository = aiScoreRunRepository;
    }

    public MetricasIaResponse metricas(String orientacaoId) {
        boolean filtrar = orientacaoId != null && !orientacaoId.isBlank();
        List<IdeiaDocument> ideias = filtrar
                ? ideiaRepository.findByAtivoTrueAndOrientacaoId(orientacaoId)
                : ideiaRepository.findByAtivoTrue();
        List<AiScoreRunDocument> execucoes = filtrar
                ? aiScoreRunRepository.findByTipoAndOrientacaoId(AiScoreRunDocument.TIPO_SCORE, orientacaoId)
                : aiScoreRunRepository.findByTipo(AiScoreRunDocument.TIPO_SCORE);
        Map<String, String> divisaoPorOrientacao = orientacaoRepository.findAll().stream()
                .filter(o -> o.getId() != null)
                .collect(Collectors.toMap(
                        OrientacaoDocument::getId,
                        o -> o.getDivisao() == null || o.getDivisao().isBlank() ? TODO_GRUPO : o.getDivisao()));

        List<IdeiaDocument> comScore = ideias.stream().filter(i -> i.getScoreIa() != null).toList();
        List<IdeiaDocument> comOpiniao = ideias.stream().filter(i -> i.getConcordaIa() != null).toList();
        long concordancias = comOpiniao.stream().filter(i -> Boolean.TRUE.equals(i.getConcordaIa())).count();
        long fallbacks = execucoes.stream().filter(AiScoreRunDocument::isFallback).count();

        return new MetricasIaResponse(
                comScore.size(),
                comOpiniao.size(),
                concordancias,
                percentual(concordancias, comOpiniao.size()),
                execucoes.size(),
                percentual(fallbacks, execucoes.size()),
                tempoMedioTriagemHoras(ideias),
                agrupar(comScore, i -> i.getArea() == null || i.getArea().isBlank() ? SEM_AREA : i.getArea()),
                agrupar(comScore, i -> divisaoPorOrientacao.getOrDefault(i.getOrientacaoId(), TODO_GRUPO)),
                aprovacaoPorFaixa(comScore)
        );
    }

    private static List<GrupoScore> agrupar(List<IdeiaDocument> ideias, Function<IdeiaDocument, String> chave) {
        return ideias.stream()
                .collect(Collectors.groupingBy(chave))
                .entrySet().stream()
                .map(e -> new GrupoScore(
                        e.getKey(),
                        e.getValue().size(),
                        arredondar(e.getValue().stream().mapToInt(IdeiaDocument::getScoreIa).average().orElse(0))))
                .sorted(Comparator.comparing(GrupoScore::grupo))
                .toList();
    }

    private static List<FaixaAprovacao> aprovacaoPorFaixa(List<IdeiaDocument> comScore) {
        List<FaixaAprovacao> faixas = new ArrayList<>();
        for (String faixa : List.of(RubricaIdeia.FAIXA_ALTA, RubricaIdeia.FAIXA_MEDIA, RubricaIdeia.FAIXA_BAIXA)) {
            List<IdeiaDocument> avaliadas = comScore.stream()
                    .filter(i -> faixa.equals(RubricaIdeia.faixa(i.getScoreIa())))
                    .filter(i -> i.getDataAvaliacao() != null)
                    .toList();
            long aprovadas = avaliadas.stream()
                    .filter(i -> i.getStatus() != StatusIdeia.REPROVADA && i.getStatus() != StatusIdeia.PENDENTE)
                    .count();
            faixas.add(new FaixaAprovacao(faixa, avaliadas.size(), aprovadas, percentual(aprovadas, avaliadas.size())));
        }
        return faixas;
    }

    private static Double tempoMedioTriagemHoras(List<IdeiaDocument> ideias) {
        List<Duration> tempos = ideias.stream()
                .filter(i -> i.getDataCriacao() != null && i.getDataAvaliacao() != null)
                .map(i -> Duration.between(i.getDataCriacao(), i.getDataAvaliacao()))
                .filter(d -> !d.isNegative())
                .toList();
        if (tempos.isEmpty()) {
            return null;
        }
        double mediaMinutos = tempos.stream().mapToLong(Duration::toMinutes).average().orElse(0);
        return arredondar(mediaMinutos / 60.0);
    }

    private static Double percentual(long parte, long total) {
        return total == 0 ? null : arredondar(parte * 100.0 / total);
    }

    private static double arredondar(double valor) {
        return Math.round(valor * 10) / 10.0;
    }
}
