package com.gtnix.inovagab.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gtnix.inovagab.config.GeminiProperties;
import com.gtnix.inovagab.document.IdeiaDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Detecta ideias repetidas pelo significado (mesma proposta com outras palavras), complementando o
 * Jaccard de {@link SimilaridadeIdeias}. Sem chave ou com falha do Gemini, simplesmente não sinaliza.
 */
@Service
public class EmbeddingService {

    private static final Logger log = LoggerFactory.getLogger(EmbeddingService.class);

    static final double LIMIAR_SEMANTICO = 0.88;
    static final int MAX_TEXTO = 2000;

    private final GeminiProperties geminiProperties;
    private final GeminiClient geminiClient;
    private final ObjectMapper objectMapper;

    public EmbeddingService(GeminiProperties geminiProperties, GeminiClient geminiClient, ObjectMapper objectMapper) {
        this.geminiProperties = geminiProperties;
        this.geminiClient = geminiClient;
        this.objectMapper = objectMapper;
    }

    public Optional<List<Double>> gerar(String titulo, String descricao) {
        if (!geminiProperties.isConfigured()) {
            return Optional.empty();
        }
        String texto = (titulo == null ? "" : titulo) + "\n" + (descricao == null ? "" : descricao);
        if (texto.length() > MAX_TEXTO) {
            texto = texto.substring(0, MAX_TEXTO);
        }
        try {
            String body = geminiClient.embedContent(
                    geminiProperties.getEmbeddingModel(), geminiProperties.getApiKey(), texto);
            JsonNode values = objectMapper.readTree(body).path("embedding").path("values");
            if (!values.isArray() || values.isEmpty()) {
                return Optional.empty();
            }
            List<Double> vetor = new ArrayList<>(values.size());
            values.forEach(v -> vetor.add(v.asDouble()));
            return Optional.of(vetor);
        } catch (Exception ex) {
            log.warn("Embedding indisponível, duplicidade semântica ignorada: {}", ex.getMessage());
            return Optional.empty();
        }
    }

    static Optional<String> encontrarSemelhante(List<Double> alvo, List<IdeiaDocument> existentes) {
        return existentes.stream()
                .filter(doc -> doc.getId() != null && doc.getEmbedding() != null)
                .filter(doc -> cosseno(alvo, doc.getEmbedding()) >= LIMIAR_SEMANTICO)
                .map(IdeiaDocument::getId)
                .findFirst();
    }

    static double cosseno(List<Double> a, List<Double> b) {
        if (a == null || b == null || a.isEmpty() || a.size() != b.size()) {
            return 0;
        }
        double produto = 0;
        double normaA = 0;
        double normaB = 0;
        for (int i = 0; i < a.size(); i++) {
            produto += a.get(i) * b.get(i);
            normaA += a.get(i) * a.get(i);
            normaB += b.get(i) * b.get(i);
        }
        if (normaA == 0 || normaB == 0) {
            return 0;
        }
        return produto / (Math.sqrt(normaA) * Math.sqrt(normaB));
    }
}
