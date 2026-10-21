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
    val prioridade: Int? = null,
    val scoreIa: Int? = null,
    val justificativaIa: String? = null,
    val scoreIaFallback: Boolean = false,
    val upvotes: Int = 0,
    val temSimilaridade: Boolean = false,
    val avaliadorId: String? = null,
    val feedbackUtil: Boolean? = null,
    val problema: String? = null,
    val publicoAfetado: String? = null,
    val frequencia: FrequenciaProblema? = null,
    val beneficio: String? = null,
    val analiseIa: AnaliseIa? = null
) {
    val scorePriorizacao: Int
        get() = impactoEstimado - esforcoEstimado + upvotes

    val temContexto: Boolean
        get() = !problema.isNullOrBlank() || !publicoAfetado.isNullOrBlank() ||
            frequencia != null || !beneficio.isNullOrBlank()

    fun isAprovadaOuConvertida(): Boolean =
        status == StatusIdeia.APROVADA || status == StatusIdeia.CONVERTIDA_PROJETO
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
