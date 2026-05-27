package com.gtnix.aguiabranca.data.repository

import com.gtnix.aguiabranca.data.local.dao.UsuarioDao
import com.gtnix.aguiabranca.data.mapper.UsuarioMapper
import com.gtnix.aguiabranca.data.util.PasswordHasher
import com.gtnix.aguiabranca.domain.model.AreaAtuacao
import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.domain.model.Usuario
import com.gtnix.aguiabranca.domain.repository.UsuarioRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UsuarioRepositoryImpl @Inject constructor(
    private val dao: UsuarioDao,
    private val mapper: UsuarioMapper
) : UsuarioRepository {

    override suspend fun buscarPorEmail(email: String): Usuario? {
        return dao.buscarPorEmail(email)?.let { mapper.toDomain(it) }
    }

    override suspend fun buscarPorId(id: String): Usuario? {
        return dao.buscarPorId(id)?.let { mapper.toDomain(it) }
    }

    override fun listarTodos(): Flow<List<Usuario>> {
        return dao.listarTodos().map { entities ->
            mapper.toDomainList(entities)
        }
    }

    override fun listarPorPerfil(perfil: PerfilUsuario): Flow<List<Usuario>> {
        return dao.listarPorPerfil(perfil.name).map { entities ->
            mapper.toDomainList(entities)
        }
    }

    override fun listarPorArea(area: AreaAtuacao): Flow<List<Usuario>> {
        return dao.listarPorArea(area.name).map { entities ->
            mapper.toDomainList(entities)
        }
    }

    override suspend fun salvar(usuario: Usuario) {
        val entity = mapper.toEntity(usuario, PasswordHasher.hash("senha123"))
        dao.inserir(entity)
    }

    override suspend fun excluir(id: String) {
        dao.excluirPorId(id)
    }

    override suspend fun autenticar(email: String, senha: String): Usuario? {
        val senhaHash = PasswordHasher.hash(senha)
        return dao.autenticar(email, senhaHash)?.let { mapper.toDomain(it) }
    }
}
