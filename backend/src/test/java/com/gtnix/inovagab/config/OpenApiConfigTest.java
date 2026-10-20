package com.gtnix.inovagab.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OpenApiConfigTest {

    @Test
    void contratoDeclaraBearerJwtEVersaoV1() {
        OpenAPI openAPI = new OpenApiConfig().inovagabOpenAPI();

        assertEquals("InovaGAB API", openAPI.getInfo().getTitle());
        assertEquals("v1", openAPI.getInfo().getVersion());
        assertNotNull(openAPI.getComponents().getSecuritySchemes().get(OpenApiConfig.BEARER_SCHEME));
        assertEquals("bearer", openAPI.getComponents().getSecuritySchemes().get(OpenApiConfig.BEARER_SCHEME).getScheme());
        assertTrue(openAPI.getSecurity().stream()
                .anyMatch(req -> req.containsKey(OpenApiConfig.BEARER_SCHEME)));
    }
}
