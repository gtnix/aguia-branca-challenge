package com.gtnix.inovagab.service;

import com.gtnix.inovagab.document.CriterioIa;

import java.util.List;

/** Rubrica já validada vinda do Gemini para uma única ideia. */
record AnaliseIa(
        List<CriterioIa> criterios,
        int impactoSugerido,
        int esforcoSugerido,
        String resumo,
        List<String> riscos,
        List<String> perguntas
) {
}
