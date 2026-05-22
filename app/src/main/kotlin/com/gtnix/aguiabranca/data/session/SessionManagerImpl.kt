package com.gtnix.aguiabranca.data.session

import com.gtnix.aguiabranca.domain.model.Usuario
import com.gtnix.aguiabranca.domain.session.SessionManager
import com.gtnix.aguiabranca.domain.session.UserSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementação reativa do [SessionManager] baseada em [MutableStateFlow].
 *
 * Mantém compatibilidade com o [UserSession] legado: ao fazer login/logout,
 * atualiza ambos para que ViewModels ainda não migrados continuem funcionando.
 */
@Singleton
class SessionManagerImpl @Inject constructor(
    private val userSession: UserSession
) : SessionManager {

    private val _currentUser = MutableStateFlow<Usuario?>(null)
    override val currentUserFlow: Flow<Usuario?> = _currentUser.asStateFlow()

    override suspend fun login(usuario: Usuario) {
        _currentUser.value = usuario

        userSession.perfil = usuario.perfil
        userSession.userId = usuario.id
        userSession.userName = usuario.nome
        userSession.area = usuario.area
    }

    override suspend fun logout() {
        _currentUser.value = null
        userSession.clear()
    }

    override fun getCurrentUser(): Usuario? = _currentUser.value
}
