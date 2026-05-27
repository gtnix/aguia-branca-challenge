package com.gtnix.aguiabranca.domain.model

data class Ideia(
    val id: String,
    val titulo: String,
    val descricao: String,
    val tipo: TipoIdeia,
    val area: AreaAtuacao,
    val status: StatusIdeia = StatusIdeia.PENDENTE,
    val autorId: String,
    val autorNome: String,
    val orientacaoId: String? = null,
    val feedback: String? = null,
    val projetoId: String? = null,
    val dataCriacao: Long = System.currentTimeMillis(),
    val dataAvaliacao: Long? = null,
    val impactoEstimado: Int = 0,
    val esforcoEstimado: Int = 0,
    val upvotes: Int = 0,
    val temSimilaridade: Boolean = false
) {
    val scorePriorizacao: Int
        get() = impactoEstimado - esforcoEstimado
}

enum class TipoIdeia {
    IDEIA,
    PROBLEMA
}

enum class StatusIdeia {
    PENDENTE,
    EM_ANALISE,
    APROVADA,
    REPROVADA,
    CONVERTIDA_PROJETO
}
