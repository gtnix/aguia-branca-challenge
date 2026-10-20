package com.gtnix.aguiabranca.domain.usecase.ideia

import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.domain.repository.IdeiaRepository
import com.gtnix.aguiabranca.domain.session.SessionManager
import com.gtnix.aguiabranca.domain.util.Result
import com.gtnix.aguiabranca.domain.util.mutationFailure
import javax.inject.Inject

class PriorizarIdeiaUseCase @Inject constructor(
    private val ideiaRepository: IdeiaRepository,
    private val sessionManager: SessionManager
) {
    suspend operator fun invoke(ideiaId: String, prioridade: Int): Result<Unit> {
        val user = sessionManager.getCurrentUser()
            ?: return Result.Error(Exception("Usuário não logado"))

        if (user.perfil != PerfilUsuario.GESTOR) {
            return Result.Error(Exception("Sem permissão para priorizar ideias"))
        }

        if (prioridade !in 1..100) {
            return Result.Error(Exception("Prioridade deve estar entre 1 e 100"))
        }

        return try {
            ideiaRepository.priorizar(ideiaId, prioridade)
            Result.Success(Unit)
        } catch (e: Exception) {
            mutationFailure(e, "Sem permissão para priorizar ideias")
        }
    }
}
