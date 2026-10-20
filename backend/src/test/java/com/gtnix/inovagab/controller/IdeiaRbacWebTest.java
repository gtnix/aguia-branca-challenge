package com.gtnix.inovagab.controller;

import com.gtnix.inovagab.config.SecurityConfig;
import com.gtnix.inovagab.domain.PerfilUsuario;
import com.gtnix.inovagab.domain.StatusIdeia;
import com.gtnix.inovagab.dto.ideia.EstimativaRequest;
import com.gtnix.inovagab.dto.ideia.IdeiaRequest;
import com.gtnix.inovagab.dto.ideia.IdeiaResponse;
import com.gtnix.inovagab.security.CustomUserDetailsService;
import com.gtnix.inovagab.security.JsonAuthHandlers;
import com.gtnix.inovagab.security.JwtAuthenticationFilter;
import com.gtnix.inovagab.security.JwtProperties;
import com.gtnix.inovagab.security.JwtService;
import com.gtnix.inovagab.security.WithMockInovagabUser;
import com.gtnix.inovagab.service.IdeiaService;
import com.gtnix.inovagab.web.ApiException;
import com.gtnix.inovagab.web.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = IdeiaController.class)
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
class IdeiaRbacWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IdeiaService ideiaService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void semTokenRetorna401() throws Exception {
        mockMvc.perform(get("/api/v1/ideias"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    @WithMockInovagabUser(perfil = PerfilUsuario.OPERADOR, userId = "user-operador", email = "pedro.santos@aguiabranca.com.br")
    void operadorListaComSucesso() throws Exception {
        when(ideiaService.listar(any(), any(), any(), any())).thenReturn(List.of(sample()));

        mockMvc.perform(get("/api/v1/ideias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].orientacaoId").value("ori-1"))
                .andExpect(jsonPath("$[0].autorUserId").value("user-operador"))
                .andExpect(jsonPath("$[0].autorNome").value("Pedro Santos"));
    }

    @Test
    @WithMockInovagabUser(perfil = PerfilUsuario.GESTOR, userId = "user-gestor", email = "ana.oliveira@aguiabranca.com.br")
    void gestorNaoCria() throws Exception {
        mockMvc.perform(post("/api/v1/ideias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"titulo":"Nova","descricao":"d","tipo":"IDEIA","area":"OPERACOES","orientacaoId":"ori-1"}
                                """))
                .andExpect(status().isForbidden());
        verify(ideiaService, never()).criar(any(), any());
    }

    @Test
    @WithMockInovagabUser(perfil = PerfilUsuario.LIDER)
    void liderNaoCria() throws Exception {
        mockMvc.perform(post("/api/v1/ideias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"titulo":"Nova","descricao":"d","tipo":"IDEIA","area":"OPERACOES","orientacaoId":"ori-1"}
                                """))
                .andExpect(status().isForbidden());
        verify(ideiaService, never()).criar(any(), any());
    }

    @Test
    @WithMockInovagabUser(perfil = PerfilUsuario.OPERADOR, userId = "user-operador", email = "pedro.santos@aguiabranca.com.br")
    void operadorCriaComContrato() throws Exception {
        when(ideiaService.criar(any(), any())).thenReturn(sample());

        mockMvc.perform(post("/api/v1/ideias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"titulo":"Nova","descricao":"d","tipo":"IDEIA","area":"OPERACOES","orientacaoId":"ori-1"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("ideia-1"))
                .andExpect(jsonPath("$.orientacaoId").value("ori-1"))
                .andExpect(jsonPath("$.status").value("PENDENTE"));
    }

    @Test
    @WithMockInovagabUser(perfil = PerfilUsuario.OPERADOR, userId = "user-operador", email = "pedro.santos@aguiabranca.com.br")
    void criarSemOrientacaoRetorna400() throws Exception {
        mockMvc.perform(post("/api/v1/ideias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"titulo":"Nova","descricao":"d","tipo":"IDEIA","area":"OPERACOES","orientacaoId":""}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
        verify(ideiaService, never()).criar(any(), any());
    }

    @Test
    @WithMockInovagabUser(perfil = PerfilUsuario.OPERADOR, userId = "user-operador", email = "pedro.santos@aguiabranca.com.br")
    void frequenciaInvalidaRetorna400() throws Exception {
        mockMvc.perform(post("/api/v1/ideias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"titulo":"Nova","descricao":"d","tipo":"IDEIA","area":"OPERACOES","orientacaoId":"ori-1","frequencia":"SEMPRE"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
        verify(ideiaService, never()).criar(any(), any());
    }

    @Test
    @WithMockInovagabUser(perfil = PerfilUsuario.OPERADOR, userId = "user-operador", email = "pedro.santos@aguiabranca.com.br")
    void impactoEsforcoEnviadosPeloOperadorNaoChegamAoCadastro() throws Exception {
        when(ideiaService.criar(any(), any())).thenReturn(sample());

        mockMvc.perform(post("/api/v1/ideias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"titulo":"Nova","descricao":"d","tipo":"IDEIA","area":"OPERACOES","orientacaoId":"ori-1",
                                 "impacto":5,"esforco":1,"problema":"Pneus furam","frequencia":"DIARIA"}
                                """))
                .andExpect(status().isOk());

        ArgumentCaptor<IdeiaRequest> captor = ArgumentCaptor.forClass(IdeiaRequest.class);
        verify(ideiaService).criar(captor.capture(), any());
        assertEquals("Pneus furam", captor.getValue().problema());
        assertEquals("DIARIA", captor.getValue().frequencia());
        assertFalse(java.util.Arrays.stream(IdeiaRequest.class.getRecordComponents())
                .anyMatch(c -> c.getName().equals("impacto") || c.getName().equals("esforco")));
    }

    @Test
    @WithMockInovagabUser(perfil = PerfilUsuario.OPERADOR, userId = "user-operador", email = "pedro.santos@aguiabranca.com.br")
    void operadorNaoDefineEstimativa() throws Exception {
        mockMvc.perform(patch("/api/v1/ideias/ideia-1/estimativa")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"impacto":5,"esforco":1}
                                """))
                .andExpect(status().isForbidden());
        verify(ideiaService, never()).salvarEstimativa(any(), any(), any());
    }

    @Test
    @WithMockInovagabUser(perfil = PerfilUsuario.LIDER)
    void liderNaoDefineEstimativa() throws Exception {
        mockMvc.perform(patch("/api/v1/ideias/ideia-1/estimativa")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"impacto":5,"esforco":1}
                                """))
                .andExpect(status().isForbidden());
        verify(ideiaService, never()).salvarEstimativa(any(), any(), any());
    }

    @Test
    @WithMockInovagabUser(perfil = PerfilUsuario.GESTOR, userId = "user-gestor", email = "ana.oliveira@aguiabranca.com.br")
    void gestorDefineEstimativa() throws Exception {
        when(ideiaService.salvarEstimativa(eq("ideia-1"), any(), any())).thenReturn(sample());

        mockMvc.perform(patch("/api/v1/ideias/ideia-1/estimativa")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"impacto":4,"esforco":2,"motivoAjuste":"Exige integração"}
                                """))
                .andExpect(status().isOk());

        ArgumentCaptor<EstimativaRequest> captor = ArgumentCaptor.forClass(EstimativaRequest.class);
        verify(ideiaService).salvarEstimativa(eq("ideia-1"), captor.capture(), any());
        assertEquals(4, captor.getValue().impacto());
        assertEquals("Exige integração", captor.getValue().motivoAjuste());
    }

    @Test
    @WithMockInovagabUser(perfil = PerfilUsuario.GESTOR, userId = "user-gestor", email = "ana.oliveira@aguiabranca.com.br")
    void estimativaForaDaFaixaRetorna400() throws Exception {
        mockMvc.perform(patch("/api/v1/ideias/ideia-1/estimativa")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"impacto":9,"esforco":2}
                                """))
                .andExpect(status().isBadRequest());
        verify(ideiaService, never()).salvarEstimativa(any(), any(), any());
    }

    @Test
    @WithMockInovagabUser(perfil = PerfilUsuario.GESTOR, userId = "user-gestor", email = "ana.oliveira@aguiabranca.com.br")
    void obterInexistenteRetorna404() throws Exception {
        when(ideiaService.obter(any(), any()))
                .thenThrow(new ApiException(HttpStatus.NOT_FOUND, "Ideia não encontrada"));

        mockMvc.perform(get("/api/v1/ideias/faltando"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Ideia não encontrada"));
    }

    @Test
    @WithMockInovagabUser(perfil = PerfilUsuario.GESTOR, userId = "user-gestor", email = "ana.oliveira@aguiabranca.com.br")
    void statusInvalidoRetorna400() throws Exception {
        mockMvc.perform(get("/api/v1/ideias").param("status", "FOO"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
        verify(ideiaService, never()).listar(any(), any(), any(), any());
    }

    @Test
    @WithMockInovagabUser(perfil = PerfilUsuario.OPERADOR, userId = "user-operador", email = "pedro.santos@aguiabranca.com.br")
    void operadorExcluiRetorna204() throws Exception {
        mockMvc.perform(delete("/api/v1/ideias/ideia-1"))
                .andExpect(status().isNoContent());
        verify(ideiaService).excluir(any(), any());
    }

    @Test
    @WithMockInovagabUser(perfil = PerfilUsuario.GESTOR, userId = "user-gestor", email = "ana.oliveira@aguiabranca.com.br")
    void gestorAvaliaComSucesso() throws Exception {
        when(ideiaService.avaliar(any(), any(), any())).thenReturn(sample());

        mockMvc.perform(patch("/api/v1/ideias/ideia-1/avaliacao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"status":"APROVADA"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("ideia-1"));
        verify(ideiaService).avaliar(any(), any(), any());
    }

    @Test
    @WithMockInovagabUser(perfil = PerfilUsuario.LIDER)
    void liderNaoAvalia() throws Exception {
        mockMvc.perform(patch("/api/v1/ideias/ideia-1/avaliacao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"status":"APROVADA"}
                                """))
                .andExpect(status().isForbidden());
        verify(ideiaService, never()).avaliar(any(), any(), any());
    }

    @Test
    @WithMockInovagabUser(perfil = PerfilUsuario.OPERADOR, userId = "user-operador", email = "pedro.santos@aguiabranca.com.br")
    void operadorNaoAvalia() throws Exception {
        mockMvc.perform(patch("/api/v1/ideias/ideia-1/avaliacao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"status":"APROVADA"}
                                """))
                .andExpect(status().isForbidden());
        verify(ideiaService, never()).avaliar(any(), any(), any());
    }

    @Test
    @WithMockInovagabUser(perfil = PerfilUsuario.OPERADOR, userId = "user-operador", email = "pedro.santos@aguiabranca.com.br")
    void operadorNaoPrioriza() throws Exception {
        mockMvc.perform(patch("/api/v1/ideias/ideia-1/prioridade")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"prioridade":80}
                                """))
                .andExpect(status().isForbidden());
        verify(ideiaService, never()).priorizar(any(), any());
    }

    @Test
    @WithMockInovagabUser(perfil = PerfilUsuario.GESTOR, userId = "user-gestor", email = "ana.oliveira@aguiabranca.com.br")
    void gestorPriorizaComSucesso() throws Exception {
        when(ideiaService.priorizar(any(), any())).thenReturn(sample());

        mockMvc.perform(patch("/api/v1/ideias/ideia-1/prioridade")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"prioridade":80}
                                """))
                .andExpect(status().isOk());
        verify(ideiaService).priorizar(any(), any());
    }

    @Test
    @WithMockInovagabUser(perfil = PerfilUsuario.LIDER)
    void liderNaoPrioriza() throws Exception {
        mockMvc.perform(patch("/api/v1/ideias/ideia-1/prioridade")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"prioridade":80}
                                """))
                .andExpect(status().isForbidden());
        verify(ideiaService, never()).priorizar(any(), any());
    }

    @Test
    @WithMockInovagabUser(perfil = PerfilUsuario.OPERADOR, userId = "user-operador", email = "pedro.santos@aguiabranca.com.br")
    void operadorNaoApoiaMasMarcaFeedbackUtil() throws Exception {
        when(ideiaService.marcarFeedbackUtil(any(), anyBoolean(), any())).thenReturn(sample());

        mockMvc.perform(post("/api/v1/ideias/ideia-1/apoio")).andExpect(status().isForbidden());
        mockMvc.perform(post("/api/v1/ideias/ideia-1/feedback-util")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"util\":true}"))
                .andExpect(status().isOk());
        verify(ideiaService, never()).apoiar(any(), any());
    }

