package com.gtnix.inovagab.security;

import com.gtnix.inovagab.domain.PerfilUsuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        JwtProperties properties = new JwtProperties();
        properties.setSecret("test-secret-change-me-min-32-characters!");
        properties.setExpirationSeconds(3600);
        jwtService = new JwtService(properties);
    }

    @Test
    void generateAndParseTokenIncludesSubjectEmailAndPerfil() {
        String token = jwtService.generateToken("user-gestor", "ana.oliveira@aguiabranca.com.br", PerfilUsuario.GESTOR);

        var claims = jwtService.parseClaims(token);

        assertEquals("user-gestor", claims.getSubject());
        assertEquals("ana.oliveira@aguiabranca.com.br", claims.get("email", String.class));
        assertEquals("GESTOR", claims.get("perfil", String.class));
        assertEquals(3600, jwtService.getExpirationSeconds());
        assertNotNull(claims.getExpiration());
    }

    @Test
    void parseRejectsTamperedToken() {
        String token = jwtService.generateToken("user-lider", "marcos.silva@aguiabranca.com.br", PerfilUsuario.LIDER);
        String tampered = token.substring(0, token.length() - 4) + "xxxx";

        assertThrows(Exception.class, () -> jwtService.parseClaims(tampered));
    }
}
