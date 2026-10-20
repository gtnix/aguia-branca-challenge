package com.gtnix.aguiabranca.domain.usecase.ideia

import com.gtnix.aguiabranca.domain.model.Ideia
import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.domain.repository.IdeiaRepository
import com.gtnix.aguiabranca.domain.session.SessionManager
import com.gtnix.aguiabranca.domain.util.Result
import com.gtnix.aguiabranca.domain.util.mutationFailure
import javax.inject.Inject

/** O gestor é quem define os valores oficiais de impacto e esforço; a sugestão da IA é só ponto de partida. */
class SalvarEstimativaUseCase @Inject constructor(
    private val ideiaRepository: IdeiaRepository,
    private val sessionManager: SessionManager
) {
    suspend operator fun invoke(
        ideiaId: String,
        impacto: Int,
        esforco: Int,
        motivoAjuste: String? = null
    ): Result<Ideia> {
        val user = sessionManager.getCurrentUser()
            ?: return Result.Error(Exception("Usuário não logado"))

        if (user.perfil != PerfilUsuario.GESTOR) {
            return Result.Error(Exception(SEM_PERMISSAO))
        }

        if (impacto !in 1..5 || esforco !in 1..5) {
            return Result.Error(Exception("Impacto e esforço devem estar entre 1 e 5"))
        }

        return try {
            Result.Success(ideiaRepository.salvarEstimativa(ideiaId, impacto, esforco, motivoAjuste))
        } catch (e: Exception) {
            mutationFailure(e, SEM_PERMISSAO)
        }
    }

    private companion object {
        const val SEM_PERMISSAO = "Somente o Gestor define impacto e esforço"
    }
}