    @Test
    @WithMockInovagabUser(perfil = PerfilUsuario.GESTOR, userId = "user-gestor", email = "ana.oliveira@aguiabranca.com.br")
    void gestorApoiaMasNaoMarcaFeedbackUtil() throws Exception {
        when(ideiaService.apoiar(any(), any())).thenReturn(sample());

        mockMvc.perform(post("/api/v1/ideias/ideia-1/apoio")).andExpect(status().isOk());
        mockMvc.perform(post("/api/v1/ideias/ideia-1/feedback-util")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"util\":true}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockInovagabUser(perfil = PerfilUsuario.OPERADOR, userId = "user-operador", email = "pedro.santos@aguiabranca.com.br")
    void feedbackUtilSemCorpoRetorna400() throws Exception {
        mockMvc.perform(post("/api/v1/ideias/ideia-1/feedback-util")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    private static IdeiaResponse sample() {
        Instant now = Instant.parse("2026-01-01T00:00:00Z");
        return new IdeiaResponse(
                "ideia-1",
                "user-operador",
                "Pedro Santos",
                "Título",
                "Descrição",
                "IDEIA",
                "OPERACOES",
                "ori-1",
                StatusIdeia.PENDENTE,
                null,
                3,
                2,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                0,
                now,
                now,
                null,
                null,
                null,
                null,
                null
        );
    }
}
