package com.gtnix.aguiabranca.domain.usecase.ideia

import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.domain.model.ScoreIdeiasResult
import com.gtnix.aguiabranca.domain.repository.IdeiaRepository
import com.gtnix.aguiabranca.domain.session.SessionManager
import com.gtnix.aguiabranca.domain.util.Result
import com.gtnix.aguiabranca.domain.util.mutationFailure
import javax.inject.Inject

class ScoreIdeiasUseCase @Inject constructor(
    private val ideiaRepository: IdeiaRepository,
    private val sessionManager: SessionManager
) {
    suspend operator fun invoke(
        orientacaoId: String,
        ideiaIds: List<String>
    ): Result<ScoreIdeiasResult> {
        val user = sessionManager.getCurrentUser()
            ?: return Result.Error(Exception("Usuário não logado"))

        if (user.perfil != PerfilUsuario.GESTOR) {
            return Result.Error(Exception("Somente o Gestor pode pontuar ideias com IA"))
        }

        if (orientacaoId.isBlank() || ideiaIds.isEmpty()) {
            return Result.Error(Exception("Informe a orientação e ao menos uma ideia pendente"))
        }

        return try {
            val lotes = ideiaIds.distinct().chunked(MAX_IDEIAS_POR_LOTE)
                .map { lote -> ideiaRepository.scoreIdeias(orientacaoId, lote) }
            Result.Success(
                ScoreIdeiasResult(
                    ranking = lotes.flatMap { it.ranking },
                    modelo = lotes.last().modelo,
                    fallback = lotes.any { it.fallback }
                )
            )
        } catch (e: Exception) {
            mutationFailure(e, "Somente o Gestor pode pontuar ideias com IA")
        }
    }

    companion object {
        /** Espelha ScoreIdeasRequest.MAX_IDEIAS do backend, que rejeita lotes maiores com 400. */
        const val MAX_IDEIAS_POR_LOTE = 20
    }
}
