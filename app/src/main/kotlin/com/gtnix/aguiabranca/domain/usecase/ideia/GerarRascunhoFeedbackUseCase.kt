package com.gtnix.aguiabranca.domain.usecase.ideia

import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.domain.model.RascunhoFeedback
import com.gtnix.aguiabranca.domain.repository.IdeiaRepository
import com.gtnix.aguiabranca.domain.session.SessionManager
import com.gtnix.aguiabranca.domain.util.Result
import com.gtnix.aguiabranca.domain.util.mutationFailure
import javax.inject.Inject

/** Gera um rascunho editável; a mensagem só é enviada quando o gestor reprova com o texto revisado. */
class GerarRascunhoFeedbackUseCase @Inject constructor(
    private val ideiaRepository: IdeiaRepository,
    private val sessionManager: SessionManager
) {
    suspend operator fun invoke(ideiaId: String, observacoes: String? = null): Result<RascunhoFeedback> {
        val user = sessionManager.getCurrentUser()
            ?: return Result.Error(Exception("Usuário não logado"))

        if (user.perfil != PerfilUsuario.GESTOR) {
            return Result.Error(Exception(SEM_PERMISSAO))
        }

        return try {
            Result.Success(ideiaRepository.gerarRascunhoFeedback(ideiaId, observacoes))
        } catch (e: Exception) {
            mutationFailure(e, SEM_PERMISSAO)
        }
    }

    private companion object {
        const val SEM_PERMISSAO = "Somente o Gestor gera rascunho de feedback"
    }
}
