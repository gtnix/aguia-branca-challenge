package com.gtnix.aguiabranca.domain.usecase.dashboard

import com.gtnix.aguiabranca.domain.model.AreaAtuacao
import com.gtnix.aguiabranca.domain.model.Ideia
import com.gtnix.aguiabranca.domain.model.OrientacaoEstrategica
import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.domain.model.Projeto
import com.gtnix.aguiabranca.domain.model.StatusIdeia
import com.gtnix.aguiabranca.domain.model.StatusProjeto
import com.gtnix.aguiabranca.domain.model.Usuario
import com.gtnix.aguiabranca.domain.repository.IdeiaRepository
import com.gtnix.aguiabranca.domain.repository.OrientacaoRepository
import com.gtnix.aguiabranca.domain.repository.ProjetoRepository
import com.gtnix.aguiabranca.domain.session.SessionManager
import com.gtnix.aguiabranca.domain.util.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

data class IdeiaConvertidaRecente(
    val ideia: Ideia,
    val projeto: Projeto
) {
    val roi: Double
        get() = projeto.roi
}

data class AreaDesempenhoMetric(
    val label: String,
    val value: Float,
    val quantidade: Int = 0
)

data class WeeklyRecap(
    val novasIdeias: Int = 0,
    val avaliacoes: Int = 0,
    val projetosAtualizados: Int = 0
)

data class DashboardData(
    val orientacoes: List<OrientacaoEstrategica>,
    val ideias: List<Ideia>,
    val projetosEmAndamento: List<Projeto>,
    val ideiasConvertidasRecentes: List<IdeiaConvertidaRecente>,
    val totalIdeias: Int,
    val totalProjetos: Int,
    val ideiasAprovadas: Int,
    val ideiasEmProjeto: Int,
    val ideiasPendentesAvaliacao: Int,
    val investimentoTotal: Double,
    val retornoTotal: Double,
    val roiConsolidado: Double,
    val desempenhoPorArea: List<AreaDesempenhoMetric> = emptyList(),
    val tempoMedioAprovacaoDias: Int = 0,
    val weeklyRecap: WeeklyRecap = WeeklyRecap(),
    val ideiasRecentesParaAtividade: List<Ideia> = emptyList(),
    val ideiasPorOrientacao: Map<String, Int> = emptyMap(),
    val atrasadosPorOrientacao: Map<String, Int> = emptyMap(),
    /** Pendentes mais antigas primeiro: é a ordem do SLA de avaliação. */
    val filaPendente: List<Ideia> = emptyList(),
    val diasMaisAntigaPendente: Int? = null,
    val aprovadasSemProjeto: List<Ideia> = emptyList(),
    val projetosAtrasados: Int = 0
)

private data class DashboardRawInput(
    val orientacoes: List<OrientacaoEstrategica>,
    val ideias: List<Ideia>,
    val projetos: List<Projeto>,
    val user: Usuario?
)

/**
 * Consolida KPIs do dashboard combinando dados de ideias, projetos e orientações.
 *
 * Cálculos pesados executam em [Dispatchers.Default] para evitar ANR na UI.
 */
