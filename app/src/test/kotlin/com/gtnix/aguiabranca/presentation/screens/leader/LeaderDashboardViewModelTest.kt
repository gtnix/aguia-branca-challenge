package com.gtnix.aguiabranca.presentation.screens.leader

import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.domain.usecase.dashboard.AreaDesempenhoMetric
import com.gtnix.aguiabranca.domain.usecase.dashboard.DashboardData
import com.gtnix.aguiabranca.domain.usecase.dashboard.GetDashboardUseCase
import com.gtnix.aguiabranca.domain.util.Result
import com.gtnix.aguiabranca.util.MainDispatcherRule
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

@OptIn(ExperimentalCoroutinesApi::class)
class LeaderDashboardViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: LeaderDashboardViewModel
    private val getDashboardUseCase = mockk<GetDashboardUseCase>()

    private val dashboardData = DashboardData(
        orientacoes = emptyList(),
        ideias = emptyList(),
        projetosEmAndamento = emptyList(),
        ideiasConvertidasRecentes = emptyList(),
        totalIdeias = 15,
        totalProjetos = 5,
        ideiasAprovadas = 8,
        ideiasEmProjeto = 3,
        ideiasPendentesAvaliacao = 4,
        investimentoTotal = 500_000.0,
        retornoTotal = 50_000.0,
        roiConsolidado = 20.0,
        desempenhoPorArea = listOf(
            AreaDesempenhoMetric("Operações", 60f),
            AreaDesempenhoMetric("Logística", 40f)
        ),
        tempoMedioAprovacaoDias = 3
    )

    @Before
    fun setup() {
        every { getDashboardUseCase() } returns flowOf(Result.Success(dashboardData))
    }

    @Test
    fun `dado dados disponiveis quando iniciar então carrega dashboard`() = runTest {
        viewModel = LeaderDashboardViewModel(getDashboardUseCase)
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
        assertEquals(50_000.0, state.retornoTotal, 0.01)
        assertEquals(20.0, state.roiMedio, 0.01)
        assertEquals(3, state.tempoMedio)
        assertEquals(2, state.desempenhoAreas.size)
        assertTrue(state.aiNarrative.isNotBlank())
        assertNull(state.errorMessageRes)
    }

    @Test
    fun `dado erro quando iniciar então retorna erro`() = runTest {
        every { getDashboardUseCase() } returns flowOf(Result.Error(Exception("Erro")))

        viewModel = LeaderDashboardViewModel(getDashboardUseCase)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect { }
        }
        advanceUntilIdle()

        val state = viewModel.uiState.first { !it.isLoading }
        assertEquals(R.string.error_load_dashboard, state.errorMessageRes)
    }

    @Test
    fun `dado loading quando iniciar então estado é carregando`() = runTest {
        every { getDashboardUseCase() } returns flowOf(Result.Loading)

        viewModel = LeaderDashboardViewModel(getDashboardUseCase)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect { }
        }
        advanceUntilIdle()

        val state = viewModel.uiState.first()
        assertTrue(state.isLoading)
    }

    @Test
    fun `dado zero ideias quando carregar então narrativa incentiva equipe`() = runTest {
        val dashboardVazio = dashboardData.copy(totalIdeias = 0, roiConsolidado = 0.0)
        every { getDashboardUseCase() } returns flowOf(Result.Success(dashboardVazio))

        viewModel = LeaderDashboardViewModel(getDashboardUseCase)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect { }
        }
        advanceUntilIdle()

        val state = viewModel.uiState.first { !it.isLoading }
        assertTrue(state.aiNarrative.contains("Ainda não há ideias"))
    }

    @Test
    fun `dado roi positivo quando carregar então narrativa mostra roi`() = runTest {
        viewModel = LeaderDashboardViewModel(getDashboardUseCase)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect { }
        }
        advanceUntilIdle()

        val state = viewModel.uiState.first { !it.isLoading }
        assertTrue(state.aiNarrative.contains("ROI"))
    }

    @Test
    fun `dado roi zero quando carregar então narrativa sugere acompanhamento`() = runTest {
        val dashboardSemRoi = dashboardData.copy(roiConsolidado = 0.0)
        every { getDashboardUseCase() } returns flowOf(Result.Success(dashboardSemRoi))

        viewModel = LeaderDashboardViewModel(getDashboardUseCase)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect { }
        }
        advanceUntilIdle()

        val state = viewModel.uiState.first { !it.isLoading }
        assertTrue(state.aiNarrative.contains("Acompanhe"))
    }
}
