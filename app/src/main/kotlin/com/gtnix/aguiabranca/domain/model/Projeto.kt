package com.gtnix.aguiabranca.domain.model

/**
 * Entity de Domínio: Projeto
 *
 * ## Conceito de Negócio
 *
 * Um Projeto é criado quando uma ideia é aprovada e se torna
 * uma iniciativa formal com equipe, prazos e metas.
 *
 * ## Ciclo de Vida
 *
 * ```
 * ┌───────────┐    iniciar    ┌─────────────┐    concluir    ┌───────────┐
 * │ PLANEJADO ├──────────────►│ EM_ANDAMENTO├───────────────►│ CONCLUIDO │
 * └───────────┘               └──────┬──────┘               └───────────┘
 *                                    │
 *                                 pausar
 *                                    │
 *                                    ▼
 *                              ┌──────────┐
 *                              │  PAUSADO │
 *                              └────┬─────┘
 *                                   │
 *                               retomar
 *                                   │
 *                                   ▼
 *                           EM_ANDAMENTO
 * ```
 *
 * @property id Identificador único
 * @property nome Nome do projeto
 * @property objetivo Objetivo principal a ser alcançado
 * @property descricao Descrição detalhada
 * @property area Área de atuação principal
 * @property status Status atual do projeto
 * @property ideiaOrigemId ID da ideia que originou (se houver)
 * @property orientacaoId Orientação estratégica alinhada
 * @property responsavelId ID do gestor responsável
 * @property responsavelNome Nome do responsável
 * @property membrosIds IDs dos membros da equipe
 * @property dataCriacao Timestamp de criação
 * @property dataInicio Quando o projeto iniciou
 * @property dataPrevistaConclusao Prazo previsto
 * @property dataConclusao Quando foi concluído (se houver)
 * @property progresso Percentual de progresso (0-100)
 * @property resultados Descrição dos resultados obtidos
 */
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
    val resultados: String? = null
)

/**
 * Status do projeto no ciclo de vida.
 */
enum class StatusProjeto {
    /** Em fase de planejamento */
    PLANEJADO,
    
    /** Em execução ativa */
    EM_ANDAMENTO,
    
    /** Temporariamente pausado */
    PAUSADO,
    
    /** Finalizado com sucesso */
    CONCLUIDO,
    
    /** Cancelado */
    CANCELADO
}
