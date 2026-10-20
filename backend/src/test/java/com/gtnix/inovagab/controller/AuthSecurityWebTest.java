package com.gtnix.inovagab.controller;

import com.gtnix.inovagab.config.SecurityConfig;
import com.gtnix.inovagab.domain.PerfilUsuario;
import com.gtnix.inovagab.dto.auth.LoginResponse;
import com.gtnix.inovagab.dto.auth.UsuarioResponse;
import com.gtnix.inovagab.security.CustomUserDetailsService;
import com.gtnix.inovagab.security.JsonAuthHandlers;
import com.gtnix.inovagab.security.JwtAuthenticationFilter;
import com.gtnix.inovagab.security.JwtProperties;
import com.gtnix.inovagab.security.JwtService;
import com.gtnix.inovagab.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {AuthController.class, HealthController.class})
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
class AuthSecurityWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void healthIsPublic() throws Exception {
        mockMvc.perform(get("/api/v1/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void meWithoutTokenReturns401Json() throws Exception {
        mockMvc.perform(get("/api/v1/auth/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Não autenticado"));
    }

    @Test
    void loginReturnsTokenContract() throws Exception {
        UsuarioResponse usuario = new UsuarioResponse(
                "user-gestor",
                "Ana Oliveira",
                "ana.oliveira@aguiabranca.com.br",
                PerfilUsuario.GESTOR,
                "OPERACOES",
                "LOGISTICA",
                "CD Serra"
        );
        when(authService.login(any())).thenReturn(
                new LoginResponse("jwt-token", "Bearer", 3600, usuario)
        );

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"ana.oliveira@aguiabranca.com.br","senha":"123456"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("jwt-token"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.expiresIn").value(3600))
                .andExpect(jsonPath("$.usuario.perfil").value("GESTOR"))
                .andExpect(jsonPath("$.usuario.email").value("ana.oliveira@aguiabranca.com.br"));
    }

    @Test
    void loginWithInvalidPayloadReturns400() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"nao-e-email","senha":""}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void loginWithMalformedJsonReturns400() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void meWithInvalidTokenReturns401() throws Exception {
        mockMvc.perform(get("/api/v1/auth/me")
                        .header("Authorization", "Bearer not-a-jwt"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }
}
