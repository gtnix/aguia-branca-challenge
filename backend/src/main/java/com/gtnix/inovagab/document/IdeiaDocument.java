package com.gtnix.inovagab.document;

import com.gtnix.inovagab.domain.OrigemEstimativa;
import com.gtnix.inovagab.domain.StatusAnaliseIa;
import com.gtnix.inovagab.domain.StatusIdeia;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Document(collection = "ideias")
@CompoundIndexes({
        @CompoundIndex(name = "idx_ideias_autor_ativo", def = "{'autorUserId': 1, 'ativo': 1}"),
        @CompoundIndex(name = "idx_ideias_status_ativo", def = "{'status': 1, 'ativo': 1}"),
        @CompoundIndex(name = "idx_ideias_area_ativo", def = "{'area': 1, 'ativo': 1}"),
        @CompoundIndex(name = "idx_ideias_orientacao_status_ativo", def = "{'orientacaoId': 1, 'status': 1, 'ativo': 1}")
})
public class IdeiaDocument extends SoftDeletableDocument {

    @Id
    private String id;
    private String autorUserId;
    private String titulo;
    private String descricao;
    private String tipo;
    private String area;
    private String orientacaoId;
    private StatusIdeia status;
    private Integer prioridade;
    private Integer impacto;
    private Integer esforco;
    private Integer scoreIa;
    private String justificativaIa;
    private String scoreIaModelo;
    private Boolean scoreIaFallback;
    private String feedbackReprovacao;
    private String avaliadorUserId;
    private Boolean feedbackUtil;
    private String duplicadaDe;
    private int apoios;
    private Instant dataAvaliacao;
    private Instant dataCriacao;
    private Instant dataAtualizacao;

    private String problema;
    private String publicoAfetado;
    private String frequencia;
    private String beneficio;

    private StatusAnaliseIa scoreIaStatus;
    private List<CriterioIa> criteriosIa;
    private List<String> riscosIa;
    private List<String> perguntasIa;
    private Integer impactoSugeridoIa;
    private Integer esforcoSugeridoIa;
    private Instant scoreIaEm;

    private OrigemEstimativa estimativaOrigem;
    private String motivoAjusteEstimativa;
    private String estimativaPorUserId;
    private Instant estimativaEm;

    private Boolean concordaIa;
    private String motivoDiscordanciaIa;

    private List<Double> embedding;

    public String getProblema() {
        return problema;
    }

    public void setProblema(String problema) {
        this.problema = problema;
    }

    public String getPublicoAfetado() {
        return publicoAfetado;
    }

    public void setPublicoAfetado(String publicoAfetado) {
        this.publicoAfetado = publicoAfetado;
    }

    public String getFrequencia() {
        return frequencia;
    }

    public void setFrequencia(String frequencia) {
        this.frequencia = frequencia;
    }

    public String getBeneficio() {
        return beneficio;
    }

    public void setBeneficio(String beneficio) {
        this.beneficio = beneficio;
    }

    public StatusAnaliseIa getScoreIaStatus() {
        return scoreIaStatus;
    }

    public void setScoreIaStatus(StatusAnaliseIa scoreIaStatus) {
        this.scoreIaStatus = scoreIaStatus;
    }

    public List<CriterioIa> getCriteriosIa() {
        return criteriosIa;
    }

    public void setCriteriosIa(List<CriterioIa> criteriosIa) {
        this.criteriosIa = criteriosIa;
    }

    public List<String> getRiscosIa() {
        return riscosIa;
    }

    public void setRiscosIa(List<String> riscosIa) {
        this.riscosIa = riscosIa;
    }

    public List<String> getPerguntasIa() {
        return perguntasIa;
    }

    public void setPerguntasIa(List<String> perguntasIa) {
        this.perguntasIa = perguntasIa;
    }

    public Integer getImpactoSugeridoIa() {
        return impactoSugeridoIa;
    }

    public void setImpactoSugeridoIa(Integer impactoSugeridoIa) {
        this.impactoSugeridoIa = impactoSugeridoIa;
    }

    public Integer getEsforcoSugeridoIa() {
        return esforcoSugeridoIa;
    }

    public void setEsforcoSugeridoIa(Integer esforcoSugeridoIa) {
        this.esforcoSugeridoIa = esforcoSugeridoIa;
    }

    public Instant getScoreIaEm() {
        return scoreIaEm;
    }

    public void setScoreIaEm(Instant scoreIaEm) {
        this.scoreIaEm = scoreIaEm;
    }

