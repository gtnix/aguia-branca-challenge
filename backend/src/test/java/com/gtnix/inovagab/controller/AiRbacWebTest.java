package com.gtnix.inovagab.controller;

import com.gtnix.inovagab.config.SecurityConfig;
import com.gtnix.inovagab.domain.PerfilUsuario;
import com.gtnix.inovagab.domain.StatusAnaliseIa;
import com.gtnix.inovagab.dto.ai.DashboardNarrativeResponse;
import com.gtnix.inovagab.dto.ai.IdeaScoreItem;
import com.gtnix.inovagab.dto.ai.MetricasIaResponse;
import com.gtnix.inovagab.dto.ai.RascunhoFeedbackResponse;
import com.gtnix.inovagab.dto.ai.ScoreIdeasRequest;
import com.gtnix.inovagab.dto.ai.ScoreIdeasResponse;
import com.gtnix.inovagab.security.CustomUserDetailsService;
import com.gtnix.inovagab.security.JsonAuthHandlers;
import com.gtnix.inovagab.security.JwtAuthenticationFilter;
import com.gtnix.inovagab.security.JwtProperties;
import com.gtnix.inovagab.security.JwtService;
import com.gtnix.inovagab.security.WithMockInovagabUser;
import com.gtnix.inovagab.service.AiScoreService;
import com.gtnix.inovagab.service.MetricasIaService;
import com.gtnix.inovagab.web.GlobalExceptionHandler;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AiController.class)
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
class AiRbacWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AiScoreService aiScoreService;

    @MockBean
    private MetricasIaService metricasIaService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void semTokenRetorna401() throws Exception {
        mockMvc.perform(post("/api/v1/ai/score-ideas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"orientacaoId":"ori-1","ideiaIds":["ideia-1"]}
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockInovagabUser(perfil = PerfilUsuario.GESTOR, userId = "user-gestor", email = "ana.oliveira@aguiabranca.com.br")
    void gestorPontuaComSucesso() throws Exception {
        when(aiScoreService.score(any(), eq("user-gestor"))).thenReturn(new ScoreIdeasResponse(
                List.of(new IdeaScoreItem("ideia-1", 87, "Alto alinhamento", StatusAnaliseIa.IA, "ALTA")),
                "gemini-3.6-flash",
                false
        ));

        mockMvc.perform(post("/api/v1/ai/score-ideas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"orientacaoId":"ori-1","ideiaIds":["ideia-1"]}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fallback").value(false))
                .andExpect(jsonPath("$.ranking[0].score").value(87))
                .andExpect(jsonPath("$.ranking[0].faixa").value("ALTA"))
                .andExpect(jsonPath("$.ranking[0].status").value("IA"));
    }

    @Test
    @WithMockInovagabUser(perfil = PerfilUsuario.GESTOR, userId = "user-gestor", email = "ana.oliveira@aguiabranca.com.br")
    void loteAcimaDoLimiteRetorna400() throws Exception {
        String ids = java.util.stream.IntStream.rangeClosed(1, ScoreIdeasRequest.MAX_IDEIAS + 1)
                .mapToObj(i -> "\"ideia-" + i + "\"")
                .collect(java.util.stream.Collectors.joining(","));
        mockMvc.perform(post("/api/v1/ai/score-ideas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"orientacaoId\":\"ori-1\",\"ideiaIds\":[" + ids + "]}"))
                .andExpect(status().isBadRequest());
        verify(aiScoreService, never()).score(any(), any());
    }

    // ---- rascunho de feedback ----

    @Test
    @WithMockInovagabUser(perfil = PerfilUsuario.GESTOR, userId = "user-gestor", email = "ana.oliveira@aguiabranca.com.br")
    void gestorGeraRascunhoDeFeedback() throws Exception {
        when(aiScoreService.rascunhoFeedback(eq("ideia-1"), any(), eq("user-gestor")))
                .thenReturn(new RascunhoFeedbackResponse("Obrigado pela ideia.", "gemini-3.6-flash", false));

        mockMvc.perform(post("/api/v1/ai/ideias/ideia-1/rascunho-feedback")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"observacoes":"orçamento comprometido"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rascunho").value("Obrigado pela ideia."));
    }

    @Test
    @WithMockInovagabUser(perfil = PerfilUsuario.OPERADOR)
    void operadorNaoGeraRascunho() throws Exception {
        mockMvc.perform(post("/api/v1/ai/ideias/ideia-1/rascunho-feedback")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
        verify(aiScoreService, never()).rascunhoFeedback(any(), any(), any());
    }

    // ---- métricas ----

    @Test
    @WithMockInovagabUser(perfil = PerfilUsuario.LIDER)
    void liderObtemMetricas() throws Exception {
        when(metricasIaService.metricas("ori-1")).thenReturn(new MetricasIaResponse(
                3, 2, 1, 50.0, 4, 25.0, 12.5, List.of(), List.of(), List.of()));

        mockMvc.perform(get("/api/v1/ai/metricas").param("orientacaoId", "ori-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.taxaConcordancia").value(50.0))
                .andExpect(jsonPath("$.percentualFallback").value(25.0));
    }

    @Test
    @WithMockInovagabUser(perfil = PerfilUsuario.GESTOR)
    void gestorNaoObtemMetricas() throws Exception {
        mockMvc.perform(get("/api/v1/ai/metricas"))
                .andExpect(status().isForbidden());
        verify(metricasIaService, never()).metricas(any());
    }

    @Test
    @WithMockInovagabUser(perfil = PerfilUsuario.LIDER)
    void liderNaoPontua() throws Exception {
        mockMvc.perform(post("/api/v1/ai/score-ideas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"orientacaoId":"ori-1","ideiaIds":["ideia-1"]}
                                """))
                .andExpect(status().isForbidden());
        verify(aiScoreService, never()).score(any(), any());
    }

    @Test
    @WithMockInovagabUser(perfil = PerfilUsuario.OPERADOR, userId = "user-operador", email = "pedro.santos@aguiabranca.com.br")
    void operadorNaoPontua() throws Exception {
        mockMvc.perform(post("/api/v1/ai/score-ideas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"orientacaoId":"ori-1","ideiaIds":["ideia-1"]}
                                """))
                .andExpect(status().isForbidden());
        verify(aiScoreService, never()).score(any(), any());
    }

    @Test
    @WithMockInovagabUser(perfil = PerfilUsuario.GESTOR, userId = "user-gestor", email = "ana.oliveira@aguiabranca.com.br")
    void requestSemIdeiasRetorna400() throws Exception {
        mockMvc.perform(post("/api/v1/ai/score-ideas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"orientacaoId":"ori-1","ideiaIds":[]}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
        verify(aiScoreService, never()).score(any(), any());
    }

    // ---- dashboard-narrative RBAC ----

    @Test
    void narrativaSemTokenRetorna401() throws Exception {
        mockMvc.perform(get("/api/v1/ai/dashboard-narrative"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockInovagabUser(perfil = PerfilUsuario.LIDER)
    void liderObtemNarrativa() throws Exception {
        when(aiScoreService.narrativaDashboard(null)).thenReturn(new DashboardNarrativeResponse(
                "ROI sólido com gargalo na conversão",
                "O portfólio conta com 7 ideias e ROI de 28%.",
                java.util.List.of("ROI de 28%"),
                "Converter as ideias aprovadas.",
                "gemini-3.6-flash",
                false,
                Instant.now()));

        mockMvc.perform(get("/api/v1/ai/dashboard-narrative"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("ROI sólido com gargalo na conversão"))
                .andExpect(jsonPath("$.resumo").isNotEmpty())
                .andExpect(jsonPath("$.fallback").value(false));
    }

    @Test
    @WithMockInovagabUser(perfil = PerfilUsuario.LIDER)
    void liderObtemNarrativaFiltradaPorOrientacao() throws Exception {
        when(aiScoreService.narrativaDashboard("ori-1")).thenReturn(new DashboardNarrativeResponse(
                "Estratégia Operações: ROI de 35%",
                "A estratégia conta com 3 ideias e ROI de 35% a.a.",
                java.util.List.of("ROI de 35%", "3 ideias"),
                "Converter as ideias pendentes em projetos.",
                "gemini-3.6-flash",
                false,
                Instant.now()));

        mockMvc.perform(get("/api/v1/ai/dashboard-narrative").param("orientacaoId", "ori-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Estratégia Operações: ROI de 35%"))
                .andExpect(jsonPath("$.fallback").value(false));
    }

    @Test
    @WithMockInovagabUser(perfil = PerfilUsuario.GESTOR)
    void gestorNaoObtemNarrativa() throws Exception {
        mockMvc.perform(get("/api/v1/ai/dashboard-narrative"))
                .andExpect(status().isForbidden());
        verify(aiScoreService, never()).narrativaDashboard(any());
    }

    @Test
    @WithMockInovagabUser(perfil = PerfilUsuario.OPERADOR)
    void operadorNaoObtemNarrativa() throws Exception {
        mockMvc.perform(get("/api/v1/ai/dashboard-narrative"))
                .andExpect(status().isForbidden());
        verify(aiScoreService, never()).narrativaDashboard(any());
    }
}
