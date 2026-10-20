package com.gtnix.inovagab.service;

import com.gtnix.inovagab.document.PontoEventoDocument;
import com.gtnix.inovagab.domain.PerfilUsuario;
import com.gtnix.inovagab.domain.TipoEvento;
import com.gtnix.inovagab.repository.PontoEventoRepository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;

/** Faz um mock de {@link PontoEventoRepository} se comportar como uma coleção real, para testar o ledger com estado. */
final class LedgerEmMemoria {

    final List<PontoEventoDocument> eventos = new ArrayList<>();

    LedgerEmMemoria(PontoEventoRepository repo) {
        lenient().when(repo.save(any(PontoEventoDocument.class))).thenAnswer(inv -> salvar(inv.getArgument(0)));
        lenient().when(repo.saveAll(anyList())).thenAnswer(inv -> {
            List<PontoEventoDocument> docs = inv.getArgument(0);
            docs.forEach(this::salvar);
            return docs;
        });
        lenient().when(repo.existsByChave(anyString()))
                .thenAnswer(inv -> eventos.stream().anyMatch(e -> e.getChave().equals(inv.getArgument(0))));
        lenient().when(repo.findByUserIdOrderByCriadoEmDesc(anyString()))
                .thenAnswer(inv -> doUsuario(inv.getArgument(0)));
        lenient().when(repo.findByUserIdAndVistoFalseOrderByCriadoEmDesc(anyString()))
                .thenAnswer(inv -> doUsuario(inv.getArgument(0)).stream().filter(e -> !e.isVisto()).toList());
        lenient().when(repo.findByRefIdAndEstornadoFalse(anyString()))
                .thenAnswer(inv -> eventos.stream()
                        .filter(e -> inv.getArgument(0).equals(e.getRefId()) && !e.isEstornado())
                        .toList());
        lenient().when(repo.findByTemporadaIdAndPerfil(anyString(), any(PerfilUsuario.class)))
                .thenAnswer(inv -> eventos.stream()
                        .filter(e -> inv.getArgument(0).equals(e.getTemporadaId()) && inv.getArgument(1) == e.getPerfil())
                        .toList());
        lenient().when(repo.findByPerfil(any(PerfilUsuario.class)))
                .thenAnswer(inv -> eventos.stream().filter(e -> inv.getArgument(0) == e.getPerfil()).toList());
        lenient().when(repo.countByUserIdAndTipoAndCriadoEmAfter(anyString(), any(TipoEvento.class), any(Instant.class)))
                .thenAnswer(inv -> eventos.stream()
                        .filter(e -> e.getUserId().equals(inv.getArgument(0))
                                && e.getTipo() == inv.getArgument(1)
                                && e.getCriadoEm().isAfter(inv.getArgument(2)))
                        .count());
    }

    private PontoEventoDocument salvar(PontoEventoDocument doc) {
        if (doc.getId() == null) {
            doc.setId(UUID.randomUUID().toString());
            eventos.add(doc);
        }
        return doc;
    }

    private List<PontoEventoDocument> doUsuario(String userId) {
        return eventos.stream()
                .filter(e -> e.getUserId().equals(userId))
                .sorted(Comparator.comparing(PontoEventoDocument::getCriadoEm).reversed())
                .toList();
    }

    int asas(String userId) {
        return eventos.stream().filter(e -> e.getUserId().equals(userId)).mapToInt(PontoEventoDocument::getAsas).sum();
    }

    int xp(String userId) {
        return eventos.stream().filter(e -> e.getUserId().equals(userId)).mapToInt(PontoEventoDocument::getXp).sum();
    }

    long contar(String userId, TipoEvento tipo) {
        return eventos.stream().filter(e -> e.getUserId().equals(userId) && e.getTipo() == tipo).count();
    }
}
