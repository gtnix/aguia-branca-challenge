package com.gtnix.aguiabranca.data.repository

import com.gtnix.aguiabranca.data.local.dao.UsuarioDao
import com.gtnix.aguiabranca.data.mapper.UsuarioMapper
import com.gtnix.aguiabranca.data.remote.api.AuthApiService
import com.gtnix.aguiabranca.data.remote.dto.LoginRequestDto
import com.gtnix.aguiabranca.data.remote.dto.toDomain
import com.gtnix.aguiabranca.data.session.TokenStore
import com.gtnix.aguiabranca.data.util.PasswordHasher
import com.gtnix.aguiabranca.domain.model.AreaAtuacao
import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.domain.model.Usuario
import com.gtnix.aguiabranca.domain.repository.UsuarioRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import javax.inject.Inject

class UsuarioRepositoryImpl @Inject constructor(
    private val dao: UsuarioDao,
    private val mapper: UsuarioMapper,
    private val authApi: AuthApiService,
    private val tokenStore: TokenStore
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
        return try {
            val response = authApi.login(LoginRequestDto(email = email, senha = senha))
            tokenStore.save(response.accessToken, response.expiresIn)
            response.usuario.toDomain().also { usuario ->
                dao.buscarPorEmail(usuario.email)
                    ?.takeIf { it.id != usuario.id }
                    ?.let { dao.excluir(it) }
                dao.inserir(mapper.toEntity(usuario, REMOTE_USER_MARKER))
            }
        } catch (e: HttpException) {
            if (e.code() == 401 || e.code() == 400) {
                null
            } else {
                throw e
            }
        }
    }

    private companion object {
        const val REMOTE_USER_MARKER = "REMOTE_AUTH"
    }
}
