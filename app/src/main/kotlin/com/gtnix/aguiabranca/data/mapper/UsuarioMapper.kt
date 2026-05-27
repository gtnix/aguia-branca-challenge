package com.gtnix.aguiabranca.data.mapper

import com.gtnix.aguiabranca.data.util.safeValueOf
import com.gtnix.aguiabranca.data.local.entity.UsuarioEntity
import com.gtnix.aguiabranca.domain.model.AreaAtuacao
import com.gtnix.aguiabranca.domain.model.DivisaoNegocio
import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.domain.model.Usuario
import javax.inject.Inject

class UsuarioMapper @Inject constructor() {

    fun toDomain(entity: UsuarioEntity): Usuario {
        return Usuario(
            id = entity.id,
            nome = entity.nome,
            email = entity.email,
            perfil = safeValueOf(entity.perfil, PerfilUsuario.OPERADOR),
            area = safeValueOf(entity.area, AreaAtuacao.OPERACOES),
            divisao = safeValueOf(entity.divisao, DivisaoNegocio.LOGISTICA),
            fotoPerfil = entity.fotoPerfil,
            dataCadastro = entity.dataCadastro
        )
    }

    fun toEntity(domain: Usuario, senhaHash: String): UsuarioEntity {
        return UsuarioEntity(
            id = domain.id,
            nome = domain.nome,
            email = domain.email,
            senhaHash = senhaHash,
            perfil = domain.perfil.name,
            area = domain.area.name,
            divisao = domain.divisao.name,
            fotoPerfil = domain.fotoPerfil,
            dataCadastro = domain.dataCadastro
        )
    }

    fun toDomainList(entities: List<UsuarioEntity>): List<Usuario> {
        return entities.map { toDomain(it) }
    }
}
