package com.gtnix.aguiabranca.data.repository

import com.gtnix.aguiabranca.data.remote.NetworkMonitor
import com.gtnix.aguiabranca.data.remote.api.ProjetoApiService
import com.gtnix.aguiabranca.data.remote.dto.ProgressoRequestDto
import com.gtnix.aguiabranca.data.remote.dto.toDomain
import com.gtnix.aguiabranca.data.remote.dto.toRequestDto
import com.gtnix.aguiabranca.domain.model.AreaAtuacao
import com.gtnix.aguiabranca.domain.model.Projeto
import com.gtnix.aguiabranca.domain.model.StatusProjeto
import com.gtnix.aguiabranca.domain.repository.ProjetoEstatisticas
import com.gtnix.aguiabranca.domain.repository.ProjetoRepository
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
class ProjetoRepositoryImpl @Inject constructor(
    private val api: ProjetoApiService,
    private val networkMonitor: NetworkMonitor
) : ProjetoRepository {

    private val items = MutableStateFlow<List<Projeto>>(emptyList())

    override fun listarTodos(): Flow<List<Projeto>> = flow {
        refresh()
        emitAll(items)
    }

    override fun listarPorArea(area: AreaAtuacao): Flow<List<Projeto>> = flow {
        refresh()
        emitAll(items.map { list -> list.filter { it.area == area } })
    }

    override fun listarPorStatus(status: StatusProjeto): Flow<List<Projeto>> = flow {
        refresh()
        emitAll(items.map { list -> list.filter { it.status == status } })
    }

    override fun listarPorUsuario(usuarioId: String): Flow<List<Projeto>> = flow {
        refresh()
        emitAll(items.map { list ->
            list.filter { it.responsavelId == usuarioId || usuarioId in it.membrosIds }
        })
    }

    override fun listarPorOrientacao(orientacaoId: String): Flow<List<Projeto>> = flow {
        refresh()
        emitAll(items.map { list -> list.filter { it.orientacaoId == orientacaoId } })
    }

    override suspend fun buscarPorId(id: String): Projeto? {
        ensureOnline()
        return try {
            api.obter(id).toDomain()
        } catch (e: HttpException) {
            if (e.code() == 404) null else throw e
        }
    }

    override suspend fun salvar(projeto: Projeto) {
        ensureOnline()
        val request = projeto.toRequestDto()
        if (projeto.id.isBlank()) {
            api.criar(request)
        } else {
            api.atualizar(projeto.id, request)
        }
        refresh()
    }

    override suspend fun atualizarStatus(id: String, novoStatus: StatusProjeto) {
        ensureOnline()
        api.atualizarProgresso(id, ProgressoRequestDto(status = novoStatus.name))
        refresh()
    }

    override suspend fun atualizarProgresso(id: String, progresso: Int) {
        ensureOnline()
        api.atualizarProgresso(
            id,
            ProgressoRequestDto(progressoPercentual = progresso.coerceIn(0, 100))
        )
        refresh()
    }

    override suspend fun adicionarMembro(projetoId: String, membroId: String) {
        // Equipe permanece local à Sprint 1; sem endpoint na API desta fatia.
    }

    override suspend fun removerMembro(projetoId: String, membroId: String) {
        // Equipe permanece local à Sprint 1; sem endpoint na API desta fatia.
    }

    override suspend fun excluir(id: String) {
        ensureOnline()
        val response = api.excluir(id)
        if (!response.isSuccessful) {
            throw HttpException(response)
        }
        refresh()
    }

    override suspend fun contarPorStatus(): Map<StatusProjeto, Int> {
        ensureOnline()
        refresh()
        return items.value.groupingBy { it.status }.eachCount()
    }

    override suspend fun obterEstatisticas(): ProjetoEstatisticas {
        ensureOnline()
        refresh()
        val all = items.value
        val total = all.size
        val emAndamento = all.count { it.status == StatusProjeto.EM_ANDAMENTO }
        val concluidos = all.count { it.status == StatusProjeto.CONCLUIDO }
        return ProjetoEstatisticas(
            totalProjetos = total,
            emAndamento = emAndamento,
            concluidos = concluidos,
            taxaConclusao = if (total > 0) concluidos.toFloat() / total else 0f,
            progressoMedio = if (emAndamento > 0) {
                all.filter { it.status == StatusProjeto.EM_ANDAMENTO }
                    .map { it.progresso }
                    .average()
                    .toFloat()
            } else {
                0f
            }
        )
    }

    private suspend fun refresh() {
        ensureOnline()
        items.value = try {
            api.listar().map { it.toDomain() }
        } catch (e: HttpException) {
            // Operador não possui acesso a projetos; para os fluxos agregados da Home,
            // a ausência autorizada de projetos equivale a uma coleção vazia.
            if (e.code() == 403) emptyList() else throw e
        }
    }

    private fun ensureOnline() {
        if (!networkMonitor.isOnline()) {
            throw IOException("offline")
        }
    }
}
