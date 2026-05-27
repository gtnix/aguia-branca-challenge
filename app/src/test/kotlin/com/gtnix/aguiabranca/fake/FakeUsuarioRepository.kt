package com.gtnix.aguiabranca.fake

import com.gtnix.aguiabranca.domain.model.AreaAtuacao
import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.domain.model.Usuario
import com.gtnix.aguiabranca.domain.repository.UsuarioRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeUsuarioRepository : UsuarioRepository {

    private val usuarios = MutableStateFlow<List<Usuario>>(emptyList())
    private var authResult: Usuario? = null
    private var shouldThrowOnAuth = false

    fun setAuthResult(usuario: Usuario?) {
        authResult = usuario
    }

    fun setShouldThrowOnAuth(shouldThrow: Boolean) {
        shouldThrowOnAuth = shouldThrow
    }

    override suspend fun buscarPorEmail(email: String): Usuario? =
        usuarios.value.find { it.email == email }

    override suspend fun buscarPorId(id: String): Usuario? =
        usuarios.value.find { it.id == id }

    override fun listarTodos(): Flow<List<Usuario>> = usuarios

    override fun listarPorPerfil(perfil: PerfilUsuario): Flow<List<Usuario>> =
        usuarios.map { list -> list.filter { it.perfil == perfil } }

    override fun listarPorArea(area: AreaAtuacao): Flow<List<Usuario>> =
        usuarios.map { list -> list.filter { it.area == area } }

    override suspend fun salvar(usuario: Usuario) {
        val current = usuarios.value.toMutableList()
        val index = current.indexOfFirst { it.id == usuario.id }
        if (index >= 0) {
            current[index] = usuario
        } else {
            current.add(usuario)
        }
        usuarios.value = current
    }

    override suspend fun excluir(id: String) {
        usuarios.value = usuarios.value.filterNot { it.id == id }
    }

    override suspend fun autenticar(email: String, senha: String): Usuario? {
        if (shouldThrowOnAuth) throw RuntimeException("Erro de autenticação")
        return authResult ?: usuarios.value.find { it.email == email }
    }
}
