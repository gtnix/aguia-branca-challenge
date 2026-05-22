package com.gtnix.aguiabranca.domain.model

/**
 * Entity de Domínio: Orientação Estratégica
 *
 * ## Conceito de Negócio
 *
 * Orientações Estratégicas são diretrizes definidas pela liderança
 * que guiam quais tipos de ideias e projetos são prioritários.
 *
 * Exemplo: "Reduzir custos operacionais em 15% até dezembro"
 *
 * ## Fluxo no Sistema
 *
 * 1. LÍDER cria orientações estratégicas
 * 2. OPERADORES veem as orientações e submetem ideias alinhadas
 * 3. GESTORES avaliam ideias considerando as orientações
 * 4. Projetos priorizados seguem as orientações
 *
 * @property id Identificador único
 * @property titulo Título resumido da orientação
 * @property descricao Descrição detalhada
 * @property categoria Categoria da orientação (custo, qualidade, etc.)
 * @property prioridade Nível de prioridade (1 = mais alta)
 * @property ativa Se a orientação ainda está vigente
 * @property criadoPor ID do líder que criou
 * @property dataCriacao Timestamp de criação
 * @property dataExpiracao Quando a orientação expira (opcional)
 */
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

/**
 * Categorias de orientações estratégicas.
 *
 * Ajudam a classificar e filtrar orientações por objetivo.
 */
enum class CategoriaOrientacao {
    /** Foco em redução de custos */
    REDUCAO_CUSTOS,
    
    /** Melhoria de qualidade de serviço */
    QUALIDADE_SERVICO,
    
    /** Inovação tecnológica */
    INOVACAO_TECNOLOGICA,
    
    /** Sustentabilidade e meio ambiente */
    SUSTENTABILIDADE,
    
    /** Segurança do trabalho */
    SEGURANCA,
    
    /** Experiência do cliente */
    EXPERIENCIA_CLIENTE,
    
    /** Eficiência operacional */
    EFICIENCIA_OPERACIONAL
}