class GetDashboardUseCase @Inject constructor(
    private val ideiaRepository: IdeiaRepository,
    private val projetoRepository: ProjetoRepository,
    private val orientacaoRepository: OrientacaoRepository,
    private val sessionManager: SessionManager
) {
    operator fun invoke(): Flow<Result<DashboardData>> {
        return combine(
            orientacaoRepository.listarAtivas(),
            ideiaRepository.listarTodas(),
            projetoRepository.listarTodos(),
            sessionManager.currentUserFlow
        ) { orientacoes, ideias, projetos, user ->
            DashboardRawInput(orientacoes, ideias, projetos, user)
        }
            .map { input -> buildDashboard(input) }
            .flowOn(Dispatchers.Default)
            .catch { e ->
                emit(Result.Error(Exception(e)))
            }
    }

    private fun buildDashboard(input: DashboardRawInput): Result<DashboardData> {
        val user = input.user
            ?: return Result.Error(Exception("Usuário não logado"))

        val orientacoes = input.orientacoes
        val ideias = input.ideias
        val projetos = input.projetos

        val ideiasFiltradas = when (user.perfil) {
            PerfilUsuario.OPERADOR -> ideias.filter { it.autorId == user.id }
            PerfilUsuario.GESTOR, PerfilUsuario.LIDER -> ideias
        }

        val projetosFiltrados = when (user.perfil) {
            PerfilUsuario.OPERADOR -> emptyList()
            PerfilUsuario.GESTOR, PerfilUsuario.LIDER -> projetos
        }

        val carteira = projetosFiltrados.filter { it.status != StatusProjeto.CANCELADO }
        val investimento = carteira.sumOf { it.investimentoRealizado }
        val retorno = carteira.sumOf { it.retornoRealizadoMensal }
        val roi = if (investimento > 0) {
            ((retorno * 12) - investimento) / investimento * 100
        } else {
            0.0
        }

        val ideiasConvertidasRecentes = ideiasFiltradas
            .filter { it.projetoId != null }
            .sortedByDescending { it.dataAvaliacao ?: it.dataCriacao }
            .mapNotNull { ideia ->
                val projeto = projetos.find { it.id == ideia.projetoId } ?: return@mapNotNull null
                IdeiaConvertidaRecente(ideia = ideia, projeto = projeto)
            }
            .take(3)

        val ideiasLider = ideias
        val totalIdeiasLider = ideiasLider.size.coerceAtLeast(1)
        val desempenhoPorArea = ideiasLider
            .groupBy { it.area }
            .map { (area, areaIdeias) ->
                AreaDesempenhoMetric(
                    label = area.label(),
                    value = areaIdeias.size * 100f / totalIdeiasLider
                )
            }
            .sortedByDescending { it.value }
            .take(4)

        val tempoMedioAprovacaoDias = ideiasLider
            .mapNotNull { ideia ->
                ideia.dataAvaliacao?.let { avaliacao ->
                    (avaliacao - ideia.dataCriacao) / MILLIS_PER_DAY
                }
            }
            .takeIf { it.isNotEmpty() }
            ?.average()
            ?.toInt()
            ?: 0

        val ideiasPendentesAvaliacao = when (user.perfil) {
            PerfilUsuario.GESTOR, PerfilUsuario.LIDER, PerfilUsuario.OPERADOR -> ideiasFiltradas.count {
                it.status == StatusIdeia.PENDENTE || it.status == StatusIdeia.EM_ANALISE
            }
        }

        val weekStart = System.currentTimeMillis() - MILLIS_PER_WEEK
        val weeklyRecap = WeeklyRecap(
            novasIdeias = ideiasFiltradas.count { it.dataCriacao >= weekStart },
            avaliacoes = ideiasFiltradas.count {
                it.dataAvaliacao != null && it.dataAvaliacao >= weekStart
            },
            projetosAtualizados = projetosFiltrados.count { projeto ->
                val lastActivity = listOfNotNull(
                    projeto.dataCriacao,
                    projeto.dataInicio,
                    projeto.dataConclusao
                ).maxOrNull() ?: projeto.dataCriacao
                lastActivity >= weekStart
            }
        )

        val ideiasRecentesParaAtividade = ideiasFiltradas
            .sortedByDescending { it.dataCriacao }
            .take(3)

        val pendentesPorIdade = ideiasFiltradas
            .filter { it.status == StatusIdeia.PENDENTE || it.status == StatusIdeia.EM_ANALISE }
            .sortedBy { it.dataCriacao }
        val diasMaisAntigaPendente = pendentesPorIdade.firstOrNull()?.let { maisAntiga ->
            ((System.currentTimeMillis() - maisAntiga.dataCriacao) / MILLIS_PER_DAY).toInt().coerceAtLeast(0)
        }
        val aprovadasSemProjeto = ideiasFiltradas
            .filter { it.status == StatusIdeia.APROVADA && it.projetoId == null }
            .sortedBy { it.dataAvaliacao ?: it.dataCriacao }

        return Result.Success(
            DashboardData(
                orientacoes = orientacoes.take(3),
                ideias = ideiasFiltradas.take(5),
                projetosEmAndamento = projetosFiltrados.filter {
                    it.status == StatusProjeto.EM_ANDAMENTO
                }.take(5),
                ideiasConvertidasRecentes = ideiasConvertidasRecentes,
                totalIdeias = ideiasFiltradas.size,
                totalProjetos = carteira.size,
                ideiasAprovadas = ideiasFiltradas.count { it.isAprovadaOuConvertida() },
                ideiasEmProjeto = ideiasFiltradas.count { it.status == StatusIdeia.CONVERTIDA_PROJETO },
                ideiasPendentesAvaliacao = ideiasPendentesAvaliacao,
                investimentoTotal = investimento,
                retornoTotal = retorno,
                roiConsolidado = roi,
                desempenhoPorArea = desempenhoPorArea,
                tempoMedioAprovacaoDias = tempoMedioAprovacaoDias,
                weeklyRecap = weeklyRecap,
                ideiasRecentesParaAtividade = ideiasRecentesParaAtividade,
                ideiasPorOrientacao = ideiasFiltradas
                    .mapNotNull { it.orientacaoId }
                    .groupingBy { it }
                    .eachCount(),
                atrasadosPorOrientacao = projetosFiltrados
                    .filter { it.orientacaoId != null && it.estaAtrasado() }
                    .groupingBy { it.orientacaoId!! }
                    .eachCount(),
                filaPendente = pendentesPorIdade.take(3),
                diasMaisAntigaPendente = diasMaisAntigaPendente,
                aprovadasSemProjeto = aprovadasSemProjeto,
                projetosAtrasados = projetosFiltrados.count { it.estaAtrasado() }
            )
        )
    }

    private fun AreaAtuacao.label(): String = when (this) {
        AreaAtuacao.OPERACOES -> "Operações"
        AreaAtuacao.LOGISTICA -> "Logística"
        AreaAtuacao.COMERCIAL -> "Comercial"
        AreaAtuacao.FINANCEIRO -> "Financeiro"
        AreaAtuacao.RH -> "RH"
        AreaAtuacao.TI -> "TI"
        AreaAtuacao.MARKETING -> "Marketing"
        AreaAtuacao.QUALIDADE -> "Qualidade"
    }

    companion object {
        private const val MILLIS_PER_DAY = 24 * 60 * 60 * 1000L
        private const val MILLIS_PER_WEEK = 7 * MILLIS_PER_DAY
    }
}
