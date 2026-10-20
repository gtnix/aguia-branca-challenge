package com.gtnix.aguiabranca.domain.repository

import com.gtnix.aguiabranca.domain.model.AreaAtuacao
import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.domain.model.Usuario
import kotlinx.coroutines.flow.Flow

interface UsuarioRepository {
    suspend fun buscarPorEmail(email: String): Usuario?
    suspend fun buscarPorId(id: String): Usuario?
    fun listarTodos(): Flow<List<Usuario>>
    fun listarPorPerfil(perfil: PerfilUsuario): Flow<List<Usuario>>
    fun listarPorArea(area: AreaAtuacao): Flow<List<Usuario>>
    suspend fun salvar(usuario: Usuario)
    suspend fun excluir(id: String)
    suspend fun autenticar(email: String, senha: String): Usuario?
}
