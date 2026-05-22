package com.gtnix.aguiabranca.data.mapper

import com.gtnix.aguiabranca.data.local.entity.ProjetoEntity
import com.gtnix.aguiabranca.domain.model.AreaAtuacao
import com.gtnix.aguiabranca.domain.model.Projeto
import com.gtnix.aguiabranca.domain.model.StatusProjeto
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import javax.inject.Inject

/**
 * Mapper de Projeto
 *
 * ## Nota sobre membrosIds
 *
 * A lista de membros é serializada como JSON no banco.
 * O mapper converte entre List<String> e String JSON.
 */
class ProjetoMapper @Inject constructor() {

    private val gson = Gson()

    fun toDomain(entity: ProjetoEntity): Projeto {
        return Projeto(
            id = entity.id,
            nome = entity.nome,
            objetivo = entity.objetivo,
            descricao = entity.descricao,
            area = AreaAtuacao.valueOf(entity.area),
            status = StatusProjeto.valueOf(entity.status),
            ideiaOrigemId = entity.ideiaOrigemId,
            orientacaoId = entity.orientacaoId,
            responsavelId = entity.responsavelId ?: "",
            responsavelNome = entity.responsavelNome,
            membrosIds = jsonToList(entity.membrosIds),
            dataCriacao = entity.dataCriacao,
            dataInicio = entity.dataInicio,
            dataPrevistaConclusao = entity.dataPrevistaConclusao,
            dataConclusao = entity.dataConclusao,
            progresso = entity.progresso,
            resultados = entity.resultados
        )
    }

    fun toEntity(domain: Projeto): ProjetoEntity {
        return ProjetoEntity(
            id = domain.id,
            nome = domain.nome,
            objetivo = domain.objetivo,
            descricao = domain.descricao,
            area = domain.area.name,
            status = domain.status.name,
            ideiaOrigemId = domain.ideiaOrigemId,
            orientacaoId = domain.orientacaoId,
            responsavelId = domain.responsavelId,
            responsavelNome = domain.responsavelNome,
            membrosIds = listToJson(domain.membrosIds),
            dataCriacao = domain.dataCriacao,
            dataInicio = domain.dataInicio,
            dataPrevistaConclusao = domain.dataPrevistaConclusao,
            dataConclusao = domain.dataConclusao,
            progresso = domain.progresso,
            resultados = domain.resultados
        )
    }

    fun toDomainList(entities: List<ProjetoEntity>): List<Projeto> {
        return entities.map { toDomain(it) }
    }

    private fun listToJson(list: List<String>): String {
        return gson.toJson(list)
    }

    private fun jsonToList(json: String): List<String> {
        if (json.isEmpty()) return emptyList()
        val type = object : TypeToken<List<String>>() {}.type
        return try {
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}
