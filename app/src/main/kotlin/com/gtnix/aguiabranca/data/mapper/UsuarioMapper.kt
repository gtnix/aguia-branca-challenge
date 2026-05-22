package com.gtnix.aguiabranca.data.mapper

import com.gtnix.aguiabranca.data.local.entity.UsuarioEntity
import com.gtnix.aguiabranca.domain.model.AreaAtuacao
import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.domain.model.Usuario
import javax.inject.Inject

/**
 * Mapper de Usuario
 *
 * ## Conceito FIAP - Clean Architecture
 *
 * Mappers são responsáveis por converter entre:
 * - **Entity (Room)**: Representa dados persistidos
 * - **Domain Model**: Representa regras de negócio
 *
 * ### Por que separar?
 *
 * 1. Domain Model pode ter enums, objetos complexos
 * 2. Room Entity precisa de tipos simples (String, Int, etc.)
 * 3. Se mudarmos o banco, só alteramos o Mapper
 *
 * ### @Inject constructor()
 *
 * Permite que o Hilt crie instâncias automaticamente.
 */
class UsuarioMapper @Inject constructor() {

    /**
     * Converte Entity (banco) → Domain (negócio)
     */
    fun toDomain(entity: UsuarioEntity): Usuario {
        return Usuario(
            id = entity.id,
            nome = entity.nome,
            email = entity.email,
            perfil = PerfilUsuario.valueOf(entity.perfil),
            area = AreaAtuacao.valueOf(entity.area),
            fotoPerfil = entity.fotoPerfil,
            dataCadastro = entity.dataCadastro
        )
    }

    /**
     * Converte Domain (negócio) → Entity (banco)
     * 
     * @param senhaHash Hash da senha (não armazenamos senha em texto puro!)
     */
    fun toEntity(domain: Usuario, senhaHash: String): UsuarioEntity {
        return UsuarioEntity(
            id = domain.id,
            nome = domain.nome,
            email = domain.email,
            senhaHash = senhaHash,
            perfil = domain.perfil.name,
            area = domain.area.name,
            fotoPerfil = domain.fotoPerfil,
            dataCadastro = domain.dataCadastro
        )
    }

    /**
     * Converte lista de Entities → lista de Domains
     */
    fun toDomainList(entities: List<UsuarioEntity>): List<Usuario> {
        return entities.map { toDomain(it) }
    }
}
