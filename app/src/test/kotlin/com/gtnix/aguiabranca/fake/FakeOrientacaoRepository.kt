package com.gtnix.aguiabranca.fake

import com.gtnix.aguiabranca.domain.model.CategoriaOrientacao
import com.gtnix.aguiabranca.domain.model.OrientacaoEstrategica
import com.gtnix.aguiabranca.domain.repository.OrientacaoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeOrientacaoRepository(
    initialOrientacoes: List<OrientacaoEstrategica> = emptyList()
) : OrientacaoRepository {

    private val orientacoes = MutableStateFlow(initialOrientacoes.toList())

    override fun listarAtivas(): Flow<List<OrientacaoEstrategica>> =
        orientacoes.map { list -> list.filter { it.ativa } }

    override fun listarTodas(): Flow<List<OrientacaoEstrategica>> = orientacoes

    override fun listarPorCategoria(categoria: CategoriaOrientacao): Flow<List<OrientacaoEstrategica>> =
        orientacoes.map { list -> list.filter { it.categoria == categoria } }

    override suspend fun buscarPorId(id: String): OrientacaoEstrategica? =
        orientacoes.value.find { it.id == id }

    override suspend fun salvar(orientacao: OrientacaoEstrategica) {
        val current = orientacoes.value.toMutableList()
        val index = current.indexOfFirst { it.id == orientacao.id }
        if (index >= 0) {
            current[index] = orientacao
        } else {
            current.add(orientacao)
        }
        orientacoes.value = current
    }

    override suspend fun desativar(id: String) {
        orientacoes.value = orientacoes.value.map { orientacao ->
            if (orientacao.id == id) orientacao.copy(ativa = false) else orientacao
        }
    }

    override suspend fun ativar(id: String) {
        orientacoes.value = orientacoes.value.map { orientacao ->
            if (orientacao.id == id) orientacao.copy(ativa = true) else orientacao
        }
    }

    override suspend fun excluir(id: String) {
        orientacoes.value = orientacoes.value.filterNot { it.id == id }
    }
}
