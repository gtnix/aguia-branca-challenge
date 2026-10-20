package com.gtnix.inovagab.domain;

public enum StatusResgate {
    SOLICITADO,
    APROVADO,
    ENTREGUE,
    NEGADO;

    public boolean canTransitionTo(StatusResgate next) {
        return switch (this) {
            case SOLICITADO -> next == APROVADO || next == ENTREGUE;
            case APROVADO -> next == ENTREGUE;
            case ENTREGUE, NEGADO -> false;
        };
    }
}
