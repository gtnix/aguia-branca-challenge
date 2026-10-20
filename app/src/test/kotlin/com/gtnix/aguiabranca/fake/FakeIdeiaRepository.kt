package com.gtnix.aguiabranca.fake

import com.gtnix.aguiabranca.domain.model.AnaliseIa
import com.gtnix.aguiabranca.domain.model.AreaAtuacao
import com.gtnix.aguiabranca.domain.model.FaixaIa
import com.gtnix.aguiabranca.domain.model.Ideia
import com.gtnix.aguiabranca.domain.model.OrigemEstimativa
import com.gtnix.aguiabranca.domain.model.RankingIdeia
import com.gtnix.aguiabranca.domain.model.RascunhoFeedback
import com.gtnix.aguiabranca.domain.model.ScoreIdeiasResult
import com.gtnix.aguiabranca.domain.model.StatusAnaliseIa
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

    var lastConcordancia: Pair<Boolean?, String?>? = null
        private set

    /** Tamanho e ids de cada chamada a [scoreIdeias], para conferir o fatiamento em lotes. */
    val scoreCalls = mutableListOf<Pair<Int, List<String>>>()

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
        feedback: String?,
        concordaIa: Boolean?,
        motivoDiscordanciaIa: String?
    ) {
        lastStatusUpdate = Triple(id, novoStatus, feedback)
        lastConcordancia = concordaIa to motivoDiscordanciaIa
        ideias.value = ideias.value.map { ideia ->
            if (ideia.id == id) {
                ideia.copy(
                    status = novoStatus,
                    feedback = feedback,
                    dataAvaliacao = System.currentTimeMillis(),
                    analiseIa = ideia.analiseIa?.copy(
                        concordaIa = concordaIa,
                        motivoDiscordanciaIa = motivoDiscordanciaIa
                    )
                )
            } else {
                ideia
            }
        }
    }

    override suspend fun priorizar(id: String, prioridade: Int) {
        ideias.value = ideias.value.map { ideia ->
            if (ideia.id == id) ideia.copy(prioridade = prioridade) else ideia
        }
    }

    override suspend fun salvarEstimativa(
        id: String,
        impacto: Int,
        esforco: Int,
        motivoAjuste: String?
    ): Ideia {
        ideias.value = ideias.value.map { ideia ->
            if (ideia.id == id) {
                ideia.copy(
                    impactoEstimado = impacto,
                    esforcoEstimado = esforco,
                    analiseIa = (ideia.analiseIa ?: AnaliseIa(status = StatusAnaliseIa.ESTIMATIVA)).copy(
                        estimativaOrigem = OrigemEstimativa.GESTOR,
                        motivoAjusteEstimativa = motivoAjuste
                    )
                )
            } else {
                ideia
            }
        }
        return ideias.value.first { it.id == id }
    }

    /** Imita o fallback do backend: sem impacto/esforço não há score. */
    override suspend fun scoreIdeias(orientacaoId: String, ideiaIds: List<String>): ScoreIdeiasResult {
        scoreCalls += ideiaIds.size to ideiaIds
        val ranking = ideiaIds.mapNotNull { id ->
            val ideia = getById(id) ?: return@mapNotNull null
            if (ideia.impactoEstimado <= 0 || ideia.esforcoEstimado <= 0) {
                RankingIdeia(id, null, "Dados insuficientes", StatusAnaliseIa.DADOS_INSUFICIENTES)
            } else {
                val score = (ideia.impactoEstimado * 20 - ideia.esforcoEstimado * 5).coerceIn(10, 100)
                RankingIdeia(id, score, "Score heurístico de teste", StatusAnaliseIa.ESTIMATIVA)
            }
        }
        ideias.value = ideias.value.map { ideia ->
            val item = ranking.find { it.ideiaId == ideia.id }
            if (item == null) {
                ideia
            } else {
                ideia.copy(
                    scoreIa = item.score,
                    justificativaIa = item.justificativa,
                    scoreIaFallback = true,
                    analiseIa = AnaliseIa(
                        status = item.status,
                        faixa = item.score?.let { faixaDe(it) }
                    )
                )
            }
        }
        return ScoreIdeiasResult(ranking = ranking, modelo = "heuristic-local", fallback = true)
    }

    override suspend fun gerarRascunhoFeedback(id: String, observacoes: String?): RascunhoFeedback {
        val ideia = getById(id) ?: error("ideia inexistente")
        val texto = buildString {
            append("Obrigado por enviar \"${ideia.titulo}\".")
            observacoes?.takeIf { it.isNotBlank() }?.let { append(" $it") }
        }
        return RascunhoFeedback(texto = texto, fallback = true)
    }

    private fun faixaDe(score: Int): FaixaIa = when {
        score >= 70 -> FaixaIa.ALTA
        score >= 40 -> FaixaIa.MEDIA
        else -> FaixaIa.BAIXA
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

    override suspend fun marcarFeedbackUtil(id: String, util: Boolean): Ideia {
        ideias.value = ideias.value.map { ideia ->
            if (ideia.id == id) ideia.copy(feedbackUtil = util) else ideia
        }
        return ideias.value.first { it.id == id }
    }
}
