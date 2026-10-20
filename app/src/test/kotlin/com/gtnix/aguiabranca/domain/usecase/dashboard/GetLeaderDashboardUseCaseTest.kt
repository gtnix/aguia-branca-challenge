package com.gtnix.aguiabranca.domain.usecase.dashboard

import app.cash.turbine.test
import com.gtnix.aguiabranca.domain.model.FunnelStepMetric
import com.gtnix.aguiabranca.domain.model.LeaderDashboardSnapshot
import com.gtnix.aguiabranca.domain.repository.DashboardRepository
import com.gtnix.aguiabranca.domain.util.Result
import com.gtnix.aguiabranca.domain.util.isOffline
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

class GetLeaderDashboardUseCaseTest {

    private val repository = mockk<DashboardRepository>()
    private val useCase = GetLeaderDashboardUseCase(repository)

    private val snapshot = LeaderDashboardSnapshot(
        roiMedio = 20.0,
        lucroTotal = 20_000.0,
        investimentoTotal = 100_000.0,
        retornoTotal = 10_000.0,
        projetosNoPrazo = 1,
        projetosAtrasados = 0,
        ganhoProdutividadePercentual = 40.0,
        totalIdeias = 4,
        ideiasAprovadas = 2,
        ideiasEmProjeto = 1,
        projetosAtivos = 1,
        tempoMedioAprovacaoDias = 3,
        funil = listOf(FunnelStepMetric("IDEIAS", "Ideias", 4)),
        desempenhoPorArea = listOf(AreaDesempenhoMetric("Operações", 50f))
    )

    @Test
    fun `retorna snapshot calculado no servidor`() = runTest {
        coEvery { repository.obterResumo() } returns Result.Success(snapshot)

        useCase().test {
            val result = awaitItem()
            assertTrue(result is Result.Success)
            val data = (result as Result.Success).data
            assertEquals(20.0, data.roiMedio, 0.01)
            assertEquals(4, data.totalIdeias)
            awaitComplete()
        }
    }

    @Test
    fun `propaga erro de rede`() = runTest {
        coEvery { repository.obterResumo() } returns Result.Error(
            IOException("Sem conexão. Esta ação exige rede na Sprint 2.")
        )

        useCase().test {
            val result = awaitItem()
            assertTrue(result is Result.Error)
            assertTrue((result as Result.Error).isOffline())
            awaitComplete()
        }
    }
}
