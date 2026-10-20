package com.gtnix.inovagab.controller;

import com.gtnix.inovagab.domain.StatusResgate;
import com.gtnix.inovagab.dto.gamificacao.RecompensaDtos.DecisaoResgateRequest;
import com.gtnix.inovagab.dto.gamificacao.RecompensaDtos.ResgateResponse;
import com.gtnix.inovagab.security.SecurityUtils;
import com.gtnix.inovagab.service.RecompensaService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/resgates")
public class ResgateController {

    private final RecompensaService recompensaService;

    public ResgateController(RecompensaService recompensaService) {
        this.recompensaService = recompensaService;
    }

    @GetMapping("/meus")
    public List<ResgateResponse> meus() {
        return recompensaService.meus(SecurityUtils.currentUser());
    }

    @GetMapping
    @PreAuthorize("hasRole('LIDER')")
    public List<ResgateResponse> listar(@RequestParam(required = false) StatusResgate status) {
        return recompensaService.listar(status);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('LIDER')")
    public ResgateResponse decidir(@PathVariable String id, @Valid @RequestBody DecisaoResgateRequest request) {
        return recompensaService.decidir(id, request, SecurityUtils.currentUser());
    }
}
