package com.gtnix.aguiabranca.domain.model

/**
 * Entity de Domínio: Ideia
 *
 * ## Conceito de Negócio
 *
 * Uma Ideia representa uma sugestão de melhoria ou inovação
 * submetida por um colaborador. O fluxo é:
 *
 * ```
 * OPERADOR submete → GESTOR avalia → Se aprovado → Vira PROJETO
 *                                  → Se reprovado → Feedback
 * ```
 *
 * ## Ciclo de Vida
 *
 * ```
 * ┌─────────┐   avaliar   ┌───────────┐   aprovar   ┌──────────┐
 * │ PENDENTE├────────────►│ EM_ANALISE├────────────►│ APROVADA │
 * └─────────┘             └─────┬─────┘             └────┬─────┘
 *                               │                        │
 *                           reprovar                virar projeto
 *                               │                        │
 *                               ▼                        ▼
 *                         ┌───────────┐            ┌──────────┐
 *                         │ REPROVADA │            │ PROJETO  │
 *                         └───────────┘            └──────────┘
 * ```
 *
 * @property id Identificador único
 * @property titulo Título resumido da ideia
 * @property descricao Descrição detalhada
 * @property tipo Se é uma IDEIA de melhoria ou PROBLEMA identificado
 * @property area Área de atuação relacionada
 * @property status Status atual no fluxo
 * @property autorId ID do usuário que submeteu
 * @property autorNome Nome do autor (denormalizado para performance)
 * @property orientacaoId ID da orientação estratégica alinhada (opcional)
 * @property feedback Feedback do gestor (se avaliada)
 * @property projetoId ID do projeto criado (se aprovada e convertida)
 * @property dataCriacao Timestamp de criação
 * @property dataAvaliacao Timestamp da avaliação (se houver)
 */
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
    val dataAvaliacao: Long? = null
)

/**
 * Tipo da submissão: pode ser uma ideia de melhoria ou um problema identificado.
 */
enum class TipoIdeia {
    /** Sugestão de melhoria ou inovação */
    IDEIA,
    
    /** Problema ou gargalo identificado */
    PROBLEMA
}

/**
 * Status da ideia no fluxo de avaliação.
 *
 * ## Material FIAP - State Machine
 *
 * Este enum representa uma máquina de estados simples.
 * As transições válidas são:
 *
 * - PENDENTE → EM_ANALISE (gestor inicia avaliação)
 * - EM_ANALISE → APROVADA (gestor aprova)
 * - EM_ANALISE → REPROVADA (gestor reprova)
 * - APROVADA → CONVERTIDA_PROJETO (vira projeto)
 */
enum class StatusIdeia {
    /** Aguardando avaliação de um gestor */
    PENDENTE,
    
    /** Gestor está analisando */
    EM_ANALISE,
    
    /** Aprovada pelo gestor */
    APROVADA,
    
    /** Reprovada pelo gestor */
    REPROVADA,
    
    /** Convertida em projeto */
    CONVERTIDA_PROJETO
}
