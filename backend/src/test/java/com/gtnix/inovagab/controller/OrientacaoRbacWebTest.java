package com.gtnix.inovagab.controller;

import com.gtnix.inovagab.config.SecurityConfig;
import com.gtnix.inovagab.domain.PerfilUsuario;
import com.gtnix.inovagab.dto.orientacao.EncerramentoCampanhaResponse;
import com.gtnix.inovagab.dto.orientacao.OrientacaoResponse;
import com.gtnix.inovagab.security.CustomUserDetailsService;
import com.gtnix.inovagab.security.JsonAuthHandlers;
import com.gtnix.inovagab.security.JwtAuthenticationFilter;
import com.gtnix.inovagab.security.JwtProperties;
import com.gtnix.inovagab.security.JwtService;
import com.gtnix.inovagab.security.WithMockInovagabUser;
import com.gtnix.inovagab.service.OrientacaoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = OrientacaoController.class)
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
class OrientacaoRbacWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrientacaoService orientacaoService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void semTokenRetorna401() throws Exception {
        mockMvc.perform(get("/api/v1/orientacoes"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    @WithMockInovagabUser(perfil = PerfilUsuario.OPERADOR, userId = "user-operador", email = "pedro.santos@aguiabranca.com.br")
    void operadorListaComSucesso() throws Exception {
        when(orientacaoService.listar(true)).thenReturn(List.of(sample()));

        mockMvc.perform(get("/api/v1/orientacoes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].campanha").value("Águia 2026"))
                .andExpect(jsonPath("$[0].categoria").value("EFICIENCIA_OPERACIONAL"));
    }

    @Test
    @WithMockInovagabUser(perfil = PerfilUsuario.OPERADOR, userId = "user-operador", email = "pedro.santos@aguiabranca.com.br")
    void operadorNaoCria() throws Exception {
        mockMvc.perform(post("/api/v1/orientacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"titulo":"Nova","descricao":"d","categoria":"EFICIENCIA_OPERACIONAL","campanha":"Águia 2026"}
                                """))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
        verify(orientacaoService, never()).criar(any(), any());
    }

    @Test
    @WithMockInovagabUser(perfil = PerfilUsuario.GESTOR, userId = "user-gestor", email = "ana.oliveira@aguiabranca.com.br")
    void gestorNaoAtualiza() throws Exception {
        mockMvc.perform(put("/api/v1/orientacoes/ori-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"titulo":"Nova","descricao":"d","categoria":"EFICIENCIA_OPERACIONAL","campanha":"Águia 2026"}
                                """))
                .andExpect(status().isForbidden());
        verify(orientacaoService, never()).atualizar(any(), any());
    }

    @Test
    @WithMockInovagabUser(perfil = PerfilUsuario.LIDER)
    void liderCriaComContratoCompleto() throws Exception {
        when(orientacaoService.criar(any(), any())).thenReturn(sample());

        mockMvc.perform(post("/api/v1/orientacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"titulo":"Nova","descricao":"d","categoria":"EFICIENCIA_OPERACIONAL","campanha":"Águia 2026"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("ori-1"))
                .andExpect(jsonPath("$.criadoPorUserId").value("user-lider"))
                .andExpect(jsonPath("$.ativo").value(true))
                .andExpect(jsonPath("$.divisao").value("PASSAGEIROS"));
    }

    @Test
    @WithMockInovagabUser(perfil = PerfilUsuario.LIDER)
    void criarComDivisaoInvalidaRetorna400() throws Exception {
        mockMvc.perform(post("/api/v1/orientacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"titulo":"Nova","descricao":"d","categoria":"EFICIENCIA_OPERACIONAL","campanha":"Águia 2026","divisao":"VAREJO"}
                                """))
                .andExpect(status().isBadRequest());
        verify(orientacaoService, never()).criar(any(), any());
    }

    @Test
    @WithMockInovagabUser(perfil = PerfilUsuario.LIDER)
    void liderExcluiRetorna204() throws Exception {
        mockMvc.perform(delete("/api/v1/orientacoes/ori-1"))
                .andExpect(status().isNoContent());
        verify(orientacaoService).excluir("ori-1");
    }

    @Test
    @WithMockInovagabUser(perfil = PerfilUsuario.LIDER)
    void criarSemTituloRetorna400() throws Exception {
        mockMvc.perform(post("/api/v1/orientacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"titulo":"","descricao":"d","categoria":"EFICIENCIA_OPERACIONAL","campanha":"Águia 2026"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
        verify(orientacaoService, never()).criar(any(), any());
    }

    @Test
    @WithMockInovagabUser(perfil = PerfilUsuario.OPERADOR, userId = "user-operador", email = "pedro.santos@aguiabranca.com.br")
    void operadorNaoListaInativasMesmoComQuery() throws Exception {
        when(orientacaoService.listar(true)).thenReturn(List.of(sample()));

        mockMvc.perform(get("/api/v1/orientacoes").param("apenasAtivas", "false"))
                .andExpect(status().isOk());

        verify(orientacaoService).listar(true);
        verify(orientacaoService, never()).listar(false);
    }

    @Test
    @WithMockInovagabUser(perfil = PerfilUsuario.LIDER)
    void liderPodeListarInativas() throws Exception {
        when(orientacaoService.listar(false)).thenReturn(List.of(sample()));

        mockMvc.perform(get("/api/v1/orientacoes").param("apenasAtivas", "false"))
                .andExpect(status().isOk());

        verify(orientacaoService).listar(false);
    }

    @Test
    @WithMockInovagabUser(perfil = PerfilUsuario.LIDER)
    void liderAlteraStatusAtivo() throws Exception {
        when(orientacaoService.definirAtivo(eq("ori-1"), eq(false))).thenReturn(
                new OrientacaoResponse("ori-1", "Título", "d", "EFICIENCIA_OPERACIONAL",
                        "Águia 2026", Instant.parse("2026-01-01T00:00:00Z"), Instant.parse("2026-01-01T00:00:00Z"),
                        false, "user-lider", null, null, null, null, false, null)
        );

        mockMvc.perform(put("/api/v1/orientacoes/ori-1/ativo").param("ativo", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ativo").value(false));
    }

    @Test
    @WithMockInovagabUser(perfil = PerfilUsuario.GESTOR, userId = "user-gestor", email = "ana.oliveira@aguiabranca.com.br")
    void gestorNaoEncerraCampanha() throws Exception {
        mockMvc.perform(post("/api/v1/orientacoes/ori-1/campanha/encerrar")).andExpect(status().isForbidden());
        verify(orientacaoService, never()).encerrarCampanha(any());
    }

    @Test
    @WithMockInovagabUser(perfil = PerfilUsuario.LIDER)
    void liderEncerraCampanhaEVeVencedores() throws Exception {
        when(orientacaoService.encerrarCampanha("ori-1")).thenReturn(new EncerramentoCampanhaResponse(
                "ori-1", "Jornada Segura", Instant.parse("2026-11-30T12:00:00Z"),
                List.of(new EncerramentoCampanhaResponse.Vencedor("user-operador", "Pedro Santos", 45, 50))));

        mockMvc.perform(post("/api/v1/orientacoes/ori-1/campanha/encerrar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.vencedores[0].nome").value("Pedro Santos"))
                .andExpect(jsonPath("$.vencedores[0].bonusAsas").value(50));
    }

    private static OrientacaoResponse sample() {
        Instant now = Instant.parse("2026-01-01T00:00:00Z");
        return new OrientacaoResponse(
                "ori-1",
                "Título",
                "Descrição",
                "EFICIENCIA_OPERACIONAL",
                "Águia 2026",
                now,
                now,
                true,
                "user-lider",
                null,
                null,
                null,
                null,
                false,
                "PASSAGEIROS"
        );
    }
}
