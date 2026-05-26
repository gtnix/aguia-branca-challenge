package com.gtnix.aguiabranca.data.local.database

import com.gtnix.aguiabranca.data.local.dao.IdeiaDao
import com.gtnix.aguiabranca.data.local.dao.OrientacaoDao
import com.gtnix.aguiabranca.data.local.dao.ProjetoDao
import com.gtnix.aguiabranca.data.local.dao.UsuarioDao
import com.gtnix.aguiabranca.data.local.entity.IdeiaEntity
import com.gtnix.aguiabranca.data.local.entity.OrientacaoEntity
import com.gtnix.aguiabranca.data.local.entity.ProjetoEntity
import com.gtnix.aguiabranca.data.local.entity.UsuarioEntity
import javax.inject.Inject

class DatabaseSeeder @Inject constructor(
    private val usuarioDao: UsuarioDao,
    private val orientacaoDao: OrientacaoDao,
    private val ideiaDao: IdeiaDao,
    private val projetoDao: ProjetoDao
) {

    suspend fun seedDatabaseIfEmpty() {
        if (usuarioDao.contar() > 0) return

        val agora = System.currentTimeMillis()

        // 1. Usuarios
        val usuarios = listOf(
            UsuarioEntity(
                id = "user-lider",
                nome = "Carlos Silva (CEO)",
                email = "ceo@aguiabranca.com.br",
                senhaHash = "123456",
                perfil = "LIDER",
                area = "OPERACOES",
                fotoPerfil = null,
                dataCadastro = agora
            ),
            UsuarioEntity(
                id = "user-gestor",
                nome = "Ana Oliveira (Gerente Operações)",
                email = "gestor@aguiabranca.com.br",
                senhaHash = "123456",
                perfil = "GESTOR",
                area = "OPERACOES",
                fotoPerfil = null,
                dataCadastro = agora
            ),
            UsuarioEntity(
                id = "user-operador",
                nome = "Pedro Santos (Motorista)",
                email = "motorista@aguiabranca.com.br",
                senhaHash = "123456",
                perfil = "OPERADOR",
                area = "OPERACOES",
                fotoPerfil = null,
                dataCadastro = agora
            )
        )
        usuarioDao.inserirTodos(usuarios)

        // 2. Orientacao estrategica
        val orientacao = OrientacaoEntity(
            id = "orient-1",
            titulo = "Redução de Emissões CO2",
            descricao = "Reduzir as emissões de carbono da frota em 30% até 2027 através de monitoramento e otimização de rotas.",
            categoria = "AMBIENTAL",
            prioridade = 1,
            ativa = true,
            criadoPor = "user-lider",
            dataCriacao = agora,
            dataExpiracao = null
        )
        orientacaoDao.inserir(orientacao)

        // 3. Projeto (inserido antes das ideias para respeitar FK constraint)
        // ideiaOrigemId = null para evitar dependência circular (Projeto -> Ideia -> Projeto)
        val projeto = ProjetoEntity(
            id = "proj-1",
            nome = "Monitoramento de Emissões",
            objetivo = "Reduzir emissões de CO2 da frota em 30% com monitoramento em tempo real.",
            descricao = "Projeto piloto para instalação de sensores IoT em 50 veículos da frota, com dashboard de acompanhamento de emissões.",
            area = "OPERACOES",
            status = "EM_ANDAMENTO",
            ideiaOrigemId = null,
            orientacaoId = "orient-1",
            responsavelId = "user-gestor",
            responsavelNome = "Ana Oliveira (Gerente Operações)",
            membrosIds = "[\"user-operador\"]",
            dataCriacao = agora - 86_400_000,
            dataInicio = agora - 43_200_000,
            dataPrevistaConclusao = agora + 7_776_000_000,
            dataConclusao = null,
            progresso = 45,
            resultados = null,
            investimentoEstimado = 150000.0,
            investimentoRealizado = 85000.0,
            retornoEstimadoMensal = 12000.0,
            retornoRealizadoMensal = 8500.0
        )
        projetoDao.inserir(projeto)

        // 4. Ideias (inseridas após o projeto para respeitar FK constraint)
        val ideias = listOf(
            IdeiaEntity(
                id = "ideia-1",
                titulo = "Horário flexível para motoristas",
                descricao = "Implementar sistema de turnos flexíveis para melhorar qualidade de vida dos motoristas e reduzir rotatividade.",
                tipo = "IDEIA",
                area = "OPERACOES",
                status = "PENDENTE",
                autorId = "user-operador",
                autorNome = "Pedro Santos (Motorista)",
                orientacaoId = null,
                feedback = null,
                projetoId = null,
                dataCriacao = agora,
                dataAvaliacao = null,
                impactoEstimado = 0,
                esforcoEstimado = 0
            ),
            IdeiaEntity(
                id = "ideia-2",
                titulo = "Rastreamento de emissões por viagem",
                descricao = "Instalar sensores IoT nos veículos para medir emissões de CO2 em tempo real durante cada viagem.",
                tipo = "IDEIA",
                area = "OPERACOES",
                status = "APROVADA",
                autorId = "user-operador",
                autorNome = "Pedro Santos (Motorista)",
                orientacaoId = "orient-1",
                feedback = "Excelente ideia! Alinhada com nossa meta de sustentabilidade.",
                projetoId = "proj-1",
                dataCriacao = agora - 86_400_000,
                dataAvaliacao = agora,
                impactoEstimado = 8,
                esforcoEstimado = 3
            )
        )
        ideiaDao.inserirTodas(ideias)
    }
}
