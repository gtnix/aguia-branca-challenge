package com.gtnix.inovagab.controller;

import com.gtnix.inovagab.dto.projeto.ProgressoRequest;
import com.gtnix.inovagab.dto.projeto.ProjetoRequest;
import com.gtnix.inovagab.dto.projeto.ProjetoResponse;
import com.gtnix.inovagab.security.SecurityUtils;
import com.gtnix.inovagab.service.ProjetoService;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/projetos")
public class ProjetoController {

    private final ProjetoService projetoService;

    public ProjetoController(ProjetoService projetoService) {
        this.projetoService = projetoService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('GESTOR','LIDER')")
    public List<ProjetoResponse> listar() {
        return projetoService.listar();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('GESTOR','LIDER')")
    public ProjetoResponse obter(@PathVariable String id) {
        return projetoService.obter(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('GESTOR')")
    public ProjetoResponse criar(@Valid @RequestBody ProjetoRequest request) {
        return projetoService.criar(request, SecurityUtils.currentUser());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('GESTOR')")
    public ProjetoResponse atualizar(@PathVariable String id, @Valid @RequestBody ProjetoRequest request) {
        return projetoService.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('GESTOR')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable String id) {
        projetoService.excluir(id);
    }

    @PatchMapping("/{id}/progresso")
    @PreAuthorize("hasRole('GESTOR')")
    public ProjetoResponse progresso(@PathVariable String id, @Valid @RequestBody ProgressoRequest request) {
        return projetoService.atualizarProgresso(id, request);
    }
}
