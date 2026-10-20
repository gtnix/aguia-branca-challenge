package com.gtnix.inovagab.document;

import org.springframework.data.mongodb.core.index.Indexed;

import java.time.Instant;

public abstract class SoftDeletableDocument {

    @Indexed
    private boolean ativo = true;
    private Instant deletedAt;

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public Instant getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Instant deletedAt) {
        this.deletedAt = deletedAt;
    }

    public void marcarExcluido() {
        this.ativo = false;
        this.deletedAt = Instant.now();
    }
}
