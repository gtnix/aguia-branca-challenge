package com.gtnix.inovagab.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "inovagab.cors")
public class CorsProperties {

    /**
     * Origens permitidas, separadas por vírgula. Android nativo não usa CORS;
     * a restrição vale para Swagger UI e clientes browser.
     */
    private String origins = "http://localhost:8080,http://127.0.0.1:8080";

    public String getOrigins() {
        return origins;
    }

    public void setOrigins(String origins) {
        this.origins = origins;
    }
}
