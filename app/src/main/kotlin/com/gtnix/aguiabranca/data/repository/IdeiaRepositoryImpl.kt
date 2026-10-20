package com.gtnix.aguiabranca.data.repository

import com.gtnix.aguiabranca.data.remote.NetworkMonitor
import com.gtnix.aguiabranca.data.remote.api.AiApiService
import com.gtnix.aguiabranca.data.remote.api.IdeiaApiService
import com.gtnix.aguiabranca.data.remote.dto.AvaliacaoRequestDto
import com.gtnix.aguiabranca.data.remote.dto.EstimativaRequestDto
import com.gtnix.aguiabranca.data.remote.dto.FeedbackUtilRequestDto
import com.gtnix.aguiabranca.data.remote.dto.PrioridadeRequestDto
import com.gtnix.aguiabranca.data.remote.dto.RascunhoFeedbackRequestDto
import com.gtnix.aguiabranca.data.remote.dto.ScoreIdeasRequestDto
import com.gtnix.aguiabranca.data.remote.dto.toDomain
import com.gtnix.aguiabranca.data.remote.dto.toRequestDto
import com.gtnix.aguiabranca.domain.model.AreaAtuacao
import com.gtnix.aguiabranca.domain.model.Ideia
import com.gtnix.aguiabranca.domain.model.RascunhoFeedback
import com.gtnix.aguiabranca.domain.model.ScoreIdeiasResult
import com.gtnix.aguiabranca.domain.model.StatusIdeia
import com.gtnix.aguiabranca.domain.repository.IdeiaRepository
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
class IdeiaRepositoryImpl @Inject constructor(
    private val api: IdeiaApiService,
    private val aiApi: AiApiService,
    private val networkMonitor: NetworkMonitor
) : IdeiaRepository {

    private val items = MutableStateFlow<List<Ideia>>(emptyList())

    override fun listarTodas(): Flow<List<Ideia>> = flow {
        refresh()
        emitAll(items)
    }

    override fun listarPorAutor(autorId: String): Flow<List<Ideia>> = flow {
        refresh()
        emitAll(items.map { list -> list.filter { it.autorId == autorId } })
    }

    override fun listarPorArea(area: AreaAtuacao): Flow<List<Ideia>> = flow {
        refresh()
        emitAll(items.map { list -> list.filter { it.area == area } })
    }

    override fun listarPorStatus(status: StatusIdeia): Flow<List<Ideia>> = flow {
        refresh()
        emitAll(items.map { list -> list.filter { it.status == status } })
    }

    override fun listarPorOrientacao(orientacaoId: String): Flow<List<Ideia>> = flow {
        refresh()
        emitAll(items.map { list -> list.filter { it.orientacaoId == orientacaoId } })
    }

    override suspend fun buscarPorId(id: String): Ideia? {
        ensureOnline()
        return try {
            api.obter(id).toDomain()
        } catch (e: HttpException) {
            if (e.code() == 404) null else throw e
        }
    }

    override suspend fun salvar(ideia: Ideia) {
        ensureOnline()
        val request = ideia.toRequestDto()
        if (ideia.id.isBlank()) {
            api.criar(request)
        } else {
            api.atualizar(ideia.id, request)
        }
        refresh()
    }

    override suspend fun atualizarStatus(
        id: String,
        novoStatus: StatusIdeia,
        feedback: String?,
        concordaIa: Boolean?,
        motivoDiscordanciaIa: String?
    ) {
        if (novoStatus != StatusIdeia.APROVADA && novoStatus != StatusIdeia.REPROVADA) {
            return
        }
        ensureOnline()
        api.avaliar(
            id,
            AvaliacaoRequestDto(
                status = novoStatus.name,
                feedback = feedback,
                concordaIa = concordaIa,
                motivoDiscordanciaIa = motivoDiscordanciaIa?.takeIf { it.isNotBlank() }
            )
        )
        refresh()
    }

    override suspend fun priorizar(id: String, prioridade: Int) {
        ensureOnline()
        api.priorizar(id, PrioridadeRequestDto(prioridade = prioridade))
        refresh()
    }

    override suspend fun salvarEstimativa(
        id: String,
        impacto: Int,
        esforco: Int,
        motivoAjuste: String?
    ): Ideia {
        ensureOnline()
        val atualizada = api.salvarEstimativa(
            id,
            EstimativaRequestDto(
                impacto = impacto,
                esforco = esforco,
                motivoAjuste = motivoAjuste?.takeIf { it.isNotBlank() }
            )
        ).toDomain()
        refresh()
        return atualizada
    }

    override suspend fun gerarRascunhoFeedback(id: String, observacoes: String?): RascunhoFeedback {
        ensureOnline()
        return aiApi.rascunhoFeedback(
            id,
            RascunhoFeedbackRequestDto(observacoes?.takeIf { it.isNotBlank() })
        ).toDomain()
    }

    override suspend fun scoreIdeias(orientacaoId: String, ideiaIds: List<String>): ScoreIdeiasResult {
        ensureOnline()
        val result = aiApi.scoreIdeas(
            ScoreIdeasRequestDto(orientacaoId = orientacaoId, ideiaIds = ideiaIds)
        ).toDomain()
        refresh()
        return result
    }

    override suspend fun vincularProjeto(ideiaId: String, projetoId: String) {
        // Conversão ideia → projeto é feita no POST /projetos do backend.
    }

    override suspend fun excluir(id: String) {
        ensureOnline()
        val response = api.excluir(id)
        if (!response.isSuccessful) {
            throw HttpException(response)
        }
        refresh()
    }

    override suspend fun contarPorStatus(): Map<StatusIdeia, Int> {
        ensureOnline()
        refresh()
        return items.value.groupingBy { it.status }.eachCount()
    }

    override suspend fun incrementUpvote(id: String) {
        ensureOnline()
        api.apoiar(id)
        refresh()
    }

    override suspend fun marcarFeedbackUtil(id: String, util: Boolean): Ideia {
        ensureOnline()
        val atualizada = api.marcarFeedbackUtil(id, FeedbackUtilRequestDto(util)).toDomain()
        refresh()
        return atualizada
    }

    private suspend fun refresh(
        status: String? = null,
        area: String? = null,
        orientacaoId: String? = null
    ) {
        ensureOnline()
        items.value = api.listar(status = status, area = area, orientacaoId = orientacaoId)
            .map { it.toDomain() }
    }

    private fun ensureOnline() {
        if (!networkMonitor.isOnline()) {
            throw IOException("offline")
        }
    }
}
