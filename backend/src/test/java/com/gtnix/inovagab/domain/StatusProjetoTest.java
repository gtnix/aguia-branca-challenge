package com.gtnix.inovagab.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StatusProjetoTest {

    @Test
    void estadosTerminaisNaoReabrem() {
        assertTrue(StatusProjeto.CONCLUIDO.isTerminal());
        assertTrue(StatusProjeto.CANCELADO.isTerminal());
        assertFalse(StatusProjeto.CONCLUIDO.canTransitionTo(StatusProjeto.EM_ANDAMENTO));
        assertTrue(StatusProjeto.PAUSADO.canTransitionTo(StatusProjeto.EM_ANDAMENTO));
        assertTrue(StatusProjeto.EM_ANDAMENTO.canTransitionTo(StatusProjeto.CONCLUIDO));
    }
}
