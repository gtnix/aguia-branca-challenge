package com.gtnix.aguiabranca.data.repository

import com.gtnix.aguiabranca.data.local.dao.UsuarioDao
import com.gtnix.aguiabranca.data.mapper.UsuarioMapper
import com.gtnix.aguiabranca.domain.model.AreaAtuacao
import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.domain.model.Usuario
import com.gtnix.aguiabranca.domain.repository.UsuarioRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.security.MessageDigest
import javax.inject.Inject

/**
 * Implementação do UsuarioRepository
 *
 * ## Conceito FIAP - Material 10A (Repository Pattern)
 *
 * Esta classe implementa a interface definida no Domain.
 * Ela é a "ponte" entre o Domain e o Data Layer.
 *
 * ### @Inject constructor
 *
 * O Hilt injeta automaticamente:
 * - `dao`: UsuarioDao (provido pelo DatabaseModule)
 * - `mapper`: UsuarioMapper (criado automaticamente por ter @Inject constructor)
 *
 * ### Flow para Reatividade
 *
 * ```kotlin
 * // No DAO
 * fun listarTodos(): Flow<List<UsuarioEntity>>
 *
 * // No Repository - converte Entity → Domain
 * override fun listarTodos(): Flow<List<Usuario>> {
 *     return dao.listarTodos().map { entities ->
 *         mapper.toDomainList(entities)
 *     }
 * }
 * ```
 *
 * O `map` transforma cada emissão do Flow.
 */
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
        // Para novos usuários, usamos uma senha padrão hash
        // Em produção, isso viria do cadastro
        val entity = mapper.toEntity(usuario, hashSenha("senha123"))
        dao.inserir(entity)
    }

    override suspend fun excluir(id: String) {
        dao.excluirPorId(id)
    }

    override suspend fun autenticar(email: String, senha: String): Usuario? {
        val senhaHash = hashSenha(senha)
        return dao.autenticar(email, senhaHash)?.let { mapper.toDomain(it) }
    }

    /**
     * Hash simples da senha usando SHA-256.
     * 
     * ATENÇÃO: Em produção, use bcrypt ou Argon2!
     */
    private fun hashSenha(senha: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(senha.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
