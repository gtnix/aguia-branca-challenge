package com.gtnix.aguiabranca.data.repository

import com.gtnix.aguiabranca.data.local.dao.ProjetoDao
import com.gtnix.aguiabranca.data.mapper.ProjetoMapper
import com.gtnix.aguiabranca.domain.model.AreaAtuacao
import com.gtnix.aguiabranca.domain.model.Projeto
import com.gtnix.aguiabranca.domain.model.StatusProjeto
import com.gtnix.aguiabranca.domain.repository.ProjetoEstatisticas
import com.gtnix.aguiabranca.domain.repository.ProjetoRepository
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProjetoRepositoryImpl @Inject constructor(
    private val dao: ProjetoDao,
    private val mapper: ProjetoMapper
) : ProjetoRepository {

    private val gson = Gson()

    override fun listarTodos(): Flow<List<Projeto>> {
        return dao.listarTodos().map { mapper.toDomainList(it) }
    }

    override fun listarPorArea(area: AreaAtuacao): Flow<List<Projeto>> {
        return dao.listarPorArea(area.name).map { mapper.toDomainList(it) }
    }

    override fun listarPorStatus(status: StatusProjeto): Flow<List<Projeto>> {
        return dao.listarPorStatus(status.name).map { mapper.toDomainList(it) }
    }

    override fun listarPorUsuario(usuarioId: String): Flow<List<Projeto>> {
        return dao.listarPorUsuario(usuarioId).map { mapper.toDomainList(it) }
    }

    override fun listarPorOrientacao(orientacaoId: String): Flow<List<Projeto>> {
        return dao.listarPorOrientacao(orientacaoId).map { mapper.toDomainList(it) }
    }

    override suspend fun buscarPorId(id: String): Projeto? {
        return dao.buscarPorId(id)?.let { mapper.toDomain(it) }
    }

    override suspend fun salvar(projeto: Projeto) {
        dao.inserir(mapper.toEntity(projeto))
    }

    override suspend fun atualizarStatus(id: String, novoStatus: StatusProjeto) {
        dao.atualizarStatus(id, novoStatus.name)
    }

    override suspend fun atualizarProgresso(id: String, progresso: Int) {
        dao.atualizarProgresso(id, progresso.coerceIn(0, 100))
    }

    override suspend fun adicionarMembro(projetoId: String, membroId: String) {
        val projeto = dao.buscarPorId(projetoId) ?: return
        val membros = jsonToList(projeto.membrosIds).toMutableList()
        if (membroId !in membros) {
            membros.add(membroId)
            dao.atualizarMembros(projetoId, gson.toJson(membros))
        }
    }

    override suspend fun removerMembro(projetoId: String, membroId: String) {
        val projeto = dao.buscarPorId(projetoId) ?: return
        val membros = jsonToList(projeto.membrosIds).toMutableList()
        membros.remove(membroId)
        dao.atualizarMembros(projetoId, gson.toJson(membros))
    }

    override suspend fun excluir(id: String) {
        dao.excluirPorId(id)
    }

    override suspend fun contarPorStatus(): Map<StatusProjeto, Int> {
        return StatusProjeto.entries.associateWith { status ->
            dao.contarPorStatus(status.name)
        }
    }

    override suspend fun obterEstatisticas(): ProjetoEstatisticas {
        val total = dao.contarTotal()
        val emAndamento = dao.contarPorStatus(StatusProjeto.EM_ANDAMENTO.name)
        val concluidos = dao.contarPorStatus(StatusProjeto.CONCLUIDO.name)
        val progressoMedio = dao.mediaProgressoEmAndamento() ?: 0f

        return ProjetoEstatisticas(
            totalProjetos = total,
            emAndamento = emAndamento,
            concluidos = concluidos,
            taxaConclusao = if (total > 0) concluidos.toFloat() / total else 0f,
            progressoMedio = progressoMedio
        )
    }

    private fun jsonToList(json: String): List<String> {
        if (json.isEmpty()) return emptyList()
        return try {
            gson.fromJson(json, Array<String>::class.java)?.toList() ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}
