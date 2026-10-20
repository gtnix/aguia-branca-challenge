package com.gtnix.inovagab.controller;

import com.gtnix.inovagab.config.SecurityConfig;
import com.gtnix.inovagab.domain.PerfilUsuario;
import com.gtnix.inovagab.dto.dashboard.AreaDesempenhoResponse;
import com.gtnix.inovagab.dto.dashboard.DashboardByProjectResponse;
import com.gtnix.inovagab.dto.dashboard.DashboardByStrategyResponse;
import com.gtnix.inovagab.dto.dashboard.DashboardSummaryResponse;
import com.gtnix.inovagab.dto.dashboard.FunnelStepResponse;
import com.gtnix.inovagab.security.CustomUserDetailsService;
import com.gtnix.inovagab.security.JsonAuthHandlers;
import com.gtnix.inovagab.security.JwtAuthenticationFilter;
import com.gtnix.inovagab.security.JwtProperties;
import com.gtnix.inovagab.security.JwtService;
import com.gtnix.inovagab.security.WithMockInovagabUser;
import com.gtnix.inovagab.service.DashboardService;
import com.gtnix.inovagab.web.ApiException;
import com.gtnix.inovagab.web.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = DashboardController.class)
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
class DashboardRbacWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DashboardService dashboardService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void semTokenRetorna401() throws Exception {
        mockMvc.perform(get("/api/v1/dashboard/summary"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
        verify(dashboardService, never()).summary(org.mockito.ArgumentMatchers.any());
    }

    @Test
    @WithMockInovagabUser(perfil = PerfilUsuario.OPERADOR, userId = "user-operador", email = "pedro.santos@aguiabranca.com.br")
    void operadorNaoConsultaSummary() throws Exception {
        mockMvc.perform(get("/api/v1/dashboard/summary"))
                .andExpect(status().isForbidden());
        verify(dashboardService, never()).summary(org.mockito.ArgumentMatchers.any());
    }

    @Test
    @WithMockInovagabUser(perfil = PerfilUsuario.GESTOR, userId = "user-gestor", email = "ana.oliveira@aguiabranca.com.br")
    void gestorNaoConsultaSummary() throws Exception {
        mockMvc.perform(get("/api/v1/dashboard/summary"))
                .andExpect(status().isForbidden());
        verify(dashboardService, never()).summary(org.mockito.ArgumentMatchers.any());
    }

    @Test
    @WithMockInovagabUser(perfil = PerfilUsuario.LIDER)
    void liderConsultaSummaryComSeries() throws Exception {
        when(dashboardService.summary(null)).thenReturn(sampleSummary());

        mockMvc.perform(get("/api/v1/dashboard/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roiMedio").value(20.00))
                .andExpect(jsonPath("$.lucroTotal").value(20000.00))
                .andExpect(jsonPath("$.investimentoTotal").value(100000.00))
                .andExpect(jsonPath("$.retornoTotal").value(10000.00))
                .andExpect(jsonPath("$.projetosNoPrazo").value(1))
                .andExpect(jsonPath("$.projetosAtrasados").value(0))
                .andExpect(jsonPath("$.ganhoProdutividadePercentual").value(40.0))
                .andExpect(jsonPath("$.totalIdeias").value(4))
                .andExpect(jsonPath("$.ideiasAprovadas").value(2))
                .andExpect(jsonPath("$.ideiasEmProjeto").value(1))
                .andExpect(jsonPath("$.projetosAtivos").value(1))
                .andExpect(jsonPath("$.tempoMedioAprovacaoDias").value(3))
                .andExpect(jsonPath("$.funil[0].etapa").value("IDEIAS"))
                .andExpect(jsonPath("$.funil[0].quantidade").value(4))
                .andExpect(jsonPath("$.desempenhoPorArea[0].area").value("OPERACOES"))
                .andExpect(jsonPath("$.desempenhoPorArea[0].percentual").value(50.0));
    }

    @Test
    @WithMockInovagabUser(perfil = PerfilUsuario.LIDER)
    void liderConsultaFunilEPorArea() throws Exception {
        when(dashboardService.funnel()).thenReturn(sampleSummary().funil());
        when(dashboardService.byArea()).thenReturn(sampleSummary().desempenhoPorArea());

        mockMvc.perform(get("/api/v1/dashboard/funnel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[2].etapa").value("EM_PROJETO"));
        mockMvc.perform(get("/api/v1/dashboard/by-area"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].label").value("Operações"));
    }

    @Test
    @WithMockInovagabUser(perfil = PerfilUsuario.GESTOR, userId = "user-gestor", email = "ana.oliveira@aguiabranca.com.br")
    void gestorNaoConsultaFunil() throws Exception {
        mockMvc.perform(get("/api/v1/dashboard/funnel"))
                .andExpect(status().isForbidden());
        verify(dashboardService, never()).funnel();
    }

    @Test
    @WithMockInovagabUser(perfil = PerfilUsuario.LIDER)
    void liderConsultaPorEstrategia() throws Exception {
        when(dashboardService.byStrategy("ori-1")).thenReturn(new DashboardByStrategyResponse(
                "ori-1",
                "Eficiência",
                true,
                new BigDecimal("80000.00"),
                new BigDecimal("9000.00"),
                new BigDecimal("28000.00"),
                new BigDecimal("35.00"),
                1,
                0,
                List.of()
        ));

        mockMvc.perform(get("/api/v1/dashboard/by-strategy/ori-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orientacaoId").value("ori-1"))
                .andExpect(jsonPath("$.roiMedio").value(35.00))
                .andExpect(jsonPath("$.investimentoTotal").value(80000.00));
    }

    @Test
    @WithMockInovagabUser(perfil = PerfilUsuario.LIDER)
    void estrategiaInexistenteRetorna404() throws Exception {
        when(dashboardService.byStrategy("faltando"))
                .thenThrow(new ApiException(HttpStatus.NOT_FOUND, "Orientação não encontrada"));

        mockMvc.perform(get("/api/v1/dashboard/by-strategy/faltando"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @WithMockInovagabUser(perfil = PerfilUsuario.LIDER)
    void liderConsultaPorProjeto() throws Exception {
        when(dashboardService.byProject("proj-1")).thenReturn(new DashboardByProjectResponse(
                "proj-1",
                "Telemetria",
                "ori-1",
                "EM_ANDAMENTO",
                new BigDecimal("100000.00"),
                new BigDecimal("10000.00"),
                new BigDecimal("20000.00"),
                new BigDecimal("20.00"),
                LocalDate.parse("2026-12-31"),
                true,
                40
        ));

        mockMvc.perform(get("/api/v1/dashboard/by-project/proj-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projetoId").value("proj-1"))
                .andExpect(jsonPath("$.roi").value(20.00))
                .andExpect(jsonPath("$.lucroAnualizado").value(20000.00))
                .andExpect(jsonPath("$.noPrazo").value(true));
    }

    @Test
    @WithMockInovagabUser(perfil = PerfilUsuario.LIDER)
    void projetoInexistenteRetorna404() throws Exception {
        when(dashboardService.byProject("faltando"))
                .thenThrow(new ApiException(HttpStatus.NOT_FOUND, "Projeto não encontrado"));

        mockMvc.perform(get("/api/v1/dashboard/by-project/faltando"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @WithMockInovagabUser(perfil = PerfilUsuario.OPERADOR, userId = "user-operador", email = "pedro.santos@aguiabranca.com.br")
    void operadorNaoConsultaPorProjeto() throws Exception {
        mockMvc.perform(get("/api/v1/dashboard/by-project/proj-1"))
                .andExpect(status().isForbidden());
        verify(dashboardService, never()).byProject("proj-1");
    }

    private static DashboardSummaryResponse sampleSummary() {
        return new DashboardSummaryResponse(
                new BigDecimal("20.00"),
                new BigDecimal("20000.00"),
                new BigDecimal("100000.00"),
                new BigDecimal("10000.00"),
                1,
                0,
                new BigDecimal("40.0"),
                4,
                2,
                1,
                1,
                3,
                List.of(
                        new FunnelStepResponse("IDEIAS", "Ideias", 4),
                        new FunnelStepResponse("APROVADAS", "Aprovadas", 2),
                        new FunnelStepResponse("EM_PROJETO", "Em Projeto", 1)
                ),
                List.of(new AreaDesempenhoResponse("OPERACOES", "Operações", 2, new BigDecimal("50.0")))
        );
    }
}
