package com.gtnix.aguiabranca.data.local.database

import com.gtnix.aguiabranca.data.local.dao.IdeiaDao
import com.gtnix.aguiabranca.data.local.dao.OrientacaoDao
import com.gtnix.aguiabranca.data.local.dao.ProjetoDao
import com.gtnix.aguiabranca.data.local.dao.UsuarioDao
import com.gtnix.aguiabranca.data.local.entity.IdeiaEntity
import com.gtnix.aguiabranca.data.local.entity.OrientacaoEntity
import com.gtnix.aguiabranca.data.local.entity.ProjetoEntity
import com.gtnix.aguiabranca.data.local.entity.UsuarioEntity
import com.gtnix.aguiabranca.data.util.PasswordHasher
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
        val senhaHash = PasswordHasher.hash("123456")
        val dayMs = 86_400_000L

        val usuarios = listOf(
            UsuarioEntity(
                id = "user-lider",
                nome = "Marcos Silva",
                email = "marcos.silva@aguiabranca.com.br",
                senhaHash = senhaHash,
                perfil = "LIDER",
                area = "OPERACOES",
                divisao = "LOGISTICA",
                fotoPerfil = null,
                dataCadastro = agora - dayMs * 90
            ),
            UsuarioEntity(
                id = "user-gestor",
                nome = "Ana Oliveira",
                email = "ana.oliveira@aguiabranca.com.br",
                senhaHash = senhaHash,
                perfil = "GESTOR",
                area = "OPERACOES",
                divisao = "LOGISTICA",
                fotoPerfil = null,
                dataCadastro = agora - dayMs * 60
            ),
            UsuarioEntity(
                id = "user-operador",
                nome = "Pedro Santos",
                email = "pedro.santos@aguiabranca.com.br",
                senhaHash = senhaHash,
                perfil = "OPERADOR",
                area = "OPERACOES",
                divisao = "LOGISTICA",
                fotoPerfil = null,
                dataCadastro = agora - dayMs * 45
            ),
            UsuarioEntity(
                id = "user-operador-operacoes-2",
                nome = "Juliana Almeida",
                email = "juliana.almeida@aguiabranca.com.br",
                senhaHash = senhaHash,
                perfil = "OPERADOR",
                area = "OPERACOES",
                divisao = "LOGISTICA",
                fotoPerfil = null,
                dataCadastro = agora - dayMs * 40
            ),
            UsuarioEntity(
                id = "user-operador-operacoes-3",
                nome = "Rafael Mendes",
                email = "rafael.mendes@aguiabranca.com.br",
                senhaHash = senhaHash,
                perfil = "OPERADOR",
                area = "OPERACOES",
                divisao = "LOGISTICA",
                fotoPerfil = null,
                dataCadastro = agora - dayMs * 35
            ),
            UsuarioEntity(
                id = "user-operador-passageiros",
                nome = "Lucas Costa",
                email = "lucas.costa@aguiabranca.com.br",
                senhaHash = senhaHash,
                perfil = "OPERADOR",
                area = "COMERCIAL",
                divisao = "PASSAGEIROS",
                fotoPerfil = null,
                dataCadastro = agora - dayMs * 30
            ),
            UsuarioEntity(
                id = "user-gestor-logistica",
                nome = "Roberto Lima",
                email = "roberto.lima@aguiabranca.com.br",
                senhaHash = senhaHash,
                perfil = "GESTOR",
                area = "LOGISTICA",
                divisao = "LOGISTICA",
                fotoPerfil = null,
                dataCadastro = agora - dayMs * 50
            )
        )
        usuarioDao.inserirTodos(usuarios)

        val orientacoes = listOf(
            OrientacaoEntity(
                id = "orient-1",
                titulo = "Redução de Emissões CO2",
                descricao = "Reduzir as emissões de carbono da frota em 30% até 2027 através de monitoramento e otimização de rotas.",
                categoria = "SUSTENTABILIDADE",
                prioridade = 1,
                ativa = true,
                criadoPor = "user-lider",
                dataCriacao = agora - dayMs * 30,
                dataExpiracao = null
            ),
            OrientacaoEntity(
                id = "orient-2",
                titulo = "Experiência Digital do Passageiro",
                descricao = "Modernizar a jornada do passageiro com autoatendimento, pagamento digital e comunicação proativa.",
                categoria = "INOVACAO_TECNOLOGICA",
                prioridade = 2,
                ativa = true,
                criadoPor = "user-lider",
                dataCriacao = agora - dayMs * 20,
                dataExpiracao = null
            ),
            OrientacaoEntity(
                id = "orient-3",
                titulo = "Eficiência Operacional da Frota",
                descricao = "Aumentar a produtividade logística com telemetria, manutenção preditiva e otimização de rotas.",
                categoria = "EFICIENCIA_OPERACIONAL",
                prioridade = 3,
                ativa = true,
                criadoPor = "user-lider",
                dataCriacao = agora - dayMs * 10,
                dataExpiracao = null
            )
        )
        orientacaoDao.inserirTodas(orientacoes)

        val projetos = listOf(
            ProjetoEntity(
                id = "proj-1",
                nome = "Monitoramento de Emissões",
                objetivo = "Reduzir emissões de CO2 da frota em 30% com monitoramento em tempo real.",
                descricao = "Projeto piloto para instalação de sensores IoT em 50 veículos da frota.",
                area = "OPERACOES",
                status = "EM_ANDAMENTO",
                ideiaOrigemId = null,
                orientacaoId = "orient-1",
                responsavelId = "user-gestor",
                responsavelNome = "Ana Oliveira",
                membrosIds = "[\"user-operador\",\"user-gestor\"]",
                dataCriacao = agora - dayMs * 25,
                dataInicio = agora - dayMs * 20,
                dataPrevistaConclusao = agora + dayMs * 90,
                dataConclusao = null,
                progresso = 45,
                resultados = null,
                investimentoEstimado = 150_000.0,
                investimentoRealizado = 85_000.0,
                retornoEstimadoMensal = 12_000.0,
                retornoRealizadoMensal = 8_500.0
            ),
            ProjetoEntity(
                id = "proj-2",
                nome = "App de Autoatendimento",
                objetivo = "Reduzir filas nos terminais com autoatendimento digital.",
                descricao = "Canal digital para compra, check-in e suporte ao passageiro.",
                area = "OPERACOES",
                status = "EM_ANDAMENTO",
                ideiaOrigemId = null,
                orientacaoId = "orient-2",
                responsavelId = "user-gestor",
                responsavelNome = "Ana Oliveira",
                membrosIds = "[\"user-gestor\"]",
                dataCriacao = agora - dayMs * 18,
                dataInicio = agora - dayMs * 12,
                dataPrevistaConclusao = agora + dayMs * 120,
                dataConclusao = null,
                progresso = 30,
                resultados = null,
                investimentoEstimado = 80_000.0,
                investimentoRealizado = 25_000.0,
                retornoEstimadoMensal = 6_000.0,
                retornoRealizadoMensal = 2_000.0
            ),
            ProjetoEntity(
                id = "proj-3",
                nome = "Telemetria da Frota",
                objetivo = "Monitorar desempenho operacional em tempo real.",
                descricao = "Dashboard de telemetria para gestores de operações.",
                area = "OPERACOES",
                status = "PLANEJADO",
                ideiaOrigemId = null,
                orientacaoId = "orient-3",
                responsavelId = "user-gestor",
                responsavelNome = "Ana Oliveira",
                membrosIds = "[\"user-gestor\",\"user-operador\"]",
                dataCriacao = agora - dayMs * 8,
                dataInicio = null,
                dataPrevistaConclusao = agora + dayMs * 150,
                dataConclusao = null,
                progresso = 10,
                resultados = null,
                investimentoEstimado = 60_000.0,
                investimentoRealizado = 0.0,
                retornoEstimadoMensal = 4_000.0,
                retornoRealizadoMensal = 0.0
            ),
            ProjetoEntity(
                id = "proj-4",
                nome = "Painel de Curadoria de Ideias",
                objetivo = "Acelerar a avaliação de ideias da área de operações.",
                descricao = "Fluxo digital para curadoria e feedback de ideias.",
                area = "OPERACOES",
                status = "EM_ANDAMENTO",
                ideiaOrigemId = null,
                orientacaoId = "orient-3",
                responsavelId = "user-gestor",
                responsavelNome = "Ana Oliveira",
                membrosIds = "[\"user-gestor\"]",
                dataCriacao = agora - dayMs * 14,
                dataInicio = agora - dayMs * 10,
                dataPrevistaConclusao = agora + dayMs * 60,
                dataConclusao = null,
                progresso = 55,
                resultados = null,
                investimentoEstimado = 40_000.0,
                investimentoRealizado = 18_000.0,
                retornoEstimadoMensal = 3_000.0,
                retornoRealizadoMensal = 1_200.0
            ),
            ProjetoEntity(
                id = "proj-5",
                nome = "Programa de Engajamento",
                objetivo = "Aumentar participação em iniciativas de inovação.",
                descricao = "Campanha interna de gamificação e reconhecimento.",
                area = "OPERACOES",
                status = "PLANEJADO",
                ideiaOrigemId = null,
                orientacaoId = "orient-2",
                responsavelId = "user-gestor",
                responsavelNome = "Ana Oliveira",
                membrosIds = "[\"user-gestor\",\"user-operador\"]",
                dataCriacao = agora - dayMs * 5,
                dataInicio = null,
                dataPrevistaConclusao = agora + dayMs * 75,
                dataConclusao = null,
                progresso = 5,
                resultados = null,
                investimentoEstimado = 25_000.0,
                investimentoRealizado = 0.0,
                retornoEstimadoMensal = 2_000.0,
                retornoRealizadoMensal = 0.0
            ),
            ProjetoEntity(
                id = "proj-6",
                nome = "Otimização de Rotas",
                objetivo = "Reduzir custos logísticos com rotas inteligentes.",
                descricao = "Projeto de otimização para operações de logística.",
                area = "LOGISTICA",
                status = "EM_ANDAMENTO",
                ideiaOrigemId = null,
                orientacaoId = null,
                responsavelId = "user-gestor-logistica",
                responsavelNome = "Roberto Lima",
                membrosIds = "[\"user-gestor-logistica\"]",
                dataCriacao = agora - dayMs * 22,
                dataInicio = agora - dayMs * 15,
                dataPrevistaConclusao = agora + dayMs * 80,
                dataConclusao = null,
                progresso = 35,
                resultados = null,
                investimentoEstimado = 70_000.0,
                investimentoRealizado = 20_000.0,
                retornoEstimadoMensal = 5_000.0,
                retornoRealizadoMensal = 1_500.0
            )
        )
        projetoDao.inserirTodos(projetos)

        val ideiasPendentesOperacoes = listOf(
            PendenteSeed(
                titulo = "Horário flexível para motoristas",
                descricao = "Implementar escala alternada que permita aos motoristas ajustar início e fim do turno em até 30 minutos, reduzindo atrasos por trânsito e aumentando a previsibilidade das saídas.",
                autorId = "user-operador",
                autorNome = "Pedro Santos",
                orientacaoId = "orient-3"
            ),
            PendenteSeed(
                titulo = "Checklist digital de embarque",
                descricao = "Substituir o checklist em papel por um app no tablet do motorista, com fotos automáticas dos itens críticos (extintor, lacres, portas) e envio direto para o supervisor.",
                autorId = "user-operador",
                autorNome = "Pedro Santos",
                orientacaoId = null
            ),
            PendenteSeed(
                titulo = "Treinamento gamificado de segurança",
                descricao = "Plataforma de microlearning com fases curtas, ranking semanal e recompensas simbólicas para reforçar protocolos de segurança e direção defensiva.",
                autorId = "user-operador-operacoes-2",
                autorNome = "Juliana Almeida",
                orientacaoId = null
            ),
            PendenteSeed(
                titulo = "Painel de indicadores da frota em tempo real",
                descricao = "Dashboard visual no terminal mostrando consumo médio, ocorrências do dia e veículos em manutenção, exibido nas TVs da garagem para a equipe operacional.",
                autorId = "user-operador-operacoes-2",
                autorNome = "Juliana Almeida",
                orientacaoId = "orient-3"
            ),
            PendenteSeed(
                titulo = "Canal rápido de sugestões via QR Code",
                descricao = "Cartazes nos pontos de apoio com QR Code que abre formulário curto (3 campos) para o operador registrar uma sugestão em menos de 30 segundos durante a pausa.",
                autorId = "user-operador-operacoes-3",
                autorNome = "Rafael Mendes",
                orientacaoId = null
            ),
            PendenteSeed(
                titulo = "Programa de reconhecimento entre pares",
                descricao = "Sistema mensal em que cada operador pode indicar um colega que demonstrou comportamento exemplar (segurança, colaboração, atendimento), com publicação no mural interno.",
                autorId = "user-operador-operacoes-3",
                autorNome = "Rafael Mendes",
                orientacaoId = null
            )
        ).mapIndexed { index, seed ->
            IdeiaEntity(
                id = "ideia-pendente-operacoes-${index + 1}",
                titulo = seed.titulo,
                descricao = seed.descricao,
                tipo = "IDEIA",
                area = "OPERACOES",
                status = "PENDENTE",
                autorId = seed.autorId,
                autorNome = seed.autorNome,
                orientacaoId = seed.orientacaoId,
                feedback = null,
                projetoId = null,
                dataCriacao = agora - dayMs * (index + 1),
                dataAvaliacao = null,
                impactoEstimado = 5,
                esforcoEstimado = 3
            )
        }

        val ideiasLucas = listOf(
            IdeiaEntity(
                id = "ideia-lucas-1",
                titulo = "Totem de autoatendimento nos terminais",
                descricao = "Totens para compra e emissão de bilhetes sem filas.",
                tipo = "IDEIA",
                area = "COMERCIAL",
                status = "APROVADA",
                autorId = "user-operador-passageiros",
                autorNome = "Lucas Costa",
                orientacaoId = "orient-2",
                feedback = "Excelente proposta para melhorar a experiência do passageiro.",
                projetoId = null,
                dataCriacao = agora - dayMs * 12,
                dataAvaliacao = agora - dayMs * 8,
                impactoEstimado = 7,
                esforcoEstimado = 4
            )
        )

        val avaliadasOperacoesSeeds = listOf(
            // Decisão #7 da seção 12 — manter pelo menos 1 ideia CONVERTIDA_PROJETO
            // para o funil "Em Projeto" não exibir 0 e parecer quebrado na demo.
            AvaliadaSeed(
                titulo = "Manutenção preditiva por análise de vibração",
                descricao = "Sensores acoplados ao motor coletam dados de vibração e enviam alertas antes de falhas críticas, reduzindo paradas não programadas.",
                status = "CONVERTIDA_PROJETO",
                autorId = "user-operador",
                autorNome = "Pedro Santos",
                projetoId = "proj-1"
            ),
            AvaliadaSeed(
                titulo = "Onboarding em vídeo curto para novos motoristas",
                descricao = "Série de vídeos de 2 minutos cobrindo cada etapa do dia a dia, disponíveis no app interno desde o primeiro dia do colaborador.",
                status = "APROVADA",
                autorId = "user-operador-operacoes-2",
                autorNome = "Juliana Almeida",
                projetoId = null
            ),
            AvaliadaSeed(
                titulo = "Reaproveitamento de água da lavagem da frota",
                descricao = "Sistema de filtragem e recirculação da água utilizada na lavagem dos veículos, com meta de reuso de 60% do volume.",
                status = "APROVADA",
                autorId = "user-operador-operacoes-3",
                autorNome = "Rafael Mendes",
                projetoId = null
            ),
            AvaliadaSeed(
                titulo = "Mapa de calor de incidentes operacionais",
                descricao = "Visualização geográfica das ocorrências dos últimos 90 dias para identificar trechos críticos e reforçar a inspeção preventiva.",
                status = "APROVADA",
                autorId = "user-operador",
                autorNome = "Pedro Santos",
                projetoId = null
            ),
            AvaliadaSeed(
                titulo = "Brindes em material reciclado para passageiros",
                descricao = "Substituir brindes plásticos da campanha de fidelidade por itens feitos de material reciclado da própria operação.",
                status = "REPROVADA",
                autorId = "user-operador-operacoes-2",
                autorNome = "Juliana Almeida",
                projetoId = null
            ),
            AvaliadaSeed(
                titulo = "Concurso interno de fotos da operação",
                descricao = "Campanha mensal pedindo fotos da operação no Instagram interno; ideia avaliada como dispersiva em relação às prioridades atuais.",
                status = "REPROVADA",
                autorId = "user-operador-operacoes-3",
                autorNome = "Rafael Mendes",
                projetoId = null
            ),
            AvaliadaSeed(
                titulo = "Café da manhã rotativo entre garagens",
                descricao = "Realizar café da manhã itinerante entre garagens; reprovada por inviabilidade logística sem ganho operacional claro.",
                status = "REPROVADA",
                autorId = "user-operador",
                autorNome = "Pedro Santos",
                projetoId = null
            ),
            AvaliadaSeed(
                titulo = "Mascote interno por área",
                descricao = "Criar mascote por área para campanhas internas; reprovada por baixa aderência ao foco estratégico do trimestre.",
                status = "REPROVADA",
                autorId = "user-operador-operacoes-2",
                autorNome = "Juliana Almeida",
                projetoId = null
            ),
            AvaliadaSeed(
                titulo = "Customização visual dos veículos por filial",
                descricao = "Permitir personalização visual dos ônibus por filial; reprovada por conflito com o manual de identidade visual corporativa.",
                status = "REPROVADA",
                autorId = "user-operador-operacoes-3",
                autorNome = "Rafael Mendes",
                projetoId = null
            ),
            AvaliadaSeed(
                titulo = "Sorteio diário de pontos extras",
                descricao = "Sorteio diário entre operadores para pontos extras na gamificação; reprovada por estimular volume sem qualidade.",
                status = "REPROVADA",
                autorId = "user-operador",
                autorNome = "Pedro Santos",
                projetoId = null
            )
        )

        val ideiasAvaliadasOperacoes = avaliadasOperacoesSeeds.mapIndexed { index, seed ->
            val numero = index + 1
            IdeiaEntity(
                id = "ideia-avaliada-operacoes-$numero",
                titulo = seed.titulo,
                descricao = seed.descricao,
                tipo = "IDEIA",
                area = "OPERACOES",
                status = seed.status,
                autorId = seed.autorId,
                autorNome = seed.autorNome,
                orientacaoId = when (numero % 3) {
                    0 -> "orient-1"
                    1 -> "orient-2"
                    else -> "orient-3"
                },
                feedback = "Avaliada por Ana Oliveira.",
                projetoId = seed.projetoId,
                dataCriacao = agora - dayMs * (numero + 5),
                dataAvaliacao = agora - dayMs * numero,
                impactoEstimado = 6,
                esforcoEstimado = 3
            )
        }

        val ideiasAvaliadasRoberto = (1..7).map { index ->
            IdeiaEntity(
                id = "ideia-roberto-$index",
                titulo = "Melhoria logística #$index",
                descricao = "Ideia avaliada pela gestão de logística.",
                tipo = "IDEIA",
                area = "LOGISTICA",
                status = if (index <= 2) "APROVADA" else "REPROVADA",
                autorId = "user-operador",
                autorNome = "Pedro Santos",
                orientacaoId = null,
                feedback = "Avaliada por Roberto Lima.",
                projetoId = null,
                dataCriacao = agora - dayMs * (index + 3),
                dataAvaliacao = agora - dayMs * index,
                impactoEstimado = 5,
                esforcoEstimado = 2
            )
        }

        ideiaDao.inserirTodas(
            ideiasPendentesOperacoes + ideiasLucas + ideiasAvaliadasOperacoes + ideiasAvaliadasRoberto
        )
    }

    private data class PendenteSeed(
        val titulo: String,
        val descricao: String,
        val autorId: String,
        val autorNome: String,
        val orientacaoId: String?
    )

    private data class AvaliadaSeed(
        val titulo: String,
        val descricao: String,
        val status: String,
        val autorId: String,
        val autorNome: String,
        val projetoId: String?
    )
}
