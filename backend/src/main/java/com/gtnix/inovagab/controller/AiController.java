package com.gtnix.inovagab.controller;

import com.gtnix.inovagab.dto.ai.DashboardNarrativeResponse;
import com.gtnix.inovagab.dto.ai.MetricasIaResponse;
import com.gtnix.inovagab.dto.ai.RascunhoFeedbackRequest;
import com.gtnix.inovagab.dto.ai.RascunhoFeedbackResponse;
import com.gtnix.inovagab.dto.ai.ScoreIdeasRequest;
import com.gtnix.inovagab.dto.ai.ScoreIdeasResponse;
import com.gtnix.inovagab.security.SecurityUtils;
import com.gtnix.inovagab.service.AiScoreService;
import com.gtnix.inovagab.service.MetricasIaService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ai")
public class AiController {

    private final AiScoreService aiScoreService;
    private final MetricasIaService metricasIaService;

    public AiController(AiScoreService aiScoreService, MetricasIaService metricasIaService) {
        this.aiScoreService = aiScoreService;
        this.metricasIaService = metricasIaService;
    }

    @PostMapping("/score-ideas")
    @PreAuthorize("hasRole('GESTOR')")
    public ScoreIdeasResponse scoreIdeas(@Valid @RequestBody ScoreIdeasRequest request) {
        return aiScoreService.score(request, SecurityUtils.currentUser().getUserId());
    }

    @PostMapping("/ideias/{id}/rascunho-feedback")
    @PreAuthorize("hasRole('GESTOR')")
    public RascunhoFeedbackResponse rascunhoFeedback(
            @PathVariable String id,
            @Valid @RequestBody(required = false) RascunhoFeedbackRequest request) {
        return aiScoreService.rascunhoFeedback(id, request, SecurityUtils.currentUser().getUserId());
    }

    @GetMapping("/dashboard-narrative")
    @PreAuthorize("hasRole('LIDER')")
    public DashboardNarrativeResponse dashboardNarrative(
            @RequestParam(required = false) String orientacaoId) {
        return aiScoreService.narrativaDashboard(orientacaoId);
    }

    @GetMapping("/metricas")
    @PreAuthorize("hasRole('LIDER')")
    public MetricasIaResponse metricas(@RequestParam(required = false) String orientacaoId) {
        return metricasIaService.metricas(orientacaoId);
    }
}
