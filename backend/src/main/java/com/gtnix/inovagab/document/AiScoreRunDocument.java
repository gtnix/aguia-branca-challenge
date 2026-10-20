package com.gtnix.inovagab.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/** Uma execução de IA (score ou rascunho de feedback), guardada para auditoria e métricas de calibração. */
@Document(collection = "ai_score_runs")
@CompoundIndex(name = "idx_ai_runs_tipo_orientacao", def = "{'tipo': 1, 'orientacaoId': 1}")
public class AiScoreRunDocument {

    public static final String TIPO_SCORE = "SCORE";
    public static final String TIPO_RASCUNHO_FEEDBACK = "RASCUNHO_FEEDBACK";

    @Id
    private String id;
    private String tipo;
    private String ideiaId;
    private String orientacaoId;
    private String gestorUserId;
    private String modelo;
    private String promptVersao;
    private String inputHash;
    private String saidaBruta;
    private long latenciaMs;
    private boolean fallback;
    private String erro;
    private Instant criadoEm;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getIdeiaId() {
        return ideiaId;
    }

    public void setIdeiaId(String ideiaId) {
        this.ideiaId = ideiaId;
    }

    public String getOrientacaoId() {
        return orientacaoId;
    }

    public void setOrientacaoId(String orientacaoId) {
        this.orientacaoId = orientacaoId;
    }

    public String getGestorUserId() {
        return gestorUserId;
    }

    public void setGestorUserId(String gestorUserId) {
        this.gestorUserId = gestorUserId;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getPromptVersao() {
        return promptVersao;
    }

    public void setPromptVersao(String promptVersao) {
        this.promptVersao = promptVersao;
    }

    public String getInputHash() {
        return inputHash;
    }

    public void setInputHash(String inputHash) {
        this.inputHash = inputHash;
    }

    public String getSaidaBruta() {
        return saidaBruta;
    }

    public void setSaidaBruta(String saidaBruta) {
        this.saidaBruta = saidaBruta;
    }

    public long getLatenciaMs() {
        return latenciaMs;
    }

    public void setLatenciaMs(long latenciaMs) {
        this.latenciaMs = latenciaMs;
    }

    public boolean isFallback() {
        return fallback;
    }

    public void setFallback(boolean fallback) {
        this.fallback = fallback;
    }

    public String getErro() {
        return erro;
    }

    public void setErro(String erro) {
        this.erro = erro;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Instant criadoEm) {
        this.criadoEm = criadoEm;
    }
}
