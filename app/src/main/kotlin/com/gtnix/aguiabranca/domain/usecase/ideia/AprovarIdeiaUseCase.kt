package com.gtnix.aguiabranca.domain.usecase.ideia

import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.domain.model.StatusIdeia
import com.gtnix.aguiabranca.domain.repository.IdeiaRepository
import com.gtnix.aguiabranca.domain.session.SessionManager
import com.gtnix.aguiabranca.domain.util.Result
import javax.inject.Inject

class AprovarIdeiaUseCase @Inject constructor(
    private val ideiaRepository: IdeiaRepository,
    private val sessionManager: SessionManager
) {
    suspend operator fun invoke(
        ideiaId: String,
        aprovada: Boolean,
        feedback: String? = null,
        impactoEstimado: Int? = null,
        esforcoEstimado: Int? = null
    ): Result<Unit> {
        val user = sessionManager.getCurrentUser()
            ?: return Result.Error(Exception("Usuário não logado"))

        if (user.perfil != PerfilUsuario.GESTOR && user.perfil != PerfilUsuario.LIDER) {
            return Result.Error(Exception("Sem permissão para avaliar ideias"))
        }

        if (!aprovada && feedback.isNullOrBlank()) {
            return Result.Error(Exception("Feedback obrigatório ao reprovar"))
        }

        return try {
            val status = if (aprovada) StatusIdeia.APROVADA else StatusIdeia.REPROVADA

            if (impactoEstimado != null || esforcoEstimado != null) {
                val ideia = ideiaRepository.buscarPorId(ideiaId)
                    ?: return Result.Error(Exception("Ideia não encontrada"))

                val atualizada = ideia.copy(
                    status = status,
                    feedback = feedback,
                    impactoEstimado = impactoEstimado ?: ideia.impactoEstimado,
                    esforcoEstimado = esforcoEstimado ?: ideia.esforcoEstimado,
                    dataAvaliacao = System.currentTimeMillis()
                )
                ideiaRepository.salvar(atualizada)
            } else {
                ideiaRepository.atualizarStatus(ideiaId, status, feedback)
            }

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
