package com.gtnix.inovagab.service;

import com.gtnix.inovagab.document.IdeiaDocument;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Detecção local de ideia repetida pelo título (índice de Jaccard sobre palavras normalizadas).
 * Não usa IA: a regra precisa rodar em toda criação, sem custo e sem latência externa.
 */
final class SimilaridadeIdeias {

    static final double LIMIAR = 0.7;

    private static final Set<String> STOPWORDS = Set.of(
            "para", "com", "sem", "dos", "das", "nos", "nas", "por", "uma", "que", "mais", "menos", "the"
    );

    private SimilaridadeIdeias() {
    }

    static Optional<String> encontrarDuplicada(String titulo, List<IdeiaDocument> existentes) {
        Set<String> alvo = tokens(titulo);
        if (alvo.size() < 2) {
            return Optional.empty();
        }
        return existentes.stream()
                .filter(doc -> doc.getId() != null)
                .filter(doc -> jaccard(alvo, tokens(doc.getTitulo())) >= LIMIAR)
                .map(IdeiaDocument::getId)
                .findFirst();
    }

    static Set<String> tokens(String texto) {
        if (texto == null) {
            return Set.of();
        }
        String semAcento = Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT);
        return Arrays.stream(semAcento.split("[^a-z0-9]+"))
                .filter(t -> t.length() >= 3)
                .filter(t -> !STOPWORDS.contains(t))
                .collect(Collectors.toSet());
    }

    static double jaccard(Set<String> a, Set<String> b) {
        if (a.isEmpty() || b.isEmpty()) {
            return 0;
        }
        Set<String> intersecao = new HashSet<>(a);
        intersecao.retainAll(b);
        Set<String> uniao = new HashSet<>(a);
        uniao.addAll(b);
        return (double) intersecao.size() / uniao.size();
    }
}
