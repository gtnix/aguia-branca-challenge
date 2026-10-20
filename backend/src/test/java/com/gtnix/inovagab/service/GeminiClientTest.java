package com.gtnix.inovagab.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.ExpectedCount.times;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class GeminiClientTest {

    private static final String URL = GeminiClient.GENERATE_URL_PREFIX + "m:generateContent";

    private MockRestServiceServer server;
    private GeminiClient client;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();
        RestClient restClient = builder.build();
        client = new GeminiClient(restClient, restClient, Duration.ZERO);
    }

    @Test
    void enviaTemperaturaBaixaESchemaJson() {
        server.expect(requestTo(URL))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header(GeminiClient.API_KEY_HEADER, "k"))
                .andExpect(jsonPath("$.generationConfig.temperature").value(GeminiClient.TEMPERATURA))
                .andExpect(jsonPath("$.generationConfig.responseMimeType").value("application/json"))
                .andExpect(jsonPath("$.generationConfig.responseSchema.type").value("OBJECT"))
                .andRespond(withSuccess("{\"ok\":true}", MediaType.APPLICATION_JSON));

        String body = client.generateContent("m", "k", "prompt", Map.of("type", "OBJECT"));

        assertEquals("{\"ok\":true}", body);
        server.verify();
    }

    @Test
    void repeteUmaVezApos429() {
        server.expect(requestTo(URL)).andRespond(withStatus(HttpStatus.TOO_MANY_REQUESTS));
        server.expect(requestTo(URL)).andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

        assertEquals("{}", client.generateContent("m", "k", "prompt"));
        server.verify();
    }

    @Test
    void desisteAposEsgotarTentativas() {
        server.expect(times(GeminiClient.MAX_TENTATIVAS), requestTo(URL))
                .andRespond(withStatus(HttpStatus.TOO_MANY_REQUESTS));

        assertThrows(HttpClientErrorException.TooManyRequests.class,
                () -> client.generateContent("m", "k", "prompt"));
        server.verify();
    }

    @Test
    void naoRepeteOutrosErros() {
        server.expect(times(1), requestTo(URL)).andRespond(withStatus(HttpStatus.BAD_REQUEST));

        assertThrows(HttpClientErrorException.BadRequest.class,
                () -> client.generateContent("m", "k", "prompt"));
        server.verify();
    }
}
