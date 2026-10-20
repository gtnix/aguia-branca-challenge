package com.gtnix.inovagab.controller;

import com.gtnix.inovagab.domain.PerfilUsuario;
import com.gtnix.inovagab.dto.gamificacao.GamificacaoDtos.EquipeRankingResponse;
import com.gtnix.inovagab.dto.gamificacao.GamificacaoDtos.ExtratoResponse;
import com.gtnix.inovagab.dto.gamificacao.GamificacaoDtos.IndicacoesInovaGabResponse;
import com.gtnix.inovagab.dto.gamificacao.GamificacaoDtos.MeResponse;
import com.gtnix.inovagab.dto.gamificacao.GamificacaoDtos.RankingResponse;
import com.gtnix.inovagab.security.SecurityUtils;
import com.gtnix.inovagab.service.GamificacaoPainelService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/gamificacao")
public class GamificacaoController {

    private final GamificacaoPainelService painelService;

    public GamificacaoController(GamificacaoPainelService painelService) {
        this.painelService = painelService;
    }

    @GetMapping("/me")
    public MeResponse me() {
        return painelService.me(SecurityUtils.currentUser());
    }

    @GetMapping("/extrato")
    public ExtratoResponse extrato() {
        return painelService.extrato(SecurityUtils.currentUser());
    }

    @PostMapping("/novidades/vistas")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void marcarNovidadesVistas() {
        painelService.marcarNovidadesVistas(SecurityUtils.currentUser());
    }

    @GetMapping("/ranking")
    public RankingResponse ranking(
            @RequestParam(required = false) PerfilUsuario perfil,
            @RequestParam(defaultValue = "DIVISAO") String escopo,
            @RequestParam(required = false) String temporada,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "50") int tamanho
    ) {
        return painelService.ranking(SecurityUtils.currentUser(), perfil, escopo, temporada, pagina, tamanho);
    }

    @GetMapping("/indicacoes-inovagab")
    @PreAuthorize("hasRole('LIDER')")
    public IndicacoesInovaGabResponse indicacoes(@RequestParam(required = false) String temporada) {
        return painelService.indicacoes(SecurityUtils.currentUser(), temporada);
    }

    @GetMapping("/ranking/equipes")
    public List<EquipeRankingResponse> rankingEquipes(@RequestParam(defaultValue = "DIVISAO") String escopo) {
        return painelService.rankingEquipes(SecurityUtils.currentUser(), escopo);
    }
}
