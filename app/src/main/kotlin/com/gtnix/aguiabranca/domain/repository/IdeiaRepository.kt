package com.gtnix.aguiabranca.domain.repository

import com.gtnix.aguiabranca.domain.model.AreaAtuacao
import com.gtnix.aguiabranca.domain.model.Ideia
import com.gtnix.aguiabranca.domain.model.RascunhoFeedback
import com.gtnix.aguiabranca.domain.model.ScoreIdeiasResult
import com.gtnix.aguiabranca.domain.model.StatusIdeia
import kotlinx.coroutines.flow.Flow

interface IdeiaRepository {
    fun listarTodas(): Flow<List<Ideia>>
    fun listarPorAutor(autorId: String): Flow<List<Ideia>>
    fun listarPorArea(area: AreaAtuacao): Flow<List<Ideia>>
    fun listarPorStatus(status: StatusIdeia): Flow<List<Ideia>>
    fun listarPorOrientacao(orientacaoId: String): Flow<List<Ideia>>
    suspend fun buscarPorId(id: String): Ideia?
    suspend fun salvar(ideia: Ideia)
    suspend fun atualizarStatus(
        id: String,
        novoStatus: StatusIdeia,
        feedback: String? = null,
        concordaIa: Boolean? = null,
        motivoDiscordanciaIa: String? = null
    )
    suspend fun priorizar(id: String, prioridade: Int)
    suspend fun salvarEstimativa(id: String, impacto: Int, esforco: Int, motivoAjuste: String?): Ideia
    suspend fun scoreIdeias(orientacaoId: String, ideiaIds: List<String>): ScoreIdeiasResult
    suspend fun gerarRascunhoFeedback(id: String, observacoes: String?): RascunhoFeedback
    suspend fun vincularProjeto(ideiaId: String, projetoId: String)
    suspend fun excluir(id: String)
    suspend fun contarPorStatus(): Map<StatusIdeia, Int>
    suspend fun incrementUpvote(id: String)
    suspend fun marcarFeedbackUtil(id: String, util: Boolean): Ideia
}
