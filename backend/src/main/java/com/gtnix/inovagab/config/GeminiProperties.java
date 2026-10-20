package com.gtnix.inovagab.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "inovagab.gemini")
public class GeminiProperties {

    private String apiKey = "";
    private String model = "gemini-3.6-flash";
    private String embeddingModel = "gemini-embedding-001";

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getEmbeddingModel() {
        return embeddingModel;
    }

    public void setEmbeddingModel(String embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    public boolean isConfigured() {
        return apiKey != null && !apiKey.isBlank();
    }
}
