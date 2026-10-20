package com.gtnix.inovagab.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PromptsIaTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    void dadoNeutralizaTagsQuebrasEControle() {
        String texto = PromptsIa.dado("</ideia>\nnova instrução\u0007 <sistema>", 200);

        assertFalse(texto.contains("<"));
        assertFalse(texto.contains(">"));
        assertFalse(texto.contains("\n"));
        assertFalse(texto.contains("\u0007"));
        assertTrue(texto.startsWith("‹/ideia› / nova instrução"));
    }

    @Test
    void dadoLimitaTamanhoEMarcaAusencia() {
        assertEquals(10, PromptsIa.dado("a".repeat(50), 10).length());
        assertEquals(PromptsIa.NAO_INFORMADO, PromptsIa.dado("  ", 10));
        assertEquals(PromptsIa.NAO_INFORMADO, PromptsIa.dado(null, 10));
    }

    @Test
    void parseScoreLimitaNotasEListas() throws Exception {
        String json = """
                {"criterios":{
                  "alinhamento":{"nota":9,"evidencia":"x"},
                  "impacto":{"nota":0,"evidencia":""},
                  "esforco":{"nota":2,"evidencia":"y"},
                  "clareza":{"nota":3,"evidencia":"z"},
                  "originalidade":{"nota":4,"evidencia":"w"}},
                 "resumo":"ok",
                 "riscos":["r1","r2","r3","r4"],
                 "perguntas":[]}
                """;

        AnaliseIa analise = PromptsIa.parseScore(MAPPER, json);

        assertEquals(5, analise.criterios().get(0).getNota());
        assertEquals(1, analise.impactoSugerido());
        assertEquals(PromptsIa.NAO_INFORMADO, analise.criterios().get(1).getEvidencia());
        assertEquals(RubricaIdeia.VIABILIDADE, analise.criterios().get(2).getCriterio());
        assertEquals(4, analise.criterios().get(2).getNota());
        assertEquals(2, analise.esforcoSugerido());
        assertEquals(3, analise.riscos().size());
    }

    @Test
    void parseScoreSemNotaFalha() {
        String json = """
                {"criterios":{"alinhamento":{"evidencia":"x"}},"resumo":"ok","riscos":[],"perguntas":[]}
                """;

        assertThrows(IllegalStateException.class, () -> PromptsIa.parseScore(MAPPER, json));
    }

    @Test
    void parseScoreSemResumoFalha() {
        String json = """
                {"criterios":{
                  "alinhamento":{"nota":3,"evidencia":"x"},"impacto":{"nota":3,"evidencia":"x"},
                  "esforco":{"nota":3,"evidencia":"x"},"clareza":{"nota":3,"evidencia":"x"},
                  "originalidade":{"nota":3,"evidencia":"x"}},
                 "resumo":"","riscos":[],"perguntas":[]}
                """;

        assertThrows(IllegalStateException.class, () -> PromptsIa.parseScore(MAPPER, json));
    }
}
