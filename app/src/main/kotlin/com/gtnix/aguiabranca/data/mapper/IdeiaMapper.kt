package com.gtnix.aguiabranca.data.mapper

import com.gtnix.aguiabranca.data.local.entity.IdeiaEntity
import com.gtnix.aguiabranca.domain.model.AreaAtuacao
import com.gtnix.aguiabranca.domain.model.Ideia
import com.gtnix.aguiabranca.domain.model.StatusIdeia
import com.gtnix.aguiabranca.domain.model.TipoIdeia
import javax.inject.Inject

/**
 * Mapper de Ideia
 */
class IdeiaMapper @Inject constructor() {

    fun toDomain(entity: IdeiaEntity): Ideia {
        return Ideia(
            id = entity.id,
            titulo = entity.titulo,
            descricao = entity.descricao,
            tipo = TipoIdeia.valueOf(entity.tipo),
            area = AreaAtuacao.valueOf(entity.area),
            status = StatusIdeia.valueOf(entity.status),
            autorId = entity.autorId,
            autorNome = entity.autorNome,
            orientacaoId = entity.orientacaoId,
            feedback = entity.feedback,
            projetoId = entity.projetoId,
            dataCriacao = entity.dataCriacao,
            dataAvaliacao = entity.dataAvaliacao
        )
    }

    fun toEntity(domain: Ideia): IdeiaEntity {
        return IdeiaEntity(
            id = domain.id,
            titulo = domain.titulo,
            descricao = domain.descricao,
            tipo = domain.tipo.name,
            area = domain.area.name,
            status = domain.status.name,
            autorId = domain.autorId,
            autorNome = domain.autorNome,
            orientacaoId = domain.orientacaoId,
            feedback = domain.feedback,
            projetoId = domain.projetoId,
            dataCriacao = domain.dataCriacao,
            dataAvaliacao = domain.dataAvaliacao
        )
    }

    fun toDomainList(entities: List<IdeiaEntity>): List<Ideia> {
        return entities.map { toDomain(it) }
    }
}
