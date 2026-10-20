package com.gtnix.aguiabranca.data.mapper

import com.gtnix.aguiabranca.data.util.safeValueOf
import com.gtnix.aguiabranca.data.local.entity.IdeiaEntity
import com.gtnix.aguiabranca.domain.model.AreaAtuacao
import com.gtnix.aguiabranca.domain.model.Ideia
import com.gtnix.aguiabranca.domain.model.StatusIdeia
import com.gtnix.aguiabranca.domain.model.TipoIdeia
import javax.inject.Inject

class IdeiaMapper @Inject constructor() {

    fun toDomain(entity: IdeiaEntity): Ideia {
        return Ideia(
            id = entity.id,
            titulo = entity.titulo,
            descricao = entity.descricao,
            tipo = safeValueOf(entity.tipo, TipoIdeia.IDEIA),
            area = safeValueOf(entity.area, AreaAtuacao.OPERACOES),
            status = safeValueOf(entity.status, StatusIdeia.PENDENTE),
            autorId = entity.autorId,
            autorNome = entity.autorNome,
            orientacaoId = entity.orientacaoId,
            feedback = entity.feedback,
            projetoId = entity.projetoId,
            dataCriacao = entity.dataCriacao,
            dataAvaliacao = entity.dataAvaliacao,
            impactoEstimado = entity.impactoEstimado,
            esforcoEstimado = entity.esforcoEstimado,
            upvotes = entity.upvotes,
            temSimilaridade = entity.temSimilaridade
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
            dataAvaliacao = domain.dataAvaliacao,
            impactoEstimado = domain.impactoEstimado,
            esforcoEstimado = domain.esforcoEstimado,
            upvotes = domain.upvotes,
            temSimilaridade = domain.temSimilaridade
        )
    }

    fun toDomainList(entities: List<IdeiaEntity>): List<Ideia> {
        return entities.map { toDomain(it) }
    }
}
