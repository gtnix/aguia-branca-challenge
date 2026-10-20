package com.gtnix.inovagab.controller;

import com.gtnix.inovagab.dto.gamificacao.RecompensaDtos.RecompensaRequest;
import com.gtnix.inovagab.dto.gamificacao.RecompensaDtos.RecompensaResponse;
import com.gtnix.inovagab.dto.gamificacao.RecompensaDtos.ResgateResponse;
import com.gtnix.inovagab.security.SecurityUtils;
import com.gtnix.inovagab.service.RecompensaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
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
@RequestMapping("/api/v1/recompensas")
public class RecompensaController {

    private final RecompensaService recompensaService;

    public RecompensaController(RecompensaService recompensaService) {
        this.recompensaService = recompensaService;
    }

    @GetMapping
    public List<RecompensaResponse> listar(@RequestParam(defaultValue = "false") boolean incluirInativas) {
        return recompensaService.listar(SecurityUtils.currentUser(), incluirInativas);
    }

    @PostMapping
    @PreAuthorize("hasRole('LIDER')")
    @ResponseStatus(HttpStatus.CREATED)
    public RecompensaResponse criar(@Valid @RequestBody RecompensaRequest request) {
        return recompensaService.criar(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('LIDER')")
    public RecompensaResponse atualizar(@PathVariable String id, @Valid @RequestBody RecompensaRequest request) {
        return recompensaService.atualizar(id, request);
    }

    @PostMapping("/{id}/resgatar")
    @PreAuthorize("hasAnyRole('OPERADOR','GESTOR')")
    @ResponseStatus(HttpStatus.CREATED)
    public ResgateResponse resgatar(@PathVariable String id) {
        return recompensaService.solicitar(id, SecurityUtils.currentUser());
    }
}
