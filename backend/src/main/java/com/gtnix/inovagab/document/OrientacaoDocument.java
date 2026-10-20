package com.gtnix.inovagab.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.LocalDate;

@Document(collection = "orientacoes")
@CompoundIndex(name = "idx_orientacoes_categoria_ativo", def = "{'categoria': 1, 'ativo': 1}")
public class OrientacaoDocument extends SoftDeletableDocument {

    @Id
    private String id;
    private String titulo;
    private String descricao;
    @Indexed
    private String categoria;
    private String campanha;
    private Instant dataCriacao;
    private Instant dataAtualizacao;
    private String criadoPorUserId;
    private LocalDate campanhaFim;
    private Integer campanhaBonusAsas;
    private Integer campanhaTopN;
    private Instant campanhaEncerradaEm;
    /** PASSAGEIROS, LOGISTICA ou COMERCIO; nulo quando a diretriz vale para todo o grupo. */
    private String divisao;

    public String getDivisao() {
        return divisao;
    }

    public void setDivisao(String divisao) {
        this.divisao = divisao;
    }

    public LocalDate getCampanhaFim() {
        return campanhaFim;
    }

    public void setCampanhaFim(LocalDate campanhaFim) {
        this.campanhaFim = campanhaFim;
    }

    public Integer getCampanhaBonusAsas() {
        return campanhaBonusAsas;
    }

    public void setCampanhaBonusAsas(Integer campanhaBonusAsas) {
        this.campanhaBonusAsas = campanhaBonusAsas;
    }

    public Integer getCampanhaTopN() {
        return campanhaTopN;
    }

    public void setCampanhaTopN(Integer campanhaTopN) {
        this.campanhaTopN = campanhaTopN;
    }

    public Instant getCampanhaEncerradaEm() {
        return campanhaEncerradaEm;
    }

    public void setCampanhaEncerradaEm(Instant campanhaEncerradaEm) {
        this.campanhaEncerradaEm = campanhaEncerradaEm;
    }

    /** Campanha patrocinada: tem prazo, ainda não venceu e não foi encerrada. */
    public boolean isCampanhaAtiva(LocalDate hoje) {
        return isAtivo() && campanhaFim != null && campanhaEncerradaEm == null && !hoje.isAfter(campanhaFim);
    }

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

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getCampanha() {
        return campanha;
    }

    public void setCampanha(String campanha) {
        this.campanha = campanha;
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

    public String getCriadoPorUserId() {
        return criadoPorUserId;
    }

    public void setCriadoPorUserId(String criadoPorUserId) {
        this.criadoPorUserId = criadoPorUserId;
    }
}
