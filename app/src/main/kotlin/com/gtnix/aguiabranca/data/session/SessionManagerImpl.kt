package com.gtnix.aguiabranca.data.session

import com.gtnix.aguiabranca.data.remote.api.AuthApiService
import com.gtnix.aguiabranca.data.remote.dto.toDomain
import com.gtnix.aguiabranca.domain.model.Usuario
import com.gtnix.aguiabranca.domain.session.SessionManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManagerImpl @Inject constructor(
    private val tokenStore: TokenStore,
    private val authApi: AuthApiService
) : SessionManager {

    private val _currentUser = MutableStateFlow<Usuario?>(null)
    override val currentUserFlow: Flow<Usuario?> = _currentUser.asStateFlow()

    override suspend fun login(usuario: Usuario) {
        applySession(usuario)
    }

    override suspend fun logout() {
        tokenStore.clear()
        _currentUser.value = null
    }

    override suspend fun restoreSession() {
        if (tokenStore.getAccessToken().isNullOrBlank()) return
        try {
            applySession(authApi.me().toDomain())
        } catch (e: HttpException) {
            if (e.code() == 401 || e.code() == 403) {
                tokenStore.clear()
            }
        } catch (_: IOException) {
            // Sem rede: mantém o token para restaurar na próxima tentativa (D4).
        }
    }

    override fun getCurrentUser(): Usuario? = _currentUser.value

    private fun applySession(usuario: Usuario) {
        _currentUser.value = usuario
    }
}
