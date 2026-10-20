package com.gtnix.inovagab.controller;

import com.gtnix.inovagab.config.SecurityConfig;
import com.gtnix.inovagab.domain.PerfilUsuario;
import com.gtnix.inovagab.dto.projeto.ProjetoResponse;
import com.gtnix.inovagab.security.CustomUserDetailsService;
import com.gtnix.inovagab.security.JsonAuthHandlers;
import com.gtnix.inovagab.security.JwtAuthenticationFilter;
import com.gtnix.inovagab.security.JwtProperties;
import com.gtnix.inovagab.security.JwtService;
import com.gtnix.inovagab.security.WithMockInovagabUser;
import com.gtnix.inovagab.service.ProjetoService;
import com.gtnix.inovagab.web.ApiException;
import com.gtnix.inovagab.web.GlobalExceptionHandler;
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

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ProjetoController.class)
@EnableConfigurationProperties(JwtProperties.class)
@Import({
        SecurityConfig.class,
        JwtAuthenticationFilter.class,
        JwtService.class,
        JsonAuthHandlers.class,
        GlobalExceptionHandler.class
})
@TestPropertySource(properties = {
        "inovagab.jwt.secret=test-secret-change-me-min-32-characters!",
        "inovagab.jwt.expiration-seconds=3600"
})
class ProjetoRbacWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProjetoService projetoService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void semTokenRetorna401() throws Exception {
        mockMvc.perform(get("/api/v1/projetos"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    @WithMockInovagabUser(perfil = PerfilUsuario.OPERADOR, userId = "user-operador", email = "pedro.santos@aguiabranca.com.br")
    void operadorNaoLista() throws Exception {
        mockMvc.perform(get("/api/v1/projetos"))
                .andExpect(status().isForbidden());
        verify(projetoService, never()).listar();
    }

    @Test
    @WithMockInovagabUser(perfil = PerfilUsuario.LIDER)
    void liderListaComSucesso() throws Exception {
        when(projetoService.listar()).thenReturn(List.of(sample()));

        mockMvc.perform(get("/api/v1/projetos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("proj-1"))
                .andExpect(jsonPath("$[0].ideiaId").value("ideia-1"))
                .andExpect(jsonPath("$[0].roi").value(20.00));
    }

    @Test
    @WithMockInovagabUser(perfil = PerfilUsuario.LIDER)
    void liderNaoCria() throws Exception {
        mockMvc.perform(post("/api/v1/projetos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload()))
                .andExpect(status().isForbidden());
        verify(projetoService, never()).criar(any(), any());
    }

    @Test
    @WithMockInovagabUser(perfil = PerfilUsuario.LIDER)
    void liderNaoAtualizaProgresso() throws Exception {
        mockMvc.perform(patch("/api/v1/projetos/proj-1/progresso")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"status":"EM_ANDAMENTO","progressoPercentual":40}
                                """))
                .andExpect(status().isForbidden());
        verify(projetoService, never()).atualizarProgresso(any(), any());
    }

    @Test
    @WithMockInovagabUser(perfil = PerfilUsuario.OPERADOR, userId = "user-operador", email = "pedro.santos@aguiabranca.com.br")
    void operadorNaoCria() throws Exception {
        mockMvc.perform(post("/api/v1/projetos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload()))
                .andExpect(status().isForbidden());
        verify(projetoService, never()).criar(any(), any());
    }

    @Test
    @WithMockInovagabUser(perfil = PerfilUsuario.GESTOR, userId = "user-gestor", email = "ana.oliveira@aguiabranca.com.br")
    void gestorCriaComContrato() throws Exception {
        when(projetoService.criar(any(), any())).thenReturn(sample());

        mockMvc.perform(post("/api/v1/projetos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("proj-1"))
                .andExpect(jsonPath("$.orientacaoId").value("ori-1"))
                .andExpect(jsonPath("$.status").value("PLANEJADO"));
    }

    @Test
    @WithMockInovagabUser(perfil = PerfilUsuario.GESTOR, userId = "user-gestor", email = "ana.oliveira@aguiabranca.com.br")
    void criarSemOrientacaoRetorna400() throws Exception {
        mockMvc.perform(post("/api/v1/projetos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"titulo":"Novo","orientacaoId":""}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
        verify(projetoService, never()).criar(any(), any());
    }

    @Test
    @WithMockInovagabUser(perfil = PerfilUsuario.GESTOR, userId = "user-gestor", email = "ana.oliveira@aguiabranca.com.br")
    void progressoForaDaFaixaRetorna400() throws Exception {
        mockMvc.perform(patch("/api/v1/projetos/proj-1/progresso")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"progressoPercentual":140}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
        verify(projetoService, never()).atualizarProgresso(any(), any());
    }

    @Test
    @WithMockInovagabUser(perfil = PerfilUsuario.GESTOR, userId = "user-gestor", email = "ana.oliveira@aguiabranca.com.br")
    void obterInexistenteRetorna404() throws Exception {
        when(projetoService.obter("faltando"))
                .thenThrow(new ApiException(HttpStatus.NOT_FOUND, "Projeto não encontrado"));

        mockMvc.perform(get("/api/v1/projetos/faltando"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Projeto não encontrado"));
    }

    @Test
    @WithMockInovagabUser(perfil = PerfilUsuario.GESTOR, userId = "user-gestor", email = "ana.oliveira@aguiabranca.com.br")
    void gestorExcluiRetorna204() throws Exception {
        mockMvc.perform(delete("/api/v1/projetos/proj-1"))
                .andExpect(status().isNoContent());
        verify(projetoService).excluir("proj-1");
    }

    @Test
    @WithMockInovagabUser(perfil = PerfilUsuario.GESTOR, userId = "user-gestor", email = "ana.oliveira@aguiabranca.com.br")
    void gestorAtualiza() throws Exception {
        when(projetoService.atualizar(any(), any())).thenReturn(sample());

        mockMvc.perform(put("/api/v1/projetos/proj-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("proj-1"));
        verify(projetoService).atualizar(any(), any());
    }

    private static String payload() {
        return """
                {
                  "titulo":"Telemetria da frota",
                  "objetivo":"Reduzir combustível",
                  "descricao":"Descrição",
                  "area":"OPERACOES",
                  "orientacaoId":"ori-1",
                  "ideiaId":"ideia-1",
                  "investimento":100000,
                  "retorno":10000,
                  "prazo":"2026-12-31"
                }
                """;
    }

    private static ProjetoResponse sample() {
        Instant now = Instant.parse("2026-01-01T00:00:00Z");
        return new ProjetoResponse(
                "proj-1",
                "Telemetria da frota",
                "Reduzir combustível",
                "Descrição",
                "OPERACOES",
                "ori-1",
                "ideia-1",
                "PLANEJADO",
                "PLANEJADO",
                BigDecimal.valueOf(100_000),
                BigDecimal.valueOf(10_000),
                new BigDecimal("20.00"),
                LocalDate.parse("2026-12-31"),
                0,
                null,
                "user-gestor",
                now,
                now
        );
    }
}
