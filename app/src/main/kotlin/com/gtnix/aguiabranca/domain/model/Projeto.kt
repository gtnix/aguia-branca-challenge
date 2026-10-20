package com.gtnix.aguiabranca.domain.model

data class Projeto(
    val id: String,
    val nome: String,
    val objetivo: String,
    val descricao: String,
    val area: AreaAtuacao,
    val status: StatusProjeto = StatusProjeto.PLANEJADO,
    val ideiaOrigemId: String? = null,
    val orientacaoId: String? = null,
    val responsavelId: String,
    val responsavelNome: String,
    val membrosIds: List<String> = emptyList(),
    val dataCriacao: Long = System.currentTimeMillis(),
    val dataInicio: Long? = null,
    val dataPrevistaConclusao: Long? = null,
    val dataConclusao: Long? = null,
    val progresso: Int = 0,
    val resultados: String? = null,
    val investimentoEstimado: Double = 0.0,
    val investimentoRealizado: Double = 0.0,
    val retornoEstimadoMensal: Double = 0.0,
    val retornoRealizadoMensal: Double = 0.0,
    val roiServidor: Double? = null
) {
    val roi: Double
        get() = roiServidor ?: if (investimentoRealizado > 0)
            ((retornoRealizadoMensal * 12) - investimentoRealizado) / investimentoRealizado * 100
        else 0.0

    /** Projeto ainda aberto cuja data prevista de conclusão já passou. */
    fun estaAtrasado(agora: Long = System.currentTimeMillis()): Boolean {
        val prazo = dataPrevistaConclusao ?: return false
        if (status == StatusProjeto.CONCLUIDO || status == StatusProjeto.CANCELADO) return false
        return agora > prazo
    }
}

enum class StatusProjeto {
    PLANEJADO,
    EM_ANDAMENTO,
    PAUSADO,
    CONCLUIDO,
    CANCELADO
}
