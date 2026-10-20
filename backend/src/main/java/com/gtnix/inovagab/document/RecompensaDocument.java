package com.gtnix.inovagab.document;

import com.gtnix.inovagab.domain.NivelGamificacao;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "recompensas")
public class RecompensaDocument extends SoftDeletableDocument {

    @Id
    private String id;
    private String titulo;
    private String descricao;
    private String categoria;
    private String parceiro;
    private int custoAsas;
    private int estoque;
    private NivelGamificacao nivelMinimo;
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

    public String getParceiro() {
        return parceiro;
    }

    public void setParceiro(String parceiro) {
        this.parceiro = parceiro;
    }

    public int getCustoAsas() {
        return custoAsas;
    }

    public void setCustoAsas(int custoAsas) {
        this.custoAsas = custoAsas;
    }

    public int getEstoque() {
        return estoque;
    }

    public void setEstoque(int estoque) {
        this.estoque = estoque;
    }

    public NivelGamificacao getNivelMinimo() {
        return nivelMinimo;
    }

    public void setNivelMinimo(NivelGamificacao nivelMinimo) {
        this.nivelMinimo = nivelMinimo;
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
