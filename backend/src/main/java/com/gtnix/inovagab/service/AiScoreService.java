package com.gtnix.inovagab.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gtnix.inovagab.config.GeminiProperties;
import com.gtnix.inovagab.document.AiScoreRunDocument;
import com.gtnix.inovagab.document.IdeiaDocument;
import com.gtnix.inovagab.document.OrientacaoDocument;
import com.gtnix.inovagab.domain.StatusAnaliseIa;
import com.gtnix.inovagab.dto.ai.DashboardNarrativeResponse;
import com.gtnix.inovagab.dto.ai.IdeaScoreItem;
import com.gtnix.inovagab.dto.ai.RascunhoFeedbackRequest;
import com.gtnix.inovagab.dto.ai.RascunhoFeedbackResponse;
import com.gtnix.inovagab.dto.ai.ScoreIdeasRequest;
import com.gtnix.inovagab.dto.ai.ScoreIdeasResponse;
import com.gtnix.inovagab.dto.dashboard.DashboardSummaryResponse;
import com.gtnix.inovagab.web.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class AiScoreService {

    private static final Logger log = LoggerFactory.getLogger(AiScoreService.class);

    static final String MODELO_HEURISTICO = "heuristic-local";

    static final String FALLBACK_TITULO_PT = "Resumo automático do portfólio";
    static final String FALLBACK_NARRATIVA_PT =
            "Portfólio de inovação carregado com sucesso. Consulte os indicadores financeiros e o funil de ideias para acompanhar o progresso das iniciativas.";

    private final GeminiProperties geminiProperties;
    private final IdeiaService ideiaService;
    private final OrientacaoService orientacaoService;
    private final DashboardService dashboardService;
    private final ObjectMapper objectMapper;
    private final GeminiClient geminiClient;
    private final AiAuditoriaService aiAuditoriaService;

    public AiScoreService(
            GeminiProperties geminiProperties,
            IdeiaService ideiaService,
            OrientacaoService orientacaoService,
            DashboardService dashboardService,
            ObjectMapper objectMapper,
            GeminiClient geminiClient,
            AiAuditoriaService aiAuditoriaService
    ) {
        this.geminiProperties = geminiProperties;
        this.ideiaService = ideiaService;
        this.orientacaoService = orientacaoService;
        this.dashboardService = dashboardService;
        this.objectMapper = objectMapper;
        this.geminiClient = geminiClient;
        this.aiAuditoriaService = aiAuditoriaService;
    }

    /**
     * Gera narrativa estruturada para o portfólio completo.
     */
    public DashboardNarrativeResponse narrativaDashboard() {
        return narrativaDashboard(null);
    }

    /**
     * Gera narrativa estruturada, opcionalmente filtrada por orientação estratégica.
     *
     * @param orientacaoId ID da orientação ou {@code null} para portfólio completo.
     */
    public DashboardNarrativeResponse narrativaDashboard(String orientacaoId) {
        DashboardSummaryResponse summary = dashboardService.summary(orientacaoId);
        if (!geminiProperties.isConfigured()) {
            log.debug("Gemini API key not configured; using local narrative fallback");
            return buildFallbackNarrativa(summary);
        }
        try {
            String prompt = buildDashboardPrompt(summary);
            String responseBody = geminiClient.generateContent(
                    geminiProperties.getModel(), geminiProperties.getApiKey(), prompt);
            JsonNode root = objectMapper.readTree(responseBody);
            String rawText = root.path("candidates")
                    .path(0).path("content").path("parts").path(0).path("text").asText().trim();
            if (rawText.isBlank()) {
                throw new IllegalStateException("resposta Gemini vazia");
            }
            return parseStructuredNarrativa(rawText, geminiProperties.getModel());
        } catch (Exception ex) {
            log.warn("Gemini narrative failed, using local fallback: {}", ex.getMessage());
            return buildFallbackNarrativa(summary);
        }
    }

    private DashboardNarrativeResponse parseStructuredNarrativa(String rawText, String modelo) throws Exception {
        // Extrai JSON do texto — Gemini pode prefixar com markdown ```json
        String json = rawText;
        int start = json.indexOf('{');
        int end = json.lastIndexOf('}');
        if (start >= 0 && end > start) {
            json = json.substring(start, end + 1);
        }
        JsonNode node = objectMapper.readTree(json);
        String titulo = node.path("titulo").asText("").trim();
        String resumo = node.path("resumo").asText("").trim();
        List<String> evidencias = new ArrayList<>();
        JsonNode evNode = node.path("evidencias");
        if (evNode.isArray()) {
            evNode.forEach(e -> evidencias.add(e.asText("").trim()));
        }
        String acao = node.path("acaoRecomendada").asText("").trim();
        if (titulo.isBlank() || resumo.isBlank()) {
            throw new IllegalStateException("campos obrigatórios ausentes no JSON Gemini");
        }
        return new DashboardNarrativeResponse(titulo, resumo, evidencias, acao, modelo, false, Instant.now());
    }

    private String buildDashboardPrompt(DashboardSummaryResponse s) {
        StringBuilder sb = new StringBuilder();
        sb.append("Você é um analista executivo de inovação corporativa.\n");
        sb.append("Analise as métricas do portfólio abaixo e retorne APENAS um JSON com esta estrutura exata:\n");
        sb.append("{\n");
        sb.append("  \"titulo\": \"<sentença executiva de até 10 palavras>\",\n");
        sb.append("  \"resumo\": \"<até 2 frases sobre a saúde do portfólio>\",\n");
        sb.append("  \"evidencias\": [\"<dado numérico 1>\", \"<dado numérico 2>\", \"<dado numérico 3>\"],\n");
        sb.append("  \"acaoRecomendada\": \"<uma ação concreta e executável>\"\n");
        sb.append("}\n\n");
        sb.append("REGRAS OBRIGATÓRIAS:\n");
        sb.append("1. Use APENAS números presentes nos dados. Nunca invente métricas.\n");
        sb.append("2. Identifique UMA anomalia ou padrão (gargalo, prazo em risco, baixa conversão).\n");
        sb.append("3. Evidências devem ser frases curtas citando o número exato (ex: \"ROI de 28%\").\n");
        sb.append("4. Responda em português do Brasil, tom executivo.\n");
        sb.append("5. Retorne APENAS o JSON — sem markdown, sem explicação extra.\n");
        sb.append("6. \"Ideias aprovadas\" = ainda sem projeto (fila). \"Ideias em projeto\" = já convertidas. Não subtraia uma da outra.\n\n");
        sb.append("Métricas do portfólio:\n");
        sb.append("- Total de ideias: ").append(s.totalIdeias()).append("\n");
        sb.append("- Ideias aprovadas (ainda sem projeto): ").append(s.ideiasAprovadas()).append("\n");
        sb.append("- Ideias em projeto (já convertidas): ").append(s.ideiasEmProjeto()).append("\n");
        sb.append("- Projetos ativos: ").append(s.projetosAtivos()).append("\n");
        sb.append("- Projetos no prazo: ").append(s.projetosNoPrazo()).append("\n");
        sb.append("- Projetos atrasados: ").append(s.projetosAtrasados()).append("\n");
        sb.append("- Investimento total: R$ ").append(s.investimentoTotal()).append("\n");
        sb.append("- Retorno mensal: R$ ").append(s.retornoTotal()).append("\n");
        sb.append("- Lucro anualizado: R$ ").append(s.lucroTotal()).append("\n");
        sb.append("- ROI médio: ").append(s.roiMedio()).append("%\n");
        sb.append("- Progresso médio dos projetos: ").append(s.ganhoProdutividadePercentual()).append("%\n");
        sb.append("- Tempo médio de aprovação: ").append(s.tempoMedioAprovacaoDias()).append(" dias\n");
        if (s.desempenhoPorArea() != null && !s.desempenhoPorArea().isEmpty()) {
            sb.append("- Participação por área:\n");
            for (var area : s.desempenhoPorArea()) {
                sb.append("  * ").append(area.label()).append(": ").append(area.percentual()).append("%\n");
            }
        }
        return sb.toString();
    }

    private DashboardNarrativeResponse buildFallbackNarrativa(DashboardSummaryResponse s) {
        long totalIdeias = s.totalIdeias();
        double roi = s.roiMedio() != null ? s.roiMedio().doubleValue() : 0.0;
        long aprovadas = s.ideiasAprovadas();
        long emProjeto = s.ideiasEmProjeto();
        long atrasados = s.projetosAtrasados();

        String titulo;
        String resumo;
        String acao;
        List<String> evidencias = new ArrayList<>();

        if (totalIdeias == 0) {
            titulo = "Portfólio sem ideias registradas";
            resumo = "Ainda não há ideias registradas. Incentive a equipe a submeter propostas alinhadas às orientações estratégicas.";
            acao = "Compartilhe as orientações estratégicas com a equipe para estimular novas submissões.";
        } else if (atrasados > 0) {
            titulo = String.format("Portfólio com %d projeto(s) atrasado(s)", atrasados);
            resumo = String.format("O portfólio conta com %d ideias e ROI de %.0f%% a.a. Há %d projeto(s) fora do prazo exigindo atenção.", totalIdeias, roi, atrasados);
            acao = String.format("Revisar os %d projeto(s) atrasado(s) e definir plano de recuperação de prazo.", atrasados);
            evidencias.add(String.format("ROI de %.0f%% a.a.", roi));
            evidencias.add(String.format("%d projeto(s) atrasado(s)", atrasados));
        } else if (roi > 0) {
            titulo = String.format("Portfólio com ROI de %.0f%% e %d ideias ativas", roi, totalIdeias);
            if (aprovadas > 0) {
                resumo = String.format("O portfólio conta com %d ideias ativas e ROI consolidado de %.0f%% a.a. %d ideia(s) aprovada(s) aguardam conversão em projeto.", totalIdeias, roi, aprovadas);
                acao = "Converter as ideias aprovadas em projetos para maximizar o retorno consolidado.";
            } else if (emProjeto > 0) {
                resumo = String.format("O portfólio conta com %d ideias ativas e ROI consolidado de %.0f%% a.a. %d já viraram projeto.", totalIdeias, roi, emProjeto);
                acao = "Acompanhar o prazo e o progresso dos projetos em execução.";
            } else {
                resumo = String.format("O portfólio conta com %d ideias ativas e ROI consolidado de %.0f%% a.a.", totalIdeias, roi);
                acao = "Priorizar a avaliação das ideias pendentes para acelerar o início de novos projetos.";
            }
            evidencias.add(String.format("ROI de %.0f%% a.a.", roi));
            if (aprovadas > 0) evidencias.add(String.format("%d na fila de conversão", aprovadas));
            if (emProjeto > 0) evidencias.add(String.format("%d já em projeto", emProjeto));
        } else {
            titulo = String.format("Portfólio com %d ideias ativas", totalIdeias);
            resumo = String.format("O portfólio conta com %d ideias. Acompanhe a conversão em projetos para elevar o retorno consolidado.", totalIdeias);
            acao = "Priorizar a avaliação das ideias pendentes para acelerar o início de novos projetos.";
            evidencias.add(String.format("%d ideias totais", totalIdeias));
            evidencias.add(String.format("%d aprovadas", aprovadas));
        }

        return new DashboardNarrativeResponse(
                titulo, resumo, evidencias, acao, MODELO_HEURISTICO, true, Instant.now());
    }

    public ScoreIdeasResponse score(ScoreIdeasRequest request, String gestorUserId) {
        OrientacaoDocument orientacao = orientacaoService.requireAtiva(request.orientacaoId());
        List<IdeiaDocument> ideias = ideiaService.findPendentesByIds(request.ideiaIds(), request.orientacaoId());
        if (ideias.isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Nenhuma ideia pendente válida informada");
        }
        boolean configurado = geminiProperties.isConfigured();
        if (!configurado) {
            log.debug("Gemini API key not configured; using automatic estimate fallback");
        }
        List<IdeaScoreItem> ranking = new ArrayList<>();
        boolean algumaIa = false;
        boolean algumFallback = false;
        for (IdeiaDocument ideia : ideias) {
            AnaliseIa analise = configurado ? analisar(orientacao, ideia, gestorUserId) : null;
            String modelo = analise != null ? geminiProperties.getModel() : MODELO_HEURISTICO;
            IdeaScoreItem item = ideiaService.aplicarAnalise(ideia, analise, modelo);
            ranking.add(item);
            algumaIa |= item.status() == StatusAnaliseIa.IA;
            algumFallback |= analise == null;
        }
        ranking.sort(Comparator.comparing(IdeaScoreItem::score, Comparator.nullsLast(Comparator.reverseOrder())));
        return new ScoreIdeasResponse(ranking, algumaIa ? geminiProperties.getModel() : MODELO_HEURISTICO, algumFallback);
    }

    /** Uma chamada por ideia: a nota não depende de quais outras ideias estão no lote nem da posição. */
    private AnaliseIa analisar(OrientacaoDocument orientacao, IdeiaDocument ideia, String gestorUserId) {
        String prompt = PromptsIa.score(orientacao, ideia);
        long inicio = System.nanoTime();
        String saida = null;
        try {
            saida = extrairTexto(geminiClient.generateContent(
                    geminiProperties.getModel(), geminiProperties.getApiKey(), prompt, PromptsIa.SCHEMA_SCORE));
            AnaliseIa analise = PromptsIa.parseScore(objectMapper, saida);
            auditar(AiScoreRunDocument.TIPO_SCORE, ideia, gestorUserId, geminiProperties.getModel(),
                    PromptsIa.VERSAO_SCORE, prompt, saida, inicio, false, null);
            return analise;
        } catch (Exception ex) {
            log.warn("Gemini scoring failed for idea {}, using automatic estimate: {}", ideia.getId(), ex.getMessage());
            auditar(AiScoreRunDocument.TIPO_SCORE, ideia, gestorUserId, geminiProperties.getModel(),
                    PromptsIa.VERSAO_SCORE, prompt, saida, inicio, true, ex.getMessage());
            return null;
        }
    }

    public RascunhoFeedbackResponse rascunhoFeedback(String ideiaId, RascunhoFeedbackRequest request, String gestorUserId) {
        IdeiaDocument ideia = ideiaService.buscarAtiva(ideiaId);
        String observacoes = request == null ? null : request.observacoes();
        if (!geminiProperties.isConfigured()) {
            return new RascunhoFeedbackResponse(PromptsIa.rascunhoLocal(ideia, observacoes), MODELO_HEURISTICO, true);
        }
        OrientacaoDocument orientacao = orientacaoOuNulo(ideia.getOrientacaoId());
        String prompt = PromptsIa.rascunhoFeedback(orientacao, ideia, observacoes);
        long inicio = System.nanoTime();
        String saida = null;
        try {
            saida = extrairTexto(geminiClient.generateContent(
                    geminiProperties.getModel(), geminiProperties.getApiKey(), prompt, PromptsIa.SCHEMA_RASCUNHO));
            String rascunho = PromptsIa.parseRascunho(objectMapper, saida);
            auditar(AiScoreRunDocument.TIPO_RASCUNHO_FEEDBACK, ideia, gestorUserId, geminiProperties.getModel(),
                    PromptsIa.VERSAO_RASCUNHO, prompt, saida, inicio, false, null);
            return new RascunhoFeedbackResponse(rascunho, geminiProperties.getModel(), false);
        } catch (Exception ex) {
            log.warn("Gemini feedback draft failed, using local template: {}", ex.getMessage());
            auditar(AiScoreRunDocument.TIPO_RASCUNHO_FEEDBACK, ideia, gestorUserId, geminiProperties.getModel(),
                    PromptsIa.VERSAO_RASCUNHO, prompt, saida, inicio, true, ex.getMessage());
            return new RascunhoFeedbackResponse(PromptsIa.rascunhoLocal(ideia, observacoes), MODELO_HEURISTICO, true);
        }
    }

    private OrientacaoDocument orientacaoOuNulo(String orientacaoId) {
        try {
            return orientacaoId == null ? null : orientacaoService.requireAtiva(orientacaoId);
        } catch (ApiException ex) {
            return null;
        }
    }

    private String extrairTexto(String responseBody) throws Exception {
        JsonNode root = objectMapper.readTree(responseBody);
        String text = root.path("candidates").path(0).path("content").path("parts").path(0).path("text").asText();
        if (text.isBlank()) {
            throw new IllegalStateException("resposta Gemini vazia");
        }
        return text;
    }

    private void auditar(String tipo, IdeiaDocument ideia, String gestorUserId, String modelo, String versao,
                         String prompt, String saida, long inicioNanos, boolean fallback, String erro) {
        long latenciaMs = (System.nanoTime() - inicioNanos) / 1_000_000;
        aiAuditoriaService.registrar(new AiAuditoriaService.Execucao(
                tipo, ideia.getId(), ideia.getOrientacaoId(), gestorUserId, modelo, versao,
                prompt, saida, latenciaMs, fallback, erro));
    }
}
