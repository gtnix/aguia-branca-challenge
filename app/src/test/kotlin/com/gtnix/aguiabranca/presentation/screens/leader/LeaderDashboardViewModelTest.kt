package com.gtnix.aguiabranca.presentation.screens.leader

import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.domain.model.DashboardInsight
import com.gtnix.aguiabranca.domain.model.FunnelStepMetric
import com.gtnix.aguiabranca.domain.model.LeaderDashboardSnapshot
import com.gtnix.aguiabranca.domain.model.MetricasIa
import com.gtnix.aguiabranca.domain.repository.DashboardRepository
import com.gtnix.aguiabranca.domain.repository.OrientacaoRepository
import com.gtnix.aguiabranca.domain.usecase.dashboard.AreaDesempenhoMetric
import com.gtnix.aguiabranca.domain.usecase.dashboard.GetLeaderDashboardUseCase
import com.gtnix.aguiabranca.domain.util.Result
import com.gtnix.aguiabranca.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class LeaderDashboardViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: LeaderDashboardViewModel
    private val getLeaderDashboardUseCase = mockk<GetLeaderDashboardUseCase>()
    private val dashboardRepository = mockk<DashboardRepository>()
    private val orientacaoRepository = mockk<OrientacaoRepository>()

    private val snapshot = LeaderDashboardSnapshot(
        roiMedio = 20.0,
        lucroTotal = 30_000.0,
        investimentoTotal = 500_000.0,
        retornoTotal = 50_000.0,
        projetosNoPrazo = 4,
        projetosAtrasados = 1,
        ganhoProdutividadePercentual = 40.0,
        totalIdeias = 15,
        ideiasAprovadas = 8,
        ideiasEmProjeto = 3,
        projetosAtivos = 5,
        tempoMedioAprovacaoDias = 3,
        funil = listOf(
            FunnelStepMetric("IDEIAS", "Ideias", 15),
            FunnelStepMetric("APROVADAS", "Aprovadas", 8),
            FunnelStepMetric("EM_PROJETO", "Em Projeto", 3)
        ),
        desempenhoPorArea = listOf(
            AreaDesempenhoMetric("Operações", 60f, 9),
            AreaDesempenhoMetric("Logística", 40f, 6)
        )
    )

    private val insightFallback = DashboardInsight(
        titulo = "Portfólio com 15 ideias ativas",
        resumo = "Portfólio com 15 ideias e ROI de 20% a.a.",
        evidencias = listOf("ROI de 20%"),
        acaoRecomendada = "Converter ideias.",
        modelo = "heuristic-local",
        isFallback = true,
        geradoEm = null
    )

    @Before
    fun setup() {
        every { getLeaderDashboardUseCase(any()) } returns flowOf(Result.Success(snapshot))
        every { orientacaoRepository.listarAtivas() } returns flowOf(emptyList())
        coEvery { dashboardRepository.obterInsightIa(any()) } returns Result.Success(insightFallback)
        coEvery { dashboardRepository.obterMetricasIa(any()) } returns Result.Success(metricas)
    }

    private val metricas = MetricasIa(
        ideiasAnalisadas = 10,
        decisoesComOpiniao = 4,
        concordancias = 3,
        taxaConcordancia = 75.0,
        execucoes = 12,
        percentualFallback = 25.0,
        tempoMedioTriagemHoras = 20.5,
        scorePorArea = emptyList(),
        scorePorDivisao = emptyList(),
        aprovacaoPorFaixa = emptyList()
    )

    private fun buildVm() = LeaderDashboardViewModel(
        getLeaderDashboardUseCase,
        dashboardRepository,
        orientacaoRepository
    )

    @Test
    fun `dado dados da API quando iniciar então carrega dashboard`() = runTest {
        viewModel = buildVm()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect { }
        }
        advanceUntilIdle()

        val state = viewModel.uiState.first { !it.isLoading }
        assertEquals(15, state.totalIdeias)
        assertEquals(5, state.projetosAtivos)
        assertEquals(8, state.ideiasAprovadas)
        assertEquals(3, state.ideiasEmProjeto)
        assertEquals(500_000.0, state.investimentoTotal, 0.01)
        assertEquals(50_000.0, state.retornoMensal, 0.01)
        assertEquals(30_000.0, state.lucroAnualizado, 0.01)
        assertEquals(20.0, state.roiMedio, 0.01)
        assertEquals(3, state.tempoMedio)
        assertEquals(4, state.projetosNoPrazo)
        assertEquals(1, state.projetosAtrasados)
        assertEquals(40.0, state.progressoMedioProjetos, 0.01)
        assertEquals(3, state.funil.size)
        assertEquals(2, state.desempenhoAreas.size)
        assertNull(state.errorMessageRes)
    }

    @Test
    fun `dado dados da API quando iniciar então calcula taxas de conversão`() = runTest {
        viewModel = buildVm()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect { }
        }
        advanceUntilIdle()

        val state = viewModel.uiState.first { !it.isLoading }
        // aprovadasNoFluxo = 8 + 3 = 11; taxaAprovacao = 11/15*100 = 73
        assertEquals(73, state.taxaAprovacaoPercent)
        // taxaConversao = 3/11*100 = 27
        assertEquals(27, state.taxaConversaoPercent)
    }

    @Test
    fun `dado fila vazia e ideias em projeto quando carregar então conversão é 100`() = runTest {
        val semFila = snapshot.copy(
            totalIdeias = 8,
            ideiasAprovadas = 0,
            ideiasEmProjeto = 3
        )
        every { getLeaderDashboardUseCase(any()) } returns flowOf(Result.Success(semFila))

        viewModel = buildVm()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect { }
        }
        advanceUntilIdle()

        val state = viewModel.uiState.first { !it.isLoading }
        assertEquals(37, state.taxaAprovacaoPercent)
        assertEquals(100, state.taxaConversaoPercent)
    }

    @Test
    fun `dado insight da IA quando carregado então aparece no estado`() = runTest {
        viewModel = buildVm()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect { }
        }
        advanceUntilIdle()

        val state = viewModel.uiState.first { !it.isLoading && !it.isInsightLoading }
        assertEquals("Portfólio com 15 ideias ativas", state.insight.titulo)
        assertTrue(state.insight.isFallback)
    }

    @Test
    fun `dado erro no dashboard quando iniciar então retorna erro`() = runTest {
        every { getLeaderDashboardUseCase(any()) } returns flowOf(Result.Error(Exception("Erro")))

        viewModel = buildVm()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect { }
        }
        advanceUntilIdle()

        val state = viewModel.uiState.first { !it.isLoading }
        assertEquals(R.string.error_load_dashboard, state.errorMessageRes)
    }

    @Test
    fun `dado offline quando iniciar então informa rede obrigatória`() = runTest {
        every { getLeaderDashboardUseCase(any()) } returns flowOf(
            Result.Error(IOException("Sem conexão. Esta ação exige rede na Sprint 2."))
        )

        viewModel = buildVm()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect { }
        }
        advanceUntilIdle()

        val state = viewModel.uiState.first { !it.isLoading }
        assertEquals(R.string.error_network_required, state.errorMessageRes)
    }

    @Test
    fun `dado 403 quando iniciar então informa perfil lider`() = runTest {
        every { getLeaderDashboardUseCase(any()) } returns flowOf(
            Result.Error(Exception("Apenas o perfil Líder pode consultar o dashboard executivo."))
        )

        viewModel = buildVm()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect { }
        }
        advanceUntilIdle()

        val state = viewModel.uiState.first { !it.isLoading }
        assertEquals(R.string.error_dashboard_forbidden, state.errorMessageRes)
    }

    @Test
    fun `dado loading quando iniciar então estado é carregando`() = runTest {
        every { getLeaderDashboardUseCase(any()) } returns flowOf(Result.Loading)

        viewModel = buildVm()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect { }
        }
        advanceUntilIdle()

        val state = viewModel.uiState.first()
        assertTrue(state.isLoading)
    }

    @Test
    fun `dado zero ideias quando carregar então taxas de conversão são zero`() = runTest {
        val vazio = snapshot.copy(totalIdeias = 0, ideiasAprovadas = 0, ideiasEmProjeto = 0)
        every { getLeaderDashboardUseCase(any()) } returns flowOf(Result.Success(vazio))

        viewModel = buildVm()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect { }
        }
        advanceUntilIdle()

        val state = viewModel.uiState.first { !it.isLoading }
        assertEquals(0, state.taxaAprovacaoPercent)
        assertEquals(0, state.taxaConversaoPercent)
    }

    @Test
    fun `dado filtro selecionado quando alterar então use case é chamado com orientacaoId`() = runTest {
        viewModel = buildVm()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect { }
        }
        advanceUntilIdle()

        viewModel.selectOrientacao("ori-1")
        advanceUntilIdle()

        // O fluxo roda em Dispatchers.IO, fora do agendador de teste: a verificação espera a chamada.
        io.mockk.verify(timeout = 2_000) { getLeaderDashboardUseCase("ori-1") }
    }

    @Test
    fun `dado erro no insight quando iniciar então estado mostra insight vazio`() = runTest {
        coEvery { dashboardRepository.obterInsightIa(any()) } returns
            Result.Error(Exception("IA indisponível"))

        viewModel = buildVm()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect { }
        }
        advanceUntilIdle()

        val state = viewModel.uiState.first { !it.isInsightLoading }
        // Dashboard still loads even when insight fails
        assertNull(state.errorMessageRes)
        assertTrue(state.insight.titulo.isBlank())
    }

    @Test
    fun `dado metricas de governanca quando iniciar então exibe concordancia da IA`() = runTest {
        viewModel = buildVm()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect { }
        }
        advanceUntilIdle()

        val state = viewModel.uiState.first { it.metricasIa != null && !it.isLoading }
        assertEquals(75.0, state.metricasIa!!.taxaConcordancia!!, 0.01)
    }

    @Test
    fun `dado erro nas metricas quando iniciar então dashboard segue sem o card`() = runTest {
        coEvery { dashboardRepository.obterMetricasIa(any()) } returns Result.Error(Exception("403"))

        viewModel = buildVm()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect { }
        }
        advanceUntilIdle()

        val state = viewModel.uiState.first { !it.isInsightLoading && !it.isLoading }
        assertNull(state.metricasIa)
        assertNull(state.errorMessageRes)
    }
}
