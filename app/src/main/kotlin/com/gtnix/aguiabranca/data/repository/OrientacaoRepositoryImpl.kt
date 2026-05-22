package com.gtnix.aguiabranca.data.repository

import com.gtnix.aguiabranca.data.local.dao.OrientacaoDao
import com.gtnix.aguiabranca.data.mapper.OrientacaoMapper
import com.gtnix.aguiabranca.domain.model.CategoriaOrientacao
import com.gtnix.aguiabranca.domain.model.OrientacaoEstrategica
import com.gtnix.aguiabranca.domain.repository.OrientacaoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Implementação do OrientacaoRepository
 */
class OrientacaoRepositoryImpl @Inject constructor(
    private val dao: OrientacaoDao,
    private val mapper: OrientacaoMapper
) : OrientacaoRepository {

    override fun listarAtivas(): Flow<List<OrientacaoEstrategica>> {
        return dao.listarAtivas().map { mapper.toDomainList(it) }
    }

    override fun listarTodas(): Flow<List<OrientacaoEstrategica>> {
        return dao.listarTodas().map { mapper.toDomainList(it) }
    }

    override fun listarPorCategoria(categoria: CategoriaOrientacao): Flow<List<OrientacaoEstrategica>> {
        return dao.listarPorCategoria(categoria.name).map { mapper.toDomainList(it) }
    }

    override suspend fun buscarPorId(id: String): OrientacaoEstrategica? {
        return dao.buscarPorId(id)?.let { mapper.toDomain(it) }
    }

    override suspend fun salvar(orientacao: OrientacaoEstrategica) {
        dao.inserir(mapper.toEntity(orientacao))
    }

    override suspend fun desativar(id: String) {
        dao.desativar(id)
    }

    override suspend fun excluir(id: String) {
        dao.excluirPorId(id)
    }
}
