package com.gtnix.aguiabranca.domain.repository

import com.gtnix.aguiabranca.domain.model.CategoriaOrientacao
import com.gtnix.aguiabranca.domain.model.OrientacaoEstrategica
import kotlinx.coroutines.flow.Flow

interface OrientacaoRepository {
    fun listarAtivas(): Flow<List<OrientacaoEstrategica>>
    fun listarTodas(): Flow<List<OrientacaoEstrategica>>
    fun listarPorCategoria(categoria: CategoriaOrientacao): Flow<List<OrientacaoEstrategica>>
    suspend fun buscarPorId(id: String): OrientacaoEstrategica?
    suspend fun salvar(orientacao: OrientacaoEstrategica)
    suspend fun desativar(id: String)
    suspend fun ativar(id: String)
    suspend fun excluir(id: String)
}
