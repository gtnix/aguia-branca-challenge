package com.gtnix.aguiabranca.domain.repository

import com.gtnix.aguiabranca.domain.model.CategoriaOrientacao
import com.gtnix.aguiabranca.domain.model.OrientacaoEstrategica
import kotlinx.coroutines.flow.Flow

/**
 * Interface do Repositório de Orientações Estratégicas.
 *
 * ## Conceito de Negócio
 *
 * Orientações são criadas pela liderança e consumidas por:
 * - OPERADORES: Veem para alinhar suas ideias
 * - GESTORES: Usam como critério de avaliação
 * - DASHBOARD: Mostra alinhamento ideias/orientações
 */
interface OrientacaoRepository {

    /**
     * Lista todas as orientações ativas.
     * Flow emite quando houver mudanças.
     */
    fun listarAtivas(): Flow<List<OrientacaoEstrategica>>

    /**
     * Lista todas as orientações (ativas e inativas).
     */
    fun listarTodas(): Flow<List<OrientacaoEstrategica>>

    /**
     * Filtra orientações por categoria.
     */
    fun listarPorCategoria(categoria: CategoriaOrientacao): Flow<List<OrientacaoEstrategica>>

    /**
     * Busca orientação por ID.
     */
    suspend fun buscarPorId(id: String): OrientacaoEstrategica?

    /**
     * Cria ou atualiza uma orientação.
     * Apenas LÍDER pode executar.
     */
    suspend fun salvar(orientacao: OrientacaoEstrategica)

    /**
     * Desativa uma orientação (soft delete).
     */
    suspend fun desativar(id: String)

    /**
     * Remove permanentemente uma orientação.
     */
    suspend fun excluir(id: String)
}
