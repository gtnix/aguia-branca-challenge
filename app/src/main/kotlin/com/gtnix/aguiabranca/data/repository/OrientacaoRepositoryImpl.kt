package com.gtnix.aguiabranca.data.repository

import com.gtnix.aguiabranca.data.remote.NetworkMonitor
import com.gtnix.aguiabranca.data.remote.api.OrientacaoApiService
import com.gtnix.aguiabranca.data.remote.dto.toDomain
import com.gtnix.aguiabranca.data.remote.dto.toRequestDto
import com.gtnix.aguiabranca.domain.model.CategoriaOrientacao
import com.gtnix.aguiabranca.domain.model.OrientacaoEstrategica
import com.gtnix.aguiabranca.domain.model.VencedorCampanha
import com.gtnix.aguiabranca.domain.repository.OrientacaoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrientacaoRepositoryImpl @Inject constructor(
    private val api: OrientacaoApiService,
    private val networkMonitor: NetworkMonitor
) : OrientacaoRepository {

    private val items = MutableStateFlow<List<OrientacaoEstrategica>>(emptyList())

    override fun listarAtivas(): Flow<List<OrientacaoEstrategica>> = flow {
        refresh(apenasAtivas = true)
        emitAll(items.map { list -> list.filter { it.ativa } })
    }

    override fun listarTodas(): Flow<List<OrientacaoEstrategica>> = flow {
        refresh(apenasAtivas = false)
        emitAll(items)
    }

    override fun listarPorCategoria(categoria: CategoriaOrientacao): Flow<List<OrientacaoEstrategica>> = flow {
        refresh(apenasAtivas = true)
        emitAll(items.map { list -> list.filter { it.ativa && it.categoria == categoria } })
    }

    override suspend fun buscarPorId(id: String): OrientacaoEstrategica? {
        ensureOnline()
        return try {
            api.obter(id).toDomain()
        } catch (e: HttpException) {
            if (e.code() == 404) null else throw e
        }
    }

    override suspend fun salvar(orientacao: OrientacaoEstrategica) {
        ensureOnline()
        val request = orientacao.toRequestDto()
        if (orientacao.id.isBlank()) {
            api.criar(request)
        } else {
            api.atualizar(orientacao.id, request)
        }
        refresh(apenasAtivas = false)
    }

    override suspend fun desativar(id: String) {
        ensureOnline()
        api.definirAtivo(id, false)
        refresh(apenasAtivas = false)
    }

    override suspend fun ativar(id: String) {
        ensureOnline()
        api.definirAtivo(id, true)
        refresh(apenasAtivas = false)
    }

    override suspend fun excluir(id: String) {
        ensureOnline()
        val response = api.excluir(id)
        if (!response.isSuccessful) {
            throw HttpException(response)
        }
        refresh(apenasAtivas = false)
    }

    override suspend fun encerrarCampanha(id: String): List<VencedorCampanha> {
        ensureOnline()
        val vencedores = api.encerrarCampanha(id).vencedores.map { it.toDomain() }
        refresh(apenasAtivas = false)
        return vencedores
    }

    private suspend fun refresh(apenasAtivas: Boolean) {
        ensureOnline()
        items.value = api.listar(apenasAtivas = apenasAtivas).map { it.toDomain() }
    }

    private fun ensureOnline() {
        if (!networkMonitor.isOnline()) {
            throw IOException("offline")
        }
    }
}
