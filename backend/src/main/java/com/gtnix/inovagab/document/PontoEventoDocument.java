package com.gtnix.inovagab.document;

import com.gtnix.inovagab.domain.PerfilUsuario;
import com.gtnix.inovagab.domain.TipoEvento;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * Lançamento do extrato de gamificação. Imutável depois de gravado, exceto pelas flags
 * {@code estornado} e {@code visto}; correções entram como um novo evento {@link TipoEvento#ESTORNO}.
 */
@Document(collection = "pontos_eventos")
@CompoundIndexes({
        @CompoundIndex(name = "idx_eventos_user_criado", def = "{'userId': 1, 'criadoEm': -1}"),
        @CompoundIndex(name = "idx_eventos_temporada_perfil", def = "{'temporadaId': 1, 'perfil': 1}"),
        @CompoundIndex(name = "idx_eventos_ref", def = "{'refId': 1}")
})
public class PontoEventoDocument {

    @Id
    private String id;
    @Indexed(unique = true)
    private String chave;
    private String userId;
    private PerfilUsuario perfil;
    private String divisao;
    private String equipe;
    private TipoEvento tipo;
    private int xp;
    private int asas;
    private String refId;
    private String descricao;
    private String tag;
    private String temporadaId;
    private Instant criadoEm;
    private boolean estornado;
    private boolean visto;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getChave() {
        return chave;
    }

    public void setChave(String chave) {
        this.chave = chave;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public PerfilUsuario getPerfil() {
        return perfil;
    }

    public void setPerfil(PerfilUsuario perfil) {
        this.perfil = perfil;
    }

    public String getDivisao() {
        return divisao;
    }

    public void setDivisao(String divisao) {
        this.divisao = divisao;
    }

    public String getEquipe() {
        return equipe;
    }

    public void setEquipe(String equipe) {
        this.equipe = equipe;
    }

    public TipoEvento getTipo() {
        return tipo;
    }

    public void setTipo(TipoEvento tipo) {
        this.tipo = tipo;
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public int getAsas() {
        return asas;
    }

    public void setAsas(int asas) {
        this.asas = asas;
    }

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTemporadaId() {
        return temporadaId;
    }

    public void setTemporadaId(String temporadaId) {
        this.temporadaId = temporadaId;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Instant criadoEm) {
        this.criadoEm = criadoEm;
    }

    public boolean isEstornado() {
        return estornado;
    }

    public void setEstornado(boolean estornado) {
        this.estornado = estornado;
    }

    public boolean isVisto() {
        return visto;
    }

    public void setVisto(boolean visto) {
        this.visto = visto;
    }
}
