package com.gtnix.aguiabranca.fake

import com.gtnix.aguiabranca.domain.model.Usuario
import com.gtnix.aguiabranca.domain.session.SessionManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeSessionManager : SessionManager {

    private val _currentUserFlow = MutableStateFlow<Usuario?>(null)
    override val currentUserFlow: Flow<Usuario?> = _currentUserFlow.asStateFlow()

    fun setUser(user: Usuario?) {
        _currentUserFlow.value = user
    }

    override suspend fun login(usuario: Usuario) {
        _currentUserFlow.value = usuario
    }

    override suspend fun logout() {
        _currentUserFlow.value = null
    }

    override suspend fun restoreSession() = Unit

    override fun getCurrentUser(): Usuario? = _currentUserFlow.value
}
