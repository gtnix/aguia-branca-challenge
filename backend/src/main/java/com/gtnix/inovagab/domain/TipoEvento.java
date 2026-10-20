package com.gtnix.inovagab.domain;

/**
 * Catálogo de eventos da Economia de Inovação GAB. Valores-base antes de multiplicadores.
 * A tabela completa está em docs/GAMIFICACAO.md §4.
 */
public enum TipoEvento {
    IDEIA_ENVIADA(2, 0, "Ideia enviada"),
    IDEIA_APROVADA(20, 10, "Ideia aprovada pelo gestor"),
    IDEIA_CONVERTIDA(40, 30, "Ideia convertida em projeto"),
    PROJETO_CONCLUIDO_AUTOR(80, 100, "Projeto da sua ideia concluído"),
    RETORNO_AUTOR(0, 0, "Participação no retorno do projeto"),
    FEEDBACK_RECEBIDO(3, 0, "Aprendiz: feedback recebido"),
    APOIO_RECEBIDO(1, 0, "Apoio recebido na ideia"),

    AVALIACAO_NO_PRAZO(10, 0, "Avaliação dentro do prazo"),
    AVALIACAO_FORA_PRAZO(3, 0, "Avaliação fora do prazo"),
    FEEDBACK_UTIL(5, 5, "Sua avaliação foi marcada como útil"),
    APROVACAO_CONVERTIDA(15, 0, "Ideia que você aprovou virou projeto"),
    PROJETO_NO_PRAZO(50, 50, "Projeto entregue no prazo"),
    ROI_POSITIVO(20, 0, "Projeto concluído com ROI positivo"),

    IDEIA_ALINHADA(3, 0, "Ideia alinhada à sua orientação"),
    PROJETO_CONCLUIDO_ESTRATEGIA(60, 0, "Projeto da sua orientação concluído"),

    CAMPANHA_BONUS(0, 0, "Bônus de campanha patrocinada"),
    MISSAO_CONCLUIDA(0, 0, "Missão semanal concluída"),
    RESGATE(0, 0, "Resgate de recompensa"),
    ESTORNO(0, 0, "Estorno");

    private final int xpBase;
    private final int asasBase;
    private final String descricao;

    TipoEvento(int xpBase, int asasBase, String descricao) {
        this.xpBase = xpBase;
        this.asasBase = asasBase;
        this.descricao = descricao;
    }

    public int getXpBase() {
        return xpBase;
    }

    public int getAsasBase() {
        return asasBase;
    }

    public String getDescricao() {
        return descricao;
    }

    /** Eventos que contam como atividade de valor (sequência semanal e missões). */
    public boolean isAtividade() {
        return this != MISSAO_CONCLUIDA && this != RESGATE && this != ESTORNO && this != CAMPANHA_BONUS;
    }
}
