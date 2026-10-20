package com.gtnix.inovagab.controller;

import com.gtnix.inovagab.domain.PerfilUsuario;
import com.gtnix.inovagab.dto.orientacao.EncerramentoCampanhaResponse;
import com.gtnix.inovagab.dto.orientacao.OrientacaoRequest;
import com.gtnix.inovagab.dto.orientacao.OrientacaoResponse;
import com.gtnix.inovagab.security.SecurityUtils;
import com.gtnix.inovagab.security.UsuarioPrincipal;
import com.gtnix.inovagab.service.OrientacaoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
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
@RequestMapping("/api/v1/orientacoes")
public class OrientacaoController {

    private final OrientacaoService orientacaoService;

    public OrientacaoController(OrientacaoService orientacaoService) {
        this.orientacaoService = orientacaoService;
    }

    @GetMapping
    public List<OrientacaoResponse> listar(
            @RequestParam(defaultValue = "true") boolean apenasAtivas
    ) {
        UsuarioPrincipal user = SecurityUtils.currentUser();
        boolean soAtivas = apenasAtivas || user.getPerfil() != PerfilUsuario.LIDER;
        return orientacaoService.listar(soAtivas);
    }

    @GetMapping("/{id}")
    public OrientacaoResponse obter(@PathVariable String id) {
        return orientacaoService.obter(id, SecurityUtils.currentUser());
    }

    @PostMapping
    @PreAuthorize("hasRole('LIDER')")
    public OrientacaoResponse criar(@Valid @RequestBody OrientacaoRequest request) {
        return orientacaoService.criar(request, SecurityUtils.currentUser());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('LIDER')")
    public OrientacaoResponse atualizar(@PathVariable String id, @Valid @RequestBody OrientacaoRequest request) {
        return orientacaoService.atualizar(id, request);
    }

    @PutMapping("/{id}/ativo")
    @PreAuthorize("hasRole('LIDER')")
    public OrientacaoResponse definirAtivo(@PathVariable String id, @RequestParam boolean ativo) {
        return orientacaoService.definirAtivo(id, ativo);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('LIDER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable String id) {
        orientacaoService.excluir(id);
    }

    @PostMapping("/{id}/campanha/encerrar")
    @PreAuthorize("hasRole('LIDER')")
    public EncerramentoCampanhaResponse encerrarCampanha(@PathVariable String id) {
        return orientacaoService.encerrarCampanha(id);
    }
}
