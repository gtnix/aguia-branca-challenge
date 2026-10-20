package com.gtnix.inovagab.service;

import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@Component
public class GeminiClient {

    static final String GENERATE_URL_PREFIX = "https://generativelanguage.googleapis.com/v1beta/models/";
    static final String API_KEY_HEADER = "x-goog-api-key";

    static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(5);
    static final Duration GENERATE_READ_TIMEOUT = Duration.ofSeconds(15);
    /** Embedding roda dentro da criação da ideia: precisa falhar rápido para não travar o colaborador. */
    static final Duration EMBED_READ_TIMEOUT = Duration.ofSeconds(4);
    static final int MAX_TENTATIVAS = 2;
    static final double TEMPERATURA = 0.2;

    private final RestClient generateClient;
    private final RestClient embedClient;
    private final Duration backoff;

    public GeminiClient() {
        this(
                RestClient.builder().requestFactory(requestFactory(GENERATE_READ_TIMEOUT)).build(),
                RestClient.builder().requestFactory(requestFactory(EMBED_READ_TIMEOUT)).build(),
                Duration.ofSeconds(2)
        );
    }

    GeminiClient(RestClient restClient) {
        this(restClient, restClient, Duration.ZERO);
    }

    GeminiClient(RestClient generateClient, RestClient embedClient, Duration backoff) {
        this.generateClient = generateClient;
        this.embedClient = embedClient;
        this.backoff = backoff;
    }

    public String generateContent(String model, String apiKey, String prompt) {
        return generateContent(model, apiKey, prompt, null);
    }

    /**
     * @param responseSchema schema OpenAPI aceito pelo Gemini; quando informado, a resposta vem em JSON puro.
     */
    public String generateContent(String model, String apiKey, String prompt, Map<String, Object> responseSchema) {
        Map<String, Object> generationConfig = new LinkedHashMap<>();
        generationConfig.put("temperature", TEMPERATURA);
        if (responseSchema != null) {
            generationConfig.put("responseMimeType", "application/json");
            generationConfig.put("responseSchema", responseSchema);
        }
        Map<String, Object> body = Map.of(
                "contents", List.of(Map.of(
                        "parts", List.of(Map.of("text", prompt))
                )),
                "generationConfig", generationConfig
        );
        String url = GENERATE_URL_PREFIX + model + ":generateContent";
        return comRetry(() -> generateClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .header(API_KEY_HEADER, apiKey)
                .body(body)
                .retrieve()
                .body(String.class));
    }

    public String embedContent(String model, String apiKey, String text) {
        Map<String, Object> body = Map.of(
                "model", "models/" + model,
                "content", Map.of("parts", List.of(Map.of("text", text)))
        );
        String url = GENERATE_URL_PREFIX + model + ":embedContent";
        return embedClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .header(API_KEY_HEADER, apiKey)
                .body(body)
                .retrieve()
                .body(String.class);
    }

    private String comRetry(Supplier<String> chamada) {
        for (int tentativa = 1; ; tentativa++) {
            try {
                return chamada.get();
            } catch (HttpClientErrorException.TooManyRequests ex) {
                if (tentativa >= MAX_TENTATIVAS) {
                    throw ex;
                }
                aguardar(backoff.multipliedBy(tentativa));
            }
        }
    }

    private static void aguardar(Duration duracao) {
        if (duracao.isZero()) {
            return;
        }
        try {
            Thread.sleep(duracao.toMillis());
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("retry interrompido", ex);
        }
    }

    private static SimpleClientHttpRequestFactory requestFactory(Duration readTimeout) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(CONNECT_TIMEOUT);
        factory.setReadTimeout(readTimeout);
        return factory;
    }
}
