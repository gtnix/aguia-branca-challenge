package com.gtnix.aguiabranca.fake

import com.gtnix.aguiabranca.domain.model.AreaAtuacao
import com.gtnix.aguiabranca.domain.model.Ideia
import com.gtnix.aguiabranca.domain.model.StatusIdeia
import com.gtnix.aguiabranca.domain.repository.IdeiaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeIdeiaRepository(
    initialIdeias: List<Ideia> = emptyList()
) : IdeiaRepository {

    private val ideias = MutableStateFlow(initialIdeias.toList())

    var lastStatusUpdate: Triple<String, StatusIdeia, String?>? = null
        private set

    fun getById(id: String): Ideia? = ideias.value.find { it.id == id }

    override fun listarTodas(): Flow<List<Ideia>> = ideias

    override fun listarPorAutor(autorId: String): Flow<List<Ideia>> =
        ideias.map { list -> list.filter { it.autorId == autorId } }

    override fun listarPorArea(area: AreaAtuacao): Flow<List<Ideia>> =
        ideias.map { list -> list.filter { it.area == area } }

    override fun listarPorStatus(status: StatusIdeia): Flow<List<Ideia>> =
        ideias.map { list -> list.filter { it.status == status } }

    override fun listarPorOrientacao(orientacaoId: String): Flow<List<Ideia>> =
        ideias.map { list -> list.filter { it.orientacaoId == orientacaoId } }

    override suspend fun buscarPorId(id: String): Ideia? = getById(id)

    override suspend fun salvar(ideia: Ideia) {
        val current = ideias.value.toMutableList()
        val index = current.indexOfFirst { it.id == ideia.id }
        if (index >= 0) {
            current[index] = ideia
        } else {
            current.add(ideia)
        }
        ideias.value = current
    }

    override suspend fun atualizarStatus(
        id: String,
        novoStatus: StatusIdeia,
        feedback: String?
    ) {
        lastStatusUpdate = Triple(id, novoStatus, feedback)
        ideias.value = ideias.value.map { ideia ->
            if (ideia.id == id) {
                ideia.copy(
                    status = novoStatus,
                    feedback = feedback,
                    dataAvaliacao = System.currentTimeMillis()
                )
            } else {
                ideia
            }
        }
    }

    override suspend fun vincularProjeto(ideiaId: String, projetoId: String) {
        ideias.value = ideias.value.map { ideia ->
            if (ideia.id == ideiaId) {
                ideia.copy(
                    projetoId = projetoId,
                    status = StatusIdeia.CONVERTIDA_PROJETO
                )
            } else {
                ideia
            }
        }
    }

    override suspend fun excluir(id: String) {
        ideias.value = ideias.value.filterNot { it.id == id }
    }

    override suspend fun contarPorStatus(): Map<StatusIdeia, Int> =
        ideias.value.groupingBy { it.status }.eachCount()

    override suspend fun incrementUpvote(id: String) {
        ideias.value = ideias.value.map { ideia ->
            if (ideia.id == id) ideia.copy(upvotes = ideia.upvotes + 1) else ideia
        }
    }
}
