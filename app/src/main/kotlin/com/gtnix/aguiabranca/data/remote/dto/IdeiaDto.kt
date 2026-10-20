package com.gtnix.aguiabranca.data.remote.dto

data class IdeiaDto(
    val id: String,
    val autorUserId: String? = null,
    val autorNome: String? = null,
    val titulo: String,
    val descricao: String? = null,
    val tipo: String? = null,
    val area: String? = null,
    val orientacaoId: String? = null,
    val status: String? = null,
    val prioridade: Int? = null,
    val impacto: Int? = null,
    val esforco: Int? = null,
    val scoreIa: Int? = null,
    val justificativaIa: String? = null,
    val scoreIaFallback: Boolean? = null,
    val feedbackReprovacao: String? = null,
    val avaliadorUserId: String? = null,
    val feedbackUtil: Boolean? = null,
    val duplicadaDe: String? = null,
    val apoios: Int = 0,
    val dataCriacao: String? = null,
    val dataAtualizacao: String? = null,
    val problema: String? = null,
    val publicoAfetado: String? = null,
    val frequencia: String? = null,
    val beneficio: String? = null,
    val analiseIa: AnaliseIaDto? = null
)

data class AnaliseIaDto(
    val status: String? = null,
    val faixa: String? = null,
    val criterios: List<CriterioIaDto> = emptyList(),
    val riscos: List<String> = emptyList(),
    val perguntas: List<String> = emptyList(),
    val impactoSugerido: Int? = null,
    val esforcoSugerido: Int? = null,
    val estimativaOrigem: String? = null,
    val motivoAjusteEstimativa: String? = null,
    val modelo: String? = null,
    val concordaIa: Boolean? = null,
    val motivoDiscordanciaIa: String? = null
)

data class CriterioIaDto(
    val criterio: String,
    val nota: Int,
    val peso: Int,
    val evidencia: String? = null
)

/** Impacto e esforço não são enviados pelo colaborador: o gestor os define em [EstimativaRequestDto]. */
data class IdeiaRequestDto(
    val titulo: String,
    val descricao: String,
    val tipo: String,
    val area: String,
    val orientacaoId: String,
    val problema: String? = null,
    val publicoAfetado: String? = null,
    val frequencia: String? = null,
    val beneficio: String? = null
)

data class AvaliacaoRequestDto(
    val status: String,
    val feedback: String? = null,
    val concordaIa: Boolean? = null,
    val motivoDiscordanciaIa: String? = null
)

data class EstimativaRequestDto(
    val impacto: Int,
    val esforco: Int,
    val motivoAjuste: String? = null
)

data class PrioridadeRequestDto(
    val prioridade: Int
)

data class ScoreIdeasRequestDto(
    val orientacaoId: String,
    val ideiaIds: List<String>
)

data class IdeaScoreItemDto(
    val ideiaId: String,
    val score: Int? = null,
    val justificativa: String? = null,
    val status: String? = null,
    val faixa: String? = null
)

data class ScoreIdeasResponseDto(
    val ranking: List<IdeaScoreItemDto> = emptyList(),
    val modelo: String? = null,
    val fallback: Boolean = false
)

data class RascunhoFeedbackRequestDto(
    val observacoes: String? = null
)

data class RascunhoFeedbackResponseDto(
    val rascunho: String,
    val modelo: String? = null,
    val fallback: Boolean = false
)
