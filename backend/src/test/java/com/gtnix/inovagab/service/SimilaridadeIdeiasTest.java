package com.gtnix.inovagab.service;

import com.gtnix.inovagab.document.IdeiaDocument;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SimilaridadeIdeiasTest {

    private final List<IdeiaDocument> existentes = List.of(
            GamificacaoServiceTest.ideia("ideia-checkin", "op-1", null),
            GamificacaoServiceTest.ideia("ideia-rotas", "op-2", null));

    {
        existentes.get(0).setTitulo("Check-in digital no embarque");
        existentes.get(1).setTitulo("Roteirização sustentável da última milha");
    }

    @Test
    void mesmoTituloComAcentoECaixaDiferentesEhDuplicado() {
        assertEquals(Optional.of("ideia-checkin"),
                SimilaridadeIdeias.encontrarDuplicada("CHECK-IN DIGITAL NO EMBARQUE!", existentes));
    }

    @Test
    void stopwordsNaoContam() {
        assertEquals(Optional.of("ideia-rotas"),
                SimilaridadeIdeias.encontrarDuplicada("Roteirizacao sustentavel para a ultima milha", existentes));
    }

    @Test
    void ideiaDiferenteNaoEhDuplicada() {
        assertTrue(SimilaridadeIdeias.encontrarDuplicada("Check-list digital de pneus", existentes).isEmpty());
    }

    @Test
    void tituloCurtoDemaisNaoEhComparado() {
        assertTrue(SimilaridadeIdeias.encontrarDuplicada("Embarque", existentes).isEmpty());
    }
}
