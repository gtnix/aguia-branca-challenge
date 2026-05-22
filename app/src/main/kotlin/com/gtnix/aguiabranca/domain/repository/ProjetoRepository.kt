package com.gtnix.aguiabranca.domain.repository

import com.gtnix.aguiabranca.domain.model.AreaAtuacao
import com.gtnix.aguiabranca.domain.model.Projeto
import com.gtnix.aguiabranca.domain.model.StatusProjeto
import kotlinx.coroutines.flow.Flow

/**
 * Interface do Repositório de Projetos.
 *
 * ## Conceito de Negócio
 *
 * Projetos são iniciativas formais que surgiram de ideias aprovadas.
 * Possuem equipe, prazos e acompanhamento de progresso.
 */
interface ProjetoRepository {

    /**
     * Lista todos os projetos.
     */
    fun listarTodos(): Flow<List<Projeto>>

    /**
     * Lista projetos por área.
     * Usado por GESTOR para ver projetos da sua área.
     */
    fun listarPorArea(area: AreaAtuacao): Flow<List<Projeto>>

    /**
     * Lista projetos por status.
     * Ex: todos os projetos EM_ANDAMENTO.
     */
    fun listarPorStatus(status: StatusProjeto): Flow<List<Projeto>>

    /**
     * Lista projetos onde o usuário é responsável ou membro.
     */
    fun listarPorUsuario(usuarioId: String): Flow<List<Projeto>>

    /**
     * Lista projetos alinhados a uma orientação estratégica.
     */
    fun listarPorOrientacao(orientacaoId: String): Flow<List<Projeto>>

    /**
     * Busca projeto por ID.
     */
    suspend fun buscarPorId(id: String): Projeto?

    /**
     * Cria ou atualiza um projeto.
     */
    suspend fun salvar(projeto: Projeto)

    /**
     * Atualiza o status do projeto.
     */
    suspend fun atualizarStatus(id: String, novoStatus: StatusProjeto)

    /**
     * Atualiza o progresso do projeto.
     * @param progresso Valor entre 0 e 100
     */
    suspend fun atualizarProgresso(id: String, progresso: Int)

    /**
     * Adiciona membro à equipe do projeto.
     */
    suspend fun adicionarMembro(projetoId: String, membroId: String)

    /**
     * Remove membro da equipe do projeto.
     */
    suspend fun removerMembro(projetoId: String, membroId: String)

    /**
     * Remove um projeto.
     */
    suspend fun excluir(id: String)

    /**
     * Conta projetos por status para dashboard.
     */
    suspend fun contarPorStatus(): Map<StatusProjeto, Int>

    /**
     * Calcula estatísticas de projetos para dashboard.
     */
    suspend fun obterEstatisticas(): ProjetoEstatisticas
}

/**
 * Estatísticas de projetos para o dashboard.
 */
data class ProjetoEstatisticas(
    val totalProjetos: Int,
    val emAndamento: Int,
    val concluidos: Int,
    val taxaConclusao: Float,
    val progressoMedio: Float
)
