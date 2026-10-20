package com.gtnix.inovagab.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gtnix.inovagab.config.GeminiProperties;
import com.gtnix.inovagab.document.IdeiaDocument;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EmbeddingServiceTest {

    @Test
    void cosseno() {
        assertEquals(1.0, EmbeddingService.cosseno(List.of(1.0, 2.0), List.of(2.0, 4.0)), 1e-9);
        assertEquals(0.0, EmbeddingService.cosseno(List.of(1.0, 0.0), List.of(0.0, 1.0)), 1e-9);
        assertEquals(0.0, EmbeddingService.cosseno(List.of(1.0), List.of(1.0, 0.0)), 1e-9);
    }

    @Test
    void encontraSemelhanteAcimaDoLimiar() {
        IdeiaDocument parecida = ideia("p", List.of(1.0, 0.1));
        IdeiaDocument diferente = ideia("d", List.of(0.0, 1.0));
        IdeiaDocument semVetor = ideia("s", null);

        assertEquals(Optional.of("p"),
                EmbeddingService.encontrarSemelhante(List.of(1.0, 0.0), List.of(semVetor, diferente, parecida)));
        assertTrue(EmbeddingService.encontrarSemelhante(List.of(0.6, 0.4), List.of(diferente)).isEmpty());
    }

    @Test
    void semChaveNaoChamaGemini() {
        GeminiProperties props = new GeminiProperties();
        GeminiClient client = mock(GeminiClient.class);

        Optional<List<Double>> vetor = new EmbeddingService(props, client, new ObjectMapper()).gerar("t", "d");

        assertTrue(vetor.isEmpty());
        verify(client, never()).embedContent(any(), any(), any());
    }

    @Test
    void falhaDoGeminiNaoBloqueiaCadastro() {
        GeminiProperties props = new GeminiProperties();
        props.setApiKey("k");
        GeminiClient client = mock(GeminiClient.class);
        when(client.embedContent(any(), any(), any())).thenThrow(new RuntimeException("timeout"));

        assertTrue(new EmbeddingService(props, client, new ObjectMapper()).gerar("t", "d").isEmpty());
    }

    @Test
    void lêVetorDaResposta() {
        GeminiProperties props = new GeminiProperties();
        props.setApiKey("k");
        GeminiClient client = mock(GeminiClient.class);
        when(client.embedContent(any(), any(), any())).thenReturn("{\"embedding\":{\"values\":[0.1,0.2]}}");

        assertEquals(Optional.of(List.of(0.1, 0.2)),
                new EmbeddingService(props, client, new ObjectMapper()).gerar("t", "d"));
    }

    private static IdeiaDocument ideia(String id, List<Double> embedding) {
        IdeiaDocument doc = new IdeiaDocument();
        doc.setId(id);
        doc.setEmbedding(embedding);
        return doc;
    }
}
