package com.gtnix.aguiabranca.domain.usecase.ideia

import com.gtnix.aguiabranca.domain.model.Ideia
import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.domain.repository.IdeiaRepository
import com.gtnix.aguiabranca.domain.session.SessionManager
import com.gtnix.aguiabranca.domain.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

/**
 * Obtém a lista de ideias filtrada pelo perfil do usuário logado.
 *
 * Regras de visibilidade por perfil (RBAC):
 * - OPERADOR: vê apenas suas próprias ideias
 * - GESTOR: vê ideias da sua área de atuação
 * - LIDER: vê todas as ideias (visão consolidada)
 */
class GetIdeiasUseCase @Inject constructor(
    private val ideiaRepository: IdeiaRepository,
    private val sessionManager: SessionManager
) {
    operator fun invoke(): Flow<Result<List<Ideia>>> {
        return combine(
            ideiaRepository.listarTodas(),
            sessionManager.currentUserFlow
        ) { ideias, user ->
            if (user == null) {
                return@combine Result.Error(Exception("Usuário não logado"))
            }

            val filtered = when (user.perfil) {
                PerfilUsuario.OPERADOR -> ideias.filter { it.autorId == user.id }
                PerfilUsuario.GESTOR -> ideias.filter { it.area == user.area }
                PerfilUsuario.LIDER -> ideias
            }
            Result.Success(filtered) as Result<List<Ideia>>
        }.catch { e ->
            emit(Result.Error(Exception(e)))
        }
    }
}
