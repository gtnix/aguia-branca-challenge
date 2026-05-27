package com.gtnix.aguiabranca.data.session

import com.gtnix.aguiabranca.domain.model.Usuario
import com.gtnix.aguiabranca.domain.repository.UsuarioRepository
import com.gtnix.aguiabranca.domain.session.SessionManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementação reativa do [SessionManager] com persistência via DataStore.
 */
@Singleton
class SessionManagerImpl @Inject constructor(
    private val sessionDataStore: SessionDataStore,
    private val usuarioRepository: UsuarioRepository
) : SessionManager {

    private val _currentUser = MutableStateFlow<Usuario?>(null)
    override val currentUserFlow: Flow<Usuario?> = _currentUser.asStateFlow()

    override suspend fun login(usuario: Usuario) {
        sessionDataStore.saveUserId(usuario.id)
        applySession(usuario)
    }

    override suspend fun logout() {
        sessionDataStore.clearUserId()
        _currentUser.value = null
    }

    override suspend fun restoreSession() {
        val userId = sessionDataStore.getUserId() ?: return
        val usuario = usuarioRepository.buscarPorId(userId)
        if (usuario != null) {
            applySession(usuario)
        } else {
            sessionDataStore.clearUserId()
        }
    }

    override fun getCurrentUser(): Usuario? = _currentUser.value

    private fun applySession(usuario: Usuario) {
        _currentUser.value = usuario
    }
}
