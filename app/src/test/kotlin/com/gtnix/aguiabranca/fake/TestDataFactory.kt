package com.gtnix.aguiabranca.fake

import com.gtnix.aguiabranca.domain.model.AreaAtuacao
import com.gtnix.aguiabranca.domain.model.CategoriaOrientacao
import com.gtnix.aguiabranca.domain.model.Ideia
import com.gtnix.aguiabranca.domain.model.OrientacaoEstrategica
import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.domain.model.Projeto
import com.gtnix.aguiabranca.domain.model.StatusIdeia
import com.gtnix.aguiabranca.domain.model.StatusProjeto
import com.gtnix.aguiabranca.domain.model.TipoIdeia
import com.gtnix.aguiabranca.domain.model.Usuario

object TestDataFactory {

    fun createUsuario(
        id: String = "user-1",
        nome: String = "Usuário Teste",
        email: String = "teste@aguiabranca.com.br",
        perfil: PerfilUsuario = PerfilUsuario.OPERADOR,
        area: AreaAtuacao = AreaAtuacao.OPERACOES
    ) = Usuario(
        id = id,
        nome = nome,
        email = email,
        perfil = perfil,
        area = area
    )

    fun createIdeia(
        id: String = "ideia-1",
        titulo: String = "Ideia Teste",
        descricao: String = "Descrição da ideia de teste",
        autorId: String = "user-1",
        autorNome: String = "Autor Teste",
        area: AreaAtuacao = AreaAtuacao.OPERACOES,
        status: StatusIdeia = StatusIdeia.PENDENTE,
        projetoId: String? = null,
        dataAvaliacao: Long? = null,
        impactoEstimado: Int = 0,
        esforcoEstimado: Int = 0
    ) = Ideia(
        id = id,
        titulo = titulo,
        descricao = descricao,
        tipo = TipoIdeia.IDEIA,
        area = area,
        status = status,
        autorId = autorId,
        autorNome = autorNome,
        projetoId = projetoId,
        dataAvaliacao = dataAvaliacao,
        impactoEstimado = impactoEstimado,
        esforcoEstimado = esforcoEstimado
    )

    fun createProjeto(
        id: String = "projeto-1",
        nome: String = "Projeto Teste",
        area: AreaAtuacao = AreaAtuacao.OPERACOES,
        status: StatusProjeto = StatusProjeto.EM_ANDAMENTO,
        investimentoRealizado: Double = 100_000.0,
        retornoRealizadoMensal: Double = 10_000.0
    ) = Projeto(
        id = id,
        nome = nome,
        objetivo = "Objetivo do projeto",
        descricao = "Descrição do projeto",
        area = area,
        status = status,
        responsavelId = "user-gestor",
        responsavelNome = "Gestor Teste",
        investimentoRealizado = investimentoRealizado,
        retornoRealizadoMensal = retornoRealizadoMensal
    )

    fun createOrientacao(
        id: String = "orientacao-1",
        titulo: String = "Orientação Teste",
        ativa: Boolean = true
    ) = OrientacaoEstrategica(
        id = id,
        titulo = titulo,
        descricao = "Descrição da orientação",
        categoria = CategoriaOrientacao.REDUCAO_CUSTOS,
        ativa = ativa,
        criadoPor = "user-lider"
    )
}
