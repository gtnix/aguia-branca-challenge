package com.gtnix.aguiabranca.domain.session

import com.gtnix.aguiabranca.domain.model.Usuario
import kotlinx.coroutines.flow.Flow

/**
 * Contrato para gerenciamento reativo da sessão do usuário.
 *
 * Diferente do [UserSession] (singleton em memória com vars mutáveis),
 * esta interface expõe a sessão como [Flow] reativo, permitindo que
 * Use Cases e ViewModels reajam automaticamente a mudanças de login/logout.
 */
interface SessionManager {
    val currentUserFlow: Flow<Usuario?>
    suspend fun login(usuario: Usuario)
    suspend fun logout()
    fun getCurrentUser(): Usuario?
}
