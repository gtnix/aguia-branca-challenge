package com.gtnix.aguiabranca.domain.repository

import com.gtnix.aguiabranca.domain.model.AreaAtuacao
import com.gtnix.aguiabranca.domain.model.Projeto
import com.gtnix.aguiabranca.domain.model.StatusProjeto
import kotlinx.coroutines.flow.Flow

interface ProjetoRepository {
    fun listarTodos(): Flow<List<Projeto>>
    fun listarPorArea(area: AreaAtuacao): Flow<List<Projeto>>
    fun listarPorStatus(status: StatusProjeto): Flow<List<Projeto>>
    fun listarPorUsuario(usuarioId: String): Flow<List<Projeto>>
    fun listarPorOrientacao(orientacaoId: String): Flow<List<Projeto>>
    suspend fun buscarPorId(id: String): Projeto?
    suspend fun salvar(projeto: Projeto)
    suspend fun atualizarStatus(id: String, novoStatus: StatusProjeto)
    suspend fun atualizarProgresso(id: String, progresso: Int)
    suspend fun adicionarMembro(projetoId: String, membroId: String)
    suspend fun removerMembro(projetoId: String, membroId: String)
    suspend fun excluir(id: String)
    suspend fun contarPorStatus(): Map<StatusProjeto, Int>
    suspend fun obterEstatisticas(): ProjetoEstatisticas
}

data class ProjetoEstatisticas(
    val totalProjetos: Int,
    val emAndamento: Int,
    val concluidos: Int,
    val taxaConclusao: Float,
    val progressoMedio: Float
)