    public OrigemEstimativa getEstimativaOrigem() {
        return estimativaOrigem;
    }

    public void setEstimativaOrigem(OrigemEstimativa estimativaOrigem) {
        this.estimativaOrigem = estimativaOrigem;
    }

    public String getMotivoAjusteEstimativa() {
        return motivoAjusteEstimativa;
    }

    public void setMotivoAjusteEstimativa(String motivoAjusteEstimativa) {
        this.motivoAjusteEstimativa = motivoAjusteEstimativa;
    }

    public String getEstimativaPorUserId() {
        return estimativaPorUserId;
    }

    public void setEstimativaPorUserId(String estimativaPorUserId) {
        this.estimativaPorUserId = estimativaPorUserId;
    }

    public Instant getEstimativaEm() {
        return estimativaEm;
    }

    public void setEstimativaEm(Instant estimativaEm) {
        this.estimativaEm = estimativaEm;
    }

    public Boolean getConcordaIa() {
        return concordaIa;
    }

    public void setConcordaIa(Boolean concordaIa) {
        this.concordaIa = concordaIa;
    }

    public String getMotivoDiscordanciaIa() {
        return motivoDiscordanciaIa;
    }

    public void setMotivoDiscordanciaIa(String motivoDiscordanciaIa) {
        this.motivoDiscordanciaIa = motivoDiscordanciaIa;
    }

    public List<Double> getEmbedding() {
        return embedding;
    }

    public void setEmbedding(List<Double> embedding) {
        this.embedding = embedding;
    }

    public String getAvaliadorUserId() {
        return avaliadorUserId;
    }

    public void setAvaliadorUserId(String avaliadorUserId) {
        this.avaliadorUserId = avaliadorUserId;
    }

    public Boolean getFeedbackUtil() {
        return feedbackUtil;
    }

    public void setFeedbackUtil(Boolean feedbackUtil) {
        this.feedbackUtil = feedbackUtil;
    }

    public String getDuplicadaDe() {
        return duplicadaDe;
    }

    public void setDuplicadaDe(String duplicadaDe) {
        this.duplicadaDe = duplicadaDe;
    }

    public int getApoios() {
        return apoios;
    }

    public void setApoios(int apoios) {
        this.apoios = apoios;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAutorUserId() {
        return autorUserId;
    }

    public void setAutorUserId(String autorUserId) {
        this.autorUserId = autorUserId;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getOrientacaoId() {
        return orientacaoId;
    }

    public void setOrientacaoId(String orientacaoId) {
        this.orientacaoId = orientacaoId;
    }

    public StatusIdeia getStatus() {
        return status;
    }

    public void setStatus(StatusIdeia status) {
        this.status = status;
    }

    public Integer getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(Integer prioridade) {
        this.prioridade = prioridade;
    }

    public Integer getImpacto() {
        return impacto;
    }

    public void setImpacto(Integer impacto) {
        this.impacto = impacto;
    }

    public Integer getEsforco() {
        return esforco;
    }

    public void setEsforco(Integer esforco) {
        this.esforco = esforco;
    }

    public Integer getScoreIa() {
        return scoreIa;
    }

    public void setScoreIa(Integer scoreIa) {
        this.scoreIa = scoreIa;
    }

    public String getJustificativaIa() {
        return justificativaIa;
    }

    public void setJustificativaIa(String justificativaIa) {
        this.justificativaIa = justificativaIa;
    }

    public String getScoreIaModelo() {
        return scoreIaModelo;
    }

    public void setScoreIaModelo(String scoreIaModelo) {
        this.scoreIaModelo = scoreIaModelo;
    }

    public Boolean getScoreIaFallback() {
        return scoreIaFallback;
    }

    public void setScoreIaFallback(Boolean scoreIaFallback) {
        this.scoreIaFallback = scoreIaFallback;
    }

    public String getFeedbackReprovacao() {
        return feedbackReprovacao;
    }

    public void setFeedbackReprovacao(String feedbackReprovacao) {
        this.feedbackReprovacao = feedbackReprovacao;
    }

    public Instant getDataAvaliacao() {
        return dataAvaliacao;
    }

    public void setDataAvaliacao(Instant dataAvaliacao) {
        this.dataAvaliacao = dataAvaliacao;
    }

    public Instant getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(Instant dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public Instant getDataAtualizacao() {
        return dataAtualizacao;
    }

    public void setDataAtualizacao(Instant dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }
}
