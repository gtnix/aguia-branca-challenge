package com.gtnix.aguiabranca.domain.usecase.dashboard

import com.gtnix.aguiabranca.domain.model.Ideia
import com.gtnix.aguiabranca.domain.model.OrientacaoEstrategica
import com.gtnix.aguiabranca.domain.model.Projeto
import com.gtnix.aguiabranca.domain.model.StatusIdeia
import com.gtnix.aguiabranca.domain.model.StatusProjeto
import com.gtnix.aguiabranca.domain.repository.IdeiaRepository
import com.gtnix.aguiabranca.domain.repository.OrientacaoRepository
import com.gtnix.aguiabranca.domain.repository.ProjetoRepository
import com.gtnix.aguiabranca.domain.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

data class DashboardData(
    val orientacoes: List<OrientacaoEstrategica>,
    val ideias: List<Ideia>,
    val projetosEmAndamento: List<Projeto>,
    val totalIdeias: Int,
    val totalProjetos: Int,
    val ideiasAprovadas: Int,
    val ideiasEmProjeto: Int,
    val investimentoTotal: Double,
    val retornoTotal: Double,
    val roiConsolidado: Double
)

/**
 * Consolida KPIs do dashboard combinando dados de ideias, projetos e orientações.
 *
 * Extrai a lógica de cálculo de ROI e contagens que antes vivia no HomeViewModel.
 */
class GetDashboardUseCase @Inject constructor(
    private val ideiaRepository: IdeiaRepository,
    private val projetoRepository: ProjetoRepository,
    private val orientacaoRepository: OrientacaoRepository
) {
    operator fun invoke(): Flow<Result<DashboardData>> {
        return combine(
            orientacaoRepository.listarAtivas(),
            ideiaRepository.listarTodas(),
            projetoRepository.listarTodos()
        ) { orientacoes, ideias, projetos ->
            val investimento = projetos.sumOf { it.investimentoRealizado }
            val retorno = projetos.sumOf { it.retornoRealizadoMensal }
            val roi = if (investimento > 0)
                ((retorno * 12) - investimento) / investimento * 100
            else 0.0

            Result.Success(
                DashboardData(
                    orientacoes = orientacoes.take(3),
                    ideias = ideias.take(5),
                    projetosEmAndamento = projetos.filter {
                        it.status == StatusProjeto.EM_ANDAMENTO
                    }.take(5),
                    totalIdeias = ideias.size,
                    totalProjetos = projetos.size,
                    ideiasAprovadas = ideias.count {
                        it.status == StatusIdeia.APROVADA || it.status == StatusIdeia.CONVERTIDA_PROJETO
                    },
                    ideiasEmProjeto = ideias.count { it.status == StatusIdeia.CONVERTIDA_PROJETO },
                    investimentoTotal = investimento,
                    retornoTotal = retorno,
                    roiConsolidado = roi
                )
            ) as Result<DashboardData>
        }.catch { e ->
            emit(Result.Error(Exception(e)))
        }
    }
}
