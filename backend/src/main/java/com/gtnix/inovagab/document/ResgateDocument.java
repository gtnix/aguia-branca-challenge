package com.gtnix.inovagab.document;

import com.gtnix.inovagab.domain.StatusResgate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "resgates")
@CompoundIndexes({
        @CompoundIndex(name = "idx_resgates_user", def = "{'userId': 1, 'solicitadoEm': -1}"),
        @CompoundIndex(name = "idx_resgates_status", def = "{'status': 1, 'solicitadoEm': 1}")
})
public class ResgateDocument {

    @Id
    private String id;
    private String recompensaId;
    private String recompensaTitulo;
    private String userId;
    private String userNome;
    private int custoAsas;
    private StatusResgate status;
    private String observacao;
    private String aprovadorUserId;
    private Instant solicitadoEm;
    private Instant decididoEm;
    private Instant entregueEm;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRecompensaId() {
        return recompensaId;
    }

    public void setRecompensaId(String recompensaId) {
        this.recompensaId = recompensaId;
    }

    public String getRecompensaTitulo() {
        return recompensaTitulo;
    }

    public void setRecompensaTitulo(String recompensaTitulo) {
        this.recompensaTitulo = recompensaTitulo;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserNome() {
        return userNome;
    }

    public void setUserNome(String userNome) {
        this.userNome = userNome;
    }

    public int getCustoAsas() {
        return custoAsas;
    }

    public void setCustoAsas(int custoAsas) {
        this.custoAsas = custoAsas;
    }

    public StatusResgate getStatus() {
        return status;
    }

    public void setStatus(StatusResgate status) {
        this.status = status;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public String getAprovadorUserId() {
        return aprovadorUserId;
    }

    public void setAprovadorUserId(String aprovadorUserId) {
        this.aprovadorUserId = aprovadorUserId;
    }

    public Instant getSolicitadoEm() {
        return solicitadoEm;
    }

    public void setSolicitadoEm(Instant solicitadoEm) {
        this.solicitadoEm = solicitadoEm;
    }

    public Instant getDecididoEm() {
        return decididoEm;
    }

    public void setDecididoEm(Instant decididoEm) {
        this.decididoEm = decididoEm;
    }

    public Instant getEntregueEm() {
        return entregueEm;
    }

    public void setEntregueEm(Instant entregueEm) {
        this.entregueEm = entregueEm;
    }
}
