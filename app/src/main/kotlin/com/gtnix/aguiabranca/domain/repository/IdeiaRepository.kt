package com.gtnix.aguiabranca.domain.repository

import com.gtnix.aguiabranca.domain.model.AreaAtuacao
import com.gtnix.aguiabranca.domain.model.Ideia
import com.gtnix.aguiabranca.domain.model.StatusIdeia
import kotlinx.coroutines.flow.Flow

/**
 * Interface do Repositório de Ideias.
 *
 * ## Fluxo de Negócio
 *
 * 1. OPERADOR chama `salvar()` para submeter ideia
 * 2. GESTOR chama `listarPorArea()` para ver ideias da sua área
 * 3. GESTOR chama `atualizarStatus()` para aprovar/reprovar
 * 4. Se aprovada, GESTOR chama `vincularProjeto()` para converter em projeto
 */
interface IdeiaRepository {

    /**
     * Lista todas as ideias.
     */
    fun listarTodas(): Flow<List<Ideia>>

    /**
     * Lista ideias submetidas por um autor específico.
     * Usado na tela "Minhas Ideias".
     */
    fun listarPorAutor(autorId: String): Flow<List<Ideia>>

    /**
     * Lista ideias de uma área específica.
     * Usado por GESTOR para avaliar ideias da sua área.
     */
    fun listarPorArea(area: AreaAtuacao): Flow<List<Ideia>>

    /**
     * Lista ideias por status.
     * Ex: todas as ideias PENDENTES para avaliação.
     */
    fun listarPorStatus(status: StatusIdeia): Flow<List<Ideia>>

    /**
     * Lista ideias alinhadas a uma orientação estratégica.
     */
    fun listarPorOrientacao(orientacaoId: String): Flow<List<Ideia>>

    /**
     * Busca ideia por ID.
     */
    suspend fun buscarPorId(id: String): Ideia?

    /**
     * Cria ou atualiza uma ideia.
     */
    suspend fun salvar(ideia: Ideia)

    /**
     * Atualiza o status da ideia.
     * @param id ID da ideia
     * @param novoStatus Novo status
     * @param feedback Feedback do gestor (obrigatório se REPROVADA)
     */
    suspend fun atualizarStatus(
        id: String,
        novoStatus: StatusIdeia,
        feedback: String? = null
    )

    /**
     * Vincula a ideia a um projeto criado a partir dela.
     */
    suspend fun vincularProjeto(ideiaId: String, projetoId: String)

    /**
     * Remove uma ideia.
     * Apenas autor ou admin pode remover ideias PENDENTES.
     */
    suspend fun excluir(id: String)

    /**
     * Conta ideias por status para dashboard.
     */
    suspend fun contarPorStatus(): Map<StatusIdeia, Int>
}
