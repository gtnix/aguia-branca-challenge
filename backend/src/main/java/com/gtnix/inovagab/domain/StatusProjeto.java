package com.gtnix.inovagab.domain;

public enum StatusProjeto {
    PLANEJADO,
    EM_ANDAMENTO,
    PAUSADO,
    CONCLUIDO,
    CANCELADO;

    public boolean isTerminal() {
        return this == CONCLUIDO || this == CANCELADO;
    }

    public boolean canTransitionTo(StatusProjeto next) {
        if (next == null || this == next) {
            return true;
        }
        return !isTerminal();
    }
}
