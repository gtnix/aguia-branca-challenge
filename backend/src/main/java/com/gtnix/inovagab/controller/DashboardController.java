package com.gtnix.inovagab.controller;

import com.gtnix.inovagab.dto.dashboard.AreaDesempenhoResponse;
import com.gtnix.inovagab.dto.dashboard.DashboardByProjectResponse;
import com.gtnix.inovagab.dto.dashboard.DashboardByStrategyResponse;
import com.gtnix.inovagab.dto.dashboard.DashboardSummaryResponse;
import com.gtnix.inovagab.dto.dashboard.FunnelStepResponse;
import com.gtnix.inovagab.service.DashboardService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/dashboard")
@PreAuthorize("hasRole('LIDER')")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/summary")
    public DashboardSummaryResponse summary(
            @RequestParam(required = false) String orientacaoId) {
        return dashboardService.summary(orientacaoId);
    }

    @GetMapping("/funnel")
    public List<FunnelStepResponse> funnel() {
        return dashboardService.funnel();
    }

    @GetMapping("/by-area")
    public List<AreaDesempenhoResponse> byArea() {
        return dashboardService.byArea();
    }

    @GetMapping("/by-strategy/{orientacaoId}")
    public DashboardByStrategyResponse byStrategy(@PathVariable String orientacaoId) {
        return dashboardService.byStrategy(orientacaoId);
    }

    @GetMapping("/by-project/{projetoId}")
    public DashboardByProjectResponse byProject(@PathVariable String projetoId) {
        return dashboardService.byProject(projetoId);
    }
}
