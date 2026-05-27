package com.gtnix.aguiabranca.domain.model

data class OrientacaoEstrategica(
    val id: String,
    val titulo: String,
    val descricao: String,
    val categoria: CategoriaOrientacao,
    val prioridade: Int = 1,
    val ativa: Boolean = true,
    val criadoPor: String,
    val dataCriacao: Long = System.currentTimeMillis(),
    val dataExpiracao: Long? = null
)

enum class CategoriaOrientacao {
    REDUCAO_CUSTOS,
    QUALIDADE_SERVICO,
    INOVACAO_TECNOLOGICA,
    SUSTENTABILIDADE,
    SEGURANCA,
    EXPERIENCIA_CLIENTE,
    EFICIENCIA_OPERACIONAL
}
