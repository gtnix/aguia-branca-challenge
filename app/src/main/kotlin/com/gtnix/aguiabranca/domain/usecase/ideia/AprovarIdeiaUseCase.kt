package com.gtnix.aguiabranca.domain.usecase.ideia

import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.domain.model.StatusIdeia
import com.gtnix.aguiabranca.domain.repository.IdeiaRepository
import com.gtnix.aguiabranca.domain.session.SessionManager
import com.gtnix.aguiabranca.domain.util.Result
import com.gtnix.aguiabranca.domain.util.mutationFailure
import javax.inject.Inject

class AprovarIdeiaUseCase @Inject constructor(
    private val ideiaRepository: IdeiaRepository,
    private val sessionManager: SessionManager
) {
    suspend operator fun invoke(
        ideiaId: String,
        aprovada: Boolean,
        feedback: String? = null,
        concordaIa: Boolean? = null,
        motivoDiscordanciaIa: String? = null
    ): Result<Unit> {
        val user = sessionManager.getCurrentUser()
            ?: return Result.Error(Exception("Usuário não logado"))

        if (user.perfil != PerfilUsuario.GESTOR) {
            return Result.Error(Exception("Sem permissão para avaliar ideias"))
        }

        if (!aprovada && feedback.isNullOrBlank()) {
            return Result.Error(Exception("Feedback obrigatório ao reprovar"))
        }

        return try {
            val status = if (aprovada) StatusIdeia.APROVADA else StatusIdeia.REPROVADA
            ideiaRepository.atualizarStatus(
                ideiaId,
                status,
                feedback,
                concordaIa,
                motivoDiscordanciaIa.takeIf { concordaIa == false }
            )
            Result.Success(Unit)
        } catch (e: Exception) {
            mutationFailure(e, "Sem permissão para avaliar ideias")
        }
    }
}
