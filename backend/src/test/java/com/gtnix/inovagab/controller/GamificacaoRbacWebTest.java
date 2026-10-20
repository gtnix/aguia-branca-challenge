package com.gtnix.inovagab.controller;

import com.gtnix.inovagab.config.SecurityConfig;
import com.gtnix.inovagab.domain.PerfilUsuario;
import com.gtnix.inovagab.domain.StatusResgate;
import com.gtnix.inovagab.dto.gamificacao.RecompensaDtos.RecompensaResponse;
import com.gtnix.inovagab.dto.gamificacao.RecompensaDtos.ResgateResponse;
import com.gtnix.inovagab.security.CustomUserDetailsService;
import com.gtnix.inovagab.security.JsonAuthHandlers;
import com.gtnix.inovagab.security.JwtAuthenticationFilter;
import com.gtnix.inovagab.security.JwtProperties;
import com.gtnix.inovagab.security.JwtService;
import com.gtnix.inovagab.security.WithMockInovagabUser;
import com.gtnix.inovagab.service.GamificacaoPainelService;
import com.gtnix.inovagab.service.RecompensaService;
import com.gtnix.inovagab.web.ApiException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {GamificacaoController.class, RecompensaController.class, ResgateController.class})
@EnableConfigurationProperties(JwtProperties.class)
@Import({
        SecurityConfig.class,
        JwtAuthenticationFilter.class,
        JwtService.class,
        JsonAuthHandlers.class
})
@TestPropertySource(properties = {
        "inovagab.jwt.secret=test-secret-change-me-min-32-characters!",
        "inovagab.jwt.expiration-seconds=3600"
})
class GamificacaoRbacWebTest {

    private static final String RECOMPENSA_JSON = """
            {"titulo":"Vale R$ 100","categoria":"VALE","custoAsas":60,"estoque":10}
            """;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GamificacaoPainelService painelService;
    @MockBean
    private RecompensaService recompensaService;
    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void semTokenRetorna401() throws Exception {
        mockMvc.perform(get("/api/v1/gamificacao/me")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/v1/recompensas")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockInovagabUser(perfil = PerfilUsuario.OPERADOR, userId = "user-operador")
    void operadorVeLojaEResgata() throws Exception {
        when(recompensaService.listar(any(), eq(false))).thenReturn(List.of(recompensa()));
        when(recompensaService.solicitar(eq("rec-vale"), any())).thenReturn(resgate(StatusResgate.APROVADO));

        mockMvc.perform(get("/api/v1/recompensas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].podeResgatar").value(true));
        mockMvc.perform(post("/api/v1/recompensas/rec-vale/resgatar"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("APROVADO"));
    }

    @Test
    @WithMockInovagabUser(perfil = PerfilUsuario.OPERADOR, userId = "user-operador")
    void saldoInsuficienteViraConflito() throws Exception {
        when(recompensaService.solicitar(eq("rec-vale"), any()))
                .thenThrow(new ApiException(HttpStatus.CONFLICT, "Faltam 10 Asas"));

        mockMvc.perform(post("/api/v1/recompensas/rec-vale/resgatar"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Faltam 10 Asas"));
    }

    @Test
    @WithMockInovagabUser(perfil = PerfilUsuario.OPERADOR, userId = "user-operador")
    void operadorNaoGerenciaCatalogoNemDecideResgates() throws Exception {
        mockMvc.perform(post("/api/v1/recompensas").contentType(MediaType.APPLICATION_JSON).content(RECOMPENSA_JSON))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/api/v1/resgates")).andExpect(status().isForbidden());
        mockMvc.perform(put("/api/v1/resgates/r-1/status").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"ENTREGUE\"}"))
                .andExpect(status().isForbidden());
        verify(recompensaService, never()).criar(any());
        verify(recompensaService, never()).decidir(any(), any(), any());
    }

    @Test
    @WithMockInovagabUser(perfil = PerfilUsuario.GESTOR, userId = "user-gestor")
    void gestorTambemResgataMasNaoAprova() throws Exception {
        when(recompensaService.solicitar(eq("rec-vale"), any())).thenReturn(resgate(StatusResgate.APROVADO));

        mockMvc.perform(post("/api/v1/recompensas/rec-vale/resgatar")).andExpect(status().isCreated());
        mockMvc.perform(put("/api/v1/resgates/r-1/status").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"ENTREGUE\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockInovagabUser(perfil = PerfilUsuario.LIDER, userId = "user-lider")
    void liderGerenciaCatalogoEDecideMasNaoResgata() throws Exception {
        when(recompensaService.criar(any())).thenReturn(recompensa());
        when(recompensaService.listar(StatusResgate.APROVADO)).thenReturn(List.of(resgate(StatusResgate.APROVADO)));
        when(recompensaService.decidir(eq("r-1"), any(), any())).thenReturn(resgate(StatusResgate.ENTREGUE));

        mockMvc.perform(post("/api/v1/recompensas").contentType(MediaType.APPLICATION_JSON).content(RECOMPENSA_JSON))
                .andExpect(status().isCreated());
        mockMvc.perform(get("/api/v1/resgates").param("status", "APROVADO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userNome").value("Pedro Santos"));
        mockMvc.perform(put("/api/v1/resgates/r-1/status").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"ENTREGUE\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ENTREGUE"));
        mockMvc.perform(post("/api/v1/recompensas/rec-vale/resgatar")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockInovagabUser(perfil = PerfilUsuario.LIDER, userId = "user-lider")
    void recompensaInvalidaRetorna400() throws Exception {
        mockMvc.perform(post("/api/v1/recompensas").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"titulo\":\"\",\"categoria\":\"VALE\",\"custoAsas\":0,\"estoque\":1}"))
                .andExpect(status().isBadRequest());
        verify(recompensaService, never()).criar(any());
    }

    @Test
    @WithMockInovagabUser(perfil = PerfilUsuario.OPERADOR, userId = "user-operador")
    void rankingRepassaFiltrosEBloqueioDoServico() throws Exception {
        when(painelService.ranking(any(), eq(PerfilUsuario.GESTOR), eq("DIVISAO"), any(), eq(0), eq(50)))
                .thenThrow(new ApiException(HttpStatus.FORBIDDEN, "Ranking não disponível para o seu perfil"));

        mockMvc.perform(get("/api/v1/gamificacao/ranking").param("perfil", "GESTOR"))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/api/v1/gamificacao/indicacoes-inovagab"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockInovagabUser(perfil = PerfilUsuario.OPERADOR, userId = "user-operador")
    void marcarNovidadesVistasRetorna204() throws Exception {
        mockMvc.perform(post("/api/v1/gamificacao/novidades/vistas")).andExpect(status().isNoContent());
        verify(painelService).marcarNovidadesVistas(any());
    }

    private static RecompensaResponse recompensa() {
        return new RecompensaResponse("rec-vale", "Vale R$ 100", "d", "VALE", "Parceiro", 60, 10,
                "INICIANTE", "Iniciante", true, true, null);
    }

    private static ResgateResponse resgate(StatusResgate status) {
        return new ResgateResponse("r-1", "rec-vale", "Vale R$ 100", "user-operador", "Pedro Santos", 60, status,
                null, null, Instant.parse("2026-10-19T12:00:00Z"), null, null);
    }
}
