package com.gtnix.aguiabranca.fake

import com.gtnix.aguiabranca.domain.model.AreaAtuacao
import com.gtnix.aguiabranca.domain.model.Projeto
import com.gtnix.aguiabranca.domain.model.StatusProjeto
import com.gtnix.aguiabranca.domain.repository.ProjetoEstatisticas
import com.gtnix.aguiabranca.domain.repository.ProjetoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeProjetoRepository(
    initialProjetos: List<Projeto> = emptyList()
) : ProjetoRepository {

    private val projetos = MutableStateFlow(initialProjetos.toList())

    override fun listarTodos(): Flow<List<Projeto>> = projetos

    override fun listarPorArea(area: AreaAtuacao): Flow<List<Projeto>> =
        projetos.map { list -> list.filter { it.area == area } }

    override fun listarPorStatus(status: StatusProjeto): Flow<List<Projeto>> =
        projetos.map { list -> list.filter { it.status == status } }

    override fun listarPorUsuario(usuarioId: String): Flow<List<Projeto>> =
        projetos.map { list ->
            list.filter { it.responsavelId == usuarioId || usuarioId in it.membrosIds }
        }

    override fun listarPorOrientacao(orientacaoId: String): Flow<List<Projeto>> =
        projetos.map { list -> list.filter { it.orientacaoId == orientacaoId } }

    override suspend fun buscarPorId(id: String): Projeto? =
        projetos.value.find { it.id == id }

    override suspend fun salvar(projeto: Projeto) {
        val current = projetos.value.toMutableList()
        val index = current.indexOfFirst { it.id == projeto.id }
        if (index >= 0) {
            current[index] = projeto
        } else {
            current.add(projeto)
        }
        projetos.value = current
    }

    override suspend fun atualizarStatus(id: String, novoStatus: StatusProjeto) {
        projetos.value = projetos.value.map { projeto ->
            if (projeto.id == id) projeto.copy(status = novoStatus) else projeto
        }
    }

    override suspend fun atualizarProgresso(id: String, progresso: Int) {
        projetos.value = projetos.value.map { projeto ->
            if (projeto.id == id) projeto.copy(progresso = progresso) else projeto
        }
    }

    override suspend fun adicionarMembro(projetoId: String, membroId: String) = Unit

    override suspend fun removerMembro(projetoId: String, membroId: String) = Unit

    override suspend fun excluir(id: String) {
        projetos.value = projetos.value.filterNot { it.id == id }
    }

    override suspend fun contarPorStatus(): Map<StatusProjeto, Int> =
        projetos.value.groupingBy { it.status }.eachCount()

    override suspend fun obterEstatisticas(): ProjetoEstatisticas {
        val all = projetos.value
        return ProjetoEstatisticas(
            totalProjetos = all.size,
            emAndamento = all.count { it.status == StatusProjeto.EM_ANDAMENTO },
            concluidos = all.count { it.status == StatusProjeto.CONCLUIDO },
            taxaConclusao = 0f,
            progressoMedio = all.map { it.progresso }.average().toFloat()
        )
    }
}
