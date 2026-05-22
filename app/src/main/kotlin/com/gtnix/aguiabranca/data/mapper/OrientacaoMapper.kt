package com.gtnix.aguiabranca.data.mapper

import com.gtnix.aguiabranca.data.local.entity.OrientacaoEntity
import com.gtnix.aguiabranca.domain.model.CategoriaOrientacao
import com.gtnix.aguiabranca.domain.model.OrientacaoEstrategica
import javax.inject.Inject

/**
 * Mapper de Orientação Estratégica
 */
class OrientacaoMapper @Inject constructor() {

    fun toDomain(entity: OrientacaoEntity): OrientacaoEstrategica {
        return OrientacaoEstrategica(
            id = entity.id,
            titulo = entity.titulo,
            descricao = entity.descricao,
            categoria = CategoriaOrientacao.valueOf(entity.categoria),
            prioridade = entity.prioridade,
            ativa = entity.ativa,
            criadoPor = entity.criadoPor ?: "",
            dataCriacao = entity.dataCriacao,
            dataExpiracao = entity.dataExpiracao
        )
    }

    fun toEntity(domain: OrientacaoEstrategica): OrientacaoEntity {
        return OrientacaoEntity(
            id = domain.id,
            titulo = domain.titulo,
            descricao = domain.descricao,
            categoria = domain.categoria.name,
            prioridade = domain.prioridade,
            ativa = domain.ativa,
            criadoPor = domain.criadoPor,
            dataCriacao = domain.dataCriacao,
            dataExpiracao = domain.dataExpiracao
        )
    }

    fun toDomainList(entities: List<OrientacaoEntity>): List<OrientacaoEstrategica> {
        return entities.map { toDomain(it) }
    }
}
