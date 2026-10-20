package com.gtnix.aguiabranca.domain.model

import java.time.LocalDate

data class OrientacaoEstrategica(
    val id: String,
    val titulo: String,
    val descricao: String,
    val categoria: CategoriaOrientacao,
    val prioridade: Int = 1,
    val ativa: Boolean = true,
    val criadoPor: String,
    val dataCriacao: Long = System.currentTimeMillis(),
    val dataExpiracao: Long? = null,
    val campanha: String = "",
    val campanhaPatrocinada: CampanhaPatrocinada? = null,
    /** Divisão de negócio a que a orientação se destina; nulo vale para todo o grupo. */
    val divisao: DivisaoNegocio? = null
)

/** Campanha com prazo: ideias valem x1,5 até [fim] e os top N recebem [bonusAsas] no encerramento. */
data class CampanhaPatrocinada(
    val fim: LocalDate,
    val bonusAsas: Int,
    val topN: Int,
    val ativa: Boolean = true,
    val encerrada: Boolean = false
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
