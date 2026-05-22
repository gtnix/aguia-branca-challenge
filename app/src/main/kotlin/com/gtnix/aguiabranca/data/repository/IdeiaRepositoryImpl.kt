package com.gtnix.aguiabranca.data.repository

import com.gtnix.aguiabranca.data.local.dao.IdeiaDao
import com.gtnix.aguiabranca.data.mapper.IdeiaMapper
import com.gtnix.aguiabranca.domain.model.AreaAtuacao
import com.gtnix.aguiabranca.domain.model.Ideia
import com.gtnix.aguiabranca.domain.model.StatusIdeia
import com.gtnix.aguiabranca.domain.repository.IdeiaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Implementação do IdeiaRepository
 */
class IdeiaRepositoryImpl @Inject constructor(
    private val dao: IdeiaDao,
    private val mapper: IdeiaMapper
) : IdeiaRepository {

    override fun listarTodas(): Flow<List<Ideia>> {
        return dao.listarTodas().map { mapper.toDomainList(it) }
    }

    override fun listarPorAutor(autorId: String): Flow<List<Ideia>> {
        return dao.listarPorAutor(autorId).map { mapper.toDomainList(it) }
    }

    override fun listarPorArea(area: AreaAtuacao): Flow<List<Ideia>> {
        return dao.listarPorArea(area.name).map { mapper.toDomainList(it) }
    }

    override fun listarPorStatus(status: StatusIdeia): Flow<List<Ideia>> {
        return dao.listarPorStatus(status.name).map { mapper.toDomainList(it) }
    }

    override fun listarPorOrientacao(orientacaoId: String): Flow<List<Ideia>> {
        return dao.listarPorOrientacao(orientacaoId).map { mapper.toDomainList(it) }
    }

    override suspend fun buscarPorId(id: String): Ideia? {
        return dao.buscarPorId(id)?.let { mapper.toDomain(it) }
    }

    override suspend fun salvar(ideia: Ideia) {
        dao.inserir(mapper.toEntity(ideia))
    }

    override suspend fun atualizarStatus(
        id: String,
        novoStatus: StatusIdeia,
        feedback: String?
    ) {
        dao.atualizarStatus(
            id = id,
            novoStatus = novoStatus.name,
            feedback = feedback,
            dataAvaliacao = System.currentTimeMillis()
        )
    }

    override suspend fun vincularProjeto(ideiaId: String, projetoId: String) {
        dao.vincularProjeto(ideiaId, projetoId)
    }

    override suspend fun excluir(id: String) {
        dao.excluirPorId(id)
    }

    override suspend fun contarPorStatus(): Map<StatusIdeia, Int> {
        return StatusIdeia.entries.associateWith { status ->
            dao.contarPorStatus(status.name)
        }
    }
}
