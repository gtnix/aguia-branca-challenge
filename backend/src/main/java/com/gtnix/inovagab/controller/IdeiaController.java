package com.gtnix.inovagab.controller;

import com.gtnix.inovagab.domain.StatusIdeia;
import com.gtnix.inovagab.dto.ideia.AvaliacaoRequest;
import com.gtnix.inovagab.dto.ideia.EstimativaRequest;
import com.gtnix.inovagab.dto.ideia.FeedbackUtilRequest;
import com.gtnix.inovagab.dto.ideia.IdeiaRequest;
import com.gtnix.inovagab.dto.ideia.IdeiaResponse;
import com.gtnix.inovagab.dto.ideia.PrioridadeRequest;
import com.gtnix.inovagab.security.SecurityUtils;
import com.gtnix.inovagab.service.IdeiaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ideias")
public class IdeiaController {

    private final IdeiaService ideiaService;

    public IdeiaController(IdeiaService ideiaService) {
        this.ideiaService = ideiaService;
    }

    @GetMapping
    public List<IdeiaResponse> listar(
            @RequestParam(required = false) StatusIdeia status,
            @RequestParam(required = false) String area,
            @RequestParam(required = false) String orientacaoId
    ) {
        return ideiaService.listar(SecurityUtils.currentUser(), status, area, orientacaoId);
    }

    @GetMapping("/{id}")
    public IdeiaResponse obter(@PathVariable String id) {
        return ideiaService.obter(id, SecurityUtils.currentUser());
    }

    @PostMapping
    @PreAuthorize("hasRole('OPERADOR')")
    public IdeiaResponse criar(@Valid @RequestBody IdeiaRequest request) {
        return ideiaService.criar(request, SecurityUtils.currentUser());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('OPERADOR')")
    public IdeiaResponse atualizar(@PathVariable String id, @Valid @RequestBody IdeiaRequest request) {
        return ideiaService.atualizar(id, request, SecurityUtils.currentUser());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('OPERADOR')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable String id) {
        ideiaService.excluir(id, SecurityUtils.currentUser());
    }

    @PatchMapping("/{id}/prioridade")
    @PreAuthorize("hasRole('GESTOR')")
    public IdeiaResponse priorizar(@PathVariable String id, @Valid @RequestBody PrioridadeRequest request) {
        return ideiaService.priorizar(id, request);
    }

    @PatchMapping("/{id}/estimativa")
    @PreAuthorize("hasRole('GESTOR')")
    public IdeiaResponse salvarEstimativa(@PathVariable String id, @Valid @RequestBody EstimativaRequest request) {
        return ideiaService.salvarEstimativa(id, request, SecurityUtils.currentUser());
    }

    @PatchMapping("/{id}/avaliacao")
    @PreAuthorize("hasRole('GESTOR')")
    public IdeiaResponse avaliar(@PathVariable String id, @Valid @RequestBody AvaliacaoRequest request) {
        return ideiaService.avaliar(id, request, SecurityUtils.currentUser());
    }

    @PostMapping("/{id}/apoio")
    @PreAuthorize("hasAnyRole('GESTOR','LIDER')")
    public IdeiaResponse apoiar(@PathVariable String id) {
        return ideiaService.apoiar(id, SecurityUtils.currentUser());
    }

    @PostMapping("/{id}/feedback-util")
    @PreAuthorize("hasRole('OPERADOR')")
    public IdeiaResponse feedbackUtil(@PathVariable String id, @Valid @RequestBody FeedbackUtilRequest request) {
        return ideiaService.marcarFeedbackUtil(id, request.util(), SecurityUtils.currentUser());
    }
}
