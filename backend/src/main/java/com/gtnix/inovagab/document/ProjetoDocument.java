package com.gtnix.inovagab.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Document(collection = "projetos")
@CompoundIndex(
        name = "idx_projetos_ideiaId_ativo_unique",
        def = "{'ideiaId': 1}",
        unique = true,
        partialFilter = "{'ativo': true, 'ideiaId': {'$type': 'string'}}"
)
@CompoundIndex(name = "idx_projetos_orientacao_ativo", def = "{'orientacaoId': 1, 'ativo': 1}")
@CompoundIndex(name = "idx_projetos_status_ativo", def = "{'status': 1, 'ativo': 1}")
public class ProjetoDocument extends SoftDeletableDocument {

    @Id
    private String id;
    private String titulo;
    private String objetivo;
    private String descricao;
    private String area;
    private String orientacaoId;
    private String ideiaId;
    private String etapa;
    private String status;
    private BigDecimal investimento;
    private BigDecimal retorno;
    private LocalDate prazo;
    private Integer progressoPercentual;
    private String resultadosObtidos;
    private String responsavelUserId;
    private Instant dataCriacao;
    private Instant dataAtualizacao;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getObjetivo() {
        return objetivo;
    }

    public void setObjetivo(String objetivo) {
        this.objetivo = objetivo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
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

    public String getIdeiaId() {
        return ideiaId;
    }

    public void setIdeiaId(String ideiaId) {
        this.ideiaId = ideiaId;
    }

    public String getEtapa() {
        return etapa;
    }

    public void setEtapa(String etapa) {
        this.etapa = etapa;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getInvestimento() {
        return investimento;
    }

    public void setInvestimento(BigDecimal investimento) {
        this.investimento = investimento;
    }

    public BigDecimal getRetorno() {
        return retorno;
    }

    public void setRetorno(BigDecimal retorno) {
        this.retorno = retorno;
    }

    public LocalDate getPrazo() {
        return prazo;
    }

    public void setPrazo(LocalDate prazo) {
        this.prazo = prazo;
    }

    public Integer getProgressoPercentual() {
        return progressoPercentual;
    }

    public void setProgressoPercentual(Integer progressoPercentual) {
        this.progressoPercentual = progressoPercentual;
    }

    public String getResultadosObtidos() {
        return resultadosObtidos;
    }

    public void setResultadosObtidos(String resultadosObtidos) {
        this.resultadosObtidos = resultadosObtidos;
    }

    public String getResponsavelUserId() {
        return responsavelUserId;
    }

    public void setResponsavelUserId(String responsavelUserId) {
        this.responsavelUserId = responsavelUserId;
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
